package com.bookstore.security;

// =============================================================================
// OWASP A03 — INJECTION ATTACKS
// =============================================================================
// Injection is the #3 most critical web security risk.
// It occurs when untrusted input is interpreted as a command or query.
//
// Categories of injection:
//   SQL injection       — user input alters a SQL query
//   Command injection   — user input is passed to the OS shell
//   LDAP injection      — user input alters an LDAP query
//   OGNL injection      — user input alters an expression in templates (Struts CVE)
//
// All injection vulnerabilities share the same root cause:
//   The application fails to clearly separate DATA from COMMANDS.
//
// The fix in all cases is the same principle:
//   Never concatenate user input into a query or command — use a safe API
//   that treats input as DATA only (parameterized queries, safe APIs).
// =============================================================================

import java.sql.*;
import java.util.List;
import java.util.regex.Pattern;

// Simulating a simple database connection for demonstration
class DatabaseConnection {
    private static Connection connection;
    
    public static Connection get() {
        return connection; // In real code this returns a pooled connection
    }
}


// =============================================================================
// SECTION 1: SQL Injection — Vulnerable Code (DO NOT USE IN PRODUCTION)
// =============================================================================
// ⚠️  THESE METHODS ARE INTENTIONALLY VULNERABLE — FOR TEACHING PURPOSES ONLY
//     Never write code like this in a real application.
// =============================================================================

class VulnerableBookRepository {

    // -------------------------------------------------------------------------
    // VULNERABILITY 1: String concatenation in a SELECT query
    // -------------------------------------------------------------------------
    // An attacker can pass: author = ' OR '1'='1
    // Resulting SQL: SELECT * FROM books WHERE author = '' OR '1'='1'
    // This returns ALL books — authentication bypass or data exfiltration.
    //
    // Or they pass: author = '; DROP TABLE books; --
    // Resulting SQL: SELECT * FROM books WHERE author = ''; DROP TABLE books; --'
    // This deletes the entire books table!
    // -------------------------------------------------------------------------

    public List<String> findBooksByAuthor_VULNERABLE(String author) throws SQLException {
        // ❌ NEVER DO THIS — user input concatenated directly into SQL string
        String query = "SELECT title FROM books WHERE author = '" + author + "'";

        //  If author = "' OR '1'='1", the query becomes:
        //  SELECT title FROM books WHERE author = '' OR '1'='1'
        //  That condition is ALWAYS TRUE — returns every row in the table.

        System.out.println("[VULNERABLE] Executing: " + query);

        Statement stmt = DatabaseConnection.get().createStatement();
        ResultSet rs = stmt.executeQuery(query);

        List<String> results = new java.util.ArrayList<>();
        while (rs.next()) {
            results.add(rs.getString("title"));
        }
        return results;
    }

    // -------------------------------------------------------------------------
    // VULNERABILITY 2: String concatenation in a login query
    // -------------------------------------------------------------------------
    // Classic "login bypass" attack.
    // Normal user: username = "alice", password = "secret"
    // SQL: SELECT id FROM users WHERE username='alice' AND password='secret'
    //
    // Attacker: username = "admin'--", password = "anything"
    // SQL: SELECT id FROM users WHERE username='admin'--' AND password='anything'
    // Everything after -- is a comment in SQL!
    // The query becomes: SELECT id FROM users WHERE username='admin'
    // The password check is bypassed completely.
    // -------------------------------------------------------------------------

    public boolean authenticateUser_VULNERABLE(String username, String password)
            throws SQLException {
        // ❌ NEVER DO THIS
        String query = "SELECT id FROM users WHERE username='" + username
            + "' AND password='" + password + "'";

        //  Attacker input:  username = "admin'--"
        //  Resulting SQL:   SELECT id FROM users WHERE username='admin'--' AND password='x'
        //  The -- comments out the password check → attacker is authenticated as admin!

        System.out.println("[VULNERABLE] Login query: " + query);

        Statement stmt = DatabaseConnection.get().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs.next(); // returns true if any row matched → user "authenticated"
    }

    // -------------------------------------------------------------------------
    // VULNERABILITY 3: UNION-based injection for data exfiltration
    // -------------------------------------------------------------------------
    // Attacker passes:
    //   isbn = "' UNION SELECT username, password FROM users--"
    // The UNION appends the users table results to the books result set.
    // The attacker retrieves usernames and (hopefully unhashed) passwords.
    // -------------------------------------------------------------------------

    public String findBookByIsbn_VULNERABLE(String isbn) throws SQLException {
        // ❌ NEVER DO THIS
        String query = "SELECT title FROM books WHERE isbn='" + isbn + "'";

        //  Attacker: isbn = "' UNION SELECT username, password FROM users--"
        //  SQL: SELECT title FROM books WHERE isbn=''
        //       UNION SELECT username, password FROM users--'
        //  Response now contains the users table data!

        Statement stmt = DatabaseConnection.get().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs.next() ? rs.getString(1) : null;
    }
}


