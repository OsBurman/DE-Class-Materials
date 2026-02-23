# Week 3 - Day 11: HTML & CSS
## Part 1: HTML Foundations — Structure, Semantics & Forms
### Slide Descriptions

---

## Slide 1: Title Slide

**Title:** HTML & CSS — Part 1: HTML Foundations
**Subtitle:** Structure, Semantics, Forms & the DOM
**Week 3, Day 11 | Frontend Development Track**

Visual: Clean browser window mockup showing an HTML file with colorful syntax highlighting on a light background. Contrasts with the dark, terminal-heavy aesthetic of the Java weeks to signal a visual paradigm shift.

---

## Slide 2: The Web Stack — HTML, CSS, and JavaScript

**Visual:** Three horizontal layers stacked like a building:
- Layer 1 (Foundation): HTML — Structure
- Layer 2 (Walls/Exterior): CSS — Presentation
- Layer 3 (Plumbing/Wiring): JavaScript — Behavior

**Content:**

| Language | Role | Analogy |
|----------|------|---------|
| **HTML** | Structure — defines what content *exists* | Skeleton / Blueprint |
| **CSS** | Presentation — defines how content *looks* | Paint / Clothing |
| **JavaScript** | Behavior — defines what content *does* | Muscles / Wiring |

**Key points:**
- These three are the **only languages that run natively in the browser** — no compilation, no runtime to install
- Every website you've ever visited is built from these three
- HTML is **not a programming language** — it's a markup language; it describes structure, not logic
- Today: master HTML (Part 1) and CSS (Part 2)
- Tomorrow: JavaScript fundamentals (Day 12)
- The mental shift from Java: move from *imperative* (tell the computer what to do step-by-step) to *declarative* (describe what you want and let the browser figure out how to render it)

---

## Slide 3: HTML Document Structure — The Universal Skeleton

**Visual:** A code editor view showing the full document skeleton with each part labeled and color-coded.

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>My Page Title</title>
    <link rel="stylesheet" href="styles.css" />
  </head>
  <body>
    <h1>Hello, World!</h1>
    <p>My first webpage.</p>
  </body>
</html>
```

**Annotated breakdown:**
- `<!DOCTYPE html>` — NOT an HTML tag; a document type declaration telling the browser to use **HTML5 standards mode**. Without it, browsers enter "quirks mode" (broken rendering from the 1990s) — never omit this
- `<html lang="en">` — root element wrapping the entire document; `lang` attribute is essential for accessibility tools (screen readers, translation services) and SEO
- `<head>` — metadata container: information *about* the document that is **not displayed** to users; loads resources (CSS, JS, fonts)
- `<body>` — **everything the user sees** lives here; all visible content goes inside `<body>`
- **Rule: every HTML file ever written follows this exact skeleton** — there are no valid exceptions in professional web development

---

## Slide 4: Inside the `<head>` — Metadata That Powers Your Page

**Visual:** Side-by-side: a `<head>` section with each element highlighted, and a browser showing the visible effects (tab title, Google snippet, mobile rendering).

```html
<head>
  <!-- 1. Character encoding — FIRST element, always -->
  <meta charset="UTF-8" />

  <!-- 2. Mobile responsiveness — critical for all modern sites -->
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />

  <!-- 3. Browser tab title + search results headline -->
  <title>Software Academy — Learn to Code</title>

  <!-- 4. Google search snippet description -->
  <meta name="description" content="Learn full-stack development with Java, Spring Boot, and React." />

  <!-- 5. Link CSS file -->
  <link rel="stylesheet" href="styles.css" />

  <!-- 6. Browser tab icon (favicon) -->
  <link rel="icon" href="favicon.ico" />

  <!-- 7. Load JavaScript (defer = run after HTML parses) -->
  <script src="app.js" defer></script>
</head>
```

**Each element explained:**
- `charset="UTF-8"` — defines character encoding; handles international characters (è, ü, 中文), emojis (😊), and math symbols (→); without it, special characters appear as garbled nonsense
- `viewport` meta — tells mobile browsers **not** to zoom out and display a shrunken desktop-size page; without this, your site looks terrible on phones
- `<title>` — appears in browser tab, bookmarks, and as the headline in Google search results; every page must have a unique, descriptive title
- `<meta name="description">` — the snippet shown under your link in search results; 150-160 characters
- `<link rel="stylesheet">` — attaches external CSS file; browser loads and applies it before rendering
- `defer` on script tag — tells browser to download JS in background but execute it after the HTML document is fully parsed (prevents JS errors from running before elements exist)
- **Nothing in `<head>` renders on the page** — it's purely metadata and resource loading instructions

---

## Slide 5: The DOM — Document Object Model

**Visual:** Split screen — left: HTML source code; right: the corresponding DOM tree diagram with connecting lines showing parent/child/sibling relationships.

```
document
└── html
    ├── head
    │   ├── meta (charset)
    │   ├── meta (viewport)
    │   ├── title: "My Page"
    │   └── link (stylesheet)
    └── body
        ├── header
        │   └── nav
        │       └── ul
        │           ├── li → a: "Home"
        │           └── li → a: "About"
        ├── main
        │   ├── h1: "Welcome"
        │   ├── p: "Content here"
        │   └── article
        │       ├── h2: "Article Title"
        │       └── p: "Article text"
        └── footer
            └── p: "© 2025"
