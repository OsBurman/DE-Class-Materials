-- Exercise 03: Aggregate Functions and GROUP BY HAVING
-- Run setup.sql first.

-- Query 1: Total number of products
-- TODO: Use COUNT(*) to count all rows in products


-- Query 2: Number of products per category, ordered by count descending
-- TODO: GROUP BY category, SELECT category and COUNT(*) AS product_count, ORDER BY product_count DESC


-- Query 3: Min, max, and average price across all products
-- TODO: Use MIN(), MAX(), and ROUND(AVG(), 2) on the price column


-- Query 4: Total stock value (price * stock) per category, ordered by stock_value descending
-- TODO: SELECT category, SUM(price * stock) AS stock_value, GROUP BY category, ORDER BY stock_value DESC


-- Query 5: Only categories that have more than 2 products
-- TODO: Use the same GROUP BY as Query 2, but add HAVING COUNT(*) > 2


-- Query 6: Categories where the average price exceeds $100
-- TODO: GROUP BY category, use HAVING ROUND(AVG(price), 2) > 100, ORDER BY avg_price DESC


-- Query 7: Number of orders per customer (show name and count)
-- TODO: JOIN orders to customers on customer_id, GROUP BY customer, SELECT name + COUNT(order_id) AS order_count


-- Query 8: Total revenue per order (SUM of quantity * unit_price in order_items)
-- TODO: GROUP BY order_id, SELECT order_id and SUM(quantity * unit_price) AS total, ORDER BY total DESC
