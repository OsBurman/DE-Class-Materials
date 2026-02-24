# Day 11 Application — HTML & CSS: Personal Portfolio Page

## Overview

You'll build a **Personal Portfolio Page** — a fully responsive, multi-section static webpage using semantic HTML and modern CSS. No JavaScript. This is all about solid structure and beautiful layout.

---

## Learning Goals

- Write semantic HTML with proper document structure
- Build accessible forms with validation attributes
- Apply CSS selectors, specificity, and the cascade
- Implement layouts with Flexbox and CSS Grid
- Create responsive designs with media queries
- Use CSS variables, transitions, and animations
- Apply Bootstrap's grid system

---

## Prerequisites

- A browser (Chrome or Firefox)
- A code editor (VS Code)
- No install needed — open `index.html` directly in your browser

---

## Project Structure

```
starter-code/
├── index.html        ← TODO: complete the HTML structure
├── styles.css        ← TODO: complete the CSS
└── assets/           ← placeholder images provided
```

---

## Part 1 — HTML Structure

**Task 1 — Semantic document structure**  
Your page needs these semantic sections in `index.html`:
- `<header>` — your name, title, and nav links
- `<nav>` — anchor links to each section
- `<main>`
  - `<section id="about">` — photo, bio paragraph
  - `<section id="skills">` — skills list
  - `<section id="projects">` — project cards
  - `<section id="contact">` — contact form
- `<footer>` — copyright line

**Task 2 — Forms**  
In the contact section, create a form with:
- Text input for name (required, minlength=2)
- Email input (required, type="email")
- Select dropdown for subject (3 options)
- Textarea for message (required, rows=5)
- Submit button
- All inputs must have matching `<label>` elements using `for`/`id`

**Task 3 — Projects section**  
Create at least 3 project cards. Each card must use:
- A `<figure>` with an `<img>` and `<figcaption>`
- A heading, description paragraph, and a list of tech tags
- An anchor link

---

## Part 2 — CSS Styling

**Task 4 — CSS Variables**  
At the top of `styles.css`, define at least 5 custom properties in `:root`:
```css
:root {
  --primary-color: #2563eb;
  --text-color: #1e293b;
  /* ... etc */
}
```

**Task 5 — Selectors & Specificity**  
Use at least one of each: element selector, class selector, ID selector, attribute selector (`[type="email"]`), and pseudo-class (`:hover`, `:focus`, `:nth-child`).

**Task 6 — Box Model**  
Explicitly set `margin`, `padding`, and `border` on at least 3 elements. Comment which part of the box model each targets.

**Task 7 — Flexbox**  
Use Flexbox for at least 2 layouts:
- The `<nav>` links (horizontal row, space-between)
- The `<header>` content (centered vertically)

**Task 8 — CSS Grid**  
Use Grid for the projects section — a 3-column grid that collapses to 1 column on mobile.

**Task 9 — Responsive Design**  
Write at least 2 media queries:
- `max-width: 768px` — single column layout, stacked nav
- `max-width: 480px` — smaller font sizes, full-width buttons

**Task 10 — Transitions & Animations**  
- Add a CSS `transition` on project card hover (scale + shadow)
- Add a CSS `@keyframes` animation on the header (fade-in on load)

---

## Part 3 — Bootstrap (Optional Enhancement)

Add the Bootstrap CDN to `index.html` and use Bootstrap classes for:
- The navbar component
- Button styles on the form submit
- The grid system for the skills section

---

## Stretch Goals

1. Add a "dark mode" using a CSS class toggle (just CSS — use `:root` variable overrides).
2. Make the nav sticky with `position: sticky`.
3. Add a CSS-only hamburger menu for mobile using the checkbox hack.

---

## Submission Checklist

- [ ] All 5 semantic elements used (`header`, `nav`, `main`, `section`, `footer`)
- [ ] Form has all required fields with labels and HTML5 validation
- [ ] CSS custom properties defined and used
- [ ] Flexbox used in at least 2 places
- [ ] CSS Grid used for the projects layout
- [ ] At least 2 media queries written
- [ ] Hover transition on project cards
- [ ] At least one `@keyframes` animation
- [ ] Page is usable on mobile (no horizontal scroll)
