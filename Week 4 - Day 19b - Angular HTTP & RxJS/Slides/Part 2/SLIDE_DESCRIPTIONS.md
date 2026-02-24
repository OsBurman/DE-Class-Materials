# Day 19b — Part 2: RxJS Observables, Operators & Memory Management
## Slide Descriptions

---

### Slide 1: Title Slide
**"RxJS — Observables, Operators & Subscriptions"**
Subtitle: Reactive Programming, Operators, Subjects, Async Pipe, Memory Leaks
Week 4 – Day 19b, Part 2

---

### Slide 2: What Is RxJS?

**RxJS (Reactive Extensions for JavaScript):**
- A library for composing asynchronous events using observable sequences
- Angular ships with RxJS — it is not optional, it is built into the framework
- `HttpClient` returns Observables. `Router` events are Observables. `FormControl.valueChanges` is an Observable. Angular's reactivity model is built on RxJS.

**The core concept — everything is a stream:**
```
Mouse clicks:  ─────click─────────click───click──────────→
HTTP response: ──────────────────────────data────complete→
Form input:    ─k─────e─────y─────────────────────────────→
Timer:         ──0ms────100ms────200ms────300ms────────────→
```

**Observable vs Promise — the key differences:**

| | Observable | Promise |
|---|---|---|
| Values emitted | 0, 1, or many | Exactly 1 |
| Lazy? | ✅ Yes — runs only when subscribed | ❌ No — starts immediately |
| Cancellable? | ✅ Yes — unsubscribe | ❌ No |
| Operators? | ✅ Rich operator library | `.then()` / `.catch()` only |
| Synchronous? | Can be | Always async |

**The mental model:**
- A **Promise** is a one-time delivery — like ordering a package. It arrives once.
- An **Observable** is a subscription — like a newspaper. You subscribe, and it keeps sending you papers until you cancel your subscription.

---

### Slide 3: Creating Observables

**You rarely create Observables from scratch — you consume them. But understanding creation helps the mental model:**

```typescript
import { Observable, of, from, interval, fromEvent } from 'rxjs';

// of — emit a fixed list of values synchronously, then complete
const numbers$ = of(1, 2, 3, 4, 5);
numbers$.subscribe(n => console.log(n));  // 1, 2, 3, 4, 5

// from — wrap a Promise or array as an Observable
const promise$ = from(fetch('/api/products').then(r => r.json()));

// interval — emit incrementing numbers at a fixed interval
const timer$ = interval(1000);   // emits 0, 1, 2, 3... every 1 second
// ⚠️ Never completes on its own — must unsubscribe

// fromEvent — DOM events as an Observable
const clicks$ = fromEvent(document, 'click');

// Custom Observable
const custom$ = new Observable<number>(subscriber => {
  subscriber.next(1);
  subscriber.next(2);
  subscriber.complete();    // signals no more values
});
```

**The `$` naming convention:** Variables that hold Observables are conventionally named with a trailing `$` (dollar sign) to visually distinguish them. Not required — just a community convention you'll see everywhere.

---

### Slide 4: Subscribing to Observables

**The full subscribe API:**
```typescript
import { Subscription } from 'rxjs';

// Full observer object
const subscription: Subscription = observable$.subscribe({
  next:     (value) => console.log('Value:', value),      // received a value
  error:    (err)   => console.error('Error:', err),      // error occurred (terminal)
  complete: ()      => console.log('Complete')            // no more values (terminal)
});

// Shorthand — just the next handler
observable$.subscribe(value => console.log(value));

// Unsubscribe to stop receiving values and free resources
subscription.unsubscribe();
```

**Observable lifecycle:**
```
subscribe()
    │
    ├─ next(value1) → next handler called
    ├─ next(value2) → next handler called
    │
    ├─ error(err) → error handler called, Observable TERMINATES
    │   OR
    └─ complete()  → complete handler called, Observable TERMINATES
```

After `error` or `complete`, the Observable stops emitting. No more `next` calls. The subscription is automatically cleaned up on a terminal event.

**HTTP Observables are special:** `HttpClient` observables complete automatically after emitting one value. They're "cold" — a new request fires every time you subscribe.

---

### Slide 5: The pipe() Method and Operator Chains

**Raw Observables give you data. Operators transform it.**

```typescript
import { of } from 'rxjs';
import { map, filter, tap } from 'rxjs/operators';

const numbers$ = of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

numbers$.pipe(
  filter(n => n % 2 === 0),        // keep only even numbers: 2, 4, 6, 8, 10
  map(n => n * 10),                 // transform each: 20, 40, 60, 80, 100
  tap(n => console.log('Debug:', n))// side effect, value unchanged
).subscribe(n => console.log(n));   // 20, 40, 60, 80, 100
```

