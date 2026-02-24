# Day 20b — Part 1 Walkthrough Script
# Angular Signals (Angular 16+) — Morning Session

**File referenced:** `01-signals.ts`  
**Total time:** ~90 minutes  
**Format:** Instructor-led code walkthrough — file open in editor

---

## Segment 1 — Week 4 Recap & The Problem Signals Solve (8 min)

> "Good morning everyone. We're in the final day of Week 4, and we've covered a lot of ground — Angular fundamentals, services, dependency injection, routing, forms, HTTP, and RxJS. Today we close out Angular with two powerful topics: Signals and Testing."

> "Before we write any code, I want to explain *why* Signals exist. What problem were the Angular team trying to solve?"

> "Here's the story. Angular has always used Zone.js for change detection. Zone.js patches browser APIs — setTimeout, addEventListener, HTTP requests — so that after any async event, Angular can check: did anything change? Should I re-render?"

> "This works, but it has a cost. When Zone.js fires, Angular does a change detection pass across your ENTIRE component tree — even components that have nothing to do with what just changed. In a large app, that's expensive."

> "Angular 16 introduced Signals as a fundamentally different approach. Instead of asking 'did anything change anywhere?' after every event, Signals let Angular know *exactly* which values changed and *exactly* which parts of the template depend on them. Only those nodes are updated. Everything else is left alone."

> "This is called fine-grained reactivity, and it's the same model used by SolidJS and Vue 3's Composition API."

> "Signals are NOT a replacement for everything Angular — they coexist with Observables and Zone.js. They're the right tool for synchronous UI state. We'll cover exactly when to use each at the end of Part 1."

> "Let's open `01-signals.ts` and work through it."

---

## Segment 2 — Section 1: What Is a Signal? (5 min)

*Navigate to Section 1 comment block*

> "Read through the Section 1 comment with me. Key bullet: a Signal ALWAYS holds a value. It's never undefined unless you typed it that way. That's different from an Observable, which might not emit for a while."

> "Reading a signal is just a function call — `mySignal()`. That's it. No `.value` property, no `.subscribe()`. Just call it like a function."

> "The other key point: writing a signal tells Angular exactly what changed. Angular can then surgically update only the DOM nodes that depended on that signal."

> "Three things to remember: a signal holds a value, you read it by calling it, and writing it triggers smart updates. Let's see it in code."

---

## Segment 3 — Section 2: Creating and Using Signals (15 min)

*Navigate to the `courseTitle` signal*

> "Here's the simplest possible signal. `signal('Angular Signals Deep Dive')` creates a `WritableSignal<string>`. The type is inferred from the initial value — Angular knows this is a string signal."

> "To read it: `courseTitle()`. Notice the parentheses — it's a function call, not a property access. This trips people up at first. Why a function call? Because when Angular evaluates `courseTitle()` inside a template or a computed, it needs to register that location as a dependency. A plain property access can't do that."

> "**Watch out:** If you write `courseTitle` without the parentheses in your template, you'll render `[object Object]` or the function itself — not the value. Always include `()`."

*Point to `.set()` and `.update()`*

> "Two ways to write a signal. `.set()` replaces the value entirely — use this when you have the new value in hand. `.update()` takes a callback that receives the current value and returns the new value — use this when the new value depends on the old one."

> "Question for the class: if I have a counter signal and I want to increment it, which should I use — `.set()` or `.update()`?"

*(Expected: .update(), since the new value = old + 1)*

> "Exactly. `.set(count() + 1)` would technically work but it's an anti-pattern — you're reading and writing in two separate operations. `.update(n => n + 1)` is atomic and expressive."

*Navigate to the `selectedCourse` object signal*

> "Here's where beginners make their most common mistake. Say you have an object signal and you want to update just the rating. You might try: `selectedCourse().rating = 4.9`. DON'T do this."

> "Why? Because you're mutating the object in place. Angular's Signal system detects changes by comparing references. If you mutate the object, the reference hasn't changed, so Angular doesn't know anything is different."

> "**Watch out:** Always return a NEW object or array from `.update()`. Spread the old value with `{ ...course }` and change what you need. Same rule as Redux reducers — immutability is your friend."

*Navigate to `courseList` array signal*

> "Same principle for arrays. Never `.push()` into a signal's array. Always spread: `[...list, newItem]`. Push mutates — spread creates a new reference."

