# Exercise 02: Sorting, LIMIT, and DISTINCT

## Objective
Practice controlling query result order and volume using `ORDER BY`, `LIMIT`/`OFFSET`, and the `DISTINCT` keyword.

## Background
After filtering rows, you often need to sort results and return only a page of data. Run `setup.sql` first to create and populate the tables.

## Requirements
Write one SQL query for each task below. Label each with a comment (e.g., `-- Query 1`).

1. **Alphabetical customers** — Select `first_name`, `last_name` from `customers` ordered by `last_name` ascending, then `first_name` ascending.
2. **Most expensive products first** — Select `product_name` and `price` from `products` ordered by `price` descending.
3. **Cheapest 5 products** — Select `product_name` and `price` from `products`, returning only the 5 cheapest products (ascending price).
4. **Page 2 of products** — Using `LIMIT` and `OFFSET`, retrieve products 6–10 when sorted by `product_id` ascending (page size = 5, page number = 2).
5. **Distinct categories** — Select the unique values in the `category` column from `products` using `DISTINCT`.
6. **Distinct cities** — Select the distinct `city` values from `customers` where `is_active = TRUE`, ordered alphabetically.
7. **Top 3 most expensive Electronics** — Select `product_name` and `price` for `Electronics` products, ordered by price descending, limited to 3 rows.
8. **Recent orders** — Select `order_id`, `customer_id`, and `order_date` from `orders` ordered by `order_date` descending, limited to the 5 most recent.

## Hints
- `ORDER BY col ASC` is the default; you can also write `ORDER BY col1 ASC, col2 DESC` to sort by multiple columns.
- `LIMIT 5 OFFSET 5` skips the first 5 rows and returns the next 5.
- `OFFSET` = (page_number - 1) × page_size.
- `SELECT DISTINCT col` eliminates duplicate values in the result.

## Expected Output

**Query 3** — cheapest 5 products:
```
product_name       | price
-------------------+-------
Ballpoint Pens     |  4.99
Notebook (5-pack)  |  9.99
Cotton T-Shirt     | 19.99
Resistance Bands   | 22.50
SQL for Beginners  | 29.99
```

**Query 5** — distinct categories (order may vary):
```
category
-----------
Books
Clothing
Electronics
Furniture
Kitchen
Sports
Stationery
```

**Query 7** — top 3 Electronics by price:
```
product_name       | price
-------------------+--------
Laptop Pro         | 999.99
Smart Watch        | 299.99
Wireless Headphones|  79.99
```
