# Exercise 01: SELECT, WHERE, and Filtering Operators

## Objective
Practice writing `SELECT` queries that filter rows using `WHERE`, logical operators (`AND`, `OR`, `NOT`), and pattern/range operators (`LIKE`, `IN`, `BETWEEN`).

## Background
You are querying a small e-commerce database. The main tables you will use in this exercise are `customers` and `products`. Run `setup.sql` first to create and populate the tables.

## Requirements
Write one SQL query for each task below. Label each query with a comment (e.g., `-- Query 1`).

1. **All customers** — Select all columns from the `customers` table.
2. **Specific columns** — Select only `first_name`, `last_name`, and `email` from `customers`.
3. **Active customers** — Select all customers where `is_active = TRUE`.
4. **Products under $50** — Select `product_name` and `price` from `products` where `price < 50`.
5. **Electronics OR Clothing** — Select all products in the `'Electronics'` or `'Clothing'` category using the `IN` operator.
6. **Mid-range products** — Select products where `price BETWEEN 20 AND 100`.
7. **Name search** — Select all customers whose `last_name` starts with the letter `'S'` using `LIKE`.
8. **Email domain filter** — Select all customers whose `email` ends with `'@example.com'`.
9. **Combined filter** — Select products that are in category `'Electronics'` AND have a `price < 200`.
10. **Exclusion filter** — Select all products that are NOT in the `'Books'` category using `NOT IN`.

## Hints
- Use `SELECT *` for "all columns" and list column names separated by commas for specific columns.
- `LIKE 'S%'` matches any string starting with 'S'; `LIKE '%@example.com'` matches strings ending with that suffix.
- `IN ('val1', 'val2')` is equivalent to `= 'val1' OR = 'val2'` but cleaner.
- `BETWEEN low AND high` is inclusive on both ends.

## Expected Output

**Query 1** — returns all 10 rows from `customers` (all columns).

**Query 5** — Electronics OR Clothing products:
```
product_name          | price  | category
----------------------+--------+-----------
Laptop Pro            | 999.99 | Electronics
Wireless Headphones   |  79.99 | Electronics
Cotton T-Shirt        |  19.99 | Clothing
Running Shoes         |  89.99 | Clothing
Smart Watch           | 299.99 | Electronics
```

**Query 9** — Electronics under $200:
```
product_name          | price  | category
----------------------+--------+-----------
Wireless Headphones   |  79.99 | Electronics
```
