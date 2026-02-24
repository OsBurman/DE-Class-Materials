# Day 11 — Part 1 Walkthrough Script
## HTML Document Structure · Semantic Markup · Tables · Forms
**Estimated time: ~90 minutes**

---

## Opening (3 min)

> "Welcome to Week 3. We are switching gears completely — no more Java. For the next three weeks we are building the web layer of a full-stack application. Today is HTML and CSS day — the bones and the skin of every web page you have ever visited."

> "Here's the mental model I want you to carry: HTML is a tree. The browser reads your file, builds a tree of objects in memory — called the DOM — and then renders it. Every tag you write becomes a node in that tree. CSS then styles the nodes. JavaScript then interacts with them. We're starting at the root today."

**[ASK]** "Before we look at a single line of code — what do you think the browser does the moment you hit Enter on a URL?"
- Accept any answers. Guide towards: DNS lookup → TCP connection → HTTP request → server sends HTML → browser parses and builds DOM → fetches CSS/JS → renders

---

## FILE 1 — `01-html-structure-and-semantics.html` (~50 min)

### 1.1 — The DOM Tree Diagram (5 min)

**[ACTION]** Draw this on the board before opening the file:

```
Document
└── html
    ├── head  ← metadata (title, charset, CSS links — nothing visible)
    └── body  ← everything users see
        ├── header
        │   └── nav
        ├── main
        │   ├── section
        │   └── article
        └── footer
```

> "This is the Document Object Model. Every HTML file creates exactly this kind of tree. `document` is the root object in JavaScript — you'll use `document.querySelector()` later this week to grab any node in this tree."

> "Notice the two children of `<html>`: `<head>` and `<body>`. The head is invisible metadata. The body is everything your user actually sees."

---

### 1.2 — DOCTYPE and `<html>` (3 min)

Open `01-html-structure-and-semantics.html`. Point to line 1.

> "The very first line — `<!DOCTYPE html>` — is not actually a tag. It's an instruction to the browser saying: parse this file in HTML5 mode. Without it, browsers fall back to 'quirks mode', which is a compatibility mode from the 1990s where CSS behaves differently. Always include it."

> "Next: `<html lang='en'>`. The `lang` attribute tells screen readers which language to use for pronunciation. Accessibility rule: always set it."

---

### 1.3 — The `<head>` section (5 min)

> "Let's look at the head section. These three meta tags are the minimum every real page needs."

Walk through each:
- `<meta charset="UTF-8">` — "Without this, characters like ñ, ü, emoji get corrupted. UTF-8 covers every character in every language."
- `<meta name="viewport" ...>` — "This is the responsive web design meta tag. Without it, mobile browsers zoom out to show the desktop version. `width=device-width` says: use the actual screen width. `initial-scale=1.0` says: don't zoom."
- `<title>` — "This appears in the browser tab and in Google search results. It's one of the most important SEO elements on your page."

**⚠️ WATCH OUT:** "A very common mistake: putting visible content in the `<head>`. The browser won't render it. Head = metadata only."

---

### 1.4 — Semantic Structure: `<header>`, `<nav>`, `<main>`, `<footer>` (7 min)

> "Now the body. I want you to see two ways to write the same structure."

Write on the board:
```html
<!-- Non-semantic (meaningless) -->          <!-- Semantic (meaningful) -->
<div id="header">...</div>                   <header>...</header>
<div id="nav">...</div>                      <nav>...</nav>
<div id="content">...</div>                  <main>...</main>
<div id="footer">...</div>                   <footer>...</footer>
```

> "Both render identically in the browser. The difference is meaning. The semantic version tells screen readers, search engines, and your colleagues exactly what each section is for."

> "A screen reader user can hit a keyboard shortcut to jump directly to the `<nav>`, then to `<main>`, then to `<footer>`. With `<div id='nav'>` they can't do that."

**[ASK]** "How many `<main>` elements should a page have?"
→ Exactly one. It marks the primary content. `<header>` and `<footer>` can appear multiple times (once per `<article>` for example).

Point to the `<header>` → `<h1>` relationship in the file.

