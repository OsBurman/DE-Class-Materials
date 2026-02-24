# Day 19b — Angular HTTP & RxJS
## Comprehensive Review Guide

---

## Part 1: HttpClient

---

### 1. Setup

```typescript
// Angular 17+ standalone — app.config.ts
import { provideHttpClient, withInterceptors } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor, loggingInterceptor])
    )
  ]
};
```

```typescript
// Module-based (Angular < 17)
// app.module.ts
import { HttpClientModule } from '@angular/common/http';

@NgModule({ imports: [HttpClientModule] })
export class AppModule {}
```

**Inject into any service:**
```typescript
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/products`;
}
```

---

### 2. All HTTP Methods

```typescript
import { Observable } from 'rxjs';
import { catchError, throwError } from 'rxjs';

export interface Product { id: number; name: string; price: number; }

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/products`;

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  create(data: Partial<Product>): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, data);
  }

  update(id: number, data: Partial<Product>): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, data);
  }

  patch(id: number, changes: Partial<Product>): Observable<Product> {
    return this.http.patch<Product>(`${this.apiUrl}/${id}`, changes);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

---

### 3. HttpParams — Query Strings

**`HttpParams` is immutable — always reassign on modification.**

```typescript
import { HttpParams } from '@angular/common/http';

getProducts(page: number, size: number, search?: string): Observable<Product[]> {
  let params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sort', 'name,asc');

  if (search) {
    params = params.set('search', search);   // ← must reassign
  }

  return this.http.get<Product[]>(this.apiUrl, { params });
}
// GET /api/products?page=0&size=20&sort=name%2Casc&search=widget

// Multiple values for same key
let params = new HttpParams()
  .append('tag', 'electronics')
  .append('tag', 'sale');
// GET /api/products?tag=electronics&tag=sale
```

---

### 4. HttpHeaders

**`HttpHeaders` is also immutable — always reassign on modification.**

```typescript
import { HttpHeaders } from '@angular/common/http';

const headers = new HttpHeaders({
  'Content-Type':  'application/json',
  'Authorization': `Bearer ${this.authService.getToken()}`
});

this.http.post<Product>(this.apiUrl, product, { headers });

// Conditional header building
let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
const token = localStorage.getItem('token');
if (token) {
  headers = headers.set('Authorization', `Bearer ${token}`);  // ← must reassign
}
```

---

### 5. Interceptors

**Functional interceptor (Angular 15+):**

```typescript
// auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();
  if (!token) return next(req);

  // Requests are immutable — must clone to modify
  return next(req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  }));
};
```

```typescript
// logging.interceptor.ts
import { tap } from 'rxjs/operators';

