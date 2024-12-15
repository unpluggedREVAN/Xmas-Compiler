package ParserLexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        try {
            // Rutas a los archivos
            String archivoEntrada = "src/main/resources/ejemplo.txt";
            String archivoSalida = "src/main/resources/output_tokens.txt";

            // Tabla para almacenar los tokens
            List<String[]> tokenTable = new ArrayList<>();

            // Crear el lexer
            Reader reader = new BufferedReader(new FileReader(archivoEntrada));
            Lexer lexer = new Lexer(reader);

            System.out.println("Tokens reconocidos:");
            while (true) {
                java_cup.runtime.Symbol token = lexer.next_token();
                if (token.sym == sym.EOF) break;

                // Guardar cada token en la tabla
                String lexema = (token.value != null) ? token.value.toString() : lexer.yytext();
                String tipoToken = sym.terminalNames[token.sym];
                int linea = token.left + 1; // Línea donde aparece el token

                tokenTable.add(new String[]{lexema, tipoToken, String.valueOf(linea)});

                // Imprimir el token
                System.out.println("Token: " + tipoToken + ", Valor: " + lexema);
            }

            // Escribir la tabla de tokens en el archivo de salida
            escribirTokensEnArchivo(tokenTable, archivoSalida);
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
     *
     * @param tokenTable    Lista con los tokens reconocidos.
     * @param archivoSalida Ruta del archivo de salida.
     */
    private static void escribirTokensEnArchivo(List<String[]> tokenTable, String archivoSalida) {
        try (PrintWriter writer = new PrintWriter(archivoSalida)) {
            // Encabezado de la tabla
            writer.printf("%-15s %-20s %-10s%n", "Lexema", "Tipo de Token", "Línea");
            writer.println("--------------------------------------------------------");

            // Escribir cada fila de la tabla
            for (String[] fila : tokenTable) {
                writer.printf("%-15s %-20s %-10s%n", fila[0], fila[1], fila[2]);
            }
        } catch (Exception e) {
            System.err.println("Error al escribir en el archivo de salida:");
            e.printStackTrace();
        }
    }
}
