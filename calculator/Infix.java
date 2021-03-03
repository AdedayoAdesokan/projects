import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Infix {

    private String expression;
    private boolean mode;

    /*
     * A constructor that sets the mode of the calculator to false
     */
    public Infix() {
        mode = false;
    }

    /*
     * Determines whether the calculator is on or off
     * @param value determines the mode of this calculator
     */
    public void isOn(boolean value) {
        mode = value;
    }

    /*
     * Gets the mode of this calculator
     * @return this calculator's mode
     */
    public boolean getMode() {
        return mode;
    }

    /*
     * A helper method that determines if a variable is an operator, whitespace, or decimal point that needs to be skipped
     * @param variable the variable that is being checked to see if it needs to be skipped
     * @return true if the variable is an operator or whitespace
     */
    private boolean skip(String variable) {
        boolean status = false;
        if (variable.equals("+") || variable.equals("-") || variable.equals("*") || variable.equals("/") || variable.equals("^") || variable.equals("(") || variable.equals(")") || variable.equals(" ") || variable.equals(".")) {
            status = true;
        }
        return status;
    }

    /*
     * Determines if the given expression only uses valid operators
     * @param expression the expression being checked for validity
     * @return true if the expression is valid
     */
    public boolean validExpression(String expression) {
        boolean status = true;
        for (int i = 0; i < expression.length(); i++) {  //Iterate through the expression
            char charVariable = expression.charAt(i);
            String variable = Character.toString(charVariable);  //Check each string in the expression
            if (!this.skip(variable)) {
                try {
                    float operand = Float.parseFloat(variable);  //Performs the proper type conversion on the variable
                } catch (Exception NumberFormatException) {
                    System.out.println("Please use valid operators");  //If the string cannot be converted then it is an invalid operator
                    status = false;  //Set status equal to false
                }
            }
        }
        return status;
    }

    /*
     * Evaluates the given expression
     * @param the expression th
     * @return the answer to the expression as a String
     */
    public float evaluateExpression(String expression) {
        CharStream inputStream = CharStreams.fromString(expression);  //Create a CharStream
        InfixLexer lexer = new InfixLexer(inputStream);  //Create a lexer form the Infix Grammar and pass the CharStream into it
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);  //Create a Common Token Stream and pass the lexer into it
        InfixParser parser = new InfixParser(commonTokenStream);  //Create a parser form the Infix Grammar and pass the token stream into it

        ParseTree tree = parser.expr();  //Create a parse tree that starts at the expr (expression) parse rule
        InfixInterpreter eval = new InfixInterpreter();  //Create an Infix Interpreter that evaluates the parse tree
        String answer = eval.visit(tree);  //Evaluate the parse tree and save the answer
        return Float.parseFloat(answer);  //Return the answer
    }


    /*  //A test program
    public static void main(String[] args) {
        String input = "(22 / 2 - 2.5) ^ 2 + (4 - 6 / 6) ^ 2";

        CharStream inputStream = CharStreams.fromString(input);
        InfixLexer lexer = new InfixLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        InfixParser parser = new InfixParser(commonTokenStream);

        ParseTree tree = parser.expr();
        System.out.println("Parse tree output: " + tree.toString());
        InfixInterpreter eval = new InfixInterpreter();
        String result = eval.visit(tree);
        System.out.println("result :" + result);
        System.out.println("length :" + eval.visit(tree).length());
    }
    */

}
