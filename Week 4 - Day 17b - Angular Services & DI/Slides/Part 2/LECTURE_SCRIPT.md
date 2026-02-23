# Week 4 - Day 17b: Angular Services & DI
## Part 2 Lecture Script

**Duration:** 60 minutes
**Pacing:** ~165 words/minute
**Topics:** Custom pipes, Services & DI, Providers & injector hierarchy, Creating & injecting services, Sharing state across components, Component encapsulation

---

## [00:00–02:00] Opening

Welcome back. You just spent an hour learning how components talk to each other — parent to child with `@Input`, child to parent with `@Output`. You have structural directives, custom attribute directives, and built-in pipes.

But here's the gap. What happens when you have a `ProductListComponent` and a `NavbarComponent` — two completely unrelated components with no parent-child relationship? The user adds something to the cart in the product list. The navbar badge needs to update. Right now, you have no clean way to make that happen.

Part 2 solves that. We're covering four topics. First: custom pipes — building your own transformation logic. Second: services — classes that hold shared logic and data. Third: dependency injection — how Angular hands services to the components that need them. And fourth: component encapsulation — how Angular keeps your component CSS scoped and isolated.

These four topics together complete the Angular mental model. By the end of this hour, you'll be able to build applications where any component can access shared data and logic, without tangling your component tree into a mess. Let's start with custom pipes, since we set them up in Part 1.

---

## [02:00–10:00] Custom Pipes — Why and How

We looked at built-in pipes in Part 1. But what happens when Angular's built-in set doesn't cover your use case?

Slide 2 shows three real scenarios. First: you need to display "5 minutes ago" or "yesterday" instead of a formatted date string. The `date` pipe can't do that. Second: you need to display a phone number as `(555) 123-4567` but all you have is the raw string `"5551234567"`. No built-in pipe for that. Third: you want to filter a list in the template — `products | filter:'electronics'`. Also not built in.

The rule for when to build a custom pipe: when you need to transform a value for display in multiple templates and the logic is more than a one-liner. That phrase "for display" is important. Pipes should never modify original data or trigger side effects. They transform; they don't act.

Slide 3 — the anatomy. Generate it with `ng generate pipe phone-number`. Angular creates the file and the spec.

The pipe class has two parts. The `@Pipe` decorator with a `name` property — that name is what you use in the template. Here it's `'phoneNumber'`, so you'll write `{{ value | phoneNumber }}`.

The class implements `PipeTransform`, which requires one method: `transform`. The first argument is always the value on the left side of the pipe. Any subsequent arguments map to colon-separated parameters in the template.

Look at the implementation. We strip non-digit characters with a regex. We check if the format is 'us' and the length is 10. If so, we format with parentheses and hyphen. Otherwise, we return the original value unchanged. Simple, testable, reusable.

In the template: `{{ '5551234567' | phoneNumber }}` or with an explicit format argument: `{{ '5551234567' | phoneNumber:'us' }}`. The first colon after the pipe name starts the argument list. Each additional colon separates the next argument.

Don't forget: you must declare your custom pipe in your NgModule's `declarations` array — or if you're using standalone components, mark it as `standalone: true`. This is the number one mistake with custom pipes: creating the class and forgetting to register it, then wondering why the template throws an error saying the pipe doesn't exist.

---

## [10:00–16:00] Pure vs Impure Pipes

Slide 4 — this distinction trips people up in interviews and in production code.

By default, every pipe is pure. A pure pipe has a performance optimization baked in: Angular only calls `transform()` when the input reference changes. If you pass in the same object reference, Angular assumes the output won't change and skips calling the pipe.

This is a massive performance win. Angular's change detection runs constantly. If pipes re-ran on every cycle for every binding, templates with many pipes would be very slow.

But here's the catch: if you pass an array to a pure pipe, and then you push a new item into that array without replacing the reference, the pure pipe won't re-run. The reference is the same. Angular doesn't know the contents changed.

