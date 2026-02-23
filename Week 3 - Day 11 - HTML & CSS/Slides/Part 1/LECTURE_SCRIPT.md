# Week 3 - Day 11: HTML & CSS
## Part 1 Lecture Script — HTML Foundations
### 60-Minute Verbatim Delivery Script

---

**Delivery notes:** ~165 words/minute. Timing markers every 2 minutes. All code examples should be live-typed or shown on screen as described. Pause at [PAUSE] markers for student questions or reactions.

---

## [00:00–02:00] Welcome to Week 3 — The Web Begins Here

Good morning, everyone, and welcome to Week 3. Let's take just a moment to acknowledge what you've accomplished — two full weeks of Java. You understand objects and classes. You can write multithreaded code. You've worked with generics and lambdas and streams. That's genuinely impressive, and I don't want you to lose sight of it.

Today we make a complete pivot. We're leaving the JVM behind — at least for this week — and we're entering the browser. This is your frontend week, and it starts with the three languages of the web.

Look at this slide. HTML. CSS. JavaScript. These are literally the only languages that run natively in every browser on the planet — no installation, no compilation step, no runtime to download. And each one has a completely different job.

HTML is structure — it defines *what content exists* on the page. Headings, paragraphs, images, links, forms — that's HTML's job. CSS is presentation — it defines *how that content looks*. Colors, fonts, layouts, animations. And JavaScript is behavior — it defines *what content does* when you interact with it. JavaScript is Day 12. Today we own HTML and CSS.

Here's the mental shift I want you to make: HTML is NOT a programming language. It has no variables, no loops, no conditions, no algorithms. It's a markup language — you're describing the structure of content, not telling a computer what to compute. Coming from Java, this will feel strange at first, almost too simple. Trust me, professional HTML takes real skill, and you'll see exactly why by the time we finish today.

## [02:00–04:00] The HTML Document Skeleton

Let me show you the first thing every HTML developer memorizes — the document skeleton. This is the code on Slide 3, and every single HTML file you will ever write starts with this exact structure. Not a similar structure. This exact one.

First line: `<!DOCTYPE html>`. Those angle brackets might make you think it's an HTML tag, but it's not. It's a document type declaration. It tells the browser: "Use HTML5 standards mode." Without it, browsers enter something called "quirks mode" — a backwards-compatibility mode that emulates the broken behavior of 1990s browsers. You don't want that. It's always first, always lowercase.

Then `<html lang="en">`. This is the root element that wraps everything. The `lang` attribute is not optional — it tells screen readers what language to use when reading the page aloud, and it tells translation services what language to auto-translate from. Always include it.

Inside `<html>` you have exactly two children: `<head>` and `<body>`. The `<head>` contains metadata — information about the page that is not displayed to users. The `<body>` contains everything the user actually sees. That split is fundamental: head is for the browser, body is for the user.

I want you to open a text file right now, type this skeleton, save it as `index.html`, and drag it into a browser. You just built a webpage. It doesn't look like much yet, but it's valid, correct HTML.

## [04:00–06:00] What Goes in the `<head>`

Let's look at what belongs inside `<head>`, because these elements are invisible to users but they do critical work.

The first element inside `<head>` should always be `<meta charset="UTF-8" />`. This tells the browser how to decode the text in the file. UTF-8 handles English, French, Japanese, Arabic, emojis — basically all human writing. Without this, special characters render as garbled nonsense. I've seen this on real production sites — when the encoding is wrong, your user sees question marks and boxes instead of accented letters. Always first, always UTF-8.

Next: `<meta name="viewport" content="width=device-width, initial-scale=1.0" />`. This is your mobile responsiveness switch. Without it, mobile browsers will zoom out and show a tiny shrunken desktop-size page. With it, they render at the device's actual width. If your page doesn't have this tag, it will look broken on every phone in existence. This is non-negotiable for any page that humans will actually view.

Then `<title>Your Page Title</title>`. This appears in the browser tab. It also appears as the clickable headline in Google search results. Every page needs a unique, descriptive title — not "Untitled Document," not just your company name.

