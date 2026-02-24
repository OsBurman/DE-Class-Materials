-- Exercise 06 SOLUTION: Subqueries, EXISTS, and Correlated Subqueries

-- Query 1: Products above the average price
-- The subquery runs once and returns a single scalar value
SELECT product_name, price
FROM products
WHERE price > (SELECT AVG(price) FROM products)
ORDER BY price DESC;

-- Query 2: Most expensive product per category — correlated subquery
-- The inner SELECT re-executes for each row of the outer query
SELECT category, product_name, price
FROM products p
WHERE price = (
    SELECT MAX(price)
    FROM products
    WHERE category = p.category  -- references outer alias p
)
ORDER BY category;

-- Query 3: Customers who ordered 'Laptop Pro' — nested multi-row subqueries
SELECT first_name, last_name
FROM customers
WHERE customer_id IN (
    SELECT customer_id
    FROM orders
    WHERE order_id IN (
        SELECT oi.order_id
        FROM order_items oi
        JOIN products p ON p.product_id = oi.product_id
        WHERE p.product_name = 'Laptop Pro'
    )
);

-- Query 4: Products never ordered — NOT IN excludes all product_ids that appear in order_items
SELECT product_name, category
FROM products
WHERE product_id NOT IN (
    SELECT DISTINCT product_id
    FROM order_items
)
ORDER BY category, product_name;

-- Query 5: Customers with at least one order — EXISTS short-circuits on first match
SELECT first_name, last_name
FROM customers c
WHERE EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.customer_id
)
ORDER BY last_name;

-- Query 6: Customers with NO orders — NOT EXISTS
SELECT first_name, last_name
FROM customers c
WHERE NOT EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.customer_id
)
ORDER BY last_name;

-- Query 7: Orders above that customer's own average total
-- Step 1: compute each order's total; Step 2: compare to customer's avg using correlated subquery
SELECT order_id, customer_id, total
FROM (
    -- Derive a row for each order with its total cost
    SELECT o.order_id, o.customer_id, SUM(oi.quantity * oi.unit_price) AS total
    FROM orders o
    JOIN order_items oi ON oi.order_id = o.order_id
    GROUP BY o.order_id, o.customer_id
) order_totals
WHERE total > (
    -- Correlated: compute this customer's average order total
    SELECT AVG(inner_total)
    FROM (
        SELECT o2.order_id, SUM(oi2.quantity * oi2.unit_price) AS inner_total
        FROM orders o2
        JOIN order_items oi2 ON oi2.order_id = o2.order_id
        WHERE o2.customer_id = order_totals.customer_id
        GROUP BY o2.order_id
    ) customer_orders
)
ORDER BY customer_id, order_id;
