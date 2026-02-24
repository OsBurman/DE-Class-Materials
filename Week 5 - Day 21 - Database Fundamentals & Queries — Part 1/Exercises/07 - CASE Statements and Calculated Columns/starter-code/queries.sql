-- Exercise 07: CASE Statements and Calculated Columns
-- Run setup.sql first.

-- Query 1: Price tier label using CASE WHEN
-- TODO: SELECT product_name, price, and a CASE expression as price_tier
--       'Budget' < 30 | 'Mid-range' 30–149.99 | 'Premium' >= 150
--       ORDER BY price ASC


-- Query 2: Order status label
-- TODO: SELECT order_id, customer_id, order_date, and a CASE on status as status_label
--       delivered → '✅ Complete' | shipped → '🚚 In Transit'
--       pending → '⏳ Waiting'   | cancelled → '❌ Cancelled'


-- Query 3: Stock health label
-- TODO: SELECT product_name, stock, and a CASE on stock as stock_health
--       0 → 'Out of stock' | 1–20 → 'Low' | 21–99 → 'OK' | >= 100 → 'High'
--       ORDER BY stock ASC


-- Query 4: Line item total and discounted total
-- TODO: SELECT order_id, product_id, quantity, unit_price
--       line_total = quantity * unit_price
--       discounted_total: 10% off when quantity >= 3, else same as line_total
--       (Use a CASE expression inside the discounted_total column)


-- Query 5: Customer activity + years as member
-- TODO: SELECT first_name, last_name, is_active
--       activity_status: CASE on is_active → 'Active' / 'Inactive'
--       years_as_member: EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM joined_date)


-- Query 6: Category sales performance with CASE label
-- TODO: JOIN order_items to products, GROUP BY category
--       total_units_sold = SUM(quantity)
--       total_revenue    = SUM(quantity * unit_price)
--       performance: CASE on total_revenue >= 1000 → 'Star' | >= 500 → 'Good' | else → 'Low'
--       ORDER BY total_revenue DESC
