import java.util.*;
import java.util.function.*;

/**
 * DAY 8 — PART 1 | Optional
 * ──────────────────────────────────────────────────────────────
 * Optional<T> is a container object that may or may not hold a non-null value.
 * It was introduced in Java 8 to reduce NullPointerExceptions and to make
 * "might not have a value" explicit in method signatures.
 *
 * KEY PRINCIPLE: Optional is NOT just a null check wrapper.
 *   ✅ Use Optional as a method RETURN TYPE when absence is a normal outcome.
 *   ❌ Don't use Optional as a field type, parameter type, or in collections.
 *
 * CREATION:
 *   Optional.of(value)          — value MUST be non-null (throws NPE otherwise)
 *   Optional.ofNullable(value)  — safe: wraps value or empty if null
 *   Optional.empty()            — explicitly an absent value
 *
 * RETRIEVAL:
 *   opt.get()                   — gets value; throws NoSuchElementException if empty
 *   opt.orElse(default)         — returns value or a default (default ALWAYS evaluated)
 *   opt.orElseGet(Supplier)     — returns value or supplier result (lazy — preferred)
 *   opt.orElseThrow(Supplier)   — returns value or throws a custom exception
 *
 * INSPECTION:
 *   opt.isPresent()             — true if has value
 *   opt.isEmpty()               — true if empty (Java 11+)
 *
 * TRANSFORMATION:
 *   opt.map(Function)           — transforms the value if present
 *   opt.flatMap(Function)       — like map, but Function returns Optional<R>
 *   opt.filter(Predicate)       — keeps value only if predicate holds
 *
 * SIDE EFFECTS:
 *   opt.ifPresent(Consumer)     — runs consumer if present
 *   opt.ifPresentOrElse(Consumer, Runnable)  — Java 9+
 */
public class OptionalClass {

