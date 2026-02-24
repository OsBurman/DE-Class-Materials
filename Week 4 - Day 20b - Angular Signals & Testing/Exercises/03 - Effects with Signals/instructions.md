# Exercise 03: Effects with Signals

## Objective
Practice using `effect()` to run side-effects automatically whenever a tracked signal changes.

## Background
An `effect` is a function that Angular re-runs whenever any signal it reads changes. It is similar to `ngOnChanges` but reactive — it automatically re-subscribes when its dependencies update. Effects must be created inside an **injection context** (e.g., inside a constructor or a factory passed to `runInInjectionContext`). Effects can also return a cleanup function that runs before the next execution.

## Requirements
1. Create a writable signal `theme` of type `'light' | 'dark'` initialised to `'light'`.
2. Create a writable signal `fontSize` (number) initialised to `16`.
3. Create a writable signal `logHistory` as a `string[]` signal initialised to `[]`.
4. In the **constructor**, create an `effect` that:
   - Reads `theme()` and `fontSize()`
   - Calls `document.body.setAttribute('data-theme', theme())` to apply the theme to the DOM
   - Calls `document.body.style.fontSize = fontSize() + 'px'`
   - Pushes a log entry string like `"[14:30:00] theme=dark, fontSize=16"` into `logHistory` using `.update()`
   (Use `new Date().toLocaleTimeString()` for the timestamp.)
5. Add a `toggleTheme()` method that switches `theme` between `'light'` and `'dark'` using `.update()`.
6. Add an `increaseFontSize()` method that adds 2 to `fontSize` using `.update()`, capped at `24`.
7. Add a `decreaseFontSize()` method that subtracts 2 from `fontSize` using `.update()`, minimum `10`.
8. In the template, display `theme()`, `fontSize()`, and the full `logHistory()` array (one entry per line). Add buttons for Toggle Theme, Increase Font, and Decrease Font.

## Hints
- Import `effect` from `@angular/core` alongside `signal` and `computed`.
- The effect **must** be created inside the constructor so Angular's injection context is active.
- `.update(prev => [...prev, newEntry])` creates a new array — signals track reference equality for arrays.
- In the template use `@for (entry of logHistory(); track $index)` (or `*ngFor`) to render the log.

## Expected Output
On first load the effect runs immediately:
```
Theme: light   Font Size: 16px
Log:
[HH:MM:SS] theme=light, fontSize=16
```
After clicking Toggle Theme then Increase Font:
```
Theme: dark   Font Size: 18px
Log:
[HH:MM:SS] theme=light, fontSize=16
[HH:MM:SS] theme=dark, fontSize=16
[HH:MM:SS] theme=dark, fontSize=18
```
