import java.io.FileReader;
import java.io.IOException;

public class ExceptionHierarchyDemo {

    // Unchecked — ArithmeticException propagates to caller naturally
    static int divide(int a, int b) {
        return a / b;  // throws ArithmeticException if b == 0
    }

    // Checked — compiler forces caller to handle IOException
    // FileReader throws FileNotFoundException (subclass of IOException) if file not found
    static void readMissingFile() throws IOException {
        new FileReader("does_not_exist.txt");
    }

    // Multi-catch: both exception types handled identically — no need to duplicate the catch body
    static void parseAndDivide(String numStr, int divisor) {
        try {
            int val = Integer.parseInt(numStr);  // throws NumberFormatException
            int result = 100 / divisor;           // throws ArithmeticException
            System.out.println("Result: " + result);
        } catch (NumberFormatException | ArithmeticException e) {
            System.out.println("Caught NumberFormatException or ArithmeticException: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        // ---- Part 1: ArithmeticException (unchecked / RuntimeException) ----
        System.out.println("=== Unchecked: ArithmeticException ===");
        try {
            divide(10, 0);
        } catch (ArithmeticException e) {
            System.out.println("Caught ArithmeticException: " + e.getMessage());
        }
        System.out.println();

        // ---- Part 2: NumberFormatException (unchecked / RuntimeException) ----
        System.out.println("=== Unchecked: NumberFormatException ===");
        try {
            Integer.parseInt("not-a-number");
        } catch (NumberFormatException e) {
            System.out.println("Caught NumberFormatException: " + e.getMessage());
        }
        System.out.println();

        // ---- Part 3: NullPointerException (unchecked / RuntimeException) ----
        System.out.println("=== Unchecked: NullPointerException ===");
        try {
            String s = null;
            s.length();  // NullPointerException — can't call methods on null
        } catch (NullPointerException e) {
            System.out.println("Caught NullPointerException: Cannot invoke method on null");
        }
        System.out.println();

        // ---- Part 4: IOException (CHECKED — must catch or declare throws) ----
        System.out.println("=== Checked: IOException ===");
        try {
            readMissingFile();
        } catch (IOException e) {
            // FileNotFoundException message typically: "filename (No such file or directory)"
            System.out.println("Caught IOException: " + e.getMessage());
        }
        System.out.println();

        // ---- Part 5: Multi-catch ----
        System.out.println("=== Multi-catch ===");
        System.out.print("parseAndDivide(\"abc\", 5): ");
        parseAndDivide("abc", 5);   // triggers NumberFormatException
        System.out.print("parseAndDivide(\"0\", 0): ");
        parseAndDivide("0", 0);     // triggers ArithmeticException
        System.out.println();

        // ---- Part 6: Catching by supertype ----
        System.out.println("=== Catching by Supertype ===");
        try {
            String[] arr = new String[2];
            String val = arr[5];  // ArrayIndexOutOfBoundsException is-a RuntimeException
        } catch (RuntimeException e) {
            // The reference type is RuntimeException, but the actual object is AIOOBE
            System.out.println("Caught as RuntimeException, actual type: " + e.getClass().getSimpleName());
        }
    }
}
