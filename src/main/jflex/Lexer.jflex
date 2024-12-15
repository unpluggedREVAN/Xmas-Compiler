/* Lexer.jflex */
package ParserLexer;
import java_cup.runtime.*;
import java.util.ArrayList;
import java.util.List;

%%
%public
%unicode
%cup
%line
%column
%class Lexer

%{
    StringBuffer string = new StringBuffer();
    // Lista para almacenar la información de los tokens
    public List<String[]> tokenTable = new ArrayList<>();

    private Symbol symbol(int type) {
        // Guarda el lexema, tipo de token, línea y columna en la tabla
        tokenTable.add(new String[]{yytext(), sym.terminalNames[type], (yyline + 1) + ":" + (yycolumn + 1)});
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
        // Guarda el lexema, tipo de token, línea y columna en la tabla
        tokenTable.add(new String[]{yytext(), sym.terminalNames[type], (yyline + 1) + ":" + (yycolumn + 1)});
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

/* Definiciones */
LineTerminator = \r|\n|\r\n
WhiteSpace = ({LineTerminator} | [ \t\f])

%%

/* Palabras clave */
"rodolfo" { return symbol(sym.INTEGER); }
"cometa"  { return symbol(sym.STRING); }
"cupido"  { return symbol(sym.CHAR); }

/* Identificadores */
[_][_a-zA-Z][_a-zA-Z0-9]* { return symbol(sym.IDENTIFICADOR); }

/* Literales */
[0-9]+ { return symbol(sym.L_INTEGER, Integer.parseInt(yytext())); }

/* Operadores */
"entrega" { return symbol(sym.ASIGNA); }

/* Espacios y comentarios */
{WhiteSpace} { /* Ignorar espacios en blanco */ }
"#" {
    while (!zzAtEOF && !yytext().contains("\n")) {
        yypushback(1); // Retrocede un carácter
    }
}

/* Error */
[^] { throw new Error("Caracter ilegal: " + yytext() + " en línea " + (yyline + 1) + ", columna " + (yycolumn + 1)); }
