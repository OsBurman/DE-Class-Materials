-- =============================================================
-- Exercise 02: ALTER and DROP Statements
-- File: solution/setup.sql
-- =============================================================

DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS authors;

CREATE TABLE authors (
    author_id   SERIAL       PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL,
    last_name   VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    birth_year  INTEGER      CHECK (birth_year BETWEEN 1800 AND 2010),
    nationality VARCHAR(50)  DEFAULT 'Unknown'
);

CREATE TABLE genres (
    genre_id   SERIAL      PRIMARY KEY,
    genre_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE books (
    book_id        SERIAL        PRIMARY KEY,
    title          VARCHAR(200)  NOT NULL,
    isbn           CHAR(13)      UNIQUE NOT NULL,
    author_id      INTEGER       NOT NULL REFERENCES authors(author_id),
    genre_id       INTEGER       REFERENCES genres(genre_id),
    published_year INTEGER       CHECK (published_year BETWEEN 1000 AND 2100),
    price          NUMERIC(10,2) CHECK (price > 0),
    stock          INTEGER       DEFAULT 0 CHECK (stock >= 0)
);

CREATE TABLE members (
    member_id       SERIAL       PRIMARY KEY,
    username        VARCHAR(30)  UNIQUE NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    joined_date     DATE         DEFAULT CURRENT_DATE,
    membership_type VARCHAR(20)  DEFAULT 'standard'
                                 CHECK (membership_type IN ('standard', 'premium', 'student'))
);

INSERT INTO authors (first_name, last_name, email, birth_year, nationality)
VALUES ('George',  'Orwell',   'gorwell@lib.com',   1903, 'British'),
       ('Toni',    'Morrison', 'tmorrison@lib.com', 1931, 'American'),
       ('Haruki',  'Murakami', 'hmurakami@lib.com', 1949, 'Japanese');

INSERT INTO genres (genre_name)
VALUES ('Fiction'), ('Non-Fiction'), ('Science Fiction');

INSERT INTO books (title, isbn, author_id, genre_id, published_year, price, stock)
VALUES ('1984',           '9780451524935', 1, 1, 1949, 12.99, 10),
       ('Animal Farm',    '9780451526342', 1, 1, 1945, 8.99,  5),
       ('Beloved',        '9781400033416', 2, 1, 1987, 14.99, 7),
       ('Norwegian Wood', '9780375704024', 3, 1, 1987, 13.99, 4);

INSERT INTO members (username, email, membership_type)
VALUES ('jdoe',   'jdoe@email.com',   'standard'),
       ('asmith', 'asmith@email.com', 'premium'),
       ('lwang',  'lwang@email.com',  'student');
