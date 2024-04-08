import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.upm.aedlib.fifo.FIFO;
import es.upm.aedlib.fifo.FIFOArray;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.indexedlist.IndexedList;

public class Analizador_lexico {
	//Objeto fichero que nos permite leer un fichero.
	private FicheroInput codeFile;

	//Objeto fichero que nos permite escribir en un fichero.
	private FicheroOutput tokensFile;

	//Objeto fichero que nos permite escribir la tabla de simbolos.
	private FicheroOutput tSFile;
	
	private FicheroOutput erroresFile;

	//El map donde vamos a introducir los identificadores, con su numero en la TS. 
	private Tabla_de_simbolos tablaDeSimbolos;	
	private Tabla_de_simbolos tablaDeSimbolosSecundaria;
	
	private FIFO<Tabla_de_simbolos> colaTS;

	private boolean estoyPrincipal;

	//Iremos metiendo los identificadores en esta lista para facilitar el print. 
	private IndexedList<String> listaSimbolos;

	//El siguiente caracter y su codigo ascii.
	private char nextChar;
	private int charASCII;

	//lexicoico para las cadenas que vallamos leyendo.
	private String lexico;
	private int longit;

	//Numero para los numeros que vallamos leyendo.
	private int num;

	//El estado y la accion actual del automata finito determinista
	private int estado;
	private String accion;

	//Contador de la posicion de la tabla de simbolos.
	private int pos_TS;
	
	private int pos_TSsec;

	//Enumerador de lineas del texto
	private int nLinea;

	private boolean eof;

	//Para no tener error de caracter varias veces 
	private boolean anteriorErrorLetra;

	//Para saber si estoy en zona de declaraion
	private boolean zonaDeDeclaracion;

	//Numero de tablas
	private int nTablas; //se suma 1 cuando se vuelve falso zona de declaracion
	
	//Para poder incrementar linea tras que el sintactico y el semantico hayan terminado su trabajo.
	private int incrementoLinea;
	
	private final boolean global = true;


	private final Pair[][] AFD = {{new Pair(0, "A"), new Pair(1, "B"), new Pair(3, "C"), new Pair(2, "A"), new Pair(4, "A"), new Pair(5, "A"), new Pair(6, "A"), new Pair(15, "S"), new Pair(-1, "6"), new Pair(0,"A")},
			{new Pair(8, "I"), new Pair(1, "B"), new Pair(1, "B"), new Pair(8, "I"), new Pair(8, "I"), new Pair(8, "I"), new Pair(8, "I"), new Pair(8, "I"), new Pair(8, "I"), new Pair(8, "I")},
			{new Pair(2, "B"), new Pair(2, "B"), new Pair(2, "B"), new Pair(9, "G9"), new Pair(2, "B"), new Pair(2, "B"), new Pair(2, "B"), new Pair(2, "B"), new Pair(2, "B"), new Pair(-1, "4")},
			{new Pair(8, "G3"), new Pair(-1, "1"), new Pair(3, "C"), new Pair(11, "G3"), new Pair(11, "G3"), new Pair(11, "G3"), new Pair(11, "G3"), new Pair(11, "G3"), new Pair(11, "G3"), new Pair(11, "G3")},
			{new Pair(-1, "2"), new Pair(-1, "2"), new Pair(-1, "2"), new Pair(-1, "2"), new Pair(12, "G7"), new Pair(-1, "2"), new Pair(-1, "2"), new Pair(-1, "2"), new Pair(-1, "2"), new Pair(-1, "2")},
			{new Pair(-1, "3"), new Pair(-1, "3"), new Pair(-1, "3"), new Pair(-1, "3"), new Pair(-1, "3"), new Pair(13, "G10"), new Pair(-1, "3"), new Pair(-1, "3"), new Pair(-1, "3"), new Pair(-1, "3")},
			{new Pair(-1, "4"), new Pair(-1, "4"), new Pair(-1, "4"), new Pair(-1, "4"), new Pair(-1, "4"), new Pair(-1, "4"), new Pair(7, "A"), new Pair(-1, "4"), new Pair(-1, "4"), new Pair(-1, "4")},
			{new Pair(7, "A"), new Pair(7, "A"), new Pair(7, "A"), new Pair(7, "A"), new Pair(7, "A"), new Pair(7, "A"), new Pair(7, "A"), new Pair(7, "A"), new Pair(0, "A"), new Pair(0, "A")}};

