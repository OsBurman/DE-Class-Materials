# Day 19b — Part 2: RxJS Observables, Operators & Memory Management
## Lecture Script

**Delivery time:** ~60 minutes
**Pace:** ~165 words/minute
**Format:** Verbatim instructor script with timing markers

---

`[00:00–03:00]`

Welcome back. In Part 1, we made real HTTP calls with Angular's `HttpClient`. And every time we called a service method, it returned something called an Observable. We called `.subscribe()` on it without really explaining what that means. Part 2 is where we fix that.

RxJS — Reactive Extensions for JavaScript — is a library for working with asynchronous data streams. And I want to say this clearly: RxJS is not optional in Angular. It's built into the framework. `HttpClient` returns Observables. The Angular Router emits events as Observables. `FormControl.valueChanges` is an Observable. If you're going to build Angular applications, you are going to work with RxJS every single day.

The goal for Part 2: by the end, you should understand what an Observable is, how the core operators work, when to use `forkJoin` vs `combineLatest` for combining streams, when to use Subjects and which kind, how to prevent memory leaks — which are very real in Angular — and how the async pipe makes all of this cleaner. Let's start with the mental model.

---

`[03:00–10:00]`

**Slide 2 — What is RxJS?**

The fundamental idea is: everything is a stream. A stream is a sequence of values that arrives over time. Mouse clicks — each click is a value. An HTTP response — that's one value arriving after a delay. Form input — each keystroke is a value. A timer — values at fixed intervals.

Promises also handle asynchronous operations. So why do we need Observables? Let me show you the key differences.

A Promise emits exactly one value — either it resolves once or it rejects once. An Observable can emit zero, one, or many values over time. A Promise starts executing the moment you create it — it's eager. An Observable doesn't do anything until someone subscribes — it's lazy. A Promise can't be cancelled — once it's running, it runs to completion. An Observable can be cancelled by unsubscribing. And Observables have a rich operator library — `pipe()`, `map()`, `filter()`, `switchMap()`, dozens more — that lets you transform and combine streams in powerful ways.

The analogy I like: a Promise is like ordering a package. It arrives once, and that's it. An Observable is like a newspaper subscription. You subscribe, and the newspaper keeps arriving every morning until you cancel. If you don't cancel, it never stops.

For HTTP requests, an Observable and a Promise behave pretty similarly — you get one response back, then it's done. The power of Observables shows up in the more complex scenarios we'll cover today — search boxes, real-time data, state management.

---

`[10:00–18:00]`

**Slide 4 — Subscribing to Observables.**

You've already been subscribing. Let me formalize it. When you call `.subscribe()` on an Observable, you pass an observer — an object with three optional callbacks.

`next` receives each emitted value. This is the happy path — data arrives here.

`error` receives the error if something goes wrong. After an error, the Observable terminates. No more `next` calls. The subscription ends.

`complete` fires when the Observable is done — no more values will ever arrive. After complete, the subscription also ends.

An HTTP Observable from `HttpClient` follows a specific pattern: it emits exactly one value in `next` — your response data — then immediately calls `complete`. So for HTTP calls, you'll often see just `next` and `error` in the subscribe call.

The return value of `.subscribe()` is a `Subscription` object. You can call `.unsubscribe()` on it to stop receiving values and clean up. This is important — we come back to it when we talk about memory leaks.

---

`[18:00–27:00]`

**Slide 5 — The pipe() method and operators.**

Raw Observables just give you data. Operators are what make RxJS powerful — they let you transform, filter, combine, and react to streams in a composable way.

The `pipe()` method is how you chain operators. You call `observable$.pipe(operator1, operator2, operator3)`, and each operator transforms the stream for the next one in the chain. The output of `operator1` becomes the input of `operator2`, and so on. The original Observable is never modified.

**Slides 6 — map, filter, tap.**

`map` transforms each value in the stream — exactly like `Array.map`, but for Observable values. If your API returns `{ data: Product[], total: number }` but you only need the products array, you pipe through `map(response => response.data)`. Now whoever subscribes gets a clean `Product[]` instead of the whole wrapper object. You can also use `map` to add computed properties — convert a price number to a formatted string, add an `isOnSale` flag based on price.

`filter` keeps only values that pass a condition. If your Observable emits router events — NavigationStart, NavigationEnd, NavigationCancel, NavigationError — and you only care about NavigationEnd, you pipe through `filter(event => event instanceof NavigationEnd)`. Values that don't match the condition are simply dropped — they never reach the next operator or the subscriber.

