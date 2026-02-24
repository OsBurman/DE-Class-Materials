# Walkthrough Script — Day 6, Part 1
## Collections & Generics: Java Collections Framework + Iterators

**Files covered (in order):**
1. `01-collections-framework-overview.java`
2. `02-iterators-and-for-loop.java`

**Estimated time:** 90 minutes

---

## File 1: Collections Framework Overview
**File:** `Part-1/01-collections-framework-overview.java`
**Time estimate:** ~60 minutes

---

### Opening — Why Collections? (5 min)

[ACTION] Open the file. Don't run anything yet. Point to the top comment block showing the interface hierarchy.

"Good morning. Last week we learned to build objects — classes that model real-world things. Today we learn where to put them. Almost every real program maintains groups of things: a list of users, a set of unique tags, a map of product prices by SKU. Java's Collections Framework gives you ready-made, battle-tested data structures for all of these.

The framework has three layers. First: **interfaces** — these define what operations a collection supports. Second: **implementations** — the concrete classes you actually instantiate. Third: **algorithms** — utility methods like sorting and shuffling. We'll cover algorithms in Part 2.

Let me show you the hierarchy."

[ACTION] Read or point to the hierarchy in the top comment:
- `Iterable → Collection → List / Set / Queue`
- `Map` is separate — not a Collection, but still part of the JCF

"Notice that `Map` is NOT a `Collection`. It's in the JCF family, but it doesn't implement `Collection` because it deals in key-value pairs, not individual elements. This trips people up — watch for it."

[ASK] "Before we look at any code — what's the difference between a List and a Set? Intuition only."

Wait for: List allows duplicates and preserves order; Set doesn't allow duplicates.

"Exactly. Hold onto that. Let's see it in code."

---

### Section 1: ArrayList (10 min)

[ACTION] Scroll to `demonstrateArrayList()`.

"ArrayList is the workhorse. Probably 80% of the time when someone says 'I need a list', they mean ArrayList. It's backed by a plain Java array that resizes automatically."

[ACTION] Point to the declaration: `List<String> shoppingList = new ArrayList<>();`

"Notice: the variable type is `List<String>`, not `ArrayList<String>`. We're programming to the **interface**. This is a best practice from Day 5's OOP lesson — if later we decide LinkedList is a better fit, we change only the right side. Everything that uses `shoppingList` stays the same."

[ACTION] Point to the duplicate add: `shoppingList.add("Apples")` appears twice.

"What happens when we add 'Apples' a second time?"

Wait for: both entries appear (List allows duplicates).

"Correct. Lists allow duplicates. The second 'Apples' goes in as its own entry."

[ACTION] Point to `shoppingList.get(0)` and `shoppingList.get(shoppingList.size() - 1)`.

"Index-based access — O(1). Constant time regardless of the list size. This is ArrayList's superpower. An array is just a block of memory, so jumping to position 500,000 is the same cost as jumping to position 0."

[ACTION] Point to `shoppingList.add(2, "Butter")`.

"Insert at index 2. What does Java have to do with everything already at index 2 and beyond?"

Wait for: shift everything right.

"Yes — O(n) in the worst case. Every element from index 2 to the end shifts one position right. That's the cost of the flexibility. For a 5-element list it's nothing. For a 5-million-element list with frequent middle insertions, you'd reconsider your data structure choice."

[ACTION] Point to both `remove()` calls.

"Two flavors: `remove(4)` removes by **index** — it knows the position. `remove('Eggs')` removes by **value** — it has to scan the list to find it. Both are O(n) because of the shifting."

⚠️ WATCH OUT: "A common bug: `remove(int)` vs `remove(Object)`. If you have a `List<Integer>` and call `list.remove(2)`, Java calls `remove(int)` — removes index 2. To remove the VALUE 2, you must call `list.remove(Integer.valueOf(2))`. The autoboxing ambiguity catches people regularly."

[ACTION] Point to `subList(0, 2)`.

