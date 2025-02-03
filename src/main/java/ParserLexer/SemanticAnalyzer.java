package ParserLexer;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Analizador ampliado / se migraron los métodos acá
 *
 */
public class SemanticAnalyzer {

    private SymbolTableManager symbolTable;
    private List<String> asignacionesSimples;  // Lista de "assign(x=expr)" recolectadas

    public SemanticAnalyzer(SymbolTableManager stm, List<String> assigns) {
        this.symbolTable = stm;
        this.asignacionesSimples = assigns;
    }

    public void runSemanticChecks() {
        System.out.println("\n[SEMANTIC] Iniciando chequeos semánticos...");

        // Chequeos que ya existían
        checkDeclaracionesYTipos();
        checkTiposExpresiones();

        checkUniqueMain();
        checkArrayDimensions();
        checkFunctionParams();
        // control de break falta

        System.out.println("[SEMANTIC] Análisis semántico terminado.\n");
    }

    /**
     * 1) Verificación de uso de variables (deben estar declaradas antes de usarse).
     *
     */
    private void checkDeclaracionesYTipos() {
        for (String asig : asignacionesSimples) {
            String varName = parseVarFromAssign(asig);
            SymbolTableManager.SymbolData sd = symbolTable.findSymbolRecursive(varName);
            if (sd == null) {
                symbolTable.addSemanticError("Variable '" + varName
                        + "' usada sin declarar. En: " + asig);
            }
        }
    }

    /**
     * 2) Chequeo de tipos de expresiones
     *    Ejemplo: si la variable es int y la expresión contiene '.', asume float.
     */
    private void checkTiposExpresiones() {
        for (String asig : asignacionesSimples) {
            String var = parseVarFromAssign(asig);
            String expr = parseExprFromAssign(asig);

            SymbolTableManager.SymbolData sd = symbolTable.findSymbolRecursive(var);
            if (sd == null) continue; // Ya se reportó

            // Chequeo de "float" vs "int"
            if (sd.type.equals("int") && expr.contains(".")) {
                symbolTable.addSemanticError("Asignando float a variable int ("+ var + ") en " + asig);
            }

            // Detectar división por cero
            if (expr.contains("/0")) {
                symbolTable.addSemanticError("División por cero en la expresión: " + expr);
            }
        }
    }

    /**
     * 3) Verificar que exista exactamente 1 main.
     */
    private void checkUniqueMain() {
        int foundMains = 0;
        // Recorremos toda la tabla
        for (var entry : symbolTable.getTablaSimbolos().entrySet()) {
            for (var sd : entry.getValue()) {
                if (sd.isFunction && sd.lexeme.equals("main")) {
                    foundMains++;
                }
            }
        }
        if (foundMains == 0) {
            symbolTable.addSemanticError("No se declaró la función 'main'. Debe existir exactamente una.");
        } else if (foundMains > 1) {
            symbolTable.addSemanticError("Existen múltiples funciones 'main' ("+foundMains+"). Debe haber solo una.");
        }
    }

    /**
     * 4) Revisión de que los arreglos tengan dimensiones válidas.
     */
    private void checkArrayDimensions() {
        for (var entry : symbolTable.getTablaSimbolos().entrySet()) {
            for (var sd : entry.getValue()) {
                if (sd.isArray && sd.arraySize < 1) {
                    symbolTable.addSemanticError("Array '"+sd.lexeme+"' con dimensión inválida: "+sd.arraySize);
                }
            }
        }
    }

    /**
     * 5) Verifica que los parámetros de cada función no se repitan
     *    y que los tipos sean válidos.
     */
    private void checkFunctionParams() {
        for (var entry : symbolTable.getTablaSimbolos().entrySet()) {
            for (var sd : entry.getValue()) {
                if (sd.isFunction) {
                    // Revisar sd.paramTypes
                    Set<String> seenParamNames = new HashSet<>();
                    for (String paramDesc : sd.paramTypes) {
                        String[] parts = paramDesc.split(":");
                        if (parts.length == 2) {
                            String pType = parts[0];
                            String pName = parts[1];
                            // Comprobar duplicados de nombre
                            if (seenParamNames.contains(pName)) {
                                symbolTable.addSemanticError("Parámetro repetido '" + pName
                                        + "' en la función '" + sd.lexeme + "'");
                            } else {
                                seenParamNames.add(pName);
                            }
                            // Comprobar tipo válido
                            if (!symbolTable.isValidType(pType)) {
                                symbolTable.addSemanticError("Tipo de parámetro inválido '"
                                        + pType + "' en la función '" + sd.lexeme + "'");
                            }
                        }
                    }
                }
            }
        }
    }

    // --------------------------------------------------
    // para parsear la asignación simple
    // --------------------------------------------------
    private String parseVarFromAssign(String asigStr) {
        // "assign(x= expr)"
        int openPar = asigStr.indexOf("(");
        int eq = asigStr.indexOf("=");
        if (openPar < 0 || eq < openPar) return "???";
        return asigStr.substring(openPar + 1, eq).trim();
    }

    private String parseExprFromAssign(String asigStr) {
        int eq = asigStr.indexOf("=") + 1;
        int closePar = asigStr.lastIndexOf(")");
        if (eq < 1 || closePar < eq) return "";
        return asigStr.substring(eq, closePar).trim();
    }
}
