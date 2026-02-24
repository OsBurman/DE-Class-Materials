# Day 20b Part 1 — Angular Signals
## Lecture Script

**Total time: 60 minutes**
**Slides: 16**
**Pace: ~165 words/minute**

---

### [00:00–03:00] Slide 1 — Opening

Good morning everyone. Last week we wrapped up the React track with advanced patterns and deployment — render props, compound components, memoization, code splitting, and getting your app into production. Today is Day 20b, the final day of Week 4, and we're closing out the Angular track with two important topics. Part 1 is Angular Signals — a new reactive primitive that Angular introduced in version 16. Part 2 this afternoon is testing in Angular — Jasmine, Karma, and TestBed.

Let me tell you why I'm excited about signals specifically. When Angular was first built, it leaned heavily on RxJS Observables for reactivity. Observables are powerful, but for something as simple as "I have a counter, and I want the template to update when it changes," they require a lot of ceremony — BehaviorSubject, asObservable, async pipe, remember to unsubscribe. Signals solve that specific problem cleanly.

By the end of Part 1, you'll be able to create reactive state with `signal()`, derive values automatically with `computed()`, respond to changes with `effect()`, and know exactly when to reach for a Signal versus an Observable. Let's dive in.

---

### [03:00–11:00] Slides 2–3 — Why Signals and What They Are

**[Slide 2]**

Let me show you the problem Signals solve. Here's the old way to hold a simple counter in an Angular service. You create a private `BehaviorSubject`, a public Observable via `.asObservable()`, and an `increment` method that reads the current value with `.getValue()` and then calls `.next()` to push a new one. That's five lines of TypeScript just to hold the number zero.

In the component, you store the Observable reference, and in the template you use the `async` pipe. And here's the subtle trap with `async` — it subscribes for you and unsubscribes when the component destroys, which is great, but if you forget the `async`, you render an Observable object instead of a number, and the bug is silent.

Now multiply that pattern across every piece of state in your app — user object, cart items, filter settings, UI flags. It gets verbose fast. And on top of that, zone.js is running change detection across the entire component tree on every browser event — every click, every keystroke — because it can't know exactly which component changed.

Signals are Angular's answer: synchronous, fine-grained reactivity with no subscription management.

**[Slide 3]**

Let me give you the mental model. A Signal is a reactive value container. Three things define it.

First: it holds a value. Always synchronous. Always has one. Unlike an Observable that might not emit for a while, a signal always has a current value you can read right now.

Second: it notifies dependents. Whenever the value changes, anything that was reading that signal gets updated automatically.

Third: it tracks who reads it. This is the automatic dependency tracking. When you read a signal inside `computed()` or `effect()`, Angular registers that dependency silently — you don't have to declare it.

Look at this comparison table. A regular variable is read as `count` — fast to type, zero reactivity. A BehaviorSubject is read via subscribe or the async pipe — reactive but asynchronous and verbose. A Signal is read as `count()` — calling it like a function — reactive and synchronous, no subscription needed.

The analogy I love is Excel. If cell A1 contains a value and cell B1 has the formula `=A1 * 2`, whenever A1 changes, B1 updates automatically. You didn't write any event listener. You didn't subscribe to A1. B1 just stays derived. That's exactly what `computed()` does in Angular, and we'll see that in a few slides.

---

### [11:00–21:00] Slides 4–5 — Creating, Reading, and Writing Signals

**[Slide 4]**

Creating a signal is one line. You import `signal` from `@angular/core`, call it with an initial value, and TypeScript infers the type. `signal(0)` gives you a `WritableSignal<number>`. `signal('Alice')` gives you a `WritableSignal<string>`. You can also pass an explicit generic when the type isn't obvious — `signal<User | null>(null)` tells TypeScript this can be a User object or null.

