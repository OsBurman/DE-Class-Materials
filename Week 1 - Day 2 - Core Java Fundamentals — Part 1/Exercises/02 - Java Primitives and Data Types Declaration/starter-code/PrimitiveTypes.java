public class PrimitiveTypes {

    // TODO: Declare a static inner class called DefaultValues with one instance field
    //       of each primitive type (no initialization — let them take their default values).
    //       We'll use this to demonstrate what default values look like for instance fields.
    static class DefaultValues {
        // TODO: Declare one instance field of each primitive type: byte, short, int, long,
        //       float, double, char, boolean — no assignment, just the declaration.
    }

    public static void main(String[] args) {

        System.out.println("=== Primitive Types ===");

        // TODO: Declare a byte variable named 'maxByte' with the value 127
        //       Then print: "byte    : " followed by its value

        // TODO: Declare a short variable named 'smallNum' with the value 32000
        //       Then print: "short   : " followed by its value

        // TODO: Declare an int variable named 'population' with the value 2_000_000
        //       (use underscores in the literal for readability)
        //       Then print: "int     : " followed by its value

        // TODO: Declare a long variable named 'bigNum' with the value 9_000_000_000L
        //       (don't forget the L suffix)
        //       Then print: "long    : " followed by its value

        // TODO: Declare a float variable named 'pi' with the value 3.14f
        //       (don't forget the f suffix)
        //       Then print: "float   : " followed by its value

        // TODO: Declare a double variable named 'piPrecise' with the value 3.141592653589793
        //       Then print: "double  : " followed by its value

        // TODO: Declare a char variable named 'grade' with the value 'A'
        //       Then print: "char    : " followed by its value

        // TODO: Declare a boolean variable named 'isJavaFun' with the value true
        //       Then print: "boolean : " followed by its value

        System.out.println();
        System.out.println("=== Primitive Defaults (as instance fields) ===");

        // TODO: Create an instance of DefaultValues called 'defaults'
        DefaultValues defaults = new DefaultValues();

        // TODO: Print each default value using the format "[type]    default : [value]"
        //       For char, the default is the null character — print the string "\u0000" (not the actual null char)
        //       Hint: the char default will print as an empty space; print the literal string "\\u0000" instead
    }
}
