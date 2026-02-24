// ============================================================
// FILE: 03-operators.java
// TOPIC: Mathematical, Logical, and Comparison Operators
// ============================================================

public class Operators {

    public static void main(String[] args) {

        // ── MATHEMATICAL (ARITHMETIC) OPERATORS ──────────────────────
        System.out.println("=== Arithmetic Operators ===");

        int a = 17;
        int b = 5;

        System.out.println("a = " + a + ", b = " + b);
        System.out.println("a + b  = " + (a + b));   // 22  — addition
        System.out.println("a - b  = " + (a - b));   // 12  — subtraction
        System.out.println("a * b  = " + (a * b));   // 85  — multiplication
        System.out.println("a / b  = " + (a / b));   // 3   — INTEGER division (truncates!)
        System.out.println("a % b  = " + (a % b));   // 2   — modulus (remainder)

        // ⚠️ Integer division truncates
        System.out.println("10 / 3 = " + (10 / 3));          // 3, NOT 3.33
        System.out.println("10.0/3 = " + (10.0 / 3));        // 3.333... (double division)
        System.out.println("(double)10/3 = " + ((double)10 / 3)); // cast to get decimal result

        // Modulus use case — check even/odd
        System.out.println("17 % 2 = " + (17 % 2) + "  ← odd (remainder 1)");
        System.out.println("16 % 2 = " + (16 % 2) + "  ← even (remainder 0)");

        System.out.println();
        System.out.println("=== Increment and Decrement ===");

        int score = 10;
        System.out.println("score     = " + score);
        System.out.println("score++   = " + score++); // post-increment: uses THEN increments
        System.out.println("score now = " + score);   // 11
        System.out.println("++score   = " + ++score); // pre-increment: increments THEN uses
        System.out.println("score now = " + score);   // 12

        System.out.println();
        System.out.println("=== Compound Assignment Operators ===");

        int points = 100;
        points += 25;   // same as: points = points + 25
        System.out.println("points += 25  → " + points);  // 125

        points -= 10;
        System.out.println("points -= 10  → " + points);  // 115

        points *= 2;
        System.out.println("points *= 2   → " + points);  // 230

        points /= 5;
        System.out.println("points /= 5   → " + points);  // 46

        points %= 7;
        System.out.println("points %= 7   → " + points);  // 4

        System.out.println();
        System.out.println("=== Math class ===");

        System.out.println("Math.abs(-42)       : " + Math.abs(-42));
        System.out.println("Math.max(17, 5)     : " + Math.max(17, 5));
        System.out.println("Math.min(17, 5)     : " + Math.min(17, 5));
        System.out.println("Math.pow(2, 8)      : " + Math.pow(2, 8));    // 256.0
        System.out.println("Math.sqrt(144)      : " + Math.sqrt(144));    // 12.0
        System.out.println("Math.floor(3.9)     : " + Math.floor(3.9));   // 3.0
        System.out.println("Math.ceil(3.1)      : " + Math.ceil(3.1));    // 4.0
        System.out.println("Math.round(3.5)     : " + Math.round(3.5));   // 4
        System.out.println("Math.random()       : " + Math.random());     // [0.0, 1.0)
        // Random int 1-10:
        int dice = (int)(Math.random() * 6) + 1;
        System.out.println("Random dice roll    : " + dice);

        System.out.println();

        // ── COMPARISON (RELATIONAL) OPERATORS ────────────────────────
        // Always return boolean
        System.out.println("=== Comparison Operators ===");

        int passingScore = 70;
        int studentScore = 85;

        System.out.println("studentScore > passingScore  : " + (studentScore > passingScore));   // true
        System.out.println("studentScore < passingScore  : " + (studentScore < passingScore));   // false
        System.out.println("studentScore >= 85           : " + (studentScore >= 85));            // true
        System.out.println("studentScore <= 70           : " + (studentScore <= 70));            // false
        System.out.println("studentScore == 85           : " + (studentScore == 85));            // true (primitives: == is fine)
        System.out.println("studentScore != 85           : " + (studentScore != 85));            // false

        System.out.println();

        // ── LOGICAL OPERATORS ─────────────────────────────────────────
        System.out.println("=== Logical Operators ===");

        boolean isEnrolled  = true;
        boolean hasTextbook = false;
        boolean hasPassed   = studentScore >= passingScore;

        // AND — both must be true
        System.out.println("isEnrolled && hasPassed    : " + (isEnrolled && hasPassed));    // true
        System.out.println("isEnrolled && hasTextbook  : " + (isEnrolled && hasTextbook));  // false

        // OR — at least one must be true
        System.out.println("isEnrolled || hasTextbook  : " + (isEnrolled || hasTextbook));  // true
        System.out.println("!isEnrolled || hasTextbook : " + (!isEnrolled || hasTextbook)); // false

        // NOT — flips the boolean
        System.out.println("!isEnrolled               : " + !isEnrolled);                   // false
        System.out.println("!hasTextbook              : " + !hasTextbook);                  // true

        System.out.println();
        System.out.println("=== Short-Circuit Evaluation ===");
        // && stops evaluating as soon as it finds a false
        // || stops evaluating as soon as it finds a true
        // This prevents NullPointerExceptions — check for null FIRST

        String name = null;
        // Without short-circuit: name.length() would throw NullPointerException
        // With &&: if name == null → false → stops, never calls name.length()
        boolean isLongName = (name != null) && (name.length() > 5);
        System.out.println("null name, safe length check: " + isLongName); // false, no NPE

        System.out.println();

        // ── TERNARY OPERATOR ──────────────────────────────────────────
        System.out.println("=== Ternary Operator ===");
        // condition ? valueIfTrue : valueIfFalse
        // Compact inline if-else for simple assignments

        String grade       = studentScore >= 90 ? "A" :
                             studentScore >= 80 ? "B" :
                             studentScore >= 70 ? "C" : "F";
        System.out.println("Score " + studentScore + " → Grade: " + grade);

        String status = isEnrolled ? "Active" : "Inactive";
        System.out.println("Enrollment status: " + status);

        System.out.println();

        // ── BITWISE OPERATORS (AWARENESS) ────────────────────────────
        System.out.println("=== Bitwise Operators (overview) ===");
        int x = 0b1010;  // 10 in binary
        int y = 0b1100;  // 12 in binary

        System.out.println("x & y  (AND) : " + (x & y)  + "  (0b1000 = 8)");
        System.out.println("x | y  (OR)  : " + (x | y)  + "  (0b1110 = 14)");
        System.out.println("x ^ y  (XOR) : " + (x ^ y)  + "  (0b0110 = 6)");
        System.out.println("~x     (NOT) : " + (~x));           // flips all bits
        System.out.println("x << 1 (left shift) : " + (x << 1) + "  (* 2)");
        System.out.println("x >> 1 (right shift): " + (x >> 1) + "  (/ 2)");
    }
}
