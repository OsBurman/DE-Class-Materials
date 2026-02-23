# Week 2 - Day 6 (Monday) Part 1: Lecture Script
## Collections Framework Overview & Basic Collections — 60-Minute Verbatim Delivery

**Pacing Note:** Aim for natural conversational delivery. Timing markers every ~2 minutes. Total time approximately 60 minutes.

---

## [00:00-02:00] Introduction: Welcome to Collections Week

Welcome to Week 2, everyone. Last week, you completed OOP fundamentals—classes, inheritance, polymorphism, abstract classes, interfaces, packages. You're thinking in objects now. You understand design. This week, we layer on advanced Java. Today is Collections. Collections are critical. Every real Java application manipulates groups of data. Whenever you have multiple items to store and manage—students in a course, tasks in a list, products in a catalog—you need collections. Java provides a powerful, unified framework. By the end of today, you'll know the difference between ArrayList and LinkedList, when to use HashSet vs TreeSet, and how to traverse and manipulate collections safely. Let's begin.

---

## [02:00-04:00] The Problem Collections Solve

Before collections, Java was painful. Say you're building a student management system. You need to store students. You use an array:

```java
Student[] students = new Student[100];
```

But what if you have 101 students? You create a new array of size 200, copy all 100 students, throw away the old array. What if you remove students? You have wasted space. You're manually managing growth and shrinkage. It's tedious and error-prone.

Collections solve this. They grow automatically as you add items. They shrink as you remove items. You focus on your business logic, not managing arrays. The Java Collections Framework provides a unified, tested solution.

---

## [04:00-06:00] Collections Framework Architecture Overview

The Collections Framework has a clean architecture. At the top, you have interfaces: Collection, List, Set, Map, Queue. These define contracts. Then you have implementations: concrete classes that implement these interfaces. ArrayList implements List. HashSet implements Set. HashMap implements Map. And all of this is based on common interfaces and algorithms.

Think of the interfaces as shapes: List, Set, Map, Queue. And implementations as specific shapes: ArrayList is a specific List, HashSet is a specific Set. You code against interfaces, not implementations. This gives you flexibility. Tomorrow you realize ArrayList is slow for your use case? Switch to LinkedList. The rest of your code stays the same because it depends on the List interface, not ArrayList specifically.

---

## [06:00-08:00] The Collection Hierarchy

Let's talk structure. The main hierarchy stems from the Collection interface. Under that, you have List, Set, and Queue. Lists are ordered. You insert three items, they stay in that order. Sets are unordered and contain unique items—no duplicates. Queues follow FIFO logic: first in, first out. Maps are separate. They're key-value pairs. Each interface has multiple implementations. List has ArrayList, LinkedList, Vector. Set has HashSet, TreeSet, LinkedHashSet. This variety exists because different implementations have different performance characteristics. One size doesn't fit all.

---

## [08:00-10:00] Lists: The Workhorse Collection

Lists are collections where order matters. You add items in a sequence, and that sequence is preserved. Lists allow duplicates—you can add "Apple" twice. Two main implementations dominate: ArrayList and LinkedList.

ArrayList is built on arrays. Under the hood, it's a dynamic array that grows as needed. You get O(1) random access—accessing element at index 5 is instant. But inserting in the middle is O(n) because you must shift elements.

LinkedList is a doubly-linked list. Each element knows the previous and next elements. Adding to the front or back is O(1). But random access is O(n) because you traverse from the beginning. Use ArrayList by default. Switch to LinkedList only if you're frequently adding/removing at the front.

---

## [10:00-12:00] ArrayList in Action

ArrayList is the most common collection. Let me show you:

```java
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
names.add("Charlie");
names.add("David");

System.out.println(names.get(0));      // Alice
System.out.println(names.size());      // 4
System.out.println(names.contains("Bob"));  // true

names.remove(2);                        // Remove Charlie
names.set(0, "Alicia");                 // Replace Alice with Alicia

for (String name : names) {
    System.out.println(name);
}
```