```

**What the DOM is:**
- When a browser loads an HTML file, it **parses the text and builds a tree structure in memory** — this is the DOM
- Every HTML element becomes a **node** in the tree
- Nodes have **parent** (the element directly above), **children** (elements directly below), and **sibling** (elements at the same level) relationships
- `document` is the root that wraps everything — even `<html>`

**Why the DOM matters:**
- Browsers use the DOM to decide what to **render as pixels** on your screen
- CSS selectors traverse the DOM tree to find elements to style
- Day 13 covers how JavaScript interacts with the DOM: selecting nodes (`document.querySelector()`), modifying them, and responding to events (`addEventListener()`)
- **Today's scope:** Understand the tree structure that your HTML creates — the mental model you'll use for CSS, JavaScript, and debugging

---

## Slide 6: HTML Elements, Tags, and Attributes — Anatomy

**Visual:** A highly annotated diagram of an HTML element with arrows pointing to each part, plus a code example.

```html
<!-- Complete element anatomy -->
<a href="https://example.com" target="_blank" rel="noopener noreferrer">
  Visit Example Site
</a>
<!-- ↑ opening tag  ↑ attribute name="value" pairs       ↑ content  ↑ closing tag -->

<!-- Void elements — self-closing, no content, no closing tag -->
<img src="photo.jpg" alt="Mountain landscape" width="800" height="600" />
<input type="text" name="username" placeholder="Your name" />
<br />       <!-- line break -->
<hr />       <!-- horizontal rule / thematic break -->
<meta charset="UTF-8" />
<link rel="stylesheet" href="style.css" />
```

**Core vocabulary:**
- **Element** = opening tag + content + closing tag (or a self-closing void element)
- **Tag** = `<tagname>` — the markup wrapper; opening tags start content, closing tags end it
- **Attribute** = key-value pair providing additional information about an element; lives **only in the opening tag**; syntax: `name="value"` (always use double quotes)
- **Void elements** are self-closing — they cannot have children or content (img, input, br, hr, meta, link)

**Rules:**
- HTML is case-insensitive but **lowercase is universal professional standard**
- Attributes should always be quoted: `href="/about"` not `href=/about`
- **Proper nesting is mandatory** — tags must close in reverse order of opening; `<p><strong>text</strong></p>` is correct; `<p><strong>text</p></strong>` is invalid
- **Malformed HTML** — browsers will try to "fix" it (fill in missing closing tags, etc.) but the results are unpredictable; always write valid HTML

---

## Slide 7: Block vs Inline Elements — The Layout Divide

**Visual:** Two side-by-side browser panels showing how block elements stack vertically and inline elements flow horizontally within text.

```html
<!-- Block elements — start on new line, take full available width -->
<div>I am a div — a generic block container</div>
<p>I am a paragraph — block element, adds default top/bottom margin</p>
<h2>I am a heading — block element, styled by default</h2>

<!-- Inline elements — flow within text, only as wide as their content -->
<p>
  This paragraph contains <strong>bold text</strong>,
  <em>italic text</em>, a <a href="#">link</a>,
  and <code>some code</code> — all inline elements.
</p>

<!-- Block/inline containment rule -->
<!-- ❌ INVALID: block element inside inline element -->
<span><div>This nesting is invalid HTML!</div></span>

<!-- ✅ VALID: inline inside block -->
<div><span>This is perfectly valid</span></div>
```

**Comparison table:**

| Property | Block Elements | Inline Elements |
|----------|---------------|----------------|
| Line behavior | Always starts on a new line | Flows within surrounding text |
| Default width | Takes full available width | Only as wide as content |
| Content rules | Can contain block and inline | Can only contain inline elements |
| Default height | Sized by content | Sized by content |
| Common examples | `div, p, h1-h6, ul, ol, li, table, form, header, nav, main, section, article, aside, footer` | `span, a, img, strong, em, b, i, code, input, button, label` |

**Important:** CSS's `display` property can change an element's block/inline behavior — you'll use this constantly in Part 2. This is a default behavior rule, not a locked-in rule.

---

## Slide 8: Semantic HTML — Writing Markup That Means Something

**Visual:** Two-column browser rendering — left shows a `<div>`-soup page (no semantic elements, all divs with ids), right shows the same page using semantic HTML5 elements.

```html
<!-- ❌ Non-semantic approach (pre-HTML5 style) -->
<div id="header">
  <div id="nav">
    <div class="nav-item">Home</div>
    <div class="nav-item">About</div>
  </div>
</div>
<div id="main-content">
  <div class="blog-post">
    <div class="post-title">My First Blog Post</div>
    <div class="post-body">Content here...</div>
  </div>
</div>
<div id="footer">© 2025</div>

