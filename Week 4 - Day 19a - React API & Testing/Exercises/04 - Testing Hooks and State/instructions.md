# Exercise 04: Testing Hooks and State Changes

## Objective
Use `renderHook` and `act` from React Testing Library to test a custom hook in isolation, verifying that state updates and side effects behave correctly.

## Background
Custom hooks encapsulate reusable logic, but testing them through a component adds noise. `@testing-library/react` exports `renderHook`, which mounts a minimal host component just to run the hook. Wrapping state-changing calls in `act(...)` ensures React processes all updates before you make assertions.

## Requirements
1. A custom hook `useCounter` is provided in `useCounter.ts`. It:
   - Accepts an `initialValue: number` (default `0`).
   - Returns `{ count, increment, decrement, reset }`.
   - `increment` adds 1, `decrement` subtracts 1, `reset` sets count back to `initialValue`.
2. In `useCounter.test.ts`, write the following tests inside a `describe('useCounter', ...)` block:
   - **"initialises count to the provided value"** — render the hook with `initialValue: 5` and assert `count === 5`.
   - **"increments count by 1"** — call `increment()` inside `act` and assert `count === 1` (starting from 0).
   - **"decrements count by 1"** — call `decrement()` inside `act` and assert `count === -1`.
   - **"resets to the initial value"** — increment twice, then call `reset()` inside `act`, assert `count === 0`.
3. All four tests must pass.

## Hints
- Import `renderHook`, `act` from `@testing-library/react`.
- Access the hook's return value via `result.current`.
- Remember: after calling a state-updating function you must read `result.current` again — the object reference updates.
- You do NOT need to render any JSX — `renderHook` is enough.

## Expected Output
```
PASS  src/useCounter.test.ts
  useCounter
    ✓ initialises count to the provided value
    ✓ increments count by 1
    ✓ decrements count by 1
    ✓ resets to the initial value

Tests: 4 passed, 4 total
```
