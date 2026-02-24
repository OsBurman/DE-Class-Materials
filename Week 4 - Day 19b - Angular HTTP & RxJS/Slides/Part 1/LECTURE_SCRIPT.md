# Day 19b — Part 1: Angular HttpClient & HTTP Requests
## Lecture Script

**Delivery time:** ~60 minutes
**Pace:** ~165 words/minute
**Format:** Verbatim instructor script with timing markers

---

`[00:00–02:30]`

Good morning everyone. So if you were in the React session yesterday — Day 19a — you saw how to use `fetch` and `axios` to pull data from an API. Today is the Angular version of that story. Same concepts, different tools.

Here's where we left off on Day 18b. You built Angular routes, you built reactive forms, you had forms collecting data and routes displaying pages. The question is: where does the form data go? And where does the data on those pages actually come from? Today we answer both of those questions with Angular's built-in HTTP client.

Our agenda for Part 1: we start by setting up `HttpClient`, then we make GET, POST, PUT, and DELETE requests. After that, `HttpParams` for query strings, `HttpHeaders` for auth tokens, interceptors — which are Angular's version of middleware for HTTP calls — and error handling with `catchError`.

One thing to keep in mind as we go: everything that `HttpClient` returns is an `Observable`. Not a Promise — an Observable. I'm going to use that word without fully explaining it until Part 2, but don't worry — the way we use it today is exactly like a Promise. The deeper concepts come in Part 2 where we cover RxJS properly.

---

`[02:30–10:00]`

**Slide 3 — HttpClient setup.**

Before you can make any HTTP requests, you need to tell Angular that `HttpClient` is available. In a modern standalone Angular app — Angular 17 and up — you do this in `app.config.ts` with `provideHttpClient()`. That registers the HttpClient in Angular's dependency injection system. Then anywhere in your app — any service, any component — you can inject it.

In a module-based app, you import `HttpClientModule` into your `AppModule` instead. Same effect, different syntax. If you're on Angular 16 or earlier with modules, that's your path.

Now, a crucial design choice: do you inject `HttpClient` directly into your components? The answer is no, and here's why. Components have one job: manage the template. They bind data, react to user events, and update the view. The moment you put `http.get()` calls inside a component, you've mixed two responsibilities — data fetching and view management — into the same class. That violates the Single Responsibility Principle. And practically, it makes testing much harder, because now you can't test the component without also mocking HTTP calls.

The right home for `HttpClient` is a service. You create a `ProductService`, you inject `HttpClient` there, and the service's job is to talk to the API. The component's job is to call the service and bind the result to the template. Clean separation.

With Angular's `inject()` function, you just write `private http = inject(HttpClient)` inside the class body. No constructor required. You can also do constructor injection — `constructor(private http: HttpClient){}` — both work, the `inject()` style is the modern approach.

---

`[10:00–20:00]`

**Slide 4 — GET requests.**

Let's make our first real HTTP call. In the `ProductService`, we add a `getProducts` method. It returns `Observable<Product[]>`. The `<Product[]>` generic type tells TypeScript what shape of data to expect — the same way you'd type a Promise. The method body is just one line: `return this.http.get<Product[]>(this.apiUrl)`.

That's it. One line. Angular handles JSON parsing automatically. You get back a typed observable — no `.json()` call, no response wrapper — the data comes back directly as the type you specified.

Now here is the most important thing about Observables to understand right now: they are **lazy**. Calling `this.http.get()` does not send the HTTP request. Nothing happens until you subscribe. Think of an Observable like setting up a recipe — the recipe doesn't cook itself until you turn on the stove. `.subscribe()` is turning on the stove.

So in the component's `ngOnInit`, you call the service and subscribe. The subscribe object has three optional callbacks: `next`, which receives the data on success; `error`, which receives the error if something goes wrong; and `complete`, which fires when the observable closes. For HTTP calls, complete fires automatically after the first emission — an HTTP GET either returns data once and completes, or it errors.

Notice how clean this is: the component doesn't know whether data came from a local cache, a REST API, or a GraphQL server. It just calls `getProducts()` and gets data. If tomorrow you change the implementation from REST to GraphQL, you only change the service — zero changes in the component.

---

`[20:00–30:00]`

**Slide 5 — POST, PUT, DELETE.**

The pattern for all the mutation verbs is the same as GET — one method per operation, each returning an Observable.

For POST — creating a new product — you call `this.http.post<Product>(this.apiUrl, product)`. The second argument is the request body. Angular automatically serializes your TypeScript object to JSON and sets the Content-Type header for you. No `JSON.stringify()`, no manual header — Angular handles it.

PUT and PATCH follow the same pattern. PUT replaces the entire resource at a given ID. PATCH updates only the fields you provide. For DELETE, you typically expect no body back — so the return type is `Observable<void>`.

In the component, when a form is submitted, you call the service method and subscribe. In the `next` callback — the success path — you navigate away. In the `error` callback, you set an error message string that displays in the template.

One thing I see students miss: you still need to subscribe to POST, PUT, and DELETE. They're Observables too. A common bug is calling `this.productService.createProduct(data)` without subscribing — the HTTP request is never sent. Always subscribe.

---

`[30:00–38:00]`

