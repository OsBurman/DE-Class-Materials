# Exercise 01: HTML Document Structure & Semantic Markup

## Objective
Write a properly structured HTML5 document using semantic elements and understand the difference between block-level and inline elements.

## Background
Every web page is a tree of HTML elements — the **DOM (Document Object Model)**. Browsers parse your HTML into this tree and use it to render the page. Semantic HTML tags like `<header>`, `<nav>`, `<main>`, `<article>`, `<section>`, `<aside>`, and `<footer>` give meaning to the content they wrap, which improves accessibility, SEO, and maintainability compared to using generic `<div>` tags everywhere.

## Requirements

1. **Document boilerplate:** Create a valid HTML5 document with `<!DOCTYPE html>`, the `<html lang="en">` root element, a `<head>` section (with `<meta charset>`, `<meta name="viewport">`, and a `<title>`), and a `<body>`.

2. **Semantic page structure:** Inside `<body>`, use the following semantic elements — each must contain meaningful placeholder content:
   - `<header>` — site title inside an `<h1>`
   - `<nav>` — an unordered list (`<ul>`) of three navigation links (`<a href="#">`)
   - `<main>` — wrapping the primary content below
   - `<article>` inside `<main>` — a blog post with an `<h2>` heading, two `<p>` paragraphs, and one `<img>` with `src` and `alt` attributes
   - `<aside>` inside `<main>` — a sidebar with a short `<p>`
   - `<footer>` — a `<p>` with a copyright notice

3. **Heading hierarchy:** Use headings in order — `<h1>` once in the header, `<h2>` for the article, `<h3>` for at least one sub-section inside the article.

4. **Inline vs block demonstration:** Inside the article, include one example of each:
   - A block-level element: `<blockquote>` with a quoted sentence
   - Inline elements: `<strong>` (bold) and `<em>` (italic) used inside a paragraph

5. **A list:** Include an ordered list (`<ol>`) of at least 3 items somewhere on the page (e.g., "Top 3 reasons...").

6. **A hyperlink with a real attribute:** Make one `<a>` tag open in a new tab using `target="_blank"` and include `rel="noopener noreferrer"`.

## Hints
- A semantic element tells the browser (and screen readers) *what* the content is, not just *how to display it*. `<article>` means "a self-contained piece of content"; `<div>` means nothing.
- Block-level elements start on a new line and take full width (`<p>`, `<h1>`–`<h6>`, `<ul>`, `<div>`). Inline elements sit inside text flow (`<a>`, `<strong>`, `<em>`, `<span>`).
- The `<meta name="viewport" content="width=device-width, initial-scale=1.0">` tag is essential for responsive design.
- Open the file directly in a browser to see the result — no server needed.

## Expected Output

When opened in a browser, the page should render with:
- A visible site title at the top
- A navigation bar with three links
- An article with a heading, paragraphs, bold/italic text, a blockquote, and an image placeholder
- A sidebar alongside the article
- An ordered list
- A footer with copyright text

*(Visual layout will be unstyled — that is expected for this exercise.)*
