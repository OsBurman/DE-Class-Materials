# Angular Signals & Testing — EXPANDED PRESENTATION
### One concept per slide throughout

Total slides: 46

---

═══════════════════════════════════════════
PART 1: SIGNALS IN ANGULAR
═══════════════════════════════════════════

---

[SLIDE 1: Title Slide]
Title: "Angular Signals & Testing"
Subtitle: "Reactive State Management + Writing Reliable Tests"
Your name, date, course name

SCRIPT:
Good morning everyone. Today we're covering two big topics that are going to change how you think about building and maintaining Angular applications — Signals and Testing.
These two things might seem unrelated at first, but they actually complement each other really well. Signals change how we manage state in our components, and testing gives us the confidence that our state management and logic actually works the way we think it does.
By the end of this session, you should be able to create and use signals in a real component, write computed values and effects, bridge signals with Observables, and write unit tests for components, services, and HTTP calls using Jasmine and Karma.
Let's get into it.

---

[SLIDE 2: The Problem Signals Solve]
Title: "Before Signals — What We Were Doing"

The old way:
- RxJS BehaviorSubject for local state
- Manual subscribe() calls
- Manual unsubscribe() to prevent memory leaks
- async pipe in templates
- Unpredictable change detection cycles

The cost:
- Verbose boilerplate even for simple counters
- Easy to forget to unsubscribe → memory leaks
- Hard to track what depends on what

SCRIPT:
Before we look at signals, I want you to understand why they exist. Before Angular 16, if you wanted a piece of reactive state in a component — even something as simple as a counter — you were reaching for a BehaviorSubject from RxJS. You'd create it, subscribe to it in the template with the async pipe, and remember to unsubscribe when the component was destroyed or you'd get a memory leak.
For experienced developers that's workable. But for anyone learning Angular, or for any team that wants simple, readable code, it was a lot of overhead for very little gain on straightforward state.
Angular Signals were introduced to fix this. They give you reactive state that is simple, synchronous, and automatic — no subscriptions, no leaks, no ceremony.

---

[SLIDE 3: What Is a Signal?]
Title: "A Signal Is a Reactive Box"

Definition:
"A signal is a reactive primitive that holds a value and notifies consumers when that value changes."

Think of it like this:
- A box that holds a value
- Anyone who reads from the box is automatically tracked
- When the value changes, Angular knows exactly who was reading it
- Only those consumers get updated — nothing else

Key properties:
- Available since Angular 16
- Always has a value (never undefined unless you say so)
- Synchronous — no waiting, no async
- No manual subscriptions needed

SCRIPT:
Here's the mental model I want you to hold onto. A signal is a box. The box holds a value. Whenever a piece of your app reads from that box — a template, a computed value, an effect — Angular records that fact. It says "this template is reading from this signal." When the value in the box changes, Angular already knows exactly who needs to be updated, so it updates only those things. Nothing else gets touched.
This is fundamentally different from how change detection used to work, where Angular had to guess what changed by checking the whole component tree.
Three properties are the most important: signals are always synchronous, they always have a value, and you never need to subscribe to them or clean them up. Those three things together are what makes them so much simpler to work with.

---

[SLIDE 4: Creating a Signal]
Title: "How to Create a Signal"

```typescript
import { signal } from '@angular/core';

// Create a signal with an initial value
count = signal(0);

// Signals can hold any type
name = signal('Angular');
isOpen = signal(false);
items = signal<string[]>([]);
```

- Import signal from @angular/core
- Pass the initial value as the argument
- TypeScript infers the type — or you can provide it explicitly with signal<Type>()

SCRIPT:
Creating a signal is one line. Import signal from @angular/core, call it with the initial value, and you're done. Angular infers the type from what you pass in — so signal(0) gives you a Signal of number, signal('Angular') gives you a Signal of string, and so on.
If you need to be explicit about the type — especially useful for arrays or objects that start empty — you can write signal<string[]>([]) to tell TypeScript this will hold an array of strings even though it starts empty.
This is the entire creation API. There is nothing else to configure. No subject, no observable, no pipe.

---

[SLIDE 5: Reading a Signal]
Title: "Reading a Signal — Call It Like a Function"

```typescript
count = signal(0);

// ✅ Correct — call it with parentheses
console.log(this.count());   // 0

// In a template — same syntax
// <p>{{ count() }}</p>

// ❌ Wrong — this gives you the signal object, not the value
console.log(this.count);     // Signal object, not 0
```

Why the parentheses?
Angular uses that function call to track who is reading the value.
Every time you call count(), Angular records: "this context depends on count."
That's how it knows what to update when count changes.

SCRIPT:
This is the one thing that trips everyone up the first time, so I'm giving it its own slide. To read a signal's value you call it like a function — this.count() with parentheses. It is not a property you just access. It is a getter function.
The reason for this is intentional and important. That function call is how Angular tracks dependencies. When your template calls count(), Angular records that this template depends on the count signal. When count changes, Angular knows to re-render this template. If you just accessed this.count without the parentheses, Angular would have no way to track that dependency.
In templates it works exactly the same way — you write count() with parentheses, just like in TypeScript. This is consistent everywhere, which makes it easy to remember once you get past the initial surprise.

---

[SLIDE 6: Updating a Signal — set()]
Title: "Changing a Signal's Value with set()"

```typescript
count = signal(0);

// set() replaces the value entirely
this.count.set(5);
console.log(this.count()); // 5

this.count.set(0);
console.log(this.count()); // 0

// Use set() when you have the new value ready
// and it doesn't depend on the old value
```

When to use set():
- You know the exact new value already
- Resetting to a default
- Setting from user input (e.g. a form field value)

SCRIPT:
There are two ways to change a signal's value and they're used in different situations. The first is set(). You call set() with the new value and it replaces whatever was there. Simple.
Use set() when you already know exactly what the new value should be and it doesn't need to depend on what the current value is. Resetting a counter to zero, updating a name from a form field, toggling a flag to a specific state — those are all set() situations.

---

[SLIDE 7: Updating a Signal — update()]
Title: "Changing a Signal's Value with update()"

```typescript
count = signal(0);

// update() takes a function: current value → new value
this.count.update(val => val + 1);
console.log(this.count()); // 1

this.count.update(val => val + 1);
console.log(this.count()); // 2

// Works for any type
isOpen = signal(false);
this.isOpen.update(current => !current); // toggle

items = signal<string[]>([]);
this.items.update(list => [...list, 'new item']); // add to array
```

When to use update():
- The new value depends on the current value
- Incrementing, decrementing, toggling
- Adding or removing from an array

SCRIPT:
The second way to change a signal is update(). update() takes a function. That function receives the current value as its argument and returns the new value. Angular handles reading and writing for you.
Use update() whenever the new value depends on what's already there. Incrementing a counter, toggling a boolean, adding an item to an array — all of these need to know the current value before they can calculate the new one. update() is the right tool for all of them.
Notice the array example — items.update(list => [...list, 'new item']). The spread operator is important here. You're not mutating the existing array, you're returning a new one with the item added. Signals work best with immutable updates — always return a new value rather than modifying the existing one in place.

---

[SLIDE 8: Signals in a Component — The Template]
Title: "Using Signals in a Component Template"

```typescript
@Component({
  selector: 'app-counter',
  template: `
    <p>Count: {{ count() }}</p>
    <button (click)="increment()">+</button>
    <button (click)="decrement()">-</button>
    <button (click)="reset()">Reset</button>
  `
})
export class CounterComponent {
  count = signal(0);
  // ...
}
```

