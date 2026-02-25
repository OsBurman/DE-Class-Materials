package com.academy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Day 22 — Part 1: Data Definition Language (DDL) & Database Design
 * ===================================================================
 * Topics covered:
 *   ✓ CREATE TABLE with all common SQL data types
 *   ✓ Constraints: PRIMARY KEY, FOREIGN KEY, UNIQUE, NOT NULL, CHECK, DEFAULT
 *   ✓ Composite primary keys
 *   ✓ ALTER TABLE — ADD COLUMN, ALTER COLUMN, DROP COLUMN, RENAME
 *   ✓ DROP TABLE (with dependency order)
 *   ✓ Schema introspection via INFORMATION_SCHEMA
 *   ✓ Normalization concepts: 1NF → 2NF → 3NF (demonstrated with before/after)
 *   ✓ Entity-Relationship thinking — parent/child table design
 *
 * Database : H2 in-memory (E-commerce schema)
 * Run      : mvn compile exec:java
 */
public class Main {

    private static final String URL  = "jdbc:h2:mem:ecommerce;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   Day 22 · Part 1 — DDL & Database Design                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {

            // ── 1. SQL Data Types ──────────────────────────────────────────
            section("1 · SQL Data Types — overview");
            System.out.println("""
                  Numeric  : INT, BIGINT, SMALLINT, DECIMAL(p,s), FLOAT, DOUBLE
                  String   : VARCHAR(n), CHAR(n), TEXT, CLOB
                  Date/Time: DATE, TIME, TIMESTAMP
                  Boolean  : BOOLEAN
                  Binary   : BLOB, VARBINARY
                  Special  : UUID, JSON (H2 / PostgreSQL)
                """);

            // ── 2. CREATE TABLE ────────────────────────────────────────────
            section("2 · CREATE TABLE — customers with various data types");
            exec(conn, """
                CREATE TABLE customers (
                    id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
                    email         VARCHAR(150) NOT NULL UNIQUE,
                    first_name    VARCHAR(50)  NOT NULL,
                    last_name     VARCHAR(50)  NOT NULL,
                    phone         VARCHAR(20),
                    birth_date    DATE,
                    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
                    credit_limit  DECIMAL(10,2)         DEFAULT 1000.00,
                    created_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
                    notes         TEXT
                )
                """);
            printSchema(conn, "CUSTOMERS");

            section("2b · CREATE TABLE — products with CHECK constraint");
            exec(conn, """
                CREATE TABLE categories (
                    id   INT         PRIMARY KEY,
                    name VARCHAR(50) NOT NULL UNIQUE
                )
                """);

            exec(conn, """
                CREATE TABLE products (
                    id           INT          PRIMARY KEY AUTO_INCREMENT,
                    sku          VARCHAR(20)  NOT NULL UNIQUE,
                    name         VARCHAR(100) NOT NULL,
                    description  TEXT,
                    price        DECIMAL(10,2) NOT NULL CHECK (price >= 0),
                    stock_qty    INT           NOT NULL DEFAULT 0 CHECK (stock_qty >= 0),
                    category_id  INT           NOT NULL,
                    is_available BOOLEAN       NOT NULL DEFAULT TRUE,
                    created_at   TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (category_id) REFERENCES categories(id)
                )
                """);
            printSchema(conn, "PRODUCTS");

            section("2c · CREATE TABLE — orders and order_items (parent-child relationship)");
            exec(conn, """
                CREATE TABLE orders (
                    id           BIGINT        PRIMARY KEY AUTO_INCREMENT,
                    customer_id  BIGINT        NOT NULL,
                    order_date   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    status       VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                                               CHECK (status IN ('PENDING','PROCESSING','SHIPPED','DELIVERED','CANCELLED')),
                    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                    FOREIGN KEY (customer_id) REFERENCES customers(id)
                )
                """);

            exec(conn, """
                CREATE TABLE order_items (
                    id         BIGINT        PRIMARY KEY AUTO_INCREMENT,
                    order_id   BIGINT        NOT NULL,
                    product_id INT           NOT NULL,
                    quantity   INT           NOT NULL CHECK (quantity > 0),
                    unit_price DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (order_id)   REFERENCES orders(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                )
                """);
            System.out.println("  ✓ Created: orders, order_items");

            // ── 3. Composite Primary Key ───────────────────────────────────
            section("3 · Composite PRIMARY KEY (table with no surrogate key)");
            exec(conn, """
                CREATE TABLE product_tags (
                    product_id INT        NOT NULL,
                    tag        VARCHAR(30) NOT NULL,
                    PRIMARY KEY (product_id, tag),         -- composite PK
                    FOREIGN KEY (product_id) REFERENCES products(id)
                )
                """);
            System.out.println("  ✓ product_tags uses (product_id, tag) as composite PK");

            // ── 4. ALTER TABLE ─────────────────────────────────────────────
            section("4 · ALTER TABLE — add, modify, and drop columns");
            exec(conn, "ALTER TABLE customers ADD COLUMN loyalty_points INT DEFAULT 0");
            System.out.println("  ADD COLUMN: customers.loyalty_points INT DEFAULT 0");

            exec(conn, "ALTER TABLE customers ALTER COLUMN phone VARCHAR(30)");
            System.out.println("  ALTER COLUMN: customers.phone → VARCHAR(30)");

            exec(conn, "ALTER TABLE customers ADD COLUMN tier VARCHAR(10) DEFAULT 'BRONZE'");
            exec(conn, "ALTER TABLE customers DROP COLUMN tier");
            System.out.println("  ADD then DROP COLUMN: customers.tier removed");

            // Print updated schema
            printSchema(conn, "CUSTOMERS");

