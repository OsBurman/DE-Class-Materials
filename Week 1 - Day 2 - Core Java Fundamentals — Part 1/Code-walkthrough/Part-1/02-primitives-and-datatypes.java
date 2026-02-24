// ============================================================
// FILE: 02-primitives-and-datatypes.java
// TOPIC: Java Primitives and Data Types
// ============================================================

public class PrimitivesAndDatatypes {

    public static void main(String[] args) {

        // ── THE 8 PRIMITIVE TYPES ─────────────────────────────────────
        //
        //  Type      | Size    | Range / Notes
        //  ----------|---------|--------------------------------------
        //  byte      | 1 byte  | -128 to 127
        //  short     | 2 bytes | -32,768 to 32,767
        //  int       | 4 bytes | -2,147,483,648 to 2,147,483,647  ← most common integer type
        //  long      | 8 bytes | very large integers (use L suffix)
        //  float     | 4 bytes | ~6-7 decimal digits precision (use f suffix)
        //  double    | 8 bytes | ~15 decimal digits precision   ← most common decimal type
        //  char      | 2 bytes | single Unicode character (use single quotes)
        //  boolean   | 1 bit   | true or false ONLY

        System.out.println("=== Integer Types ===");

        byte  studentAge      = 22;           // small whole numbers
        short classCapacity   = 30_000;       // _ is a readability separator (Java 7+)
        int   enrollmentCount = 1_500_000;    // default integer type
        long  worldPopulation = 8_100_000_000L; // L suffix REQUIRED for long literals

        System.out.println("byte  studentAge     : " + studentAge);
        System.out.println("short classCapacity  : " + classCapacity);
        System.out.println("int   enrollmentCount: " + enrollmentCount);
        System.out.println("long  worldPopulation: " + worldPopulation);

        System.out.println();
        System.out.println("=== Decimal Types ===");

        float  itemPrice   = 9.99f;       // f suffix REQUIRED for float literals
        double accountBalance = 10_250.75; // default decimal type — use this over float

        System.out.println("float  itemPrice     : " + itemPrice);
        System.out.println("double accountBalance: " + accountBalance);

        System.out.println();
        System.out.println("=== Character Type ===");

        char grade         = 'A';         // single quotes for char — NOT double quotes
        char copyrightSign = '\u00A9';    // Unicode escape — © symbol

        System.out.println("char grade        : " + grade);
        System.out.println("char copyright    : " + copyrightSign);
        System.out.println("char as int       : " + (int) grade); // 'A' is 65 in ASCII/Unicode

        System.out.println();
        System.out.println("=== Boolean Type ===");

        boolean isEnrolled   = true;
        boolean hasPassed    = false;
        boolean isAdult      = studentAge >= 18;   // boolean result of a comparison

        System.out.println("isEnrolled: " + isEnrolled);
        System.out.println("hasPassed : " + hasPassed);
        System.out.println("isAdult   : " + isAdult);

        System.out.println();
        System.out.println("=== Default Values (class fields, not local variables) ===");
        // Local variables MUST be initialized before use — they have NO default value
        // Class-level fields have defaults:
        //   int/short/byte/long → 0
        //   float/double        → 0.0
        //   char                → '\u0000' (null char)
        //   boolean             → false
        //   Object reference    → null
        System.out.println("Note: local variables must be initialized before use.");
        System.out.println("Class fields get default values (0, false, null).");

        System.out.println();
        System.out.println("=== MIN / MAX VALUES ===");
        System.out.println("Integer.MIN_VALUE : " + Integer.MIN_VALUE);
        System.out.println("Integer.MAX_VALUE : " + Integer.MAX_VALUE);
        System.out.println("Double.MAX_VALUE  : " + Double.MAX_VALUE);
        // Each primitive type has a Wrapper class with useful constants
    }
}
