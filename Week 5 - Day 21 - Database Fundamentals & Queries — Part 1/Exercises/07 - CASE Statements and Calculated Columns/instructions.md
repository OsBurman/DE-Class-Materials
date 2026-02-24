# Exercise 07: CASE Statements and Calculated Columns

## Objective
Practice writing `CASE WHEN` expressions to apply conditional logic inline in SQL queries, and build calculated columns using arithmetic expressions.

## Background
SQL's `CASE` expression works like an `if/else` block inside a query — you can use it in `SELECT`, `ORDER BY`, or `WHERE` to categorise, label, or transform values without leaving the database. Calculated columns combine column values and arithmetic into derived fields. Run `setup.sql` first.

## Requirements
Write one SQL query for each task. Label each with a comment.

1. **Price tier label** — Select `product_name`, `price`, and a `CASE` expression that labels each product:
   - `'Budget'` if price < 30
   - `'Mid-range'` if price BETWEEN 30 AND 149.99
   - `'Premium'` if price >= 150
   Call the column `price_tier`. Order by `price` ascending.

2. **Order status label** — Select `order_id`, `customer_id`, `order_date`, and a `CASE` on `status`:
   - `'✅ Complete'` when status = `'delivered'`
   - `'🚚 In Transit'` when status = `'shipped'`
   - `'⏳ Waiting'` when status = `'pending'`
   - `'❌ Cancelled'` when status = `'cancelled'`
   Call the column `status_label`.

3. **Stock health** — Select `product_name`, `stock`, and a `CASE` expression:
   - `'Out of stock'` if stock = 0
   - `'Low'` if stock BETWEEN 1 AND 20
   - `'OK'` if stock BETWEEN 21 AND 99
   - `'High'` if stock >= 100
   Call the column `stock_health`, order by `stock` ascending.

4. **Line item total with discount** — In `order_items`, calculate:
   - `line_total = quantity * unit_price`
   - `discounted_total`: apply a 10% discount if `quantity >= 3`, otherwise no discount
   Show `order_id`, `product_id`, `quantity`, `unit_price`, `line_total`, and `discounted_total`.

5. **Customer activity summary** — Select `first_name`, `last_name`, `is_active`, and a `CASE` that returns `'Active'` or `'Inactive'` as `activity_status`. Also add a calculated column `years_as_member` = current year minus the year of `joined_date` (use `EXTRACT(YEAR FROM ...)` or `DATE_PART`).

6. **Category sales performance** — Join `order_items` to `products`, group by `category`, and produce:
   - `total_units_sold = SUM(quantity)`
   - `total_revenue = SUM(quantity * unit_price)`
   - A `CASE` column `performance` that labels revenue: `'Star'` if >= 1000, `'Good'` if >= 500, `'Low'` otherwise.
   Order by `total_revenue` descending.

## Hints
- `CASE WHEN condition THEN value ... ELSE value END` — the `ELSE` clause is optional but recommended.
- You can nest `CASE` inside aggregates: `SUM(CASE WHEN ... THEN qty ELSE 0 END)`.
- `EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM joined_date)` gives an approximate year count.
- Aliases defined in `SELECT` cannot be referenced in `WHERE` (but can in `ORDER BY` in most databases).

## Expected Output

**Query 1** — price tier (first 5 rows):
```
product_name       | price  | price_tier
-------------------+--------+------------
Ballpoint Pens     |   4.99 | Budget
Notebook (5-pack)  |   9.99 | Budget
Cotton T-Shirt     |  19.99 | Budget
Resistance Bands   |  22.50 | Budget
Yoga Mat           |  45.00 | Mid-range
```

**Query 3** — stock health (first 5 rows):
```
product_name   | stock | stock_health
---------------+-------+-------------
Office Chair   |     8 | Low
Standing Desk  |     5 | Low
Coffee Maker   |  25   | OK
...
```
