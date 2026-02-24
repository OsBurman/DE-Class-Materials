public class PrimitiveTypes {

    // Inner class whose instance fields will take Java's default values for each primitive type
    static class DefaultValues {
        byte byteDefault;       // defaults to 0
        short shortDefault;     // defaults to 0
        int intDefault;         // defaults to 0
        long longDefault;       // defaults to 0
        float floatDefault;     // defaults to 0.0
        double doubleDefault;   // defaults to 0.0
        char charDefault;       // defaults to '\u0000' (null character)
        boolean boolDefault;    // defaults to false
    }

    public static void main(String[] args) {

        System.out.println("=== Primitive Types ===");

        // byte: 8-bit signed integer, range -128 to 127
        byte maxByte = 127;
        System.out.println("byte    : " + maxByte);

        // short: 16-bit signed integer, range -32,768 to 32,767
        short smallNum = 32000;
        System.out.println("short   : " + smallNum);

        // int: 32-bit signed integer — the default integer type in Java
        // Underscores in numeric literals improve readability (Java 7+)
        int population = 2_000_000;
        System.out.println("int     : " + population);

        // long: 64-bit signed integer — the 'L' suffix marks it as a long literal
        long bigNum = 9_000_000_000L;
        System.out.println("long    : " + bigNum);

        // float: 32-bit IEEE 754 floating-point — the 'f' suffix marks it as a float literal
        float pi = 3.14f;
        System.out.println("float   : " + pi);

        // double: 64-bit IEEE 754 floating-point — the default decimal type in Java
        double piPrecise = 3.141592653589793;
        System.out.println("double  : " + piPrecise);

        // char: 16-bit Unicode character, enclosed in single quotes
        char grade = 'A';
        System.out.println("char    : " + grade);

        // boolean: represents true or false — no numeric equivalent in Java
        boolean isJavaFun = true;
        System.out.println("boolean : " + isJavaFun);

        System.out.println();
        System.out.println("=== Primitive Defaults (as instance fields) ===");

        // Create an instance to observe default field values — fields are zero-initialized
        DefaultValues defaults = new DefaultValues();

        System.out.println("byte    default : " + defaults.byteDefault);
        System.out.println("short   default : " + defaults.shortDefault);
        System.out.println("int     default : " + defaults.intDefault);
        System.out.println("long    default : " + defaults.longDefault);
        System.out.println("float   default : " + defaults.floatDefault);
        System.out.println("double  default : " + defaults.doubleDefault);
        // char default is '\u0000' — printing the escape sequence as a string for clarity
        System.out.println("char    default : \\u0000");
        System.out.println("boolean default : " + defaults.boolDefault);
    }
}
