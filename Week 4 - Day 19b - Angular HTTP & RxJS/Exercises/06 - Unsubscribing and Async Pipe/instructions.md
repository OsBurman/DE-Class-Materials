# Exercise 06 — Unsubscribing and the Async Pipe

## Learning Objectives
- Understand why subscriptions must be cleaned up to prevent memory leaks
- Manually unsubscribe using the `takeUntil` + `Subject` pattern in `ngOnDestroy`
- Use the `async` pipe in a template to manage subscriptions automatically
- Compare manual vs automatic subscription management

## Scenario
You are building a **live ticker** that emits a new number every second (using `interval`). You will implement two components:
1. `ManualUnsubscribeComponent` — subscribes manually and tears down with `takeUntil`.
2. `AsyncPipeComponent` — exposes an Observable directly to the template with the `async` pipe.

## Instructions

### Step 1 — Build `TickerService`
Open `ticker.service.ts` and complete the `TODO` item:
- Return `interval(1000).pipe(map(i => i + 1))` from `getTicker()`.

### Step 2 — Complete `ManualUnsubscribeComponent`
Open `manual-unsubscribe.component.ts` and complete the `TODO` items:

1. Declare `private destroy$ = new Subject<void>()`.
2. In `ngOnInit`, subscribe to `tickerService.getTicker()` piped through `takeUntil(this.destroy$)`; set `this.tick` on each emission.
3. In `ngOnDestroy`, call `this.destroy$.next()` then `this.destroy$.complete()`.

### Step 3 — Complete `AsyncPipeComponent`
Open `async-pipe.component.ts` and complete the `TODO` item:
- Assign `this.tick$ = this.tickerService.getTicker()`.
- In the template, use `| async` to subscribe: `{{ tick$ | async }}`.

### Step 4 — Compare behaviour
- Both components count up from 1 each second.
- When `ManualUnsubscribeComponent` is destroyed (e.g. via `*ngIf`), no more subscriptions fire — confirmed by the console being silent.
- The `async` pipe unsubscribes automatically when the component is destroyed.

## Expected Behaviour
- Both tickers count up independently.
- No memory leaks occur.
- Toggling visibility stops the manual component cleanly.

## Key Concepts
| Concept | API |
|---|---|
| Teardown subject | `private destroy$ = new Subject<void>()` |
| Auto-complete stream | `.pipe(takeUntil(this.destroy$))` |
| Cleanup hook | `ngOnDestroy() { this.destroy$.next(); this.destroy$.complete(); }` |
| Template subscription | `{{ observable$ | async }}` |
| Conditional async | `*ngIf="data$ | async as data"` |
