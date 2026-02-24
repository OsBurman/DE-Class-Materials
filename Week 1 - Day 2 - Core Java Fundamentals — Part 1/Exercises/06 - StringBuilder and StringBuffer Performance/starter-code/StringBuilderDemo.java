public class StringBuilderDemo {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Mutability — String += vs StringBuilder
        // =====================================================================
        System.out.println("=== Mutability: String vs StringBuilder ===");

        // TODO: Declare a String variable named 'result' initialized to ""
        //       Then write a for loop from i = 0 to i < 5
        //       In each iteration, append ("item" + i + " ") to 'result' using +=
        //       After the loop, print: "String (+=) result    : " followed by result

        // TODO: Declare a StringBuilder named 'sb' using new StringBuilder()
        //       Then write a for loop from i = 0 to i < 5
        //       In each iteration, call sb.append("item" + i + " ")
        //       After the loop, print: "StringBuilder result  : " followed by sb.toString()

        // =====================================================================
        // SECTION 2: StringBuilder Method Practice
        // =====================================================================
        System.out.println();
        System.out.println("=== StringBuilder Method Practice ===");

        StringBuilder sb2 = new StringBuilder("Hello World");

        // TODO: Call sb2.append("!") and print the result
        //       Format: "After append(\"!\")     : [value]"

        // TODO: Call sb2.insert(5, ",") and print the result
        //       Format: "After insert(5, \",\")  : [value]"

        // TODO: Call sb2.delete(0, 6) and print the result
        //       Format: "After delete(0, 6)    : [value]"

        // TODO: Call sb2.reverse() and print the result
        //       Format: "After reverse()       : [value]"

        // TODO: Print the current length of sb2
        //       Format: "length()              : [value]"

        // TODO: Print sb2.toString()
        //       Format: "toString()            : [value]"

        /*
         * TODO: Replace this TODO comment with a 2-3 sentence explanation of when to use
         * StringBuffer instead of StringBuilder, focusing on thread safety.
         */

        // =====================================================================
        // SECTION 3: StringBuffer Demo
        // =====================================================================
        System.out.println();
        System.out.println("=== StringBuffer Demo ===");

        // TODO: Create a StringBuffer named 'safeSb' initialized with "Thread"
        //       Append "-Safe" to it
        //       Print: "StringBuffer result   : " followed by safeSb.toString()
    }
}