"subList returns a **view**, not a copy. It's backed by the same underlying array. Modify the sublist and you modify the original. Copy it with `new ArrayList<>(shoppingList.subList(0, 2))` if you want independence."

[ACTION] Point to the pre-sizing comment: `new ArrayList<>(1000)`.

"Performance tip: if you know you're adding approximately 1,000 items, tell ArrayList upfront. ArrayList starts with a capacity of 10 and doubles it each time it fills up. Each resize copies the entire backing array. Pre-sizing avoids that overhead."

---

### Section 2: LinkedList (8 min)

[ACTION] Scroll to `demonstrateLinkedList()`.

"LinkedList is backed by a chain of node objects — each node holds a value, a reference to the previous node, and a reference to the next node. No array involved."

[ACTION] Point to `addFirst()` and `addLast()`.

"These are LinkedList-specific methods that aren't on the `List` interface. This is why I declared this variable as `LinkedList<String> tasks`, not `List<String> tasks` — if I want to use these extras, I need the concrete type."

[ACTION] Point to `peek()` vs `poll()`.

"Two families of methods to know. `peek()` — look at the head without removing. Safe: returns null if empty. `poll()` — remove and return the head. Also safe: returns null if empty. Contrast with `remove()` — throws NoSuchElementException if empty. In general, prefer `peek()` and `poll()` in queue usage."

[ACTION] Point to the `browserHistory` demo using `push()`/`pop()`.

"LinkedList implements Deque, which lets it act as a stack. `push()` = `addFirst()`. `pop()` = `removeFirst()`. New pages go on top. Back button removes from top. This is LIFO — Last In, First Out."

[ACTION] Point to the printed comparison notes at the bottom.

"The practical rule of thumb: if you're doing mostly random reads, use ArrayList. If you're constantly adding or removing from the front or back — tasks like a queue or a stack — LinkedList is a better fit."

---

### Section 3: HashSet (7 min)

[ACTION] Scroll to `demonstrateHashSet()`.

"Now we move from List to Set. The defining property: no duplicates."

[ACTION] Point to the duplicate `add("London")`.

"We add London twice. What's the size of the set?"

Wait for: 4 (London counted once).

"Exactly. The second add is silently ignored. No exception, no error — just nothing. `add()` returns a boolean: `true` if the element was actually inserted, `false` if it was already there. Most people ignore that return value. In competitive programming, you'd check it."

[ACTION] Point to `visitedCities.contains("London")`.

"O(1). That's why you use a Set when you need fast membership checks. Is this username taken? Is this IP in the blocklist? Has this order ID been processed? A HashSet answers all of those in constant time."

⚠️ WATCH OUT: "HashSet gives you no ordering guarantee at all. Run this code five times and you may get a different order each time. Never write code that depends on the iteration order of a HashSet."

[ACTION] Point to the set operations: `addAll()`, `retainAll()`, `removeAll()`.

"These three methods implement the classic set algebra operations: union, intersection, and difference. These are used constantly in real systems — 'which users are in group A AND group B?', 'which items are in the cart but not yet shipped?'"

---

### Section 4: TreeSet (5 min)

[ACTION] Scroll to `demonstrateTreeSet()`.

"TreeSet is the sorted sibling of HashSet. You trade O(1) for O(log n) — and you get guaranteed ascending order on every iteration."

[ACTION] Add the scores and show output.

[ASK] "We add 87, 42, 95, 61, 87. What will the TreeSet contain?"

Wait for: [42, 61, 87, 95] (duplicate 87 removed, sorted).

"Perfect. Now look at the NavigableSet methods."

[ACTION] Point to `floor()`, `ceiling()`, `headSet()`, `tailSet()`.

"`floor(80)` — the largest value that is ≤ 80. That's 61. `ceiling(80)` — the smallest value that is ≥ 80. That's 87. `headSet(90)` — everything below 90. `tailSet(60)` — everything from 60 upward. These are incredibly useful for range queries."