Reading a signal is the interesting part. You call it like a function — `count()`. That's not just syntactic sugar. The parentheses do two things: they return the current value, AND they register whoever is calling as a dependent. If you read `count()` inside a `computed()` function, Angular says "okay, this computed value depends on count — if count changes, rerun this computed." If you read it in a template, Angular says "this view depends on count — if count changes, update this template." All automatic.

In templates it's the same syntax. No async pipe. `{{ count() }}`, `{{ user()?.displayName ?? 'Guest' }}`. The optional chaining and nullish coalescing work exactly like you'd expect because you're just getting a regular value back.

**[Slide 5]**

Now writing. There are two methods on a writable signal.

`.set()` replaces the value entirely. `count.set(10)` — done, the value is now 10. Simple.

`.update()` computes the new value from the current one. You pass a function that receives the current value and returns the new value. `count.update(c => c + 1)` — increment. `count.update(c => c * 2)` — double it. This is particularly useful for arrays.

And here's the critical rule with arrays and objects: when you update them, you must return a new reference. Signals use `===` to detect changes. If you mutate the existing array in place — `list.push(item)` and return the same array — the signal sees "same reference, nothing changed" and doesn't notify anyone. The correct pattern is spread: `items.update(list => [...list, newItem])`. Create a new array with the existing items plus the new one.

There was a `.mutate()` method in early Angular 16 that let you mutate in place, but it was removed in Angular 17.1 because it caused exactly these kinds of silent bugs. Use `.update()` with spread.

Finally, `.asReadonly()` — this is a pattern you'll use in services. You keep the writable signal private inside the service, and expose a read-only view to consumers. The read-only view is exactly the same signal but without `.set()` or `.update()`. Components can read it, they just can't write to it. Clean encapsulation.

---

### [21:00–31:00] Slides 6–7 — computed()

**[Slide 6]**

Let's talk about `computed()`. This is one of my favorite APIs in Angular right now because it makes derived state effortless.

Here's the scenario: a shopping cart. You have items — a signal holding an array of CartItems. You have a discount rate — another signal, starting at ten percent. Now you want a subtotal: the sum of price times quantity for every item. And you want a total: subtotal minus the discount.

The old way would have you listening for changes on `items$`, piping with `map`, doing the same for discount, `combineLatest`-ing them together — it's a chain of RxJS operators.

With `computed()`, you write `const subtotal = computed(() => items().reduce(...))`. Angular reads that function, sees that you called `items()` inside it — that read registers `items` as a dependency — and whenever `items` changes, `subtotal` recalculates. Then `total` is `computed(() => subtotal() * (1 - discount()))`. Angular sees reads of both `subtotal` and `discount`, so `total` recalculates when either changes.

Watch the demo values: you add a laptop at $999. `subtotal()` is now 999. `total()` is 899.10. Then you call `discount.set(0.20)`. The items didn't change, but `total()` is now 799.20 — because it depends on discount. You never explicitly recalculated total. Angular did it for you.

In templates: `{{ total() | currency }}`. Same syntax as any signal read.

**[Slide 7]**

Let me go through the four properties of `computed()` because they matter for how you design your state.

Lazy. A computed value is not evaluated until someone reads it. Angular doesn't compute values that nothing is looking at. This is good for performance — if a computed panel is hidden, its computation doesn't run.

Memoized. Once computed, the result is cached. If you read `subtotal()` fifty times in a loop, the reduce function runs once. It only reruns when a tracked dependency actually changes. This makes computed safe to use generously.

Read-only. `computed()` returns a `Signal<T>`, not a `WritableSignal<T>`. You cannot call `.set()` or `.update()` on it. The value is entirely determined by the function you gave it.

No async. You cannot do HTTP calls or Promises inside computed. Computed is a pure synchronous function. If you need async derived data — like "when the user ID signal changes, fetch the user from the API" — that's an effect or a service method, which we'll cover.

When should you use `computed()`? Any time you have a value that is derived from other signals. Filtered lists. Sorted arrays. Sums and aggregates. Formatted display strings. Boolean flags like `isEmpty` or `isValid`. Always prefer `computed()` over manually updating a signal when another changes.