ArrayList supports add, get, set, remove, contains, size, clear. It's rich. Most of the time, ArrayList is your answer. When you're unsure what collection to use, reach for ArrayList. It's the default.

---

## [12:00-14:00] LinkedList: When Order of Addition Changes Everything

LinkedList shines when you frequently add or remove at the front. Imagine a printer queue. Jobs arrive, get added to the back. The printer takes jobs from the front. LinkedList is perfect.

```java
Queue<String> jobs = new LinkedList<>();
jobs.offer("Print Document A");
jobs.offer("Print Document B");
jobs.offer("Print Document C");

String next = jobs.poll();  // Remove Document A
next = jobs.poll();         // Remove Document B

jobs.addFirst("Emergency Print");  // Add to front
```

LinkedList also implements the Queue interface. The offer, poll, peek methods are natural for queue operations. When you need queue semantics, LinkedList is your go-to.

---

## [14:00-16:00] Performance Characteristics: When to Choose

Here's the reality: ArrayList is faster for most operations. Random access is instant. Adding to the end is amortized O(1). But inserting in the middle? O(n). LinkedList is opposite. Random access is O(n). Adding to the front? O(1). The key question: What do you do most? If random access, ArrayList. If add/remove at endpoints, LinkedList. In practice, ArrayList dominates because random access is common. LinkedList is used when you specifically need its characteristics.

---

## [16:00-18:00] Sets: Unique Items

Sets are collections that guarantee uniqueness. Add "Java" twice, it only appears once. Sets don't have order—well, some do, but HashSet doesn't. Sets are useful for membership testing: "Is this user already registered?"

Two main implementations: HashSet is the default—fast O(1) operations but unpredictable order. TreeSet maintains sorted order but is O(log n). There's also LinkedHashSet that maintains insertion order.

```java
Set<String> visited = new HashSet<>();
visited.add("Home");
visited.add("About");
visited.add("Contact");
visited.add("Home");  // Duplicate; ignored

System.out.println(visited.size());  // 3, not 4
System.out.println(visited.contains("About"));  // true
```

HashSet is fast because it uses hashing. It trades order for speed. If you don't care about order—and usually you don't for a "visited" set—HashSet is perfect.

---

## [18:00-20:00] TreeSet: Sorted Uniqueness

TreeSet is different. It maintains items in sorted order. By default, natural ordering. For integers, that's ascending. For strings, that's alphabetical. For your own objects, you define the ordering.

```java
Set<Integer> scores = new TreeSet<>();
scores.add(85);
scores.add(72);
scores.add(92);
scores.add(85);  // Duplicate; ignored

// Iteration is always in sorted order: 72, 85, 92
for (int score : scores) {
    System.out.println(score);
}
```

TreeSet is useful when you need sorted uniqueness. A leaderboard where you want top scores without duplicates? TreeSet. The trade-off: operations are O(log n) instead of O(1). But you get sorted iteration for free.

---

## [20:00-22:00] LinkedHashSet: The Best of Both Worlds

LinkedHashSet combines HashSet's O(1) speed with LinkedList's insertion-order preservation.

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

Use LinkedHashSet when you want fast lookups AND you care about iteration order. It's the Goldilocks of sets—fast and predictable.

---

## [22:00-24:00] Maps: Key-Value Pairs

Maps are different from the Collection hierarchy. They're key-value stores. Imagine a phone book: names (keys) map to phone numbers (values). Maps provide fast lookup: given a name, find the number instantly.

HashMap is the default. Fast O(1) lookups. You can also iterate over keys or values. But iteration order is unpredictable. TreeMap maintains sorted keys. LinkedHashMap maintains insertion order.

```java
Map<String, Integer> ages = new HashMap<>();
ages.put("Alice", 25);
ages.put("Bob", 30);
ages.put("Charlie", 28);

System.out.println(ages.get("Alice"));  // 25
System.out.println(ages.containsKey("Bob"));  // true

ages.remove("Charlie");
ages.put("Bob", 31);  // Update value

for (String name : ages.keySet()) {
    System.out.println(name + ": " + ages.get(name));
}
```

