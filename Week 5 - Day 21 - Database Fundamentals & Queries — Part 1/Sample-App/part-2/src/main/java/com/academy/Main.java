package com.academy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Day 21 — Part 2: SQL Advanced Queries
 * =======================================
 * Topics covered:
 *   ✓ Aggregate functions — COUNT, SUM, AVG, MIN, MAX
 *   ✓ GROUP BY clause
 *   ✓ HAVING — filtering on aggregate results
 *   ✓ Table relationships and foreign keys
 *   ✓ INNER JOIN — matching rows in both tables
 *   ✓ LEFT JOIN — all rows from left, matched from right
 *   ✓ RIGHT JOIN — all rows from right, matched from left
 *   ✓ FULL OUTER JOIN — all rows from both tables
 *   ✓ Multiple JOINs in a single query
 *   ✓ Single-row and multi-row subqueries
 *   ✓ Correlated subqueries
 *   ✓ EXISTS and NOT EXISTS
 *   ✓ CASE statements (simple and searched)
 *   ✓ Calculated columns and expressions
 *
 * Database : H2 in-memory (University schema — 4 tables)
 * Run      : mvn compile exec:java
 */
public class Main {

    private static final String URL  = "jdbc:h2:mem:university;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   Day 21 · Part 2 — SQL Advanced Queries                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            setupDatabase(conn);

            // ── 1. Aggregate Functions ─────────────────────────────────────
            section("1 · Aggregate Functions — COUNT, SUM, AVG, MIN, MAX");
            query(conn, "SELECT COUNT(*) AS total_students FROM students");
            query(conn, "SELECT COUNT(DISTINCT major) AS distinct_majors FROM students");
            query(conn, "SELECT AVG(gpa) AS avg_gpa, MIN(gpa) AS min_gpa, MAX(gpa) AS max_gpa FROM students");
            query(conn, "SELECT SUM(credits) AS total_credits FROM courses");

            // ── 2. GROUP BY ────────────────────────────────────────────────
            section("2 · GROUP BY — aggregate per group");
            query(conn, """
                SELECT major,
                       COUNT(*)      AS student_count,
                       ROUND(AVG(gpa), 2) AS avg_gpa,
                       MAX(gpa)      AS top_gpa
                FROM   students
                GROUP  BY major
                ORDER  BY student_count DESC
                """);

            query(conn, """
                SELECT d.name AS department, COUNT(c.id) AS course_count
                FROM   departments d
                JOIN   courses c ON c.department_id = d.id
                GROUP  BY d.name
                """);

            // ── 3. HAVING ──────────────────────────────────────────────────
            section("3 · HAVING — filter on aggregated results (WHERE is for rows, HAVING is for groups)");
            query(conn, """
                SELECT major, COUNT(*) AS cnt, ROUND(AVG(gpa), 2) AS avg_gpa
                FROM   students
                GROUP  BY major
                HAVING COUNT(*) >= 2
                """);

            query(conn, """
                SELECT major, ROUND(AVG(gpa), 2) AS avg_gpa
                FROM   students
                GROUP  BY major
                HAVING AVG(gpa) > 3.3
                ORDER  BY avg_gpa DESC
                """);

            // ── 4. INNER JOIN ──────────────────────────────────────────────
            section("4 · INNER JOIN — only rows that match in BOTH tables");
            query(conn, """
                SELECT s.first_name, s.last_name, c.title, e.grade
                FROM   enrollments e
                INNER JOIN students s ON s.id = e.student_id
                INNER JOIN courses  c ON c.id = e.course_id
                ORDER  BY s.last_name
                """);

            // ── 5. LEFT JOIN ───────────────────────────────────────────────
            section("5 · LEFT JOIN — all rows from LEFT table, NULLs where no match on RIGHT");
            query(conn, """
                SELECT s.first_name, s.major, c.title AS enrolled_in
                FROM   students s
                LEFT JOIN enrollments e ON e.student_id = s.id
                LEFT JOIN courses     c ON c.id = e.course_id
                WHERE  e.id IS NULL  -- students with NO enrollments
                """);

