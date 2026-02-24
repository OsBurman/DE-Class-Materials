# Day 19b — Angular HTTP & RxJS
# Part 1 Walkthrough Script
# Estimated time: ~90 minutes

---

## Overview of Part 1

| Segment | Topic | Time |
|---|---|---|
| 0 | Recap + Day intro | 5 min |
| 1 | HttpClient setup | 5 min |
| 2 | Data models | 5 min |
| 3 | HttpParams | 8 min |
| 4 | HttpHeaders | 5 min |
| 5 | Error handling helper | 10 min |
| 6 | GET requests | 10 min |
| 7 | POST / PUT / DELETE | 10 min |
| 8 | Component usage | 7 min |
| 9 | Interceptors — intro | 5 min |
| 10 | Class-based interceptors | 10 min |
| 11 | Functional interceptors | 5 min |
| 12 | Registration + chain order | 5 min |
| 13 | Q&A + wrap-up | 5 min |

**Files:** `01-http-client-crud.ts`, `02-http-interceptors.ts`

---

## Segment 0 — Recap & Day Introduction (5 min)

"Good morning, everyone. Yesterday in Day 18b we wrapped up Angular routing and reactive forms — you saw how to build complex forms with validation and submit data to a component. Today we're going to close out React and Angular Week by answering one very important question: **how does your Angular app actually talk to a backend API?**

Today is split into two big parts. Part 1 is all about **Angular's HttpClient** — making GET, POST, PUT, and DELETE requests, building query strings, adding headers, and intercepting every request with interceptors. Part 2 this afternoon is **RxJS** — the reactive programming library that powers Angular's async model.

These two topics are deeply connected. Every HttpClient method returns an **Observable**, and observables are RxJS. So Part 1 gives you the 'what' — how to make HTTP calls — and Part 2 gives you the 'how' — how to transform, combine, and manage those data streams.

Let's go. Open `01-http-client-crud.ts`."

---

## Segment 1 — HttpClient Setup (5 min)

*Point to Section 0 in `01-http-client-crud.ts`*

"Before we can use HttpClient, Angular needs to know that HTTP support is provided. This is the most commonly forgotten step and it causes a very specific error message — `NullInjectorError: No provider for HttpClient`. Let's look at how to set it up.

In **modern Angular 17+ standalone apps**, you go to your `main.ts` or `app.config.ts` and add `provideHttpClient()` inside the providers array.

```typescript
bootstrapApplication(AppComponent, {
  providers: [provideHttpClient()]
});
```

In an **NgModule-based app** — which you'll still see in many enterprise codebases — you add `HttpClientModule` to the `imports` array of your `AppModule`.

> **⚠️ Watch out:** Every time you see `NullInjectorError: No provider for HttpClient`, your very first question should be: 'Did I add provideHttpClient() or HttpClientModule?' Nine times out of ten, that's the fix.

Once that's done, Angular can inject `HttpClient` anywhere in your app. You'll see exactly how in the service we're about to build."

---

## Segment 2 — Data Models (5 min)

*Point to Section 1 — interfaces*

"Good TypeScript habits start with defining your data shapes. Let's look at three interfaces at the top of the file.

`Course` — this is our main domain model. An id, title, instructor, duration, level, and an optional enrollmentCount.

`PaginatedResponse<T>` — notice this is **generic**. The `T` lets us say 'a paginated response of *whatever type we want*'. So `PaginatedResponse<Course>` means the data array contains Course objects.

`ApiError` — a shape we'll use when an error comes back from the server.

> **❓ Question for the class:** Why define a generic PaginatedResponse instead of a specific CoursePaginatedResponse? 
> *(Answer: reusability — you can use PaginatedResponse<User>, PaginatedResponse<Enrollment>, etc. without rewriting the shape)*

Good. These models make our code self-documenting and TypeScript will catch shape mismatches at compile time instead of at runtime."

---

## Segment 3 — HttpParams (8 min)

*Point to Section 2 — `buildCourseSearchParams`*

"When you need to pass query parameters — the `?page=1&level=beginner` part of a URL — you use **HttpParams**. You never manually concatenate strings onto the URL.

Look at `buildCourseSearchParams`. We start with `new HttpParams()` and immediately chain `.set()` calls.

```typescript
let params = new HttpParams()
  .set('page', page.toString())
  .set('pageSize', pageSize.toString());
```

> **⚠️ Watch out — critical:** `HttpParams` is **immutable**. Every call to `.set()` returns a NEW instance. That's why we reassign: `params = params.set(...)`. If you just call `params.set(...)` without reassigning, your params object is unchanged. This trips up a LOT of developers coming from a mutable-objects mindset.