export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  const start = Date.now();
  console.log(`→ ${req.method} ${req.url}`);
  return next(req).pipe(
    tap({
      next:     () => console.log(`← ${req.url} [${Date.now() - start}ms]`),
      error: (err) => console.error(`✗ ${req.url}`, err)
    })
  );
};
```

```typescript
// error.interceptor.ts
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) router.navigate(['/login']);
      if (err.status === 403) router.navigate(['/forbidden']);
      return throwError(() => err);
    })
  );
};
```

**Registration order:** Interceptors run in order on request, in reverse on response.
```typescript
provideHttpClient(withInterceptors([authInterceptor, loggingInterceptor, errorInterceptor]))
// Request:  auth → logging → error → server
// Response: server → error → logging → auth
```

---

### 6. Error Handling with catchError

```typescript
import { catchError, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

private handleError(err: HttpErrorResponse): Observable<never> {
  let message: string;

  if (err.status === 0) {
    message = 'Network error — check your connection';
  } else {
    message = err.error?.message ?? `HTTP ${err.status}: ${err.statusText}`;
  }

  return throwError(() => new Error(message));
}

getProducts(): Observable<Product[]> {
  return this.http.get<Product[]>(this.apiUrl).pipe(
    catchError(err => this.handleError(err))
  );
}
```

**`HttpErrorResponse` fields:**
| Field | Description |
|---|---|
| `status` | HTTP status code (0 = network error) |
| `statusText` | Status message (e.g., "Not Found") |
| `error` | Parsed response body (server error object) |
| `headers` | Response headers |
| `url` | Request URL |

---

### 7. Environment Files

```typescript
// src/environments/environment.ts  ← development
export const environment = { production: false, apiUrl: 'http://localhost:8080/api' };

// src/environments/environment.prod.ts  ← production build
export const environment = { production: true,  apiUrl: 'https://api.myapp.com' };
```

```typescript
// Usage in service
import { environment } from '../environments/environment';
private apiUrl = `${environment.apiUrl}/products`;
```

Angular swaps files at build time via `fileReplacements` in `angular.json`. Same concept as Vite's `.env.development` / `.env.production`.

---

### 8. Three-State Pattern in Components

```typescript
@Component({
  template: `
    <div *ngIf="loading" aria-label="Loading" class="spinner"></div>
    <p  *ngIf="error"   role="alert">{{ error }}</p>
    <ul *ngIf="!loading && !error">
      <li *ngFor="let p of products">{{ p.name }}</li>
    </ul>
  `
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.productService.getProducts().pipe(
      finalize(() => this.loading = false)   // runs on both success and error
    ).subscribe({
      next:  data => this.products = data,
      error: err  => this.error = err.message
    });
  }
}
```

---

## Part 2: RxJS

---

### 9. Observable vs Promise

| | Observable | Promise |
|---|---|---|
| Values | 0, 1, or many | Exactly 1 |
| Lazy? | ✅ Starts on subscribe | ❌ Starts immediately |
| Cancellable? | ✅ `unsubscribe()` | ❌ No |
| Operators | ✅ `pipe()` + many | `.then()` / `.catch()` only |
| Complete event | ✅ Yes | Implicit (settles once) |

---

### 10. Observable Lifecycle

```typescript
import { Observable, Subscription } from 'rxjs';

const subscription: Subscription = observable$.subscribe({
  next:     value => { /* received a value */ },
  error:    err   => { /* error — Observable terminates */ },
  complete: ()    => { /* done — Observable terminates */ }
});

subscription.unsubscribe();   // stop receiving values, free resources
```

**HTTP Observables:** emit one value → auto-complete. No manual unsubscribe needed.
**Infinite Observables** (`interval`, `fromEvent`, `BehaviorSubject`): must unsubscribe manually.

---

### 11. Core Operators

```typescript
import { map, filter, tap, debounceTime, distinctUntilChanged, 
         catchError, finalize, retry } from 'rxjs/operators';
import { of } from 'rxjs';

observable$.pipe(

  // transform each value
  map(product => ({ ...product, displayPrice: `$${product.price}` })),

  // keep only matching values
  filter(product => product.price > 0),

  // side effects — value passes through unchanged
  tap(product => console.log('Processing:', product.name)),

  // wait N ms after last emission before passing it on
  debounceTime(300),

  // skip consecutive identical values
  distinctUntilChanged(),

  // retry N times on error before throwing
  retry(3),

  // handle error — can return fallback Observable
  catchError(err => {
    console.error(err);
    return of([]);   // return empty array, keep stream alive
  }),

  // always runs on complete or error (like finally)
  finalize(() => this.loading = false)

).subscribe({ /* ... */ });
```

---

### 12. Higher-Order Mapping Operators

**When each value triggers a new Observable (e.g., an HTTP request):**

```typescript
import { switchMap, mergeMap, concatMap, exhaustMap } from 'rxjs/operators';

// switchMap — cancel previous inner Observable on new value
// Best for: typeahead search, route param changes
searchInput.valueChanges.pipe(
  debounceTime(300),
  distinctUntilChanged(),
  switchMap(query => this.service.search(query))
).subscribe(results => this.results = results);

// mergeMap — concurrent inner Observables (all run simultaneously)
// Best for: parallel batch operations
from(productIds).pipe(
  mergeMap(id => this.service.delete(id))
).subscribe();

// concatMap — sequential (wait for each to finish)
// Best for: order-sensitive operations
from(items).pipe(
  concatMap(item => this.service.process(item))
).subscribe();

// exhaustMap — ignore new values while current inner is running
// Best for: preventing double-submits
submitButton.clicks.pipe(
  exhaustMap(() => this.service.save(formData))
).subscribe();
```

| Operator | Previous inner | New inner | Use when |
|---|---|---|---|
| `switchMap` | Cancels | Starts | Latest wins (search) |
| `mergeMap` | Keeps running | Starts | Parallel OK |
| `concatMap` | Waits for it | Queued | Order matters |
| `exhaustMap` | Keeps running | Ignored | Prevent double-submit |

---

### 14. Combining Observables — forkJoin and combineLatest

**`forkJoin` — parallel requests, emit once when ALL complete (like `Promise.all`):**
```typescript
import { forkJoin } from 'rxjs';

// Object form — named results (recommended)
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
  error: err => this.error = err.message
});

// Array form
forkJoin([
  this.http.get<Product[]>('/api/products'),
  this.http.get<Category[]>('/api/categories')
]).subscribe(([products, categories]) => {
  this.products   = products;
  this.categories = categories;
});
```

**Rules:**
- All sources must **complete** — `HttpClient` Observables are ideal; `BehaviorSubject` never completes
- If any source errors, `forkJoin` errors immediately
- Emits exactly once — the last value from each source

---

**`combineLatest` — emit whenever ANY source emits, with the latest from all:**
```typescript
import { combineLatest } from 'rxjs';
import { startWith, debounceTime, switchMap } from 'rxjs/operators';

