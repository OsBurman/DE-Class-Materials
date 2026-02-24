// ============================================================
// FILE: 02-stringbuilder-and-stringbuffer.java
// TOPIC: StringBuilder and StringBuffer (mutability, performance)
// ============================================================

public class StringBuilderAndStringBuffer {

    public static void main(String[] args) {

        // ── WHY STRINGBUILDER? ────────────────────────────────────────
        // String is IMMUTABLE — every modification creates a brand new String object
        // In a loop, this creates thousands of temporary String objects → memory waste
        //
        // StringBuilder is MUTABLE — you modify the same object in place
        // Use StringBuilder when you need to build or modify strings dynamically

        System.out.println("=== WHY StringBuilder? (Performance Demo) ===");

        // Bad: String concatenation in a loop
        // Each iteration creates a NEW String and discards the old one
        long startBad = System.nanoTime();
        String result = "";
        for (int i = 0; i < 10_000; i++) {
            result += i;   // creates a new String every iteration
        }
        long endBad = System.nanoTime();
        System.out.println("String concat loop time   : " + (endBad - startBad) / 1_000_000 + " ms");

        // Good: StringBuilder in a loop — one object, modified in place
        long startGood = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10_000; i++) {
            sb.append(i);  // modifies the SAME object — no garbage created
        }
        String sbResult = sb.toString();  // convert to String once at the end
        long endGood = System.nanoTime();
        System.out.println("StringBuilder loop time   : " + (endGood - startGood) / 1_000_000 + " ms");
        System.out.println("(StringBuilder is dramatically faster for large loops)");

        System.out.println();
        System.out.println("=== StringBuilder Methods ===");

        StringBuilder builder = new StringBuilder("Hello");

        // append — adds to the end
        builder.append(", ");
        builder.append("World");
        builder.append("!");
        System.out.println("After append()   : " + builder);  // Hello, World!

        // insert — inserts at index
        builder.insert(7, "Java ");
        System.out.println("After insert(7)  : " + builder);  // Hello, Java World!

        // delete — removes characters from start (inclusive) to end (exclusive)
        builder.delete(7, 12);
        System.out.println("After delete()   : " + builder);  // Hello, World!

        // replace — replaces characters from start to end
        builder.replace(7, 12, "Students");
        System.out.println("After replace()  : " + builder);  // Hello, Students!

        // reverse
        StringBuilder rev = new StringBuilder("Java");
        rev.reverse();
        System.out.println("After reverse()  : " + rev);      // avaJ

        // length and charAt — same as String
        System.out.println("length()         : " + builder.length());
        System.out.println("charAt(0)        : " + builder.charAt(0));

        // setCharAt — modify a single character
        builder.setCharAt(0, 'h');
        System.out.println("After setCharAt(): " + builder);  // hello, Students!

        // indexOf
        System.out.println("indexOf(\"Students\"): " + builder.indexOf("Students"));

        // deleteCharAt
        builder.deleteCharAt(builder.length() - 1); // removes last '!'
        System.out.println("After deleteCharAt(): " + builder);

        // Convert back to String
        String finalString = builder.toString();
        System.out.println("toString()       : '" + finalString + "'");

        // Method chaining — StringBuilder methods return 'this' so you can chain
        String chained = new StringBuilder()
                .append("Student: ")
                .append("Alice")
                .append(" | Score: ")
                .append(97)
                .toString();
        System.out.println("Chained          : " + chained);

        System.out.println();
        System.out.println("=== StringBuilder vs StringBuffer ===");

        // StringBuffer — same API as StringBuilder but THREAD-SAFE
        // Every method is synchronized — only one thread can use it at a time
        // Use StringBuffer only when multiple threads share the same buffer
        // Use StringBuilder in all other cases (faster — no synchronization overhead)

        StringBuffer buffer = new StringBuffer("Thread");
        buffer.append("-Safe");
        System.out.println("StringBuffer     : " + buffer);

        System.out.println();
        System.out.println("=== Summary: When to Use Each ===");
        System.out.println("String          → immutable, use for fixed text, keys, constants");
        System.out.println("StringBuilder   → mutable, use for building strings in loops or methods");
        System.out.println("StringBuffer    → thread-safe mutable, use ONLY in multi-threaded context");
    }
}
