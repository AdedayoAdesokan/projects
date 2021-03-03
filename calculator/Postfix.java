import java.util.Stack;

public class Postfix {

    private Stack<Float> operands;
    private boolean mode;

    /*
     * A constructor that creates the internal stack needed for postfix operations
     */
    public Postfix() {
        operands = new Stack<>();  //Create the stack
        mode = false;  //Set mode equal to false
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
     * A helper method that determines if the given String is an operator
     * @param variable the given String being tested
     * @return true if variable is a valid operator
     */
    public boolean isOperator(String variable) {
        boolean status = false;
        if (variable.equals("+") || variable.equals("-") || variable.equals("*") || variable.equals("/") || variable.equals("^")) {
            status = true;
        }
        return status;
    }


    /*
     * A helper method that determines if a variable is an operator, whitespace, or decimal point that needs to be skipped
     * @param variable the variable that is being checked to see if it needs to be skipped
     * @return true if the variable is an operator or whitespace
     */
    private boolean skip(String variable) {
        boolean status = false;
        if (this.isOperator(variable) || variable.equals(" ") || variable.equals(".")) {
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
     * A helper method that evaluates the given operator
     * @param operator the operator you want to evaluate
     */
    private void evaluateOperator(String operator) {
        float operand1 = operands.pop();  //Pop the stack twice, indicating the first and second numbers
        float operand2 = operands.pop();
        float result = 0.0f;
        if (operator.equals("+")) {
            result = operand2 + operand1;  //Adds the second number to the first number
            operands.push(result);  //Pushes the result back onto the stack
        } else if (operator.equals("-")) {
            result = operand2 - operand1;  //Subtracts the first number from the second number
            operands.push(result);  //Pushes the result back onto the stack
        } else if (operator.equals("*")) {
            result = operand2 * operand1;  //Multiplies the second number by the first number
            operands.push(result);  //Pushes the result back onto the stack
        } else if (operator.equals("/")) {
            result = operand2 / operand1;  //Divides the second number by the first number
            operands.push(result);  //Pushes the result back onto the stack
        } else {  //if (operator.equals("^"))
            result = (float) Math.pow(operand2, operand1);  //Raises the second number to the power of the first number
            operands.push(result);  //Pushes the result back onto the stack
        }
    }

    /*
     * Evaluates the given expression
     * @param expression the input string in postfix notation given by the user
     * @return the answer
     */
    public float evaluateExpression(String expression) {
        String[] list = expression.split(" ");  //Splits the expression by its whitespace characters
        for (String variable : list) {  //Iterates through the list
            if (!this.isOperator(variable)) {  //Checks to see if the given variable is not an operator
                float operand = Float.parseFloat(variable);  //Performs the proper type conversion on the variable
                operands.push(operand);  //Pushes the operand onto the Stack
            } else {
                this.evaluateOperator(variable);  //If the given variable is an operator, evaluate it using the helper method
            }
        }
        float answer = 0.0f;
        answer = operands.pop();  //Once the list of tokens is exhausted, pop the stack and return the popped value as the result of the expression
        return answer;
    }

    /*
     * Evaluates the given array
     * @param list an expression split into an array by its whitespace characters
     */
    public float evaluateList(String[] list) {
        for (String variable : list) {  //Iterates through the list
            if (!this.isOperator(variable)) {  //Checks to see if the given variable is not an operator
                try {
                    float operand = Float.parseFloat(variable);  //Performs the proper type conversion on the variable
                    operands.push(operand);  //Pushes the operand onto the Stack
                } catch (Exception NumberFormatException) {
                    System.out.println("Please use valid operators");
                }
            } else {
                this.evaluateOperator(variable);  //If the given variable is an operator, this evaluates it using the helper method
            }
        }
        return operands.pop();  //Return the popped value as the result of the expression
    }

    /*  //A test program
    public static void main(String[] args) {
        Postfix post = new Postfix();
        String input = "5 5 + 6 ;";
        System.out.println(post.evaluateExpression(input));

        Scanner in = new Scanner(System.in);
        int count = 1;
        while (in.hasNextLine()) {
            String expression = in.nextLine();
            System.out.println("Expression " + count + ": [" + expression + "]");
            post.evaluateExpression(expression);
            System.out.println();
            count++;
        }
    }
    */
}
