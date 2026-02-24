# Exercise 02: SQL Injection and XSS Prevention

## Objective

Rewrite vulnerable Java methods to defend against SQL injection using `PreparedStatement` and against Cross-Site Scripting (XSS) by HTML-encoding untrusted output.

## Background

Injection (OWASP A03) and XSS (now folded into A03 in the 2021 list) are two of the oldest and most exploited vulnerability classes. SQL injection occurs when user input is concatenated into a SQL string; XSS occurs when user input is reflected back to the browser without encoding. Both are fixed the same way — never trust raw input, always sanitise or parameterise.

## Requirements

1. In `SqlInjectionDemo.java` (starter-code), the method `findUserByUsername(Connection conn, String username)` builds its query with string concatenation.
   - Rewrite it to use a `PreparedStatement` with a `?` placeholder.
   - The method must return the username string retrieved from the simulated result, or `"not found"` if no row matches.
2. In `SqlInjectionDemo.java`, the method `deleteUserByUsername(Connection conn, String username)` also uses concatenation.
   - Rewrite it to use a `PreparedStatement`.
3. In `XssDemo.java` (starter-code), the method `renderWelcomePage(String username)` returns an HTML string that embeds the username without encoding.
   - Rewrite it using the provided `HtmlEncoder.encode(String)` helper so that characters like `<`, `>`, `"`, and `&` are replaced with their HTML entities.
4. In `XssDemo.java`, write `renderComment(String commentText)` to safely embed comment text inside a `<p>` tag.
5. Run `SqlXssTest` and confirm all assertions pass.

## Hints

- `PreparedStatement` uses `?` as a placeholder; set values with `ps.setString(1, value)`.
- HTML entities: `<` → `&lt;`, `>` → `&gt;`, `&` → `&amp;`, `"` → `&quot;`.
- Order matters when replacing `&` — do it first, before replacing `<` or `>`.
- The attack string `' OR '1'='1` should return `"not found"` after your fix because it won't match any username in the simulated data.

## Expected Output

```
=== SQL Injection Demo ===
findUser("alice")              → alice
findUser("' OR '1'='1")        → not found    ← injection neutralised
deleteUser called with PreparedStatement — safe

=== XSS Demo ===
renderWelcomePage("Alice")     → <h1>Welcome, Alice!</h1>
renderWelcomePage("<script>")  → <h1>Welcome, &lt;script&gt;!</h1>
renderComment("Nice post!")    → <p>Nice post!</p>
renderComment("<b>bold</b>")   → <p>&lt;b&gt;bold&lt;/b&gt;</p>
```
