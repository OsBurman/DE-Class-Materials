-- Exercise 01 SOLUTION: SELECT, WHERE, and Filtering Operators
-- Run setup.sql first.

-- Query 1: All columns from customers
SELECT * FROM customers;

-- Query 2: Specific columns only
SELECT first_name, last_name, email
FROM customers;

-- Query 3: Active customers — simple equality filter
SELECT *
FROM customers
WHERE is_active = TRUE;

-- Query 4: Products under $50
SELECT product_name, price
FROM products
WHERE price < 50;

-- Query 5: Electronics OR Clothing — IN is cleaner than multiple ORs
SELECT product_name, price, category
FROM products
WHERE category IN ('Electronics', 'Clothing');

-- Query 6: Mid-range price — BETWEEN is inclusive on both ends
SELECT product_name, price
FROM products
WHERE price BETWEEN 20 AND 100;

-- Query 7: Last name starts with 'S' — % matches any sequence of characters
SELECT first_name, last_name
FROM customers
WHERE last_name LIKE 'S%';

-- Query 8: Email ending with '@example.com'
SELECT first_name, last_name, email
FROM customers
WHERE email LIKE '%@example.com';

-- Query 9: Electronics AND under $200 — both conditions must be true
SELECT product_name, price, category
FROM products
WHERE category = 'Electronics'
  AND price < 200;

-- Query 10: All products except Books — NOT IN excludes listed values
SELECT product_name, category
FROM products
WHERE category NOT IN ('Books');
