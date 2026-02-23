# Week 2 - Day 6 (Monday) Part 1: Collections Framework Overview & Basic Collections
## Java Collections: Managing Groups of Objects

---

## Slide 1: Welcome to Collections
**Visual:** Colorful containers/collection imagery; Java Collections Framework logo

Welcome to Week 2, Day 6. Last week, you mastered OOP—classes, inheritance, polymorphism, abstractions, packages. This week, we layer on advanced Java concepts. Today, we tackle Collections. Collections are fundamental to Java programming. Every real application needs to store and manipulate groups of data. Rather than creating arrays of fixed size, Java provides powerful collection types: lists, sets, maps, and queues. Today, you'll learn the Collections Framework. By day's end, you'll know when to use ArrayList vs LinkedList, HashSet vs TreeSet, HashMap vs TreeMap. You'll understand how to iterate and manipulate collections. This knowledge is essential for every Java programmer.

---

## Slide 2: Why Collections Matter
**Visual:** Problem: many arrays with fixed sizes; Solution: flexible collections

Before collections, Java programmers struggled. Want to store 10 items? Create an array of size 10. Want to add one more? Create a new array of size 11, copy all data, discard the old one. Error-prone and inefficient. Collections solve this. Arrays are fixed-size. Collections grow dynamically. You add items, they expand automatically. You remove items, they shrink. You iterate, sort, filter. Collections are the tools professionals use.

---

## Slide 3: Java Collections Framework Overview
**Visual:** Hierarchy diagram: Collection → List, Set, Queue; Map separate

The Collections Framework is a unified architecture for representing and manipulating collections. It includes interfaces (Collection, List, Set, Map, Queue), implementations (ArrayList, HashSet, HashMap), and algorithms (sort, search, shuffle). All standardized. All efficient. All battle-tested.

The framework has two main hierarchies:
1. **Collection interface**: Lists (ordered), Sets (unique), Queues (FIFO/priority)
2. **Map interface**: Key-value pairs (separate hierarchy)

---

## Slide 4: The Collection Interface Hierarchy
**Visual:** Detailed hierarchy tree showing Collection at top, List/Set/Queue below, implementations below each

```
Collection (interface)
├── List (ordered, allows duplicates)
│   ├── ArrayList (resizable array)
│   ├── LinkedList (doubly-linked list)
│   └── Vector (legacy, synchronized)
├── Set (unique items, no duplicates)
│   ├── HashSet (unordered, hash table)
│   ├── TreeSet (sorted, red-black tree)
│   └── LinkedHashSet (insertion-order, hash table)
└── Queue (FIFO, specialized access)
    ├── LinkedList (also a Queue)
    ├── PriorityQueue (priority-based)
    └── Deque (double-ended)
```

Each type has different performance characteristics. Choose wisely.

---

## Slide 5: Lists: Ordered Collections
**Visual:** Three boxes in order: 1, 2, 3; same order maintained

A List is an ordered collection. Items appear in the order you insert them. You can access items by index (position). Lists allow duplicates. Two main implementations:

**ArrayList**: Built on dynamic arrays. Fast random access (O(1)). Slow insertions in middle (O(n)).

**LinkedList**: Doubly-linked list. Slow random access (O(n)). Fast insertions at front or end (O(1)).

Use ArrayList for frequent random access. Use LinkedList for frequent insertions/deletions at endpoints.

---

## Slide 6: ArrayList Fundamentals
**Visual:** Dynamic array growing: [1] → [1, 2] → [1, 2, 3]

ArrayList is the workhorse. It's a resizable array. When you add items, it grows automatically.

```java
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
names.add("Charlie");

System.out.println(names.get(0));      // Alice
System.out.println(names.size());      // 3
names.remove(1);                        // Bob removed
```

ArrayList stores items sequentially in memory. Accessing by index is instant. But inserting in the middle requires shifting elements. For most use cases, ArrayList is the default choice.

---

## Slide 7: LinkedList Fundamentals
**Visual:** Linked boxes with pointers: [Alice|→] [Bob|→] [Charlie|null]

LinkedList is a doubly-linked list. Each element points to the previous and next elements. No contiguous memory required.

```java
List<String> names = new LinkedList<>();
names.add("Alice");
names.add("Bob");
names.add("Charlie");

names.remove(0);                        // Fast at front
names.addFirst("Zoe");                  // Add to front
names.addLast("David");                 // Add to back
```

