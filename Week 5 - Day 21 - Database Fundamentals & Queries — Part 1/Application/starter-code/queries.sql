-- =============================================================
-- queries.sql
-- Day 21: Database Fundamentals & Queries — Part 1
-- School Query Challenge
--
-- Instructions:
--   1. Run school_schema.sql first
--   2. Run sample_data.sql second
--   3. Write your SQL for each TODO below
-- =============================================================

-- ============================================================
-- PART 1 — Basic SELECT, Filtering & Sorting (Queries 1–5)
-- ============================================================

-- Query 1: List all students (first name, last name, email) sorted alphabetically by last name
-- TODO: Write your SELECT query here


-- Query 2: List all courses that are worth MORE than 3 credits
-- TODO:


-- Query 3: Find all students whose GPA is 3.5 or higher
-- Show: first_name, last_name, gpa — sorted by gpa descending
-- TODO:


-- Query 4: Find all students who have NOT declared a major (major_id is NULL)
-- TODO:


-- Query 5: Find all courses taught in 'Fall 2024' with fewer than 25 max_enrollment spots
-- TODO:


-- ============================================================
-- PART 2 — Aggregate Functions, GROUP BY & HAVING (Queries 6–10)
-- ============================================================

-- Query 6: Count the total number of students in each major
-- Show: major_name, student_count — sorted by student_count descending
-- TODO:


-- Query 7: Find the average GPA of students in each major
-- Show: major_name, avg_gpa (rounded to 2 decimal places)
-- Exclude students without a declared major
-- TODO:


-- Query 8: Find the total number of credits per instructor
-- (sum the credits of all courses they teach)
-- Show: instructor first_name + last_name, total_credits
-- TODO:


-- Query 9: Find all majors where the average student GPA is above 3.0
-- (Use HAVING — not WHERE)
-- TODO:


-- Query 10: Count how many students received each grade (A, B, C, D, F)
-- Show: grade, count — sorted by grade
-- Exclude NULL grades (students still enrolled)
-- TODO:


-- ============================================================
-- PART 3 — JOINs (Queries 11–15)
-- ============================================================

-- Query 11: List all enrollments with student full name and course name
-- INNER JOIN — only students who ARE enrolled
-- Show: student first+last name, course_name, grade
-- TODO:


-- Query 12: List ALL students and any courses they are enrolled in
-- LEFT JOIN — include students with NO enrollments (Luna and Jack should appear)
-- Show: student first+last name, course_name (NULL if not enrolled)
-- TODO:


-- Query 13: List all courses and the number of students enrolled in each
-- Include courses with ZERO enrollments if any
-- Show: course_name, enrollment_count — sorted descending
-- TODO:


-- Query 14: Show each student's full name, their major name, and their instructor's name
-- A student → major is not needed; show: student name, major_name, course_name, instructor full name
-- (3-table JOIN: students → enrollments → courses → instructors)
-- TODO:


-- Query 15: Find all students enrolled in CS101 along with their grade
-- Show: student first+last name, grade
-- TODO:


-- ============================================================
-- PART 4 — Subqueries & CASE Expressions (Queries 16–20)
-- ============================================================

-- Query 16: Find students whose GPA is above the overall average GPA
-- Use a subquery in the WHERE clause
-- Show: first_name, last_name, gpa
-- TODO:


-- Query 17: Find the course(s) with the highest enrollment count
-- Use a subquery — do NOT use LIMIT or TOP
-- TODO:


-- Query 18: List all students and classify them using CASE:
--   GPA >= 3.7 → 'Honors'
--   GPA >= 3.0 → 'Good Standing'
--   GPA >= 2.0 → 'Satisfactory'
--   GPA < 2.0  → 'Academic Probation'
--   GPA IS NULL → 'Not Yet Evaluated'
-- Show: first_name, last_name, gpa, standing
-- TODO:


-- Query 19: Find instructors who teach more than one course
-- Use a subquery or HAVING
-- TODO:


-- Query 20: For each major, show the student with the highest GPA
-- (Hint: use a subquery or window function if your DB supports it)
-- Show: major_name, first_name, last_name, gpa
-- TODO:
