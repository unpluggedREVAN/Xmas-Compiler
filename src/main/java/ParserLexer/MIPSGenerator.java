package ParserLexer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MIPSGenerator {

    private SymbolTableManager symbolTable;
    private List<String> asignaciones;  // Lista de asignaciones (puede usarse para chequeos adicionales)
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
            // Sección .data: Declaraciones
            // ---------------------------
            pw.println(".data");
            // Recorremos los símbolos del scope "global" para reservar espacio
            List<SymbolTableManager.SymbolData> globalSymbols = symbolTableSymbolsInScope("global");
            if (globalSymbols != null) {
                for (SymbolTableManager.SymbolData sd : globalSymbols) {
                    // Generar la directiva adecuada según el tipo declarado
                    if (sd.type.equals("int")) {
                        pw.println(sd.lexeme + ": .word 0");
                    } else if (sd.type.equals("float")) {
                        pw.println(sd.lexeme + ": .float 0.0");
                    } else if (sd.type.equals("bool")) {
                        // Usamos .word para booleanos (0 para false, 1 para true)
                        pw.println(sd.lexeme + ": .word 0");
                    } else if (sd.type.equals("char")) {
                        // Reservamos un byte para un char
                        pw.println(sd.lexeme + ": .byte 0");
                    } else if (sd.type.equals("string")) {
                        // Para cadenas, se reserva espacio con .asciiz (se puede modificar para almacenar el literal real)
                        pw.println(sd.lexeme + ": .asciiz \"\"");
                    }
                    // Si se necesitan otros tipos o arrays, se pueden agregar más casos aquí.
                }
            }
            // Se pueden agregar directivas de alineación o constantes, según se requiera.

            // ---------------------------
            // Sección .text: Código
            // ---------------------------
            pw.println("\n.text");
            pw.println(".globl main");
            pw.println("main:");
            // Se escriben todas las instrucciones acumuladas en la lista codigoMIPS
            for (String instr : codigoMIPS) {
                pw.println(instr);
            }
            // Instrucciones finales para terminar la ejecución del programa
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
