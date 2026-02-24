# Day 6 — Collections & Generics
## Quick Reference Guide

---

## 1. Collections Framework Hierarchy

```
java.lang.Iterable
└── java.util.Collection
    ├── List          (ordered, allows duplicates)
    │   ├── ArrayList
    │   └── LinkedList
    ├── Set           (no duplicates)
    │   ├── HashSet        (unordered)
    │   ├── LinkedHashSet  (insertion order)
    │   └── TreeSet        (sorted)
    └── Queue / Deque
        ├── LinkedList     (also implements Deque)
        ├── ArrayDeque
        └── PriorityQueue  (heap-based, min by default)

java.util.Map          (key-value pairs; NOT a Collection)
    ├── HashMap        (unordered)
    ├── LinkedHashMap  (insertion order)
    └── TreeMap        (sorted by key)
```

---

## 2. List — ArrayList vs LinkedList

| Aspect | `ArrayList` | `LinkedList` |
|--------|------------|-------------|
| Underlying structure | Dynamic array | Doubly-linked list |
| `get(i)` | O(1) | O(n) |
| `add(end)` | O(1) amortised | O(1) |
| `add(middle)` | O(n) (shift) | O(n) (traverse) + O(1) insert |
| `remove(i)` | O(n) (shift) | O(n) (traverse) + O(1) remove |
| Memory | Less overhead | More (node objects + pointers) |
| **Default choice** | ✅ Most use cases | ✅ When frequent head/tail insert-remove |

```java
List<String> list = new ArrayList<>();
list.add("Alice");
list.add(0, "Bob");         // insert at index 0
list.get(1);                // "Alice"
list.remove("Alice");       // remove by value (first occurrence)
list.remove(0);             // remove by index
list.set(0, "Charlie");     // replace
list.size();                // count
list.contains("Bob");       // true/false
list.subList(0, 2);         // [start, end)
Collections.sort(list);     // in-place sort
```

---

## 3. Set

```java
Set<String> hashSet     = new HashSet<>();       // fastest; no order guaranteed
Set<String> linkedSet   = new LinkedHashSet<>();  // preserves insertion order
Set<String> treeSet     = new TreeSet<>();        // sorted (natural or Comparator)

set.add("A");
set.contains("A");    // true
set.remove("A");
set.size();

// Set operations
Set<Integer> a = new HashSet<>(Set.of(1, 2, 3));
Set<Integer> b = new HashSet<>(Set.of(2, 3, 4));
a.retainAll(b);   // intersection → {2, 3}
a.addAll(b);      // union
a.removeAll(b);   // difference
```

---

## 4. Map

```java
Map<String, Integer> map = new HashMap<>();

// Put / get / check
map.put("Alice", 90);
map.get("Alice");             // 90
map.getOrDefault("Bob", 0);   // 0 (key absent)
map.containsKey("Alice");     // true
map.containsValue(90);        // true
map.remove("Alice");

// Useful operations
map.putIfAbsent("Bob", 85);
map.computeIfAbsent("Carol", k -> 0);
map.merge("Alice", 5, Integer::sum);   // add 5 to Alice's value

// Iteration
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " → " + entry.getValue());
}
map.forEach((k, v) -> System.out.println(k + " → " + v));
map.keySet();      // Set of keys
map.values();      // Collection of values
```

---

## 5. Queue & Deque

```java
Queue<Integer> queue = new LinkedList<>();   // FIFO
queue.offer(1);    // add to tail (returns false if full; prefer over add())
queue.peek();      // look at head (null if empty)
queue.poll();      // remove head (null if empty)

Deque<Integer> deque = new ArrayDeque<>();   // double-ended; also used as Stack
deque.offerFirst(1);   deque.offerLast(2);
deque.peekFirst();     deque.peekLast();
deque.pollFirst();     deque.pollLast();

PriorityQueue<Integer> pq = new PriorityQueue<>();  // min-heap
PriorityQueue<Integer> maxPq = new PriorityQueue<>(Comparator.reverseOrder());
```

---

## 6. Iterator & For-Each

