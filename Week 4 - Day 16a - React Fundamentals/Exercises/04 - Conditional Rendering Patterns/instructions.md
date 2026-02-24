# Exercise 04: Conditional Rendering Patterns

## Objective
Apply four different conditional rendering techniques in React — all inside JSX — to show or hide UI based on data.

## Background
React has no template directives like `v-if` or `*ngIf`. Instead, you use plain JavaScript expressions inside `{}`. There are four common patterns:

| Pattern | Best for |
|---------|----------|
| `&&` (short-circuit) | Show something only when a condition is `true` |
| Ternary `? :` | Switch between two alternatives |
| Early return | Skip rendering an entire component when data is missing |
| Function / switch | Render one of several distinct variants based on a value |

## Requirements

1. **`&&` operator** — Create a `ProductBadge` component:
   - Accepts `name` (string) and `isNew` (boolean)
   - Renders the product name in an `<h3>`
   - Uses `&&` to render a `<span className="badge new-badge">NEW!</span>` **only if** `isNew` is `true`

2. **Ternary** — Create a `StockStatus` component:
   - Accepts `inStock` (boolean)
   - Renders `<p className="in-stock">✅ In Stock</p>` if `true`, or `<p className="out-of-stock">❌ Out of Stock</p>` if `false`

3. **Early return** — Create a `UserProfile` component:
   - Accepts a `user` prop (object or `null`)
   - If `user` is `null` or `undefined`, **return null** (renders nothing)
   - Otherwise render `<div className="profile"><h3>{user.name}</h3><p>{user.email}</p></div>`

4. **Function/switch** — Create a `StatusBadge` component:
   - Accepts a `status` prop: one of `"active"`, `"pending"`, `"suspended"`, or any unknown string
   - Implement a helper function `getStatusStyle(status)` that returns an object `{ label, color }`:
     - `"active"` → `{ label: "Active", color: "green" }`
     - `"pending"` → `{ label: "Pending", color: "orange" }`
     - `"suspended"` → `{ label: "Suspended", color: "red" }`
     - default → `{ label: "Unknown", color: "gray" }`
   - Render `<span style={{ color }}>⬤ {label}</span>`

5. In `App`, render all four patterns with varied prop values so every branch is visible.

## Hints
- **Avoid** putting `if` statements directly in JSX — put them in the function body above the `return`, or use `&&` / ternary inside `{}`
- `&&` can cause a `0` to render if the left side is a number — use a boolean: `{count > 0 && <span>...</span>}` not `{count && ...}`
- Early return must appear **before** the `return (...)`, not inside JSX

## Expected Output
The browser renders product cards demonstrating all four patterns:

```
Widget Pro  [NEW!]          Classic Mug
✅ In Stock                  ❌ Out of Stock

[User profile card — Alice]  [nothing — user is null]

⬤ Active   ⬤ Pending   ⬤ Suspended   ⬤ Unknown
```
