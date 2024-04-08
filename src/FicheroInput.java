

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FicheroInput {
	private File archivo;
	private FileReader fr;
	private BufferedReader br;
	public FicheroInput(String direccion) {
		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			archivo = new File (direccion);
			fr = new FileReader (archivo);
			br = new BufferedReader(fr);
		}catch(Exception e){
			System.out.println("Error en la inicializacion del fichero\n");
		}
	}
	
	
	public int nextASCIIChar() {
		int next=0;
		try {
			br.mark(1);
			next = br.read();
			br.reset();
		}
		catch(Exception e3) {
			System.out.println("Error en la lectura del siguiente caracter\n");
		}
		
		return next;
	}
	
	public void leer() {
		try {
			br.skip(1);
		} catch (IOException e4) {
			System.out.println("Error en el salto del caracter\n");
		}
	}
	
	public void cerrarFichero(){
		try{                    
			if( null != fr ){   
				fr.close();     
			}                  
		}catch (Exception e2){ 
			System.out.println("No se ha podido cerrar el fichero\n");
		}
	}
}
