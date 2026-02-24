# Day 20b Part 1 — Angular Signals
## Slide Descriptions

**Total slides: 16**

---

### Slide 1 — Title Slide

**Title:** Angular Signals
**Subtitle:** Reactive State Without the Boilerplate
**Day:** Week 4 — Day 20b | Part 1 of 2

**Objectives listed on slide:**
- Understand why Signals were introduced and what problem they solve
- Create, read, and update signals with `signal()`
- Derive values automatically with `computed()`
- Trigger side effects reactively with `effect()`
- Use signals in components and services
- Bridge signals and Observables with `toSignal()` and `toObservable()`
- Choose between Signals and Observables for the right use case

---

### Slide 2 — The Problem Signals Solve

**Title:** Why Do We Need Signals?

**Left column — Before Signals (BehaviorSubject for local state):**
```typescript
// service.ts — 5 lines just to hold a number
private _count = new BehaviorSubject(0);
count$ = this._count.asObservable();
increment() {
  this._count.next(this._count.getValue() + 1);
}

// component.ts
count$ = this.counterService.count$;

// template — async pipe required
<p>{{ count$ | async }}</p>
```

**Right column — Pain points:**
- `BehaviorSubject` for simple state is ceremonial boilerplate
- Must remember to unsubscribe (or use `async` pipe)
- `zone.js` checks the entire component tree on every browser event
- "Is this a value or an Observable?" — mental tracking overhead
- No built-in derived-value primitive

**Bottom callout:**
> Signals are Angular's answer: synchronous, fine-grained reactivity with zero subscription management.

---

### Slide 3 — What Is a Signal?

**Title:** Signals — Mental Model

**Top definition box:**
> A **signal** is a reactive value container that:
> 1. **Holds a value** — always synchronous, always has one
> 2. **Notifies dependents** when the value changes
> 3. **Tracks who reads it** — automatic dependency tracking

**Comparison table:**

| | Regular Variable | BehaviorSubject | Signal |
|---|---|---|---|
| Read syntax | `count` | `count$.subscribe()` or `async` pipe | `count()` |
| Always has value | ✅ | ✅ | ✅ |
| Reactivity | ❌ | ✅ (async) | ✅ (sync) |
| Derived values | ❌ | `pipe(map(...))` | `computed()` |
| Subscription needed | ❌ | ✅ | ❌ |

**Analogy callout:**
> Think of an Excel cell: change A1 and every formula that references A1 recalculates automatically — you never write `subscribe()`.

---

### Slide 4 — Creating and Reading Signals

**Title:** `signal()` — Create and Read

**Code block:**
```typescript
import { signal } from '@angular/core';

// Create — TypeScript infers the type
const count   = signal(0);           // WritableSignal<number>
const name    = signal('Alice');     // WritableSignal<string>
const isOpen  = signal(false);       // WritableSignal<boolean>
const user    = signal<User | null>(null);  // explicit generic

// Read — call it like a function
console.log(count());   // 0
console.log(name());    // 'Alice'

// In a component template — same () syntax, no async pipe
```
```html
<p>Count: {{ count() }}</p>
<p>Name: {{ name() }}</p>
<p>User: {{ user()?.displayName ?? 'Guest' }}</p>
```

**Key insight box:**
> The `()` is intentional. Calling a signal reads its current value AND registers the caller as a dependent — that's how Angular tracks what needs to update.

---

### Slide 5 — Writing to Signals

**Title:** `signal()` — Set and Update

**Code block:**
```typescript
const count = signal(0);
const items = signal<string[]>([]);

// .set() — replace the value entirely
count.set(10);
count.set(0);

// .update() — compute new value from current value
count.update(current => current + 1);    // 0 → 1
count.update(c => c * 2);               // 1 → 2

// .update() with arrays — ALWAYS create a new array reference
items.update(list => [...list, 'Angular']);   // ✅ new reference
items.update(list => list.filter(i => i !== 'Vue'));

// .asReadonly() — expose a read-only view to consumers
@Injectable({ providedIn: 'root' })
export class CartService {
  private _count = signal(0);
  readonly count = this._count.asReadonly(); // consumers can read, not write
}
```

