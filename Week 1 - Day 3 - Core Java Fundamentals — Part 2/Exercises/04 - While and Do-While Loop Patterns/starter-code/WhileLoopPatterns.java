public class WhileLoopPatterns {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: While loop — digit sum
        // =====================================================================
        System.out.println("=== While: Digit Sum ===");

        int number = 1534;
        int digitSum = 0;

        // TODO: Write a while loop that runs as long as 'number' is greater than 0
        //       Inside the loop:
        //         1. Extract the last digit using: number % 10
        //         2. Add that digit to 'digitSum'
        //         3. Remove the last digit using: number = number / 10
        // After the loop, print: "Digit sum of 1534 : [digitSum]"

        // =====================================================================
        // SECTION 2: While loop — powers of 2
        // =====================================================================
        System.out.println();
        System.out.println("=== While: Powers of 2 < 1000 ===");

        // TODO: Declare an int named 'power' starting at 1
        //       Write a while loop that runs while 'power' is less than 1000
        //       Inside the loop: print 'power', then double it with power *= 2

        // =====================================================================
        // SECTION 3: Do-while loop — PIN simulation
        // =====================================================================
        System.out.println();
        System.out.println("=== Do-While: PIN Attempts ===");

        int correctPin = 1234;
        int[] attempts = {9999, 0, 1234};
        int index = 0;

        // TODO: Write a do-while loop:
        //       Body:
        //         1. Get the current attempt: int attempt = attempts[index]
        //         2. Print: "Attempting PIN: " + attempt
        //         3. Increment index
        //       Condition (at the bottom): loop while the attempt != correctPin
        //                                  AND index < attempts.length
        //       After the loop: print "Access granted!" if the last attempt matched correctPin,
        //                       otherwise print "Access denied!"

        /*
         * TODO: Replace this comment with a 2-sentence explanation of why do-while
         * is more appropriate than while for the PIN entry scenario.
         */
    }
}