*Navigate to `.asReadonly()`*

> "Finally — `.asReadonly()`. This is how services expose signals publicly without allowing callers to write to them. The service keeps the `WritableSignal` private and exposes only the `Signal<T>` read-only view. TypeScript enforces this — if you try to call `.set()` on a read-only signal, you get a compile error."

---

## Segment 4 — Section 3: Computed Signals (15 min)

*Navigate to Section 3*

> "Computed signals are derived, read-only signals. You describe a relationship — 'this value equals some transformation of other signals' — and Angular figures out the rest."

> "Two signals: `price` and `discountPercent`. One computed: `discountedPrice`. Look at the lambda — it reads both `price()` and `discountPercent()`. Angular registers both as dependencies."

> "Ask yourself: when I change `price`, what should happen? The computed should update. When I change `discountPercent`? Also update. Angular does this automatically — you never call `.update()` on a computed signal, you just declare the relationship."

> "Let's follow the output. Initial: 199.99 × 0.80 = 159.99. We change price to 249.99: 249.99 × 0.80 = 199.99. We change discount to 30%: 249.99 × 0.70 = 174.99. Each change propagates through the dependency graph automatically."

> "**Watch out:** You cannot `.set()` or `.update()` a computed signal. TypeScript will stop you with an error. Computed signals are always derived — if you want to manually control a value, use a plain `signal()` instead."

*Navigate to the `publishedCourses` and `averageRating` chain*

> "Here's a more realistic example — chained computed signals. `publishedCourses` filters the `courses` array to only published ones. `averageRating` reads `publishedCourses()`, which itself reads `courses()`."

> "This creates a dependency chain: `averageRating` depends on `publishedCourses` depends on `courses`. When I update `courses`, Angular re-evaluates the chain automatically, starting from the bottom."

> "And it's memoized — if `courses` hasn't changed since the last read, Angular won't re-run the `publishedCourses` filter. Computed signals only recompute when needed. This is the 'lazy' part."

> "Question: we have 3 courses. Two are published (ids 1 and 3). Average rating of 4.8 and 4.7 is 4.75. Then we publish course 2 (rating 4.9). What's the new average?"

*(Expected: (4.8 + 4.7 + 4.9) / 3 = 4.8)*

> "Right — and the code confirms this. The computed cascade just works."

---

## Segment 5 — Section 4: Effects with Signals (15 min)

*Navigate to Section 4*

> "Computed signals are for values — they produce something. Effects are for side effects — they DO something. Logging, writing to localStorage, making API calls, manipulating the DOM."

> "The rule: if your reactive code produces a value, use computed(). If it performs an action, use effect()."

*Navigate to `loggingEffect`*

> "Simplest effect: log whenever something changes. Notice the effect reads both `searchQuery()` and `isLoading()`. Angular tracks both as dependencies. Any time either changes, this function runs."

> "Effects run ONCE immediately when created. This is how Angular establishes the initial dependency set — it has to call the function at least once to know what signals it reads."

> "After that, every time a dependency changes, the effect re-runs synchronously before the next render."

*Navigate to `themeEffect`*

> "Here's a practical use case: syncing a signal to localStorage. This is a clean pattern — every time `theme` changes, we write to storage. The component doesn't have to remember to call localStorage anywhere else."

*Navigate to `cleanupEffect`*

> "This is the most important pattern for effects: cleanup. Some effects set up resources — intervals, event listeners, subscriptions — that need to be torn down before the next run."

> "The `onCleanup` callback is the mechanism. Angular calls it: (a) right before the effect re-runs, and (b) when the effect is destroyed."

> "Look at the sequence: we create a 1000ms interval. We change `timerInterval` to 500ms. Angular: 1) calls `onCleanup` → clears the old interval, 2) runs the effect body → starts the new 500ms interval. No lingering timers."

> "**Watch out:** If you set up an interval or event listener inside an effect and don't use `onCleanup`, you'll create a new one on every effect run. After 10 changes you have 10 parallel intervals. Always clean up."

*Navigate to the component context comment*

> "In a real Angular component, effects belong in the constructor. Angular automatically destroys all component effects when the component is destroyed — you don't have to call `.destroy()` yourself. This is much simpler than managing RxJS subscriptions manually."

---