Then we conditionally add optional params:
```typescript
if (level) {
  params = params.set('level', level);
}
```

This produces `?page=1&pageSize=10&level=beginner` — cleanly, safely, with no manual string building.

> **❓ Question:** What would happen if you forgot to reassign and just wrote `params.set('level', level)` without `params =`?
> *(Answer: nothing — the new params would be thrown away and level would never appear in the URL)*

The function returns the fully built params object, which we'll pass to http.get() in a moment."

---

## Segment 4 — HttpHeaders (5 min)

*Point to Section 3 — `buildAuthHeaders`*

"Headers work the same way as params — also immutable, also use `.set()` and reassign.

```typescript
return new HttpHeaders({
  'Content-Type': 'application/json',
  Authorization: `Bearer ${token}`,
  'X-App-Version': '2.0',
});
```

You can pass an object literal to the constructor when you know all your headers up front — this is the cleanest approach.

In the real world you'll most commonly add:
- `Authorization: Bearer <token>` for authenticated requests
- `Content-Type: application/json` — though Angular usually sets this automatically for POST/PUT with a JS object body
- Custom headers like `X-Correlation-ID` for tracing, or `X-API-Key`

> **Note:** In Part 1 of this file we're setting headers manually. In the interceptor file (which we'll reach shortly), you'll see how to add the token **automatically** to every request without touching the service code at all."

---

## Segment 5 — Error Handling Helper (10 min)

*Point to Section 4 — `handleHttpError`*

"This is one of the most important sections. Let's look at `handleHttpError`.

Angular's `HttpErrorResponse` comes in two fundamentally different flavours, and we handle them differently.

**Flavour 1 — status === 0:**
```typescript
if (error.status === 0) {
  userMessage = 'Network error — check your internet connection.';
}
```
Status zero means the request never reached the server. Could be: no internet, CORS preflight blocked, or the server is totally unreachable. The `error.error` property is a `ProgressEvent`, not a server response.

**Flavour 2 — any other status (4xx / 5xx):**
```typescript
userMessage = error.error?.message ?? `Server error: ${error.status} ${error.statusText}`;
```
The server responded, but with an error. `error.error` is the parsed response body — and our backend puts a `message` field there. We use optional chaining `?.` as a safety net in case the body is malformed.

Now look at the return statement:
```typescript
return throwError(() => new Error(userMessage));
```

`throwError()` creates an Observable that immediately errors. When we pipe this through `catchError`, instead of propagating the raw HttpErrorResponse, we give back a clean JavaScript `Error` with a user-friendly message.

> **❓ Question:** Why do we return `throwError(() => ...)` instead of just `throw new Error(...)`?
> *(Answer: We're inside an Observable pipeline. `throw` would crash the JavaScript call stack. `throwError` creates an errored Observable that the subscriber's `error:` callback receives gracefully)*

Finally, at the bottom — look at the error scenarios reference (Section 7). I won't read all of these but I want you to bookmark this. Every status code has a meaning. When debugging API integration issues, knowing what 401 vs 403 vs 422 means will save you hours."

---

## Segment 6 — GET Requests (10 min)

*Point to Section 5 — CourseService, starting with 5a and 5b*

"Now we're in `CourseService`. Let's look at the class declaration:

```typescript
@Injectable({ providedIn: 'root' })
export class CourseService {
  private readonly apiUrl = 'https://api.lms-demo.io/v1/courses';
  constructor(private http: HttpClient) {}
}
```

`providedIn: 'root'` means Angular creates one singleton instance of this service for the whole app. The constructor uses dependency injection to get `HttpClient` — Angular provides it because we called `provideHttpClient()` at bootstrap.

Now `getCourses()`:
```typescript
getCourses(page = 1, pageSize = 10, level?: string, search?: string) {
  const params = buildCourseSearchParams(page, pageSize, level, search);
  return this.http
    .get<PaginatedResponse<Course>>(this.apiUrl, { params })
    .pipe(
      retry(1),
      catchError(handleHttpError)
    );
}
```

`http.get<T>(url, options)` — the generic `T` tells TypeScript what type to expect in the response body. Angular will parse the JSON for us automatically.

We pass `{ params }` in the options — that's shorthand for `{ params: params }`.

We pipe through `retry(1)` — retry once on failure. This is safe for GET because GET is idempotent. **Don't retry POST/PUT/DELETE** — you could duplicate a write.

Then `catchError(handleHttpError)` — the helper we just wrote.

