SLIDE 1 — Title Slide
Slide Content:

HTML Deep Dive
Document Structure, Tags, Semantics, Tables & Forms
"The skeleton of every webpage you've ever seen"

Script:
"Good [morning/afternoon], everyone. Today we're going deep into HTML. By the end of this lesson, you'll understand how every webpage is structured from the inside out. You'll know how to write clean, meaningful HTML, display data in tables, and build forms that actually work and are accessible to all users. Let's get into it."

SLIDE 2 — Today's Roadmap
Slide Content:

HTML Document Structure & the DOM
HTML Tags & Semantic Markup
Elements & Attributes (Inline vs Block)
Common HTML Tags
Tables
Forms, Input Types & Validation

Script:
"Here's our roadmap. We've already touched on some basics in previous lessons, so today we're building on that foundation and going much further. Keep your notes open — there's a lot of good stuff here."

SLIDE 3 — HTML Document Structure
Slide Content:
html<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Page</title>
  </head>
  <body>
    <!-- Visible content goes here -->
  </body>
</html>
```

**Script:**
"Every HTML file you ever write starts with this structure — no exceptions. Let's walk through it.

`<!DOCTYPE html>` is a declaration, not a tag. It tells the browser: 'This is a modern HTML5 document.' Without it, browsers enter 'quirks mode' and things can render unexpectedly.

`<html lang='en'>` is the root element — everything lives inside it. The `lang` attribute tells browsers and screen readers what language the page is in, which matters for accessibility.

The `<head>` is invisible to users. It holds metadata — information *about* the page. The `charset` meta tag ensures special characters display correctly. The `viewport` meta tag makes the page responsive on mobile. `<title>` sets the browser tab text.

The `<body>` is where everything the user sees goes.

Think of it like a human body: the `<head>` does behind-the-scenes work, the `<body>` is what everyone interacts with."

---

## SLIDE 4 — The DOM (Document Object Model)

**Slide Content:**
```
html
├── head
│   ├── meta
│   └── title
└── body
    ├── h1
    ├── p
    └── div
        └── a

DOM = the browser's internal tree structure of your HTML
Parent → child → sibling relationships
JavaScript uses the DOM to find and change elements

Script:
"When a browser reads your HTML, it doesn't just display it — it parses it and builds the Document Object Model, or DOM. The DOM is a tree of all your elements and how they relate.
Look at the tree. html is the root with two children: head and body. Inside body we have an h1, a p, and a div. Inside that div is an anchor tag.
Why does this matter? JavaScript — which you'll learn later — uses the DOM to find and change elements on the page. When your HTML is well-structured, the DOM is clean and easy to work with. Messy HTML equals a messy DOM equals bugs.
Elements inside other elements are called children. Elements at the same level are siblings. Get comfortable with this mental model — it's fundamental."

SLIDE 5 — HTML Tags & Semantic Markup
Slide Content:

Non-semantic (generic): <div>, <span> — mean nothing
Semantic (meaningful): <header>, <nav>, <main>, <article>, <section>, <footer>, <aside>, <figure>

Use for...Use this tagPage header area<header>Navigation links<nav>Primary content<main>Independent article<article>Grouped content<section>Page footer<footer>
Script:
"This is one of the most important concepts in modern HTML: semantic markup.
A tag labels content and tells the browser what it is. Every tag has an opening and closing: <p> opens a paragraph, </p> closes it.
Semantic markup means choosing tags that describe the meaning of your content, not just how it looks.
For years, developers built entire websites with only <div> tags. A <div> is a generic container — it means absolutely nothing. HTML5 gave us semantic tags to fix that. Instead of a <div> for navigation, use <nav>. Instead of a <div> for the page header, use <header>. Instead of a <div> for main content, use <main>.
Why does this matter? Three reasons. First, accessibility — screen readers used by visually impaired users understand semantic tags and can navigate your page properly. Second, SEO — search engines understand your content structure better. Third, readability — when another developer reads your code, semantic tags make it immediately clear what each section does.
A page that looks right can still be badly written HTML. Semantic markup is the difference between code that works and code that's good."

