import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class MethodReferencesDemo {

    // Simple value class to demonstrate constructor references
    static class Person {
        private final String name;
        public Person(String name) { this.name = name; }
        @Override public String toString() { return "Person{" + name + "}"; }
    }

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Static method reference — ClassName::staticMethod
        // Equivalent lambda: s -> Integer.parseInt(s)
        // ============================================================
        System.out.println("=== Static Method Reference ===");

        Function<String, Integer> parseInt = Integer::parseInt;
        System.out.println("Parsed: " + parseInt.apply("42"));
        System.out.println("Parsed: " + parseInt.apply("100"));

        // Objects.isNull(obj) is a static method — fits Predicate<Object> signature perfectly
        Predicate<String> isNull = Objects::isNull;
        System.out.println("null isNull: " + isNull.test(null));
        System.out.println("\"hello\" isNull: " + isNull.test("hello"));
        System.out.println();

        // ============================================================
        // PART 2: Instance method reference on a parameter — ClassName::instanceMethod
        // The first parameter becomes the object the method is called on
        // Equivalent lambda: s -> s.toUpperCase()
        // ============================================================
        System.out.println("=== Instance Method on Parameter ===");

        Function<String, String> toUpper = String::toUpperCase;
        System.out.println(toUpper.apply("hello world"));

        Predicate<String> isEmpty = String::isEmpty;
        System.out.println("\"\" isEmpty: " + isEmpty.test(""));
        System.out.println("\"java\" isEmpty: " + isEmpty.test("java"));

        // String::compareTo satisfies Comparator<String> — compare(s1, s2) → s1.compareTo(s2)
        List<String> animals = new ArrayList<>(Arrays.asList("Zebra", "Mango", "Apple"));
        animals.sort(String::compareTo);
        System.out.println("Sorted: " + animals);
        System.out.println();

        // ============================================================
        // PART 3: Instance method reference on a specific object
        // The captured object (prefix) is fixed — equivalent to: s -> prefix.concat(s)
        // ============================================================
        System.out.println("=== Instance Method on Specific Object ===");

        String prefix = "Hello, ";
        Function<String, String> greet = prefix::concat;
        System.out.println(greet.apply("Alice"));
        System.out.println(greet.apply("Bob"));
        System.out.println();

        // ============================================================
        // PART 4: Constructor reference — ClassName::new
        // Equivalent lambda: name -> new Person(name)
        // ============================================================
        System.out.println("=== Constructor Reference ===");

        Function<String, Person> makePerson = Person::new;
        Person carol = makePerson.apply("Carol");
        Person dave  = makePerson.apply("Dave");
        System.out.println("Created person: " + carol);
        System.out.println("Created person: " + dave);
        System.out.println();

        // ============================================================
        // PART 5: Method reference with forEach
        // System.out::println is an instance method ref on the specific System.out object
        // ============================================================
        System.out.println("=== Method Reference with forEach ===");

        List<String> words = Arrays.asList("one", "two", "three", "four", "five");
        words.forEach(System.out::println);
    }
}
