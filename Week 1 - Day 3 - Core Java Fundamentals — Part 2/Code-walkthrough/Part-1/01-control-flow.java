/**
 * 01-control-flow.java
 * Day 3 — Core Java Fundamentals Part 2
 *
 * Topics covered:
 *   - if statements (simple, if-else, if-else-if chain)
 *   - switch statement (traditional)
 *   - switch expression (Java 14+ arrow syntax)
 *   - Nested conditionals
 *   - Ternary operator (quick review in context)
 */
public class ControlFlow {

    public static void main(String[] args) {

        // =========================================================
        // SECTION 1: Simple if statement
        // =========================================================

        int temperature = 28;

        if (temperature > 25) {
            System.out.println("It's a hot day — stay hydrated!");
        }

        // =========================================================
        // SECTION 2: if-else statement
        // =========================================================

        int studentScore = 55;

        if (studentScore >= 60) {
            System.out.println("Result: PASS");
        } else {
            System.out.println("Result: FAIL");
        }

        // =========================================================
        // SECTION 3: if-else-if chain
        // =========================================================

        // Grading system: A B C D F
        int examScore = 78;

        if (examScore >= 90) {
            System.out.println("Grade: A");
        } else if (examScore >= 80) {
            System.out.println("Grade: B");
        } else if (examScore >= 70) {
            System.out.println("Grade: C");
        } else if (examScore >= 60) {
            System.out.println("Grade: D");
        } else {
            System.out.println("Grade: F");
        }
        // Output: Grade: C  (78 is >= 70 but not >= 80)

        // =========================================================
        // SECTION 4: Nested if statements
        // =========================================================

        boolean isLoggedIn = true;
        boolean isAdmin = false;

        if (isLoggedIn) {
            System.out.println("Welcome back!");
            if (isAdmin) {
                System.out.println("Admin panel: accessible");
            } else {
                System.out.println("Admin panel: restricted");
            }
        } else {
            System.out.println("Please log in to continue.");
        }

        // =========================================================
        // SECTION 5: Ternary operator (compact if-else)
        // =========================================================

        // Syntax: condition ? valueIfTrue : valueIfFalse
        int age = 20;
        String accessLevel = (age >= 18) ? "Adult access" : "Minor access";
        System.out.println(accessLevel);  // Adult access

        // =========================================================
        // SECTION 6: Traditional switch statement
        // =========================================================

        // switch evaluates a variable against specific case VALUES
        // Each case needs a break to prevent "fall-through"
        String dayOfWeek = "Wednesday";

        switch (dayOfWeek) {
            case "Monday":
                System.out.println("Start of the work week — coffee time!");
                break;
            case "Wednesday":
                System.out.println("Hump day — halfway there!");
                break;
            case "Friday":
                System.out.println("TGIF — great work this week!");
                break;
            case "Saturday":
            case "Sunday":
                // Two cases sharing one action — intentional fall-through
                System.out.println("Weekend — recharge and relax!");
                break;
            default:
                System.out.println("Just another weekday, keep going!");
        }

        // =========================================================
        // SECTION 7: Fall-through demonstration (intentional vs bug)
        // =========================================================

        int priority = 2;
        System.out.println("Tasks for priority level " + priority + " and below:");

        switch (priority) {
            case 1:
                System.out.println("  - Deploy to production");
                // FALL-THROUGH intentional: priority 1 also does priority 2 tasks
            case 2:
                System.out.println("  - Code review");
                // FALL-THROUGH intentional
            case 3:
                System.out.println("  - Update documentation");
                break;
            default:
                System.out.println("  - No specific tasks");
        }
        // Output: Code review + Update documentation  (starts at case 2, falls through to 3)

        // =========================================================
        // SECTION 8: switch expression (Java 14+ — arrow syntax)
        // =========================================================

        // The arrow (->) syntax:
        //  - No fall-through by default
        //  - No break needed
        //  - Can return a value directly
        String season = "Summer";

        String activity = switch (season) {
            case "Spring"  -> "Go hiking";
            case "Summer"  -> "Hit the beach";
            case "Autumn"  -> "Visit a pumpkin patch";
            case "Winter"  -> "Build a snowman";
            default        -> "Stay indoors";
        };

        System.out.println("Suggested activity: " + activity);  // Hit the beach

        // =========================================================
        // SECTION 9: switch expression with multiple labels (Java 14+)
        // =========================================================

        int month = 4;  // April

        int daysInMonth = switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11            -> 30;
            case 2                      -> 28;  // ignoring leap year for simplicity
            default                     -> throw new IllegalArgumentException("Invalid month: " + month);
        };

        System.out.println("Days in month " + month + ": " + daysInMonth);  // 30

        // =========================================================
        // SECTION 10: switch on String (traditional, available since Java 7)
        // =========================================================

        String userRole = "editor";

        switch (userRole) {
            case "admin":
                System.out.println("Full access granted");
                break;
            case "editor":
                System.out.println("Edit access granted");
                break;
            case "viewer":
                System.out.println("Read-only access granted");
                break;
            default:
                System.out.println("Unknown role — access denied");
        }
    }
}
