# Day 19b — Part 1: Angular HttpClient & HTTP Requests
## Slide Descriptions

---

### Slide 1: Title Slide
**"Angular HTTP & HttpClient"**
Subtitle: Making Real API Calls in Angular
Week 4 – Day 19b, Part 1

---

### Slide 2: Day 18b Recap + Today's Agenda

**Day 18b recap:**
- Angular Router — `RouterModule`, `<router-outlet>`, `routerLink`, route guards (`CanActivate`)
- Template-driven forms — `FormsModule`, `[(ngModel)]`, `#ref` variables
- Reactive forms — `FormBuilder`, `FormGroup`, `FormControl`, validators, `statusChanges`/`valueChanges`

**The missing piece:**
- Forms collect data — but where does it go?
- Routes display pages — but what fills them with real data?
- Answer: **HttpClient** — Angular's built-in HTTP client

**Today's agenda — Part 1:**
1. Providing and injecting `HttpClient`
2. GET, POST, PUT, DELETE requests
3. `HttpParams` for query strings
4. `HttpHeaders` for auth tokens and content type
5. Interceptors — modify every request or response in one place
6. Error handling with `catchError`

**Connection to yesterday (Day 19a):**
- React used `fetch`/`axios` + Promises
- Angular uses `HttpClient` + RxJS Observables (Part 2 goes deep on Observables)
- Same mental model: request → wait → success or error

---

### Slide 3: HttpClient Setup

**Import into your application:**
```typescript
// app.config.ts (Angular 17+ standalone)
import { provideHttpClient } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient()    // ← makes HttpClient injectable everywhere
  ]
};
```

**Or in a module-based app:**
```typescript
// app.module.ts
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  imports: [HttpClientModule]
})
export class AppModule {}
```

**Inject into any service or component:**
```typescript
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/products';

  // methods go here
}
```

**Why inject into a service, not a component?**
- Services are the right layer for data access
- Components should receive data — not fetch it
- Follows Single Responsibility Principle (SRP)
- Services are easily mocked in unit tests

---

### Slide 4: GET Requests

**Basic typed GET:**
```typescript
import { Observable } from 'rxjs';

export interface Product {
  id: number;
  name: string;
  price: number;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/products';

  // Returns an Observable — not the data itself yet
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }
}
```

**In the component:**
```typescript
@Component({ /* ... */ })
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  private productService = inject(ProductService);

  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: (data) => this.products = data,
      error: (err)  => console.error('Failed to load products', err)
    });
  }
}
```

**Key point:** `http.get<T>()` returns an `Observable<T>` — nothing happens until you call `.subscribe()`. Observables are **lazy** — they don't execute until subscribed to.

---

### Slide 5: POST, PUT, DELETE

```typescript
@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/products';

  // POST — create
  createProduct(product: Partial<Product>): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  // PUT — replace entire resource
  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  // PATCH — partial update
  patchProduct(id: number, changes: Partial<Product>): Observable<Product> {
    return this.http.patch<Product>(`${this.apiUrl}/${id}`, changes);
  }

  // DELETE — typically returns void or a status object
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

**Component example — form submit:**
```typescript
onSubmit(): void {
  this.productService.createProduct(this.form.value).subscribe({
    next: (created) => {
      console.log('Created:', created);
      this.router.navigate(['/products']);
    },
    error: (err) => this.errorMessage = err.message
  });
}
```

---

### Slide 6: HttpParams — Query String Parameters

**Without HttpParams — manual string building (brittle):**
```typescript
// ❌ Hard to maintain, easy to break
this.http.get<Product[]>('/api/products?page=0&size=10&sort=name,asc');
```

**With HttpParams — composable and type-safe:**
```typescript
import { HttpParams } from '@angular/common/http';

getProducts(page: number, size: number, search?: string): Observable<Product[]> {
  let params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sort', 'name,asc');

  // Conditionally add optional parameter
  if (search) {
    params = params.set('search', search);
  }

  return this.http.get<Product[]>(this.apiUrl, { params });
}
// Produces: /api/products?page=0&size=10&sort=name%2Casc&search=widget
```

**Multiple values for the same key:**
```typescript
let params = new HttpParams()
  .append('tag', 'electronics')
  .append('tag', 'sale');
// Produces: ?tag=electronics&tag=sale
```

**`HttpParams` is immutable** — each `.set()` or `.append()` returns a new instance. Always reassign: `params = params.set(...)`.

---

### Slide 7: HttpHeaders

```typescript
import { HttpHeaders } from '@angular/common/http';