**`pipe()` chains operators left-to-right.** Each operator receives the output of the previous one. The original Observable is never modified.

```
of(1,2,3,4,5,6,7,8,9,10)
    → filter(even)       → [2, 4, 6, 8, 10]
    → map(x * 10)        → [20, 40, 60, 80, 100]
    → tap(log)           → [20, 40, 60, 80, 100] (side effect only)
    → subscribe(log)     → prints 20, 40, 60, 80, 100
```

---

### Slide 6: map, filter, tap

**`map` — transform each value:**
```typescript
import { map } from 'rxjs/operators';

// Transform API response
this.http.get<ApiProduct[]>('/api/products').pipe(
  map(products => products.map(p => ({
    ...p,
    displayPrice: `$${p.price.toFixed(2)}`,
    isOnSale: p.price < 10
  })))
).subscribe(enrichedProducts => this.products = enrichedProducts);

// Extract a nested property
this.http.get<{ data: Product[], total: number }>('/api/products').pipe(
  map(response => response.data)  // unwrap the data property
).subscribe(products => this.products = products);
```

**`filter` — keep only matching values:**
```typescript
import { filter } from 'rxjs/operators';

// Only process events for a specific route
this.router.events.pipe(
  filter(event => event instanceof NavigationEnd)
).subscribe(event => this.trackPageView(event));

// Filter out null values (common with Subjects)
this.searchTerm$.pipe(
  filter(term => term !== null && term.length >= 2)
).subscribe(term => this.search(term));
```

**`tap` — side effects without modifying the stream:**
```typescript
import { tap } from 'rxjs/operators';

// Logging for debugging — value passes through unchanged
this.productService.getProducts().pipe(
  tap(products => console.log('Received', products.length, 'products')),
  tap(() => this.loading = false)   // ← set loading before subscribing
).subscribe(products => this.products = products);
```

---

### Slide 7: switchMap — Higher-Order Observables

**`switchMap` — the most important operator in Angular apps.**

Problem: You have an Observable of values, and for each value you want to make a new HTTP request. But you only care about the result of the *latest* one.

```
User types:  ──"a"──"an"──"ang"──"angu"──"angul"──→
Each triggers a search request, but only the latest matters.
Old requests should be CANCELLED when a new one starts.
```

```typescript
import { switchMap, debounceTime, distinctUntilChanged } from 'rxjs/operators';

// Search box: cancel the previous search when the user types again
this.searchForm.get('query')!.valueChanges.pipe(
  debounceTime(300),          // wait 300ms after user stops typing
  distinctUntilChanged(),     // only emit if the value actually changed
  switchMap(query =>          // cancel previous, start new request
    this.productService.searchProducts(query)
  )
).subscribe(results => this.searchResults = results);
```

**What `switchMap` does:**
1. Receives a value from the outer Observable (search term)
2. Calls your function to create a new inner Observable (HTTP request)
3. **Cancels** the previous inner Observable if still in progress
4. Emits values from the new inner Observable only

**Use `switchMap` when:** each new outer value should replace/cancel the previous inner operation. Classic cases: typeahead search, route param changes, tab switching.

---

### Slide 8: mergeMap and concatMap

**`mergeMap` — run inner Observables concurrently:**
```typescript
import { mergeMap, from } from 'rxjs';

// Delete multiple products in parallel — all run at the same time
const productIds$ = from([1, 2, 3, 4, 5]);

productIds$.pipe(
  mergeMap(id => this.productService.deleteProduct(id))
).subscribe({
  complete: () => console.log('All deleted')
});
```

**When to use `mergeMap`:** You want concurrent operations and don't care about order. Batch operations, parallel uploads.

**`concatMap` — run inner Observables one at a time, in order:**
```typescript
import { concatMap } from 'rxjs/operators';

// Process items sequentially — wait for each to complete before starting the next
from([item1, item2, item3]).pipe(
  concatMap(item => this.service.processItem(item))
).subscribe();
```

**When to use `concatMap`:** Order matters. Each operation must complete before the next starts.

**Operator selection guide:**
| Situation | Use |
|---|---|
| Latest wins — cancel previous | `switchMap` |
| All concurrent, order doesn't matter | `mergeMap` |
| Sequential — wait for each to finish | `concatMap` |
| Only one at a time, ignore new while busy | `exhaustMap` |

