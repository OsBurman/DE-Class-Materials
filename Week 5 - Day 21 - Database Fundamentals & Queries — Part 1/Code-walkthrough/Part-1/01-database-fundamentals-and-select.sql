-- =============================================================================
-- Day 21 — Part 1: Database Fundamentals & SELECT Queries
-- =============================================================================
-- Topics covered:
--   1. Database fundamentals and RDBMS concepts
--   2. SQL basics and syntax
--   3. Data Query Language (DQL)
--   4. SELECT statements and query structure
--   5. WHERE clause and filtering
--   6. Logical operators (AND, OR, NOT)
--   7. LIKE, IN, BETWEEN operators
--   8. ORDER BY and sorting (ASC, DESC)
--   9. LIMIT and pagination
--  10. DISTINCT keyword
--
-- DATABASE: PostgreSQL (all syntax is ANSI SQL — compatible with MySQL too,
--           with minor notes where they differ)
-- =============================================================================
-- HOW TO USE THIS FILE:
--   1. Run the SETUP section first to create and populate all tables
--   2. Then run individual SELECT sections during the walkthrough
--   3. Each section is clearly labelled so you can jump to it by scrolling
-- =============================================================================


-- =============================================================================
-- SETUP — Create and populate the demo database
-- =============================================================================
-- Schema: an online learning platform
--   • students    — people enrolled in courses
--   • instructors — people who teach courses
--   • courses     — available courses
--   • enrollments — which student is in which course (many-to-many bridge)
-- =============================================================================

-- Clean up if re-running
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS instructors;

-- Students table
CREATE TABLE students (
    student_id   SERIAL PRIMARY KEY,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    city         VARCHAR(50),
    country      VARCHAR(50)  DEFAULT 'USA',
    joined_date  DATE         NOT NULL,
    is_active    BOOLEAN      DEFAULT TRUE
);

-- Instructors table
CREATE TABLE instructors (
    instructor_id SERIAL PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    specialty     VARCHAR(100),
    rating        NUMERIC(3,1) CHECK (rating BETWEEN 0 AND 5),
    country       VARCHAR(50)
);

-- Courses table
CREATE TABLE courses (
    course_id    SERIAL PRIMARY KEY,
    title        VARCHAR(150) NOT NULL,
    category     VARCHAR(50),
    price        NUMERIC(8,2) NOT NULL,
    duration_hrs INTEGER,
    level        VARCHAR(20)  CHECK (level IN ('Beginner','Intermediate','Advanced')),
    instructor_id INTEGER     REFERENCES instructors(instructor_id),
    published    BOOLEAN      DEFAULT TRUE,
    created_at   DATE         NOT NULL
);

-- Enrollments table (bridge / junction table)
CREATE TABLE enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id    INTEGER REFERENCES students(student_id),
    course_id     INTEGER REFERENCES courses(course_id),
    enrolled_date DATE    NOT NULL,
    completed     BOOLEAN DEFAULT FALSE,
    score         NUMERIC(5,2)
);

-- ── Seed data ─────────────────────────────────────────────────────────────────

INSERT INTO instructors (full_name, specialty, rating, country) VALUES
    ('Alice Nguyen',   'Java & Spring Boot',      4.9, 'USA'),
    ('Bob Martinez',   'React & Frontend',        4.7, 'USA'),
    ('Carol Thompson', 'Databases & SQL',          4.8, 'Canada'),
    ('David Kim',      'DevOps & Cloud',           4.6, 'UK'),
    ('Eva Patel',      'Machine Learning',         4.9, 'India'),
    ('Frank Schulz',   'Angular & TypeScript',     4.5, 'Germany');

