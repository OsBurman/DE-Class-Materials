# Exercise 04: Signals vs Observables

## Objective
Compare Angular Signals with RxJS Observables by implementing the same counter in both approaches, then practice bridging between them with `toSignal()` and `toObservable()`.

## Background
Signals and Observables solve similar problems but with different models. Observables are lazy, stream-based, and require subscriptions. Signals are eager, always hold a current value, and synchronously propagate changes through computed dependencies. Angular 16+ ships `toSignal()` and `toObservable()` in `@angular/core/rxjs-interop` to let you mix the two models in the same application.

## Requirements

### Part A — Side-by-side Counter
1. Implement an **Observable-based counter** using a `BehaviorSubject<number>(0)`. Expose the Observable via `.asObservable()`. Provide `incrementObs()`, `decrementObs()`, `resetObs()` methods.
2. Implement a **Signal-based counter** using `signal<number>(0)`. Provide `incrementSig()`, `decrementSig()`, `resetSig()` methods.
3. In the template, display both counters side by side, using the `async` pipe for the Observable and calling the signal directly. Label each section clearly ("Observable Counter" and "Signal Counter").

### Part B — Interop
4. Use `toSignal()` to convert the Observable counter stream into a signal called `obsAsSignal`. Display it in the template with the label "Observable → Signal".
5. Use `toObservable()` to convert the Signal counter into an Observable. Subscribe to it in `ngOnInit` and store the latest emitted value in a plain number property `sigAsObsValue`. Display it with the label "Signal → Observable".
6. Add `ngOnDestroy` and unsubscribe from the `toObservable()` subscription to avoid memory leaks.

## Hints
- Import `toSignal` and `toObservable` from `@angular/core/rxjs-interop`.
- `toSignal(observable$)` requires an injection context — call it as a class field initialiser or inside the constructor.
- `toObservable(signal)` also requires an injection context.
- The `async` pipe automatically subscribes and unsubscribes — no manual cleanup needed for that one.
- Use `Subscription` from `rxjs` to hold the `toObservable()` subscription for cleanup.

## Expected Output
Initial state:
```
Observable Counter: 0
Signal Counter: 0
Observable → Signal: 0
Signal → Observable: 0
```
After clicking Observable +1 twice, then Signal +1 once:
```
Observable Counter: 2
Signal Counter: 1
Observable → Signal: 2
Signal → Observable: 1
```