<!-- ✅ Semantic HTML5 approach -->
<header>
  <nav>
    <a href="/">Home</a>
    <a href="/about">About</a>
  </nav>
</header>
<main>
  <article>
    <h1>My First Blog Post</h1>
    <p>Content here...</p>
  </article>
</main>
<footer>© 2025</footer>
```

**Why semantic HTML is the professional standard — four concrete reasons:**

1. **Accessibility (♿):** Screen readers announce "navigation landmark" when encountering `<nav>`, "main content region" for `<main>`, and "article" for `<article>`. Users who are blind navigate by jumping between landmarks. With `<div>` soup, they just hear an undifferentiated wall of content.

2. **SEO:** Search engines treat `<article>` content as the primary page content and give it more weight. They recognize `<nav>` as navigation (and skip it for content ranking). Semantic structure directly impacts your Google ranking.

3. **Readability and maintainability:** A developer reading `<nav>` instantly knows it's navigation. Reading `<div id="nav-wrapper">` requires inferring meaning. Every second saved reading code is value.

4. **WCAG compliance:** The Web Content Accessibility Guidelines (legally required for public-facing government and many corporate websites in the US, EU, and UK) depend on semantic structure for compliance.

---

## Slide 9: HTML5 Semantic Layout Elements — Reference Guide

**Visual:** A page layout wireframe diagram with each semantic region labeled and color-coded, next to the corresponding code.

```html
<body>
  <header>
    <!-- Site-wide header: logo, hero, top navigation -->
    <nav>
      <!-- Navigation: main menu, breadcrumbs, table of contents -->
      <ul>
        <li><a href="/">Home</a></li>
        <li><a href="/courses">Courses</a></li>
        <li><a href="/contact">Contact</a></li>
      </ul>
    </nav>
  </header>

  <main>
    <!-- Primary, unique content of this page — ONE per page -->

    <section id="featured-courses">
      <!-- Thematic group of related content; typically has a heading -->
      <h2>Featured Courses</h2>

      <article>
        <!-- Self-contained, redistributable content -->
        <!-- A blog post, news article, product card, comment -->
        <h3>Java Fundamentals</h3>
        <p>A comprehensive 10-week course...</p>
        <footer>
          <!-- Article footer: author, date, tags -->
          <time datetime="2025-01-15">January 15, 2025</time>
        </footer>
      </article>

      <article>
        <h3>Web Development</h3>
        <p>From HTML to React...</p>
      </article>
    </section>

    <aside>
      <!-- Tangentially related content: sidebar, related articles, ads -->
      <h3>Related Resources</h3>
    </aside>
  </main>

  <footer>
    <!-- Site-wide footer: copyright, policies, social links -->
    <p>&copy; 2025 Software Academy. All rights reserved.</p>
    <nav aria-label="Footer navigation">
      <a href="/privacy">Privacy Policy</a>
      <a href="/terms">Terms of Service</a>
    </nav>
  </footer>
</body>
```

**Element quick reference:**
- `<header>` — introductory content; can appear inside `<body>` (site-wide header) **or** inside `<article>`/`<section>` (section header)
- `<nav>` — navigation links; a page can have multiple `<nav>` elements (use `aria-label` to differentiate)
- `<main>` — the unique primary content of a page; **only one per page**; skip-navigation links land here
- `<section>` — thematic content grouping; always include a heading to describe the section
- `<article>` — independently distributable, self-contained content; could be syndicated to RSS feed
- `<aside>` — tangentially related to surrounding content; sidebar, related links, pull quotes
- `<footer>` — closing information; appears in `<body>` (site footer) or inside sections/articles

---

## Slide 10: Text Content Elements — The Full Toolkit

**Visual:** A rendered HTML page showing all these elements with their visual defaults, next to the code that produced them.

```html
<!-- Headings — define document outline, NOT for visual sizing -->
<h1>Page Title — Only ONE h1 per page (SEO and accessibility)</h1>
<h2>Major Section Heading</h2>
<h3>Subsection Heading</h3>
<h4>Sub-subsection</h4>
<!-- h5, h6 exist but are rarely needed — never skip levels! -->
<!-- ❌ Never: h1 → h3 (skipping h2 breaks the outline) -->

<!-- Paragraphs — the primary container for body text -->
<p>Use paragraphs for all prose. Browsers add top and bottom margin automatically.</p>

<!-- Semantic emphasis -->
<strong>Critically important text</strong>      <!-- bold + semantic weight -->
<b>Bold formatting only</b>                     <!-- visual only, no semantic meaning -->
<em>Emphasized/stressed text</em>              <!-- italic + semantic stress -->
<i>Italic formatting only</i>                  <!-- idiomatic text, titles, terms -->

<!-- Code -->
<code>const message = "Hello";</code>           <!-- inline code snippet -->
<pre><code>                                      <!-- code block (preserves whitespace) -->
function calculateTotal(items) {
  return items.reduce((sum, item) => sum + item.price, 0);
}
</code></pre>