That's where impure pipes come in. Set `pure: false` in the `@Pipe` decorator. An impure pipe runs on every change detection cycle. It will catch mutations inside arrays and objects. But it has a real performance cost.

Slide 5 shows an impure filter pipe. We set `pure: false`. The `transform` method takes the items array, a search term, and a field name to search on. It filters using `includes` with a case-insensitive comparison.

In the template: `products | filter:searchTerm:'name'`. Three parts — the pipe name, the search term from a bound property, and `'name'` as a string literal for which field to search.

It's `pure: false` because the `products` array reference likely doesn't change between keystrokes — you'd be pushing to the same array. The impure pipe detects the content change.

However, here's a professional tip: for large lists, impure filter pipes can cause real performance problems. An alternative is to do the filtering in the component class, store the result in a `filteredProducts` computed property, and bind to that. You get the same result with a pure data flow and no pipe overhead. Both approaches are valid; know the trade-offs.

---

## [16:00–23:00] Services — The Problem and the Solution

Now for the centerpiece of Part 2. Slide 6 — the "why" of services.

Think about what we've been doing so far: data lives inside each component. `ProductListComponent` has a products array. `CartComponent` has a cart array. But these things are related — and in a real application, you also want that data to come from an API, not be hardcoded.

The two problems services solve: duplicated logic and shared state.

Duplicated logic: if `ProductListComponent` and `OrderComponent` both need to fetch and format products, they'd each have to implement that logic independently. One bug? You fix it in two places and forget the third.

Shared state: the user adds a product to the cart in `ProductListComponent`. The `NavbarComponent` needs to know the cart count. These two components have no parent-child relationship. You can't use `@Input` and `@Output` to bridge them without threading the state through every parent in the tree, which gets ugly fast.

The service solution: extract all shared logic and state into a separate class. Any component that needs it gets an injected instance. One `ProductService` holds the products logic. One `CartService` holds the cart state. They're singletons — there's one instance for the whole application. Every component that injects `CartService` gets the same instance, so they all see the same cart.

Slide 7 — Dependency Injection. The concept. Without DI, you'd write `new ProductService()` right in your component constructor. That works technically, but it's a problem for three reasons. First, you can't swap it — if you want a mock for testing, you're stuck with the real service. Second, Angular can't manage its lifetime — it can't clean it up or share it. Third, each component that does `new ProductService()` gets a different instance with its own state. Your cart is no longer shared; each component has its own.

With DI, you declare the dependency in the constructor: `constructor(private productService: ProductService) {}`. Angular sees the type annotation, looks it up in the injector, finds or creates the instance, and passes it in. You get a shared singleton, Angular manages it, and swapping it in tests is trivial.

The word "dependency" here just means "a thing this class needs to do its job." The word "injection" means "someone else creates it and gives it to you."

---

## [23:00–31:00] Creating a Service

Let's actually build a service. Slide 8.

Generate it: `ng generate service product`. Angular creates `product.service.ts` and a spec file.

Look at the `@Injectable` decorator. The critical part is `providedIn: 'root'`. This one property does something very important: it registers this service with the application's root injector, which means Angular creates exactly one instance of this service for the entire application, and it's available to any component anywhere in the app.

And here's a benefit that might surprise you: `providedIn: 'root'` also makes the service tree-shakable. If no component ever injects this service, Angular's build tools can detect that and exclude it from the production bundle entirely. That doesn't happen if you register services in `NgModule.providers` arrays instead.

The service class itself has no magic. It's just a TypeScript class. Here we have a private `products` array and methods to get all products or get one by ID. The class encapsulates the data and the operations on it.

Slide 9 — injecting the service. In `ProductListComponent`, we declare the dependency in the constructor: `private productService: ProductService`. That's Angular's signal: "I need a `ProductService`." Angular handles the rest.

Then in `ngOnInit`, we call `this.productService.getProducts()` and store the result. This is the pattern: constructor declares dependencies, ngOnInit uses them.

