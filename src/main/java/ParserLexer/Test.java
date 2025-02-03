package ParserLexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Test {
    // Ejecuta el análisis léxico y realiza una prueba del análisis sintáctico
    public void ejecutarAnalisis(String archivoEntrada) {
        try {
            // Se construye la ruta de entrada y salida
            Path rutaEntrada = Paths.get(archivoEntrada);
            String directorioSalida = rutaEntrada.getParent().toString();
            String archivoSalidaTokens = directorioSalida + "/output_tokens.txt";

            // Creación del lexer
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

            // Escribir la tabla de tokens en un archivo de salida
            escribirTokensEnArchivo(lexer.tokenTable, archivoSalidaTokens);
            System.out.println("\nOutput del scanner guardado en: " + archivoSalidaTokens);

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
                    parser.getAsignaciones()  // Se pasan las asignaciones recolectadas
            );
            semAnalyzer.runSemanticChecks();

            // ============== FASE DE GENERACIÓN DE CÓDIGO MIPS ==============
            System.out.println("Iniciando generación de código MIPS...");
            // Se crea la instancia de MIPSGenerator pasando:
            // 1. La tabla de símbolos (obtención con getSymbolTableManager())
            // 2. La lista de instrucciones MIPS
            // 3. La lista de asignaciones (getAsignaciones())
            MIPSGenerator mipsGen = new MIPSGenerator(
                    parser.getSymbolTableManager(),
                    parser.getCodigoMIPS(),
                    parser.getAsignaciones()
            );
            String archivoSalidaMIPS = directorioSalida + "/output.asm";
            mipsGen.generateCode(archivoSalidaMIPS);
            System.out.println("Generación de código finalizada en " + archivoSalidaMIPS);

        } catch (Exception e) {
            System.err.println("Error durante el análisis:");
            e.printStackTrace();
        }
    }

    /**
     * Escribe la tabla de tokens en un archivo de salida.
     * @param tokenTable Lista de tokens (cada token es un arreglo de String con lexema, tipo y posición)
     * @param archivoSalida Ruta del archivo de salida
     */
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

    // Método main para ejecutar el compilador
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