	public Analizador_lexico(String direccionInput, String direccionOutput, String direccionTS, FicheroOutput erroresFile) {
		listaSimbolos = new ArrayIndexedList<String>();
		codeFile = new FicheroInput(direccionInput);
		tokensFile = new FicheroOutput(direccionOutput);
		tSFile = new FicheroOutput(direccionTS);
		colaTS = new FIFOArray<>(); 
		this.erroresFile = erroresFile;
		pos_TS = 1;
		pos_TSsec = 1;
		eof = false;
		nLinea = 1;
		anteriorErrorLetra = false;
		estoyPrincipal = true;
		zonaDeDeclaracion = false;
		nTablas = 0;
		incrementoLinea = 0;
		zonaDeDeclaracion = false;
	}



	public String nextToken() {
		Pair par;
		String token = ""; 
		lexico = "";
		num = 0;
		estado = 0;
		accion = "";
		longit = 0;
		
		nLinea+=incrementoLinea;
		incrementoLinea = 0;
		while(estado>=0 && estado<8 && charASCII != -1 ){
			charASCII = codeFile.nextASCIIChar();

			nextChar = (char) charASCII;

			if (charASCII == 32 || charASCII == 9) {
				par = AFD[estado][0];
			} else if (charASCII >= 48 && charASCII <= 57) {
				par = AFD[estado][2];
				//System.out.println("ASCII " + charASCII + ": Digit");
			} else if ((charASCII >= 65 && charASCII <= 90) || charASCII == 95) {
				par = AFD[estado][1];
			} else if (charASCII >= 97 && charASCII <= 122) {
				par = AFD[estado][1];
			} else if(charASCII == 34){
				par = AFD[estado][3];
				//System.out.println("ASCII " + charASCII + ": Special Character");
			} else if(charASCII == 43){
				par = AFD[estado][4];
				//System.out.println("ASCII " + charASCII + ": Special Character");
			} else if(charASCII == 38){
				par = AFD[estado][5];
				//System.out.println("ASCII " + charASCII + ": Special Character");
			} else if(charASCII == 47){
				par = AFD[estado][6];
				//System.out.println("ASCII " + charASCII + ": Special Character");
			} else if(charASCII == 45 || charASCII == 44 || charASCII == 61 || charASCII == 60 || charASCII == 59 || charASCII == 40 || charASCII == 41 || charASCII == 123 || charASCII == 125){
				par = AFD[estado][7];
			} else if(charASCII == 10 || charASCII == 13){
				
				par = AFD[estado][9];
				//System.out.println("ASCII " + charASCII + ": Special Character");
			}
			else if(charASCII == -1){
				par = new Pair(8, "EOF");
				eof = true;
			}
			else {
				erroresFile.escribir("Error Lexico: This caracter -> "+charASCII+"-"+nextChar+" is considered another caracter\n"); //ERROR
				par = AFD[0][8]; //ERROR
				//token = "Letra desconocida en la linea " +nLinea+" : "+nextChar+"\n";

			}

			estado = par.getKey();
			accion = par.getValue();
			//System.out.println("This caracter -> "+charASCII+"-"+nextChar+"\n");
			//System.out.println("El siguiente posicion es: "+estado+" Con la accion: "+accion+"\n\n");



			if(!(estado == -1 && accion == "6")){
				anteriorErrorLetra = false;

			}

			if(estado == -1){
				switch (accion){
				case "1": erroresFile.escribir("Error Lexico: Error_Numero en la linea "+nLinea+"\n"); //no se ejecuta
				break;
				case "2": erroresFile.escribir("Error Lexico: Error_Incremento en la linea "+nLinea+"\n\n");
				break;
				case "3": erroresFile.escribir("Error Lexico: Error_AND en la linea "+nLinea+"\n\n");
				break;
				case "4": erroresFile.escribir("Error Lexico: Error_Comentario en la linea "+nLinea+"\n");

				while ( codeFile.nextASCIIChar() != 10 && codeFile.nextASCIIChar() >= 0){
					codeFile.leer();
				}
				return nextToken();

				case "5": erroresFile.escribir("Error Lexico: Error desconocido en la linea "+nLinea+"\n"); //no se puede ejecutar
				break;
				case "6": if (!anteriorErrorLetra){
					erroresFile.escribir("Error Lexico: Error Letra desconocida en la linea " +nLinea+" : "+nextChar+"\n\n");
					anteriorErrorLetra = true;
				}

				}
			}
			else if(!eof){
				if (accion.compareTo("A")==0) {
					codeFile.leer();
					if(nextChar == 10) {
						incrementoLinea++;
					}
				} else if (accion.compareTo("B")==0) {
					lexico += nextChar;
					longit++;
					codeFile.leer();
				} else if (accion.compareTo("C")==0) {
					num = num * 10 + (charASCII - 48);
					codeFile.leer(); // (0,9) - 0
				} else if (accion.compareTo("I")==0) {
					if (lexico.compareTo("boolean")==0) {
						token = "<011, > //es un boolean\n";
					} else if (lexico.compareTo("else")==0) {
						token = "<106, > //es un else\n";
					} else if (lexico.compareTo("function")==0) {
						token = "<101, > //es un function\n";
					} else if (lexico.compareTo("get")==0) {
						token = "<103, > //es un get\n";
					} else if (lexico.compareTo("if")==0) {
						token = "<105, > //es un if\n";
					} else if (lexico.compareTo("int")==0) {
						token = "<010, > //es un int\n";
					} else if (lexico.compareTo("put")==0) {
						token = "<104, > //es un put\n";
					} else if (lexico.compareTo("let")==0) {
						token = "<102, > //es un let\n";
					} else if (lexico.compareTo("return")==0) {
						token = "<107, > //es un return\n";
					} else if (lexico.compareTo("string")==0) {
						token = "<012, > //es un string\n";
					} else if (lexico.compareTo("void")==0) {
						token = "<013, > //es un void\n";
					} else {
						int posicion = 0;
						System.err.println(estoyPrincipal+ " "+tablaDeSimbolos.containsLexema(lexico)+" "+lexico );
							if(estoyPrincipal && tablaDeSimbolos.containsLexema(lexico) && zonaDeDeclaracion) {
								posicion = tablaDeSimbolos.getPos(lexico);
								erroresFile.escribir("Error Lexico: Error, el id ya estaba declarado.  Linea: "+getNumLineas()+"\n");
							}
							else if(estoyPrincipal && tablaDeSimbolos.containsLexema(lexico) && !zonaDeDeclaracion) {
								posicion = tablaDeSimbolos.getPos(lexico);
							}
							else if(estoyPrincipal && !tablaDeSimbolos.containsLexema(lexico) && zonaDeDeclaracion){
								posicion = InsertaTS(lexico, global);
							}
							else if(estoyPrincipal && !tablaDeSimbolos.containsLexema(lexico) && !zonaDeDeclaracion){
								posicion = InsertaTS(lexico, global);
								tablaDeSimbolos.setTipo(posicion, "Entero");
								tablaDeSimbolos.setDesplazamiento(posicion);
							}
							else if(tablaDeSimbolosSecundaria.containsLexema(lexico) && zonaDeDeclaracion){
								posicion = -tablaDeSimbolosSecundaria.getPos(lexico);
								erroresFile.escribir("Error Lexico: Error, el id ya estaba declarado.  Linea: "+getNumLineas()+"\n");
							}
							else if(tablaDeSimbolosSecundaria.containsLexema(lexico) && !zonaDeDeclaracion){
								posicion = -tablaDeSimbolosSecundaria.getPos(lexico);
							}
							else if(tablaDeSimbolos.containsLexema(lexico) && zonaDeDeclaracion) {
								posicion = InsertaTS(lexico, !global);
							}
							else if(tablaDeSimbolos.containsLexema(lexico) && !zonaDeDeclaracion){
								posicion = tablaDeSimbolos.getPos(lexico);
							}	
							else if(zonaDeDeclaracion && enFuncion()) {	//No esta en ninguna tabla, pero se está declarando con let
								posicion = InsertaTS(lexico, !global);
							}
							else if(zonaDeDeclaracion && !enFuncion()) {
								posicion = InsertaTS(lexico, global);
							}
							else {			//Declaracion implicita global
								posicion = InsertaTS(lexico, global);
								tablaDeSimbolos.setTipo(posicion, "Entero");
								tablaDeSimbolos.setDesplazamiento(posicion);
							}
							token = "<200, " + posicion + "> //es un identificador con posicion en la tabla de simbolos: " +Math.abs(posicion)+ "\n";

							if(tablaDeSimbolos.isEmpty()){ 
								tSFile.escribir("CONTENIDOS DE LA TABLA PRINCIPAL # 1 :\r");
							}
					}
				} else if (accion == "G3") {
					if (num < 32767) {
						token = "<402, " + num + "> //es una constante con valor " + num + "\n";
					}
					else {
						token = "<402, " + 32766 + "> //es una constante con valor " + num + "\n";
						erroresFile.escribir("Error Lexico: Error numero fuera de rango. Linea de error "+nLinea+" numero en cuestion: "+num+"\n");


						//token = "Error numero fuera de rango. Linea de error "+nLinea+" numero en cuestion: "+num; //ERROR

					}
				} else if (accion == "S") {
					if (nextChar == '-') {
						token = "<301, > //es un -\n";
					} else if (nextChar == '<') {
						token = "<302, > //es un <\n";
					} else if (nextChar == '=') {
						token = "<201, > //es un =\n";
					} else if (nextChar == ',') {
						token = "<202, > //es un ,\n";
					} else if (nextChar == ';') {
						token = "<203, > //es un ;\n";
					} else if (nextChar == '(') {
						token = "<204, > //es un (\n";
					} else if (nextChar == ')') {
						token = "<205, > //es un )\n";
					} else if (nextChar == '{') {
						token = "<206, > //es un {\n";
					} else if (nextChar == '}') {
						token = "<207, > //es un }\n";
					}
					codeFile.leer();
				} else if (accion == "G7") {
					token = "<304, > // es un ++\n";
					codeFile.leer();

				} else if (accion == "G9") {
					if (longit <= 64) {
						token = "<401, " + lexico + "> //es una cadena\n";
						codeFile.leer();
					}
					else {
						token = "<401, " + lexico.substring(0, 64)+">";
						erroresFile.escribir("Error Lexico: Error string demasiado largo. Linea de error "+nLinea+" el string en cuestion: "+lexico+"\n"); //ERROR
						codeFile.leer();
					}
				} else if (accion == "G10") {
					token = "<303, > //es un &&\n";
					codeFile.leer();
				}
			}

		}
		if(eof) {
			token = "<666, EOF>";
		}


		tokensFile.escribir(token);



		return token;
	}
	
	
	public boolean enTS(int numeroTS) {
		boolean res = false;
		if(numeroTS >0){
			res = tablaDeSimbolos.containsKey(numeroTS);
		}
		else{
			res = tablaDeSimbolosSecundaria.containsKey(-numeroTS);
		}
		return res;
	}

