import java.util.*;
import java.util.function.*;

/**
 * DAY 6 — Collections & Generics
 * Part 2, File 1: Generics — Syntax, Use Cases, Bounded Types & Wildcards
 *
 * Topics covered:
 *   - Why generics exist (type safety, no casting)
 *   - Generic classes (single and multiple type parameters)
 *   - Generic methods
 *   - Bounded type parameters: extends (upper bound)
 *   - Wildcards: ? (unbounded), ? extends T (upper bounded), ? super T (lower bounded)
 *   - Type erasure — what happens at runtime
 *   - The PECS rule (Producer Extends, Consumer Super)
 */
public class GenericsSyntaxAndUseCases {

    // =========================================================================
    // SECTION 1: WHY GENERICS? — THE PRE-GENERICS PROBLEM
    // =========================================================================
    // Before Java 5, collections held Object. You had to cast on every read,
    // and wrong casts caused runtime ClassCastExceptions — not compile errors.
    // =========================================================================

    // Pre-generics style (DO NOT USE in real code — demonstration only)
    static void preGenericsProblems() {
        List rawList = new ArrayList();  // no type parameter = raw type
        rawList.add("hello");
        rawList.add(42);             // compiler allows anything
        rawList.add(3.14);

        // Every read requires a cast — and can crash at runtime:
        String s = (String) rawList.get(0);  // OK
        // String bad = (String) rawList.get(1); // ClassCastException at RUNTIME — not caught at compile time!
        System.out.println("Pre-generics: compile allows mixing types → runtime crashes");

        // With generics — the mistake is caught at COMPILE TIME:
        List<String> safeList = new ArrayList<>();
        safeList.add("hello");
        // safeList.add(42);  // COMPILE ERROR: required String, found int
        String safe = safeList.get(0);   // NO CAST NEEDED — already String
        System.out.println("With generics: type mismatch caught at compile time: " + safe);
    }

    // =========================================================================
    // SECTION 2: GENERIC CLASSES
    // =========================================================================
    // Syntax: class ClassName<T> { ... }
    // T is a type parameter — a placeholder for the actual type.
    // Common naming conventions:
    //   T = Type (generic)
    //   E = Element (used in collections)
    //   K = Key, V = Value (used in maps)
    //   N = Number
    //   R = Return type
    // =========================================================================

    // A generic Pair class — holds any two values of potentially different types
    static class Pair<A, B> {
        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst()  { return first; }
        public B getSecond() { return second; }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }

