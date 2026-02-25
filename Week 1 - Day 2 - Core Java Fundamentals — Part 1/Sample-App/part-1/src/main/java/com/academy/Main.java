package com.academy;

/**
 * Day 2 Part 1 — JVM Architecture, Primitives, Type Conversion & Casting
 *
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Day 2 Part 1 — Data Types & Type Casting   ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        demoPrimitives();
        demoWrapperClasses();
        demoTypeCasting();
        demoAutoboxing();
        demoOverflow();
    }

    // ─────────────────────────────────────────────────────────
    // 1. Primitive Types
    // ─────────────────────────────────────────────────────────
    static void demoPrimitives() {
        System.out.println("=== 1. Primitive Data Types ===");

        byte   myByte   = 100;                  // -128 to 127
        short  myShort  = 32_000;               // -32,768 to 32,767
        int    myInt    = 2_147_483_647;         // ~2.1 billion (Integer.MAX_VALUE)
        long   myLong   = 9_000_000_000L;        // requires 'L' suffix
        float  myFloat  = 3.14f;                 // requires 'f' suffix, ~7 digits precision
        double myDouble = 3.14159265358979;      // default decimal type, ~15 digits
        char   myChar   = 'A';                   // single Unicode character
        boolean myBool  = true;                  // true or false

        System.out.printf("  byte:    %d   (range: -128 to 127)%n", myByte);
        System.out.printf("  short:   %d (range: ±32,767)%n", myShort);
        System.out.printf("  int:     %d (max: Integer.MAX_VALUE)%n", myInt);
        System.out.printf("  long:    %d (needs 'L' suffix)%n", myLong);
        System.out.printf("  float:   %.2f (needs 'f' suffix)%n", myFloat);
        System.out.printf("  double:  %.15f (default decimal)%n", myDouble);
        System.out.printf("  char:    %c (Unicode: \\u0041)%n", myChar);
        System.out.printf("  boolean: %b%n%n", myBool);
    }

    // ─────────────────────────────────────────────────────────
    // 2. Wrapper Classes
    // ─────────────────────────────────────────────────────────
    static void demoWrapperClasses() {
        System.out.println("=== 2. Wrapper Classes ===");

        // Each primitive has a corresponding Wrapper class
        Integer  wrapped    = Integer.valueOf(42);
        Double   wrappedDbl = Double.valueOf(3.14);

        // Useful static constants
        System.out.println("  Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("  Integer.MIN_VALUE = " + Integer.MIN_VALUE);
        System.out.println("  Double.MAX_VALUE  = " + Double.MAX_VALUE);

        // Parsing strings to numbers
        int    parsed     = Integer.parseInt("123");
        double parsedDbl  = Double.parseDouble("3.99");
        System.out.println("  Integer.parseInt(\"123\")    = " + parsed);
        System.out.println("  Double.parseDouble(\"3.99\") = " + parsedDbl);

        // Converting numbers to String
        String asString = Integer.toString(99);
        System.out.println("  Integer.toString(99)       = \"" + asString + "\"");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 3. Type Casting
    // ─────────────────────────────────────────────────────────
    static void demoTypeCasting() {
        System.out.println("=== 3. Type Casting ===");

        // Widening (implicit) — smaller type → larger type, no data loss
        int    myInt    = 100;
        long   widened  = myInt;    // int → long  (automatic)
        double widened2 = myInt;    // int → double (automatic)
        System.out.printf("  Widening:  int %d → long %d → double %.1f%n", myInt, widened, widened2);

        // Narrowing (explicit) — larger type → smaller type, may lose data
        double price      = 9.99;
        int    truncated  = (int) price;   // 9.99 → 9 (decimal part lost!)
        System.out.printf("  Narrowing: double %.2f → int %d (decimal LOST!)%n", price, truncated);

        double largeVal = 300.7;
        byte   overflow = (byte) largeVal; // 300 overflows byte range (-128 to 127)
        System.out.printf("  Overflow:  double %.1f → byte %d (value wraps around!)%n%n", largeVal, overflow);
    }

    // ─────────────────────────────────────────────────────────
    // 4. Autoboxing & Unboxing
    // ─────────────────────────────────────────────────────────
    static void demoAutoboxing() {
        System.out.println("=== 4. Autoboxing & Unboxing ===");

        // Autoboxing: Java automatically converts primitive → Wrapper
        int     primitive  = 42;
        Integer boxed      = primitive;   // autoboxing: int → Integer
        System.out.println("  Autoboxing:  int " + primitive + " → Integer " + boxed);

        // Unboxing: Java automatically converts Wrapper → primitive
        Integer wrapped   = Integer.valueOf(99);
        int     unboxed   = wrapped;      // unboxing: Integer → int
        System.out.println("  Unboxing:    Integer " + wrapped + " → int " + unboxed);

        // Common pitfall: null unboxing causes NullPointerException!
        Integer nullValue = null;
        try {
            int result = nullValue; // NPE!
        } catch (NullPointerException e) {
            System.out.println("  ⚠ Unboxing null Integer → NullPointerException!");
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 5. Integer Overflow Demo
    // ─────────────────────────────────────────────────────────
    static void demoOverflow() {
        System.out.println("=== 5. Integer Overflow ===");

        int max = Integer.MAX_VALUE;          // 2,147,483,647
        int overflowed = max + 1;             // wraps to -2,147,483,648!
        System.out.println("  Integer.MAX_VALUE + 1 = " + overflowed + "  ← wraps around!");

        // Fix: use long
        long noOverflow = (long) max + 1;
        System.out.println("  (long) MAX_VALUE + 1  = " + noOverflow + " ← correct with long");
        System.out.println();

        System.out.println("✓ Data Types & Casting demo complete.");
    }
}
