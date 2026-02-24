-- Exercise 02 SOLUTION: Sorting, LIMIT, and DISTINCT

-- Query 1: Alphabetical customers — primary sort last_name, secondary first_name
SELECT first_name, last_name
FROM customers
ORDER BY last_name ASC, first_name ASC;

-- Query 2: Most expensive products first
SELECT product_name, price
FROM products
ORDER BY price DESC;

-- Query 3: Cheapest 5 — ORDER BY price ASC then LIMIT
SELECT product_name, price
FROM products
ORDER BY price ASC
LIMIT 5;

-- Query 4: Page 2 of products (rows 6–10)
-- OFFSET = (page_number - 1) * page_size = (2-1) * 5 = 5
SELECT product_id, product_name, price
FROM products
ORDER BY product_id ASC
LIMIT 5 OFFSET 5;

-- Query 5: Unique categories — DISTINCT eliminates duplicates before returning results
SELECT DISTINCT category
FROM products
ORDER BY category ASC;

-- Query 6: Active customers' cities, alphabetically
SELECT DISTINCT city
FROM customers
WHERE is_active = TRUE
ORDER BY city ASC;

-- Query 7: Top 3 Electronics by price
SELECT product_name, price
FROM products
WHERE category = 'Electronics'
ORDER BY price DESC
LIMIT 3;

-- Query 8: 5 most recent orders
SELECT order_id, customer_id, order_date
FROM orders
ORDER BY order_date DESC
LIMIT 5;
