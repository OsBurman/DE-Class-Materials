public class StringOperations {

    public static void main(String[] args) {

        String product = "  Java Programming: A Complete Guide  ";

        // =====================================================================
        // SECTION 1: String Inspection Methods
        // =====================================================================
        System.out.println("=== String Inspection ===");

        // TODO: Print the total length of 'product' (including leading/trailing spaces)
        //       Format: "Length         : [value]"

        // TODO: Print the character at index 7 of 'product'
        //       Format: "charAt(7)      : [value]"

        // TODO: Print the index where "Programming" first appears in 'product'
        //       Format: "indexOf(...)   : [value]"

        // TODO: Print whether 'product' contains the word "Guide" (true/false)
        //       Format: "contains(...)  : [value]"

        // TODO: Print whether 'product' is empty (true/false)
        //       Format: "isEmpty()      : [value]"

        // TODO: Print whether 'product' starts with "Java" (check the original — it starts with spaces, so this should be false)
        //       Format: "startsWith()   : [value]"

        // =====================================================================
        // SECTION 2: String Transformation Methods
        // =====================================================================
        System.out.println();
        System.out.println("=== String Transformation ===");

        // TODO: Declare a new String 'trimmed' by calling trim() on 'product'
        //       Print: "trim()         : [trimmed value]"

        // TODO: Print trimmed.toUpperCase()
        //       Format: "toUpperCase()  : [value]"

        // TODO: Print trimmed.toLowerCase()
        //       Format: "toLowerCase()  : [value]"

        // TODO: Print trimmed.replace("Guide", "Reference")
        //       Format: "replace()      : [value]"

        // TODO: Print trimmed.substring(5, 23)
        //       Format: "substring(5,23): [value]"

        // =====================================================================
        // SECTION 3: Splitting
        // =====================================================================
        System.out.println();
        System.out.println("=== Split on \": \" ===");

        // TODO: Call trimmed.split(": ") and store the result in a String array named 'parts'
        //       Print "Part 1: " followed by parts[0]
        //       Print "Part 2: " followed by parts[1]

        // =====================================================================
        // SECTION 4: String Comparison
        // =====================================================================
        System.out.println();
        System.out.println("=== String Comparison ===");

        String s1 = "hello";
        String s2 = "HELLO";

        // TODO: Print the result of s1.equals(s2)
        //       Format: "equals()           : [value]"

        // TODO: Print the result of s1.equalsIgnoreCase(s2)
        //       Format: "equalsIgnoreCase() : [value]"

        // TODO: Print the result of s1.compareTo(s2)
        //       Format: "compareTo()        : [value]"

        // =====================================================================
        // SECTION 5: Concatenation
        // =====================================================================
        System.out.println();
        System.out.println("=== Concatenation ===");

        // TODO: Build a sentence using + operator: "Language: " + "Java" + " | Version: " + 21
        //       Store in a variable and print it
    }
}
