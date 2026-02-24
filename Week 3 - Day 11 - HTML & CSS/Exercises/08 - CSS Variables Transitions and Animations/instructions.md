# Exercise 08: CSS Variables, Transitions & Animations

## Objective
Apply CSS custom properties (variables) for consistent theming, add smooth hover transitions, and create keyframe animations.

## Background
**CSS custom properties** (also called CSS variables) store reusable values that can be changed in one place and cascade throughout the document. **Transitions** animate a CSS property from one value to another when a state changes (e.g., `:hover`). **Animations** (`@keyframes`) run on their own timeline, repeating as many times as you specify without needing user interaction.

## Requirements

The starter provides an HTML file. **Edit only `styles.css`.**

### Part A — CSS Variables (Custom Properties)

1. Define the following variables on `:root`:
   ```
   --color-primary: #0066cc
   --color-primary-dark: #0050a0
   --color-bg: #f8f9fa
   --color-text: #212529
   --color-border: #dee2e6
   --radius: 6px
   --transition-speed: 0.3s
   ```
2. Use `var(--color-primary)` etc. throughout *all* other rules in this exercise — do not hardcode any of the above values directly.

### Part B — Button Transitions

Target `.btn`:
- `background-color: var(--color-primary)`, `color: white`
- `padding: 0.6rem 1.4rem`, `border: none`, `border-radius: var(--radius)`, `cursor: pointer`
- `transition: background-color var(--transition-speed) ease, transform var(--transition-speed) ease`

Target `.btn:hover`:
- `background-color: var(--color-primary-dark)`
- `transform: translateY(-2px)` (subtle lift effect)

Target `.btn:active`:
- `transform: translateY(0)` (snap back on click)

### Part C — Card Hover Transition

Target `.card`:
- `background: var(--color-bg)`, `border: 1px solid var(--color-border)`, `border-radius: var(--radius)`, `padding: 1.25rem`
- `transition: box-shadow var(--transition-speed) ease, transform var(--transition-speed) ease`

Target `.card:hover`:
- `box-shadow: 0 8px 16px rgba(0,0,0,0.12)`
- `transform: translateY(-4px)`

### Part D — Colour Theme Toggle via CSS Variable Override

The `.dark-theme` class on a container overrides the root variables:
```css
.dark-theme {
  --color-bg: #1e1e2e;
  --color-text: #cdd6f4;
  --color-border: #45475a;
}
```
Apply `color: var(--color-text)` and `background: var(--color-bg)` to the `.theme-demo` element so that adding/removing the `.dark-theme` class changes colours without touching any other rules.

### Part E — @keyframes Animations

1. **Fade-in:** Create `@keyframes fadeIn` that goes from `opacity: 0` to `opacity: 1`. Apply it to `.fade-in` with `animation: fadeIn 1s ease forwards`.

2. **Bounce:** Create `@keyframes bounce` with:
   - `0%` → `transform: translateY(0)`
   - `40%` → `transform: translateY(-20px)`
   - `60%` → `transform: translateY(-10px)`
   - `100%` → `transform: translateY(0)`
   Apply to `.bounce` with `animation: bounce 1s ease infinite`.

3. **Spinner:** Create `@keyframes spin` that rotates from `0deg` to `360deg`. Apply to `.spinner` with `animation: spin 1s linear infinite`.
   - `.spinner`: `width: 40px`, `height: 40px`, `border: 4px solid var(--color-border)`, `border-top-color: var(--color-primary)`, `border-radius: 50%`

## Hints
- Variables are declared with `--name: value` and used with `var(--name)`.
- `transition` takes `property duration timing-function` — you can list multiple transitions separated by commas.
- `forwards` fill mode on an animation keeps the final keyframe state after it finishes.
- `animation: spin 1s linear infinite` — `linear` means constant speed (no ease in/out), which is what you want for a spinning loader.

## Expected Output

When opened in a browser:
- All colours come from `--color-primary` and related variables
- Buttons lift and darken smoothly on hover, snap back on click
- Cards cast a shadow and lift on hover
- The `.dark-theme` container visibly changes background/text colour
- `.fade-in` text fades in on page load
- `.bounce` element bounces continuously
- `.spinner` rotates continuously
