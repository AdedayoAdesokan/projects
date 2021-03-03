import java.util.ArrayList;
import java.util.LinkedList;

public class Predicate {
    private ArrayList<String> atoms;
    private boolean variable;
    private ArrayList<LinkedList<String>> lists;

    /*
     * A constructor that creates a Predicate object
     */
    public Predicate() {
        atoms = new ArrayList<>();  //Creates an array list of Strings
        variable = false;  //Initialize variable to false, indicating that this predicate doesn't contain any variables
        lists = new ArrayList<>();  //Initialize the list of linked lists
    }

    /*
     * Adds the given atom to the list
     * @param atom the atom being added to the list of atoms
     */
    public void addAtom(String atom) {
        atoms.add(atom);
    }

    /*
     * Gets the atom at the given index within the list
     * @param index the index of the desired atom
     * @return the atom at the given index
     */
    public String getAtom(int index) {
        return atoms.get(index);
    }

    /*
     * Gets the index of a given atom
     * @param atom the atom whose index will be returned
     * @return the index of the given atom
     */
    public int getIndex(String atom) {
        return atoms.indexOf(atom);
    }

    /*
     * Gets the arity of the predicate
     * @return the number of atoms within the list
     */
    public int getArity() {
        //  return atoms.size() + lists.size();
        return atoms.size();
    }

    /*
     * Sets variable equal to true, indicating that this predicate contains a variable
     */
    public void enableVariable() {
        variable = true;
    }

    /*
     * Determines if this predicate contains a variable or not
     * @return true if this predicate contains a variable
     */
    public boolean isVariable() {
        return variable;
    }

    /*
     * Gets the list of linked lists
     * @return the list of linked lists
     */
    public ArrayList<LinkedList<String>> getLists() {
        return lists;
    }

    /*
     * Determines if the predicate contains a list
     * @return true if the predicate contains a list
     */
    public boolean containsList() {
        boolean contains = false;  //Initialize contains to false
        for (LinkedList<String> list : lists) {  //Iterate through the list of linked lists
            if (list.size() > 0) {  //If the size of the list is greater than 0
                contains = true;  //Set contains to true
                break;  //Exit the loop
            }
        }
        return contains;
    }
}