**Warning callout:**
> When updating arrays or objects with `.update()`, always return a **new reference** — signals use `===` to detect changes. Mutating the existing array won't trigger updates.

---

### Slide 6 — computed()

**Title:** `computed()` — Automatic Derived Values

**Scenario:** A shopping cart that needs subtotal, discount, and total.

**Code block:**
```typescript
import { signal, computed } from '@angular/core';

interface CartItem { name: string; price: number; qty: number; }

const items    = signal<CartItem[]>([]);
const discount = signal(0.10);   // 10% discount

// computed — reads from other signals; auto-updates when they change
const subtotal = computed(() =>
  items().reduce((sum, item) => sum + item.price * item.qty, 0)
);
const total = computed(() => subtotal() * (1 - discount()));

// Reading
console.log(subtotal());  // 0

items.update(list => [...list, { name: 'Laptop', price: 999, qty: 1 }]);
console.log(subtotal());  // 999
console.log(total());     // 899.10

discount.set(0.20);
console.log(total());     // 799.20  ← updated because discount changed
```
```html
<p>Subtotal: {{ subtotal() | currency }}</p>
<p>Total:    {{ total()    | currency }}</p>
```

---

### Slide 7 — computed() Properties

**Title:** `computed()` — How It Works Under the Hood

**Property cards:**

**🦥 Lazy**
- Not computed until first read
- Angular doesn't compute values no one is looking at

**🧠 Memoized**
- Once computed, the result is cached
- Only recomputes when a tracked signal dependency changes
- Reading it 100 times after one `.set()` → runs the function once

**🔒 Read-Only**
- Returns `Signal<T>`, not `WritableSignal<T>`
- You cannot call `.set()` or `.update()` on a computed signal

**🚫 No Async**
- Cannot do HTTP calls or Promises inside `computed()`
- For async derived data: use a service + `toSignal()`

**When to use computed():**
- Filtered or sorted lists → `computed(() => items().filter(i => i.active))`
- Aggregated numbers → `computed(() => items().reduce(...))`
- Formatted display values → `computed(() => user().name.toUpperCase())`
- Boolean flags → `computed(() => cart().length > 0)`
- Anything derived from other signals — always prefer `computed()` over manual tracking

---

### Slide 8 — effect()

**Title:** `effect()` — Reactive Side Effects

**Concept:** Run code automatically whenever tracked signals change.

**Code block:**
```typescript
import { Component, signal, computed, effect } from '@angular/core';

@Component({ ... })
export class CartComponent {
  items = signal<CartItem[]>([]);
  total = computed(() => items().reduce((s, i) => s + i.price, 0));

  constructor() {
    // Effect runs once immediately, then again whenever items() changes
    effect(() => {
      console.log(`Cart updated: ${this.items().length} items`);
      localStorage.setItem('cart', JSON.stringify(this.items()));
    });

    // Effect with cleanup — runs before next execution and on destroy
    effect((onCleanup) => {
      const timer = setInterval(() => this.syncWithServer(), 30_000);
      onCleanup(() => clearInterval(timer));  // cleanup the old timer
    });
  }
}
```

**How it works:**
1. Effect runs once when created
2. Angular records which signals were read (`items()` in this case)
3. Whenever any tracked signal changes, the effect re-runs
4. `onCleanup` lets you tear down resources before the next run

---

### Slide 9 — effect() Rules and When to Use

**Title:** `effect()` — Rules and Use Cases

**Rules — two columns:**

**✅ DO use effects for:**
- Persisting state to localStorage / sessionStorage
- Logging analytics or debug events
- Imperatively managing focus (DOM operations)
- Bridging to non-Angular code (3rd party libraries)
- Starting/stopping an interval or timeout in response to state

**❌ DON'T use effects for:**
- Setting other signals (creates infinite loops)
  ```typescript
  // ❌ BROKEN — infinite loop
  effect(() => { this.doubled.set(this.count() * 2); });
  // ✅ Use computed() instead
  doubled = computed(() => this.count() * 2);
  ```
- Deriving state — that's `computed()`'s job
- Replacing async data fetching — use RxJS + `toSignal()` for HTTP

**Injection context rule:**

