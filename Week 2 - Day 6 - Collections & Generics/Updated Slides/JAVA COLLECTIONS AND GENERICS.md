# Java Collections and Generics - 60-Minute Lecture Script

## SLIDE 1: Title Slide
**Content:** Java Collections Framework & Generics
**Duration:** 1 minute

**Script:**
"Good morning/afternoon everyone! Today we're diving into one of the most essential topics in Java programming - the Collections Framework and Generics. By the end of this session, you'll understand how to store, organize, and manipulate groups of objects efficiently, and how to write type-safe code using generics. Let's get started!"

---

## SLIDE 2: Learning Objectives
**Content:**
- Understand the Java Collections Framework architecture
- Master List, Set, Map, and Queue interfaces
- Learn when to use ArrayList vs LinkedList, HashSet vs TreeSet
- Implement Generics for type safety
- Use Comparable and Comparator for sorting
- Apply Collections utility methods

**Duration:** 2 minutes

**Script:**
"Here are our learning objectives for today. We'll explore the Collections Framework, understand the different interfaces and their implementations, learn how generics provide type safety, and see how to sort and manipulate collections effectively. This is fundamental knowledge you'll use in almost every Java project you work on."

---

## SLIDE 3: Why Collections?
**Content:**
- Arrays are fixed size
- Need dynamic, resizable data structures
- Common operations: add, remove, search, sort
- Type safety with Generics
- Built-in algorithms and utilities

**Duration:** 2 minutes

**Script:**
"Before we had Collections, we primarily used arrays. But arrays have a major limitation - they're fixed size. What happens when you don't know how many elements you'll need? Or when you need to insert or remove elements frequently? This is where Collections come in. They provide dynamic, resizable data structures with built-in methods for common operations. And with Generics, we get compile-time type safety, catching errors before runtime."

---

## SLIDE 4: Collections Framework Hierarchy
**Content:**
```
Collection (interface)
├── List (interface)
│   ├── ArrayList
│   └── LinkedList
├── Set (interface)
│   ├── HashSet
│   └── TreeSet
└── Queue (interface)
    ├── LinkedList
    └── Deque (interface)
        └── ArrayDeque

Map (interface) - separate hierarchy
├── HashMap
├── TreeMap
└── LinkedHashMap
```

**Duration:** 3 minutes

**Script:**
"The Collections Framework has a clear hierarchy. At the top, we have the Collection interface, which extends into List, Set, and Queue. Notice that Map is separate - it's not a true Collection because it stores key-value pairs, not single elements.

Lists maintain insertion order and allow duplicates. Sets don't allow duplicates and may or may not maintain order. Queues follow FIFO or priority ordering. Maps associate keys with values. You'll also notice Deque in the Queue branch - we'll cover that specifically later as it's an important and flexible interface. Each has specific implementations optimized for different use cases. Keep this hierarchy in mind as we explore each interface."

---

## SLIDE 5: The List Interface
**Content:**
- Ordered collection (maintains insertion order)
- Allows duplicate elements
- Index-based access (like arrays)
- Key methods: add(), get(), set(), remove(), size()
- Main implementations: ArrayList, LinkedList

**Duration:** 2 minutes

**Script:**
"Let's start with List. A List is an ordered collection that maintains insertion order and allows duplicates. You can access elements by index, just like arrays. The key difference? Lists are dynamic - they grow and shrink as needed. The most common implementations are ArrayList and LinkedList, and choosing between them matters for performance."

---

## SLIDE 6: ArrayList - The Workhorse
**Content:**
```java
// Creating an ArrayList with Generics
List<String> names = new ArrayList<>();

// Adding elements
names.add("Alice");
names.add("Bob");
names.add("Charlie");

// Accessing elements
String first = names.get(0); // "Alice"

// Modifying
names.set(1, "Robert"); // Replace Bob

// Removing
names.remove(0); // Removes Alice
names.remove("Charlie"); // Removes by value
```

**Characteristics:**
- Backed by resizable array
- Fast random access: O(1)
- Slow insertion/deletion in middle: O(n)
- Best for: frequent access, rare modifications

**Duration:** 4 minutes

**Script:**
"ArrayList is your go-to List implementation 90% of the time. It's backed by a resizable array internally. Notice how we use Generics - the angle brackets with String tell Java this ArrayList only holds Strings. This is type safety - you can't accidentally add an Integer.

ArrayList excels at random access - getting an element by index is O(1), constant time. However, inserting or removing from the middle requires shifting elements, which is O(n). Use ArrayList when you need frequent access and few modifications in the middle of the list."

---

## SLIDE 7: LinkedList - The Specialist
**Content:**
```java
List<String> tasks = new LinkedList<>();
tasks.add("Task 1");
tasks.add("Task 2");
tasks.add(0, "Urgent Task"); // Insert at beginning

// LinkedList as Queue
Queue<String> queue = new LinkedList<>();
queue.offer("First");
queue.offer("Second");
String processed = queue.poll(); // "First"
```

