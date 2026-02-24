# Exercise 01 — Render Props Pattern

## Objective
Practice the render props pattern to share stateful logic between components without inheritance.

## Background
A render prop is a function prop that a component calls to know what to render. It is one of the original React composition patterns — still widely used in libraries like React Router and Formik — and understanding it helps you read real-world codebases and appreciate how hooks later replaced many of its use cases.

## Requirements
1. Create a `MouseTracker` component that:
   - Tracks the mouse `x` and `y` position in its own state using `useState`.
   - Attaches a `mousemove` listener to the `window` in a `useEffect` (clean it up on unmount).
   - Accepts a **render prop** named `render` of type `(pos: { x: number; y: number }) => React.ReactNode`.
   - Calls `props.render({ x, y })` as its return value — it renders **nothing else** itself.
2. Create a `CoordinateDisplay` component that uses `<MouseTracker render={...} />` to display:
   - A paragraph showing `Mouse position: X: {x}, Y: {y}` (updated live as the mouse moves).
3. Create a `CrosshairBox` component that also uses `<MouseTracker render={...} />` to display:
   - A 300×200px bordered box.
   - A small 10×10px circle centred at `(x, y)` relative to the viewport using `position: fixed`.
4. Render both `CoordinateDisplay` and `CrosshairBox` inside `App`.

## Hints
- The render prop receives the mouse position — the parent component owns no mouse state itself.
- Clean up `window.addEventListener` inside the `useEffect` return function.
- The crosshair circle should use `position: fixed; left: x - 5; top: y - 5` to centre it on the cursor.
- Both consumers can use the same `MouseTracker` — the state is tracked once per instance.

## Expected Output
```
Mouse position: X: 412, Y: 280    ← updates live as mouse moves

[300×200 bordered box displayed, with a small dot following the cursor]
```
