package ParserLexer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MIPSGenerator {

    private SymbolTableManager symbolTable;
    private List<String> asignaciones;

    public MIPSGenerator(SymbolTableManager stm, List<String> asigns) {
        this.symbolTable = stm;
        this.asignaciones = asigns;
    }

    public void generateCode(String outputFile) {
        System.out.println("[CODEGEN] Generando código MIPS en: " + outputFile);

        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
            // Sección .data para variables globales
            pw.println(".data");
            // Podrías recorrer la tabla de símbolos en el scope "global" e
            // imprimir directivas para reservar espacio, por ejemplo:
            for (SymbolTableManager.SymbolData sd : symbolTableSymbolsInScope("global")) {
                // Asumimos un int -> .word 0
                if (sd.type.equals("int")) {
                    pw.println(sd.lexeme + ": .word 0");
                }
                // Otras reservas de espacio (char, float, etc.)
                // ...
            }

            pw.println("\n.text");
            pw.println(".globl main");
            pw.println("main:");

            // Generar un final sencillo: exit
            pw.println("  # Simplemente llamamos a syscall 10 para terminar");
            pw.println("  li $v0, 10");
            pw.println("  syscall");

            System.out.println("[CODEGEN] Código MIPS generado satisfactoriamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna los símbolos de la tabla en un scope dado.
     */
    private List<SymbolTableManager.SymbolData> symbolTableSymbolsInScope(String scopeName) {
        return symbolTable.getTablaSimbolos().get(scopeName);
    }
}
