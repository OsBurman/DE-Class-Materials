# Day 19b — Angular HTTP & RxJS
# Part 2 Walkthrough Script
# Estimated time: ~95 minutes

---

## Overview of Part 2

| Segment | Topic | Time |
|---|---|---|
| 0 | Transition from Part 1 | 3 min |
| 1 | What is an Observable? Cold vs Hot | 10 min |
| 2 | Creation helpers: of, from, interval, fromEvent | 7 min |
| 3 | map operator | 8 min |
| 4 | filter operator | 6 min |
| 5 | tap operator | 5 min |
| 6 | switchMap — cancel previous | 10 min |
| 7 | mergeMap — parallel | 7 min |
| 8 | debounceTime + distinctUntilChanged | 7 min |
| 9 | Full search pipeline | 8 min |
| 10 | concatMap + exhaustMap overview | 5 min |
| 11 | Subject | 8 min |
| 12 | BehaviorSubject | 10 min |
| 13 | ReplaySubject | 5 min |
| 14 | Memory leaks + unsubscription patterns | 12 min |
| 15 | async pipe | 8 min |
| 16 | Summary + Q&A | 7 min |

**Files:** `01-rxjs-operators.ts`, `02-rxjs-subjects-and-memory.ts`

---

## Segment 0 — Transition from Part 1 (3 min)

"Welcome back. In Part 1 we built out the full HttpClient layer — GET, POST, PUT, DELETE, HttpParams, HttpHeaders, and interceptors. Every single one of those `http.get()` calls returned something. Let's zoom in on what that something actually is.

```typescript
getCourses(): Observable<PaginatedResponse<Course>> { ... }
```

It returns an `Observable`. That word appeared in Part 1 a lot. Now we're going to understand it completely.

Open `01-rxjs-operators.ts`."

---

## Segment 1 — What is an Observable? (10 min)

*Point to Section 1 in `01-rxjs-operators.ts`*

"RxJS is a library for composing asynchronous and event-based programs using sequences — or **streams** — of values. An Observable is the core primitive: it's a **lazy, cancellable stream of values over time**.

Let me contrast it with a Promise, because you already know Promises.

| | Promise | Observable |
|---|---|---|
| **When does it start?** | Immediately — eager | Only when subscribed — lazy |
| **How many values?** | Exactly one | Zero, one, or many |
| **Cancellable?** | No | Yes — unsubscribe() |
| **Operators?** | .then(), .catch() | Hundreds of RxJS operators |

Key point: **lazy**. When you call `courseService.getCourses()`, that HTTP request has NOT been made yet. The Observable is just a description of what will happen. The request fires only when you call `.subscribe()`.

Let's look at the manual Observable:

```typescript
const manualObservable = new Observable<string>((subscriber) => {
  subscriber.next('First value');
  subscriber.next('Second value');
  subscriber.complete();
});
```

`subscriber.next(value)` — emit a value.
`subscriber.complete()` — signal that no more values are coming.
`subscriber.error(err)` — signal an error; terminates the stream.

Then subscribe:
```typescript
manualObservable.subscribe({
  next: (val) => console.log('Received:', val),
  error: (err) => console.error('Error:', err),
  complete: () => console.log('Stream complete'),
});
```

You pass an **observer** — an object with three optional callbacks: `next`, `error`, `complete`.

