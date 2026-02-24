-- =============================================================
-- Exercise 03: Normalization and Schema Design
-- File: solution/queries.sql
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Normalization violations identified
-- ---------------------------------------------------------------

/*
  TODO 1a — 1NF violation:
  The column `book_genres` stores multiple values in a single cell,
  e.g. "Fiction, Dystopia". A column must hold only atomic (single)
  values in 1NF. This violates 1NF because you cannot query
  individual genres without string manipulation.

  TODO 1b — Partial dependencies:
  library_old has a single-column PK (loan_id), so partial
  dependencies are not possible by definition. However, the data
  effectively embeds entities that have their own natural keys:
    - member_email → member_name, member_city, member_country
    - author_email → author_first, author_last
    - publisher_name → publisher_city

  TODO 1c — Transitive dependencies (3NF violations):
  1. loan_id → author_email → author_first, author_last
     (author attributes depend on author_email, not on loan_id)
  2. loan_id → publisher_name → publisher_city
     (publisher_city depends on publisher_name, not on loan_id)
  3. loan_id → member_email → member_city, member_country
     (member location depends on member_email, not on loan_id)
*/

-- ---------------------------------------------------------------
-- Part 2 — Normalized schema (3NF)
-- ---------------------------------------------------------------

DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS book_genres;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS publishers;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS members;

-- Members (no transitive dependencies)
CREATE TABLE members (
    member_id  SERIAL       PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    city       VARCHAR(50),
    country    VARCHAR(50)
);

-- Authors
CREATE TABLE authors (
    author_id  SERIAL       PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) UNIQUE
);

-- Publishers
CREATE TABLE publishers (
    publisher_id   SERIAL      PRIMARY KEY,
    publisher_name VARCHAR(100) UNIQUE NOT NULL,
    city           VARCHAR(50)
);

-- Genres lookup
CREATE TABLE genres (
    genre_id   SERIAL      PRIMARY KEY,
    genre_name VARCHAR(50) UNIQUE NOT NULL
);

-- Books (FK → authors, publishers)
CREATE TABLE books (
    book_id      SERIAL       PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    author_id    INTEGER      NOT NULL REFERENCES authors(author_id),
    publisher_id INTEGER      REFERENCES publishers(publisher_id)
);

-- Book-genre junction (many-to-many)
CREATE TABLE book_genres (
    book_id  INTEGER NOT NULL REFERENCES books(book_id),
    genre_id INTEGER NOT NULL REFERENCES genres(genre_id),
    PRIMARY KEY (book_id, genre_id)
);

-- Loans (FK → members, books)
CREATE TABLE loans (
    loan_id     SERIAL  PRIMARY KEY,
    member_id   INTEGER NOT NULL REFERENCES members(member_id),
    book_id     INTEGER NOT NULL REFERENCES books(book_id),
    loan_date   DATE    NOT NULL DEFAULT CURRENT_DATE,
    return_date DATE
);

-- ---------------------------------------------------------------
-- Part 3 — Populate and verify
-- ---------------------------------------------------------------

-- Members
INSERT INTO members (first_name, last_name, email, city, country)
VALUES ('Jane',  'Doe',   'jane@email.com', 'New York', 'USA'),
       ('Alex',  'Smith', 'alex@email.com', 'London',   'UK');

-- Authors
INSERT INTO authors (first_name, last_name, email)
VALUES ('George', 'Orwell',   'gorwell@pub.com'),
       ('Toni',   'Morrison', 'tmorrison@pub.com'),
       ('Haruki', 'Murakami', 'hmurakami@pub.com');

-- Publishers
INSERT INTO publishers (publisher_name, city)
VALUES ('Secker & Warburg', 'London'),
       ('Alfred A. Knopf',  'New York'),
       ('Kodansha',          'Tokyo');

-- Books
INSERT INTO books (title, author_id, publisher_id)
VALUES ('1984',           1, 1),
       ('Beloved',        2, 2),
       ('Norwegian Wood', 3, 3);

-- Genres
INSERT INTO genres (genre_name)
VALUES ('Fiction'), ('Dystopia'), ('Historical');

-- Book-genre assignments (1984 gets two genres)
INSERT INTO book_genres (book_id, genre_id) VALUES (1, 1), (1, 2);
INSERT INTO book_genres (book_id, genre_id) VALUES (2, 1), (2, 3);
INSERT INTO book_genres (book_id, genre_id) VALUES (3, 1);

-- Loans
INSERT INTO loans (member_id, book_id, loan_date, return_date)
VALUES (1, 1, '2024-01-05', '2024-01-19'),
       (2, 2, '2024-01-10', '2024-01-24'),
       (2, 3, '2024-03-01', NULL);

-- Verification query: member name, book title, loan date
SELECT m.first_name || ' ' || m.last_name AS member_name,
       b.title                             AS book_title,
       l.loan_date
FROM   loans    l
JOIN   members  m ON m.member_id = l.member_id
JOIN   books    b ON b.book_id   = l.book_id
ORDER  BY l.loan_date;

-- Expected:
-- member_name | book_title      | loan_date
-- ------------+-----------------+------------
-- Jane Doe    | 1984            | 2024-01-05
-- Alex Smith  | Beloved         | 2024-01-10
-- Alex Smith  | Norwegian Wood  | 2024-03-01
