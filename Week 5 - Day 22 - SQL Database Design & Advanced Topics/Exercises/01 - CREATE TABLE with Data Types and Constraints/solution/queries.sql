-- =============================================================
-- Exercise 01: CREATE TABLE with Data Types and Constraints
-- File: solution/queries.sql
-- =============================================================

-- ---------------------------------------------------------------
-- TODO 1: Create the `authors` table
-- ---------------------------------------------------------------
CREATE TABLE authors (
    author_id   SERIAL       PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL,
    last_name   VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    birth_year  INTEGER      CHECK (birth_year BETWEEN 1800 AND 2010),
    nationality VARCHAR(50)  DEFAULT 'Unknown'
);

-- ---------------------------------------------------------------
-- TODO 2: Create the `genres` table
-- ---------------------------------------------------------------
CREATE TABLE genres (
    genre_id   SERIAL      PRIMARY KEY,
    genre_name VARCHAR(50) UNIQUE NOT NULL
);

-- ---------------------------------------------------------------
-- TODO 3: Create the `books` table
-- ---------------------------------------------------------------
CREATE TABLE books (
    book_id        SERIAL        PRIMARY KEY,
    title          VARCHAR(200)  NOT NULL,
    isbn           CHAR(13)      UNIQUE NOT NULL,
    author_id      INTEGER       NOT NULL
                                 REFERENCES authors(author_id),
    genre_id       INTEGER       REFERENCES genres(genre_id),
    published_year INTEGER       CHECK (published_year BETWEEN 1000 AND 2100),
    price          NUMERIC(10,2) CHECK (price > 0),
    stock          INTEGER       DEFAULT 0 CHECK (stock >= 0)
);

-- ---------------------------------------------------------------
-- TODO 4: Create the `members` table
-- ---------------------------------------------------------------
CREATE TABLE members (
    member_id       SERIAL       PRIMARY KEY,
    username        VARCHAR(30)  UNIQUE NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    joined_date     DATE         DEFAULT CURRENT_DATE,
    membership_type VARCHAR(20)  DEFAULT 'standard'
                                 CHECK (membership_type IN ('standard', 'premium', 'student'))
);

-- ---------------------------------------------------------------
-- TODO 5: Verify the books table structure
-- ---------------------------------------------------------------
SELECT column_name, data_type
FROM   information_schema.columns
WHERE  table_name = 'books'
ORDER  BY ordinal_position;

-- Expected result:
-- column_name    | data_type
-- ---------------+------------------
-- book_id        | integer
-- title          | character varying
-- isbn           | character
-- author_id      | integer
-- genre_id       | integer
-- published_year | integer
-- price          | numeric
-- stock          | integer