> "The `<h1>` inside `<header>` is the main heading for the entire page. Only one `<h1>` per page — this is important for SEO. Then `<h2>` for sections, `<h3>` for subsections, and so on. Never skip levels — don't go from `h1` to `h4`."

---

### 1.5 — Headings and Paragraphs (5 min)

Point to the `<article>` section with headings.

> "Two text-level elements you'll use constantly: `<strong>` and `<em>`. Strong means important — bold by default. Em means emphasis — italic by default. Do NOT use `<b>` and `<i>` — those are presentational with no semantic meaning."

Point to the inline elements inside the paragraph:
- `<code>` — "For inline code snippets. Use `<pre><code>` for multi-line code blocks."
- `<abbr title="...">` — "Defines an abbreviation. The `title` attribute shows on hover."
- `<mark>` — "Highlighted text — like using a yellow marker."
- `<time datetime="...">` — "Wraps dates/times. The `datetime` attribute gives the machine-readable ISO format — important for search engines and calendar parsers."
- `<q>` — "Inline quote — browser adds quotation marks automatically."

**⚠️ WATCH OUT:** "The `<br />` tag is an inline element for a line break. It's tempting to use for spacing — don't. Spacing should always be done with CSS margin and padding. `<br />` is for content that genuinely has a line break, like a postal address or a poem."

---

### 1.6 — Lists (8 min)

> "Three kinds of lists in HTML. Let me show you each and when to use them."

Point to `<ul>`:
> "Unordered list — when the order doesn't matter. Navigation menus are almost always `<ul>`, even if they look horizontal. Items inside are always `<li>` — that is the ONLY valid direct child of `<ul>`."

**⚠️ WATCH OUT:** "Don't put anything except `<li>` directly inside a `<ul>` or `<ol>`. No `<div>`, no `<p>`. The browser will try to fix it, but the DOM ends up broken."

Point to `<ol>`:
> "Ordered list — when sequence matters. Perfect for steps, instructions, rankings."

Point to the nested list:
> "You can nest lists inside list items. Just make sure the inner `<ul>` or `<ol>` is inside the `<li>`, not after it."

Point to `<dl>`:
> "Description list — underused but very useful. `<dt>` is the term, `<dd>` is the definition. Great for glossaries, key-value data, FAQ pages."

---

### 1.7 — Links and Images (7 min)

**Links:**

> "The anchor tag `<a>` is an inline element. The `href` attribute is its reason for existing."

Walk through the four link types:
1. Absolute URL — "Full URL including protocol. For external sites."
2. Relative URL — "Path within the same site. Browser resolves it relative to the current page."
3. Anchor link `href="#contact"` — "Jumps to the element with `id='contact'` on the same page. This is how 'back to top' buttons work."
4. `mailto:` link — "Opens the user's email client."

> "Two attributes on the external link: `target='_blank'` opens a new tab. `rel='noopener noreferrer'` is a security requirement — without it, the new tab can access your page via `window.opener` and redirect it. Always add this when using `target='_blank'`."

**[ASK]** "What happens if you leave out the `href` attribute on an `<a>` tag?"
→ It renders as text but it's not clickable. It's also not keyboard-navigable. Always include `href`.

**Images:**

Point to `<figure>` and `<img>`:
> "The `<img>` tag has no closing tag — it's a void element. The two required attributes are `src` (path to the image) and `alt` (alternative text)."

> "Alt text is not optional. If the image fails to load, the alt text shows. Screen readers read it aloud. Search engines index it. Rule: describe what the image shows, not just 'image of X'. If the image is purely decorative, use `alt=''` — empty string, not missing entirely."

> "Always add `width` and `height` attributes matching the image's aspect ratio. The browser reserves the space before the image loads, preventing layout shift — this improves your Core Web Vitals score."

> "`<figure>` wraps a self-contained piece of media. `<figcaption>` provides the visible caption. This association is semantic — screen readers announce the caption with the image."

---

### 1.8 — Other Semantic Elements (5 min)

