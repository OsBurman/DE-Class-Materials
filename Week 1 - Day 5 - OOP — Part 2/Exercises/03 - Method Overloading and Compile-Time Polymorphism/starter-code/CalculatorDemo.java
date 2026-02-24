class MathUtils {

    // TODO: Write add(int a, int b) — returns sum of two ints

    // TODO: Write add(int a, int b, int c) — returns sum of three ints
    //       This is the SAME method name — Java picks the right one based on argument count

    // TODO: Write add(double a, double b) — returns sum of two doubles
    //       This is the SAME method name — Java picks based on argument type

    // TODO: Write multiply(int a, int b) — returns product of two ints

    // TODO: Write multiply(double a, double b) — returns product of two doubles

    // TODO: Write describe(int n) — returns "Integer: [n]"

    // TODO: Write describe(double n) — returns "Double: [n]"

    // TODO: Write describe(String s) — returns "String: \"[s]\" (length [s.length()])"
    //       Example: describe("hello") → String: "hello" (length 5)
    //       Tip: to include a literal quote in a string, escape it: "\""

    // TODO: Write print(int value) — prints "Printing int: [value]"

    // TODO: Write print(String value) — prints "Printing String: [value]"
}

public class CalculatorDemo {
    public static void main(String[] args) {
        System.out.println("=== Method Overloading Demo ===\n");

        MathUtils math = new MathUtils();

        // TODO: Call add(3, 7) and print: "add(3, 7)       = " + result
        // TODO: Call add(3, 7, 2) and print: "add(3, 7, 2)    = " + result
        // TODO: Call add(1.5, 2.3) and print: "add(1.5, 2.3)   = " + result

        System.out.println();

        // TODO: Call multiply(4, 5) and print: "multiply(4, 5)     = " + result
        // TODO: Call multiply(2.5, 4.0) and print: "multiply(2.5, 4.0) = " + result

        System.out.println();

        // TODO: Call describe(42) and print: "describe(42)      → " + result
        // TODO: Call describe(3.14) and print: "describe(3.14)    → " + result
        // TODO: Call describe("hello") and print: "describe(\"hello\") → " + result

        System.out.println();

        // TODO: Call math.print(99)
        // TODO: Call math.print("world")
    }
}
