# Exercise 03 — HTTP Interceptor for Auth Headers

## Learning Objectives
- Implement the `HttpInterceptor` interface
- Clone an outgoing `HttpRequest` and attach an `Authorization` header
- Register an interceptor with the `HTTP_INTERCEPTORS` multi-provider token
- Understand the interceptor chain and `HttpHandler`

## Scenario
Your app must send a `Bearer` token on every outgoing HTTP request. Rather than manually attaching the header inside each service, you will build a centralised **auth interceptor**.

## Instructions

### Step 1 — Create `AuthInterceptor`
Open `auth.interceptor.ts` and complete the `TODO` items:

1. Implement `HttpInterceptor` on the class.
2. Inside `intercept(req, next)`:
   - Clone the request with `req.clone({ setHeaders: { Authorization: 'Bearer my-secret-token' } })`.
   - Pass the cloned request to `next.handle(clonedReq)` and return the result.

### Step 2 — Register the interceptor
Open `app.module.ts` and complete the `TODO` items:

1. Import `HTTP_INTERCEPTORS` from `@angular/common/http`.
2. Add a provider entry:
   ```ts
   { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
   ```

### Step 3 — Verify in the component
`AppComponent` already makes a GET request in `ngOnInit`. Open browser DevTools → Network tab and confirm every request carries the `Authorization: Bearer my-secret-token` header.

## Expected Behaviour
- The interceptor fires for every request made through `HttpClient`.
- The original request object is **never mutated** — only the clone is modified.
- Adding `multi: true` preserves any other interceptors already registered.

## Key Concepts
| Concept | API |
|---|---|
| Interceptor interface | `implements HttpInterceptor` |
| Method signature | `intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>` |
| Cloning a request | `req.clone({ setHeaders: { ... } })` |
| Registering | `{ provide: HTTP_INTERCEPTORS, useClass: ..., multi: true }` |
