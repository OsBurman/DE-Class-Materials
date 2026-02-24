# Exercise 01: ArrayList and LinkedList Operations

## Objective
Practice using the `List` interface with both `ArrayList` and `LinkedList` implementations, understanding their common API and when each implementation is appropriate.

## Background
A university registrar system manages lists of student names and course waitlists. Lists allow duplicate entries, preserve insertion order, and provide index-based access. `ArrayList` stores elements in a resizable array (fast random access), while `LinkedList` stores elements as a doubly-linked chain (fast insertions/removals at ends).

## Requirements

1. Create an `ArrayList<String>` named `roster` and:
   - Add students: `"Alice"`, `"Bob"`, `"Carol"`, `"Dave"`, `"Alice"` (duplicate — allowed in List)
   - Print the full roster
   - Print the size
   - Print the element at index 2
   - Check if `"Carol"` is in the list and print the result
   - Remove `"Bob"` by value; print the updated roster
   - Remove the element at index 0; print the updated roster

2. Create a `LinkedList<String>` named `waitlist` and:
   - Add `"Eve"`, `"Frank"`, `"Grace"` using `add()`
   - Add `"Zara"` to the **front** using `addFirst()`
   - Add `"Henry"` to the **back** using `addLast()`
   - Print the full waitlist
   - Print and remove the first element using `removeFirst()`
   - Print and remove the last element using `removeLast()`
   - Print the final waitlist

3. Iterate the `roster` list (after all modifications) using:
   - A standard indexed `for` loop — print `"[index]: [name]"` for each
   - An enhanced for loop — print each name on its own line

4. Print a brief comparison note as a comment-style output line:
   - `"ArrayList: fast random access (get by index). LinkedList: fast add/remove at ends."`

## Hints
- Both `ArrayList` and `LinkedList` implement the `List<E>` interface — they share `add()`, `remove()`, `get()`, `size()`, `contains()`
- `LinkedList` also implements `Deque`, giving it `addFirst()`, `addLast()`, `removeFirst()`, `removeLast()`
- `list.remove("Bob")` removes by value (first occurrence); `list.remove(0)` removes by index — watch the overloads!
- Duplicates ARE allowed in `List` — `ArrayList` will happily store two `"Alice"` entries

## Expected Output

```
=== ArrayList - Student Roster ===
Roster: [Alice, Bob, Carol, Dave, Alice]
Size: 5
Element at index 2: Carol
Contains Carol: true
After removing Bob: [Alice, Carol, Dave, Alice]
After removing index 0: [Carol, Dave, Alice]

=== LinkedList - Waitlist ===
Waitlist: [Zara, Eve, Frank, Grace, Henry]
Removed first: Zara → Waitlist: [Eve, Frank, Grace, Henry]
Removed last: Henry → Waitlist: [Eve, Frank, Grace]

=== Indexed For Loop ===
0: Carol
1: Dave
2: Alice

=== Enhanced For Loop ===
Carol
Dave
Alice

ArrayList: fast random access (get by index). LinkedList: fast add/remove at ends.
```
