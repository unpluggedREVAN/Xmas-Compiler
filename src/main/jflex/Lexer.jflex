/* Lexer.jflex */
package ParserLexer;
import java_cup.runtime.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Este archivo fue desarrollado tomando como base el código de ejemplo proporcionado
 * en la documentación oficial de JFlex y CUP:
 * - https://www.jflex.de/
 * - https://www2.cs.tum.edu/projects/cup/examples.php
 */

%%

%public
%unicode
%cup
%line
%column
%class Lexer

%{
    // Declaración de estructuras para manejar cadenas temporales, tokens reconocidos y errores léxicos.
    StringBuffer string = new StringBuffer();
    public List<String[]> tokenTable = new ArrayList<>();
    public List<String> lexicalErrors = new ArrayList<>();

    // registrar tokens simples en la tabla de tokens con su tipo y posición.
    private Symbol symbol(int type) {
        tokenTable.add(new String[]{yytext(), sym.terminalNames[type], (yyline + 1) + ":" + (yycolumn + 1)});
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    // registrar tokens con valores asociados (como números o cadenas) en la tabla de tokens.
    private Symbol symbol(int type, Object value) {
        tokenTable.add(new String[]{yytext(), sym.terminalNames[type], (yyline + 1) + ":" + (yycolumn + 1)});
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    // guardar errores en lista de errores. Esto se iba a usar para manejar los errores de otra forma pero bueno
    public void addLexicalError(String errorMessage) {
        lexicalErrors.add(errorMessage);
    }
%}

/********************************************
 * Definiciones de patrones básicos
 ********************************************/

LineTerminator = (\r\n|\r|\n)
WhiteSpace = [ \t\f]+

Digit = [0-9]
Letter = [a-zA-Z]
Underscore = [_]

/* Identificadores */
Identifier = "_"[a-zA-Z0-9_]*"_"

/* Literales Num */
IntLiteral = {Digit}+
FloatLiteral = {Digit}+"."{Digit}+

/* significan Double Quote y Backslash */
DQ = \"
BS = \\

CharLiteral = \'([^\'\\]|\\.)\'
StringLiteral = {DQ}({BS}.|[^\"\\\n])*{DQ}

/* Comentarios */
SingleLineComment = "#"[^\r\n]*
MultiLineComment = "\\_"([^\\_]|\\n|\\r)*"_/"

/********************************************
 * Sección de Reglas
 ********************************************/

%%

{WhiteSpace}           { /* espacios en blanco - ignorar */ }
{LineTerminator}       { /* saltos de línea - ignorar */ }

{SingleLineComment}    { /* ignorar comentario */ }
{MultiLineComment}     { /* ignorar */ }

/* apertura y cierre */
"abreregalo"          { return symbol(sym.ABREREGALO); }
"cierraregalo"        { return symbol(sym.CIERRAREGALO); }
"abrecuento"          { return symbol(sym.ABRECUENTO); }
"cierracuento"        { return symbol(sym.CIERRACUENTO); }
"abreempaque"         { return symbol(sym.ABREEMPAQUE); }
"cierraempaque"       { return symbol(sym.CIERRAEMPAQUE); }

/* Palabras clave de tipos */
"rodolfo"             { return symbol(sym.INTEGER); }
"bromista"            { return symbol(sym.FLOAT); }
"trueno"              { return symbol(sym.BOOL); }
"cupido"              { return symbol(sym.CHAR); }
"cometa"              { return symbol(sym.STRING); }

/* main */
"_verano_"            { return symbol(sym.MAIN); }

/* Estructuras de control */
"elfo"                { return symbol(sym.IF); }
"hada"                { return symbol(sym.ELSE); }
"envuelve"            { return symbol(sym.WHILE); }
"duende"              { return symbol(sym.FOR); }
"varios"              { return symbol(sym.SWITCH); }
"historia"            { return symbol(sym.CASE); }
"ultimo"              { return symbol(sym.DEFAULT); }
"corta"               { return symbol(sym.BREAK); }
"envia"               { return symbol(sym.RETURN); }
"sigue"               { return symbol(sym.DOS_PUNTOS); }

/* Entrada/Salida */
"narra"               { return symbol(sym.PRINT); }
"escucha"             { return symbol(sym.READ); }

/* Operadores aritméticos binarios */
"entrega"             { return symbol(sym.ASIGNA); }      // =
"navidad"             { return symbol(sym.SUMA); }        // +
"intercambio"         { return symbol(sym.RESTA); }       // -
"reyes"               { return symbol(sym.DIVISION); }    // /
"nochebuena"          { return symbol(sym.MULTIPLICACION); } // *
"magos"               { return symbol(sym.MODULE); }      // %
"adviento"            { return symbol(sym.POTENCIA); }    // ^

/* Operadores unarios */
"quien"               { return symbol(sym.INCREMENTO); }  // ++
"grinch"              { return symbol(sym.DECREMENTO); }  // --

/* Operadores relacionales */
"snowball"            { return symbol(sym.MENOR); }       // <
"evergreen"           { return symbol(sym.MENOROIGUAL); } // <=
"minstix"             { return symbol(sym.MAYOR); }       // >
"upatree"             { return symbol(sym.MAYOROIGUAL); } // >=
"mary"                { return symbol(sym.IGUALDAD); }    // ==
"openslae"            { return symbol(sym.DIFERENTE); }   // !=

/* Operadores lógicos */
"melchor"             { return symbol(sym.CONJUNCION); }  // &&
"gaspar"              { return symbol(sym.DISYUNCION); }  // ||
"baltazar"            { return symbol(sym.NEGACION); }    // !

/* otros símbolos */
"finregalo"           { return symbol(sym.PUNTO_Y_COMA); }  // ;
","                   { return symbol(sym.COMA); }

/* Identificadores */
{Identifier}          { return symbol(sym.IDENTIFICADOR, yytext()); }

/* Literales numéricos */
{FloatLiteral}        { return symbol(sym.FLOAT_LITERAL, Float.valueOf(yytext())); }
{IntLiteral}          { return symbol(sym.L_INTEGER, Integer.valueOf(yytext())); }

/* Literales booleanos */
"true"                { return symbol(sym.BOOL_LITERAL, Boolean.TRUE); }
"false"               { return symbol(sym.BOOL_LITERAL, Boolean.FALSE); }

/* Literales char y string */
{CharLiteral}         { return symbol(sym.CHAR_LITERAL, yytext().charAt(1)); }
{StringLiteral}       { return symbol(sym.STRING_LITERAL, yytext().substring(1, yytext().length()-1)); }

/* EOF */
<<EOF>>               { return symbol(sym.EOF); }

/* Cualquier otro caracter no reconocido */
.                     {
    addLexicalError("Caracter ilegal: " + yytext() + " en línea " + (yyline + 1) + ", columna " + (yycolumn + 1));
    return symbol(sym.UNKNOWN_TOKEN, yytext());
}