LinkedList excels at insertions and deletions. But random access is slow—you must traverse from the beginning. Use LinkedList when you frequently add/remove at the front or end.

---

## Slide 8: ArrayList vs LinkedList: Performance
**Visual:** Comparison table with operation and Big O complexity

| Operation | ArrayList | LinkedList |
|-----------|---|---|
| Get by index | O(1) | O(n) |
| Add to end | O(1) amortized | O(1) |
| Remove from middle | O(n) | O(n) |
| Add to front | O(n) | O(1) |
| Remove from front | O(n) | O(1) |

**Rule of thumb:** Default to ArrayList. Switch to LinkedList only if you frequently add/remove at the front or you've profiled and identified it as a bottleneck.

---

## Slide 9: Sets: Unique Collections
**Visual:** Set showing only unique items: {1, 2, 3}, duplicate 2 rejected

A Set is an unordered collection with no duplicates. If you add the same item twice, the second add is ignored. Sets are useful for membership testing ("Is this item in the set?").

Two main implementations:

**HashSet**: Unordered. Very fast O(1) add, remove, contains. No guarantees on iteration order.

**TreeSet**: Sorted (natural order or custom comparator). O(log n) operations. Guarantees sorted iteration.

---

## Slide 10: HashSet Fundamentals
**Visual:** Hash table with scattered items; show constant-time lookup

HashSet uses a hash table internally. Each item is hashed to a bucket. Lookups, insertions, deletions are O(1) on average.

```java
Set<String> colors = new HashSet<>();
colors.add("Red");
colors.add("Green");
colors.add("Blue");
colors.add("Red");                      // Duplicate; ignored

System.out.println(colors.size());      // 3, not 4
System.out.println(colors.contains("Green"));  // true
```

Order is unpredictable. You might iterate as {Green, Red, Blue} or {Blue, Green, Red}. If you need order, use TreeSet or LinkedHashSet.

---

## Slide 11: TreeSet Fundamentals
**Visual:** Sorted tree structure; items in sorted order

TreeSet maintains elements in sorted order. By default, natural order (alphabetical for strings, numeric for integers). You can provide a custom comparator for custom sorting.

```java
Set<Integer> numbers = new TreeSet<>();
numbers.add(5);
numbers.add(2);
numbers.add(8);
numbers.add(2);                         // Duplicate; ignored

// Iteration is always in sorted order: 2, 5, 8
for (int n : numbers) {
    System.out.println(n);
}
```

TreeSet operations are O(log n) because it uses a red-black tree internally. Slightly slower than HashSet, but guarantees sorted order.

---

## Slide 12: LinkedHashSet: Predictable Order
**Visual:** Hash table combined with linked list for insertion order

LinkedHashSet is a hybrid. It uses a hash table (fast lookups like HashSet) but maintains insertion order (like a linked list).

```java
Set<String> order = new LinkedHashSet<>();
order.add("First");
order.add("Second");
order.add("Third");

// Iteration is in insertion order: First, Second, Third
for (String s : order) {
    System.out.println(s);
}
```

LinkedHashSet is useful when you want fast lookups and predictable iteration order. Not as fast as HashSet, but guarantees you'll iterate in insertion order.

---

## Slide 13: Maps: Key-Value Pairs
**Visual:** Telephone book analogy: name → phone number

A Map is a collection of key-value pairs. Think of a telephone book: names (keys) map to phone numbers (values). Maps allow fast lookup by key. No duplicate keys. Each key maps to exactly one value.

Main implementations:

**HashMap**: Unordered. O(1) average-case lookups. Fast but unpredictable iteration order.

**TreeMap**: Sorted by key. O(log n) operations. Guarantees sorted iteration.

**LinkedHashMap**: Maintains insertion order like LinkedHashSet.

---

## Slide 14: HashMap Fundamentals
**Visual:** Dictionary mapping keys to values; fast O(1) lookup

HashMap is the workhorse map. Fast insertion, deletion, lookup.

```java
Map<String, Integer> phoneBook = new HashMap<>();
phoneBook.put("Alice", 5551234);
phoneBook.put("Bob", 5555678);
phoneBook.put("Charlie", 5559999);

System.out.println(phoneBook.get("Alice"));        // 5551234
System.out.println(phoneBook.containsKey("Bob"));  // true
phoneBook.remove("Charlie");

for (String name : phoneBook.keySet()) {
    System.out.println(name + ": " + phoneBook.get(name));
}
```

