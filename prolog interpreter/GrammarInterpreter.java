import java.util.ArrayList;
import java.util.LinkedList;

public class GrammarInterpreter extends PrologBaseVisitor<PrologHolder> {

    /*
     * Interprets an atom within a list
     * @return an atom within a prologHolder
     */
    public PrologHolder visitListAtom(PrologParser.ListAtomContext ctx) {
        return new PrologHolder(ctx.ATOM().getText());  //Returns the atom within a holder
    }

    /*
     * Interprets a variable within a list
     * @return a variable within a prologHolder
     */
    public PrologHolder visitListVariable(PrologParser.ListVariableContext ctx) {
        PrologHolder holder = new PrologHolder(ctx.VARIABLE().getText());  //Gets the variable and places it into a holder
        holder.changeToVariable();  //Change the holder to indicate that it's holding a variable
        return holder;  //Returns the variable within a holder
    }

    /*
     * Interprets an atom within a list that's preceded by a comma
     * @return an atom within a prologHolder
     */
    public PrologHolder visitMiddleListAtom(PrologParser.MiddleListAtomContext ctx) {
        return new PrologHolder(ctx.ATOM().getText());  //Returns the atom within a holder
    }

    /*
     * Interprets a variable within a list that's preceded by a comma
     * @return a variable within a prologHolder
     */
    public PrologHolder visitMiddleListVariable(PrologParser.MiddleListVariableContext ctx) {
        PrologHolder holder = new PrologHolder(ctx.VARIABLE().getText());  //Gets the variable and places it into a holder
        holder.changeToVariable();  //Change the holder to indicate that it's holding a variable
        return holder;  //Returns the variable within a holder
    }

    /*
     * Interprets an empty list
     * @return null to signify an empty list
     */
    public PrologHolder visitEmptyList(PrologParser.EmptyListContext ctx) {
        return null;
    }

    /*
     * Interprets a list that contains a divider (often used in rules)
     * @return a list within a prologHolder
     */
    public PrologHolder visitSoloDividerList(PrologParser.SoloDividerListContext ctx) {
        LinkedList<String> list = new LinkedList<>();  //Create a new list
        PrologHolder headHolder = visit(ctx.getChild(1));  //Get the holder fom the child
        String head = headHolder.getAtom();  //Get the head of the list
        list.add(head);  //Add the head to the list

        PrologHolder restHolder = visit(ctx.getChild(3));  //Get the holder from the child
        String rest = restHolder.getAtom();  //Get the rest of the list
        list.add(rest);  //Add the rest to the list

        PrologHolder listHolder = new PrologHolder(list);  //Place the list in a prologHolder
        if (headHolder.isVariable() || restHolder.isVariable()) {  //If head or rest is a variable
            listHolder.changeToVariable();  //Change the holder to indicate that it contains a variable
        }
        return listHolder;  //Return the list in a holder
    }

    /*
     * Interprets a list that contains a divider that's preceded by a comma
     * @return a list within a prologHolder
     */
    public PrologHolder visitMiddleDividerList(PrologParser.MiddleDividerListContext ctx) {
        LinkedList<String> list = new LinkedList<>();  //Create a new list
        boolean variable = false;  //Indicates if this list contains a variable
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {  //Visit every child
            if (i != ctx.getChildCount() - 3) {  //Ignore the divider
                PrologHolder atomHolder = visit(ctx.getChild(i));  //Get the holder from the child
                String atom = atomHolder.getAtom();  //Get the atom in the holder
                list.add(atom);  //Add the atom to the list
                if (atomHolder.isVariable()) {  //If the atom is a variable
                    variable = true;  ///Set variable to true
                }
            }
        }
        PrologHolder listHolder = new PrologHolder(list);  //Place the list in a prologHolder
        if (variable) {  //If the list contains a variable
            listHolder.changeToVariable();  //Change the holder to indicate that it contains a variable
        }
        return listHolder;  //Return the list in a holder
    }

    /*
     * Interprets a list of elements
     * @return a list within a prologHolder
     */
    public PrologHolder visitNormalList(PrologParser.NormalListContext ctx) {
        LinkedList<String> list = new LinkedList<>();  //Create a new list
        boolean variable = false;  //Initialize variable to false
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {  //Visit each child
            PrologHolder atomHolder = visit(ctx.getChild(i));  //Get the holder from the child
            String atom = atomHolder.getAtom();  //Get the atom in the holder
            list.add(atom);  //Add the atom to the list
            if (atomHolder.isVariable()) {  //If the holder contains a variable
                variable = true;  //Set variable equal to true
            }
        }
        PrologHolder listHolder = new PrologHolder(list);  //Place the list in a prologHolder
        if (variable) {  //If variable is true
            listHolder.changeToVariable();  //Change the holder to indicate that it contains a variable
        }
        return listHolder;  //Return the list in a holder
    }

