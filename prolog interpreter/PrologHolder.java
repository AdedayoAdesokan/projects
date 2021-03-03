import java.util.ArrayList;
import java.util.LinkedList;

/*
 * A class that holds items specific to prolog
 */
public class PrologHolder {
    private Fact fact;
    private Predicate predicate;
    private Query query;
    private ConjunctiveQuery queries;
    private String atom;
    private PrologRule prologRule;
    private String type;
    private boolean variable;
    private boolean allFound;
    private String answer;
    private ArrayList<Substitution> substitutions;
    private LinkedList<String> list;


    /*
     * Creates a Holder for a single fact
     */
    public PrologHolder(Fact fact) {
        this.fact = fact;
        type = "fact";
        variable = false;
    }

    /*
     * Creates a Holder for a single predicate
     */
    public PrologHolder(Predicate predicate) {
        this.predicate = predicate;
        type = "predicate";
        variable = false;
    }

    /*
     * Creates a Holder for a single atom
     */
    public PrologHolder(String atom) {
        this.atom = atom;
        type = "atom";
        variable = false;
    }

    /*
     * Creates a Holder for a single query
     */
    public PrologHolder(Query query) {
        this.query = query;
        type = "simple query";
        variable = false;
    }

    /*
     * Creates a Holder for a single query
     */
    public PrologHolder(ConjunctiveQuery queries) {
        this.queries = queries;
        type = "conjunctive query";
        variable = false;
    }

    /*
     * Creates a Holder for a single prologRule
     */
    public PrologHolder(PrologRule prologRule) {
        this.prologRule = prologRule;
        type = "rule";
        variable = false;
    }

    /*
     * Creates a Holder for a list of substitutions
     */
    public PrologHolder(ArrayList<Substitution> substitutions) {
        this.substitutions = substitutions;
        type = "substitutions";
        variable = false;
        answer = null;
    }

    /*
     * Creates a Holder for a list of substitutions
     */
    public PrologHolder(ArrayList<Substitution> substitutions, boolean allFound) {
        this.substitutions = substitutions;
        type = "substitutions";
        variable = false;
        this.allFound = allFound;
    }

    /*
     * Creates a Holder for a list
     */
    public PrologHolder(LinkedList<String> list) {
        this.list = list;
        type = "list";
        variable = false;
    }

    /*
     * Gets the fact that's being held
     */
    public Fact getFact() {
        return fact;
    }

    /*
     * Gets the predicate that's being held
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /*
     * Gets the atom that's being held
     */
    public String getAtom() {
        return atom;
    }

    /*
     * Gets the query that's being held
     */
    public Query getQuery() {
        return query;
    }

    /*
     * Gets the conjunctive query that's being held
     */
    public ConjunctiveQuery getConjunctiveQuery() {
        return queries;
    }

    /*
     * Gets the prologRule that's being held
     */
    public PrologRule getPrologRule() {
        return prologRule;
    }

    /*
     * Gets the list of substitutions that's being held
     */
    public ArrayList<Substitution> getSubstitutions() {
        return substitutions;
    }

    /*
     * Gets the list that's being held
     */
    public LinkedList<String> getList() {
        return list;
    }

    /*
     * Gets the type of item that's being held
     */
    public String getType() {
        return type;
    }

    /*
     * Sets variable equal to true, indicating that this predicate contains a variable
     */
    public void changeToVariable() {
        variable = true;
    }

    /*
     * Determines if this holder contains a variable or not
     * @return true if this holder contains a variable
     */
    public boolean isVariable() {
        return variable;
    }

    /*
     * Returns the boolean allFound
     * @return the boolean allFound
     */
    public boolean isAllFound() {
        return allFound;
    }

    /*
     * Sets answer equal to the given string
     */
    public void addString(String answer) {
        this.answer = answer;
    }

    /*
     * Gets the answer
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }
}
