-- Exercise 05 SOLUTION: CROSS JOIN, Self Joins, and Multiple Joins

-- Query 1: CROSS JOIN — every category × every order status combination
-- Subqueries produce the distinct value sets; CROSS JOIN pairs them
SELECT cats.category, stats.status
FROM (SELECT DISTINCT category FROM products) cats
CROSS JOIN (SELECT DISTINCT status FROM orders) stats
ORDER BY cats.category, stats.status;

-- Query 2: Self-join — employee with manager's name
-- LEFT JOIN so the CEO (manager_id IS NULL) still appears
SELECT
    emp.full_name  AS employee,
    mgr.full_name  AS manager
FROM employees emp
LEFT JOIN employees mgr ON emp.manager_id = mgr.employee_id
ORDER BY mgr.full_name NULLS FIRST, emp.full_name;

-- Query 3: Direct reports of 'Tom VP Eng'
SELECT
    emp.full_name,
    emp.job_title
FROM employees emp
JOIN employees mgr ON emp.manager_id = mgr.employee_id
WHERE mgr.full_name = 'Tom VP Eng';

-- Query 4: Full receipt view — chaining 4 tables
SELECT
    c.first_name,
    c.last_name,
    o.order_id,
    o.order_date,
    p.product_name,
    oi.quantity,
    oi.unit_price
FROM orders o
JOIN customers    c  ON c.customer_id = o.customer_id
JOIN order_items  oi ON oi.order_id   = o.order_id
JOIN products     p  ON p.product_id  = oi.product_id
ORDER BY o.order_id, p.product_name;

-- Query 5: Total spending per customer
SELECT
    c.first_name,
    c.last_name,
    SUM(oi.quantity * oi.unit_price) AS total_spent
FROM orders o
JOIN customers    c  ON c.customer_id = o.customer_id
JOIN order_items  oi ON oi.order_id   = o.order_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY total_spent DESC;
