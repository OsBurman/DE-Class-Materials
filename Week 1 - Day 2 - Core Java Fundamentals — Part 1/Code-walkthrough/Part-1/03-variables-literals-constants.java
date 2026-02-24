// ============================================================
// FILE: 03-variables-literals-constants.java
// TOPIC: Variables, Literals, and Constants
// ============================================================

public class VariablesLiteralsConstants {

    // ── CLASS-LEVEL CONSTANT ──────────────────────────────────────────
    // 'static final' = class-level constant — belongs to the class, not an instance
    // Convention: ALL_CAPS_WITH_UNDERSCORES
    static final double TAX_RATE   = 0.08;        // 8% sales tax — never changes
    static final int    MAX_RETRIES = 3;
    static final String APP_NAME    = "StudentPortal";

    public static void main(String[] args) {

        // ── VARIABLES ─────────────────────────────────────────────────
        // Syntax: <type> <name> = <value>;
        // Three things in one statement: declaration + initialization + assignment

        // Declaration only (value assigned later):
        int studentCount;
        studentCount = 25;  // assigned here

        // Declaration + initialization in one line:
        String courseName = "Core Java Fundamentals";
        double averageScore = 87.4;

        System.out.println("=== Variables ===");
        System.out.println("Students    : " + studentCount);
        System.out.println("Course      : " + courseName);
        System.out.println("Avg Score   : " + averageScore);

        System.out.println();

        // ── LITERALS ──────────────────────────────────────────────────
        // A literal is a fixed value written directly in code

        System.out.println("=== Literals ===");

        // Integer literals
        int decimal     = 100;           // standard base-10
        int binary      = 0b01100100;    // binary prefix 0b  (= 100 in decimal)
        int octal       = 0144;          // octal prefix 0    (= 100 in decimal)
        int hex         = 0x64;          // hex prefix 0x     (= 100 in decimal)

        System.out.println("decimal = " + decimal);
        System.out.println("binary  = " + binary  + "  (0b01100100)");
        System.out.println("octal   = " + octal   + "  (0144)");
        System.out.println("hex     = " + hex     + "  (0x64)");

        // Float/double literals
        double scientific = 1.5e3;      // 1.5 × 10³ = 1500.0 — scientific notation
        System.out.println("scientific  = " + scientific);

        // Char literals
        char letter  = 'J';
        char unicode = '\u0041';  // Unicode for 'A'
        System.out.println("char letter  = " + letter);
        System.out.println("char unicode = " + unicode);

        // String literal — stored in the String Pool (NOT the heap)
        String greeting = "Hello, Java!";
        System.out.println("String      = " + greeting);

        // null literal — reference types can be null (primitives CANNOT)
        String unassigned = null;
        System.out.println("null string = " + unassigned);

        System.out.println();

        // ── CONSTANTS ─────────────────────────────────────────────────
        // 'final' = value cannot be reassigned after initialization
        // Use final for local constants (method-scope)

        final double DISCOUNT_RATE = 0.15;  // 15% discount — local final
        double originalPrice = 200.00;
        double discountAmount = originalPrice * DISCOUNT_RATE;
        double finalPrice     = originalPrice - discountAmount;

        // DISCOUNT_RATE = 0.20;  // ← COMPILE ERROR: cannot assign to final variable

        System.out.println("=== Constants ===");
        System.out.println("TAX_RATE (class const)  : " + TAX_RATE);
        System.out.println("DISCOUNT_RATE (local)   : " + DISCOUNT_RATE);
        System.out.println("Original Price          : $" + originalPrice);
        System.out.println("After Discount          : $" + finalPrice);
        System.out.println("After Tax               : $" + (finalPrice * (1 + TAX_RATE)));

        System.out.println();

        // ── NAMING CONVENTIONS ────────────────────────────────────────
        System.out.println("=== Naming Conventions ===");
        System.out.println("Variables / methods : camelCase       → studentName, calculateTotal()");
        System.out.println("Classes             : PascalCase      → StudentRecord, CourseManager");
        System.out.println("Constants           : ALL_CAPS        → MAX_RETRIES, TAX_RATE");
        System.out.println("Packages            : all.lowercase   → com.company.app");
    }
}
