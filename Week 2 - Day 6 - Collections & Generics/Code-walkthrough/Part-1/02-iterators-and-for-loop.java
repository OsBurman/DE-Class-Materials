import java.util.*;

/**
 * DAY 6 — Collections & Generics
 * FILE 2: Iterators and the Enhanced For Loop
 *
 * Topics covered:
 *   - The Iterable/Iterator contract
 *   - Iterator: hasNext(), next(), remove()
 *   - ListIterator: bidirectional traversal, set(), add()
 *   - Enhanced for loop (for-each) across List, Set, Map, Queue
 *   - ConcurrentModificationException — why it happens and how to avoid it
 *   - Map.forEach() with lambdas (preview of Day 8 Streams)
 */
public class IteratorsAndForLoop {

    public static void main(String[] args) {

        System.out.println("=== SECTION 1: THE ITERATOR INTERFACE ===");
        demonstrateIterator();

        System.out.println("\n=== SECTION 2: ENHANCED FOR LOOP (FOR-EACH) ===");
        demonstrateEnhancedForLoop();

        System.out.println("\n=== SECTION 3: LISTITERATOR — BIDIRECTIONAL ===");
        demonstrateListIterator();

        System.out.println("\n=== SECTION 4: ITERATING MAPS ===");
        demonstrateMapIteration();

        System.out.println("\n=== SECTION 5: ConcurrentModificationException ===");
        demonstrateConcurrentModification();

        System.out.println("\n=== SECTION 6: ITERATING QUEUE AND SET ===");
        demonstrateQueueAndSetIteration();
    }

    // =========================================================================
    // SECTION 1: THE ITERATOR INTERFACE
    // =========================================================================
    // Every Collection extends Iterable<T>, which means every Collection can
    // provide an Iterator<T>.
    //
    // Iterator<T> has three methods:
    //   boolean hasNext()  — is there another element?
    //   T       next()     — return the next element AND advance the cursor
    //   void    remove()   — remove the LAST element returned by next()
    //                        (only safe way to remove while iterating)
    //
    // The for-each loop is SYNTAX SUGAR for the Iterator pattern.
    // =========================================================================
    static void demonstrateIterator() {

        List<String> planets = new ArrayList<>(
            List.of("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn")
        );

        // --- Basic Iterator usage ---
        System.out.println("Iterating with explicit Iterator:");
        Iterator<String> it = planets.iterator();
        while (it.hasNext()) {
            String planet = it.next();
            System.out.println("  " + planet);
        }

        // --- Iterator.remove() — the ONLY safe way to remove during iteration ---
        // Remove all planets with names shorter than 5 characters
        List<String> planetsCopy = new ArrayList<>(planets);
        Iterator<String> removeIt = planetsCopy.iterator();
        while (removeIt.hasNext()) {
            String planet = removeIt.next();
            if (planet.length() < 5) {
                removeIt.remove();   // safe! removes from underlying list
            }
        }
        System.out.println("Planets with name ≥ 5 chars: " + planetsCopy);

        // --- Showing that next() both returns AND advances ---
        List<Integer> numbers = new ArrayList<>(List.of(10, 20, 30, 40, 50));
        Iterator<Integer> numIt = numbers.iterator();
        System.out.print("Every other element: ");
        while (numIt.hasNext()) {
            System.out.print(numIt.next() + " ");  // take one
            if (numIt.hasNext()) numIt.next();      // skip one (advance without printing)
        }
        System.out.println();
    }

