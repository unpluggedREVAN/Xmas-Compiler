package ParserLexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Test {
    /**
     * Ejecuta el análisis léxico y sintáctico.
     * @param archivoEntrada Ruta al archivo de entrada.
     */
    public void ejecutarAnalisis(String archivoEntrada) {
        try {
            // Obtener la ruta de salida eliminando el nombre del archivo
            Path rutaEntrada = Paths.get(archivoEntrada);
            String directorioSalida = rutaEntrada.getParent().toString();
            String archivoSalida = directorioSalida + "/output_tokens.txt";

            // Crear el lexer
            Reader reader = new BufferedReader(new FileReader(archivoEntrada));
            Lexer lexer = new Lexer(reader);

            System.out.println("Tokens reconocidos:");
            while (true) {
                java_cup.runtime.Symbol token = lexer.next_token();
                if (token.sym == sym.EOF) break;

                String lexema = (token.value != null) ? token.value.toString() : lexer.yytext();
                String tipoToken = sym.terminalNames[token.sym];
                String posicion = token.left + ":" + token.right; // Línea:Columna

                System.out.println("Token: " + tipoToken + ", Valor: " + lexema + ", Posición: " + posicion);
            }

            // Escribir la tabla de tokens en el archivo de salida
            escribirTokensEnArchivo(lexer.tokenTable, archivoSalida);
            System.out.println("\nOutput del scanner escrito en: " + archivoSalida);

            // Reiniciar el lector para el parser
            reader = new BufferedReader(new FileReader(archivoEntrada));
            lexer = new Lexer(reader);
            Parser parser = new Parser(lexer);

            // Ejecutar el análisis sintáctico
            System.out.println("\nIniciando el análisis...");
            parser.parse();
            System.out.println("Análisis completado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error durante el análisis:");
            e.printStackTrace();
        }
    }

    /**
     * Método para escribir la tabla de tokens en un archivo con formato tabular.
     */
    private void escribirTokensEnArchivo(List<String[]> tokenTable, String archivoSalida) {
        try (PrintWriter writer = new PrintWriter(archivoSalida)) {
            // Encabezado de la tabla
            writer.printf("%-15s %-20s %-10s%n", "Lexema", "Tipo de Token", "Posición");
            writer.println("-------------------------------------------------------------");

            // Escribir cada fila de la tabla
            for (String[] fila : tokenTable) {
                writer.printf("%-15s %-20s %-10s%n", fila[0], fila[1], fila[2]);
            }
        } catch (Exception e) {
            System.err.println("Error al escribir en el archivo de salida:");
            e.printStackTrace();
        }
    }

    /**
     * Método principal que recibe el archivo de entrada como parámetro.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Debe proporcionar la ruta del archivo .txt como parámetro.");
            System.err.println("Uso: java ParserLexer.Test <ruta_archivo.txt>");
            return;
        }

        // Ruta del archivo de entrada
        String archivoEntrada = args[0];

        // Ejecutar el análisis
        Test analizador = new Test();
        analizador.ejecutarAnalisis(archivoEntrada);
    }
}