> **❓ Question:** If you subscribe to this Observable three times, how many times does each value get emitted in total?
> *(Answer: Three times each — one per subscription. This is because it's a COLD Observable — each subscriber gets its own independent execution. With HTTP, each subscription triggers a new HTTP request)*

Compare that to a **hot** Observable — like mouse clicks or a BehaviorSubject (which we'll see shortly). Hot Observables share one execution across all subscribers."

---

## Segment 2 — Creation Helpers (7 min)

*Point to Section 2*

"You'll rarely create Observables from scratch with `new Observable()`. RxJS provides creation helpers.

**`of(1, 2, 3, 4, 5)`** — takes static values and emits them one at a time, then completes. Think of it as the Observable equivalent of having a list of values you already know.

**`from([...])`** — converts an existing array, Promise, or iterable. Very useful for converting a fetch Promise to an Observable if you're working in a context that expects Observables.

**`interval(1000)`** — emits 0, 1, 2, 3... every 1 second, forever. We pipe it through `take(5)` to limit it to 5 emissions.

**`fromEvent(element, 'input')`** — wraps a DOM event. This is how we turn keyboard input into an Observable stream. Every keypress becomes a value in the stream.

> **⚠️ Watch out:** `interval()` never completes on its own. If you subscribe to it without cleaning up, it runs forever — even after the component is destroyed. This is the root cause of memory leaks in Angular. We'll come back to this."

---

## Segment 3 — map (8 min)

*Point to Section 3*

"Operators are functions you pipe through your Observable to transform it. `pipe()` is how you chain them.

The most fundamental operator is `map`. Let's look at `rawCourses$` and `mappedCourses$`.

The API returns data in snake_case — `course_title`, `instructor_name`, `duration_hours`. Our Angular components expect camelCase. We use `map` to normalise this:

```typescript
const mappedCourses$ = rawCourses$.pipe(
  map((rawList) =>
    rawList.map((raw): Course => ({
      id: raw.id,
      title: raw.course_title,
      instructor: raw.instructor_name,
      duration: raw.duration_hours,
    }))
  )
);
```

> **⚠️ Watch out — two different maps!** The OUTER `map()` is the **RxJS operator** — it runs once per Observable emission. The INNER `.map()` is **Array.prototype.map** — it transforms each item inside the array. Students mix these up. The RxJS map runs on the Observable; the array map runs on the data inside.

This is exactly the kind of normalisation you'd put in your Angular service, so components always work with clean camelCase models regardless of what the backend sends."

---

## Segment 4 — filter (6 min)

*Point to Section 4*

"`filter` works exactly like you'd expect from `Array.filter` — but for Observable streams. Only values that pass the predicate make it through.

```typescript
const completedEnrollments$ = enrollments$.pipe(
  filter((enrollment) => enrollment.status === 'completed')
);
```

Then we chain another operator — `filter` again, this time to narrow the TypeScript type:
```typescript
filter((e): e is Enrollment & { grade: number } => e.grade !== undefined)
```

That `e is ...` syntax is a **type predicate** — it tells TypeScript that if this filter passes, `e.grade` is definitely a number. Now TypeScript is happy when we access `e.grade` in the next `map`.

> **❓ Question:** What would `grades$` emit if you changed the filter to `enrollment.status === 'active'`?
> *(Answer: Nothing — active enrollments don't have grades in this dataset, so after the grade filter there's nothing left to emit)*"

---

## Segment 5 — tap (5 min)

*Point to Section 5*

"`tap` is your debugging best friend in RxJS chains. It lets you peek at the stream without changing anything.

```typescript
const coursePipeline$ = rawCourses$.pipe(
  tap((raw) => console.log('📥 Raw data received:', raw)),
  map((rawList) => rawList.map(raw => ({...}))),
  tap((courses) => console.log('✅ Mapped courses:', courses))
);
```

Whatever you log in `tap`, the value passes through UNCHANGED to the next operator. It's like putting a spy in the pipeline.

> **⚠️ Watch out:** Don't leave `tap(console.log)` calls in production code — they add noise and can leak sensitive data to the console. Remove them (or replace with a logging service) before shipping.

When you're debugging a complex RxJS chain and values aren't what you expect, drop a `tap` at each step and work through it methodically."

---

## Segment 6 — switchMap (10 min)

*Point to Section 6*

"Now we get to the operator that students find most confusing — and most powerful once it clicks.

`switchMap` subscribes to a new 'inner' Observable for each value the 'outer' Observable emits. Here's the twist: **if a new outer value arrives before the inner Observable completes, it CANCELS the previous inner subscription** and switches to the new one.

Let me make this concrete with our search example. The user is typing 'angular':

```
User types 'a'   → switchMap subscribes to searchCourses('a')
User types 'an'  → searchCourses('a') still running... CANCELLED. Subscribes to searchCourses('an')
User types 'ang' → searchCourses('an') still running... CANCELLED. Subscribes to searchCourses('ang')
User types 'angu'→ searchCourses('ang') still running... CANCELLED. Subscribes to searchCourses('angu')
searchCourses('angu') completes → results shown
```

Without switchMap, you'd fire a request on EVERY keystroke, they could arrive out of order, and you'd display stale results. switchMap solves all of that.

```typescript
const searchResults$ = searchTerms$.pipe(
  debounceTime(200),
  distinctUntilChanged(),
  switchMap((term) => searchCourses(term))
);
```

> **❓ Question:** If searchCourses() returns a Promise (not an Observable), can you still use switchMap?
> *(Answer: Yes — switchMap can accept a function that returns an Observable, a Promise, or an array. Angular converts it)*

> **⚠️ Watch out:** `switchMap` is NOT right for parallel independent operations. If you're loading course details for 3 courses and you use switchMap, loading course 2 would cancel the request for course 1. For that, use `mergeMap` — which we'll see next."

---

## Segment 7 — mergeMap (7 min)

*Point to Section 7*

"`mergeMap` also subscribes to a new inner Observable for each outer emission. The difference: **it doesn't cancel**. All inner Observables run concurrently.

```typescript
const allCourseDetails$ = courseIds2$.pipe(
  mergeMap((id) => getCourseDetails(id))
);
```

IDs 1, 2, and 3 all fire simultaneously. Results arrive as each one completes — not necessarily in order 1, 2, 3. Whichever HTTP response is fastest comes first.

> **❓ Quick check:** When should you use switchMap vs mergeMap?
> *(Answer: switchMap for things where only the LATEST matters — search, navigation. mergeMap when ALL should succeed independently — loading a dashboard with multiple parallel API calls)*

I'll give you a quick summary of all four flattening operators now — we'll revisit them in Section 11. The key insight: they all handle the 'outer Observable emits → subscribe to inner Observable' case, but differ in what happens when multiple inner subscriptions are active."

---

## Segment 8 — debounceTime and distinctUntilChanged (7 min)

*Point to Sections 8 and 9*

"These two operators nearly always appear together in typeahead search.

**`debounceTime(400)`** — wait for 400ms of silence. If the user keeps typing and values keep arriving within that window, keep resetting the timer. Only emit when there's been a pause.

Look at the example:
```typescript
const rapidKeystrokes$ = of('r', 'rx', 'rxj', 'rxjs').pipe(
  debounceTime(300)
);
// Output: Debounced search term: rxjs
```

All four values come in rapid succession. Only the last one — 'rxjs' — makes it through. This prevents 4 HTTP requests and sends just 1.

**`distinctUntilChanged()`** — if the current value equals the previous emitted value, drop it.

```typescript
const repeatedTerms$ = of('angular', 'angular', 'react', 'angular', 'angular').pipe(
  distinctUntilChanged()
);
// Output: angular, react, angular
```

The consecutive 'angular' pair is collapsed to one. The second group of 'angular' still emits because 'react' appeared in between.

> **⚠️ Watch out:** `distinctUntilChanged()` only compares CONSECUTIVE values. If the same value appears twice but not consecutively, both emissions pass through. For truly unique values across all history, you'd use a Set or a different approach.

These two operators are almost always used together before `switchMap` in a search pipeline. Think of debounceTime as 'wait for quiet', and distinctUntilChanged as 'don't bother if it's the same term'."

---

## Segment 9 — Full Search Pipeline (8 min)

*Point to Section 10 — the commented-out component*

"This commented-out component shows the complete real-world typeahead pattern. Let's read through it:

```typescript
fromEvent<Event>(this.searchInputEl.nativeElement, 'input').pipe(
  map((event) => (event.target as HTMLInputElement).value),
  filter((term) => term.length >= 2 || term.length === 0),
  debounceTime(400),
  distinctUntilChanged(),
  tap((term) => { this.searching = true; this.searchError = ''; }),
  switchMap((term) =>
    this.courseService.searchCourses(term).pipe(
      catchError((err) => {
        this.searchError = err.message;
        return of([]);
      })
    )
  ),
  finalize(() => { this.searching = false; })
).subscribe((results) => {
  this.searchResults = results;
  this.searching = false;
});
```

Let's trace each operator:
1. `fromEvent` — raw DOM events
2. `map` — extract string value
3. `filter` — skip 1-character terms (too broad to search)
4. `debounceTime(400)` — wait for 400ms pause
5. `distinctUntilChanged()` — skip if same term
6. `tap` — set loading state
7. `switchMap` — fire HTTP search, cancel previous
8. `catchError` inside switchMap — handle errors WITHOUT killing the outer stream
9. `finalize` — hide spinner always

> **⚠️ Critical:** Notice `catchError` is inside the `switchMap`, not outside. If you put `catchError` outside, a single search error would kill the entire pipeline and future keystrokes wouldn't work. By putting it inside, errors are handled per-search and the outer stream survives.

This is the professional-grade search pattern. Once you've written it once, you'll use a variation of it in every Angular app."

---

## Segment 10 — concatMap and exhaustMap (5 min)

*Point to Section 11*

"I want to give you a quick mental model for all four flattening operators before we move on.

| Operator | Cancels previous? | Queues? | Best for |
|---|---|---|---|
| switchMap | YES | No | Search, navigation |
| mergeMap | No | No | Parallel independent calls |
| concatMap | No | YES | Sequential ordered calls |
| exhaustMap | Ignores new | No | Prevent double-submit |

`concatMap` — waits for each inner Observable to complete before starting the next. Results come in order. Use for sequential file uploads, ordered processing steps.

`exhaustMap` — if an inner Observable is already running, it IGNORES any new outer emissions. Use for save/login buttons — the first click fires, subsequent clicks are dropped until the first one finishes.

You won't use concatMap and exhaustMap every day, but recognising when you need them will save you from subtle bugs.

Now let's move to the second file — `02-rxjs-subjects-and-memory.ts`."

---

## Segment 11 — Subject (8 min)

*Point to Section 1 in `02-rxjs-subjects-and-memory.ts`*

"A Subject is simultaneously an Observable AND an Observer. It's both a producer AND a consumer.

Because of that dual nature, you can manually push values into it:
```typescript
const courseRefresh$ = new Subject<void>();
courseRefresh$.next(); // Push a value in
```

And subscribe to it like any other Observable:
```typescript
courseRefresh$.subscribe(() => { /* reload courses */ });
```

This makes Subject the perfect event bus — one part of your app fires it, another part listens.

But there's a critical limitation:

```typescript
const lateSubject$ = new Subject<string>();
lateSubject$.next('Early value — nobody listening yet');

lateSubject$.subscribe((val) => console.log(val));
lateSubject$.next('This value arrives after subscription');
// Output: This value arrives after subscription
```

The 'Early value' is lost. A Subject has no memory. Late subscribers see nothing from the past.

> **❓ Question:** If you use a Subject for 'is the user logged in?' state, what happens when a component initialises 2 seconds after login?
> *(Answer: The component subscribes after the login event was emitted, so it never knows the user is logged in — a bug. This is exactly the problem BehaviorSubject solves)*"

---

## Segment 12 — BehaviorSubject (10 min)

*Point to Sections 2 and 3*

"BehaviorSubject solves the 'late subscriber' problem for STATE.

```typescript
private cartCount = new BehaviorSubject<number>(0);
```

Two differences from Subject:
1. Requires an initial value — `0` here
2. Always remembers the LATEST value

When any new subscriber joins:
```typescript
cartService.cartCount$.subscribe((count) => console.log('Cart count:', count));
```

They immediately receive the current value — whatever was last passed to `.next()`. Then they continue receiving future updates.

Let's trace through the CartService:
```typescript
cartService.addItem(); // Emits 1
cartService.addItem(); // Emits 2

// Subscribe NOW — immediately gets 2 (the current value)
cartService.cartCount$.subscribe(count => console.log(count));
// Output: 2

cartService.addItem(); // Emits 3
// Output: 3
```

This is perfect for any 'what is the current state?' question.

Now look at `asObservable()`:
```typescript
cartCount$: Observable<number> = this.cartCount.asObservable();
```

> **⚠️ Watch out — this pattern matters.** The raw `BehaviorSubject` has `.next()` — anyone with a reference to it can push values and corrupt state. By exposing `.asObservable()`, you give consumers a read-only view. They can subscribe and read, but they can't write. This enforces data flow in one direction: service in, components out.

In the `AuthService` example, see how we build derived Observables:
```typescript
isLoggedIn$ = this.currentUser$.pipe(map(user => user !== null));
isAdmin$ = this.currentUser$.pipe(map(user => user?.roles.includes('admin') ?? false));
```

These automatically stay in sync whenever `currentUser$` changes. No manual updates needed."

---

## Segment 13 — ReplaySubject (5 min)

*Point to Section 4*

"`ReplaySubject(bufferSize)` is like BehaviorSubject, but instead of replaying 1 value, it replays the last N values.

```typescript
const recentNotifications$ = new ReplaySubject<string>(3);

recentNotifications$.next('Course updated');
recentNotifications$.next('Grade: 95%');
recentNotifications$.next('New course available');
recentNotifications$.next('System maintenance'); // Pushes oldest out of buffer

recentNotifications$.subscribe(n => console.log('Notification:', n));
// Replays: 'Grade: 95%', 'New course available', 'System maintenance'
```

The buffer holds 3. When the 4th arrives, the first is dropped. A new subscriber sees the last 3 — 'Grade', 'New course', 'System maintenance'.

Summary table for all three:
- **Subject** — no replay, no initial value → events and triggers
- **BehaviorSubject** — replays 1 (latest), requires initial → current state
- **ReplaySubject(n)** — replays last n → recent history

Now the topic that trips up almost every Angular beginner: memory leaks."

---

## Segment 14 — Memory Leaks and Unsubscription (12 min)

*Point to Sections 5, 6, 7 in `02-rxjs-subjects-and-memory.ts`*

"Let's talk about the most common Angular performance bug: subscribing without cleaning up.

Read the bad example comment:
```typescript
ngOnInit(): void {
  interval(1000).subscribe((n) => {
    this.count = n;
  });
}
// No ngOnDestroy
```

`interval(1000)` emits forever. The user opens this component, the subscription starts. The user navigates away — Angular destroys the component. But the subscription? Still alive. It keeps firing every second, trying to set `this.count` on a component that no longer exists. Over time, if the user navigates back and forth, you accumulate more and more of these zombie subscriptions.

This is a **memory leak** and it can also cause **ExpressionChangedAfterItHasBeenCheckedError** crashes.

**Three ways to fix it:**

---

**Pattern 1 — Manual Subscription container:**
```typescript
private subscriptions = new Subscription();

ngOnInit(): void {
  const sub = interval(1000).subscribe(...);
  this.subscriptions.add(sub);
}

ngOnDestroy(): void {
  this.subscriptions.unsubscribe(); // Cancels ALL at once
}
```

This is the Angular ≤15 standard approach. Still completely valid. Works everywhere.

---

**Pattern 2 — `takeUntilDestroyed()` (Angular 16+, recommended):**
```typescript
private destroyRef = inject(DestroyRef);

ngOnInit(): void {
  interval(1000).pipe(
    takeUntilDestroyed(this.destroyRef)
  ).subscribe(...);
}
// No ngOnDestroy needed
```

`takeUntilDestroyed` automatically completes the Observable when Angular destroys the component. Zero boilerplate. This is the modern best practice.

---

**Pattern 3 — `async pipe` (best for template-only Observables):**

No subscription in the component class at all:
```html
<p>Cart items: {{ cartService.cartCount$ | async }}</p>
```

The async pipe subscribes when the component renders and automatically unsubscribes when it's destroyed. It also triggers change detection when new values arrive.

> **⚠️ Watch out:** You need to import `AsyncPipe` in standalone components, or have `CommonModule` imported in NgModule-based components. Forgetting this is a common source of 'No pipe found' errors.

> **❓ Question:** When would you choose `takeUntilDestroyed` over the async pipe?
> *(Answer: When you need to use the Observable's value in component logic — for example, storing it in a local array, performing calculations, triggering router navigation. If it only ever goes to the template, async pipe is cleaner)*"

---

## Segment 15 — async Pipe Deep Dive (8 min)

*Point to Section 8 in `02-rxjs-subjects-and-memory.ts`*

"Let's look at the async pipe in templates more carefully.

Basic usage:
```html
<p>Cart items: {{ cartService.cartCount$ | async }}</p>
```

The `async` pipe unwraps the Observable value and updates the template automatically. New value emitted → template re-renders.

The `as` syntax for multiple bindings:
```html
<ng-container *ngIf="authService.currentUser$ | async as user">
  <p>Welcome, {{ user.name }}!</p>
</ng-container>
```

This subscribes once and assigns the value to the `user` local variable. You can use `user.name`, `user.email`, etc. inside that `ng-container` block.

> **⚠️ Watch out:** If you write `{{ myObs$ | async }}` twice in the template, Angular subscribes TWICE — two separate subscriptions. For HTTP Observables, this fires TWO HTTP requests. 
> Fix: use the `as` syntax to subscribe once:
> ```html
> <ng-container *ngIf="courses$ | async as courses">
>   <div *ngFor="let c of courses">...</div>
> </ng-container>
> ```

The loading pattern:
```html
<ng-container *ngIf="courses$ | async as courses; else loading">
  <div *ngFor="let c of courses">{{ c.title }}</div>
</ng-container>
<ng-template #loading>
  <p>Loading...</p>
</ng-template>
```

This shows 'Loading...' while the Observable hasn't emitted yet, then switches to the course list. Clean, declarative, no manual loading flags needed."

---

## Segment 16 — Summary and Q&A (7 min)

"Let's close out Day 19b with a big picture recap.

**Part 1:** HttpClient is Angular's HTTP layer. `http.get/post/put/delete` all return Observables. HttpParams builds query strings safely. HttpHeaders attaches tokens. Interceptors give you one place to handle cross-cutting concerns: auth, logging, error handling.

**Part 2:** RxJS is the reactive programming engine. Operators let you transform, filter, combine, and flatten Observable streams. The four flattening operators — switchMap, mergeMap, concatMap, exhaustMap — solve different concurrency problems. Subjects let you push values into a stream. BehaviorSubject holds state. ReplaySubject replays history. Always clean up subscriptions with async pipe, takeUntilDestroyed, or a Subscription container.

These two topics together are the foundation of all real-world Angular data flow. HTTP brings data in. RxJS operators shape it. The async pipe delivers it to your template. Clean, reactive, and memory-safe."

---

**Q&A Prompts:**

1. "If you have a component that subscribes to `interval(500)` using `takeUntilDestroyed()`, but also subscribes to a `BehaviorSubject` from a shared service — does the BehaviorSubject subscription also get cleaned up by `takeUntilDestroyed`?"
   *(Answer: Yes — any subscription that has `takeUntilDestroyed(this.destroyRef)` in its pipe will be cancelled when the component is destroyed, regardless of what Observable it came from)*

2. "What's the difference between putting `catchError` inside the switchMap vs outside it?"
   *(Answer: Inside — only that one inner search fails; the outer stream stays alive. Outside — the entire pipeline terminates on the first error)*

3. "You're loading a user profile, their recent orders, and their cart all when the dashboard loads. Which operator would you use — switchMap or mergeMap? Why?"
   *(Answer: mergeMap — all three are independent and should run in parallel. switchMap would cancel the first two when the third fires)*

4. "Why does Angular's async pipe subscribe twice if you use it twice on the same Observable?"
   *(Answer: Each `| async` creates its own subscription. The Observable is cold — each subscriber triggers independent execution)*

5. "What is `getValue()` on a BehaviorSubject and when should you use it?"
   *(Answer: It reads the current value synchronously. Use it in route guards, resolvers, or anywhere you need the value without subscribing — but only in imperative code, not in reactive chains)*

---

**Take-Home Exercises:**

1. **HTTP Service** — Create an Angular service that wraps a public REST API (e.g. JSONPlaceholder). Implement GET list, GET by ID, POST, and DELETE methods with proper error handling.

2. **Auth Interceptor** — Write a functional interceptor that reads a token from localStorage and attaches it to every request, but skips requests to `/auth/login` and `/auth/register`.

3. **Search Component** — Build a standalone Angular component with a search input that uses `fromEvent`, `debounceTime`, `distinctUntilChanged`, and `switchMap` to call a search service.

4. **Cart State Service** — Create a `CartStateService` using `BehaviorSubject<CartItem[]>` that exposes `cartItems$`, `cartTotal$` (derived Observable), `addItem()`, `removeItem()`, and `clearCart()` methods. Connect it to two components — one with the cart icon (item count) and one with the cart drawer.

---

*End of Part 2 Script*

---

*End of Day 19b — Angular HTTP & RxJS*