```typescript
// ✅ OK — in constructor (injection context)
constructor() { effect(() => { ... }); }

// ✅ OK — in field initializer
myEffect = effect(() => { ... });

// ❌ Breaks — outside injection context
someMethod() { effect(() => { ... }); }  // throws error

// ✅ Fix — use runInInjectionContext
someMethod() {
  runInInjectionContext(this.injector, () => effect(() => { ... }));
}
```

---

### Slide 10 — Signals in Components

**Title:** Signals in Components — Complete Example

**Code block — full standalone component:**
```typescript
import { Component, signal, computed } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector:  'app-counter',
  standalone: true,
  imports:   [CurrencyPipe],
  template: `
    <h2>Shopping Cart</h2>
    <p>Items: {{ itemCount() }}</p>
    <p>Total: {{ total() | currency }}</p>
    <button (click)="addItem()">Add $10 Item</button>
    <button (click)="reset()" [disabled]="itemCount() === 0">Clear</button>
  `
})
export class CartComponent {
  private items = signal<number[]>([]);

  // Derived values — update automatically
  itemCount = computed(() => this.items().length);
  total     = computed(() => this.items().reduce((s, p) => s + p, 0));

  addItem() { this.items.update(list => [...list, 10]); }
  reset()   { this.items.set([]); }
}
```

**Side notes on slide:**
- No `async` pipe needed — signals are synchronous
- Works with `ChangeDetectionStrategy.OnPush` automatically
- Angular 18+ supports **zoneless** change detection with signals — no `zone.js` at all

---

### Slide 11 — Signals in Services

**Title:** Signals in Services — Shared State

**Code block:**
```typescript
import { Injectable, signal, computed } from '@angular/core';

export interface CartItem { id: string; name: string; price: number; qty: number; }

@Injectable({ providedIn: 'root' })
export class CartService {
  // Private — only this service can write
  private items = signal<CartItem[]>([]);

  // Public read-only — components read these, can't write
  readonly items$  = this.items.asReadonly();
  readonly count   = computed(() => this.items().length);
  readonly total   = computed(() => this.items().reduce((s, i) => s + i.price * i.qty, 0));
  readonly isEmpty = computed(() => this.items().length === 0);

  addItem(item: CartItem): void {
    this.items.update(list => [...list, item]);
  }

  removeItem(id: string): void {
    this.items.update(list => list.filter(i => i.id !== id));
  }

  clear(): void {
    this.items.set([]);
  }
}
```
```html
<!-- Any component that injects CartService -->
<p>{{ cartService.count() }} items — {{ cartService.total() | currency }}</p>
```

**Pattern note:** Service owns the writable signal; components get read-only access. This is the signal equivalent of the BehaviorSubject + public Observable pattern.

---

### Slide 12 — toSignal() and toObservable() — Interop Bridges

**Title:** Mixing Signals and Observables

**Concept:** Signals and Observables are complementary, not competing.

**toSignal() — Observable → Signal (use in templates without async pipe):**
```typescript
import { toSignal } from '@angular/core/rxjs-interop';
import { inject }   from '@angular/core';

@Component({ ... })
export class ProductsComponent {
  private productService = inject(ProductService);

  // Observable from HTTP → Signal (must be called in injection context)
  products = toSignal(
    this.productService.getProducts(),
    { initialValue: [] as Product[] }  // value before first emission
  );

  // Template: {{ products() }} — no async pipe, no subscribe
}
```

**toObservable() — Signal → Observable (use in RxJS pipelines):**
```typescript
import { toObservable } from '@angular/core/rxjs-interop';

searchTerm = signal('');

// Convert to Observable to use debounce, switchMap, etc.
results$ = toObservable(this.searchTerm).pipe(
  debounceTime(300),
  distinctUntilChanged(),
  switchMap(term => this.productService.search(term))
);
```

---

### Slide 13 — Signal Inputs and model() (Angular 17+)

**Title:** Signal Inputs — `input()` and `model()`

**Old way (still valid):**
```typescript
@Input()                    title: string = '';
@Input({ required: true })  user!: User;
```