SLIDE 6 — Block vs Inline Elements
Slide Content:
BlockInlineStarts on a new lineFlows within textFull width of containerOnly as wide as content<div>, <p>, <h1>–<h6>, <ul>, <section><span>, <a>, <strong>, <em>, <img>, <input>
html<p>This has <strong>bold text</strong> and a <a href="#">link</a> inside.</p>
Script:
"Every HTML element is either block-level or inline — knowing the difference prevents a lot of confusion.
Block elements always start on a new line and stretch to fill the full width of their container. Headings, paragraphs, divs, lists — all block.
Inline elements live within text. They don't break to a new line. They're only as wide as their content. Anchors, spans, bold tags — all inline.
In the example, <p> is block — it sits on its own line. But <strong> and <a> inside it are inline — they sit within the text without breaking its flow.
Common mistake: putting a block element inside an inline element, like a <div> inside a <span>. Don't do that. Inline elements should only contain other inline elements or text."

SLIDE 7 — Attributes
Slide Content:

Format: attribute="value" inside the opening tag
Common: id, class, href, src, alt, type, name, placeholder, required

html<a href="https://example.com" target="_blank">Visit Site</a>
<img src="photo.jpg" alt="A sunset over the mountains">
<input type="email" placeholder="Enter your email" required>
Script:
"Attributes add extra information to elements inside the opening tag.
The anchor tag uses href for where the link goes and target='_blank' to open in a new tab.
The image tag uses src to point to the file and alt for a text description. Alt text is critical — if the image fails to load, users see the alt text. For screen readers, it's how visually impaired users know what an image shows. Always write meaningful alt text. 'photo.jpg' is not meaningful. 'A sunset over the mountains' is.
id gives an element a unique name — use each ID only once per page. class is reusable across many elements. You'll use both heavily in CSS and JavaScript."

SLIDE 8 — Headings, Paragraphs & Text
Slide Content:
html<h1>Main Page Title</h1>
<h2>Section Heading</h2>
<h3>Sub-section Heading</h3>
<!-- h4, h5, h6 also exist -->

<p>This is a paragraph of text.</p>

<br>   <!-- Line break — use sparingly -->
<hr>   <!-- Horizontal divider line -->

One <h1> per page — it defines the page's topic
Don't skip heading levels (h1 → h3 without h2)

Script:
"Headings go from h1 to h6. H1 is the most important — like a book's title. H2 is a chapter. H3 is a sub-section.
Every page should have exactly one <h1>. It tells search engines and screen readers what the page is primarily about. Don't skip levels either — going from h2 to h4 breaks the document outline.
<p> tags are for paragraphs. Browsers automatically space them. Resist using <br> for spacing — that's CSS's job. Use <br> only for actual line breaks in content, like a poem or a mailing address."

SLIDE 9 — Lists & Links
Slide Content:
html<!-- Unordered list (bullets) -->
<ul>
  <li>HTML</li>
  <li>CSS</li>
</ul>

<!-- Ordered list (numbered) -->
<ol>
  <li>Step one</li>
  <li>Step two</li>
</ol>

<!-- Absolute, relative, and anchor links -->
<a href="https://google.com">Google</a>
<a href="about.html">About Page</a>
<a href="#section2">Jump to Section 2</a>
Script:
"Unordered lists use <ul> — they show bullet points and are for items where order doesn't matter. Ordered lists use <ol> — they're numbered and used for sequences. Every item in both uses <li>.
The anchor tag <a> is the backbone of the web. Three types of links:

Absolute — full https:// URLs to external sites
Relative — paths to pages in your own project
Anchor — links that jump to a section of the same page using # and an ID

Navigation menus are typically an unordered list of anchor tags inside a <nav> element — now you know why."

SLIDE 10 — Images & Figures
Slide Content:
html<img src="images/dog.jpg" alt="A golden retriever playing fetch" width="400">

<!-- Semantic image with caption -->
<figure>
  <img src="chart.png" alt="Bar chart showing monthly sales data">
  <figcaption>Monthly sales — Q1 2024</figcaption>
</figure>
Script:
"<img> is self-closing — no closing tag needed. It requires src and alt at minimum.
For images with captions, use <figure> and <figcaption>. This semantically links the image and its caption — much more meaningful than an image next to random text in a div. Screen readers understand this relationship."

SLIDE 11 — HTML Tables
Slide Content:
html<table>
  <thead>
    <tr>
      <th>Name</th>
      <th>Age</th>
      <th>City</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Alice</td>
      <td>28</td>
      <td>New York</td>
    </tr>
  </tbody>
</table>

<table> → <thead> / <tbody> → <tr> → <th> or <td>
Tables are for data. Never for page layout.

