-- =============================================================
-- Exercise 03: Normalization and Schema Design
-- File: solution/setup.sql
-- =============================================================

DROP TABLE IF EXISTS library_old;

CREATE TABLE library_old (
    loan_id        INTEGER,
    member_name    VARCHAR(100),
    member_email   VARCHAR(100),
    member_city    VARCHAR(50),
    member_country VARCHAR(50),
    book_title     VARCHAR(200),
    book_genres    VARCHAR(200),
    author_first   VARCHAR(50),
    author_last    VARCHAR(50),
    author_email   VARCHAR(100),
    publisher_name VARCHAR(100),
    publisher_city VARCHAR(50),
    loan_date      DATE,
    return_date    DATE
);

INSERT INTO library_old VALUES
(1,  'Jane Doe',    'jane@email.com',  'New York', 'USA', '1984',           'Fiction, Dystopia',      'George', 'Orwell',   'gorwell@pub.com',   'Secker & Warburg', 'London',   '2024-01-05', '2024-01-19'),
(2,  'Jane Doe',    'jane@email.com',  'New York', 'USA', 'Animal Farm',    'Fiction, Satire',        'George', 'Orwell',   'gorwell@pub.com',   'Secker & Warburg', 'London',   '2024-02-01', '2024-02-15'),
(3,  'Alex Smith',  'alex@email.com',  'London',   'UK',  'Beloved',        'Fiction, Historical',    'Toni',   'Morrison', 'tmorrison@pub.com', 'Alfred A. Knopf',  'New York', '2024-01-10', '2024-01-24'),
(4,  'Alex Smith',  'alex@email.com',  'London',   'UK',  'Norwegian Wood', 'Fiction, Romance',       'Haruki', 'Murakami', 'hmurakami@pub.com', 'Kodansha',         'Tokyo',    '2024-03-01', NULL),
(5,  'Jane Doe',    'jane@email.com',  'New York', 'USA', 'Kafka on Shore', 'Fiction, Magic Realism', 'Haruki', 'Murakami', 'hmurakami@pub.com', 'Kodansha',         'Tokyo',    '2024-03-10', NULL);
