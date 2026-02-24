import java.io.FileReader;
import java.io.IOException;

public class ExceptionHierarchyDemo {

    // TODO: Implement divide(int a, int b) — simply return a / b
    //       (No try-catch here — let the caller handle the ArithmeticException)
    static int divide(int a, int b) {
        return 0; // replace this
    }

    // TODO: Implement readMissingFile() — create a new FileReader("does_not_exist.txt")
    //       This is a checked exception — the method signature must declare: throws IOException
    static void readMissingFile() throws IOException {
        // TODO: new FileReader("does_not_exist.txt")
    }

    // TODO: Implement parseAndDivide(String numStr, int divisor)
    //       Parse numStr to int with Integer.parseInt(), then return 100 / divisor
    //       Use a single multi-catch block for NumberFormatException | ArithmeticException
    static void parseAndDivide(String numStr, int divisor) {
        // TODO: try { parse and divide } catch (NumberFormatException | ArithmeticException e) { ... }
        //       Print: "Caught NumberFormatException or ArithmeticException: " + e.getMessage()
    }

    public static void main(String[] args) {

        // ---- Part 1: ArithmeticException ----
        System.out.println("=== Unchecked: ArithmeticException ===");
        // TODO: Call divide(10, 0) inside a try block
        //       Catch ArithmeticException and print: "Caught ArithmeticException: " + e.getMessage()
        System.out.println();

        // ---- Part 2: NumberFormatException ----
        System.out.println("=== Unchecked: NumberFormatException ===");
        // TODO: Call Integer.parseInt("not-a-number") inside a try block
        //       Catch NumberFormatException and print: "Caught NumberFormatException: " + e.getMessage()
        System.out.println();

        // ---- Part 3: NullPointerException ----
        System.out.println("=== Unchecked: NullPointerException ===");
        // TODO: Declare String s = null; call s.length() inside a try block
        //       Catch NullPointerException and print: "Caught NullPointerException: Cannot invoke method on null"
        System.out.println();

        // ---- Part 4: Checked IOException ----
        System.out.println("=== Checked: IOException ===");
        // TODO: Call readMissingFile() inside a try block
        //       Catch IOException and print: "Caught IOException: " + e.getMessage()
        System.out.println();

        // ---- Part 5: Multi-catch ----
        System.out.println("=== Multi-catch ===");
        // TODO: Call parseAndDivide("abc", 5) and print the label before calling it
        // TODO: Call parseAndDivide("0", 0) and print the label before calling it
        System.out.println();

        // ---- Part 6: Catching by supertype ----
        System.out.println("=== Catching by Supertype ===");
        // TODO: Access index 5 of a String array with only 2 elements
        //       Catch as RuntimeException (not ArrayIndexOutOfBoundsException)
        //       Print: "Caught as RuntimeException, actual type: " + e.getClass().getSimpleName()
    }
}