`<link rel="stylesheet" href="styles.css" />` attaches your external CSS file. The browser fetches and applies it before rendering the page. `<script src="app.js" defer></script>` loads your JavaScript — `defer` means "download in the background, but execute after the full HTML has been parsed," which prevents errors from running before your elements exist. We'll use defer when we get to JavaScript.

## [06:00–08:00] The DOM — The Browser's Mental Model

Let me introduce a concept that will underpin everything you do in frontend development: the DOM — the Document Object Model.

When a browser loads an HTML file, it doesn't just display the text. It **parses the HTML and builds a tree structure in memory.** That tree is the DOM. Every element becomes a node. Every node has a parent, potentially children, and potentially siblings. The DOM is how the browser thinks about your page internally.

Look at the tree diagram in Slide 5. `document` is the root — it wraps everything. Inside `document` is `html`. Inside `html` are `head` and `body`. Inside `body` are `header`, `main`, and `footer`. Inside `header` is `nav`. Inside `nav` is `ul`. Inside `ul` are `li` elements. Each `li` contains an `a` element. It's a tree — exactly like the data structure you've seen in computer science.

This matters for three reasons. First: **rendering**. The browser walks the DOM tree to decide what to draw on screen. Second: **CSS**. CSS selectors traverse this tree to find elements to style — "find all `<a>` elements that are inside a `<nav>` element" is a tree traversal. Third: **JavaScript**. In Day 13, you'll use `document.querySelector()` to find nodes in this tree and modify them, and `addEventListener()` to respond when users interact with them.

Today's scope is: understand the tree structure that HTML creates. Day 13 is where the manipulation happens.

## [08:00–10:00] Elements, Tags, and Attributes

Let's nail down the vocabulary. When I say "HTML element," I mean the full thing: an opening tag, content, and a closing tag. When I say "tag," I mean just the markup part — the angle brackets with the name inside. When I say "attribute," I mean a key-value pair that lives inside the opening tag.

Look at this `<a>` element. The opening tag is `<a>`. Inside the opening tag are two attributes: `href="https://example.com"` and `target="_blank"`. The content is the text "Visit Example Site." The closing tag is `</a>`. The whole assembly is the element.

Now look at `<img>`. It has attributes — `src`, `alt`, `width`, `height` — but it has no closing tag and no content. This is called a **void element**, or self-closing element. You cannot put children inside an `<img>`. Same with `<input>`, `<br>`, `<hr>`, `<meta>`, and `<link>`. These elements exist in name only — they represent a single thing (an image, a line break, a form field) rather than wrapping content.

A few rules. Attributes live only in the opening tag, never the closing tag. Attribute values should always be quoted — double quotes are standard. HTML is case-insensitive, but lowercase is the universal professional convention. And proper nesting matters: if you open `<p>` then `<strong>`, you must close `<strong>` before you close `<p>`. If you close them in the wrong order, you have invalid HTML, and the browser will try to repair it in unpredictable ways.

## [10:00–12:00] Block vs Inline Elements

Here's one of the most important conceptual distinctions in HTML, and it's the foundation for understanding CSS layout. Every HTML element is either block-level or inline by default.

**Block elements** start on a new line and take up the full available width of their container. Put two block elements next to each other in HTML and they stack vertically. `<div>`, `<p>`, `<h1>`, `<ul>`, `<table>`, `<form>` — all block elements. They're the building blocks of page structure.

**Inline elements** flow within text. They don't force a new line. They're only as wide as their content. `<span>`, `<a>`, `<strong>`, `<em>`, `<code>`, `<img>` — all inline elements. They're the details within a block.

Look at the code example. When I have a `<p>` element containing `<strong>bold text</strong>`, `<em>italic text</em>`, a `<a href="#">link</a>`, and `<code>some code</code>` — all of those inline elements flow within the paragraph. The paragraph (block) provides the container; the inline elements add detail inside it.

There's a nesting rule: you cannot put a block element inside an inline element. Technically, you cannot put a `<div>` inside a `<span>`. You can put a `<span>` inside a `<div>`. Browsers will try to fix invalid nesting, but the result is unpredictable.

