package com.academy;

/**
 * Day 7 Part 1 — Exception Handling: try/catch/finally, Custom Exceptions
 *
 * Theme: Safe Student Grade Calculator
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Day 7 Part 1 — Exception Handling Demo              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        demoBasicExceptions();
        demoCustomExceptions();
        demoFinallyBlock();
        demoMultiCatch();
        demoExceptionHierarchy();
    }

    static void demoBasicExceptions() {
        System.out.println("=== 1. Basic try-catch ===");

        // ArithmeticException
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("  Caught ArithmeticException: " + e.getMessage());
        }

        // ArrayIndexOutOfBoundsException
        try {
            int[] arr = {1, 2, 3};
            int bad = arr[10];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  Caught ArrayIndexOutOfBounds: " + e.getMessage());
        }

        // NullPointerException
        try {
            String s = null;
            int len = s.length();
        } catch (NullPointerException e) {
            System.out.println("  Caught NullPointerException: calling method on null reference");
        }

        // NumberFormatException
        try {
            int num = Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            System.out.println("  Caught NumberFormatException: " + e.getMessage());
        }
        System.out.println();
    }

    static void demoCustomExceptions() {
        System.out.println("=== 2. Custom Exceptions ===");

        GradeBook book = new GradeBook();
        book.addStudent("Alice", 92);
        book.addStudent("Bob",   78);

        // Test our custom exception
        try {
            book.addStudent("Carol", 105);  // invalid grade > 100
        } catch (InvalidGradeException e) {
            System.out.println("  Caught InvalidGradeException: " + e.getMessage());
        }

        try {
            book.getGrade("Unknown");       // student not found
        } catch (StudentNotFoundException e) {
            System.out.println("  Caught StudentNotFoundException: " + e.getMessage());
            System.out.println("  Student: " + e.getStudentName());
        }

        try {
            double avg = book.getClassAverage();
            System.out.printf("  Class average: %.1f%n", avg);
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
        System.out.println();
    }

    static void demoFinallyBlock() {
        System.out.println("=== 3. finally block (always executes) ===");

        for (boolean throwEx : new boolean[]{false, true}) {
            System.out.println("  Test (throw=" + throwEx + "):");
            try {
                System.out.println("    → try block executing");
                if (throwEx) throw new RuntimeException("Test exception");
                System.out.println("    → try block succeeded");
            } catch (RuntimeException e) {
                System.out.println("    → catch block: " + e.getMessage());
            } finally {
                System.out.println("    → finally always runs (e.g. close DB connection)");
            }
        }
        System.out.println();
    }

    static void demoMultiCatch() {
        System.out.println("=== 4. Multi-catch (Java 7+) ===");

        String[] inputs = {"42", "abc", null, "-5"};
        for (String input : inputs) {
            try {
                if (input == null) throw new NullPointerException("null input");
                int value = Integer.parseInt(input);
                if (value < 0) throw new InvalidGradeException("negative score: " + value);
                System.out.println("  Valid input: " + value);
            } catch (NumberFormatException | NullPointerException e) {
                System.out.println("  Input error for \"" + input + "\": " + e.getMessage());
            } catch (InvalidGradeException e) {
                System.out.println("  Grade error: " + e.getMessage());
            }
        }
        System.out.println();
    }

    static void demoExceptionHierarchy() {
        System.out.println("=== 5. Exception Hierarchy ===");
        System.out.println("  Throwable");
        System.out.println("  ├── Error (don't catch — JVM errors)");
        System.out.println("  │   ├── StackOverflowError");
        System.out.println("  │   └── OutOfMemoryError");
        System.out.println("  └── Exception");
        System.out.println("      ├── RuntimeException (unchecked — no need to declare)");
        System.out.println("      │   ├── NullPointerException");
        System.out.println("      │   ├── ArrayIndexOutOfBoundsException");
        System.out.println("      │   └── IllegalArgumentException");
        System.out.println("      └── Checked Exception (must try/catch or throws)");
        System.out.println("          ├── IOException");
        System.out.println("          └── SQLException");
        System.out.println("\n✓ Exception Handling demo complete.");
    }
}

// Custom checked exception
class StudentNotFoundException extends Exception {
    private final String studentName;
    public StudentNotFoundException(String name) {
        super("Student not found: " + name);
        this.studentName = name;
    }
    public String getStudentName() { return studentName; }
}

// Custom unchecked exception
class InvalidGradeException extends RuntimeException {
    public InvalidGradeException(String msg) { super("Invalid grade — " + msg); }
}

class GradeBook {
    private final java.util.Map<String, Integer> grades = new java.util.HashMap<>();

    public void addStudent(String name, int grade) {
        if (grade < 0 || grade > 100) throw new InvalidGradeException("must be 0-100, got " + grade);
        grades.put(name, grade);
        System.out.println("  Added: " + name + " → " + grade);
    }

    public int getGrade(String name) throws StudentNotFoundException {
        if (!grades.containsKey(name)) throw new StudentNotFoundException(name);
        return grades.get(name);
    }

    public double getClassAverage() {
        return grades.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
