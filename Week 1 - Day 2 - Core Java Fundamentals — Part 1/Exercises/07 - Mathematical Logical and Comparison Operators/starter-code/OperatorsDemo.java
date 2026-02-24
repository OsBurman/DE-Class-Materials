public class OperatorsDemo {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Arithmetic Operators
        // =====================================================================
        System.out.println("=== Arithmetic Operators (a=17, b=5) ===");

        int a = 17;
        int b = 5;

        // TODO: Print the result of a + b with label "a + b  : "
        // TODO: Print the result of a - b with label "a - b  : "
        // TODO: Print the result of a * b with label "a * b  : "

        // TODO: Print the result of a / b with label "a / b  : "
        //       Add a comment on the same line explaining why the result is 3, not 3.4

        // TODO: Print the result of a % b with label "a % b  : "
        //       Add a comment explaining what % (modulo) means

        // TODO: Cast 'a' to double and divide by 'b' — print with label "(double)a/b : "
        //       This time the result should be 3.4

        // =====================================================================
        // SECTION 2: Comparison Operators
        // =====================================================================
        System.out.println();
        System.out.println("=== Comparison Operators (x=10, y=20) ===");

        int x = 10;
        int y = 20;

        // TODO: Print the results of x==y, x!=y, x<y, x>y, x<=y, x>=y
        //       Use the format "[operator] : [true/false]" as shown in the expected output

        // =====================================================================
        // SECTION 3: Logical Operators
        // =====================================================================
        System.out.println();
        System.out.println("=== Logical Operators (p=true, q=false) ===");

        boolean p = true;
        boolean q = false;

        // TODO: Print the results of p&&q, p||q, !p, !q with labels

        // TODO: Write a compound condition using && that demonstrates short-circuit evaluation.
        //       Example structure: if (someCondition && someOtherCondition)
        //       Add a comment explaining that if someCondition is false, someOtherCondition is never evaluated.

        // =====================================================================
        // SECTION 4: Compound Assignment Operators
        // =====================================================================
        System.out.println();
        System.out.println("=== Compound Assignment (n starts at 10) ===");

        int n = 10;

        // TODO: Apply n += 5 and print: "n += 5  → n = [value]"
        // TODO: Apply n -= 3 and print: "n -= 3  → n = [value]"
        // TODO: Apply n *= 2 and print: "n *= 2  → n = [value]"
        // TODO: Apply n /= 4 and print: "n /= 4  → n = [value]"
        // TODO: Apply n %= 3 and print: "n %= 3  → n = [value]"

        // =====================================================================
        // SECTION 5: Ternary Operator
        // =====================================================================
        System.out.println();
        System.out.println("=== Ternary Operator ===");

        int score = 74;

        // TODO: Use the ternary operator to assign "Pass" if score >= 70, else "Fail"
        //       Store result in a String variable named 'grade'
        //       Print: "Score 74: [grade]"

        // =====================================================================
        // SECTION 6: Operator Precedence
        // =====================================================================
        System.out.println();
        System.out.println("=== Operator Precedence ===");

        // TODO: Print the result of: 2 + 3 * 4
        //       Add a comment explaining why this equals 14 (multiplication first)

        // TODO: Print the result of: 10 - 4 / 2
        //       Add a comment explaining why this equals 8 (division first)

        // TODO: Print the result of: true || false && false
        //       Add a comment explaining why this equals true (&& has higher precedence than ||)
    }
}
