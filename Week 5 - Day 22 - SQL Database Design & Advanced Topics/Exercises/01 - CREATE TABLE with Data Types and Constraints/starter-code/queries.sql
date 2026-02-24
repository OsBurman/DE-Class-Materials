-- =============================================================
-- Exercise 01: CREATE TABLE with Data Types and Constraints
-- File: starter-code/queries.sql
-- Instructions: Run setup.sql first, then complete each TODO below.
-- =============================================================

-- TODO 1: Create the `authors` table with these columns:
--   - author_id   SERIAL           PRIMARY KEY
--   - first_name  VARCHAR(50)      NOT NULL
--   - last_name   VARCHAR(50)      NOT NULL
--   - email       VARCHAR(100)     UNIQUE  NOT NULL
--   - birth_year  INTEGER          CHECK between 1800 and 2010
--   - nationality VARCHAR(50)      DEFAULT 'Unknown'


-- TODO 2: Create the `genres` table with these columns:
--   - genre_id   SERIAL       PRIMARY KEY
--   - genre_name VARCHAR(50)  UNIQUE  NOT NULL


-- TODO 3: Create the `books` table with these columns:
--   - book_id        SERIAL           PRIMARY KEY
--   - title          VARCHAR(200)     NOT NULL
--   - isbn           CHAR(13)         UNIQUE  NOT NULL
--   - author_id      INTEGER          NOT NULL  FOREIGN KEY → authors(author_id)
--   - genre_id       INTEGER          FOREIGN KEY → genres(genre_id)
--   - published_year INTEGER          CHECK between 1000 and 2100
--   - price          NUMERIC(10,2)    CHECK > 0
--   - stock          INTEGER          DEFAULT 0  CHECK >= 0


-- TODO 4: Create the `members` table with these columns:
--   - member_id       SERIAL        PRIMARY KEY
--   - username        VARCHAR(30)   UNIQUE  NOT NULL
--   - email           VARCHAR(100)  UNIQUE  NOT NULL
--   - joined_date     DATE          DEFAULT CURRENT_DATE
--   - membership_type VARCHAR(20)   CHECK IN ('standard','premium','student')
--                                   DEFAULT 'standard'


-- TODO 5: Verify the books table structure.
-- Write a SELECT against information_schema.columns that returns
-- the column_name and data_type for every column in 'books'.