            query(conn, """
                SELECT c.title, COUNT(e.student_id) AS enrolled_count
                FROM   courses c
                LEFT JOIN enrollments e ON e.course_id = c.id
                GROUP  BY c.title
                ORDER  BY enrolled_count DESC
                """);

            // ── 6. FULL OUTER JOIN ─────────────────────────────────────────
            section("6 · FULL OUTER JOIN — all rows from both tables, NULLs where no match");
            query(conn, """
                SELECT s.first_name, c.title
                FROM   students s
                FULL OUTER JOIN enrollments e ON e.student_id = s.id
                FULL OUTER JOIN courses     c ON c.id = e.course_id
                ORDER  BY s.first_name NULLS LAST
                LIMIT 10
                """);

            // ── 7. Multiple JOINs ──────────────────────────────────────────
            section("7 · Multiple JOINs — students + courses + departments + instructors");
            query(conn, """
                SELECT
                    s.first_name || ' ' || s.last_name  AS student,
                    c.title                              AS course,
                    d.name                               AS department,
                    i.name                               AS instructor,
                    e.grade
                FROM  enrollments e
                JOIN  students    s ON s.id = e.student_id
                JOIN  courses     c ON c.id = e.course_id
                JOIN  departments d ON d.id = c.department_id
                JOIN  instructors i ON i.id = c.instructor_id
                ORDER BY student, course
                """);

            // ── 8. Subqueries — Single-Row ─────────────────────────────────
            section("8 · Single-Row Subquery — value returned by inner query used in outer WHERE");
            query(conn, """
                SELECT first_name, gpa
                FROM   students
                WHERE  gpa > (SELECT AVG(gpa) FROM students)
                ORDER  BY gpa DESC
                """);

            query(conn, """
                SELECT first_name, gpa
                FROM   students
                WHERE  gpa = (SELECT MAX(gpa) FROM students)
                """);

            // ── 9. Subqueries — Multi-Row ──────────────────────────────────
            section("9 · Multi-Row Subquery — inner query returns a list used with IN");
            query(conn, """
                SELECT first_name, major
                FROM   students
                WHERE  id IN (
                    SELECT student_id
                    FROM   enrollments e
                    JOIN   courses c ON c.id = e.course_id
                    WHERE  c.course_code = 'CS201'
                )
                """);

            // ── 10. Correlated Subquery ────────────────────────────────────
            section("10 · Correlated Subquery — inner query references outer query row by row");
            query(conn, """
                SELECT first_name, major, gpa
                FROM   students s
                WHERE  gpa > (
                    SELECT AVG(gpa)
                    FROM   students s2
                    WHERE  s2.major = s.major   -- correlated: same major as outer row
                )
                ORDER BY major, gpa DESC
                """);

            // ── 11. EXISTS and NOT EXISTS ──────────────────────────────────
            section("11 · EXISTS and NOT EXISTS — check whether subquery returns any row");
            query(conn, """
                SELECT first_name
                FROM   students s
                WHERE  EXISTS (
                    SELECT 1
                    FROM   enrollments e
                    WHERE  e.student_id = s.id
                )
                """);

            query(conn, """
                SELECT first_name
                FROM   students s
                WHERE  NOT EXISTS (
                    SELECT 1
                    FROM   enrollments e
                    WHERE  e.student_id = s.id
                )
                """);

            // ── 12. CASE Statements ────────────────────────────────────────
            section("12 · CASE — conditional logic inside a SELECT");
            query(conn, """
                SELECT first_name, gpa,
                    CASE
                        WHEN gpa >= 3.7 THEN 'A — Distinction'
                        WHEN gpa >= 3.3 THEN 'B — Good Standing'
                        WHEN gpa >= 3.0 THEN 'C — Satisfactory'
                        ELSE                 'D — Needs Improvement'
                    END AS standing
                FROM students
                ORDER BY gpa DESC
                """);

