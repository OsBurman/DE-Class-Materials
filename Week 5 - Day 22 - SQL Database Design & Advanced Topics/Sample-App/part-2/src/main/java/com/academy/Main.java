package com.academy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Day 22 — Part 2: DML, Indexes, Views, Transactions & ACID
 * ===========================================================
 * Topics covered:
 *   ✓ INSERT — single row, multi-row, INSERT … SELECT
 *   ✓ UPDATE — simple, conditional, computed columns
 *   ✓ DELETE — single row, conditional, TRUNCATE concept
 *   ✓ Indexes — CREATE INDEX, index types, when to use them
 *   ✓ Views — CREATE VIEW, query a view, update through a view, DROP VIEW
 *   ✓ Transactions — BEGIN / COMMIT / ROLLBACK
 *   ✓ ACID properties demonstrated with savepoint & rollback
 *   ✓ Isolation levels overview
 *   ✓ Query optimization hints
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
        System.out.println("║   Day 22 · Part 2 — DML, Views, Indexes & Transactions      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            createSchema(conn);

            // ── 1. INSERT ──────────────────────────────────────────────────
            section("1 · INSERT — single row");
            exec(conn, """
                INSERT INTO customers (email, first_name, last_name, city)
                VALUES ('alice@shop.com', 'Alice', 'Johnson', 'New York')
                """);
            exec(conn, """
                INSERT INTO customers (email, first_name, last_name, city)
                VALUES ('bob@shop.com', 'Bob', 'Smith', 'Chicago')
                """);
            query(conn, "SELECT * FROM customers");

            section("1b · INSERT — multiple rows in one statement");
            exec(conn, """
                INSERT INTO products (name, price, stock_qty, category) VALUES
                ('Java Programming Book',  49.99,  100, 'Books'),
                ('Mechanical Keyboard',   129.99,   50, 'Electronics'),
                ('Wireless Mouse',         39.99,   75, 'Electronics'),
                ('Standing Desk',         299.99,   20, 'Furniture'),
                ('Notebook Set',            9.99,  200, 'Stationery'),
                ('USB-C Hub',              59.99,   40, 'Electronics')
                """);
            query(conn, "SELECT id, name, price, stock_qty, category FROM products");

            section("1c · INSERT … SELECT — copy rows from another query");
            exec(conn, """
                CREATE TABLE archived_products (
                    id       INT,
                    name     VARCHAR(100),
                    price    DECIMAL(10,2),
                    archived TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
            exec(conn, """
                INSERT INTO archived_products (id, name, price)
                SELECT id, name, price
                FROM   products
                WHERE  stock_qty < 30
                """);
            query(conn, "SELECT * FROM archived_products");

            // ── 2. UPDATE ──────────────────────────────────────────────────
            section("2 · UPDATE — modify existing rows");
            System.out.println("  Before update:");
            query(conn, "SELECT id, name, price, stock_qty FROM products WHERE name LIKE '%Mouse%'");

            exec(conn, "UPDATE products SET price = 34.99, stock_qty = 80 WHERE name LIKE '%Mouse%'");
            System.out.println("  After: price=34.99, stock_qty=80");
            query(conn, "SELECT id, name, price, stock_qty FROM products WHERE name LIKE '%Mouse%'");

            section("2b · UPDATE — conditional / computed update");
            exec(conn, """
                UPDATE products
                SET price = ROUND(price * 0.90, 2)   -- 10% discount
                WHERE category = 'Electronics'
                """);
            System.out.println("  Applied 10% discount to all Electronics:");
            query(conn, "SELECT name, price, category FROM products WHERE category = 'Electronics'");

            section("2c · UPDATE — using values from another table (correlated)");
            exec(conn, """
                CREATE TABLE discounts (
                    category VARCHAR(50) PRIMARY KEY,
                    pct      DECIMAL(4,2)
                )
                """);
            exec(conn, "INSERT INTO discounts VALUES ('Books', 0.15), ('Furniture', 0.05)");

            exec(conn, """
                UPDATE products p
                SET    p.price = ROUND(p.price * (1 - d.pct), 2)
                FROM   discounts d
                WHERE  d.category = p.category
                """);
            System.out.println("  Applied category-specific discounts:");
            query(conn, "SELECT name, price, category FROM products ORDER BY category");

            // ── 3. DELETE ──────────────────────────────────────────────────
            section("3 · DELETE — remove rows");
            exec(conn, "DELETE FROM archived_products WHERE id = 1");
            System.out.println("  Deleted archived_products row with id=1");
            query(conn, "SELECT * FROM archived_products");

            section("3b · DELETE with subquery");
            exec(conn, """
                DELETE FROM products
                WHERE  id IN (
                    SELECT id FROM archived_products
                )
                """);
            System.out.println("  Deleted products that were archived:");
            query(conn, "SELECT id, name FROM products");

            // ── 4. Indexes ─────────────────────────────────────────────────
            section("4 · Indexes — speed up queries on frequently searched columns");
            System.out.println("""
                  Index Types:
                    B-Tree (default) — great for =, <, >, BETWEEN, ORDER BY
                    Hash             — fast for equality (=) only
                    Full-Text        — for LIKE '%word%' searches
                    Unique           — enforces uniqueness AND speeds up lookups
                    Composite        — covers multiple columns (left-prefix rule)
                """);

            exec(conn, "CREATE INDEX idx_products_category ON products(category)");
            System.out.println("  ✓ B-Tree index on products.category");

            exec(conn, "CREATE INDEX idx_products_price ON products(price)");
            System.out.println("  ✓ B-Tree index on products.price (for range queries)");

            exec(conn, "CREATE UNIQUE INDEX idx_customers_email ON customers(email)");
            System.out.println("  ✓ UNIQUE index on customers.email");

            exec(conn, "CREATE INDEX idx_products_cat_price ON products(category, price)");
            System.out.println("  ✓ Composite index on (category, price) — covers queries filtering by both");

            query(conn, """
                SELECT index_name, table_name, column_name
                FROM   information_schema.indexes
                WHERE  table_schema = 'PUBLIC' AND table_name IN ('PRODUCTS','CUSTOMERS')
                ORDER  BY table_name, index_name
                """);

            // ── 5. Views ───────────────────────────────────────────────────
            section("5 · Views — named, reusable SELECT queries");
            exec(conn, """
                CREATE VIEW electronics_products AS
                SELECT id, name, price, stock_qty
                FROM   products
                WHERE  category = 'Electronics'
                """);
            System.out.println("  Created view: electronics_products");
            query(conn, "SELECT * FROM electronics_products");

            exec(conn, """
                CREATE VIEW product_summary AS
                SELECT
                    category,
                    COUNT(*)             AS product_count,
                    ROUND(AVG(price), 2) AS avg_price,
                    SUM(stock_qty)       AS total_stock
                FROM products
                GROUP BY category
                """);
            System.out.println("  Created aggregate view: product_summary");
            query(conn, "SELECT * FROM product_summary ORDER BY product_count DESC");

            // ── 6. Transactions ────────────────────────────────────────────
            section("6 · Transactions — COMMIT: all or nothing");
            conn.setAutoCommit(false); // manual transaction control

            System.out.println("  BEGIN TRANSACTION");
            exec(conn, "INSERT INTO customers (email,first_name,last_name,city) VALUES ('carol@shop.com','Carol','Davis','Houston')");
            exec(conn, "INSERT INTO customers (email,first_name,last_name,city) VALUES ('dave@shop.com','Dave','Wilson','Phoenix')");
            System.out.println("  Inserted 2 customers (not yet committed)");

            query(conn, "SELECT COUNT(*) AS count_in_tx FROM customers");

            conn.commit();
            System.out.println("  COMMIT — both inserts saved permanently");

            query(conn, "SELECT id, first_name, city FROM customers");

            // ── 7. ROLLBACK ────────────────────────────────────────────────
            section("7 · Transactions — ROLLBACK: undo on error");
            System.out.println("  BEGIN TRANSACTION (will be rolled back)");
            exec(conn, "UPDATE products SET price = 0.01 WHERE category = 'Electronics'");
            System.out.println("  Updated all Electronics prices to $0.01 (mistake!)");
            query(conn, "SELECT name, price FROM products WHERE category = 'Electronics'");

            conn.rollback();
            System.out.println("  ROLLBACK — prices restored to pre-transaction values");
            query(conn, "SELECT name, price FROM products WHERE category = 'Electronics'");

            conn.setAutoCommit(true);

            // ── 8. Savepoints ──────────────────────────────────────────────
            section("8 · Savepoints — partial rollback within a transaction");
            conn.setAutoCommit(false);

            exec(conn, "UPDATE products SET stock_qty = stock_qty + 10 WHERE category = 'Books'");
            System.out.println("  Step 1: Restocked Books (+10) ✓");

            Savepoint sp1 = conn.setSavepoint("after_books_restock");
            System.out.println("  SAVEPOINT after_books_restock");

            exec(conn, "UPDATE products SET stock_qty = 0 WHERE category = 'Furniture'");
            System.out.println("  Step 2: Zeroed Furniture stock (MISTAKE)");

            conn.rollback(sp1);
            System.out.println("  ROLLBACK TO SAVEPOINT — Furniture stock restored; Books update kept");

            conn.commit();
            System.out.println("  COMMIT");
            query(conn, "SELECT name, stock_qty, category FROM products ORDER BY category");
            conn.setAutoCommit(true);

            // ── 9. ACID Properties ─────────────────────────────────────────
            section("9 · ACID Properties — key guarantees of a relational database");
            System.out.println("""
                  A — Atomicity   : a transaction is all-or-nothing.
                                    If any statement fails, the whole transaction rolls back.

                  C — Consistency : a transaction takes the DB from one valid state to another.
                                    CHECK constraints, FK constraints etc. are always enforced.

                  I — Isolation   : concurrent transactions cannot see each other's uncommitted data.
                                    Isolation levels (READ UNCOMMITTED → SERIALIZABLE) trade
                                    safety for performance.

                  D — Durability  : once COMMITted, data survives crashes (written to disk / WAL).

                  ── Isolation Levels (lowest → highest isolation, highest → lowest concurrency) ──
                  READ UNCOMMITTED  — can see dirty reads (rarely used)
                  READ COMMITTED    — default in many DBs (PostgreSQL, SQL Server)
                  REPEATABLE READ   — default in MySQL/InnoDB
                  SERIALIZABLE      — highest safety; transactions run as if sequential
                """);

            System.out.println("\n✅  DML, views, indexes and transactions demo complete!");
            System.out.println("──────────────────────────────────────────────────────────────");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Schema
    // ─────────────────────────────────────────────────────────────────
    private static void createSchema(Connection conn) throws SQLException {
        System.out.println("\n📦  Building e-commerce schema...");
        Statement st = conn.createStatement();

        st.execute("""
            CREATE TABLE customers (
                id         INT         PRIMARY KEY AUTO_INCREMENT,
                email      VARCHAR(150) NOT NULL UNIQUE,
                first_name VARCHAR(50)  NOT NULL,
                last_name  VARCHAR(50)  NOT NULL,
                city       VARCHAR(60)
            )
            """);

        st.execute("""
            CREATE TABLE products (
                id        INT           PRIMARY KEY AUTO_INCREMENT,
                name      VARCHAR(100)  NOT NULL,
                price     DECIMAL(10,2) NOT NULL,
                stock_qty INT           NOT NULL DEFAULT 0,
                category  VARCHAR(50)   NOT NULL
            )
            """);

        System.out.println("✅  Schema ready: customers, products\n");
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
        String display = sql.replaceAll("--[^\n]*", "").replaceAll("\\s+", " ").trim();
        System.out.println("\n  SQL ▸ " + display);
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            printTable(rs);
        }
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