<!-- Quotations -->
<blockquote cite="https://source.com">
  The best way to predict the future is to invent it.
</blockquote>
<p>She said <q>Hello, how are you?</q></p>     <!-- inline quote, auto-adds quotation marks -->

<!-- Other useful semantic elements -->
<abbr title="HyperText Markup Language">HTML</abbr>
<time datetime="2025-06-15">June 15, 2025</time>  <!-- machine-readable date -->
<mark>Highlighted text</mark>
<del>Removed/deprecated text</del>
<ins>Newly added text</ins>
<sub>Subscript</sub> and <sup>Superscript</sup>

<!-- Use sparingly — prefer CSS for spacing -->
<br />   <!-- line break: next content starts on new line -->
<hr />   <!-- thematic break: horizontal rule -->
```

**Semantic vs presentational — why it matters:**
- `<strong>` tells screen readers: "This text is critically important" — `<b>` does not
- `<em>` tells screen readers: "Emphasize this word when reading aloud" — `<i>` does not
- **Rule:** Always prefer semantic over presentational elements. Use CSS to control visual appearance.
- **Heading rule:** Never use `<h3>` just because you want smaller text — use CSS for sizing. Never skip heading levels (h1 → h3 breaks document outline).

---

## Slide 11: Lists — Unordered, Ordered, and Description

**Visual:** Three browser panels showing rendered output of each list type.

```html
<!-- Unordered list — items with no meaningful sequence -->
<ul>
  <li>JavaScript</li>
  <li>Python</li>
  <li>Java</li>
</ul>

<!-- Ordered list — items where order/sequence matters -->
<ol>
  <li>Open a terminal</li>
  <li>Navigate to the project folder</li>
  <li>Run <code>npm install</code></li>
  <li>Run <code>npm start</code></li>
</ol>

<!-- Ordered list with custom start -->
<ol start="3">    <!-- counting starts at 3 -->
  <li>Third place</li>
  <li>Fourth place</li>
</ol>

<!-- Nested list -->
<ul>
  <li>Frontend Development
    <ul>
      <li>HTML</li>
      <li>CSS</li>
      <li>JavaScript
        <ul>
          <li>React</li>
          <li>Angular</li>
        </ul>
      </li>
    </ul>
  </li>
  <li>Backend Development</li>
</ul>

<!-- Description list — term + definition pairs -->
<dl>
  <dt>DOM</dt>
  <dd>Document Object Model — the browser's in-memory tree representation of an HTML document</dd>

  <dt>AJAX</dt>
  <dd>Asynchronous JavaScript and XML — technique for making HTTP requests without page reloads</dd>

  <dt>API</dt>
  <dd>Application Programming Interface — a contract for how software components communicate</dd>
</dl>
```

**When to use each:**
- `<ul>` — items where order doesn't matter; **navigation menus are almost always `<ul>` elements styled with CSS**
- `<ol>` — steps, rankings, numbered instructions, table of contents
- `<dl>` — glossaries, FAQ pages, metadata display (key-value pairs), technical specifications
- Nesting: fine to 2-3 levels; avoid deeper nesting for readability

---

## Slide 12: Links and Images — Connecting the Web

**Visual:** Annotated code examples with visual output showing a link and a figure with caption.

```html
<!-- Anchor element — the fundamental unit of hypertext -->
<a href="https://www.google.com">Visit Google</a>           <!-- absolute URL -->
<a href="/about">About Us</a>                               <!-- root-relative (same site) -->
<a href="contact.html">Contact</a>                          <!-- document-relative -->
<a href="#section-installation">Skip to Installation</a>    <!-- anchor (same page) -->
<a href="mailto:support@example.com">Email Support</a>
<a href="tel:+15555550100">Call Us</a>

<!-- External link — ALWAYS use rel="noopener noreferrer" with target="_blank" -->
<a href="https://github.com" target="_blank" rel="noopener noreferrer">
  GitHub (opens in new tab)
</a>
<!-- Without rel="noopener noreferrer": the new tab can access window.opener -->
<!-- and redirect your original page — a real security vulnerability -->

<!-- Images -->
<img
  src="images/hero.jpg"
  alt="A developer writing code at a standing desk in a modern office"
  width="1200"
  height="600"
/>

<!-- Figure — semantic container for images, diagrams, code, etc. -->
<figure>
  <img
    src="charts/revenue-q3.png"
    alt="Bar chart showing monthly revenue: Jan $10K, Jun $50K, steady growth trend"
  />
  <figcaption>Q3 2025 Revenue Growth — January through June</figcaption>
</figure>

