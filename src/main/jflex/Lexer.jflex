%%
%class SimpleLexer
%unicode
%cup

%%
[a-zA-Z_][a-zA-Z0-9_]* { return new java_cup.runtime.Symbol(0, yytext()); }
[0-9]+                  { return new java_cup.runtime.Symbol(1, yytext()); }
.                       { System.err.println("Error léxico: " + yytext()); }