[ACTION] Point to `descendingSet()`.

"You get a reversed view of the same TreeSet — no copying involved. It's just a different view of the same underlying tree."

---

### Section 5: HashMap (8 min)

[ACTION] Scroll to `demonstrateHashMap()`.

"We're into Maps now. A Map stores key-value pairs. Every key is unique. Values can repeat. Think of it as a dictionary."

[ACTION] Point to the second `put("java", 20)`.

"We put 'java' with value 15, then put 'java' again with value 20. What happens?"

Wait for: value is replaced.

"Exactly. The key 'java' can only appear once. The new value 20 overwrites 15. This is unlike List which would add a second entry."

[ACTION] Point to `getOrDefault()`.

"'python' was never added. `get('python')` would return `null`. If your code does `int count = map.get('python')` without this check, you'll get a NullPointerException when Java tries to unbox null to int. `getOrDefault()` is the safe way."

[ACTION] Point to `putIfAbsent()`.

"Two common patterns: 'set this value, replace if exists' → use `put()`. 'Set this value only if the key isn't already there' → use `putIfAbsent()`. Classic use case: building a cache."

[ACTION] Point to the `entrySet()` loop.

"To iterate a Map with both keys and values, iterate `entrySet()`. Each entry is a `Map.Entry<K,V>` object with `getKey()` and `getValue()`."

[ACTION] Point to the `merge()` word-frequency demo.

"This is one of the most elegant Map patterns. `merge(word, 1, Integer::sum)` says: if 'banana' isn't in the map, put 1. If it is, apply `Integer::sum` to the current value and 1. You'll use this in Streams and real data pipelines constantly."

---

### Section 6: TreeMap (4 min)

[ACTION] Scroll to `demonstrateTreeMap()`.

"TreeMap does for Maps what TreeSet does for Sets — keys are always sorted. You get all the NavigableMap methods: `firstKey()`, `lastKey()`, `headMap()`, `tailMap()`, `floorKey()`, `ceilingKey()`."

[ACTION] Point to the leaderboard with `Collections.reverseOrder()`.

"You can pass a `Comparator` to TreeMap's constructor to control the sort order. `Collections.reverseOrder()` gives you a comparator that reverses natural ordering — so numbers go highest-first."

⚠️ WATCH OUT: "The `put('Dave', 3100)` overwrites Bob because both have score 3100 and 3100 is the key. If you need to store multiple players with the same score, use `Map<Integer, List<String>>` — map a score to a list of names."

---

### Section 7: LinkedHashMap (4 min)

[ACTION] Scroll to `demonstrateLinkedHashMap()`.

"LinkedHashMap is the 'best of both worlds' between HashMap (fast) and TreeMap (ordered). It maintains insertion order — the order you put keys in is the order you get them back."

[ACTION] Point to the HTTP headers demo.

"HTTP headers are a perfect use case. When you're building an HTTP request, the order of headers matters for readability and sometimes for signature validation. HashMap would shuffle them. LinkedHashMap keeps them exactly as you inserted."

[ACTION] Point to the LRU cache demo.

"You can also put LinkedHashMap in access-order mode. The most recently accessed key moves to the end. Override `removeEldestEntry()` to evict the front entry when the cache gets too big. Three lines of code, and you have a basic LRU cache."

---

### Section 8: ArrayDeque — Queue (5 min)

[ACTION] Scroll to `demonstrateQueue()`.

"Last collection type for today: Queue. The contract is FIFO — First In, First Out. Think of a print queue, a request queue, customers in a line."

[ACTION] Point to `offer()` and `poll()`.

"Always prefer `offer()` over `add()` for queues, and `poll()` over `remove()`. The `offer/poll/peek` family returns null or false on failure; the `add/remove/element` family throws exceptions. For queues in production code, null returns are safer to handle."

[ACTION] Point to `PriorityQueue`.