<!-- Decorative image — empty alt tells screen readers to skip it -->
<img src="decorative-wave.svg" alt="" role="presentation" />
```

**Critical rules:**
- **`alt` on every image is mandatory** — describes the image for screen readers and when the image fails to load; legally required for accessibility in many jurisdictions; no `alt` = accessibility violation
- **Descriptive alt text:** describe what the image *conveys* not just what it *depicts* — for a chart, describe the trend, not just "a chart"
- `target="_blank"` requires `rel="noopener noreferrer"` — prevents tab-napping (the opened page can access and redirect your original window without this)
- `width` and `height` attributes — allow the browser to reserve layout space before the image downloads, preventing cumulative layout shift (a Core Web Vital metric)
- Empty `alt=""` — signals to screen readers that the image is purely decorative (background pattern, divider) and should be skipped

---

## Slide 13: HTML Tables — Data Presentation Done Right

**Visual:** A rendered table with visible grid structure, next to the annotated code.

```html
<table>
  <caption>Q3 2025 Enrollment by Course</caption>    <!-- accessible table title -->

  <thead>    <!-- semantic: column headers section -->
    <tr>
      <th scope="col">Course</th>
      <th scope="col">Enrolled</th>
      <th scope="col">Completed</th>
      <th scope="col">Completion Rate</th>
    </tr>
  </thead>

  <tbody>    <!-- semantic: data rows section -->
    <tr>
      <th scope="row">Java Fundamentals</th>    <!-- row header for accessibility -->
      <td>142</td>
      <td>128</td>
      <td>90%</td>
    </tr>
    <tr>
      <th scope="row">Web Development</th>
      <td>98</td>
      <td>81</td>
      <td>83%</td>
    </tr>
    <tr>
      <td colspan="2">Total Students / Completed</td>   <!-- spans 2 columns -->
      <td>209</td>
      <td>—</td>
    </tr>
  </tbody>

  <tfoot>    <!-- semantic: summary/totals row -->
    <tr>
      <td>Overall</td>
      <td>240</td>
      <td colspan="2">Average: 87% completion</td>
    </tr>
  </tfoot>
</table>
```

**Table structure explained:**
- `<caption>` — accessible title; screen readers announce this before the table content
- `<thead>` / `<tbody>` / `<tfoot>` — semantic sections; browsers can scroll `<tbody>` independently on long tables; print styling can repeat `<thead>` on each page
- `<th>` — header cell (bold, centered by default); `scope="col"` means "header for this column"; `scope="row"` means "header for this row" — both are required for screen readers to correctly announce data cells
- `<td>` — data cell
- `colspan="2"` — cell spans 2 columns; `rowspan="2"` — cell spans 2 rows

**⚠️ The critical rule: Never use `<table>` for layout**
- Tables describe *data relationships* between rows and columns
- Using tables for page layout (centering, columns, positioning) was widespread in the 1990s
- It creates accessibility nightmares (screen readers read tables cell-by-cell) and maintenance disasters
- CSS Grid and Flexbox completely replaced table-based layouts — use them instead

---

## Slide 14: HTML Forms — Structure and Submission

**Visual:** A simple registration form rendered in the browser next to its code.

```html
<form action="/api/users/register" method="POST">

  <!-- Individual form field group (common pattern) -->
  <div class="form-group">
    <label for="fullname">Full Name</label>
    <input type="text" id="fullname" name="fullname"
           placeholder="Jane Smith" autocomplete="name" required />
  </div>

  <div class="form-group">
    <label for="email">Email Address</label>
    <input type="email" id="email" name="email"
           placeholder="jane@example.com" autocomplete="email" required />
  </div>

  <div class="form-group">
    <label for="password">Password</label>
    <input type="password" id="password" name="password"
           minlength="8" required />
  </div>

  <div class="form-actions">
    <button type="submit">Create Account</button>
    <button type="reset">Clear</button>
  </div>

</form>
```

**Core form concepts:**
- `action="/api/users/register"` — the URL where form data is sent on submission
- `method="GET"` — data appended to URL as query string: `/search?q=hello&sort=date`
  - Use for: search forms, filters, anything shareable as a bookmark
  - Data visible in URL and browser history
- `method="POST"` — data sent in HTTP request body, not visible in URL
  - Use for: login, registration, payment, anything with sensitive or large data
- `autocomplete` — hints browser to fill from saved data (improves mobile UX significantly)
- `placeholder` — hint text inside the field (disappears when user starts typing); **not a substitute for a label**

**Modern context:** In single-page applications (React/Angular — Weeks 4+), forms rarely use the `action` attribute directly. JavaScript intercepts the submit event, reads the form data, and sends it via `fetch()` / Axios. But the HTML form model is the foundation.

---

## Slide 15: Form Input Types — The Complete Reference

**Visual:** A grid showing each input type with its rendered browser UI — text field, date picker, color picker, slider, checkboxes, etc.

```html
<!-- ── Text-based inputs ─────────────────────────────────── -->
<input type="text"     placeholder="Free text" />
<input type="email"    placeholder="user@example.com" />     <!-- validates @ format -->
<input type="password" />                                     <!-- text is masked -->
<input type="search"   placeholder="Search..." />             <!-- shows × to clear -->
<input type="tel"      placeholder="+1 (555) 000-0000" />
<input type="url"      placeholder="https://example.com" />  <!-- validates URL format -->

<!-- ── Numeric inputs ────────────────────────────────────── -->
<input type="number"   min="0"   max="100"  step="5" />
<input type="range"    min="1"   max="10"   step="1" value="5" />  <!-- slider -->

