# Day 20b Application — Angular Signals & Testing: Signal Counter Dashboard

## Overview

You'll build a **Reactive Counter Dashboard** using Angular Signals for state management, then write comprehensive Jasmine/Karma unit tests for both components and services.

---

## Learning Goals

- Create and update signals with `signal()`
- Derive values with `computed()`
- React to changes with `effect()`
- Compare signals to Observables
- Write component tests with Jasmine and Karma
- Use `TestBed` and `ComponentFixture`
- Mock service dependencies in tests

---

## Prerequisites

- `cd starter-code && npm install && npm run start`
- Tests: `npm run test`

---

## Project Structure

```
starter-code/
└── src/app/
    ├── app.component.ts/.html
    ├── services/
    │   └── counter.service.ts          ← TODO: signals-based service
    └── components/
        ├── counter-panel/              ← TODO: use signals
        └── stats-panel/               ← TODO: computed signals
    └── spec/
        ├── counter.service.spec.ts     ← TODO: service tests
        └── counter-panel.spec.ts       ← TODO: component tests
```

---

## Part 1 — `CounterService` with Signals

**Task 1**  
```ts
@Injectable({ providedIn: 'root' })
export class CounterService {
  count = signal(0);
  history = signal<number[]>([]);
  doubled = computed(() => this.count() * 2);
  isPositive = computed(() => this.count() > 0);

  increment() { this.count.update(v => v + 1); }
  decrement() { this.count.update(v => v - 1); }
  reset() { this.count.set(0); }
  stepBy(n: number) { this.count.update(v => v + n); }
}
```
Add an `effect()` that logs `"Counter changed to: [value]"` whenever `count` changes. Add to `history` signal on every change.

---

## Part 2 — `CounterPanel` Component

**Task 2 — Use signals in template**  
Inject `CounterService`. In the template, read signal values directly:
```html
<p>Count: {{ counterService.count() }}</p>
<p>Doubled: {{ counterService.doubled() }}</p>
```
Buttons call `increment()`, `decrement()`, `reset()`, and `stepBy(5)` / `stepBy(-5)`.

**Task 3 — Signal vs Observable**  
Add a comment block in `counter-panel.component.ts` explaining:
- How signals differ from `BehaviorSubject` (no subscribe/unsubscribe needed)
- Why signals don't need `async` pipe

---

## Part 3 — `StatsPanel` Component

**Task 4 — Computed signals**  
Inject `CounterService`. Display:
- Current count
- Doubled value
- Whether it's positive (conditional text: "Positive" / "Zero or Negative")
- History of last 5 values (from `history` signal)

---

## Part 4 — Tests

**Task 5 — `counter.service.spec.ts`**  
```ts
describe('CounterService', () => {
  it('should start at 0');
  it('should increment by 1');
  it('should decrement by 1');
  it('should reset to 0');
  it('should compute doubled value correctly');
  it('should reflect isPositive correctly');
});
```
Use `TestBed.inject(CounterService)` and read signal values with `service.count()`.

**Task 6 — `counter-panel.spec.ts`**  
```ts
describe('CounterPanelComponent', () => {
  it('should create');
  it('should display initial count as 0');
  it('should increment count when increment button is clicked');
  it('should reset count when reset button is clicked');
});
```
Use `TestBed.createComponent(CounterPanelComponent)`, `fixture.detectChanges()`, `nativeElement.querySelector('button')`.click()`, and `fixture.debugElement`.

---

## Submission Checklist

- [ ] `signal()` used for mutable state
- [ ] `computed()` used for derived values
- [ ] `effect()` used for side effects
- [ ] Signal values read in template with `()`
- [ ] Signal vs Observable comparison comment added
- [ ] At least 6 service tests pass
- [ ] At least 4 component tests pass
- [ ] All tests pass with `npm run test`
