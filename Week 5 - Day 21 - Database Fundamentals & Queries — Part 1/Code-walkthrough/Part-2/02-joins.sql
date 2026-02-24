-- =============================================================================
-- Day 21 — Part 2, File 2: JOINs
-- Database: PostgreSQL (compatible with MySQL/SQL Server with noted differences)
-- Schema: Online learning platform (students, instructors, courses, enrollments)
-- Run Part-1/01-database-fundamentals-and-select.sql SETUP section first
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: TABLE RELATIONSHIPS AND JOIN CONCEPTS
-- ─────────────────────────────────────────────────────────────────────────────

-- Our schema has four tables. Here's how they relate:
--
--   students ──────────── enrollments ──────────── courses ──────── instructors
--   student_id (PK)       student_id  (FK→students) course_id (PK)   instructor_id (PK)
--   first_name            course_id   (FK→courses)  instructor_id (FK→instructors)
--   last_name             enrollment_date           title
--   email                 score                     category
--   country                                         price
--   join_date
--
-- Relationship types:
--   students → enrollments : one-to-many (1 student can have many enrollments)
--   courses  → enrollments : one-to-many (1 course can have many enrollments)
--   students ↔ courses     : many-to-many (via enrollments bridge table)
--   instructors → courses  : one-to-many (1 instructor can teach many courses)
--
-- JOINs combine rows from two or more tables based on a related column.
-- Without JOINs, we can only see one table at a time.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: INNER JOIN
-- ─────────────────────────────────────────────────────────────────────────────

-- INNER JOIN returns rows that have a match in BOTH tables.
-- Rows with no match in either table are excluded.

-- 2a. Students and their enrolled courses
SELECT
  s.first_name,
  s.last_name,
  c.title         AS course_title,
  e.enrollment_date,
  e.score
FROM enrollments e
INNER JOIN students s ON e.student_id = s.student_id
INNER JOIN courses  c ON e.course_id  = c.course_id
ORDER BY s.last_name, e.enrollment_date;
-- Table aliases (e, s, c) shorten the query and are required when
-- the same column name exists in multiple tables (e.g., student_id)

-- 2b. Courses with their instructor names
SELECT
  c.title,
  c.category,
  c.price,
  i.first_name || ' ' || i.last_name AS instructor_name,
  i.rating
FROM courses c
INNER JOIN instructors i ON c.instructor_id = i.instructor_id
ORDER BY c.category, c.title;

-- 2c. INNER JOIN + WHERE — enrolled students in Backend courses only
SELECT
  s.first_name,
  s.last_name,
  s.country,
  c.title,
  c.level
FROM enrollments e
INNER JOIN students    s ON e.student_id = s.student_id
INNER JOIN courses     c ON e.course_id  = c.course_id
WHERE c.category = 'Backend'
ORDER BY s.last_name;

-- 2d. INNER JOIN + GROUP BY — number of students per course (with course name)
SELECT
  c.title,
  c.category,
  COUNT(e.student_id) AS enrolled_students
FROM courses c
INNER JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.title, c.category
ORDER BY enrolled_students DESC;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: LEFT JOIN (LEFT OUTER JOIN)
-- ─────────────────────────────────────────────────────────────────────────────

-- LEFT JOIN returns ALL rows from the LEFT table,
-- plus matching rows from the right table.
-- Where there's no match in the right table, NULLs fill the right columns.

-- 3a. All courses, including those with no enrollments
SELECT
  c.title,
  c.category,
  c.price,
  COUNT(e.enrollment_id) AS enrollment_count
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.title, c.category, c.price
ORDER BY enrollment_count DESC;
-- INNER JOIN here would hide courses with zero enrollments!

-- 3b. Find courses with NO enrollments — NULLs reveal the gap
SELECT
  c.title,
  c.category,
  c.price
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
WHERE e.enrollment_id IS NULL    -- NULL on right side = no match found
ORDER BY c.category;

-- 3c. All students, with their most recent enrollment (if any)
SELECT
  s.first_name,
  s.last_name,
  s.country,
  MAX(e.enrollment_date) AS last_enrollment_date,
  COUNT(e.course_id)     AS total_courses
