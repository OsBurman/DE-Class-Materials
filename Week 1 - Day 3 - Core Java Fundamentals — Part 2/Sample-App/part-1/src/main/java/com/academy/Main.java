package com.academy;

/**
 * Day 3 Part 1 — Control Flow: if/else, switch, loops, break, continue
 *
 * Theme: A simple Student Grade Evaluator
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║  Day 3 Part 1 — Control Flow Demo             ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        demoIfElse();
        demoSwitch();
        demoLoops();
        demoBreakContinue();
    }

    static void demoIfElse() {
        System.out.println("=== 1. if / else if / else ===");

        int[] scores = {95, 82, 73, 61, 45};

        for (int score : scores) {
            String grade;
            String feedback;

            if (score >= 90) {
                grade = "A"; feedback = "Excellent!";
            } else if (score >= 80) {
                grade = "B"; feedback = "Great work.";
            } else if (score >= 70) {
                grade = "C"; feedback = "Satisfactory.";
            } else if (score >= 60) {
                grade = "D"; feedback = "Needs improvement.";
            } else {
                grade = "F"; feedback = "Please retake.";
            }

            System.out.printf("  Score: %3d → Grade: %s (%s)%n", score, grade, feedback);
        }
        System.out.println();
    }

    static void demoSwitch() {
        System.out.println("=== 2. switch Statement ===");

        String[] days = {"MONDAY", "WEDNESDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

        for (String day : days) {
            String activity;
            switch (day) {
                case "MONDAY":
                case "TUESDAY":
                case "WEDNESDAY":
                case "THURSDAY":
                case "FRIDAY":
                    activity = "Class day — attend lecture & do exercises";
                    break;
                case "SATURDAY":
                    activity = "Review day — study and practice";
                    break;
                case "SUNDAY":
                    activity = "Rest day — recharge for the week ahead";
                    break;
                default:
                    activity = "Unknown day";
            }
            System.out.println("  " + day + ": " + activity);
        }

        // Java 14+ Switch Expression
        System.out.println("\n  Modern switch expression (Java 14+):");
        int month = 4;
        int daysInMonth = switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11            -> 30;
            case 2                      -> 28;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
        System.out.println("  Month " + month + " has " + daysInMonth + " days.");
        System.out.println();
    }

    static void demoLoops() {
        System.out.println("=== 3. Loops (for, while, do-while, enhanced for) ===");

        // Standard for loop
        System.out.print("  for loop (1..5):   ");
        for (int i = 1; i <= 5; i++) System.out.print(i + " ");
        System.out.println();

        // while loop — count down
        System.out.print("  while loop (5..1): ");
        int count = 5;
        while (count >= 1) { System.out.print(count + " "); count--; }
        System.out.println();

        // do-while — always executes at least once
        System.out.print("  do-while:          ");
        int n = 1;
        do { System.out.print(n + " "); n *= 2; } while (n <= 16);
        System.out.println("(powers of 2)");

        // Enhanced for loop
        String[] students = {"Alice", "Bob", "Carol", "Dave"};
        System.out.print("  enhanced for:      ");
        for (String s : students) System.out.print(s + " ");
        System.out.println();

        // Nested loops — multiplication table
        System.out.println("\n  Nested loops (3×3 multiplication table):");
        for (int i = 1; i <= 3; i++) {
            System.out.print("  ");
            for (int j = 1; j <= 3; j++) {
                System.out.printf("%3d", i * j);
            }
            System.out.println();
        }
        System.out.println();
    }

    static void demoBreakContinue() {
        System.out.println("=== 4. break and continue ===");

        // break — stop when first failing student found
        System.out.println("  break: find first failing score:");
        int[] scores = {88, 75, 92, 55, 70, 43};
        for (int score : scores) {
            if (score < 60) {
                System.out.println("  → First failing score: " + score + " (stopping search)");
                break;
            }
            System.out.println("    Passing score: " + score);
        }

        // continue — skip absent students
        System.out.println("\n  continue: skip absent students (-1 = absent):");
        int[] attendance = {1, -1, 1, 1, -1, 1};
        int presentCount = 0;
        for (int status : attendance) {
            if (status == -1) { System.out.println("    Student absent — skipping"); continue; }
            presentCount++;
            System.out.println("    Student " + presentCount + " marked present");
        }
        System.out.println("  Total present: " + presentCount);

        System.out.println("\n✓ Control Flow demo complete.");
    }
}
