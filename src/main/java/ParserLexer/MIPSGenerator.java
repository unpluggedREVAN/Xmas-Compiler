package ParserLexer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MIPSGenerator {

    private SymbolTableManager symbolTable;
    private List<String> asignaciones;  // Lista de asignaciones (para chequeos extra)
    private List<String> codigoMIPS;    // Lista de instrucciones MIPS generadas en el parser

    /**
     * Constructor actualizado para recibir la tabla de símbolos, la lista de instrucciones MIPS
     * y la lista de asignaciones.
     */
    public MIPSGenerator(SymbolTableManager stm, List<String> codigoMIPS, List<String> asigns) {
        this.symbolTable = stm;
        this.codigoMIPS = codigoMIPS;
        this.asignaciones = asigns;
    }

    /**
     * Genera el archivo de código MIPS completo.
     * Se escribe la sección .data tomando como base la tabla de símbolos global,
     * y la sección .text utilizando las instrucciones acumuladas.
     */
    public void generateCode(String outputFile) {
        System.out.println("[CODEGEN] Generando código MIPS en: " + outputFile);

        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
            // ---------------------------
            // Sección .data: Declaración de variables y arreglos
            // ---------------------------
            pw.println(".data");
            // Recorremos los símbolos del scope "global"
            List<SymbolTableManager.SymbolData> globalSymbols = symbolTableSymbolsInScope("global");
            if (globalSymbols != null) {
                for (SymbolTableManager.SymbolData sd : globalSymbols) {
                    if (sd.isArray) {
                        // Reservar espacio para arreglos según el tipo base y tamaño.
                        if (sd.type.equals("int") || sd.type.equals("bool")) {
                            // Se asume 4 bytes por elemento.
                            pw.println(sd.lexeme + ": .space " + (sd.arraySize * 4));
                        } else if (sd.type.equals("float")) {
                            pw.println(sd.lexeme + ": .space " + (sd.arraySize * 4));
                        } else if (sd.type.equals("char")) {
                            // 1 byte por elemento.
                            pw.println(sd.lexeme + ": .space " + sd.arraySize);
                        } else if (sd.type.equals("string")) {
                            // Los arreglos de strings requieren un manejo especial; por ahora, se deja un comentario.
                            pw.println("# Arreglo de strings " + sd.lexeme + " requiere manejo especial");
                        }
                    } else {
                        // Variables escalares
                        if (sd.type.equals("int")) {
                            pw.println(sd.lexeme + ": .word " + (sd.value != null ? sd.value : 0));
                        } else if (sd.type.equals("float")) {
                            pw.println(sd.lexeme + ": .float " + (sd.value != null ? sd.value : 0.0));
                        } else if (sd.type.equals("bool")) {
                            // Convertir boolean a 1 (true) o 0 (false)
                            int boolVal = 0;
                            if (sd.value != null && sd.value instanceof Boolean) {
                                boolVal = ((Boolean) sd.value) ? 1 : 0;
                            }
                            pw.println(sd.lexeme + ": .word " + boolVal);
                        } else if (sd.type.equals("char")) {
                            // Reservar un byte para un char; se convierte el valor a ASCII.
                            String charVal = "";
                            if (sd.value != null) {
                                charVal = String.valueOf(sd.value);
                            }
                            pw.println(sd.lexeme + ": .byte " + (charVal.isEmpty() ? 0 : (int) charVal.charAt(0)));
                        } else if (sd.type.equals("string")) {
                            // Reservar espacio para la cadena usando .asciiz; si no hay valor se reserva cadena vacía.
                            String strVal = (sd.value != null) ? sd.value.toString() : "";
                            pw.println(sd.lexeme + ": .asciiz \"" + strVal + "\"");
                        }
                    }
                }
            }

            // ---------------------------
            // Sección .text: Código MIPS
            // ---------------------------
            pw.println("\n.text");
            pw.println("main:");
            for (String instr : codigoMIPS) {
                pw.println(instr);
            }
            // Instrucciones finales para terminar la ejecución del program
            pw.println("\n# Fin del programa - syscall de salida");
            pw.println("li $v0, 10");
            pw.println("syscall");

            System.out.println("[CODEGEN] Código MIPS generado satisfactoriamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna los símbolos de la tabla de símbolos que pertenecen a un scope dado.
     *
     * @param scopeName el nombre del scope (por ejemplo, "global")
     * @return la lista de símbolos asociados a ese scope
     */
    private List<SymbolTableManager.SymbolData> symbolTableSymbolsInScope(String scopeName) {
        return symbolTable.getTablaSimbolos().get(scopeName);
    }
}