// One-off headers in a single request
createProduct(product: Partial<Product>): Observable<Product> {
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${this.authService.getToken()}`
  });
  return this.http.post<Product>(this.apiUrl, product, { headers });
}

// Build headers conditionally
buildHeaders(): HttpHeaders {
  let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  const token = localStorage.getItem('token');
  if (token) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }
  return headers;
}
```

**Common headers in Angular apps:**
| Header | Purpose |
|---|---|
| `Content-Type: application/json` | Tell server what format the body is in |
| `Authorization: Bearer <token>` | Authenticate the request |
| `Accept: application/json` | Tell server what format you want back |
| `X-Request-ID` | Tracing / correlation ID |

**`HttpHeaders` is also immutable** — same rule as `HttpParams`: reassign when modifying.

**Better approach:** Use interceptors to add auth headers globally instead of manually per request.

---

### Slide 8: Interceptors — The Angular Middleware

**What is an interceptor?**
- A function (or class) that sits in the HTTP pipeline
- Runs for **every** request or response automatically
- Angular equivalent of axios request/response interceptors (Day 19a)

```
Component
  → HttpClient.get()
    → [Request Interceptors run here]
      → HTTP network call →
    → [Response Interceptors run here]
  → Observable<T> arrives back
```

**Three most common uses:**
1. **Auth interceptor** — add `Authorization` header to every request
2. **Logging interceptor** — log every request URL and response time
3. **Error interceptor** — handle 401/403/500 globally

```typescript
// auth.interceptor.ts (Angular 15+ functional interceptor)
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    // Clone the request — requests are immutable
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(authReq);
  }

  return next(req);
};
```

---

### Slide 9: Logging and Error Interceptors

**Logging interceptor — measure request duration:**
```typescript
// logging.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
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

**Registering multiple interceptors:**
```typescript
// app.config.ts
import { provideHttpClient, withInterceptors } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([
        authInterceptor,     // runs first on request, last on response
        loggingInterceptor   // runs second on request, first on response
      ])
    )
  ]
};
```

**Order matters:** Interceptors run in order on the way out, in reverse order on the way back. Auth → Logging on request; Logging → Auth on response.

**Why clone the request?** Angular's `HttpRequest` objects are immutable. You can't modify them — you clone with changes. This prevents interceptors from accidentally sharing mutation state.

---

### Slide 10: Error Handling with catchError

**Angular HttpClient throws on 4xx and 5xx (like axios).**

```typescript
import { catchError, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let message: string;

    if (error.status === 0) {
      // Client-side or network error
      message = 'Network error — check your connection';
    } else if (error.status === 401) {
      message = 'Unauthorized — please log in';
    } else if (error.status === 404) {
      message = 'Resource not found';
    } else if (error.status >= 500) {
      message = 'Server error — please try again later';
    } else {
      message = error.error?.message ?? `HTTP ${error.status}: ${error.statusText}`;
    }

    console.error('HTTP error:', error);
    return throwError(() => new Error(message));
  }
}
```

**In the component:**
```typescript
ngOnInit(): void {
  this.productService.getProducts().subscribe({
    next: (data) => {
      this.products = data;
      this.loading = false;
    },
    error: (err) => {
      this.errorMessage = err.message;
      this.loading = false;
    }
  });
}
```

---

### Slide 11: Global Error Interceptor

**Centralize error handling across all requests:**
```typescript
// error.interceptor.ts
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }
      if (error.status === 403) {
        router.navigate(['/forbidden']);
      }
      // Re-throw for any component-level handling
      return throwError(() => error);
    })
  );
};
```

**The layered error strategy:**
```
Global interceptor     → handles 401 (redirect to login), 403 (forbidden), 500 (toast notification)
Service catchError     → maps HttpErrorResponse to a readable Error message
Component error handler → sets errorMessage in template, updates loading state
```

Each layer does only its job. Don't duplicate error logic across all three.

---

### Slide 12: Complete Service Example

```typescript
// product.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../environments/environment';
import { Product } from './product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/products`;

  getProducts(page = 0, size = 20): Observable<Product[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Product[]>(this.apiUrl, { params }).pipe(
      catchError(err => throwError(() => new Error(err.error?.message ?? 'Failed to load products')))
    );
  }

  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => throwError(() => new Error(err.error?.message ?? 'Product not found')))
    );
  }

  createProduct(data: Partial<Product>): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, data).pipe(
      catchError(err => throwError(() => new Error(err.error?.message ?? 'Failed to create product')))
    );
  }

  updateProduct(id: number, data: Partial<Product>): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(err => throwError(() => new Error(err.error?.message ?? 'Failed to update product')))
    );
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => throwError(() => new Error(err.error?.message ?? 'Failed to delete product')))
    );
  }
}
```

---

### Slide 13: Environment Files in Angular

```typescript
// src/environments/environment.ts  ← development
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// src/environments/environment.prod.ts  ← production (ng build --configuration production)
export const environment = {
  production: true,
  apiUrl: 'https://api.myapp.com'
};
```

**`angular.json` file replacement config:**
```json
"fileReplacements": [
  {
    "replace": "src/environments/environment.ts",
    "with": "src/environments/environment.prod.ts"
  }
]
```

**Usage in service:**
```typescript
import { environment } from '../environments/environment';

