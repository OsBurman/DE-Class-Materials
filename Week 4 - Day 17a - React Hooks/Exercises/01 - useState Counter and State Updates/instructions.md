# Exercise 01: useState Counter and State Updates

## Objective
Use the `useState` hook to manage multiple independent pieces of state and practice all three patterns for updating state: direct set, functional update, and resetting to default.

## Background
`useState` is the most fundamental React hook. It lets a functional component "remember" a value between renders. Every time you call the setter function, React schedules a re-render with the new value. Understanding when to use a direct value vs. a functional update (e.g., `setCount(c => c + 1)`) is critical for avoiding stale-closure bugs.

## Requirements

1. Create a `Counter` component with two independent state variables:
   - `count` (number, initial value `0`)
   - `step` (number, initial value `1`)

2. Render the current count in an `<h2>`: `"Count: 0"`

3. Add three buttons that modify `count`:
   - **Increment** — increases `count` by `step` using a **functional update**: `setCount(prev => prev + step)`
   - **Decrement** — decreases `count` by `step` using a functional update
   - **Reset** — resets `count` back to `0`

4. Add a "Step" control:
   - A `<label>` with text `"Step:"`
   - An `<input type="number">` bound via `value={step}` and `onChange` that updates `step` (parse to integer with `parseInt`)

5. Create a second independent component `ClickTracker`:
   - Tracks a `clicks` state (number, starts at `0`)
   - Has a single `<button>` that increments `clicks` on every click
   - Displays: `"You've clicked N times"` where N updates in real-time
   - Has a Reset button to set `clicks` back to `0`

6. Render both `<Counter />` and `<ClickTracker />` from `App`.

## Hints
- `useState` returns a pair `[value, setter]` — destructure it: `const [count, setCount] = useState(0)`
- Use the functional form `setCount(prev => prev + step)` when the new state depends on the old state — it avoids stale closures in async scenarios
- Each component instance gets its own independent state — `Counter`'s `count` and `ClickTracker`'s `clicks` don't share any memory
- `parseInt(e.target.value)` converts the string from an input to a number; `|| 1` prevents NaN: `parseInt(e.target.value) || 1`

## Expected Output
```
Count: 5
Step: [2]
[Increment]  [Decrement]  [Reset]

You've clicked 3 times
[Click Me!]  [Reset]
```
(Clicking Increment with step=2 increases count by 2 each click.)