HashMap is perfect for lookups. When you have a key and need the associated value instantly, HashMap delivers.

---

## Slide 15: TreeMap Fundamentals
**Visual:** Sorted binary search tree; keys in sorted order

TreeMap maintains keys in sorted order. Like TreeSet but for key-value pairs.

```java
Map<String, Integer> scores = new TreeMap<>();
scores.put("Charlie", 85);
scores.put("Alice", 95);
scores.put("Bob", 88);

// Iteration is in sorted key order: Alice, Bob, Charlie
for (String name : scores.keySet()) {
    System.out.println(name + ": " + scores.get(name));
}
```

TreeMap is useful when you need sorted iteration by key. Operations are O(log n). Use when sorting by key is important.

---

## Slide 16: LinkedHashMap: Insertion Order Maps
**Visual:** Hash table with linked structure maintaining insertion order

LinkedHashMap maintains insertion order while offering HashMap's O(1) lookups.

```java
Map<String, String> order = new LinkedHashMap<>();
order.put("First", "A");
order.put("Second", "B");
order.put("Third", "C");

// Iteration is in insertion order
for (String key : order.keySet()) {
    System.out.println(key);  // First, Second, Third
}
```

LinkedHashMap is useful when you want fast lookups and predictable iteration order. Common in caches and access-order maps.

---

## Slide 17: Queues: FIFO Collections
**Visual:** Queue structure: front [1, 2, 3] back; operations at different ends

A Queue is a FIFO (First-In-First-Out) collection. Items enter at the back, exit from the front. Think of a line at a store. Main operations:

- **offer(E)**: Add to back
- **poll()**: Remove from front
- **peek()**: View front without removing
- **add(E)**: Add to back (throws exception if full)
- **remove()**: Remove from front (throws exception if empty)

---

## Slide 18: LinkedList as Queue
**Visual:** Linked list with front/back operations

LinkedList implements the Queue interface. You can use it as a queue:

```java
Queue<String> queue = new LinkedList<>();
queue.offer("First");
queue.offer("Second");
queue.offer("Third");

System.out.println(queue.poll());   // First
System.out.println(queue.poll());   // Second
System.out.println(queue.peek());   // Third (doesn't remove)
```

LinkedList is perfect for queues because adding to the back and removing from the front are both O(1).

---

## Slide 19: PriorityQueue: Priority-Based Access
**Visual:** Queue with priorities; highest priority items exit first

PriorityQueue is not FIFO. Items exit based on priority, not insertion order. By default, natural ordering (smallest first for integers, alphabetical for strings). You can provide a custom comparator.

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(5);
pq.offer(2);
pq.offer(8);

System.out.println(pq.poll());   // 2 (smallest)
System.out.println(pq.poll());   // 5
System.out.println(pq.poll());   // 8
```

PriorityQueue is useful for task scheduling, Dijkstra's algorithm, and other applications where priority matters.

---

## Slide 20: Iterators: Traversing Collections
**Visual:** Iterator pattern: hasNext(), next()

An Iterator is an object that lets you traverse a collection one element at a time. All Collection types provide iterators.

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Iterator<String> iter = names.iterator();

while (iter.hasNext()) {
    String name = iter.next();
    System.out.println(name);
}
```

Iterators provide a uniform way to traverse any collection. You can use the same code with ArrayList, LinkedList, HashSet, etc.

---

## Slide 21: Enhanced For Loop (For-Each Loop)
**Visual:** Simplified syntax; syntactic sugar for iterators

The enhanced for loop is cleaner syntax for iteration. It's syntactic sugar for iterators.

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// This:
for (String name : names) {
    System.out.println(name);
}

// Is equivalent to:
for (Iterator<String> iter = names.iterator(); iter.hasNext();) {
    String name = iter.next();
    System.out.println(name);
}
```

The enhanced for loop works with any Collection. Use it whenever possible—it's cleaner and less error-prone.

---

## Slide 22: Safe Iteration: Avoiding ConcurrentModificationException
**Visual:** Red X on modification during iteration; green check on using iterator.remove()

A common mistake: modifying a collection while iterating over it. This throws ConcurrentModificationException.

```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

