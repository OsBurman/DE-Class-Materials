public class TypeConversion {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Widening Conversions (automatic — no cast needed)
        // =====================================================================
        System.out.println("=== Widening Conversions ===");

        // TODO: Declare a byte named 'smallValue' with value 42
        byte smallValue = 42;

        // TODO: Assign 'smallValue' to an int named 'intValue' (widening — no cast needed)

        // TODO: Assign 'intValue' to a long named 'longValue' (widening — no cast needed)

        // TODO: Assign 'longValue' to a double named 'doubleValue' (widening — no cast needed)

        // TODO: Print each variable with the format shown in instructions.md
        //       "byte  42   → int    : [value]"   etc.

        // =====================================================================
        // SECTION 2: Narrowing Conversions (explicit cast required)
        // =====================================================================
        System.out.println();
        System.out.println("=== Narrowing Conversions (explicit cast required) ===");

        // TODO: Cast the double literal 9.99 to an int named 'truncated'
        //       Print: "(int) 9.99           : [value]    (truncated, not rounded)"

        // TODO: Cast the int literal 130 to a byte named 'overflow'
        //       Print: "(byte) 130           : [value] (overflow wrap-around)"

        // TODO: Cast the int literal 65 to a char named 'letter'
        //       Print: "(char) 65            : [value]    (code point 65 = 'A')"

        // =====================================================================
        // SECTION 3: Autoboxing (primitive → wrapper class)
        // =====================================================================
        System.out.println();
        System.out.println("=== Autoboxing (primitive → wrapper) ===");

        // TODO: Assign the int literal 100 to an Integer variable named 'boxedInt'
        //       (Java autoboxes the int into an Integer object automatically)
        //       Print: "Integer autoboxed    : [value]"

        // TODO: Assign the double literal 3.14 to a Double variable named 'boxedDouble'
        //       Print: "Double  autoboxed    : [value]"

        // =====================================================================
        // SECTION 4: Unboxing (wrapper class → primitive)
        // =====================================================================
        System.out.println();
        System.out.println("=== Unboxing (wrapper → primitive) ===");

        // TODO: Unbox 'boxedInt' into a plain int named 'unboxedInt'
        //       (Just assign the Integer to an int — Java unboxes automatically)
        //       Print: "Unboxed int          : [value]"

        // TODO: Print the result of unboxedInt + 50
        //       Print: "Unboxed int + 50     : [value]"

        // =====================================================================
        // SECTION 5: String → Primitive using parseXxx
        // =====================================================================
        System.out.println();
        System.out.println("=== String → Primitive (parseXxx) ===");

        // TODO: Use Integer.parseInt() to parse the String "42" into an int named 'parsedInt'
        //       Print: "Integer.parseInt(\"42\")       : [value]"

        // TODO: Use Double.parseDouble() to parse the String "3.14" into a double named 'parsedDouble'
        //       Print: "Double.parseDouble(\"3.14\")   : [value]"
    }
}
