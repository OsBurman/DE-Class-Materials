import java.util.*;

/**
 * DAY 6 — Collections & Generics
 * FILE 1: Java Collections Framework Overview
 *
 * Topics covered:
 *   - Java Collections Framework (JCF) overview
 *   - List interface and implementations: ArrayList, LinkedList
 *   - Set interface and implementations: HashSet, TreeSet
 *   - Map interface and implementations: HashMap, TreeMap, LinkedHashMap
 *   - Queue interface and implementations: LinkedList (as Queue), ArrayDeque
 *
 * All demo classes are static inner classes inside CollectionsFrameworkOverview.
 */
public class CollectionsFrameworkOverview {

    // =========================================================================
    // SECTION 1: THE JAVA COLLECTIONS FRAMEWORK — OVERVIEW
    // =========================================================================
    //
    //  The JCF is a unified architecture for storing and manipulating groups
    //  of objects. Three things make it up:
    //
    //    1. INTERFACES  — define what operations a collection supports
    //    2. IMPLEMENTATIONS — concrete classes (ArrayList, HashMap, etc.)
    //    3. ALGORITHMS  — utility methods (Collections.sort(), etc.) — Part 2
    //
    //  Core interface hierarchy (simplified):
    //
    //    Iterable
    //      └── Collection
    //            ├── List       (ordered, allows duplicates, index-based)
    //            ├── Set        (no duplicates)
    //            │     └── SortedSet → NavigableSet
    //            └── Queue      (FIFO / priority ordering)
    //                  └── Deque (double-ended queue)
    //
    //    Map  (key-value pairs — NOT a Collection, but part of JCF)
    //          └── SortedMap → NavigableMap
    //
    // =========================================================================

    public static void main(String[] args) {

        System.out.println("=== SECTION 1: LIST — ArrayList ===");
        demonstrateArrayList();

        System.out.println("\n=== SECTION 2: LIST — LinkedList ===");
        demonstrateLinkedList();

        System.out.println("\n=== SECTION 3: SET — HashSet ===");
        demonstrateHashSet();

        System.out.println("\n=== SECTION 4: SET — TreeSet ===");
        demonstrateTreeSet();

        System.out.println("\n=== SECTION 5: MAP — HashMap ===");
        demonstrateHashMap();

        System.out.println("\n=== SECTION 6: MAP — TreeMap ===");
        demonstrateTreeMap();

        System.out.println("\n=== SECTION 7: MAP — LinkedHashMap ===");
        demonstrateLinkedHashMap();

        System.out.println("\n=== SECTION 8: QUEUE — ArrayDeque ===");
        demonstrateQueue();

        System.out.println("\n=== SECTION 9: CHOOSING THE RIGHT COLLECTION ===");
        choosingTheRightCollection();
    }

    // =========================================================================
    // SECTION 1: LIST — ArrayList
    // =========================================================================
    // - Backed by a resizable array
    // - O(1) random access (get by index)
    // - O(n) insert/remove in the middle (shifts elements)
    // - Best when you READ more than you INSERT/DELETE
    // =========================================================================
    static void demonstrateArrayList() {

        // Creating an ArrayList — the generic type <String> enforces type safety
        List<String> shoppingList = new ArrayList<>();

        // --- Adding elements ---
        shoppingList.add("Apples");
        shoppingList.add("Bread");
        shoppingList.add("Milk");
        shoppingList.add("Eggs");
        shoppingList.add("Apples");  // ArrayList ALLOWS duplicates

        System.out.println("Shopping list: " + shoppingList);
        System.out.println("Size: " + shoppingList.size());

        // --- Index-based access (O(1) — fast!) ---
        System.out.println("First item: " + shoppingList.get(0));
        System.out.println("Last item:  " + shoppingList.get(shoppingList.size() - 1));

        // --- Insert at specific position ---
        shoppingList.add(2, "Butter");  // inserts at index 2, shifts right
        System.out.println("After inserting Butter at index 2: " + shoppingList);

        // --- Update an element ---
        shoppingList.set(0, "Bananas");
        System.out.println("After replacing Apples with Bananas: " + shoppingList);

        // --- Remove by index ---
        shoppingList.remove(4);                 // removes by index
        System.out.println("After remove(4): " + shoppingList);

        // --- Remove by value ---
        shoppingList.remove("Eggs");            // removes first occurrence by value
        System.out.println("After remove('Eggs'): " + shoppingList);

        // --- Searching ---
        System.out.println("Contains 'Milk'? " + shoppingList.contains("Milk"));
        System.out.println("Index of 'Milk': " + shoppingList.indexOf("Milk"));

        // --- Sub-list (a view, not a copy!) ---
        List<String> sub = shoppingList.subList(0, 2);
        System.out.println("Sub-list [0,2): " + sub);

        // --- Converting array → ArrayList ---
        String[] fruits = {"Mango", "Papaya", "Kiwi"};
        List<String> fruitList = new ArrayList<>(Arrays.asList(fruits));
        System.out.println("From array: " + fruitList);

        // --- Clearing ---
        List<String> temp = new ArrayList<>(List.of("a", "b", "c"));
        temp.clear();
        System.out.println("After clear: " + temp + " | isEmpty: " + temp.isEmpty());

        // --- ArrayList initial capacity (performance tip) ---
        // If you know roughly how many items you'll store, pre-size to avoid
        // repeated resizing (each resize copies the backing array):
        List<Integer> scores = new ArrayList<>(1000);
        System.out.println("Pre-sized ArrayList, capacity hint = 1000");
    }

