/* Lexer.jflex */
package ParserLexer;
import java_cup.runtime.*;

%%
%public
%unicode
%cup
%line
%column
%class Lexer

%{
    StringBuffer string = new StringBuffer();

    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/* Definiciones */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\\r\\n]
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
    // Ignorar comentario hasta el final de la línea
    while (!zzAtEOF && !yytext().contains("\n")) {
        yypushback(1); // Retrocede un carácter
    }
}

/* Error */
[^] { throw new Error("Caracter ilegal: " + yytext()); }
