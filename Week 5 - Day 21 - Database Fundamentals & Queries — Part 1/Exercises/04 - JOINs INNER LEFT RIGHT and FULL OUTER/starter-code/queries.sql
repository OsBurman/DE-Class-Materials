-- Exercise 04: JOINs — INNER, LEFT, RIGHT, and FULL OUTER
-- Run setup.sql first.

-- Query 1: Customers with orders — INNER JOIN (only matched rows)
-- TODO: JOIN orders ON orders.customer_id = customers.customer_id
--       SELECT first_name, last_name, order_id, order_date


-- Query 2: ALL customers with or without orders — LEFT JOIN
-- TODO: LEFT JOIN orders so customers without orders still appear with NULL order columns


-- Query 3: Order count per customer including zero-order customers
-- TODO: LEFT JOIN + GROUP BY + COUNT(o.order_id) AS order_count
--       (COUNT on the FK column returns 0 when there is no match, not on *)


-- Query 4: Order items with product details — INNER JOIN order_items to products
-- TODO: JOIN order_items to products on product_id, SELECT order_id, product_name, quantity, unit_price


-- Query 5: Customers who have NEVER placed an order
-- TODO: LEFT JOIN orders, then filter WHERE o.order_id IS NULL


-- Query 6: All products with any matching order_items (RIGHT JOIN)
-- TODO: Write order_items LEFT JOIN products (or products RIGHT JOIN order_items)
--       so every product appears even with no order_items row


-- Query 7: FULL OUTER JOIN customers and orders
-- TODO: SELECT customer and order columns with FULL OUTER JOIN so both
--       unmatched customers AND unmatched orders appear
