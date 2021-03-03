public class InfixInterpreter extends InfixBaseVisitor<String> {

    /*
     * A helper method that uses the postfix class to evaluate expressions
     * @param list the expression you wan to evaluate
     * @return the answer
     */
    private float evaluate(String[] list) {
        Postfix postfix = new Postfix();  //Create a postfix calculator
        String temp = "";  //Initialize a temporary string
        int i = 0;
        while (i < list.length) {  //Iterate through the expression
            String element = list[i];  //Get the element in the ith position of the expression
            if (postfix.isOperator(element)) {  //If the element is an operator
                temp = list[i + 1];  //Store the proceeding element in the temporary value
                list[i + 1] = element;  //Move the element to the next position
                list[i] = temp;  //Store the proceeding element in the previous position
                i = i + 2;  //Increment the counter by 2 to skip over the swapped elements
            } else {  //If the current element is not an operator
                i++;  //Increment the counter by 1
            }
        }
        return postfix.evaluateList(list);  //Use the postfix calculator to evaluate the expression and return the answer
    }

    /*
     * A node that holds a Division sign and a Number
     * @param
     * @return a division sign and number
     */
    public String visitDivNum(InfixParser.DivNumContext ctx) {
        String child1 = ctx.DIV().getText() + " ";  //Get the division sign and add a space
        String child2 = ctx.NUM().getText() + " ";  //Get the number and add a space
        return child1 + child2;  //Return the division sign and number
    }

    /*
     * A node that deals with Division signs followed by Parentheses
     * @param
     * @return an answer or a sub-expression
     */
    public String visitDivPar(InfixParser.DivParContext ctx) {
        StringBuilder allChildren = new StringBuilder();  //A string that holds an expression made up of the subexpressions from this node's children
        String child1 = ctx.DIV().getText() + " ";  //Get the division sign and store it with a space
        allChildren.append(child1);  //Add the division sign to the string
        for (int i = 2; i < ctx.getChildCount() - 1; i++) {
            String child = visit(ctx.getChild(i));  //Visit every child except the first, second and last because those hold the division sign and the parenthesis
            allChildren.append(child);  //Append the result to the string of subexpressions
        }

        String[] list = allChildren.toString().split(" ");  //Split the expression by its whitespace characters
        String[] copy = new String[list.length - 1];  //Create a new array to hold a portion of the expression
        System.arraycopy(list, 1, copy, 0, list.length - 1);  //Copy the expression minus the first element (the division sign)

        String result = "";
        if (copy.length > 2) {  //If the copied expression has more than two elements (example = 2 * 3), it can be evaluated
            float evaluation = this.evaluate(copy);  //Evaluate the copied expression
            result = "/ " + String.valueOf(evaluation) + " ";  //Add the division sign back to the expression
        } else {
            result = allChildren.toString();  //If the copied expression only has 2 elements (example = 1 +), return the original expression
        }
        return result;  //Return the answer or expression
    }

    /*
     * A node that holds a Multiplication sign and a Number
     * @param
     * @return a multiplication sign and number
     */
    public String visitMultiNum(InfixParser.MultiNumContext ctx) {
        String child1 = ctx.MULTI().getText() + " ";  //Get the multiplication sign and add a space
        String child2 = ctx.NUM().getText() + " ";  //Get the number and add a space
        return child1 + child2;  //Return the multiplication sign and number
    }

    /*
     * A parent node that deals with Multiplication signs followed by Parentheses
     * @param
     * @return an answer or a sub-expression
     */
    public String visitMultiPar(InfixParser.MultiParContext ctx) {
        StringBuilder allChildren = new StringBuilder();  //A string that holds an expression made up of the subexpressions from this node's children
        String child1 = ctx.MULTI().getText() + " ";  //Get the multiplication sign and store it with a space
        allChildren.append(child1);  //Add the multiplication sign to the string
        for (int i = 2; i < ctx.getChildCount() - 1; i++) {
            String child = visit(ctx.getChild(i));  //Visit every child except the first, second and last because those hold the multiplication sign and the parenthesis
            allChildren.append(child);  //Append the result to the string of subexpressions
        }

        String[] list = allChildren.toString().split(" ");  //Split the expression by its whitespace characters
        String[] copy = new String[list.length - 1];  //Create a new array to hold a portion of the expression
        System.arraycopy(list, 1, copy, 0, list.length - 1);  //Copy the expression minus the first element (the multiplication sign)

        String result = "";
        if (copy.length > 2) {  //If the copied expression has more than two elements (example = 2 * 3), it can be evaluated
            float evaluation = this.evaluate(copy);  //Evaluate the copied expression
            result = "* " + String.valueOf(evaluation) + " ";  //Add the multiplication sign back to the expression
        } else {
            result = allChildren.toString();  //If the copied expression only has 2 elements (example = 1 +), return the original expression
        }
        return result;  //Return the answer or expression
    }

    /*
     * A node that holds an Exponent sign and a Number
     * @param
     * @return an exponent and number
     */
    public String visitExpoNum(InfixParser.ExpoNumContext ctx) {
        String child1 = ctx.EXPO().getText() + " ";  //Get the exponent sign and add a space
        String child2 = ctx.NUM().getText() + " ";  //Get the number and add a space
        return child1 + child2;  //Return the exponent and number
    }

    /*
     * A node that deals with Operators followed by an Exponent sign and a Number
     */
    public String visitOpExpNum(InfixParser.OpExpNumContext ctx) {
        StringBuilder allChildren = new StringBuilder();  //A string that holds an expression made up of the subexpressions from this node's children
        String child1 = visit(ctx.getChild(0));  //Visit the first child and get its result
        allChildren.append(child1);  //Add the result to the string
        String expo = ctx.EXPO().getText() + " ";  //Get the exponent sign with a space
        allChildren.append(expo);  //Add the exponent sign to the string
        String child2 = ctx.NUM().getText() + " ";  //Visit the second child and get its result
        allChildren.append(child2);  //Add the result to the string

        String[] list = allChildren.toString().split(" ");  //Split the expression by its whitespace characters
        String result = "";
        String[] copy = new String[list.length - 1];  //Create a new array to hold a portion of the expression
        System.arraycopy(list, 1, copy, 0, list.length - 1);  //Copy the expression minus the first element (the operator)
        if (copy.length > 2) {  //If the copied expression has more than two elements (example = 2 * 3), it can be evaluated
            float evaluation = this.evaluate(copy);  //Evaluate the copied expression
            result = list[0] + " " + String.valueOf(evaluation) + " ";  //Add the operator back to the expression
        } else {
            result = allChildren.toString();  //If the copied expression only has 2 elements (example = 1 +), return the original expression
        }
        return result;  //Return the answer or expression
    }


    /*
     * A node that deals with Operators followed by an Exponent sign and a Sub-expression
     * @param
     * @return a sub-expression or an answer
     */
    public String visitOpExpSub(InfixParser.OpExpSubContext ctx) {
        Postfix postfix = new Postfix();

        StringBuilder allChildren = new StringBuilder();  //A string that holds an expression made up of the subexpressions from this node's children
        String child1 = visit(ctx.getChild(0));  //Visit the first child and get its result
        allChildren.append(child1);  //Add the result to the string
        String expo = ctx.EXPO().getText() + " ";  //Get the exponent sign with a space
        allChildren.append(expo);  //Add the exponent sign to the string
        String child2 = visit(ctx.getChild(2));  //Visit the second child and get its result
        allChildren.append(child2);  //Add the result to the string

        String[] list = allChildren.toString().split(" ");  //Split the expression by its whitespace characters
        String result = "";
        if (postfix.isOperator(list[list.length - 1])) {  //If the last element in the expression is an operator
            String[] copy = new String[list.length - 2];  //Create a new array to hold a portion of the expression
            for (int i = 1; i < list.length - 1; i++) {
                copy[i - 1] = list[i];  //Copy the expression minus the first and last elements (the operators)
            }
            if (copy.length > 2) {  //If the copied expression has more than two elements (example = 2 * 3), it can be evaluated
                float evaluation = this.evaluate(copy);  //Evaluate the expression
                result = list[0] + " " + String.valueOf(evaluation) + " " + list[list.length - 1] + " ";  //Add the operators back to the expression
            } else {
                result = allChildren.toString();  //If the copied expression has less than 2 elements, return the original expression
            }
        } else {  //If the last element in the expression isn't an operator
            String[] copy = new String[list.length - 1];  //Create a new array to hold a portion of the expression
            System.arraycopy(list, 1, copy, 0, list.length - 1);  //Copy the expression minus the first element (the operator)

            if (copy.length > 2) {  //If the copied expression has more than two elements (example = 2 * 3), it can be evaluated
                float evaluation = this.evaluate(copy);  //Evaluate the copied expression
                result = list[0] + " " + String.valueOf(evaluation) + " ";  //Add the operator back to the expression
            } else {
                result = allChildren.toString();  //If the copied expression only has 2 elements (example = 1 +), return the original expression
            }
        }
        return result;  //Return the answer or expression
    }

    /*
     * A node that deals with Exponent signs followed by Parentheses
     * @param
     * @return a sub-expression or an answer
     */
    public String visitExpoPar(InfixParser.ExpoParContext ctx) {
        StringBuilder allChildren = new StringBuilder();  //A string that holds an expression made up of the subexpressions from this node's children
        String child1 = ctx.EXPO().getText() + " ";  //Get the exponent sign and store it with a space
        allChildren.append(child1);  //Add the exponent sign to the string
        for (int i = 2; i < ctx.getChildCount() - 1; i++) {
            String child = visit(ctx.getChild(i));  //Visit every child except the first, second and last because those hold the exponent sign and the parenthesis
            allChildren.append(child);  //Append the result to the string of subexpressions
        }

        String[] list = allChildren.toString().split(" ");  //Split the expression by its whitespace characters
        String[] copy = new String[list.length - 1];  //Create a new array to hold a portion of the expression
        System.arraycopy(list, 1, copy, 0, list.length - 1);  //Copy the expression minus the first element (the exponent sign)

        String result = "";
        if (copy.length > 2) {  //If the copied expression has more than two elements (example = 2 * 3), it can be evaluated
            float evaluation = this.evaluate(copy);  //Evaluate the copied expression
            result = "^ " + String.valueOf(evaluation) + " ";  //Add the exponent sign back to the expression
        } else {
            result = allChildren.toString();  //If the copied expression only has 2 elements (example = 1 +), return the original expression
        }
        return result;  //Return the answer or expression
    }

    /*
     * A node that holds an addition sign
     * @param
     * @return the addition sign
     */
    public String visitAddition(InfixParser.AdditionContext ctx) {
        return ctx.getText() + " ";  //Return the addition sign with a space
    }

    /*
     * A node that holds a subtraction sign
     * @param
     * @return the subtraction sign
     */
    public String visitSubtraction(InfixParser.SubtractionContext ctx) {
        return ctx.getText() + " ";  //Return the subtraction sign with a space
    }

    /*
     * A node that holds single numbers
     * @param
     * @return an individual number
     */
    public String visitSoloNum(InfixParser.SoloNumContext ctx) {
        return ctx.getText() + " ";  //Return the single number with a space
    }

    /*
     * A node that deals with Numbers followed by Operators
     * @param
     * @return an answer or a sub-expression
     */
    public String visitNumThenOp(InfixParser.NumThenOpContext ctx) {
        String result = "";
        String child1 = visit(ctx.getChild(0));  //Visit the first child and get its result
        String child2 = visit(ctx.getChild(1));  //Visit the second child and get its result
        String child = child1 + child2;  //Combine the results into a single expression

        String[] list = child.split(" ");  //Split the expression by its whitespace characters
        if (list.length > 2) {  //If the expression has more than two elements (example = 2 * 3), it can be evaluated
            Postfix postfix = new Postfix();
            if (postfix.isOperator(list[list.length - 1])) {  //If the last element in the expression is an operator
                //System.out.println("almost there!");
                String[] copy = new String[list.length - 1];  //Create a new array to hold a portion of the expression
                for (int i = 0; i < list.length - 1; i++) {
                    copy[i] = list[i];  //Copy the expression minus the last element (the operators)
                }
                if (copy.length > 2) {  //If the copied expression has more than two elements (example = 2 * 3), it can be evaluated
                    float evaluation = this.evaluate(copy);  //Evaluate the expression
                    result = String.valueOf(evaluation) + " " + list[list.length - 1] + " ";  //Add the operator back to the expression
                } else {
                    result = child;  //If the copied expression has less than 2 elements, return the original expression
                }
            } else {  ////If the last element in the expression is not an operator
                float evaluation = this.evaluate(list);  //Evaluate the expression
                result = String.valueOf(evaluation) + " ";  //Convert the answer to a string
            }
        } else {
            result = child;  //If the expression is only has 2 elements (example = 1 +), return it
        }
        return result;  //Return the result or expression
    }

    /*
     * A node that deals with Parentheses
     * @param
     * @return an answer or a sub-expression
     */
    public String visitParenthesis(InfixParser.ParenthesisContext ctx) {
        StringBuilder allChildren = new StringBuilder();  //A string that holds an expression made up of the sub-expressions from this node's children
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            String child = visit(ctx.getChild(i));  //Visit every child except the first and last because those hold the parenthesis
            allChildren.append(child);  //Append the result of each child to the string of sub-expressions
        }

        String result = "";
        String[] list = allChildren.toString().split(" ");  //Splits the expression by its whitespace characters
        if (list.length > 2) {  //If the expression has more than two elements (example = 2 * 3), it can be evaluated
            float evaluation = this.evaluate(list);  //Evaluate the expression
            result = String.valueOf(evaluation) + " ";  //Convert the answer to a string
        } else {
            result = allChildren.toString();  //If the expression is only has 2 elements (example = 1 +), return it
        }

        return result;  //Return the answer or expression
    }

    /*
     * The root node of the tree
     * @param
     * @return an answer of a sub-expression as a String
     */
    public String visitExpression(InfixParser.ExpressionContext ctx) {
        StringBuilder allChildren = new StringBuilder();  //A string that holds an expression made up of the children's sub-expressions
        for (int i = 0; i < ctx.getChildCount(); i++) {
            String child = visit(ctx.getChild(i));  //Visit each child
            allChildren.append(child);  //Append the result to the string of sub-expressions
        }

        String[] list = allChildren.toString().split(" ");  //Splits the expression by its whitespace characters
        float evaluation = this.evaluate(list);  //Evaluate the expression using the helper method
        return String.valueOf(evaluation);  //Return the answer as a string
    }
}