	public String tipoId(int numeroTS) {
		//Buscar en la tabla de simbolos y dar el tipo del identificador.
		//Prestar atencion a como est�n denomidas los tipos al inicio del sintactico.
		String tipo = "";

		if(numeroTS >0){
			tipo = tablaDeSimbolos.getTipo(numeroTS);

		}else{
			tipo = tablaDeSimbolosSecundaria.getTipo(-numeroTS);
		}


		return tipo;
	}


	public ArrayList<String> parametros(int numeroTS){
		//Retornar los parametros necesarios para que el id(tiene que ser funcion) funcione.
		ArrayList<String> res = new ArrayList<String>();


		if(numeroTS >0){
			res = tablaDeSimbolos.getTipoParametros(numeroTS);

		}else{
			res = tablaDeSimbolosSecundaria.getTipoParametros(-numeroTS);
		}

		return res;
	}

	public String tipoReturn(int numeroTS) {
		//Retornar el tipo de return de la funcion dado el numero de TS.
		String res = "";

		if(numeroTS >0){
			res = tablaDeSimbolos.getTipoDevolucion(numeroTS);

		}else{
			res = tablaDeSimbolosSecundaria.getTipoDevolucion(-numeroTS);
		}
		return res;
	}

	public boolean enFuncion() {
		//Decirme si estoy en una funcion.
		return !estoyPrincipal;
	}

