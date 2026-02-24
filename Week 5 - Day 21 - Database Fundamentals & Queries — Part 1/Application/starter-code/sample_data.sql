-- =============================================================
-- sample_data.sql
-- Seed data for the School Database
-- Run AFTER school_schema.sql
-- =============================================================

-- -------------------------
-- MAJORS (5 rows)
-- -------------------------
INSERT INTO majors (major_id, major_name, department, total_credits_required) VALUES
(1, 'Computer Science',        'Engineering',          128),
(2, 'Data Science',            'Engineering',          124),
(3, 'Business Administration', 'Business',             120),
(4, 'Psychology',              'Social Sciences',      120),
(5, 'Graphic Design',          'Arts & Humanities',    116);

-- -------------------------
-- INSTRUCTORS (5 rows)
-- -------------------------
INSERT INTO instructors (instructor_id, first_name, last_name, email, hire_date, salary, department) VALUES
(1, 'Alice',   'Nguyen',    'a.nguyen@school.edu',    '2018-08-15', 85000.00, 'Engineering'),
(2, 'Robert',  'Kim',       'r.kim@school.edu',       '2015-01-10', 92000.00, 'Engineering'),
(3, 'Sandra',  'Patel',     's.patel@school.edu',     '2020-08-20', 78000.00, 'Business'),
(4, 'Marcus',  'Thompson',  'm.thompson@school.edu',  '2019-03-05', 80000.00, 'Social Sciences'),
(5, 'Julia',   'Reyes',     'j.reyes@school.edu',     '2021-08-18', 75000.00, 'Arts & Humanities');

-- -------------------------
-- STUDENTS (22 rows)
-- Intentional variety:
--   - Some students have NULL major_id (undeclared)
--   - Students 21 & 22 have no enrollments (for LEFT JOIN practice)
-- -------------------------
INSERT INTO students (student_id, first_name, last_name, email, dob, enroll_date, major_id, gpa) VALUES
(1,  'Emma',    'Wilson',    'emma.w@student.edu',    '2002-03-14', '2021-09-01', 1, 3.85),
(2,  'Liam',    'Torres',    'liam.t@student.edu',    '2001-07-22', '2020-09-01', 2, 3.42),
(3,  'Olivia',  'Chen',      'olivia.c@student.edu',  '2003-01-08', '2022-09-01', 1, 3.92),
(4,  'Noah',    'Johnson',   'noah.j@student.edu',    '2002-11-30', '2021-09-01', 3, 2.78),
(5,  'Ava',     'Martinez',  'ava.m@student.edu',     '2001-05-19', '2020-09-01', 2, 3.67),
(6,  'Ethan',   'Brown',     'ethan.b@student.edu',   '2003-08-03', '2022-09-01', 4, 3.10),
(7,  'Sophia',  'Davis',     'sophia.d@student.edu',  '2002-12-25', '2021-09-01', 5, 3.55),
(8,  'James',   'Lee',       'james.l@student.edu',   '2001-02-14', '2020-09-01', 1, 2.95),
(9,  'Isabella','Garcia',    'isabella.g@student.edu','2003-06-07', '2022-09-01', 3, 3.20),
(10, 'William', 'Anderson',  'william.a@student.edu', '2002-09-16', '2021-09-01', 2, 3.80),
(11, 'Mia',     'Taylor',    'mia.t@student.edu',     '2001-04-11', '2020-09-01', 4, 3.45),
(12, 'Oliver',  'White',     'oliver.w@student.edu',  '2003-03-28', '2022-09-01', 5, 2.60),
(13, 'Charlotte','Harris',   'charlotte.h@student.edu','2002-07-05','2021-09-01', 1, 3.98),
(14, 'Benjamin','Martin',    'ben.m@student.edu',     '2001-10-17', '2020-09-01', 3, 3.15),
(15, 'Amelia',  'Jackson',   'amelia.j@student.edu',  '2003-02-22', '2022-09-01', 2, 3.72),
(16, 'Lucas',   'Thompson',  'lucas.t@student.edu',   '2002-05-30', '2021-09-01', NULL, 2.40),  -- undeclared
(17, 'Harper',  'Robinson',  'harper.r@student.edu',  '2001-08-08', '2020-09-01', NULL, 1.95),  -- undeclared
(18, 'Henry',   'Clark',     'henry.c@student.edu',   '2003-11-12', '2022-09-01', 4, 3.30),
(19, 'Evelyn',  'Lewis',     'evelyn.l@student.edu',  '2002-01-25', '2021-09-01', 5, 3.88),
(20, 'Alexander','Walker',   'alex.w@student.edu',    '2001-09-03', '2020-09-01', 1, 3.05),
(21, 'Luna',    'Hall',      'luna.h@student.edu',    '2003-04-18', '2022-09-01', 3, NULL),     -- no enrollments yet
(22, 'Jack',    'Young',     'jack.y@student.edu',    '2002-06-14', '2022-09-01', NULL, NULL);  -- no enrollments, undeclared