And here's the key point for CSS: the `display` property can override these defaults. You can make an `<a>` display as a block. You can make a `<div>` display as inline. The HTML default is just a starting point — CSS gives you full control, and you'll see exactly how in Part 2.

## [12:00–14:00] Why Semantic HTML Matters

Look at the two versions of the same page on Slide 8. On the left, every structural element is a `<div>` with an id or class: `<div id="header">`, `<div id="nav">`, `<div class="main-content">`. On the right, the same structure using semantic HTML5 elements: `<header>`, `<nav>`, `<main>`.

They look identical in a browser — at least visually, to a sighted user. But to everything else that processes that page — screen readers, search engines, browser accessibility tools, and other developers reading the code — they are completely different.

Here's why semantic HTML is the professional standard, and I want to give you four concrete reasons because I want this to stick.

**Reason one: accessibility.** When a screen reader encounters `<nav>`, it announces: "navigation landmark." Users who are blind can jump between landmarks — navigation, main content, aside, footer — using keyboard shortcuts. With `<div id="nav">`, the screen reader just sees an undifferentiated container and reads everything in linear order. The page becomes inaccessible.

**Reason two: SEO.** Search engines understand semantic structure. They give `<article>` content more weight as primary page content. They recognize `<nav>` as navigation and deprioritize it for content ranking. Semantic markup is a direct input to your Google search ranking.

**Reason three: readability.** When a developer six months from now opens your file and sees `<nav>`, they instantly understand. `<div id="nav-container-wrapper">` requires reading, context, and inference. Every second saved reading code is money and sanity saved.

**Reason four: legal compliance.** WCAG — the Web Content Accessibility Guidelines — are the international standard for web accessibility. In the US, UK, and EU, many organizations are legally required to meet WCAG standards for public-facing websites. Semantic HTML is the foundation.

## [14:00–16:00] HTML5 Semantic Layout Elements

Let's go through each semantic layout element systematically. I'll show you the full page structure, then break down each element's role.

`<header>` is introductory content. It can be the site-wide header at the top of the page — containing your logo, navigation, hero section. But it can also appear inside an `<article>` or `<section>` as that section's header. Multiple `<header>` elements on one page is perfectly valid.

`<nav>` is for navigation links — your main menu, breadcrumbs, a table of contents. A page can have more than one `<nav>` element. When you have multiple, add `aria-label` attributes to distinguish them: `aria-label="Main navigation"` and `aria-label="Footer navigation"`.

`<main>` is the primary, unique content of the page. There should be **only one `<main>` per page**. Screen reader users often activate "skip to main content" links that jump directly to this element, bypassing the header and navigation.

`<section>` is a thematic grouping of related content within `<main>`. It should always have a heading element inside it. If you're using `<section>` without a heading, you probably want `<div>` instead.

`<article>` is for self-contained, independently distributable content. A blog post. A news article. A product card. A comment. If you could pick it up and put it on a different website and it would still make sense on its own — that's an `<article>`.

`<aside>` is tangentially related content — a sidebar, a pull quote, a list of related articles, an advertisement. Semantically, it's content that's related but not essential to understanding the main content.

`<footer>` appears at the end — the site-wide footer for copyright, policy links, contact info, social links. Like `<header>`, it can also appear inside `<article>` or `<section>`.

## [16:00–18:00] Text Content Elements

Now let's cover the elements for text content — these are the elements you'll use every single day.

Headings first. `<h1>` through `<h6>` define your document's outline. Screen readers use them for navigation — users can jump from heading to heading to skim. Search engines use them to understand page structure. The `<h1>` is the main page title, and there should be exactly one per page. Then `<h2>` for major sections, `<h3>` for subsections, and so on.

The most common mistake with headings is using them for visual size. I see developers write `<h3>` because they want medium-sized text, even when it's not a subsection heading. Don't do this. If you want specific text size, use CSS. Use headings to describe document hierarchy — not visual appearance. And never skip levels. Going from `<h1>` to `<h3>` (skipping `<h2>`) breaks the document outline for screen readers and assistive technology.

For body text, `<p>` is your workhorse. Every paragraph of prose goes in a `<p>` element. Browsers add top and bottom margin automatically, so paragraphs visually separate.