Script:
"Tables display tabular data — things that genuinely belong in rows and columns. Important: tables are for data, not for layout. In the early 2000s people built entire pages with tables. That's outdated — CSS handles layout now.
The structure: <table> wraps everything. <thead> holds the header row. <tbody> holds the data rows. Each row is a <tr>. Header cells use <th> — bold by default. Data cells use <td>.
The <thead> and <tbody> separation is semantic — screen readers use it to understand which cells are column headers."

SLIDE 12 — Forms: The Big Picture
Slide Content:
html<form action="/submit" method="POST">
  <!-- Inputs go here -->
</form>

action — where the data is sent
method="GET" — data visible in URL (searches, filters)
method="POST" — data in request body (logins, sensitive info)

Script:
"Forms are how users interact with websites — signing up, logging in, searching, purchasing. Every form uses the <form> element as the wrapper.
action is where the data goes on submission. method is how it gets there.
GET appends data to the URL — you've seen this when searching Google. Use it for non-sensitive searches.
POST sends data in the request body, invisible in the URL. Always use POST for passwords and personal information."

SLIDE 13 — Inputs & Labels
Slide Content:
html<label for="username">Username:</label>
<input type="text" id="username" name="username" placeholder="Enter username">

<label for="email">Email:</label>
<input type="email" id="email" name="email">

<label for="password">Password:</label>
<input type="password" id="password" name="password">

<label for="age">Age:</label>
<input type="number" id="age" name="age" min="1" max="120">

<label for="..."> must match <input id="...">
Never use placeholder text as a substitute for a label

Script:
"<input> is the workhorse of forms. Its type attribute determines what kind of field it is.
type='text' is a basic text field. type='email' validates email format automatically. type='password' hides characters as the user types. type='number' only accepts numbers, with optional min and max.
The most critical rule: every input needs a label. The for attribute on the label must match the id on the input — this links them. When linked, clicking the label focuses the input. Screen readers announce the label when a user tabs to the field.
Never use placeholder text as a substitute for labels. Placeholder disappears when the user starts typing — they shouldn't have to remember what a field was for.
The name attribute is what gets sent to the server — it's the key in the data pair."

SLIDE 14 — More Input Types
Slide Content:
html<!-- Checkbox -->
<input type="checkbox" id="terms" name="terms">
<label for="terms">I agree to the terms</label>

<!-- Radio buttons (share same name) -->
<input type="radio" id="student" name="role" value="student">
<label for="student">Student</label>
<input type="radio" id="teacher" name="role" value="teacher">
<label for="teacher">Teacher</label>

<!-- Dropdown -->
<label for="country">Country:</label>
<select id="country" name="country">
  <option value="">-- Select --</option>
  <option value="us">United States</option>
</select>

<!-- Textarea, date, file -->
<textarea id="message" name="message" rows="4"></textarea>
<input type="date" id="dob" name="dob">
<input type="file" id="upload" name="upload">
Script:
"Checkboxes are for yes/no choices or selecting multiple options. Each is independent.
Radio buttons are for choosing one option from a group. Key detail: all radios in the same group must share the same name — that's how the browser groups them.
Select dropdowns use <select> with <option> elements inside. The empty first option is a best practice — prevents a value from being pre-selected.
Textarea is for multi-line text. Unlike <input>, it has a closing tag. type='date' gives a date picker. type='file' lets users upload files."

SLIDE 15 — Buttons
Slide Content:
html<button type="submit">Submit Form</button>
<button type="reset">Clear Form</button>
<button type="button">Click Me</button>

Always set the type attribute on buttons
Default type is submit — can trigger accidental form submissions

Script:
"Buttons seem simple but have a gotcha. If you put a <button> inside a form without a type, it defaults to submit and will trigger form submission — even if you just wanted a regular button for JavaScript. Always set the type explicitly.
type='reset' clears all fields. Be careful — users can accidentally clear all their input.
type='button' does nothing by default — it's meant for JavaScript interaction.
Prefer <button> over <input type='submit'> — it's more flexible because you can put HTML content inside it."

SLIDE 16 — Form Validation
Slide Content:
html<input type="text"     name="name"     required>
<input type="email"    name="email"    required>
<input type="password" name="password" minlength="8" required>
<input type="number"   name="age"      min="18" max="99">
<input type="text"     name="zip"      pattern="[0-9]{5}" 
       title="5-digit ZIP code">
