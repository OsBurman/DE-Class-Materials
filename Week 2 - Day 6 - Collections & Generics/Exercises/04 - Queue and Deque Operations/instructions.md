# Exercise 04: Queue and Deque Operations

## Objective
Practice using `Queue` and `Deque` implementations (`ArrayDeque` and `PriorityQueue`) for FIFO processing and priority-based ordering.

## Background
A customer support ticketing system processes support requests in order of arrival (FIFO) using a `Queue`. A hospital triage system uses a `PriorityQueue` to process patients by urgency level rather than arrival order. Understanding queues is essential for breadth-first algorithms, task scheduling, and message processing.

## Requirements

1. Create an `ArrayDeque<String>` used as a **Queue** (FIFO) named `supportQueue` and:
   - Enqueue (add to back): `"Ticket-101"`, `"Ticket-102"`, `"Ticket-103"` using `offer()`
   - Peek at the front without removing: use `peek()` and print the result
   - Process (dequeue from front) all tickets using `poll()` in a loop, printing `"Processing: [ticket]"` for each
   - After the loop, print the result of `poll()` on the empty queue (it returns `null`, not an exception)

2. Create an `ArrayDeque<String>` used as a **Deque** (double-ended) named `deque` and:
   - Add `"B"` to the back using `offerLast()`
   - Add `"A"` to the front using `offerFirst()`
   - Add `"C"` to the back using `offerLast()`
   - Print the deque
   - Remove and print from the front using `pollFirst()`
   - Remove and print from the back using `pollLast()`
   - Print the final deque

3. Create a `PriorityQueue<String>` named `triageQueue` and:
   - Add patients: `"PatientC"`, `"PatientA"`, `"PatientB"` (PriorityQueue uses natural ordering by default)
   - Print `peek()` — the highest-priority (alphabetically first) patient
   - Poll all patients in priority order using a loop, printing each one

## Hints
- `offer()` is the preferred way to add to a queue (returns `false` on failure); `add()` throws an exception on failure
- `poll()` returns `null` if the queue is empty; `remove()` throws `NoSuchElementException` — prefer `poll()` in production code
- `peek()` reads the head without removing it; `poll()` removes and returns it
- `PriorityQueue` does NOT store elements in sorted order internally — it only guarantees that `poll()` always returns the minimum (natural ordering) element next

## Expected Output

```
=== ArrayDeque as Queue (FIFO) ===
Front of queue: Ticket-101
Processing: Ticket-101
Processing: Ticket-102
Processing: Ticket-103
Poll on empty queue: null

=== ArrayDeque as Deque (double-ended) ===
Deque: [A, B, C]
Removed from front: A
Removed from back: C
Remaining: [B]

=== PriorityQueue (natural order) ===
Highest priority peek: PatientA
Polling in priority order:
  PatientA
  PatientB
  PatientC
```