INSERT INTO students (first_name, last_name, email, city, country, joined_date, is_active) VALUES
    ('James',    'Wilson',    'james.wilson@email.com',    'New York',    'USA',       '2024-01-15', TRUE),
    ('Sofia',    'Garcia',    'sofia.garcia@email.com',    'Miami',       'USA',       '2024-02-01', TRUE),
    ('Liam',     'Johnson',   'liam.j@email.com',          'Chicago',     'USA',       '2024-01-20', TRUE),
    ('Aisha',    'Okonkwo',   'aisha.o@email.com',         'Lagos',       'Nigeria',   '2024-03-10', TRUE),
    ('Noah',     'Brown',     'noah.brown@email.com',      'Austin',      'USA',       '2024-02-28', FALSE),
    ('Emma',     'Davis',     'emma.davis@email.com',      'London',      'UK',        '2024-03-05', TRUE),
    ('Oliver',   'Smith',     'oliver.s@email.com',        'Toronto',     'Canada',    '2024-01-30', TRUE),
    ('Priya',    'Sharma',    'priya.sharma@email.com',    'Bangalore',   'India',     '2024-04-12', TRUE),
    ('Carlos',   'Mendez',    'carlos.m@email.com',        'Mexico City', 'Mexico',    '2024-04-20', TRUE),
    ('Hannah',   'Lee',       'hannah.lee@email.com',      'Seoul',       'South Korea','2024-05-01', TRUE),
    ('Marcus',   'Taylor',    'marcus.t@email.com',        'Atlanta',     'USA',       '2024-01-05', FALSE),
    ('Yuki',     'Tanaka',    'yuki.t@email.com',          'Tokyo',       'Japan',     '2024-05-15', TRUE);

INSERT INTO courses (title, category, price, duration_hrs, level, instructor_id, published, created_at) VALUES
    ('Java Fundamentals',             'Java',       49.99,  40, 'Beginner',     1, TRUE,  '2023-06-01'),
    ('Spring Boot Mastery',           'Java',       79.99,  60, 'Advanced',     1, TRUE,  '2023-08-15'),
    ('React from Scratch',            'Frontend',   59.99,  35, 'Beginner',     2, TRUE,  '2023-07-01'),
    ('Advanced React Patterns',       'Frontend',   89.99,  25, 'Advanced',     2, TRUE,  '2023-11-20'),
    ('SQL for Developers',            'Database',   39.99,  20, 'Beginner',     3, TRUE,  '2023-05-10'),
    ('Database Design & Optimization','Database',   69.99,  30, 'Intermediate', 3, TRUE,  '2023-09-01'),
    ('Docker & Kubernetes',           'DevOps',     74.99,  45, 'Intermediate', 4, TRUE,  '2023-10-15'),
    ('AWS Cloud Practitioner',        'Cloud',      54.99,  28, 'Beginner',     4, TRUE,  '2024-01-05'),
    ('Machine Learning with Python',  'AI/ML',      99.99,  55, 'Intermediate', 5, TRUE,  '2023-12-01'),
    ('Deep Learning & Neural Nets',   'AI/ML',     119.99,  70, 'Advanced',     5, TRUE,  '2024-02-20'),
    ('Angular Complete Guide',        'Frontend',   64.99,  50, 'Intermediate', 6, TRUE,  '2023-08-01'),
    ('TypeScript Deep Dive',          'Frontend',   44.99,  18, 'Intermediate', 6, FALSE, '2024-03-01'),
    ('GraphQL APIs',                  'Backend',    59.99,  22, 'Intermediate', 1, TRUE,  '2024-01-15'),
    ('Linux & Shell Scripting',       'DevOps',     34.99,  15, 'Beginner',     4, TRUE,  '2023-04-01');