---

### Slide 9: Other Essential Operators

**`debounceTime` — wait for silence before emitting:**
```typescript
import { debounceTime } from 'rxjs/operators';

// Only emit after user stops typing for 300ms
this.searchInput.valueChanges.pipe(
  debounceTime(300)
).subscribe(term => this.search(term));
```

**`distinctUntilChanged` — skip duplicate consecutive values:**
```typescript
import { distinctUntilChanged } from 'rxjs/operators';

// Don't re-search if the term didn't actually change
this.searchInput.valueChanges.pipe(
  debounceTime(300),
  distinctUntilChanged()   // "ng" → "ng" → skip; "ng" → "angular" → emit
).subscribe(term => this.search(term));
```

**`retry` and `retryWhen` — retry failed requests:**
```typescript
import { retry } from 'rxjs/operators';

this.http.get<Product[]>('/api/products').pipe(
  retry(3)   // retry up to 3 times on error before throwing
).subscribe({ /* ... */ });
```

**`catchError` — handle and recover from errors:**
```typescript
import { catchError, of } from 'rxjs';

this.productService.getProducts().pipe(
  catchError(err => {
    this.error = err.message;
    return of([]);   // return empty array to keep the stream alive
  })
).subscribe(products => this.products = products);
```

**`finalize` — always runs on complete or error (like `finally`):**
```typescript
import { finalize } from 'rxjs/operators';

this.productService.getProducts().pipe(
  finalize(() => this.loading = false)
).subscribe(products => this.products = products);
```

---

### Slide 10: Combining Observables — forkJoin and combineLatest

**`forkJoin` — run multiple Observables in parallel, wait for ALL to complete:**
```typescript
import { forkJoin } from 'rxjs';

// Like Promise.all — fires all requests simultaneously, emits when every one completes
// If ANY Observable errors, forkJoin errors immediately
forkJoin({
  products:   this.productService.getProducts(),
  categories: this.categoryService.getCategories(),
  user:       this.userService.getCurrentUser()
}).subscribe({
  next: ({ products, categories, user }) => {
    this.products   = products;
    this.categories = categories;
    this.user       = user;
    this.loading    = false;
  },
  error: err => {
    this.error   = err.message;
    this.loading = false;
  }
});
```

**Key rules for `forkJoin`:**
- All source Observables must **complete** — `forkJoin` only emits after all have finished
- `HttpClient` Observables are perfect for `forkJoin` because they auto-complete after one value
- If an Observable never completes (like a `BehaviorSubject`), `forkJoin` never emits
- Use the object form `forkJoin({ a$, b$ })` for named results (easier than the array form)

---

**`combineLatest` — emit whenever ANY source emits, combining the latest value from each:**
```typescript
import { combineLatest } from 'rxjs';

// In a component with multiple filter controls
combineLatest({
  category: this.categoryFilter$.pipe(startWith('all')),
  minPrice: this.minPriceFilter$.pipe(startWith(0)),
  search:   this.searchTerm$.pipe(startWith(''))
}).pipe(
  debounceTime(200),
  switchMap(({ category, minPrice, search }) =>
    this.productService.getProducts({ category, minPrice, search })
  )
).subscribe(results => this.products = results);
```

**Key rules for `combineLatest`:**
- Emits every time ANY source Observable emits a new value
- Each source must emit at least one value before `combineLatest` emits anything — use `startWith` to provide an initial value if needed
- Never completes unless all source Observables complete
- Does NOT cancel previous inner Observables — for that, combine with `switchMap`

**`forkJoin` vs `combineLatest`:**
| | `forkJoin` | `combineLatest` |
|---|---|---|
| Emits | Once, when ALL complete | Every time ANY source emits |
| Use with | HTTP requests (complete once) | Streams / live data / form controls |
| Like | `Promise.all` | Reactive derived state |
| Requires all to complete | ✅ Yes | ❌ No |

---

### Slide 11: RxJS Subjects

**A Subject is both an Observable AND an Observer.**
- As an Observable: others can subscribe to it
- As an Observer: you can push values into it imperatively
- Useful for cross-component communication and service-to-component signaling

```typescript
import { Subject } from 'rxjs';

const subject$ = new Subject<string>();

// Subscribe — receives future values
subject$.subscribe(val => console.log('A:', val));
subject$.subscribe(val => console.log('B:', val));

subject$.next('hello');    // logs: A: hello, B: hello
subject$.next('world');    // logs: A: world, B: world
subject$.complete();       // closes the subject
```

