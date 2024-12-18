package org.example;

import ParserLexer.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    // Rutas a los archivos
    private static final String BASE_DIR = "src/main/java/ParserLexer";
    private static final String JFLEX_COMMAND = "java -jar lib/jflex-full-1.9.1.jar -d " + BASE_DIR + " src/main/jflex/Lexer.jflex";
    private static final String CUP_COMMAND = "java -jar lib/java-cup-11b.jar -destdir " + BASE_DIR + " -parser Parser src/main/cup/parser.cup";
    private static final String GENERATED_DIR = "src/main/generated";
    private static final String TEST_FILE = "src/main/resources/ejemplo.txt";
    private static final String OUTPUT_FILE = "src/main/resources/output_tokens.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Menú Principal =====");
            System.out.println("1. Generar el ParserLexer (borra los anteriores y genera nuevos)");
            System.out.println("2. Compilar los archivos");
            System.out.println("3. Probar el scanner");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            try {
                switch (opcion) {
                    case 1:
                        generarParserLexer();
                        break;
                    case 2:
                        compilarArchivos();
                        break;
                    case 3:
                        probarScanner();
                        break;
                    case 4:
                        System.out.println("Saliendo del programa. ¡Hasta luego!");
                        return;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (Exception e) {
                System.err.println("Ocurrió un error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Opción 1: Generar el Parser y Lexer
    private static void generarParserLexer() throws Exception {
        System.out.println("Eliminando archivos anteriores...");

        // Eliminar archivos existentes
        borrarArchivo(BASE_DIR + "/Lexer.java");
        borrarArchivo(BASE_DIR + "/Parser.java");
        borrarArchivo(BASE_DIR + "/sym.java");

        System.out.println("Generando Lexer...");
        ejecutarComando(JFLEX_COMMAND);

        System.out.println("Generando Parser...");
        ejecutarComando(CUP_COMMAND);

        System.out.println("Generación de ParserLexer completada.");
    }

    // Opción 2: Compilar los archivos
    private static void compilarArchivos() throws Exception {
        System.out.println("Compilando archivos...");
        String comandoCompilacion = "javac -cp \"lib/*\" -d " + GENERATED_DIR + " " +
                "src/main/java/org/example/*.java src/main/java/ParserLexer/*.java";
        ejecutarComando(comandoCompilacion);
        System.out.println("Compilación completada. Archivos generados en: " + GENERATED_DIR);
    }

    // Opción 3: Probar el Scanner
    private static void probarScanner() {
        Scanner scanner = new Scanner(System.in);

        // Pedir al usuario la ruta del archivo de entrada
        System.out.print("Ingrese la ruta del archivo de entrada (.txt): ");
        String rutaEntrada = scanner.nextLine();

        // Definir la ruta del archivo de salida
        String archivoSalida = "output_tokens.txt"; // Archivo de salida fijo

        // Verificar si el archivo de entrada existe
        if (!new File(rutaEntrada).exists()) {
            System.err.println("Error: El archivo no existe en la ruta proporcionada.");
            return;
        }

        System.out.println("Ejecutando análisis léxico y sintáctico...");
        Test analizador = new Test();
        analizador.ejecutarAnalisis(rutaEntrada);

        System.out.println("Análisis completado. Resultados guardados en: " + archivoSalida);
    }


    // Método para borrar archivos si existen
    private static void borrarArchivo(String ruta) {
        try {
            Path path = Paths.get(ruta);
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("Archivo eliminado: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("No se pudo eliminar el archivo: " + ruta);
        }
    }

    // Método para ejecutar comandos en la terminal
    private static void ejecutarComando(String comando) throws Exception {
        Process proceso = Runtime.getRuntime().exec(comando);
        proceso.waitFor();

        if (proceso.exitValue() == 0) {
            System.out.println("Comando ejecutado correctamente: " + comando);
        } else {
            System.err.println("Error al ejecutar el comando: " + comando);
        }
    }
}