    // =========================================================================
    // SECTION 2: LIST — LinkedList
    // =========================================================================
    // - Backed by a doubly-linked list of nodes
    // - O(1) insert/remove at head or tail
    // - O(n) random access (must traverse the chain)
    // - Also implements Deque — can be used as a stack or queue
    // - Best when you INSERT/DELETE frequently, especially at ends
    // =========================================================================
    static void demonstrateLinkedList() {

        LinkedList<String> tasks = new LinkedList<>();

        // Adding to the end (tail)
        tasks.add("Write tests");
        tasks.add("Fix bug #42");
        tasks.add("Deploy to staging");

        // LinkedList-specific methods (not available on List interface)
        tasks.addFirst("Review pull request");   // add to front (head)
        tasks.addLast("Send release notes");     // add to end (tail)

        System.out.println("Task queue: " + tasks);

        System.out.println("First task: " + tasks.getFirst());
        System.out.println("Last task:  " + tasks.getLast());

        // Peek — look at head without removing
        System.out.println("Peek (head): " + tasks.peek());

        // Poll — remove and return head (returns null if empty — safe!)
        String nextTask = tasks.poll();
        System.out.println("Polled task: " + nextTask);
        System.out.println("Remaining: " + tasks);

        // Using LinkedList as a Stack (LIFO):
        LinkedList<String> browserHistory = new LinkedList<>();
        browserHistory.push("google.com");   // push = addFirst
        browserHistory.push("github.com");
        browserHistory.push("stackoverflow.com");
        System.out.println("Browser history (stack): " + browserHistory);
        System.out.println("Back button → " + browserHistory.pop()); // pop = removeFirst
        System.out.println("After back:  " + browserHistory);

        // ArrayList vs LinkedList — practical distinction
        System.out.println("\n-- ArrayList vs LinkedList performance note --");
        System.out.println("ArrayList.get(500_000)  → O(1) — array index");
        System.out.println("LinkedList.get(500_000) → O(n) — walks the chain");
        System.out.println("LinkedList.addFirst()   → O(1) — just relinks head");
        System.out.println("ArrayList.add(0, x)     → O(n) — shifts all elements");
    }

