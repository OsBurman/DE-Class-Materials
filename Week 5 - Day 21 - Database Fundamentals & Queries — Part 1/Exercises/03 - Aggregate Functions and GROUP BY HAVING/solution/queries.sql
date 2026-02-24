-- Exercise 03 SOLUTION: Aggregate Functions and GROUP BY HAVING

-- Query 1: Total products
SELECT COUNT(*) AS total_products
FROM products;

-- Query 2: Products per category — every SELECT column not aggregated must be in GROUP BY
SELECT category, COUNT(*) AS product_count
FROM products
GROUP BY category
ORDER BY product_count DESC;

-- Query 3: Price statistics across all products
SELECT
    MIN(price)          AS min_price,
    MAX(price)          AS max_price,
    ROUND(AVG(price), 2) AS avg_price
FROM products;

-- Query 4: Stock value per category — calculated column inside SUM
SELECT
    category,
    SUM(price * stock) AS stock_value
FROM products
GROUP BY category
ORDER BY stock_value DESC;

-- Query 5: Categories with more than 2 products — HAVING filters after GROUP BY
SELECT category, COUNT(*) AS product_count
FROM products
GROUP BY category
HAVING COUNT(*) > 2
ORDER BY product_count DESC;

-- Query 6: Categories where avg price > $100
SELECT
    category,
    ROUND(AVG(price), 2) AS avg_price
FROM products
GROUP BY category
HAVING AVG(price) > 100
ORDER BY avg_price DESC;

-- Query 7: Orders per customer — JOIN then GROUP BY customer
SELECT
    c.first_name,
    c.last_name,
    COUNT(o.order_id) AS order_count
FROM customers c
JOIN orders o ON o.customer_id = c.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY order_count DESC;

-- Query 8: Total revenue per order
SELECT
    order_id,
    SUM(quantity * unit_price) AS total
FROM order_items
GROUP BY order_id
ORDER BY total DESC;
