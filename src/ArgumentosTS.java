import java.util.ArrayList;

public class ArgumentosTS {
	private String tipo;
	private int desplazamiento;
	private String lexema;
	private int nParametros;
	private ArrayList<String> parametros;
	private String tipoReturn;
	private String etiquetaFun;

	//El primero es el tipo. El segundo es el desplazamiento. El tercero lexema

	//A partir de ah� son espec�ficos de funciones.
	// numParametros, tipoParametros, tipoDev, EtiqFuncion
	public ArgumentosTS(String lexema) {
		this.tipo = "";
		this.desplazamiento = 0;
		this.lexema = lexema;
		this.nParametros = 0;
		this.parametros = new ArrayList<>();
		this.tipoReturn = "";
		this.etiquetaFun = "Et"+lexema+"01"; //TODO
	}

	public ArgumentosTS() {
		this.tipo = "";
		this.desplazamiento = 0;
		this.lexema = "";
		this.nParametros = 0;
		this.parametros = new ArrayList<>();
		this.tipoReturn = "";
		this.etiquetaFun = "";
	}
	

	public boolean esFuncion() {
		return this.tipo.compareTo("Funcion")==0;
	}

	public String getTipo() {
		return tipo;
	}

	public int getDesplazamiento() {
		return desplazamiento;
	}

	public String getLexema() {
		return lexema;
	}

	public String getEtiquetaFun() {
		return etiquetaFun;
	}
	public int getnParametros() {
		return nParametros;
	}

	public ArrayList<String> getParametros() {
		return parametros;
	}

	public String getTipoReturn() {
		return tipoReturn;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
		/*
		 * 	private final String entero = "Entero";
		private final String cadena = "Cadena";
		private final String bool = "Boleano";
		 */
	}

	public void setDesplazamiento(int desplazamiento) {
		this.desplazamiento = desplazamiento;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public void setnParametros(int nParametros) {
		this.nParametros = nParametros;
	}

	public void setParametros(ArrayList<String> parametros) {
		this.parametros = parametros;
	}

	public void setTipoReturn(String tipoReturn) {
		this.tipoReturn = tipoReturn;
	}
	
	public void setEtiqueta(String etiqueta) {
		this.etiquetaFun = etiqueta;
	}
		
	
}