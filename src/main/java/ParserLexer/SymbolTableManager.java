package ParserLexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class SymbolTableManager {

    public static class SymbolData {
        public String lexeme;
        public String type;
        public int line;
        public int column;
        public String scope;
        public Object value;

        public SymbolData(String lexeme, String type, int line, int column, String scope, Object value) {
            this.lexeme = lexeme;
            this.type = type;
            this.line = line;
            this.column = column;
            this.scope = scope;
            this.value = value;
        }
    }

    // Tabla de símbolos principal: scope -> lista de símbolos
    private HashMap<String, ArrayList<SymbolData>> tablaSimbolos;

    // Manejo de scopes (ámbitos) usando una pila
    private Stack<String> scopeStack;

    // Scope actual
    private String currentScope;

    // Listas para almacenar errores e info adicional
    private List<String> erroresSemanticos;
    private List<String> estructurasControl;
    private List<String> derivaciones;  // Producciones reconocidas

    public SymbolTableManager() {
        this.tablaSimbolos = new HashMap<>();
        this.scopeStack = new Stack<>();
        this.erroresSemanticos = new ArrayList<>();
        this.estructurasControl = new ArrayList<>();
        this.derivaciones = new ArrayList<>();

        // Iniciar el scope global
        this.scopeStack.push("global");
        this.currentScope = "global";
        this.tablaSimbolos.put("global", new ArrayList<SymbolData>());
    }

    // Getter para la tabla de símbolos
    public HashMap<String, ArrayList<SymbolData>> getTablaSimbolos() {
        return this.tablaSimbolos;
    }

    /* ------------------------------------------------------------------
       Métodos de manejo de scopes
    ------------------------------------------------------------------ */
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

    /* ------------------------------------------------------------------
       Métodos para manejar símbolos
    ------------------------------------------------------------------ */
    public void crearScopeFuncion(String type, String funcName, int line, int col) {
        System.out.println("Se crea nuevo scope para función: " + funcName);
        pushScope(funcName);

        SymbolData funcData = new SymbolData(
                funcName, "funcType:" + type,
                line, col, currentScope, null
        );
        tablaSimbolos.get(currentScope).add(funcData);
    }

    public void addSimbolo(String scope, String lexeme, int line, int col, String type) {
        if (!tablaSimbolos.containsKey(scope)) {
            tablaSimbolos.put(scope, new ArrayList<SymbolData>());
        }
        SymbolData data = new SymbolData(lexeme, type, line, col, scope, null);
        tablaSimbolos.get(scope).add(data);
        System.out.println("Símbolo agregado -> "
                + "lex:'" + lexeme + "', tipo:'" + type
                + "', scope:'" + scope + "', línea:" + line + ", col:" + col);
    }

    // Búsqueda de símbolos dentro de un scope específico
    public SymbolData findSymbolInScope(String lexeme, String scope) {
        if (!tablaSimbolos.containsKey(scope)) return null;
        for (SymbolData sd : tablaSimbolos.get(scope)) {
            if (sd.lexeme.equals(lexeme)) {
                return sd;
            }
        }
        return null;
    }

    // Búsqueda de símbolo en el scope actual y ascendiendo la pila
    public SymbolData findSymbolRecursive(String lexeme) {
        Stack<String> tempStack = new Stack<>();
        tempStack.addAll(scopeStack);

        while (!tempStack.isEmpty()) {
            String sc = tempStack.peek();
            SymbolData sym = findSymbolInScope(lexeme, sc);
            if (sym != null) {
                return sym;
            }
            tempStack.pop();
        }
        // Si no se encontró
        return null;
    }

    // Reporte final
    public void imprimirReporte() {
        System.out.println("\n--- 1) TABLAS DE SÍMBOLOS ---");
        System.out.println("Lexema\tTipo\tLínea\tCol\tScope\tValor");
        System.out.println("---------------------------------------------");
        for (String scopeKey : tablaSimbolos.keySet()) {
            for (SymbolData sd : tablaSimbolos.get(scopeKey)) {
                System.out.printf("%s\t%s\t%d\t%d\t%s\t%s\n",
                        sd.lexeme, sd.type, sd.line, sd.column, sd.scope,
                        (sd.value != null ? sd.value.toString() : "null"));
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

    // Otros métodos auxiliares
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