            query(conn, """
                SELECT title, credits,
                    CASE credits
                        WHEN 4 THEN 'Heavy load'
                        WHEN 3 THEN 'Standard'
                        ELSE       'Light'
                    END AS workload
                FROM courses
                """);

            // ── 13. Calculated Columns ────────────────────────────────────
            section("13 · Calculated columns and expressions");
            query(conn, """
                SELECT
                    first_name,
                    gpa,
                    ROUND(gpa * 25, 1)               AS percentage_score,
                    enrollment_year,
                    (2024 - enrollment_year)          AS years_enrolled
                FROM students
                ORDER BY gpa DESC
                """);

            System.out.println("\n✅  All advanced query demos complete!");
            System.out.println("──────────────────────────────────────────────────────────────");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Database Setup — 4 related tables
    // ─────────────────────────────────────────────────────────────────
    private static void setupDatabase(Connection conn) throws SQLException {
        System.out.println("\n📦  Building university database (4 tables)...");
        Statement st = conn.createStatement();

        st.execute("""
            CREATE TABLE departments (
                id       INT         PRIMARY KEY,
                name     VARCHAR(50) NOT NULL,
                building VARCHAR(40)
            )
            """);

        st.execute("""
            CREATE TABLE instructors (
                id            INT         PRIMARY KEY,
                name          VARCHAR(60) NOT NULL,
                department_id INT
            )
            """);

        st.execute("""
            CREATE TABLE students (
                id              INT          PRIMARY KEY,
                first_name      VARCHAR(50)  NOT NULL,
                last_name       VARCHAR(50)  NOT NULL,
                email           VARCHAR(100) UNIQUE,
                major           VARCHAR(50),
                gpa             DECIMAL(3,2),
                enrollment_year INT
            )
            """);

        st.execute("""
            CREATE TABLE courses (
                id            INT         PRIMARY KEY,
                course_code   VARCHAR(10) UNIQUE,
                title         VARCHAR(100),
                credits       INT,
                department_id INT,
                instructor_id INT
            )
            """);

        st.execute("""
            CREATE TABLE enrollments (
                id         INT        PRIMARY KEY,
                student_id INT,
                course_id  INT,
                grade      VARCHAR(2),
                semester   VARCHAR(20)
            )
            """);

        // Seed data
        st.execute("""
            INSERT INTO departments VALUES
            (1, 'Computer Science', 'Tech Hall'),
            (2, 'Mathematics',      'Science Wing'),
            (3, 'Business',         'Commerce Center'),
            (4, 'Physics',          'Science Wing')
            """);

        st.execute("""
            INSERT INTO instructors VALUES
            (1, 'Dr. Adams',  1),
            (2, 'Dr. Brown',  1),
            (3, 'Dr. Chen',   1),
            (4, 'Dr. Davis',  2),
            (5, 'Dr. Evans',  2),
            (6, 'Dr. Foster', 3),
            (7, 'Dr. Garcia', 4)
            """);

        st.execute("""
            INSERT INTO students VALUES
            (1,  'Alice',  'Johnson',  'alice@uni.edu',  'Computer Science', 3.80, 2022),
            (2,  'Bob',    'Smith',    'bob@uni.edu',    'Mathematics',      3.20, 2021),
            (3,  'Carol',  'Davis',    'carol@uni.edu',  'Computer Science', 3.90, 2023),
            (4,  'David',  'Wilson',   'david@uni.edu',  'Business',         2.90, 2022),
            (5,  'Emma',   'Brown',    'emma@uni.edu',   'Physics',          3.50, 2020),
            (6,  'Frank',  'Miller',   'frank@uni.edu',  'Computer Science', 3.10, 2023),
            (7,  'Grace',  'Taylor',   'grace@uni.edu',  'Mathematics',      3.70, 2021),
            (8,  'Henry',  'Anderson', 'henry@uni.edu',  'Business',         2.70, 2023),
            (9,  'Iris',   'Thomas',   'iris@uni.edu',   'Physics',          3.40, 2022),
            (10, 'James',  'Jackson',  'james@uni.edu',  'Computer Science', 3.60, 2023)
            """);

        st.execute("""
            INSERT INTO courses VALUES
            (1, 'CS101',   'Intro to Programming',   3, 1, 1),
            (2, 'CS201',   'Data Structures',        4, 1, 2),
            (3, 'CS301',   'Database Systems',       3, 1, 3),
            (4, 'MATH101', 'Calculus I',             4, 2, 4),
            (5, 'MATH201', 'Linear Algebra',         3, 2, 5),
            (6, 'BUS101',  'Business Fundamentals',  3, 3, 6),
            (7, 'PHYS101', 'Physics I',              4, 4, 7)
            """);

        st.execute("""
            INSERT INTO enrollments VALUES
            (1,  1, 1, 'A',  'Fall 2023'),
            (2,  1, 2, 'B+', 'Fall 2023'),
            (3,  2, 4, 'B',  'Fall 2023'),
            (4,  2, 5, 'A',  'Fall 2023'),
            (5,  3, 1, 'A+', 'Fall 2023'),
            (6,  3, 3, 'A',  'Fall 2023'),
            (7,  4, 6, 'C+', 'Fall 2023'),
            (8,  5, 7, 'B+', 'Fall 2023'),
            (9,  6, 1, 'B',  'Fall 2023'),
            (10, 6, 2, 'B-', 'Fall 2023'),
            (11, 7, 4, 'A-', 'Fall 2023'),
            (12, 7, 5, 'B+', 'Fall 2023'),
            (13, 9, 7, 'A-', 'Fall 2023'),
            (14, 10,1, 'B+', 'Fall 2023'),
            (15, 10,2, 'A',  'Fall 2023')
            """);
        // Note: students 8 (Henry) has NO enrollment — useful for LEFT JOIN demos

        System.out.println("✅  5 tables ready (students, courses, departments, instructors, enrollments)\n");
    }

