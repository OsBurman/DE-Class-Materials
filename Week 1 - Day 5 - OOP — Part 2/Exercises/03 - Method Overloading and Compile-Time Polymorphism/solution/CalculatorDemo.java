// MathUtils demonstrates method overloading — same name, different signatures
// The compiler resolves which overload to call based on argument count and types
class MathUtils {

    // add — overloaded for 2 ints, 3 ints, and 2 doubles
    public int add(int a, int b)              { return a + b; }
    public int add(int a, int b, int c)       { return a + b + c; }
    public double add(double a, double b)     { return a + b; }

    // multiply — overloaded for int and double pairs
    public int multiply(int a, int b)         { return a * b; }
    public double multiply(double a, double b){ return a * b; }

    // describe — overloaded for int, double, and String
    public String describe(int n)    { return "Integer: " + n; }
    public String describe(double n) { return "Double: " + n; }
    public String describe(String s) {
        // Embed literal quotes using escape sequence \"
        return "String: \"" + s + "\" (length " + s.length() + ")";
    }

    // print — overloaded for int and String
    public void print(int value)    { System.out.println("Printing int: " + value); }
    public void print(String value) { System.out.println("Printing String: " + value); }
}

public class CalculatorDemo {
    public static void main(String[] args) {
        System.out.println("=== Method Overloading Demo ===\n");

        MathUtils math = new MathUtils();

        // Compiler picks add(int, int) because both args are int literals
        System.out.println("add(3, 7)       = " + math.add(3, 7));
        // Compiler picks add(int, int, int) because three int args
        System.out.println("add(3, 7, 2)    = " + math.add(3, 7, 2));
        // Compiler picks add(double, double) because args have decimal points
        System.out.println("add(1.5, 2.3)   = " + math.add(1.5, 2.3));

        System.out.println();

        System.out.println("multiply(4, 5)     = " + math.multiply(4, 5));
        System.out.println("multiply(2.5, 4.0) = " + math.multiply(2.5, 4.0));

        System.out.println();

        // Compiler picks describe(int) for 42, describe(double) for 3.14, describe(String) for "hello"
        System.out.println("describe(42)      → " + math.describe(42));
        System.out.println("describe(3.14)    → " + math.describe(3.14));
        System.out.println("describe(\"hello\") → " + math.describe("hello"));

        System.out.println();

        math.print(99);       // resolves to print(int)
        math.print("world");  // resolves to print(String)
    }
}
