import java.util.Scanner;

public class Calculator {

    private Postfix postfixCalc;
    private Infix infixCalc;

    /*
     * A constructor that creates a calculator in either postfix or infix mode
     * @param input determines the mode of the calculator
     */
    public Calculator(String input) {
        postfixCalc = new Postfix();  //Create a postfix calculator
        infixCalc = new Infix();  //Create an infix calculator
        if (input.toLowerCase().equals("postfix")) {  //Turn on the appropriate calculator determined by the input
            postfixCalc.isOn(true);
        } else if (input.toLowerCase().equals("infix")) {
            infixCalc.isOn(true);
        } else {
            System.out.println("Please Input Mode Postfix or Infix");  //If the input is invalid print this statement
        }
    }

    /*
     * Changes the mode of the calculator to either postfix or infix
     * @param input the mode the calculator is changing to
     */
    public void changeMode(String input) {
        if (input.toLowerCase().equals("postfix")) {  //If the input is postfix
            postfixCalc.isOn(true);  //Turn the postfix calculator on
            infixCalc.isOn(false);  //Turn the infix calculator off
        } else if (input.toLowerCase().equals("infix")) {  //If the input is infix
            infixCalc.isOn(true);  //Turn the infix calculator on
            postfixCalc.isOn(false);  //Turn the postfix calculator off
        } else {
            System.out.println("Please Enter a Valid Mode: Postfix or Infix");  //If the input is invalid print this statement
        }
    }

    /*
     * Determines if the calculator is in postfix mode
     * @return true if the postfix calculator is set to on
     */
    public boolean isPostfix() {
        return postfixCalc.getMode();
    }

    /*
     * Determines if the calculator is in infix mode
     * @return true if the infix calculator is set to on
     */
    public boolean isInfix() {
        return infixCalc.getMode();
    }

    /*
     * The main method that runs the calculator
     * @param args Uses command-line arguments to initialize the mode of the calculator
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);  //Create a scanner
        Calculator calc = new Calculator(args[0]);  //Create the calculator using the command-line argument
        String prompt = "Calculator> ";
        System.out.print(prompt);  //Print the prompt for the user

        while (in.hasNextLine()) {  //The program ends when it encounters the EOF character (CTRL + Z for Windows)
            String expression = in.nextLine();  //Get the given expression
            if (expression.isBlank()) {
                if (expression.length() > 0) {
                    System.out.println("Please input a valid expression");  //Check for blank expressions that are not returns
                }
            } else {
                if (expression.toLowerCase().equals("postfix") || expression.toLowerCase().equals("infix")) {
                    calc.changeMode(expression);  //If the expression is infix or postfix, change the calculator's current mode
                } else if (calc.isInfix()) {
                    if (calc.infixCalc.validExpression(expression)) {  //If the calculator is in infix mode, ensure that the given expression is valid
                        System.out.println(calc.infixCalc.evaluateExpression(expression));  //Evaluate the expression and print the answer
                    }
                } else if (calc.isPostfix()) {  //If the calculator is in postfix mode
                    if (calc.postfixCalc.validExpression(expression)) {  //If the calculator is in infix mode, ensure that the given expression is valid
                        System.out.println(calc.postfixCalc.evaluateExpression(expression));  //Evaluate the expression and print the answer
                    }
                }
            }
            System.out.print(prompt);  //Print the prompt for the user
        }
        in.close();  //Close the scanner
    }
}