**Characteristics:**
- Doubly-linked node structure
- Fast insertion/deletion at ends: O(1)
- Slow random access: O(n)
- Best for: frequent insertions/deletions, queue operations

**Duration:** 3 minutes

**Script:**
"LinkedList is a doubly-linked list where each element points to the next and previous elements. This makes insertions and deletions at the beginning or end very fast - O(1). But random access requires traversing the list, so it's O(n).

Use LinkedList when you're frequently adding or removing from the ends, or when you're implementing a queue. Notice LinkedList implements both List and Queue interfaces, making it versatile. In most other cases, ArrayList is faster due to better memory locality."

---

## SLIDE 8: ArrayList vs LinkedList - When to Use
**Content:**
| Operation | ArrayList | LinkedList |
|-----------|-----------|------------|
| get(index) | O(1) ⚡ | O(n) 🐌 |
| add(end) | O(1)* | O(1) |
| add(index) | O(n) | O(n) |
| remove(index) | O(n) | O(n) |
| add/remove (start) | O(n) | O(1) ⚡ |

**Rule of Thumb:**
- Default to ArrayList
- Use LinkedList for: queues, frequent start/end modifications

**Duration:** 2 minutes

**Script:**
"Here's the performance comparison. ArrayList wins for random access - that's why it's the default choice. LinkedList shines only when you're frequently modifying the start or end, or implementing queue behavior. In practice, unless you have a specific reason, use ArrayList. The performance difference for most operations isn't worth the memory overhead of LinkedList's node structure."

---

## SLIDE 9: The Set Interface
**Content:**
- No duplicate elements
- No guaranteed order (except TreeSet, LinkedHashSet)
- Modeling mathematical sets
- Key methods: add(), remove(), contains(), size()
- Main implementations: HashSet, TreeSet, LinkedHashSet

**Duration:** 2 minutes

**Script:**
"Moving to Set - the interface for collections with no duplicates. If you try to add a duplicate, it simply won't be added. Sets are perfect for membership testing, removing duplicates, and mathematical set operations. The three main implementations differ in ordering and performance characteristics."

---

## SLIDE 10: HashSet - Fast and Unordered
**Content:**
```java
Set<String> uniqueNames = new HashSet<>();
uniqueNames.add("Alice");
uniqueNames.add("Bob");
uniqueNames.add("Alice"); // Won't be added - duplicate

System.out.println(uniqueNames.size()); // 2

// Fast membership testing
boolean hasAlice = uniqueNames.contains("Alice"); // true
```

**Characteristics:**
- Backed by HashMap
- No guaranteed order
- O(1) add, remove, contains (average case)
- Best for: fast lookup, no ordering needed

**Duration:** 3 minutes

**Script:**
"HashSet is the fastest Set implementation. It uses a hash table internally, giving you constant-time performance for add, remove, and contains operations. The trade-off? No ordering. Elements appear in whatever order the hash function determines.

Use HashSet when you need fast membership testing and don't care about order. It's perfect for removing duplicates or checking if something exists in a collection. Notice how adding 'Alice' twice only results in one entry."

---

## SLIDE 11: TreeSet - Sorted Order
**Content:**
```java
Set<Integer> sortedNumbers = new TreeSet<>();
sortedNumbers.add(5);
sortedNumbers.add(1);
sortedNumbers.add(9);
sortedNumbers.add(3);

// Always maintains sorted order
System.out.println(sortedNumbers); // [1, 3, 5, 9]

// Range operations
SortedSet<Integer> subset = sortedNumbers.subSet(2, 6);
System.out.println(subset); // [3, 5]
```

**Characteristics:**
- Backed by Red-Black tree
- Elements always sorted (natural order or Comparator)
- O(log n) operations
- Best for: sorted data, range queries

**Duration:** 3 minutes

**Script:**
"TreeSet maintains elements in sorted order automatically. It uses a Red-Black tree, a balanced binary search tree, which gives O(log n) performance. Elements are sorted using their natural ordering - for numbers, that's ascending order - or you can provide a custom Comparator.

TreeSet is your choice when you need sorted data or want to perform range queries. The subSet method lets you extract ranges efficiently. The cost? Slower than HashSet, but you get ordering for free."

---

## SLIDE 12: LinkedHashSet - Insertion Order
**Content:**
```java
Set<String> orderedSet = new LinkedHashSet<>();
orderedSet.add("First");
orderedSet.add("Second");
orderedSet.add("Third");

// Maintains insertion order
for (String s : orderedSet) {
    System.out.println(s); // First, Second, Third
}
```

**Characteristics:**
- Hybrid of HashSet and LinkedList
- Maintains insertion order
- Slightly slower than HashSet
- Best for: need both uniqueness and insertion order

**Duration:** 2 minutes