// WRONG:
for (int n : numbers) {
    if (n % 2 == 0) {
        numbers.remove(Integer.valueOf(n));  // CRASH!
    }
}

// CORRECT: Use iterator.remove()
Iterator<Integer> iter = numbers.iterator();
while (iter.hasNext()) {
    int n = iter.next();
    if (n % 2 == 0) {
        iter.remove();  // Safe!
    }
}
```

Use `iterator.remove()` to safely remove during iteration.

---

## Slide 23: Common Beginner Mistake: Confusing List and Array
**Visual:** Array with fixed size vs ArrayList growing

Arrays have fixed size. Lists grow dynamically. Beginners often confuse them.

```java
// ARRAY: Fixed size
String[] array = new String[3];
array[0] = "Alice";
array[1] = "Bob";
array[2] = "Charlie";
// array[3] = "David";  // ArrayIndexOutOfBoundsException!

// LIST: Dynamic size
List<String> list = new ArrayList<>();
list.add("Alice");
list.add("Bob");
list.add("Charlie");
list.add("David");  // Works fine! List grows.
```

Use List when size is unknown or changes. Use arrays only when size is truly fixed and known at compile time.

---

## Slide 24: Common Beginner Mistake: Using HashSet When Order Matters
**Visual:** Red X on HashSet with unpredictable order; green check on TreeSet with sorted order

HashSet doesn't guarantee order. If you iterate twice, you might get different orders (though unlikely in practice). Beginners rely on HashSet when they need sorted order.

```java
// WRONG: Relying on HashSet order
Set<Integer> numbers = new HashSet<>();
numbers.add(3);
numbers.add(1);
numbers.add(2);
// Iteration order is unpredictable!

// CORRECT: Use TreeSet for sorted order
Set<Integer> numbers = new TreeSet<>();
numbers.add(3);
numbers.add(1);
numbers.add(2);
// Iteration order is always 1, 2, 3
```

If order matters, use TreeSet or LinkedHashSet. If order doesn't matter, HashSet is fastest.

---

## Slide 25: Common Beginner Mistake: Modifying Map While Iterating
**Visual:** Red X on modifying map during iteration

Like lists, modifying a map while iterating causes ConcurrentModificationException.

```java
// WRONG:
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 87);
scores.put("Charlie", 92);

for (String name : scores.keySet()) {
    if (scores.get(name) < 90) {
        scores.remove(name);  // CRASH!
    }
}

// CORRECT: Use iterator or create a copy
Set<String> keysToRemove = new HashSet<>();
for (String name : scores.keySet()) {
    if (scores.get(name) < 90) {
        keysToRemove.add(name);
    }
}
scores.keySet().removeAll(keysToRemove);  // Safe!
```

Either collect items to remove then remove them afterward, or use an iterator.

---

## Slide 26: Choosing the Right Collection: Decision Matrix
**Visual:** Flowchart: Need unique? → Set or Map; Order matters? → TreeSet; Need fast lookups? → HashMap

Decision-making guide:

1. **Storing a list of items?** Use List
   - Need fast random access? ArrayList (default)
   - Frequent add/remove at front or back? LinkedList

2. **Storing unique items?** Use Set
   - Need order? TreeSet (sorted) or LinkedHashSet (insertion order)
   - Don't need order? HashSet (fastest)

3. **Key-value pairs?** Use Map
   - Need fast lookups? HashMap (default)
   - Need sorted by key? TreeMap
   - Need insertion order? LinkedHashMap

4. **Queue behavior?** Use Queue
   - Normal FIFO? LinkedList
   - Priority-based? PriorityQueue

---

## Slide 27: Performance Summary: Big O Cheat Sheet
**Visual:** Table with all collections and their operations

| Collection | Get | Add | Remove | Contains |
|-----------|---|---|---|---|
| ArrayList | O(1) | O(1) amortized | O(n) | O(n) |
| LinkedList | O(n) | O(1) | O(n) | O(n) |
| HashSet | — | O(1) | O(1) | O(1) |
| TreeSet | — | O(log n) | O(log n) | O(log n) |
| HashMap | — | O(1) | O(1) | O(1) |
| TreeMap | — | O(log n) | O(log n) | O(log n) |
| PriorityQueue | — | O(log n) | O(log n) | — |

(— indicates operation not applicable for that collection type)

---

## Slide 28: Real-World Example: Managing a Task List
**Visual:** Task list app: add tasks, remove completed, iterate, check for duplicates

```java
// Task list: ordered items, no duplicates required
List<String> tasks = new ArrayList<>();
tasks.add("Email client");
tasks.add("Debug login");
tasks.add("Write tests");
tasks.add("Code review");

