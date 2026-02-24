import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WildcardsDemo {

    // ============================================================
    // TODO 1: Upper bounded wildcard — reads from a collection of Numbers
    //   static double sumList(List<? extends Number> list)
    //   Iterate and sum all elements; return as double
    // ============================================================


    // ============================================================
    // TODO 2: Lower bounded wildcard — writes Integers into a list
    //   static void addNumbers(List<? super Integer> list)
    //   Add the integers 10, 20, 30 to the list
    // ============================================================


    // ============================================================
    // TODO 3: Bounded type parameter — clamp a value to [min, max]
    //   static <T extends Comparable<T>> T clamp(T value, T min, T max)
    //   Return min if value < min, max if value > max, else value
    // ============================================================


    public static void main(String[] args) {

        // ---- Upper bounded wildcard ----
        System.out.println("=== Upper Bounded Wildcard: <? extends Number> ===");

        // TODO: Create List<Integer> with 1,2,3,4,5 — call sumList and print result
        // TODO: Create List<Double> with 1.5, 2.5, 3.0 — call sumList and print result

        /*
         * WHY you cannot add to a List<? extends Number>:
         *
         * List<? extends Number> list = new ArrayList<Integer>();
         * list.add(3.14);  // COMPILE ERROR — could corrupt an Integer-only list!
         * list.add(42);    // COMPILE ERROR — same reason
         *
         * The compiler only knows it's SOME subtype of Number, not which one.
         * Reading is safe (every element IS-A Number), but writing is not.
         */

        System.out.println();

        // ---- Lower bounded wildcard ----
        System.out.println("=== Lower Bounded Wildcard: <? super Integer> ===");

        // TODO: Create an empty List<Integer>, call addNumbers(list), print it
        // TODO: Create an empty List<Number>, call addNumbers(list), print it

        System.out.println();

        // ---- PECS principle ----
        System.out.println("=== PECS Principle ===");
        System.out.println("Producer Extends: use <? extends T> when reading/consuming from the collection");
        System.out.println("Consumer Super:  use <? super T>  when writing/producing into the collection");

        System.out.println();

        // ---- Bounded type parameter ----
        System.out.println("=== Bounded Type Parameter: clamp() ===");

        // TODO: Call clamp(15, 1, 10) and print "clamp(15, 1, 10): [result]"
        // TODO: Call clamp(5, 1, 10) and print "clamp(5, 1, 10): [result]"

    }
}
