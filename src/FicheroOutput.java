

import java.io.FileWriter;
import java.io.PrintWriter;

public class FicheroOutput {
	FileWriter fichero;
	PrintWriter pw;

	public FicheroOutput(String direccionOutput) {
		try
		{
			//Si se quiere escribir al inicio del fichero:
			fichero = new FileWriter(direccionOutput);

			//Si se quiere escribir al final del fichero:
			//FileWriter fichero = new FileWriter("c:/prueba.txt",true);  añadir true al final.
			pw = new PrintWriter(fichero);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void escribir(String cadena) {
		pw.print(cadena);
	}

	public void cerrarFichero() {
		try {
			// Nuevamente aprovechamos el finally para 
			// asegurarnos que se cierra el fichero.
			if (null != fichero)
				fichero.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
