import java.util.ArrayList;

public class Fact {
    private String functor;
    private ArrayList<Predicate> predicates;

    /*
     * A constructor that creates a Fact given a functor
     * @param functor the name of the fact
     */
    public Fact(String functor) {
        this.functor = functor;  //initializes the functor
        this.predicates = new ArrayList<Predicate>();  //Creates an array list of Predicates
    }

    /*
     * Adds the given Predicate to the list of predicates
     * @param predicate the predicate being added to the list
     */
    public void addPredicate(Predicate predicate) {
        predicates.add(predicate);
    }

    /*
     * Gets the predicate at the given index
     * @param index the index of the desired predicate
     * @return the predicate at the given index
     */
    public Predicate getPredicate(int index) {
        return predicates.get(index);
    }

    /*
     * Gets the index of a given predicate
     * @param predicate the predicate whose index will be found
     * @return the index of the predicate
     */
    public int getIndex(Predicate predicate) {
        return predicates.indexOf(predicate);
    }

    /*
     * Gets the functor of the fact
     * @return the name of the fact
     */
    public String getFunctor() {
        return functor;
    }

    public int getArity(int index) {
        return predicates.get(index).getArity();
    }

    /*
     * Gets the number of predicates the fact holds
     * @return the number of predicates within the list
     */
    public int getNumberOfPredicates() {
        return predicates.size();
    }
}