INSERT INTO enrollments (student_id, course_id, enrolled_date, completed, score) VALUES
    (1, 1,  '2024-02-01', TRUE,  92.5),
    (1, 2,  '2024-03-15', FALSE, NULL),
    (1, 5,  '2024-02-10', TRUE,  88.0),
    (2, 3,  '2024-02-05', TRUE,  95.0),
    (2, 4,  '2024-04-01', FALSE, NULL),
    (2, 11, '2024-03-20', TRUE,  78.5),
    (3, 1,  '2024-02-15', TRUE,  85.0),
    (3, 9,  '2024-03-01', FALSE, NULL),
    (4, 5,  '2024-03-15', TRUE,  91.0),
    (4, 6,  '2024-04-10', FALSE, NULL),
    (5, 7,  '2024-03-20', FALSE, NULL),
    (6, 8,  '2024-04-01', TRUE,  74.0),
    (6, 3,  '2024-04-05', TRUE,  89.0),
    (7, 2,  '2024-04-10', FALSE, NULL),
    (7, 13, '2024-05-01', FALSE, NULL),
    (8, 9,  '2024-04-15', TRUE,  97.5),
    (8, 10, '2024-05-10', FALSE, NULL),
    (9, 1,  '2024-05-01', FALSE, NULL),
    (10, 11,'2024-05-05', TRUE,  82.0),
    (10, 12,'2024-05-20', FALSE, NULL),
    (11, 5, '2024-01-20', TRUE,  66.0),
    (12, 9, '2024-05-15', FALSE, NULL);


-- =============================================================================
-- SECTION 1 — Database Fundamentals and RDBMS Concepts
-- =============================================================================
-- Key terms (review with students before running queries):
--
-- Database       — an organised collection of structured data
-- RDBMS          — Relational Database Management System; stores data in
--                  related TABLES (rows and columns)
-- Table          — like a spreadsheet; rows = records, columns = fields
-- Primary Key    — uniquely identifies each row (e.g. student_id)
-- Foreign Key    — a column that references the PK of another table
-- Schema         — the blueprint (structure) of the database
-- SQL            — Structured Query Language; the language used to talk to an RDBMS
--
-- Popular RDBMS:  PostgreSQL, MySQL, Oracle, SQL Server, SQLite
-- Our tool today: PostgreSQL
-- =============================================================================

-- Explore the schema we just created
-- (run these one at a time in psql or a GUI like pgAdmin / DBeaver)

-- What tables exist?
-- \dt                  ← psql command (not SQL)

-- What columns does the students table have?
-- \d students          ← psql describe command

-- Simple "am I connected?" query
SELECT current_database(), current_user, version();


-- =============================================================================
-- SECTION 2 — SQL Basics and Syntax / Data Query Language (DQL)
-- =============================================================================
-- DQL = the SELECT sub-language: used ONLY to READ data, never to change it.
--
-- Basic SELECT anatomy:
--   SELECT  column1, column2, ...   ← what to return
--   FROM    table_name              ← where the data lives
--   WHERE   condition               ← optional filter
--   ORDER BY column ASC|DESC        ← optional sort
--   LIMIT   n                       ← optional row limit
--
-- SQL is NOT case-sensitive for keywords, but convention is:
--   KEYWORDS in UPPERCASE, identifiers (table/column names) in lowercase
-- =============================================================================

-- Select ALL columns from students (* = wildcard = every column)
SELECT * FROM students;

-- Select SPECIFIC columns — more efficient and readable
SELECT first_name, last_name, email
FROM students;

-- Column aliases — rename output columns with AS
SELECT
    first_name  AS "First Name",
    last_name   AS "Last Name",
    joined_date AS "Member Since"
FROM students;

-- Concatenate columns into one expression
SELECT
    first_name || ' ' || last_name AS full_name,  -- PostgreSQL concat
    email
FROM students;
-- MySQL equivalent: CONCAT(first_name, ' ', last_name)


