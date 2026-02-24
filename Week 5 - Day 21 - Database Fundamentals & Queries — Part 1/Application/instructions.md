# Day 21 Application — Database Fundamentals: School Query Challenge

## Overview

You have a pre-built **school database** schema. Your job is to write a series of SQL queries of increasing complexity — covering `SELECT`, aggregates, `JOIN`, subqueries, and `CASE`.

---

## Learning Goals

- Write `SELECT` with `WHERE`, `ORDER BY`, `LIMIT`
- Use aggregate functions: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`
- Group with `GROUP BY` and filter with `HAVING`
- Perform `INNER`, `LEFT`, `RIGHT`, and `FULL OUTER JOIN`
- Write correlated and non-correlated subqueries
- Use `CASE` expressions

---

## Prerequisites

- SQLite, PostgreSQL, or MySQL installed — **OR** use [SQLiteOnline.com](https://sqliteonline.com/)
- Run `school_schema.sql` first to create the database, then `sample_data.sql` to seed it
- Write your answers in `queries.sql`

---

## Database Schema

```
students(id, name, email, enrollment_year, major_id)
majors(id, name, department)
courses(id, name, credits, instructor_id)
instructors(id, name, email, department)
enrollments(id, student_id, course_id, grade, semester)
```

---

## Part 1 — Basic SELECT (Queries 1–5)

**Q1** — List all students sorted by name ascending.  
**Q2** — Find all courses with more than 3 credits.  
**Q3** — List the top 5 students by enrollment year (most recent first).  
**Q4** — Find all students whose email ends in `@gmail.com`.  
**Q5** — Count the total number of enrollments.

---

## Part 2 — Aggregates & GROUP BY (Queries 6–10)

**Q6** — Find the average number of credits per course.  
**Q7** — Count how many students are in each major. Sort by count descending.  
**Q8** — Find all majors that have more than 10 students (use `HAVING`).  
**Q9** — Show each student's name and their GPA (avg of `grade` in enrollments, where A=4, B=3, C=2, D=1, F=0).  
**Q10** — Find the instructor who has the most courses assigned.

---

## Part 3 — JOINs (Queries 11–15)

**Q11** — `INNER JOIN`: List each student's name with their major name.  
**Q12** — `LEFT JOIN`: List all students, and their enrolled courses (if any). Include students with no enrollments.  
**Q13** — `INNER JOIN` with 3 tables: List student name, course name, and grade for all enrollments.  
**Q14** — Find courses that have never been enrolled in (hint: `LEFT JOIN` + `WHERE IS NULL`).  
**Q15** — List all instructors and the count of students in their courses (use JOINs and GROUP BY).

---

## Part 4 — Subqueries & CASE (Queries 16–20)

**Q16** — Find students who are enrolled in more courses than the average number of enrollments per student.  
**Q17** — Find the course with the highest average grade.  
**Q18** — Use a `CASE` expression to label each enrollment grade as 'Excellent' (A), 'Good' (B), 'Average' (C), or 'Below Average' (D/F).  
**Q19** — Using a subquery, list students who have not enrolled in any course.  
**Q20** — Using a correlated subquery, list each student with the name of their most recently enrolled course.

---

## Submission Checklist

- [ ] 20 queries written in `queries.sql`
- [ ] All queries run without errors
- [ ] Q9 correctly calculates GPA
- [ ] Q12 uses LEFT JOIN and returns students with no enrollments
- [ ] Q18 uses CASE expression with at least 4 branches