	private int InsertaTS(String lexico2, boolean global) {
		int res = 0;
		if(enFuncion() && !global) {
			tablaDeSimbolosSecundaria.add(pos_TSsec, new ArgumentosTS(lexico2));
			res = -pos_TSsec;
			pos_TSsec++;
		}
		else {
			tablaDeSimbolos.add(pos_TS, new ArgumentosTS(lexico2));
			res = pos_TS;
			pos_TS++;
		}
		return res;
	}


	public void printTS(){ //Imprimir antes de las declaraciones de funciones 
		//Iterator<String> it = listaSimbolos.iterator(); //Cuidado con esto

		//tSFile.escribir("* Me metí");
		tablaDeSimbolos.escribePrincipal(tSFile);
		int i=1;
		while(!colaTS.isEmpty()) {
			colaTS.dequeue().escribeSecundaria(tSFile, i);
			i++;
		}
		/* 
		while(it.hasNext()){
			tSFile.escribir("* LEXEMA : '"+it.next()+"'\n");
		}
		*/
	}
	
	public void closeFiles() {
		tokensFile.cerrarFichero();
		codeFile.cerrarFichero();
		tSFile.cerrarFichero();
	}

	public boolean endFile() {
		return eof;
	}
	public int getNumLineas() {
		return nLinea;
	}

