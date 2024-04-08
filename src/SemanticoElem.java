import java.util.ArrayList;

public class SemanticoElem {
	private String tipo;
	private String tipoReturn;
	private int posId = 0;
	private ArrayList<String> parametros;
	
	public SemanticoElem() {
		this.tipo = "empty";
		this.tipoReturn = "empty";
	}
	
	public SemanticoElem(String tipo) {
		this.tipo = tipo;
		this.tipoReturn = "empty";
	}
	public SemanticoElem(String tipo, String tipoReturn) {
		this.tipo = tipo;
		this.tipoReturn = tipoReturn;
	}
	public SemanticoElem(String tipo, ArrayList<String> parametros) {
		this.tipo = tipo;
		this.parametros = parametros;
	}

	public String getTipo() {
		return tipo;
	}

	public String getTipoReturn() {
		return tipoReturn;
	}
	public int getPosicionId() {
		return posId;
	}
	
	public ArrayList<String> getParametros() {
		return parametros;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setTipoReturn(String tipo) {
		this.tipoReturn = tipo;
	}
	
	public void setParametros(ArrayList<String> parametros) {
		this.parametros = parametros;
	}
	
	public void setPosicionId(int newPosId) {
		posId = newPosId;
	}
	
	
}
