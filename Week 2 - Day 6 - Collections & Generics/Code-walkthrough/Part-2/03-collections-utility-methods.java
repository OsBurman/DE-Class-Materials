import java.util.*;

/**
 * DAY 6 — Collections & Generics
 * Part 2, File 3: Collections Utility Methods
 *
 * Topics covered:
 *   - Collections.sort() (with and without Comparator)
 *   - Collections.reverse()
 *   - Collections.shuffle()
 *   - Collections.min() and Collections.max()
 *   - Collections.frequency()
 *   - Collections.nCopies()
 *   - Collections.swap()
 *   - Collections.fill()
 *   - Collections.copy()
 *   - Collections.disjoint()
 *   - Collections.unmodifiableList() / unmodifiableMap() / unmodifiableSet()
 *   - Collections.synchronizedList() (thread-safety awareness)
 *   - Collections.singletonList() / emptyList()
 *   - List.of() / Map.of() / Set.of() — immutable factory methods (Java 9+)
 */
public class CollectionsUtilityMethods {

    public static void main(String[] args) {

        System.out.println("=== SECTION 1: sort, reverse, shuffle ===");
        demonstrateSortReverseShffle();

        System.out.println("\n=== SECTION 2: min, max, frequency ===");
        demonstrateMinMaxFrequency();

        System.out.println("\n=== SECTION 3: nCopies, fill, swap, copy ===");
        demonstrateNcopiesFillSwapCopy();

        System.out.println("\n=== SECTION 4: disjoint ===");
        demonstrateDisjoint();

        System.out.println("\n=== SECTION 5: unmodifiableList/Set/Map ===");
        demonstrateUnmodifiable();

        System.out.println("\n=== SECTION 6: synchronizedList (thread-safety awareness) ===");
        demonstrateSynchronized();

        System.out.println("\n=== SECTION 7: Immutable factory methods (Java 9+) ===");
        demonstrateImmutableFactories();

        System.out.println("\n=== SECTION 8: Practical patterns combining utilities ===");
        practicalPatterns();
    }

    // =========================================================================
    // SECTION 1: SORT, REVERSE, SHUFFLE
    // =========================================================================
    static void demonstrateSortReverseShffle() {

        List<Integer> scores = new ArrayList<>(List.of(87, 42, 95, 61, 73, 88, 55, 91));
        System.out.println("Original: " + scores);

        // --- Collections.sort() — sorts in natural (ascending) order ---
        // Requires elements to implement Comparable
        // Uses a merge sort variant — O(n log n), stable
        Collections.sort(scores);
        System.out.println("After sort():    " + scores);

        // --- Collections.reverse() — reverses the current order ---
        // NOT the same as sorting descending! Reverses whatever order is current.
        Collections.reverse(scores);
        System.out.println("After reverse(): " + scores);

        // --- To sort descending: sort first, THEN reverse ---
        List<Integer> scores2 = new ArrayList<>(List.of(87, 42, 95, 61, 73, 88, 55, 91));
        Collections.sort(scores2);
        Collections.reverse(scores2);
        System.out.println("Sorted descending (sort + reverse): " + scores2);

        // --- OR: use Comparator.reverseOrder() directly ---
        List<Integer> scores3 = new ArrayList<>(List.of(87, 42, 95, 61, 73, 88, 55, 91));
        scores3.sort(Comparator.reverseOrder());
        System.out.println("Sorted descending (reverseOrder):   " + scores3);

        // --- Collections.sort() with Comparator ---
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob", "Dave", "Eve"));
        System.out.println("\nNames before sort: " + names);

        // Sort by length, then alphabetically on tie
        Collections.sort(names,
            Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())
        );
        System.out.println("Sorted by length then alpha: " + names);

        // --- Collections.shuffle() — randomizes order ---
        // Useful for: card games, quiz randomization, A/B testing, load balancing
        List<String> cards = new ArrayList<>(List.of("Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"));
        Collections.shuffle(cards);
        System.out.println("\nShuffled deck (first 5): " + cards.subList(0, 5));