`tap` is for side effects. You want to log something, or set a loading flag, but you don't want to transform the data. `tap` receives each value, lets you run code, and then passes the value through unchanged. It's like adding a spy into the middle of the pipeline that can observe but not modify. Very useful for debugging — `tap(data => console.log('Before next operator:', data))`.

---

`[27:00–37:00]`

**Slide 7 — switchMap.**

`switchMap` is the most important operator for Angular applications, and I want to spend real time on it.

Picture a search box. The user types a letter — you fire a search request. They type another letter — you fire another. They type quickly — you fire five requests in quick succession. But you only want the results from the *most recent* search term. The earlier requests might even come back after the later ones — that's the classic race condition. The user typed "angular" but the results for "a" arrive last and overwrite the correct results.

`switchMap` solves this. Here's what it does: it takes each value from an outer Observable, and maps it to a new inner Observable. When a new value arrives on the outer Observable, it *cancels* whatever inner Observable was running and starts fresh. The user typed "a" — a search request starts. They type "an" — `switchMap` cancels the "a" request and starts a new one. "ang" — cancels "an", starts "ang". By the time they stop typing, only one request is in flight, and it's always the most recent one.

The template for a search box: `form.valueChanges.pipe(debounceTime(300), distinctUntilChanged(), switchMap(query => this.service.search(query)))`.

`debounceTime(300)` waits 300 milliseconds after the user stops typing before emitting. This prevents a request per keystroke. `distinctUntilChanged()` skips if the user backspaced and retyped the same thing — no need to re-search. `switchMap` fires the request and cancels the previous.

**Slide 8 — mergeMap and the comparison.**

`mergeMap` is `switchMap`'s sibling, but instead of cancelling previous inner Observables, it lets them all run concurrently. Use this for batch operations where you want parallelism. If you have a list of product IDs and you want to delete all of them, you use `mergeMap` — fire all the delete requests at once, handle each result as it comes back.

The selector guide: if you want "latest wins, cancel previous" — `switchMap`. If you want "all concurrent, order doesn't matter" — `mergeMap`. If you want "sequential, wait for each" — `concatMap`. And `exhaustMap` — which we didn't go deep on but you should know exists — means "ignore new values while the current inner Observable is still running." Useful for preventing double-submits on a form.

---

`[37:00–42:00]`

**Slide 10 — forkJoin and combineLatest.**

Here's a very common real-world scenario: a product detail page needs to load the product itself, the list of categories for a dropdown, and the current user's permissions — all before it can render correctly. You could chain these with `switchMap`, firing them one after another. But that's slow — you're waiting for each request to finish before starting the next. These three requests are independent. There's no reason not to run them simultaneously.

`forkJoin` is the answer. It takes multiple Observables — you can pass them as an object with named keys — fires them all simultaneously, and waits until every single one has completed before emitting a single result. You get back an object with all the results at once. It's the RxJS equivalent of `Promise.all`.

Two rules to keep in mind. First: every source Observable must complete — `forkJoin` won't emit until they all do. `HttpClient` Observables complete automatically after one value, so they're a perfect fit. If you accidentally pass a `BehaviorSubject` to `forkJoin`, it will never emit because `BehaviorSubject` never completes on its own. Second: if any one source errors, `forkJoin` errors immediately and discards the others. Handle errors in the subscribe block.

Now `combineLatest` is different. Instead of waiting for completion, it emits whenever ANY of its source Observables emits a new value. The emitted value is always an array — or object — of the most recent value from each source. Picture a product list page with three filters: category dropdown, min price input, and a search box. Each of those is a form control Observable. You want to re-query the API any time any filter changes. `combineLatest` of those three Observables gives you a stream that emits every time any one of them changes, carrying the latest value from all three. Then you pipe that into `switchMap` to trigger the search.

One gotcha with `combineLatest`: each source must emit at least one value before `combineLatest` emits anything at all. If your category filter starts empty, you need to give it an initial value with `startWith('all')`. Otherwise `combineLatest` silently waits and your component never loads.

The distinction in one sentence: use `forkJoin` when you have a fixed set of HTTP requests that should all complete before you do anything; use `combineLatest` when you have multiple live streams and you want to react to any change in any of them.

---

`[42:00–50:00]`

**Slides 11, 12, 13 — Subjects.**

Everything we've talked about so far are Observables you read from — HTTP responses, form value changes, router events. Subjects are different: they're both an Observable and an Observer. You can subscribe to them *and* push values into them. This makes them useful for cross-component communication — one place emits, multiple places listen.

