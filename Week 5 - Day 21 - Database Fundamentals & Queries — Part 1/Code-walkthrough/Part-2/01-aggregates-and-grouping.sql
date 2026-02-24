-- =============================================================================
-- Day 21 — Part 2, File 1: Aggregate Functions & GROUP BY
-- Database: PostgreSQL (compatible with MySQL with noted differences)
-- Schema: Online learning platform (students, instructors, courses, enrollments)
-- Run Part-1/01-database-fundamentals-and-select.sql SETUP section first
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: AGGREGATE FUNCTIONS OVERVIEW
-- ─────────────────────────────────────────────────────────────────────────────

-- Aggregate functions collapse many rows into a single result value.
-- They operate on a set of rows and return one value per group (or one total).
--
-- The five core aggregate functions:
--   COUNT(*)         → total number of rows
--   COUNT(column)    → number of NON-NULL values in that column
--   COUNT(DISTINCT)  → number of UNIQUE non-null values
--   SUM(column)      → total numeric sum
--   AVG(column)      → average numeric value
--   MIN(column)      → smallest value (works on numbers, dates, text)
--   MAX(column)      → largest value

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: COUNT()
-- ─────────────────────────────────────────────────────────────────────────────

-- 2a. Count all rows in a table
SELECT COUNT(*) AS total_students
FROM students;
-- Returns: 12

-- 2b. Count non-null values in a specific column
--     COUNT(column) IGNORES NULL values — important distinction from COUNT(*)
SELECT COUNT(score) AS students_with_scores
FROM enrollments;
-- Returns only rows where score IS NOT NULL

-- 2c. What's the difference?
SELECT
  COUNT(*)          AS total_enrollments,
  COUNT(score)      AS completed_enrollments,
  COUNT(*) - COUNT(score) AS pending_enrollments
FROM enrollments;
-- NULL scores = students enrolled but no score assigned yet

-- 2d. COUNT(DISTINCT) — count unique values
SELECT COUNT(DISTINCT country) AS unique_countries
FROM students;
-- How many different countries our students come from

SELECT COUNT(DISTINCT category) AS unique_categories
FROM courses;
-- How many course categories exist

-- 2e. COUNT with WHERE — count filtered rows
SELECT COUNT(*) AS active_courses
FROM courses
WHERE is_active = TRUE;

SELECT COUNT(*) AS advanced_courses
FROM courses
WHERE level = 'Advanced';

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: SUM, AVG, MIN, MAX
-- ─────────────────────────────────────────────────────────────────────────────

-- 3a. SUM — total of all values
SELECT SUM(price) AS total_course_revenue_potential
FROM courses
WHERE is_active = TRUE;

-- 3b. AVG — arithmetic mean
SELECT ROUND(AVG(price), 2) AS average_course_price
FROM courses;
-- ROUND(value, decimal_places) — always round currency to 2 decimal places

SELECT ROUND(AVG(score), 2) AS average_student_score
FROM enrollments
WHERE score IS NOT NULL;
-- AVG automatically ignores NULLs — no need to filter them out

-- 3c. MIN and MAX
SELECT
  MIN(price) AS cheapest_course,
  MAX(price) AS most_expensive_course,
  MAX(price) - MIN(price) AS price_range
FROM courses;

SELECT
  MIN(enrollment_date) AS first_enrollment,
  MAX(enrollment_date) AS most_recent_enrollment
FROM enrollments;
-- MIN/MAX works on dates — returns earliest/latest date

SELECT
  MIN(score)            AS lowest_score,
  MAX(score)            AS highest_score,
  ROUND(AVG(score), 2)  AS average_score
FROM enrollments
WHERE score IS NOT NULL;

-- 3d. All five aggregates in one query (course price stats)
SELECT
  COUNT(*)              AS total_courses,
  COUNT(DISTINCT level) AS unique_levels,
  ROUND(AVG(price), 2)  AS avg_price,
  SUM(price)            AS total_price_sum,
  MIN(price)            AS min_price,
  MAX(price)            AS max_price
FROM courses
WHERE is_active = TRUE;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 4: GROUP BY — Aggregating by Category
-- ─────────────────────────────────────────────────────────────────────────────

-- GROUP BY groups rows that share the same value in a column,
-- then applies the aggregate function to each group separately.
-- Result: one row per unique group value.

-- 4a. Count courses per category
SELECT
  category,
  COUNT(*) AS course_count
FROM courses
GROUP BY category
ORDER BY course_count DESC;

-- 4b. Average price per category
SELECT
  category,
  COUNT(*)              AS course_count,
  ROUND(AVG(price), 2)  AS avg_price,
  MIN(price)            AS min_price,
  MAX(price)            AS max_price
FROM courses
GROUP BY category
ORDER BY avg_price DESC;

-- 4c. Count enrollments per course
SELECT
  course_id,
  COUNT(*) AS enrollment_count
FROM enrollments
GROUP BY course_id
ORDER BY enrollment_count DESC;

-- 4d. Average score per course
SELECT
  course_id,
  COUNT(score)          AS scored_enrollments,
  ROUND(AVG(score), 2)  AS avg_score
FROM enrollments
WHERE score IS NOT NULL
GROUP BY course_id
ORDER BY avg_score DESC;

-- 4e. GROUP BY multiple columns
--     Groups by the COMBINATION of both columns — one row per unique pair
SELECT
  level,
  category,
  COUNT(*) AS course_count,
  ROUND(AVG(price), 2) AS avg_price