What happens in the template:
- count() is called — Angular tracks this template as a consumer of count
- When count changes, Angular re-renders only this template
- No async pipe, no subscription, no ChangeDetectorRef needed

SCRIPT:
Here's the template side of a counter component. The key thing to notice is count() with parentheses in the template — same as in TypeScript. Angular tracks that read, knows this template depends on the count signal, and re-renders it automatically whenever count changes.
There is no async pipe. There is no subscribe. There is no change detection configuration. Angular handles all of that for you because of the signal tracking mechanism. This is what makes signals so powerful in templates — the template declares its dependencies just by calling signal functions, and Angular takes care of the rest.

---

[SLIDE 9: Signals in a Component — The Methods]
Title: "Wiring Up Methods to Signal Updates"

```typescript
export class CounterComponent {
  count = signal(0);

  increment() {
    this.count.update(v => v + 1);  // depends on current value → update()
  }

  decrement() {
    this.count.update(v => v - 1);  // depends on current value → update()
  }

  reset() {
    this.count.set(0);              // exact value known → set()
  }
}
```

Notice:
- increment and decrement use update() — the new value depends on the current one
- reset uses set() — we know exactly what the value should be (0)
- Methods are clean, readable, and short
- No this.changeDetector.markForCheck(), no Subject.next()

SCRIPT:
Here are the methods that go with that template. Each one is one line that calls either set() or update() on the signal, and then Angular handles the rest.
Notice the deliberate choice between set() and update(). increment and decrement use update() because the new value depends on what's already there. reset uses set() because we know we want exactly zero — the current value doesn't matter.
Compare this to the BehaviorSubject equivalent where you'd write this.count$.next(this.count$.getValue() + 1) or something equally verbose. Signals compress all of that down to one readable line.

---

[SLIDE 10: Computed Signals — The Concept]
Title: "Computed Signals — Deriving Values Automatically"

What is a computed signal?
A signal whose value is automatically derived from other signals.

