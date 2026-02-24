/**
 * A collection of static utility methods for common mathematical operations.
 *
 * <p>This class is not meant to be instantiated — all methods are static.
 * It demonstrates proper Javadoc documentation style for a Java utility class.</p>
 *
 * @author Student
 * @version 1.0
 */
public class MathUtils {

    /**
     * Returns the sum of two integers.
     *
     * @param a the first integer operand
     * @param b the second integer operand
     * @return the sum of a and b
     */
    public static int add(int a, int b) {
        return a + b;
    }

    /**
     * Divides the numerator by the denominator and returns the result as a double.
     *
     * <p>This method performs floating-point division, so the result preserves
     * the decimal component (e.g., 7.0 / 2.0 returns 3.5).</p>
     *
     * @param numerator   the number to be divided
     * @param denominator the number to divide by; must not be zero
     * @return the quotient of numerator divided by denominator
     * @throws ArithmeticException if denominator is 0
     */
    public static double divide(double numerator, double denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return numerator / denominator;
    }

    /**
     * Determines whether a given integer is a prime number.
     *
     * <p>A prime number is a natural number greater than 1 that has no positive
     * divisors other than 1 and itself.</p>
     *
     * @param number the integer to test for primality; must be a positive integer
     * @return {@code true} if the number is prime; {@code false} otherwise
     */
    public static boolean isPrime(int number) {
        // Numbers less than 2 (including 0, 1, and all negatives) are not prime by definition
        if (number < 2) {
            return false;
        }

        // Only check divisors up to the square root of number — if number has a factor
        // larger than its square root, the corresponding smaller factor would already be found
        for (int i = 2; i <= Math.sqrt(number); i++) {
            // If number divides evenly by i, it has a factor other than 1 and itself — not prime
            if (number % i == 0) {
                return false;
            }
        }

        // No divisors found — the number is prime
        return true;
    }
}


class DocumentationDemo {

    public static void main(String[] args) {
        /*
         * This program demonstrates best practices for Java code documentation,
         * including single-line comments, multi-line block comments, and Javadoc
         * comments with @param, @return, and @throws tags on the MathUtils utility class.
         * Run this file to see the output of each MathUtils method invocation.
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