Why not call the service in the constructor? Constructors run before the component is fully initialized. There's no guarantee that inputs have been received, that the view has been prepared, or that other lifecycle hooks have run. `ngOnInit` runs after all that setup is complete. Keep constructors fast and simple — just declare dependencies.

The template doesn't change at all. It still binds to `this.products`. The only difference is where that data comes from — now it comes from the service instead of being hardcoded in the class.

---

## [31:00–39:00] The Injector Hierarchy

Slide 10 — let's talk about how Angular actually manages service instances.

Angular has a hierarchy of injectors, and this hierarchy determines who gets which instance of a service.

At the top is the Root Injector. Services registered with `providedIn: 'root'` live here. One instance for the whole application. This is what you want 90% of the time.

Below that are Module Injectors for feature modules, particularly lazy-loaded ones. If you have a module that's loaded lazily — only when a user navigates to a specific route — it gets its own injector. Services provided there are separate from the root.

At the bottom are Element Injectors — one per component in the component tree. If you put a service in a component's `providers` array, that component and all its children get their own separate instance of that service. Different from the root singleton.

Look at the comparison table. `providedIn: 'root'` — entire app shares one instance. `NgModule.providers` — all components in that module share an instance. `@Component({ providers: [...] })` — that component subtree gets a fresh instance.

When would you use component-level providers? Here's a real example: a multi-step form wizard. You might have a `WizardStateService` that tracks which step you're on and what the user has entered. If you have two independent wizard components on the same page, you don't want them sharing state. Put the service in each component's `providers` array and each wizard gets its own isolated state.

When Angular resolves a dependency, it walks up the injector tree. If it doesn't find the service at the component level, it checks the module level, then the root. This walkup is automatic — you don't have to configure anything for it.

---

## [39:00–47:00] Sharing State Between Components

Now for the big payoff — the problem we opened Part 2 with. Slide 11.

Here's a `CartService`. It's `providedIn: 'root'` — one instance for the whole app. It has a private `items` array — note it's private, no component can poke at it directly. Public methods: `add`, `remove`, `getItems`, `getCount`, `clear`.

Notice that `getItems` returns `[...this.items]` — a copy using the spread operator. Why? If we returned the original array, a component could push to it directly, bypassing the `add` method entirely. By returning a copy, we force all mutations through our controlled interface. This is called encapsulating state.

Now look at the two components using it. `ProductCardComponent` injects `CartService` and calls `cart.add(product)` when the user clicks. `NavbarComponent` — completely separate, no parent-child relationship with `ProductCard` — also injects `CartService` and calls `cart.getCount()`.

Because both are injecting the same singleton, the `add` call from `ProductCard` mutates the service's internal state. When the navbar's getter is called — from a binding in the template — it reads from that same mutated state and returns the updated count.

This is how you share data across components without any parent-child relationship. The service is the shared source of truth.

I do want to flag something for a preview: right now, if `ProductCard` adds to the cart, the navbar badge doesn't automatically update in real time unless Angular's change detection happens to run for both components at the right time. For full real-time reactivity, you'd use RxJS `BehaviorSubject` — where the service emits new values and components subscribe to them. We cover RxJS in Day 19b. For now, the approach on this slide works correctly, especially for simpler scenarios and for learning the pattern.

---

## [47:00–53:00] Component Encapsulation

Slide 12 — this is a topic that surprises developers new to Angular.

Here's the problem. You write `h3 { color: red }` in `product-card.component.css`. You only want that style to apply to the h3 tags inside `ProductCardComponent`. But in a regular web page, CSS is global — that rule would make every h3 in the entire application red.

Angular solves this with ViewEncapsulation. By default, it uses `Emulated` mode. Angular takes your component's CSS and programmatically adds a unique attribute selector to every rule, and stamps a matching attribute on every element inside that component.

So your `h3 { color: red }` becomes `h3[_ngcontent-xyz-c23] { color: red }`. And every h3 inside `ProductCardComponent` gets the attribute `_ngcontent-xyz-c23` added. The `h3` in your navbar — which doesn't have that attribute — is completely unaffected. The style is scoped to exactly the component that owns it.