Maps are everywhere. Databases are maps. Caches are maps. Configuration files are maps. Master maps, and you'll solve many problems efficiently.

---

## [24:00-26:00] TreeMap: Sorted Keys

TreeMap keeps keys in sorted order. By default, natural ordering. You can provide a custom comparator for custom sort orders.

```java
Map<String, Integer> leaderboard = new TreeMap<>();
leaderboard.put("Charlie", 100);
leaderboard.put("Alice", 95);
leaderboard.put("Bob", 98);

// Iteration is in sorted key order: Alice, Bob, Charlie
for (String name : leaderboard.keySet()) {
    System.out.println(name + ": " + leaderboard.get(name));
}
```

TreeMap is useful when you need sorted keys. Operations are O(log n). Trade the speed of HashMap for the guarantee of sorted iteration.

---

## [26:00-28:00] Queues and LinkedList as Queue

Queues follow FIFO semantics. LinkedList implements the Queue interface perfectly. The primary operations are offer (add to back), poll (remove from front), and peek (view front).

```java
Queue<String> tasks = new LinkedList<>();
tasks.offer("Task 1");
tasks.offer("Task 2");
tasks.offer("Task 3");

while (!tasks.isEmpty()) {
    String task = tasks.poll();
    System.out.println("Processing: " + task);
}
```

This outputs Task 1, Task 2, Task 3 in order. Queues are essential for breadth-first search, processing jobs, and modeling real-world queues like printer queues or customer service lines.

---

## [28:00-30:00] PriorityQueue: Priority-Based Access

PriorityQueue isn't FIFO. Items exit based on priority. By default, natural ordering (smallest for numbers, alphabetical for strings). You can provide a custom comparator for custom priorities.

```java
PriorityQueue<Integer> tasks = new PriorityQueue<>();
tasks.offer(5);
tasks.offer(2);
tasks.offer(8);

System.out.println(tasks.poll());  // 2 (smallest)
System.out.println(tasks.poll());  // 5
System.out.println(tasks.poll());  // 8
```

PriorityQueue is used for task scheduling (process highest-priority tasks first), Dijkstra's algorithm, and other applications where priority matters.

---

## [30:00-32:00] Iterators: Uniform Traversal

Every collection provides an Iterator. An Iterator lets you traverse one element at a time safely. You can modify the collection through the iterator.

```java
List<String> names = new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie"));
Iterator<String> iter = names.iterator();

while (iter.hasNext()) {
    String name = iter.next();
    System.out.println(name);
}
```

Iterators work the same whether you're iterating ArrayList, LinkedList, HashSet, or TreeSet. The contract is identical: hasNext(), next(). This uniformity is powerful. You write traversal code once, it works everywhere.

---

## [32:00-34:00] Enhanced For Loop: The Modern Way

The enhanced for loop (for-each loop) is cleaner than explicit iterators. It works with any Collection.

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

for (String name : names) {
    System.out.println(name);
}
```

This is equivalent to:

```java
for (Iterator<String> iter = names.iterator(); iter.hasNext();) {
    String name = iter.next();
    System.out.println(name);
}
```

Use the enhanced for loop—it's cleaner, less error-prone, and works everywhere.

---

## [34:00-36:00] A Critical Error: ConcurrentModificationException

Here's a trap many fall into: modifying a collection while iterating. This crashes with ConcurrentModificationException.

```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

