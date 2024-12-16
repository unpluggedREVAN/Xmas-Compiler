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
"quien" {return symbol(sym.); }
"grinch" {return symbol(sym.); }
"snowball" {return}
"minstix" {return}
"upatree" {return}
"mary" {return}
"openslae" {return}

/*Estructuras de control*/
"elfo" {return }
"hada" {return }
"envuelve" {return }
"duende" {return }
"varios" {return }
"historia" {return }
"ultimo" {return }
"corta" {return }
"envia" {return }
"sigue" {return }

/*Entrada y salida de datos*/
"narra" {return }
"escucha" {return }

/* Espacios y comentarios */
{WhiteSpace} { /* Ignorar espacios en blanco */ }
"#" {
    while (!zzAtEOF && !yytext().contains("\n")) {
        yypushback(1); // Retrocede un carácter
    }
}

/* Error */
[^] { throw new Error("Caracter ilegal: " + yytext() + " en línea " + (yyline + 1) + ", columna " + (yycolumn + 1)); }
