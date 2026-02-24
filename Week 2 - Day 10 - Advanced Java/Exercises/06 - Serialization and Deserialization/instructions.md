# Exercise 06: Serialization and Deserialization

## Objective
Serialize a Java object to a binary file and deserialize it back, observing how the `transient` keyword prevents a field from being serialized.

## Background
**Serialization** converts an object's state to a byte stream so it can be stored in a file, sent over a network, or cached. **Deserialization** reconstructs the object from that byte stream.

Key rules:
- The class must implement `java.io.Serializable` (a marker interface — no methods to implement)
- Always declare `private static final long serialVersionUID` to avoid `InvalidClassException` when the class evolves
- Fields marked `transient` are **skipped** during serialization; they will be `null` (or default primitive values) after deserialization
- Use `ObjectOutputStream` to write and `ObjectInputStream` to read; always wrap in `try-with-resources`

## Requirements

1. **`Employee` class:**
   - Implements `Serializable`
   - `serialVersionUID = 1L`
   - Fields: `String name`, `String department`, `transient String password`
   - A constructor that accepts all three fields
   - Override `toString()` → `"Employee{name='Alice', department='Engineering', password='secret123'}"`

2. **Serialization:**
   - Create an `Employee("Alice", "Engineering", "secret123")`
   - Print it (shows all fields including password)
   - Write it to `"employee.ser"` using `ObjectOutputStream` + `FileOutputStream` (try-with-resources)
   - Print `"Written to employee.ser"`

3. **Deserialization:**
   - Read back from `"employee.ser"` using `ObjectInputStream` + `FileInputStream` (try-with-resources)
   - Print the deserialized object — `password` should now be `null` (transient)
   - Print `"Note: 'password' is transient — it was NOT serialized"`
   - Catch both `IOException` and `ClassNotFoundException`

## Hints
- `serialVersionUID` must match between the written and read class; if they differ, Java throws `InvalidClassException`
- `transient` is commonly used for passwords, tokens, database connections, and any field that shouldn't leave the JVM
- `ObjectOutputStream` / `ObjectInputStream` must wrap a byte-based stream (`FileOutputStream`, `ByteArrayOutputStream`, etc.), not a `PrintWriter`
- Delete `employee.ser` between runs if you change the class definition but not the `serialVersionUID`

## Expected Output

```
=== Serialization ===
Serialized: Employee{name='Alice', department='Engineering', password='secret123'}
Written to employee.ser

=== Deserialization ===
Deserialized: Employee{name='Alice', department='Engineering', password='null'}
Note: 'password' is transient — it was NOT serialized
```
