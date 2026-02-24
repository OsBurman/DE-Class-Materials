-- Exercise 02: Sorting, LIMIT, and DISTINCT
-- Run setup.sql first to create and populate the tables.

-- Query 1: Customers ordered alphabetically by last_name, then first_name
-- TODO: SELECT first_name, last_name FROM customers with ORDER BY last_name ASC, first_name ASC


-- Query 2: Products ordered by price descending (most expensive first)
-- TODO: SELECT product_name, price FROM products ordered by price DESC


-- Query 3: The 5 cheapest products
-- TODO: Order products by price ASC and use LIMIT to return only 5 rows


-- Query 4: Products 6–10 by product_id (page 2, page size 5)
-- TODO: Use LIMIT 5 OFFSET 5 with ORDER BY product_id ASC


-- Query 5: Distinct product categories
-- TODO: Use SELECT DISTINCT on the category column from products


-- Query 6: Distinct cities for active customers, sorted A–Z
-- TODO: Use SELECT DISTINCT on city, filter is_active = TRUE, ORDER BY city ASC


-- Query 7: Top 3 most expensive Electronics products
-- TODO: Filter by category = 'Electronics', order by price DESC, LIMIT 3


-- Query 8: 5 most recent orders
-- TODO: Order orders by order_date DESC and LIMIT to 5 rows
