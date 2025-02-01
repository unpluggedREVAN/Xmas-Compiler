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
        public int arraySize;      // tamaño 1D (puedes extender a multi-dim)
        public boolean isFunction;
        public List<String> paramTypes;  // p.e. ["int:param1", "float:param2"]

        // NUEVO: para controlar uso de variables sin inicializar (opcional)
        public boolean isInitialized;

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

            // Por defecto, una variable no está inicializada hasta que se le asigne un valor
            this.isInitialized = false;
        }
    }

    // ----------------------------------------------------------------
    // Estructuras principales
    // ----------------------------------------------------------------
    private HashMap<String, ArrayList<SymbolData>> tablaSimbolos;
    private Stack<String> scopeStack;
    private String currentScope;
    private String lastDeclaredIdentifier;
    private String lastDeclaredVar;

    // Manejo de reportes
    private List<String> erroresSemanticos;
    private List<String> estructurasControl;
    private List<String> derivaciones;

    // ----------------------------------------------------------------
    // NUEVOS CAMPOS para manejo de tipo de retorno y estructuras de control
    // ----------------------------------------------------------------
    /**
     * Pila para el tipo de retorno de la función actual.
     * Cuando entramos en una función, hacemos push; al salir, pop.
     */
    private Stack<String> functionReturnTypeStack;

    /**
     * Pila para controlar estructuras de control anidadas
     * (while, for, switch...) y validar 'break'/'continue'.
     */
    private Stack<String> controlStructureStack;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public SymbolTableManager() {
        this.tablaSimbolos = new HashMap<>();
        this.scopeStack = new Stack<>();

        this.erroresSemanticos = new ArrayList<>();
        this.estructurasControl = new ArrayList<>();
        this.derivaciones = new ArrayList<>();

        // Scope inicial: "global"
        this.scopeStack.push("global");
        this.currentScope = "global";
        this.tablaSimbolos.put("global", new ArrayList<SymbolData>());

        // Inicializamos las pilas nuevas
        this.functionReturnTypeStack = new Stack<>();
        this.controlStructureStack = new Stack<>();
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
        // Verificar duplicado en el mismo scope
        if (findSymbolInScope(funcName, currentScope) != null) {
            addSemanticError("Función '" + funcName + "' redeclarada en scope '" + currentScope + "'");
            return;
        }

        SymbolData funcData = new SymbolData(funcName, "funcType:" + returnType, line, col, currentScope, null);
        funcData.isFunction = true;
        tablaSimbolos.get(currentScope).add(funcData);

        System.out.println("Se crea nuevo scope para la función: " + funcName);
        pushScope(funcName);
    }

    /**
     * Agrega un símbolo (variable) al scope indicado.
     */
    public void addSimbolo(String scope, String lexeme, int line, int col, String type) {
        // Verificar duplicado
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
        SymbolData funcSym = findSymbolInScope(funcScope, funcScope);
        if (funcSym != null && funcSym.isFunction) {
            // Solo añadimos si no está repetido
            String joined = paramType + ":" + paramName;
            if (funcSym.paramTypes.contains(joined)) {
                addSemanticError("Parámetro repetido '" + paramName
                        + "' en la función '" + funcScope + "'");
                return;
            }
            funcSym.paramTypes.add(joined);
        } else {
            addSemanticError("No se encontró la función '" + funcScope
                    + "' para añadir el parámetro '" + paramName + "'");
        }
    }

    // ----------------------------------------------------------------
    // Búsquedas de símbolos
    // ----------------------------------------------------------------
    public SymbolData findSymbolInScope(String lexeme, String scope) {
        if (!tablaSimbolos.containsKey(scope)) return null;
        for (SymbolData sd : tablaSimbolos.get(scope)) {
            if (sd.lexeme.equals(lexeme)) {
                return sd;
            }
        }
        return null;
    }

    public SymbolData findSymbolRecursive(String lexeme) {
        // Copia de la pila actual
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

    public void setVarName(String varName) {
        this.lastDeclaredVar = varName;
    }

    public String getVarName() {
        return this.lastDeclaredVar;
    }

    /**
     * Verifica si un string corresponde a un tipo básico válido
     */
    public boolean isValidType(String t) {
        // Ajustar según los tipos que manejas (tipado explícito y fuerte)
        return t.equals("int") || t.equals("float")
                || t.equals("bool") || t.equals("char")
                || t.equals("string");
    }

    /**
     * Verifica si el tipo declarado es compatible con el tipo de la expresión.
     * Permite, por ejemplo, asignar un 'int' a una variable de tipo 'float'.
     */
    public boolean esCompatible(String tipoDeclarado, String tipoExpr) {
        // Si alguno de los tipos es "error", se evita cascada de errores.
        if (tipoDeclarado.equals("error") || tipoExpr.equals("error")) {
            return true;
        }
        // Si los tipos son exactamente iguales, son compatibles.
        if (tipoDeclarado.equals(tipoExpr)) {
            return true;
        }
        // Permitir conversión implícita: asignar un int a una variable float.
        if (tipoDeclarado.equals("float") && tipoExpr.equals("int")) {
            return true;
        }
        // Se pueden añadir más reglas según las conversiones permitidas.
        return false;
    }

    // ----------------------------------------------------------------
    // NUEVOS MÉTODOS para la pila de retorno de funciones
    // ----------------------------------------------------------------
    /**
     * Indica si estamos dentro de alguna función.
     */
    public boolean isInsideFunction() {
        return !functionReturnTypeStack.isEmpty();
    }

    /**
     * Devuelve el tipo de retorno de la función actual (en la cima de la pila),
     * o null si no estamos en una función.
     */
    public String getCurrentFunctionReturnType() {
        if (!functionReturnTypeStack.isEmpty()) {
            return functionReturnTypeStack.peek();
        }
        return null;
    }

    /**
     * Ingresa un tipo de retorno al comenzar una función.
     */
    public void pushFunctionReturnType(String returnType) {
        functionReturnTypeStack.push(returnType);
    }

    /**
     * Quita el tipo de retorno al terminar la función.
     */
    public void popFunctionReturnType() {
        if (!functionReturnTypeStack.isEmpty()) {
            functionReturnTypeStack.pop();
        }
    }

    // ----------------------------------------------------------------
    // NUEVOS MÉTODOS para la pila de estructuras de control
    // ----------------------------------------------------------------
    /**
     * Invocado cuando se entra a un while, for, o switch (u otras si aplican).
     * Ej.: pushControlStructure("while"), pushControlStructure("for"), etc.
     */
    public void pushControlStructure(String structType) {
        controlStructureStack.push(structType);
    }

    /**
     * Invocado cuando se sale de la estructura de control.
     */
    public void popControlStructure() {
        if (!controlStructureStack.isEmpty()) {
            controlStructureStack.pop();
        }
    }

    /**
     * Verifica si en la cima de la pila de estructuras hay un while/for/switch
     * que justifique un 'break'.
     */
    public boolean canBreakHere() {
        if (controlStructureStack.isEmpty()) return false;
        String top = controlStructureStack.peek();
        return top.equals("while") || top.equals("for") || top.equals("switch");
    }

    /**
     * Verifica si en la cima de la pila de estructuras hay un while/for
     * que justifique un 'continue'.
     */
    public boolean canContinueHere() {
        if (controlStructureStack.isEmpty()) return false;
        String top = controlStructureStack.peek();
        return top.equals("while") || top.equals("for");
    }
}
