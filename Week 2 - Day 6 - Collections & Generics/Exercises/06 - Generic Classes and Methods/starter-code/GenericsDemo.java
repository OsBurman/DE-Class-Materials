public class GenericsDemo {

    // ============================================================
    // TODO 1: Define a generic class Box<T>
    //   - private field: T value
    //   - constructor: Box(T value)
    //   - methods: getValue(), setValue(T), isEmpty(), toString()
    //     toString() returns "Box[value]" or "Box[empty]" if null
    // ============================================================


    // ============================================================
    // TODO 2: Define a generic class Pair<A, B>
    //   - private fields: A first, B second
    //   - constructor: Pair(A first, B second)
    //   - methods: getFirst(), getSecond(), toString() -> "(first, second)"
    // ============================================================


    // ============================================================
    // TODO 3: Define a generic static method max()
    //   Signature: static <T extends Comparable<T>> T max(T a, T b)
    //   Returns whichever of a or b is larger
    // ============================================================


    // ============================================================
    // MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("=== Generic Box<T> ===");

        // TODO: Create a Box<String> with "Hello, Generics!" and print it
        // TODO: Call setValue("Updated") and print the box again

        // TODO: Create a Box<Integer> with 42 and print it
        // TODO: Set the integer box value to null and print isEmpty()


        System.out.println();
        System.out.println("=== Generic Pair<A, B> ===");

        // TODO: Create a Pair<String, Integer> for student name "Alice" and age 21
        // TODO: Print the pair


        System.out.println();
        System.out.println("=== Generic Method max() ===");

        // TODO: Call max(10, 37) and print "max(10, 37): [result]"
        // TODO: Call max("Zebra", "Apple") and print "max(\"Zebra\", \"Apple\"): [result]"

    }
}
