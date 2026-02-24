public class TypeConversion {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Widening Conversions (automatic — no cast needed)
        // =====================================================================
        System.out.println("=== Widening Conversions ===");

        byte smallValue = 42;

        // byte → int: widening, Java does this silently because int is larger
        int intValue = smallValue;
        System.out.println("byte  42   → int    : " + intValue);

        // int → long: widening, still safe
        long longValue = intValue;
        System.out.println("int   42   → long   : " + longValue);

        // long → double: widening; note that whole numbers print with ".0" as a double
        double doubleValue = longValue;
        System.out.println("long  42   → double : " + doubleValue);

        // =====================================================================
        // SECTION 2: Narrowing Conversions (explicit cast required)
        // =====================================================================
        System.out.println();
        System.out.println("=== Narrowing Conversions (explicit cast required) ===");

        // double → int: the cast TRUNCATES the decimal (floor toward zero, not round)
        int truncated = (int) 9.99;
        System.out.println("(int) 9.99           : " + truncated + "    (truncated, not rounded)");

        // int → byte: 130 > 127 (byte max), so the value wraps: 130 - 256 = -126
        byte overflow = (byte) 130;
        System.out.println("(byte) 130           : " + overflow + " (overflow wrap-around)");

        // int → char: Unicode code point 65 is the character 'A'
        char letter = (char) 65;
        System.out.println("(char) 65            : " + letter + "    (code point 65 = 'A')");

        // =====================================================================
        // SECTION 3: Autoboxing (primitive → wrapper class)
        // =====================================================================
        System.out.println();
        System.out.println("=== Autoboxing (primitive → wrapper) ===");

        // The compiler calls Integer.valueOf(100) behind the scenes — this is autoboxing
        Integer boxedInt = 100;
        System.out.println("Integer autoboxed    : " + boxedInt);

        // The compiler calls Double.valueOf(3.14) behind the scenes
        Double boxedDouble = 3.14;
        System.out.println("Double  autoboxed    : " + boxedDouble);

        // =====================================================================
        // SECTION 4: Unboxing (wrapper class → primitive)
        // =====================================================================
        System.out.println();
        System.out.println("=== Unboxing (wrapper → primitive) ===");

        // The compiler calls boxedInt.intValue() behind the scenes — this is unboxing
        int unboxedInt = boxedInt;
        System.out.println("Unboxed int          : " + unboxedInt);

        // Arithmetic on the unboxed value works like any other int
        System.out.println("Unboxed int + 50     : " + (unboxedInt + 50));

        // =====================================================================
        // SECTION 5: String → Primitive using parseXxx
        // =====================================================================
        System.out.println();
        System.out.println("=== String → Primitive (parseXxx) ===");

        // Integer.parseInt() converts a numeric String to a primitive int
        int parsedInt = Integer.parseInt("42");
        System.out.println("Integer.parseInt(\"42\")       : " + parsedInt);

        // Double.parseDouble() converts a decimal String to a primitive double
        double parsedDouble = Double.parseDouble("3.14");
        System.out.println("Double.parseDouble(\"3.14\")   : " + parsedDouble);
    }
}
