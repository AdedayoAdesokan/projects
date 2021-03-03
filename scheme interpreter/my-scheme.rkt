#lang racket

;determines if the given expression is self-evaluating
(define (self-evaluating? expr)
  (if (number? expr)  ;a number is self-evaluating
      #t
      (if (string? expr)  ;a string is self-evaluating
          #t
          (if (boolean? expr)
              #t
              #f))))


;determines if the given expression is a variable
(define (variable? expr)
  (symbol? expr))  ;variables are represented by symbols


;determines if the 1st element in the expression is the given tag
(define (tagged-list? expr tag)
  (if (pair? expr)
      (if (eq? (car expr) tag)  ;if the 1st emelemnt in the expression == tag
          #t  ;return true
          #f)  ;else return false
      #f))


;determines if the given expression is quoted
(define (quoted? expr)
  (tagged-list? expr 'quote))
;gets the text of the quoted list
(define (text-of-quotation expr)
  (cadr expr))


;determines if the given expression is an assignment
(define (assignment? expr)
  (tagged-list? expr 'set!))  ;an assignment is indicated with set!
;gets the variable that's being assigned a new value
(define (assignment-variable expr)
  (cadr expr))
;gets the new value of the assignment
(define (assignment-value expr)
  (caddr expr))


;determines if the given expresson is a definition
(define (definition? expr)
  (tagged-list? expr 'define))
;gets the variable of the definition expression
(define (definition-variable expr)
  (if (symbol? (cadr expr))  ;if the 1st element of the rest is a symbol
      (cadr expr)  ;return the symbol
      (caadr expr)))  ;else return the 1st of the 1st of the rest
;gets the value of the definition expression
(define (definition-value expr)
  (if (symbol? (cadr expr))  ;if the 1st element of the rest is a symbol
      (caddr expr)  ;get the 1st of the rest of the rest
      (make-lambda (cdadr expr) (cddr expr))))  ;converts the expression into a lambda expr


;determines if the given expression is a lambda expression
(define (lambda? expr)
  (tagged-list? expr 'lambda))
;gets the parameters of the lambda expression
(define (lambda-parameters expr)
  (cadr expr))
;gets the body of the lambda expression
(define (lambda-body expr)
  (cddr expr))  ;might have to be caddr
;converts an expression into a lambda expression given the parameters and body
(define (make-lambda parameters body)
  (cons 'lambda (cons parameters body)))


;determines if the given expression is an if-statement
(define (if? expr)
  (tagged-list? expr 'if))
;gets the predicate expression of the if-statement 
(define (if-predicate expr)
  (cadr expr))
;gets the consequent exoression of the if-statement
(define (if-consequent expr)
  (caddr expr))
;gets the alternative expression of the if-statement
(define (if-alternative expr)
  (if (not (null? (cdddr expr)))
      (cadddr expr)
      'false))
;converts an expression into an if-statement given a predicate, consequent and alternative
(define (make-if predicate consequent alternative)
  (list 'if predicate consequent alternative))


;determines if thr given expression is a begin expression
(define (begin? expr)
  (tagged-list? expr 'begin))
;gets the sequence of expressions in the begin expression
(define (begin-actions expr)
  (cdr expr))
;determines if the given expression is the last expression in the sequence
(define (last-expr? sequence)
  (null? (cdr sequence)))
;gets the first-expression in the sequence 
(define (first-expr sequence)
  (car sequence))
;gets the rest of the expressions in the sequence
(define (rest-exprs sequence)
  (cdr sequence))
;converts the given sequence into a begin expression
(define (make-begin sequence)
  (cons 'begin sequence))
;converts a sequence into a single expression
(define (seq-to-expr sequence)
  (cond ((null? sequence) sequence)
        ((last-expr? sequence) (first-expr sequence))
        (else (make-begin sequence))))


;determines if the given expression is an and expression
(define (and? expr)
  (tagged-list? expr 'and))
;gets the parameters of the and expression
(define (and-parameters expr)
  (cdr expr))
;determines if the given expression is an or expression
(define (or? expr)
  (tagged-list? expr 'or))
;gets the parameters of the or expression
(define (or-parameters expr)
  (cdr expr))


;deetermines of the given expression is a procedure application
(define (application? expr)
  (pair? expr))
;gets the operator of the given procedure application
(define (operator expr)
  (car expr))
;gets the list of operands of the given procedure application
(define (operands expr)
  (cdr expr))
;determines if there aren't any operands in the given list of operands
(define (no-operands? operands)
  (null? operands))
;gets the first operand in the given list of operands 
(define (first-operand operands)
  (car operands))
;gets the rest of the operands of the given list of operands
(define (rest-operands operands)
  (cdr operands))


;determines if the given expression is a conditional statement
(define (cond? expr)
  (tagged-list? expr 'cond))
;gets the clauses of the conditional statement 
(define (cond-clauses expr)
  (cdr expr))
;gets the predicate clause of the given clauses
(define (cond-predicate clauses)
  (car clauses))
;determines if the given clauses is an else clause 
(define (cond-else-clause? clause)
  (eq? (cond-predicate clause) 'else))
;gets the rest of the clauses in the given clauses
(define (cond-actions clauses)
  (cdr clauses))
;converts the given clauses into an if satement
(define (expand-clauses clauses)
  (if (null? clauses)  ;if there's no else clause, return false
      'false
      (let ((first (car clauses))  ;let first = the 1st clauses in the given clauses
            (rest (cdr clauses)))  ;let rest = the rest of the clauses 
        (if (cond-else-clause? first)  ;if you're at the else clause
            (if (null? rest)  ;if it's the last clause in the list
                (seq-to-expr (cond-actions first))  ;return the empty list
                (error "ELSE clause isn't last -- COND-TO-IF" clauses))  ;else throw error
            (make-if (cond-predicate first)  ;convert to an if-statement 
                     (seq-to-expr (cond-actions first))
                     (expand-clauses rest))))))
;converts the given conditional statement into an if statement
(define (cond-to-if expr)
  (expand-clauses (cond-clauses expr)))


;determines whether the given parameter is true
(define (true? x)
  (eq? x true))
;determines whether the given parameter is false
(define (false? x)
  (eq? x false))


;converts the expression to a procedure given the parameters, body and environment
(define (make-procedure parameters body env)
  (list 'procedure parameters body env))
;determines of the given parameter is a procedure
(define (compound-procedure? p)
  (tagged-list? p 'procedure))
;gets the parameters of the given procedure 
(define (procedure-parameters p)
  (cadr p))
;gets the body of the given procedure 
(define (procedure-body p)
  (caddr p))
;gets the environment of the given procedure 
(define (procedure-environment p)
  (cadddr p))


;gets the enclosing environment of a given environment
(define (enclosing-environment env)
  (cadr env))
;gets the first frame of a given environment
(define (frame env)
  (car env))
;gets the id of the given environment
(define (id env)
  (caddr env))
;defines an empty environment
(define the-empty-environment '())
;makes an environment given a frame, enclosing environment and an id
(define (make-environment frame enclosing-env id)
  (list frame enclosing-env id))


;makes a frame given a list of variables and vales
(define (make-frame variables values)
  (cons variables values))
;gets the list of variables of a given frame
(define (frame-variables frame)
  (car frame))
;gets the list of values of a given frame
(define (frame-values frame)
  (cdr frame))
;adds a new variable and value to an environment
;returns a new environment with an updated frame
(define (add-binding-to-env var val env) 
  (make-environment (make-frame  ;creates a new frame for the environment 
                     (append (frame-variables (frame env)) (list var))  ;adds the variable to the frame
                     (append (frame-values (frame env)) (list val)))   ;adds the value to the frame
                    (enclosing-environment env)  ;determines the new environment's enclosing environment
                    (id env)))  ;adds the environments ID


;creates a new nevironment whose enclosing environment is the given base environment
(define (extend-environment vars vals base-env)
  (if (= (length vars) (length vals))  ;if the number of variables == number of values
      (make-environment (make-frame vars vals) base-env (+ (id base-env) 1)) ;create new env
      (if (< (length vars) (length vals))  ;else throw an error
          (error "Too many arguments supplied" vars vals)
          (error "Too few arguments supplied" vars vals))))
;searches the environment for the given variable
(define (lookup-variable-value var env)
  (define (env-loop env)
    (define (scan vars vals)
      (cond ((null? vars)  ;if there's no more variables in the frame
             (env-loop (enclosing-environment env)))  ;search the enclosing environment
            ((eq? var (car vars))  ;if we find the variable, return it
             (car vals))
            (else (scan (cdr vars) (cdr vals)))))  ;else scan the rest of the variables
    (if (eq? env the-empty-environment)  ;if we reach the empty environment, throw error
        (error "Unbound variable:" var)
        (let ((frame (frame env)))  ;let frame = the frame of the environment
          (scan (frame-variables frame)  ;scan the environment's variables and values
                (frame-values frame)))))
  (env-loop env))  ;begin with the given environment 
;helmper method that reurns a new list of values with the old-val replaced with the new-val
(define (update-value old-val new-val env)
  (define (traverse values) 
    (if (eq? (car values) old-val)  ;if the current element == the old value
        (append (list new-val) (rest values))  ;replace the old value with the new one
        (append (list (car values)) (traverse (rest values)))))  ;else keep traversing
  (traverse (frame-values (frame env))))  ;traverse the list of values of the given env
;creates a new environment with an updated value
(define (update-variable-value var val env)
  (define (env-loop env)
    (define (scan vars vals)
      (cond ((null? vars)  ;if there's no more variables in the frame
             (env-loop (enclosing-environment env)))  ;search the enclosing environment
            ((eq? var (car vars))  ;if we find the variable
             (make-environment  ;create a new environment with the updated value
              (make-frame (frame-variables (frame env))
                          (update-value (car vals) val env))
              (enclosing-environment env)
              (id env)))
            (else (scan (cdr vars) (cdr vals)))))  ;else scan the rest of the variables
    (if (eq? env the-empty-environment)  ;if we reach the empty environment, throw error
        (error "Unbound variable:" var)
        (let ((frame (frame env)))  ;let frame = the frame of the environment
          (scan (frame-variables frame)  ;scan the environment's variables and values
                (frame-values frame)))))
  (env-loop env))  ;begin with the given environment 
;adds the given variable and value to the environment
(define (define-variable var val env)
  (let ((frame (frame env)))  ;let frame = the environment's frame
    (define (scan vars vals)
      (cond ((null? vars)  ;if the variable is not already in the environment
             (add-binding-to-env var val env))  ;add to to the environment
            ((eq? var (car vars))  ;if the variable is already in the environment
             (error "Variable already defined:" var))  ;throw an error
            (else (scan (cdr vars) (cdr vals)))))  ;else scan rest of the variables/values
    (scan (frame-variables frame) (frame-values frame))))  ;scan the variables and values


;gets the list of arguments a procedure will be applied to
(define (list-of-values exprs env)
  (if (no-operands? exprs)
      (list)
      (cons (my-sub-eval (first-operand exprs) env)  ;use my-sub-eval to evaluate the first operand and add it to the list
            (list-of-values (rest-operands exprs) env))))  ;evaluate the rest of the operands
;evaluates the given if-statement
(define (eval-if expr env)
  (if (true? (my-sub-eval (if-predicate expr) env))  ;if the predicate evaluates to true
      (my-sub-eval (if-consequent expr) env)  ;evaluate the consequent
      (my-sub-eval (if-alternative expr) env)))  ;else evaluate the alternative
;evaluates the expressions given in a sequence
(define (eval-sequence exprs env)
  (cond ((last-expr? exprs) (my-sub-eval (first-expr exprs) env))  ;evaluate the last expression
        (else ;(my-sub-eval (first-expr exprs) env)  ;evaluate the expressions in order
              (eval-sequence (rest-exprs exprs) (my-sub-eval (first-expr exprs) env)))))
;evaluates the given and expression
(define (eval-and exprs env)
  (if (last-expr? exprs)  ;evaluate the last expression and return the boolean answer
      (my-sub-eval (first-expr exprs) env)
      (if (my-sub-eval (first-expr exprs) env)  ;if the 1st expression is true
          (eval-and (rest-exprs exprs) env)  ;evaluate the remaining expressions
          false)))  ;else return false
;evaluates the given or expression
(define (eval-or exprs env)
  (if (last-expr? exprs)  ;evaluate the last expression and return the boolean answer
      (my-sub-eval (first-expr exprs) env)
      (if (my-sub-eval (first-expr exprs) env)  ;if the 1st expression is true
          true  ;return true
          (eval-or (rest-exprs exprs) env))))  ;else evaluate the remaining expressions
;evaluates the given assignment expression
(define (eval-assignment expr env)
  (update-variable-value (assignment-variable expr)
                         (my-sub-eval (assignment-value expr) env)
                         env))  ;update the given environment
;evaluates the given define expression
(define (eval-definition expr env)
  (define-variable (definition-variable expr)
                   (my-sub-eval (definition-value expr) env)
                   env))
;evaluates the given espression within the given environment
(define (my-sub-eval expr env)
  (cond ((self-evaluating? expr) expr)  ;evaluates self-evaliating expressions
        ((variable? expr) (lookup-variable-value expr env))  ;evaluates variables
        ((quoted? expr) (text-of-quotation expr))  ;evaluates quoted  expressions
        ((assignment? expr) (eval-assignment expr env))  ;evaluates assingments
        ((definition? expr) (eval-definition expr env))  ;evaluates definitions
        ((if? expr) (eval-if expr env))  ;evaluates if-statements
        ((lambda? expr)  ;evaluates lambda expressions
         (make-procedure (lambda-parameters expr)
                         (lambda-body expr)
                         env))
        ((begin? expr)  ;evaluates begin-statements
         (eval-sequence (begin-actions expr) env))
        ((cond? expr) (my-sub-eval (cond-to-if expr) env))  ;evaluates conditional statements
        ((and? expr)  ;evaluates and expressions
         (eval-and (and-parameters expr) env))
        ((or? expr)  ;evaluates or expressions
         (eval-or (or-parameters expr) env))
        ((application? expr)  ;evaluates applications
         (my-apply (my-sub-eval (operator expr) env)
                   (list-of-values (operands expr) env)
                   env));)                  
        (else  ;if the type of expression is not determined, throw an error
         (error "Unknown expression type -- MY-EVAL: " expr))))


;A list of all of the accepted primitive procedures
(define primitive-procedures
  (list (list 'cons cons) (list 'car car) (list 'cdr cdr) (list '+ +)
        (list '- -) (list '* *) (list '/ /) (list 'remainder remainder)
        (list 'modulo modulo) (list '= =) (list '> >) (list '< <)
        (list '>= >=) (list '<= <=) (list 'eq? eq?) (list 'equal? equal?)
        (list 'empty? empty?) (list 'number? number?) (list 'pair? pair?)
        (list 'symbol? symbol?)))
;a list of the names of the primitive procedures
(define (primitive-procedure-names)
  (map car primitive-procedures))
;determines if a procedure is a primitive procedure
(define (primitive-procedure? p)
  (tagged-list? p 'primitive))
;the implementation procedures for the primitive procedures
(define (primitive-implementation p)
  (cadr p))
;a list of the implementations of the primitive procedures
(define (primitive-procedure-objects)
  (map (lambda (p) (list 'primitive (cadr p)))
       primitive-procedures))


;calls the apply function
(define apply-in-underlying-scheme apply)
;applies the implementation procedure to the arguments
(define (apply-primitive-procedure p args)
  (apply-in-underlying-scheme (primitive-implementation p) args))
;applies the procedure to the arguments given an environment
(define (my-apply procedure arguments env)
  (cond ((primitive-procedure? procedure)  ;applies primitive procedures
         (apply-primitive-procedure procedure arguments))
        ((compound-procedure? procedure)  ;applies compound procedures
         (eval-sequence
          (procedure-body procedure)  ;sequentially evaluates the body of the procedure
          (extend-environment  ;creates the environment for evaluating the procedure's body
           (procedure-parameters procedure)
           arguments
           (if (empty? (enclosing-environment env))
               (extend-environment (frame-variables (frame env)) (frame-values (frame env)) (procedure-environment procedure))
               (extend-environment (frame-variables (frame (enclosing-environment env))) (frame-values (frame (enclosing-environment env))) (procedure-environment procedure))))))
           ;(extend-environment (frame-variables (frame env)) (frame-values (frame env)) (procedure-environment procedure)))))
        (else  ;if the given procedure is neither a primitive nor compound, throw an error
         (error "Unknown procedure type -- MY-APPLY: " procedure))))

;sets up the initial environment
(define (setup-environment)
  (let ((initial-env  ;creates an environment with all of the primitive procedures
         (make-environment (make-frame (primitive-procedure-names)
                                 (primitive-procedure-objects))
                             the-empty-environment
                             1)))
    (define initial1 (define-variable 'true true initial-env))  ;adds true to the env
    (define initial2 (define-variable 'false false initial1))  ;adds flase to the env
    (define initial3 (define-variable '#t #t initial2))  ;adds #t to the env
    (define initial4 (define-variable '#f #f initial3))  ;adds #f to the env
    (define initial5 (define-variable 'define 'BUILT-IN-FUNCTION initial4))  ;adds define to the env
    (define initial6 (define-variable 'lambda 'BUILT-IN-FUNCTION initial5))  ;adds lambda to the env
    (define initial7 (define-variable 'set! 'BUILT-IN-FUNCTION initial6))  ;adds set! to the env
    (define initial8 (define-variable 'if 'BUILT-IN-FUNCTION initial7))  ;adds if to the env
    (define initial9 (define-variable 'quote 'BUILT-IN-FUNCTION initial8))  ;adds quote to the env
    initial9  ;returns the initial environment
    ))
;gets the initial global environment
(define the-global-environment
  (setup-environment))


;a helper function that evaluates a list of S-expressions
(define (my-sub-eval-prog exprs)
  (define (traverse exprs env)  ;traverses the list of s-expressions given an environment
    (if (empty? exprs)  ;if the expression is empty, return the environment
        env  ;depending on the expressions, the environment may return an answer or an env
        (traverse (cdr exprs) (my-sub-eval (car exprs) env))))  ;else traverse the rest of the expressions where the new environment is built from the first expression
  (traverse exprs the-global-environment))  ;begin traversal with the global environment
;evaluates a list of S-expressions
(define (eval-prog exprs)
  (if (primitive-procedure? (my-sub-eval-prog exprs))
      'BUILT-IN-FUNCTION
      (my-sub-eval-prog exprs)))
;evaluates an expression given the environment and a list of mappings from environment names to environments
(define (my-eval expr env env-map)
  (if (definition? expr)  ;if the expression is a definition
      (cons 'BUILT-IN-FUNCTION (my-sub-eval expr env))  ;return built-in-function with the new environment
      (if (assignment? expr)  ;if the expression is an assignment
          (cons 'BUILT-IN-FUNCTION (my-sub-eval expr env))  ;return built-in-function with the updated environment
          (if (variable? expr)  ;if the expression is a variable
              (if (primitive-procedure? (my-sub-eval expr env))  ;if it evaluates to a primitive procedure
                (cons 'BUILT-IN-FUNCTION env)  ;return built-in-function with the given environment
                (cons (my-sub-eval expr env) env))  ;else, evaluate the variable and return it with the environment 
              (cons (my-sub-eval expr env) env)))))  ;else, return a cons pair where the first element is the evaluation result and the second element is the new global environment.  


;gets the initial global environment
(define (get-global-env)
  the-global-environment)
;gets the initial mapping of environment names to environments
(define (get-global-env-map)
  (make-immutable-hash (list (cons (id (get-global-env)) (get-global-env)))))