package ParserLexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Gestiona la tabla de símbolos, scopes y la información
 * necesaria para el análisis sintáctico y base del semántico.
 */
public class SymbolTableManager {

    /**
     * Estructura que guarda la información de cada símbolo.
     */
    public static class SymbolData {
        public String lexeme;  // nombre
        public String type;    // p.e. "int", "float", "bool", o "funcType:..."
        public int line;
        public int column;
        public String scope;   // en qué scope se declaró
        public Object value;   // valor en tiempo de compilación (opcional)

        // Indicadores extra:
        public boolean isArray;
        public int arraySize;      // tamaño 1D (puedes extender para multidimensional)
        public boolean isFunction;
        public List<String> paramTypes;  // p.e. ["int", "float"]

        public SymbolData(String lexeme, String type, int line, int column, String scope, Object value) {
            this.lexeme = lexeme;
            this.type = type;
            this.line = line;
            this.column = column;
            this.scope = scope;
            this.value = value;

            this.isArray = false;
            this.arraySize = -1;
            this.isFunction = false;
            this.paramTypes = new ArrayList<>();
        }
    }

    // ----------------------------------------------------------------
    // Estructuras principales
    // ----------------------------------------------------------------
    private HashMap<String, ArrayList<SymbolData>> tablaSimbolos; // scope -> lista de SymbolData
    private Stack<String> scopeStack;                             // pila para scopes anidados
    private String currentScope;
    private String lastDeclaredIdentifier;