// =============================================================================
// SECTION 2: SQL Injection — Secure Code Using Parameterized Queries
// =============================================================================
// The fix: use PreparedStatement. The SQL structure is compiled FIRST.
// User input is then bound as a DATA parameter — it can NEVER alter the query structure.
// Even if an attacker passes "' OR '1'='1", it is treated as a literal string value,
// not SQL syntax.
// =============================================================================

class SecureBookRepository {

    // ✅  SECURE: PreparedStatement with positional parameters
    //     The ? placeholder marks where data will be inserted.
    //     The SQL structure is fixed at compile time.
    //     User input is ONLY ever treated as data — never as SQL syntax.

    public List<String> findBooksByAuthor_SECURE(String author) throws SQLException {
        // SQL structure is fixed; ? is a parameter slot
        String query = "SELECT title FROM books WHERE author = ?";

        PreparedStatement pstmt = DatabaseConnection.get().prepareStatement(query);
        pstmt.setString(1, author);  // bind the user input as a pure string value

        //  If author = "' OR '1'='1", the database receives:
        //  SELECT title FROM books WHERE author = ''' OR ''1''=''1'
        //  It looks for a book whose author is LITERALLY that string — finds nothing.
        //  The injection has no effect.

        ResultSet rs = pstmt.executeQuery();
        List<String> results = new java.util.ArrayList<>();
        while (rs.next()) {
            results.add(rs.getString("title"));
        }
        return results;
    }

    // ✅  SECURE: Parameterized login query
    public boolean authenticateUser_SECURE(String username, String password)
            throws SQLException {
        // ⚠️  In real applications you would NEVER store plaintext passwords.
        //     You store a bcrypt hash and compare hashes. This example uses
        //     a plain query only to show the parameterization pattern.
        String query = "SELECT id FROM users WHERE username = ? AND password_hash = ?";

        PreparedStatement pstmt = DatabaseConnection.get().prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, password); // in production: use BCrypt.checkpw() instead

        //  Attacker: username = "admin'--"
        //  PreparedStatement sends: WHERE username = 'admin''--'  (escaped)
        //  No user named "admin'--" exists → returns no rows → login fails.
        //  Attack completely neutralized.

        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    }

    // ✅  SECURE: Parameterized query with multiple parameters
    public List<String> findBooksByGenreAndMaxPrice_SECURE(String genre, double maxPrice)
            throws SQLException {
        String query = "SELECT title FROM books WHERE genre = ? AND price <= ?";

        PreparedStatement pstmt = DatabaseConnection.get().prepareStatement(query);
        pstmt.setString(1, genre);
        pstmt.setDouble(2, maxPrice);

        ResultSet rs = pstmt.executeQuery();
        List<String> results = new java.util.ArrayList<>();
        while (rs.next()) {
            results.add(rs.getString("title"));
        }
        return results;
    }

    // ✅  SECURE: Handling dynamic ORDER BY (a common pitfall)
    // PreparedStatement cannot parameterize column names — only values.
    // So for ORDER BY, validate the column name against a whitelist.
    public List<String> findBooksOrderedBy_SECURE(String sortField) throws SQLException {
        // Whitelist: only these values are allowed
        List<String> allowedColumns = List.of("title", "author", "price", "created_at");

        if (!allowedColumns.contains(sortField)) {
            throw new IllegalArgumentException(
                "Invalid sort field: " + sortField + ". Allowed: " + allowedColumns
            );
        }

        // Safe to interpolate because we've already validated against a whitelist
        String query = "SELECT title FROM books ORDER BY " + sortField + " ASC";
        Statement stmt = DatabaseConnection.get().createStatement();
        ResultSet rs = stmt.executeQuery(query);

        List<String> results = new java.util.ArrayList<>();
        while (rs.next()) {
            results.add(rs.getString("title"));
        }
        return results;
    }
}


// =============================================================================
// SECTION 3: Command Injection — Vulnerable and Secure
// =============================================================================
// Command injection: user input is passed to Runtime.exec() or ProcessBuilder
// and interpreted as a shell command.
//
// Attackers can append shell metacharacters: ; | && || ` $() etc.
// These let them execute arbitrary commands on your server.
//
// Real-world impact: read /etc/passwd, cat SSH keys, install malware,
// pivot to internal network, exfiltrate database backups.
// =============================================================================

class BookCoverConverter {

    // -------------------------------------------------------------------------
    // VULNERABILITY: User input passed to Runtime.exec() via shell
    // -------------------------------------------------------------------------
    // Intended use: convert a book cover image from PNG to JPEG
    // Input: filename = "cover.png"
    // Command: convert cover.png cover.jpg
    //
    // Attacker input: filename = "cover.png; cat /etc/passwd > /var/www/passwd.txt"
    // Command becomes: convert cover.png; cat /etc/passwd > /var/www/passwd.txt
    // The ; is a shell command separator — both commands execute!
    // -------------------------------------------------------------------------

