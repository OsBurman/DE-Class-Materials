# Exercise 03: React Testing Library Basics — Component Tests

## Objective
Write unit tests for a React component using **Jest** and **React Testing Library (RTL)**, covering rendering, text content, and user interaction.

## Background
React Testing Library encourages testing components the way users interact with them — by querying the DOM for visible text, roles, and labels rather than component internals. Jest provides the test runner, assertion library, and mocking utilities. Create React App (and Vite with the testing plugin) ship with both pre-configured.

## Requirements
1. A `Greeting` component is provided that:
   - Accepts a `name: string` prop.
   - Renders `<h1>Hello, {name}!</h1>`.
   - Renders a button labelled **"Change Name"**.
   - Clicking the button changes the displayed name to `"World"`.
2. In `Greeting.test.tsx`, write the following tests inside a `describe('Greeting', ...)` block:
   - **"renders the greeting with the given name"** — render with `name="Alice"` and assert `Hello, Alice!` is in the document.
   - **"renders the Change Name button"** — assert a button with the text `Change Name` exists.
   - **"changes the name to World when button is clicked"** — use `fireEvent.click` on the button and assert `Hello, World!` is in the document.
   - **"does not render old name after button click"** — assert `Hello, Alice!` is no longer in the document after the click.
3. All four tests must pass with `npm test`.

## Hints
- Import `render`, `screen`, `fireEvent` from `@testing-library/react`.
- Use `screen.getByText(...)` and `screen.getByRole('button', { name: /change name/i })`.
- `expect(element).toBeInTheDocument()` requires `@testing-library/jest-dom` (already set up via `setupTests.ts`).
- `screen.queryByText(...)` returns `null` if not found — use this for "not present" assertions.

## Expected Output
```
PASS  src/Greeting.test.tsx
  Greeting
    ✓ renders the greeting with the given name
    ✓ renders the Change Name button
    ✓ changes the name to World when button is clicked
    ✓ does not render old name after button click

Test Suites: 1 passed, 1 total
Tests:       4 passed, 4 total
```
