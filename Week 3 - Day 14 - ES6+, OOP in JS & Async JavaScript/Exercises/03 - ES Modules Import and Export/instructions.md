# Exercise 03: ES Modules — Import and Export

## Objective
Practice splitting JavaScript code across multiple files using ES Module `export` and `import` syntax, and understand the difference between named exports and default exports.

## Background
Before ES Modules, JavaScript had no native module system — code was either concatenated or loaded via `<script>` tags. ES Modules (`import`/`export`) give every file its own scope, allow explicit declarations of what a module exposes, and are the foundation of modern bundlers (Vite, Webpack) and JavaScript environments (Node.js with `"type": "module"`). In a browser, modules are loaded by adding `type="module"` to the script tag.

## Requirements
1. Open `index.html` in a browser. It already loads `main.js` as a module script (`<script type="module" src="main.js">`).
2. In `mathUtils.js`, create the following **named exports**:
   - A function `add(a, b)` that returns `a + b`.
   - A function `subtract(a, b)` that returns `a - b`.
   - A constant `PI` set to `3.14159`.
3. In `mathUtils.js`, also create a **default export**: a function `multiply(a, b)` that returns `a * b`.
4. In `stringUtils.js`, create the following named exports:
   - A function `capitalize(str)` that returns the string with its first character uppercased.
   - A function `reverseString(str)` that returns the string reversed.
5. In `main.js`, import `add`, `subtract`, and `PI` from `mathUtils.js` using a **named import**.
6. In `main.js`, import the default export from `mathUtils.js` as `multiply`.
7. In `main.js`, import all named exports from `stringUtils.js` using a **namespace import** (`import * as StringUtils from './stringUtils.js'`).
8. In `main.js`, use all the imported functions and constants to log the following results (see Expected Output).
9. In `main.js`, demonstrate **re-exporting**: create a one-liner that re-exports `capitalize` from `stringUtils.js` (use `export { capitalize } from './stringUtils.js'`). Then import and use it in a `console.log`.

## Hints
- Browsers require `type="module"` on the `<script>` tag. Module scripts are deferred automatically and run in strict mode.
- Named imports use curly braces: `import { add, PI } from './mathUtils.js'`. Default imports do NOT use curly braces: `import multiply from './mathUtils.js'`.
- Import paths in browsers must include the file extension (`.js`) and must be relative (`./`).
- A single file can have both named exports and one default export. They are imported separately but can be combined: `import multiply, { add, PI } from './mathUtils.js'`.

## Expected Output

Browser DevTools console:
```
5 + 3 = 8
10 - 4 = 6
PI = 3.14159
4 × 7 = 28
Capitalize "hello": Hello
Reverse "module": eludom
Re-exported capitalize "world": World
```