FROM students s
LEFT JOIN enrollments e ON s.student_id = e.student_id
GROUP BY s.student_id, s.first_name, s.last_name, s.country
ORDER BY last_enrollment_date DESC NULLS LAST;
-- NULLS LAST puts students with no enrollments at the bottom

-- 3d. LEFT JOIN practical use: find students who have NEVER enrolled
SELECT
  s.first_name,
  s.last_name,
  s.email,
  s.join_date
FROM students s
LEFT JOIN enrollments e ON s.student_id = e.student_id
WHERE e.student_id IS NULL
ORDER BY s.join_date;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 4: RIGHT JOIN (RIGHT OUTER JOIN)
-- ─────────────────────────────────────────────────────────────────────────────

-- RIGHT JOIN returns ALL rows from the RIGHT table,
-- plus matching rows from the left table.
-- Less common than LEFT JOIN — you can always rewrite a RIGHT JOIN as a LEFT JOIN
-- by swapping the table order.

-- 4a. All instructors, including those with no courses
SELECT
  i.first_name || ' ' || i.last_name AS instructor_name,
  i.rating,
  COUNT(c.course_id) AS course_count
FROM courses c
RIGHT JOIN instructors i ON c.instructor_id = i.instructor_id
GROUP BY i.instructor_id, i.first_name, i.last_name, i.rating
ORDER BY course_count DESC;

-- 4b. The same result using LEFT JOIN (just swap table order)
SELECT
  i.first_name || ' ' || i.last_name AS instructor_name,
  i.rating,
  COUNT(c.course_id) AS course_count
FROM instructors i
LEFT JOIN courses c ON i.instructor_id = c.instructor_id
GROUP BY i.instructor_id, i.first_name, i.last_name, i.rating
ORDER BY course_count DESC;
-- Identical result — RIGHT JOIN is just LEFT JOIN with tables reversed.
-- Most developers write LEFT JOINs exclusively for readability.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 5: FULL OUTER JOIN
-- ─────────────────────────────────────────────────────────────────────────────

-- FULL OUTER JOIN returns ALL rows from BOTH tables.
-- NULLs fill missing matches on either side.
-- Useful for finding mismatches or comparing two datasets.

-- 5a. All students and all courses — show unmatched on both sides
SELECT
  s.first_name,
  s.last_name,
  c.title       AS course_title,
  e.score
FROM enrollments e
FULL OUTER JOIN students s ON e.student_id = s.student_id
FULL OUTER JOIN courses  c ON e.course_id  = c.course_id
WHERE s.student_id IS NULL OR c.course_id IS NULL
ORDER BY s.last_name NULLS LAST;
-- NOTE: MySQL does NOT support FULL OUTER JOIN natively.
-- MySQL workaround: LEFT JOIN UNION ALL RIGHT JOIN WHERE left IS NULL

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 6: CROSS JOIN
-- ─────────────────────────────────────────────────────────────────────────────

-- CROSS JOIN produces a cartesian product: every row from table A
-- paired with every row from table B.
-- Result count: rows_in_A × rows_in_B
-- Use case: generating all possible combinations.

-- 6a. All possible student-course combinations (no WHERE condition)
SELECT
  s.first_name || ' ' || s.last_name AS student_name,
  c.title AS course_title
FROM students s
CROSS JOIN courses c
ORDER BY student_name, course_title
LIMIT 20;
-- 12 students × 14 courses = 168 possible combinations
-- Use LIMIT to preview — full result would be large

-- 6b. Practical use: generate a size × color matrix
--     (e.g., for a merchandise catalog — all size/category combos)
SELECT
  sizes.size_label,
  categories.category
FROM (VALUES ('S'), ('M'), ('L'), ('XL')) AS sizes(size_label)
CROSS JOIN (
  SELECT DISTINCT category FROM courses
) AS categories
ORDER BY sizes.size_label, categories.category;
-- VALUES creates an inline table — a quick way to list options without a real table

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 7: SELF JOIN
-- ─────────────────────────────────────────────────────────────────────────────