-- =============================================================================
-- SECTION 3 — WHERE Clause and Filtering
-- =============================================================================
-- WHERE comes after FROM and before ORDER BY / LIMIT
-- Comparison operators: =  !=(<>)  <  >  <=  >=
-- NULL checks: IS NULL, IS NOT NULL  (never use = NULL — it won't work!)
-- =============================================================================

-- Filter by exact match
SELECT first_name, last_name, country
FROM students
WHERE country = 'USA';

-- Filter by inequality
SELECT title, price
FROM courses
WHERE price > 60.00;

-- Filter on boolean column
SELECT first_name, last_name
FROM students
WHERE is_active = TRUE;

-- IS NULL check — find enrollments with no score yet (not completed)
SELECT student_id, course_id, enrolled_date
FROM enrollments
WHERE score IS NULL;

-- IS NOT NULL — find completed enrollments
SELECT student_id, course_id, score
FROM enrollments
WHERE score IS NOT NULL;


-- =============================================================================
-- SECTION 4 — Logical Operators (AND, OR, NOT)
-- =============================================================================
-- AND   — both conditions must be true
-- OR    — at least one condition must be true
-- NOT   — reverses the condition
-- Use parentheses to control evaluation order (just like algebra)
-- =============================================================================

-- AND — advanced courses that cost more than $80
SELECT title, level, price
FROM courses
WHERE level = 'Advanced'
AND   price > 80.00;

-- OR — courses in Frontend OR Database category
SELECT title, category, price
FROM courses
WHERE category = 'Frontend'
OR    category = 'Database';

-- NOT — everything except beginner courses
SELECT title, level
FROM courses
WHERE NOT level = 'Beginner';

-- Combining AND with OR — use parentheses to be explicit!
-- "Active students from USA or Canada"
SELECT first_name, last_name, country, is_active
FROM students
WHERE is_active = TRUE
AND   (country = 'USA' OR country = 'Canada');

-- WATCH OUT: without parentheses the meaning changes completely:
-- WHERE is_active = TRUE AND country = 'USA' OR country = 'Canada'
-- reads as: (is_active AND USA) OR Canada — returns all Canadian students


-- =============================================================================
-- SECTION 5 — LIKE, IN, and BETWEEN Operators
-- =============================================================================

-- ── LIKE — pattern matching ───────────────────────────────────────────────────
-- % = any sequence of characters (including none)
-- _ = exactly one character
-- ILIKE = case-insensitive LIKE (PostgreSQL only; MySQL LIKE is already case-insensitive)

-- Courses whose title starts with "Java"
SELECT title, category
FROM courses
WHERE title LIKE 'Java%';

-- Courses with "React" anywhere in the title
SELECT title
FROM courses
WHERE title LIKE '%React%';

-- Emails ending in .com
SELECT first_name, email
FROM students
WHERE email LIKE '%.com';

-- Names where the second character is 'a' (e.g. "Liam", "Carlos")
SELECT first_name
FROM students
WHERE first_name LIKE '_a%';

-- ── IN — match any value in a list ────────────────────────────────────────────
-- Cleaner alternative to multiple OR conditions

-- Same as: WHERE category = 'Frontend' OR category = 'AI/ML' OR category = 'Database'
SELECT title, category
FROM courses
WHERE category IN ('Frontend', 'AI/ML', 'Database');

-- Students NOT from these countries
SELECT first_name, last_name, country
FROM students
WHERE country NOT IN ('USA', 'Canada', 'UK');

-- ── BETWEEN — inclusive range filter ──────────────────────────────────────────
-- Equivalent to: price >= 40 AND price <= 70
SELECT title, price
FROM courses
WHERE price BETWEEN 40.00 AND 70.00;

-- Date range — students who joined in Q1 2024
SELECT first_name, last_name, joined_date
FROM students
WHERE joined_date BETWEEN '2024-01-01' AND '2024-03-31';

-- NOT BETWEEN — outside a range
SELECT title, duration_hrs
FROM courses
WHERE duration_hrs NOT BETWEEN 20 AND 40;


-- =============================================================================
-- SECTION 6 — ORDER BY and Sorting (ASC, DESC)
-- =============================================================================
-- Default sort direction is ASC (ascending)
-- Multiple columns: sort by first, then break ties with second
-- NULL values sort LAST in ASC, FIRST in DESC (PostgreSQL default)
-- =============================================================================

-- Sort courses by price lowest to highest (ASC is default)
SELECT title, price
FROM courses
ORDER BY price ASC;

-- Sort by price highest to lowest
SELECT title, price
FROM courses
ORDER BY price DESC;

-- Sort alphabetically by category, then by price within each category
SELECT title, category, price
FROM courses
ORDER BY category ASC, price DESC;

-- Sort students by join date — newest first
SELECT first_name, last_name, joined_date
FROM students
ORDER BY joined_date DESC;

-- Combine WHERE and ORDER BY
SELECT title, price, level
FROM courses
WHERE published = TRUE
ORDER BY level ASC, price ASC;


-- =============================================================================
-- SECTION 7 — LIMIT and Pagination
-- =============================================================================
-- LIMIT n             → return only the first n rows
-- OFFSET n            → skip the first n rows
-- Together they implement pagination
--
-- MySQL / PostgreSQL: LIMIT / OFFSET
-- SQL Server:         FETCH FIRST n ROWS ONLY / OFFSET n ROWS FETCH NEXT n ROWS ONLY
-- Oracle:             ROWNUM or FETCH FIRST
-- =============================================================================

-- Top 5 most expensive courses
SELECT title, price
FROM courses
ORDER BY price DESC
LIMIT 5;

-- Pagination example — page size = 3 rows
-- Page 1 (rows 1-3)
SELECT title, price
FROM courses
ORDER BY price DESC
LIMIT 3 OFFSET 0;

-- Page 2 (rows 4-6)
SELECT title, price
FROM courses
ORDER BY price DESC
LIMIT 3 OFFSET 3;

-- Page 3 (rows 7-9)
SELECT title, price
FROM courses
ORDER BY price DESC
LIMIT 3 OFFSET 6;

-- Formula: OFFSET = (page_number - 1) * page_size

-- Most recently enrolled (last 5 enrollments)
SELECT student_id, course_id, enrolled_date
FROM enrollments
ORDER BY enrolled_date DESC
LIMIT 5;


-- =============================================================================
-- SECTION 8 — DISTINCT Keyword
-- =============================================================================
-- DISTINCT removes duplicate rows from the result set.
-- Applied AFTER the SELECT — duplicates are eliminated from the output.
-- =============================================================================

-- All unique countries students come from
SELECT DISTINCT country
FROM students
ORDER BY country;

-- All unique categories in the course catalogue
SELECT DISTINCT category
FROM courses
ORDER BY category;

-- Distinct combination of country + active status
SELECT DISTINCT country, is_active
FROM students
ORDER BY country, is_active;

-- How many students are from each country? (preview of GROUP BY — Part 2)
-- First, see the duplicates:
SELECT country FROM students ORDER BY country;
-- Then with DISTINCT:
SELECT DISTINCT country FROM students ORDER BY country;

-- DISTINCT vs GROUP BY — DISTINCT is for deduplication,
-- GROUP BY is for aggregation (more on this in Part 2)


-- =============================================================================
-- SECTION 9 — Putting It All Together (Realistic Queries)
-- =============================================================================

-- "Show me all published Frontend courses priced under $70,
--  sorted cheapest first"
SELECT title, category, price, level
FROM courses
WHERE category = 'Frontend'
AND   published = TRUE
AND   price < 70.00
ORDER BY price ASC;

-- "Find active students from the USA who joined in 2024,
--  showing their name and join date, newest first, limit 5"
SELECT first_name, last_name, joined_date
FROM students
WHERE country = 'USA'
AND   is_active = TRUE
AND   joined_date BETWEEN '2024-01-01' AND '2024-12-31'
ORDER BY joined_date DESC
LIMIT 5;

-- "Search for courses with 'Python' or 'Machine' in the title"
SELECT title, price, level
FROM courses
WHERE title LIKE '%Python%'
OR    title LIKE '%Machine%';

-- "Show unique levels of courses that cost over $50"
SELECT DISTINCT level
FROM courses
WHERE price > 50.00
ORDER BY level;