-- -------------------------
-- COURSES (10 rows)
-- -------------------------
INSERT INTO courses (course_id, course_code, course_name, credits, instructor_id, max_enrollment, semester) VALUES
(1,  'CS101',  'Introduction to Programming',     3, 1, 30, 'Fall 2024'),
(2,  'CS201',  'Data Structures & Algorithms',    4, 1, 25, 'Fall 2024'),
(3,  'CS301',  'Database Systems',                3, 2, 28, 'Fall 2024'),
(4,  'DS101',  'Introduction to Data Science',    3, 2, 30, 'Fall 2024'),
(5,  'DS201',  'Machine Learning Fundamentals',   4, 2, 20, 'Fall 2024'),
(6,  'BA101',  'Principles of Management',        3, 3, 35, 'Fall 2024'),
(7,  'BA201',  'Financial Accounting',            3, 3, 30, 'Fall 2024'),
(8,  'PSY101', 'Introduction to Psychology',      3, 4, 40, 'Fall 2024'),
(9,  'PSY201', 'Developmental Psychology',        3, 4, 30, 'Fall 2024'),
(10, 'GD101',  'Fundamentals of Design',          3, 5, 20, 'Fall 2024');

-- -------------------------
-- ENROLLMENTS (52 rows)
-- -------------------------
INSERT INTO enrollments (enrollment_id, student_id, course_id, enroll_date, grade) VALUES
-- Emma (CS major, high GPA)
(1,  1,  1,  '2024-08-25', 'A'),
(2,  1,  2,  '2024-08-25', 'A'),
(3,  1,  3,  '2024-08-25', 'B'),
-- Liam (Data Science)
(4,  2,  1,  '2024-08-25', 'B'),
(5,  2,  4,  '2024-08-25', 'A'),
(6,  2,  5,  '2024-08-25', 'A'),
-- Olivia (CS, highest GPA)
(7,  3,  1,  '2024-08-25', 'A'),
(8,  3,  2,  '2024-08-25', 'A'),
(9,  3,  3,  '2024-08-25', 'A'),
-- Noah (Business)
(10, 4,  6,  '2024-08-25', 'B'),
(11, 4,  7,  '2024-08-25', 'C'),
(12, 4,  1,  '2024-08-25', 'C'),
-- Ava (Data Science)
(13, 5,  4,  '2024-08-25', 'A'),
(14, 5,  5,  '2024-08-25', 'B'),
(15, 5,  3,  '2024-08-25', 'A'),
-- Ethan (Psychology)
(16, 6,  8,  '2024-08-25', 'B'),
(17, 6,  9,  '2024-08-25', 'A'),
-- Sophia (Graphic Design)
(18, 7,  10, '2024-08-25', 'A'),
(19, 7,  6,  '2024-08-25', 'B'),
-- James (CS, lower GPA)
(20, 8,  1,  '2024-08-25', 'C'),
(21, 8,  2,  '2024-08-25', 'D'),
(22, 8,  3,  '2024-08-25', 'C'),
-- Isabella (Business)
(23, 9,  6,  '2024-08-25', 'B'),
(24, 9,  7,  '2024-08-25', 'B'),
-- William (Data Science)
(25, 10, 4,  '2024-08-25', 'A'),
(26, 10, 5,  '2024-08-25', 'A'),
(27, 10, 1,  '2024-08-25', NULL),  -- currently enrolled, no grade yet
-- Mia (Psychology)
(28, 11, 8,  '2024-08-25', 'B'),
(29, 11, 9,  '2024-08-25', 'A'),
(30, 11, 6,  '2024-08-25', 'B'),
-- Oliver (Graphic Design)
(31, 12, 10, '2024-08-25', 'C'),
(32, 12, 6,  '2024-08-25', 'D'),
-- Charlotte (CS, top GPA)
(33, 13, 1,  '2024-08-25', 'A'),
(34, 13, 2,  '2024-08-25', 'A'),
(35, 13, 3,  '2024-08-25', 'A'),
-- Benjamin (Business)
(36, 14, 6,  '2024-08-25', 'B'),
(37, 14, 7,  '2024-08-25', 'B'),
-- Amelia (Data Science)
(38, 15, 4,  '2024-08-25', 'A'),
(39, 15, 5,  '2024-08-25', 'B'),
-- Lucas (undeclared)
(40, 16, 1,  '2024-08-25', 'D'),
(41, 16, 8,  '2024-08-25', 'C'),
-- Harper (undeclared, low GPA)
(42, 17, 1,  '2024-08-25', 'F'),
(43, 17, 6,  '2024-08-25', 'D'),
-- Henry (Psychology)
(44, 18, 8,  '2024-08-25', 'A'),
(45, 18, 9,  '2024-08-25', 'B'),
-- Evelyn (Graphic Design)
(46, 19, 10, '2024-08-25', 'A'),
(47, 19, 6,  '2024-08-25', 'A'),
-- Alexander (CS)
(48, 20, 1,  '2024-08-25', 'B'),
(49, 20, 2,  '2024-08-25', NULL),  -- currently enrolled
(50, 20, 3,  '2024-08-25', 'C'),
-- Cross-enrollment examples
(51, 6,  1,  '2024-08-25', 'B'),  -- Psychology student in CS
(52, 4,  8,  '2024-08-25', 'B');  -- Business student in Psychology
-- Note: Students 21 (Luna) and 22 (Jack) have NO enrollments — use them for LEFT JOIN queries