// Reactive filtering — re-query API when any filter changes
combineLatest({
  category: this.categoryFilter$.pipe(startWith('all')),
  minPrice: this.priceFilter$.pipe(startWith(0)),
  search:   this.searchTerm$.pipe(startWith(''))
}).pipe(
  debounceTime(200),
  switchMap(filters => this.productService.getProducts(filters))
).subscribe(results => this.products = results);
```

**Rules:**
- Each source must emit at least once before `combineLatest` emits — use `startWith` for initial values
- Emits every time any source emits a new value
- Never completes unless all sources complete
- Does not cancel in-progress inner Observables — combine with `switchMap` for that

**`forkJoin` vs `combineLatest`:**
| | `forkJoin` | `combineLatest` |
|---|---|---|
| Emits | Once, when ALL complete | On every source emission |
| Requires completion | ✅ Yes | ❌ No |
| Best for | Parallel HTTP requests | Live filter streams, derived state |
| Analogy | `Promise.all` | Spreadsheet formula (recalculates on any input change) |

---

### 14. Subjects

```typescript
import { Subject, BehaviorSubject, ReplaySubject } from 'rxjs';
```

**`Subject` — hot, no replay:**
```typescript
const events$ = new Subject<string>();
events$.subscribe(e => console.log('A:', e));   // subscribe first
events$.next('click');                           // A: click
// Late subscriber misses 'click'
events$.subscribe(e => console.log('B:', e));   // nothing yet
events$.next('scroll');                          // A: scroll, B: scroll
```

**`BehaviorSubject` — always has a current value, replays 1:**
```typescript
const state$ = new BehaviorSubject<number>(0);
state$.next(1);
state$.next(2);

// Late subscriber immediately gets current value (2)
state$.subscribe(n => console.log(n));   // logs 2 immediately
state$.getValue();                        // read synchronously → 2
```

**`BehaviorSubject` as a service state store:**
```typescript
@Injectable({ providedIn: 'root' })
export class CartService {
  private items$ = new BehaviorSubject<CartItem[]>([]);
  readonly cartItems$ = this.items$.asObservable();   // read-only for consumers

  addItem(item: CartItem): void {
    this.items$.next([...this.items$.getValue(), item]);
  }
  clearCart(): void { this.items$.next([]); }
}
```

**`ReplaySubject(n)` — replays last n values:**
```typescript
const log$ = new ReplaySubject<string>(3);
log$.next('a'); log$.next('b'); log$.next('c'); log$.next('d');
// Late subscriber gets: b, c, d (last 3)
log$.subscribe(v => console.log(v));
```

**Subject Reference:**
| Type | Replay | Initial value | Use when |
|---|---|---|---|
| `Subject` | None | ❌ | Event bus, one-time signals |
| `BehaviorSubject` | 1 (current) | ✅ Required | State management |
| `ReplaySubject(n)` | Last n | ❌ | History/notification streams |
| `AsyncSubject` | Last on complete | ❌ | Rare — Promise-like |

---

### 15. Memory Leak Prevention

**The problem:** Subscriptions keep running after component is destroyed.

**✅ Angular 16+ — `takeUntilDestroyed` (preferred):**
```typescript
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DestroyRef, inject } from '@angular/core';

@Component({ /* ... */ })
export class MyComponent implements OnInit {
  private destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    interval(1000).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.tick());

    this.cartService.cartItems$.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(items => this.items = items);
  }
}
```

**✅ Pre-Angular 16 — `takeUntil` pattern (common in existing codebases):**
```typescript
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({ /* ... */ })
export class MyComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    interval(1000).pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => this.tick());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

**What needs cleanup:**
| Observable | Needs cleanup? | Why |
|---|---|---|
| `this.http.get(...)` | ❌ No | Auto-completes after one emission |
| `interval(ms)` | ✅ Yes | Never completes |
| `fromEvent(element, 'click')` | ✅ Yes | Never completes |
| `BehaviorSubject.asObservable()` | ✅ Yes | Never completes unless subject is completed |
| `this.route.params` | ✅ Yes | Never completes |
| `formControl.valueChanges` | ✅ Yes | Never completes |

---

### 16. Async Pipe

**Recommended approach for displaying Observable data in templates.**

```typescript
@Component({
  template: `
    <!-- async pipe subscribes, updates view, and unsubscribes on destroy -->
    <ul *ngIf="products$ | async as products; else loadingTpl">
      <li *ngFor="let p of products">{{ p.name }} — {{ p.price | currency }}</li>
    </ul>
    <ng-template #loadingTpl><p>Loading...</p></ng-template>
  `
})
export class ProductListComponent implements OnInit {
  products$!: Observable<Product[]>;

  ngOnInit(): void {
    this.products$ = this.productService.getProducts();
    // Assign Observable — do NOT subscribe
  }
}
```