-- A self join joins a table to itself.
-- You MUST use table aliases to distinguish the two copies.
-- Use case: hierarchical data (managers/employees), finding related rows.

-- 7a. Find pairs of courses taught by the same instructor
SELECT
  c1.title            AS course_1,
  c2.title            AS course_2,
  c1.instructor_id
FROM courses c1
INNER JOIN courses c2
  ON c1.instructor_id = c2.instructor_id
  AND c1.course_id < c2.course_id     -- prevents (A,B) and (B,A) duplicates
                                       -- and prevents (A,A) self-pairing
ORDER BY c1.instructor_id, c1.title;

-- 7b. Classic self join: employees with their managers
--     (showing the pattern even though our schema uses instructors/courses)
--     Imagine an 'employees' table where manager_id references employee_id:
--
--   employees(employee_id, name, manager_id → employees.employee_id)
--
--   SELECT e.name AS employee, m.name AS manager
--   FROM employees e
--   LEFT JOIN employees m ON e.manager_id = m.employee_id
--   ORDER BY m.name NULLS LAST, e.name;

-- 7c. Students who joined in the same month as another student
SELECT
  s1.first_name || ' ' || s1.last_name AS student_1,
  s2.first_name || ' ' || s2.last_name AS student_2,
  DATE_TRUNC('month', s1.join_date) AS joined_month
FROM students s1
INNER JOIN students s2
  ON DATE_TRUNC('month', s1.join_date) = DATE_TRUNC('month', s2.join_date)
  AND s1.student_id < s2.student_id
ORDER BY joined_month;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 8: MULTI-TABLE JOINS (3 AND 4 TABLES)
-- ─────────────────────────────────────────────────────────────────────────────

-- 8a. 3-table join: student → enrollment → course
SELECT
  s.first_name || ' ' || s.last_name AS student_name,
  s.country,
  c.title        AS course,
  c.level,
  e.score,
  e.enrollment_date
FROM students s
INNER JOIN enrollments e ON s.student_id = e.student_id
INNER JOIN courses     c ON e.course_id  = c.course_id
WHERE s.is_active = TRUE
ORDER BY student_name, e.enrollment_date;

-- 8b. 4-table join: student → enrollment → course → instructor
SELECT
  s.first_name || ' ' || s.last_name   AS student_name,
  c.title                               AS course_title,
  c.category,
  i.first_name || ' ' || i.last_name   AS instructor_name,
  e.score,
  e.enrollment_date
FROM students s
INNER JOIN enrollments e ON s.student_id   = e.student_id
INNER JOIN courses     c ON e.course_id    = c.course_id
INNER JOIN instructors i ON c.instructor_id = i.instructor_id
ORDER BY student_name, c.category;

-- 8c. 4-table join + aggregation: average score per instructor
SELECT
  i.first_name || ' ' || i.last_name   AS instructor_name,
  i.rating                              AS instructor_rating,
  COUNT(DISTINCT c.course_id)           AS courses_taught,
  COUNT(e.enrollment_id)                AS total_students,
  ROUND(AVG(e.score), 2)               AS avg_student_score
FROM instructors i
LEFT JOIN courses     c ON i.instructor_id  = c.instructor_id
LEFT JOIN enrollments e ON c.course_id      = e.course_id
GROUP BY i.instructor_id, i.first_name, i.last_name, i.rating
ORDER BY avg_student_score DESC NULLS LAST;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 9: JOIN TYPE SUMMARY
-- ─────────────────────────────────────────────────────────────────────────────

-- INNER JOIN   → only matching rows from BOTH tables
-- LEFT JOIN    → ALL rows from LEFT table + matching rows from right (NULLs if no match)
-- RIGHT JOIN   → ALL rows from RIGHT table + matching rows from left (NULLs if no match)
-- FULL OUTER   → ALL rows from BOTH tables (NULLs on either side if no match)
-- CROSS JOIN   → cartesian product (every combination)
-- SELF JOIN    → table joined to itself (requires aliases)
--
-- Choose based on whether you want to KEEP or DISCARD unmatched rows.
-- Ask: "Do I need rows from table A even when there's no match in table B?"
-- → Yes = use LEFT JOIN   No = use INNER JOIN