**Script:**
"LinkedHashSet is the middle ground - it maintains insertion order while preventing duplicates. It's essentially a HashSet with a linked list running through it. Performance is slightly slower than HashSet but faster than TreeSet. Use it when you need to maintain the order elements were added while ensuring uniqueness - like tracking the order users registered."

---

## SLIDE 13: The Map Interface
**Content:**
- Stores key-value pairs
- Keys must be unique
- One value per key
- Not a true Collection (separate hierarchy)
- Key methods: put(), get(), remove(), containsKey(), keySet(), values(), entrySet()
- Main implementations: HashMap, TreeMap, LinkedHashMap

**Duration:** 2 minutes

**Script:**
"Now let's talk about Maps - one of the most used data structures in Java. Maps store associations between keys and values. Each key maps to exactly one value, and keys must be unique. If you put a value with an existing key, it replaces the old value.

Maps aren't technically Collections since they store pairs, not single elements. But they're crucial - think of them as dictionaries or lookup tables."

---

## SLIDE 14: HashMap - The Default Map
**Content:**
```java
Map<String, Integer> ages = new HashMap<>();

// Adding key-value pairs
ages.put("Alice", 25);
ages.put("Bob", 30);
ages.put("Charlie", 35);

// Retrieving values
int aliceAge = ages.get("Alice"); // 25

// Updating
ages.put("Alice", 26); // Replaces old value

// Checking existence
boolean hasAlice = ages.containsKey("Alice"); // true

// Iterating
for (Map.Entry<String, Integer> entry : ages.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

**Characteristics:**
- O(1) average performance
- No ordering
- Best for: most mapping needs

**Duration:** 4 minutes

**Script:**
"HashMap is your default Map implementation. It provides constant-time performance for get and put operations. Notice the Generics syntax - HashMap<String, Integer> means keys are Strings and values are Integers. Type safety at compile time!

The put method adds or updates entries. If the key exists, the value is replaced. The get method retrieves values by key. For iteration, we use entrySet() which returns key-value pairs. HashMap is unordered - don't rely on any particular iteration order."

---

## SLIDE 15: TreeMap - Sorted Keys
**Content:**
```java
Map<String, String> sortedMap = new TreeMap<>();
sortedMap.put("Zebra", "Black and White");
sortedMap.put("Ant", "Small");
sortedMap.put("Monkey", "Playful");

// Keys are automatically sorted
for (String key : sortedMap.keySet()) {
    System.out.println(key); // Ant, Monkey, Zebra
}

// Range operations
SortedMap<String, String> subMap = sortedMap.subMap("B", "N");
```

**Characteristics:**
- Keys always sorted
- O(log n) operations
- Best for: sorted key access, range queries

**Duration:** 2 minutes

**Script:**
"TreeMap keeps keys in sorted order, just like TreeSet keeps elements sorted. It's backed by a Red-Black tree with O(log n) performance. Use TreeMap when you need sorted key iteration or range operations on keys. Common use case: implementing a phone book that's always alphabetically sorted."

---

## SLIDE 16: LinkedHashMap - Insertion Order
**Content:**
```java
Map<String, Integer> orderedMap = new LinkedHashMap<>();
orderedMap.put("First", 1);
orderedMap.put("Second", 2);
orderedMap.put("Third", 3);

// Maintains insertion order
for (String key : orderedMap.keySet()) {
    System.out.println(key); // First, Second, Third
}

// LRU Cache capability
Map<String, String> lruCache = new LinkedHashMap<>(16, 0.75f, true);
```

**Characteristics:**
- Maintains insertion order (or access order)
- Slightly slower than HashMap
- Best for: ordered maps, LRU caches

**Duration:** 2 minutes

**Script:**
"LinkedHashMap maintains insertion order, making iteration predictable. It can also maintain access order, making it perfect for implementing LRU (Least Recently Used) caches. This is slightly slower than HashMap but faster than TreeMap. Use it when you need predictable iteration order or are building a cache."

---

## SLIDE 17: The Queue Interface
**Content:**
- FIFO (First-In-First-Out) ordering
- Or priority ordering (PriorityQueue)
- Key methods: offer(), poll(), peek()
- offer: adds element (returns false if full)
- poll: removes and returns head (returns null if empty)
- peek: returns head without removing (returns null if empty)

**Duration:** 2 minutes

**Script:**
"Queues represent waiting lines - first in, first out. The Queue interface provides special methods that don't throw exceptions: offer instead of add, poll instead of remove, and peek to look at the head without removing it. These are safer for bounded queues where operations might fail."

---

## SLIDE 18: Queue Example
**Content:**
```java
Queue<String> queue = new LinkedList<>();

// Adding elements
queue.offer("First Customer");
queue.offer("Second Customer");
queue.offer("Third Customer");

// Processing queue
while (!queue.isEmpty()) {
    String customer = queue.poll();
    System.out.println("Processing: " + customer);
}

