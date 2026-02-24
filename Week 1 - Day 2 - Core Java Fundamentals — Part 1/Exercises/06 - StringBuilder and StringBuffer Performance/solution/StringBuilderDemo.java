public class StringBuilderDemo {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Mutability — String += vs StringBuilder
        // =====================================================================
        System.out.println("=== Mutability: String vs StringBuilder ===");

        // Each += creates a brand-new String object — wasteful in a loop
        String result = "";
        for (int i = 0; i < 5; i++) {
            result += "item" + i + " ";
        }
        System.out.println("String (+=) result    : " + result);

        // StringBuilder maintains one mutable buffer — much more efficient
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append("item" + i + " ");
        }
        System.out.println("StringBuilder result  : " + sb.toString());

        // =====================================================================
        // SECTION 2: StringBuilder Method Practice
        // =====================================================================
        System.out.println();
        System.out.println("=== StringBuilder Method Practice ===");

        StringBuilder sb2 = new StringBuilder("Hello World");

        // append() adds to the END of the buffer
        sb2.append("!");
        System.out.println("After append(\"!\")     : " + sb2);

        // insert(index, str) inserts BEFORE the character at the given index
        // Index 5 is the space between "Hello" and "World" — inserting "," there gives "Hello, World!"
        sb2.insert(5, ",");
        System.out.println("After insert(5, \",\")  : " + sb2);

        // delete(start, end) removes characters from start (inclusive) to end (exclusive)
        // Deletes indices 0-5 ("Hello,"), leaving "World!"
        sb2.delete(0, 6);
        System.out.println("After delete(0, 6)    : " + sb2);

        // reverse() reverses the entire buffer in place
        sb2.reverse();
        System.out.println("After reverse()       : " + sb2);

        // length() returns the current number of characters in the buffer
        System.out.println("length()              : " + sb2.length());

        // toString() converts the StringBuilder to an immutable String
        System.out.println("toString()            : " + sb2.toString());

        /*
         * When to use StringBuffer vs StringBuilder:
         * Use StringBuffer when the string builder will be accessed by multiple threads simultaneously,
         * because its methods are synchronized — only one thread can modify it at a time, preventing
         * race conditions. In single-threaded code (the vast majority of cases), always use StringBuilder
         * because it has identical functionality but no synchronization overhead, making it faster.
         */

        // =====================================================================
        // SECTION 3: StringBuffer Demo
        // =====================================================================
        System.out.println();
        System.out.println("=== StringBuffer Demo ===");

        // StringBuffer has the exact same API as StringBuilder, just thread-safe
        StringBuffer safeSb = new StringBuffer("Thread");
        safeSb.append("-Safe");
        System.out.println("StringBuffer result   : " + safeSb.toString());
    }
}