**New way — `input()` returns `InputSignal<T>`:**
```typescript
import { Component, input, computed } from '@angular/core';

@Component({ ... })
export class UserCardComponent {
  // Optional input with default value
  title = input('Untitled');              // InputSignal<string>

  // Required input — throws if not provided by parent
  user  = input.required<User>();         // InputSignal<User>

  // Derived value from input — auto-updates when parent changes input
  greeting = computed(() => `Hello, ${this.user().name}!`);
}
```
```html
<!-- Parent template -->
<app-user-card [title]="'Profile'" [user]="currentUser" />
```

**model() — two-way binding signal:**
```typescript
@Component({ ... })
export class RatingComponent {
  value = model(0);   // ModelSignal<number>

  increase() { this.value.update(v => v + 1); }
}
```
```html
<!-- Parent: [(value)] is two-way banana-in-a-box syntax -->
<app-rating [(value)]="myRating" />
```

---

### Slide 14 — Signals vs Observables — Full Comparison

**Title:** Signals vs Observables — When to Use What

**Comparison table:**

| Feature | Signals | Observables (RxJS) |
|---|---|---|
| Value access | `count()` — synchronous | `.subscribe()` or `async` pipe |
| Always has a value | ✅ Yes | Only if `BehaviorSubject` / `startWith` |
| Derived values | `computed()` | `pipe(map(...))` |
| Side effects | `effect()` | `subscribe()` / `tap()` |
| Lazy | `computed()` is lazy | Observable is lazy |
| Cancellation | ❌ No | ✅ Unsubscribe |
| Async (HTTP, events) | ❌ Not directly | ✅ Native |
| Error handling | ❌ No error channel | ✅ `catchError()` |
| Operators (debounce, etc.) | ❌ No | ✅ Full RxJS library |
| Boilerplate | Low | Medium–High |
| Best for | Component/service state | HTTP, events, complex async |

---

### Slide 15 — When to Choose Signals vs Observables

**Title:** Decision Guide — Signals vs Observables

**Use Signals when:**
```
✅ Managing component UI state (counter, toggle, form field value)
✅ Sharing state across components via a service (cart, auth state, theme)
✅ The value is always synchronous and always available
✅ You want derived state (computed) that auto-tracks dependencies
✅ You want to replace BehaviorSubject + async pipe boilerplate
```

**Use Observables when:**
```
✅ Making HTTP requests (Angular HttpClient returns Observables)
✅ Handling streams: DOM events, WebSockets, SSE
✅ You need RxJS operators: debounceTime, switchMap, retry, combineLatest
✅ You need explicit cancellation (cancel a pending HTTP request)
✅ Complex async coordination across multiple sources
```

**Use both with the interop bridge:**
```typescript
// HTTP Observable → Signal for clean template reading
products = toSignal(this.http.get<Product[]>('/api/products'), { initialValue: [] });

// Signal → Observable to add debounce to search
results$ = toObservable(this.searchTerm).pipe(debounceTime(300), switchMap(...));
```

**Rule of thumb:**
> Signals for **state**, Observables for **events and async data flows**.

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Recap — Angular Signals

**API reference table:**

| API | Type Returned | Read | Write | Use for |
|---|---|---|---|---|
| `signal(value)` | `WritableSignal<T>` | `sig()` | `.set()` `.update()` | State |
| `computed(fn)` | `Signal<T>` (read-only) | `comp()` | ❌ | Derived values |
| `effect(fn)` | `EffectRef` | N/A | via side effect | Sync to external |
| `input(default)` | `InputSignal<T>` | `inp()` | ❌ (parent writes) | Component inputs |
| `model(default)` | `ModelSignal<T>` | `mod()` | `.set()` `.update()` | Two-way binding |
| `toSignal(obs$)` | `Signal<T>` | `sig()` | ❌ | Obs → Signal bridge |
| `toObservable(sig)` | `Observable<T>` | subscribe | N/A | Signal → Obs bridge |

**Key rules:**
- Read signals with `()` — this also registers the dependency
- Never set a signal inside `effect()` — use `computed()` instead
- `.update()` with arrays: always return a new reference
- `effect()` must be created in an injection context
- `computed()` is lazy and memoized — Angular only recalculates when needed

**Up next:** Testing Angular applications with Jasmine, Karma, and TestBed.
