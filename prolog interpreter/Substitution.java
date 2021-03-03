import java.util.ArrayList;

public class Substitution {

    private String name;  //The name of the variable the list of substitutions are for
    private ArrayList<String> substitutions;  //A list of substitutions associated with name
    private int index;  //The index of the variable in the query
    private int addCount;  //A counter that keeps track of how many times a substitution has been added to the list within the scope of a single fact
    private int queryID;  //Indicates which query (in a conjunctive query) this substitution belongs

    /*
     * Creates a Substitution object with the given name
     * @param name the name of the variable that these substitutions are for
     */
    public Substitution(String name) {
        this.name = name; //Initialize name
        substitutions = new ArrayList<>();  //Create the array list
        index = -1;  //Set index to -1 to indicate that this Substitution's index has not been set yet
        addCount = 0;  //Set add count equal to zero
        queryID = -1;  //Set the id to -1 to indicate that this Substitution's queryID has not been set yet
    }

    /*
     * Gets the name of the variable that these substitutions are associated with
     * @return the name of the variable that these substitutions are associated with
     */
    public String getName() {
        return name;
    }

    /*
     * Adds the given substitution to the list of substitutions
     * @param substitution the substitution being added to the list
     */
    public void addSubstitution(String substitution) {
        substitutions.add(substitution);
    }

    /*
     * Removes the last substitution from the list a given amount of times
     * @param amount the amount of times the last substitution will be removed
     */
    public void removeRecent(int amount) {
        for (int i = 0; i < amount; i++) {  //Iterate through the list
            substitutions.remove(substitutions.size() - 1);  //Delete the last substitution
        }
    }

    /*
     * Gets the list of substitutions
     * @return the list of substitutions
     */
    public String getSubstitution(int index) {
        return substitutions.get(index);
    }

    /*
     * Gets the size of substitutions
     * @return the size of the array list
     */
    public int getSize() {
        return substitutions.size();
    }

    /*
     * Removes the given string from the list
     * @param name the name of the string to be removed
     */
    public void removeSubstitution(String name) {
        substitutions.remove(name);
    }

    /*
     * Removes the substitution at the given index from the list
     * @param index the index of the substitution to be removed
     */
    public void removeSubstitutionAtIndex(int index) {
        substitutions.remove(index);
    }

    /*
     * Sets index to the given index
     * @param index the index in the query of the variable that this substitution is associated with
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /*
     * Gets the index of the variable this substitution is associated with
     * @retun the variable's index
     */
    public int getIndex() {
        return index;
    }

    /*
     * Increments the addCount by 1, indicating that a substitution has been added to the list
     */
    public void incrementAddCount() {
        addCount++;
    }

    /*
     * Gets the addCount which indicates the number of times a substitution has been added to the list within the scope of a single fact
     * @return the addCount
     */
    public int getAddCount() {
        return addCount;
    }

    /*
     * Sets addCount to zero
     */
    public void clearAddCount() {
        addCount = 0;
    }

    /*
     * Sets the queryID to the given id
     * @param id the new id of the substitution
     */
    public void setQueryID(int id) {
        queryID = id;
    }

    /*
     * Gets the queryID for this substitution
     * @retun this substitution's queryID
     */
    public int getQueryID() {
        return queryID;
    }

    /*
     * Sets the string at the given index within substitutions to the given string
     * @param index the index of the string to be modified
     * @param newSubstitution the new string to be placed into the list at the given index
     */
    public void setSubstitution(int index, String newSubstitution) {
        substitutions.set(index, newSubstitution);
    }

    /*
     * Sets the addCount to the given addCount
     * @param newAddCount the new cound for the counter addCount
     */
    public void setAddCount(int newAddCount) {
        addCount = newAddCount;
    }
}