Walk through quickly:
- `<aside>` — "Tangentially related content — sidebars, pull quotes, related articles."
- `<details>` + `<summary>` — "Native browser disclosure widget. Click to expand/collapse — no JavaScript needed. Great for FAQs."
- `<blockquote>` — "For long quotations from another source. The `cite` attribute can hold the URL."
- `<hr />` — "Thematic break — a shift in topic. Renders as a horizontal line."
- `<address>` — "Contact info for the nearest `<article>` or the whole page. Not for postal addresses in general — only for the author/publisher's contact info."

**→ TRANSITION:** "You now have the structure. Let's look at tables and forms — the two most complex HTML components."

---

## FILE 2 — `02-html-tables-and-forms.html` (~37 min)

### 2.1 — Tables: Structure (8 min)

Open `02-html-tables-and-forms.html`. Point to the first table.

> "Tables are for tabular data — data that naturally belongs in rows and columns, like a spreadsheet. Tables are NOT for page layout. We used to build entire websites with table layout in the 2000s — it was a nightmare. CSS Grid replaced that need entirely."

Draw on the board:
```
<table>
  <caption> ← title
  <thead>   ← header rows
  <tbody>   ← data rows (required even if you don't write it — browser adds it)
  <tfoot>   ← summary/totals rows
    <tr>    ← table row
      <th>  ← header cell
      <td>  ← data cell
```

Point to `scope="col"` on `<th>`:
> "The `scope` attribute tells screen readers which direction this header applies — `col` means it's a column header, `row` means it's a row header. Without it, a screen reader in a complex table can't tell which header labels which data."

Point to `<caption>`:
> "Caption goes right inside `<table>`, before `<thead>`. Browsers render it above the table. Screen readers announce it before reading the table. It replaces the need for a separate heading tag before the table."

Point to `<tfoot>`:
> "tfoot comes BEFORE tbody in the source — that's counterintuitive. The browser renders it at the bottom regardless. The reason: large tables can be paginated across print pages, and having the footer defined early lets it appear on each page."

**[ASK]** "What does `colspan='2'` do?"
→ Makes a cell span two columns wide. Show the tfoot example. Draw it on the board.

Point to the second table with `rowspan`:
> "Rowspan is the vertical equivalent — a cell spans multiple rows. The trick: in each subsequent row, you skip the `<td>` for that column because the rowspanned cell already covers it. If you include it, the table shifts and breaks."

---

### 2.2 — Forms: Structure and Accessibility (5 min)

Point to the `<form>` tag:
> "Three key attributes: `action` is where the data goes (a URL). `method` is GET or POST. POST sends data in the request body — use this for login forms, registration, anything sensitive. GET puts the data in the URL — use for search forms, where you want the URL to be shareable."

> "`novalidate` on this form disables the browser's built-in validation so we can demo each input freely. In production you usually leave validation on."

Point to the `<fieldset>` + `<legend>` pattern:
> "Fieldset groups related inputs visually and semantically. Legend is the label for the group. Screen readers announce the legend before each input inside it: 'Personal Information — Full Name'. Without this grouping, inputs just appear one after another with no context."

---

### 2.3 — Labels and Input Types (10 min)

**LABELS — most important accessibility rule:**

Point to the `<label for="full-name">` and `<input id="full-name">`:
> "The `for` attribute on the label must exactly match the `id` on the input. This creates an association: clicking the label focuses the input. Screen readers announce the label when the input is focused. Without this, screen reader users have no idea what a text box is for."

**⚠️ WATCH OUT:** "Using `placeholder` as a label substitute is one of the most common accessibility mistakes. Placeholder text disappears the moment you start typing. Use a real `<label>` always."

Walk through the input types in the Personal Information fieldset:

| Type | What it does |
|---|---|
| `type="text"` | Basic text, single line |
| `type="email"` | Validates `@` + domain; shows email keyboard on mobile |
| `type="password"` | Masks characters |
| `type="date"` | Calendar picker; value is always `yyyy-mm-dd` |
| `type="tel"` | No built-in format validation; shows number keyboard on mobile |
| `type="url"` | Must start with `http://` or `https://` |

