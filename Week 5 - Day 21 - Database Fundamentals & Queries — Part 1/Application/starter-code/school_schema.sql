-- =============================================================
-- school_schema.sql
-- School Database Schema — Day 21: Database Fundamentals
-- Run this file FIRST, then sample_data.sql, then write queries.sql
-- =============================================================

-- Drop tables in reverse dependency order (safe to re-run)
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS instructors;
DROP TABLE IF EXISTS majors;

-- -------------------------------------------------------------
-- MAJORS — academic departments / programs
-- -------------------------------------------------------------
CREATE TABLE majors (
    major_id    INTEGER PRIMARY KEY,
    major_name  VARCHAR(100) NOT NULL UNIQUE,
    department  VARCHAR(100) NOT NULL,
    total_credits_required INTEGER NOT NULL DEFAULT 120
);

-- -------------------------------------------------------------
-- INSTRUCTORS — teaching staff
-- -------------------------------------------------------------
CREATE TABLE instructors (
    instructor_id   INTEGER PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    hire_date       DATE         NOT NULL,
    salary          DECIMAL(10,2),
    department      VARCHAR(100)
);

-- -------------------------------------------------------------
-- STUDENTS — enrolled learners
-- -------------------------------------------------------------
CREATE TABLE students (
    student_id  INTEGER PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL,
    last_name   VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    dob         DATE,
    enroll_date DATE         NOT NULL,
    major_id    INTEGER REFERENCES majors(major_id),  -- nullable: undeclared major
    gpa         DECIMAL(3,2) CHECK (gpa >= 0.0 AND gpa <= 4.0)
);

-- -------------------------------------------------------------
-- COURSES — available classes
-- -------------------------------------------------------------
CREATE TABLE courses (
    course_id       INTEGER PRIMARY KEY,
    course_code     VARCHAR(10)  NOT NULL UNIQUE,
    course_name     VARCHAR(150) NOT NULL,
    credits         INTEGER      NOT NULL CHECK (credits BETWEEN 1 AND 6),
    instructor_id   INTEGER REFERENCES instructors(instructor_id),
    max_enrollment  INTEGER      NOT NULL DEFAULT 30,
    semester        VARCHAR(20)  NOT NULL  -- e.g. 'Fall 2024'
);

-- -------------------------------------------------------------
-- ENROLLMENTS — junction table (student ↔ course)
-- -------------------------------------------------------------
CREATE TABLE enrollments (
    enrollment_id   INTEGER PRIMARY KEY,
    student_id      INTEGER NOT NULL REFERENCES students(student_id),
    course_id       INTEGER NOT NULL REFERENCES courses(course_id),
    enroll_date     DATE    NOT NULL,
    grade           CHAR(1) CHECK (grade IN ('A','B','C','D','F')),  -- NULL = in progress
    UNIQUE (student_id, course_id)  -- a student can't enroll in the same course twice
);