	public void setZonaDeclaracion(Boolean boton) {
		this.zonaDeDeclaracion = boton;
	}
	public void setFuncion(Boolean boton) {
		this.estoyPrincipal = !boton;
	}



	public Tabla_de_simbolos crearTablaSecundaria() {
		// TODO Auto-generated method stub
		if(this.tablaDeSimbolosSecundaria == null) {
			tablaDeSimbolosSecundaria = new Tabla_de_simbolos();
		}
		else {
			//Si no esta a null, es que seguimos en una funcion
			erroresFile.escribir("Seguimos en una funcion y el lenguaje no soporta anidamiento de funciones. Linea: "+getNumLineas());
		}
		return this.tablaDeSimbolosSecundaria;
	}

	public int getNTablas() {
		return nTablas;
	}


	public Tabla_de_simbolos crearTablaSimbolos() {
		// TODO Auto-generated method stub
		this.tablaDeSimbolos = new Tabla_de_simbolos();
		return this.tablaDeSimbolos;
	}
	
	public void destruirTablaSimbolosSecundaria() {
        colaTS.enqueue(new Tabla_de_simbolos(tablaDeSimbolosSecundaria));
        this.pos_TSsec = 1;
        this.nTablas++;
        this.tablaDeSimbolosSecundaria = null;
    }
}