**A plain `Subject` is "hot" — subscribers only receive values emitted AFTER they subscribe.** Late subscribers miss everything that happened before they subscribed.

---

### Slide 12: BehaviorSubject

**`BehaviorSubject` — always has a current value:**
```typescript
import { BehaviorSubject } from 'rxjs';

// Must be initialized with a default value
const count$ = new BehaviorSubject<number>(0);

// Late subscriber immediately receives the current value
count$.subscribe(val => console.log('A:', val));   // logs: A: 0 immediately
count$.next(1);   // logs: A: 1
count$.next(2);   // logs: A: 2

// Late subscriber joins after values were emitted
count$.subscribe(val => console.log('B:', val));   // logs: B: 2 (current value)
count$.next(3);   // logs: A: 3, B: 3

// Read current value synchronously without subscribing
const currentValue = count$.getValue();   // 3
```

**Real-world use — application state in a service:**
```typescript
@Injectable({ providedIn: 'root' })
export class CartService {
  // Private writable subject — only the service can push values
  private items$ = new BehaviorSubject<CartItem[]>([]);

  // Public read-only observable — components subscribe to this
  readonly cartItems$ = this.items$.asObservable();

  addItem(item: CartItem): void {
    const current = this.items$.getValue();
    this.items$.next([...current, item]);
  }

  clearCart(): void {
    this.items$.next([]);
  }
}
```

**This is the Angular equivalent of a Redux store slice** — BehaviorSubject holds state, components subscribe to changes, the service is the only thing that modifies state.

---

### Slide 13: ReplaySubject and AsyncSubject

**`ReplaySubject` — replay N past values to late subscribers:**
```typescript
import { ReplaySubject } from 'rxjs';

// Replay the last 3 values to any new subscriber
const replay$ = new ReplaySubject<string>(3);

replay$.next('a');
replay$.next('b');
replay$.next('c');
replay$.next('d');   // 'a' is now dropped (buffer size is 3)

// Late subscriber receives 'b', 'c', 'd' immediately on subscribe
replay$.subscribe(val => console.log(val));   // b, c, d
```

**When to use `ReplaySubject`:** You want late subscribers to catch up on recent history — like a notification stream that shows the last 5 notifications to a component that loads after some events already fired.

**`AsyncSubject` — only emits the last value, on complete:**
```typescript
import { AsyncSubject } from 'rxjs';

const async$ = new AsyncSubject<number>();
async$.next(1);
async$.next(2);
async$.next(3);
async$.complete();   // only NOW does it emit: 3

async$.subscribe(val => console.log(val));   // 3
```

**When to use:** Rare. When you only care about the final result — similar to a Promise.

**Subject Quick Reference:**
| Subject type | Behavior | Use when... |
|---|---|---|
| `Subject` | No replay — miss past values | Event bus, user actions |
| `BehaviorSubject` | Replays 1 (current value) | State management, settings |
| `ReplaySubject(n)` | Replays last n values | Notification streams, logs |
| `AsyncSubject` | Only last value on complete | Rare — like a Promise |

---

### Slide 14: Memory Leaks — The Problem

**Angular components are created and destroyed as you navigate. Subscriptions are NOT automatically cleaned up.**

```typescript
// ❌ MEMORY LEAK — subscription lives forever
@Component({ /* ... */ })
export class BadComponent implements OnInit {
  count = 0;

  ngOnInit(): void {
    // This subscription keeps the component alive in memory
    // even after the component is destroyed
    interval(1000).subscribe(() => {
      this.count++;
      console.log(this.count);   // still runs after component is destroyed!
    });
  }
}
```

**What happens:**
1. User navigates to `/dashboard` → `BadComponent` is created, subscription starts
2. User navigates to `/products` → `BadComponent` is destroyed (template removed from DOM)
3. But the subscription is still alive — the callback still fires every second
4. User navigates back to `/dashboard` → a new `BadComponent` is created, a NEW subscription starts
5. Now two subscriptions are running simultaneously
6. Do this 10 times → 10 subscriptions running in the background

This causes memory growth, unexpected behavior, and subtle bugs that are very hard to debug.

---

### Slide 15: Preventing Memory Leaks — takeUntilDestroyed

**Angular 16+ — the modern solution:**
```typescript
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({ /* ... */ })
export class GoodComponent implements OnInit {
  count = 0;

  // DestroyRef is injected automatically
  private destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    // ✅ Automatically unsubscribes when the component is destroyed
    interval(1000).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.count++);
  }
}
```

