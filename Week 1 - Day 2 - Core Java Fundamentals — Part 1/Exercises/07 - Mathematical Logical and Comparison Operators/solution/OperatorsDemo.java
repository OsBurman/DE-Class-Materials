public class OperatorsDemo {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Arithmetic Operators
        // =====================================================================
        System.out.println("=== Arithmetic Operators (a=17, b=5) ===");

        int a = 17;
        int b = 5;

        System.out.println("a + b  : " + (a + b));
        System.out.println("a - b  : " + (a - b));
        System.out.println("a * b  : " + (a * b));

        // Integer division: both operands are int, so the result is int — the decimal is truncated
        System.out.println("a / b  : " + (a / b) + "   (integer division — truncated)");

        // Modulo: gives the remainder after division; 17 = 3 * 5 + 2, so remainder is 2
        System.out.println("a % b  : " + (a % b) + "   (remainder)");

        // Cast one operand to double first — then the division is floating-point
        System.out.println("(double)a/b : " + ((double) a / b));

        // =====================================================================
        // SECTION 2: Comparison Operators
        // =====================================================================
        System.out.println();
        System.out.println("=== Comparison Operators (x=10, y=20) ===");

        int x = 10;
        int y = 20;

        System.out.println("x == y : " + (x == y));
        System.out.println("x != y : " + (x != y));
        System.out.println("x < y  : " + (x < y));
        System.out.println("x > y  : " + (x > y));
        System.out.println("x <= y : " + (x <= y));
        System.out.println("x >= y : " + (x >= y));

        // =====================================================================
        // SECTION 3: Logical Operators
        // =====================================================================
        System.out.println();
        System.out.println("=== Logical Operators (p=true, q=false) ===");

        boolean p = true;
        boolean q = false;

        // && returns true only if BOTH sides are true
        System.out.println("p && q : " + (p && q));

        // || returns true if AT LEAST ONE side is true
        System.out.println("p || q : " + (p || q));

        // ! negates the boolean value
        System.out.println("!p     : " + (!p));
        System.out.println("!q     : " + (!q));

        // Short-circuit &&: if p && q is false, the second condition is NOT evaluated.
        // Here q is false, so (p && q) is false immediately — the second part never runs.
        // This prevents NullPointerExceptions in real code (e.g., obj != null && obj.getValue() > 0)
        if (p && q) {
            System.out.println("Both are true");
        } else {
            System.out.println("Short-circuit: q was false, so no need to check further");
        }

        // =====================================================================
        // SECTION 4: Compound Assignment Operators
        // =====================================================================
        System.out.println();
        System.out.println("=== Compound Assignment (n starts at 10) ===");

        int n = 10;

        n += 5;   // n = n + 5 = 15
        System.out.println("n += 5  → n = " + n);

        n -= 3;   // n = n - 3 = 12
        System.out.println("n -= 3  → n = " + n);

        n *= 2;   // n = n * 2 = 24
        System.out.println("n *= 2  → n = " + n);

        n /= 4;   // n = n / 4 = 6
        System.out.println("n /= 4  → n = " + n);

        n %= 3;   // n = n % 3 = 0 (6 divides evenly by 3, remainder is 0)
        System.out.println("n %= 3  → n = " + n);

        // =====================================================================
        // SECTION 5: Ternary Operator
        // =====================================================================
        System.out.println();
        System.out.println("=== Ternary Operator ===");

        int score = 74;

        // Ternary: condition ? valueIfTrue : valueIfFalse
        // Equivalent to: if (score >= 70) grade = "Pass"; else grade = "Fail";
        String grade = (score >= 70) ? "Pass" : "Fail";
        System.out.println("Score 74: " + grade);

        // =====================================================================
        // SECTION 6: Operator Precedence
        // =====================================================================
        System.out.println();
        System.out.println("=== Operator Precedence ===");

        // * has higher precedence than + → 3*4=12, then 2+12=14 (NOT (2+3)*4=20)
        System.out.println("2 + 3 * 4       = " + (2 + 3 * 4));

        // / has higher precedence than - → 4/2=2, then 10-2=8 (NOT (10-4)/2=3)
        System.out.println("10 - 4 / 2      = " + (10 - 4 / 2));

        // && has higher precedence than || → false&&false=false, then true||false=true
        System.out.println("true || false && false = " + (true || false && false));
    }
}