For emphasis: use `<strong>` when text is critically important — screen readers will stress the word when reading. Use `<em>` for stressed emphasis — the kind you'd add when speaking. Use `<b>` and `<i>` only when you want the visual effect without semantic meaning (like a technical term in italics, or a product name in bold).

For code, `<code>` is for inline snippets — a variable name, a method call within a sentence. For code blocks, wrap a `<pre>` around `<code>` — the `<pre>` element preserves whitespace and line breaks, giving you properly formatted multi-line code display.

## [18:00–20:00] Lists

Three types of lists. Let's go through them.

`<ul>` — unordered list — for items where the sequence doesn't matter. You're saying: here's a collection of things. Navigation menus across the entire internet are almost universally `<ul>` elements — the list is semantic even if the bullets are hidden with CSS.

`<ol>` — ordered list — for items where sequence matters. Steps in a process. Numbered instructions. Rankings. The browser renders them with numbers by default. You can customize starting number with `start="3"` and reverse the order with the `reversed` attribute.

`<dl>` — description list — the underused one. It's for term-definition pairs: a glossary, an FAQ, metadata display, technical specifications. `<dt>` is the term, `<dd>` is the description. Multiple `<dd>` per `<dt>` is valid.

All lists can be nested. Nest a `<ul>` inside an `<li>` to create a sub-list. Practical rule: don't go deeper than three levels — it becomes hard to navigate visually and even harder to navigate with a screen reader.

One pattern I want you to internalize: **navigation menus are lists.** If you have a site header with five navigation links, that's semantically a `<nav>` containing a `<ul>` with five `<li>` elements, each containing an `<a>`. The bullets and vertical/horizontal layout come from CSS. The semantic structure is a list of navigation items. This is the pattern you'll see in every professional codebase.

## [20:00–22:00] Links and Images

Links are the backbone of the web. The `<a>` element — anchor — is how you connect pages. The `href` attribute specifies the destination. Let's go through the four kinds of hrefs.

Absolute URL: `href="https://www.google.com"` — full URL including protocol. Use for external sites.

Root-relative URL: `href="/about"` — starts from the root of your site. If your site is at `https://example.com`, this goes to `https://example.com/about`. Use this for navigation within your own site.

Document-relative URL: `href="contact.html"` — relative to the current document's location. This is fragile; changing the file's folder breaks links. Prefer root-relative.

Anchor link: `href="#section-installation"` — jumps to an element with `id="section-installation"` on the current page. Used for in-page navigation in documentation and FAQs.

For external links with `target="_blank"`, always add `rel="noopener noreferrer"`. Without it, the opened page gains access to your page's `window.opener` reference and can redirect your original page. It's a real attack vector, and every linter and security tool will flag you if you omit it.

For images, the `alt` attribute is mandatory. It's the text equivalent of the image. When a screen reader encounters `<img alt="A developer writing code">`, it reads "Image: A developer writing code." When the image fails to load — network error, wrong path, slow connection — the alt text appears instead. For purely decorative images, use `alt=""` (empty string) — this explicitly tells screen readers to skip it.

The `<figure>` and `<figcaption>` pair gives images semantic context. The figure is a self-contained illustration; the figcaption is its caption. Use these instead of just dumping `<img>` inside a `<div>`.

## [22:00–24:00] HTML Tables

Tables are for presenting tabular data — information that has rows, columns, and the relationships between them are meaningful. Not for layout. Let me say that again more emphatically: **never use a table for page layout.** Using tables for layout was how web pages were built in 1998, before CSS existed. It creates accessibility nightmares, maintenance disasters, and overly complex markup. CSS Grid and Flexbox completely replaced it. Tables: data only.

For actual tabular data — spreadsheet-style information, comparison charts, schedules — HTML tables are exactly the right tool.

The structure has three semantic sections. `<thead>` wraps the header row. `<tbody>` wraps the data rows. `<tfoot>` wraps summary/total rows. Each section contains `<tr>` (table row) elements. Inside rows, `<th>` is a header cell and `<td>` is a data cell.

