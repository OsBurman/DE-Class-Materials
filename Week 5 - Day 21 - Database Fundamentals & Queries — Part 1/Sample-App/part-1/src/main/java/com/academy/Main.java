package com.academy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Day 21 — Part 1: SQL SELECT Fundamentals
 * ==========================================
 * Topics covered:
 *   ✓ SELECT * and specific columns
 *   ✓ Column aliases with AS
 *   ✓ WHERE clause — comparison operators (=, <>, <, >, <=, >=)
 *   ✓ Logical operators — AND, OR, NOT
 *   ✓ LIKE operator with % and _ wildcards
 *   ✓ IN and NOT IN operators
 *   ✓ BETWEEN ... AND ... (inclusive range)
 *   ✓ ORDER BY ASC / DESC (single and multi-column)
 *   ✓ LIMIT and OFFSET — pagination
 *   ✓ DISTINCT — eliminating duplicate values
 *
 * Database : H2 in-memory (University schema)
 * Run      : mvn compile exec:java
 */
public class Main {

    private static final String URL  = "jdbc:h2:mem:university;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    // ─────────────────────────────────────────────────────────────────
    //  Entry Point
    // ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   Day 21 · Part 1 — SQL SELECT Fundamentals                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            setupDatabase(conn);

            // ── 1. Basic SELECT ────────────────────────────────────────────
            section("1 · Basic SELECT — all columns vs specific columns");
            query(conn, "SELECT * FROM students");
            query(conn, "SELECT first_name, last_name, major, gpa FROM students");

            // ── 2. Column Aliases ──────────────────────────────────────────
            section("2 · Column Aliases with AS");
            query(conn, """
                SELECT
                    first_name || ' ' || last_name  AS full_name,
                    gpa                             AS grade_point_avg,
                    major                           AS field_of_study
                FROM students
                """);

            // ── 3. WHERE — Comparison Operators ───────────────────────────
            section("3 · WHERE clause — comparison operators (=, <>, >=, <, <=, >)");
            query(conn, "SELECT first_name, gpa   FROM students WHERE gpa >= 3.7");
            query(conn, "SELECT first_name, age   FROM students WHERE age < 21");
            query(conn, "SELECT first_name, major FROM students WHERE major <> 'Business'");

            // ── 4. Logical Operators ───────────────────────────────────────
            section("4 · Logical Operators — AND, OR, NOT");
            query(conn, """
                SELECT first_name, major, gpa
                FROM   students
                WHERE  major = 'Computer Science' AND gpa >= 3.6
                """);
            query(conn, """
                SELECT first_name, major
                FROM   students
                WHERE  major = 'Physics' OR major = 'Mathematics'
                """);
            query(conn, """
                SELECT first_name, major
                FROM   students
                WHERE  NOT (major = 'Business' OR major = 'Physics')
                """);

            // ── 5. LIKE Operator ───────────────────────────────────────────
            section("5 · LIKE — % matches any sequence, _ matches exactly one character");
            query(conn, "SELECT first_name, email FROM students WHERE email LIKE '%@uni.edu'");
            query(conn, "SELECT first_name         FROM students WHERE first_name LIKE 'A%'          -- starts with A");
            query(conn, "SELECT first_name         FROM students WHERE first_name LIKE '_r%'         -- 2nd char = r");
            query(conn, "SELECT title              FROM courses  WHERE title      LIKE '%Data%'      -- contains Data");

            // ── 6. IN Operator ─────────────────────────────────────────────
            section("6 · IN and NOT IN — match against a value list");
            query(conn, "SELECT first_name, major FROM students WHERE major IN ('Computer Science', 'Physics')");
            query(conn, "SELECT first_name, major FROM students WHERE major NOT IN ('Business', 'Physics')");
            query(conn, "SELECT title, credits    FROM courses  WHERE credits IN (3)");

            // ── 7. BETWEEN ─────────────────────────────────────────────────
            section("7 · BETWEEN ... AND ... — inclusive range filter");
            query(conn, "SELECT first_name, gpa             FROM students WHERE gpa             BETWEEN 3.1 AND 3.5");
            query(conn, "SELECT first_name, enrollment_year FROM students WHERE enrollment_year BETWEEN 2021 AND 2022");

