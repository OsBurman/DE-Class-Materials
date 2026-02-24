# Exercise 05 — BehaviorSubject and ReplaySubject as Shared State

## Learning Objectives
- Create a `BehaviorSubject` and expose it as an `Observable` with `.asObservable()`
- Emit new values with `.next()`
- Subscribe to a `BehaviorSubject` and receive the current value immediately on subscribe
- Create a `ReplaySubject` and understand the buffer parameter
- Share state between sibling components through a service

## Scenario
You are building a **counter widget** split across two sibling components:
- `CounterControlsComponent` — has Increment, Decrement, and Reset buttons
- `CounterDisplayComponent` — shows the current count and a log of the last 3 messages

Both components share state through a `StateService`.

## Instructions

### Step 1 — Build `StateService`
Open `state.service.ts` and complete the `TODO` items:

1. Declare `private countSubject = new BehaviorSubject<number>(0)`.
2. Expose `count$ = this.countSubject.asObservable()`.
3. Implement `increment()` — emit `this.countSubject.value + 1`.
4. Implement `decrement()` — emit `this.countSubject.value - 1`.
5. Implement `reset()` — emit `0`.
6. Declare `private logSubject = new ReplaySubject<string>(3)`.
7. Expose `log$ = this.logSubject.asObservable()`.
8. In each of `increment`, `decrement`, `reset`, call `this.logSubject.next(...)` with a descriptive message.

### Step 2 — Complete `CounterDisplayComponent`
Open `counter-display.component.ts` and complete the `TODO` items:

1. Inject `StateService`.
2. Subscribe to `count$` in `ngOnInit`, set `this.count`.
3. Subscribe to `log$`, push each message onto `this.messages`.

### Step 3 — Complete `CounterControlsComponent`
Open `counter-controls.component.ts` and delegate button clicks to the service methods.

## Expected Behaviour
- Clicking Increment / Decrement updates the displayed count immediately.
- The log panel shows up to 3 most-recent action messages.
- A late subscriber to `log$` still receives the last 3 messages (ReplaySubject buffer).

## Key Concepts
| Concept | API |
|---|---|
| Current-value subject | `new BehaviorSubject<T>(initialValue)` |
| Expose as observable | `.asObservable()` |
| Emit value | `.next(value)` |
| Read current value | `.value` (BehaviorSubject only) |
| Buffered replay | `new ReplaySubject<T>(bufferSize)` |