    public static void main(String[] args) {
        demonstrateCreation();
        demonstrateRetrieval();
        demonstrateInspection();
        demonstrateTransformation();
        demonstrateSideEffects();
        demonstrateRealWorldPatterns();
        demonstrateAntiPatterns();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Creating Optional Values
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCreation() {
        System.out.println("=== Creating Optional Values ===");

        // Optional.of — use when you KNOW the value is non-null
        Optional<String> name = Optional.of("Alice");
        System.out.println("Optional.of:         " + name);           // Optional[Alice]

        // Optional.ofNullable — use when the value MIGHT be null
        String nullableValue = null;
        Optional<String> maybeNull = Optional.ofNullable(nullableValue);
        System.out.println("ofNullable(null):    " + maybeNull);      // Optional.empty

        String nonNullValue = "Bob";
        Optional<String> maybePresent = Optional.ofNullable(nonNullValue);
        System.out.println("ofNullable(\"Bob\"):  " + maybePresent);  // Optional[Bob]

        // Optional.empty — explicitly absent
        Optional<String> absent = Optional.empty();
        System.out.println("Optional.empty():    " + absent);         // Optional.empty

        // ⚠️ Optional.of(null) throws NullPointerException immediately
        try {
            Optional<String> boom = Optional.of(null);  // throws NPE here
        } catch (NullPointerException e) {
            System.out.println("Optional.of(null) → NPE as expected");
        }

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Retrieving Values
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateRetrieval() {
        System.out.println("=== Retrieving Values ===");

        Optional<String> present = Optional.of("Hello");
        Optional<String> empty   = Optional.empty();

        // ----- 2a. get() — only use when you are CERTAIN it's present -----
        System.out.println("get() on present: " + present.get());   // Hello
        try {
            String value = empty.get();  // ⚠️ throws NoSuchElementException!
        } catch (NoSuchElementException e) {
            System.out.println("get() on empty → NoSuchElementException");
        }

        // ----- 2b. orElse(default) — always evaluates the default -----
        String result1 = present.orElse("default");   // "Hello"   (default not used)
        String result2 = empty.orElse("default");     // "default"
        System.out.println("present.orElse: " + result1);
        System.out.println("empty.orElse:   " + result2);

        // ⚠️ WATCH OUT — orElse evaluates its argument EAGERLY (even if value is present)
        // This means an expensive operation is always called:
        System.out.println("-- orElse vs orElseGet eagerness --");
        Optional<String> opt = Optional.of("exists");
        String r1 = opt.orElse(computeExpensiveDefault());      // computeExpensiveDefault() RUNS
        String r2 = opt.orElseGet(() -> computeExpensiveDefault()); // does NOT run (lazy)
        System.out.println("r1=" + r1 + "  r2=" + r2);  // both "exists"

        // ----- 2c. orElseGet(Supplier) — lazy, preferred for expensive defaults -----
        String result3 = present.orElseGet(() -> "computed default");
        String result4 = empty.orElseGet(() -> "computed default");
        System.out.println("present.orElseGet: " + result3);
        System.out.println("empty.orElseGet:   " + result4);

        // ----- 2d. orElseThrow — throw a meaningful exception on absence -----
        try {
            String val = present.orElseThrow(() ->
                    new IllegalStateException("Value was expected but not found"));
            System.out.println("orElseThrow on present: " + val);  // Hello
        } catch (IllegalStateException e) {
            System.out.println("Should not reach here");
        }

        try {
            String val = empty.orElseThrow(() ->
                    new IllegalArgumentException("User not found in the database"));
        } catch (IllegalArgumentException e) {
            System.out.println("orElseThrow on empty → " + e.getMessage());
        }

        System.out.println();
    }

    static String computeExpensiveDefault() {
        // Imagine a database call here
        System.out.println("  [computeExpensiveDefault() called!]");
        return "expensive-default";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Inspection
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateInspection() {
        System.out.println("=== Inspection ===");

        Optional<String> present = Optional.of("Hello");
        Optional<String> empty   = Optional.empty();

        // isPresent / isEmpty
        System.out.println("present.isPresent(): " + present.isPresent());  // true
        System.out.println("empty.isPresent():   " + empty.isPresent());    // false
        System.out.println("present.isEmpty():   " + present.isEmpty());    // false (Java 11+)
        System.out.println("empty.isEmpty():     " + empty.isEmpty());      // true  (Java 11+)

        // ⚠️ ANTI-PATTERN: Don't use isPresent() + get() — defeats the purpose of Optional
        // BAD:
        if (present.isPresent()) {
            System.out.println("BAD style (but it works): " + present.get());
        }
        // GOOD: Use orElse / map / ifPresent instead

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Transformation: map, flatMap, filter
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateTransformation() {
        System.out.println("=== Transformation ===");

        // ----- 4a. map(Function) — transforms the value; result is still Optional -----
        Optional<String> name = Optional.of("  alice  ");
        Optional<String> trimmedUpper = name
                .map(String::trim)
                .map(String::toUpperCase);
        System.out.println("map result: " + trimmedUpper);  // Optional[ALICE]

        // If the Optional is empty, map skips transformation and stays empty
        Optional<String> empty = Optional.empty();
        Optional<Integer> emptyLength = empty.map(String::length);
        System.out.println("map on empty: " + emptyLength);  // Optional.empty

        // Real use: get user's city name safely
        Optional<User> user = findUserById(1);
        Optional<String> city = user
                .map(User::getAddress)      // Optional<Address>? No — map expects T -> R
                .map(Address::getCity);
        // ⚠️ This only works if getAddress() returns Address (not Optional<Address>)
        System.out.println("City (map chain): " + city);  // Optional[Springfield]

        // ----- 4b. flatMap — when the mapper itself returns Optional<R> -----
        // Use flatMap when the mapping function returns Optional<T> (avoids Optional<Optional<T>>)
        Optional<User> user2 = findUserById(1);
        Optional<String> zipCode = user2
                .flatMap(OptionalClass::getAddressOptional)  // returns Optional<Address>
                .map(Address::getZip);
        System.out.println("ZipCode (flatMap): " + zipCode);  // Optional[62701]

        Optional<User> missingUser = findUserById(999);
        Optional<String> missingZip = missingUser
                .flatMap(OptionalClass::getAddressOptional)
                .map(Address::getZip);
        System.out.println("Missing zip:       " + missingZip);  // Optional.empty

        // ----- 4c. filter — keeps the value only if predicate is satisfied -----
        Optional<Integer> number = Optional.of(42);

        Optional<Integer> evenNumber = number.filter(n -> n % 2 == 0);
        System.out.println("filter (42 even?): " + evenNumber);  // Optional[42]

        Optional<Integer> oddNumber = Optional.of(7).filter(n -> n % 2 == 0);
        System.out.println("filter (7 even?):  " + oddNumber);   // Optional.empty

        // Real use: validate string format
        Optional<String> validCode = Optional.of("ABC-123")
                .filter(s -> s.matches("[A-Z]{3}-\\d{3}"));
        System.out.println("Valid code: " + validCode);           // Optional[ABC-123]

        Optional<String> invalidCode = Optional.of("bad-code")
                .filter(s -> s.matches("[A-Z]{3}-\\d{3}"));
        System.out.println("Invalid code: " + invalidCode);       // Optional.empty

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Side Effects: ifPresent, ifPresentOrElse
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSideEffects() {
        System.out.println("=== Side Effects ===");

        Optional<String> present = Optional.of("Hello");
        Optional<String> empty   = Optional.empty();

        // ----- 5a. ifPresent(Consumer) — runs only if value present -----
        present.ifPresent(v -> System.out.println("ifPresent: " + v));   // Hello
        empty.ifPresent(v -> System.out.println("This won't print"));    // skipped

        // Equivalent lambda or method reference:
        present.ifPresent(System.out::println);   // Hello

        // ----- 5b. ifPresentOrElse(Consumer, Runnable) — Java 9+ -----
        present.ifPresentOrElse(
                v -> System.out.println("Present: " + v),         // consumer for present
                () -> System.out.println("No value found")        // runnable for absent
        );

        empty.ifPresentOrElse(
                v -> System.out.println("Present: " + v),
                () -> System.out.println("No value found — running fallback")
        );

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — Real-World Patterns
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateRealWorldPatterns() {
        System.out.println("=== Real-World Patterns ===");

        // ----- Pattern 1: Repository lookup — return Optional from "find" methods -----
        // findUserById returns Optional<User> to signal "might not exist"
        Optional<User> foundUser    = findUserById(1);
        Optional<User> missingUser  = findUserById(999);

        String displayName = foundUser
                .map(User::getName)
                .map(n -> "Welcome, " + n + "!")
                .orElse("Welcome, Guest!");
        System.out.println(displayName);  // Welcome, Alice!

        String missingDisplay = missingUser
                .map(User::getName)
                .map(n -> "Welcome, " + n + "!")
                .orElse("Welcome, Guest!");
        System.out.println(missingDisplay);  // Welcome, Guest!

        // ----- Pattern 2: Chain Optional transformations safely -----
        // Without Optional you'd have:
        //   if (user != null && user.getAddress() != null) { ... }
        // With Optional:
        String city = findUserById(1)
                .map(User::getAddress)
                .map(Address::getCity)
                .orElse("Unknown City");
        System.out.println("City: " + city);  // Springfield

        // ----- Pattern 3: orElseThrow for service layer validation -----
        try {
            User user = findUserById(999)
                    .orElseThrow(() -> new NoSuchElementException("User 999 not found"));
        } catch (NoSuchElementException e) {
            System.out.println("Service exception: " + e.getMessage());
        }

        // ----- Pattern 4: filter + map for conditional business logic -----
        // Only process premium users
        Optional<User> userOpt = findUserById(1);
        double discount = userOpt
                .filter(User::isPremium)
                .map(u -> 0.20)      // 20% discount for premium users
                .orElse(0.0);        // no discount otherwise
        System.out.println("Discount: " + (discount * 100) + "%");  // 0.0% (Alice is not premium)

        // ----- Pattern 5: Optional in a loop / list processing -----
        List<Integer> userIds = Arrays.asList(1, 2, 999, 3);
        System.out.println("Found users:");
        userIds.stream()
               .map(OptionalClass::findUserById)
               .filter(Optional::isPresent)      // keep only found users
               .map(Optional::get)               // safe to call get() here
               .map(User::getName)
               .forEach(System.out::println);

        System.out.println();
    }

    // ----- Pattern 6 — What NOT to do (Anti-Patterns) -----
    static void demonstrateAntiPatterns() {
        System.out.println("=== Anti-Patterns (What NOT to Do) ===");

        Optional<String> opt = Optional.of("value");

        // ❌ Anti-pattern 1: Using isPresent() + get() — verbose, old style
        if (opt.isPresent()) {
            System.out.println("❌ Bad: " + opt.get());
        }
        // ✅ Better: use ifPresent or orElse
        opt.ifPresent(v -> System.out.println("✅ Good: " + v));

        // ❌ Anti-pattern 2: Returning null from Optional method
        //   public Optional<User> findUser(int id) {
        //       return null;  // ← NEVER DO THIS
        //   }
        // ✅ Return Optional.empty() instead

        // ❌ Anti-pattern 3: Using Optional as a parameter type
        //   void process(Optional<String> name) { ... }  ← awkward API
        // ✅ Use method overloading or nullable param instead

        // ❌ Anti-pattern 4: Optional field in a class
        //   class User { private Optional<String> middleName; }  ← bad, not serializable
        // ✅ Use nullable field; return Optional only from methods

        // ❌ Anti-pattern 5: Optional.get() without checking — identical risk to null dereference
        Optional<String> empty = Optional.empty();
        try {
            String val = empty.get();  // NoSuchElementException — as bad as NPE
        } catch (NoSuchElementException e) {
            System.out.println("❌ Never call get() without isPresent() guard or use orElse*");
        }

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER METHODS simulating a small "repository" and domain model
    // ─────────────────────────────────────────────────────────────────────────
    static final Map<Integer, User> USER_DB = new HashMap<>();
    static {
        USER_DB.put(1, new User(1, "Alice",   new Address("123 Main St", "Springfield", "62701"), false));
        USER_DB.put(2, new User(2, "Bob",     new Address("456 Oak Ave", "Shelbyville", "62702"), true));
        USER_DB.put(3, new User(3, "Charlie", new Address("789 Pine Rd", "Capital City", "62703"), false));
    }

    static Optional<User> findUserById(int id) {
        return Optional.ofNullable(USER_DB.get(id));
    }

    static Optional<Address> getAddressOptional(User user) {
        // In real code, a user might not have an address on file
        return Optional.ofNullable(user.getAddress());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INNER CLASSES — User, Address
    // ─────────────────────────────────────────────────────────────────────────
    static class User {
        private final int     id;
        private final String  name;
        private final Address address;
        private final boolean premium;

        User(int id, String name, Address address, boolean premium) {
            this.id      = id;
            this.name    = name;
            this.address = address;
            this.premium = premium;
        }

        int     getId()        { return id; }
        String  getName()      { return name; }
        Address getAddress()   { return address; }
        boolean isPremium()    { return premium; }

        @Override
        public String toString() { return "User{" + id + ", " + name + "}"; }
    }

    static class Address {
        private final String street;
        private final String city;
        private final String zip;

        Address(String street, String city, String zip) {
            this.street = street;
            this.city   = city;
            this.zip    = zip;
        }

        String getStreet() { return street; }
        String getCity()   { return city; }
        String getZip()    { return zip; }
    }
}