Now `getCourseById()` at 5b — almost identical but we build the URL with a template literal: `${this.apiUrl}/${id}`. Nice and clean.

Finally, `getCourseWithFullResponse()` at 5f:
```typescript
{ observe: 'response' }
```
By default HttpClient gives you just the parsed body. `observe: 'response'` gives you the full HttpResponse — you get `.status`, `.headers`, and `.body`. Useful when you need to read response headers (like a `Location` header after a POST)."

---

## Segment 7 — POST, PUT, DELETE (10 min)

*Point to sections 5c, 5d, 5e, 5g*

"Now the write operations. `createCourse()`:

```typescript
createCourse(courseData: Omit<Course, 'id'>, authToken: string): Observable<Course> {
  const headers = buildAuthHeaders(authToken);
  return this.http
    .post<Course>(this.apiUrl, courseData, { headers })
    .pipe(catchError(handleHttpError));
}
```

`Omit<Course, 'id'>` — a TypeScript utility type that says 'Course but without the id field'. The server generates the ID, so we shouldn't be sending it. This enforces that contract at compile time.

We pass `courseData` as the second argument — Angular serialises it to JSON automatically. The third argument is the options object with headers.

Notice we're NOT using `retry()` here. POST is not idempotent. Retrying a POST could create duplicate courses.

`updateCourse()` — same shape as POST but uses `http.put()`. PUT means 'replace the entire resource'. The body needs all fields.

> **⚠️ Watch out:** PUT vs PATCH. PUT replaces the whole thing. PATCH sends only the changed fields. Angular has `http.patch()` as well. In REST APIs this distinction matters — a poorly designed PUT that sends partial data can accidentally nullify fields.

`deleteCourse()` — uses `http.delete<void>()`. The `void` generic means we're not expecting a response body. Most DELETE endpoints return 204 No Content, which is fine.

Now `enrollInCourse()` at 5g — this one shows **both params AND headers in one request**:
```typescript
return this.http.post<...>(
  `${this.apiUrl}/enroll`,
  body,
  { headers, params }
);
```
Both `headers` and `params` can live in the same options object. Clean, simple."

---

## Segment 8 — Component Usage (7 min)

*Point to Section 6 — the commented-out component*

"This section is commented out — it's not a real component file, it's just showing you how a component would consume this service.

```typescript
const sub = this.courseService
  .getCourses(1, 10, 'beginner', 'angular')
  .subscribe({
    next: (response) => { ... },
    error: (err: Error) => { ... },
    complete: () => { ... },
  });
this.subscription.add(sub);
```

`subscribe()` takes an observer object with three optional callbacks:
- `next` — called for each emitted value (usually just once for HTTP)
- `error` — called if the observable errors
- `complete` — called when the stream closes (after the response)

We add the subscription to a `Subscription` container and call `subscription.unsubscribe()` in `ngOnDestroy()`.

> **⚠️ Watch out:** If you don't unsubscribe, and the user navigates away, the component is destroyed but the subscription stays alive. When the response eventually arrives, it tries to update a component that no longer exists. This is a **memory leak**. We'll cover the modern ways to prevent this — the `async pipe` and `takeUntilDestroyed` — in Part 2.

Now let's switch to the interceptors file. Same folder, `02-http-interceptors.ts`."

---

## Segment 9 — What are Interceptors? (5 min)

*Point to Section 1 in `02-http-interceptors.ts`*

"Interceptors are middleware for Angular's HTTP layer. Look at the pipeline diagram in the comment:

```
Component → Service → [Interceptor A] → [Interceptor B] → Backend
         ←            ←                ←                ←
```

Every outgoing request travels through ALL interceptors in order. Every incoming response travels through them in REVERSE order.

This gives you a central place to do things that apply to EVERY HTTP call — without touching every individual service method.

Common uses listed in the comment:
- Attach JWT tokens
- Log timing
- Show/hide a global spinner  
- Handle 401 globally and redirect to login
- Add correlation IDs for distributed tracing

> **❓ Question for the class:** Why is this better than adding the auth header to every individual service method?
> *(Answer: DRY — one place, one change. If your token storage mechanism changes, you change one interceptor, not 20 service methods)*"

---

## Segment 10 — Class-Based Interceptors (10 min)

*Point to Sections 2, 3, 4 in `02-http-interceptors.ts`*

"Let's look at `AuthTokenInterceptor` first. It implements `HttpInterceptor` which requires one method: `intercept(req, next)`.

The critical rule:
```typescript
const authenticatedReq = req.clone({
  headers: req.headers.set('Authorization', `Bearer ${token}`),
});
return next.handle(authenticatedReq);
```

