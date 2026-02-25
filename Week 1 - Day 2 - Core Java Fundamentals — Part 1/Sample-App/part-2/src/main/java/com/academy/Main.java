package com.academy;

/**
 * Day 2 Part 2 — Strings, StringBuilder, StringBuffer & Operators
 *
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔═════════════════════════════════════════════╗");
        System.out.println("║  Day 2 Part 2 — Strings & Operators Demo   ║");
        System.out.println("╚═════════════════════════════════════════════╝\n");

        demoStringMethods();
        demoStringImmutability();
        demoStringBuilder();
        demoStringBufferVsBuilder();
        demoOperators();
    }

    // ─────────────────────────────────────────────────────────
    // 1. Common String Methods
    // ─────────────────────────────────────────────────────────
    static void demoStringMethods() {
        System.out.println("=== 1. Common String Methods ===");

        String greeting = "  Hello, Academy!  ";

        System.out.println("  Original:            \"" + greeting + "\"");
        System.out.println("  .trim():             \"" + greeting.trim() + "\"");
        System.out.println("  .toUpperCase():      \"" + greeting.trim().toUpperCase() + "\"");
        System.out.println("  .toLowerCase():      \"" + greeting.trim().toLowerCase() + "\"");
        System.out.println("  .length():           " + greeting.length());
        System.out.println("  .charAt(8):          '" + greeting.trim().charAt(7) + "'");
        System.out.println("  .substring(7,14):    \"" + greeting.trim().substring(7, 14) + "\"");
        System.out.println("  .contains(\"Academy\"): " + greeting.contains("Academy"));
        System.out.println("  .startsWith(\"Hello\"): " + greeting.trim().startsWith("Hello"));
        System.out.println("  .replace(\"Hello\",\"Hi\"): \"" + greeting.trim().replace("Hello", "Hi") + "\"");
        System.out.println("  .indexOf(\"Academy\"): " + greeting.indexOf("Academy"));

        // Split
        String csv = "Alice,Bob,Carol,Dave";
        String[] names = csv.split(",");
        System.out.print("  \"" + csv + "\".split(\",\"): [");
        for (int i = 0; i < names.length; i++) {
            System.out.print(names[i] + (i < names.length - 1 ? ", " : "]\n"));
        }

        // Join
        String joined = String.join(" | ", names);
        System.out.println("  String.join(\" | \", names): \"" + joined + "\"");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 2. String Immutability
    // ─────────────────────────────────────────────────────────
    static void demoStringImmutability() {
        System.out.println("=== 2. String Immutability & String Pool ===");

        String a = "hello";
        String b = "hello";      // same reference from String Pool
        String c = new String("hello"); // new object on heap

        System.out.println("  String a = \"hello\", b = \"hello\", c = new String(\"hello\")");
        System.out.println("  a == b:        " + (a == b) + "  (same String pool reference)");
        System.out.println("  a == c:        " + (a == c) + "  (different object on heap)");
        System.out.println("  a.equals(c):   " + a.equals(c) + "  (content comparison — always use .equals()!)");

        // Immutability: operations return NEW strings
        String original = "Java";
        String modified = original.concat(" Rocks!");
        System.out.println("  original after .concat(): \"" + original + "\" (unchanged!)");
        System.out.println("  new string from .concat(): \"" + modified + "\"");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 3. StringBuilder — Mutable & Fast
    // ─────────────────────────────────────────────────────────
    static void demoStringBuilder() {
        System.out.println("=== 3. StringBuilder (Mutable, NOT thread-safe) ===");

        StringBuilder sb = new StringBuilder();
        sb.append("Students: ");
        sb.append("Alice").append(", ").append("Bob").append(", ").append("Carol");
        System.out.println("  After appends: \"" + sb + "\"");

        sb.insert(0, ">> ");        // insert at index 0
        System.out.println("  After insert:  \"" + sb + "\"");

        sb.delete(0, 3);             // delete chars 0-2
        System.out.println("  After delete:  \"" + sb + "\"");

        sb.reverse();
        System.out.println("  After reverse: \"" + sb + "\"");

        sb.reverse(); // reverse back
        sb.replace(10, 15, "Dave");
        System.out.println("  After replace: \"" + sb + "\"");
        System.out.println("  Length: " + sb.length());

        // Performance comparison: String vs StringBuilder
        System.out.println("\n  Performance demo (building 10,000 char string):");
        long start = System.nanoTime();
        String slowStr = "";
        for (int i = 0; i < 1000; i++) slowStr += "x"; // creates 1000 new String objects!
        long stringTime = System.nanoTime() - start;

        start = System.nanoTime();
        StringBuilder fastSb = new StringBuilder();
        for (int i = 0; i < 1000; i++) fastSb.append("x"); // one mutable buffer
        long sbTime = System.nanoTime() - start;

        System.out.println("  String concatenation: " + stringTime / 1_000 + " µs");
        System.out.println("  StringBuilder:        " + sbTime / 1_000 + " µs  (much faster!)");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 4. StringBuffer vs StringBuilder
    // ─────────────────────────────────────────────────────────
    static void demoStringBufferVsBuilder() {
        System.out.println("=== 4. StringBuffer vs StringBuilder ===");
        System.out.println("  StringBuffer:  synchronized (thread-safe), slower");
        System.out.println("  StringBuilder: NOT synchronized (not thread-safe), faster");
        System.out.println("  Rule: Use StringBuilder unless multiple threads share the same buffer.");

        StringBuffer buffer = new StringBuffer("Thread-safe buffer");
        buffer.append(" — synchronized methods");
        System.out.println("  StringBuffer result: \"" + buffer + "\"");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 5. Operators
    // ─────────────────────────────────────────────────────────
    static void demoOperators() {
        System.out.println("=== 5. Operators ===");

        // Arithmetic
        int a = 17, b = 5;
        System.out.println("  Arithmetic:  17+5=" + (a+b) + "  17-5=" + (a-b) +
                           "  17*5=" + (a*b) + "  17/5=" + (a/b) + "  17%5=" + (a%b));

        // Compound assignment
        int x = 10;
        x += 5; System.out.println("  x=10, x+=5 → " + x);
        x *= 2; System.out.println("  x*=2      → " + x);
        x %= 7; System.out.println("  x%=7      → " + x);

        // Comparison
        System.out.println("  Comparison:  5>3=" + (5>3) + "  5==5=" + (5==5) + "  5!=3=" + (5!=3));

        // Logical
        boolean p = true, q = false;
        System.out.println("  Logical:     true&&false=" + (p&&q) + "  true||false=" + (p||q) + "  !true=" + (!p));

        // Bitwise
        System.out.println("  Bitwise:     5&3=" + (5&3) + "  5|3=" + (5|3) + "  5^3=" + (5^3) + "  ~5=" + (~5));
        System.out.println("  Shift:       8>>1=" + (8>>1) + "  2<<2=" + (2<<2));

        // Ternary
        int score = 82;
        String grade = score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : "F";
        System.out.println("  Ternary:     score=82 → grade=\"" + grade + "\"");

        System.out.println("\n✓ Strings & Operators demo complete.");
    }
}
