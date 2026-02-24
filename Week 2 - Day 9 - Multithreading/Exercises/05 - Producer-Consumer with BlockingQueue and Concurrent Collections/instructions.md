# Exercise 05: Producer-Consumer Pattern with BlockingQueue and Concurrent Collections

## Objective
Implement the producer-consumer pattern using `LinkedBlockingQueue` and explore thread-safe concurrent collections including `ConcurrentHashMap`.

## Background
The **producer-consumer** problem is a classic concurrency challenge: one or more threads produce items and place them in a shared buffer; one or more threads consume items from that buffer. The naive approach using a plain `ArrayList` with manual `wait/notify` is error-prone. Java's `BlockingQueue` implementations handle all the coordination — `put()` blocks when the queue is full; `take()` blocks when the queue is empty. For shared maps and lists, `ConcurrentHashMap` and `CopyOnWriteArrayList` are thread-safe alternatives to `HashMap` and `ArrayList`.

## Requirements

1. **Producer-consumer with LinkedBlockingQueue**:
   - Create a `LinkedBlockingQueue<String>` with a capacity of 5
   - Create a `Producer` thread that puts 8 items (`"Item-1"` through `"Item-8"`) into the queue, sleeping 80 ms between puts, then puts a special sentinel `"DONE"` to signal completion. Print `"Produced: Item-N"` for each item.
   - Create a `Consumer` thread that loops calling `take()`, prints `"Consumed: Item-N"`, and stops when it reads `"DONE"`
   - Start both threads and join both

2. **ConcurrentHashMap**: Create a `ConcurrentHashMap<String, Integer>`. Launch 3 threads that each insert 5 key-value pairs (keys like `"thread-1-key-1"`, values = key length). After all threads join, print the total size: `"ConcurrentHashMap size: N"` and print 3 sample entries.

3. **ConcurrentHashMap computeIfAbsent / merge**: Using a `ConcurrentHashMap<String, Integer>` as a word-frequency counter, process the list `["apple","banana","apple","cherry","banana","apple"]` in parallel across 2 threads. Use `merge(word, 1, Integer::sum)` to count occurrences. After joining, print each word and its count sorted alphabetically.

## Hints
- `LinkedBlockingQueue` constructor takes a capacity argument — `new LinkedBlockingQueue<>(capacity)`
- `queue.put(item)` blocks if the queue is full; `queue.take()` blocks if the queue is empty — no `wait/notify` needed
- The sentinel value `"DONE"` pattern is a common way to signal "no more work" to a consumer
- `map.merge(key, 1, Integer::sum)` is atomic in `ConcurrentHashMap` — it adds 1 if the key exists, or inserts 1 if it doesn't

## Expected Output

```
=== Producer-Consumer with LinkedBlockingQueue ===
Produced: Item-1
Consumed: Item-1
Produced: Item-2
Consumed: Item-2
Produced: Item-3
Consumed: Item-3
Produced: Item-4
Produced: Item-5
Consumed: Item-4
Produced: Item-6
Consumed: Item-5
Consumed: Item-6
Produced: Item-7
Consumed: Item-7
Produced: Item-8
Consumed: Item-8
Consumer received DONE signal — stopping.

=== ConcurrentHashMap ===
ConcurrentHashMap size: 15
thread-1-key-1 = 12
thread-2-key-1 = 12
thread-3-key-1 = 12

=== Word Frequency with merge ===
apple = 3
banana = 2
cherry = 1
```

> Note: Producer and consumer output lines will interleave differently on each run.