// PriorityQueue - natural ordering
Queue<Integer> priorityQueue = new PriorityQueue<>();
priorityQueue.offer(5);
priorityQueue.offer(1);
priorityQueue.offer(3);
System.out.println(priorityQueue.poll()); // 1 (smallest first)
```

**Duration:** 3 minutes

**Script:**
"Here's a typical queue usage pattern. We offer elements to the back and poll from the front. The while loop processes all elements in order. PriorityQueue is special - it orders elements by priority rather than insertion order. By default, it uses natural ordering, so smallest numbers come out first. You can provide a Comparator for custom priority ordering."

---

## SLIDE 19: The Deque Interface - Double-Ended Queue
**Content:**
- **Deque** = Double-Ended Queue (pronounced "deck")
- Can add/remove from **both** ends
- Acts as both a Queue (FIFO) AND a Stack (LIFO)
- Key implementations: **ArrayDeque**, LinkedList
- Preferred over Stack class for stack operations

**Key Methods:**
| Front of Deque | Back of Deque |
|---|---|
| addFirst() / offerFirst() | addLast() / offerLast() |
| removeFirst() / pollFirst() | removeLast() / pollLast() |
| peekFirst() | peekLast() |

**Duration:** 3 minutes

**Script:**
"The Deque interface is one you'll encounter frequently in interviews and real codebases, so pay close attention. Deque stands for Double-Ended Queue - you can add and remove elements from either end. This makes it extremely versatile: it can function as a standard FIFO queue, or as a stack with last-in-first-out behavior.

You'll notice it has symmetric method pairs - offerFirst and offerLast, pollFirst and pollLast. This naming makes it very clear which end of the deque you're working with. The preferred implementation is ArrayDeque, which we'll look at on the next slide."

---

## SLIDE 20: ArrayDeque - The Preferred Stack and Queue
**Content:**
```java
// As a Stack (LIFO)
Deque<String> stack = new ArrayDeque<>();
stack.push("First");   // pushes to front
stack.push("Second");
stack.push("Third");
System.out.println(stack.pop());  // "Third" - last in, first out
System.out.println(stack.peek()); // "Second" - look without removing

// As a Queue (FIFO)
Deque<String> queue = new ArrayDeque<>();
queue.offer("First");   // adds to back
queue.offer("Second");
queue.offer("Third");
System.out.println(queue.poll()); // "First" - first in, first out
```

**Why ArrayDeque over Stack?**
- Java's `Stack` class is a legacy class (extends Vector) — avoid it
- ArrayDeque is faster, has no synchronization overhead
- More memory-efficient than LinkedList for the same operations

**Duration:** 3 minutes

**Script:**
"ArrayDeque is the implementation you should reach for when you need either a stack or a queue. Java has an old Stack class, but it's considered legacy - it extends Vector, which is synchronized and slow. Never use Stack in new code.

ArrayDeque uses a resizable array internally. As a stack, push and pop from the front. As a queue, offer to the back and poll from the front. It's faster than LinkedList for both use cases because arrays have better memory locality than linked nodes. This is one of those things that comes up in technical interviews constantly - knowing that ArrayDeque is the modern replacement for both Stack and LinkedList-as-queue will serve you well."

---

## SLIDE 21: Iterators and Enhanced For Loop
**Content:**
```java
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");

// Enhanced for loop (for-each)
for (String item : list) {
    System.out.println(item);
}

// Iterator - more control
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String item = iterator.next();
    if (item.equals("B")) {
        iterator.remove(); // Safe removal during iteration
    }
}

// For Maps
Map<String, Integer> map = new HashMap<>();
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

**Duration:** 3 minutes

**Script:**
"There are two main ways to iterate collections. The enhanced for loop is cleaner and preferred for simple iteration. Behind the scenes, it uses an Iterator. When you need more control, especially for removing elements during iteration, use Iterator explicitly. Never modify a collection directly while iterating with for-each - you'll get a ConcurrentModificationException. Use iterator.remove() instead."

---

## SLIDE 22: Choosing the Right Collection
**Content:**
| Need | Use |
|------|-----|
| Fast random access | ArrayList |
| Frequent insertions at start/end | LinkedList |
| No duplicates, fast lookup | HashSet |
| No duplicates, sorted | TreeSet |
| No duplicates, insertion order | LinkedHashSet |
| Key-value pairs, fast lookup | HashMap |
| Key-value pairs, sorted keys | TreeMap |
| Key-value pairs, insertion order | LinkedHashMap |
| FIFO queue | ArrayDeque |
| Stack (LIFO) | ArrayDeque |
| Priority queue | PriorityQueue |

**Duration:** 2 minutes

**Script:**
"Here's your cheat sheet for choosing collections. Start with these defaults: ArrayList for lists, HashSet for sets, HashMap for maps. Switch to Tree versions when you need sorting, or Linked versions when insertion order matters. Notice that ArrayDeque now covers both FIFO queue and stack use cases - this is the modern recommendation. This decision tree will serve you well in 95% of situations."

---