```java
// For-each (syntactic sugar over Iterator)
for (String s : list) { System.out.println(s); }

// Explicit Iterator — required when removing during iteration
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.startsWith("A")) it.remove();   // safe removal
}

// ⚠️ ConcurrentModificationException — thrown when you modify a collection
//    while iterating it WITHOUT using iterator.remove()
for (String s : list) {
    list.remove(s);   // ❌ throws ConcurrentModificationException
}
```

---

## 7. Collections Utility Class

```java
Collections.sort(list);                          // natural order, in-place
Collections.sort(list, Comparator.reverseOrder());
Collections.reverse(list);
Collections.shuffle(list);
Collections.min(list);    Collections.max(list);
Collections.frequency(list, "Alice");            // count occurrences
Collections.unmodifiableList(list);              // read-only view
Collections.synchronizedList(list);             // thread-safe wrapper (prefer ConcurrentHashMap)
Collections.nCopies(5, "X");                     // ["X", "X", "X", "X", "X"]
```

---

## 8. Comparable vs Comparator

| | `Comparable<T>` | `Comparator<T>` |
|---|---|---|
| Package | `java.lang` | `java.util` |
| Defines | **Natural ordering** of the class itself | **External ordering** — separate logic |
| Method | `compareTo(T other)` | `compare(T a, T b)` |
| Modify the class? | ✅ Yes (must implement on the class) | ❌ No (external class or lambda) |
| Multiple orderings | ❌ One per class | ✅ Many Comparators |

```java
// Comparable — natural order built into the class
public class Student implements Comparable<Student> {
    private String name;
    private int grade;

    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.grade, other.grade);  // ascending by grade
        // return this.name.compareTo(other.name);        // alphabetical by name
    }
}

// Comparator — external, ad-hoc, or chained
Comparator<Student> byName    = Comparator.comparing(Student::getName);
Comparator<Student> byGrade   = Comparator.comparingInt(Student::getGrade);
Comparator<Student> combined  = byGrade.reversed().thenComparing(byName);

list.sort(combined);
// or: Collections.sort(list, combined);

// Convention: return negative if a < b, 0 if equal, positive if a > b
```

---

## 9. Generics

**Generic class:**
```java
public class Box<T> {
    private T value;
    public Box(T value) { this.value = value; }
    public T get()      { return value; }
}

Box<String>  strBox = new Box<>("hello");
Box<Integer> intBox = new Box<>(42);
```

**Generic method:**
```java
public static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
}
```

**Multiple type parameters:**
```java
public class Pair<K, V> {
    private K key;
    private V value;
    // ...
}
```

---

## 10. Wildcards

| Wildcard | Meaning | Use Case |
|----------|---------|----------|
| `<?>` | Unknown type | Read-only; no writes |
| `<? extends T>` | T or any subtype | **Producer** — read from it |
| `<? super T>` | T or any supertype | **Consumer** — write to it |

**PECS: Producer Extends, Consumer Super**

```java
// Can READ from a list of Shape or any subtype
public void printAreas(List<? extends Shape> shapes) {
    for (Shape s : shapes) System.out.println(s.area());
}

// Can WRITE Circles into a list of Circle or any supertype
public void addCircles(List<? super Circle> list) {
    list.add(new Circle("red", 5.0));
}
```

---

## 11. Type Erasure

Generics are a **compile-time** feature only. At runtime, all type parameters are erased to their bounds (or `Object`).

```java
List<String> strings = new ArrayList<>();
List<Integer> ints   = new ArrayList<>();
// At runtime: both are just List (raw type)
strings.getClass() == ints.getClass();   // true!
```

**Consequences:**
- Cannot use `instanceof` with a generic type: `obj instanceof List<String>` ❌
- Cannot create generic arrays: `new T[]` ❌
- Cannot use primitives as type params: use `Integer` not `int`

---

## 12. Common Patterns

```java
// Frequency map
Map<Character, Integer> freq = new HashMap<>();
for (char c : s.toCharArray()) {
    freq.merge(c, 1, Integer::sum);
}

// Group objects by a property
Map<String, List<Student>> byDept =
    students.stream().collect(Collectors.groupingBy(Student::getDepartment));

// Immutable collections (Java 9+)
List<String>  names = List.of("Alice", "Bob");
Set<Integer>  ids   = Set.of(1, 2, 3);
Map<String, Integer> scores = Map.of("Alice", 90, "Bob", 85);
```
