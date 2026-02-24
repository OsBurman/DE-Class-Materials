# Exercise 01: Creating and Using Signals

## Objective
Practice creating writable Angular signals and updating their values with `.set()` and `.update()`.

## Background
Angular 16 introduced Signals as a new reactive primitive. A signal holds a value and notifies any consumers when that value changes. Unlike RxJS Observables, you read a signal by calling it as a function — `mySignal()` — and write to it with `.set()` or `.update()`.

## Requirements
1. Create a writable signal called `count` initialised to `0`.
2. Create a writable signal called `username` initialised to `'Guest'`.
3. Add an `increment()` method that uses `.update()` to add 1 to `count`.
4. Add a `decrement()` method that uses `.update()` to subtract 1 from `count`, but never let the value go below `0` (clamp at zero).
5. Add a `reset()` method that uses `.set()` to return `count` to `0`.
6. Add a `setUsername(name: string)` method that uses `.set()` to update `username`.
7. Display both signal values in the template by calling them with `()` — e.g. `{{ count() }}`.
8. Wire four buttons in the template: **Increment**, **Decrement**, **Reset**, and a text input + **Set Name** button that calls `setUsername()` with the input value.

## Hints
- Import `signal` from `@angular/core`.
- Signals are read by calling them like a function: `this.count()` in class code, `count()` in templates.
- `.update(prev => ...)` receives the current value and returns the new value — great for math operations.
- Use `Math.max(0, prev - 1)` inside `.update()` to clamp the decrement.

## Expected Output
When the page loads:
```
Count: 0
Hello, Guest
```
After clicking Increment twice, then Decrement once, then typing "Alice" and clicking Set Name:
```
Count: 1
Hello, Alice
```
After clicking Reset:
```
Count: 0
Hello, Alice
```