<!-- ── Date and time inputs ──────────────────────────────── -->
<input type="date"           />   <!-- calendar picker -->
<input type="time"           />   <!-- time picker -->
<input type="datetime-local" />   <!-- combined date + time -->
<input type="month"          />
<input type="week"           />

<!-- ── Selection inputs ──────────────────────────────────── -->
<input type="checkbox" name="notifications" value="email" />  <!-- multi-select -->
<input type="radio"    name="plan" value="starter" />         <!-- one of group -->
<input type="radio"    name="plan" value="pro"     />
<input type="color"    value="#4285f4" />                     <!-- color picker -->
<input type="file"     accept="image/*,application/pdf" multiple />

<!-- ── Special ───────────────────────────────────────────── -->
<input type="hidden"   name="csrf_token" value="abc123xyz" />  <!-- invisible field -->

<!-- ── Multi-line text ───────────────────────────────────── -->
<textarea name="message" rows="5" placeholder="Your message..."></textarea>

<!-- ── Dropdown select ───────────────────────────────────── -->
<select name="country">
  <option value="">— Select country —</option>
  <optgroup label="Americas">
    <option value="us">United States</option>
    <option value="ca">Canada</option>
    <option value="mx">Mexico</option>
  </optgroup>
  <optgroup label="Europe">
    <option value="gb">United Kingdom</option>
    <option value="de">Germany</option>
  </optgroup>
</select>

<!-- ── Multi-select list ─────────────────────────────────── -->
<select name="skills" multiple size="4">
  <option value="java">Java</option>
  <option value="js">JavaScript</option>
  <option value="python">Python</option>
</select>
```

**Key behaviors:**
- **Mobile keyboards adapt to type:** `type="number"` shows numeric keypad; `type="email"` shows keyboard with `@`; `type="tel"` shows phone keypad
- `name` attribute = key sent to server; required for form submission to include the field
- `value` = initial/sent value; `placeholder` = hint text (not submitted)
- `type="hidden"` — field is not shown to user but IS submitted (commonly used for CSRF tokens, session IDs)
- `<optgroup>` — creates labeled groups inside `<select>` dropdowns

---

## Slide 16: Labels, Fieldsets & Accessible Forms

**Visual:** A styled form with accessibility annotations showing label associations and fieldset grouping.

```html
<!-- ── Explicit label association (preferred method) ────── -->
<label for="username">Username</label>
<input type="text" id="username" name="username" />
<!-- "for" on <label> must match "id" on <input> exactly -->

<!-- Clicking the label now focuses the input — larger click target on mobile -->

<!-- ── Implicit association (wrapping) ──────────────────── -->
<label>
  Email Address
  <input type="email" name="email" />
</label>

<!-- ── Grouping with fieldset + legend ───────────────────── -->
<fieldset>
  <legend>Billing Address</legend>

  <label for="street">Street Address</label>
  <input type="text" id="street" name="billing_street" required />

  <label for="city">City</label>
  <input type="text" id="city" name="billing_city" required />

  <label for="zip">ZIP Code</label>
  <input type="text" id="zip" name="billing_zip"
         pattern="[0-9]{5}" required />
</fieldset>

<!-- ── Radio button group — MUST use fieldset + legend ──── -->
<fieldset>
  <legend>Preferred Contact Method</legend>
  <label>
    <input type="radio" name="contact_pref" value="email" checked />
    Email
  </label>
  <label>
    <input type="radio" name="contact_pref" value="phone" />
    Phone
  </label>
  <label>
    <input type="radio" name="contact_pref" value="text" />
    Text Message
  </label>
</fieldset>

<!-- ── Button types ──────────────────────────────────────── -->
<button type="submit">Submit Form</button>     <!-- submits form -->
<button type="reset">Clear All Fields</button> <!-- resets all inputs to defaults -->
<button type="button">Run JavaScript</button>  <!-- no default action; JS-controlled -->
```

**Accessibility rules (non-negotiable in professional development):**
- **Every `<input>` must have an associated `<label>`** — no exceptions; screen readers announce the label when the input receives focus
- `for` attribute on `<label>` must exactly match `id` attribute on `<input>`
- Clicking a properly associated label focuses the input — this significantly increases the click/tap target on mobile devices
- Radio button groups **require** `<fieldset>` + `<legend>` — screen readers announce "Preferred Contact Method: Email" (the legend context is essential for radio groups)
- `type="button"` is critical — a `<button>` inside a `<form>` defaults to `type="submit"`; always be explicit to prevent accidental submissions
- `<input type="image">` submits coordinates of the click — use `type="submit"` with CSS styling instead

---

## Slide 17: HTML5 Form Validation — Built-In Constraint Checking

**Visual:** A form with various fields and the browser's native validation error tooltips shown for invalid inputs.

```html
<form action="/submit" method="POST">

  <!-- Required: field cannot be empty on submit -->
  <input type="text"  name="name"     required />

  <!-- Text length constraints -->
  <input type="text"  name="username"
         minlength="3" maxlength="20"
         required />

  <!-- Number/date value constraints -->
  <input type="number" name="age"     min="18" max="120" required />
  <input type="date"   name="checkin" min="2025-01-01"   required />

  <!-- Pattern validation (regular expression) -->
  <input type="text"
         name="zipcode"
         pattern="[0-9]{5}(-[0-9]{4})?"
         title="US ZIP: 12345 or 12345-6789"
         required />

  <input type="text"
         name="username_strict"
         pattern="[a-zA-Z0-9_]{3,16}"
         title="3-16 characters: letters, numbers, underscores only"
         required />

  <!-- Type-based auto-validation (no extra attributes needed) -->
  <input type="email" name="email"   required />  <!-- validates @ and domain -->
  <input type="url"   name="website"            />  <!-- validates URL format -->

  <!-- Disable browser validation (handle yourself) -->
  <!-- <form novalidate> disables all built-in checking -->

  <button type="submit">Submit</button>