FROM courses
GROUP BY level, category
ORDER BY level, category;

-- 4f. Count students per country
SELECT
  country,
  COUNT(*) AS student_count,
  COUNT(CASE WHEN is_active = TRUE THEN 1 END) AS active_count
FROM students
GROUP BY country
ORDER BY student_count DESC;
-- CASE inside COUNT — a preview of CASE statements (covered in Part 2, File 3)

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 5: HAVING — Filtering Groups
-- ─────────────────────────────────────────────────────────────────────────────

-- WHERE filters ROWS before they are grouped.
-- HAVING filters GROUPS after aggregation.
--
-- Rule: if you need to filter on an aggregate result (COUNT, AVG, SUM, etc.),
-- you MUST use HAVING. You cannot use WHERE with aggregate functions.

-- 5a. HAVING with COUNT — categories with more than 2 courses
SELECT
  category,
  COUNT(*) AS course_count
FROM courses
GROUP BY category
HAVING COUNT(*) > 2
ORDER BY course_count DESC;

-- 5b. HAVING with AVG — courses whose avg score is above 75
SELECT
  course_id,
  COUNT(score)          AS scored_students,
  ROUND(AVG(score), 2)  AS avg_score
FROM enrollments
WHERE score IS NOT NULL          -- WHERE filters rows BEFORE grouping
GROUP BY course_id
HAVING AVG(score) > 75           -- HAVING filters groups AFTER aggregation
ORDER BY avg_score DESC;

-- 5c. HAVING with SUM — students enrolled in more than 2 courses
SELECT
  student_id,
  COUNT(*) AS course_count
FROM enrollments
GROUP BY student_id
HAVING COUNT(*) > 2
ORDER BY course_count DESC;

-- 5d. WHERE + GROUP BY + HAVING together (full pipeline)
--     Find active course categories with average price above $60
SELECT
  category,
  COUNT(*)              AS course_count,
  ROUND(AVG(price), 2)  AS avg_price
FROM courses
WHERE is_active = TRUE           -- Step 1: filter rows (only active courses)
GROUP BY category                -- Step 2: group remaining rows by category
HAVING AVG(price) > 60.00        -- Step 3: filter groups (only expensive categories)
ORDER BY avg_price DESC;         -- Step 4: sort groups

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 6: SQL EXECUTION ORDER
-- ─────────────────────────────────────────────────────────────────────────────

-- SQL clauses execute in this order (NOT the order you write them):
--
--   1. FROM        — identify the source table(s)
--   2. WHERE       — filter individual rows
--   3. GROUP BY    — group filtered rows
--   4. HAVING      — filter groups
--   5. SELECT      — calculate output columns
--   6. DISTINCT    — remove duplicate rows from output
--   7. ORDER BY    — sort the output
--   8. LIMIT/OFFSET — truncate the final result
--
-- WHY THIS MATTERS:
--   - You cannot use a SELECT alias in a WHERE clause (WHERE runs before SELECT)
--   - You CAN use a SELECT alias in ORDER BY (ORDER BY runs after SELECT)
--   - You cannot use aggregate functions in WHERE (WHERE runs before GROUP BY)
--   - You CAN use aggregate functions in HAVING (HAVING runs after GROUP BY)

-- This query FAILS because WHERE runs before SELECT:
-- SELECT price * 0.9 AS discounted FROM courses WHERE discounted < 50;
-- ❌ ERROR: column "discounted" does not exist

-- This query WORKS:
SELECT price * 0.9 AS discounted
FROM courses
WHERE price * 0.9 < 50        -- must repeat the expression
ORDER BY discounted;           -- ORDER BY CAN use the alias ✅

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 7: REALISTIC AGGREGATE QUERIES
-- ─────────────────────────────────────────────────────────────────────────────

-- 7a. Course performance summary: count students, avg score, completion rate
SELECT
  course_id,
  COUNT(*)                                    AS total_enrolled,
  COUNT(score)                                AS completed,
  COUNT(*) - COUNT(score)                     AS not_scored,
  ROUND(AVG(score), 2)                        AS avg_score,
  ROUND(COUNT(score)::numeric / COUNT(*) * 100, 1) AS completion_pct
FROM enrollments
GROUP BY course_id
ORDER BY total_enrolled DESC;
-- ::numeric is PostgreSQL cast syntax — converts integer to decimal for division
-- In MySQL: use COUNT(score) / COUNT(*) * 100 directly

-- 7b. Student engagement: total courses enrolled, avg score per student
SELECT
  student_id,
  COUNT(*)                    AS courses_enrolled,
  COUNT(score)                AS courses_completed,
  ROUND(AVG(score), 2)        AS avg_score,
  MAX(enrollment_date)        AS most_recent_enrollment
FROM enrollments
GROUP BY student_id
ORDER BY courses_enrolled DESC;

-- 7c. Course catalog stats by difficulty level
SELECT
  level,
  COUNT(*)              AS course_count,
  ROUND(AVG(price), 2)  AS avg_price,
  ROUND(AVG(duration_hours), 1) AS avg_duration_hrs,
  MIN(price)            AS cheapest,
  MAX(price)            AS most_expensive
FROM courses
WHERE is_active = TRUE
GROUP BY level
ORDER BY avg_price DESC;