Move to Course Preferences:

| Type | What it does |
|---|---|
| `<select>` | Dropdown; `<option value="">` with empty value is the placeholder |
| `type="number"` | `min`, `max`, `step` constrain values |
| `type="range"` | Slider; `oninput` updates a live label showing the value |
| `type="radio"` | Grouped by `name`; only one selectable per group |
| `type="checkbox"` | Independent; multiple selectable |

**[ASK]** "What's the key difference between radio and checkbox?"
→ Radio = one-of-many (same `name`). Checkbox = any-of-many (independent).

Point to the `checked` attribute on the Frontend checkbox:
> "`checked` is a boolean attribute — its presence means true. You don't write `checked='true'`, just `checked`."

---

### 2.4 — Validation Attributes (7 min)

Point to the password field:
> "HTML5 gives us built-in validation. The browser checks these before allowing the form to submit. No JavaScript needed for basic cases."

Walk through each validation attribute:
- `required` — "Field can't be empty on submit."
- `minlength="8"` — "At least 8 characters."
- `pattern="..."` — "Must match this regular expression. We'll see regex properly in JavaScript week, but you can write simple patterns now."
- `min` / `max` on number/date — "Hard boundaries."
- `step` on range/number — "Only values that are multiples of step are valid."

> "When validation fails, the browser shows a tooltip — the exact style varies per browser. If you want custom error messages and styling, you'll use the Constraint Validation API in JavaScript."

Point to `type="file"` with `accept`:
> "The `accept` attribute is a hint to the file picker — it filters which files the user sees. `.pdf,.doc,.docx` restricts to those types. But it's a hint only — users can override it. Always validate file types server-side too."

Point to `type="hidden"`:
> "Hidden fields are invisible to the user but sent with the form data on submit. Common uses: CSRF tokens (security), version numbers, pre-populated IDs from the server."

---

### 2.5 — Buttons (3 min)

Point to the button row:
> "Three button types."

- `type="submit"` — "Triggers the browser's validation pass, then submits the form. Default behaviour if you omit `type` — which is why buttons accidentally submit forms inside forms."
- `type="reset"` — "Resets all inputs to their default values. Rarely useful in production — most users hate it because one click destroys everything they typed."
- `type="button"` — "Does nothing without JavaScript. Safe to use inside a form without worrying about accidental submission."

**⚠️ WATCH OUT:** "This trips everyone up eventually: if you put a `<button>` inside a `<form>` without a `type` attribute, it defaults to `type='submit'`. A button that's just for opening a modal or toggling something will accidentally submit the form. Always write `type='button'` explicitly for non-submit buttons."

---

### 2.6 — Input Type Reference and Wrap-Up (4 min)

Point to the reference comment block at the bottom:
> "That's the full inventory of HTML5 input types. You don't need to memorise all of them today — just know they exist and where to find them. The critical ones for forms you'll build: text, email, password, number, date, radio, checkbox, select, textarea, file."

**Wrap-Up Part 1 Q&A (5 min)**

Ask:
1. "What's the difference between `<strong>` and `<b>`?"
   → `<strong>` = semantic importance; `<b>` = presentational boldness, no meaning.

2. "A `<div>` and a `<section>` look identical in the browser. When would you choose `<section>` over `<div>`?"
   → Use `<section>` when the content has its own heading and could appear in a table of contents. Use `<div>` purely for CSS/JS grouping with no semantic meaning.

3. "What happens if you forget `alt=""` on a decorative image?"
   → Screen readers might read the file name, which is meaningless noise for users.

4. "Checkboxes and radio buttons — what attribute groups radio buttons together?"
   → The `name` attribute. All radios with the same `name` form one group.

> "Great. Open those HTML files in your browser while you tackle the exercises. See how the rendering changes as you tweak the markup. In Part 2 we add all the visual styling — that's where things get exciting."

---

## Handoff to Exercises

> "Exercises for Part 1: build a personal profile page using all the semantic elements we covered, and build a job application form with at least five different input types and full validation. No CSS yet — raw HTML only."