The `scope` attribute on `<th>` is what makes tables accessible. `scope="col"` means "this is a column header." `scope="row"` means "this is a row header." Screen readers use scope to announce the column and row context when reading a data cell — "January comma Revenue comma 24500 dollars" — without scope, users just hear a stream of numbers with no context.

`colspan` and `rowspan` let cells span multiple columns or rows. They're powerful but can make tables complex quickly — use them only when the data genuinely has merged cells.

`<caption>` is the accessible title for the table. Screen readers announce it before reading the table. Always include one for non-trivial tables.

## [24:00–26:00] HTML Forms — Why They Matter

Forms are how users send data to servers. They're the mechanism behind login pages, registration flows, checkout processes, search bars, comment sections — essentially any interaction where users provide input. Understanding forms deeply is essential.

Look at the form structure on Slide 14. The `<form>` element is the wrapper. It has two essential attributes: `action` and `method`.

`action` is the URL where the form data gets sent when the user submits. It's almost always an API endpoint — something like `/api/users/register` or `/api/login`. In modern single-page applications, JavaScript intercepts the submit event and uses fetch or Axios to send the data, but the endpoint is still the destination.

`method` is GET or POST. GET sends the form data as URL query parameters — like `/search?q=javascript&sort=relevance`. It's visible in the URL, can be bookmarked, can be shared. Use GET for searches and filters. POST sends data in the request body, invisible in the URL. Use POST for logins, registrations, payments — anything with sensitive or large data. You'll learn in Week 5 that POST maps to REST's "create" operation.

The `novalidate` attribute disables built-in browser validation. Use it only when you're handling validation entirely in JavaScript — for example, when you want custom error messages and styling rather than browser-native tooltips.

The reason I'm spending time on form fundamentals: even in React and Angular apps, you're still building forms. The mental model of `name`, `value`, and `method` transfers directly. The technology around it changes, but the concepts don't.

## [26:00–30:00] Form Input Types

HTML5 gave us a rich set of input types, and developers who don't know all of them end up re-building things the browser can already do for free. Let me walk through the full list.

**Text-based inputs:** `type="text"` is the fallback — accepts anything. `type="email"` auto-validates that the format looks like an email address. `type="password"` masks the text as dots. `type="search"` often shows a clear (×) button. `type="tel"` shows a phone keypad on mobile. `type="url"` validates URL format.

**Numeric inputs:** `type="number"` shows a numeric keyboard on mobile and supports `min`, `max`, and `step` attributes. `type="range"` renders a slider — great for rating inputs.

**Date and time inputs:** `type="date"` shows a calendar picker. `type="time"` shows a time picker. `type="datetime-local"` is combined. These are wonderful — zero JavaScript required for a native, accessible date picker that respects the user's locale.

**Selection inputs:** `type="checkbox"` allows multiple selections within a group. `type="radio"` allows exactly one selection — all radios with the same `name` form a mutually exclusive group. `type="color"` opens a color picker dialog. `type="file"` opens a file browser — the `accept` attribute filters by MIME type.

**Special:** `type="hidden"` is invisible but submitted. It's used for CSRF tokens, session IDs, and other data the user shouldn't modify. Despite being invisible in the UI, it absolutely appears in the submitted form data and the DOM — it's not secret.

`<textarea>` is for multi-line text — message boxes, bio fields, comment areas. Unlike `<input>`, it has a closing tag. `<select>` creates a dropdown. `<optgroup>` creates labeled option groups inside the dropdown.

[PAUSE] Any questions about input types before we move to how we label them?

## [30:00–34:00] Labels, Fieldsets, and Accessibility

Every input field needs a label. I mean this absolutely — no exceptions in professional web development. This rule is fundamental for two reasons: accessibility and usability.

The explicit association method: `<label for="username">Username</label>` and `<input id="username" />`. The `for` attribute must exactly match the `id` on the input. When a screen reader focuses the input, it reads "Username" — without this association, it just says "edit text," which tells the user nothing about what to type.

There's also a usability benefit: clicking the label text focuses the input. This significantly increases the effective click target on mobile — instead of needing to tap precisely on the 20-pixel tall input field, users can tap anywhere on the label text. Huge win for forms.

