import java.util.Optional;

public class OptionalDemo {

    // TODO: Implement findUserById(int id) returning Optional<String>
    //   id == 1 → Optional.of("Alice")
    //   id == 2 → Optional.of("Bob")
    //   otherwise → Optional.empty()
    static Optional<String> findUserById(int id) {
        return Optional.empty(); // replace with real logic
    }

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Creating Optionals
        // ============================================================
        System.out.println("=== Creating Optionals ===");

        // TODO: Create Optional.of("Java"), Optional.ofNullable(null), Optional.empty()
        // TODO: Print isPresent() for each:
        //   "Optional.of(\"Java\") present: [result]"
        //   "Optional.ofNullable(null) present: [result]"
        //   "Optional.empty() present: [result]"


        System.out.println();

        // ============================================================
        // PART 2: Retrieving values safely
        // ============================================================
        System.out.println("=== orElse / orElseGet / orElseThrow ===");

        Optional<String> present = Optional.of("Java");
        Optional<String> empty = Optional.empty();

        // TODO: Print "orElse on present: " + present.orElse("DEFAULT")
        // TODO: Print "orElse on empty: "   + empty.orElse("DEFAULT")
        // TODO: Print "orElseGet on empty: " + empty.orElseGet(() -> "Generated default")
        // TODO: Try empty.orElseThrow(() -> new RuntimeException("No value present"))
        //       Catch the exception and print "orElseThrow caught: " + e.getMessage()


        System.out.println();

        // ============================================================
        // PART 3: map and filter
        // ============================================================
        System.out.println("=== map and filter ===");

        Optional<String> name = Optional.of("  alice  ");
        // TODO: Chain .map(String::trim).map(String::toUpperCase)
        //       Use .ifPresent(s -> System.out.println("Trimmed + uppercased: " + s))


        Optional<String> code1 = Optional.of("PROMO50");
        Optional<String> code2 = Optional.of("SALE30");

        // TODO: Apply .filter(s -> s.startsWith("PROMO")) to code1 and code2
        //       Print "Filter matches \"PROMO50\": " + result1.orElse("(empty)")
        //       Print "Filter rejects \"SALE30\": "  + result2.orElse("(empty)")


        System.out.println();

        // ============================================================
        // PART 4: Realistic lookup
        // ============================================================
        System.out.println("=== Realistic lookup ===");

        // TODO: Call findUserById(1), findUserById(2), findUserById(99)
        //       Use ifPresentOrElse() for each:
        //         present: System.out.println("Found: " + name)
        //         empty:   System.out.println("User not found")
    }
}
