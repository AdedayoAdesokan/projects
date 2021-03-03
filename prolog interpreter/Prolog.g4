grammar Prolog;

/*
 * Parser Rules
 */
startList   : ATOM              #listAtom       //A single atom
            | VARIABLE          #listVariable   //A single variable
            ;
middleList  : COMMA ATOM        #middleListAtom         //A comma followed by an atom
            | COMMA VARIABLE    #middleListVariable     //A comma followed by a variable
            ;
list        : BRACKET BRACKET                                                       #emptyList              //An empty list
            | BRACKET startList DIVIDER startList BRACKET                           #soloDividerList        //A list with a divider in it
            | BRACKET startList middleList* middleList DIVIDER startList BRACKET    #middleDividerList      //A list with a divider and other terms in it
            | BRACKET startList middleList* BRACKET                                 #normalList             //A list without a divider in it
            ;

startTerm   : ATOM          #atom       //A single atom
            | VARIABLE      #variable   //A single variable
            | list          #soloList
            ;
midTerm     : COMMA ATOM        #midAtom        //A comma followed by an atom
            | COMMA VARIABLE    #midVariable    //A comma followed by a variable
            | COMMA list        #midList
            ;

predicate   : startTerm midTerm*      //A startTerm followed by zero or more midTerms
            ;

fact        : ATOM PAREN predicate PAREN PERIOD ;       //An atom followed by a predicate within parentheses and a period
simpleQuery : ATOM PAREN predicate PAREN QUESTION ;     //An atom followed by a predicate within parentheses and a question mark

startQuery  : ATOM PAREN predicate PAREN ;                  //An atom followed by a predicate within parentheses
midQuery    : COMMA ATOM PAREN predicate PAREN ;            //A comma followed by an atom and a predicate within parentheses
endQuery    : COMMA ATOM PAREN predicate PAREN QUESTION ;   //A comma followed by an atom, a predicate within parentheses, and a question mark
conjQuery   : startQuery midQuery* endQuery;                //A startQuery followed by zero or more midQueries and an endQuery

head        : ATOM PAREN predicate PAREN ;                  //An atom followed by a predicate in parenthesis
startBody   : ATOM PAREN predicate PAREN ;                  //An atom followed by a predicate in parenthesis
midBody     : COMMA ATOM PAREN predicate PAREN ;            //A comma followed by an atom and a predicate in parenthesis
endBody     : COMMA ATOM PAREN predicate PAREN PERIOD ;     //A comma followed by an atom, a predicate in parenthesis, and a period
simplePrologRule        : head BACK_ARROW startBody ;                    //A head followed by a back arrow and a startBody
conjunctivePrologRule   : head BACK_ARROW startBody midBody* endBody ;   //A head followed by a back arrow, startBody, zero or more midBodies, and an endBody

input   : fact                      //#inputFact                  //The input is a fact
        | simpleQuery               //#inputSimpleQuery           //The input is a simple query
        | conjQuery                 //#inputConjunctiveQuery      //The input is a conjunctive query
        | simplePrologRule          //#simpleRule                  //The input is a rule
        | conjunctivePrologRule     //conjunctiveRule
        ;

/*
 * Lexer Rules
 */
WS          : [ \t\r\n]+ -> skip ;  //Skips white space
ATOM        : [a-z]+                //A series of letters
            | [a-z]+ [A-Z]+         //A series of lowercase letters followed by uppercase letters
            | [a-z]+ [A-Z]+ ATOM    //A series of lowercase and uppercase letters
            ;
PAREN       : '('  //Open parenthesis
            | ')'  //Closed parenthesis
            ;
COMMA       : ',' ;                 //A comma
PERIOD      : '.' ;                 //A period
QUESTION    : '?' ;                 //A question mark
VARIABLE    : [A-Z]+                //A series of capital letters
            | [A-Z] ATOM            //A capital letter followed by a series of lowercase letters
            | [A-Z]+ ATOM VARIABLE  //Capital letters followed by lowercase and uppercase letters
            ;
BACK_ARROW   : ':-' ;       //The back arrow

BRACKET     : '[' | ']' ;   //An open or closed bracket
DIVIDER     : '|' ;         //An OR symbol (used in lists)
