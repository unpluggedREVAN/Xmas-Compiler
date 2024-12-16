%%
%class SimpleLexer
%unicode
%cup

%%
[a-zA-Z_][a-zA-Z0-9_]* { return new java_cup.runtime.Symbol(0, yytext()); }
[0-9]+                  { return new java_cup.runtime.Symbol(1, yytext()); }
.                       { System.err.println("Error léxico: " + yytext()); }


/* Definiciones */
LineTerminator = \r|\n|\r\n
WhiteSpace = ({LineTerminator} | [ \t\f])

%%

/*Apertura y clausura funciones*/
"abrecuento" {return }
"cierracuento" {return }

/* Palabras clave */
"rodolfo" { return symbol(sym.INTEGER); }
"bromista" {return symbol(sym.FLOAT); }
"trueno" {return symbol(sym.BOOL); }
"cometa"  { return symbol(sym.STRING); }
"cupido"  { return symbol(sym.CHAR); }


/* Identificadores */
[_][_a-zA-Z][_a-zA-Z0-9]* { return symbol(sym.IDENTIFICADOR); }

/* Literales */
[0-9]+ { return symbol(sym.L_INTEGER, Integer.parseInt(yytext())); }

/* Operadores */
"entrega" { return symbol(sym.ASIGNA); }
"navidad" {return symbol(sym.SUMA); }
"intercambio" {return symbol(sym.RESTA); }
"reyes" {return symbol(sym.DIVISION); }
"nochebuena" {return symbol(sym.MULTIPLICACION); }
"magos" {return symbol(sym.MODULE); }
"adviento" {return symbol(sym.POTENCIA); }
"quien" {return symbol(sym.INCREMENTO); }
"grinch" {return symbol(sym.DECREMENTO); }
"snowball" {return symbol(sym.MENOR); }
"evergreen" {return symbol(sym.MENOROIGUAL); }
"minstix" {return symbol(sym.MAYOR); }
"upatree" {return symbol(sym.MAYOROIGUAL); }
"mary" {return symbol(sym.IGUALDAD); }
"openslae" {return symbol(sym.DIFERENTE); }
"melchor" {return symbol(sym.CONJUNCION); }
"gaspar" {return symbol(sym.DISYUNCION); }
"baltazar" {return symbol(sym.NEGACION); }

/*Estructuras de control*/
"elfo" {return symbol(sym.IF); }
"hada" {return symbol(sym.ELSE); }
"envuelve" {return symbol(sym.WHILE); }
"duende" {return symbol(sym.FOR); }
"varios" {return symbol(sym.SWITCH); }
"historia" {return symbol(sym.CASE); }
"ultimo" {return symbol(sym.DEFAULT); }
"corta" {return symbol(sym.BREAK); }
"envia" {return symbol(sym.RETURN); }
"sigue" {return symbol(sym.DOS_PUNTOS); }

/*Entrada y salida de datos*/
"narra" {return symbol(sym.PRINT); }
"escucha" {return symbol(sym.READ); }

/* Espacios y comentarios */
{WhiteSpace} { /* Ignorar espacios en blanco */ }
"#" {
    while (!zzAtEOF && !yytext().contains("\n")) {
        yypushback(1); // Retrocede un carácter
    }
}

/*Comando main para iniciar el programa*/
"_verano_" {return symbol(sym.MAIN); }

/* Error */
[^] { throw new Error("Caracter ilegal: " + yytext() + " en línea " + (yyline + 1) + ", columna " + (yycolumn + 1)); }