"PriorityQueue ignores insertion order entirely. Elements come out in sorted order — smallest first by default. If you want largest first, pass `Collections.reverseOrder()` to the constructor. This is the classic 'min-heap' / 'max-heap' pattern — heavily used in interview questions and scheduling algorithms."

---

### Section 9: Choosing the Right Collection (3 min)

[ACTION] Scroll to the summary table. Read it aloud briefly.

"Keep this table in mind. The single most important question: do you need order? And what kind — insertion order, sorted, or none? That one question eliminates most wrong choices. The second question: what operation are you doing most — reading by index, checking membership, inserting, or key lookup?"

---

→ TRANSITION: "Now you know all the major collection types. But every single one of those collections has the same property: you can iterate over it. Let's look at exactly how that works."

---

## File 2: Iterators and the Enhanced For Loop
**File:** `Part-1/02-iterators-and-for-loop.java`
**Time estimate:** ~30 minutes

---

### Opening (2 min)

[ACTION] Open the file.

"Every collection you just learned about — ArrayList, HashSet, TreeMap — they all implement `Iterable<T>`. That interface has one method: `iterator()`, which returns an `Iterator<T>`. The Iterator is the actual engine that lets you walk through the collection one element at a time.

The for-each loop you've been using is just syntax sugar for the Iterator. The compiler rewrites it for you. Understanding the Iterator itself matters for two reasons: removing elements safely while iterating, and understanding what the for-each loop can and can't do."

---

### Section 1: The Iterator Interface (8 min)

[ACTION] Scroll to `demonstrateIterator()`.

"Three methods: `hasNext()` — is there a next element? `next()` — give me the next element AND advance the cursor. `remove()` — remove the element I just got from `next()`."

[ACTION] Point to the basic while loop.

"This is exactly what the compiler generates when it sees a for-each loop. The variable type, the condition, the body — it all maps to this pattern."

[ACTION] Point to the `removeIt.remove()` usage.

"Here's the reason you'd use an explicit Iterator: safe removal while iterating. We want to remove planets with short names. We call `it.next()` — that returns the planet AND advances the cursor. Then `it.remove()` removes the element we just got. The Iterator knows the position and handles the removal safely."

⚠️ WATCH OUT: "You MUST call `next()` before calling `remove()`. If you call `remove()` twice in a row without another `next()` call in between, you'll get an `IllegalStateException`. The remove says 'remove the last element I returned' — if there was no last element, it throws."

[ACTION] Point to the "every other element" demo.

"This one's neat. We call `next()` twice per loop iteration — once to get the value we print, once to skip an element. This pattern only works with an explicit Iterator, not a for-each."

---

### Section 2: Enhanced For Loop (8 min)

[ACTION] Scroll to `demonstrateEnhancedForLoop()`.

"The for-each loop is the right tool for 90% of iteration. Clean, readable, and impossible to get the boundary conditions wrong."

[ACTION] Point to the List example, then the HashSet, then the TreeSet.

"Notice something: the for-each works identically on all three. That's the power of the `Iterable` interface — you don't need to know the internal structure. You just get elements one at a time."

[ASK] "What's the difference in iteration order between HashSet and TreeSet in this code?"

Wait for: HashSet is unpredictable; TreeSet is alphabetical.

[ACTION] Point to the index simulation and the traditional for loop.

"Two things the for-each CAN'T do: give you the index, and let you remove elements. For index access, maintain a counter variable alongside the loop, or just use a traditional `for (int i = 0; i < list.size(); i++)` loop. Both are valid."

[ACTION] Point to the 2D array loop.

"for-each works on arrays too — including nested arrays. `for (int[] row : matrix)` gives you each row as an array, then `for (int cell : row)` walks each cell. Much cleaner than tracking two index variables."

---

### Section 3: ListIterator (5 min)

[ACTION] Scroll to `demonstrateListIterator()`.

"ListIterator is an Iterator with superpowers — but only for Lists. Two extra directions: you can go forward and backward. Plus `set()` to replace the current element, and `add()` to insert."