The wrapping method is an alternative: put the `<input>` inside the `<label>` element. This creates an implicit association without needing matching `for`/`id` pairs. Both methods are valid.

Now, `<fieldset>` and `<legend>`. These group related inputs. Think of billing address fields, shipping address fields, or radio button groups. The `<fieldset>` creates the group; the `<legend>` provides the group's name.

For radio button groups, `<fieldset>` and `<legend>` are not optional from an accessibility standpoint. Without them, a screen reader focused on a radio button saying "Email" has no context. With `<fieldset>` and `<legend>` reading "Preferred Contact Method: Email" — now the user understands they're choosing between contact methods. The legend is read with every item in the group.

Button types: be explicit. A `<button>` inside a `<form>` defaults to `type="submit"`. If you add JavaScript to a button and don't want it to submit the form, you MUST add `type="button"`. I've seen this bite developers — a button that was supposed to do something else accidentally submits the form. Always specify the type.

## [34:00–38:00] HTML5 Form Validation

Before JavaScript, before server-side validation, the browser itself provides a layer of validation. HTML5 built-in validation is genuinely useful for catching basic mistakes before the user even hits submit.

`required` is the simplest: the field cannot be empty. The browser prevents form submission and highlights the empty field.

`minlength` and `maxlength` enforce character count on text inputs. A username field might be `minlength="3" maxlength="20"` — the browser enforces both bounds.

For numeric and date inputs, `min` and `max` work on values. Age between 18 and 120. Check-in date after today. These prevent nonsense inputs before any JavaScript runs.

`pattern` is the most powerful — it takes a regular expression that the field's value must fully match. I have two examples here. `pattern="[0-9]{5}(-[0-9]{4})?"` matches either a 5-digit ZIP code or a ZIP+4 format. `pattern="[a-zA-Z0-9_]{3,16}"` matches usernames: 3 to 16 characters, letters, numbers, and underscores only. The `title` attribute provides the error message the browser shows when the pattern doesn't match.

Type-based validation is automatic and free: `type="email"` validates email format, `type="url"` validates URL format — no attributes needed.

When validation fails, browsers apply the `:invalid` CSS pseudo-class to the element. That's how you style validation errors with CSS — target `:invalid` and you can color the border red, show an icon, or whatever visual treatment you want. We'll cover this in Part 2.

Now, the most important rule in web security: **HTML validation is for user experience only.** It runs in the browser. A user can open DevTools and delete the `required` attribute in five seconds. A malicious user can send raw HTTP requests with no browser at all and submit anything they want. The server must always validate incoming data independently. HTML validation catches mistakes from good-faith users. Server validation is your actual security.

## [38:00–42:00] Common Mistakes Deep Dive

Let's walk through the most common HTML mistakes I see from new developers, because knowing what not to do is just as important as knowing what to do.

**Missing alt text on images.** This is the most widespread accessibility violation on the web. Every image that conveys information needs descriptive alt text. Not just "image" — actually descriptive. For a bar chart, describe the trend. For a product photo, describe the product. For a purely decorative image, use `alt=""` — the empty string tells assistive technology to skip it entirely.

**Using tables for layout.** I keep repeating this because it's such an ingrained bad habit in older code. The moment you reach for a `<table>` to position two columns side by side, stop. That's what Flexbox is for. Tables are for data with row-column relationships. Period.

**Skipping heading levels.** If your page has an `<h1>` and then jumps to `<h3>` because you want a slightly smaller heading, you've broken the document outline. Screen reader users navigate by headings. They expect a linear hierarchy. Use CSS to size text, not heading levels.

**Using `<br>` for spacing.** Putting four `<br>` tags between sections because you want whitespace is HTML from 1996. Use CSS `margin` on your elements. It's easier to maintain, more consistent, and responsive.

**`<b>` instead of `<strong>`.** Both make text bold visually. But `<strong>` tells the browser "this is semantically important." `<em>` emphasizes with stress. `<b>` and `<i>` are purely presentational with no semantic meaning. In most cases, use the semantic versions. Use `<b>` and `<i>` only for specific use cases like book titles in italics or keywords in bold where emphasis isn't the intent.

