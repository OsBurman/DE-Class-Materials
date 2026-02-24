# Exercise 07: Component Encapsulation — ViewEncapsulation Modes

## Objective
Observe how Angular's three `ViewEncapsulation` modes (`Emulated`, `None`, `ShadowDom`) affect CSS scoping by placing identical style rules in three different components and watching which styles "leak" into sibling components.

## Background
By default Angular uses `ViewEncapsulation.Emulated` — it adds unique attribute selectors to component styles so they only match elements inside that component. Setting it to `None` disables scoping entirely (styles go global). `ShadowDom` uses the native browser Shadow DOM for true style isolation. Understanding the difference prevents hard-to-debug styling conflicts in large apps.

## Requirements

### Shared Setup
In `AppComponent`'s template, render all three demo components side-by-side. Each component contains a `<div>` with class `"card"` and an `<h3>` heading.

### Component A — `EmulatedCardComponent`
1. Use `ViewEncapsulation.Emulated` (this is the **default** — you can omit the `encapsulation` property or set it explicitly).
2. In the component's `styles` array, define `.card { border: 3px solid green; background: #e8f5e9; }`.
3. Display the heading "Emulated Encapsulation" and a short description paragraph.
4. Observe: these styles should **not** affect the other two components' cards.

### Component B — `NoneCardComponent`
1. Set `encapsulation: ViewEncapsulation.None` in the decorator.
2. In the component's `styles` array, define `.card { border: 3px solid red; }`.
3. Display the heading "No Encapsulation" and a short description paragraph.
4. Observe: because `None` styles are global, the `.card { border: 3px solid red }` rule will leak and override the green border on Component A's card in the browser (whichever loads last wins).

### Component C — `ShadowCardComponent`
1. Set `encapsulation: ViewEncapsulation.ShadowDom`.
2. In the component's `styles` array, define `.card { border: 3px solid blue; background: #e3f2fd; }`.
3. Display the heading "Shadow DOM Encapsulation" and a short description paragraph.
4. Observe: Shadow DOM styles are truly isolated — they cannot be overridden from outside, and they cannot leak out.

### `AppComponent`
- Add a `<style>` block (inline in the template) that sets a global `.card { padding: 16px; margin: 12px; border-radius: 6px; font-family: Arial, sans-serif; }` so all cards have consistent spacing.

5. Declare all three components in `AppModule`.

## Hints
- Import `ViewEncapsulation` from `'@angular/core'`.
- `ViewEncapsulation.Emulated` adds unique attribute selectors like `[_ngcontent-abc-c0]` to component styles — inspect the DOM to see them.
- `ViewEncapsulation.None` styles are injected into the `<head>` with no scoping attribute.
- `ShadowDom` creates a real Shadow Root — use DevTools to inspect the `#shadow-root`.
- The goal is observation, not perfection: the "leaking" effect of `None` is intentional and is the lesson.

## Expected Output
```
┌─ Emulated Card ──────────────────────────────┐   ← green border (normally)
│  Emulated Encapsulation                      │      (red if None loads after)
│  Styles are scoped with attribute selectors. │
└──────────────────────────────────────────────┘

┌─ None Card ──────────────────────────────────┐   ← red border (global leak)
│  No Encapsulation                            │
│  These styles leak to other components!      │
└──────────────────────────────────────────────┘

┌─ Shadow DOM Card ────────────────────────────┐   ← blue border (isolated)
│  Shadow DOM Encapsulation                    │
│  Truly isolated via native Shadow DOM.       │
└──────────────────────────────────────────────┘
```