private apiUrl = `${environment.apiUrl}/products`;
```

Angular automatically swaps the file at build time based on `--configuration`. Same concept as Vite's `.env.development` / `.env.production` from Day 19a.

---

### Slide 14: Observing the Full Response

By default, `http.get<T>()` returns the response body directly. But sometimes you need headers, status codes, or the full response object:

```typescript
import { HttpResponse } from '@angular/common/http';

// observe: 'response' — get the full HttpResponse<T>
getProductWithHeaders(id: number): Observable<HttpResponse<Product>> {
  return this.http.get<Product>(`${this.apiUrl}/${id}`, {
    observe: 'response'
  });
}

// In the component:
this.productService.getProductWithHeaders(1).subscribe(response => {
  console.log('Status:', response.status);                      // 200
  console.log('X-Total-Count:', response.headers.get('X-Total-Count'));
  console.log('Body:', response.body);                          // Product object
});
```

**Other observe options:**
```typescript
// observe: 'events' — streaming upload/download progress
this.http.get(url, { observe: 'events', reportProgress: true }).subscribe(event => {
  if (event.type === HttpEventType.DownloadProgress) {
    this.progress = Math.round(100 * event.loaded / (event.total ?? 1));
  }
});
```

**responseType:**
```typescript
// Download a blob (file download)
this.http.get(url, { responseType: 'blob' }).subscribe(blob => {
  const url = URL.createObjectURL(blob);
  window.open(url);
});
```

---

### Slide 15: Loading and Error States in Components

**Full three-state pattern in Angular:**
```typescript
@Component({
  template: `
    <div *ngIf="loading" class="spinner" aria-label="Loading"></div>
    <p *ngIf="error" role="alert" class="error">{{ error }}</p>
    <ul *ngIf="!loading && !error">
      <li *ngFor="let product of products">{{ product.name }}</li>
    </ul>
  `
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  error: string | null = null;

  private productService = inject(ProductService);

  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }
}
```

**Using `finalize` for loading cleanup (like `finally` in Promises):**
```typescript
import { finalize } from 'rxjs/operators';

this.productService.getProducts().pipe(
  finalize(() => this.loading = false)   // runs whether success or error
).subscribe({
  next: (data) => this.products = data,
  error: (err)  => this.error = err.message
});
```

---

### Slide 16: HttpClient vs fetch / axios Comparison

| Feature | Angular HttpClient | fetch (Day 19a) | axios (Day 19a) |
|---|---|---|---|
| Return type | `Observable<T>` | `Promise<Response>` | `Promise<AxiosResponse>` |
| JSON parsing | Automatic | Manual `.json()` | Automatic (`response.data`) |
| Throws on 4xx/5xx | ✅ Yes | ❌ No (check `.ok`) | ✅ Yes |
| Interceptors | ✅ Built-in | ❌ Manual | ✅ Built-in |
| Typed responses | ✅ Generics `get<T>()` | Manual casting | ✅ Generics |
| Cancellation | `takeUntil` / `unsubscribe` | `AbortController` | `CancelToken` |
| Request retries | `retry()` operator | Manual | Manual |
| Error type | `HttpErrorResponse` | Response object | `AxiosError` |

**Key difference to internalize:** HttpClient returns Observables, not Promises. Observables are more powerful — you can cancel them, retry them, combine them, debounce them. Part 2 covers all of this with RxJS.

---

### Slide 17: Part 1 Summary

**What you can now do:**
- Provide `HttpClient` with `provideHttpClient()` in `app.config.ts`
- Inject `HttpClient` into services and call `get<T>()`, `post<T>()`, `put<T>()`, `delete<T>()`
- Add query parameters with `HttpParams` (immutable — always reassign)
- Add headers with `HttpHeaders` (immutable — always reassign)
- Write functional interceptors for auth tokens, logging, and global error handling
- Handle `HttpErrorResponse` with `catchError` + `throwError`
- Use `environment.ts` for environment-specific API URLs
- Use `finalize()` to clear loading state regardless of success or error

**Coming up in Part 2:** We've been calling `.subscribe()` without fully understanding what it means. `Observable`, `pipe`, `catchError`, `tap`, `switchMap` — these all come from **RxJS**, and Part 2 goes deep on how it all works.
