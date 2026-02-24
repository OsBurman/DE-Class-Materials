import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WildcardsDemo {

    // ============================================================
    // Upper bounded wildcard: <? extends Number>
    // The list is a PRODUCER — we only READ from it.
    // Accepts List<Integer>, List<Double>, List<Float>, etc.
    // ============================================================
    static double sumList(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) {
            // n is guaranteed to be a Number — doubleValue() is available on all Numbers
            sum += n.doubleValue();
        }
        return sum;
    }

    // ============================================================
    // Lower bounded wildcard: <? super Integer>
    // The list is a CONSUMER — we only WRITE Integers into it.
    // Accepts List<Integer>, List<Number>, List<Object>.
    // ============================================================
    static void addNumbers(List<? super Integer> list) {
        list.add(10);
        list.add(20);
        list.add(30);
    }

    // ============================================================
    // Bounded type parameter: <T extends Comparable<T>>
    // T is used in THREE places (value, min, max) — needs a name.
    // Wildcard would not work here because we need to refer to T.
    // ============================================================
    static <T extends Comparable<T>> T clamp(T value, T min, T max) {
        if (value.compareTo(min) < 0) return min;
        if (value.compareTo(max) > 0) return max;
        return value;
    }

    public static void main(String[] args) {

        // ---- Upper bounded wildcard ----
        System.out.println("=== Upper Bounded Wildcard: <? extends Number> ===");

        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("Sum of integers " + ints + ": " + sumList(ints));

        List<Double> doubles = Arrays.asList(1.5, 2.5, 3.0);
        System.out.println("Sum of doubles " + doubles + ": " + sumList(doubles));

        /*
         * WHY you cannot add to a List<? extends Number>:
         *
         * List<? extends Number> list = new ArrayList<Integer>();
         * list.add(3.14);  // COMPILE ERROR — could corrupt an Integer-only list!
         * list.add(42);    // COMPILE ERROR — same reason
         *
         * The compiler sees "some unknown subtype of Number" — it refuses to let
         * you add anything because the actual type could be more specific than
         * whatever you're trying to add.
         */

        System.out.println();

        // ---- Lower bounded wildcard ----
        System.out.println("=== Lower Bounded Wildcard: <? super Integer> ===");

        List<Integer> intList = new ArrayList<>();
        addNumbers(intList);
        System.out.println("List<Integer> after addNumbers: " + intList);

        List<Number> numList = new ArrayList<>();
        addNumbers(numList);     // Integer IS-A Number — safe to add Integer to List<Number>
        System.out.println("List<Number> after addNumbers: " + numList);

        System.out.println();

        // ---- PECS principle ----
        System.out.println("=== PECS Principle ===");
        System.out.println("Producer Extends: use <? extends T> when reading/consuming from the collection");
        System.out.println("Consumer Super:  use <? super T>  when writing/producing into the collection");

        System.out.println();

        // ---- Bounded type parameter ----
        System.out.println("=== Bounded Type Parameter: clamp() ===");
        System.out.println("clamp(15, 1, 10): " + clamp(15, 1, 10));
        System.out.println("clamp(5, 1, 10): " + clamp(5, 1, 10));
    }
}
