%%

%class Lexer
%unicode
%cup

%%

[a-zA-Z_][a-zA-Z0-9_]*   { System.out.println("Identificador: " + yytext()); }
[0-9]+                   { System.out.println("Número: " + yytext()); }
"+"                      { System.out.println("Operador +"); }
"-"                      { System.out.println("Operador -"); }
.                        { System.out.println("Carácter no reconocido: " + yytext()); }
