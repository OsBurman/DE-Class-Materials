-- =============================================================
-- Exercise 05: Indexes and Query Optimization
-- File: solution/setup.sql  (identical to starter-code/setup.sql)
-- =============================================================

DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS book_genres;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS publishers;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS members;

CREATE TABLE members (
    member_id       SERIAL       PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    city            VARCHAR(50),
    country         VARCHAR(50),
    membership_type VARCHAR(20)  DEFAULT 'standard'
                                 CHECK (membership_type IN ('standard', 'premium', 'student'))
);

CREATE TABLE authors (
    author_id  SERIAL       PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) UNIQUE
);

CREATE TABLE publishers (
    publisher_id   SERIAL       PRIMARY KEY,
    publisher_name VARCHAR(100) UNIQUE NOT NULL,
    city           VARCHAR(50)
);

CREATE TABLE genres (
    genre_id   SERIAL      PRIMARY KEY,
    genre_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE books (
    book_id        SERIAL        PRIMARY KEY,
    title          VARCHAR(200)  NOT NULL,
    author_id      INTEGER       NOT NULL REFERENCES authors(author_id),
    publisher_id   INTEGER       REFERENCES publishers(publisher_id),
    published_year INTEGER,
    price          NUMERIC(10,2) CHECK (price > 0),
    stock          INTEGER       DEFAULT 0 CHECK (stock >= 0)
);

CREATE TABLE book_genres (
    book_id  INTEGER NOT NULL REFERENCES books(book_id),
    genre_id INTEGER NOT NULL REFERENCES genres(genre_id),
    PRIMARY KEY (book_id, genre_id)
);

CREATE TABLE loans (
    loan_id     SERIAL  PRIMARY KEY,
    member_id   INTEGER NOT NULL REFERENCES members(member_id),
    book_id     INTEGER NOT NULL REFERENCES books(book_id),
    loan_date   DATE    NOT NULL DEFAULT CURRENT_DATE,
    return_date DATE
);

INSERT INTO authors (first_name, last_name, email)
VALUES ('George',   'Orwell',     'gorwell@pub.com'),
       ('Toni',     'Morrison',   'tmorrison@pub.com'),
       ('Haruki',   'Murakami',   'hmurakami@pub.com'),
       ('F. Scott', 'Fitzgerald', 'fscott@pub.com'),
       ('Jane',     'Austen',     'jausten@pub.com');

INSERT INTO publishers (publisher_name, city)
VALUES ('Secker & Warburg', 'London'),
       ('Alfred A. Knopf',  'New York'),
       ('Kodansha',          'Tokyo'),
       ('Scribner',          'New York'),
       ('John Murray',       'London');

INSERT INTO genres (genre_name)
VALUES ('Fiction'), ('Dystopia'), ('Historical'), ('Romance'), ('Classic');

INSERT INTO members (first_name, last_name, email, city, country, membership_type)
VALUES ('Jane',  'Doe',    'jane@email.com',    'New York', 'USA',    'standard'),
       ('Alex',  'Smith',  'alex@email.com',    'London',   'UK',     'premium'),
       ('Maria', 'Garcia', 'mgarcia@email.com', 'Madrid',   'Spain',  'premium'),
       ('Liam',  'Chen',   'lchen@email.com',   'Toronto',  'Canada', 'student'),
       ('Sara',  'Nowak',  'snowak@email.com',  'Warsaw',   'Poland', 'standard');

INSERT INTO books (title, author_id, publisher_id, published_year, price, stock)
VALUES ('1984',               1, 1, 1949, 12.99, 10),
       ('Animal Farm',        1, 1, 1945,  8.99,  5),
       ('Beloved',            2, 2, 1987, 14.99,  7),
       ('Norwegian Wood',     3, 3, 1987, 13.99,  4),
       ('The Great Gatsby',   4, 4, 1925, 11.99,  8),
       ('Pride & Prejudice',  5, 5, 1813,  9.99, 12),
       ('Kafka on the Shore', 3, 3, 2002, 15.49,  6);

INSERT INTO book_genres VALUES (1,1),(1,2),(2,1),(3,1),(3,3),(4,1),(4,4),(5,1),(5,5),(6,1),(6,4),(6,5),(7,1);

INSERT INTO loans (member_id, book_id, loan_date, return_date)
VALUES (1, 1, '2024-01-05', '2024-01-19'),
       (1, 5, '2024-02-01', '2024-02-15'),
       (2, 3, '2024-01-10', '2024-01-24'),
       (2, 4, '2024-03-01', NULL),
       (3, 6, '2024-01-15', '2024-01-29'),
       (3, 2, '2024-02-10', NULL),
       (4, 7, '2024-03-05', NULL),
       (5, 1, '2024-03-08', NULL),
       (5, 3, '2024-03-12', NULL);
