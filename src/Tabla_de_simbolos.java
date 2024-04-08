import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Tabla_de_simbolos {
	private Map<Integer, ArgumentosTS> tablaDeSimbolos;
	private int desplazamientoAcumulado;
	
	public Tabla_de_simbolos() {
		tablaDeSimbolos = new HashMap<>();
		desplazamientoAcumulado = 0;
	}
	public Tabla_de_simbolos(Tabla_de_simbolos tsCopia) {
		tablaDeSimbolos = tsCopia.tablaDeSimbolos;
		desplazamientoAcumulado = tsCopia.desplazamientoAcumulado;
	}
	
	
	public boolean containsKey(Integer key) {
		return tablaDeSimbolos.containsKey(key);
	}
	
	public boolean containsLexema(String lexema) {
		boolean res = false;
		
		Iterator<ArgumentosTS> it = tablaDeSimbolos.values().iterator();
		while(it.hasNext() && !res) {
			res = it.next().getLexema().compareTo(lexema) == 0;
		}
		return res;
	}

	public int getDesplazamientoAcumulado(){
		return desplazamientoAcumulado;
	}
	
	public int getPos(String lexema) {
		int res = 0;
		
		Iterator<Entry<Integer, ArgumentosTS>> it = tablaDeSimbolos.entrySet().iterator();
		Entry<Integer, ArgumentosTS> entry;
		while(it.hasNext() && res==0) {
			entry = it.next();
			if(entry.getValue().getLexema().compareTo(lexema) == 0) {
				res = entry.getKey();
			}
		}
		return res;
	}
	
	public void add(Integer pos, ArgumentosTS argumentos) {
		tablaDeSimbolos.put(pos, argumentos);
	}
	
	public boolean isEmpty() {
		return tablaDeSimbolos.isEmpty();
	}

	public String getLexema(Integer pos) {
		return tablaDeSimbolos.get(pos).getLexema();
	}
	

	public String getTipo(Integer pos){
		return tablaDeSimbolos.get(pos).getTipo();
	}


	public boolean esFuncion(Integer pos){

		return tablaDeSimbolos.get(pos).esFuncion();
	}

	public int getDesplazamiento(Integer pos){


		return tablaDeSimbolos.get(pos).getDesplazamiento();
	}


	public int getNumParametros(Integer pos){


		return tablaDeSimbolos.get(pos).getnParametros();
	}

	public ArrayList<String> getTipoParametros(Integer pos){

		return tablaDeSimbolos.get(pos).getParametros();
	}

	public String getTipoDevolucion(Integer pos){

		return tablaDeSimbolos.get(pos).getTipoReturn();
	}
	
	
	public void setTipo(Integer pos, String tipo){
		tablaDeSimbolos.get(pos).setTipo(tipo);
		
	}

	
	public void setDesplazamiento(Integer pos){
		tablaDeSimbolos.get(pos).setDesplazamiento(desplazamientoAcumulado);
		String tipo = tablaDeSimbolos.get(pos).getTipo();
		desplazamientoAcumulado += (tipo.equals("Entero") ? 1 :  (tipo.equals("Cadena") ? 64 : (tipo.equals("Boleano") ? 1: 0)));	
	}
	

	public void setNumParametros(Integer pos, int nParametros){
		tablaDeSimbolos.get(pos).setnParametros(nParametros);
	}

	public void setTipoParametros(Integer pos, ArrayList<String> parametros){

		tablaDeSimbolos.get(pos).setParametros(parametros);
	}

	public void setTipoDevolucion(Integer pos, String tipoReturn){

		tablaDeSimbolos.get(pos).setTipoReturn(tipoReturn);
	}
	
	public String getEtiqueta(Integer pos){

        return tablaDeSimbolos.get(pos).getEtiquetaFun();
    }
	
	public void setEtiqueta(Integer pos,String etiqueta) {
		tablaDeSimbolos.get(pos).setEtiqueta(etiqueta);
	}

    public void escribePrincipal(FicheroOutput tSFile) {
		ArgumentosTS elSiguiente;
		Iterator<ArgumentosTS> it = tablaDeSimbolos.values().iterator();
		tSFile.escribir("CONTENIDOS DE LA TABLA PRINCIPAL # 1 :\r");
		while(it.hasNext()){

			elSiguiente= it.next();

			tSFile.escribir("* LEXEMA : '"+elSiguiente.getLexema()+"'\n");
			tSFile.escribir("  + tipo : '"+elSiguiente.getTipo()+"'\n");
			if(elSiguiente.getTipo().compareTo("Funcion") == 0) {
				tSFile.escribir("  + EtiquetaFuncion : '"+elSiguiente.getEtiquetaFun()+"'\n");
				tSFile.escribir("  + TipoRetorno : '"+elSiguiente.getTipoReturn()+"'\n");
				tSFile.escribir("  + numParametros : '"+elSiguiente.getnParametros()+"'\n");
				ArrayList<String> param = elSiguiente.getParametros();
				Iterator<String> iterator = param.iterator();
				int nparam = 1;
				while(iterator.hasNext()) {
					tSFile.escribir("  + TipoParam"+nparam+" : '"+iterator.next()+"'\n");
					tSFile.escribir("  + ModoParam"+nparam+" : 'PorValor'\n");
					nparam++;	
				}
			}
			else {
				tSFile.escribir("  + despl : '"+elSiguiente.getDesplazamiento()+"'\n");
			}
			
		}
    }

	public void escribeSecundaria(FicheroOutput tSFile, int nTablas) {
		ArgumentosTS elSiguiente;
		Iterator<ArgumentosTS> it = tablaDeSimbolos.values().iterator();
		Iterator<String> it2;
		int nParametros = 1;

		tSFile.escribir("CONTENIDOS DE LA TABLA SECUNDARIA # " + nTablas+ " :\r");

		while(it.hasNext()){

			elSiguiente= it.next();

			tSFile.escribir("* LEXEMA : '"+elSiguiente.getLexema()+"'\n");
			tSFile.escribir("  + tipo : '"+elSiguiente.getTipo()+"'\n");

			if(elSiguiente.esFuncion()){
				// numParametros, tipoParametros, tipoDev, EtiqFuncion

				tSFile.escribir("  + numParametros : '"+elSiguiente.getnParametros()+"'\n");

				it2 = elSiguiente.getParametros().iterator();

				while(it2.hasNext()){

					if(nParametros >= 100){
						System.out.println("Toma demasiados parámetros"); //TODO
					}

					tSFile.escribir("  + tipoParametro"+nParametros+ " : '"+it2.next()+ "'\n");
					nParametros++;

				}
				
				tSFile.escribir("  + tipoRetorno : '"+elSiguiente.getTipoReturn()+"'\n");
				tSFile.escribir("  + EtiqFuncion : '"+elSiguiente.getEtiquetaFun()+"'\n");

			}else tSFile.escribir("  + despl : '"+elSiguiente.getDesplazamiento()+"'\n");
			
		}
    }
	
}