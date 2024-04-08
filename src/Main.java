import java.util.HashMap;

public class Main {
	
	public static void main(String[] args){
		
		String codigoFuente = "ej2.txt";
		String ficheroToken = "FicheroToken.txt";
		String ficheroTS = "FicheroTS.txt";
		String ficheroParse = "FicheroParse.txt";
		String ficheroErrores = "FicheroErrores.txt";
		FicheroOutput erroresFile = new FicheroOutput(ficheroErrores);		
		
	    Analizador_lexico aLexico = new Analizador_lexico(codigoFuente, ficheroToken, ficheroTS, erroresFile);
	    Analizador_sintatico aSintatico = new Analizador_sintatico(ficheroParse, erroresFile, aLexico, "acciones.txt", "GOTO.txt");

	    aSintatico.run();
	    
		aLexico.printTS();
		aLexico.closeFiles();
		aSintatico.closeFile();
		
		erroresFile.cerrarFichero();

	}
}
