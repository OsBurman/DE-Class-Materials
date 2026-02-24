public class GenericsDemo {

    // ============================================================
    // Generic class: Box<T>
    // T is a type parameter — resolved at compile time for each usage
    // ============================================================
    static class Box<T> {
        private T value;

        public Box(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        // isEmpty() checks for null — useful for optional/container patterns
        public boolean isEmpty() {
            return value == null;
        }

        @Override
        public String toString() {
            return isEmpty() ? "Box[empty]" : "Box[" + value + "]";
        }
    }

    // ============================================================
    // Generic class: Pair<A, B>
    // Two independent type parameters allow heterogeneous pairs
    // ============================================================
    static class Pair<A, B> {
        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() { return first; }
        public B getSecond() { return second; }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }

    // ============================================================
    // Generic static method: max()
    // <T extends Comparable<T>> — T must implement Comparable
    // This is an upper-bounded TYPE PARAMETER (not a wildcard)
    // ============================================================
    static <T extends Comparable<T>> T max(T a, T b) {
        // compareTo() returns positive if a > b
        return a.compareTo(b) >= 0 ? a : b;
    }

    // ============================================================
    // MAIN
    // ============================================================
    public static void main(String[] args) {

        System.out.println("=== Generic Box<T> ===");

        // Box<String> — type parameter T is bound to String at compile time
        Box<String> stringBox = new Box<>("Hello, Generics!");
        System.out.println("Box<String>: " + stringBox);

        stringBox.setValue("Updated");
        System.out.println("After setValue: " + stringBox);

        // Box<Integer> — same class, different type — no casting needed
        Box<Integer> intBox = new Box<>(42);
        System.out.println("Box<Integer>: " + intBox);

        intBox.setValue(null);
        System.out.println("Box is empty: " + intBox.isEmpty());

        System.out.println();
        System.out.println("=== Generic Pair<A, B> ===");

        // Pair<String, Integer> — A=String, B=Integer
        Pair<String, Integer> studentPair = new Pair<>("Alice", 21);
        System.out.println("Student pair: " + studentPair);

        System.out.println();
        System.out.println("=== Generic Method max() ===");

        // The compiler infers T=Integer from the arguments
        System.out.println("max(10, 37): " + max(10, 37));

        // The compiler infers T=String — String.compareTo() uses lexicographic order
        System.out.println("max(\"Zebra\", \"Apple\"): " + max("Zebra", "Apple"));
    }
}
