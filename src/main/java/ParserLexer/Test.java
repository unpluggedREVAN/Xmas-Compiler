package ParserLexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

public class Test {
    public static void main(String[] args) {
        try {
            // Ruta al archivo de prueba
            String archivoEntrada = "src/main/resources/ejemplo.txt";

            // Crear el lexer y el parser
            Reader reader = new BufferedReader(new FileReader(archivoEntrada));
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(lexer);

            // Ejecutar el análisis
            System.out.println("Iniciando el análisis...");
            parser.parse();
            System.out.println("Análisis completado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error durante el análisis:");
            e.printStackTrace();
        }
    }
}