    /*
     * Interprets a startTerm, which is a single atom
     * @return an atom within a PrologHolder
     */
    public PrologHolder visitAtom(PrologParser.AtomContext ctx) {
        return new PrologHolder(ctx.ATOM().getText());  //Returns the atom within a holder
    }

    /*
     * Interprets a startTerm, which is a single variable
     * @return an atom within a PrologHolder
     */
    public PrologHolder visitVariable(PrologParser.VariableContext ctx) {
        PrologHolder holder = new PrologHolder(ctx.VARIABLE().getText());  //Gets the variable and places it into a holder
        holder.changeToVariable();  //Change the holder to indicate that it's holding a variable
        return holder;  //Returns the variable within a holder
    }

    /*
     * Interprets a single list
     * @return the list within a prologHolder
     */
    public PrologHolder visitSoloList(PrologParser.SoloListContext ctx) {
        return visit(ctx.getChild(0));  //Vist the only child to get the list
    }

    /*
     * Interprets a midTerm
     * @return an atom within a PrologHolder
     */
    public PrologHolder visitMidAtom(PrologParser.MidAtomContext ctx) {
        return new PrologHolder(ctx.ATOM().getText());  //Returns the atom within a holder
    }

    /*
     * Interprets a midTerm
     * @return a variable within a PrologHolder
     */
    public PrologHolder visitMidVariable(PrologParser.MidVariableContext ctx) {
        PrologHolder holder = new PrologHolder(ctx.VARIABLE().getText());  //Gets the variable and places it into a holder
        holder.changeToVariable();  //Change the holder to indicate that it's holding a variable
        return holder;  //Returns the variable within a holder
    }

    /*
     * Interprets a list that is preceded by a comma
     * @return a list within a prologHolder
     */
    public PrologHolder visitMidList(PrologParser.MidListContext ctx) {
        return visit(ctx.getChild(1));  //Ignore the comma at index 0 and visit the child at index 1 to get the list
    }

    /*
     * Interprets a predicate
     * @return a predicate within a PrologHolder
     */
    public PrologHolder visitPredicate(PrologParser.PredicateContext ctx) {
        Predicate predicate = new Predicate();  //Creates a predicate
        for (int i = 0; i < ctx.getChildCount(); i++) {  //Visits each child
            if (visit(ctx.getChild(i)) != null) {
                PrologHolder atomHolder = visit(ctx.getChild(i));  //Get the holder from the child
                if (atomHolder.getType().equals("atom")) {
                    String atom = atomHolder.getAtom();  //Get the atom within the holder
                    predicate.addAtom(atom);  //Add the atom to the predicate
                    if (atomHolder.isVariable()) {  //If the holder contains a variable
                        predicate.enableVariable();  //Change the predicate to indicate that it's holding a variable
                    }
                } else if (atomHolder.getType().equals("list")) {
                    ArrayList<LinkedList<String>> lists = predicate.getLists();
                    LinkedList<String> list = atomHolder.getList();
                    lists.add(list);
                    if (atomHolder.isVariable()) {  //If the holder contains a variable
                        predicate.enableVariable();  //Change the predicate to indicate that it's holding a variable
                    }
                }
            }
        }
        return new PrologHolder(predicate);  //Return the predicate within a holder
    }

    /*
     * Interprets Facts
     * @return a Fact within a PrologHolder
     */
    public PrologHolder visitFact(PrologParser.FactContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the fact
        PrologHolder predicateHolder = visit(ctx.getChild(2));  //The child at index 2 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Fact fact = new Fact(functor);  //Create a fact from the given functor
        fact.addPredicate(predicate);  //Add the predicates to the fact

        return new PrologHolder(fact);  //Return the fact within a holder
    }

    /*
     * Interprets a query
     * @return a query within a PrologHolder
     */
    public PrologHolder visitSimpleQuery(PrologParser.SimpleQueryContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the query
        PrologHolder predicateHolder = visit(ctx.getChild(2));  //The child at index 2 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Query query = new Query(functor, predicate);  //Create a query from the functor and predicate
        return new PrologHolder(query);  //Return the query within a holder
    }

    /*
     * Interprets a startQuery
     * @return a query within a PrologHolder
     */
    public PrologHolder visitStartQuery(PrologParser.StartQueryContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the query
        PrologHolder predicateHolder = visit(ctx.getChild(2));  //The child at index 2 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Query query = new Query(functor, predicate);  //Create a query from the functor and predicate
        return new PrologHolder(query);  //Return the query within a holder
    }

    /*
     * Interprets a midQuery
     * @return a query within a PrologHolder
     */
    public PrologHolder visitMidQuery(PrologParser.MidQueryContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the query
        PrologHolder predicateHolder = visit(ctx.getChild(3));  //The child at index 3 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Query query = new Query(functor, predicate);  //Create a query from the functor and predicate
        return new PrologHolder(query);  //Return the query within a holder
    }