// WRONG: This crashes
for (int n : numbers) {
    if (n % 2 == 0) {
        numbers.remove(Integer.valueOf(n));  // CRASH!
    }
}
```

The collection detects you're modifying it during iteration and crashes. To fix it, use iterator.remove():

```java
Iterator<Integer> iter = numbers.iterator();
while (iter.hasNext()) {
    int n = iter.next();
    if (n % 2 == 0) {
        iter.remove();  // Safe!
    }
}
```

iterator.remove() is the safe way to remove during iteration. Or collect items to remove, then remove them after iteration.

---

## [36:00-38:00] Choosing the Right Collection

Here's the decision tree. First question: Do you need uniqueness? If yes, use Set. If no, use List. Second question: If Set, do you need sorted? If yes, TreeSet. If no, HashSet. Third question: If List, do you frequently random access? If yes, ArrayList. If you frequently add/remove at endpoints, LinkedList. For maps: Similar logic. Do you need sorted keys? TreeMap. If not, HashMap. That's the framework for choosing. Practice with these eight collections—ArrayList, LinkedList, HashSet, TreeSet, LinkedHashMap, HashMap, TreeMap, PriorityQueue—and you'll solve most problems efficiently.

---

## [38:00-40:00] Real-World Example: Student Management System

Let me show you a real-world scenario. You're building a student management system. You need to store students, iterate them, and check for duplicates.

```java
List<String> students = new ArrayList<>();
students.add("Alice Johnson");
students.add("Bob Smith");
students.add("Charlie Brown");

// Add a new student
students.add("Diana Prince");

// Remove a student
students.remove("Charlie Brown");

// Check if student exists
System.out.println(students.contains("Alice Johnson"));  // true

// Display all students
for (String student : students) {
    System.out.println(student);
}
```

ArrayList is perfect here. You maintain an ordered list of students. Iteration is easy. Contains-check is O(n) but acceptable for small lists. For large lists, you might use a Set of IDs for faster uniqueness checking.

---

## [40:00-42:00] Real-World Example: Unique User Roles

Your system tracks user roles: admin, user, moderator. You want to ensure roles are unique and access them quickly.

```java
Set<String> roles = new HashSet<>();
roles.add("admin");
roles.add("user");
roles.add("moderator");

// Check if user has role
System.out.println(roles.contains("admin"));  // true

// Add role
roles.add("guest");

// Remove role
roles.remove("moderator");

System.out.println("Total roles: " + roles.size());
```

HashSet ensures uniqueness. O(1) contains() checks. Order doesn't matter. Perfect for roles, tags, or any unique collection.

---

## [42:00-44:00] Real-World Example: Course Grades Lookup

You're building a grade book. Student ID (key) maps to grade (value). Fast lookup is essential.

```java
Map<Integer, String> grades = new HashMap<>();
grades.put(101, "A");
grades.put(102, "B");
grades.put(103, "A+");
grades.put(104, "C");

// Look up a student's grade
System.out.println("Student 102's grade: " + grades.get(102));  // B

// Update grade
grades.put(102, "A");

// Remove student
grades.remove(104);

// Check if student exists
System.out.println(grades.containsKey(103));  // true

// Display all grades
for (int id : grades.keySet()) {
    System.out.println("Student " + id + ": " + grades.get(id));
}
```

HashMap provides instant lookup by ID. Perfect for databases and lookups.

---

## [44:00-46:00] Common Beginner Mistake: Confusing Collections and Arrays

Arrays are fixed-size. Collections are dynamic. Beginners often use arrays when they should use collections.

```java
// ARRAY: Fixed size of 10
String[] names = new String[10];
names[0] = "Alice";
names[1] = "Bob";
// names[10] = "Charlie";  // IndexOutOfBoundsException!

// LIST: Dynamic size
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
names.add("Charlie");  // Works! List grows.
```

When size is unknown or changes, use collections. Arrays only when size is truly fixed and known.

---

## [46:00-48:00] Common Beginner Mistake: HashSet When Order Matters

HashSet doesn't guarantee order. Iteration order is unpredictable. If you need sorted or insertion-order iteration, use TreeSet or LinkedHashSet.

```java
// WRONG: Expecting sorted order from HashSet
Set<Integer> numbers = new HashSet<>();
numbers.add(3);
numbers.add(1);
numbers.add(2);
// Iteration order might be 1, 3, 2 or 2, 1, 3 or...