    // =========================================================================
    // SECTION 3: SET — HashSet
    // =========================================================================
    // - Backed by a HashMap internally
    // - No duplicates (guaranteed by equals() + hashCode())
    // - No ordering guarantee (order appears random)
    // - O(1) add, remove, contains (amortized)
    // - Best when you need fast membership checks and don't care about order
    // =========================================================================
    static void demonstrateHashSet() {

        Set<String> visitedCities = new HashSet<>();

        visitedCities.add("New York");
        visitedCities.add("London");
        visitedCities.add("Tokyo");
        visitedCities.add("London");   // DUPLICATE — silently ignored
        visitedCities.add("Paris");

        System.out.println("Visited cities: " + visitedCities);
        System.out.println("Size (London counted once): " + visitedCities.size());

        // add() returns false if the element already exists
        boolean added = visitedCities.add("Tokyo");
        System.out.println("Adding Tokyo again → added: " + added);

        // Fast membership check — O(1)
        System.out.println("Visited London? " + visitedCities.contains("London"));
        System.out.println("Visited Sydney? " + visitedCities.contains("Sydney"));

        // Set operations
        Set<String> wishList = new HashSet<>(Set.of("Sydney", "Rome", "Tokyo", "Cairo"));

        // UNION: add all from wishList into visitedCities
        Set<String> allCities = new HashSet<>(visitedCities);
        allCities.addAll(wishList);
        System.out.println("Union (visited + wishList): " + allCities);

        // INTERSECTION: keep only cities in both sets
        Set<String> alreadySeen = new HashSet<>(wishList);
        alreadySeen.retainAll(visitedCities);
        System.out.println("Intersection (wishList ∩ visited): " + alreadySeen);

        // DIFFERENCE: remove wishList cities already visited
        Set<String> stillToVisit = new HashSet<>(wishList);
        stillToVisit.removeAll(visitedCities);
        System.out.println("Still to visit (wishList - visited): " + stillToVisit);
    }

    // =========================================================================
    // SECTION 4: SET — TreeSet
    // =========================================================================
    // - Backed by a Red-Black tree (self-balancing BST)
    // - Elements are ALWAYS SORTED (natural order or custom Comparator)
    // - O(log n) add, remove, contains
    // - Implements NavigableSet: floor(), ceiling(), headSet(), tailSet()
    // - Best when you need a sorted, duplicate-free collection
    // =========================================================================
    static void demonstrateTreeSet() {

        TreeSet<Integer> scores = new TreeSet<>();
        scores.add(87);
        scores.add(42);
        scores.add(95);
        scores.add(61);
        scores.add(87);   // duplicate — ignored

        System.out.println("Scores (sorted automatically): " + scores);
        System.out.println("Lowest score: " + scores.first());
        System.out.println("Highest score: " + scores.last());

        // NavigableSet methods
        System.out.println("Floor of 80 (≤ 80): " + scores.floor(80));     // 61
        System.out.println("Ceiling of 80 (≥ 80): " + scores.ceiling(80)); // 87
        System.out.println("Scores below 90: " + scores.headSet(90));       // [42, 61, 87]
        System.out.println("Scores 60 and above: " + scores.tailSet(60));   // [61, 87, 95]

        // TreeSet with Strings — sorted alphabetically
        TreeSet<String> tags = new TreeSet<>(Set.of("java", "spring", "docker", "aws", "kafka"));
        System.out.println("Tags (alphabetical): " + tags);

        // Descending view
        System.out.println("Descending: " + tags.descendingSet());
    }

    // =========================================================================
    // SECTION 5: MAP — HashMap
    // =========================================================================
    // - Key-value store backed by a hash table
    // - Keys must be unique; values can repeat
    // - No ordering guarantee on keys
    // - O(1) get, put, remove (amortized)
    // - Allows one null key and multiple null values
    // =========================================================================
    static void demonstrateHashMap() {

        Map<String, Integer> wordCount = new HashMap<>();

        // --- put() ---
        wordCount.put("java", 15);
        wordCount.put("spring", 8);
        wordCount.put("docker", 5);
        wordCount.put("kafka", 3);
        wordCount.put("java", 20);    // KEY ALREADY EXISTS → value is REPLACED

        System.out.println("Word counts: " + wordCount);
        System.out.println("Count of 'java': " + wordCount.get("java")); // 20

        // --- get() with default ---
        int count = wordCount.getOrDefault("python", 0);
        System.out.println("Count of 'python' (default 0): " + count);

        // --- putIfAbsent() — only inserts if key NOT already present ---
        wordCount.putIfAbsent("java", 99);   // java already there → ignored
        wordCount.putIfAbsent("redis", 2);   // new key → inserted
        System.out.println("After putIfAbsent: " + wordCount);

        // --- containsKey / containsValue ---
        System.out.println("Has 'docker'? " + wordCount.containsKey("docker"));
        System.out.println("Has count 8? " + wordCount.containsValue(8));

        // --- remove() ---
        wordCount.remove("kafka");
        System.out.println("After removing 'kafka': " + wordCount);

        // --- Iterating over entries (key-value pairs) ---
        System.out.println("-- All entries --");
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            System.out.println("  " + entry.getKey() + " → " + entry.getValue());
        }

