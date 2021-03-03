grammar Infix;

/*
 * Parser Rules
 */
operator	: EXPO NUM                      #expoNum            //An exponent sign followed by a number
            | operator EXPO NUM             #opExpNum           //An operator followed by an exponent sign and a number
            | operator EXPO subexpr         #opExpSub           //An operator followed by an exponent sign and a sub-expression
            | EXPO PAREN subexpr+ PAREN     #expoPar            //An exponent sign followed by a sub-expression within parentheses

            | MULTI NUM                     #multiNum           //Multiplication followed by a number
            | MULTI PAREN subexpr+ PAREN    #multiPar           //Multiplication followed by a sub-expression within parentheses

        	| DIV NUM                       #divNum             //Division followed by a number
            | DIV PAREN subexpr+ PAREN      #divPar             //Division followed by a sub-expression within parentheses

		    | ADD                           #addition           //An addition sign
		    | SUB                           #subtraction        //A subtraction sign
	    	;

subexpr : NUM                               #soloNum            //A single number
        | PAREN subexpr+ PAREN              #parenthesis        //A subexpression within parentheses
        | subexpr operator                  #numThenOp          //A number followed by an operator
    	;

expr   : subexpr+                          #expression         //One or multiple sub-expressions
       ;
/*
 * Lexer Rules
 */
WS      : [ \t\r\n]+ -> skip ;          //Skips white space
NUM	    : '-'?[0-9]+                    //Positive or negativee numbers
	    | '-'?[0-9]+ '.' [0-9]+         //Positive or negative decimals
    	;

SUB     : '-' ;     //Subtraction
ADD     : '+' ;     //Addition
DIV     : '/' ;     //Division
MULTI   : '*' ;     //Multiplication
EXPO    : '^' ;     //Exponent
PAREN   : '('       //Parentheses
        | ')'
        ;