A plain `Subject` is hot — it only sends values to subscribers who are already listening at the moment the value is emitted. If you subscribe after a value was pushed, you missed it. Good for events where you only care about what happens after you subscribe — like user actions.

`BehaviorSubject` is the one you'll use most. It's initialized with a default value and always holds the current value. When you subscribe, you immediately receive the current value — you don't have to wait for the next emission. And you can read the current value synchronously at any time with `.getValue()`. This makes `BehaviorSubject` perfect for state management.

Here's the pattern: in a service, you create a private `BehaviorSubject` with the initial state. Private — only the service can push new values. You expose a public `asObservable()` — this strips away the Subject's ability to push values, giving external code a read-only Observable. Components subscribe to the public Observable and receive state updates. Only the service's methods can change the state. This is the Angular equivalent of a Redux store — but with no external library, just one RxJS class.

`ReplaySubject(n)` replays the last `n` values to any new subscriber. If a component loads after some events fired, it can still catch up on recent history. Think of a notification feed — you want new components to see the last few notifications even if they joined late.

---

`[50:00–57:00]`

**Slides 14 and 15 — Memory leaks.**

Here is the most practical thing I'll say in all of Part 2. If you don't clean up subscriptions, you will leak memory. This is not theoretical — it happens in real Angular apps and it causes real bugs.

The scenario: a component subscribes to `interval(1000)` in `ngOnInit`. Every second, a value fires. The user navigates away — Angular destroys the component, removes the template from the DOM. But the subscription is still alive. It's still firing every second. If the callback touches `this.someProperty`, that keeps the component's memory from being garbage collected. Navigate back to the page — a new component is created, a new subscription starts. Now two subscriptions are running. Navigate 10 times — 10 subscriptions. This is a memory leak.

HTTP Observables from `HttpClient` are safe — they complete automatically after emitting once, so the subscription cleans itself up. But `interval`, `fromEvent`, `BehaviorSubject.asObservable()`, any long-lived Observable — these need manual cleanup.

The modern Angular 16+ solution is `takeUntilDestroyed`. You inject `DestroyRef` and pass it to `takeUntilDestroyed`. Angular automatically calls it when the component is destroyed. One line, and the subscription is managed.

If you're on an older codebase — Angular 15 or earlier — you'll see the `Subject` + `takeUntil` pattern. You create a `destroy$` Subject, you pipe `takeUntil(this.destroy$)` onto every subscription, and in `ngOnDestroy` you call `destroy$.next()` and `destroy$.complete()`. This emits on `destroy$`, which triggers `takeUntil` to complete all the subscriptions. It's more verbose but you'll encounter it constantly in existing codebases.

---

`[57:00–60:00]`

**Slides 16 and 17 — The async pipe.**

And now — my favorite part — the async pipe. This is how senior Angular developers write most of their HTTP code, and once you see why, you won't go back to manual subscriptions for data in templates.

Here's the pattern: instead of subscribing in `ngOnInit` and storing the result in a component property, you store the *Observable itself* — `products$`. Then in the template, you pipe it through `async`: `*ngIf="products$ | async as products"`. The async pipe subscribes to the Observable. When new data arrives, it triggers change detection and the template updates. When the component is destroyed, the async pipe automatically unsubscribes. Zero manual cleanup.

The `as products` syntax assigns the unwrapped value to a local template variable. Without it, every time you used `products$ | async` in the template, it would create a separate subscription. With `as`, one subscription, one variable.

When do you NOT use the async pipe? If you need to use the data in component code — passing it to a method, logging it, combining it with other data before displaying — you still need `.subscribe()` there. The async pipe is for when the Observable's output goes directly into the template.

**Slide 18 — Day 19b summary.**

Two parts, two big ideas. Part 1: `HttpClient` is Angular's typed, interceptor-powered HTTP client — always inject into services, use `HttpParams` for query strings, use interceptors for auth tokens and global error handling. Part 2: RxJS Observables are lazy streams — `pipe()` + operators transform them cleanly, `BehaviorSubject` holds state, `takeUntilDestroyed` prevents memory leaks, and the async pipe is the cleanest way to connect Observables to templates.

Coming up tomorrow — Friday — two tracks. React side: Day 20a covers advanced patterns, performance optimization with `React.memo`, `useMemo`, `useCallback`, code splitting, and deploying to production. Angular side: Day 20b covers Signals — Angular's newer, simpler reactive primitive — and Angular testing with Jasmine and Karma. Great work today — see you tomorrow.