Without computed — the manual way (don't do this):
```typescript
price = signal(100);
taxRate = signal(0.1);
totalPrice = 0; // regular property

constructor() {
  // You'd have to manually keep this in sync — error prone
}
```

With computed — the right way:
```typescript
price = signal(100);
taxRate = signal(0.1);
totalPrice = computed(() => this.price() * (1 + this.taxRate()));
// totalPrice() is always correct, automatically
```

SCRIPT:
Computed signals are one of the most powerful parts of the signals API. The idea is simple: sometimes you have a value that should always equal some calculation based on other signals. Instead of manually keeping that value in sync — which is error-prone — you declare the relationship once with computed() and Angular keeps it accurate forever.
On the left is the trap people fall into when they're new to signals. They declare a regular property and think they'll update it manually when the signals change. But then they have to remember to update it everywhere, and they'll miss a spot, and they'll have a bug that's hard to find.
On the right is the correct pattern. You declare totalPrice as a computed signal. The function inside it reads from price and taxRate. From now on, Angular knows that totalPrice depends on those two signals. Whenever either changes, totalPrice is automatically recalculated on the next read.

---

[SLIDE 11: Computed Signals — Rules and Behavior]
Title: "Two Rules for Computed Signals"

```typescript
price = signal(100);
taxRate = signal(0.1);
totalPrice = computed(() => this.price() * (1 + this.taxRate()));

// Reading it:
console.log(this.totalPrice()); // 110

// Rule 1: Read-only — you cannot call set() on a computed signal
this.totalPrice.set(200); // ❌ ERROR — computed signals have no set()

// Rule 2: Lazy — only recalculates when a dependency changes AND it's read
// If nothing reads totalPrice, Angular won't bother recalculating it
this.price.set(200);
// totalPrice hasn't recalculated yet — nothing has read it
console.log(this.totalPrice()); // 220 — recalculates NOW on read
```

Rule 1 — Read-only:
Computed signals are derived, not owned. You can't set() them directly.
If you need to change the value, change the source signals.

Rule 2 — Lazy:
Angular only recalculates when something actually reads the value.
This makes computed signals very efficient.

SCRIPT:
Two rules to memorize about computed signals.
Rule one: computed signals are read-only. You derive them, you don't own them. If you want totalPrice to change, you change price or taxRate — the sources. You never call set() directly on a computed signal. Angular will throw an error if you try.
Rule two: computed signals are lazy. Angular doesn't immediately recalculate the value every time a dependency changes. It marks the computed signal as stale and waits. Only when something actually reads the computed signal does it recalculate. This is an efficiency feature — if nothing in your app is currently reading totalPrice, Angular isn't spending time recalculating it.
Both of these rules make computed signals predictable. They always reflect the current state of their dependencies, and they never do more work than necessary.

---

[SLIDE 12: Effects — The Concept]
Title: "Effects — Reacting to Signal Changes"

What is an effect?
A function that runs automatically whenever any signal it reads from changes.

Use effects for side effects — things that need to happen when state changes but don't produce a value:
- Logging to the console
- Writing to localStorage
- Sending analytics events
- Syncing with something outside Angular

NOT for:
- Deriving values from signals → use computed() for that
- Updating other signals → use computed() or service methods

SCRIPT:
The third piece of the signals API is effect. If signal is the box and computed is the derived value, then effect is the "do something when state changes" tool.
An effect is for side effects — things that need to happen in response to state changes but don't produce a signal value. Logging is the simplest example. Saving something to localStorage when the user's preferences change. Sending an analytics event when a step is completed. These are all appropriate uses of effect.
What effects are not for is deriving values. If you find yourself thinking "I'll use an effect to update this other signal when this signal changes," stop — that's what computed() is for, and doing it in an effect is both more work and more dangerous.

---

[SLIDE 13: Effects — The Syntax]
Title: "Writing an Effect"

```typescript
import { signal, effect } from '@angular/core';

export class PreferencesComponent {
  theme = signal('light');

  constructor() {
    effect(() => {
      // This runs once immediately, then again whenever theme() changes
      console.log(`Theme changed to: ${this.theme()}`);
      localStorage.setItem('theme', this.theme());
    });
  }
}
```

What happens:
1. Effect runs immediately when the component is created
2. Angular records which signals were read inside it (theme)
3. When theme changes, the effect runs again automatically
4. You don't specify which signals to watch — Angular figures it out

SCRIPT:
Here's the syntax. You call effect() inside the constructor — we'll explain why in a moment — and pass it a function. That function does whatever side effect you need.
When the component is created, the effect runs once right away. Angular records every signal that was read during that run — in this case, theme. From that point on, whenever theme changes, the effect runs again. You never have to tell the effect which signals to watch. It discovers them automatically by tracking which signals were called during the first run.
This auto-tracking is very powerful. If you have an effect that reads five different signals, and any one of them changes, the effect re-runs. You don't maintain a list of dependencies anywhere.

---

[SLIDE 14: Effects — The Injection Context Rule]
Title: "⚠️ Effects Must Be in an Injection Context"

```typescript
export class MyComponent {
  name = signal('Angular');

  // ✅ Option 1: In the constructor
  constructor() {
    effect(() => {
      console.log(this.name());
    });
  }

  // ✅ Option 2: As a class field initializer
  private logEffect = effect(() => {
    console.log(this.name());
  });

  // ❌ Will throw an error
  ngOnInit() {
    effect(() => {           // ERROR: Not in an injection context
      console.log(this.name());
    });
  }
}
```

Why?
Effects use Angular's dependency injection system internally.
DI is only available during construction, not in lifecycle hooks like ngOnInit.

SCRIPT:
This is the gotcha I see most often with effects, so it gets its own slide. Effects must be created inside an injection context. In practice that means one of two places: inside the constructor, or as a class field initializer at the top of the class.
You cannot create an effect inside ngOnInit, ngOnChanges, or any other lifecycle hook. Angular will throw an error if you try because the dependency injection system that effects rely on internally is only available during construction.
I know a lot of people's instinct is to put initialization logic in ngOnInit. For effects, that instinct is wrong. Put them in the constructor or as field initializers. Write this rule on a sticky note if you have to — it will save you significant debugging time.

---

[SLIDE 15: Effects — The Infinite Loop Warning]
Title: "⚠️ Never Update Signals Inside Effects"

```typescript
count = signal(0);

// ❌ DANGER — infinite loop
constructor() {
  effect(() => {
    console.log(this.count()); // reads count — creates dependency
    this.count.set(this.count() + 1); // updates count — triggers effect again
    // → effect runs again → updates count → effect runs again → ...
  });
}

// ✅ If you need a derived value, use computed() instead
doubleCount = computed(() => this.count() * 2);
```

The rule:
Effects are for reading signals and causing side effects outside Angular.
If you need to derive one signal from another, use computed().
If you find yourself setting a signal inside an effect, you have the wrong tool.

SCRIPT:
This is the most dangerous mistake you can make with effects. If you read a signal inside an effect and also set that same signal — or any signal that the effect also reads — you create an infinite loop. The effect runs, it sets the signal, the signal change triggers the effect, the effect runs again, and so on.
Angular will usually detect this and throw an error rather than hanging indefinitely, but it will crash your component and the error message can be confusing if you don't know what caused it.
The fix is to ask yourself: am I trying to derive a value from another signal? If yes, use computed(). computed() is designed for exactly this. Effects are for talking to the outside world — localStorage, console, analytics, external APIs. They read from signals, they don't write to them.

---

[SLIDE 16: Signals vs Observables — When to Use Each]
Title: "Signals vs Observables — Choosing the Right Tool"

| | Signals | Observables |
|---|---|---|
| Value | Always has one | May or may not |
| Sync/Async | Synchronous | Can be async |
| Subscription | Not needed | Required |
| Best for | Local state, derived values | HTTP, events, streams |
| Template | `{{ value() }}` | `{{ value$ \| async }}` |
| Learning curve | Lower | Higher |

SCRIPT:
Both tools are still valid — Observables are not going away. The question is which one to reach for in a given situation.
Signals are synchronous and always have a value. They're the right choice for local component state — things like whether a dropdown is open, the current count, the selected tab, anything that lives inside your component or service and changes in response to user actions.
Observables are the right choice for anything asynchronous or stream-based — HTTP calls, WebSocket messages, router events, form value changes over time. The Angular HTTP client returns Observables and that's not changing. RxJS operators like debounceTime, switchMap, and combineLatest are powerful tools that signals don't replace.

---

[SLIDE 17: Signals vs Observables — The Mental Model]
Title: "The Mental Model"

Ask yourself: where does this value come from?

From INSIDE your component or service:
→ User clicked a button
→ Form field changed
→ Timer ticked
→ Use a Signal

From OUTSIDE your component, arriving over time:
→ HTTP response
→ WebSocket message
→ Router navigation
→ Another service's stream
→ Use an Observable

The good news: they work together.
Angular gives you tools to convert between them — next slide.

SCRIPT:
Here's the question I ask myself when I'm deciding which to use: where is this value coming from and when does it arrive?
If the value lives and changes entirely within my component or service — a counter, a toggle, a list of items I'm managing — that's a signal. I control it, it's synchronous, and signals are the simpler tool.
If the value is arriving from outside — an HTTP response, a stream of real-time data, a router event — that's an Observable. The data is coming from somewhere else, possibly asynchronously, and Observables are designed for exactly that.
And crucially — you don't have to choose one world and live in it forever. Angular gives you conversion utilities that let you take an Observable and use it like a signal, or take a signal and pipe it through RxJS operators.

---

[SLIDE 18: Bridging — toSignal()]
Title: "toSignal() — Use an Observable Like a Signal"

```typescript
import { toSignal } from '@angular/core/rxjs-interop';
import { HttpClient } from '@angular/common/http';

@Component({ ... })
export class ProductsComponent {
  private http = inject(HttpClient);

  // HTTP returns an Observable — wrap it in a signal
  products = toSignal(
    this.http.get<Product[]>('/api/products'),
    { initialValue: [] }   // required — signals always need a starting value
  );
}
```

In the template — no async pipe needed:
```html
<li *ngFor="let p of products()">{{ p.name }}</li>
```

Why initialValue is required:
The HTTP call is async — the Observable hasn't emitted yet when the component renders.
Signals always need a value. initialValue is what the signal holds until the data arrives.

SCRIPT:
toSignal() is the utility you'll use constantly once you start working with Angular's HTTP client alongside signals. It takes an Observable and wraps it in a signal. From that point on you can use it in templates with products() just like any other signal — no async pipe.
The initialValue option is required and important. Think about what happens when the component first renders. The HTTP call hasn't come back yet. The Observable hasn't emitted. But signals always need a value — they can't be undefined unless you explicitly allow it. So you provide initialValue as the placeholder. For an array of products that's an empty array. For a single object it might be null. Whatever makes sense as a "loading" state.
toSignal() also handles the subscription for you. You don't need to unsubscribe. When the component is destroyed, the subscription is automatically cleaned up.

---

[SLIDE 19: Bridging — toObservable()]
Title: "toObservable() — Use a Signal With RxJS Operators"

```typescript
import { toObservable } from '@angular/core/rxjs-interop';

export class SearchComponent {
  searchTerm = signal('');

  // Convert signal to Observable to use RxJS operators
  results$ = toObservable(this.searchTerm).pipe(
    debounceTime(300),        // wait 300ms after user stops typing
    distinctUntilChanged(),   // don't search if the value hasn't changed
    switchMap(term =>
      this.http.get(`/api/search?q=${term}`)
    )
  );
}
```

When to use toObservable():
- You have a signal but need to apply RxJS timing operators
- Debouncing user input before sending HTTP requests
- Combining signal values with other Observables using combineLatest or merge

Note: Both utilities are imported from @angular/core/rxjs-interop — not from @angular/core.

SCRIPT:
toObservable() goes the other direction. You have a signal — maybe a search input the user is typing into — and you want to run RxJS operators on the stream of values it produces. debounceTime, distinctUntilChanged, switchMap — these are still the right tools for that pattern, and signals alone can't do it.
toObservable() converts the signal into an Observable that emits every time the signal's value changes. From there you can pipe it through any RxJS operator chain you want.
The search example is the most common real-world use case. The user types into a search box — that updates a signal. You convert that signal to an Observable, debounce it so you're not hammering the API with every keystroke, then switchMap to the HTTP call. This is clean, readable, and uses exactly the right tool for each job.
One thing to remember: both toSignal() and toObservable() come from @angular/core/rxjs-interop — that's a different package from @angular/core. Easy to forget the import path the first time.

---

[SLIDE 20: Signals in a Service — The Pattern]
Title: "Signals in Services — Shared Reactive State"

Why put signals in a service?
- Multiple components need the same state
- State should persist across component creation/destruction
- You want one source of truth, not copies in each component

The pattern:
```typescript
@Injectable({ providedIn: 'root' })
export class CartService {
  private items = signal<CartItem[]>([]);   // private — only this service can change it

  readonly cartItems = this.items.asReadonly();  // expose read-only to outside world
  readonly itemCount = computed(() => this.items().length);
  readonly total = computed(() =>
    this.items().reduce((sum, item) => sum + item.price, 0)
  );
}
```

SCRIPT:
Signals aren't just for individual components. When you put signals in a service, any component that injects that service can read those signals, and whenever the service updates them, every component automatically re-renders. One signal, many consumers, all kept in sync automatically.
The pattern to follow is shown here. The signal itself is private — only the service can call set() or update() on it. The outside world sees a read-only version via asReadonly(). Derived values like item count and total are computed signals — they're always accurate, they're efficient, and they can't be accidentally overwritten.
This is a lightweight state management pattern. For many applications it completely replaces the need for NgRx or other state libraries.

---

[SLIDE 21: Signals in a Service — The Methods]
Title: "Encapsulating State Changes in Methods"

```typescript
@Injectable({ providedIn: 'root' })
export class CartService {
  private items = signal<CartItem[]>([]);

  readonly cartItems = this.items.asReadonly();
  readonly itemCount = computed(() => this.items().length);
  readonly total = computed(() =>
    this.items().reduce((sum, item) => sum + item.price, 0)
  );

  addItem(item: CartItem) {
    this.items.update(current => [...current, item]);
  }

  removeItem(id: string) {
    this.items.update(current => current.filter(i => i.id !== id));
  }
}
```

Why this matters:
- State can only change through addItem() and removeItem()
- Components cannot accidentally corrupt the state
- You have one place to add validation, logging, or side effects
- This is the same principle as a reducer in NgRx — centralized, predictable mutations

SCRIPT:
Here's the complete service with methods. The methods are the only doors into the private signal. Components inject this service and call addItem() or removeItem(). They can't reach past those methods to touch items directly.
Notice that addItem uses update() with the spread operator to return a new array — it doesn't mutate the existing one. And removeItem filters to a new array. Immutable updates are important with signals for the same reason they're important everywhere in reactive programming — Angular detects changes by reference equality, so if you mutate an array in place rather than returning a new one, Angular may not know anything changed.
The key point: this service is your single source of truth for cart state. Any component anywhere in the app that reads cartItems() sees the same data. When one component calls addItem(), all components that display the cart update automatically.

---

═══════════════════════════════════════════
PART 2: TESTING IN ANGULAR
═══════════════════════════════════════════

---

[SLIDE 22: Why We Test]
Title: "Why Testing Matters"

What testing gives you:
- Catch bugs before your users do
- Refactor code without fear — if tests pass, behavior is unchanged
- Add features confidently — tests tell you what you broke
- Tests are living documentation — they describe exactly what the code should do
- Required at every professional software company

The alternative:
- Manual testing after every change
- "Works on my machine"
- Bugs found in production, by customers
- Fear of touching old code

SCRIPT:
I want to spend a minute on the why before we touch any code, because testing is the topic students most often skip or underinvest in. And then they get a job and their tech lead asks them why there are no tests and they don't know where to start.
Testing is not extra work. Testing is how professional software development works. When you have a test suite, you can change code, add features, and upgrade dependencies with confidence. The tests tell you immediately if anything broke. Without tests, every change is a gamble.
Tests are also documentation. A well-written test describes exactly what a function or component should do in plain English. A new developer joining your team can read the tests and understand the codebase before they even read the implementation.

---

[SLIDE 23: Jasmine — The Testing Framework]
Title: "Jasmine — What It Is and What It Does"

Jasmine is the testing framework — it gives you the language for writing tests:

- describe() — groups related tests into a suite
- it() — defines one individual test case
- expect() — makes an assertion
- Matchers: toBe, toEqual, toContain, toBeTruthy, toBeFalsy, toHaveBeenCalled

A test file looks like:
```typescript
describe('My Feature', () => {

  it('should do something specific', () => {
    expect(result).toBe(expectedValue);
  });

});
```

SCRIPT:
Jasmine is the testing framework Angular ships with. It gives you the vocabulary for writing tests. describe() groups related tests. it() is one test. expect() makes an assertion about a value.
Think of describe() as a folder — it organizes tests that belong together, usually tests for one class or feature. Think of it() as a test case — it's one specific scenario. The string you pass to it() is what shows up in the test results report, so write it as a real sentence describing what should happen.
We'll look at the matchers — toBe, toEqual, and so on — in more detail in a moment.

---

[SLIDE 24: Karma — The Test Runner]
Title: "Karma — What It Is and What It Does"

Karma is the test runner — it executes your Jasmine tests:

- Starts a real browser (Chrome by default)
- Loads your compiled tests into that browser
- Runs them and reports results in the terminal and browser window
- Watches your files — re-runs tests automatically on save

How to run:
```bash
ng test
```

That's it. Karma handles the rest.

Jasmine writes the tests.
Karma runs the tests.
They always work together.

SCRIPT:
Karma is the test runner. Its job is to take your Jasmine tests and actually execute them. It fires up a real browser — Chrome by default — and runs the tests there. This matters because your Angular components eventually run in a browser, and running tests in a real browser means you're testing in the real environment.
When you run ng test, Karma starts up, opens a browser window, runs all the tests, and shows you results in both the browser and the terminal. It also watches your files. Save a change to your component and the tests re-run automatically. This feedback loop while you're developing is one of the most valuable parts of the testing workflow.
Remember: Jasmine is the language you write tests in. Karma is the engine that runs them. You'll use both but you'll barely think about Karma — ng test is all you need.

---

[SLIDE 25: Jasmine Matchers]
Title: "Jasmine Matchers — Asserting on Values"

```typescript
// toBe — strict equality (===)
expect(2 + 2).toBe(4);
expect('hello').toBe('hello');

// toEqual — deep equality (for objects and arrays)
expect([1, 2, 3]).toEqual([1, 2, 3]);
expect({ name: 'Alice' }).toEqual({ name: 'Alice' });

// toBeTruthy / toBeFalsy
expect(true).toBeTruthy();
expect(null).toBeFalsy();
expect(0).toBeFalsy();

// toContain — for arrays and strings
expect([1, 2, 3]).toContain(2);
expect('hello world').toContain('world');

// toBeNull, toBeUndefined, toBeGreaterThan, toBeLessThan...
```

Rule of thumb:
- Primitives (number, string, boolean) → toBe
- Objects and arrays → toEqual
- Checking if a method was called → toHaveBeenCalled (spies — covered later)

SCRIPT:
Matchers are how you tell Jasmine what you expect to be true. Let's walk through the most common ones.
toBe uses strict equality — the triple equals operator. Use it for primitives: numbers, strings, booleans. Two separate objects won't pass toBe even if they look identical, because they're different references in memory.
For objects and arrays, use toEqual. It compares the structure and values deeply, so two objects with the same properties and values will pass toEqual even if they're not the same reference.
toBeTruthy and toBeFalsy check for truthiness rather than exact equality. toBeTruthy passes for anything that isn't null, undefined, 0, empty string, or false. Useful when you care that something exists, not what it exactly equals.
toContain works on both arrays and strings — use it when you need to know if something is present somewhere in a collection or string.

---

[SLIDE 26: Test Lifecycle — beforeEach and afterEach]
Title: "Test Lifecycle — beforeEach and afterEach"

```typescript
describe('CartService', () => {
  let service: CartService;

  beforeEach(() => {
    // Runs before EVERY test in this describe block
    // Use it to: create fresh instances, reset state, configure mocks
    TestBed.configureTestingModule({});
    service = TestBed.inject(CartService);
  });

  afterEach(() => {
    // Runs after EVERY test in this describe block
    // Use it to: clean up, verify no unexpected calls were made
    // (You'll use this with HTTP testing)
  });

  it('test one — gets a fresh service', () => { ... });
  it('test two — also gets a fresh service', () => { ... });
});
```

Key rules:
- Each test gets a clean slate — state from one test never affects another
- If a test fails, afterEach still runs
- beforeEach is where you set up; afterEach is where you clean up

SCRIPT:
beforeEach and afterEach are lifecycle hooks for your test suite. They run around every single test in the describe block.
beforeEach is where you set up everything each test needs: create your component or service, inject dependencies, configure mocks. The crucial word is "each." It runs again before every test, giving every test a completely fresh starting point. Test one cannot accidentally dirty the state that test two sees.
afterEach is for cleanup. The most common use you'll see is verifying that no unexpected HTTP calls were made — we'll look at that when we get to HTTP testing. But the principle is the same: it runs after every test, regardless of whether the test passed or failed.
This isolation is fundamental. Tests that share state are brittle and produce mysterious failures. beforeEach ensures that never happens by resetting everything between tests.

---

[SLIDE 27: Anatomy of a Jasmine Test — AAA Pattern]
Title: "The AAA Pattern — Arrange, Act, Assert"

```typescript
describe('Calculator', () => {

  it('should add two numbers correctly', () => {

    // ARRANGE — set up your data and conditions
    const a = 2;
    const b = 3;

    // ACT — call the thing you're testing
    const result = add(a, b);

    // ASSERT — verify the result is what you expected
    expect(result).toBe(5);

  });

});
```

Every test you write should follow this pattern:
1. Arrange — prepare the inputs and any needed state
2. Act — call the function or trigger the behavior
3. Assert — verify the outcome

If your test doesn't have all three parts, something is missing.

SCRIPT:
Every test you write — every single one — should follow the AAA pattern. Arrange, Act, Assert.
Arrange is where you set up everything the test needs. The inputs, any objects you need, any state that should exist before the action happens.
Act is where you actually do the thing you're testing. Call the function. Click the button. Call the service method.
Assert is where you verify that what happened was correct. This is your expect() call.
This pattern keeps tests readable and organized. When a test fails, you immediately know where to look — is the setup wrong (Arrange), am I calling the right thing (Act), or is my expectation wrong (Assert)?
Some tests have a very small Arrange section because beforeEach does that work. That's fine — the pattern still applies, it's just that the Arrange step is in beforeEach.

---

[SLIDE 28: TestBed — What It Is]
Title: "TestBed — Angular's Test Environment"

What is TestBed?
Angular's utility for creating a mini testing module.
Think of it as a stripped-down NgModule just for your test.

Why do you need it?
Angular components and services rely on Angular's dependency injection system.
You can't just instantiate a component with new CounterComponent() — it won't have its dependencies.
TestBed sets up the injection system for you.

```typescript
import { TestBed } from '@angular/core/testing';

beforeEach(async () => {
  await TestBed.configureTestingModule({
    declarations: [CounterComponent],  // components to test
    providers: [CartService],          // services to provide
    imports: [HttpClientTestingModule] // modules to import
  }).compileComponents();
});
```

SCRIPT:
TestBed is Angular's testing module builder. Before you can test any Angular component or service, you need to set up Angular's dependency injection system — the thing that makes inject() and constructor injection work. TestBed does that for you.
You configure it similarly to how you configure an NgModule. You declare the components you're testing, provide the services they need, and import any Angular modules they depend on. Then you call compileComponents() which compiles the templates.
This setup goes inside beforeEach so it runs fresh before every test, giving each test a clean Angular environment.
The key insight: you are not testing in isolation from Angular itself — you're testing your component inside a real (if minimal) Angular environment. That's important because it means your templates compile, change detection runs, and dependency injection works exactly as it would in the real app.

---

[SLIDE 29: ComponentFixture — The Test Wrapper]
Title: "ComponentFixture — Your Handle on the Component"

```typescript
let component: CounterComponent;
let fixture: ComponentFixture<CounterComponent>;

beforeEach(async () => {
  await TestBed.configureTestingModule({
    declarations: [CounterComponent]
  }).compileComponents();

  // Create the component
  fixture = TestBed.createComponent(CounterComponent);

  // Access the component class instance
  component = fixture.componentInstance;

  // Trigger initial rendering (runs ngOnInit, renders template)
  fixture.detectChanges();
});
```

What fixture gives you:
- fixture.componentInstance — the TypeScript class, call methods and read signals
- fixture.nativeElement — the actual DOM node, query HTML elements
- fixture.detectChanges() — manually trigger change detection

SCRIPT:
When you call TestBed.createComponent(), you get back a ComponentFixture. Think of it as a test harness — a wrapper around your component that gives you controlled access to it.
There are three things you'll use constantly from the fixture. componentInstance is the TypeScript class itself — you call methods on it, read signal values from it. nativeElement is the rendered DOM — you query it with querySelector to find HTML elements. And detectChanges() triggers Angular's change detection, which updates the DOM to reflect the current state.
That last call in beforeEach — fixture.detectChanges() — is critical. It triggers ngOnInit and renders the initial template. Without it, your component hasn't fully initialized and your tests will see an empty or incorrect DOM.

---

[SLIDE 30: fixture.detectChanges() — Why It's Manual]
Title: "Why You Must Call detectChanges() Manually"

In a running app:
Angular detects changes automatically after events, HTTP responses, and async operations.

In tests:
Angular does NOT run change detection automatically.
You are in control. You trigger it when you're ready.

```typescript
it('should update the DOM when count changes', () => {
  // Act
  component.increment();
  // DOM has NOT updated yet — change detection hasn't run

  fixture.detectChanges();
  // NOW the DOM is updated

  // Assert
  const p = fixture.nativeElement.querySelector('p');
  expect(p.textContent).toContain('1');
});
```

The rule: action → detectChanges() → assert
Miss the middle step and your assertion will see stale DOM.

SCRIPT:
This is the mistake I see in almost every student's first Angular test. They click a button, immediately check the DOM, and get a failure. The code is correct, but the DOM hasn't updated yet because they forgot detectChanges().
In a running application, Angular's change detection fires automatically after events and async operations. In tests, that's turned off. You are in full control of when change detection runs. This is actually a feature — it lets you make multiple state changes and then check the result in a controlled way.
The rule is simple: action, then detectChanges(), then assert. Every time. If you're clicking something, calling a method, or changing a signal directly in a test, you always call fixture.detectChanges() before checking the DOM. Make this a reflex.

---

[SLIDE 31: Writing a Component Test — Testing the DOM]
Title: "Testing What the User Sees — DOM Assertions"

```typescript
it('should display the initial count as 0', () => {
  // Arrange — done by beforeEach

  // Act — nothing to do, just checking initial state

  // Assert — query the DOM and check text content
  const paragraph = fixture.nativeElement.querySelector('p');
  expect(paragraph.textContent).toContain('0');
});

it('should update the count display when increment is clicked', () => {
  // Act
  const button = fixture.nativeElement.querySelector('button');
  button.click();
  fixture.detectChanges();  // flush the change to the DOM

  // Assert
  const paragraph = fixture.nativeElement.querySelector('p');
  expect(paragraph.textContent).toContain('1');
});
```

SCRIPT:
DOM tests verify what the user actually sees. You're not testing component logic here — you're testing that your template is correctly wired to your component logic.
fixture.nativeElement gives you the root DOM element of the component. querySelector() works exactly like it does in the browser — pass a CSS selector and get the first matching element.
The first test just checks the initial state — no action needed, just query and assert. The second test simulates a real user click: find the button, click it, call detectChanges() to flush the change to the DOM, then query the paragraph and verify it shows the new value.
Why bother testing the DOM when you can just test the signal value directly? Because the DOM test catches a whole category of bugs that signal tests can't — things like forgetting the parentheses on count() in the template, or binding to the wrong method, or having the wrong element selector.

---

[SLIDE 32: Writing a Component Test — Testing Logic Directly]
Title: "Testing Component Logic — Signal Assertions"

```typescript
it('should start with count of 0', () => {
  expect(component.count()).toBe(0);  // read the signal directly
});

it('should increment the count signal', () => {
  component.increment();
  expect(component.count()).toBe(1);  // no detectChanges() needed — just checking the signal
});

it('should reset to 0', () => {
  component.count.set(5);     // set signal directly for precise test setup
  component.reset();
  expect(component.count()).toBe(0);
});
```

When to test logic vs DOM:
- Test LOGIC directly when you want to verify the signal value itself
- Test the DOM when you want to verify the template is correctly rendering that value
- Ideally do both — each catches different bugs

SCRIPT:
You can also test the component's logic directly without going through the DOM. This is faster and more targeted. You call component methods, then read signal values directly with component.count().
Notice the third test — setting the signal directly to 5 before calling reset. This is one of the powerful things about testing with signals. Because signals are synchronous, you can set them to any state you need for a test without simulating user interactions. You want to test what happens when count is 5? Just set it to 5. No clicking the increment button five times.
Also notice there's no detectChanges() call here. That's only needed when you want the DOM to update. If you're just checking the signal value, you don't need it — signal reads are synchronous and immediate.

---

[SLIDE 33: Testing Services — Setup]
Title: "Testing a Service — The Setup"

```typescript
import { TestBed } from '@angular/core/testing';
import { CartService } from './cart.service';

describe('CartService', () => {
  let service: CartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});  // minimal config — no components needed
    service = TestBed.inject(CartService);  // get the service from Angular's DI
  });

  it('should be created', () => {
    expect(service).toBeTruthy();  // basic smoke test — did it even instantiate?
  });
});
```

Why TestBed.inject() and not new CartService()?
Because the service may have its own dependencies injected via the constructor.
TestBed handles that for you — just call inject() and let Angular wire it up.

SCRIPT:
Service tests are simpler than component tests because there's no DOM to worry about. The setup is minimal — configure TestBed with an empty object (or with any modules the service actually needs), then call TestBed.inject() to get the service instance.
Why use inject() instead of just new CartService()? Because in a real Angular application, services get their dependencies injected by Angular's DI system. If you instantiate with new, you'd have to manually provide all the constructor arguments. inject() tells Angular to do that for you, exactly the same way it would in the running app.
The first test — just checking that the service is truthy — is a smoke test. It catches the most basic failure: the service can't even be created because of a missing dependency or configuration error.

---

[SLIDE 34: Testing Services — Testing Logic and Signals]
Title: "Testing Service Methods and Signals"

```typescript
it('should add an item to the cart', () => {
  const item = { id: '1', name: 'Book', price: 20 };

  service.addItem(item);

  expect(service.cartItems()).toContain(item);
  expect(service.itemCount()).toBe(1);
});

it('should remove an item from the cart', () => {
  service.addItem({ id: '1', name: 'Book', price: 20 });
  service.addItem({ id: '2', name: 'Pen', price: 5 });

  service.removeItem('1');

  expect(service.itemCount()).toBe(1);
  expect(service.cartItems().find(i => i.id === '1')).toBeUndefined();
});

it('should compute the total correctly', () => {
  service.addItem({ id: '1', name: 'A', price: 30 });
  service.addItem({ id: '2', name: 'B', price: 20 });
  expect(service.total()).toBe(50);
});
```

SCRIPT:
Here's the meat of service testing. Call a method, read a signal, assert on the value. That's it. Because signals are synchronous, there's no subscribe(), no async/await, no waiting for anything.
The add test calls addItem() and immediately checks cartItems() and itemCount(). Both are signals — calling them returns the current value right now. No ceremony.
The remove test sets up two items, removes one, then verifies that only one remains and the right one was removed. Testing that the removed item is undefined uses toBeUndefined() — a clean way to verify something is gone.
The computed total test is important. Test your computed signals explicitly. A bug in a calculation formula won't be caught by any other test. This test catches the case where your reduce formula is wrong, or you're looking at the wrong property, or you forgot to handle an edge case.

---

[SLIDE 35: Why We Mock — Testing in Isolation]
Title: "Mocking Dependencies — Why We Don't Use Real Services"

The problem: a component that uses a service

```typescript
// OrderComponent uses CartService
// CartService makes HTTP calls, writes to a database, etc.
```

If you test OrderComponent with the REAL CartService:
- Your test may fail because CartService has a bug → not OrderComponent's fault
- Your test makes real HTTP calls → slow, requires a running server
- You can't control what CartService returns → can't test edge cases
- A test failure doesn't tell you which code is broken

The solution: use a spy (a fake service)
- The component thinks it has the real CartService
- Actually it has a fake that you fully control
- Component test only tests the component
- Service has its own separate tests

SCRIPT:
This is one of the most important principles in testing: test one thing at a time. A component test should test the component. A service test should test the service. They should not test each other.
When you inject the real CartService into a component test, you're now testing both the component and the service at the same time. If CartService makes an HTTP call that fails because your dev server is down, your component test fails — but your component didn't do anything wrong. That's a false failure, and false failures erode trust in your test suite.
The solution is a spy — a fake object that looks like the real service to the component but is actually a hollow shell you control. It records calls, returns whatever you tell it to return, and does nothing else. The component can't tell the difference.

---

[SLIDE 36: Creating a Spy Object]
Title: "jasmine.createSpyObj() — Building the Fake Service"

```typescript
let cartServiceSpy: jasmine.SpyObj<CartService>;

beforeEach(async () => {
  cartServiceSpy = jasmine.createSpyObj(
    'CartService',            // label for debugging
    ['addItem', 'removeItem'], // methods to spy on
    {                          // properties (use real signals)
      cartItems: signal([]),
      itemCount: signal(0)
    }
  );
});
```

What createSpyObj gives you:
- Argument 1: A label that shows in error messages
- Argument 2: Method names — each becomes a fake that records calls but does nothing by default
- Argument 3: Properties — for signal properties, provide real signals with initial values (the component template will call these, so they need to work)

SCRIPT:
createSpyObj takes three arguments. The first is just a label for debugging — it shows up in error messages to identify which spy failed. The second is an array of method names. Each name in that array becomes a spy method on the fake object — it does nothing when called but records that it was called.
The third argument is for properties. This is where signals live. The component's template is going to call cartItems() and itemCount() — those are signal reads in the template. If they're undefined, the template will throw. So you provide real signals with whatever initial values make sense for your test. The component can read them, and if you need to change what they return mid-test, you can call cartServiceSpy.cartItems.set(someNewValue).
The type annotation jasmine.SpyObj<CartService> gives you TypeScript autocomplete on the spy and ensures you've implemented everything CartService has.

---

[SLIDE 37: Injecting the Spy Into the Component]
Title: "Replacing the Real Service With the Spy"

```typescript
beforeEach(async () => {
  cartServiceSpy = jasmine.createSpyObj( ... );

  await TestBed.configureTestingModule({
    declarations: [OrderComponent],
    providers: [
      // Tell Angular: when something asks for CartService, give this spy instead
      { provide: CartService, useValue: cartServiceSpy }
    ]
  }).compileComponents();

  fixture = TestBed.createComponent(OrderComponent);
  component = fixture.componentInstance;
  fixture.detectChanges();
});
```

The key line:
```typescript
{ provide: CartService, useValue: cartServiceSpy }
```
"When any class in this test asks for CartService via injection, give them the spy."
The component has no idea it's talking to a fake.

SCRIPT:
Once you have the spy, you inject it by adding it to the providers array in TestBed.configureTestingModule. The { provide, useValue } pattern tells Angular's DI system: whenever something requests CartService, give them this value instead of creating a real CartService.
From this point on, every time the OrderComponent calls this.cartService.addItem() or reads this.cartService.cartItems(), it's calling the spy. The component is completely unaware. It just knows it has something that looks like CartService.
The rest of the setup is identical to any other component test — createComponent, get componentInstance, call detectChanges. Nothing special.

---

[SLIDE 38: Asserting on Spy Calls]
Title: "Verifying What the Component Did — Spy Matchers"

```typescript
it('should call addItem when the user orders', () => {
  component.orderItem({ id: '1', name: 'Book', price: 20 });

  // Was it called at all?
  expect(cartServiceSpy.addItem).toHaveBeenCalled();
});

it('should pass the correct item to addItem', () => {
  const item = { id: '1', name: 'Book', price: 20 };
  component.orderItem(item);

  // Was it called with the right argument?
  expect(cartServiceSpy.addItem).toHaveBeenCalledWith(item);
});

it('should only call addItem once', () => {
  component.orderItem({ id: '1', name: 'Book', price: 20 });

  // Was it called exactly once?
  expect(cartServiceSpy.addItem).toHaveBeenCalledTimes(1);
});
```

Three matchers for spies:
- toHaveBeenCalled() — was the method called at all?
- toHaveBeenCalledWith(arg) — was it called with these exact arguments?
- toHaveBeenCalledTimes(n) — was it called exactly n times?

SCRIPT:
Here are the three spy matchers you'll use constantly.
toHaveBeenCalled() is the baseline — did the component even attempt to call the service method? If this fails it means the component never called addItem at all, which is a logic bug.
toHaveBeenCalledWith() is more precise. It verifies the component not only called the method but called it with the right data. This catches a whole class of bugs where the method is called but the wrong item, wrong ID, or wrong arguments are passed.
toHaveBeenCalledTimes() catches accidental double-calls. Imagine a checkout button that triggers a form submit AND a click event — you might accidentally call the service twice. This matcher catches that.
Remember: you are testing the component, not the service. These assertions verify that the component sends the right messages to the service. Whether the service does the right thing with those messages is the service test's job.

---

[SLIDE 39: HTTP Testing — Why You Never Use Real HTTP in Tests]
Title: "HTTP Testing — Never Make Real Network Calls in Tests"

Why not?
- Real HTTP calls require a running server — tests would fail if the server is down
- HTTP calls are slow — tests should run in milliseconds, not seconds
- You can't control what the server returns — can't test error responses or edge cases
- Tests become unreliable — same test can pass one day and fail the next

The solution:
Angular's HttpClientTestingModule intercepts HTTP calls before they go anywhere.
You decide what response to send back.
Tests are fast, reliable, and fully controlled.

SCRIPT:
The same isolation principle that applies to service mocking applies to HTTP testing. Real network calls in unit tests are a bad idea for four reasons: they require a server, they're slow, they're unreliable, and you can't control what comes back.
Angular gives us HttpClientTestingModule, which installs an interceptor that catches every outgoing HTTP request before it leaves the application. The request never reaches the network. You then manually decide what response to return. This is called flushing — you flush the intercepted request with fake data, and the Observable in your service emits with that data.
The result is tests that run in milliseconds, work offline, work in CI pipelines, and let you test error scenarios that would be impossible to reproduce reliably with a real server.

---

[SLIDE 40: HTTP Testing — The Setup]
Title: "HTTP Testing — Configuring the Test Module"

```typescript
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],  // intercepts all HTTP
      providers: [ProductService]
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();  // fails test if any HTTP calls were made but not handled
  });
});
```

Two new things:
- HttpClientTestingModule — import this instead of HttpClientModule
- HttpTestingController — inject this to inspect and respond to intercepted requests

SCRIPT:
The setup has two new pieces compared to regular service tests. Instead of importing HttpClientModule, you import HttpClientTestingModule. That's the swap that installs the interceptor.
You also inject HttpTestingController. This is your control panel — you'll use it to find intercepted requests, check their URLs and methods, and send back fake responses.
The afterEach with httpMock.verify() is something you should always include. It fails the test if your code made any HTTP requests that you didn't handle in the test. This is a guard against unexpected requests — if your service makes a bonus HTTP call somewhere that you didn't expect, verify() will catch it.

---

[SLIDE 41: HTTP Testing — The Test Flow]
Title: "HTTP Testing — The Order of Operations"

```typescript
it('should fetch products from the API', () => {
  // ARRANGE
  const mockProducts = [{ id: 1, name: 'Widget' }];

  // ACT — Step 1: Trigger the HTTP call (it gets intercepted immediately)
  service.getProducts().subscribe(products => {
    // Step 4: This callback runs AFTER flush() below
    expect(products).toEqual(mockProducts);
  });

  // ACT — Step 2: Find the intercepted request
  const req = httpMock.expectOne('/api/products');

  // ACT — Step 3: Verify it was a GET request
  expect(req.request.method).toBe('GET');

  // ACT — Step 4: Simulate the server responding
  req.flush(mockProducts);  // this triggers the subscribe callback above
});
```

SCRIPT:
Here's the actual test. Read through the steps carefully because the order is counterintuitive the first time.
Step 1: you call the service method. The HTTP request fires but is intercepted immediately. Nothing goes to the network. You get back an Observable but it hasn't emitted yet — there's been no response.
Step 2: you call expectOne() with the URL you expect was called. If your service didn't make a request to that URL, this line fails the test right there — that itself is a useful assertion.
Step 3: you verify the method was GET. This catches cases where someone accidentally sends a POST when they should be reading data.
Step 4: flush(). This simulates the server sending back your mock data. The moment flush() is called, the Observable emits, the subscribe callback runs, and your expect assertion fires.
The subscribe callback with the assertion runs last, even though it appears first in the code. Write that down — it's the most confusing part of HTTP testing for most students.

---

[SLIDE 42: Testing Private Signals — The Rule]
Title: "Private Signals — Test Through the Public API"

```typescript
// Inside CartService:
private items = signal<CartItem[]>([]);     // private — intentionally hidden
readonly cartItems = this.items.asReadonly(); // public — intentionally exposed

// ❌ Don't do this in your tests — it breaks encapsulation
(service as any)._items.set([...]);

// ✅ Do this — test the way a real consumer would
service.addItem({ id: '1', name: 'Book', price: 20 });
expect(service.cartItems()).toContain({ id: '1', name: 'Book', price: 20 });
```

Why this matters:
If you reach into private state, your tests become tightly coupled to the internal implementation.
Someone refactors the internals tomorrow — renames the private signal, changes the structure — and your test breaks, even though the service still works perfectly correctly from the outside.
Test what a consumer of the service sees. Nothing else.

SCRIPT:
A quick but important principle before we wrap up. Private signals are private for a reason — they're implementation details. The public API is what matters.
If you test a private signal directly by using a type cast to bypass TypeScript's access rules, you've created a coupling between your test and the implementation. The day someone refactors that service — even a perfectly safe internal refactor — your test breaks. Not because anything is wrong, but because the test knew too much about how the internals were structured.
Test through the public API. Call the public methods. Read the public signals. If you need a specific state, get there by calling the methods that create it. This produces tests that are stable across refactors and tests that actually reflect how the service is used in the real application.

---

[SLIDE 43: Common Mistakes — Signals]
Title: "Common Mistakes — Signals"

1. Forgetting () when reading a signal in TypeScript
```typescript
console.log(this.count);   // ❌ logs the Signal object
console.log(this.count()); // ✅ logs the value
```

2. Mutating arrays instead of replacing them
```typescript
this.items.update(list => { list.push(item); return list; }); // ❌ same reference
this.items.update(list => [...list, item]);                   // ✅ new reference
```

3. Creating effects outside an injection context
```typescript
ngOnInit() { effect(() => {...}); }    // ❌ throws an error
constructor() { effect(() => {...}); } // ✅
```

4. Updating signals inside effects → infinite loops
```typescript
effect(() => { this.count.set(this.count() + 1); }); // ❌ infinite loop
```

5. Forgetting initialValue with toSignal()
```typescript
toSignal(this.http.get(...))                       // ❌ may be undefined
toSignal(this.http.get(...), { initialValue: [] }) // ✅
```

SCRIPT:
Here are the signal-specific mistakes I see constantly. Run through these in your head any time something isn't working.
Missing parentheses is the most common beginner mistake — remember, it's always count() with parens in TypeScript, and the same in templates.
Mutating arrays in update() is subtle. If you push to the existing array and return it, Angular sees the same array reference and may not detect the change. Always return a new array with spread or filter.
Effects outside the constructor will throw. Effects inside effects that update signals will loop. And toSignal without initialValue will give you undefined on the first render.

---

[SLIDE 44: Common Mistakes — Testing]
Title: "Common Mistakes — Testing"

1. Forgetting fixture.detectChanges() after an action
```typescript
button.click();
// DOM hasn't updated yet!
expect(p.textContent).toContain('1'); // ❌ fails — sees old value
```

2. Using the real service in a component test
```typescript
providers: [CartService]  // ❌ real service — slow, brittle
providers: [{ provide: CartService, useValue: cartServiceSpy }] // ✅
```

3. Forgetting httpMock.verify() in afterEach
```typescript
afterEach(() => { httpMock.verify(); }); // ✅ catches unexpected HTTP calls
```

4. Expecting the wrong URL in expectOne()
```typescript
httpMock.expectOne('/products');     // ❌ if service calls '/api/products'
httpMock.expectOne('/api/products'); // ✅ must match exactly
```

5. Reaching into private signals in tests
```typescript
(service as any)._items.set([]);  // ❌ brittle — breaks on refactor
service.addItem(item);            // ✅ use the public API
```

SCRIPT:
And the testing-specific mistakes. The biggest one by far is the missing detectChanges(). If your DOM assertion is failing but you're sure the logic is correct, check if you forgot that call.
Using real services in component tests is the second most common — always swap in a spy so you're testing in isolation.
The expectOne URL must match exactly what your service sends. If your service adds a base URL, includes query parameters, or formats the path differently from what you put in expectOne(), the test will fail with a confusing error about unexpected requests.

---

[SLIDE 45: Recap — Signals]
Title: "What We Covered — Signals"

| Concept | What It Does |
|---|---|
| signal(value) | Creates reactive state |
| count() | Reads the value |
| count.set(x) | Replaces the value |
| count.update(fn) | Updates based on current value |
| computed(() => ...) | Derived, read-only, lazy signal |
| effect(() => ...) | Side effects when signals change |
| asReadonly() | Expose signal without write access |
| toSignal(obs$) | Observable → Signal (use in templates) |
| toObservable(sig) | Signal → Observable (use with RxJS) |

SCRIPT:
Here's the complete signals API we covered today in one table. Signal creation, reading, two update methods, computed for derived values, effect for side effects, asReadonly for encapsulation, and the two bridge utilities.
This is a reference you can come back to. If you ever forget which method to use, scan this table. If you can explain every row here in your own words, you understand signals.

---

[SLIDE 46: Recap — Testing]
Title: "What We Covered — Testing"

| Concept | What It Does |
|---|---|
| describe() / it() | Organize and define tests |
| expect().toBe/toEqual | Assert values |
| beforeEach / afterEach | Setup and teardown around each test |
| TestBed | Creates an Angular testing environment |
| ComponentFixture | Wrapper: access component, DOM, change detection |
| fixture.detectChanges() | Flush state changes to the DOM |
| jasmine.createSpyObj() | Create a fake service for isolation |
| toHaveBeenCalledWith() | Verify correct arguments were passed |
| HttpClientTestingModule | Intercept HTTP calls in tests |
| httpMock.expectOne() | Capture and inspect an HTTP request |
| req.flush() | Simulate a server response |

SCRIPT:
And the complete testing toolkit. From the basic building blocks — describe, it, expect — all the way through TestBed, fixtures, spies, and HTTP mocking.
Testing is a skill that builds with practice. The first few test files you write will feel slow and mechanical. By your tenth test file it will feel natural. The key is to start — write the test before you decide it's too hard.

---

[SLIDE 47: Practice Exercise]
Title: "Today's Exercise — Todo List App"

Build it:
- TodoService with a signal-based items array, addTodo(), removeTodo(), and a computed pendingCount
- TodoComponent that displays the list, has an input for new items, a delete button per item, and shows the pending count

Test it — write all of these:
- Service: addTodo() updates the items signal
- Service: removeTodo() removes the correct item
- Service: pendingCount() returns the correct computed value
- Component: renders the list from the service
- Component: calls addTodo() when the form is submitted (use a spy)
- Component: calls removeTodo() with the correct ID when delete is clicked (use a spy)
- Bonus: add a loadTodos() method that fetches from /api/todos and write the HTTP test for it

SCRIPT:
Your practice today is a todo list. This is classic because it exercises every concept from the session — signal creation, computed values, service encapsulation, component tests, spy injection, and optionally HTTP testing.
Build the service first, then the component, then write the tests. If you can build and test the service, then switch to a spy for the component test and write all six listed tests, you fully understand today's material. The bonus HTTP test is there if you finish early.
Bring anything you get stuck on to the next session. See you then.