You don't have to do anything for this to work. It's automatic. But you should understand it for two reasons.

First: if you inspect a component in the browser dev tools, you'll see these `_ngcontent-xxx` attributes. Now you know what they are.

Second: occasionally you need to style something you don't own — like a child component's internals, or a third-party library's DOM. `Emulated` encapsulation won't let your styles pierce into child components. For those cases, Angular provides the `::ng-deep` pseudo-class combinator, though it's officially deprecated. The better approach is to use component themes with CSS custom properties. But that's advanced territory — for now, know that `Emulated` mode keeps your CSS safely scoped.

The other two modes: `None` removes all encapsulation — styles apply globally. You'd use this for a component that intentionally sets global theme styles. `ShadowDom` uses the browser's native Shadow DOM API — much stronger isolation, but with some browser quirks. Use `Emulated` unless you have a specific reason not to.

---

## [53:00–58:00] Smart vs Presentational Components — The Full Pattern

Slide 15 shows a pattern that ties everything together. This is how professional Angular applications are structured.

The idea is to separate your components into two roles. Presentational components — sometimes called "dumb" components — handle only display. They receive data via `@Input` and report user actions via `@Output`. They don't inject services. They don't know where the data comes from. They just display what they're given.

Smart components — sometimes called "container" components — handle orchestration. They inject services, load data in `ngOnInit`, process user actions, and pass data down to presentational children.

Look at the example. `ProductCardComponent` has `@Input() product` and `@Output() addToCart`. No constructor injection. It doesn't know about `ProductService` or `CartService`. It's a pure display unit.

`ProductListComponent` injects both services. In `ngOnInit`, it loads products from `ProductService`. Its `handleAddToCart` method delegates to `CartService`. It passes data down via property bindings and listens for events via event bindings.

The template wires them together: `*ngFor` to stamp product cards, `[product]="p"` to pass data in, `(addToCart)="handleAddToCart($event)"` to catch events.

This is the cleanest Angular architecture. Smart components are small and focused on orchestration. Presentational components are reusable — you could use `ProductCardComponent` in three different parent containers and it would work the same everywhere.

React has the exact same pattern. You already saw it in Day 17a. The `useEffect` that fetches data is in the container; the display components are pure. This cross-framework consistency is worth noting: good component architecture principles are universal.

---

## [58:00–60:00] Day 17b Summary

Slide 17 — let's close the loop.

Five things you now know how to do in Angular.

One: component communication. `@Input` brings data in from parents. `@Output` plus `EventEmitter` sends events out to parents. Data down, events up.

Two: directives. `*ngIf` with else blocks, `*ngFor` with trackBy and exported variables, `*ngSwitch` for multi-branch display. Custom attribute directives with `@Directive` and `@HostListener`. And Angular 17+ gives you `@if`, `@for`, `@switch` as the modern alternative.

Three: pipes. Built-in pipes for formatting. Custom pipes implementing `PipeTransform`. Pure by default for performance, impure when you need to react to mutations.

Four: services and DI. `@Injectable({ providedIn: 'root' })` creates an app-wide singleton. Constructor injection gets it into any component. Services hold shared business logic and shared state. The injector hierarchy governs which instance you get.

Five: encapsulation. `ViewEncapsulation.Emulated` — the default — keeps your CSS scoped to its component automatically.

The architecture pattern tying it all together: smart container components inject services and orchestrate data flow. Presentational components are pure display units connected by `@Input` and `@Output`. Services hold the truth.

On Day 18b, we're going to add Angular Router and Forms to this picture. You'll be able to navigate between multiple views, protect routes with guards, and build fully validated reactive forms. Everything we did today — services, dependency injection, component communication — those patterns all persist and deepen. The service you built today will be injected into route guards and form validators in Day 18b. Great work today — see you Wednesday.