## SLIDE 23: Introduction to Generics
**Content:**
- Type parameters for classes and methods
- Compile-time type safety
- Eliminate casting
- Code reusability

**Before Generics (Java 1.4):**
```java
List list = new ArrayList();
list.add("Hello");
list.add(123); // No compile error!
String s = (String) list.get(0); // Cast required
String s2 = (String) list.get(1); // Runtime error!
```

**With Generics:**
```java
List<String> list = new ArrayList<>();
list.add("Hello");
list.add(123); // Compile error - type safety!
String s = list.get(0); // No cast needed
```

**Duration:** 3 minutes

**Script:**
"Now let's talk about Generics - one of Java's most important features. Before generics, collections stored Objects, requiring casts and risking runtime errors. With generics, you declare the type at compile time, catching errors early.

The angle brackets contain type parameters. ArrayList<String> means it only holds Strings. Try to add an Integer? Compile error. This is type safety - finding bugs at compile time, not runtime. And no more casting!"

---

## SLIDE 24: Generic Class Syntax
**Content:**
```java
// Generic class with type parameter T
public class Box<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
}

// Usage
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String s = stringBox.get(); // No cast!

Box<Integer> intBox = new Box<>();
intBox.set(123);
Integer i = intBox.get();
```

**Duration:** 2 minutes

**Script:**
"You can create your own generic classes using type parameters. T is conventionally used for 'Type', though you can use any letter. The type parameter acts as a placeholder that gets replaced when you create an instance.

Box<String> replaces all T's with String. Box<Integer> replaces them with Integer. Same class, different types - that's code reusability with type safety."

---

## SLIDE 25: Generic Methods
**Content:**
```java
// Generic method - note <T> before return type
public static <T> void printArray(T[] array) {
    for (T element : array) {
        System.out.print(element + " ");
    }
    System.out.println();
}

// Usage
Integer[] intArray = {1, 2, 3};
String[] stringArray = {"A", "B", "C"};

printArray(intArray);    // Works with Integer[]
printArray(stringArray); // Works with String[]

// Generic method with return type
public static <T> T getFirstElement(List<T> list) {
    return list.isEmpty() ? null : list.get(0);
}
```

**Duration:** 2 minutes

**Script:**
"You can also create generic methods. The type parameter goes before the return type. This method works with any type of array. Java infers the type from the arguments - you don't need to specify it explicitly.

Generic methods are powerful for utility functions that work with different types. The Collections class is full of them."

---

## SLIDE 26: Bounded Type Parameters
**Content:**
```java
// Upper bound - T must be Number or subclass
public class NumberBox<T extends Number> {
    private T number;
    
    public double getDoubleValue() {
        return number.doubleValue(); // Can call Number methods
    }
}

// Usage
NumberBox<Integer> intBox = new NumberBox<>(); // OK
NumberBox<Double> doubleBox = new NumberBox<>(); // OK
NumberBox<String> stringBox = new NumberBox<>(); // Compile error!

// Multiple bounds
public class MultiBox<T extends Number & Comparable<T>> {
    // T must be both Number and Comparable
}
```

**Duration:** 3 minutes

**Script:**
"Sometimes you need to constrain type parameters. The 'extends' keyword creates an upper bound. T extends Number means T must be Number or any subclass like Integer or Double. This lets you call Number methods on T.

You can have multiple bounds using ampersand. T extends Number & Comparable means T must extend Number AND implement Comparable. This gives you access to methods from both."

---

## SLIDE 27: Wildcards - ? Unknown Type
**Content:**
```java
// Unbounded wildcard
public void printList(List<?> list) {
    for (Object obj : list) {
        System.out.println(obj);
    }
}

// Works with any type of List
printList(new ArrayList<String>());
printList(new ArrayList<Integer>());

// Upper bounded wildcard (? extends)
public double sumList(List<? extends Number> list) {
    double sum = 0;
    for (Number num : list) {
        sum += num.doubleValue();
    }
    return sum;
}
```

**Duration:** 3 minutes

**Script:**
"Wildcards use the question mark for unknown types. There are three kinds, and the first two are the most common. Unbounded (?) accepts any type but you can only read elements as Object. Upper bounded - using 'extends' - means the list holds some subtype of Number, so you can safely read values as Number. This is perfect for methods that only need to read from a collection."

---

## SLIDE 28: Wildcards Continued - Lower Bounded and PECS
**Content:**
```java
// Lower bounded wildcard (? super)
public void addNumbers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
}

// Works with Integer and superclasses
addNumbers(new ArrayList<Integer>());
addNumbers(new ArrayList<Number>());
addNumbers(new ArrayList<Object>());
```

**PECS - Producer Extends, Consumer Super:**
- Use `? extends T` when the collection **produces** (you read from it)
- Use `? super T` when the collection **consumes** (you write to it)
- Use unbounded `?` when you only call methods on Object

**Duration:** 2 minutes

