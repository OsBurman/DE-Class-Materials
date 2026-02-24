# Exercise 04: File Read and Write with Buffered Streams

## Objective
Practice writing to and reading from text files using `FileWriter`, `FileReader`, `BufferedWriter`, and `BufferedReader`, with explicit resource management (manual close in `finally`).

## Background
File I/O in Java involves layers: a low-level `FileWriter`/`FileReader` handles bytes, and a `BufferedWriter`/`BufferedReader` wraps it to add buffering — dramatically improving performance by reducing disk access. Understanding this manually-managed approach also helps you appreciate why `try-with-resources` (Exercise 05) was introduced.

## Requirements

1. **Write to a file** — write a grocery list to `grocery_list.txt`:
   - Use `FileWriter` and `BufferedWriter`
   - Write these lines (each on its own line): `"Apples"`, `"Bananas"`, `"Carrots"`, `"Dates"`, `"Eggplant"`
   - Use `newLine()` after each item (not `\n`) for platform-safe line endings
   - Close both writer resources in a `finally` block (not try-with-resources — that is Ex 05)
   - Print `"File written: grocery_list.txt"` after successful write

2. **Read from the file** — read `grocery_list.txt` back:
   - Use `FileReader` and `BufferedReader`
   - Read line by line with `readLine()` in a while loop
   - Print each line as: `"Read: [line]"`
   - Close both reader resources in a `finally` block
   - Print `"Done reading grocery_list.txt"` after the loop

3. **Handle missing file** — attempt to read `missing_file.txt`:
   - Catch the resulting `FileNotFoundException` (subclass of `IOException`)
   - Print: `"File not found: missing_file.txt"`

## Hints
- Declare `BufferedWriter bw = null` (and `FileWriter fw = null`) **before** the try block so they are in scope in the `finally` block
- In `finally`, check `if (bw != null)` before calling `bw.close()` to avoid a `NullPointerException` if the writer was never successfully opened
- `BufferedReader.readLine()` returns `null` at end-of-file — use `while ((line = br.readLine()) != null)`
- `FileNotFoundException` is in `java.io` — it extends `IOException`, so catching `IOException` also works

## Expected Output

```
=== Part 1: Write to file ===
File written: grocery_list.txt

=== Part 2: Read from file ===
Read: Apples
Read: Bananas
Read: Carrots
Read: Dates
Read: Eggplant
Done reading grocery_list.txt

=== Part 3: Handle missing file ===
File not found: missing_file.txt
```