## Segment 6 — Section 5: Signals vs Observables (12 min)

*Navigate to Section 5*

> "This is the question everyone asks: 'Do Signals replace Observables?' The short answer: no. They solve different problems. Let's see exactly when to use each."

*Read through the bullet points together*

> "Signals are perfect for: synchronous state that lives in components. A selected item. A counter. A toggle. A search query string. Values that are always defined, always readable, that drive the UI directly."

> "Observables are perfect for: streams of values over time. An HTTP response. WebSocket messages. A timer that fires every second. Router events. Form valueChanges. These things are fundamentally asynchronous — they might not have a value yet, they might emit multiple times, and you often need to transform them with operators."

*Navigate to the side-by-side comparison*

> "Look at the Observable approach. Notice what you need: BehaviorSubject, async pipe in the template, Subject for cleanup, ngOnDestroy. Three extra concepts just to hold a title string."

> "Now the Signal approach: just `title = signal('Angular')`. Call it in the template without async pipe. Destroy is handled automatically. Same reactive behavior, half the code."

> "This is why Angular recommends Signals for UI state going forward. Not because Observables are bad — but because Signals are a much better fit for the 'hold a value, react when it changes' use case."

*Navigate to `toSignal` and `toObservable`*

> "The bridge functions are the key insight. In a real app, data often starts as an Observable — HTTP returns an Observable, the Router gives you Observables. You can convert those to Signals at the boundary with `toSignal()`."

> "This means you keep Observables for what they're good at — async streaming, RxJS operators — and convert to Signals at the component level for clean template binding. The two systems coexist."

*Navigate to the decision tree*

> "Here's your cheat sheet. Synchronous UI state → Signal. Async source → Observable. Need RxJS operators → Observable. Simple derived value → Computed signal."

> "I want everyone to bookmark this decision tree. In Week 6 when we build Spring Boot APIs, your Angular frontend will use Observables for HTTP (because HttpClient returns them) and Signals for the component state that drives your UI. They'll live side by side."

---

## Segment 7 — Live Coding Challenge (10 min)

> "Before we break, I want you to write something from scratch. This is a 10-minute challenge."

> "Build a `ShoppingCartService` concept using signals. You need:"

> "One: a `cartItems` signal — array of `{ id: number, name: string, price: number, quantity: number }`"

> "Two: a computed `totalItems` — sum of all quantities"

> "Three: a computed `totalPrice` — sum of (price × quantity) for all items"

> "Four: an `addItem` method using `.update()`"

> "Five: a `removeItem(id)` method using `.update()`"

> "Six: an effect that logs the cart total whenever it changes"

*(Give 8 minutes, then walk through a solution)*

> "Let's look at a solution. The key things to verify: `.update()` with a spread for addItem, the `.filter()` for removeItem, the two computed signals reading `cartItems`, and the effect reading `totalPrice`."

---

## Segment 8 — Part 1 Wrap-Up (5 min)

> "Three things to take away from this morning:"

> "One: Signals are Angular's reactive primitive for UI state. `signal()`, `computed()`, `effect()` — that's the whole API."

> "Two: Immutability. When you write to a signal that holds an object or array, always create a new reference. Spread, don't mutate."

> "Three: Signals and Observables coexist. Use Signals for synchronous state. Use Observables for async streams. Use `toSignal()` to bridge them when needed."

> "After lunch we're going into Angular testing — Jasmine, Karma, TestBed, and how to test components, services, and HTTP calls. See you in an hour."

---

## Instructor Q&A Prompts

Use these to spark discussion or fill time:

1. **"What would happen if you called `.set()` on a computed signal? What TypeScript error would you see?"**  
   *(Expected: TypeScript error — `Signal<T>` has no `.set()` method, only `WritableSignal<T>` does)*

2. **"An effect reads three signals: A, B, and C. You update only signal B. Does the effect re-run?"**  
   *(Expected: Yes — any dependency change triggers the effect)*

3. **"What's the difference between using a computed signal and just calling a regular method in the template — `{{ getTotal() }}`?"**  
   *(Expected: computed is memoized — only recomputes when deps change. A method call runs on every change detection cycle)*

4. **"When would you use `toSignal()` instead of the async pipe?"**  
   *(Expected: when you want to use the value in a computed or effect — async pipe only works in templates)*
