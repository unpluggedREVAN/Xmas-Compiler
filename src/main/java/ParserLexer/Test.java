package ParserLexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Test {
    // ejecuta el análisis léxico y hay una prueba del sintáctico
    public void ejecutarAnalisis(String archivoEntrada) {
        try {
            // a la ruta hay que ponerle el nombre del archivo
            Path rutaEntrada = Paths.get(archivoEntrada);
            String directorioSalida = rutaEntrada.getParent().toString();
            String archivoSalida = directorioSalida + "/output_tokens.txt";

            // creación del lexer
            Reader reader = new BufferedReader(new FileReader(archivoEntrada));
            Lexer lexer = new Lexer(reader);

            System.out.println("Tokens reconocidos:");
            while (true) {
                java_cup.runtime.Symbol token = lexer.next_token();
                if (token.sym == sym.EOF) break;

                String lexema = (token.value != null) ? token.value.toString() : lexer.yytext();
                String tipoToken = sym.terminalNames[token.sym];
                String posicion = token.left + ":" + token.right; // Línea:Columna

                System.out.println("Tipo: " + tipoToken + ", Lexema: " + lexema + ", Posición: " + posicion);
            }

            // aquí escribe la tabla de tokens en un archivo de salida
            escribirTokensEnArchivo(lexer.tokenTable, archivoSalida);
            System.out.println("\nOutput del scanner guardado en: " + archivoSalida);

            // Reiniciar el lector para el parser
            reader = new BufferedReader(new FileReader(archivoEntrada));
            lexer = new Lexer(reader);
            Parser parser = new Parser(lexer);

            // Ejecutar el análisis sintáctico
            System.out.println("\nIniciando el análisis (parser)...");
            parser.parse();
            System.out.println("Análisis sintáctico completado.");

            // ============== FASE DE ANÁLISIS SEMÁNTICO ==============
            System.out.println("Iniciando análisis semántico...");
            SemanticAnalyzer semAnalyzer = new SemanticAnalyzer(
                    parser.getSymbolTableManager(),
                    parser.getAsignaciones() // la lista de asignaciones
            );
            semAnalyzer.runSemanticChecks();  // Realiza validaciones

            // ============== FASE DE GENERACIÓN DE CÓDIGO ==============
            System.out.println("Iniciando generación de código MIPS...");
            MIPSGenerator mipsGen = new MIPSGenerator(
                    parser.getSymbolTableManager(),
                    parser.getAsignaciones()
            );
            String codigoMIPSsalida = directorioSalida + "/output.asm";
            mipsGen.generateCode(codigoMIPSsalida);
            System.out.println("Generación de código finalizada en " + codigoMIPSsalida);

        } catch (Exception e) {
            System.err.println("Error durante el análisis:");
            e.printStackTrace();
        }
    }

    private void escribirTokensEnArchivo(List<String[]> tokenTable, String archivoSalida) {
        try (PrintWriter writer = new PrintWriter(archivoSalida)) {
            writer.printf("%-15s %-20s %-10s%n", "Lexema", "Tipo de Token", "Posición");
            writer.println("-------------------------------------------------------------");

            for (String[] fila : tokenTable) {
                writer.printf("%-15s %-20s %-10s%n", fila[0], fila[1], fila[2]);
            }
        } catch (Exception e) {
            System.err.println("Error al escribir en el archivo de salida:");
            e.printStackTrace();
        }
    }

    // main
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Debe proporcionar la ruta del archivo .txt como parámetro.");
            System.err.println("Uso: java ParserLexer.Test <ruta_archivo.txt>");
            return;
        }

        String archivoEntrada = args[0];
        Test analizador = new Test();
        analizador.ejecutarAnalisis(archivoEntrada);
    }
}
