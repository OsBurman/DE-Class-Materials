-- Exercise 07 SOLUTION: CASE Statements and Calculated Columns

-- Query 1: Price tier label
SELECT
    product_name,
    price,
    CASE
        WHEN price < 30           THEN 'Budget'
        WHEN price <= 149.99      THEN 'Mid-range'
        ELSE                           'Premium'
    END AS price_tier
FROM products
ORDER BY price ASC;

-- Query 2: Order status label
SELECT
    order_id,
    customer_id,
    order_date,
    CASE status
        WHEN 'delivered' THEN '✅ Complete'
        WHEN 'shipped'   THEN '🚚 In Transit'
        WHEN 'pending'   THEN '⏳ Waiting'
        WHEN 'cancelled' THEN '❌ Cancelled'
        ELSE status  -- fallback for unexpected values
    END AS status_label
FROM orders
ORDER BY order_date;

-- Query 3: Stock health label
SELECT
    product_name,
    stock,
    CASE
        WHEN stock = 0            THEN 'Out of stock'
        WHEN stock BETWEEN 1 AND 20  THEN 'Low'
        WHEN stock BETWEEN 21 AND 99 THEN 'OK'
        ELSE                          'High'
    END AS stock_health
FROM products
ORDER BY stock ASC;

-- Query 4: Line item totals with conditional 10% discount
SELECT
    order_id,
    product_id,
    quantity,
    unit_price,
    quantity * unit_price AS line_total,
    -- Apply 10% discount when 3+ units are ordered
    CASE
        WHEN quantity >= 3
            THEN ROUND(quantity * unit_price * 0.90, 2)
        ELSE quantity * unit_price
    END AS discounted_total
FROM order_items
ORDER BY order_id;

-- Query 5: Customer activity and membership duration
SELECT
    first_name,
    last_name,
    is_active,
    CASE WHEN is_active THEN 'Active' ELSE 'Inactive' END AS activity_status,
    -- EXTRACT(YEAR ...) returns a numeric year value
    EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM joined_date) AS years_as_member
FROM customers
ORDER BY years_as_member DESC;

-- Query 6: Category performance with CASE label on aggregated revenue
SELECT
    p.category,
    SUM(oi.quantity)                   AS total_units_sold,
    SUM(oi.quantity * oi.unit_price)   AS total_revenue,
    CASE
        WHEN SUM(oi.quantity * oi.unit_price) >= 1000 THEN 'Star'
        WHEN SUM(oi.quantity * oi.unit_price) >= 500  THEN 'Good'
        ELSE                                               'Low'
    END AS performance
FROM order_items oi
JOIN products p ON p.product_id = oi.product_id
GROUP BY p.category
ORDER BY total_revenue DESC;