// CORRECT: Use TreeSet for sorted order
Set<Integer> numbers = new TreeSet<>();
numbers.add(3);
numbers.add(1);
numbers.add(2);
// Iteration is always 1, 2, 3
```

HashSet is fastest but unpredictable. TreeSet is slightly slower but guarantees sorted order. Choose based on your needs.

---

## [48:00-50:00] Common Beginner Mistake: Modifying During Iteration

Don't modify a collection while iterating. Use iterator.remove() or collect items to remove then remove afterward.

```java
// WRONG:
for (String name : names) {
    if (name.startsWith("A")) {
        names.remove(name);  // CRASH!
    }
}

// CORRECT:
Iterator<String> iter = names.iterator();
while (iter.hasNext()) {
    String name = iter.next();
    if (name.startsWith("A")) {
        iter.remove();  // Safe!
    }
}
```

This pattern prevents ConcurrentModificationException.

---

## [50:00-52:00] Big O Performance: Quick Reference

Here's a cheat sheet for performance:

ArrayList: O(1) get, O(1) amortized add-to-end, O(n) insert-middle, O(n) remove.

LinkedList: O(n) get, O(1) add-to-front, O(1) remove-from-front, O(1) add-to-back.

HashSet: O(1) add, O(1) remove, O(1) contains. Order unpredictable.

TreeSet: O(log n) add, O(log n) remove, O(log n) contains. Order sorted.

HashMap: O(1) put, O(1) get, O(1) remove. Order unpredictable.

TreeMap: O(log n) put, O(log n) get, O(log n) remove. Order sorted by key.

Default to ArrayList and HashMap. Switch only if profiling identifies a bottleneck.

---

## [52:00-54:00] Decision Tree: Choosing Your Collection

Ask yourself:

1. Do I need order? Yes → List. No → Set or Map.

2. If List: Do I do random access often? Yes → ArrayList. No → LinkedList.

3. If Set: Do I need sorted order? Yes → TreeSet. No → HashSet.

4. If Map: Do I need sorted by key? Yes → TreeMap. No → HashMap.

This simple tree will solve 95% of your problems. Master these eight collections and you're proficient with Java collections.

---

## [54:00-56:00] Summary: Part 1 Concepts

Let me recap:

- Collections Framework: unified architecture with interfaces and implementations
- Lists: ArrayList for random access, LinkedList for add/remove at endpoints
- Sets: HashSet for speed, TreeSet for sorted, LinkedHashSet for insertion order
- Maps: HashMap for speed, TreeMap for sorted keys
- Queues: LinkedList or PriorityQueue
- Iterators and enhanced for loops for safe traversal
- ConcurrentModificationException prevention
- Performance characteristics guide your choices

You now understand the core collections. Solid foundation.

---

## [56:00-58:00] Preview: Part 2 (Generics)

In Part 2, we're adding type safety. Right now, you can create a List and accidentally mix types. A List could hold strings one moment, integers the next. Chaos. Generics let you specify exactly: `List<String>` holds only strings. `Map<String, Integer>` holds strings mapped to integers. This prevents type errors at compile time, not runtime. Generics are the modern way. You'll also learn Comparable vs Comparator for sorting, and Collections utility methods. Get ready to write safer, clearer code!

---

## [58:00-60:00] Closing Thoughts: Collections Mastery

You've now learned the Collections Framework. You understand the hierarchy, the performance trade-offs, and when to use each collection. This knowledge took Java programmers years to internalize years ago. You've learned it in one morning. Take pride in that. In projects, you'll encounter scenarios where choosing the right collection cuts runtime from seconds to milliseconds. That knowledge is a superpower. Practice with these collections. Build small programs. Feel how ArrayList grows, how HashSet ensures uniqueness, how HashMap provides instant lookup. Master collections, and you'll be a more proficient Java programmer. See you in Part 2 for Generics!

---
