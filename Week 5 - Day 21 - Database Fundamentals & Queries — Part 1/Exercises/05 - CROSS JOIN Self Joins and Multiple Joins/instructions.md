# Exercise 05: CROSS JOIN, Self Joins, and Multiple Joins

## Objective
Practice generating a Cartesian product with `CROSS JOIN`, querying a table against itself with a self-join, and chaining three or more tables in a single query.

## Background
Some queries require non-standard join patterns. A `CROSS JOIN` produces every combination of rows from two tables (useful for generating test data or combinations). A self-join treats one table as if it were two, commonly used to represent hierarchies. Multi-table queries chain joins to traverse relationships that span many tables. Run `setup.sql` first.

## Requirements
Write one SQL query for each task. Label each with a comment.

1. **Category × Status grid (CROSS JOIN)** — Generate all combinations of `category` (from `products`) and `status` (from `orders`). Use `SELECT DISTINCT` on both columns before crossing. Show `category` and `status`.
2. **Employee hierarchy (self-join)** — Join the `employees` table to itself to show each employee alongside their manager's name. Show `emp.full_name` as `employee`, `mgr.full_name` as `manager`. Employees with no manager (the CEO) should also appear — use a `LEFT JOIN`.
3. **Employees managed by 'Tom VP Eng'** — Using a self-join, find all direct reports of the employee named `'Tom VP Eng'`. Show `full_name` and `job_title`.
4. **Complete order detail (3-table join)** — Join `orders` → `customers` → `order_items` → `products` to produce a full receipt view. Show `first_name`, `last_name`, `order_id`, `order_date`, `product_name`, `quantity`, and `unit_price`.
5. **Customer spending summary (multi-join + aggregate)** — Using the same 3-table chain, calculate the total amount spent by each customer: `SUM(quantity * unit_price)` as `total_spent`. Show `first_name`, `last_name`, and `total_spent`, ordered by `total_spent` descending.

## Hints
- `CROSS JOIN` has no `ON` condition — it pairs every row from the left table with every row from the right.
- For a self-join, alias the same table twice: `FROM employees emp JOIN employees mgr ON emp.manager_id = mgr.employee_id`.
- Chain multiple joins sequentially: `FROM a JOIN b ON ... JOIN c ON ... JOIN d ON ...`.
- In multi-table queries, prefix every column reference with the table alias to avoid ambiguity.

## Expected Output

**Query 2** — employee hierarchy (partial):
```
employee       | manager
---------------+----------
Sarah CEO      | NULL
Tom VP Eng     | Sarah CEO
Uma VP Sales   | Sarah CEO
Victor Eng     | Tom VP Eng
Wendy Eng      | Tom VP Eng
Xavier Sales   | Uma VP Sales
Yara Sales     | Xavier Sales
Zack Sales     | Xavier Sales
```

**Query 3** — Tom's direct reports:
```
full_name   | job_title
------------+-----------------
Victor Eng  | Senior Engineer
Wendy Eng   | Engineer
```