**Script:**
"Lower bounded wildcards - using 'super' - mean the list can hold Integer or any of its superclasses. This is safe for writing because you know the list can at minimum accept an Integer.

A useful mnemonic is PECS: Producer Extends, Consumer Super. If a collection is producing values you'll read, use extends. If it's consuming values you'll write, use super. This rule will make wildcards feel intuitive once it clicks."

---

## SLIDE 29: Comparable vs Comparator
**Content:**
**Comparable:**
- Interface with compareTo() method
- Defines "natural ordering"
- Implemented by the class itself
- Single sorting logic

**Comparator:**
- Separate comparison object
- compare() method
- External to the class
- Multiple sorting strategies

**Duration:** 2 minutes

**Script:**
"For sorting, Java provides two approaches. Comparable defines the natural ordering of a class by implementing compareTo(). The class itself decides how to compare. String implements Comparable, so strings sort alphabetically by default.

Comparator is a separate object that defines how to compare. Use it for alternative sort orders or when you can't modify the class. One class can have many Comparators for different sorting strategies."

---

## SLIDE 30: Comparable Example
**Content:**
```java
public class Student implements Comparable<Student> {
    private String name;
    private int grade;
    
    public Student(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }
    
    @Override
    public int compareTo(Student other) {
        // Natural order: sort by grade
        return Integer.compare(this.grade, other.grade);
    }
    
    // Getters...
}

// Usage
List<Student> students = new ArrayList<>();
students.add(new Student("Alice", 85));
students.add(new Student("Bob", 92));
students.add(new Student("Charlie", 78));

Collections.sort(students); // Uses compareTo()
// Result: Charlie(78), Alice(85), Bob(92)
```

**Duration:** 3 minutes

**Script:**
"Here's Comparable in action. Student implements Comparable<Student> and defines compareTo(). The method returns negative if this object is less than the other, positive if greater, zero if equal.

We're sorting by grade using Integer.compare(), which handles the math for us. Now Collections.sort() knows how to sort Students naturally. One implementation, one sort order."

---

## SLIDE 31: Comparator Example
**Content:**
```java
// Modern lambda syntax (Java 8+)
Comparator<Student> nameComparator = 
    (s1, s2) -> s1.getName().compareTo(s2.getName());

// Even cleaner with method reference
Comparator<Student> byName = Comparator.comparing(Student::getName);

// Usage
Collections.sort(students, byName);
// Result: Alice, Bob, Charlie (alphabetical)

// Reverse order
Collections.sort(students, byName.reversed());

// Multiple criteria - sort by grade, then name as tiebreaker
Comparator<Student> multiComparator = 
    Comparator.comparing(Student::getGrade)
              .thenComparing(Student::getName);
```

**Duration:** 3 minutes

**Script:**
"Comparators give you flexibility. You can define multiple comparison strategies without modifying the class. Here we have a name-based comparator as an alternative to grade-based natural ordering.

Java 8 lambdas make this elegant. Comparator.comparing() creates a comparator from a getter method. You can reverse it, chain comparators for multi-level sorting, and create complex sorting logic without touching the Student class.

Avoid the old anonymous class syntax - lambdas and method references are much cleaner and are what you'll see in modern codebases."

---

## SLIDE 32: Collections Utility Class - Part 1
**Content:**
```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1));

// Sorting
Collections.sort(numbers);                           // [1, 2, 5, 8]
Collections.sort(numbers, Collections.reverseOrder()); // [8, 5, 2, 1]

// Searching (list must be sorted first!)
Collections.sort(numbers);
int index = Collections.binarySearch(numbers, 5);    // index of 5

// Reversing and Shuffling
Collections.reverse(numbers);
Collections.shuffle(numbers);

// Min and Max
int min = Collections.min(numbers);
int max = Collections.max(numbers);
```

**Duration:** 3 minutes

**Script:**
"The Collections class is a utility powerhouse with static methods for common operations. Sort takes a list and sorts in place using natural ordering, or you can pass a Comparator for custom sorting. Reverse order is a built-in Comparator that flips any natural ordering.

Binary search is O(log n) but requires the list to be sorted first - a very common mistake is calling binarySearch on an unsorted list and getting unreliable results. Min and max find extreme values using natural ordering."

---

## SLIDE 33: Collections Utility Class - Part 2
**Content:**
```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 5, 8));

// Count occurrences
int count = Collections.frequency(numbers, 5);

// Fill all elements with a value
Collections.fill(numbers, 0); // [0, 0, 0, 0]

// Swap two elements by index
Collections.swap(numbers, 0, 3);

// Unmodifiable wrapper - safe for returning internal state
List<Integer> readOnly = Collections.unmodifiableList(numbers);
readOnly.add(10); // UnsupportedOperationException at runtime

// Thread-safe wrapper
List<String> syncList = Collections.synchronizedList(new ArrayList<>());

// Disjoint check - do two collections share any elements?
Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3));
Set<Integer> set2 = new HashSet<>(Arrays.asList(4, 5, 6));
boolean noCommon = Collections.disjoint(set1, set2); // true
```

