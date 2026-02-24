-- Exercise 06: Subqueries, EXISTS, and Correlated Subqueries
-- Run setup.sql first.

-- Query 1: Products above the overall average price (single-row subquery)
-- TODO: WHERE price > (SELECT AVG(price) FROM products)
--       ORDER BY price DESC


-- Query 2: Most expensive product per category (correlated subquery)
-- TODO: For each product p, check WHERE p.price = (SELECT MAX(price) FROM products WHERE category = p.category)


-- Query 3: Customers who ordered 'Laptop Pro' (multi-row subquery with IN)
-- TODO: Find order_ids containing 'Laptop Pro' via order_items+products subquery,
--       then find customer_ids in orders with those order_ids,
--       then find customers with those customer_ids


-- Query 4: Products never ordered (NOT IN subquery)
-- TODO: WHERE product_id NOT IN (SELECT DISTINCT product_id FROM order_items)


-- Query 5: Customers who have at least one order (EXISTS)
-- TODO: WHERE EXISTS (SELECT 1 FROM orders o WHERE o.customer_id = c.customer_id)


-- Query 6: Customers who have NEVER placed an order (NOT EXISTS)
-- TODO: WHERE NOT EXISTS (SELECT 1 FROM orders o WHERE o.customer_id = c.customer_id)


-- Query 7: Orders above that customer's own average order total (correlated subquery)
-- TODO: Compute each order's total with a subquery or CTE,
--       then compare it to the avg total for the same customer_id using a correlated subquery
--       Show customer_id, order_id, total
