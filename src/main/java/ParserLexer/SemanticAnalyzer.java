package ParserLexer;

import java.util.List;

public class SemanticAnalyzer {

    private SymbolTableManager symbolTable;
    private List<String> asignacionesSimples;  // Única lista que recibimos

    // Constructor con dos parámetros
    public SemanticAnalyzer(SymbolTableManager stm, List<String> assigns) {
        this.symbolTable = stm;
        this.asignacionesSimples = assigns;
    }

    public void runSemanticChecks() {
        System.out.println("\n[SEMANTIC] Iniciando chequeos semánticos...");

        // Ejemplo de dos chequeos sencillos:
        checkDeclaracionesYTipos();
        checkTiposExpresiones();

        // Podrías expandir con más validaciones si lo deseas
        System.out.println("[SEMANTIC] Análisis semántico terminado.\n");
    }

    /**
     * 1) Verificación de uso de variables (deben estar declaradas).
     */
    private void checkDeclaracionesYTipos() {
        for (String asig : asignacionesSimples) {
            // asig: "assign(x= expr)"
            String varName = parseVarFromAssign(asig);
            // Ver si está declarado
            SymbolTableManager.SymbolData sd = symbolTable.findSymbolRecursive(varName);
            if (sd == null) {
                symbolTable.addSemanticError("Variable '" + varName
                        + "' usada sin declarar. En: " + asig);
            }
        }
    }

    /**
     * 2) Validación de tipos de expresiones (simplificado).
     *    Por ejemplo, si la variable es int y detectamos un decimal, error.
     */
    private void checkTiposExpresiones() {
        for (String asig : asignacionesSimples) {
            String var = parseVarFromAssign(asig);
            String expr = parseExprFromAssign(asig);

            SymbolTableManager.SymbolData sd = symbolTable.findSymbolRecursive(var);
            if (sd == null) continue; // Ya se reportó el error

            // Si es int y la expresión contiene '.', lo consideramos float
            if (sd.type.equals("int") && expr.contains(".")) {
                symbolTable.addSemanticError("Asignando float a variable int ("
                        + var + ") en " + asig);
            }
            // Podrías detectar "expr.contains(\"/0\")" => división por cero, etc.
        }
    }

    /**
     * Extrae el nombre de variable de un string tipo "assign(x= expr)".
     */
    private String parseVarFromAssign(String asigStr) {
        // Ejemplo rápido:
        // "assign(x= y+2 )"
        int openPar = asigStr.indexOf("(");
        int eq = asigStr.indexOf("=");
        if (openPar < 0 || eq < openPar) return "???";
        return asigStr.substring(openPar + 1, eq).trim();
    }

    /**
     * Extrae la parte de la expresión a la derecha del '=' en "assign(x= expr)".
     */
    private String parseExprFromAssign(String asigStr) {
        int eq = asigStr.indexOf("=") + 1;
        int closePar = asigStr.lastIndexOf(")");
        if (eq < 1 || closePar < eq) return "";
        return asigStr.substring(eq, closePar).trim();
    }
}
