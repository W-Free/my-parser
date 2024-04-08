import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import es.upm.aedlib.lifo.LIFO;
import es.upm.aedlib.lifo.LIFOArray;

public class Analizador_sintatico {

	private final String entero = "Entero";
	private final String cadena = "Cadena";
	private final String bool = "Boleano";
	private final String funcion = "Funcion";
	private final String ok = "Ok";
	private final String error = "Error";
	private final String vacio = "Vacio";
	private final String empty = "empty";

	private Analizador_lexico alexico; 
	
	private FicheroOutput parseFile;
	private FicheroOutput erroresFile;

	private final String[][] ACCION = new String[101][26];
	private LIFO<String> pila;
	private LIFO<SemanticoElem> pilaSemantica;

	private Pair[] n_consecuente = {new Pair(1, "A"), new Pair(2, "P"), new Pair(2, "P"), new Pair(0, "P"),
			new Pair(4, "F"), new Pair(4, "F1"), new Pair(3, "F2"), new Pair(5, "B"),
			new Pair(4, "C"), new Pair(1, "C"), new Pair(4, "D"), new Pair(0, "D"),
			new Pair(4, "B"), new Pair(1, "B"), new Pair(4, "S"), new Pair(3, "S"),
			new Pair(3, "S"), new Pair(3, "S"), new Pair(3, "S"), new Pair(5, "S"),
			new Pair(3, "E"), new Pair(1, "E"), new Pair(3, "Z"), new Pair(1, "Z"),
			new Pair(3, "G"), new Pair(1, "G"), new Pair(2, "H"), new Pair(3, "H"), 
			new Pair(1, "H"), new Pair(1, "H"), new Pair(3, "Y"), new Pair(2, "I"),
			new Pair(0, "I"), new Pair(1, "J"), new Pair(1, "J"), new Pair(2, "K"),
			new Pair(3, "L"), new Pair(1, "L"), new Pair(4, "M"), new Pair(0, "M"),
			new Pair(2, "N"), new Pair(0, "N") ,new Pair(3, "O"), new Pair(0, "O"),
			new Pair(1, "Q"), new Pair(0, "Q"), new Pair(1, "T"), new Pair(1, "T"),
			new Pair(1, "T"), new Pair(2, "X"), new Pair(0, "X"), new Pair(0, "Y")};//Aqui tiene el numero de consecuentes de cada regla con el antecedente.
	private final String[][] GOTO = new String[101][23];
	private Tabla_de_simbolos tablaDeSimbolos;
	private Tabla_de_simbolos tablaDeSimbolosSecundario;
	
	private HashMap<String, Integer> mp;

	public Analizador_sintatico(String direccionParse, FicheroOutput erroresFile, Analizador_lexico AL, String accionFile, String GOTOFile) {
		alexico = AL;
		parseFile = new FicheroOutput(direccionParse);
		this.erroresFile = erroresFile;
		pila = new LIFOArray<>();
		pilaSemantica = new LIFOArray<>();
		crearAccion(accionFile);
		crearGOTO(GOTOFile);
		mp = new HashMap<>();
		functAux();
	}

	private void functAux() {
		mp.put("P", 0);
		mp.put("F", 1);
		mp.put("F1", 2);
		mp.put("F2", 3);
		mp.put("B", 4);
		mp.put("C", 5);
		mp.put("D", 6);
		mp.put("S", 7);
		mp.put("E", 8);
		mp.put("G", 9);
		mp.put("H", 10);
		mp.put("I", 11);
		mp.put("J", 12);
		mp.put("K", 13);
		mp.put("L", 14);
		mp.put("M", 15);
		mp.put("N", 16);
		mp.put("O", 17);
		mp.put("Q", 18);
		mp.put("T", 19);
		mp.put("X", 20);
		mp.put("Y", 21);
		mp.put("Z", 22);
	}

