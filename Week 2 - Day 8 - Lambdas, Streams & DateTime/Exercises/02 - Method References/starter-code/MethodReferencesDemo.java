import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MethodReferencesDemo {

    // ============================================================
    // TODO: Define a simple Person class with:
    //   - String name field
    //   - Constructor: Person(String name)
    //   - toString() returning "Person{name}"
    // ============================================================


    public static void main(String[] args) {

        // ============================================================
        // PART 1: Static method reference — ClassName::staticMethod
        // ============================================================
        System.out.println("=== Static Method Reference ===");

        // TODO: Create Function<String, Integer> using Integer::parseInt
        //       (replaces: s -> Integer.parseInt(s))
        // Function<String, Integer> parseInt = Integer::parseInt;

        // TODO: Apply to "42" and "100", print "Parsed: [result]"


        // TODO: Create Predicate<String> using Objects::isNull
        //       Test with null and "hello", print:
        //         "null isNull: [result]"
        //         "\"hello\" isNull: [result]"


        System.out.println();

        // ============================================================
        // PART 2: Instance method reference on a parameter — ClassName::instanceMethod
        // ============================================================
        System.out.println("=== Instance Method on Parameter ===");

        // TODO: Create Function<String, String> using String::toUpperCase
        //       Apply to "hello world" and print the result


        // TODO: Create Predicate<String> using String::isEmpty
        //       Test with "" and "java", print:
        //         "\"\" isEmpty: [result]"
        //         "\"java\" isEmpty: [result]"


        // TODO: Create a mutable List<String> with ["Zebra", "Mango", "Apple"]
        //       Sort it using list.sort(String::compareTo)
        //       Print "Sorted: " + list


        System.out.println();

        // ============================================================
        // PART 3: Instance method reference on a specific object — object::instanceMethod
        // ============================================================
        System.out.println("=== Instance Method on Specific Object ===");

        // TODO: Create String prefix = "Hello, "
        //       Create Function<String, String> using prefix::concat
        //       Apply to "Alice" and "Bob", print each result


        System.out.println();

        // ============================================================
        // PART 4: Constructor reference — ClassName::new
        // ============================================================
        System.out.println("=== Constructor Reference ===");

        // TODO: Create Function<String, Person> using Person::new
        //       Use it to create Person objects for "Carol" and "Dave"
        //       Print "Created person: " + person for each


        System.out.println();

        // ============================================================
        // PART 5: Method reference with forEach
        // ============================================================
        System.out.println("=== Method Reference with forEach ===");

        List<String> words = Arrays.asList("one", "two", "three", "four", "five");

        // TODO: Use words.forEach(System.out::println) to print each word
    }
}