**Multiple Observables in one template:**
```typescript
template: `
  <div *ngIf="{
    products: products$ | async,
    user:     user$     | async
  } as vm">
    <p>Hello {{ vm.user?.name }}</p>
    <li *ngFor="let p of vm.products">{{ p.name }}</li>
  </div>
`
```

**Benefits of async pipe vs manual subscribe:**
| | `async` pipe | Manual subscribe |
|---|---|---|
| Memory leaks | ❌ Impossible — auto unsubscribes | ⚠️ Must manage manually |
| Code | Minimal | Boilerplate (`ngOnInit`, `ngOnDestroy`) |
| OnPush compat | ✅ Triggers change detection | ⚠️ Requires `markForCheck()` |
| Use data in code | ❌ Template-only | ✅ Full access |

---

### 17. Full Working Example — Search with Async Pipe

```typescript
@Component({
  template: `
    <input [formControl]="searchCtrl" placeholder="Search products...">

    <div *ngIf="error$ | async as error" role="alert">{{ error }}</div>
    <div *ngIf="loading$ | async" aria-label="Searching" class="spinner"></div>

    <ul *ngIf="results$ | async as results">
      <li *ngFor="let p of results">{{ p.name }}</li>
      <li *ngIf="results.length === 0">No results found</li>
    </ul>
  `
})
export class ProductSearchComponent {
  searchCtrl = new FormControl('');
  loading$ = new BehaviorSubject<boolean>(false);
  error$   = new BehaviorSubject<string | null>(null);

  results$: Observable<Product[]> = this.searchCtrl.valueChanges.pipe(
    debounceTime(300),
    distinctUntilChanged(),
    tap(() => { this.loading$.next(true); this.error$.next(null); }),
    switchMap(query =>
      this.productService.searchProducts(query ?? '').pipe(
        catchError(err => {
          this.error$.next(err.message);
          return of([]);
        })
      )
    ),
    tap(() => this.loading$.next(false))
  );

  private productService = inject(ProductService);
}
```

---

### 18. Common Mistakes & Fixes

| Mistake | Fix |
|---|---|
| Calling service method without subscribing | Must `.subscribe()` or use `async` pipe |
| Mutating `HttpParams` without reassigning | `params = params.set(...)` — always reassign |
| Mutating `HttpHeaders` without reassigning | `headers = headers.set(...)` — always reassign |
| Modifying `HttpRequest` in interceptor | `req.clone({ ... })` — requests are immutable |
| Not unsubscribing from long-lived Observables | Add `takeUntilDestroyed` or `takeUntil(destroy$)` |
| Subscribing to HTTP Observable in constructor | Use `ngOnInit` — template may not be ready |
| Multiple `async` pipes to same Observable | Use `as` alias: `*ngIf="obs$ | async as data"` |
| Using plain `Subject` when initial value is needed | Use `BehaviorSubject` instead |
| Not handling `error.status === 0` (network error) | Check for `0` before checking other status codes |
| Subscribing in `ngOnDestroy` | Too late — component is being destroyed |

---

### 19. Quick Comparison — Angular HttpClient vs React Fetch/Axios

| Feature | Angular HttpClient | React fetch | React axios |
|---|---|---|---|
| Return type | `Observable<T>` | `Promise<Response>` | `Promise<AxiosResponse>` |
| JSON auto-parse | ✅ | ❌ (manual `.json()`) | ✅ (`response.data`) |
| Throws on 4xx/5xx | ✅ | ❌ (check `.ok`) | ✅ |
| Interceptors | ✅ `withInterceptors` | ❌ | ✅ |
| Typed responses | ✅ `get<T>()` | ❌ | ✅ |
| Cancellation | `takeUntil` / `unsubscribe` | `AbortController` | `CancelToken` |
| Retries | `retry()` operator | Manual | Manual |
| Environment config | `environment.ts` | `.env` + `VITE_` prefix | `.env` + `VITE_` prefix |

---

### 20. Looking Ahead

**Day 20b — Angular Signals & Testing:**
- **Signals** — Angular 16+: `signal()`, `computed()`, `effect()` — simpler reactivity for component state
- Signals vs Observables: when to use each
- **Jasmine & Karma**: Angular's default testing framework
- `TestBed` — Angular's test module for DI and component setup
- Testing HttpClient with `HttpClientTestingModule` and `HttpTestingController`
- Testing components with `ComponentFixture` and `DebugElement`

**Week 6 — Day 28 (Testing deep dive):**
- JUnit 5 and Mockito for backend services
- `@WebMvcTest` for Spring MVC controller tests
- Same testing philosophies — mock dependencies, test one thing at a time — applied to Java
