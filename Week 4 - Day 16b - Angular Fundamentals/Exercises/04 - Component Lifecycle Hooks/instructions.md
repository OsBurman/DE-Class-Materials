# Exercise 04: Component Lifecycle Hooks

## Objective
Implement Angular's four most important lifecycle hooks — `ngOnInit`, `ngOnChanges`, `ngOnDestroy`, and `ngAfterViewInit` — to understand when Angular calls each one and what work belongs in each.

## Background
Every Angular component goes through a lifecycle: it is created, rendered, updated when inputs change, and eventually destroyed. Angular calls specific **lifecycle hook methods** at each stage. Knowing which hook to use for which task (e.g., fetch data in `ngOnInit`, clean up subscriptions in `ngOnDestroy`) is essential for writing correct, leak-free Angular components.

## Requirements

The exercise has two components: a **parent** (`AppComponent`) and a **child** (`LifecycleChildComponent`). The parent passes an `@Input` to the child so you can observe `ngOnChanges`.

### `LifecycleChildComponent` (`lifecycle-child.component.ts`):

1. Implement the `OnInit` interface and its `ngOnInit()` method:
   - Log `"[ngOnInit] Component initialized. message = " + this.message` to the console
   - Set `this.initTime` to the current time string: `new Date().toLocaleTimeString()`

2. Implement the `OnChanges` interface and its `ngOnChanges(changes: SimpleChanges)` method:
   - Log `"[ngOnChanges] Input changed: "` + `changes['message'].currentValue` to the console
   - (Angular calls this before `ngOnInit` on first render, then again every time the `message` input changes)

3. Implement the `AfterViewInit` interface and its `ngAfterViewInit()` method:
   - Log `"[ngAfterViewInit] View is fully initialized"` to the console
   - Set `this.viewReady = true`

4. Implement the `OnDestroy` interface and its `ngOnDestroy()` method:
   - Log `"[ngOnDestroy] Component is being destroyed"` to the console
   - Set `this.cleanupDone = true`

5. The component template (`lifecycle-child.component.html`) should display:
   - `message` (received via `@Input`)
   - `initTime` (set in ngOnInit)
   - `viewReady` (set in ngAfterViewInit)
   - `cleanupDone` (set in ngOnDestroy — will show `false` until the component is destroyed)

### `AppComponent`:

6. Add a `message` string property, initialized to `'Hello from Parent'`
7. Add a `showChild` boolean property initialized to `true`
8. Add a `changeMessage()` method that updates `message` to `'Updated message at ' + new Date().toLocaleTimeString()`
9. Add a `toggleChild()` method that toggles `showChild` between `true` and `false`
10. In `app.component.html`:
    - Use `*ngIf="showChild"` to conditionally render `<app-lifecycle-child>`
    - Pass `[message]="message"` to the child
    - Add a "Change Message" button that calls `changeMessage()`
    - Add a "Destroy / Re-create" button that calls `toggleChild()`

## Hints
- Interfaces (`OnInit`, `OnChanges`, etc.) are optional at runtime but required for TypeScript's type checking — always implement them
- `ngOnChanges` receives a `SimpleChanges` object; access a specific input with `changes['propertyName'].currentValue`
- When you click "Destroy", Angular calls `ngOnDestroy` — open the browser console to see all the log messages in order
- Hook method names must be spelled exactly right: `ngOnInit`, `ngOnChanges`, `ngAfterViewInit`, `ngOnDestroy`

## Expected Output
Browser console (in order, on first load):
```
[ngOnChanges] Input changed: Hello from Parent
[ngOnInit] Component initialized. message = Hello from Parent
[ngAfterViewInit] View is fully initialized
```

After clicking "Change Message":
```
[ngOnChanges] Input changed: Updated message at 10:32:05 AM
```

After clicking "Destroy":
```
[ngOnDestroy] Component is being destroyed
```
