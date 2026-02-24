-- Exercise 04 SOLUTION: JOINs — INNER, LEFT, RIGHT, and FULL OUTER

-- Query 1: INNER JOIN — only customers who have at least one order
SELECT
    c.first_name,
    c.last_name,
    o.order_id,
    o.order_date
FROM customers c
INNER JOIN orders o ON o.customer_id = c.customer_id
ORDER BY c.last_name, o.order_date;

-- Query 2: LEFT JOIN — all customers, NULL order columns for those with no orders
SELECT
    c.first_name,
    c.last_name,
    o.order_id,
    o.order_date
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.customer_id
ORDER BY c.last_name, o.order_date;

-- Query 3: Order count per customer including zero-order customers
-- COUNT(o.order_id) counts non-NULL values, so customers with no orders get 0
SELECT
    c.first_name,
    c.last_name,
    COUNT(o.order_id) AS order_count
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY order_count DESC;

-- Query 4: Order items with product names — INNER JOIN (both sides must match)
SELECT
    oi.order_id,
    p.product_name,
    oi.quantity,
    oi.unit_price
FROM order_items oi
INNER JOIN products p ON p.product_id = oi.product_id
ORDER BY oi.order_id;

-- Query 5: Customers with no orders — LEFT JOIN, filter on NULL FK
SELECT
    c.first_name,
    c.last_name,
    o.order_id  -- will be NULL for unmatched customers
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.customer_id
WHERE o.order_id IS NULL;

-- Query 6: All products with matching order_items rows
-- Written as products LEFT JOIN order_items (equivalent to RIGHT JOIN from order_items' perspective)
SELECT
    p.product_id,
    p.product_name,
    oi.order_id,
    oi.quantity
FROM products p
LEFT JOIN order_items oi ON oi.product_id = p.product_id
ORDER BY p.product_id;

-- Query 7: FULL OUTER JOIN — all customers and all orders regardless of match
SELECT
    c.first_name,
    c.last_name,
    o.order_id,
    o.order_date
FROM customers c
FULL OUTER JOIN orders o ON o.customer_id = c.customer_id
ORDER BY c.last_name NULLS LAST, o.order_date;