    // ─────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────
    private static void section(String title) {
        System.out.printf("%n┌────────────────────────────────────────────────────────────────┐%n");
        System.out.printf("│  %-64s│%n", title);
        System.out.printf("└────────────────────────────────────────────────────────────────┘%n");
    }

    private static void query(Connection conn, String sql) throws SQLException {
        String display = sql.replaceAll("--[^\n]*", "").replaceAll("\\s+", " ").trim();
        System.out.println("\n  SQL ▸ " + display);
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            printTable(rs);
        }
    }

    private static void printTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();

        String[] labels = new String[cols];
        for (int i = 0; i < cols; i++) labels[i] = meta.getColumnLabel(i + 1);

        List<String[]> rows = new ArrayList<>();
        while (rs.next()) {
            String[] row = new String[cols];
            for (int i = 0; i < cols; i++) {
                String v = rs.getString(i + 1);
                row[i] = (v == null) ? "NULL" : v;
            }
            rows.add(row);
        }

        int[] w = new int[cols];
        for (int i = 0; i < cols; i++) w[i] = labels[i].length();
        for (String[] row : rows)
            for (int i = 0; i < cols; i++) w[i] = Math.max(w[i], row[i].length());

        StringBuilder sep = new StringBuilder("+");
        StringBuilder fmt = new StringBuilder("|");
        for (int i = 0; i < cols; i++) {
            sep.append("-".repeat(w[i] + 2)).append("+");
            fmt.append(" %-").append(w[i]).append("s |");
        }
        fmt.append("%n");

        System.out.println("  " + sep);
        System.out.printf("  " + fmt, (Object[]) labels);
        System.out.println("  " + sep);
        if (rows.isEmpty()) System.out.println("  |  (no rows returned)");
        for (String[] row : rows) System.out.printf("  " + fmt, (Object[]) row);
        System.out.println("  " + sep);
        System.out.printf("  %d row(s)%n", rows.size());
    }
}