**No labels on inputs.** I said this already, but it bears repeating because I see it constantly: `placeholder` is NOT a substitute for a label. The placeholder disappears when the user starts typing. A user who tabs into a field and has already started typing cannot see what field they're in. Always have a visible, associated label.

**Missing `noopener noreferrer` on external links.** Security vulnerability. Add it.

**`<div>` for everything.** This is div soup. Before reaching for `<div>`, ask: is there a semantic element that fits? Header? Main? Article? Section? Nav? Use the semantic element first, `<div>` only when nothing semantic fits.

## [42:00–46:00] The Complete HTML Page Walkthrough

Let me walk you through the complete example on Slide 19, because seeing everything together is more valuable than seeing pieces in isolation.

Starting at the top: `<!DOCTYPE html>` — standards mode. `<html lang="en">` — English, root element. Inside `<head>`: UTF-8 charset first, viewport meta for mobile, descriptive title, meta description for Google, and the link to our CSS file.

In `<body>`, the first child is `<header>`. Inside: a logo link and a `<nav>` with `aria-label="Main navigation"` — the aria-label distinguishes this nav from the footer nav so screen readers can differentiate them. The nav contains a `<ul>` with four `<li>` elements, each containing an `<a>`.

Then `<main>`. An `<h1>` for the page title — this is the single `<h1>` on the page. A `<p>` of instructions.

Then the `<form>` with `action="/api/register"` and `method="POST"`. The form is divided into three `<fieldset>` groups: Personal Information, Course Selection, and Communication Preferences. Each fieldset has a `<legend>`.

In Personal Information: full name with `autocomplete="name"`, required, and length constraints. Email with `autocomplete="email"`, required. Phone optional.

In Course Selection: a `<select>` dropdown with `<optgroup>` for Beginner and Advanced courses. A date input with `min="2025-02-01"` so users can't register for past dates.

In Communication Preferences: two checkboxes wrapped in labels with no fieldset-specific legend (the checkboxes are independent, not a mutually exclusive group).

Then the form actions: a submit button and a reset button.

Footer: `<p>` with copyright symbol using `&copy;`, and a second `<nav>` with `aria-label="Footer"` containing policy links.

This is what professional HTML looks like. Semantic, accessible, validated, properly structured. When you're building your own pages, compare them against this template.

## [46:00–50:00] Browser DevTools in Practice

The browser DevTools are your primary debugging tool for frontend development. Learn to use them now — they will save you hours.

To open DevTools: F12 on Windows/Linux, Cmd+Option+I on Mac. Alternatively, right-click any element on any webpage and choose Inspect. This is how I want you to explore web pages you admire — open DevTools, inspect the elements, and see how they're built.

The Elements panel is where you'll spend most of your HTML debugging time. The left side shows the DOM tree — you can expand and collapse nodes, click any node to highlight the element in the browser viewport, and right-click nodes to delete them, copy their selector, or add attributes.

Double-click any text content in the Elements panel to edit it live. You can change a heading's text, add a new attribute to an input, or delete an element entirely. These changes are temporary — they exist only in the browser's memory and reset on page reload. But they're perfect for testing a fix before writing it in code.

The Styles panel on the right shows all CSS rules that apply to the selected element. You'll understand this fully in Part 2, but the key insight now: every CSS rule that matches the element is shown here, with the most specific rule at the top. Rules that are crossed out are being overridden by a more specific rule above them.

At the bottom of the Styles panel is a visual box model diagram — you can see exactly how much padding, border, and margin is applied. This is invaluable for debugging layout issues.

The most important mindset shift: **DevTools shows you the live DOM, not your source HTML file.** If JavaScript modified the page after it loaded, DevTools shows the modified version. If the browser auto-corrected invalid HTML, DevTools shows the corrected version. When something looks wrong, DevTools tells you what the browser actually built — which may not match what you thought you wrote.

## [50:00–54:00] Putting It All Together — Practical Tips

Let me give you five practical tips that will immediately improve the HTML you write.

