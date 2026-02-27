import java.util.*;

/**
 * Exercise 05 — Collections & Generics  (STARTER)
 * Student Gradebook — manage students, courses, and grades using
 * the Java Collections Framework.
 *
 * Fill in each TODO. The file compiles as-is.
 */
public class Main {

    // ── Student record ─────────────────────────────────────────────
    static class Student implements Comparable<Student> {
        int    id;
        String name;
        String major;

        Student(int id, String name, String major) {
            this.id = id; this.name = name; this.major = major;
        }

        // TODO 1: Implement compareTo so students sort alphabetically by name
        @Override
        public int compareTo(Student other) {
            return 0; // your code here
        }

        @Override public String toString() {
            return String.format("Student[%d] %s (%s)", id, name, major);
        }
    }

    // ── TODO 2: Generic Pair<K, V> class ───────────────────────────
    //   Fields:  K key;  V value;
    //   Constructor: Pair(K key, V value)
    //   Getters:     getKey(), getValue()
    //   toString():  "(key, value)"
    static class Pair<K, V> {
        // your code here
    }

    // ── Helper: build test data ─────────────────────────────────────
    static List<Student> buildStudents() {
        return new ArrayList<>(List.of(
            new Student(1, "Zara",   "CS"),
            new Student(2, "Alice",  "Math"),
            new Student(3, "Bob",    "CS"),
            new Student(4, "Diana",  "Physics"),
            new Student(5, "Eve",    "Math"),
            new Student(6, "Charlie","CS")
        ));
    }

    // grades: Map<studentId, Map<course, score>>
    static Map<Integer, Map<String, Integer>> buildGrades() {
        Map<Integer, Map<String, Integer>> g = new HashMap<>();
        g.put(1, new HashMap<>(Map.of("Algorithms",92,"Calculus",88,"Physics",79)));
        g.put(2, new HashMap<>(Map.of("Algorithms",78,"Calculus",95,"Physics",85)));
        g.put(3, new HashMap<>(Map.of("Algorithms",88,"Calculus",72,"Physics",91)));
        g.put(4, new HashMap<>(Map.of("Algorithms",65,"Calculus",80,"Physics",97)));
        g.put(5, new HashMap<>(Map.of("Algorithms",91,"Calculus",89,"Physics",84)));
        g.put(6, new HashMap<>(Map.of("Algorithms",75,"Calculus",68,"Physics",73)));
        return g;
    }

    // ── TODO 3: getAverageGrade(Map<String,Integer> courses) ───────
    //   Return the average of all values in the map.
    static double getAverageGrade(Map<String, Integer> courses) {
        return 0.0; // your code here
    }

    // ── TODO 4: getStudentsByMajor(List<Student>, String major) ────
    //   Return a new List containing only students in the given major.
    static List<Student> getStudentsByMajor(List<Student> students, String major) {
        return new ArrayList<>(); // your code here
    }

    // ── TODO 5: topStudentPerCourse(…) ─────────────────────────────
    //   Return a Map<String courseName, String studentName> where each
    //   entry is the student with the highest score in that course.
    static Map<String, String> topStudentPerCourse(
            List<Student> students,
            Map<Integer, Map<String, Integer>> grades) {
        return new HashMap<>(); // your code here
    }

    // ── TODO 6: getUniqueMajors(List<Student>) ─────────────────────
    //   Return a Set<String> of all distinct majors.
    static Set<String> getUniqueMajors(List<Student> students) {
        return new HashSet<>(); // your code here
    }

    // ── TODO 7: rankStudents(…) ────────────────────────────────────
    //   Return a List<Pair<Student, Double>> sorted by overall average
    //   descending (highest average first).
    //   Each Pair: (student, averageGrade)
    static List<Pair<Student, Double>> rankStudents(
            List<Student> students,
            Map<Integer, Map<String, Integer>> grades) {
        return new ArrayList<>(); // your code here
    }

    // ── Main ────────────────────────────────────────────────────────
    public static void main(String[] args) {
        List<Student> students = buildStudents();
        Map<Integer, Map<String, Integer>> grades = buildGrades();

        // Sort alphabetically (uses Comparable)
        Collections.sort(students);
        System.out.println("=== Students (alphabetical) ===");
        students.forEach(System.out::println);

        System.out.println("\n=== CS Students ===");
        getStudentsByMajor(students, "CS").forEach(System.out::println);

        System.out.println("\n=== Unique Majors ===");
        getUniqueMajors(students).forEach(System.out::println);

        System.out.println("\n=== Top Student Per Course ===");
        topStudentPerCourse(students, grades)
            .forEach((course, name) -> System.out.printf("  %-12s → %s%n", course, name));

        System.out.println("\n=== Student Rankings ===");
        List<Pair<Student, Double>> ranking = rankStudents(students, grades);
        int rank = 1;
        for (Pair<Student, Double> p : ranking) {
            System.out.printf("  #%d  %-10s  avg=%.1f%n", rank++, p.getKey().name, p.getValue());
        }

        // Sort by major using a Comparator (lambda)
        // TODO 8: Sort the students list by major name using a lambda Comparator,
        //         then print the sorted list.
        System.out.println("\n=== Students Sorted by Major ===");
        // your code here (one line: students.sort(...))
        students.forEach(System.out::println);
    }
}
