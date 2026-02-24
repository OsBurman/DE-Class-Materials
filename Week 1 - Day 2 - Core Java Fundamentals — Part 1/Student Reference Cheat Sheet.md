# Day 2 Review — Core Java Fundamentals Part 1
## Quick Reference Guide

---

## 1. JVM / JRE / JDK

```
JDK (Java Development Kit)
  └── JRE (Java Runtime Environment)
        └── JVM (Java Virtual Machine)
              + Class Libraries
        + Compiler (javac) + dev tools
```

| Component | Contains | Use Case |
|---|---|---|
| **JVM** | Execution engine | Runs bytecode on any platform |
| **JRE** | JVM + standard libraries | For end users running Java apps |
| **JDK** | JRE + `javac` + dev tools | For developers writing Java code |

**Compilation and execution flow:**
```
Source code (.java)
  → javac (compiler)
  → Bytecode (.class)          ← platform-independent
  → JVM (JIT-compiles at runtime)
  → Native machine code         ← platform-specific
```

**Verify installation:**
```bash
java -version      # check JVM
javac -version     # check compiler (only present if JDK installed)
```

---

## 2. Primitive Data Types

| Type | Size | Range | Default | Use |
|---|---|---|---|---|
| `byte` | 8-bit | -128 to 127 | 0 | Rarely; memory-constrained |
| `short` | 16-bit | -32,768 to 32,767 | 0 | Rarely |
| `int` | 32-bit | ~-2.1B to 2.1B | 0 | **Default for integers** |
| `long` | 64-bit | ~±9.2 quintillion | 0L | Large numbers; suffix `L` |
| `float` | 32-bit | ~±3.4×10³⁸ | 0.0f | Rarely; suffix `F` |
| `double` | 64-bit | ~±1.7×10³⁰⁸ | 0.0 | **Default for decimals** |
| `boolean` | 1-bit | `true` / `false` | false | Conditions |
| `char` | 16-bit | 0–65,535 (Unicode) | `'\u0000'` | Single character; single quotes |

**Rule of thumb:** Use `int` for integers, `double` for decimals. Use `long` only when values exceed `Integer.MAX_VALUE` (~2.1 billion).

**Floating-point precision warning:**
```java
System.out.println(0.1 + 0.2);  // 0.30000000000000004 — not exact!
// For money: use BigDecimal, never float/double
```

---

## 3. Variables and Constants

**Declaration and initialization:**
```java
int age;              // declaration only
int age = 25;         // declaration + initialization
final double PI = 3.14159;  // constant — cannot be reassigned
```

**Naming conventions:**
- Variables: `camelCase` — `userName`, `maxRetries`, `isEnabled`
- Constants: `ALL_CAPS_WITH_UNDERSCORES` — `MAX_USERS`, `APP_NAME`
- Be descriptive; avoid `x`, `data`, `value1`

**Scope:**
```java
{
    int x = 5;    // x exists only inside this block
}
// x is gone here
```

**Default values (instance fields only — local variables must be initialized):**
- Numeric types → `0`
- `boolean` → `false`
- `char` → `'\u0000'`
- Object references → `null`

---

## 4. Type Conversion

**Widening (implicit) — safe, automatic:**
```
byte → short → int → long → float → double

int x = 100;
double y = x;    // automatic, no cast needed
```

**Narrowing (explicit) — may lose data, requires cast:**
```java
double d = 9.99;
int i = (int) d;         // i = 9 — decimal truncated, not rounded

int big = 300;
byte b = (byte) big;     // overflow — 300 doesn't fit in -128..127
```

**Casting syntax:** `(targetType) value`

---

## 5. Autoboxing and Unboxing

Each primitive has a **wrapper class** object equivalent:

| Primitive | Wrapper |
|---|---|
| `int` | `Integer` |
| `double` | `Double` |
| `boolean` | `Boolean` |
| `char` | `Character` |
| `long` | `Long` |

```java
Integer num = 42;         // autoboxing: int → Integer (automatic)
int x = num;              // unboxing: Integer → int (automatic)

// Collections require wrapper types:
ArrayList<Integer> list = new ArrayList<>();
list.add(5);              // autoboxes 5 → Integer automatically

// Null danger:
Integer y = null;
int z = y;                // NullPointerException at runtime!
```

**Useful wrapper methods:**
```java
Integer.parseInt("42")       // String → int
String.valueOf(42)            // int → String
Integer.MAX_VALUE             // 2147483647
Integer.MIN_VALUE             // -2147483648
Double.isNaN(x)
Double.isInfinite(x)
```

---

## 6. Strings

**Strings are immutable** — operations return a new `String`, they never modify the original.

```java
String name = "Alice";
String upper = name.toUpperCase();   // "ALICE" — name is still "Alice"
```

