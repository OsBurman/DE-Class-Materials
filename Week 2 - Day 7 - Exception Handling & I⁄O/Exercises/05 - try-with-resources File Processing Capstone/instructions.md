# Exercise 05: try-with-resources File Processing Capstone

## Objective
Use `try-with-resources` to automatically manage file resources, eliminating the manual `finally`-close pattern from Exercise 04, and combine file I/O with exception handling in a realistic processing scenario.

## Background
Java 7 introduced `try-with-resources` for any class implementing `AutoCloseable`. Resources declared in the `try(...)` header are guaranteed to be closed automatically — even if an exception is thrown — without any `finally` block or null checks. This is the modern, idiomatic way to handle all I/O in Java. In this exercise you will write a temperature log file, process it to filter out invalid readings, and write only the valid readings to a results file.

## Requirements

1. **Write the input file** `temperatures.txt` using `try-with-resources` with `BufferedWriter`/`FileWriter`:
   - Write these 6 lines: `"23.5"`, `"INVALID"`, `"18.2"`, `"-5.0"`, `"CORRUPT"`, `"31.7"`
   - Print: `"Input file written: temperatures.txt"`

2. **Define a custom checked exception `InvalidReadingException`** (extends `Exception`):
   - Constructor: `InvalidReadingException(String raw)`
   - Message: `"Invalid temperature reading: '[raw]'"`

3. **Define a method `parseTemperature(String raw) throws InvalidReadingException`**:
   - Try `Double.parseDouble(raw)`; if it throws `NumberFormatException`, throw a new `InvalidReadingException(raw)` instead

4. **Process the file** using `try-with-resources`:
   - Open `temperatures.txt` for reading with `BufferedReader`/`FileReader`
   - Open `valid_temperatures.txt` for writing with `BufferedWriter`/`FileWriter`
   - Both resources can be declared in the **same** try-with-resources header (separated by `;`)
   - For each line: call `parseTemperature()`
     - If valid: write it to `valid_temperatures.txt`, print `"Valid reading: [value]°C"`
     - If `InvalidReadingException`: print `"Skipped invalid: [raw]"`
   - Print `"Processing complete."` after the loop

5. **Read back `valid_temperatures.txt`** using try-with-resources and print each line as `"Stored: [line]"`

## Hints
- try-with-resources syntax: `try (ResourceType r1 = ...; ResourceType r2 = ...) { ... }`
- Resources are closed in **reverse** declaration order — the last declared is closed first
- You can still have `catch` and `finally` after a try-with-resources block
- Catching `InvalidReadingException` inside the processing loop allows you to continue processing remaining lines rather than aborting

## Expected Output

```
=== Part 1: Write input file ===
Input file written: temperatures.txt

=== Part 2: Process temperatures ===
Valid reading: 23.5°C
Skipped invalid: INVALID
Valid reading: 18.2°C
Valid reading: -5.0°C
Skipped invalid: CORRUPT
Valid reading: 31.7°C
Processing complete.

=== Part 3: Read results file ===
Stored: 23.5
Stored: 18.2
Stored: -5.0
Stored: 31.7
```
