import java.util.*;

/**
 * Exercise 05 — Collections & Generics (SOLUTION)
 */
public class Main {

    static class Student implements Comparable<Student> {
        int id;
        String name;
        String major;

        Student(int id, String name, String major) {
            this.id = id;
            this.name = name;
            this.major = major;
        }

        @Override
        public int compareTo(Student o) {
            return this.name.compareTo(o.name);
        }

        @Override
        public String toString() {
            return String.format("Student[%d] %s (%s)", id, name, major);
        }
    }

    static class Pair<K, V> {
        private K key;
        private V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K getKey() {
            return key;
        }

        V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "(" + key + ", " + value + ")";
        }
    }

    static List<Student> buildStudents() {
        return new ArrayList<>(List.of(
                new Student(1, "Zara", "CS"), new Student(2, "Alice", "Math"),
                new Student(3, "Bob", "CS"), new Student(4, "Diana", "Physics"),
                new Student(5, "Eve", "Math"), new Student(6, "Charlie", "CS")));
    }

    static Map<Integer, Map<String, Integer>> buildGrades() {
        Map<Integer, Map<String, Integer>> g = new HashMap<>();
        g.put(1, new HashMap<>(Map.of("Algorithms", 92, "Calculus", 88, "Physics", 79)));
        g.put(2, new HashMap<>(Map.of("Algorithms", 78, "Calculus", 95, "Physics", 85)));
        g.put(3, new HashMap<>(Map.of("Algorithms", 88, "Calculus", 72, "Physics", 91)));
        g.put(4, new HashMap<>(Map.of("Algorithms", 65, "Calculus", 80, "Physics", 97)));
        g.put(5, new HashMap<>(Map.of("Algorithms", 91, "Calculus", 89, "Physics", 84)));
        g.put(6, new HashMap<>(Map.of("Algorithms", 75, "Calculus", 68, "Physics", 73)));
        return g;
    }

    static double getAverageGrade(Map<String, Integer> courses) {
        if (courses == null || courses.isEmpty())
            return 0.0;
        int sum = 0;
        for (int v : courses.values())
            sum += v;
        return (double) sum / courses.size();
    }

    static List<Student> getStudentsByMajor(List<Student> students, String major) {
        List<Student> result = new ArrayList<>();
        for (Student s : students)
            if (s.major.equalsIgnoreCase(major))
                result.add(s);
        return result;
    }

    static Map<String, String> topStudentPerCourse(List<Student> students, Map<Integer, Map<String, Integer>> grades) {
        Map<String, String> result = new HashMap<>();
        Map<String, Integer> best = new HashMap<>();
        for (Student s : students) {
            Map<String, Integer> sg = grades.get(s.id);
            if (sg == null)
                continue;
            for (Map.Entry<String, Integer> e : sg.entrySet()) {
                if (!best.containsKey(e.getKey()) || e.getValue() > best.get(e.getKey())) {
                    best.put(e.getKey(), e.getValue());
                    result.put(e.getKey(), s.name);
                }
            }
        }
        return result;
    }

    static Set<String> getUniqueMajors(List<Student> students) {
        Set<String> majors = new HashSet<>();
        for (Student s : students)
            majors.add(s.major);
        return majors;
    }

    static List<Pair<Student, Double>> rankStudents(List<Student> students, Map<Integer, Map<String, Integer>> grades) {
        List<Pair<Student, Double>> list = new ArrayList<>();
        for (Student s : students) {
            double avg = getAverageGrade(grades.getOrDefault(s.id, new HashMap<>()));
            list.add(new Pair<>(s, avg));
        }
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return list;
    }

    public static void main(String[] args) {
        List<Student> students = buildStudents();
        Map<Integer, Map<String, Integer>> grades = buildGrades();

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
        int rank = 1;
        for (Pair<Student, Double> p : rankStudents(students, grades)) {
            System.out.printf("  #%d  %-10s  avg=%.1f%n", rank++, p.getKey().name, p.getValue());
        }

        // TODO 8 solution
        System.out.println("\n=== Students Sorted by Major ===");
        students.sort(Comparator.comparing(s -> s.major));
        students.forEach(System.out::println);
    }
}