    public void convertImage_VULNERABLE(String filename) throws Exception {
        // ❌ NEVER DO THIS — passes user input through /bin/sh -c
        String command = "convert " + filename + " output.jpg";

        // Runtime.exec(String) passes the entire string to the shell
        // The shell interprets semicolons, pipes, backticks, etc.
        Runtime.getRuntime().exec(command);

        //  Attacker: filename = "cover.png; rm -rf /var/www/uploads/*"
        //  Shell executes: convert cover.png; rm -rf /var/www/uploads/*
        //  All uploaded files deleted!
    }

    // ✅  SECURE: Use ProcessBuilder with argument array — no shell involved
    public void convertImage_SECURE(String filename) throws Exception {
        // First: validate the filename — only allow safe characters
        if (!isValidImageFilename(filename)) {
            throw new IllegalArgumentException(
                "Invalid filename: contains illegal characters"
            );
        }

        // ProcessBuilder with String[] does NOT invoke a shell.
        // Each element is treated as a separate argument, not parsed by /bin/sh.
        // Metacharacters like ; | & have no special meaning here.
        ProcessBuilder pb = new ProcessBuilder("convert", filename, "output.jpg");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Image conversion failed with exit code: " + exitCode);
        }

        //  Attacker: filename = "cover.png; rm -rf /*"
        //  ProcessBuilder passes ["convert", "cover.png; rm -rf /*", "output.jpg"]
        //  ImageMagick receives the full string as a filename — file not found, error returned.
        //  Shell never involved — attack has no effect.
    }

    // ✅  Input validation: whitelist approach for filenames
    private boolean isValidImageFilename(String filename) {
        // Only allow: alphanumeric, hyphens, underscores, dots, forward slash for path
        // This blocks: ;  |  &  `  $  (  )  >  <  *  ?  ~  !  etc.
        Pattern safe = Pattern.compile("^[a-zA-Z0-9_\\-./]+\\.(png|jpg|jpeg|gif|webp)$");
        return safe.matcher(filename).matches();
    }
}


// =============================================================================
// SECTION 4: Additional Injection Prevention Patterns
// =============================================================================

class InjectionPreventionPatterns {

    // -------------------------------------------------------------------------
    // INPUT VALIDATION: Whitelist vs Blacklist approach
    // -------------------------------------------------------------------------
    // ❌ Blacklist (weak): try to block known bad characters
    //    Attackers find bypasses: encoding tricks, Unicode equivalents
    //
    // ✅ Whitelist (strong): only allow known good characters
    //    Anything not on the whitelist is rejected
    // -------------------------------------------------------------------------

    // ❌ BLACKLIST — fragile, incomplete
    public String sanitize_WEAK(String input) {
        // Blocks the obvious attack characters — but what about encoding?
        // &#39; is the HTML entity for '  — this blacklist misses it.
        // URL encoding: %27 is '  — missed again.
        return input.replace("'", "").replace(";", "").replace("--", "");
    }

    // ✅ WHITELIST — only allow what you explicitly permit
    public String sanitize_STRONG(String input, String pattern) {
        // Pattern should be defined per field:
        //   username: ^[a-zA-Z0-9_]{3,30}$
        //   email:    ^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$
        //   isbn:     ^[0-9\\-]{10,17}$
        if (!Pattern.matches(pattern, input)) {
            throw new IllegalArgumentException(
                "Input failed validation. Pattern required: " + pattern
            );
        }
        return input;
    }

    // -------------------------------------------------------------------------
    // DEMO: Running a safe query and showing what happens with bad input
    // -------------------------------------------------------------------------
    public static void demonstrateInjectionPrevention() {
        System.out.println("=== Injection Prevention Demo ===\n");

        // Safe inputs work normally
        String safeAuthor = "Robert Martin";
        System.out.println("Safe input: '" + safeAuthor + "' → executes as: " +
            "SELECT title FROM books WHERE author = ?  [bound value: 'Robert Martin']");

        // Injection attempt is neutralized
        String attackInput = "' OR '1'='1";
        System.out.println("\nAttack input: '" + attackInput + "'");
        System.out.println("With PreparedStatement, database receives:");
        System.out.println("  SELECT title FROM books WHERE author = ''' OR ''1''=''1'");
        System.out.println("  → Searches for a book by a literal author named \" OR '1'='1\"");
        System.out.println("  → Returns 0 results. Attack neutralized.");

        System.out.println("\n--- ORDER BY whitelist validation ---");
        List<String> allowed = List.of("title", "author", "price");
        String[] testFields = {"title", "price", "'; DROP TABLE books--", "author"};
        for (String field : testFields) {
            if (allowed.contains(field)) {
                System.out.println("  '" + field + "' → ALLOWED (whitelisted)");
            } else {
                System.out.println("  '" + field + "' → REJECTED (not in whitelist) → IllegalArgumentException");
            }
        }
    }

    public static void main(String[] args) {
        demonstrateInjectionPrevention();
    }
}
