-- =============================================================
-- Exercise 04: INSERT, UPDATE, and DELETE
-- File: solution/queries.sql
-- =============================================================

-- ---------------------------------------------------------------
-- Part A — INSERT
-- ---------------------------------------------------------------

-- TODO 1: Insert new author
INSERT INTO authors (first_name, last_name, email)
VALUES ('F. Scott', 'Fitzgerald', 'fscott@lib.com');

-- TODO 2: Insert new publisher
INSERT INTO publishers (publisher_name, city)
VALUES ('Scribner', 'New York');

-- TODO 3: Insert The Great Gatsby using subqueries for FK IDs
INSERT INTO books (title, author_id, publisher_id, published_year, price, stock)
VALUES (
    'The Great Gatsby',
    (SELECT author_id    FROM authors    WHERE email = 'fscott@lib.com'),
    (SELECT publisher_id FROM publishers WHERE publisher_name = 'Scribner'),
    1925,
    11.99,
    8
);

-- TODO 4: Insert two new members
INSERT INTO members (first_name, last_name, email, city, country, membership_type)
VALUES ('Maria', 'Garcia', 'mgarcia@email.com', 'Madrid',  'Spain',  'premium'),
       ('Liam',  'Chen',   'lchen@email.com',   'Toronto', 'Canada', 'student');

-- TODO 5: Insert loan for Maria Garcia borrowing The Great Gatsby
INSERT INTO loans (member_id, book_id, loan_date)
VALUES (
    (SELECT member_id FROM members WHERE email = 'mgarcia@email.com'),
    (SELECT book_id   FROM books   WHERE title = 'The Great Gatsby'),
    CURRENT_DATE
);

-- Verify Part A
SELECT first_name, last_name FROM authors ORDER BY author_id;

-- ---------------------------------------------------------------
-- Part B — UPDATE
-- ---------------------------------------------------------------

-- TODO 6: Update Maria Garcia's membership_type to 'standard'
UPDATE members
SET    membership_type = 'standard'
WHERE  email = 'mgarcia@email.com';

-- TODO 7: Update the price of 1984 to 15.99
UPDATE books
SET    price = 15.99
WHERE  title = '1984';

-- Verify
SELECT title, price FROM books WHERE title = '1984';
-- Expected: 15.99

-- TODO 8: Increase stock by 5 for books published before 2000
UPDATE books
SET    stock = stock + 5
WHERE  published_year < 2000;

-- Verify
SELECT title, published_year, stock FROM books ORDER BY published_year;

-- TODO 9: Update return_date on Maria's loan to CURRENT_DATE + 14 days
UPDATE loans
SET    return_date = CURRENT_DATE + INTERVAL '14 days'
WHERE  member_id = (SELECT member_id FROM members WHERE email = 'mgarcia@email.com')
  AND  return_date IS NULL;

-- ---------------------------------------------------------------
-- Part C — DELETE
-- ---------------------------------------------------------------

-- TODO 10: Insert a loan for Liam Chen, then delete it
INSERT INTO loans (member_id, book_id, loan_date)
VALUES (
    (SELECT member_id FROM members WHERE email = 'lchen@email.com'),
    (SELECT book_id   FROM books   WHERE title = 'Animal Farm'),
    CURRENT_DATE
);

-- Confirm it exists
SELECT * FROM loans
WHERE  member_id = (SELECT member_id FROM members WHERE email = 'lchen@email.com');

-- Delete it
DELETE FROM loans
WHERE  member_id = (SELECT member_id FROM members WHERE email = 'lchen@email.com');

-- Confirm it's gone
SELECT * FROM loans
WHERE  member_id = (SELECT member_id FROM members WHERE email = 'lchen@email.com');

-- TODO 11: Check for books with stock = 0, update one if needed, then delete
SELECT book_id, title, stock FROM books WHERE stock = 0;

-- If none found, set one to 0:
UPDATE books SET stock = 0 WHERE title = 'Norwegian Wood';

-- Delete books with stock = 0
DELETE FROM books WHERE stock = 0;

-- Verify
SELECT title, stock FROM books ORDER BY title;

-- TODO 12: Attempt to delete an author who still has books
-- (This will fail with a FK violation)
-- DELETE FROM authors WHERE last_name = 'Orwell';
-- ERROR: update or delete on table "authors" violates foreign key
--        constraint "books_author_id_fkey" on table "books"

/*
  ON DELETE CASCADE vs ON DELETE RESTRICT:

  ON DELETE CASCADE (defined on the FK in books):
    REFERENCES authors(author_id) ON DELETE CASCADE
    → Deleting an author automatically deletes all their books.
    → Use when child rows have no meaning without the parent.

  ON DELETE RESTRICT (the default):
    → Prevents deletion of an author if any book references them.
    → Protects data integrity by requiring explicit child cleanup first.
    → Safer for most business data where child records are valuable.
*/