</form>
```

**How validation works:**
- Validation fires on form **submit** — before any JavaScript runs, before any network request
- `required` — prevents submission with empty field
- `minlength` / `maxlength` — character count bounds for text inputs and textarea
- `min` / `max` — value bounds for number, date, range, time inputs
- `pattern` — the field value must fully match the regex; `title` attribute provides the error message hint
- `type="email"` — automatically validates that the format looks like `user@domain.tld`
- `type="url"` — automatically validates URL format
- Browsers display native validation UI (varies by browser); styleable with `:invalid` and `:valid` CSS pseudo-classes (covered in Part 2)

**⚠️ The most important rule in all of web security:**
HTML validation is client-side only. A malicious user can open DevTools, remove `required` attributes, and submit anything. A determined user can send raw HTTP requests with no browser at all. **Always validate on the server.** HTML validation is for user experience — catching mistakes before submission. Server validation is for security — never trust data from the client.

---

## Slide 18: Common HTML Mistakes — What to Avoid

**Visual:** Two-column table with ❌ mistake and ✅ fix side-by-side.

| ❌ Common Mistake | ✅ Correct Approach |
|------------------|-------------------|
| `<img src="photo.jpg">` (no `alt`) | `<img src="photo.jpg" alt="Descriptive text">` |
| Using `<table>` for page layout | Use CSS Flexbox or Grid |
| `<h1>Title</h1><h3>Sub</h3>` (skipped h2) | `<h1>Title</h1><h2>Sub</h2>` — never skip levels |
| `<br><br><br>` for vertical spacing | Use CSS `margin` or `padding` |
| `<b>Important</b>` instead of `<strong>` | `<strong>Important</strong>` for semantic weight |
| `<input>` with no `<label>` | Always associate a label with every input |
| `<a href="..." target="_blank">` no rel | Add `rel="noopener noreferrer"` always |
| `<div>` wrapping every group | Use semantic elements: `<header>`, `<nav>`, `<main>`, etc. |
| `<span style="font-size:24px">Heading</span>` | Use `<h2>` and style with CSS |
| Missing `<!DOCTYPE html>` | Always include as the very first line |
| `<html>` without `lang` attribute | `<html lang="en">` for accessibility |
| `<form>` without method | Always specify `method="GET"` or `method="POST"` |
| Nesting `<a>` inside `<a>` | Links cannot contain other links |
| `<input>` inside `<a>` | Don't nest interactive elements |

**The biggest mistake of all:** Writing HTML that only looks right in Chrome. **Always validate your HTML** using the W3C Markup Validation Service (validator.w3.org) — it catches nesting errors, missing attributes, and typos that the browser silently "fixes" in ways you don't expect.

---

## Slide 19: A Complete Production HTML Page

**Visual:** Full-page view of a rendered "Course Registration" form page next to the complete HTML file.

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Register for a Course — Software Academy</title>
  <meta name="description" content="Enroll in one of our expert-led software engineering courses." />
  <link rel="stylesheet" href="styles.css" />
</head>
<body>

  <header>
    <a href="/" class="logo">Software Academy</a>
    <nav aria-label="Main navigation">
      <ul>
        <li><a href="/">Home</a></li>
        <li><a href="/courses">Courses</a></li>
        <li><a href="/about">About</a></li>
        <li><a href="/contact">Contact</a></li>
      </ul>
    </nav>
  </header>

  <main>
    <h1>Register for a Course</h1>
    <p>Fill out the form below to enroll. All fields marked * are required.</p>

    <form action="/api/register" method="POST">

      <fieldset>
        <legend>Personal Information</legend>

        <label for="full-name">Full Name *</label>
        <input type="text" id="full-name" name="full_name"
               autocomplete="name" required minlength="2" maxlength="100" />

        <label for="email">Email Address *</label>
        <input type="email" id="email" name="email"
               autocomplete="email" required />

        <label for="phone">Phone Number</label>
        <input type="tel" id="phone" name="phone"
               autocomplete="tel" placeholder="+1 (555) 000-0000" />
      </fieldset>

      <fieldset>
        <legend>Course Selection</legend>

        <label for="course">Course *</label>
        <select id="course" name="course" required>
          <option value="">— Select a course —</option>
          <optgroup label="Beginner">
            <option value="html-css">HTML & CSS Fundamentals</option>
            <option value="java-intro">Introduction to Java</option>
          </optgroup>
          <optgroup label="Advanced">
            <option value="spring-boot">Spring Boot API Development</option>
            <option value="react">React & Redux</option>
          </optgroup>
        </select>

        <label for="start-date">Preferred Start Date *</label>
        <input type="date" id="start-date" name="start_date"
               min="2025-02-01" required />
      </fieldset>

      <fieldset>
        <legend>Communication Preferences</legend>
        <label>
          <input type="checkbox" name="newsletter" value="yes" />
          Send me course updates and newsletters
        </label>
        <label>
          <input type="checkbox" name="sms_alerts" value="yes" />
          Send me SMS reminders before class sessions
        </label>
      </fieldset>

      <div class="form-actions">
        <button type="submit">Complete Registration</button>
        <button type="reset">Clear Form</button>
      </div>
    </form>
  </main>

  <footer>
    <p>&copy; 2025 Software Academy. All rights reserved.</p>
    <nav aria-label="Footer">
      <a href="/privacy">Privacy Policy</a>
      <a href="/terms">Terms of Service</a>
      <a href="/contact">Contact Us</a>
    </nav>
  </footer>

</body>
</html>
```