**Slide 6 — HttpParams.**

When your GET request needs query string parameters — pagination, filtering, sorting — don't concatenate them manually into the URL string. That gets messy fast and is hard to maintain. Use `HttpParams` instead.

You create a `new HttpParams()` and chain `.set()` calls, one per parameter. Then pass it as the `params` option in the `get()` call: `this.http.get<Product[]>(url, { params })`. Angular builds and encodes the query string for you — spaces, special characters, all handled correctly.

`HttpParams` is **immutable**. This is a JavaScript design pattern where objects cannot be modified after creation — instead, each operation returns a new object. So `params.set('page', '0')` does not modify `params` — it returns a new `HttpParams` with that value set. This means you must always reassign: `params = params.set('page', '0')`. If you write `params.set(...)` without reassigning, the new parameter silently gets thrown away. It's one of the more common HttpParams bugs.

When you need to add a parameter conditionally — only if the user provided a search term — use an `if` block and reassign: `if (search) { params = params.set('search', search); }`.

For multiple values with the same key — like filtering by multiple tags — use `.append()` instead of `.set()`. `.set()` replaces the previous value; `.append()` adds alongside it.

---

`[38:00–46:00]`

**Slide 7 — HttpHeaders.**

HttpHeaders work exactly like HttpParams — immutable, same chainable API, always reassign when modifying.

For a single request, you create `new HttpHeaders({ 'Authorization': 'Bearer ' + token })` and pass it as the `headers` option. This adds the header only for that one request.

But here's the question you should already be thinking: if every API call needs an Authorization header, are you going to copy and paste that into every single method? That's a maintenance nightmare. If the token format changes, you update 20 methods instead of one. This is exactly the problem interceptors solve, and we're getting there in two slides.

The common headers you'll set: `Content-Type: application/json` tells the server the body is JSON — Angular usually handles this for POST/PUT automatically, but sometimes you need to set it explicitly. `Authorization: Bearer <token>` is the JWT auth header we'll implement fully in Week 6. `Accept: application/json` tells the server you want JSON back — rarely needed but occasionally useful. Custom headers like `X-Request-ID` for tracing are something you'll see in enterprise environments.

---

`[46:00–54:00]`

**Slides 8 and 9 — Interceptors.**

An interceptor is a function that sits in the middle of the HTTP pipeline. Every single outgoing request passes through it. Every single incoming response passes through it. And you register it once — in `app.config.ts` — and it applies everywhere. No more copying headers into every method.

In Angular 15 and later, interceptors are simple functions. The signature is `HttpInterceptorFn`, which takes a request and a `next` function. The `next` function is how you pass the request down the pipeline — you must call it or the request never leaves. Your interceptor runs *before* calling `next`, and you can process the response *after* calling `next` by piping on the observable it returns.

For the auth interceptor: you inject `AuthService`, get the token, and if there is one, you clone the request with the Authorization header added. Notice the word "clone" — `HttpRequest` is immutable, just like `HttpParams` and `HttpHeaders`. You never modify a request directly; you create a modified copy. `req.clone({ setHeaders: { Authorization: 'Bearer ...' } })` does that.

Then you pass the cloned request — or the original if there's no token — to `next`. That's it. Every HTTP call in your entire app now has the auth header without a single change to any service.

The logging interceptor shows how to process the response side. You call `next(req)` to get the response observable, then pipe on it with `tap`. The `tap` operator lets you observe values without modifying them — you log the response, and the data continues flowing to whoever subscribed.

Registration: in `app.config.ts`, pass `withInterceptors([authInterceptor, loggingInterceptor])` to `provideHttpClient`. Interceptors run in order on the way out and in reverse order on the way back.

---

`[54:00–60:00]`

**Slides 10 and 11 — Error handling with catchError.**

Angular's `HttpClient` throws on any 4xx or 5xx response — unlike the browser's `fetch`, which only throws on network errors. This is the behavior you want: if the server says something went wrong, you should know about it.

Errors come back as `HttpErrorResponse` objects. The important fields: `status` is the numeric HTTP status code. `error` is the parsed response body — if the server sent a JSON error object, that's here. `message` is a human-readable description.

One special case: `status === 0`. That means a network-level failure — no connection, DNS error, CORS issue. There is no server response in this case. Always handle this case separately.

The `catchError` operator intercepts the error in the Observable pipeline. You return `throwError(() => new Error(message))` to re-emit the error as a regular JavaScript Error — this way the component receives a simple `.message` string rather than a raw `HttpErrorResponse` with all its HTTP-specific fields. The component shouldn't know or care about HTTP status codes.

For global concerns — 401 redirecting to login, 403 going to a forbidden page — use an error interceptor. It catches those specific status codes, takes the navigation action, and re-throws so any per-service error handling still runs.

The three-layer strategy: interceptor handles navigation, service `catchError` maps the error to a readable message, component subscription handles loading state and user-facing error display. Three layers, three responsibilities, none overlapping.

**Slide 17 — Part 1 summary.** You can now make typed HTTP requests, pass query parameters and headers correctly, write interceptors that apply globally, and handle errors at the right layer. In Part 2, we pull back the curtain on all those Observables we've been using — and you'll see why they're more powerful than Promises. Take 10 minutes.
