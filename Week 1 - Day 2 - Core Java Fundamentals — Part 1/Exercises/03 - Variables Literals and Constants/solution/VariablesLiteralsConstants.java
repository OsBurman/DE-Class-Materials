public class VariablesLiteralsConstants {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Named Constants
        // =====================================================================
        System.out.println("=== Constants ===");

        // final prevents reassignment — naming convention: ALL_CAPS_WITH_UNDERSCORES
        final int SPEED_OF_LIGHT = 299_792_458;
        System.out.println("SPEED_OF_LIGHT  : " + SPEED_OF_LIGHT);

        final double PI = 3.14159265358979;
        System.out.println("PI              : " + PI);

        final int HTTP_OK = 200;
        System.out.println("HTTP_OK         : " + HTTP_OK);

        final double TAX_RATE = 0.085;
        System.out.println("TAX_RATE        : " + TAX_RATE);

        // char literal uses single quotes; '\n' is the newline escape sequence
        final char NEWLINE_CHAR = '\n';
        System.out.println("NEWLINE_CHAR    : " + NEWLINE_CHAR);
        // The line above prints an extra blank line because NEWLINE_CHAR is '\n'

        // Attempting to reassign a final variable causes a compile-time error:
        // SPEED_OF_LIGHT = 0; // ERROR: cannot assign a value to final variable SPEED_OF_LIGHT

        // =====================================================================
        // SECTION 2: Non-Decimal Literals
        // =====================================================================
        System.out.println("=== Non-Decimal Literals ===");

        // Hex literal: 0xFF0000 → 255*65536 + 0*256 + 0 = 16711680 (pure red in RGB)
        int colorRed = 0xFF0000;
        System.out.println("Red (hex 0xFF0000)  : " + colorRed);

        // Binary literal (Java 7+): 0b00001100 = 8+4 = 12
        int binaryTwelve = 0b00001100;
        System.out.println("Binary 0b00001100   : " + binaryTwelve);

        // Octal literal: a leading zero means octal; 010 octal = 8 decimal
        int octalEight = 010;
        System.out.println("Octal 010           : " + octalEight);

        // Scientific notation produces a double; cast to long to store as an integer
        long oneMillion = (long) 1e6;
        System.out.println("1e6 cast to long    : " + oneMillion);

        // =====================================================================
        // SECTION 3: String Variables
        // =====================================================================
        System.out.println();
        System.out.println("=== String Variables ===");

        // String is a class (not a primitive) — but string literals are baked into the class file
        String greeting = "Hello, Java!";
        System.out.println("greeting : " + greeting);

        // final works on reference types too — the reference cannot be changed to point elsewhere
        final String APP_NAME = "MyApp";
        System.out.println("APP_NAME : " + APP_NAME);
    }
}