**Tip one: validate your HTML.** Go to validator.w3.org, paste your HTML, and run it. It catches unclosed tags, missing attributes, invalid nesting, and deprecated elements. Many developers never use this tool and end up debugging browser rendering quirks that are actually caused by invalid HTML. Five minutes of validation saves hours of debugging.

**Tip two: use semantic elements first, `<div>` as the last resort.** Every time you're about to type `<div>`, pause and ask: is there a semantic element that communicates what this container *is*? `<nav>`, `<main>`, `<section>`, `<article>`, `<aside>`, `<footer>` — these communicate intent. `<div>` communicates nothing. Use the semantic one.

**Tip three: test your form tab order.** Users who don't use a mouse navigate forms by pressing Tab. Open your HTML file and tab through every field without touching the mouse. Does the focus order make sense? Does every field get focused? Does every label get read? If not, fix the structure.

**Tip four: check your page with images disabled.** Open DevTools, go to the Rendering tab, and check "Disable image rendering." Your page should still be intelligible. If there's information that's only communicated through images with no alt text, you have an accessibility gap.

**Tip five: get comfortable with the W3C HTML specification.** When you're unsure whether an element is being used correctly, MDN Web Docs (developer.mozilla.org) has the definitive reference for every HTML element, its attributes, its valid children, and its accessibility implications. Get in the habit of checking MDN before using an element you're unfamiliar with.

## [54:00–58:00] The Bridge Between HTML and CSS

Before I close Part 1, I want to lay the conceptual bridge to Part 2, because understanding how CSS and HTML connect is essential context.

CSS targets HTML through **selectors**. A CSS selector is a pattern that matches specific HTML elements. The simplest selector is the element name: `p { color: red; }` — matches every `<p>` element on the page and makes its text red.

But you'll often want to style *specific* elements, not all elements of a type. That's where the `class` and `id` attributes come in.

`class` is the most widely used attribute for CSS targeting. You add it to any element: `<p class="intro-text">`. Then in CSS, target it with a dot: `.intro-text { font-size: 18px; }`. Multiple elements can share the same class. One element can have multiple classes, separated by spaces: `class="btn btn-primary large"`.

`id` is for uniquely identifying one element on the entire page: `<main id="main-content">`. In CSS, target it with a hash: `#main-content { padding: 2rem; }`. IDs should be unique — only one element per page per ID.

You've been writing classes and IDs in your HTML instinctively when building apps. Now you'll understand exactly what they do in CSS terms. When we get to JavaScript on Day 12 and DOM manipulation on Day 13, you'll use these same selectors to find elements.

This is the HTML-CSS-JS pipeline: HTML defines the elements and gives them classes and IDs as handles; CSS selects those handles to apply styles; JavaScript selects those handles to apply behavior. The handles you give elements in HTML serve both CSS and JavaScript.

## [58:00–60:00] Part 1 Summary

Let's wrap up Part 1 with everything you've built in the last 60 minutes.

You now understand the **HTML document skeleton** — DOCTYPE, html, head, body — and exactly what goes in each section. You have a mental model of the **DOM** as the browser's tree representation of your HTML. You know the difference between **block and inline elements** and the nesting rules between them.

You can write **semantic HTML** — using header, nav, main, section, article, aside, and footer — and you understand *why* it matters for accessibility, SEO, and maintainability. You know all the **text content elements**: headings, paragraphs, strong, em, code, blockquote. You can build **lists** in all three types. You know how to write **accessible links** with correct href values and secure external link attributes.

You can build complete **HTML forms** with the full range of input types, proper label associations, fieldset grouping for accessibility, and HTML5 built-in validation. You know the critical rule: validate server-side always, HTML validation is for UX only.

Take a ten-minute break. When we come back, we're doing CSS. The HTML you just learned is about to transform from a plain gray page into something that actually looks like a real application.

Part 2 starts with how CSS rules are written and how browsers apply them, then moves through selectors, specificity, the box model, positioning, Flexbox, Grid, responsive design, animations, and Bootstrap. Lot to cover — we're going to move fast, but I'll flag every essential concept clearly. See you in ten minutes.
