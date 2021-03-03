import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PrologRunner {
    public static void main(String[] args) throws FileNotFoundException {
        File inputFile = new File("C:\\Users\\Dayo's XPS\\CS152\\project2\\prologInputs.txt");  //INSERT FILE HERE (an example file is provided)
        Scanner fileScanner = new Scanner(inputFile);  //Scan the input file
        PrologInterpreter interpreter = new PrologInterpreter();  //Create a new PrologInterpreter
        while (fileScanner.hasNextLine()) {  //While the file contains another line
            String input = fileScanner.nextLine();  //Get the next line from the scanner
            if (input.isBlank()) {  //If the input is blank
                continue;  //Skip the line
            } else {  //If the line isn't blank
                PrologHolder inputHolder = interpreter.evaluateInput(input);  //Evaluate the line (either a fact or rule)
                interpreter.resolve(inputHolder);  //Resolve the input (by adding it to the interpreter's program)
            }
        }
        fileScanner.close();  //Close the file scanner

        Scanner in = new Scanner(System.in);  //Create a new scanner to read inputs from the command line
        String prompt = "prolog> ";  //The prompt for the user
        System.out.print(prompt);  //Print the prompt for the user

        while (in.hasNextLine()) {
            String input = in.nextLine();  //Get the input from the user (a query)
            PrologHolder holder = interpreter.evaluateInput(input);  //Resolve the input
            PrologHolder answer = interpreter.resolve(holder);  //Get the answer to the input
            if ((holder.getType().equals("simple query") || holder.getType().equals("conjunctive query")) && !holder.isVariable()) {  //If the input is a simple or conjunctive ground query
                System.out.println(answer.getAtom());  //Print the answer to the query
                System.out.println("");  //Skip a line
            } else if (holder.getType().equals("simple query") && holder.isVariable()) {  //If the query is a simple, non-ground query
                if (answer.getType().equals("atom")) {  //If the answer to the query is a string
                    System.out.println(answer.getAtom());  //Print the answer to the query
                    System.out.println("");  //Skip a line
                } else {  //If the answer to the query is a list of substitutions
                    interpreter.printNonGroundQuery(holder, answer);  //Print the non-ground query
                    System.out.println("");  //Skip a line
                }
            } else if (holder.getType().equals("conjunctive query") && holder.isVariable()) {  //If the query is a conjunctive, non-ground query
                if (answer.getType().equals("atom")) {  //If the answer to the query is a string
                    System.out.println(answer.getAtom());  //Print the answer to the query
                    System.out.println("");  //Skip a line
                } else {  //If the answer to the query is a list of substitutions
                    interpreter.printNonGroundQuery(holder, answer);  //Print the-non ground query
                    System.out.println("");  //Skip a line
                }
            }
            System.out.print(prompt);  //Print the prompt for the user
        }
        in.close();  //Close the scanner
    }
}