---

### [31:00–41:00] Slides 8–9 — effect()

**[Slide 8]**

Now `effect()`. Effects are for running side effects in response to signal changes. Where `computed()` is for deriving values, `effect()` is for doing things — persisting to storage, logging, DOM operations.

The mechanism is the same: you write a function, Angular runs it once, records which signals were read, and re-runs it whenever any of them change.

In this example, the effect reads `this.items()`. Angular records "items is a dependency." Whenever `items` changes — an item added, one removed, quantities changed — the effect re-runs. The re-run logs the new count and writes the new items to localStorage.

The second example shows cleanup. Some effects create resources — an interval, a subscription, an event listener. You don't want those to pile up. The `onCleanup` callback runs right before the next execution and when the component destroys. You clear the old interval before setting up the new one.

The lifecycle is: create effect → runs once → dependency changes → cleanup callback → effect runs again → component destroys → cleanup callback → done.

**[Slide 9]**

Let me talk about the rules, because there are a couple of traps.

The big one: do not set signals inside effects. This creates an infinite loop. Effect reads count, count changes, effect runs again, sets count, count changes, effect runs again, forever. If you want a value that's derived from another signal, use `computed()`. That's what it's for.

The other rule: effects must be created in an injection context. That means the constructor, a class field initializer, or inside `runInInjectionContext`. Angular needs an injection context to tie the effect's lifetime to the component or service — so it gets destroyed when the host destroys. If you call `effect()` in a regular method, Angular throws an error. The fix is `runInInjectionContext(this.injector, () => effect(...))` but you'll rarely need that.

When should you use effects? Syncing to localStorage or sessionStorage is the classic one. Logging analytics events when state changes. Imperatively managing focus — when a modal opens, programmatically focus the close button. Bridging to non-Angular code — a third-party chart library that needs you to call `.update()` imperatively whenever data changes. Starting and stopping an interval based on a condition.

And a clear "when not to": if you catch yourself writing `effect(() => { this.someSignal.set(derived value) })`, stop. That's a computed signal.

---

### [41:00–49:00] Slides 10–11 — Signals in Components and Services

**[Slide 10]**

Let's see it all together in a complete component. This is a standalone component — standalone because it's Angular 14+, which we've been using throughout.

The private `items` signal holds the array. `itemCount` and `total` are computed values derived from it. In the template, we read all three by calling them as functions. The buttons call `addItem()` and `reset()`. Notice the disabled binding: `[disabled]="itemCount() === 0"` — that expression reads the signal, so Angular knows to re-evaluate the disabled state whenever `itemCount` changes. No manual change detection, no `markForCheck()`, none of that.

Two side notes I want to call out. First: this works automatically with `ChangeDetectionStrategy.OnPush`. OnPush normally means "only re-render when inputs change," but signals opt out of that restriction — Angular tracks signal reads directly and updates just the components that need it. Second: Angular 18 shipped experimental zoneless support. With signals, you don't need zone.js for change detection at all. The signals themselves tell Angular what changed. This is the future direction of the framework.

**[Slide 11]**

Now signals in a service — this is where shared state lives.

The pattern mirrors what we did with BehaviorSubject before, but cleaner. `items` is private — a `WritableSignal`. Only the service can call `.set()` and `.update()` on it. The public surface is read-only: `items$` via `.asReadonly()`, `count` and `total` and `isEmpty` as computed values.

Components inject `CartService` and read `cartService.count()`, `cartService.total()` — synchronously, no subscribe, no async pipe. When a component calls `cartService.addItem()`, the service updates the private signal, which triggers recomputation of count, total, isEmpty, and Angular updates every template that was reading those values. One write, automatic propagation everywhere.

This pattern — private writable signal, public read-only computed surface — is the canonical way to use signals in Angular services.

---

### [49:00–56:00] Slides 12–13 — Interop and Signal Inputs

**[Slide 12]**

