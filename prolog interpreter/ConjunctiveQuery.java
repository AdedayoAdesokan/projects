import java.util.ArrayList;

public class ConjunctiveQuery {
    private ArrayList<Query> queries;

    /*
     * A constructor that creates a ConjunctiveQuery
     */
    public ConjunctiveQuery() {
        queries = new ArrayList<>();  //Create the array list of Queries
    }

    /*
     * Adds a query to the list of queries
     * @param query the query being added to the list
     */
    public void addQuery(Query query) {
        queries.add(query);
    }

    /*
     * Gets the query at a given index
     * @param index the index of the desired query
     * @return the query at the given index
     */
    public Query getQuery(int index) {
        return queries.get(index);
    }

    /*
     * Gets the arity of the conjunctive query
     * @return the number queries within the list
     */
    public int getNumberOfQueries() {
        return queries.size();
    }
}
