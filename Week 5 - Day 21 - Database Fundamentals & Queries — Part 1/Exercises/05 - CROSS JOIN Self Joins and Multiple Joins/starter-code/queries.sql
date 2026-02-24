-- Exercise 05: CROSS JOIN, Self Joins, and Multiple Joins
-- Run setup.sql first.

-- Query 1: CROSS JOIN — every combination of distinct category × distinct order status
-- TODO: SELECT DISTINCT category FROM products and SELECT DISTINCT status FROM orders,
--       then CROSS JOIN them (use subqueries or CTEs for the distinct values)


-- Query 2: Self-join employees to show each employee and their manager's name
-- TODO: FROM employees emp LEFT JOIN employees mgr ON emp.manager_id = mgr.employee_id
--       SELECT emp.full_name AS employee, mgr.full_name AS manager


-- Query 3: Direct reports of 'Tom VP Eng' using a self-join
-- TODO: Same self-join as Query 2, but WHERE mgr.full_name = 'Tom VP Eng'


-- Query 4: Full order receipt — join orders + customers + order_items + products
-- TODO: FROM orders
--         JOIN customers ON ...
--         JOIN order_items ON ...
--         JOIN products ON ...
--       SELECT first_name, last_name, order_id, order_date, product_name, quantity, unit_price


-- Query 5: Total spending per customer using the multi-join chain from Query 4
-- TODO: Add GROUP BY customer and SUM(quantity * unit_price) AS total_spent, ORDER BY total_spent DESC
