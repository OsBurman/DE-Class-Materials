package com.academy;

import java.util.*;

/**
 * Day 6 Part 2 — Generics, Comparable, Comparator, Collections utility methods
 *
 * Theme: Generic Grade Book
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Day 6 Part 2 — Generics & Sorting Demo              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        demoGenerics();
        demoComparable();
        demoComparator();
        demoCollectionsUtility();
    }

    static void demoGenerics() {
        System.out.println("=== 1. Generic Classes & Methods ===");

        // Generic Pair<A,B>
        Pair<String, Integer> grade = new Pair<>("Alice", 92);
        Pair<String, String>  role  = new Pair<>("Bob",   "Admin");
        System.out.println("  Grade pair:   " + grade);
        System.out.println("  Role pair:    " + role);

        // Generic stack
        Stack<String> stack = new Stack<>();
        stack.push("Level 1"); stack.push("Level 2"); stack.push("Level 3");
        System.out.println("  Stack top: " + stack.peek());
        System.out.println("  Popped:    " + stack.pop());
        System.out.println("  Stack now: " + stack);

        // Generic method — find max in any Comparable list
        List<Integer> numbers = Arrays.asList(3, 7, 1, 9, 4);
        List<String>  names   = Arrays.asList("Charlie", "Alice", "Bob");
        System.out.println("  Max number: " + findMax(numbers));
        System.out.println("  Max name:   " + findMax(names));
        System.out.println();
    }

    // Generic method — T must extend Comparable<T>
    static <T extends Comparable<T>> T findMax(List<T> list) {
        T max = list.get(0);
        for (T item : list) if (item.compareTo(max) > 0) max = item;
        return max;
    }

    static void demoComparable() {
        System.out.println("=== 2. Comparable — Natural Ordering ===");

        List<Student> students = Arrays.asList(
            new Student("Charlie", 78),
            new Student("Alice",   95),
            new Student("Bob",     85),
            new Student("Dave",    78)
        );

        System.out.println("  Before sort: " + students);
        Collections.sort(students);  // uses Student.compareTo()
        System.out.println("  After sort (by GPA desc, then name): " + students);
        System.out.println();
    }

    static void demoComparator() {
        System.out.println("=== 3. Comparator — Custom Ordering ===");

        List<Student> students = Arrays.asList(
            new Student("Charlie", 78),
            new Student("Alice",   95),
            new Student("Bob",     85),
            new Student("Dave",    78)
        );

        // Sort by name alphabetically
        students.sort(Comparator.comparing(Student::getName));
        System.out.println("  By name (A→Z):      " + students);

        // Sort by grade descending, then name ascending
        students.sort(Comparator.comparingInt(Student::getGrade).reversed()
                                .thenComparing(Student::getName));
        System.out.println("  By grade desc+name: " + students);

        // Sort by name length
        students.sort(Comparator.comparingInt(s -> s.getName().length()));
        System.out.println("  By name length:     " + students);
        System.out.println();
    }

    static void demoCollectionsUtility() {
        System.out.println("=== 4. Collections Utility Methods ===");

        List<Integer> nums = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3));
        System.out.println("  Original: " + nums);

        Collections.sort(nums);
        System.out.println("  sort():   " + nums);

        Collections.reverse(nums);
        System.out.println("  reverse(): " + nums);

        Collections.shuffle(nums, new Random(42));
        System.out.println("  shuffle(): " + nums);

        System.out.println("  min():    " + Collections.min(nums));
        System.out.println("  max():    " + Collections.max(nums));
        System.out.println("  freq(3):  " + Collections.frequency(nums, 3));

        Collections.sort(nums);
        System.out.println("  binarySearch(8): index " + Collections.binarySearch(nums, 8));

        // Unmodifiable & synchronized wrappers
        List<String> mutable   = new ArrayList<>(Arrays.asList("A", "B", "C"));
        List<String> immutable = Collections.unmodifiableList(mutable);
        System.out.println("  unmodifiableList: " + immutable);
        try {
            immutable.add("D"); // throws UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("  ✗ Cannot modify unmodifiableList — UnsupportedOperationException");
        }

        System.out.println("\n✓ Generics & Sorting demo complete.");
    }
}

// ─────────────────────────────────────────────────────────────
// Generic Pair class
// ─────────────────────────────────────────────────────────────
class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) { this.first = first; this.second = second; }
    public A getFirst()  { return first; }
    public B getSecond() { return second; }
    @Override public String toString() { return "(" + first + ", " + second + ")"; }
}

// ─────────────────────────────────────────────────────────────
// Student implements Comparable — defines natural ordering
// ─────────────────────────────────────────────────────────────
class Student implements Comparable<Student> {
    private String name;
    private int    grade;

    public Student(String name, int grade) {
        this.name = name; this.grade = grade;
    }

    @Override
    public int compareTo(Student other) {
        // Primary: grade descending; Secondary: name ascending
        if (this.grade != other.grade) return other.grade - this.grade;
        return this.name.compareTo(other.name);
    }

    public String getName()  { return name; }
    public int    getGrade() { return grade; }

    @Override public String toString() { return name + "(" + grade + ")"; }
}