    // Manejo de reportes
    private List<String> erroresSemanticos;   // si detectas problemas semánticos
    private List<String> estructurasControl;  // info sobre if, while, etc.
    private List<String> derivaciones;         // producciones registradas

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public SymbolTableManager() {
        this.tablaSimbolos = new HashMap<>();
        this.scopeStack = new Stack<>();

        this.erroresSemanticos = new ArrayList<>();
        this.estructurasControl = new ArrayList<>();
        this.derivaciones = new ArrayList<>();

        // El scope inicial es "global"
        this.scopeStack.push("global");
        this.currentScope = "global";
        this.tablaSimbolos.put("global", new ArrayList<SymbolData>());
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public HashMap<String, ArrayList<SymbolData>> getTablaSimbolos() {
        return this.tablaSimbolos;
    }

    public String getCurrentScope() {
        return currentScope;
    }

    // ----------------------------------------------------------------
    // Manejo de Scopes
    // ----------------------------------------------------------------
    public void pushScope(String newScope) {
        scopeStack.push(newScope);
        currentScope = newScope;
        // Si no existe, crear la lista de símbolos para ese scope
        if (!tablaSimbolos.containsKey(currentScope)) {
            tablaSimbolos.put(currentScope, new ArrayList<SymbolData>());
        }
    }

    public void popScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.pop();
            currentScope = scopeStack.isEmpty() ? "global" : scopeStack.peek();
        }
    }

    // ----------------------------------------------------------------
    // Añadir símbolos
    // ----------------------------------------------------------------

    /**
     * Crea un nuevo scope para una función (y registra la función
     * en la tabla de símbolos del scope actual).
     */
    public void crearScopeFuncion(String returnType, String funcName, int line, int col) {
        // Verificar si ya hay un símbolo con ese nombre en el scope actual
        if (findSymbolInScope(funcName, currentScope) != null) {
            addSemanticError("Función '" + funcName + "' redeclarada en scope '" + currentScope + "'");
            return;
        }

        // Crear su SymbolData, marcar isFunction
        SymbolData funcData = new SymbolData(funcName, "funcType:" + returnType, line, col, currentScope, null);
        funcData.isFunction = true;
        tablaSimbolos.get(currentScope).add(funcData);

        System.out.println("Se crea nuevo scope para la función: " + funcName);
        // Cambiar el scope -> nombre de la función
        pushScope(funcName);
    }

    /**
     * Agrega un símbolo (variable) al scope indicado.
     */
    public void addSimbolo(String scope, String lexeme, int line, int col, String type) {
        // Verificar duplicado en el mismo scope
        if (findSymbolInScope(lexeme, scope) != null) {
            addSemanticError("Variable '" + lexeme + "' redeclarada en scope '" + scope + "'");
            return;
        }
        SymbolData data = new SymbolData(lexeme, type, line, col, scope, null);
        tablaSimbolos.get(scope).add(data);

        System.out.println("Símbolo agregado -> lex:'" + lexeme + "', tipo:'" + type
                + "', scope:'" + scope + "', línea:" + line + ", col:" + col);
    }

    /**
     * Marca un símbolo ya existente como array, con el tamaño dado.
     */
    public void markAsArray(String scope, String lexeme, int size) {
        SymbolData sd = findSymbolInScope(lexeme, scope);
        if (sd == null) {
            addSemanticError("No se encontró '" + lexeme + "' en scope '" + scope + "' para marcar como array");
            return;
        }
        sd.isArray = true;
        sd.arraySize = size;
    }

    /**
     * Añade un parámetro (nombre, tipo) a la función en el scope actual.
     */
    public void addParamToCurrentFunction(String paramName, String paramType) {
        String funcScope = getCurrentScope();
        // Buscar la symbolData con lexeme == funcScope en ese scope
        SymbolData funcSym = findSymbolInScope(funcScope, funcScope);
        if (funcSym != null && funcSym.isFunction) {
            // Repetido?
            if (funcSym.paramTypes.contains(paramType + ":" + paramName)) {
                addSemanticError("Parámetro repetido '" + paramName
                        + "' en la función '" + funcScope + "'");
                return;
            }
            funcSym.paramTypes.add(paramType + ":" + paramName);
        } else {
            addSemanticError("No se encontró la función '" + funcScope
                    + "' para añadir el parámetro '" + paramName + "'");
        }
    }

    // ----------------------------------------------------------------
    // Búsquedas de símbolos
    // ----------------------------------------------------------------
    /**
     * Busca un símbolo (por lexeme) en el scope dado.
     */
    public SymbolData findSymbolInScope(String lexeme, String scope) {
        if (!tablaSimbolos.containsKey(scope)) return null;
        for (SymbolData sd : tablaSimbolos.get(scope)) {
            if (sd.lexeme.equals(lexeme)) {
                return sd;
            }
        }
        return null;
    }

    /**
     * Busca un símbolo recursivamente en la pila de scopes:
     * scope actual, luego el anterior, etc.
     */
    public SymbolData findSymbolRecursive(String lexeme) {
        Stack<String> tempStack = new Stack<>();
        tempStack.addAll(scopeStack);

        while (!tempStack.isEmpty()) {
            String sc = tempStack.peek();
            SymbolData found = findSymbolInScope(lexeme, sc);
            if (found != null) return found;
            tempStack.pop();
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Reporte final
    // ----------------------------------------------------------------
    public void imprimirReporte() {
        System.out.println("\n--- 1) TABLA DE SÍMBOLOS COMPLETA ---");
        System.out.println("Lexema\tTipo\tLine\tCol\tScope\tisArr\tsize\tisFunc\tParams\tValue");
        for (Map.Entry<String, ArrayList<SymbolData>> entry : tablaSimbolos.entrySet()) {
            String scopeKey = entry.getKey();
            ArrayList<SymbolData> symbolsList = entry.getValue();
            for (SymbolData sd : symbolsList) {
                System.out.println(sd.lexeme + "\t"
                        + sd.type + "\t"
                        + sd.line + "\t"
                        + sd.column + "\t"
                        + sd.scope + "\t"
                        + sd.isArray + "\t"
                        + sd.arraySize + "\t"
                        + sd.isFunction + "\t"
                        + sd.paramTypes + "\t"
                        + (sd.value != null ? sd.value : "null"));
            }
        }

        System.out.println("\n--- 2) ERRORES SEMÁNTICOS ---");
        if (erroresSemanticos.isEmpty()) {
            System.out.println("No se encontraron errores semánticos.");
        } else {
            for (String err : erroresSemanticos) {
                System.out.println("* " + err);
            }
        }

        System.out.println("\n--- 3) ESTRUCTURAS DE CONTROL DETECTADAS ---");
        if (estructurasControl.isEmpty()) {
            System.out.println("No se detectaron estructuras de control.");
        } else {
            for (String info : estructurasControl) {
                System.out.println("- " + info);
            }
        }

        System.out.println("\n--- 4) RESUMEN DE DERIVACIONES ---");
        if (derivaciones.isEmpty()) {
            System.out.println("No se registraron derivaciones.");
        } else {
            for (String deriv : derivaciones) {
                System.out.println(deriv);
            }
        }

        System.out.println("\nFin del reporte.\n");
    }

    // ----------------------------------------------------------------
    // Métodos auxiliares para reporte
    // ----------------------------------------------------------------
    public void addControlStructure(String info) {
        estructurasControl.add(info);
    }

    public void addDerivation(String production) {
        derivaciones.add(production);
    }

    public void addSemanticError(String mensaje) {
        erroresSemanticos.add(mensaje);
    }

    public void setLastDeclaredIdentifier(String id) {
        this.lastDeclaredIdentifier = id;
    }

    public String getLastDeclaredIdentifier() {
        return this.lastDeclaredIdentifier;
    }
}
