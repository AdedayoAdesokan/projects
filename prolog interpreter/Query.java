import java.util.ArrayList;

public class Query {
    private String functor;
    private Predicate predicate;
    private ArrayList<Substitution> substitutions;
    private int id;

    /*
     * A constructor that creates a query given a functor and a predicate
     * @param functor the name of the query
     * @param predicate the predicate of the query
     */
    public Query(String functor, Predicate predicate) {
        this.functor = functor;  //Initializes the functor
        this.predicate = predicate;  //Initializes the predicate
        substitutions = new ArrayList<>();  //Create the array list
        id = -1;  //Set the id to -1 to indicate that it hasn't been set yet
    }

    /*
     * Gets the functor of the query
     * @return the name of the query
     */
    public String getFunctor() {
        return functor;
    }

    /*
     * Gets the predicate of the query
     * @return the query's predicate
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /*
     * Gets the atom at the given index
     * @param index the index of the desired atom
     * @return the atom at the given index
     */
    public String getAtom(int index) {
        return predicate.getAtom(index);
    }

    /*
     * Gets the number of arguments this query holds
     * @return the number of arguments in this query
     */
    public int getArity() {
        return predicate.getArity();
    }

    /*
     * Gets the list of substitutions
     * @return the list of substitutions
     */
    public ArrayList<Substitution> getSubstitutions() {
        return substitutions;
    }

    /*
     * Set the id to the given id
     * @param the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /*
     * Get the id for this query
     * @return this query's id
     */
    public int getId() {
        return this.id;
    }

    /*
     * Gets the number of variables within the query
     * @return the size of the list of substitutions
     */
    public int getNumberOfVariables() {
        return substitutions.size();
    }
}