**Never mutate `req` directly** — it's immutable by design. Always `.clone()` with your changes, then pass the clone to `next.handle()`.

`next.handle()` is what actually passes the request to the next interceptor (or to the backend if there are no more interceptors). If you forget to return `next.handle(...)`, your HTTP request will silently disappear — it will never be sent.

Now `LoggingInterceptor`:
```typescript
return next.handle(req).pipe(
  tap({
    next: (event) => { if (event instanceof HttpResponse) { ... } },
    error: (err) => { console.error(...) }
  })
);
```

`tap` lets us peek without changing. We check `event instanceof HttpResponse` because Angular emits different event types — upload progress, response headers, etc. We only want to log when the full response has arrived.

`GlobalErrorInterceptor` — this is where you centralise 401 and 403 handling. Instead of every component checking `if (err.status === 401) router.navigate(['/login'])`, you do it once here.

> **⚠️ Watch out:** `catchError` in the interceptor runs BEFORE the component's `subscribe({ error: ... })`. If the interceptor handles the error and returns a replacement Observable, the component's error callback won't fire. Make sure to still return `throwError(...)` for errors you don't fully handle, so the component knows something went wrong."

---

## Segment 11 — Functional Interceptors (5 min)

*Point to Sections 5 and 6 in `02-http-interceptors.ts`*

"Angular 15 introduced **functional interceptors**. Instead of a class with `@Injectable`, you just write a plain function.

```typescript
export const authTokenInterceptorFn: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('access_token');
  if (!token) return next(req);
  const authenticatedReq = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`),
  });
  return next(authenticatedReq);
};
```

Same logic as the class version, but the type is `HttpInterceptorFn` and the second argument is `HttpHandlerFn` — a function you call directly instead of `next.handle()`.

This is the **recommended approach for new standalone Angular apps**. Less boilerplate, easier to test.

The `loadingSpinnerInterceptorFn` shows a great pattern using `finalize()`:
```typescript
return next(req).pipe(
  finalize(() => {
    activeRequestCount--;
    if (activeRequestCount === 0) hideSpinner();
  })
);
```
`finalize` runs whether the observable completes OR errors — so the spinner always disappears."

---

## Segment 12 — Registration and Chain Order (5 min)

*Point to Sections 8 and 9 in `02-http-interceptors.ts`*

"How you register interceptors differs between NgModule and standalone.

**NgModule** — provide each one with `HTTP_INTERCEPTORS` token and `multi: true`:
```typescript
{ provide: HTTP_INTERCEPTORS, useClass: AuthTokenInterceptor, multi: true }
```

**Standalone** — wrap them in `withInterceptors()`:
```typescript
provideHttpClient(
  withInterceptors([authTokenInterceptorFn, loadingSpinnerInterceptorFn])
)
```

**Order matters.** Look at the chain diagram:
```
Outgoing (request):   [Auth] → [Logging] → [ErrorHandling] → Backend
Incoming (response):  Backend → [ErrorHandling] → [Logging] → [Auth]
```

The last registered interceptor is closest to the backend, the first registered is closest to your component. So:
- Put auth token first — most requests need it
- Put logging anywhere that gives you what you need to see
- Put error handling last in registration (so it's first to process responses)

That's Part 1. Before we break — any questions on HttpClient or interceptors?"

---

## Segment 13 — Q&A Prompts + Wrap-up (5 min)

**Discussion questions to pose to the class:**

1. "If you have three interceptors registered and the second one calls `return throwError(...)` without calling `next.handle()`, what happens? Does the request ever reach the backend?"
   *(Answer: No — the request is short-circuited. The third interceptor and the backend never see it)*

2. "You need to add a custom `X-Trace-ID` header to every request for distributed tracing. Where would you put that code — in the service or in an interceptor? Why?"
   *(Answer: Interceptor — it's a cross-cutting concern that shouldn't live in every service)*

3. "Your backend returns a paginated list at `/api/courses?page=1&size=10`. A junior dev writes `const url = '/api/courses?page=' + page + '&size=' + size`. What's wrong with this?"
   *(Answer: String concatenation is error-prone, doesn't encode special characters, harder to maintain. Use HttpParams)*

4. "What is the difference between `http.get<Course>` and `http.get<Course>(..., { observe: 'response' })`?"
   *(Answer: First gives you Course (just the body). Second gives you HttpResponse<Course> — the full response including status and headers)*

"Great work. Take a 10-minute break. When we come back, we're diving into **RxJS** — the reactive programming engine that makes all of this work."

---

*End of Part 1 Script*