AttributePurposerequiredField cannot be emptyminlength / maxlengthCharacter count limitsmin / maxRange for numbers & datespatternRegex the value must matchtype="email"Validates email format automatically
⚠️ HTML validation is client-side only — always also validate on the server.
Script:
"HTML5 gives us built-in form validation that's genuinely powerful.
required is the simplest — the form won't submit if the field is empty and the browser shows an error automatically.
minlength and maxlength set character limits. Great for passwords. min and max restrict number and date ranges.
pattern accepts a regular expression. Don't worry too much about regex now — just know the example [0-9]{5} means 'exactly five digits,' which validates a ZIP code. The title attribute provides the custom error message hint users see.
Important caveat: HTML validation is client-side only. A determined user can bypass it. Always validate on the server side too. But HTML validation gives immediate user feedback and is accessible by default — screen readers announce validation errors automatically."

SLIDE 17 — Full Example: Semantic Form
Slide Content:
html<main>
  <section>
    <h1>Create an Account</h1>
    <form action="/register" method="POST">
      
      <label for="fullname">Full Name:</label>
      <input type="text" id="fullname" name="fullname" required>

      <label for="email">Email Address:</label>
      <input type="email" id="email" name="email" required>

      <label for="password">Password:</label>
      <input type="password" id="password" name="password" 
             minlength="8" required>

      <label for="dob">Date of Birth:</label>
      <input type="date" id="dob" name="dob" required>

      <label for="role">I am a:</label>
      <select id="role" name="role">
        <option value="">-- Select --</option>
        <option value="student">Student</option>
        <option value="teacher">Teacher</option>
      </select>

      <input type="checkbox" id="terms" name="terms" required>
      <label for="terms">I agree to the Terms of Service</label>

      <button type="submit">Create Account</button>
    </form>
  </section>
</main>
Script:
"Let's put it all together. Here's a complete registration form using everything from today.
The form lives inside a <section> inside <main>. The <h1> says exactly what this page is for. Every input has a linked label. We're using the right input types throughout. Required fields have required. The password has minlength='8'. The terms checkbox has required — the form won't submit unless it's checked.
This is clean, semantic, accessible HTML. This is what good code looks like."

SLIDE 18 — Accessibility Checklist
Slide Content:
✅ Use semantic tags (<header>, <nav>, <main>, <footer>)
✅ One <h1> per page, don't skip heading levels
✅ Every <input> has a <label> with matching for/id
✅ Every <img> has meaningful alt text (or alt="" if decorative)
✅ Use required, minlength, pattern for validation
✅ Don't use color alone to convey meaning
Script:
"Accessibility isn't optional — it's part of writing good HTML.
Semantic tags: use them always.
Labels: every input, every time.
Alt text: meaningful descriptions on every image. If purely decorative, use alt='' so screen readers skip it.
Heading hierarchy: don't jump from h1 to h4.
Validation: built-in HTML attributes give users accessible feedback at no extra cost.
Accessibility is also a legal requirement for public websites in many countries. And practically, accessible websites rank better on search engines. It benefits everyone."

SLIDE 19 — Key Takeaways
Slide Content:

Every HTML page: DOCTYPE → html → head → body
The DOM is a tree — structure matters
Semantic tags describe meaning, not appearance
Block elements stack; inline elements flow within text
Attributes add information and behavior
Always link <label> to <input> with matching for/id
Use the right type for the right input
Validation: required, minlength, pattern, min, max
Tables are for data — never for layout
Accessibility is built into good HTML — write for it

Script:
"Your ten takeaways. These aren't just things to memorize — they're habits to build. Every time you write HTML, run through this list mentally. Is my structure correct? Are my tags semantic? Does every input have a label? Does every image have alt text? Am I using the right input type? Do I have validation on required fields? If yes to all — you're writing great HTML."

SLIDE 20 — Practice & Resources
Slide Content:

Practice: Build a complete registration form from scratch using today's concepts
Reference: MDN Web Docs — developer.mozilla.org (bookmark this)
Next lesson: [CSS / next topic]

Script:
"For practice, build a complete registration or contact form from scratch — no copying from notes. Try it from memory and refer back when stuck. That struggle is where the learning happens.
Bookmark MDN Web Docs at developer.mozilla.org right now. It's the definitive reference for HTML, CSS, and JavaScript. Any tag, any attribute, any question — MDN has the answer.
Any questions before we wrap up? [Pause]
Great work today. HTML is the foundation of everything on the web, and now you understand it at a real level — not just typing tags, but knowing why and how. See you next time."


