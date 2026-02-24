// ============================================================
// FILE: 04-type-conversion-casting-autoboxing.java
// TOPIC: Type Conversion, Casting, Autoboxing, and Unboxing
// ============================================================

public class TypeConversionCastingAutoboxing {

    public static void main(String[] args) {

        // ── WIDENING CONVERSION (Implicit / Automatic) ────────────────
        // Java automatically converts a smaller type to a larger type
        // No data loss possible → compiler does it silently
        //
        //   byte → short → int → long → float → double
        //                             char ↗
        //
        System.out.println("=== Widening Conversion (Implicit) ===");

        int    examScore  = 85;
        long   longScore  = examScore;    // int → long  (automatic, no cast needed)
        double gpa        = examScore;    // int → double (automatic)

        System.out.println("int    examScore : " + examScore);
        System.out.println("long   longScore : " + longScore);
        System.out.println("double gpa       : " + gpa);       // prints 85.0

        System.out.println();

        // ── NARROWING CONVERSION (Explicit Cast Required) ─────────────
        // Going from a larger type to a smaller type — may lose data
        // You MUST explicitly cast with (type) — tells compiler "I know the risk"
        System.out.println("=== Narrowing Conversion (Explicit Cast) ===");

        double preciseScore  = 92.75;
        int    roundedScore  = (int) preciseScore;  // TRUNCATES decimal — does NOT round
        System.out.println("double preciseScore  : " + preciseScore);
        System.out.println("int    roundedScore  : " + roundedScore);  // 92, NOT 93

        long  bigNumber = 1_500_000_000_000L;
        int   truncated = (int) bigNumber;           // DATA LOSS — value is outside int range
        System.out.println("long  bigNumber  : " + bigNumber);
        System.out.println("int   truncated  : " + truncated);  // garbage value — lost bits

        System.out.println();

        // ── CHAR AND INT CONVERSION ───────────────────────────────────
        System.out.println("=== Char / Int Conversion ===");

        char  letterA    = 'A';
        int   asciiValue = letterA;          // char → int (widening — automatic)
        char  backToChar = (char)(asciiValue + 1); // int → char (narrowing — requires cast)

        System.out.println("char  letterA    : " + letterA);
        System.out.println("int   asciiValue : " + asciiValue);  // 65
        System.out.println("char  next letter: " + backToChar);  // 'B'

        System.out.println();

        // ── STRING CONVERSIONS ────────────────────────────────────────
        // Primitive → String: use String.valueOf() or concatenate with ""
        // String → Primitive: use Integer.parseInt(), Double.parseDouble(), etc.
        System.out.println("=== String Conversions ===");

        int    numericAge    = 25;
        String ageAsString   = String.valueOf(numericAge);   // int → String
        String alsoAge       = numericAge + "";              // concat trick (works but less clear)

        String inputFromUser = "42";                         // pretend this came from the user
        int    parsedScore   = Integer.parseInt(inputFromUser);  // String → int
        double parsedGpa     = Double.parseDouble("3.85");       // String → double

        System.out.println("int → String : '" + ageAsString + "'");
        System.out.println("String → int : "  + parsedScore);
        System.out.println("String → double: " + parsedGpa);

        // What happens if the String isn't a valid number?
        try {
            int bad = Integer.parseInt("twenty"); // throws NumberFormatException
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: \"twenty\" is not a valid int");
        }

        System.out.println();

        // ── AUTOBOXING ────────────────────────────────────────────────
        // Autoboxing = Java automatically wraps a primitive into its Wrapper class
        //
        //   Primitive  →  Wrapper Class
        //   int        →  Integer
        //   double     →  Double
        //   boolean    →  Boolean
        //   char       →  Character
        //   (etc.)
        //
        // Needed because Collections (ArrayList, HashMap, etc.) can only hold OBJECTS
        // not primitives — so Java converts them for you automatically
        System.out.println("=== Autoboxing (primitive → Wrapper) ===");

        int     primitiveCount = 100;
        Integer boxedCount     = primitiveCount;   // autoboxing — compiler wraps it for you

        System.out.println("primitive int     : " + primitiveCount);
        System.out.println("Integer (boxed)   : " + boxedCount);
        System.out.println("Integer class     : " + boxedCount.getClass().getSimpleName());

        // Autoboxing in action with a collection
        java.util.ArrayList<Integer> scores = new java.util.ArrayList<>();
        scores.add(95);   // autoboxing — int 95 → Integer automatically
        scores.add(87);
        scores.add(73);
        System.out.println("ArrayList<Integer>: " + scores);

        System.out.println();

        // ── UNBOXING ──────────────────────────────────────────────────
        // Unboxing = Java automatically unwraps a Wrapper object back to a primitive
        System.out.println("=== Unboxing (Wrapper → primitive) ===");

        Integer wrappedScore = Integer.valueOf(88);  // explicit boxing
        int     rawScore     = wrappedScore;         // unboxing — compiler unwraps for you

        System.out.println("Integer wrappedScore: " + wrappedScore);
        System.out.println("int     rawScore    : " + rawScore);

        // Arithmetic with Integer objects — unboxing happens automatically
        Integer a = 30;
        Integer b = 20;
        int     sum = a + b;   // both are unboxed to int, then added
        System.out.println("Integer 30 + Integer 20 = " + sum);

        // ⚠️ WATCH OUT: NullPointerException when unboxing null
        Integer nullableScore = null;
        try {
            int boom = nullableScore;  // NullPointerException — can't unbox null
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: cannot unbox a null Integer!");
        }

        System.out.println();

        // ── INTEGER CACHE GOTCHA ──────────────────────────────────────
        // Java caches Integer objects from -128 to 127
        // == compares REFERENCES, not values — always use .equals() for objects
        System.out.println("=== Integer Cache Gotcha ===");

        Integer x = 127;
        Integer y = 127;
        System.out.println("127 == 127 (Integer): " + (x == y));    // true  (cached)

        Integer p = 128;
        Integer q = 128;
        System.out.println("128 == 128 (Integer): " + (p == q));    // false (not cached — different objects!)
        System.out.println("128.equals(128)     : " + p.equals(q)); // true  (always use equals!)
    }
}