**Common String methods:**
```java
s.length()                    // number of characters
s.charAt(2)                   // character at index 2
s.substring(1)                // from index 1 to end
s.substring(1, 4)             // index 1 inclusive to 4 exclusive
s.toUpperCase()               // all uppercase
s.toLowerCase()               // all lowercase
s.trim()                      // remove leading/trailing whitespace
s.strip()                     // trim + handles Unicode whitespace (Java 11+)
s.equals("other")             // content comparison — use this, not ==
s.equalsIgnoreCase("other")   // case-insensitive compare
s.contains("sub")             // true if substring found
s.startsWith("pre")
s.endsWith("suf")
s.indexOf('c')                // first index of character
s.replace("old", "new")       // replace all occurrences
s.split(",")                  // split into String[]
"Hello %s, you are %d".formatted("Alice", 30)   // Java 15+
```

**Critical — `==` vs `.equals()`:**
```java
String a = new String("hello");
String b = new String("hello");
a == b;          // false — different objects in memory
a.equals(b);     // true  — same content
// Always use .equals() for string content comparison
```

**StringBuilder — for efficient string building:**
```java
// BAD in loops — creates a new String object every iteration:
String result = "";
for (int i = 0; i < 1000; i++) result += i;

// GOOD — StringBuilder is mutable:
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i);
String result = sb.toString();

// Key StringBuilder methods:
sb.append(x)           // add to end
sb.insert(index, x)    // insert at position
sb.delete(start, end)  // remove range
sb.reverse()           // reverse contents
sb.toString()          // convert back to String
```

**String vs StringBuilder vs StringBuffer:**
| | `String` | `StringBuilder` | `StringBuffer` |
|---|---|---|---|
| Mutable? | ❌ No | ✅ Yes | ✅ Yes |
| Thread-safe? | ✅ Yes | ❌ No | ✅ Yes |
| Performance | Slow (in loops) | Fast | Slower than SB |
| Use when | Normal usage | Building strings in loops | Multi-threaded (rare) |

---

## 7. Operators

**Arithmetic:**
```java
+   -   *   /   %          // addition, subtraction, multiply, divide, modulo
7 / 2     // = 3           (integer division — truncates)
7.0 / 2   // = 3.5         (double division)
7 % 3     // = 1           (remainder)
```

**Increment / Decrement:**
```java
int x = 5;
int a = x++;    // a = 5, x = 6  (use THEN increment)
int b = ++x;    // x = 7, b = 7  (increment THEN use)
```

**Compound assignment:**
```java
x += 5    // x = x + 5
x -= 3    // x = x - 3
x *= 2    // x = x * 2
x /= 4    // x = x / 4
x %= 3    // x = x % 3
```

**Comparison (always return `boolean`):**
```java
==   !=   <   >   <=   >=
```

**Logical:**
```java
&&   // AND — true only if both true; short-circuits (stops if first is false)
||   // OR  — true if at least one true; short-circuits (stops if first is true)
!    // NOT — inverts boolean
```

**Ternary:**
```java
int max = a > b ? a : b;
String label = isActive ? "online" : "offline";
// condition ? valueIfTrue : valueIfFalse
```

**Operator precedence (high to low):**
1. Unary: `++`, `--`, `!`
2. Multiplicative: `*`, `/`, `%`
3. Additive: `+`, `-`
4. Relational: `<`, `>`, `<=`, `>=`
5. Equality: `==`, `!=`
6. Logical AND: `&&`
7. Logical OR: `||`
8. Assignment: `=`, `+=`, etc.

Use parentheses when in doubt.

---

## 8. Code Documentation

**Single-line comment:**
```java
// Multiply by 1000 to convert seconds to milliseconds
int ms = seconds * 1000;
```

**Multi-line comment:**
```java
/*
 * This block handles the retry logic.
 * Max retries defined by MAX_RETRY_COUNT.
 */
```

**Javadoc (for public APIs):**
```java
/**
 * Calculates the factorial of a non-negative integer.
 *
 * @param n the input number (must be >= 0)
 * @return  factorial of n
 * @throws  IllegalArgumentException if n < 0
 */
public long factorial(int n) { ... }
```

**Key Javadoc tags:** `@param`, `@return`, `@throws`, `@author`, `@deprecated`, `@see`, `@since`

**Best practice:** Comment *why*, not *what*. The code shows what. Comments explain the intent.

---

## 9. First Java Program

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        System.out.print("No newline");   // no newline at end
    }
}
```

**Compile and run:**
```bash
javac HelloWorld.java   # produces HelloWorld.class
java HelloWorld         # runs the program
```

**File name must match class name exactly:** `HelloWorld.java` → `public class HelloWorld`
