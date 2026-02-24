// TODO: Add a Javadoc comment above the MathUtils class with:
//       - A one-sentence description of the class
//       - @author Student
//       - @version 1.0
public class MathUtils {

    // TODO: Add a Javadoc comment above this method with:
    //       - One-sentence description
    //       - @param a  (describe the parameter)
    //       - @param b  (describe the parameter)
    //       - @return   (describe what is returned)
    public static int add(int a, int b) {
        return a + b;
    }

    // TODO: Add a Javadoc comment above this method with:
    //       - One-sentence description
    //       - @param numerator    (describe the parameter)
    //       - @param denominator  (describe the parameter)
    //       - @return             (describe what is returned)
    //       - @throws ArithmeticException  (describe when it is thrown)
    public static double divide(double numerator, double denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return numerator / denominator;
    }

    // TODO: Add a Javadoc comment above this method with:
    //       - One-sentence description
    //       - @param number  (describe the parameter)
    //       - @return        (describe the boolean result)
    public static boolean isPrime(int number) {
        // TODO: Add a single-line comment here explaining what this edge case handles
        if (number < 2) {
            return false;
        }

        // TODO: Add a single-line comment here explaining the loop strategy
        for (int i = 2; i <= Math.sqrt(number); i++) {
            // TODO: Add a single-line comment here explaining what this check does
            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }
}


class DocumentationDemo {

    public static void main(String[] args) {
        /* TODO: Replace this comment with a multi-line block comment (/* ... */)
         * explaining in 2-3 sentences what this program demonstrates.
         * Mention: comments, Javadoc, and the MathUtils class.
         */

        System.out.println("=== MathUtils Demo ===");

        System.out.println("add(3, 7)             : " + MathUtils.add(3, 7));
        System.out.println("add(-5, 12)           : " + MathUtils.add(-5, 12));
        System.out.println("divide(10.0, 4.0)     : " + MathUtils.divide(10.0, 4.0));
        System.out.println("divide(7.0, 2.0)      : " + MathUtils.divide(7.0, 2.0));
        System.out.println("isPrime(2)            : " + MathUtils.isPrime(2));
        System.out.println("isPrime(7)            : " + MathUtils.isPrime(7));
        System.out.println("isPrime(9)            : " + MathUtils.isPrime(9));
        System.out.println("isPrime(1)            : " + MathUtils.isPrime(1));
    }
}