**Duration:** 3 minutes

**Script:**
"Continuing with Collections utilities. Frequency counts how many times an element appears. Fill replaces every element in a list with a given value - useful for resetting state. Swap exchanges two elements by their indices.

The unmodifiable wrapper is crucial for defensive programming. When you expose an internal collection to callers, wrap it first so they can't modify your object's state - but note the check happens at runtime, not compile time. The synchronized wrapper makes a collection thread-safe, though for serious concurrent work you'd use java.util.concurrent collections instead."

---

## SLIDE 34: Applying Generics for Type Safety
**Content:**
**Benefits:**
1. Compile-time type checking
2. No casting needed
3. Code reusability
4. Better IDE support

**Best Practices:**
```java
// ✓ GOOD - Type safe
List<String> names = new ArrayList<>();
names.add("Alice");
String name = names.get(0);

// ✗ BAD - Raw type (no generics)
List names = new ArrayList();
names.add("Alice");
names.add(123); // No error!
String name = (String) names.get(0); // Cast needed

// ✓ GOOD - Diamond operator (Java 7+)
Map<String, List<Integer>> map = new HashMap<>();

// ✗ BAD - Repeating types
Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
```

**Duration:** 2 minutes

**Script:**
"Always use generics - never use raw types. Raw types exist only for backward compatibility with pre-Java 5 code. They lose all type safety benefits and force you to cast everywhere.

Use the diamond operator - the empty angle brackets on the right side. Java infers the types from the left side, reducing verbosity. Your IDE will warn you about raw types - listen to those warnings!"

---

## SLIDE 35: Implementing Sorting Logic
**Content:**
```java
// For simple objects - implement Comparable
public class Person implements Comparable<Person> {
    private String name;
    private int age;
    
    @Override
    public int compareTo(Person other) {
        return Integer.compare(this.age, other.age);
    }
}

// For custom sorts - use Comparator
List<Person> people = new ArrayList<>();

// Sort by name
people.sort(Comparator.comparing(Person::getName));

// Sort by age descending
people.sort(Comparator.comparing(Person::getAge).reversed());

// Complex sorting - age, then name as tiebreaker
people.sort(Comparator.comparing(Person::getAge)
                      .thenComparing(Person::getName));
```

**Duration:** 3 minutes

**Script:**
"When implementing sorting, follow this pattern: use Comparable for natural ordering that makes sense for all use cases. Use Comparator for alternative orderings or when you can't modify the class.

The list.sort() method takes a Comparator and sorts in place. Use Comparator.comparing() with method references for clean, readable code. Chain comparators with thenComparing() for multi-level sorting. For complex custom logic, use lambda expressions."

---

## SLIDE 36: Common Pitfalls and Best Practices
**Content:**
**Pitfalls:**
1. Modifying collection during iteration → ConcurrentModificationException
2. Using == instead of equals() for objects
3. Forgetting to override equals() and hashCode()
4. Using raw types
5. Using the legacy Stack class — use ArrayDeque instead
6. Not considering performance characteristics

**Best Practices:**
1. Use appropriate collection for your needs
2. Prefer interfaces over implementations in declarations (List, not ArrayList)
3. Always use generics
4. Implement equals() and hashCode() for custom objects used in Sets/Maps
5. Use immutable objects as Map keys
6. Use Collections.unmodifiableXXX() when exposing internal collections

**Duration:** 2 minutes

**Script:**
"Let's cover common mistakes. Never modify a collection while iterating with for-each - use Iterator.remove(). Always use equals() for object comparison, not ==. If you're putting custom objects in Sets or using them as Map keys, you MUST override equals() and hashCode() consistently.

A common one to call out: avoid Java's legacy Stack class. It was written poorly and is slow - always use ArrayDeque for stack behavior.

Best practices: declare variables with interface types like List, not ArrayList - this keeps your code flexible. Always use generics. Make Map keys immutable to avoid bugs."

---

## SLIDE 37: equals() and hashCode() Contract
**Content:**
```java
public class Person {
    private String name;
    private int age;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && 
               Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}

// Why this matters:
Set<Person> people = new HashSet<>();
people.add(new Person("Alice", 25));
// Without proper equals/hashCode, this might be added:
people.add(new Person("Alice", 25)); // Should be duplicate!
```

**Duration:** 2 minutes

**Script:**
"This is crucial: if you're using custom objects in HashSets or as HashMap keys, you must override equals() and hashCode() consistently. The contract: if equals() returns true, hashCode() must return the same value for both objects.

Use Objects.equals() and Objects.hash() utility methods - they handle nulls correctly. Without this, HashSet and HashMap won't work correctly. Your duplicates won't be detected, and lookups will fail."

---

