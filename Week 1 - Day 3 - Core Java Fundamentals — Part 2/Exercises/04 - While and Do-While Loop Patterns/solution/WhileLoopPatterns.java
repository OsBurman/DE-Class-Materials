public class WhileLoopPatterns {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: While loop — digit sum
        // =====================================================================
        System.out.println("=== While: Digit Sum ===");

        int number = 1534;
        int digitSum = 0;

        // While number still has digits left to process
        while (number > 0) {
            digitSum += number % 10; // extract units digit and add to sum
            number /= 10;            // shift all digits right (drop units digit)
        }
        // 1534 → digits: 4 + 3 + 5 + 1 = 13
        System.out.println("Digit sum of 1534 : " + digitSum);

        // =====================================================================
        // SECTION 2: While loop — powers of 2
        // =====================================================================
        System.out.println();
        System.out.println("=== While: Powers of 2 < 1000 ===");

        int power = 1;
        // The condition is checked BEFORE each iteration — if power starts >= 1000, loop never runs
        while (power < 1000) {
            System.out.println(power);
            power *= 2; // double: 1→2→4→8→…→512→1024 (1024 fails the condition, stops)
        }

        // =====================================================================
        // SECTION 3: Do-while loop — PIN simulation
        // =====================================================================
        System.out.println();
        System.out.println("=== Do-While: PIN Attempts ===");

        int correctPin = 1234;
        int[] attempts = {9999, 0, 1234};
        int index = 0;
        int attempt;

        // do-while guarantees at least one PIN attempt is made before checking the condition
        do {
            attempt = attempts[index]; // read current attempt
            System.out.println("Attempting PIN: " + attempt);
            index++; // advance to next attempt for next iteration
        } while (attempt != correctPin && index < attempts.length);
        // Loop continues if: the attempt was wrong AND there are still more attempts to try

        // Check outcome based on the last attempt value
        if (attempt == correctPin) {
            System.out.println("Access granted!");
        } else {
            System.out.println("Access denied!");
        }

        /*
         * A do-while is better here because we always need to make at least one attempt —
         * it doesn't make sense to check the condition before trying anything.
         * With a while loop, if we accidentally initialized 'attempt' to correctPin, we'd
         * skip the loop entirely and incorrectly grant access without the user doing anything.
         */
    }
}