        // Generic method inside a generic class — can use class type params
        public Pair<B, A> swap() {
            return new Pair<>(second, first);
        }
    }

    // A generic Result<T> — wraps a value or an error message
    // (simplified version of Optional — shows real-world generic class design)
    static class Result<T> {
        private final T value;
        private final String errorMessage;
        private final boolean success;

        private Result(T value, String errorMessage, boolean success) {
            this.value = value;
            this.errorMessage = errorMessage;
            this.success = success;
        }

        // Factory methods
        public static <T> Result<T> success(T value) {
            return new Result<>(value, null, true);
        }

        public static <T> Result<T> failure(String message) {
            return new Result<>(null, message, false);
        }

        public boolean isSuccess()     { return success; }
        public T getValue()            { return value; }
        public String getError()       { return errorMessage; }

        @Override
        public String toString() {
            return success ? "Success(" + value + ")" : "Failure(" + errorMessage + ")";
        }
    }

    // A generic Stack<T>
    static class GenericStack<T> {
        private final List<T> elements = new ArrayList<>();

        public void push(T item)   { elements.add(item); }
        public T pop() {
            if (isEmpty()) throw new EmptyStackException();
            return elements.remove(elements.size() - 1);
        }
        public T peek() {
            if (isEmpty()) throw new EmptyStackException();
            return elements.get(elements.size() - 1);
        }
        public boolean isEmpty()   { return elements.isEmpty(); }
        public int size()          { return elements.size(); }

        @Override
        public String toString()   { return elements.toString(); }
    }

    // =========================================================================
    // SECTION 3: GENERIC METHODS
    // =========================================================================
    // A generic method declares its own type parameters independent of any
    // class-level type parameters.
    // Syntax: <T> ReturnType methodName(T param, ...) { ... }
    // =========================================================================

    // Generic method: swap two elements in a List by index
    static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    // Generic method: fill a List with n copies of a value
    static <T> List<T> repeat(T value, int times) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            result.add(value);
        }
        return result;
    }

    // Generic method: find first matching element using a predicate
    static <T> Optional<T> findFirst(List<T> list, Predicate<T> condition) {
        for (T item : list) {
            if (condition.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    // Generic method: convert a List to a formatted string
    static <T> String joinWith(List<T> items, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i));
            if (i < items.size() - 1) sb.append(separator);
        }
        return sb.toString();
    }

    // =========================================================================
    // SECTION 4: BOUNDED TYPE PARAMETERS — upper bound with extends
    // =========================================================================
    // Syntax: <T extends SomeType>
    // Means: T must be SomeType OR a subclass/implementation of SomeType.
    // Lets you call methods defined on SomeType on the T parameter.
    //
    // Works with both classes AND interfaces:
    //   <T extends Number>     — T must be Number or subclass
    //   <T extends Comparable<T>> — T must implement Comparable
    //   <T extends Animal & Serializable> — multiple bounds with &
    // =========================================================================

    // Without bound: can't call .doubleValue() on T — compiler doesn't know T has that method
    // With <T extends Number>: we CAN call any Number method
    static <T extends Number> double sumList(List<T> numbers) {
        double total = 0;
        for (T num : numbers) {
            total += num.doubleValue();   // doubleValue() is defined on Number
        }
        return total;
    }

    // Find max in any list where elements can compare themselves
    static <T extends Comparable<T>> T findMax(List<T> items) {
        if (items.isEmpty()) throw new IllegalArgumentException("List is empty");
        T max = items.get(0);
        for (T item : items) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }

    // Multiple bounds: T must be Number AND implement Comparable
    static <T extends Number & Comparable<T>> T clamp(T value, T min, T max) {
        if (value.compareTo(min) < 0) return min;
        if (value.compareTo(max) > 0) return max;
        return value;
    }

    // =========================================================================
    // SECTION 5: WILDCARDS
    // =========================================================================
    // Wildcards (?) represent an UNKNOWN type. Unlike <T>, wildcards are used
    // in VARIABLE TYPES (method parameters, return types) — not in class/method
    // type parameter declarations.
    //
    // Three flavors:
    //   List<?>              — unbounded: any type; can only read as Object
    //   List<? extends T>    — upper bounded: T or any subtype; read-safe
    //   List<? super T>      — lower bounded: T or any supertype; write-safe
    //
    // PECS Rule: Producer Extends, Consumer Super
    //   - If a parameter PRODUCES (you read FROM it) → use ? extends T
    //   - If a parameter CONSUMES (you write TO it)  → use ? super T
    // =========================================================================

    // --- Unbounded wildcard: ? ---
    // Accepts ANY List — but you can only treat elements as Object
    static void printList(List<?> list) {
        System.out.print("  [");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i));
            if (i < list.size() - 1) System.out.print(", ");
        }
        System.out.println("]");
        // list.add("something");   // COMPILE ERROR — can't add to List<?> (type unknown)
    }

    // --- Upper-bounded wildcard: ? extends T ---
    // "Give me a list of Numbers or any subtype (Integer, Double, etc.)"
    // PRODUCER pattern: you read numbers FROM this list
    static double sumNumbers(List<? extends Number> numbers) {
        double total = 0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
        // numbers.add(3.14);  // COMPILE ERROR — can't add; don't know exact type
    }

    // --- Lower-bounded wildcard: ? super T ---
    // "Give me a list that can hold Integers — Integer or any supertype"
    // CONSUMER pattern: you write integers INTO this list
    static void addNumbers(List<? super Integer> list, int count) {
        for (int i = 1; i <= count; i++) {
            list.add(i);   // safe — list can hold Integer or anything above it
        }
    }

    // =========================================================================
    // SECTION 6: TYPE ERASURE
    // =========================================================================
    // At runtime, Java REMOVES (erases) generic type information.
    // List<String> and List<Integer> are both just "List" at runtime.
    // This preserves backward compatibility with pre-generics code.
    //
    // Practical implications:
    //   - Cannot do: new T()  or  new T[]  or  T.class
    //   - Cannot do: instanceof List<String>  (can only do instanceof List<?>)
    //   - Generic type is a compile-time check only
    // =========================================================================
    static void demonstrateTypeErasure() {
        List<String> strings = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();

        // At runtime, both are just ArrayList — getClass() returns the same thing
        System.out.println("List<String>.getClass() == List<Integer>.getClass(): "
            + (strings.getClass() == integers.getClass()));  // true

        // Cannot check generic type at runtime:
        Object obj = strings;
        System.out.println("instanceof List<?>:  " + (obj instanceof List<?>));    // OK
        // System.out.println(obj instanceof List<String>); // COMPILE ERROR — can't check generic type

        System.out.println("Type erasure: both are '" + strings.getClass().getSimpleName()
            + "' at runtime — no generic info survives");
    }

    // =========================================================================
    // MAIN
    // =========================================================================
    public static void main(String[] args) {

        System.out.println("=== SECTION 1: PRE-GENERICS PROBLEM ===");
        preGenericsProblems();

        System.out.println("\n=== SECTION 2: GENERIC CLASSES ===");

        // Pair<A, B>
        Pair<String, Integer> person = new Pair<>("Alice", 30);
        System.out.println("Pair: " + person);
        System.out.println("Name: " + person.getFirst() + ", Age: " + person.getSecond());
        System.out.println("Swapped: " + person.swap());

        Pair<String, String> coordinates = new Pair<>("40.7128° N", "74.0060° W");
        System.out.println("NYC coordinates: " + coordinates);

        // Result<T>
        Result<Integer> goodResult = Result.success(42);
        Result<Integer> badResult  = Result.failure("Value must be positive");
        System.out.println("\nResult.success: " + goodResult);
        System.out.println("Result.failure: " + badResult);
        if (goodResult.isSuccess()) {
            System.out.println("Value: " + goodResult.getValue());
        }

        // GenericStack<T>
        GenericStack<String> stack = new GenericStack<>();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        System.out.println("\nStack: " + stack);
        System.out.println("Pop: " + stack.pop());
        System.out.println("Peek: " + stack.peek());

        System.out.println("\n=== SECTION 3: GENERIC METHODS ===");
        List<String> letters = new ArrayList<>(List.of("A", "B", "C", "D", "E"));
        System.out.println("Before swap(0,4): " + letters);
        swap(letters, 0, 4);
        System.out.println("After swap(0,4):  " + letters);

        List<String> repeated = repeat("hello", 4);
        System.out.println("repeat('hello', 4): " + repeated);

        List<Integer> numbers = List.of(3, 7, 2, 9, 1, 5, 8);
        Optional<Integer> firstBig = findFirst(numbers, n -> n > 6);
        System.out.println("First number > 6: " + firstBig.orElse(-1));

        System.out.println("joinWith: " + joinWith(List.of("Java", "Python", "Go"), " | "));

        System.out.println("\n=== SECTION 4: BOUNDED TYPE PARAMETERS ===");

        List<Integer> ints    = List.of(1, 2, 3, 4, 5);
        List<Double>  doubles = List.of(1.1, 2.2, 3.3);
        List<Long>    longs   = List.of(100L, 200L, 300L);

        System.out.println("sumList(ints):    " + sumList(ints));
        System.out.println("sumList(doubles): " + sumList(doubles));
        System.out.println("sumList(longs):   " + sumList(longs));
        // sumList(List.of("a","b")); // COMPILE ERROR — String not a Number

        System.out.println("findMax([3,7,2,9,1]): " + findMax(List.of(3, 7, 2, 9, 1)));
        System.out.println("findMax(['banana','apple','cherry']): "
            + findMax(List.of("banana", "apple", "cherry")));

        System.out.println("clamp(15, 1, 10): " + clamp(15, 1, 10));  // 10
        System.out.println("clamp(5,  1, 10): " + clamp(5,  1, 10));  // 5
        System.out.println("clamp(-3, 1, 10): " + clamp(-3, 1, 10));  // 1

        System.out.println("\n=== SECTION 5: WILDCARDS ===");

        // Unbounded ?
        System.out.print("printList(strings): ");
        printList(List.of("cat", "dog", "bird"));
        System.out.print("printList(ints):    ");
        printList(ints);

        // Upper bounded ? extends Number
        List<Integer>  intList    = List.of(10, 20, 30);
        List<Double>   doubleList = List.of(1.5, 2.5, 3.5);
        List<Long>     longList   = List.of(1000L, 2000L);
        System.out.println("sumNumbers(intList):    " + sumNumbers(intList));
        System.out.println("sumNumbers(doubleList): " + sumNumbers(doubleList));
        System.out.println("sumNumbers(longList):   " + sumNumbers(longList));

        // Lower bounded ? super Integer
        List<Number> numList = new ArrayList<>();
        addNumbers(numList, 5);           // Integer is a Number — valid
        System.out.println("addNumbers into List<Number>: " + numList);

        List<Object> objList = new ArrayList<>();
        addNumbers(objList, 3);           // Integer is an Object — valid
        System.out.println("addNumbers into List<Object>: " + objList);

        // PECS summary
        System.out.println("\n-- PECS Rule --");
        System.out.println("Producer (you read FROM it): List<? extends T>");
        System.out.println("Consumer (you write TO it):  List<? super T>");
        System.out.println("Read-only / mixed:           List<?>            ");

        System.out.println("\n=== SECTION 6: TYPE ERASURE ===");
        demonstrateTypeErasure();
    }
}
