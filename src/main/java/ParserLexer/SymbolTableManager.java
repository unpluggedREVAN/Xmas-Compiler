package ParserLexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SymbolTableManager {

    public static class SymbolData {
        public String lexeme;
        public String type;  // "int", "float", "bool", etc. o "funcType:..."
        public int line;
        public int column;
        public String scope;
        public Object value;

        // Novedades
        public boolean isArray;
        public int arraySize;
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

    private HashMap<String, ArrayList<SymbolData>> tablaSimbolos;
    private Stack<String> scopeStack;
    private String currentScope;

    private List<String> erroresSemanticos;
    private List<String> estructurasControl;
    private List<String> derivaciones;

    public SymbolTableManager() {
        this.tablaSimbolos = new HashMap<>();
        this.scopeStack = new Stack<>();
        this.erroresSemanticos = new ArrayList<>();
        this.estructurasControl = new ArrayList<>();
        this.derivaciones = new ArrayList<>();

        // scope inicial
        this.scopeStack.push("global");
        this.currentScope = "global";
        this.tablaSimbolos.put("global", new ArrayList<SymbolData>());
    }

    public HashMap<String, ArrayList<SymbolData>> getTablaSimbolos() {
        return this.tablaSimbolos;
    }

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

    public String getCurrentScope() {
        return currentScope;
    }

    public void crearScopeFuncion(String returnType, String funcName, int line, int col) {
        // Verificar duplicados
        if (findSymbolInScope(funcName, currentScope) != null) {
            addSemanticError("Función '" + funcName + "' redeclarada en scope '" + currentScope + "'");
            return;
        }
        SymbolData funcData = new SymbolData(funcName, "funcType:" + returnType, line, col, currentScope, null);
        funcData.isFunction = true;
        tablaSimbolos.get(currentScope).add(funcData);

        System.out.println("Se crea nuevo scope para función: " + funcName);
        pushScope(funcName);
    }

    public void addSimbolo(String scope, String lexeme, int line, int col, String type) {
        // Checar duplicado
        if (findSymbolInScope(lexeme, scope) != null) {
            addSemanticError("Variable '" + lexeme + "' redeclarada en scope '" + scope + "'");
            return;
        }
        SymbolData data = new SymbolData(lexeme, type, line, col, scope, null);
        tablaSimbolos.get(scope).add(data);

        System.out.println("Símbolo agregado -> lex:'" + lexeme + "', tipo:'" + type
                + "', scope:'" + scope + "', línea:" + line + ", col:" + col);
    }

    public void markAsArray(String scope, String lexeme, int size) {
        SymbolData sd = findSymbolInScope(lexeme, scope);
        if (sd == null) {
            addSemanticError("Simbolo '" + lexeme + "' no encontrado para marcar como array");
            return;
        }
        sd.isArray = true;
        sd.arraySize = size;
    }

    public void addParamToCurrentFunction(String paramName, String paramType) {
        String funcScope = getCurrentScope();
        SymbolData funcSym = findSymbolInScope(funcScope, funcScope);
        if (funcSym != null && funcSym.isFunction) {
            // Checar si hay duplicado en parámetros
            if (funcSym.paramTypes.contains(paramType + ":" + paramName)) {
                addSemanticError("Parámetro repetido '" + paramName
                        + "' en la función '" + funcScope + "'");
                return;
            }
            funcSym.paramTypes.add(paramType + ":" + paramName);
        } else {
            addSemanticError("No se encontró la definición de la función '" + funcScope
                    + "' para añadir el parámetro '" + paramName + "'");
        }
    }

    public SymbolData findSymbolInScope(String lexeme, String scope) {
        if (!tablaSimbolos.containsKey(scope)) return null;
        for (SymbolData sd : tablaSimbolos.get(scope)) {
            if (sd.lexeme.equals(lexeme)) return sd;
        }
        return null;
    }

    public SymbolData findSymbolRecursive(String lexeme) {
        Stack<String> temp = new Stack<>();
        temp.addAll(scopeStack);

        while (!temp.isEmpty()) {
            String sc = temp.peek();
            SymbolData sym = findSymbolInScope(lexeme, sc);
            if (sym != null) return sym;
            temp.pop();
        }
        return null;
    }

    public void imprimirReporte() {
        System.out.println("\n--- 1) TABLAS DE SÍMBOLOS EXTENDIDA ---");
        System.out.println("Lexema\tTipo\tLine\tCol\tScope\tisArr\tsize\tisFunc\tParams\tValue");
        for (Map.Entry<String, ArrayList<SymbolData>> e : tablaSimbolos.entrySet()) {
            for (SymbolData sd : e.getValue()) {
                System.out.println(sd.lexeme + "\t" + sd.type
                        + "\t" + sd.line + "\t" + sd.column + "\t" + sd.scope
                        + "\t" + sd.isArray + "\t" + sd.arraySize
                        + "\t" + sd.isFunction + "\t" + sd.paramTypes
                        + "\t" + (sd.value!=null ? sd.value : "null"));
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
            for (String d : derivaciones) {
                System.out.println(d);
            }
        }
        System.out.println("\nFin del reporte.\n");
    }

    public void addControlStructure(String info) {
        estructurasControl.add(info);
    }

    public void addDerivation(String production) {
        derivaciones.add(production);
    }

    public void addSemanticError(String mensaje) {
        erroresSemanticos.add(mensaje);
    }
}