        // Shuffle with a seed (reproducible — useful in testing)
        List<Integer> data = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Collections.shuffle(data, new Random(42));
        System.out.println("Shuffled with seed 42: " + data);

        Collections.shuffle(data, new Random(42));  // same seed → same shuffle
        System.out.println("Same seed again:       " + data);  // identical output
    }

    // =========================================================================
    // SECTION 2: MIN, MAX, FREQUENCY
    // =========================================================================
    static void demonstrateMinMaxFrequency() {

        List<Integer> temps = new ArrayList<>(List.of(72, 68, 81, 74, 65, 88, 77, 63, 79, 85));

        // --- Collections.min() and Collections.max() ---
        // Uses natural ordering (Comparable) or a provided Comparator
        // Throws NoSuchElementException if collection is empty
        int lowest  = Collections.min(temps);
        int highest = Collections.max(temps);
        System.out.println("Temperatures: " + temps);
        System.out.printf("Min: %d°F, Max: %d°F, Range: %d°F%n", lowest, highest, highest - lowest);

        // min/max with a Comparator (when elements don't have natural order
        // or you want a different ordering)
        List<String> words = List.of("banana", "apple", "cherry", "date", "elderberry");
        String shortest = Collections.min(words, Comparator.comparingInt(String::length));
        String longest  = Collections.max(words, Comparator.comparingInt(String::length));
        System.out.println("\nShortest word: " + shortest);
        System.out.println("Longest word:  " + longest);

        // --- Collections.frequency() ---
        // Counts how many times an element appears in the collection
        // Uses equals() for comparison
        List<String> survey = new ArrayList<>(List.of(
            "Yes", "No", "Yes", "Maybe", "Yes", "No", "Yes", "No", "Yes", "Maybe"
        ));
        int yesCount   = Collections.frequency(survey, "Yes");
        int noCount    = Collections.frequency(survey, "No");
        int maybeCount = Collections.frequency(survey, "Maybe");

        System.out.println("\nSurvey results:");
        System.out.println("  Yes:   " + yesCount   + " (" + (yesCount   * 100 / survey.size()) + "%)");
        System.out.println("  No:    " + noCount    + " (" + (noCount    * 100 / survey.size()) + "%)");
        System.out.println("  Maybe: " + maybeCount + " (" + (maybeCount * 100 / survey.size()) + "%)");

        // frequency works on any Collection, including Sets (though count is always 0 or 1)
        Set<String> tagSet = new HashSet<>(Set.of("java", "spring", "docker"));
        System.out.println("\nfrequency of 'java' in Set: " + Collections.frequency(tagSet, "java"));
        System.out.println("frequency of 'aws' in Set:  " + Collections.frequency(tagSet, "aws"));
    }

    // =========================================================================
    // SECTION 3: nCopies, fill, swap, copy
    // =========================================================================
    static void demonstrateNcopiesFillSwapCopy() {

        // --- Collections.nCopies() ---
        // Returns an IMMUTABLE List of n copies of the specified object
        List<String> fiveHellos = Collections.nCopies(5, "hello");
        System.out.println("nCopies(5, 'hello'): " + fiveHellos);

        // Useful to initialize an ArrayList with a default value:
        List<Integer> zeros = new ArrayList<>(Collections.nCopies(8, 0));
        System.out.println("Initialized with zeros: " + zeros);

        // --- Collections.fill() ---
        // Replaces ALL elements of a list with a specified value
        // List must already be the right size (fill doesn't resize)
        List<String> slots = new ArrayList<>(List.of("A", "B", "C", "D", "E"));
        System.out.println("\nBefore fill: " + slots);
        Collections.fill(slots, "EMPTY");
        System.out.println("After fill:  " + slots);

        // --- Collections.swap() ---
        // Swaps elements at two indices within a List
        List<String> ranking = new ArrayList<>(List.of("3rd", "1st", "2nd", "4th", "5th"));
        System.out.println("\nBefore swap(0,1): " + ranking);
        Collections.swap(ranking, 0, 1);
        System.out.println("After swap(0,1):  " + ranking);
        Collections.swap(ranking, 1, 2);
        System.out.println("After swap(1,2):  " + ranking);

        // --- Collections.copy() ---
        // Copies all elements from source into destination
        // DESTINATION must be at least as large as source!
        // Does NOT create a new list — writes INTO an existing list
        List<Integer> source = List.of(10, 20, 30);
        List<Integer> dest = new ArrayList<>(List.of(0, 0, 0, 99, 99)); // larger than source
        System.out.println("\nSource: " + source);
        System.out.println("Before copy: " + dest);
        Collections.copy(dest, source);
        System.out.println("After copy:  " + dest);  // first 3 replaced, 99s remain

        // To make a fresh copy, use new ArrayList<>(original) instead:
        List<Integer> trueCopy = new ArrayList<>(source);
        System.out.println("True copy via new ArrayList<>(source): " + trueCopy);

        // --- Collections.binarySearch() ---
        // MUST be sorted first! Uses natural ordering or provided Comparator
        // Returns index of the element, or a negative value if not found
        List<Integer> sorted = new ArrayList<>(List.of(10, 20, 30, 40, 50, 60, 70, 80));
        int idx = Collections.binarySearch(sorted, 50);
        System.out.println("\nbinarySearch for 50 in " + sorted + " → index " + idx);
        int notFound = Collections.binarySearch(sorted, 35);
        System.out.println("binarySearch for 35 → " + notFound + " (negative = not found)");
    }

    // =========================================================================
    // SECTION 4: DISJOINT
    // =========================================================================
    static void demonstrateDisjoint() {

        // Collections.disjoint() returns true if the two collections have NO elements in common
        Set<String> teamA = Set.of("Alice", "Bob", "Carol");
        Set<String> teamB = Set.of("Dave", "Eve", "Frank");
        Set<String> teamC = Set.of("Bob", "Grace", "Dave");  // overlaps both

        System.out.println("Team A: " + teamA);
        System.out.println("Team B: " + teamB);
        System.out.println("Team C: " + teamC);

        System.out.println("\ndisjoint(A, B): " + Collections.disjoint(teamA, teamB)); // true
        System.out.println("disjoint(A, C): " + Collections.disjoint(teamA, teamC)); // false (Bob)
        System.out.println("disjoint(B, C): " + Collections.disjoint(teamB, teamC)); // false (Dave)

        // Practical: check if any purchased items are out of stock
        Set<String> cart       = Set.of("Laptop", "Mouse", "Keyboard");
        Set<String> outOfStock = Set.of("Monitor", "Webcam", "Headphones");
        boolean cartItemsAvailable = Collections.disjoint(cart, outOfStock);
        System.out.println("\nCart: " + cart);
        System.out.println("Out of stock: " + outOfStock);
        System.out.println("All cart items available: " + cartItemsAvailable);

        // Add a conflict:
        Set<String> cartWithConflict = new HashSet<>(cart);
        cartWithConflict.add("Webcam");  // Webcam is out of stock
        System.out.println("Cart with Webcam: " + cartWithConflict);
        System.out.println("All items available: " + Collections.disjoint(cartWithConflict, outOfStock));
    }

    // =========================================================================
    // SECTION 5: UNMODIFIABLE WRAPPERS
    // =========================================================================
    // These wrap an existing collection and throw UnsupportedOperationException
    // on any mutating operation (add, remove, set, clear, etc.)
    //
    // Use cases:
    //   - Returning internal state from a method without exposing mutation
    //   - Publishing a "read-only view" of a config map
    //   - Defensive programming
    //
    // NOTE: The UNDERLYING collection can still change if you hold a reference to it.
    //       For a fully frozen snapshot, use List.copyOf() or Collections.unmodifiableList(new ArrayList<>(original))
    // =========================================================================
    static void demonstrateUnmodifiable() {

        List<String> internal = new ArrayList<>(List.of("Java", "Spring", "Docker"));
        List<String> readOnly = Collections.unmodifiableList(internal);

        System.out.println("Read-only list: " + readOnly);
        System.out.println("Can read: " + readOnly.get(0));

        // Attempting to modify throws UnsupportedOperationException:
        try {
            readOnly.add("Kubernetes");
        } catch (UnsupportedOperationException e) {
            System.out.println("readOnly.add() → UnsupportedOperationException ✗");
        }
        try {
            readOnly.remove(0);
        } catch (UnsupportedOperationException e) {
            System.out.println("readOnly.remove() → UnsupportedOperationException ✗");
        }

        // WARNING: The underlying list still mutates through original reference!
        internal.add("Kafka");
        System.out.println("After internal.add('Kafka'): readOnly = " + readOnly);
        // readOnly reflects the change — it's a VIEW, not a snapshot

        // For a true snapshot, use List.copyOf():
        List<String> snapshot = List.copyOf(internal);
        internal.add("AWS");
        System.out.println("snapshot (frozen):     " + snapshot);  // doesn't change
        System.out.println("readOnly (live view):  " + readOnly);  // reflects AWS

        // unmodifiableMap
        Map<String, Integer> config = new HashMap<>();
        config.put("timeout", 30);
        config.put("retries", 3);
        Map<String, Integer> readOnlyConfig = Collections.unmodifiableMap(config);
        System.out.println("\nRead-only config: " + readOnlyConfig);
        try {
            readOnlyConfig.put("timeout", 60);
        } catch (UnsupportedOperationException e) {
            System.out.println("readOnlyConfig.put() → UnsupportedOperationException ✗");
        }

        // unmodifiableSet
        Set<String> roles = new HashSet<>(Set.of("ADMIN", "USER", "GUEST"));
        Set<String> readOnlyRoles = Collections.unmodifiableSet(roles);
        System.out.println("Read-only roles: " + readOnlyRoles);
    }

    // =========================================================================
    // SECTION 6: SYNCHRONIZEDLIST — THREAD SAFETY AWARENESS
    // =========================================================================
    // Standard collections are NOT thread-safe — concurrent modifications cause
    // data corruption or ConcurrentModificationException.
    //
    // Collections.synchronizedList() wraps a List so each method is synchronized
    // (one thread at a time). Simple but adds lock overhead.
    //
    // For higher-performance concurrent code, prefer CopyOnWriteArrayList,
    // ConcurrentHashMap, etc. (Day 9 — Multithreading covers these in depth).
    // =========================================================================
    static void demonstrateSynchronized() {
        // Creating a thread-safe list wrapper
        List<String> syncList = Collections.synchronizedList(new ArrayList<>());

        syncList.add("thread-safe add 1");
        syncList.add("thread-safe add 2");
        syncList.add("thread-safe add 3");

        System.out.println("Synchronized list: " + syncList);

        // IMPORTANT: iteration still requires manual synchronization!
        // Individual methods are thread-safe, but iteration is NOT atomic.
        synchronized (syncList) {
            for (String item : syncList) {
                System.out.println("  (synchronized) " + item);
            }
        }

        System.out.println("\n⚠️  For concurrent use, prefer ConcurrentHashMap, ");
        System.out.println("    CopyOnWriteArrayList — covered in Day 9 Multithreading.");

        // Similarly: synchronizedMap, synchronizedSet
        Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
        syncMap.put("key1", 1);
        System.out.println("Synchronized map: " + syncMap);
    }

    // =========================================================================
    // SECTION 7: IMMUTABLE FACTORY METHODS (JAVA 9+)
    // =========================================================================
    // List.of(), Set.of(), Map.of(), Map.entry() — create immutable collections.
    //
    // These are TRULY immutable (not a wrapper around mutable):
    //   - add, remove, set all throw UnsupportedOperationException
    //   - null elements are NOT allowed (throws NullPointerException)
    //
    // Compare to Collections.unmodifiableList() — which is a mutable wrapper.
    // =========================================================================
    static void demonstrateImmutableFactories() {

        // List.of() — immutable list (maintains insertion order)
        List<String> immutableList = List.of("Java", "Kotlin", "Scala", "Groovy");
        System.out.println("List.of(): " + immutableList);
        try {
            immutableList.add("Clojure");
        } catch (UnsupportedOperationException e) {
            System.out.println("List.of().add() → UnsupportedOperationException ✗");
        }

        // Set.of() — immutable set (no duplicates allowed even at creation time)
        Set<String> immutableSet = Set.of("READ", "WRITE", "EXECUTE");
        System.out.println("Set.of(): " + immutableSet);
        // Set.of("READ", "READ")  // throws IllegalArgumentException — duplicate at construction

        // Map.of() — up to 10 key-value pairs
        Map<String, Integer> immutableMap = Map.of(
            "one",   1,
            "two",   2,
            "three", 3,
            "four",  4
        );
        System.out.println("Map.of(): " + immutableMap);

        // Map.ofEntries() — more than 10 pairs, or cleaner formatting
        Map<String, String> countryCode = Map.ofEntries(
            Map.entry("US", "United States"),
            Map.entry("UK", "United Kingdom"),
            Map.entry("DE", "Germany"),
            Map.entry("JP", "Japan"),
            Map.entry("FR", "France")
        );
        System.out.println("Map.ofEntries():");
        countryCode.forEach((code, name) ->
            System.out.println("  " + code + " → " + name));

        // List.copyOf() — immutable copy of an existing collection
        List<String> original = new ArrayList<>(List.of("A", "B", "C"));
        List<String> frozen = List.copyOf(original);
        original.add("D");  // mutates original
        System.out.println("original (mutated): " + original);
        System.out.println("frozen (unchanged): " + frozen);

        // Collections.singletonList() — unmodifiable list with exactly ONE element
        List<String> single = Collections.singletonList("OnlyOne");
        System.out.println("singletonList: " + single);

        // Collections.emptyList() — unmodifiable empty list (no allocations — shared instance)
        List<String> empty = Collections.emptyList();
        System.out.println("emptyList: " + empty + " | size: " + empty.size());
    }

    // =========================================================================
    // SECTION 8: PRACTICAL PATTERNS
    // =========================================================================
    static void practicalPatterns() {

        // Pattern 1: Find top-N items using sort + subList
        List<Integer> allScores = new ArrayList<>(
            List.of(87, 42, 95, 61, 73, 88, 55, 91, 67, 79, 83, 72)
        );
        List<Integer> top5 = new ArrayList<>(allScores);
        Collections.sort(top5, Comparator.reverseOrder());
        List<Integer> topFive = top5.subList(0, 5);
        System.out.println("All scores: " + allScores);
        System.out.println("Top 5 scores: " + topFive);

        // Pattern 2: Randomly select a winner from a list
        List<String> participants = new ArrayList<>(
            List.of("Alice", "Bob", "Carol", "Dave", "Eve", "Frank")
        );
        Collections.shuffle(participants);
        String winner = participants.get(0);
        System.out.println("\nParticipants (shuffled): " + participants);
        System.out.println("Winner: " + winner);

        // Pattern 3: Build read-only constants
        List<String> HTTP_METHODS = Collections.unmodifiableList(
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS")
        );
        System.out.println("\nHTTP methods (read-only): " + HTTP_METHODS);

        // Pattern 4: Frequency map of a word list
        List<String> words = List.of(
            "apple", "banana", "apple", "cherry", "banana", "apple", "date"
        );
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            freq.merge(word, 1, Integer::sum);
        }
        // Sort the freq map by count descending
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(freq.entrySet());
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        System.out.println("\nWord frequency (sorted by count):");
        entries.forEach(e -> System.out.println("  " + e.getKey() + " → " + e.getValue()));

        // Pattern 5: Check if two permission sets overlap (authorization check)
        Set<String> userRoles    = Set.of("USER", "EDITOR");
        Set<String> adminRoles   = Set.of("ADMIN", "SUPERUSER");
        Set<String> editorRoles  = Set.of("EDITOR", "REVIEWER");

        System.out.println("\nUser has admin access: "
            + !Collections.disjoint(userRoles, adminRoles));   // false — no overlap
        System.out.println("User has editor access: "
            + !Collections.disjoint(userRoles, editorRoles));  // true — EDITOR in common
    }
}
