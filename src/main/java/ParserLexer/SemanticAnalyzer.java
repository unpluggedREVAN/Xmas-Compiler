package ParserLexer;

import java.util.List;

/**
 * Realiza el análisis semántico usando la tabla de símbolos
 * proporcionada por el parser.
 */
public class SemanticAnalyzer {

    private SymbolTableManager symbolTable;

    // Aquí podrías almacenar estructuras intermediarias (por ejemplo, lista de asignaciones o un AST).
    private List<String> asignacionesSimples;
    // Imagina que el parser llena esta lista con entradas tipo: "assign(var=expr)"

    public SemanticAnalyzer(SymbolTableManager stm, List<String> asignacionesSimples) {
        this.symbolTable = stm;
        this.asignacionesSimples = asignacionesSimples;
    }

    public void runSemanticChecks() {
        System.out.println("\n[SEMANTIC] Iniciando chequeos semánticos...");

        checkAsignaciones();

        System.out.println("[SEMANTIC] Análisis semántico terminado.\n");
    }

    /**
     * Ejemplo: Para cada asignación "assign(x=algo)", verificamos si x
     * está declarado en la tabla de símbolos.
     */
    private void checkAsignaciones() {
        for (String asig : asignacionesSimples) {
            // asig = "assign(var=expr)" -> parsearlo groseramente
            // Esto es solo un ejemplo muy simplificado.
            if (asig.startsWith("assign(") && asig.endsWith(")")) {
                String contenido = asig.substring("assign(".length(), asig.length()-1);
                // contenido = var=expr
                int eqIndex = contenido.indexOf('=');
                if (eqIndex > 0) {
                    String varName = contenido.substring(0, eqIndex);
                    varName = varName.trim();
                    // Revisar si está declarado
                    SymbolTableManager.SymbolData sd = symbolTable.findSymbolRecursive(varName);
                    if (sd == null) {
                        symbolTable.addSemanticError(
                                "Variable '" + varName + "' usada en asignación pero no declarada."
                        );
                    }
                }
            }
        }
    }
}
