/**
 * 03-common-patterns.java
 * Day 3 — Core Java Fundamentals Part 2
 *
 * Topics covered:
 *   - Accumulator pattern (running total / product)
 *   - Counter pattern (counting elements that meet a condition)
 *   - Linear search pattern (find an element or its index)
 *   - Min / Max finder pattern
 *   - Flag pattern (was a condition ever true?)
 *   - Two-pointer / index tracking pattern
 *   - Combining patterns with arrays and control flow
 *
 * These are the fundamental building blocks that appear in nearly every
 * real-world program. Recognizing and naming the pattern is the first
 * step to becoming a confident programmer.
 */
import java.util.Arrays;

public class CommonPatterns {

    public static void main(String[] args) {

        // =========================================================
        // PATTERN 1: Accumulator
        // =========================================================
        // Purpose: Build up a running total (or product, or String) over a loop
        // Template:
        //   accumulator = startingValue;
        //   for each item:
        //       accumulator = accumulator (operation) item;

        System.out.println("=== PATTERN 1: Accumulator ===");

        double[] monthlyRevenue = {12500.00, 18200.50, 9750.75, 22000.00,
                                   16400.25, 19800.00, 14300.50, 21000.00,
                                   17600.75, 20100.50, 23400.00, 25800.25};

        // Accumulate total revenue
        double totalRevenue = 0.0;  // ← accumulator starts at identity for addition (0)
        for (double revenue : monthlyRevenue) {
            totalRevenue += revenue;  // ← accumulate each month
        }
        System.out.printf("Annual revenue: $%.2f%n", totalRevenue);
        System.out.printf("Monthly average: $%.2f%n", totalRevenue / monthlyRevenue.length);

        // Accumulate a product (factorial example)
        int n = 7;
        long factorial = 1;  // ← identity for multiplication is 1 (not 0!)
        for (int i = 1; i <= n; i++) {
            factorial *= i;
        }
        System.out.println("7! = " + factorial);  // 5040

        // Accumulate a String (building a sentence word by word)
        String[] words = {"Java", "is", "powerful", "and", "versatile"};
        StringBuilder sentence = new StringBuilder();  // efficient String accumulator
        for (int i = 0; i < words.length; i++) {
            sentence.append(words[i]);
            if (i < words.length - 1) {
                sentence.append(" ");
            }
        }
        System.out.println("Sentence: " + sentence);

        // =========================================================
        // PATTERN 2: Counter
        // =========================================================
        // Purpose: Count how many elements satisfy a condition
        // Template:
        //   count = 0;
        //   for each item:
        //       if condition: count++

        System.out.println("\n=== PATTERN 2: Counter ===");

        int[] surveyRatings = {4, 2, 5, 3, 5, 1, 4, 5, 3, 2, 5, 4, 1, 3, 5};

        int countExcellent = 0;   // 5-star ratings
        int countPoor = 0;        // 1 or 2 stars
        int countNeutral = 0;     // 3 stars

        for (int rating : surveyRatings) {
            if (rating == 5) {
                countExcellent++;
            } else if (rating <= 2) {
                countPoor++;
            } else {
                countNeutral++;
            }
        }

        System.out.println("Survey results (" + surveyRatings.length + " responses):");
        System.out.println("  Excellent (5★): " + countExcellent);
        System.out.println("  Neutral   (3★): " + countNeutral);
        System.out.println("  Poor    (1-2★): " + countPoor);

        // Percentage calculation combining counter + accumulator
        double percentExcellent = (double) countExcellent / surveyRatings.length * 100;
        System.out.printf("  Satisfaction rate: %.1f%%  %n", percentExcellent);

        // =========================================================
        // PATTERN 3: Linear Search
        // =========================================================
        // Purpose: Find whether a value exists, and optionally where
        // Template:
        //   foundIndex = -1;  // -1 = sentinel for "not found"
        //   for each item (with index):
        //       if item matches target:
        //           foundIndex = index
        //           break

        System.out.println("\n=== PATTERN 3: Linear Search ===");

        String[] employeeIds = {"EMP001", "EMP047", "EMP102", "EMP203", "EMP316", "EMP412"};

        // Search for a specific ID
        String searchId = "EMP203";
        int foundAt = -1;

        for (int i = 0; i < employeeIds.length; i++) {
            if (employeeIds[i].equals(searchId)) {
                foundAt = i;
                break;  // no need to keep looking once found
            }
        }

        if (foundAt != -1) {
            System.out.println("Found " + searchId + " at position " + foundAt);
        } else {
            System.out.println(searchId + " not found in employee database");
        }

        // Search that returns first occurrence meeting a condition
        int[] temperatures = {22, 18, 31, 15, 28, 35, 19, 24};
        int firstHeatwave = -1;  // first temp above 30

        for (int i = 0; i < temperatures.length; i++) {
            if (temperatures[i] > 30) {
                firstHeatwave = i;
                break;
            }
        }

        System.out.println("First heatwave day index: " + firstHeatwave +
                " (temp: " + (firstHeatwave != -1 ? temperatures[firstHeatwave] : "N/A") + "°C)");

        // =========================================================
        // PATTERN 4: Min / Max Finder
        // =========================================================
        // Purpose: Find the largest or smallest value in a collection
        // Template:
        //   min/max = first element;  ← critical: start with actual data, not 0
        //   for each remaining item:
        //       if item < min: min = item
        //       if item > max: max = item

        System.out.println("\n=== PATTERN 4: Min / Max Finder ===");

        int[] stockPrices = {142, 156, 138, 165, 149, 172, 131, 168, 153, 177};

        // Start with the first element — NOT with 0 or Integer.MAX_VALUE (until you know why)
        int minPrice = stockPrices[0];
        int maxPrice = stockPrices[0];
        int minIndex = 0;
        int maxIndex = 0;

        for (int i = 1; i < stockPrices.length; i++) {   // start at 1 — already checked [0]
            if (stockPrices[i] < minPrice) {
                minPrice = stockPrices[i];
                minIndex = i;
            }
            if (stockPrices[i] > maxPrice) {
                maxPrice = stockPrices[i];
                maxIndex = i;
            }
        }

        System.out.println("Stock prices: " + Arrays.toString(stockPrices));
        System.out.printf("Lowest:  $%d (day %d)%n", minPrice, minIndex);
        System.out.printf("Highest: $%d (day %d)%n", maxPrice, maxIndex);
        System.out.printf("Range:   $%d%n", maxPrice - minPrice);

        // =========================================================
        // PATTERN 5: Flag pattern
        // =========================================================
        // Purpose: Track whether a condition was EVER true during a loop
        // Template:
        //   boolean flag = false;
        //   for each item:
        //       if condition: flag = true; (optionally break)
        //   // after loop: use flag to branch

        System.out.println("\n=== PATTERN 5: Flag Pattern ===");

        int[] transactionAmounts = {250, 175, 320, 480, 95, 1200, 340, 180};

        boolean hasLargeTransaction = false;  // ← the flag, starts false

        for (int amount : transactionAmounts) {
            if (amount > 1000) {
                hasLargeTransaction = true;   // ← set the flag
                break;                        // found one — no need to continue
            }
        }

        if (hasLargeTransaction) {
            System.out.println("⚠ Alert: Large transaction detected — review required");
        } else {
            System.out.println("✓ All transactions within normal range");
        }

        // Flag with multiple conditions
        String[] userInputs = {"  hello  ", "", "world", "   ", "java"};

        boolean hasBlankInput = false;
        boolean hasNullEquivalent = false;

        for (String input : userInputs) {
            if (input.isBlank()) {
                hasBlankInput = true;
            }
            if (input == null || input.trim().isEmpty()) {
                hasNullEquivalent = true;
            }
        }

        System.out.println("Has blank input:       " + hasBlankInput);
        System.out.println("Has null/empty input:  " + hasNullEquivalent);

        // =========================================================
        // PATTERN 6: Combining patterns — real-world example
        // =========================================================
        // Calculate class statistics: average, highest, lowest, passing count, grade distribution

        System.out.println("\n=== PATTERN 6: Combined Patterns (Class Statistics) ===");

        int[] classScores = {82, 67, 94, 55, 78, 88, 72, 91, 60, 85, 49, 76, 93, 63, 70};

        // Accumulator + Min/Max + Counter — all in one pass
        int sum = 0;
        int highest = classScores[0];
        int lowest = classScores[0];
        int passingCount = 0;
        int countA = 0, countB = 0, countC = 0, countD = 0, countF = 0;

        for (int score : classScores) {
            // Accumulator
            sum += score;

            // Min/Max
            if (score > highest) highest = score;
            if (score < lowest) lowest = score;

            // Counter: passing
            if (score >= 60) passingCount++;

            // Counter: grade distribution
            if      (score >= 90) countA++;
            else if (score >= 80) countB++;
            else if (score >= 70) countC++;
            else if (score >= 60) countD++;
            else                  countF++;
        }

        double average = (double) sum / classScores.length;
        boolean hasFailing = (classScores.length - passingCount) > 0;  // flag pattern

        System.out.println("Class size:   " + classScores.length);
        System.out.printf( "Average:      %.1f%n", average);
        System.out.println("Highest:      " + highest);
        System.out.println("Lowest:       " + lowest);
        System.out.println("Passing:      " + passingCount + "/" + classScores.length);
        System.out.println("Grade dist:   A=" + countA + " B=" + countB +
                           " C=" + countC + " D=" + countD + " F=" + countF);

        if (hasFailing) {
            System.out.println("⚠ Some students are failing — consider additional support.");
        }

        // =========================================================
        // PATTERN 7: Two-pointer / Index tracking
        // =========================================================
        // Used to fill an array based on a condition — very common pattern

        System.out.println("\n=== PATTERN 7: Index Tracking (Filter into New Array) ===");

        int[] rawData = {7, -3, 15, -8, 22, 0, -1, 9, 18, -5};
        int positiveCount = 0;

        // First pass: count how many positives (so we can size the new array)
        for (int val : rawData) {
            if (val > 0) positiveCount++;
        }

        // Second pass: fill new array
        int[] positives = new int[positiveCount];
        int writeIndex = 0;   // ← the "tracking index" into the destination array

        for (int val : rawData) {
            if (val > 0) {
                positives[writeIndex] = val;
                writeIndex++;
            }
        }

        System.out.println("Raw data:  " + Arrays.toString(rawData));
        System.out.println("Positives: " + Arrays.toString(positives));
    }
}