This page demonstrates every concept from Part 1 working together: semantic structure, proper heading hierarchy, accessible form with fieldsets, full input type variety, HTML5 validation, and label associations.

---

## Slide 20: Browser Developer Tools — Your Primary Debugging Tool

**Visual:** Screenshot of Chrome DevTools open on a webpage with the Elements panel active, annotated with callouts.

**Opening DevTools:**
- **Windows/Linux:** F12 or Ctrl+Shift+I
- **Mac:** Cmd+Option+I
- **Inspect a specific element:** Right-click → Inspect

**Elements Panel — what you can do:**

| Feature | How to use |
|---------|-----------|
| View DOM tree | Expand/collapse nodes with ▶ arrows |
| Find element on page | Click any node → it highlights in the browser viewport |
| Edit HTML in place | Double-click any element text to edit it live |
| Add/edit attributes | Double-click an attribute to modify it |
| Delete an element | Right-click → Delete element |
| Inspect computed box model | Bottom of Styles panel — visual padding/border/margin diagram |
| View all applied CSS rules | Styles panel (right side) — shows all matching rules |

**The key insight:** DevTools shows you the **live DOM** — the result after the browser has parsed and potentially "fixed" your HTML, and after any JavaScript modifications. It's not the raw source file. When something looks wrong, DevTools tells you what the browser actually built.

**The Computed tab:** Shows the final resolved value for every CSS property on the selected element — extremely useful for debugging specificity conflicts (you'll understand this fully in Part 2).

**Practical workflow:**
1. Notice something looks wrong on the page
2. Right-click the problem area → Inspect
3. See the DOM structure — is it nested correctly?
4. Check the Styles panel — what CSS rules apply?
5. Edit live in DevTools to test a fix
6. Copy the fix back to your actual code file

---

## Slide 21: Part 1 Summary + Part 2 Transition

**Visual:** A clean checklist with all Part 1 topics ticked off, and a visual preview strip showing what CSS can do (before/after an unstyled vs styled version of the form from Slide 19).

**Part 1 Complete — You Now Know:**
- ✅ **HTML document skeleton** — DOCTYPE, html, head, body — and what goes in each section
- ✅ **The DOM** — the browser's in-memory tree representation of your HTML
- ✅ **Block vs inline elements** — how elements flow in the document
- ✅ **Semantic HTML5** — `header`, `nav`, `main`, `section`, `article`, `aside`, `footer` — and why they matter for accessibility and SEO
- ✅ **All text content elements** — headings (h1-h6), paragraphs, emphasis, code, quotes
- ✅ **Lists** — unordered, ordered, description
- ✅ **Links and images** — with correct alt text and secure external link handling
- ✅ **Tables** — for data only, with proper semantic sections and accessibility
- ✅ **Forms** — all input types, labels, fieldsets, buttons, and HTML5 built-in validation

**Coming up in Part 2 — CSS:**
- How CSS selectors find and target HTML elements
- The cascade and specificity — why one rule wins over another
- The Box Model — how every element is a box with content, padding, border, and margin
- Positioning — how to place elements exactly where you need them
- **Flexbox** — the modern one-dimensional layout system
- **CSS Grid** — the modern two-dimensional layout system
- Responsive design — making your page look right on phones, tablets, and desktops
- CSS animations and transitions — bringing the page to life
- Bootstrap — a CSS framework for building UIs faster

**The visual transformation:** The form from Slide 19 without CSS looks like a plain 1990s webpage. With CSS, it can look like a polished, modern SaaS application. That transformation happens in the next 60 minutes.
