// ============================================================
// FILE: 01-strings-and-operations.java
// TOPIC: Strings and String Operations
// ============================================================

public class StringsAndOperations {

    public static void main(String[] args) {

        // ── CREATING STRINGS ──────────────────────────────────────────
        // String is NOT a primitive — it's an object (java.lang.String)
        // but Java gives it special syntax to feel like a primitive

        String firstName  = "Maria";             // string literal → stored in String Pool
        String lastName   = "Santos";
        String fullName   = new String("Maria"); // explicitly creates a new object on Heap
                                                  // ← almost never do this

        System.out.println("=== Creating Strings ===");
        System.out.println("firstName : " + firstName);
        System.out.println("lastName  : " + lastName);

        // ⚠️ == vs .equals() — most important String rule
        System.out.println();
        System.out.println("=== == vs .equals() ===");
        String a = "hello";
        String b = "hello";             // same literal → same pool object
        String c = new String("hello"); // forced new object on heap

        System.out.println("a == b           : " + (a == b));           // true  (same pool object)
        System.out.println("a == c           : " + (a == c));           // false (different objects)
        System.out.println("a.equals(c)      : " + a.equals(c));        // true  (same content)
        System.out.println("a.equalsIgnoreCase(\"HELLO\"): " + a.equalsIgnoreCase("HELLO")); // true

        System.out.println();
        System.out.println("=== String Concatenation ===");

        // + operator with Strings
        String greeting  = "Hello, " + firstName + "!";
        int    courseYear = 1;
        String info      = "Year " + courseYear + " student";  // int auto-converts to String

        System.out.println(greeting);
        System.out.println(info);

        System.out.println();
        System.out.println("=== String Methods ===");

        String course = "  Core Java Fundamentals  ";

        // Length
        System.out.println("length()        : " + course.length());          // includes spaces

        // Trim / Strip whitespace
        System.out.println("trim()          : '" + course.trim() + "'");      // removes leading/trailing spaces
        System.out.println("strip()         : '" + course.strip() + "'");     // Unicode-aware (Java 11+)

        // Case conversion
        System.out.println("toUpperCase()   : " + course.trim().toUpperCase());
        System.out.println("toLowerCase()   : " + course.trim().toLowerCase());

        // Substring
        String exact = "Core Java Fundamentals";
        System.out.println("substring(5)    : " + exact.substring(5));        // from index 5 to end
        System.out.println("substring(5,9)  : " + exact.substring(5, 9));     // index 5 inclusive to 9 exclusive

        // Contains / StartsWith / EndsWith
        System.out.println("contains(\"Java\"): " + exact.contains("Java"));
        System.out.println("startsWith(\"Core\"): " + exact.startsWith("Core"));
        System.out.println("endsWith(\"s\")   : " + exact.endsWith("s"));

        // indexOf / lastIndexOf
        System.out.println("indexOf(\"a\")    : " + exact.indexOf("a"));        // first occurrence
        System.out.println("lastIndexOf(\"a\"): " + exact.lastIndexOf("a"));    // last occurrence
        System.out.println("indexOf(\"xyz\")  : " + exact.indexOf("xyz"));      // -1 = not found

        // Replace
        System.out.println("replace()       : " + exact.replace("Java", "Python"));

        // Split
        String csv = "Alice,Bob,Charlie,Diana";
        String[] names = csv.split(",");
        System.out.println("split result    : " + java.util.Arrays.toString(names));
        System.out.println("split length    : " + names.length);

        // charAt
        System.out.println("charAt(5)       : " + exact.charAt(5));   // 'J'

        // isEmpty / isBlank
        System.out.println("isEmpty(\"\")     : " + "".isEmpty());      // true
        System.out.println("isBlank(\"  \")   : " + "  ".isBlank());    // true (Java 11+)

        // toCharArray
        char[] letters = "Java".toCharArray();
        System.out.print("toCharArray()   : ");
        for (char ch : letters) System.out.print(ch + " ");
        System.out.println();

        System.out.println();
        System.out.println("=== String.format() and formatted() ===");

        // String.format() — like printf in other languages
        String studentRecord = String.format("Name: %-15s | Score: %3d | GPA: %.2f",
                "Maria Santos", 95, 3.87);
        System.out.println(studentRecord);

        // Java 15+ .formatted() instance method — same as String.format()
        String report = "Student %s scored %d%%".formatted("Bob", 88);
        System.out.println(report);

        System.out.println();
        System.out.println("=== String is Immutable ===");
        // Every String method returns a NEW String — the original is unchanged
        String original = "hello";
        String upper    = original.toUpperCase();
        System.out.println("original after toUpperCase(): " + original); // still "hello"
        System.out.println("upper                       : " + upper);    // "HELLO"
    }
}