            // ── 5. Schema Introspection ────────────────────────────────────
            section("5 · INFORMATION_SCHEMA — inspect what tables and columns exist");
            query(conn, """
                SELECT table_name, table_type
                FROM   information_schema.tables
                WHERE  table_schema = 'PUBLIC'
                ORDER  BY table_name
                """);

            query(conn, """
                SELECT column_name, data_type, character_maximum_length,
                       is_nullable, column_default
                FROM   information_schema.columns
                WHERE  table_name = 'CUSTOMERS'
                ORDER  BY ordinal_position
                """);

            // ── 6. Normalization ───────────────────────────────────────────
            section("6 · Normalization — 1NF, 2NF, 3NF concepts");
            System.out.println("""
                ┌──────────────────────────────────────────────────────────────────┐
                │ UNNORMALIZED (UNF) — one flat table, repeating groups            │
                │                                                                  │
                │  order_id │ customer │ email        │ p1       │ p2      │ ...   │
                │  ──────────────────────────────────────────────────────────────  │
                │  Problem: adding a 3rd product requires a new column (not scalable)
                └──────────────────────────────────────────────────────────────────┘

                ┌──────────────────────────────────────────────────────────────────┐
                │ 1NF — atomicity: each cell holds ONE value; no repeating groups  │
                │                                                                  │
                │  orders(order_id, customer, email, product_name, qty)            │
                │  No multi-value cells, but customer data duplicated per order    │
                └──────────────────────────────────────────────────────────────────┘

                ┌──────────────────────────────────────────────────────────────────┐
                │ 2NF — remove partial dependencies (applies to composite PKs)     │
                │                                                                  │
                │  customers(customer_id, name, email)                             │
                │  orders(order_id, customer_id, order_date)                       │
                │  order_items(order_id, product_id, qty)  ← composite PK         │
                │                                                                  │
                │  product_name depends only on product_id → move it out           │
                └──────────────────────────────────────────────────────────────────┘

                ┌──────────────────────────────────────────────────────────────────┐
                │ 3NF — remove transitive dependencies                             │
                │                                                                  │
                │  If: order → customer_id → customer_email                        │
                │  Then: email belongs in customers table, not in orders           │
                │                                                                  │
                │  Our e-commerce schema above is already in 3NF ✓                │
                └──────────────────────────────────────────────────────────────────┘
                """);

            // ── 7. DROP TABLE ──────────────────────────────────────────────
            section("7 · DROP TABLE — must drop in dependency order (children first)");
            exec(conn, "DROP TABLE IF EXISTS product_tags");
            exec(conn, "DROP TABLE IF EXISTS order_items");
            exec(conn, "DROP TABLE IF EXISTS orders");
            exec(conn, "DROP TABLE IF EXISTS products");
            exec(conn, "DROP TABLE IF EXISTS categories");
            exec(conn, "DROP TABLE IF EXISTS customers");
            System.out.println("  ✓ All tables dropped in reverse FK dependency order");

            query(conn, """
                SELECT COUNT(*) AS remaining_tables
                FROM   information_schema.tables
                WHERE  table_schema = 'PUBLIC'
                """);

            System.out.println("\n✅  DDL and database design demo complete!");
            System.out.println("──────────────────────────────────────────────────────────────");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────
    private static void section(String title) {
        System.out.printf("%n┌────────────────────────────────────────────────────────────────┐%n");
        System.out.printf("│  %-64s│%n", title);
        System.out.printf("└────────────────────────────────────────────────────────────────┘%n");
    }

    private static void exec(Connection conn, String sql) throws SQLException {
        conn.createStatement().execute(sql);
    }

    private static void query(Connection conn, String sql) throws SQLException {
        String display = sql.replaceAll("\\s+", " ").trim();
        System.out.println("\n  SQL ▸ " + display);
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            printTable(rs);
        }
    }

    private static void printSchema(Connection conn, String tableName) throws SQLException {
        System.out.printf("%n  Schema of %s:%n", tableName);
        query(conn, """
            SELECT column_name AS column,
                   data_type   AS type,
                   is_nullable AS nullable,
                   column_default AS default_val
            FROM   information_schema.columns
            WHERE  table_name = '%s'
            ORDER  BY ordinal_position
            """.formatted(tableName));
    }

    private static void printTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();

        String[] labels = new String[cols];
        for (int i = 0; i < cols; i++) labels[i] = meta.getColumnLabel(i + 1);

        List<String[]> rows = new ArrayList<>();
        while (rs.next()) {
            String[] row = new String[cols];
            for (int i = 0; i < cols; i++) {
                String v = rs.getString(i + 1);
                row[i] = (v == null) ? "NULL" : v;
            }
            rows.add(row);
        }

        int[] w = new int[cols];
        for (int i = 0; i < cols; i++) w[i] = labels[i].length();
        for (String[] row : rows)
            for (int i = 0; i < cols; i++) w[i] = Math.max(w[i], row[i].length());

        StringBuilder sep = new StringBuilder("+");
        StringBuilder fmt = new StringBuilder("|");
        for (int i = 0; i < cols; i++) {
            sep.append("-".repeat(w[i] + 2)).append("+");
            fmt.append(" %-").append(w[i]).append("s |");
        }
        fmt.append("%n");

        System.out.println("  " + sep);
        System.out.printf("  " + fmt, (Object[]) labels);
        System.out.println("  " + sep);
        if (rows.isEmpty()) System.out.println("  |  (no rows returned)");
        for (String[] row : rows) System.out.printf("  " + fmt, (Object[]) row);
        System.out.println("  " + sep);
        System.out.printf("  %d row(s)%n", rows.size());
    }
}