## SLIDE 38: Real-World Example
**Content:**
```java
// Student grade management system
public class GradeManager {
    // Name -> List of grades
    private Map<String, List<Integer>> studentGrades;
    
    // Unique students, alphabetical order
    private Set<String> enrolledStudents;
    
    public GradeManager() {
        studentGrades = new HashMap<>();
        enrolledStudents = new TreeSet<>();
    }
    
    public void addGrade(String student, int grade) {
        studentGrades.computeIfAbsent(student, k -> new ArrayList<>())
                     .add(grade);
        enrolledStudents.add(student);
    }
    
    public double getAverage(String student) {
        List<Integer> grades = studentGrades.get(student);
        if (grades == null || grades.isEmpty()) return 0.0;
        return grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    
    public List<String> getTopStudents(int n) {
        return enrolledStudents.stream()
            .sorted(Comparator.comparing(this::getAverage).reversed())
            .limit(n)
            .collect(Collectors.toList());
    }
}
```

**Duration:** 2 minutes

**Script:**
"Here's a real-world example bringing it all together. We use a HashMap to store student grades - quick lookup by name. A TreeSet keeps enrolled students in alphabetical order automatically. The addGrade method uses computeIfAbsent to initialize lists lazily - this is a very common pattern, worth memorizing.

Notice how we combine collections, generics, and comparators. This is typical enterprise Java code - multiple collections working together, each chosen for its specific strengths."

---

## SLIDE 39: Summary - Key Takeaways
**Content:**
1. **Collections Framework:**
   - List (ArrayList, LinkedList) - ordered, duplicates OK
   - Set (HashSet, TreeSet) - no duplicates
   - Map (HashMap, TreeMap) - key-value pairs
   - Queue/Deque (ArrayDeque, PriorityQueue) - FIFO, LIFO, or priority

2. **Choosing Implementations:**
   - Default: ArrayList, HashSet, HashMap
   - Sorted: TreeSet, TreeMap
   - Insertion order: LinkedHashSet, LinkedHashMap
   - Stack or Queue: **ArrayDeque** (not Stack, not LinkedList)

3. **Generics:**
   - Always use type parameters: `List<String>`
   - Bounded types: `<T extends Number>`
   - Wildcards: `<?>`, `<? extends>`, `<? super>` (PECS)

4. **Sorting:**
   - Comparable for natural ordering
   - Comparator for custom sorting
   - Collections utility methods

**Duration:** 2 minutes

**Script:**
"Let's recap the essentials. The Collections Framework gives you powerful data structures - choose based on your needs. Use generics for type safety. Implement Comparable for natural ordering, Comparator for alternatives. The Collections utility class provides ready-made algorithms.

Remember: ArrayList is your default List. HashSet is your default Set. HashMap is your default Map. ArrayDeque replaces both Stack and LinkedList-as-queue. Switch to Tree versions when you need sorting. That covers 95% of use cases."

---

## SLIDE 40: What's Next?
**Content:**
- Practice with coding exercises
- Explore java.util.concurrent collections for multithreading
- Streams API (Java 8+) for functional-style operations
- Custom collection implementations
- Performance tuning and benchmarking

**Resources:**
- Oracle Java Tutorials - Collections
- Effective Java by Joshua Bloch
- Java Collections Framework documentation

**Duration:** 1 minute

**Script:**
"The best way to master this is practice. Work through coding exercises using different collections. In future lessons, we'll explore concurrent collections for multithreading, and the Streams API for functional programming.

Check out the resources on this slide. 'Effective Java' by Joshua Bloch has excellent chapters on collections and generics. Practice, experiment, and you'll internalize when to use each collection type."

---

## SLIDE 41: Q&A
**Content:**
Questions?

**Contact:** [Your email]
**Office Hours:** [Your hours]

**Duration:** Remaining time

**Script:**
"That wraps up our hour on Collections and Generics. This is foundational knowledge you'll use constantly. What questions do you have? Don't hesitate to ask - these concepts can be tricky at first, and it's important you understand them well."

---

## Teaching Notes:

**Timing Guide:**
- Stick to the durations - they total approximately 65 minutes with the new Deque slides; trim Q&A or the real-world example if needed
- If running over, skip some code examples but keep core concepts

**Common Student Questions to Prepare For:**
1. "When exactly should I use LinkedList?" (Answer: Rarely - prefer ArrayDeque for queue/stack operations)
2. "Why can't I use primitives with generics?" (Autoboxing, wrapper classes)
3. "What's the difference between Collection and Collections?" (Interface vs utility class)
4. "Do I need to implement both equals() and hashCode()?" (Yes, always together)
5. "What's the difference between Deque and Queue?" (Deque adds operations on both ends; ArrayDeque is the go-to implementation for both)

**Assessment Ideas:**
- Quiz after lesson on choosing appropriate collections
- Coding exercise: implement a simple phone book with HashMap
- Problem: sort a list of custom objects multiple ways
- Design question: what collections for a task management system?
- Interview-prep question: implement a stack and a queue using only ArrayDeque