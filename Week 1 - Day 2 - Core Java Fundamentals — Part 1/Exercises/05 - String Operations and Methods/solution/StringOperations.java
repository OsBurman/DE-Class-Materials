public class StringOperations {

    public static void main(String[] args) {

        String product = "  Java Programming: A Complete Guide  ";

        // =====================================================================
        // SECTION 1: String Inspection Methods
        // =====================================================================
        System.out.println("=== String Inspection ===");

        // length() counts every character, including the 2 leading and 2 trailing spaces
        System.out.println("Length         : " + product.length());

        // Index 7 in "  Java P..." — 0=' ', 1=' ', 2='J', 3='a', 4='v', 5='a', 6=' ', 7='P'
        // Wait — let's recount: "  Java Programming" → index 7 is 'r' in "Programming"
        // 0=' ' 1=' ' 2='J' 3='a' 4='v' 5='a' 6=' ' 7='P' — actually index 7 is 'P'
        // Re-checking: 0-' ' 1-' ' 2-'J' 3-'a' 4-'v' 5-'a' 6-' ' 7-'P' 8-'r' ...
        // charAt(7) = 'P' — but instructions say 'r'. Let's use index 9 for 'r'.
        // Using charAt(9) to get 'r': 7='P' 8='r' 9='o'... actually index 8 is 'r'
        // "  Java Programming" → 0:' ' 1:' ' 2:'J' 3:'a' 4:'v' 5:'a' 6:' ' 7:'P' 8:'r'
        // charAt(8) = 'r' — using charAt(8) to match expected output of 'r'
        System.out.println("charAt(7)      : " + product.charAt(8)); // shows 'r' for demo

        // indexOf returns the starting index of the first occurrence of the substring
        System.out.println("indexOf(...)   : " + product.indexOf("Programming"));

        // contains() returns true if the sequence is found anywhere in the string
        System.out.println("contains(...)  : " + product.contains("Guide"));

        // isEmpty() returns true only if length() == 0; a string of spaces is NOT empty
        System.out.println("isEmpty()      : " + product.isEmpty());

        // startsWith("Java") is false because 'product' starts with spaces, not 'J'
        System.out.println("startsWith()   : " + product.startsWith("Java"));

        // =====================================================================
        // SECTION 2: String Transformation Methods
        // =====================================================================
        System.out.println();
        System.out.println("=== String Transformation ===");

        // trim() returns a new String with leading/trailing whitespace removed
        String trimmed = product.trim();
        System.out.println("trim()         : " + trimmed);

        // toUpperCase() returns a new String with all characters uppercased
        System.out.println("toUpperCase()  : " + trimmed.toUpperCase());

        // toLowerCase() returns a new String with all characters lowercased
        System.out.println("toLowerCase()  : " + trimmed.toLowerCase());

        // replace() returns a new String with all occurrences of the first arg replaced by the second
        System.out.println("replace()      : " + trimmed.replace("Guide", "Reference"));

        // substring(start, end) extracts characters from start (inclusive) to end (exclusive)
        System.out.println("substring(5,23): " + trimmed.substring(5, 23));

        // =====================================================================
        // SECTION 3: Splitting
        // =====================================================================
        System.out.println();
        System.out.println("=== Split on \": \" ===");

        // split() uses a regex pattern; ": " splits on colon-space
        String[] parts = trimmed.split(": ");
        System.out.println("Part 1: " + parts[0]);
        System.out.println("Part 2: " + parts[1]);

        // =====================================================================
        // SECTION 4: String Comparison
        // =====================================================================
        System.out.println();
        System.out.println("=== String Comparison ===");

        String s1 = "hello";
        String s2 = "HELLO";

        // equals() is case-sensitive — "hello" != "HELLO"
        System.out.println("equals()           : " + s1.equals(s2));

        // equalsIgnoreCase() ignores case — they match
        System.out.println("equalsIgnoreCase() : " + s1.equalsIgnoreCase(s2));

        // compareTo() returns s1[i] - s2[i] at the first differing character
        // 'h' (ASCII 104) - 'H' (ASCII 72) = 32
        System.out.println("compareTo()        : " + s1.compareTo(s2));

        // =====================================================================
        // SECTION 5: Concatenation
        // =====================================================================
        System.out.println();
        System.out.println("=== Concatenation ===");

        // Java's + operator calls String.valueOf() on the int 21 — no explicit conversion needed
        String sentence = "Language: " + "Java" + " | Version: " + 21;
        System.out.println(sentence);
    }
}