// Remove a task
tasks.remove("Email client");

// Display all tasks
for (String task : tasks) {
    System.out.println("- " + task);
}

// Check if a task exists (inefficient with List)
System.out.println(tasks.contains("Debug login"));  // true
```

For this use case, ArrayList is perfect. It maintains order and allows easy iteration.

---

## Slide 29: Real-World Example: Managing Unique Tags
**Visual:** Set of tags: {Java, OOP, Collections}; duplicate Java rejected

```java
// Tags: unique items, order doesn't matter
Set<String> tags = new HashSet<>();
tags.add("Java");
tags.add("OOP");
tags.add("Collections");
tags.add("Java");        // Duplicate; ignored

// Count unique tags
System.out.println("Unique tags: " + tags.size());  // 3

// Check for tag
System.out.println(tags.contains("Java"));  // true

// Add multiple tags
tags.addAll(Arrays.asList("Generics", "Spring"));
```

HashSet is perfect here. You want unique items and don't care about order. O(1) contains() checks are fast.

---

## Slide 30: Real-World Example: Student Records Lookup
**Visual:** Map: student ID → Student object; fast lookup

```java
// Student records: ID to student object
Map<Integer, String> studentNames = new HashMap<>();
studentNames.put(101, "Alice Johnson");
studentNames.put(102, "Bob Smith");
studentNames.put(103, "Charlie Brown");

// Fast lookup by ID
System.out.println(studentNames.get(102));  // Bob Smith

// Check enrollment
System.out.println(studentNames.containsKey(101));  // true

// Update record
studentNames.put(102, "Bob Lee Smith");

// Remove from enrollment
studentNames.remove(103);
```

HashMap provides O(1) lookup by student ID. Perfect for databases and lookup tables.

---

## Slide 31: Recap: Collections Framework Architecture
**Visual:** Hierarchy diagram again, now with context

The Collections Framework provides a unified architecture:

- **Interfaces**: Contract for collection behavior
- **Implementations**: Concrete classes (ArrayList, HashSet, HashMap, etc.)
- **Algorithms**: Reusable operations (sort, search, shuffle)
- **Iterators**: Uniform traversal
- **Performance**: Predictable Big O complexity

Master the framework, and you'll write efficient, maintainable Java code.

---

## Slide 32: Part 1 Recap: Key Takeaways
**Visual:** Checklist of concepts

- ✓ Collections Framework overview and hierarchy
- ✓ Lists: ArrayList (fast random access), LinkedList (fast add/remove at endpoints)
- ✓ Sets: HashSet (fast, unordered), TreeSet (sorted), LinkedHashSet (insertion order)
- ✓ Maps: HashMap (fast lookups), TreeMap (sorted), LinkedHashMap (insertion order)
- ✓ Queues: LinkedList and PriorityQueue
- ✓ Iterators and enhanced for loops
- ✓ Safe iteration and modification
- ✓ Performance characteristics and decision-making

You now understand the core collections. In Part 2, we'll add Generics—type safety for collections.

---

## Slide 33: Preview: Part 2 (Generics)
**Visual:** Generic type syntax: List<String>, Map<String, Integer>

In Part 2, we'll ensure type safety. Generics let you specify exactly what types collections hold. Instead of `List` (which could hold anything), you write `List<String>` (which holds only strings). This prevents bugs and makes code clearer. You'll learn generic syntax, bounded types, wildcards, and `Comparable` vs `Comparator` for sorting. Get ready to write safer, clearer Java code!

---

## Slide 34: Word to the Wise: Know Your Collections
**Visual:** Collections are tools; choosing wisely matters

Collections are tools. Choosing the right one matters. ArrayList by default. Switch when you have a reason. HashSet for unique items. TreeSet when you need sorted order. HashMap for lookups. You've now learned the foundation. In real projects, you'll find scenarios where one collection outperforms another by 10x or 100x. That knowledge comes from experience. For now, practice with these fundamental types. Master them, and you'll have a superpower in Java programming.

---