            // ── 8. ORDER BY ────────────────────────────────────────────────
            section("8 · ORDER BY — single-column and multi-column sorting");
            query(conn, "SELECT first_name, gpa  FROM students ORDER BY gpa DESC");
            query(conn, "SELECT first_name, gpa  FROM students ORDER BY gpa ASC");
            query(conn, """
                SELECT first_name, major, gpa
                FROM   students
                ORDER  BY major ASC, gpa DESC
                """);

            // ── 9. LIMIT / OFFSET ──────────────────────────────────────────
            section("9 · LIMIT and OFFSET — pagination (page size = 3)");
            query(conn, "SELECT id, first_name, major FROM students ORDER BY id LIMIT 3");           // page 1
            query(conn, "SELECT id, first_name, major FROM students ORDER BY id LIMIT 3 OFFSET 3"); // page 2
            query(conn, "SELECT id, first_name, major FROM students ORDER BY id LIMIT 3 OFFSET 6"); // page 3
            query(conn, "SELECT id, first_name, major FROM students ORDER BY id LIMIT 3 OFFSET 9"); // page 4

            // ── 10. DISTINCT ───────────────────────────────────────────────
            section("10 · DISTINCT — remove duplicate rows");
            query(conn, "SELECT DISTINCT major           FROM students ORDER BY major");
            query(conn, "SELECT DISTINCT enrollment_year FROM students ORDER BY enrollment_year");
            query(conn, "SELECT DISTINCT credits         FROM courses  ORDER BY credits");

            System.out.println("\n✅  All SELECT fundamentals demonstrated!");
            System.out.println("──────────────────────────────────────────────────────────────");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Database Setup
    // ─────────────────────────────────────────────────────────────────
    private static void setupDatabase(Connection conn) throws SQLException {
        System.out.println("\n📦  Building university database...");
        Statement st = conn.createStatement();

        st.execute("""
            CREATE TABLE students (
                id              INT          PRIMARY KEY,
                first_name      VARCHAR(50)  NOT NULL,
                last_name       VARCHAR(50)  NOT NULL,
                email           VARCHAR(100) UNIQUE,
                age             INT,
                major           VARCHAR(50),
                gpa             DECIMAL(3,2),
                enrollment_year INT
            )
            """);

        st.execute("""
            CREATE TABLE courses (
                id          INT          PRIMARY KEY,
                course_code VARCHAR(10)  UNIQUE,
                title       VARCHAR(100),
                credits     INT,
                instructor  VARCHAR(60)
            )
            """);

        st.execute("""
            INSERT INTO students VALUES
            (1,  'Alice',  'Johnson',  'alice@uni.edu',  20, 'Computer Science', 3.80, 2022),
            (2,  'Bob',    'Smith',    'bob@uni.edu',    22, 'Mathematics',      3.20, 2021),
            (3,  'Carol',  'Davis',    'carol@uni.edu',  19, 'Computer Science', 3.90, 2023),
            (4,  'David',  'Wilson',   'david@uni.edu',  21, 'Business',         2.90, 2022),
            (5,  'Emma',   'Brown',    'emma@uni.edu',   23, 'Physics',          3.50, 2020),
            (6,  'Frank',  'Miller',   'frank@uni.edu',  20, 'Computer Science', 3.10, 2023),
            (7,  'Grace',  'Taylor',   'grace@uni.edu',  22, 'Mathematics',      3.70, 2021),
            (8,  'Henry',  'Anderson', 'henry@uni.edu',  19, 'Business',         2.70, 2023),
            (9,  'Iris',   'Thomas',   'iris@uni.edu',   21, 'Physics',          3.40, 2022),
            (10, 'James',  'Jackson',  'james@uni.edu',  20, 'Computer Science', 3.60, 2023)
            """);

        st.execute("""
            INSERT INTO courses VALUES
            (1, 'CS101',   'Intro to Programming',   3, 'Dr. Adams'),
            (2, 'CS201',   'Data Structures',        4, 'Dr. Brown'),
            (3, 'CS301',   'Database Systems',       3, 'Dr. Chen'),
            (4, 'MATH101', 'Calculus I',             4, 'Dr. Davis'),
            (5, 'MATH201', 'Linear Algebra',         3, 'Dr. Evans'),
            (6, 'BUS101',  'Business Fundamentals',  3, 'Dr. Foster'),
            (7, 'PHYS101', 'Physics I',              4, 'Dr. Garcia')
            """);

        System.out.println("✅  Tables ready: students (10 rows), courses (7 rows)\n");
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
        // Strip inline comments for cleaner display
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