    // =========================================================================
    // SECTION 2: ENHANCED FOR LOOP (FOR-EACH)
    // =========================================================================
    // The for-each loop is the most common way to iterate in Java.
    //
    // Syntax:  for (ElementType element : collection) { ... }
    //
    // Behind the scenes, the compiler converts this to an Iterator loop.
    // Works for: any Collection (List, Set, Queue) and arrays.
    //
    // Limitation: you CANNOT remove elements or access the index inside
    //             a for-each loop. Use Iterator or a traditional for loop instead.
    // =========================================================================
    static void demonstrateEnhancedForLoop() {

        // --- For-each over List (order preserved) ---
        List<String> languages = List.of("Java", "Python", "TypeScript", "Go", "Rust");
        System.out.println("Programming languages:");
        for (String lang : languages) {
            System.out.println("  - " + lang);
        }

        // --- For-each over Set (HashSet — no guaranteed order) ---
        Set<String> frameworks = new HashSet<>(Set.of("Spring", "React", "Angular", "Django"));
        System.out.println("\nFrameworks (HashSet — order unpredictable):");
        for (String fw : frameworks) {
            System.out.println("  - " + fw);
        }

        // --- For-each over TreeSet (sorted order) ---
        Set<String> sortedFrameworks = new TreeSet<>(frameworks);
        System.out.println("\nFrameworks (TreeSet — alphabetical):");
        for (String fw : sortedFrameworks) {
            System.out.println("  - " + fw);
        }

        // --- For-each with index simulation (when you need the index) ---
        List<String> menu = List.of("Burger", "Pizza", "Salad", "Pasta");
        System.out.println("\nMenu (with index):");
        int index = 0;
        for (String item : menu) {
            System.out.println("  " + (index + 1) + ". " + item);
            index++;
        }

        // Alternatively, use a traditional for loop when index matters:
        System.out.println("\nMenu (traditional for — same output):");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + menu.get(i));
        }

        // --- For-each over a 2D array ---
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println("\n3x3 matrix:");
        for (int[] row : matrix) {
            for (int cell : row) {
                System.out.printf("%3d", cell);
            }
            System.out.println();
        }
    }

    // =========================================================================
    // SECTION 3: LISTITERATOR — BIDIRECTIONAL
    // =========================================================================
    // ListIterator extends Iterator and adds:
    //   hasPrevious() / previous()  — traverse BACKWARDS
    //   nextIndex() / previousIndex() — know the current position
    //   set(E e)     — replace the last returned element
    //   add(E e)     — insert element at current position
    //
    // Only available on List (not Set, not Map).
    // =========================================================================
    static void demonstrateListIterator() {

        List<String> playlist = new ArrayList<>(
            List.of("Song A", "Song B", "Song C", "Song D", "Song E")
        );

        // --- Forward traversal with index ---
        System.out.println("Forward traversal with ListIterator:");
        ListIterator<String> lit = playlist.listIterator();
        while (lit.hasNext()) {
            int idx = lit.nextIndex();
            String song = lit.next();
            System.out.println("  [" + idx + "] " + song);
        }

        // --- Backward traversal (cursor is now at end from above) ---
        System.out.println("Backward traversal:");
        while (lit.hasPrevious()) {
            System.out.println("  " + lit.previous());
        }

        // --- set() — replace current element ---
        ListIterator<String> replaceIt = playlist.listIterator();
        while (replaceIt.hasNext()) {
            String song = replaceIt.next();
            if (song.equals("Song C")) {
                replaceIt.set("🎵 Favourite Song");  // replace in-place
            }
        }
        System.out.println("After replacing Song C: " + playlist);

        // --- add() — insert at current position ---
        ListIterator<String> addIt = playlist.listIterator();
        while (addIt.hasNext()) {
            String song = addIt.next();
            if (song.equals("Song B")) {
                addIt.add("(Interlude)");   // inserted AFTER Song B
            }
        }
        System.out.println("After inserting Interlude: " + playlist);
    }

    // =========================================================================
    // SECTION 4: ITERATING MAPS
    // =========================================================================
    // Maps are NOT Collections, so they don't implement Iterable<T> directly.
    // Three views let you iterate:
    //   map.entrySet() → Set<Map.Entry<K,V>> — iterate key AND value together
    //   map.keySet()   → Set<K>              — iterate keys only
    //   map.values()   → Collection<V>       — iterate values only
    //
    // map.forEach((k, v) -> ...) — cleaner with lambdas (Java 8+)
    // =========================================================================
    static void demonstrateMapIteration() {

        Map<String, Double> productPrices = new LinkedHashMap<>();
        productPrices.put("Laptop",    1299.99);
        productPrices.put("Monitor",    449.00);
        productPrices.put("Keyboard",    89.99);
        productPrices.put("Mouse",       39.99);
        productPrices.put("Webcam",      79.00);

        // --- 1. entrySet() for-each (most common — gets both key and value) ---
        System.out.println("Method 1 — entrySet() for-each:");
        for (Map.Entry<String, Double> entry : productPrices.entrySet()) {
            System.out.printf("  %-10s $%.2f%n", entry.getKey(), entry.getValue());
        }

        // --- 2. keySet() for-each (when you only need keys, or need to look up values) ---
        System.out.println("\nMethod 2 — keySet() for-each:");
        for (String product : productPrices.keySet()) {
            System.out.println("  Product: " + product);
        }

        // --- 3. values() for-each (when you only need values) ---
        System.out.println("\nMethod 3 — values() to sum total:");
        double total = 0;
        for (double price : productPrices.values()) {
            total += price;
        }
        System.out.printf("  Total cart value: $%.2f%n", total);

        // --- 4. forEach() with lambda (Java 8+ — cleanest) ---
        System.out.println("\nMethod 4 — forEach() lambda:");
        productPrices.forEach((product, price) ->
            System.out.printf("  %-10s $%.2f%n", product, price)
        );

        // --- 5. Modifying a map while iterating (via entrySet iterator) ---
        Map<String, Double> prices = new HashMap<>(productPrices);
        Iterator<Map.Entry<String, Double>> entryIt = prices.entrySet().iterator();
        while (entryIt.hasNext()) {
            Map.Entry<String, Double> entry = entryIt.next();
            if (entry.getValue() < 100.0) {
                entryIt.remove();  // safe removal via iterator
            }
        }
        System.out.println("\nProducts over $100 only: " + prices);
    }

    // =========================================================================
    // SECTION 5: ConcurrentModificationException
    // =========================================================================
    // Java collections use a "fail-fast" iterator. If you modify the collection
    // STRUCTURALLY (add or remove elements) while iterating — without using
    // the iterator's own remove() — the iterator throws
    // ConcurrentModificationException.
    //
    // Solutions:
    //   1. Use iterator.remove() (shown in Section 1)
    //   2. Use removeIf() on the collection (Java 8+)
    //   3. Collect items to remove in a separate list, then removeAll()
    //   4. Use CopyOnWriteArrayList (see Day 9 — Multithreading)
    // =========================================================================
    static void demonstrateConcurrentModification() {

        List<String> items = new ArrayList<>(List.of("Apple", "Banana", "Cherry", "Date", "Elderberry"));

        // BAD — this will throw ConcurrentModificationException:
        System.out.println("Attempting unsafe removal during for-each...");
        try {
            for (String item : items) {
                if (item.startsWith("B")) {
                    items.remove(item);   // DANGER: modifying the collection mid-iteration
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("  ConcurrentModificationException thrown! ✗");
        }

        // GOOD — Solution 1: iterator.remove()
        List<String> list1 = new ArrayList<>(List.of("Apple", "Banana", "Cherry", "Date"));
        Iterator<String> it = list1.iterator();
        while (it.hasNext()) {
            if (it.next().startsWith("B")) {
                it.remove();   // safe
            }
        }
        System.out.println("Solution 1 (iterator.remove()): " + list1);

        // GOOD — Solution 2: removeIf() (Java 8+ — cleanest approach)
        List<String> list2 = new ArrayList<>(List.of("Apple", "Banana", "Cherry", "Date"));
        list2.removeIf(item -> item.startsWith("B"));
        System.out.println("Solution 2 (removeIf lambda):   " + list2);

        // GOOD — Solution 3: collect then removeAll
        List<String> list3 = new ArrayList<>(List.of("Apple", "Banana", "Cherry", "Date"));
        List<String> toRemove = new ArrayList<>();
        for (String item : list3) {
            if (item.startsWith("B")) toRemove.add(item);
        }
        list3.removeAll(toRemove);
        System.out.println("Solution 3 (collect+removeAll): " + list3);
    }

    // =========================================================================
    // SECTION 6: ITERATING QUEUE AND SET
    // =========================================================================
    static void demonstrateQueueAndSetIteration() {

        // Queue — for-each preserves FIFO order visually, but note:
        // iterating a Queue does NOT dequeue elements (use poll() to dequeue)
        Queue<String> ticketQueue = new ArrayDeque<>(
            List.of("Customer A", "Customer B", "Customer C", "Customer D")
        );

        System.out.println("Queue contents (for-each, no dequeue):");
        for (String customer : ticketQueue) {
            System.out.println("  Waiting: " + customer);
        }
        System.out.println("Queue still has " + ticketQueue.size() + " items (for-each didn't dequeue)");

        System.out.println("\nProcessing queue with poll():");
        while (!ticketQueue.isEmpty()) {
            System.out.println("  Serving: " + ticketQueue.poll());
        }
        System.out.println("Queue empty: " + ticketQueue.isEmpty());

        // Set — for-each works, but order depends on implementation
        System.out.println("\nHashSet for-each (random order):");
        Set<Integer> hashSet = new HashSet<>(Set.of(5, 3, 8, 1, 9, 2, 7));
        for (int num : hashSet) {
            System.out.print(num + " ");
        }
        System.out.println();

        System.out.println("TreeSet for-each (sorted order):");
        Set<Integer> treeSet = new TreeSet<>(hashSet);
        for (int num : treeSet) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}
