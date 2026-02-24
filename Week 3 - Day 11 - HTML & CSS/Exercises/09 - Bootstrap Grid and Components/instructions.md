# Exercise 09: Bootstrap Grid & Components

## Objective
Build a responsive page layout and common UI components using Bootstrap's utility classes, grid system, and pre-built components — without writing any custom CSS.

## Background
**Bootstrap** is a CSS framework that ships with a 12-column responsive grid, utility classes, and a library of ready-made components (navbars, cards, buttons, badges, modals, etc.). You load it via CDN and apply class names directly in your HTML. Understanding Bootstrap's grid helps you rapidly prototype production-quality layouts.

## Requirements

Link Bootstrap 5 via CDN — no local install needed:
```html
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
```

### Part A — Responsive Navbar

Use the Bootstrap `navbar` component:
- `class="navbar navbar-expand-md navbar-dark bg-dark"`
- Brand link: `navbar-brand`
- Collapsible menu: `navbar-toggler` + `collapse navbar-collapse`
- At least three nav links using `nav-link` inside a `navbar-nav`

### Part B — Bootstrap Grid

Inside a `container`, build a row that:
- Shows **1 column** on mobile (`col-12`)
- Shows **2 columns** on tablet (`col-md-6`)
- Shows **3 columns** on desktop (`col-lg-4`)

Use three `div.col-12.col-md-6.col-lg-4` blocks inside a `div.row`. Each block should contain a Bootstrap card (Part C).

### Part C — Bootstrap Cards

Each of the three grid columns should contain a Bootstrap card:
- `class="card h-100"` (h-100 makes equal-height cards in a row)
- `card-img-top` with `src="https://placehold.co/600x200"` and `alt`
- `card-body` with `card-title` (`<h5>`), `card-text`, and a `btn btn-primary` button
- At least one card should have a `badge` inside the title: `<span class="badge bg-success">New</span>`

### Part D — Utility Classes

Demonstrate the following Bootstrap utilities — each in its own row or section with a label:

1. **Spacing:** A `div` with `p-4 mb-3 bg-light border` (padding 4, margin-bottom 3, light background)
2. **Typography:** `<p class="text-muted fs-5">`, `<p class="fw-bold text-primary">`, `<p class="font-monospace">`
3. **Buttons:** Show all six contextual button colours: `btn-primary`, `btn-secondary`, `btn-success`, `btn-danger`, `btn-warning`, `btn-info` — all inside one `d-flex gap-2 flex-wrap` container
4. **Alerts:** `alert alert-success`, `alert alert-warning`, `alert alert-danger` — each with the appropriate text
5. **Grid with offset:** One row with `col-md-4 offset-md-4` (centred column using offset)

### Part E — Accordion (JS Component)

Add a Bootstrap accordion with three items using the standard accordion markup (`accordion`, `accordion-item`, `accordion-header`, `accordion-button`, `accordion-collapse`, `accordion-body`). The first item should start open (`show`).

## Hints
- Bootstrap's grid is based on 12 columns. `col-4` = 4/12 = one third. Three `col-4` items fill a row.
- `h-100` on a card makes it stretch to the full height of its grid column — essential for equal-height card rows.
- The CDN JavaScript bundle (`bootstrap.bundle.min.js`) includes Popper.js — needed for accordions, modals, and dropdowns.
- Resize the browser to see the navbar collapse into a hamburger menu below the `md` breakpoint.

## Expected Output

When opened in a browser:
- A dark navbar that collapses to a hamburger menu on small screens
- Three cards in one row on desktop, two on tablet, one on mobile — all equal height
- Six coloured buttons in a row
- Three styled alert boxes
- A centred single column using offset
- A functioning accordion where clicking headers shows/hides content
