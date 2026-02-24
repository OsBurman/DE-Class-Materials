-- setup.sql  (run this first in every exercise for Day 21)
-- Creates a shared e-commerce schema used across all 7 exercises.

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS employees;

-- ── customers ────────────────────────────────────────────────────────────────
CREATE TABLE customers (
    customer_id   SERIAL PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    city          VARCHAR(50),
    country       VARCHAR(50),
    is_active     BOOLEAN DEFAULT TRUE,
    joined_date   DATE
);

INSERT INTO customers (first_name, last_name, email, city, country, is_active, joined_date) VALUES
('Alice',   'Smith',    'alice@example.com',   'New York',    'USA',    TRUE,  '2022-03-15'),
('Bob',     'Johnson',  'bob@example.com',     'London',      'UK',     TRUE,  '2021-07-22'),
('Carol',   'Williams', 'carol@example.com',   'Sydney',      'AU',     FALSE, '2020-11-05'),
('David',   'Brown',    'david@gmail.com',     'Toronto',     'CA',     TRUE,  '2023-01-10'),
('Eva',     'Schmidt',  'eva@example.com',     'Berlin',      'DE',     TRUE,  '2022-08-30'),
('Frank',   'Davis',    'frank@example.com',   'Chicago',     'USA',    FALSE, '2021-04-18'),
('Grace',   'Taylor',   'grace@example.com',   'Melbourne',   'AU',     TRUE,  '2023-06-01'),
('Henry',   'Sanchez',  'henry@example.com',   'Madrid',      'ES',     TRUE,  '2020-09-14'),
('Iris',    'Lee',      'iris@gmail.com',      'Seoul',       'KR',     TRUE,  '2022-12-03'),
('James',   'Stone',    'james@example.com',   'Boston',      'USA',    TRUE,  '2021-02-27');

-- ── products ─────────────────────────────────────────────────────────────────
CREATE TABLE products (
    product_id    SERIAL PRIMARY KEY,
    product_name  VARCHAR(100) NOT NULL,
    category      VARCHAR(50),
    price         NUMERIC(10,2) NOT NULL,
    stock         INT DEFAULT 0
);

INSERT INTO products (product_name, category, price, stock) VALUES
('Laptop Pro',           'Electronics', 999.99,  15),
('Wireless Headphones',  'Electronics',  79.99,  42),
('Cotton T-Shirt',       'Clothing',     19.99, 200),
('Running Shoes',        'Clothing',     89.99,  75),
('Smart Watch',          'Electronics', 299.99,  30),
('Python Cookbook',      'Books',        34.99,  60),
('SQL for Beginners',    'Books',        29.99,  80),
('Yoga Mat',             'Sports',       45.00,  55),
('Resistance Bands Set', 'Sports',       22.50, 110),
('Office Chair',         'Furniture',   349.00,   8),
('Standing Desk',        'Furniture',   799.00,   5),
('Coffee Maker',         'Kitchen',     129.99,  25),
('Blender Pro',          'Kitchen',      89.99,  18),
('Notebook (5-pack)',    'Stationery',    9.99, 300),
('Ballpoint Pens',       'Stationery',    4.99, 500);

-- ── orders ───────────────────────────────────────────────────────────────────
CREATE TABLE orders (
    order_id      SERIAL PRIMARY KEY,
    customer_id   INT REFERENCES customers(customer_id),
    order_date    DATE NOT NULL,
    status        VARCHAR(20) DEFAULT 'pending'
);

INSERT INTO orders (customer_id, order_date, status) VALUES
(1,  '2024-01-05', 'delivered'),
(2,  '2024-01-12', 'delivered'),
(1,  '2024-02-03', 'shipped'),
(3,  '2024-02-14', 'cancelled'),
(4,  '2024-03-01', 'delivered'),
(5,  '2024-03-15', 'pending'),
(2,  '2024-04-02', 'delivered'),
(6,  '2024-04-20', 'shipped'),
(7,  '2024-05-08', 'delivered'),
(8,  '2024-05-22', 'pending'),
(9,  '2024-06-01', 'delivered'),
(10, '2024-06-15', 'shipped'),
(1,  '2024-07-04', 'delivered'),
(4,  '2024-07-19', 'cancelled'),
(5,  '2024-08-10', 'delivered');

-- ── order_items ───────────────────────────────────────────────────────────────
CREATE TABLE order_items (
    item_id     SERIAL PRIMARY KEY,
    order_id    INT REFERENCES orders(order_id),
    product_id  INT REFERENCES products(product_id),
    quantity    INT NOT NULL,
    unit_price  NUMERIC(10,2) NOT NULL
);

INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1,  1,  1, 999.99),
(1,  2,  2,  79.99),
(2,  3,  3,  19.99),
(3,  4,  1,  89.99),
(3,  5,  1, 299.99),
(4,  6,  2,  34.99),
(5,  7,  1,  29.99),
(5,  8,  1,  45.00),
(6,  9,  2,  22.50),
(7, 10,  1, 349.00),
(8, 11,  1, 799.00),
(9, 12,  1, 129.99),
(10, 2,  1,  79.99),
(11, 1,  1, 999.99),
(12, 3,  4,  19.99),
(13, 5,  1, 299.99),
(14, 14, 5,   9.99),
(15, 15, 10,  4.99);

-- ── employees (for self-join exercise) ───────────────────────────────────────
CREATE TABLE employees (
    employee_id   SERIAL PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    job_title     VARCHAR(50),
    manager_id    INT REFERENCES employees(employee_id),
    department    VARCHAR(50),
    salary        NUMERIC(10,2)
);

INSERT INTO employees (full_name, job_title, manager_id, department, salary) VALUES
('Sarah CEO',      'CEO',               NULL, 'Executive',  150000),
('Tom VP Eng',     'VP Engineering',    1,    'Engineering', 120000),
('Uma VP Sales',   'VP Sales',          1,    'Sales',       110000),
('Victor Eng',     'Senior Engineer',   2,    'Engineering',  95000),
('Wendy Eng',      'Engineer',          2,    'Engineering',  80000),
('Xavier Sales',   'Sales Manager',     3,    'Sales',        85000),
('Yara Sales',     'Sales Rep',         6,    'Sales',        60000),
('Zack Sales',     'Sales Rep',         6,    'Sales',        58000);
