# Exercise 11 — Generics & Data Structures

## Overview
Build a **Custom Data Structure Library** by implementing generic Stack, Queue, and Binary Search Tree from scratch.

## Learning Objectives
- Write generic classes with `<T>` type parameters
- Use bounded type parameters: `<T extends Comparable<T>>`
- Understand wildcards: `List<? extends Number>`
- Implement classic data structures from scratch
- Understand nodes, pointers, and recursive algorithms

## Setup
```bash
cd Exercise-11-Generics-DataStructures/starter-code/src
javac *.java
java Main
```

## Files

| File | Class | Description |
|------|-------|-------------|
| `Stack.java` | `Stack<T>` | LIFO stack backed by linked nodes |
| `Queue.java` | `Queue<T>` | FIFO queue backed by linked nodes |
| `BST.java` | `BST<T extends Comparable<T>>` | Binary Search Tree with insert/search/traverse |
| `Utils.java` | `Utils` | Generic utility methods with wildcards |
| `Main.java` | `Main` | Driver — do not modify |

## Your TODOs

### Stack<T>
- `push(T data)` — add to top
- `pop()` — remove and return top (throw `EmptyStackException` if empty)
- `peek()` — return top without removing
- `isEmpty()`, `size()`

### Queue<T>
- `enqueue(T data)` — add to back
- `dequeue()` — remove and return front (throw `NoSuchElementException` if empty)
- `front()` — peek at front
- `isEmpty()`, `size()`

### BST<T extends Comparable<T>>
- `insert(T data)` — insert maintaining BST property
- `contains(T data)` — return true if found
- `inOrder()` — return `List<T>` via in-order traversal (sorted!)
- `height()` — return tree height (empty = 0)
- `min()`, `max()` — leftmost / rightmost nodes

### Utils
- `<T> void swap(T[] arr, int i, int j)` — swap two elements
- `<T extends Comparable<T>> T findMax(List<T> list)` — max via generics
- `double sumList(List<? extends Number> list)` — wildcard sum
- `<T> List<T> repeat(T element, int times)` — create list with repeated element