[ACTION] Point to the backward traversal.

"The cursor is at the end after the forward loop. Now we walk backward with `hasPrevious()` and `previous()`. This is useful for text editors, undo stacks, anything where you need to walk in both directions through a list."

[ACTION] Point to `set()` and `add()`.

"`set()` replaces the element you last called `next()` on — in-place, no shifting needed. `add()` inserts AFTER the element you last called `next()` on. Notice how clean this is — no index management."

---

### Section 4: Iterating Maps (5 min)

[ACTION] Scroll to `demonstrateMapIteration()`.

"Maps aren't Collections, so they don't have a direct `iterator()`. Three entry points into the data."

[ACTION] Point to `entrySet()`, `keySet()`, `values()` in order.

"`entrySet()` gives you `Map.Entry` objects — each holds a key AND value. Use this when you need both. `keySet()` gives you just the keys as a Set. `values()` gives you just the values as a Collection — note, NOT a Set, because values can repeat."

[ACTION] Point to `forEach()` lambda.

"Java 8 added `forEach()` as a convenience. For a Map, it takes a BiConsumer — a lambda with two parameters, key and value. This is the cleanest syntax for read-only iteration. We'll cover lambdas deeply on Day 8."

[ACTION] Point to the safe removal via `entrySet().iterator()`.

"If you need to remove entries while iterating a Map, go through the entry set's iterator. Same pattern as list removal — use `entryIt.remove()`."

---

### Section 5: ConcurrentModificationException (7 min)

[ACTION] Scroll to `demonstrateConcurrentModification()`.

"This is one of the most common exceptions beginners encounter. Let's understand it properly."

[ACTION] Point to the bad example in the try-catch.

"We're iterating over `items` with a for-each, and inside the loop we call `items.remove(item)`. What happens?"

Wait for: ConcurrentModificationException.

"Correct. The iterator uses a modification counter. When you call `items.remove()` directly, the counter increments. The iterator checks the counter on each `next()` call and sees 'someone changed this collection without telling me' — so it throws immediately. This is called **fail-fast** behavior."

[ASK] "Why do you think Java does this instead of silently continuing? What could go wrong?"

Wait for: corrupted state, skipped elements, infinite loop.

"Exactly. If the iterator continued blindly, you'd skip elements or revisit them. The exception is actually protecting you."

[ACTION] Point to the three solutions in order.

"Three clean fixes. Solution 1: use `iterator.remove()` — we already saw this. Solution 2: `removeIf()` — by far the most readable for Java 8+. One line, lambda condition, done. Solution 3: collect what you want to remove, then call `removeAll()` after the iteration is complete. All three are safe. I recommend Solution 2 for new code."

---

### Part 1 Self-Check

Use these questions to verify comprehension before moving to Part 2.

**Collections Framework:**
- [ ] What is the difference between `Collection` and `Map` in the JCF?
- [ ] Why declare `List<String> list = new ArrayList<>()` instead of `ArrayList<String> list = new ArrayList<>()`?
- [ ] ArrayList vs LinkedList: when would you choose each?
- [ ] HashSet vs TreeSet: what do you trade when you switch from one to the other?
- [ ] HashMap vs TreeMap vs LinkedHashMap: what's the key difference?
- [ ] What does `getOrDefault()` protect against?
- [ ] What is `merge()` on a Map useful for?
- [ ] What's the difference between `offer()`/`poll()` and `add()`/`remove()` on a Queue?

**Iterators:**
- [ ] What three methods does `Iterator` have?
- [ ] Why is `iterator.remove()` safe when direct `list.remove()` inside a for-each is not?
- [ ] What does `ListIterator` add on top of `Iterator`?
- [ ] What are the three ways to iterate a Map?
- [ ] What causes `ConcurrentModificationException` and what are three ways to fix it?

---

*End of Day 6 Part 1 walkthrough script.*