    /*
     * Interprets a endQuery
     * @return a query within a PrologHolder
     */
    public PrologHolder visitEndQuery(PrologParser.EndQueryContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the query
        PrologHolder predicateHolder = visit(ctx.getChild(3));  //The child at index 3 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Query query = new Query(functor, predicate);  //Create a query from the functor and predicate
        return new PrologHolder(query);  //Return the query within a holder
    }

    /*
     * Interprets a conjunctive query
     * @return a conjunctive query within a PrologHolder
     */
    public PrologHolder visitConjQuery(PrologParser.ConjQueryContext ctx) {
        ConjunctiveQuery queries = new ConjunctiveQuery();  //Create a conjunctive query
        for (int i = 0; i < ctx.getChildCount(); i++) {  //Visit each child
            PrologHolder queryHolder = visit(ctx.getChild(i));  //Get the holder from the child
            Query query = queryHolder.getQuery();  //Get the query within the holder
            queries.addQuery(query);  //Add the query to the list of queries
        }

        return new PrologHolder(queries);  //Return the conjunctive query within a holder
    }

    /*
     * Interprets the head of a rule
     * @return a fact within a PrologHolder
     */
    public PrologHolder visitHead(PrologParser.HeadContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the fact
        PrologHolder predicateHolder = visit(ctx.getChild(2));  //The child at index 2 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Fact fact = new Fact(functor);  //Create a fact from the given functor
        fact.addPredicate(predicate);  //Add the predicates to the fact

        return new PrologHolder(fact);  //Return the fact within a holder
    }

    /*
     * Interprets the first query of a rule
     * @return a query within a PrologHolder
     */
    public PrologHolder visitStartBody(PrologParser.StartBodyContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the query
        PrologHolder predicateHolder = visit(ctx.getChild(2));  //The child at index 2 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Query query = new Query(functor, predicate);  //Create a query from the functor and predicate
        return new PrologHolder(query);  //Return the query within a holder
    }

    /*
     * Interprets the mid-queries of a rule
     * @return a query within a PrologHolder
     */
    public PrologHolder visitMidBody(PrologParser.MidBodyContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the query
        PrologHolder predicateHolder = visit(ctx.getChild(3));  //The child at index 3 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Query query = new Query(functor, predicate);  //Create a query from the functor and predicate
        return new PrologHolder(query);  //Return the query within a holder
    }

    /*
     * Interprets the last query of a rule
     * @return a query within a PrologHolder
     */
    public PrologHolder visitEndBody(PrologParser.EndBodyContext ctx) {
        String functor = ctx.ATOM().getText();  //Gets the functor of the query
        PrologHolder predicateHolder = visit(ctx.getChild(3));  //The child at index 3 is the predicate, this gets the predicate
        Predicate predicate = predicateHolder.getPredicate();  //Get the predicate stored in the holder

        Query query = new Query(functor, predicate);  //Create a query from the functor and predicate
        return new PrologHolder(query);  //Return the query within a holder
    }

    /*
     * Interprets a simple rule
     * @return a PrologRule within a PrologHolder
     */
    public PrologHolder visitSimplePrologRule(PrologParser.SimplePrologRuleContext ctx) {
        PrologHolder factHolder = visit(ctx.getChild(0));  //The 1st child holds a fact
        Fact head = factHolder.getFact();  //Get the fact from the holder

        PrologHolder queryHolder = visit(ctx.getChild(2));  //The 3rd child holds a query
        Query query = queryHolder.getQuery();  //Get the query from the holder

        PrologRule prologRule = new PrologRule(head, query);  //Create a rule from the head and the query
        return new PrologHolder(prologRule);  //Return the rule within a holder
    }

    /*
     * Interprets a conjunctive rule
     * @return a PrologRule within a PrologHolder
     */
    public PrologHolder visitConjunctivePrologRule(PrologParser.ConjunctivePrologRuleContext ctx) {
        ConjunctiveQuery queries = new ConjunctiveQuery();
        PrologHolder factHolder = visit(ctx.getChild(0));  //The 1st child holds a fact
        Fact head = factHolder.getFact();  //Get the fact from the holder

        for (int i = 2; i < ctx.getChildCount(); i++) {  //Starts at the 1st query in the rule
            PrologHolder queryHolder = visit(ctx.getChild(i));  //Get the PrologHolder from the child
            Query query = queryHolder.getQuery();  //Get the query from the holder
            queries.addQuery(query);  //Add the query to the conjunctive query
        }

        PrologRule prologRule = new PrologRule(head, queries);  //Create a rule from the head and the conjunctive query
        return new PrologHolder(prologRule);  //Return the rule within a holder
    }


    /*
     * The root node
     * @return a PrologHolder containing a Fact, SimpleQuery, ConjunctiveQuery, or Rule depending on the input
     */
    public PrologHolder visitInput(PrologParser.InputContext ctx) {
        return visitChildren(ctx);
    }
}
