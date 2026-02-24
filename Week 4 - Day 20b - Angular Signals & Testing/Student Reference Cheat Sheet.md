# Day 20b — Angular Signals & Testing: Complete Reference

**Topics:** Signals, WritableSignal, computed, effect, signal inputs, model(), toSignal, toObservable, Jasmine, Karma, TestBed, ComponentFixture, HttpClientTestingModule, HttpTestingController, Spies, Mocking, Code Coverage

---

## Table of Contents
1. [signal() — Creating and Reading](#1-signal--creating-and-reading)
2. [Writing to Signals — .set() and .update()](#2-writing-to-signals--set-and-update)
3. [computed() — Derived Values](#3-computed--derived-values)
4. [effect() — Reactive Side Effects](#4-effect--reactive-side-effects)
5. [Signals in Components](#5-signals-in-components)
6. [Signals in Services](#6-signals-in-services)
7. [toSignal() and toObservable() — Interop](#7-tosignal-and-toobservable--interop)
8. [Signal Inputs — input() and model()](#8-signal-inputs--input-and-model)
9. [Signals vs Observables — Decision Guide](#9-signals-vs-observables--decision-guide)
10. [Jasmine Fundamentals](#10-jasmine-fundamentals)
11. [TestBed and ComponentFixture](#11-testbed-and-componentfixture)
12. [Testing Components](#12-testing-components)
13. [Testing Services](#13-testing-services)
14. [Testing HTTP Calls](#14-testing-http-calls)
15. [Mocking Dependencies](#15-mocking-dependencies)
16. [Jasmine Spies](#16-jasmine-spies)
17. [Testing Signal-Based Components](#17-testing-signal-based-components)
18. [Running Tests and Code Coverage](#18-running-tests-and-code-coverage)
19. [Common Mistakes & Fixes](#19-common-mistakes--fixes)
20. [Quick Reference Syntax](#20-quick-reference-syntax)
21. [Looking Ahead](#21-looking-ahead)

---

## Learning Objectives Checklist

- [ ] Create and read a signal with `signal()`
- [ ] Update signal state with `.set()` and `.update()`
- [ ] Derive values automatically with `computed()`
- [ ] Register reactive side effects with `effect()` (including cleanup)
- [ ] Use signals in a component template and service
- [ ] Convert an Observable to a Signal with `toSignal()`
- [ ] Convert a Signal to an Observable with `toObservable()`
- [ ] Use `input()` and `model()` for signal-based component inputs
- [ ] Explain when to use Signals vs Observables
- [ ] Write a Jasmine test with `describe`, `it`, `expect`, and matchers
- [ ] Configure a TestBed with `imports`, `declarations`, and `providers`
- [ ] Create a `ComponentFixture` and interact with it
- [ ] Trigger and assert DOM interactions in tests
- [ ] Test a service both directly and through TestBed
- [ ] Test HTTP calls with `HttpClientTestingModule`
- [ ] Mock dependencies with `useValue`, `useClass`, and `createSpyObj`
- [ ] Use `spyOn()` and assert spy calls
- [ ] Test signal state directly in component tests
- [ ] Run tests with `ng test` and generate a coverage report

---

## 1. signal() — Creating and Reading

```typescript
import { signal } from '@angular/core';

// Creation — TypeScript infers the type
const count   = signal(0);               // WritableSignal<number>
const name    = signal('Alice');         // WritableSignal<string>
const isOpen  = signal(false);           // WritableSignal<boolean>
const items   = signal<string[]>([]);    // explicit generic — required for arrays/objects
const user    = signal<User | null>(null);

// Reading — call it like a function
console.log(count());    // 0
console.log(name());     // 'Alice'

// In templates — same () syntax
```
```html
<p>{{ count() }}</p>
<p>{{ user()?.displayName ?? 'Guest' }}</p>
```

**What calling () does:** Returns the current value AND registers the caller as a reactive dependent. Inside `computed()` or `effect()`, this automatic registration means Angular knows to re-run that computation when the signal changes.

---

## 2. Writing to Signals — .set() and .update()

```typescript
const count = signal(0);
const items = signal<string[]>([]);

// .set() — replace the value entirely
count.set(10);
count.set(0);

// .update() — derive new value from current
count.update(c => c + 1);
count.update(c => c * 2);

// .update() with arrays — ALWAYS return a new reference
items.update(list => [...list, 'Angular']);               // add item
items.update(list => list.filter(i => i !== 'Vue'));      // remove item
items.update(list => list.map(i => i.toUpperCase()));     // transform

// .asReadonly() — expose read-only surface from a service
private _count = signal(0);
readonly count = this._count.asReadonly();  // Signal<number>, no .set()
```

**⚠️ Array/object mutation trap:**
```typescript
// ❌ Same reference — signal sees no change, nothing updates
items.update(list => { list.push('item'); return list; });

// ✅ New reference — signal detects change, all dependents update
items.update(list => [...list, 'item']);
```

---

## 3. computed() — Derived Values

```typescript
import { signal, computed } from '@angular/core';

const items    = signal<CartItem[]>([]);
const discount = signal(0.10);

// Automatically recomputes when dependencies change
const subtotal = computed(() =>
  items().reduce((sum, item) => sum + item.price * item.qty, 0)
);
const total = computed(() => subtotal() * (1 - discount()));
const count = computed(() => items().length);
const isEmpty = computed(() => items().length === 0);

// Reading — same () syntax
console.log(total());   // 0
items.update(list => [...list, { price: 100, qty: 2 }]);
console.log(total());   // 180  (after 10% discount)
discount.set(0.20);
console.log(total());   // 160  (recomputed because discount changed)
```

**Properties of `computed()`:**

| Property | Detail |
|---|---|
| Lazy | Not evaluated until first read |
| Memoized | Cached; only reruns when a tracked dependency changes |
| Read-only | Returns `Signal<T>`, not `WritableSignal<T>` |
| Synchronous only | No HTTP calls, no Promises inside |

**When to use:**
- Filtered / sorted arrays
- Numeric aggregates (sum, average, count)
- Formatted display strings
- Boolean flags (`isEmpty`, `isValid`, `hasError`)
- Any value derivable from other signals

---

## 4. effect() — Reactive Side Effects

```typescript
import { Component, effect, signal } from '@angular/core';

@Component({ ... })
export class CartComponent {
  items = signal<CartItem[]>([]);

  constructor() {
    // Runs immediately; re-runs whenever items() changes
    effect(() => {
      console.log(`Cart has ${this.items().length} items`);
      localStorage.setItem('cart', JSON.stringify(this.items()));
    });

    // With cleanup — runs before next execution and on destroy
    effect((onCleanup) => {
      const timer = setInterval(() => this.syncWithServer(), 30_000);
      onCleanup(() => clearInterval(timer));
    });
  }
}
```

**Rules:**

| Rule | Detail |
|---|---|
| Must be in injection context | Constructor, field initializer, or `runInInjectionContext()` |
| Runs once immediately | On creation, then again when dependencies change |
| Cleanup before re-run | `onCleanup` fires before next execution |
| Never set signals inside | Creates infinite loop — use `computed()` instead |

**Use `effect()` for:** localStorage sync, analytics events, DOM focus management, bridging to 3rd-party libraries.

**Don't use `effect()` for:** deriving values (→ `computed()`), data fetching (→ service + `toSignal()`).

---

## 5. Signals in Components

```typescript
import { Component, signal, computed } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CurrencyPipe],
  template: `
    <p>Items:  {{ itemCount() }}</p>
    <p>Total:  {{ total() | currency }}</p>
    <button (click)="addItem(10)">Add $10</button>
    <button (click)="clear()" [disabled]="isEmpty()">Clear</button>
  `
})
export class CartComponent {
  private prices = signal<number[]>([]);

  itemCount = computed(() => this.prices().length);
  total     = computed(() => this.prices().reduce((s, p) => s + p, 0));
  isEmpty   = computed(() => this.prices().length === 0);

  addItem(price: number) { this.prices.update(list => [...list, price]); }
  clear()               { this.prices.set([]); }
}
```

**Notes:**
- No `async` pipe required — signals are synchronous
- Works automatically with `ChangeDetectionStrategy.OnPush`
- `[disabled]="isEmpty()"` — binding reads the signal, Angular tracks it

---

## 6. Signals in Services

```typescript
import { Injectable, signal, computed } from '@angular/core';

export interface CartItem { id: string; name: string; price: number; qty: number; }

@Injectable({ providedIn: 'root' })
export class CartService {
  // Private — only this service writes
  private items = signal<CartItem[]>([]);

  // Public read-only surface
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

  clear(): void { this.items.set([]); }
}
```
```html
<!-- Any component that injects CartService -->
<span>{{ cartService.count() }} items — {{ cartService.total() | currency }}</span>
```

**Pattern:** Private writable signal + public read-only computed surface. Components inject and read; only the service can write.

---

## 7. toSignal() and toObservable() — Interop

```typescript
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { inject } from '@angular/core';

// toSignal — Observable → Signal
// Must be called in an injection context (constructor or field initializer)
@Component({ ... })
export class ProductsComponent {
  private svc = inject(ProductService);

  // HTTP observable consumed as a signal — no async pipe needed
  products = toSignal(
    this.svc.getProducts(),
    { initialValue: [] as Product[] }   // value before first emission
  );
}
// Template: {{ products() }}  — no async pipe

// toObservable — Signal → Observable
// Lets you apply RxJS operators to signal changes
searchTerm = signal('');

results$ = toObservable(this.searchTerm).pipe(
  debounceTime(300),
  distinctUntilChanged(),
  switchMap(term => this.productService.search(term))
);
```

**When to use each:**

| | `toSignal(obs$)` | `toObservable(sig)` |
|---|---|---|
| Use when | You want to read an HTTP/event Observable in a template or `computed()` | You want to apply RxJS operators (debounce, switchMap) to signal state changes |
| Requires | Injection context | Injection context |
| `initialValue` | Needed if Observable might not emit synchronously (HTTP) | N/A |

---

## 8. Signal Inputs — input() and model()

```typescript
import { Component, input, model, computed } from '@angular/core';

// input() — replaces @Input() decorator
@Component({ selector: 'app-user-card', ... })
export class UserCardComponent {
  // Optional with default
  title = input('Untitled');             // InputSignal<string>

  // Required — compile error if parent doesn't provide it
  user  = input.required<User>();        // InputSignal<User>

  // Use in computed — auto-updates when parent changes input
  greeting = computed(() => `Hello, ${this.user().name}!`);
}
```
```html
<!-- Parent -->
<app-user-card [title]="'Profile'" [user]="currentUser" />
<!-- Read in template of UserCardComponent -->
<p>{{ greeting() }}</p>
```

```typescript
// model() — two-way binding signal; replaces @Input() + @Output() xxxChange
@Component({ selector: 'app-rating', ... })
export class RatingComponent {
  value = model(0);   // ModelSignal<number>

  increase() { this.value.update(v => Math.min(v + 1, 5)); }
  decrease() { this.value.update(v => Math.max(v - 1, 0)); }
}
```
```html
<!-- Parent — [(value)] two-way binding -->
<app-rating [(value)]="myRating" />
```

---

## 9. Signals vs Observables — Decision Guide

| | Signals | Observables |
|---|---|---|
| Value read | `count()` — synchronous | `.subscribe()` or `async` pipe |
| Always has value | ✅ | Only with `BehaviorSubject` / `startWith` |
| Derived values | `computed()` | `pipe(map(...))` |
| Side effects | `effect()` | `subscribe()` / `tap()` |
| Lazy | `computed()` is lazy | Observable is lazy |
| Cancellation | ❌ N/A | ✅ Unsubscribe |
| HTTP / Async | ❌ Not directly | ✅ Native |
| Operators | ❌ None | ✅ Full RxJS library |
| Error channel | ❌ None | ✅ `catchError()` |
| Best for | Component/service state | HTTP, events, complex async |

**Rule of thumb:** Signals for **state**. Observables for **events and async data flows**. Use `toSignal()` to bridge HTTP responses into signal state for templates.

---

## 10. Jasmine Fundamentals

```typescript
// Test structure
describe('suite name', () => {      // groups related tests
  beforeAll(() => { ... });         // once before all tests
  beforeEach(() => { ... });        // before each test — use for fresh state
  afterEach(() => { ... });         // after each test — use for cleanup
  afterAll(() => { ... });          // once after all tests

  it('test description', () => {    // individual test
    expect(actual).toBe(expected);  // assertion
  });

  it('nested describe is valid', () => { ... });
});
```

**Common matchers:**

```typescript
expect(2 + 2).toBe(4);                        // strict equality ===
expect({ a: 1 }).toEqual({ a: 1 });           // deep equality
expect(null).toBeNull();
expect(undefined).toBeUndefined();
expect('').toBeFalsy();
expect('hello').toBeTruthy();
expect([1, 2, 3]).toContain(2);
expect('hello world').toContain('world');
expect(5).toBeGreaterThan(3);
expect(5).toBeLessThan(10);
expect(() => risky()).toThrow();
expect(() => risky()).toThrowError('message');
// Negate any matcher with .not
expect(value).not.toBeNull();
expect(spy).not.toHaveBeenCalled();
```

---

## 11. TestBed and ComponentFixture

```typescript
import { TestBed, ComponentFixture } from '@angular/core/testing';

describe('MyComponent', () => {
  let component: MyComponent;
  let fixture:   ComponentFixture<MyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:   [MyComponent],                // standalone component
      // declarations: [MyComponent],          // module-based component
      providers: [
        { provide: MyService, useValue: mockMyService }
      ]
    }).compileComponents();

    fixture   = TestBed.createComponent(MyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();   // triggers ngOnInit + first render
  });
});
```

**`ComponentFixture<T>` API:**

| Member | Type | Purpose |
|---|---|---|
| `.componentInstance` | `T` | TypeScript class instance — access properties/methods |
| `.nativeElement` | `HTMLElement` | Root DOM element — standard DOM queries |
| `.debugElement` | `DebugElement` | Angular wrapper — query by directive, CSS |
| `.detectChanges()` | `void` | Manually trigger change detection |
| `.whenStable()` | `Promise<void>` | Resolve when async work completes |
| `.destroy()` | `void` | Trigger `ngOnDestroy` |

**Getting services from TestBed:**
```typescript
const service = TestBed.inject(MyService);  // same DI instance as the component uses
```

---

## 12. Testing Components

```typescript
// The component
@Component({
  standalone: true,
  template: `
    <h1 data-testid="title">{{ title }}</h1>
    <button data-testid="btn" (click)="onClick()">Click</button>
    <p data-testid="result">{{ result }}</p>
  `
})
export class DemoComponent {
  title  = 'Demo';
  result = '';
  onClick() { this.result = 'clicked'; }
}

// The tests
it('displays the title', () => {
  const h1 = fixture.nativeElement.querySelector('[data-testid="title"]');
  expect(h1.textContent).toContain('Demo');
});

it('shows result after click', () => {
  fixture.nativeElement.querySelector('[data-testid="btn"]').click();
  fixture.detectChanges();   // re-render after state change
  expect(fixture.nativeElement.querySelector('[data-testid="result"]').textContent)
    .toContain('clicked');
});

// Simulating text input
it('updates on input', () => {
  const input = fixture.nativeElement.querySelector('input');
  input.value = 'test value';
  input.dispatchEvent(new Event('input'));
  fixture.detectChanges();
  expect(component.someField).toBe('test value');
});
```

**DOM query options:**
```typescript
// nativeElement — standard DOM
fixture.nativeElement.querySelector('h1');
fixture.nativeElement.querySelectorAll('.item');

// debugElement — Angular-aware
import { By } from '@angular/platform-browser';
fixture.debugElement.query(By.css('.card'));
fixture.debugElement.queryAll(By.directive(RouterLink));
```

---

## 13. Testing Services

```typescript
// Option A — direct instantiation (no TestBed, for simple services)
describe('CalculatorService', () => {
  let service: CalculatorService;
  beforeEach(() => { service = new CalculatorService(); });

  it('adds numbers', () => expect(service.add(2, 3)).toBe(5));
});

// Option B — TestBed injection (service has DI dependencies)
describe('CartService', () => {
  let service: CartService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CartService,
        { provide: ProductService, useValue: mockProductService }
      ]
    });
    service = TestBed.inject(CartService);
  });

  it('starts empty', () => {
    expect(service.count()).toBe(0);
    expect(service.isEmpty()).toBeTrue();
  });

  it('adds an item', () => {
    service.addItem({ id: '1', name: 'Book', price: 20, qty: 2 });
    expect(service.count()).toBe(1);
    expect(service.total()).toBe(40);
  });

  it('removes an item', () => {
    service.addItem({ id: '1', name: 'Book', price: 20, qty: 1 });
    service.removeItem('1');
    expect(service.count()).toBe(0);
  });
});
```

---

## 14. Testing HTTP Calls

```typescript
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

describe('ProductService', () => {
  let service:  ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:   [HttpClientTestingModule],
      providers: [ProductService]
    });
    service  = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());   // assert no unexpected requests

  it('GET /api/products returns product list', () => {
    const mock = [{ id: 1, name: 'Widget', price: 9.99 }];

    service.getProducts().subscribe(products => {
      expect(products).toEqual(mock);
    });

    const req = httpMock.expectOne('/api/products');
    expect(req.request.method).toBe('GET');
    req.flush(mock);                     // deliver response → triggers subscribe
  });

  it('POST /api/products creates a product', () => {
    const newProduct = { name: 'Gadget', price: 19.99 };

    service.createProduct(newProduct).subscribe();

    const req = httpMock.expectOne('/api/products');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newProduct);
    req.flush({ id: 2, ...newProduct });
  });

  it('handles server errors', () => {
    service.getProducts().subscribe({
      next:  () => fail('should have errored'),
      error: (err) => expect(err.status).toBe(500)
    });
    httpMock.expectOne('/api/products').flush('Server error', {
      status: 500, statusText: 'Internal Server Error'
    });
  });
});
```

**`httpMock` API:**

| Method | Purpose |
|---|---|
| `.expectOne(url)` | Assert exactly one request to this URL; returns `TestRequest` |
| `.expectOne({ url, method })` | Assert one request matching URL and method |
| `.expectNone(url)` | Assert no request was made to this URL |
| `.match(url)` | Return all requests matching URL (for multiple requests) |
| `.verify()` | Assert all expected requests were made, no unexpected ones remain |

**`TestRequest` API:**
| Method | Purpose |
|---|---|
| `req.flush(body)` | Respond with a successful body |
| `req.flush(body, { status, statusText })` | Respond with a custom HTTP status |
| `req.error(new ErrorEvent('...'))` | Respond with a network error |

---

## 15. Mocking Dependencies

```typescript
// Approach 1: useValue — inline mock object
providers: [
  {
    provide: AuthService,
    useValue: {
      isLoggedIn: () => true,
      currentUser: signal({ id: '1', name: 'Alice' })
    }
  }
]

// Approach 2: jasmine.createSpyObj — spy object (most common for assertions)
const mockAuth = jasmine.createSpyObj<AuthService>('AuthService', [
  'isLoggedIn', 'login', 'logout'
]);
mockAuth.isLoggedIn.and.returnValue(true);
mockAuth.login.and.returnValue(of({ token: 'test-token' }));

providers: [{ provide: AuthService, useValue: mockAuth }]

// Approach 3: useClass — fake implementation class
class MockAuthService {
  isLoggedIn() { return true; }
  login()      { return of({ token: 'test-token' }); }
  logout()     {}
}
providers: [{ provide: AuthService, useClass: MockAuthService }]
```

**When to use each:**

| Approach | Use when |
|---|---|
| `useValue` inline | Few methods needed; don't need to assert calls |
| `jasmine.createSpyObj` | Need to assert the component called the right method with right args |
| `useClass` | Mock is complex; shared across many test files |

---

## 16. Jasmine Spies

```typescript
// spyOn — wrap an existing method
const service = TestBed.inject(ProductService);
const spy = spyOn(service, 'getProducts').and.returnValue(of([mockProduct]));

// After action:
expect(spy).toHaveBeenCalled();
expect(spy).toHaveBeenCalledTimes(1);
expect(spy).toHaveBeenCalledWith({ category: 'electronics' });
expect(spy).not.toHaveBeenCalled();

// jasmine.createSpyObj — create from scratch
const mockService = jasmine.createSpyObj<UserService>('UserService', [
  'getUser', 'saveUser', 'deleteUser'
]);
// Each method is already a spy
mockService.getUser.and.returnValue(of(mockUser));
mockService.saveUser.and.returnValue(of(void 0));
```

**Spy return behaviors:**

```typescript
spy.and.returnValue(value)           // always return this value
spy.and.returnValues(a, b, c)        // a on 1st call, b on 2nd, c on 3rd
spy.and.callThrough()                // execute the real implementation
spy.and.callFake((arg) => { ... })   // custom function
spy.and.throwError('message')        // throw an error
spy.and.rejectWith(new Error('...')) // return rejected Promise
```

**Spy properties:**
```typescript
spy.calls.count()          // number of times called
spy.calls.mostRecent()     // most recent call object
spy.calls.all()            // array of all call objects
spy.calls.reset()          // reset call history
```

---

## 17. Testing Signal-Based Components

```typescript
// Signals can be read and written directly in tests
it('reads signal state', () => {
  component.count.set(10);
  fixture.detectChanges();
  const p = fixture.nativeElement.querySelector('[data-testid="count"]');
  expect(p.textContent.trim()).toBe('10');
  expect(component.count()).toBe(10);
});

// Testing computed signals
it('computed updates when signal changes', () => {
  const doubled = computed(() => component.count() * 2);
  component.count.set(5);
  expect(doubled()).toBe(10);  // synchronous — no async needed
});

// Testing a service with signals (synchronous assertions)
it('total updates when item added', () => {
  const service = TestBed.inject(CartService);
  service.addItem({ id: '1', name: 'Book', price: 25, qty: 2 });
  expect(service.total()).toBe(50);   // direct signal read
});

// Testing an effect's side-effect (localStorage)
it('syncs to localStorage when items change', () => {
  spyOn(localStorage, 'setItem');
  component.addItem();
  fixture.detectChanges();
  expect(localStorage.setItem).toHaveBeenCalledWith('cart', jasmine.any(String));
});
```

**Why signals are test-friendly:**
- Always synchronous — no `async`/`await` needed to read state
- Direct access — `component.count.set(value)` to set up state without UI clicks
- Direct read — `component.count()` to assert state without DOM queries
- `computed()` evaluates eagerly when called — test it like a function

---

## 18. Running Tests and Code Coverage

```bash
ng test                              # watch mode — re-runs on file save
ng test --watch=false                # run once (CI)
ng test --watch=false --browsers=ChromeHeadless   # CI with headless browser
ng test --code-coverage              # generate coverage/index.html
ng test --include="src/app/auth/**"  # run specific files
```

**Coverage columns:**

| Column | Measures |
|---|---|
| Statements | Every executable statement |
| Branches | Every if/else / ternary branch |
| Functions | Every function/method |
| Lines | Every line of code |

**Setting coverage thresholds in `karma.conf.js`:**
```javascript
coverageReporter: {
  thresholds: {
    global: {
      statements: 80,
      branches:   70,
      functions:  80,
      lines:      80
    }
  }
}
// Tests fail in CI if coverage drops below these numbers
```

**Coverage report location:** `coverage/project-name/index.html`

---

## 19. Common Mistakes & Fixes

### Signals

| Mistake | Fix |
|---|---|
| Setting a signal inside `effect()` | Use `computed()` instead |
| Mutating arrays in `.update()` in place | Return a new array: `list => [...list, item]` |
| Calling `effect()` in a regular method | Move to constructor or use `runInInjectionContext()` |
| Using `computed()` for async logic | Use a service method + `toSignal()` |
| Forgetting `initialValue` in `toSignal()` for HTTP | Signal is `undefined` before first emission — template breaks |

### Testing

| Mistake | Fix |
|---|---|
| DOM assertions return stale values | Call `fixture.detectChanges()` after every state change |
| Forgetting `httpMock.verify()` | Always add `afterEach(() => httpMock.verify())` |
| `async` missing on `beforeEach` with `compileComponents` | Add `async` and `await` to `beforeEach` |
| Testing private methods directly | Test the public behavior that private methods produce |
| `React.memo` with no deps — wait, wrong framework | Use `ChangeDetectionStrategy.OnPush` with signals for Angular |
| Shared state between tests | Always use `beforeEach` to create fresh instances |
| `useValue: {}` and then testing method calls | Use `jasmine.createSpyObj` when you need to assert calls |
| `expect(spy).toHaveBeenCalled()` before action | Call `fixture.detectChanges()` first to trigger `ngOnInit` |
| `querySelectorAll` returns NodeList, not array | `Array.from(fixture.nativeElement.querySelectorAll(...))` |

---

## 20. Quick Reference Syntax

```typescript
// ── Signals ────────────────────────────────────────────────────────────────
const count  = signal(0);
const items  = signal<string[]>([]);
count.set(5);
count.update(c => c + 1);
items.update(list => [...list, 'new']);
const val = count();                          // read
const ro  = count.asReadonly();               // read-only view

// ── computed ───────────────────────────────────────────────────────────────
const doubled = computed(() => count() * 2);  // read: doubled()
const total   = computed(() => items().reduce(...));

// ── effect ─────────────────────────────────────────────────────────────────
constructor() {
  effect(() => { localStorage.setItem('k', JSON.stringify(items())); });
  effect((onCleanup) => {
    const t = setInterval(fn, 1000);
    onCleanup(() => clearInterval(t));
  });
}

// ── input / model ──────────────────────────────────────────────────────────
title = input('default');            // InputSignal<string>
user  = input.required<User>();      // required
value = model(0);                    // ModelSignal<number> — two-way

// ── interop ────────────────────────────────────────────────────────────────
products   = toSignal(this.http.get<Product[]>('/api'), { initialValue: [] });
results$   = toObservable(this.searchTerm).pipe(debounceTime(300), switchMap(...));

// ── TestBed ────────────────────────────────────────────────────────────────
await TestBed.configureTestingModule({ imports: [MyComponent] }).compileComponents();
fixture   = TestBed.createComponent(MyComponent);
component = fixture.componentInstance;
fixture.detectChanges();
const el = fixture.nativeElement.querySelector('[data-testid="x"]');
const svc = TestBed.inject(MyService);

// ── Mocking ────────────────────────────────────────────────────────────────
{ provide: MyService, useValue: { method: () => returnValue } }
const mock = jasmine.createSpyObj<MyService>('MyService', ['method']);
mock.method.and.returnValue(of(data));
spyOn(service, 'method').and.returnValue(of(data));
expect(mock.method).toHaveBeenCalledWith(args);

// ── HTTP Testing ───────────────────────────────────────────────────────────
imports: [HttpClientTestingModule]
httpMock = TestBed.inject(HttpTestingController);
afterEach(() => httpMock.verify());
service.getData().subscribe(res => expect(res).toEqual(mock));
const req = httpMock.expectOne('/api/data');
req.flush(mock);
req.flush('error', { status: 500, statusText: 'Error' });
```

---

## 21. Looking Ahead

| Day 20b (Today — Angular Track Complete) | Coming Up |
|---|---|
| Signals for reactive state | Spring Boot reactive — WebFlux (awareness) |
| `computed()` derived values | Derived fields in JPA / computed columns |
| `effect()` side effects | AOP for cross-cutting concerns — Day 26 |
| `TestBed`, `ComponentFixture` | Spring `@SpringBootTest`, MockMvc — Day 28 |
| `HttpClientTestingModule` | WireMock for HTTP mocking — Day 28 |
| Jasmine spies and mocking | Mockito mocks and spies — Day 28 |
| `jasmine.createSpyObj` | `@Mock`, `@InjectMocks` in Mockito |
| Code coverage with Karma | JaCoCo coverage for Java — Day 28 |

**Week 5 Preview:**
- Day 21 — SQL fundamentals: SELECT, WHERE, JOIN, subqueries
- Day 22 — SQL design: DDL, normalization, transactions
- Day 23 — REST and API tools: HTTP, Postman, Swagger/OpenAPI
- Day 24 — Maven, Gradle, Spring Core, DI
- Day 25 — Spring Boot: auto-configuration, profiles, Actuator

---

*Day 20b Complete — Angular Signals & Testing*
