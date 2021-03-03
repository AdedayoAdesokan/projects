public class PrologRule {
    private Fact head;
    private Query simpleBody;
    private ConjunctiveQuery conjunctiveBody;
    private String type;

    /*
     * Creates a PrologRule given a Fact and a Query
     */
    public PrologRule(Fact head, Query simpleBody) {
        this.head = head;
        this.simpleBody = simpleBody;
        type = "simple query";  //Indicates the type of rule
    }

    /*
     * Creates a PrologRule given a Fact and a Conjunctive Query
     */
    public PrologRule(Fact head, ConjunctiveQuery conjunctiveBody) {
        this.head = head;
        this.conjunctiveBody = conjunctiveBody;
        type = "conjunctive query";  //Indicates the type of rule
    }

    /*
     * Gets this rule's head
     * @return this rule's head
     */
    public Fact getHead() {
        return head;
    }

    /*
     * Gets this rule's body
     * @return this rule's body
     */
    public Query getSimpleBody() {
        return simpleBody;
    }

    /*
     * Gets this rule's conjunctive body
     * @return this rule's conjunctive body
     */
    public ConjunctiveQuery getConjunctiveBody() {
        return conjunctiveBody;
    }

    /*
     * Gets this rule's type
     * @return the type of rule this is
     */
    public String getType() {
        return type;
    }
}