        // --- keySet() and values() ---
        System.out.println("Keys:   " + wordCount.keySet());
        System.out.println("Values: " + wordCount.values());

        // --- compute() — update based on current value ---
        wordCount.compute("spring", (key, oldVal) -> oldVal == null ? 1 : oldVal + 1);
        System.out.println("After compute on 'spring': " + wordCount.get("spring")); // 9

        // --- merge() — word frequency counting pattern ---
        String[] words = {"banana", "apple", "banana", "cherry", "apple", "banana"};
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            freq.merge(word, 1, Integer::sum);  // if absent: 1; else: current + 1
        }
        System.out.println("Word frequencies: " + freq);
    }

    // =========================================================================
    // SECTION 6: MAP — TreeMap
    // =========================================================================
    // - Backed by a Red-Black tree
    // - Keys are ALWAYS SORTED (natural order or custom Comparator)
    // - O(log n) get, put, remove
    // - Implements NavigableMap: firstKey(), lastKey(), headMap(), tailMap()
    // =========================================================================
    static void demonstrateTreeMap() {

        TreeMap<String, String> countryCapitals = new TreeMap<>();
        countryCapitals.put("Germany", "Berlin");
        countryCapitals.put("France", "Paris");
        countryCapitals.put("Japan", "Tokyo");
        countryCapitals.put("Brazil", "Brasília");
        countryCapitals.put("Australia", "Canberra");

        System.out.println("Countries (sorted by key): " + countryCapitals);
        System.out.println("First country: " + countryCapitals.firstKey());
        System.out.println("Last country:  " + countryCapitals.lastKey());

        // NavigableMap methods
        System.out.println("Countries before 'Japan': " + countryCapitals.headMap("Japan"));
        System.out.println("Countries from 'Japan' onward: " + countryCapitals.tailMap("Japan"));

        // Floor / Ceiling on keys
        System.out.println("Floor key of 'G': " + countryCapitals.floorKey("G"));     // France
        System.out.println("Ceiling key of 'G': " + countryCapitals.ceilingKey("G")); // Germany

        // Use case: sorted leaderboard
        TreeMap<Integer, String> leaderboard = new TreeMap<>(Collections.reverseOrder());
        leaderboard.put(2500, "Alice");
        leaderboard.put(3100, "Bob");
        leaderboard.put(2900, "Carol");
        leaderboard.put(3100, "Dave"); // same score → replaces Bob (keys must be unique)
        System.out.println("Leaderboard (highest first): " + leaderboard);
    }

    // =========================================================================
    // SECTION 7: MAP — LinkedHashMap
    // =========================================================================
    // - Backed by a hash table + doubly linked list
    // - Maintains INSERTION ORDER (unlike HashMap, unlike TreeMap's sort order)
    // - O(1) get, put, remove
    // - Can also maintain ACCESS ORDER (useful for LRU cache)
    // =========================================================================
    static void demonstrateLinkedHashMap() {

        // Insertion-order Map (default)
        Map<String, String> httpHeaders = new LinkedHashMap<>();
        httpHeaders.put("Content-Type", "application/json");
        httpHeaders.put("Authorization", "Bearer abc123");
        httpHeaders.put("Accept", "application/json");
        httpHeaders.put("X-Request-ID", "req-456");

        System.out.println("HTTP Headers (insertion order preserved):");
        httpHeaders.forEach((key, value) -> System.out.println("  " + key + ": " + value));

        // Access-order LinkedHashMap — most recently accessed goes to end
        // removeEldestEntry can be overridden to create an LRU cache
        Map<Integer, String> lruCache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                return size() > 3;   // evict oldest when size exceeds 3
            }
        };

        lruCache.put(1, "Page A");
        lruCache.put(2, "Page B");
        lruCache.put(3, "Page C");
        System.out.println("LRU cache (capacity 3): " + lruCache);

        lruCache.get(1);           // access key 1 → moves it to the end
        lruCache.put(4, "Page D"); // cache full → evicts LEAST recently accessed
        System.out.println("After accessing key 1 and adding Page D: " + lruCache);
        // key 2 is evicted (it was least recently accessed), key 1 survives
    }

    // =========================================================================
    // SECTION 8: QUEUE — ArrayDeque (and Queue interface)
    // =========================================================================
    // Queue: FIFO (First-In, First-Out) data structure
    // Deque: Double-Ended Queue — add/remove from both ends
    //
    // ArrayDeque:
    //   - Resizable array implementation of Deque
    //   - Faster than LinkedList for queue/stack use cases
    //   - Does NOT allow null elements
    //   - Use instead of Stack class (Stack is legacy/synchronized)
    // =========================================================================
    static void demonstrateQueue() {

        // --- Queue (FIFO) using ArrayDeque ---
        Queue<String> printQueue = new ArrayDeque<>();

        // offer() = enqueue (adds to tail). Prefer offer() over add() — returns false instead of throwing on full queues
        printQueue.offer("Report.pdf");
        printQueue.offer("Invoice.docx");
        printQueue.offer("Resume.pdf");
        printQueue.offer("Photo.png");

        System.out.println("Print queue: " + printQueue);
        System.out.println("Peek (next to print): " + printQueue.peek()); // doesn't remove

        // poll() = dequeue (removes from head). Returns null if empty (safe)
        System.out.println("Printing: " + printQueue.poll());
        System.out.println("Printing: " + printQueue.poll());
        System.out.println("Queue after 2 prints: " + printQueue);

        // --- Deque (Stack / both-ends) ---
        Deque<String> browserHistory = new ArrayDeque<>();
        browserHistory.push("google.com");       // addFirst (stack push)
        browserHistory.push("github.com");
        browserHistory.push("java.oracle.com");

        System.out.println("\nBrowser history (newest first): " + browserHistory);
        System.out.println("Back → " + browserHistory.pop());   // removeFirst
        System.out.println("Back → " + browserHistory.pop());
        System.out.println("Current page: " + browserHistory.peek());

        // --- PriorityQueue — elements come out in SORTED order ---
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();  // min first
        minHeap.offer(50);
        minHeap.offer(10);
        minHeap.offer(30);
        minHeap.offer(20);

        System.out.println("\nPriorityQueue peek (min): " + minHeap.peek()); // 10
        System.out.print("PriorityQueue poll order: ");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " ");  // 10, 20, 30, 50
        }
        System.out.println();

        // PriorityQueue with custom comparator (max-heap)
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        maxHeap.offer(50);
        maxHeap.offer(10);
        maxHeap.offer(30);
        System.out.print("MaxHeap poll order: ");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " ");  // 50, 30, 10
        }
        System.out.println();
    }

    // =========================================================================
    // SECTION 9: CHOOSING THE RIGHT COLLECTION
    // =========================================================================
    static void choosingTheRightCollection() {
        System.out.println("""
            ┌───────────────────────────────────────────────────────────────────────┐
            │                   CHOOSING THE RIGHT COLLECTION                      │
            ├─────────────────┬────────────────┬──────────┬────────────────────────┤
            │ Need            │ Use            │ Ordered? │ Notes                  │
            ├─────────────────┼────────────────┼──────────┼────────────────────────┤
            │ Ordered list,   │ ArrayList      │ Yes      │ Best for reads         │
            │ fast access     │                │          │                        │
            │ Frequent head/  │ LinkedList     │ Yes      │ Best for writes at ends│
            │ tail insert     │                │          │                        │
            │ No duplicates,  │ HashSet        │ No       │ O(1) membership check  │
            │ fast lookup     │                │          │                        │
            │ No duplicates,  │ TreeSet        │ Sorted   │ O(log n) operations    │
            │ sorted          │                │          │                        │
            │ Key-value,      │ HashMap        │ No       │ Most common map        │
            │ fast lookup     │                │          │                        │
            │ Key-value,      │ TreeMap        │ By key   │ NavigableMap features  │
            │ sorted by key   │                │          │                        │
            │ Key-value,      │ LinkedHashMap  │ Insertion│ LRU cache pattern      │
            │ insertion order │                │          │                        │
            │ FIFO queue      │ ArrayDeque     │ FIFO     │ Prefer over LinkedList │
            │ Priority order  │ PriorityQueue  │ By prio  │ Min-heap by default    │
            └─────────────────┴────────────────┴──────────┴────────────────────────┘
            """);
    }
}