Signals and Observables are not in competition. They're complementary. Angular's own `HttpClient` returns Observables and that's not changing. RxJS operators like `debounceTime`, `switchMap`, `retry` are genuinely useful for async flows. Signals are great for state. The interop bridge connects both worlds.

`toSignal()` converts an Observable to a Signal. The most common use case: HTTP calls. You call `this.productService.getProducts()` — that returns an Observable. Wrap it in `toSignal()` with an `initialValue` of an empty array, and now `products` is a signal. In the template: `products()`. No async pipe. The initial value is what the signal holds before the Observable emits, so you never have `null` or undefined in your template during loading.

`toObservable()` goes the other direction. You have a `searchTerm` signal that updates on every keystroke. You want to debounce that before hitting the API. Signals don't have a debounce primitive, but RxJS does. `toObservable(this.searchTerm)` gives you an Observable that emits whenever the signal changes. Now pipe it: debounce 300ms, distinct until changed, switchMap to cancel previous searches, done.

This is the pattern in practice: signals for state, Observables for async pipelines, and `toSignal()`/`toObservable()` as the bridge between them.

**[Slide 13]**

One more modern API: signal inputs. Angular 17 introduced `input()` and `model()` as signal-based alternatives to `@Input()`.

`input()` returns an `InputSignal<T>`. Reading it is `this.user()` — same as any other signal. This matters because you can use it directly in `computed()`: `greeting = computed(() => 'Hello, ' + this.user().name + '!')`. Whenever the parent changes the `user` input, the computed updates automatically.

`input.required<User>()` makes the input required — Angular throws a compile error if the parent doesn't provide it.

`model()` is for two-way binding. It creates a `ModelSignal<T>`. The component can both read and write to it. The parent gets notified when the component writes a new value via the `[(value)]` banana-in-a-box syntax. This replaces the `@Input() + @Output() valueChange` pattern with a single declaration.

You don't have to migrate all your existing `@Input()` decorators immediately. The decorator approach still works. But for new components, signal inputs give you tighter integration with the signals ecosystem.

---

### [56:00–60:00] Slides 14–16 — Comparison and Summary

**[Slide 14]**

Let's put Signals and Observables side by side. Value access: signals are synchronous, you call `count()`. Observables require subscribe or the async pipe. Always has a value: signals, yes always. Observables only if you use `BehaviorSubject` or `startWith`. Derived values: `computed()` versus `pipe(map(...))`. Lazy: `computed()` is lazy. Observables are also lazy. Cancellation: signals don't cancel — that concept doesn't apply. Observables absolutely do — unsubscribe and the work stops. Async and HTTP: signals are not directly async, Observables are designed for it. Operators: no operators for signals, full RxJS library for Observables. Boilerplate: signals win hands-down for state management.

**[Slide 15]**

The decision guide. Use signals when you're managing component UI state — toggles, counters, form field values. Use signals for shared state across components via a service — cart, auth, theme. When the value is synchronous and always available, signals are the right choice.

Use Observables when you're making HTTP requests — that's what HttpClient returns. Use Observables when handling events streams. Use them when you need operators. Use them when you need cancellation.

And the rule of thumb I want you to keep: Signals for state, Observables for events and async data flows. In most real Angular apps you'll use both, connected by `toSignal()` and `toObservable()`.

**[Slide 16]**

Let me leave you with the full API table. `signal(value)` — creates a `WritableSignal`, read with `()`, write with `.set()` and `.update()`. `computed(fn)` — creates a read-only `Signal`, lazy and memoized. `effect(fn)` — runs side effects reactively, needs injection context. `input()` and `model()` — signal-based component inputs. `toSignal()` and `toObservable()` — the interop bridge.

Three rules to tattoo on your brain: Read signals with `()`. Never set a signal inside `effect()`. When updating arrays, always return a new reference.

After lunch we'll move into testing — Jasmine, Karma, and TestBed. You'll see that testing signal-based components is actually very natural. Take a ten minute break and I'll see you back here for Part 2.
