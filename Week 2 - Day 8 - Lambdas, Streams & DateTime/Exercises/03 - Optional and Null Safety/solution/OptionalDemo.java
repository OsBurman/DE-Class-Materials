import java.util.Optional;

public class OptionalDemo {

    static Optional<String> findUserById(int id) {
        if (id == 1) return Optional.of("Alice");
        if (id == 2) return Optional.of("Bob");
        return Optional.empty();  // no result — safer than returning null
    }

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Creating Optionals
        // ============================================================
        System.out.println("=== Creating Optionals ===");

        Optional<String> withValue   = Optional.of("Java");       // throws NPE if null passed
        Optional<String> nullable    = Optional.ofNullable(null);  // safe for null inputs
        Optional<String> emptyOpt    = Optional.empty();           // explicit empty

        System.out.println("Optional.of(\"Java\") present: " + withValue.isPresent());
        System.out.println("Optional.ofNullable(null) present: " + nullable.isPresent());
        System.out.println("Optional.empty() present: " + emptyOpt.isPresent());
        System.out.println();

        // ============================================================
        // PART 2: Retrieving values safely
        // ============================================================
        System.out.println("=== orElse / orElseGet / orElseThrow ===");

        Optional<String> present = Optional.of("Java");
        Optional<String> empty   = Optional.empty();

        // orElse: always evaluates the argument expression (even if not needed)
        System.out.println("orElse on present: " + present.orElse("DEFAULT"));
        System.out.println("orElse on empty: "   + empty.orElse("DEFAULT"));

        // orElseGet: lazily evaluates the Supplier — preferred for expensive defaults
        System.out.println("orElseGet on empty: " + empty.orElseGet(() -> "Generated default"));

        // orElseThrow: throws the provided exception if empty
        try {
            empty.orElseThrow(() -> new RuntimeException("No value present"));
        } catch (RuntimeException e) {
            System.out.println("orElseThrow caught: " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        // PART 3: map and filter
        // ============================================================
        System.out.println("=== map and filter ===");

        Optional<String> name = Optional.of("  alice  ");
        // map transforms the value inside — each map returns a new Optional
        // If the Optional were empty, map would return empty without calling the function
        name.map(String::trim)
            .map(String::toUpperCase)
            .ifPresent(s -> System.out.println("Trimmed + uppercased: " + s));

        Optional<String> code1 = Optional.of("PROMO50");
        Optional<String> code2 = Optional.of("SALE30");

        // filter: if predicate is true → same Optional; if false → empty
        Optional<String> filtered1 = code1.filter(s -> s.startsWith("PROMO"));
        Optional<String> filtered2 = code2.filter(s -> s.startsWith("PROMO"));

        System.out.println("Filter matches \"PROMO50\": " + filtered1.orElse("(empty)"));
        System.out.println("Filter rejects \"SALE30\": "  + filtered2.orElse("(empty)"));
        System.out.println();

        // ============================================================
        // PART 4: Realistic lookup
        // ============================================================
        System.out.println("=== Realistic lookup ===");

        // ifPresentOrElse (Java 9+): cleaner than isPresent() + get()
        findUserById(1).ifPresentOrElse(
            u -> System.out.println("Found: " + u),
            () -> System.out.println("User not found")
        );
        findUserById(2).ifPresentOrElse(
            u -> System.out.println("Found: " + u),
            () -> System.out.println("User not found")
        );
        findUserById(99).ifPresentOrElse(
            u -> System.out.println("Found: " + u),
            () -> System.out.println("User not found")
        );
    }
}
