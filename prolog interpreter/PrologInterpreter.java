import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class PrologInterpreter {
    private ArrayList<PrologHolder> program;

    /*
     * A constructor that creates the array list of PrologHolders
     */
    public PrologInterpreter() {
        program = new ArrayList<>();

    }

    /*
     * Determines if the given query is non ground
     * @param query the query that is being evaluated
     * @return true if the query contains a variable
     */
    public boolean isNonGroundQuery(Query query) {
        boolean variable = false;  //Initialize variable to false
        for (int i = 0; i < query.getArity(); i++) {  //Iterate through the query
            String atom = query.getAtom(i);  //Get each atom in the query
            if (Character.isUpperCase(atom.charAt(0))) {  //If the first letter is uppercase
                variable = true;  //Set variable equal to true
            }
        }
        return variable;
    }

    /*
     * Evaluates the given input
     * @param input the input to be evaluated
     * @return a PrologHolder containing a Fact, Rule, or Query
     */
    public PrologHolder evaluateInput(String input) {
        CharStream inputStream = CharStreams.fromString(input);  //Create a CharStream
        PrologLexer lexer = new PrologLexer(inputStream);  //Create a lexer form the Infix Grammar and pass the CharStream into it
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);  //Create a Common Token Stream and pass the lexer into it
        PrologParser parser = new PrologParser(commonTokenStream);  //Create a parser form the Infix Grammar and pass the token stream into it
        ParseTree tree = parser.input();  //Create a parse tree that starts at the input parse rule
        GrammarInterpreter eval = new GrammarInterpreter();  //Create an GrammarInterpreter that evaluates the parse tree
        PrologHolder holder = eval.visit(tree);  //Evaluate the parse tree and return the holder

        if (holder.getType().equals("simple query") || holder.getType().equals("conjunctive query")) {  //If the input is a query
            if (holder.getType().equals("simple query")) {  //If it is a simple query
                Query query = holder.getQuery();  //Get the query from the holder
                Predicate queryPredicate = query.getPredicate();  //Get the predicate from the query
                if (queryPredicate.isVariable()) {  //If the predicate contains a variable
                    holder.changeToVariable();  //Change the holder to indicate that it contains a variable
                }
            } else {  //If the query is a conjunctive query
                ConjunctiveQuery queries = holder.getConjunctiveQuery();  //Get the queries from the holder
                for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
                    Query query = queries.getQuery(i);  //Get the query it index i
                    Predicate queryPredicate = query.getPredicate();  //Get the predicate from the query
                    if (queryPredicate.isVariable()) {  //If the predicate contains a variable
                        holder.changeToVariable();  //Change the holder to indicate that it contains a variable
                        break;  //Exit the loop
                    }
                }
            }
        } else if (holder.getType().equals("rule")) {  //If the input is a rule
            PrologRule rule = holder.getPrologRule();  //Get the rule from the query
            Fact head = rule.getHead();  //Get the head of the rule
            Predicate headPredicate = head.getPredicate(0);  //Get the predicate from the head
            if (headPredicate.isVariable()) {  //If the predicate contains a variable
                holder.changeToVariable();  //Change the holder to indicate that it contains a variable
            } else {  //If the head does not contain a variable
                if (rule.getType().equals("simple query")) {  //If the body of the rule is a simple query
                    Query bodyQuery = rule.getSimpleBody();  //Get the query from the rule
                    Predicate bodyQueryPredicate = bodyQuery.getPredicate();  //Get the predicate from the query
                    if (bodyQueryPredicate.isVariable()) {  //If the predicate contains a variable
                        holder.changeToVariable();  //Change the holder to indicate that it contains a variable
                    }
                } else {  //If the body of the rule is a conjunctive query
                    ConjunctiveQuery bodyQueries = rule.getConjunctiveBody();  //Get the queries from the rule
                    for (int i = 0; i < bodyQueries.getNumberOfQueries(); i++) {  //Iterate through the queries
                        Query bodyQuery = bodyQueries.getQuery(i);  //Get the query it index i
                        Predicate bodyQueryPredicate = bodyQuery.getPredicate();  //Get the predicate from the query
                        if (bodyQueryPredicate.isVariable()) {  //If the predicate contains a variable
                            holder.changeToVariable();  //Change the holder to indicate that it contains a variable
                            break;  //Exit the loop
                        }
                    }
                }
            }
        }
        return holder;
    }

    /*
     * Determines if the program contains a fact that shares the same name as the given functor
     * @param functor the name of the fact being searched for
     * @return true if the program contains a fact with the same name as the given functor
     */
    public boolean containsFact(String functor) {
        boolean found = false;  //initialize found to false;
        for (PrologHolder holder : program) {  //Iterate through the holders within the program
            if (holder.getType().equals("fact")) {  //If the holder contains a fact
                Fact fact = holder.getFact();  //Get the fact from the holder
                if (fact.getFunctor().equals(functor)) {  //If the found fact shares the same name as the fact within factHolder
                    found = true;  //Return true
                    break;  //Exit the loop
                }
            }
        }
        return found;
    }

    /*
     * Determines if the program contains the rule that shares the same name as the given functor
     * @param functor the name of the rule being searched for
     * @return true if the program contains a rule with the same name as the given functor
     */
    public boolean containsRule(String functor) {
        boolean found = false;  //Initialize found to false
        for (PrologHolder holder : program) {  //Iterate through the holders in the program
            if (holder.getType().equals("rule")) {  //If the holder contains a rule
                PrologRule rule = holder.getPrologRule();  //Get the rule from the holder
                Fact head = rule.getHead();  //Get the head from the rule
                if (head.getFunctor().equals(functor)) {  //If the name of the head is the same as the given functor
                    found = true;  //Set found equal to true
                    break;  //Exit the loop
                }
            }
        }
        return found;
    }

    /*
     * Gets a PrologHolder containing a fact that shares the same name with the given functor
     * @param functor the name of the desired fact
     * @return a PrologHolder containing the desired fact
     */
    public PrologHolder getFactHolder(String functor) {
        PrologHolder foundFact = null;  //Initialize the holder to null
        for (PrologHolder holder : program) {  //Iterate through the program
            if (holder.getType().equals("fact")) {  //If the holder is a fact
                Fact fact = holder.getFact();  //Get the fact from the holder
                if (fact.getFunctor().equals(functor)) {  //If the fact shares the same name as the given functor
                    foundFact = holder;  //Set found fact equal to the holder
                    break;  //Exit the loop
                }
            }
        }
        return foundFact;
    }

    /*
     * Evaluates the fact within the given PrologHolder
     * @param factHolder the holder containing the fact
     */
    public void evaluateFact(PrologHolder factHolder) {
        if (containsFact(factHolder.getFact().getFunctor())) {  //If the name of the fact is already within the program
            Fact fact = factHolder.getFact();  //Get the fact from the holder
            String functor = fact.getFunctor();  //Get the functor of the fact
            Predicate predicate = fact.getPredicate(0);  //Get the fact's predicate
            getFactHolder(functor).getFact().addPredicate(predicate);  //Add the predicate to the fact within the program
        } else {  //If the name of the fact is not already in the program
            program.add(factHolder);  //Add the factHolder to the program
        }
    }

    /*
     * Adds the rule to the program
     * @param holder the PrologHolder being added to the program
     */
    public void evaluateRule(PrologHolder ruleHolder) {
        program.add(ruleHolder);
    }

    /*
     * Determines if the given atom is a variable
     *@param atom the atom being evaluated
     * @return true if the atom is a variable
     */
    public boolean isVariable(String atom) {
        //String first = atom.substring(0, 1);
        boolean upper = false;  //Initialize upper to false
        if (Character.isUpperCase(atom.charAt(0))) {  //If the first character in the atom is uppercase
            upper = true;  //Set upper equal to true
        }
        return upper;
    }

    /*
     * Determines if the given query matches the given rule
     * @param query the query being tested to see if it matches the rule
     * @param prologRule the rule being tested to see if it matches the query
     * @return true if the query matches the rule
     */
    public boolean queryMatchesRule(Query query, PrologRule prologRule) {
        boolean matching = false;  //Initialize matching to false
        Fact ruleHead = prologRule.getHead();  //Get the head of the rule
        String queryFunctor = query.getFunctor();  //Get the functor of the query
        String ruleFunctor = ruleHead.getFunctor();  //Get the functor of the rule

        if (queryFunctor.equals(ruleFunctor) && query.getArity() == ruleHead.getArity(0)) {  //If the query and rule have the same functor and arity
            Predicate rulePredicate = ruleHead.getPredicate(0);  //Get the predicate of the rule (the index is 0 because the head of every rule is a fact with only 1 predicate)
            for (int i = 0; i < query.getArity(); i++) {  //Iterate through the query
                boolean variable = false;  //Initialize variable to false
                String queryAtom = query.getAtom(i);  //Get the atom at index i in the query
                String ruleAtom = rulePredicate.getAtom(i);  //Get the atom at index i in the rule
                if (isVariable(ruleAtom)) {  //If the atom in the rule is a variable
                    //ruleAtom = queryAtom;
                    variable = true;  //Set variable equal to true
                }
                //If the last atom in the query matches the last atom in the rule's head or the last atom in the rule is a variable
                if ((i == query.getArity() - 1 && queryAtom.equals(ruleAtom)) || (i == query.getArity() - 1 && variable)) {
                    matching = true;  //Set matching to true
                } else if (!queryAtom.equals(ruleAtom) && !variable) {  //If the atoms don't match and the atom in the rule isn't a variable
                    break;  //Exit the loop
                }
            }
        }
        return matching;
    }

    /*
     * Gets the substitutions for the head of a rule if the rule contains variables
     * @param prologRule the rule being evaluated
     * @param a the query that matches the rules head
     * @return a list of substitutions for the head
     */
    public ArrayList<Substitution> getHeadSubstitutions(PrologRule prologRule, Query a) {
        ArrayList<Substitution> headSubstitutions = new ArrayList<>();  //Create a list of substitutions for the head
        Predicate headPredicate = prologRule.getHead().getPredicate(0);  //Get the predicate from the head
        if (headPredicate.isVariable()) {  //If the predicate contains variables
            for (int i = 0; i < a.getArity(); i++) {  //Iterate through the query
                String atom = a.getAtom(i);  //Get the atom at index i
                String headAtom = headPredicate.getAtom(i);  //Get the atom in the predicate at index i
                if (isVariable(headAtom)) {  //If headAtom is a variable
                    Substitution sub = new Substitution(headAtom);  //Create a substitution for it
                    sub.addSubstitution(atom);  //Add the query's atom to the substitution
                    sub.setIndex(i);  //Set the substitution's index
                    headSubstitutions.add(sub);  //Add the substitution to the list
                }
            }
        }
        return headSubstitutions;
    }

    /*
     * Checks the substitutions associated with the given body query of a PrologRule
     * @param bodySubstitutions the substitutions associated with the bodyQuery
     * @param a the query that matches the head of the rule this bodyQuery belongs to
     * @param bodyQuery the body of a prologRule
     * @return true if matching strings are found between the bodySubstitutions and the query A
     */
    public boolean checkBodySubstitutions(ArrayList<Substitution> bodySubstitutions, Query a, Query bodyQuery) {
        boolean allFound = true;  //Indicates that all of the variables we're found
        for (int i = 0; i < a.getArity(); i++) {  //Iterate through the query
            String atom = a.getAtom(i);  //Get the atom at index i in the query
            if (!isVariable(atom)) {
                if (i < bodyQuery.getArity()) {
                    String ruleAtom = bodyQuery.getAtom(i);  //Get the atom at index i in the body
                    if (isVariable(ruleAtom)) {  //If ruleAtom is a variable
                        Substitution associatedSubstitution = bodySubstitutions.get(i);  //Get the substitution associated with that index
                        boolean foundSub = false;  //Initialize found to false
                        for (int j = 0; j < associatedSubstitution.getSize(); j++) {  //Iterate through the substitution
                            String possibleSubstitution = associatedSubstitution.getSubstitution(j);  //Get the string at index j in the substitution
                            if (isVariable(possibleSubstitution)) {
                                foundSub = true;
                                break;
                            } else if (atom.equals(possibleSubstitution)) {  //If the strings match
                                foundSub = true;  //Set foundSub to true
                                break;  //Exit the loop
                            }
                        }
                        if (!foundSub) {  //If no matching strings are found
                            allFound = false;  //Set all found to false
                            break;  //Exit the loop
                        }
                    }
                }
            }
        }
        return allFound;
    }

    /*
     * Compares the substitutions associated with the head of a PrologRule to the substitutions associated with a body query of that rule
     * @param headSubstitutions the substitutions for the head of the PrologRule
     * @param bodySubstitutions the substitutions for a body query of the PrologRule
     * @param true if matching strings are found between the headSububstitutions and the bodySubstitutions
     */
    public boolean checkHeadSubstitutions(ArrayList<Substitution> headSubstitutions, ArrayList<Substitution> bodySubstitutions) {
        boolean allFound = true;  //Set all found to true
        for (Substitution headSub : headSubstitutions) {  //Iterate through the substitutions for the head
            String headVariable = headSub.getName();  //Get the substitution's variable
            for (int i = 0; i < headSub.getSize(); i++) {  //Iterate through the substitution
                String headSubSubstitution = headSub.getSubstitution(i);  //Get the string at index i in the substitution
                if (!isVariable(headSubSubstitution)) {
                    boolean foundHeadSub = false;  //Initialize foundHeadSub to false
                    for (Substitution bodySub : bodySubstitutions) {  //Iterate through the bodySubstitutions
                        String bodyVariable = bodySub.getName();  //Get the bodySub's variable
                        if (headVariable.equals(bodyVariable)) {  //If the substitutions are for the same variable
                            for (int j = 0; j < bodySub.getSize(); j++) {  //Iterate through the bodySub
                                String bodySubSubstitution = bodySub.getSubstitution(j);  //Get the string at index j
                                if (headSubSubstitution.equals(bodySubSubstitution)) {  //If the strings match
                                    foundHeadSub = true;  //Set foundHeadSub equal to true
                                    break;  //Exit the loop
                                }
                            }
                        }
                    }
                    if (!foundHeadSub) {  //If a head substitution is not found
                        allFound = false;  //Set all found to false
                    }
                }
            }
        }
        return allFound;
    }

    /*
     * Evaluates a fact that contains a list
     * @param Query a ground Query containing a list that matches a fact
     * @param predicate the predicate from the fact that matches the query
     * @return true if the query evaluates to true in terms of the given fact
     */
    public boolean evaluateListFact(Query query, Predicate predicate) {
        ArrayList<Substitution> substitutions = new ArrayList<>();  //Create a list of substitutions
        for (int i = 0; i < predicate.getArity(); i++) {  //Iterate through the predicate
            String predicateAtom = predicate.getAtom(i);  //Get the atom in the predicate at index i
            String queryAtom = query.getPredicate().getAtom(i);  //Get the atom in the query at index i
            if (isVariable(predicateAtom)) {  //If the atom in the predicate is a variable
                Substitution sub = new Substitution(predicateAtom);  //Create a new substitution for the atom
                sub.addSubstitution(queryAtom);  //Add the query's atom to the substitution
                substitutions.add(sub);  //Add the substitution to the list of substitutions
            } else {  //If the atom in the predicate is not a variable
                if (!predicateAtom.equals(queryAtom)) {  //If the atoms don't match
                    return false;  //return false;
                }
            }
        }
        for (int i = 0; i < predicate.getLists().size(); i++) {  //Iterate through the lists in the predicate
            LinkedList<String> predicateList = predicate.getLists().get(i);  //Get the list at index i in the predicate
            LinkedList<String> queryList = query.getPredicate().getLists().get(i);  //Get the list at index i in the query
            if (predicateList.size() < queryList.size()) {
                for (int j = 0; j < predicateList.size(); j++) {  //Iterate through the predicate's list
                    String predicateListAtom = predicateList.get(j);  //Get the atom in the predicate's list at index i
                    String queryListAtom = queryList.get(j);  //Get the atom in the query's list at index i
                    if (isVariable(predicateListAtom)) {  //If the atom in the predicates's list is a variable
                        Substitution sub = new Substitution(predicateListAtom);  //Create a new substitution for the atom
                        sub.addSubstitution(queryListAtom);  //Add the atom from the query's list to the substitution
                        substitutions.add(sub);  //Add the substitution to the list of substitutions
                    } else {  //If the atom in the predicate's list is not a variable
                        if (!predicateListAtom.equals(queryListAtom)) {  //If the atoms don't match
                            return false;  //return false
                        }
                    }

                }
            } else {  //If queryList is smaller than predicateList
                for (int j = 0; j < queryList.size(); j++) {
                    String predicateListAtom = predicateList.get(j);  //Get the atom in the predicate's list at index i
                    String queryListAtom = queryList.get(j);  //Get the atom in the query's list at index i
                    if (isVariable(predicateListAtom)) {  //If the atom in the predicates's list is a variable
                        Substitution sub = new Substitution(predicateListAtom);  //Create a new substitution for the atom
                        sub.addSubstitution(queryListAtom);  //Add the atom from the query's list to the substitution
                        substitutions.add(sub);  //Add the substitution to the list of substitutions
                    } else {  //If the atom in the predicate's list is not a variable
                        if (!predicateListAtom.equals(queryListAtom)) {  //If the atoms don't match
                            return false;  //return false
                        }
                    }
                }
            }
        }

        boolean pass = true;  //Initialize pass to true, determines if the query passes the fact
        outer:
        for (int i = 0; i < substitutions.size(); i++) {  //Iterate through the list of substitutions
            Substitution firstSub = substitutions.get(i);  //Get the substitution at index i
            String firstSubVariable = firstSub.getName();  //Get the name of the variable associated with the substitution
            for (int j = i + 1; j < substitutions.size(); j++) {  //Iterate through the rest of the substitutions
                Substitution nextSub = substitutions.get(j);  //Get the substitution at index j
                String nextSubVariable = nextSub.getName();  //Get the name of the variable associated with the substitution
                if (firstSubVariable.equals(nextSubVariable)) {  //If the variables are the same
                    String firstSubSubstitution = firstSub.getSubstitution(0);  //Get the string associated with the first substitution
                    String nextSubSubstitution = nextSub.getSubstitution(0);  //Get the string associated with the next substitution
                    if (!firstSubSubstitution.equals(nextSubSubstitution)) {  //If the strings are diferent
                        pass = false;  //Set pass equal to false
                        break outer;  //Break out of the loop
                    }
                }
            }
        }
        return pass;
    }

    /*
     * Evaluates a rule that contains a list
     * @param Query a ground Query containing a list that matches a rule
     * @param prologRule the rule that matches the query
     * @return true if the query evaluates to true according to the given rule
     */
    public boolean evaluateListRule(Query query, PrologRule prologRule) {
        ArrayList<Substitution> substitutions = new ArrayList<>();  //Create a list of substitutions
        Predicate ruleHeadPredicate = prologRule.getHead().getPredicate(0);  //Get the predicate in the head of the rule
        for (int i = 0; i < ruleHeadPredicate.getArity(); i++) {  //Iterate through the predicate
            String predicateAtom = ruleHeadPredicate.getAtom(i);  //Get the atom in the predicate at index i
            String queryAtom = query.getPredicate().getAtom(i);  //Get the atom in the query at index i
            if (isVariable(predicateAtom)) {  //If the atom in the predicate is a variable
                Substitution sub = new Substitution(predicateAtom);  //Create a new substitution for the atom
                sub.addSubstitution(queryAtom);  //Add the query's atom to the substitution
                substitutions.add(sub);  //Add the substitution to the list of substitutions
            } else {  //If the atom in the predicate is not a variable
                if (!predicateAtom.equals(queryAtom)) {  //If the atoms don't match
                    return false;  //return false;
                }
            }
        }
        for (int i = 0; i < ruleHeadPredicate.getLists().size(); i++) {
            LinkedList<String> predicateList = ruleHeadPredicate.getLists().get(i);  //Get the list at index i in the predicate
            LinkedList<String> queryList = query.getPredicate().getLists().get(i);  //Get the list at index i in the query
            if (predicateList.size() <= queryList.size()) {
                for (int j = 0; j < predicateList.size(); j++) {  //Iterate through the predicate's list
                    String predicateListAtom = predicateList.get(j);  //Get the atom in the predicate's list at index i
                    String queryListAtom = queryList.get(j);  //Get the atom in the query's list at index i
                    if (isVariable(predicateListAtom)) {  //If the atom in the predicates's list is a variable
                        Substitution sub = new Substitution(predicateListAtom);  //Create a new substitution for the atom
                        sub.addSubstitution(queryListAtom);  //Add the atom from the query's list to the substitution
                        if (j == predicateList.size() - 1) {  //If we're on the last atom in the predicate's list
                            for (int k = j + 1; k < queryList.size(); k++) {  //Iterate through the rest of the atoms in the query's list
                                String restOfQueryAtom = queryList.get(k);  //Get the string at index k in the query's list
                                sub.addSubstitution(restOfQueryAtom);  //Add the string to the substitution
                            }
                        }
                        substitutions.add(sub);  //Add the substitution to the list of substitutions
                    } else {  //If the atom in the predicate's list is not a variable
                        if (!predicateListAtom.equals(queryListAtom)) {  //If the atoms don't match
                            return false;  //return false
                        }
                    }

                }
            } else {  //If queryList is smaller than predicateList
                for (int j = 0; j < queryList.size(); j++) {
                    String predicateListAtom = predicateList.get(j);  //Get the atom in the predicate's list at index i
                    String queryListAtom = queryList.get(j);  //Get the atom in the query's list at index i
                    if (isVariable(predicateListAtom)) {  //If the atom in the predicates's list is a variable
                        Substitution sub = new Substitution(predicateListAtom);  //Create a new substitution for the atom
                        sub.addSubstitution(queryListAtom);  //Add the atom from the query's list to the substitution
                        substitutions.add(sub);  //Add the substitution to the list of substitutions
                    } else {  //If the atom in the predicate's list is not a variable
                        if (!predicateListAtom.equals(queryListAtom)) {  //If the atoms don't match
                            return false;  //return false
                        }
                    }
                }
            }
        }

        Query bodyQuery = prologRule.getSimpleBody();  //Get the body of the rule
        String functor = bodyQuery.getFunctor();  //Get the name of the query
        Predicate predicate = new Predicate();  //Create a new predicate

        for (int i = 0; i < bodyQuery.getPredicate().getArity(); i++) {  //Iterate through the query
            String bodyAtom = bodyQuery.getPredicate().getAtom(i);  //Get the atom in the query
            if (isVariable(bodyAtom)) {  //If the atom is a variable
                boolean foundVariable = false;  //Determines if the variable is found within the list of substitutions
                for (int j = 0; j < substitutions.size(); j++) {  //Iterate through the list of substitutions
                    Substitution sub = substitutions.get(j);  //Get the substitution at index i
                    String variable = sub.getName();  //Get the name of the variable associated with the substitution
                    if (bodyAtom.equals(variable)) {  //If the variables match
                        foundVariable = true;  //Set foundVariable to true
                        if (sub.getSize() > 1 || j == substitutions.size() - 1) {  //If the substitution holds more than 1 string or this is the last substitution in the list
                            LinkedList<String> list = new LinkedList<>();  //Create a new list
                            for (int k = 0; k < sub.getSize(); k++) {  //Iterate through the substitution
                                String substitute = sub.getSubstitution(k);  //Get the string at index k
                                list.add(substitute);  //Add the string to the list
                            }
                            predicate.getLists().add(list);  //Add the list to the predicate
                        } else {  //If the atom is not a variable
                            String substitute = sub.getSubstitution(0);  //Get the first and only substitution
                            predicate.addAtom(substitute);  //Add the substitution to the predicate
                        }
                    }
                }
                if (!foundVariable) {  //If a matching variable is not found
                    predicate.addAtom(bodyAtom);  //Add the variable to the predicate
                }
            } else {  //If the atom is not a variable
                predicate.addAtom(bodyAtom);  //Add the atom to the predicate
            }
        }
        boolean answer = false;  //Determines if the query evaluates to true or false
        Query testQuery = new Query(functor, predicate);  //Crate a new query with the functor and predicate
        String stringAnswer = evaluateGroundQuery(new PrologHolder(testQuery));  //Evaluate the query and save the answer in a string
        if (stringAnswer.equals("true")) {  //If the query evaluates to true
            answer = true;  //Set answer to true
        }
        return answer;
    }


    /*
     * Evaluates the given ground query
     * @param queryHolder the PrologHolder containing the query to be evaluated
     * @return true if the ground query can be solved from the program
     */
    public String evaluateGroundQuery(PrologHolder queryHolder) {
        Stack<Query> resolvent = new Stack<>();  //Create the resolvent
        if (queryHolder.getType().equals("simple query")) {  //If the query is a simple query
            Query query = queryHolder.getQuery();  //Get the query from the holder
            resolvent.add(query);  //Add the query to the resolvent
        } else if (queryHolder.getType().equals("conjunctive query")) {  //If the query is a conjunctive query
            ConjunctiveQuery queryList = queryHolder.getConjunctiveQuery();  //Get the conjunctive query from the holder
            for (int i = 0; i < queryList.getNumberOfQueries(); i++) {  //Iterate through the queries
                resolvent.add(queryList.getQuery(i));  //Add the query at each index to the resolvent
            }
        }
        while (!resolvent.isEmpty()) {
            boolean factFound = true;
            Query a = resolvent.peek();  //Set A to the top of the stack
            if (containsFact(a.getFunctor())) {  //If the given query is a fact in the program
                PrologHolder factHolder = getFactHolder(a.getFunctor());  //Get the fact from the program and place it into a holder
                Fact foundFact = factHolder.getFact();  //Get the fact from the holder

                boolean found = false;  //Initialize found to false
                outer:
                for (int i = 0; i < foundFact.getNumberOfPredicates(); i++) {  //Iterate through the fact
                    Predicate predicate = foundFact.getPredicate(i);  //Get the predicate at index i in the fact
                    if (predicate.containsList()) {  //If the predicate contains a list
                        if (evaluateListFact(a, predicate)) {  //If the query evaluates to true
                            found = true;  //Set found to true
                            break;  //Break the loop
                        }
                    } else {  //If the predicate doesn't contain a list
                        if (predicate.getArity() == a.getArity()) {  //If the fact and the query share the same arity
                            for (int j = 0; j < predicate.getArity(); j++) {  //Iterate through the predicate
                                String queryAtom = a.getAtom(j);  //Get the atom at the same index in the query as the predicate
                                boolean variable = false;  //Initialize variable to false
                                String factAtom = predicate.getAtom(j);  //get the atom at index i in the predicate
                                if (isVariable(factAtom)) {  //If the atom is a variable
                                    variable = true;  //Set variable equal to true
                                }
                                //If the last atom in the predicate is equal to the atom in the query or the last atom is a variable
                                if ((j == predicate.getArity() - 1 && factAtom.equals(queryAtom)) || (j == predicate.getArity() - 1 && variable)) {
                                    found = true;  //Set found equal to true
                                    break outer;  //Break out of all the loops
                                } else if (!predicate.getAtom(j).equals(queryAtom) && !variable) {  //If the the atom in the predicate isn't the same as the atom in the query
                                    break;  //Search the next predicate
                                }
                            }
                        }
                    }
                }
                if (found) {  //If a matching fact is found
                    resolvent.pop();  //pop the resolvent
                }
                if (!found) {  //If no matching fact is found
                    factFound = false;
                }

            }
            if (containsRule(a.getFunctor()) && !factFound) {  //If the query matches a rule in the program
                boolean found = false;  //initialize found to false
                outer2:
                for (PrologHolder holder : program) {  //Iterate through the program
                    if (holder.getType().equals("rule")) {  //If the holder contains a rule
                        PrologRule prologRule = holder.getPrologRule();  //Get the rule from the holder
                        if (queryMatchesRule(a, prologRule)) {  //If the query matches the rule
                            if (prologRule.getType().equals("simple query")) {  //If the rule only has one body
                                if (prologRule.getHead().getPredicate(0).containsList()) {
                                    if (evaluateListRule(a, prologRule)) {
                                        found = true;
                                        break;
                                    }
                                } else {
                                    ArrayList<Substitution> headSubstitutions = getHeadSubstitutions(prologRule, a);  //Get the heads substitutions if it contains variables
                                    if (prologRule.getSimpleBody().getPredicate().isVariable()) {  //If the body of the rule contains variables
                                        PrologHolder ruleQueryHolder = new PrologHolder(prologRule.getSimpleBody());  //Put the body in a holder
                                        PrologHolder substitutionHolder = evaluateNonGroundQuery(ruleQueryHolder);  //Evaluate the body
                                        if (substitutionHolder.getType().equals("atom")) {  //If the body evaluates to a string (indicating that it failed)
                                            found = false;  //Set found equal to false
                                            continue;   //Exit the loop
                                        }
                                        ArrayList<Substitution> bodySubstitutions = substitutionHolder.getSubstitutions();  //Get the list of substitutions from the holder
                                        boolean allFound = true;  //Indicates that all of the variables we're found
                                        if (!checkBodySubstitutions(bodySubstitutions, a, prologRule.getSimpleBody())) {  //If the strings from the query don't match the substitutions for the body
                                            allFound = false;  //Set all found to false
                                        }
                                        if (!allFound && headSubstitutions.size() > 0) {  //If the strings in the query didn't match with the substitutions, check the head's substitutions
                                            if (checkHeadSubstitutions(headSubstitutions, bodySubstitutions)) {  //If the substitutions for the head match the substitutions for the body
                                                allFound = true;  //Set allFound to true
                                            }
                                        } else {  //If the head doesn't contain any variables and the body query returned valid substitutions
                                            allFound = true;
                                        }

                                        if (allFound) {  //If all found is true
                                            found = true;  //Set found to true
                                        }
                                    } else {  //If the body doesn't contain any variables
                                        PrologHolder ruleQueryHolder = new PrologHolder(prologRule.getSimpleBody());  //Place the body of the rule into a holder
                                        if (evaluateGroundQuery(ruleQueryHolder).equals("true")) {  //Evaluate the body, if it is found within the program
                                            found = true;  //Set found to true
                                            break;  //Break out of the loop
                                        }
                                    }
                                }
                            } else if (prologRule.getType().equals("conjunctive query")) {  //If the rule is made up of conjunctive queries
                                ConjunctiveQuery queries = prologRule.getConjunctiveBody();  //Get the queries from the rule
                                ArrayList<Substitution> headSubstitutions = getHeadSubstitutions(prologRule, a);  //Get the substitutions for the head if it contains variables
                                ConjunctiveQuery nonGroundQueries = new ConjunctiveQuery();  //Create a list for non-ground queries
                                ConjunctiveQuery groundQueries = new ConjunctiveQuery();  //Create a list for ground queries
                                boolean variableBody = false;   //Indicates if the body queries have variables in them
                                if (prologRule.getHead().getPredicate(0).containsList()) {
                                    if (evaluateListRule(a, prologRule)) {   //EVALUATE METHOD
                                        found = true;
                                        break;
                                    }
                                } else {
                                    for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
                                        Query query = queries.getQuery(i);  //Get the query at index i
                                        if (query.getPredicate().isVariable()) {  //If the query contains a variable
                                            variableBody = true;  //Set variableBody to true
                                            nonGroundQueries.addQuery(query);  //Add the query to the list of nonGroundQueries
                                        } else {  //If the query is ground
                                            groundQueries.addQuery(query);  //Add the query to the list of groundQueries
                                        }
                                    }
                                    if (variableBody) {  //If the rule contains variables in its body
                                        if (groundQueries.getNumberOfQueries() > 0) {  //If there are ground queries in the rule
                                            String groundEvaluation = evaluateGroundQuery(new PrologHolder(groundQueries));  //Evaluate the ground queries
                                            if (groundEvaluation.equals("false")) {  //If they evaluate to false
                                                found = false;  //Set found equal to false
                                                continue;  //Check the next holder in the program
                                            }
                                        }
                                        PrologHolder nonGroundSubstitutionHolder = evaluateNonGroundQuery(new PrologHolder(nonGroundQueries));  //Evaluate the nonGroundQueries
                                        if (nonGroundSubstitutionHolder.getType().equals("atom")) {  //If they evaluate to a string
                                            found = false;  //Set found equal to false
                                            continue;  //Check the next holder in the program
                                        }
                                        boolean allFound = true;  //Initialize allFound to true
                                        ArrayList<Substitution> nonGroundSubstitutions = nonGroundSubstitutionHolder.getSubstitutions();  //Get the list of nonGround substitutions
                                        for (int i = 0; i < nonGroundQueries.getNumberOfQueries(); i++) {  //Iterate through the queries
                                            boolean foundQuery = true;  //Initialize foundQuery to true
                                            Query bodyQuery = nonGroundQueries.getQuery(i);  //Get the query at index i
                                            if (!checkBodySubstitutions(nonGroundSubstitutions, a, bodyQuery)) {  //If the strings for the query A don't match the substitutions for the body
                                                foundQuery = false;  //Set foundQuery to false;
                                            }
                                            if (!foundQuery && headSubstitutions.size() > 0) {  //If the strings in the query didn't match with the substitutions, check the head's substitutions
                                                if (checkHeadSubstitutions(headSubstitutions, nonGroundSubstitutions)) {  //If the substitutions for the head match the substitutions for the body
                                                    foundQuery = true;  //Set foundQuery to true
                                                }
                                            } else {  //If the head doesn't contain any variables and the body query returned valid substitutions
                                                foundQuery = true;  //Set foundQuery to true
                                            }
                                            if (!foundQuery) {  //If all queryFound is false
                                                allFound = false;  //Set allFound to false
                                                break;
                                            }
                                        }
                                        if (allFound) {
                                            found = true;
                                        }

                                    } else {  //If the rule doesn't contain any variables
                                        for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
                                            Query subQuery = queries.getQuery(i);  //Get the query at index i
                                            PrologHolder subQueryHolder = new PrologHolder(subQuery);  //Place the query into a holder
                                            if (i == queries.getNumberOfQueries() - 1 && evaluateGroundQuery(subQueryHolder).equals("true")) {  //If the last query evaluates to true
                                                found = true;  //Set found equal to true
                                                break outer2;  //Break out of all the loops
                                            } else if (evaluateGroundQuery(subQueryHolder).equals("false")) {  //If the query evaluates to false
                                                break;  //Search the next holder
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (found) {  //If a matching rule is found
                    resolvent.pop();  //pop the resolvent
                }
                if (!found) {  //If no matching rule is found
                    break;  //Break the while loop
                }
            } else {  //If the query does not match a fact or rule in the program
                break;  //Break the while loop
            }
        }

        String answer = "false";  //Initialize answer to false
        if (resolvent.isEmpty()) {  //If the resolvent is empty
            answer = "true";  //Set answer to true
        }
        return answer;  //Return the answer
    }

    /*
     * Gets the Substitution within the given array list that has a name (and possibly index) that matches the given name and index
     * @param subs a list of substitutions that contain a substitution that matches the given name
     * @param name the name of the substitution in the list
     * @param index the index of the variable associated with the substitution within the list
     * @return a substitution that has the same name (and possible index) as the parameters
     */
    public Substitution getSubstitutionFromList(ArrayList<Substitution> subs, String name, int index) {
        Substitution foundSub = null;  //Initialize the substitution to null
        for (int i = 0; i < subs.size(); i++) {  //Iterate through the list
            Substitution substitution = subs.get(i);  //Get the substitution at index i
            if (substitution.getName().equals(name)) {  //If the substitution's name matches the given name
                int foundIndex = substitution.getIndex();  //Get the index of the substitution
                if (foundIndex == -1) {  //If the index is -1 (indicating that this substitution has not been claimed yet)
                    foundSub = substitution;  //Set foundSub equal to this substitution
                    break;  //Exit the loop
                } else if (foundIndex == index) {  //Else if the index of the substitution matches the given index
                    foundSub = substitution;  //Set foundSub equal to this substitution
                    break;  //Exit the loop
                }
            }
        }
        return foundSub;
    }

    /*
     * Determines if the given list of substitutions contains a substitution that matches the given name
     * @param name the name of the substitution to be found within the list
     * @return true if the list contains a substitution that matches the given name
     */
    public boolean containsSubstitution(ArrayList<Substitution> subs, String name) {
        boolean foundSub = false;  //Initialize foundSub to false
        for (int i = 0; i < subs.size(); i++) {  //Iterate through the list
            Substitution substitution = subs.get(i);  //Get the substitution at index i
            if (substitution.getName().equals(name)) {  //If the name of the substitution matches the given name
                foundSub = true;  //Set foundSub equal to true
            }
        }
        return foundSub;
    }

    /*
     * A helper method that creates a deep copy of the given ArrayList of substitutions
     * @param substitutions the list of substitutions to be copied
     * @return a copy of the given list
     */
    public ArrayList<Substitution> cloneSubstitution(ArrayList<Substitution> substitutions) {
        ArrayList<Substitution> clone = new ArrayList<Substitution>(substitutions.size());  //Create a new list of substitutions
        for (Substitution sub : substitutions) {  //Iterate through the given list
            Substitution clonedSub = new Substitution(sub.getName());  //Create a new substitution that has the same name
            for (int i = 0; i < sub.getSize(); i++) {  //Iterate through the list of strings in the substitution
                String realSub = sub.getSubstitution(i);  //Get the string at index i in the substitution
                clonedSub.addSubstitution(realSub);  //Add the string to the cloned substitution
            }
            clonedSub.setIndex(sub.getIndex());  //Set the index of the cloned substitution to the same index of the original substitution
            clonedSub.setAddCount(sub.getAddCount());  //Set the addCount of the cloned substitution equal to the original substitution
            clonedSub.setQueryID(sub.getQueryID());  //Set the queryID of the cloned substitution equal to the original substitution
            clone.add(clonedSub);  //Add the cloned substitution to the cloned list of substitutions
        }
        return clone;
    }

    /*
     * A helper method that checks the strings within 2 substitutions and removes non-matching strings
     * @param firstSub the first substitution to be compared
     * @param nextSub the next substitution to be compared
     * @param substitutionList the list that contains firstSub and nextSub
     */
    public void checkTwoSubstitutions(Substitution firstSub, Substitution nextSub, ArrayList<Substitution> substitutionList) {
        int firstSubSize = firstSub.getSize();  //Gets the size of the first substitution
        int nextSubSize = nextSub.getSize();  //Gets the size of the second substitution

        for (int m = firstSubSize - 1; m > -1; m--) {  //Iterate through the first substitution in reverse order
            boolean found = false;  //Initialize found to false
            String firstSubString = firstSub.getSubstitution(m);  //Get the string at index m in the first substitution
            for (int n = nextSubSize - 1; n > -1; n--) {  //Iterate through the next substitution in reverse order
                String nextSubString = nextSub.getSubstitution(n);  //Get the string at index n in the next substitution
                if (firstSubString.equals(nextSubString)) {  //If the strings are the same
                    found = true;  //Set found equal to true
                    break;  //Exit the loop
                }
            }
            if (!found) {  //If an equal string is not found
                int queryID = firstSub.getQueryID();  //Get the queryID of the first substitution
                for (Substitution sub : substitutionList) {  //Iterate through the list of substitution
                    if (sub.getQueryID() == queryID) {  //If a substitution has a matching queryID
                        sub.removeSubstitutionAtIndex(m);  //Remove the string at index m from the substitution
                    }
                }
            }
        }
    }

    /*
     * A helper method that checks the substitutions of duplicate variables to ensure that they match
     * @param substitutionList an array list of substitutions
     */
    public void checkDuplicateVariables(ArrayList<Substitution> substitutionList) {
        for (int i = 0; i < substitutionList.size(); i++) {  //Iterate through the list
            Substitution firstSub = substitutionList.get(i);  //Get the substitution at index i
            String firstSubName = firstSub.getName();  //Get the name of the substitution at index i
            for (int j = i + 1; j < substitutionList.size(); j++) {  //Iterate through the rest of the substitutions
                Substitution nextSub = substitutionList.get(j);  //Get the substitution at index j
                String nextSubName = nextSub.getName();  //Get the name of the substitution at index j
                if (firstSubName.equals(nextSubName)) {  //If the two substitutions share the same name (are associated with the same variable)
                    if (firstSub.getSize() == nextSub.getSize() && firstSub.getQueryID() == nextSub.getQueryID()) {  //If both substitutions have the same size
                        int size = firstSub.getSize();  //Get the size of the first substitution
                        for (int k = size - 1; k > -1; k--) {  //Iterate through the substitution (go backwards because we may delete strings as we go)
                            String sub1 = firstSub.getSubstitution(k);  //Get the string at index k in the first substitution
                            String sub2 = nextSub.getSubstitution(k);  //Get the string at index k in the second substitution
                            if (!sub1.equals(sub2)) {  //If the strings are not equal
                                int queryID = firstSub.getQueryID();  //Get the queryID of the first substitution
                                for (Substitution sub : substitutionList) {  //Iterate through the list of substitutions
                                    if (sub.getQueryID() == queryID) {  //If a substitution has a matching queryID
                                        sub.removeSubstitutionAtIndex(k);  //Remove the string at index m from the substitution
                                    }
                                }
                            }
                        }
                    } else {  //If the substitutions do not have the same size
                        int firstSubSize = firstSub.getSize();  //Get the size of the first substitution
                        int nextSubSize = nextSub.getSize();  //Get the size of the next substitution
                        if (firstSubSize > nextSubSize) {  //If the size of the first substitution is larger than the size of the next substitution
                            checkTwoSubstitutions(firstSub, nextSub, substitutionList);  //Call checkTwoSubstitutions to remove any strings in firstSub that aren't in nextSub
                            checkTwoSubstitutions(nextSub, firstSub, substitutionList);  //Call checkTwoSubstitutions again to remove any strings in nextSub that aren't in firstSub
                        } else {  //If the size of the next substitution is larger than the size of the first substitution
                            checkTwoSubstitutions(nextSub, firstSub, substitutionList);  //Call checkTwoSubstitutions to remove any strings in nextSub that aren't in firstSub
                            checkTwoSubstitutions(firstSub, nextSub, substitutionList);  //Call checkTwoSubstitutions again to remove any strings in firstSub that aren't in nextSub
                        }
                    }
                }
            }
        }
    }

    /*
     * A helper method that deletes incorrect substitutions from a list of substitutions
     * @param query a query being checked to see if it can be proven by the program
     * @param substitutionList a list that contains all of the substitutions
     */
    public void deleteWrongSubstitutions(Query query, ArrayList<Substitution> substitutionList) {
        String functor = query.getFunctor();  //Get the name of the query
        ArrayList<Substitution> copySubstitutions = cloneSubstitution(substitutionList);  //Copy the list of substitutions

        while (!copySubstitutions.isEmpty()) {  //While the copied list is not empty
            Predicate predicate = new Predicate();  //Create a new predicate
            for (int i = 0; i < query.getArity(); i++) {  //Iterate through the query
                String queryAtom = query.getAtom(i);  //Get the atom at index i in the query
                if (isVariable(queryAtom)) {  //If the atom is a variable
                    Substitution substitutions = getSubstitutionFromList(copySubstitutions, queryAtom, i);  //Get the (copied) list of substitutions associated with that variable
                    String sub = substitutions.getSubstitution(0);  //Get the 1st substitution in the list
                    predicate.addAtom(sub);  //Add the substitution to the predicate
                    substitutions.removeSubstitution(sub);  //Remove the substitution from the copied list
                } else {  //If the atom is not a variable
                    predicate.addAtom(queryAtom);  //Add it to the predicate
                }
            }
            Query testQuery = new Query(functor, predicate);  //Create a query with the given functor and predicate
            if (evaluateGroundQuery(new PrologHolder(testQuery)).equals("false")) {  //If the test query evaluates to false
                for (int i = 0; i < query.getArity(); i++) {  //Iterate through the query
                    String queryAtom = query.getAtom(i);  //Get the atom at index i in the query
                    if (isVariable(queryAtom)) {  //If the atom is a variable
                        Substitution substitutions = getSubstitutionFromList(substitutionList, queryAtom, i);  //Get the real list of substitutions associated with that variable
                        substitutions.removeSubstitution(testQuery.getAtom(i));  //Remove the substitution from the real list
                    }
                }
            }
            int copyCount = copySubstitutions.size();  //Get the size of the copied list
            for (int i = copyCount - 1; i > -1; i--) {  //Iterate through the copied list in reverse
                Substitution sub = copySubstitutions.get(i);  //Get the substitution at index i
                if (sub.getSize() == 0) {  //If the substitution is empty
                    copySubstitutions.remove(sub);  //Remove the substitution from the copied list
                }
            }
        }
    }

    /*
     * A helper method that checks the substitutions of a variable by testing them on a different query
     * @param query a query from a conjunctive query that is being tested with different substitutions
     * @param substitutionList a list that contains the substitutions for query
     * @param allSubstitutions a list that contains all of the substitutions for all of the conjunctive queries
     * @param queryID the id of the query these substitutions come from
     * @param message a string indicating if the substitutions in substitutionList are from the same query
     */
    public void deleteMoreWrongSubstitutions(Query query, ArrayList<Substitution> substitutionList, ArrayList<Substitution> allSubstitutions, int queryID, String message) {
        String functor = query.getFunctor();  //Get the name of the query
        ArrayList<Substitution> copySubstitutions = cloneSubstitution(substitutionList);  //Copy the list of substitutions

        int indexCounter = -1;  //Initialize indexCounter to negative 1
        while (!copySubstitutions.isEmpty()) {  //While the copied list is not empty
            Predicate predicate = new Predicate();  //Create a new predicate
            indexCounter++;  //Increment indexCounter
            for (int i = 0; i < query.getArity(); i++) {  //Iterate through the query
                String queryAtom = query.getAtom(i);  //Get the atom at index i in the query
                if (isVariable(queryAtom)) {  //If the atom is a variable
                    //Get the list of substitutions associated with that variable from the copied list
                    Substitution substitutions = getSubstitutionFromList(copySubstitutions, queryAtom, copySubstitutions.get(i).getIndex());  //substitutionList.get(i).getIndex()
                    String sub = substitutions.getSubstitution(0);  //Get the 1st substitution in the list
                    predicate.addAtom(sub);  //Add the substitution to the predicate
                    substitutions.removeSubstitution(sub);  //Remove the substitution from the copied list
                } else {  //If the atom is not a variable
                    predicate.addAtom(queryAtom);  //Add it to the predicate
                }
            }
            Query testQuery = new Query(functor, predicate);  //Create a query with the given functor and predicate
            if (evaluateGroundQuery(new PrologHolder(testQuery)).equals("false") && message.equals("same queryIDs")) {  //If the test query evaluates to false and the substitutions are from the same query
                for (Substitution sub : allSubstitutions) {  //Iterate through the entire list of substitutions
                    if (sub.getQueryID() == queryID) {  //If the ID of a substitution matches the queryID
                        sub.removeSubstitutionAtIndex(indexCounter);  //Remove the substitution at that index
                    }
                }
            } else if (evaluateGroundQuery(new PrologHolder(testQuery)).equals("false") && message.equals("different queryIDs")) {  //If the test query evaluates to false and the substitutions are from the different query
                for (Substitution sub : substitutionList) {  //Iterate through the list of substitutions
                    sub.removeSubstitutionAtIndex(indexCounter);  //Remove the substitution at that index
                }
                indexCounter = indexCounter - 1;  //Decrement the indexCounter because we removed an item from the list
            }
            int copyCount = copySubstitutions.size();  //Get the size of the copied list
            for (int i = copyCount - 1; i > -1; i--) {  //Iterate through the copied list in reverse
                Substitution sub = copySubstitutions.get(i);  //Get the substitution at index i
                if (sub.getSize() == 0) {  //If the substitution is empty
                    copySubstitutions.remove(sub);  //Remove the substitution from the copied list
                }
            }
        }
    }

    /*
     * A helper method that determines of two queries contain at least 1 identical variable
     * @param firstQuery one of the queries being checked
     * @param nextQuery the other query being checked
     * @return true if at least 1 identical variable exists in the given queries
     */
    public boolean containSameVariable(Query firstQuery, Query nextQuery) {
        boolean sameVariable = false;  //Initialize sameVariable to false
        ArrayList<Substitution> firstQuerySubstitutions = firstQuery.getSubstitutions();  //Get the list of substitutions from the first query
        ArrayList<Substitution> nextQuerySubstitutions = nextQuery.getSubstitutions();  //Get the list of substitutions from the next query
        for (Substitution firstSub : firstQuerySubstitutions) {  //Iterate through the first list of substitutions
            String firstSubVariable = firstSub.getName();  //Get the name of the variable associated with a substitution
            for (Substitution nextSub : nextQuerySubstitutions) {  //Iterate through the next list of substitutions
                String nextSubVariable = nextSub.getName();  //Get the name of the variable associated with a substitution
                if (firstSubVariable.equals(nextSubVariable)) {  //If the names of the variables are the same
                    sameVariable = true;  //Set sameVariable equal to true
                    break;  //Exit the loop
                }
            }
        }
        return sameVariable;
    }

    /*
     * Makes a deep copy of the given substitution, but leaves it's list of strings empty
     * @param substitution the substitution to be cloned
     * @return a copy of the given substitution with an empty list of strings
     */
    public Substitution makeEmptySubstitutionCopy(Substitution substitution) {
        Substitution newSubstitution = new Substitution(substitution.getName());
        newSubstitution.setIndex(substitution.getIndex());  //Set the index of the cloned substitution to the same index of the original substitution
        newSubstitution.setAddCount(substitution.getAddCount());  //Set the addCount of the cloned substitution equal to the original substitution
        newSubstitution.setQueryID(substitution.getQueryID());  //Set the queryID of the cloned substitution equal to the original substitution
        return newSubstitution;
    }

    /*
     * Given 2 queries of different variable substitution sizes, this method makes every substitution the same size
     * @param substitutions the list of substitutions that contain the substitutions associated with these queries
     * @param equalizedSubstitutions an empty list of substitutions that gets filled with substitutions of the same size
     * @param largerQuery the query with more variables
     * @param smallerQuery the query with less variables (defines the scope)
     */
    public void equalizeSubstitutions(ArrayList<Substitution> substitutions, ArrayList<Substitution> equalizedSubstitutions, Query largerQuery, Query smallerQuery) {
        int smallerQueryID = smallerQuery.getId();  //Get the id of the smaller query
        int smallerQuerySize = smallerQuery.getSubstitutions().get(0).getSize();  //Get the size of the smaller query
        int largerQuerySize = largerQuery.getSubstitutions().get(0).getSize();  //Get the size of the larger query

        int totalSize = largerQuerySize * smallerQuerySize;  //Get the total size each substitution should be
        for (Substitution sub : substitutions) {  //Iterate through the list of substitutions
            if (sub.getSize() != totalSize && sub.getQueryID() == smallerQueryID) {  //If the size of a substitution isn't equal to the total size and it's from the smaller query
                Substitution smallerSub = makeEmptySubstitutionCopy(sub);  //Make a copy of the smaller substitution with an empty list of strings
                for (int j = 0; j < largerQuerySize; j++) {  //Iterate until the size of the larger copy (Repeat the substitutions of the smaller query as many times as the larger query)
                    for (int i = 0; i < sub.getSize(); i++) {  //Iterate through the substitution
                        String substitute = sub.getSubstitution(i);  //Get the substitute at index i
                        smallerSub.addSubstitution(substitute);  //Add the substitute to the substitution
                    }
                }
                equalizedSubstitutions.add(smallerSub);  //Add the substitution to the list
            } else if (sub.getSize() != totalSize && sub.getQueryID() != smallerQueryID) {  //If the size of a substitution isn't equal to the total size and it's from the larger query
                Substitution largerSub = makeEmptySubstitutionCopy(sub);  //Make a copy of the larger substitution with an empty list of strings
                for (int i = 0; i < sub.getSize(); i++) {  //Iterate through the substitution
                    String substitute = sub.getSubstitution(i);  //Get the substitute at index i
                    for (int j = 0; j < smallerQuerySize; j++) {  //Iterate until the size of the smaller copy (Repeat each substitution of the larger query as many times as the smaller query)
                        largerSub.addSubstitution(substitute);  //Add the substitute to the substitution
                    }
                }
                equalizedSubstitutions.add(largerSub);   //Add the substitution to the list
            }
        }
        deleteMoreWrongSubstitutions(smallerQuery, equalizedSubstitutions, substitutions, largerQuery.getId(), "different queryIDs");  //Call this method to test all of the substitutions

        for (Substitution sub : substitutions) {  //Iterate through the original list of substitutions
            String subVariable = sub.getName();  //Get the variable associated with a substitution
            for (int i = 0; i < sub.getSize(); i++) {  //Iterate through the substitution
                String subSubstitute = sub.getSubstitution(i);  //Get the string at index i
                boolean found = false;  //Initialize found to false
                for (Substitution equalizedSub : equalizedSubstitutions) {  //Iterate through the list of equalized substitutions
                    String equalizedSubVariable = equalizedSub.getName();  //Get the variable associated with a substitution
                    if (subVariable.equals(equalizedSubVariable)) {  //If the variables are the same
                        for (int j = 0; j < equalizedSub.getSize(); j++) {  //Iterate through the equalized substitution
                            String equalizedSubstitute = equalizedSub.getSubstitution(j);  //Get the string at index i
                            if (subSubstitute.equals(equalizedSubstitute)) {  //If the strings match
                                found = true;  //Set found equal to true
                                break;  //Exit the loop
                            }
                        }
                    }
                }
                if (!found) {  //If a matching string is not found
                    sub.removeSubstitutionAtIndex(i);  //Remove the string at index i from the original substitution
                }
            }
        }
    }

    /*
     * A helper method that swaps the substitutions for the smallerQuery with the substitutions for the larger query (if the queries have the same vairables)
     * @param largerQuery the query with more variables
     * @param smallerQuery the query with less variables
     * @return PrologHolder holding a list of substitutions that contain substitutions from the larger query and the boolean allFound
     */
    public PrologHolder getSameSubstitutions(Query largerQuery, Query smallerQuery) {
        ArrayList<Substitution> sameSubstitutions = new ArrayList<>();  //Create a new array List
        ArrayList<Substitution> equalizedSubstitutions = new ArrayList<>();  //Create a new array list
        boolean allFound = true;  //Indicates of every substitution in the smaller query appears in the larger query
        for (Substitution smallerSub : smallerQuery.getSubstitutions()) {  //Iterate through the smaller query's substitutions
            String smallerVariable = smallerSub.getName();  //Get the name of the variable associated with a substitution
            boolean found = false;  //Initialize found to false
            for (Substitution largerSub : largerQuery.getSubstitutions()) {  //Iterate through the larger query's substitutions
                String largerVariable = largerSub.getName();  //Get the name of the variable associated with a substitution
                if (smallerVariable.equals(largerVariable)) {  //If the names of the variables are the same
                    sameSubstitutions.add(largerSub);  //Add the substitution from the larger query to the list
                    found = true;  //Set found equal to true
                    break;  //Exit the loop
                }
            }
            if (!found) {  //If a matching variable is not found
                sameSubstitutions.add(smallerSub);  //Add the substitution from the smaller query
                allFound = false;  //Set all found to false
            }
        }
        if (!allFound) {  //If a substitution from the smaller query is not found in the larger query
            equalizeSubstitutions(sameSubstitutions, equalizedSubstitutions, largerQuery, smallerQuery);  //Call this method to make all of the lists of substitutions the same size and check for invalid substitutions
        }
        return new PrologHolder(sameSubstitutions, allFound);  //Return a prologHolder containing the boolean and the list of substitutions
    }

    /*
     * Sets the list of substitutions in firstSub equal to the list in testSub
     * @param firstSub the list of substitutions being modified
     * @param nextSub the substitution with the correct substitutes
     */
    public void modifySubstitutionList(Substitution firstSub, Substitution testSub) {
        for (int i = 0; i < firstSub.getSize(); i++) {  //Iterate through the first substitution
            String newSub = testSub.getSubstitution(i);  //Get the string at index i in testSub
            firstSub.setSubstitution(i, newSub);  //Set the string at index i in firstSub to newSub
        }
    }

    /*
     * A helper method that checks the larger query to ensure that it fits into the scope defined by the smaller query
     * @param firstQuery the larger query with more variables
     * @param nextQuery the smaller query with less variables
     * @param substitutionList a list of all of the substitutions for the conjunctive query
     */
    public void checkLargerSubstitutions(Query firstQuery, Query nextQuery, ArrayList<Substitution> substitutionList) {
        PrologHolder substitutionHolder = getSameSubstitutions(firstQuery, nextQuery);
        ArrayList<Substitution> testSubs = substitutionHolder.getSubstitutions(); //Swap the substitutions for the larger query into the smaller query (if they share variables)

        if (substitutionHolder.isAllFound()) {
            deleteMoreWrongSubstitutions(nextQuery, testSubs, substitutionList, firstQuery.getId(), "same queryIDs");  //Check the swapped substitutions
            for (Substitution firstSub : firstQuery.getSubstitutions()) {  //Iterate through the first substitution
                String firstVariable = firstSub.getName();  //Get the name of the variable associate with the first substitution
                for (Substitution testSub : testSubs) {  //Iterate through the test substitutions
                    String testVariable = testSub.getName();  //Get the name of the variable associate with the test substitution
                    if (firstVariable.equals(testVariable)) {  //If the names of the variables match
                        modifySubstitutionList(firstSub, testSub);  //Modify firstSub to equal testSub
                    }
                }
            }
        }
    }

    /*
     * Checks the substitutions of all of the queries to ensure that they're valid
     * @param queries a conjunctive query that holds multiple queries
     * @param substitutionList a list of all of the substitutions for the conjunctive query
     */
    public void checkAllQueries(ConjunctiveQuery queries, ArrayList<Substitution> substitutionList) {
        for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
            Query firstQuery = queries.getQuery(i);  //Get the query at index i
            int firstQueryNumberOfVariables = firstQuery.getNumberOfVariables();  //Get the number of variables in the first query
            for (int j = i + 1; j < queries.getNumberOfQueries(); j++) {  //Iterate through the rest of the queries
                Query nextQuery = queries.getQuery(j);  //Get the next query in the list
                int nextQueryNumberOfVariables = nextQuery.getNumberOfVariables();  //Get the number of variables in the next query
                if (containSameVariable(firstQuery, nextQuery)) {  //If the queries share variables
                    if (firstQueryNumberOfVariables > nextQueryNumberOfVariables) {  //If the first query has more variables
                        checkLargerSubstitutions(firstQuery, nextQuery, substitutionList);  //Check the substitutions of the larger query by testing them in the smaller query
                    } else if ((firstQueryNumberOfVariables < nextQueryNumberOfVariables)) {  //If the next query has more variables
                        checkLargerSubstitutions(nextQuery, firstQuery, substitutionList);//Check the substitutions of the larger query by testing them in the smaller query
                    } else {  //If both queries have the same size
                        checkLargerSubstitutions(firstQuery, nextQuery, substitutionList);  //Check the substitutions for the 1st query by testing them in the next query
                        checkLargerSubstitutions(nextQuery, firstQuery, substitutionList);  //Check the substitutions for the next query by testing them in the 1st query
                    }
                }
            }
        }
    }

    /*
     * Removes invalid substitutions from the given prologHolder that either contains a query or a list of queries
     * @param queryHolder the holder containing the query/queries
     */
    public boolean removeInvalidSubstitutions(PrologHolder queryHolder) {
        if (queryHolder.getType().equals("simple query")) {  //If the holder contains a simple query
            Query query = queryHolder.getQuery();  //Get the query from the holder
            ArrayList<Substitution> substitutionList = query.getSubstitutions();  //Get the list of substitutions from the query
            checkDuplicateVariables(substitutionList);  //Call this method to remove any invalid substitutions for duplicate variables
            for (Substitution sub : substitutionList) {  //Iterate through the list of substitutions
                if (sub.getSize() == 0) {  //If a substitution is empty
                    return false;  //Return false
                }
            }
            deleteWrongSubstitutions(query, substitutionList);  //Call this method to test each substitution and remove invalid ones
            /*  //MAY NEED TO DO THIS AGAIN
            for (Substitution sub : substitutionList) {
                if (sub.getSize() == 0) {
                    validSubstitutions = false;
                }
            }
             */

        } else if (queryHolder.getType().equals("conjunctive query")) {  //If the prologHolder contains a conjunctive query
            ConjunctiveQuery queries = queryHolder.getConjunctiveQuery();  //Get the conjunctive query from the holder
            ArrayList<Substitution> substitutionList = new ArrayList<>();  //Create a list of substitutions to hold all of the substitutions from all of the queries
            for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
                Query subQuery = queries.getQuery(i);  //Get the query at index i
                for (Substitution sub : subQuery.getSubstitutions()) {  //Iterate through the query's substitutions
                    substitutionList.add(sub);  //Add the substitution to the list
                }
            }
            checkDuplicateVariables(substitutionList);  //Call this method to remove any invalid substitutions for duplicate variables
            for (Substitution sub : substitutionList) {  //Iterate through the list of substitutions
                if (sub.getSize() == 0) {  //If a substitution is empty
                    return false;  //Return false
                }
            }

            for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
                Query query = queries.getQuery(i);  //Get the query at index i
                ArrayList<Substitution> thisSubstitutionList = query.getSubstitutions();  //Get the query's list of substitutions
                deleteWrongSubstitutions(query, thisSubstitutionList);  //Call this method to test each substitution and remove invalid ones
            }
            checkAllQueries(queries, substitutionList);
        }
        return true;
    }

    /*
     * Tests the given substitutions by swapping them with the previous substitutions in the given query
     * @param bodyQuery the query being tested with new substitutions
     * @param newSubstitutions the new Substitutions for the query
     * @param indices a list that holds the index of the swapped variable in the given query
     * @return a PrologHolder containing a list of substitutions
     */
    public PrologHolder testSubstitutions(Query bodyQuery, ArrayList<Substitution> newSubstitutions, ArrayList<Integer> indices) {
        String functor = bodyQuery.getFunctor();  //Get the name of the body query
        Predicate predicate = new Predicate();  //Create a new predicate
        for (int i = 0; i < bodyQuery.getArity(); i++) {  //Iterate through the body query
            String bodyAtom = bodyQuery.getAtom(i);  //Get the atom at index i in the query
            if (isVariable(bodyAtom)) {  //If the atom is a variable
                boolean foundVariable = false;
                for (Substitution headSub : newSubstitutions) {  //Iterate through the substitutions for the head
                    String variable = headSub.getName();  //Get the variable associated with a substitution
                    if (bodyAtom.equals(variable)) {  //If the variables are the same
                        predicate.addAtom(headSub.getSubstitution(0));  //Add the substitution to the predicate
                        foundVariable = true;
                        indices.add(i);  //Add the index of the variable to the list
                    }
                }
                if (!foundVariable) {
                    predicate.addAtom(bodyAtom);
                }
            } else {  //If the atom is not a variable
                predicate.addAtom(bodyAtom);  //Add the atom to the predicate
            }
        }
        Query testQuery = new Query(functor, predicate);  //Create a new query with the functor and predicate
        return evaluateNonGroundQuery(new PrologHolder(testQuery));
    }

    /*
     * Fills the substitutions for the query with the given list of substitutions
     * @param query the query whose substitutions are being modified
     * @param substitutions the substitutions being added to the query
     * @param indices a list the holds the indices of the substitutions
     */
    public void fillSubstitutions(Query a, ArrayList<Substitution> substitutions, ArrayList<Integer> indices) {
        int indexCounter = -1;  //Counts the index of the variables
        for (int i = 0; i < a.getArity(); i++) {  //Iterate through the query
            String atom = a.getAtom(i);  //Get the atom at index i
            if (isVariable(atom)) {  //If the atom is a variable
                indexCounter++;  //Increment indexCounter
                int correctIndex = indices.get(indexCounter);  //Get the correct index form the list of indices
                Substitution foundSub = substitutions.get(correctIndex);  //Get the substitution at the correct index
                for (int j = 0; j < foundSub.getSize(); j++) {  //Iterate through the substitution
                    String bodySubstitute = foundSub.getSubstitution(j);  //Get the string at indx j
                    Substitution querySub = a.getSubstitutions().get(indexCounter);  //Get the substitution in the query
                    querySub.addSubstitution(bodySubstitute);  //Add the string to the substitution
                }

            }
        }
    }

    /*
     * Evaluates the given non ground query
     * @return false if the query cannot be solved from the program. Otherwise a holder that contains a list of substitutions for the variables within the query
     */
    public PrologHolder evaluateNonGroundQuery(PrologHolder queryHolder) {
        boolean foundRule = false;  //Determines if a valid rule is found for the query
        boolean stringAns = true;  //Determines if the answer to the query is the string "true"
        Stack<Query> resolvent = new Stack<>();  //Create a stack to hold the queries
        if (queryHolder.getType().equals("simple query")) {  //If the query is a simple query
            Query query = queryHolder.getQuery();  //Get the query from the holder
            resolvent.add(query);  //Add the query to the resolvent
            for (int i = 0; i < query.getArity(); i++) {  //Iterate through the query
                if (isVariable(query.getAtom(i))) {  //If the atom at index i is a variable
                    Substitution sub = new Substitution(query.getAtom(i));  //Create a substitution whose name is the variable
                    query.getSubstitutions().add(sub);  //Add the substitution to the query
                }
            }
        } else if (queryHolder.getType().equals("conjunctive query")) {  //If the query is a conjunctive query
            ConjunctiveQuery queryList = queryHolder.getConjunctiveQuery();  //Get the conjunctive query from the holder
            for (int i = 0; i < queryList.getNumberOfQueries(); i++) {  //Iterate through the queries
                Query query = queryList.getQuery(i);  //Get the query from the list of queries
                query.setId(i);  //Set the id of the query to its index i
                resolvent.add(query);  //Add the query to the resolvent

                for (int j = 0; j < query.getArity(); j++) {  //Iterate through the query
                    if (isVariable(query.getAtom(j))) {  //If the atom at index j is a variable
                        Substitution sub = new Substitution(query.getAtom(j));  //create a substitution whose name is the variable
                        sub.setQueryID(query.getId());  //Set the substitution's queryID
                        query.getSubstitutions().add(sub);  //Add the substitution to the query
                    }
                }
            }
        }
        while (!resolvent.isEmpty()) {
            Query a = resolvent.peek();  //Set a equal to the query at the top of the stack
            if (containsFact(a.getFunctor())) {  //If a fact exists in the program that has the same functor as the query
                PrologHolder factHolder = getFactHolder(a.getFunctor());  //Get the fact from the program and place it into a holder
                Fact foundFact = factHolder.getFact();  //Get the fact from the holder

                boolean found = false;  //Initialize found to false
                for (int i = 0; i < foundFact.getNumberOfPredicates(); i++) {
                    Predicate predicate = foundFact.getPredicate(i);  //Get the predicate at index i in the fact
                    for (Substitution substitution : a.getSubstitutions()) {  //Iterate through the list of substitutions
                        substitution.clearAddCount();  //Clear the addCount for each substitution (indicating that nothing has been added to the substitution's list yet)
                    }
                    if (predicate.getArity() == a.getArity()) {  //If the fact and the query share the same arity
                        for (int j = 0; j < predicate.getArity(); j++) {  //Iterate through the predicate
                            String queryAtom = a.getAtom(j);  //Get the atom at the same index in the query as the predicate
                            if (isVariable(queryAtom)) {  //If the atom in the query is a variable
                                Substitution sub = getSubstitutionFromList(a.getSubstitutions(), queryAtom, j);  //Get the Substitution from the list
                                if (sub.getIndex() == -1) {  //If the index of the substitution is -1 (indicating that this substitution hasn't been claimed yet)
                                    sub.setIndex(j);  //Set the index of the substitution to match the index of the variable
                                }

                                String factAtom = predicate.getAtom(j);  //Get the atom in the predicate at index j
                                sub.addSubstitution(factAtom);  //Add the atom to the list of substitutions
                                sub.incrementAddCount();  //Increment the addCount of the substitution
                                if (j == predicate.getArity() - 1) {  //If we're at the last atom in the predicate
                                    found = true;  //Set found equal to true;
                                    break;  //Search the next predicate
                                }
                            } else {  //Else if the atom in the query is not a variable
                                boolean variable = false;  //Initialize variable to false
                                String factAtom = predicate.getAtom(j);  //get the atom at index i in the predicate
                                if (isVariable(factAtom)) {  //If the atom is a variable
                                    variable = true;  //Set variable equal to true
                                }
                                //If the last atom in the predicate is equal to the atom in the query or the last atom is a variable
                                if ((j == predicate.getArity() - 1 && factAtom.equals(queryAtom)) || (j == predicate.getArity() - 1 && variable)) {
                                    found = true;  //Set found equal to true
                                    break;  //Break out of all the loops
                                } else if (!predicate.getAtom(j).equals(queryAtom) && !variable) {  //If the the atom in the predicate isn't the same as the atom in the query
                                    for (Substitution substitution : a.getSubstitutions()) {  //Iterate through the list of substitutions
                                        if (substitution.getAddCount() > 0) {  //If the substitution has been added to
                                            substitution.removeRecent(substitution.getAddCount());  //Delete the incorrect substitutions
                                        }
                                    }
                                    break;  //Search the next predicate
                                }
                            }
                        }
                    }
                }
                if (found) {  //If a matching fact is found
                    resolvent.pop();  //pop the resolvent
                    stringAns = false;
                }
                if (!found) {  //If no matching fact is found
                    break;  //Break the while loop
                }

            } else if (containsRule(a.getFunctor())) {
                boolean found = false;  //initialize found to false
                for (PrologHolder holder : program) {
                    if (holder.getType().equals("rule")) {  //If the holder contains a rule
                        PrologRule prologRule = holder.getPrologRule();  //Get the rule from the holder
                        if (queryMatchesRule(a, prologRule)) {  //If the query matches the rule
                            if (prologRule.getType().equals("simple query")) {  //If the rule only has one body
                                ArrayList<Substitution> headSubstitutions = getHeadSubstitutions(prologRule, a);  //Get the heads substitutions if it contains variables
                                if (prologRule.getSimpleBody().getPredicate().isVariable()) {  //If the body contains a variable
                                    PrologHolder ruleQueryHolder = new PrologHolder(prologRule.getSimpleBody());  //Put the body in a holder
                                    PrologHolder substitutionHolder;
                                    ArrayList<Substitution> bodySubstitutions;
                                    if (prologRule.getSimpleBody().getSubstitutions().size() == 0) {  //If the body doesn't have any substitutions
                                        substitutionHolder = evaluateNonGroundQuery(ruleQueryHolder);  //Evaluate the body
                                        if (substitutionHolder.getType().equals("atom")) {  //If the body evaluates to a string (indicating that it failed)
                                            found = false;  //Set found equal to false
                                            continue;   //Exit the loop
                                        }
                                        bodySubstitutions = substitutionHolder.getSubstitutions();  //Get the list of substitutions from the holder
                                    } else {  //If the body already contains substitutions
                                        bodySubstitutions = prologRule.getSimpleBody().getSubstitutions();  //Set bodySubstitutions to the body's substitutions
                                    }
                                    ArrayList<Substitution> correctSubstitutions;
                                    boolean allFound = true;  //Indicates that all of the variables were found

                                    boolean shareVariable = false;  //Indicates that a variable in the head of the rule is also within the body of the rule
                                    for (Substitution headSub : headSubstitutions) {  //Iterate through the head's substitutions
                                        String headVariable = headSub.getName();  //Get the variable associated with the substitution
                                        for (Substitution bodySub : bodySubstitutions) {  //Iterate through the body's substitutions
                                            String bodyVariable = bodySub.getName();  //Get the variable associated with the substitution
                                            if (headVariable.equals(bodyVariable)) {  //If the variables match
                                                shareVariable = true;  //Set shareVariable to true
                                                break;  //Exit the loop
                                            }
                                        }
                                    }

                                    if (shareVariable) {  //If a variable in the head of the rule is also within the body of the rule
                                        if (!checkBodySubstitutions(bodySubstitutions, a, prologRule.getSimpleBody())) {  //If the strings from the query don't match the substitutions for the body
                                            allFound = false;  //Set all found to false
                                        } else {
                                            ArrayList<Substitution> substitutions;  //Create an array of substitutions
                                            Query bodyQuery = prologRule.getSimpleBody();  //Get the body query of the rule
                                            ArrayList<Integer> indices = new ArrayList<>();  //Create a list that will hold the indices of the substitutions
                                            PrologHolder tests = testSubstitutions(bodyQuery, headSubstitutions, indices);  //Test the headSubstitutions by swapping them into the query
                                            if (tests.getType().equals("substitutions")) {  //If the test returns valid substitutions
                                                substitutions = tests.getSubstitutions();  //Set substitutions equal to the tests
                                            } else {  //If the test doesn't return a valid substitution
                                                substitutions = bodySubstitutions;  //Set substitutions equal to body substitutions
                                            }
                                            fillSubstitutions(a, substitutions, indices);  //Fill the substitutions for query a with the given substitutions
                                        }
                                        if (!allFound && headSubstitutions.size() > 0) {  //If the strings in the query didn't match with the substitutions, check the head's substitutions
                                            if (checkHeadSubstitutions(headSubstitutions, bodySubstitutions)) {  //If the substitutions for the head match the substitutions for the body
                                                Query bodyQuery = prologRule.getSimpleBody();  //Get the body query
                                                ArrayList<Integer> indices = new ArrayList<>();  //Create a list that will hold the indices of the substitutions
                                                PrologHolder testHolder = testSubstitutions(bodyQuery, headSubstitutions, indices);
                                                correctSubstitutions = testHolder.getSubstitutions();  //Test the head substitutions and return the valid substitutions
                                                fillSubstitutions(a, correctSubstitutions, indices);  //Fill the substitutions for query a with the given substitutions
                                                allFound = true;  //Set allFound to true
                                            }
                                        }
                                        if (allFound) { //&& case1) {  //If a matching rule is found in case 1
                                            found = true;  //Set found to true
                                            foundRule = true;  //Set found rule true
                                            stringAns = false;  //Set stringAns to false
                                        }

                                    } else {  //If the head doesn't share any variables with the body
                                        found = true;  //Set found to true because the body evaluates
                                    }
                                } else {  //If the body doesn't contain any variables
                                    if (evaluateGroundQuery(new PrologHolder(prologRule.getSimpleBody())).equals("true")) {  //Evaluate the body and if it returns true
                                        found = true;  //Set found equal to true
                                    }
                                }

                            } else if (prologRule.getType().equals("conjunctive query")) {
                                ConjunctiveQuery queries = prologRule.getConjunctiveBody();  //Get the queries from the rule
                                ArrayList<Substitution> headSubstitutions = getHeadSubstitutions(prologRule, a);  //Get the substitutions for the head if it contains variables
                                ConjunctiveQuery nonGroundQueries = new ConjunctiveQuery();  //Create a list for non-ground queries
                                ConjunctiveQuery groundQueries = new ConjunctiveQuery();  //Create a list for ground queries
                                boolean variableBody = false;   //Indicates if the body queries have variables in them
                                for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
                                    Query query = queries.getQuery(i);  //Get the query at index i
                                    if (query.getPredicate().isVariable()) {  //If the query contains a variable
                                        variableBody = true;  //Set variableBody to true
                                        nonGroundQueries.addQuery(query);  //Add the query to the list of nonGroundQueries
                                    } else {  //If the query is ground
                                        groundQueries.addQuery(query);  //Add the query to the list of groundQueries
                                    }
                                }
                                if (variableBody) {  //If the rule contains variables in its body
                                    if (groundQueries.getNumberOfQueries() > 0) {  //If there are ground queries in the rule
                                        String groundEvaluation = evaluateGroundQuery(new PrologHolder(groundQueries));  //Evaluate the ground queries
                                        if (groundEvaluation.equals("false")) {  //If they evaluate to false
                                            found = false;  //Set found equal to false
                                            continue;  //Check the next holder in the program
                                        }
                                    }
                                    boolean emptySub = false;  //Determines if a query contains substitutions
                                    for (int i = 0; i < nonGroundQueries.getNumberOfQueries(); i++) {  //Iterate through the non-ground queries
                                        Query query = nonGroundQueries.getQuery(i);  //Get the query at index i
                                        if (query.getSubstitutions().size() == 0) {  //If the query doesn't contain any substitutions
                                            emptySub = true;  //Set emptySub to true
                                            break;  //Exit the loop
                                        }
                                    }
                                    PrologHolder nonGroundSubstitutionHolder;
                                    ArrayList<Substitution> nonGroundSubstitutions;
                                    if (emptySub) {
                                        nonGroundSubstitutionHolder = evaluateNonGroundQuery(new PrologHolder(nonGroundQueries));  //Evaluate the nonGroundQueries
                                        if (nonGroundSubstitutionHolder.getType().equals("atom")) {  //If they evaluate to a string
                                            found = false;  //Set found equal to false
                                            continue;  //Check the next holder in the program
                                        }
                                        nonGroundSubstitutions = nonGroundSubstitutionHolder.getSubstitutions();  //Get the list of nonGround substitutions
                                    } else {
                                        nonGroundSubstitutions = new ArrayList<>();
                                        for (int i = 0; i < nonGroundQueries.getNumberOfQueries(); i++) {
                                            ArrayList<Substitution> nonGroundSubs = nonGroundQueries.getQuery(i).getSubstitutions();
                                            for (Substitution sub : nonGroundSubs) {
                                                if (!containsSubstitution(nonGroundSubstitutions, sub.getName())) {  //If the substitution is not in the list of correct substitutions
                                                    nonGroundSubstitutions.add(sub);  //Add the substitution to the list
                                                }
                                            }
                                        }
                                    }

                                    boolean shareVariable = false;  //Indicates that a variable in the head of the rule is also within the body of the rule
                                    for (Substitution headSub : headSubstitutions) {  //Iterate through the head's substitutions
                                        String headVariable = headSub.getName();  //Get the variable associated with the substitution
                                        for (Substitution nonGroundSub : nonGroundSubstitutions) {  //Iterate through the body's substitutions
                                            String bodyVariable = nonGroundSub.getName();  //Get the variable associated with the substitution
                                            if (headVariable.equals(bodyVariable)) {  //If the variables match
                                                shareVariable = true;  //Set shareVariable to true
                                                break;  //Exit the loop
                                            }
                                        }
                                    }

                                    if (shareVariable) {  //If the rule shares variables between the head and the body
                                        ArrayList<Substitution> correctSubstitutions = new ArrayList<>();  //Create a new list of substitutions
                                        for (Substitution headSub : headSubstitutions) {  //Iterate through the head substitutions
                                            String headVariable = headSub.getName();  //Get the variable associated with the substitution
                                            String subVariable = headSub.getSubstitution(0);  //Get the string associated with the variable
                                            Substitution correctSub = new Substitution(subVariable);  //Create a substitution for the string

                                            for (Substitution nonGroundSub : nonGroundSubstitutions) {  //Iterate through the non-ground substitutions
                                                String nonGroundVariable = nonGroundSub.getName();  //Get the variable associated with the substitution
                                                if (headVariable.equals(nonGroundVariable)) {  //If the variables match
                                                    for (int i = 0; i < nonGroundSub.getSize(); i++) {  //Iterate through the non-ground substitution
                                                        String sub = nonGroundSub.getSubstitution(i);  //Get the string at index i
                                                        correctSub.addSubstitution(sub);  //Add it to the list of correct substitution
                                                    }
                                                    correctSubstitutions.add(correctSub);  //Add the correct substitution to the list
                                                }
                                            }
                                        }

                                        for (Substitution sub : a.getSubstitutions()) {  //Iterate through the query's substitutions
                                            String variable = sub.getName();  //Get the variable associated with the substitution
                                            for (Substitution correctSub : correctSubstitutions) {  //Iterate through the list of correct substitutions
                                                String correctSubVariable = correctSub.getName();  //Get the variable associated with the correct substitution
                                                if (variable.equals(correctSubVariable)) {  //If the variables match
                                                    for (int i = 0; i < correctSub.getSize(); i++) {  //Iterate through the correct substitution
                                                        String correctSubstitute = correctSub.getSubstitution(i);  //Get the string at index i
                                                        sub.addSubstitution(correctSubstitute);  //Add it to the query's substitution
                                                    }
                                                }
                                            }
                                        }
                                        checkDuplicateVariables(correctSubstitutions);  //Check the substituions for duplicates
                                        boolean allFound = true;  //Determines if every variable in the query has a valid substitution
                                        for (Substitution sub : correctSubstitutions) {  //Iterate through the correct substitutions
                                            if (sub.getSize() < 1) {  //If the substitution is empty
                                                allFound = false;  //Set allFound to false
                                                break;  //Exit the loop
                                            }
                                        }
                                        if (allFound) {  //If every variable has a valid substitution
                                            found = true;  //Set found to true
                                            foundRule = true;  //Set found rule true
                                            stringAns = false;  //Set stringAns to false
                                        }
                                    } else {  //If the rule doesn't share variables between the head and the body
                                        found = true;
                                    }
                                } else { //If the rule doesn't contain any variables
                                    if (evaluateGroundQuery(new PrologHolder(prologRule.getSimpleBody())).equals("true")) {  //Evaluate the body and if it returns true
                                        found = true;  //Set found equal to true
                                    }
                                }
                            }
                        }
                    }
                }
                if (found) {  //If a matching fact is found
                    resolvent.pop();  //pop the resolvent
                }
                if (!found) {  //If no matching fact is found
                    break;  //Break the while loop
                }
            } else {
                break;
            }
        }

        PrologHolder answer = new PrologHolder("false");

        if (resolvent.isEmpty() && !foundRule && !stringAns) {  //If the resolvent is empty (indicating that the given query / queries were resolved)
            if (removeInvalidSubstitutions(queryHolder)) {  //If the query has valid substitutions
                ArrayList<Substitution> correctSubstitutions = new ArrayList<>();  //Create an array list of substitutions to hold all of the correct substitutions from each query
                if (queryHolder.getType().equals("simple query")) {  //If the query is a simple query
                    ArrayList<Substitution> substitutions = queryHolder.getQuery().getSubstitutions();  //Get the list of substitutions from the query
                    for (int i = 0; i < substitutions.size(); i++) {  //Iterate through the substitutions
                        Substitution sub = substitutions.get(i);  //Get the substitution at index i
                        if (!containsSubstitution(correctSubstitutions, sub.getName())) {  //If the substitution is not in the list of correct substitutions
                            correctSubstitutions.add(sub);  //Add the substitution to the list
                        }
                    }
                    answer = new PrologHolder(correctSubstitutions);  //Place the list of correct substitutions in a PrologHolder and set answer equal to it
                    //answer = new PrologHolder(queryHolder.getQuery().getSubstitutions());  //Place the query's list of substitutions in a PrologHolder and set answer equal to it
                } else {  //If the query is a conjunctive query
                    ConjunctiveQuery queries = queryHolder.getConjunctiveQuery();  //Get the conjunctive query from the holder
                    for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
                        Query subQuery = queries.getQuery(i);  //Qet the query at index i
                        for (Substitution sub : subQuery.getSubstitutions()) {  //Iterate through the query's substitutions
                            if (!containsSubstitution(correctSubstitutions, sub.getName())) {  //If the substitution is not in the list of correct substitutions
                                correctSubstitutions.add(sub);  //Add the substitution to the list
                            }
                        }
                    }
                    answer = new PrologHolder(correctSubstitutions);  //Place the list of correct substitutions in a PrologHolder and set answer equal to it
                }
            }
        } else if (resolvent.isEmpty() && stringAns) {
            answer = new PrologHolder("true");
        } else if (resolvent.isEmpty()) {
            if (queryHolder.getType().equals("simple query")) {
                answer = new PrologHolder(queryHolder.getQuery().getSubstitutions());
            } else {
                ArrayList<Substitution> allSubstitutions = new ArrayList<>();  //Create an array list of substitutions to hold all of the correct substitutions from each query
                ArrayList<Substitution> correctSubstitutions = new ArrayList<>();  //Create an array list of substitutions to hold all of the correct substitutions from each query
                ConjunctiveQuery queries = queryHolder.getConjunctiveQuery();  //Get the conjunctive query from the holder
                for (int i = 0; i < queries.getNumberOfQueries(); i++) {  //Iterate through the queries
                    Query subQuery = queries.getQuery(i);  //Qet the query at index i
                    for (Substitution sub : subQuery.getSubstitutions()) {  //Iterate through the query's substitutions
                        allSubstitutions.add(sub);  //Add the substitution to the list
                    }
                }
                checkDuplicateVariables(allSubstitutions);  //Check the duplicates for all of the substitutions
                for (Substitution sub : allSubstitutions) {  //Iterate through all of the substitutions
                    if (!containsSubstitution(correctSubstitutions, sub.getName())) {  //If the substitution is not in the list of correct substitutions
                        correctSubstitutions.add(sub);  //Add the substitution to the list
                    }
                }
                answer = new PrologHolder(correctSubstitutions);  //Place the list of correct substitutions in a PrologHolder and set answer equal to it
            }
        }
        return answer;
    }

    /*
     * Determines how to evaluate the given holder
     * @param holder the PrologHolder being evaluated
     * @return True or false if the holder contains a query, otherwise null
     */
    public PrologHolder resolve(PrologHolder holder) {
        PrologHolder answer = null;  //Initialize answer to null
        if (holder.getType().equals("fact")) {  //If the holder contains a fact
            evaluateFact(holder);  //Evaluate the fact
        } else if ((holder.getType().equals("simple query") || holder.getType().equals("conjunctive query")) && !holder.isVariable()) {  //If the holder contains a simple / conjunctive query that is ground
            answer = new PrologHolder(evaluateGroundQuery(holder));  //Evaluate the ground query / queries
        } else if ((holder.getType().equals("simple query") || holder.getType().equals("conjunctive query")) && holder.isVariable()) {  //If the holder contains a simple / conjunctive query that is non-ground
            answer = evaluateNonGroundQuery(holder);  //Evaluate the non-ground query / queries
        } else if (holder.getType().equals("rule")) {  //If the holder contains a rule
            evaluateRule(holder);  //Evaluate the rule
        }
        return answer;
    }

    /*
     * Prints the list of facts and rules within the program
     */
    public void printProgram() {
        for (PrologHolder holder : program) {
            if (holder.getType().equals("fact")) {  //If the holder contains a fact
                Fact fact = holder.getFact();  //Get the fact from the holder
                String functor = fact.getFunctor();  //Get the fact's name
                System.out.println("Functor: " + functor);  //Print the fact's name

                for (int j = 0; j < fact.getNumberOfPredicates(); j++) {  //Iterate through the fact
                    Predicate predicate = fact.getPredicate(j);  //Get the fact's predicate at index j
                    for (int k = 0; k < predicate.getArity(); k++) {  //Iterate through the predicate
                        String atom = predicate.getAtom(k);  //Get the atom at index k
                        System.out.println("Predicate " + j + "-" + k + ": " + atom);  //Print the atom
                    }
                }
                System.out.println("");  //Skip a line

            } else if (holder.getType().equals("rule")) {  //If the holder contains a rule
                PrologRule rule = holder.getPrologRule();  //Get the rule from the holder
                Fact head = rule.getHead();  //Get the head of the rule
                String functor = head.getFunctor();  //Get the functor of the rule
                System.out.println("Rule: " + functor);  //Print the functor

                //for (int j = 0; j < head.getArity(0); j++) {  //Iterate through the head
                Predicate predicate = head.getPredicate(0);  //Get the predicate of the head
                for (int k = 0; k < predicate.getArity(); k++) {  //Iterate through the predicate
                    String atom = predicate.getAtom(k);  //Get the atom at index k of the predicate
                    System.out.println("Predicate " + k + ": " + atom);  //Print the atom
                }
                if (rule.getType().equals("simple query")) {  //If the rule is a simple rule
                    Query bodyQuery = rule.getSimpleBody();  //Get the rule's query
                    String name = bodyQuery.getFunctor();  //Get the query's functor
                    System.out.println("Query: " + name);  //Print the functor

                    //for (int j = 0; j < bodyQuery.getArity(); j++) {  //Iterate through the query
                    Predicate bodyPredicate = bodyQuery.getPredicate();  //Get the predicate of the query
                    for (int k = 0; k < bodyPredicate.getArity(); k++) {  //Iterate through the predicate
                        String atom = bodyPredicate.getAtom(k);  //Get the atom at index k of the predicate
                        System.out.println("Predicate " + k + ": " + atom);  //Print the atom
                    }
                    System.out.println("");  //Skip a line

                } else if (rule.getType().equals("conjunctive query")) {  //If the rule is a conjunctive rule
                    ConjunctiveQuery queries = rule.getConjunctiveBody();  //Get the queries of the rule
                    for (int x = 0; x < queries.getNumberOfQueries(); x++) {  //Iterate through the queries
                        Query bodyQuery = queries.getQuery(x);  //Get a query at index x
                        String name = bodyQuery.getFunctor();  //Get the query's functor
                        System.out.println("Query " + x + ": " + name);  //Print the functor

                        //for (int j = 0; j < bodyQuery.getArity(); j++) {  //Iterate through the query
                        Predicate bodyPredicate = bodyQuery.getPredicate();  //Get the predicate of the query
                        for (int k = 0; k < bodyPredicate.getArity(); k++) {  //Iterate through the predicate
                            String atom = bodyPredicate.getAtom(k);  //Get the atom at index k of the predicate
                            System.out.println("Predicate " + k + ": " + atom);  //Print the predicate
                        }
                    }

                }
                System.out.println("");  //Skip a line
            }
        }
    }

    /*
     * A helper method that gets the substitutions associated with a given query
     * @param allSubstitutions a list of all od the substitutions for a conjunctive query
     * @param queryID the id of the given query
     * @return a list of substitutions for the given query
     */
    public ArrayList<Substitution> getSubstitutionsForQuery(ArrayList<Substitution> allSubstitutions, int queryID) {
        ArrayList<Substitution> querySubstitutions = new ArrayList<>();  //Create a list of substitutions
        for (Substitution sub : allSubstitutions) {  //Iterate through all the substitutions
            if (sub.getQueryID() == queryID) {  //If the substitution's queryID matches the given ID
                querySubstitutions.add(sub);  //Add the substitution to the created list
            }
        }
        return querySubstitutions;
    }

    /*
     * Prints the given non-ground query
     * @param queryHolder a PrologHolder containing a simple query or conjunctive query
     * @param substitutionHolder a PrologHolder containing the list of correct substitutions for the query / queries
     */
    public void printNonGroundQuery(PrologHolder queryHolder, PrologHolder substitutionHolder) {
        ArrayList<Substitution> subs = substitutionHolder.getSubstitutions();  //Get the list of substitutions from the substitutionHolder
        if (queryHolder.getType().equals("simple query")) {  //If the queryHolder contains a simple query
            String[] outputs = new String[subs.get(0).getSize()];  //Create an array whose size is the number of substitutions for a variable
            for (Substitution sub : subs) {  //Iterate through the list of substitutions
                String variable = sub.getName();  //Get the name of the variable
                for (int i = 0; i < sub.getSize(); i++) {  //Iterate through the substitution
                    String newOutput = variable + " = " + sub.getSubstitution(i);  // Get the variable and the substitution at i
                    String previousOutput = outputs[i];  //Get the previous output
                    if (previousOutput == null) {  //If the previous output is null
                        outputs[i] = newOutput;  //Set the string at i equal to the new output
                    } else {  //If the previous output is a string
                        outputs[i] = previousOutput + ", " + newOutput;  //Add the new output to the string
                    }
                }
            }
            for (int i = 0; i < outputs.length; i++) {  //Iterate through the list of outputs
                System.out.println(outputs[i]);  //Print each output on a new line
            }
        } else {  //If the queryHolder contains a conjunctive query
            int maxID = 0;
            for (Substitution sub : subs) {
                if (sub.getQueryID() > maxID) {
                    maxID = sub.getQueryID();
                }
            }

            for (int j = 0; j < maxID + 1; j++) {
                ArrayList<Substitution> querySubstitutions = getSubstitutionsForQuery(subs, j);
                String[] outputs = new String[querySubstitutions.get(0).getSize()];
                for (Substitution sub : querySubstitutions) {
                    String variable = sub.getName();  //Get the name of the variable
                    for (int i = 0; i < sub.getSize(); i++) {  //Iterate through the substitution
                        String newOutput = variable + " = " + sub.getSubstitution(i);  // Get the variable and the substitution at i
                        String previousOutput = outputs[i];  //Get the previous output
                        if (previousOutput == null) {  //If the previous output is null
                            outputs[i] = newOutput;  //Set the string at i equal to the new output
                        } else {  //If the previous output is a string
                            outputs[i] = previousOutput + ", " + newOutput;  //Add the new output to the string
                        }
                    }
                }
                for (int i = 0; i < outputs.length; i++) {  //Iterate through the list of outputs
                    System.out.println(outputs[i]);  //Print each output on a new line
                }
            }
        }
    }
}