	private void accionSemantica(int regla) {

		switch (regla) {
		case 0: {
			SemanticoElem P = elemSem(2);
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(P.getTipo()));
			break;
		}
		case 1:
		case 2:{
			SemanticoElem P = elemSem(2);
			SemanticoElem B = elemSem(4);
			popearN(2*n_consecuente[regla].getKey());
			if(P.getTipo().compareTo(error) * B.getTipo().compareTo(error) == 0) {
				pilaSemantica.push(new SemanticoElem(error));
			}
			else {
				pilaSemantica.push(new SemanticoElem(B.getTipo()));
			}
			break;
		}
		case 3:
		case 11:
		case 32:
		case 51:{
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem());
			break;
		}
		case 4:{
			SemanticoElem X = elemSem(4);
			SemanticoElem F1 = elemSem(8);
			popearN(2*n_consecuente[regla].getKey());
			
			alexico.setFuncion(false);
			alexico.destruirTablaSimbolosSecundaria();
			
			if(X.getTipo().compareTo(error) != 0 && (X.getTipoReturn().compareTo(F1.getTipoReturn()) == 0 || X.getTipoReturn().compareTo(empty) == 0)) {
				pilaSemantica.push(X);
			}
			else if(X.getTipo().compareTo(error)==0){
				pilaSemantica.push(new SemanticoElem(error));
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: No coinciden los tipos de retorno de la funcion. ("+
				(X.getTipoReturn().compareTo(empty)==0?vacio:X.getTipoReturn()) +") con lo que deberia retornar "+
						F1.getTipoReturn()+". Linea: "+alexico.getNumLineas()+"\n");}
			break;
		}
		case 5:{
			SemanticoElem L = elemSem(4);
			SemanticoElem F2 = elemSem(8);
			popearN(2*n_consecuente[regla].getKey());
			int nTS = F2.getPosicionId();
			if(L.getTipo().compareTo(vacio) != 0) {
				tablaDeSimbolos.setNumParametros(nTS, L.getParametros().size());
				tablaDeSimbolos.setTipoParametros(nTS, L.getParametros());
			}
			else {
				tablaDeSimbolos.setNumParametros(nTS, 0);
				tablaDeSimbolos.setTipoParametros(nTS, new ArrayList<>());
			}
			
			
			alexico.setZonaDeclaracion(false);
			
			L.setTipoReturn(F2.getTipo());
			pilaSemantica.push(L);

			break;
		}
		case 6:{
			SemanticoElem J = elemSem(2);
			int nTS = Integer.parseInt(elemSem(4).getTipo());
			/*Ponerle el tipo de funcion a la ts*/
			tablaDeSimbolos.setTipo(nTS, funcion);
			tablaDeSimbolos.setTipoDevolucion(nTS, J.getTipo());
			
			tablaDeSimbolosSecundario = alexico.crearTablaSecundaria();
			alexico.setFuncion(true);
			String etiqueta = "Et"+tablaDeSimbolos.getLexema(nTS)+alexico.getNTablas();
			tablaDeSimbolos.setEtiqueta(nTS,  etiqueta);
			
			popearN(2*n_consecuente[regla].getKey());
			J.setPosicionId(nTS);
			pilaSemantica.push(J);
			break;
		}
		case 7:{
				SemanticoElem C = elemSem(2);
				SemanticoElem E = elemSem(6);
				popearN(2*n_consecuente[regla].getKey());
				if(E.getTipo().compareTo(bool) * C.getTipo().compareTo(ok) == 0) {
					pilaSemantica.push(C);
				}
				else if(E.getTipo().compareTo(bool) != 0){
					pilaSemantica.push(new SemanticoElem(error));
					erroresFile.escribir("Error Semantico: La expresion tiene que ser booleana. Linea: "+alexico.getNumLineas()+"\n");
				}
				else {
					pilaSemantica.push(new SemanticoElem(error));
					erroresFile.escribir("Error Semantico: La sentencia no es correcto. Linea: "+alexico.getNumLineas()+"\n");
				}
				break;
		}
		case 8:{
			SemanticoElem X = elemSem(6);
			SemanticoElem D = elemSem(2);
			
			popearN(2*n_consecuente[regla].getKey());
			if(X.getTipo().compareTo(ok) * D.getTipo().compareTo(ok) * X.getTipoReturn().compareTo(empty) * D.getTipoReturn().compareTo(empty) == 0) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else if(X.getTipo().compareTo(ok) * D.getTipo().compareTo(ok) == 0 && X.getTipoReturn().compareTo(empty) != 0 && alexico.enFuncion()) {
				pilaSemantica.push(X);
			}
			else if(X.getTipo().compareTo(ok) * D.getTipo().compareTo(ok) == 0 && D.getTipoReturn().compareTo(empty) != 0 && alexico.enFuncion()) {
				pilaSemantica.push(D);
			}
			else if((X.getTipoReturn().compareTo(empty) != 0 || D.getTipoReturn().compareTo(empty) != 0) && !alexico.enFuncion()){
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: Se ha intentado hacer un return fuera de una funcion. Linea: "+alexico.getNumLineas()+"\n");
			}
			else if(X.getTipo().compareTo(error) == 0) {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: La sentencia del if es incorrecta. Linea: "+alexico.getNumLineas()+"\n");
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: La sentencia del else es incorrecta. Linea: "+alexico.getNumLineas()+"\n");
			}
			break;
		}
		case 9:{
			SemanticoElem S = elemSem(2);
			popearN(2*n_consecuente[regla].getKey());
			if(S.getTipo().compareTo(ok) == 0 && S.getTipoReturn().compareTo(empty) == 0) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else if(S.getTipo().compareTo(ok) == 0 && S.getTipoReturn().compareTo(empty) != 0 && alexico.enFuncion()) {
				pilaSemantica.push(new SemanticoElem(ok, S.getTipoReturn()));
			}
			else if(S.getTipoReturn().compareTo(empty) != 0 && !alexico.enFuncion()){
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: Se ha intentado hacer un return fuera de una funcion. Linea: "+alexico.getNumLineas()+"\n");
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: La sentencia no es correcta. Linea: "+alexico.getNumLineas()+"\n");
			
			}
			break;
		}
		case 10:{
			SemanticoElem X = elemSem(4);
			popearN(2*n_consecuente[regla].getKey());
			if(X.getTipo().compareTo(ok) == 0 && X.getTipoReturn().compareTo(empty) == 0) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else if(X.getTipo().compareTo(ok) == 0 && X.getTipoReturn().compareTo(empty) != 0 && alexico.enFuncion()) {
				pilaSemantica.push(new SemanticoElem(ok, X.getTipoReturn()));
			}
			else if(X.getTipoReturn().compareTo(empty) != 0 && !alexico.enFuncion()){
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: Se ha intentado hacer un return fuera de una funcion. Linea: "+alexico.getNumLineas()+"\n");
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: La sentencia del else no es correcta. Linea: "+alexico.getNumLineas()+"\n");
			
			}
			break;
		}
		case 12:{
			SemanticoElem K = elemSem(4);
			int nTS = Integer.parseInt(elemSem(6).getTipo());
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(ok));
			//Asignar el tipo en la ts
			if(nTS>0 && tablaDeSimbolos.getTipo(nTS).compareTo("") == 0) {
				tablaDeSimbolos.setTipo(nTS, K.getTipo());
				tablaDeSimbolos.setDesplazamiento(nTS);
			}
			else if(nTS<0 && tablaDeSimbolosSecundario.getTipo(-nTS).compareTo("") == 0) {
				tablaDeSimbolosSecundario.setTipo(Math.abs(nTS), K.getTipo());
				tablaDeSimbolosSecundario.setDesplazamiento(Math.abs(nTS));
			}
			break;
		}
		case 13:{
			SemanticoElem S = elemSem(2);
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(S);
			break;
		}
		case 14:{
			SemanticoElem E = elemSem(4);
			int nTS = Integer.parseInt(elemSem(8).getTipo());
			popearN(2*n_consecuente[regla].getKey());
			if(alexico.enTS(nTS) && alexico.tipoId(nTS).compareTo(E.getTipo()) == 0) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else if(!alexico.enTS(nTS)){
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El identificador no esta declarado. Linea: "+alexico.getNumLineas()+"\n");
			}
			else {	
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El tipo del identificador ("+alexico.tipoId(nTS)+") no coincide con el de la expresion ("+E.getTipo()+"). Linea: "+alexico.getNumLineas()+"\n");
			}
			break;
		}
		case 15:{
			int nTS = Integer.parseInt(elemSem(6).getTipo());
			popearN(2*n_consecuente[regla].getKey());
			if(alexico.enTS(nTS) && alexico.tipoId(nTS).compareTo(entero) == 0) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else if(!alexico.enTS(nTS)){
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El identificador no esta declarado. Linea: "+alexico.getNumLineas()+"\n");
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El identificador no es entero. Linea: "+alexico.getNumLineas()+"\n");
			}
			break;
		}
		case 16:{
			SemanticoElem E = elemSem(4);
			popearN(2*n_consecuente[regla].getKey());
			if((E.getTipo().compareTo(error) != 0 && (E.getTipo().compareTo(entero) == 0 || E.getTipo().compareTo(cadena) == 0))
					|| (E.getTipo().compareTo(funcion) == 0 && (E.getTipoReturn().compareTo(entero) == 0 || E.getTipoReturn().compareTo(cadena) == 0))) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: La expresion put necesita o un entero o una cadena y lo que ha recibido es: "+E.getTipo()+". Linea: "+alexico.getNumLineas()+"\n");
			}
			break;
		}
		case 17:{
			int nTS = Integer.parseInt(elemSem(4).getTipo());
			popearN(2*n_consecuente[regla].getKey());
			if(alexico.enTS(nTS) && alexico.tipoId(nTS).compareTo(entero) * alexico.tipoId(nTS).compareTo(cadena) == 0) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else if(!alexico.enTS(nTS)){
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El identificador no esta declarado. Linea: "+alexico.getNumLineas()+"\n");
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: La expresion get necesita o un entero o una cadena y el identificador no es entero ni cadena. Linea: "+alexico.getNumLineas()+"\n");
			}
			break;
		}
		case 18:{
			SemanticoElem Q = elemSem(4);
			popearN(2*n_consecuente[regla].getKey());
			if(!alexico.enFuncion()) {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: No se puede retornar porque no se esta en una funcion. Linea: "+alexico.getNumLineas()+"\n");
			}
			else if(Q.getTipo().compareTo(empty) == 0) {
				pilaSemantica.push(new SemanticoElem(ok, vacio));
			}
			else {
				pilaSemantica.push(new SemanticoElem(ok, Q.getTipoReturn()));
			}
			break;
		}
		case 19:{
			int nTS = Integer.parseInt(elemSem(10).getTipo());
			SemanticoElem N = elemSem(6);
			popearN(2*n_consecuente[regla].getKey());
			if(!tablaDeSimbolos.containsKey(nTS)) {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El identificador no existe. Linea: "+alexico.getNumLineas()+"\n");
				
			}
			else if(alexico.tipoId(nTS).compareTo(funcion) != 0) {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El identificador no es una funcion. Linea: "+alexico.getNumLineas()+"\n");
			}
			else if(comprobarParametros(alexico.parametros(nTS), N.getParametros()) && (this.tablaDeSimbolos.getNumParametros(nTS)==0 || alexico.tipoReturn(nTS).compareTo(vacio) == 0)) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else {
				if(alexico.tipoReturn(nTS).compareTo(vacio) == 0) {
					erroresFile.escribir("Error Semantico: No guardas el valor que devuelve la funcion. Linea: "+alexico.getNumLineas()+"\n");
				}
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: Se esperaban distintos tipos de argumentos para la funcion. Linea: "+alexico.getNumLineas()+"\n");
			}

			break;
		}
		case 20:{
			SemanticoElem Z = elemSem(2);
			SemanticoElem E = elemSem(6);
			if(Z.getTipo().compareTo(bool) + E.getTipo().compareTo(bool) == 0) {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(Z.getTipo()));
			}
			else {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(error));
				if(regla == 20) {
					erroresFile.escribir("Error Semantico: El && solo acepta valores booleanos.");

				}
				else {
					erroresFile.escribir("Error Semantico: El < solo acepta valores booleanos.");

				}
			}
			break;
		}
		case 22:{
			SemanticoElem H = elemSem(2);
			SemanticoElem G = elemSem(6);
			if(H.getTipo().compareTo(entero) + G.getTipo().compareTo(entero) == 0) {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(bool));
			}
			else {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El < solo acepta valores enteros.");
			}
			break;
		}
		case 24:{
			SemanticoElem H = elemSem(2);
			SemanticoElem G = elemSem(6);
			if(H.getTipo().compareTo(entero) + G.getTipo().compareTo(entero) == 0) {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(H.getTipo()));
			}
			else {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: El - solo acepta valores enteros.");
			}
			break;
		}
		case 26:{
			int nTS = Integer.parseInt(elemSem(4).getTipo());
			SemanticoElem Y = elemSem(2);
			popearN(2*n_consecuente[regla].getKey());
			if(Y.getTipo().compareTo(empty)==0) {
				if(nTS>0) {
					pilaSemantica.push(new SemanticoElem(tablaDeSimbolos.getTipo(nTS)));
				}
				else {
					pilaSemantica.push(new SemanticoElem(tablaDeSimbolosSecundario.getTipo(-nTS)));
				}
				
			}
			else if(alexico.tipoId(nTS).compareTo(funcion) !=0){
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: La variable deberia ser una funcion y no lo es. Linea: "+alexico.getNumLineas()+"\n");
			}
			else if((Y.getTipo().compareTo(vacio)==0) || (Y.getTipo().compareTo(ok) == 0 && comprobarParametros(alexico.parametros(nTS), Y.getParametros()))) {
				pilaSemantica.push(new SemanticoElem(alexico.tipoReturn(nTS)));
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: Se esperaban distintos tipos de argumentos para la funcion. Linea: "+alexico.getNumLineas()+"\n");
			}

			break;
		}
		case 27:{
			SemanticoElem E = elemSem(4);
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(E.getTipo()));

			break;
		}
		case 28:{
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(entero));
			break;
		}
		case 29:{
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(cadena));
			break;
		}
		case 30:{
			SemanticoElem N = elemSem(4);
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(N.getTipo(),N.getParametros()));

			break;
		}
		case 31:{
			SemanticoElem E = elemSem(2);
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(E.getTipo()));
			break;
		}
		case 34:
		case 37:{
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(vacio));
			break;
		}
		case 35:{
			SemanticoElem T = elemSem(4);
			SemanticoElem I = elemSem(2);
			if(I.getTipo().compareTo(empty) == 0 || I.getTipo().compareTo(T.getTipo()) == 0) {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(T.getTipo()));
			}
			else {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: Error en la asignacion. Linea: "+alexico.getNumLineas()+"\n");
			}
			break;
		}
		case 36:
		case 38:{
			SemanticoElem M = elemSem(2);
			int nTS = Integer.parseInt(elemSem(4).getTipo());
			SemanticoElem T = elemSem(6);
			ArrayList<String> parametrosNuevos;
			popearN(2*n_consecuente[regla].getKey());
			parametrosNuevos = M.getParametros();
			parametrosNuevos.add(0, T.getTipo());
			if(nTS>0) {
				tablaDeSimbolos.setTipo(nTS, T.getTipo());
				tablaDeSimbolos.setDesplazamiento(nTS);
			}
			else {
				tablaDeSimbolosSecundario.setTipo(Math.abs(nTS), T.getTipo());
				tablaDeSimbolosSecundario.setDesplazamiento(Math.abs(nTS));
			}
			
			pilaSemantica.push(new SemanticoElem(ok, parametrosNuevos));
			break;
		}

		case 39:
		case 43:{
			ArrayList<String> lista = new ArrayList<>();
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(empty,lista));
			break;
		}
		case 40:
		case 42:{
			SemanticoElem O = elemSem(2);
			SemanticoElem E = elemSem(4);
			ArrayList<String> parametrosNuevos;
			if (E.getTipo().compareTo(error) + O.getTipo().compareTo(error) != 0) {
				parametrosNuevos = O.getParametros();
				parametrosNuevos.add(0, E.getTipo());
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(ok, parametrosNuevos));
			} 
			else {
				popearN(2*n_consecuente[regla].getKey());
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: Hay un error con respecto a los parametros de paso. Linea: "+alexico.getNumLineas()+"\n");
			}
			break;
		}

		case 41:{
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(vacio));
			break;
		}
		case 21:
		case 23:
		case 25:
		case 33:{
			SemanticoElem aux = elemSem(2);
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(aux.getTipo()));
			break;
		}

		case 44:{
			SemanticoElem E = elemSem(2);
			popearN(2*n_consecuente[regla].getKey());
			if(E.getTipo().compareTo(error) != 0) {
				pilaSemantica.push(new SemanticoElem(ok, E.getTipo()));
			}
			else {
				pilaSemantica.push(new SemanticoElem(error, E.getTipo()));
			}
			break;
		}

		case 45:{
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(ok, vacio));
			break;
		}
		case 46:{
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(cadena));
			break;
		}
		case 47: {
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(entero));
			break;
		}
		//T -> bool
		case 48: {
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(bool));
			break;
		}
		case 49:{
			SemanticoElem X = elemSem(2);
			SemanticoElem B = elemSem(4);
			Boolean allOk = X.getTipo().compareTo(ok)+B.getTipo().compareTo(ok) == 0;
			popearN(2*n_consecuente[regla].getKey());
			if(allOk && X.getTipoReturn().compareTo(empty)+B.getTipoReturn().compareTo(empty)==0) {
				pilaSemantica.push(new SemanticoElem(ok));
			}
			else if(allOk && X.getTipoReturn().compareTo(empty)*B.getTipoReturn().compareTo(empty)!=0 && B.getTipoReturn().compareTo(X.getTipoReturn())==0) {
				pilaSemantica.push(B);
			}
			else if(allOk && X.getTipoReturn().compareTo(empty)*B.getTipoReturn().compareTo(empty)!=0 && B.getTipoReturn().compareTo(X.getTipoReturn())!=0) {
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: No coinciden los returns "+B.getTipoReturn()+" con "+X.getTipoReturn()+ " cuando deberia serlo. Linea: "+alexico.getNumLineas()+"\n");
			}
			else if(allOk && X.getTipoReturn().compareTo(empty) == 0 && B.getTipoReturn().compareTo(empty)!=0) {
				pilaSemantica.push(B);
			}
			else if(allOk && X.getTipoReturn().compareTo(empty) != 0 && B.getTipoReturn().compareTo(empty)==0) {
				pilaSemantica.push(X);
			}
			else if(allOk){
				pilaSemantica.push(new SemanticoElem(error));
				erroresFile.escribir("Error Semantico: No coinciden los returns "+B.getTipoReturn()+" con "+X.getTipoReturn()+ " cuando deberia serlo. Linea: "+alexico.getNumLineas()+"\n");
			}
			else {
				pilaSemantica.push(new SemanticoElem(error));
			}
			break;
		}
		case 50:{
			popearN(2*n_consecuente[regla].getKey());
			pilaSemantica.push(new SemanticoElem(ok));
			break;
		}

		default:
			pilaSemantica.push(new SemanticoElem("Algo"));
		}
	}
	public void run() {
		tablaDeSimbolos = alexico.crearTablaSimbolos();
		String sigToken = alexico.nextToken();
		int estado = 0;
		int nColumna;
		String accion = "";
		String next = "";
		int cod = 0;
		int reglaReducc = 0;
		boolean end = false;
		String prevToken = "";
		pila.push("0");
		pilaSemantica.push(new SemanticoElem());

		while(!end) {

			if (sigToken.isEmpty()){
				erroresFile.escribir("Error Sintactico: THE TOKEN IS EMPTY");
				break;
			}

			cod = Integer.parseInt(sigToken.substring(sigToken.indexOf("<")+1, sigToken.indexOf(",")));
			switch (cod){
			case 11: {
				next = "boolean";
				nColumna = 10; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 106: {
				next = "else";
				nColumna = 12; 
				break;
			}
			case 101: {
				next = "function";
				nColumna = 14; 
				alexico.setZonaDeclaracion(true);
				break;
			}
			case 103: {
				next = "get";
				nColumna = 15; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 105: {
				next = "if";
				nColumna = 17; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 10: {
				next = "int";
				nColumna = 18; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 102: {
				next = "let";
				nColumna = 19; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				alexico.setZonaDeclaracion(true);
				break;
			}
			case 104: {
				next = "put";
				nColumna = 20; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 107: {
				next = "return";
				nColumna = 21; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 12: {
				next = "string";
				nColumna = 22; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 13: {
				next = "void";
				nColumna = 23; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 304: {
				next = "++";
				nColumna = 4; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 402: {
				next = "entero";
				nColumna = 13; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 401: {
				next = "cadena";
				nColumna = 11; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 201: {
				next = "=";
				nColumna = 9; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 202: {
				next = ",";
				nColumna = 5; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 203: {
				next = ";";
				nColumna = 7; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				alexico.setZonaDeclaracion(false);
				break;
			}
			case 204: {
				next = "(";
				nColumna = 2; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 205: {
				next = ")";
				nColumna = 3; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 206: {
				next = "{";
				nColumna = 24; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 207: {
				next = "}";
				nColumna = 25; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 301: {
				next = "-";
				nColumna = 6; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 303: {
				next = "&&";
				nColumna = 1; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 302: {
				next = "<";
				nColumna = 8; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			case 666: {
				next = "EOF";
				nColumna = 0; //Buscar en que columna est� el token boolean, que es empezando desde el 0, el 10.
				break;
			}
			//Poner aqui todos los dem�s terminales segun nuestra gramatica de tokens.
			case 200: {
				next = "id";
				nColumna = 16;
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + cod);
			}
			accion = ACCION[estado][nColumna];

			

			if(!accion.isEmpty() && accion.charAt(0) == 'd') {
				pila.push(next);
				pila.push(accion.substring(1));
				if(next.compareTo("id")==0) {
					pilaSemantica.push(new SemanticoElem(sigToken.substring(sigToken.indexOf(",")+2, sigToken.indexOf(">"))));
				}
				else {
					pilaSemantica.push(new SemanticoElem("-"));
				}
				
				prevToken = String.valueOf(sigToken);

				estado = Integer.parseInt(accion.substring(1));
				sigToken = alexico.nextToken();

			}
			else if(!accion.isEmpty() && accion.charAt(0) == 'r' ){

				reglaReducc = Integer.parseInt(accion.substring(1));
				int reglaReduccMasUno = reglaReducc +1;

				parseFile.escribir(reglaReduccMasUno+"\t");

				int numDeConsecuentes = n_consecuente[reglaReducc].getKey();
				String antecedente = n_consecuente[reglaReducc].getValue();

				//Quitar todos los consecuentes por 2 de la pila.
				for(int i = 1 ; i<=2*numDeConsecuentes; i++) {
					pila.pop();
				}
				int aux = Integer.parseInt(pila.top());
				//Aniadir el antecedente
				pila.push(antecedente);
				System.err.println(aux+ "  "+mp.get(pila.top())+ " "+antecedente);
				accionSemantica(reglaReducc);
				estado = Integer.parseInt(GOTO[aux][mp.get(pila.top())]);
				pila.push(Integer.toString(estado));
			}
			else if(!accion.isEmpty() && accion.compareTo("aceptar") == 0) {
				pila.pop();
				parseFile.escribir("1");
				end = true;
				return ;
			}
			else {
				
				erroresFile.escribir("Error Sintactico: Error en la linea "+alexico.getNumLineas()+ 
						" No se esperaba que tras: " + convertirNormal(prevToken) + " hubiera " + convertirNormal(sigToken)+"\n");
				break;
			}
			//No tiene significado semantico, son los estados.
			pilaSemantica.push(new SemanticoElem());
			viewLIFO(); //Para ver como va evolucionando la pila

		}

	}


	public void crearAccion(String direccionInput) {
		FicheroInput codeFile = new FicheroInput(direccionInput);

		int asciiChar = codeFile.nextASCIIChar();
		int i=0, j=0;
		String aux="";
		boolean asignar = true;

		while(asciiChar != -1) {
			asignar = false;
			aux="";
			switch (asciiChar){
			case 9: {
				ACCION[i][j] = "";
				asignar = true;
				break;
			}
			case 10: break;
			case 13: {
				ACCION[i][j] = "";
				asignar = true;
				break;
			}
			case 97: {
				aux+= (char) (asciiChar);
				codeFile.leer();
				while(codeFile.nextASCIIChar() >=97 && codeFile.nextASCIIChar() <= 122) {
					aux+= (char)(codeFile.nextASCIIChar());
					codeFile.leer();
				}
				//sin terminar, para r24 por ejemplo
				ACCION[i][j] = aux;
				asignar = true;
				break;
			}
			case 100:
			case 114: {
				aux+= (char) (asciiChar);
				codeFile.leer();
				while(codeFile.nextASCIIChar() >=48 && codeFile.nextASCIIChar() <= 57) {
					aux+= (char)(codeFile.nextASCIIChar());
					codeFile.leer();
				}
				//sin terminar, para r24 por ejemplo
				ACCION[i][j] = aux;
				asignar = true;
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + asciiChar);
			}

			//System.out.println(asciiChar + " Que es el carater \""+(char)asciiChar+"\" "+" \n");
			//Resultados: 13 y 10 son saltos de linea, mientras que 9 es blank space, 114 es r, 100 es d
			if(asciiChar != 100 || asciiChar != 114) {
				codeFile.leer();
			}
			asciiChar = codeFile.nextASCIIChar();
			if(asignar) {
				//Avanzar a la siguiente casilla
				if(j<25) {
					j++;
				}else {
					i++;
					j=0;
				}
			}

		}
		codeFile.cerrarFichero();

	}
	public void crearGOTO(String direccionInput) {
		FicheroInput codeFile = new FicheroInput(direccionInput);

		int asciiChar = codeFile.nextASCIIChar();
		int i=0, j=0;
		String aux = "";
		boolean asignar = true;

		while(asciiChar != -1) {

			asignar = false;
			aux = "";
			if(codeFile.nextASCIIChar() >=48 && codeFile.nextASCIIChar() <= 57) {
				aux += (char)(asciiChar);
				codeFile.leer();
				while(codeFile.nextASCIIChar() >=48 && codeFile.nextASCIIChar() <= 57) {
					aux += (char)(codeFile.nextASCIIChar());
					codeFile.leer();
				}
				GOTO[i][j] = aux;
				asignar = true;
			}
			else {
				switch (asciiChar){
				case 9: {
					GOTO[i][j] = "";
					asignar = true;
					break;
				}
				case 10: {
					break;
				}
				case 13: {
					GOTO[i][j] = "";
					asignar = true;
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + asciiChar);
				}
			}
			codeFile.leer();
			asciiChar = codeFile.nextASCIIChar();
			if(asignar) {
				//Avanzar a la siguiente casilla
				if(j<22) {
					j++;
				}else if(i<99){
					i++;
					j=0;
				}
			}
		}codeFile.cerrarFichero();

	}

	public void imprimir(String file) {
		FicheroOutput ficheroOutput = new FicheroOutput(file);

		for(int i=0 ; i<99 ; i++) {
			for(int j=0 ; j<26 ; j++) {
				ficheroOutput.escribir( ACCION[i][j]+"\t"); 
			}
			ficheroOutput.escribir("\n");

		}


		for(int i=0 ; i<99 ; i++) {
			for(int j=0 ; j<23 ; j++) {
				ficheroOutput.escribir(GOTO[i][j]+"\t");
			}
			ficheroOutput.escribir("\n");
		}
		System.out.println("El elemento indicado es: "+GOTO[58][9]);
		ficheroOutput.cerrarFichero();
	}

	private void viewLIFO() {
		Stack<String> temp = new Stack<>();
		Stack<SemanticoElem> temp2 = new Stack<>();
		while(!pila.isEmpty()){
			System.out.print(pila.top()+" "+pilaSemantica.top().getTipo()+"\n");
			temp.push(pila.pop());
			temp2.push(pilaSemantica.pop());
		}
		while(!temp.isEmpty()){
			pila.push(temp.pop());
			pilaSemantica.push(temp2.pop());
		}
		System.out.println("\n\n");
	}

	private String convertirNormal(String token){
		int cod = Integer.parseInt(token.substring(token.indexOf("<")+1, token.indexOf(",")));
		String res;
		switch (cod){
		case 11: {
			res = "boolean";
			break;
		}
		case 106: {
			res = "else";
			break;
		}
		case 101: {
			res = "function";
			break;
		}
		case 103: {
			res = "get";
			break;
		}
		case 105: {
			res = "if";
			break;
		}
		case 10: {
			res = "int";
			break;
		}
		case 102: {
			res = "let";
			break;
		}
		case 104: {
			res = "put";
			break;
		}
		case 107: {
			res = "return";
			break;
		}
		case 12: {
			res = "string";
			break;
		}
		case 13: {
			res = "void";
			break;
		}
		case 304: {
			res = "++";
			break;
		}
		case 402: {
			res = "entero";
			break;
		}
		case 401: {
			res = "cadena";
			break;
		}
		case 201: {
			res = "=";
			break;
		}
		case 202: {
			res = ",";
			break;
		}
		case 203: {
			res = ";";
			break;
		}
		case 204: {
			res = "(";
			break;
		}
		case 205: {
			res = ")";
			break;
		}
		case 206: {
			res = "{";
			break;
		}
		case 207: {
			res = "}";
			break;
		}
		case 301: {
			res = "-";
			break;
		}
		case 303: {
			res = "&&";
			break;
		}
		case 302: {
			res = "<";
			break;
		}
		case 666: {
			res = "EOF";
			break;
		}
		case 200: {
			int pos = Integer.parseInt(token.substring(token.indexOf(",")+2, token.indexOf(">")));
			if(pos >0){
                res = tablaDeSimbolos.getLexema(pos);
            }else{
                if(pos == 0){
                     res= "id";
                }else{
                    res = tablaDeSimbolosSecundario.getLexema(-pos);
                }

            }break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + cod);

		}

		return res;
	}

	private SemanticoElem elemSem(int posiciones) {
		SemanticoElem res;
		int i = 0;
		Stack<SemanticoElem> temp = new Stack<>();
		while(!pilaSemantica.isEmpty() && i<posiciones){
			temp.push(pilaSemantica.pop());
			i++;
		}

		res = temp.peek();

		while(!temp.isEmpty()){
			pilaSemantica.push(temp.pop());
		}
		return res;
	}

	private void popearN(int n) {
		for(int i=0 ; i < n ; i++) {
			pilaSemantica.pop();
		}
	}

	private boolean comprobarParametros(ArrayList<String> lista1, ArrayList<String> lista2) {
		boolean res = true;
		if(lista1 == null && lista2 != null && lista2.size()==0) {
			return true;
		}
		if(lista2 == null && lista1 != null && lista1.size()==0) {
			return true;
		}
		if(lista1.size() != lista2.size()) {
			return false;
		}
		int i=0;
		while(i<lista1.size() && res) {
			if(lista1.get(i).compareTo(lista2.get(i)) != 0) {
				res = false;
			}
			i++;
		}
		return res;
	}

	
	public void closeFile() {
		this.parseFile.cerrarFichero();
	}
}