**Pre-Angular 16 — the Subject + takeUntil pattern (you will see this in most codebases):**
```typescript
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({ /* ... */ })
export class GoodComponentLegacy implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  count = 0;

  ngOnInit(): void {
    interval(1000).pipe(
      takeUntil(this.destroy$)    // complete when destroy$ emits
    ).subscribe(() => this.count++);
  }

  ngOnDestroy(): void {
    this.destroy$.next();         // emit to trigger completion
    this.destroy$.complete();     // close the subject
  }
}
```

**`takeUntil` rule:** Add it to every long-lived subscription in a component. HTTP observables (from `HttpClient`) complete automatically — no cleanup needed. `interval`, `fromEvent`, `BehaviorSubject` — these need takeUntil.

---

### Slide 16: The Async Pipe — The Best Solution

**The async pipe subscribes AND unsubscribes automatically in the template:**

```typescript
@Component({
  template: `
    <!-- ✅ async pipe: subscribes, updates on new values, unsubscribes on destroy -->
    <ul *ngIf="products$ | async as products; else loading">
      <li *ngFor="let product of products">
        {{ product.name }} — {{ product.price | currency }}
      </li>
    </ul>

    <ng-template #loading>
      <p>Loading products...</p>
    </ng-template>
  `
})
export class ProductListComponent implements OnInit {
  products$!: Observable<Product[]>;

  private productService = inject(ProductService);

  ngOnInit(): void {
    // Assign the Observable — don't subscribe!
    this.products$ = this.productService.getProducts();
  }
}
```

**Key benefits of async pipe:**
1. **No manual subscribe/unsubscribe** — template handles it
2. **No memory leaks** — automatically unsubscribes when component destroys
3. **Triggers change detection** — Angular knows when to update the view
4. **Works with OnPush change detection** — important for performance

**`as products`** assigns the unwrapped value to a local template variable. This lets you use `products` multiple times without the pipe re-subscribing.

---

### Slide 17: Combining Async Pipe with Error and Loading States

```typescript
@Component({
  template: `
    <div *ngIf="error$ | async as error" role="alert" class="error">
      {{ error }}
    </div>

    <div *ngIf="loading$ | async" aria-label="Loading" class="spinner"></div>

    <ul *ngIf="products$ | async as products">
      <li *ngFor="let product of products">{{ product.name }}</li>
    </ul>
  `
})
export class ProductListComponent implements OnInit {
  products$!: Observable<Product[]>;
  loading$ = new BehaviorSubject<boolean>(true);
  error$   = new BehaviorSubject<string | null>(null);

  private productService = inject(ProductService);

  ngOnInit(): void {
    this.products$ = this.productService.getProducts().pipe(
      tap(() => this.loading$.next(false)),
      catchError(err => {
        this.error$.next(err.message);
        this.loading$.next(false);
        return of([]);
      })
    );
  }
}
```

**Alternative: use a single state object:**
```typescript
interface LoadState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}
```

---

### Slide 18: Day 19b Summary + Looking Ahead

**Part 1 Recap — HttpClient:**
- `provideHttpClient()` registers HttpClient globally
- Always put HTTP calls in services, not components (SRP)
- `get<T>()`, `post<T>()`, `put<T>()`, `delete<T>()` — all return `Observable<T>`
- `HttpParams` and `HttpHeaders` are immutable — always reassign
- Functional interceptors: `HttpInterceptorFn` — clone requests, never modify
- `catchError` + `throwError` for per-service error handling; error interceptor for global concerns

**Part 2 Recap — RxJS:**
- `Observable` is lazy — nothing happens until `.subscribe()`
- `pipe()` chains operators: `map`, `filter`, `tap`, `debounceTime`, `distinctUntilChanged`
- `switchMap` — cancel previous inner Observable when a new outer value arrives
- `mergeMap` — concurrent inner Observables
- `BehaviorSubject` — holds current state, replays to late subscribers, Angular's lightweight store
- `ReplaySubject(n)` — replays last n values
- `takeUntilDestroyed` (Angular 16+) or `takeUntil(destroy$)` — prevent memory leaks
- **Async pipe** — best practice: subscribe in templates, automatic cleanup

**Coming up — Day 20a: React Advanced & Deployment:**
- `React.memo`, `useMemo`, `useCallback` for performance
- `React.lazy` + `Suspense` for code splitting
- React DevTools profiler
- Production build and deployment

**Day 20b — Angular Signals & Testing:**
- Angular 16+ Signals — a new primitives for reactivity (simpler than Observables for simple state)
- How Signals and Observables coexist
- Testing Angular components with Jasmine and Karma
