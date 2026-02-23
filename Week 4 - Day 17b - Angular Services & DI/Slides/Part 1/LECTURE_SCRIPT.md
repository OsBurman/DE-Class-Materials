# Week 4 - Day 17b: Angular Services & DI
## Part 1 Lecture Script

**Duration:** 60 minutes
**Pacing:** ~165 words/minute
**Topics:** Component communication (@Input/@Output/EventEmitter), Directives (structural & attribute), *ngIf/*ngFor/*ngSwitch in depth, Built-in pipes

---

## [00:00–02:00] Opening

Good morning everyone — welcome to Day 17b. Before we get into today's material, let me do a quick check-in. Yesterday was a big day — we built our first Angular components from scratch, learned the four binding types, lifecycle hooks, and `*ngIf` and `*ngFor` at a surface level. How's everyone feeling about that? [Pause for responses.]

Good. Today we're going to take everything we built on Day 16b and make it actually useful. Here's the problem we're going to solve in the first five minutes: right now our components are islands. They exist, they display data — but they can't talk to each other. A product list can't tell a shopping cart "hey, someone just added something." A parent component has no way to pass custom data into a child. That isolation is the limitation we're fixing in Part 1.

We're covering four interconnected topics: component communication using `@Input` and `@Output`, structural directives in depth, how to build custom attribute directives, and pipes — the elegant Angular way to transform values right in your templates. Let's get into it.

---

## [02:00–06:00] Day 16b Recap — The Missing Pieces

Let's look at what we can do. Slide 2 shows the recap. We can create components, bind data in both directions, handle events, use `*ngIf` and `*ngFor` at a basic level. That's solid.

But look at the right column — what we can't do yet. We can't pass data from a parent component into a child. We can't react to events that originate inside a child. We can't filter or format displayed values without writing logic in the component class. And we definitely can't share data between sibling components like a product list and a shopping cart.

Think about the Day 16b `ProductListComponent`. It had an array of hardcoded products right there in the class. Real applications don't work that way. You have one component that displays a product card, and a separate parent that manages the list. Those need to communicate.

Today's Part 1 gives us the tools to wire Angular components together into a real application tree. Part 2 is where we solve the data sharing problem with services. Let's start at the edges of the component tree and work inward.

---

## [06:00–14:00] @Input — Passing Data Into a Child

First problem: a parent has data, a child needs to display it. How do we get it there?

Look at Slide 4. Imagine we're splitting our product list into two components — a `ProductListComponent` that manages the array and a `ProductCardComponent` that displays a single card. The card needs a product to display. The list needs to pass one down.

Here's the child component — `ProductCardComponent`. I want you to notice one line: `@Input() product!: Product`. That's it. That single decorator is the child saying: "I accept a thing called `product` of type `Product` from whoever uses me."

The exclamation mark after `product` is a TypeScript definite assignment assertion — it tells the TypeScript compiler "I know this looks uninitialized, but trust me, Angular will supply it before I need it." Without the `!`, TypeScript would give you a strict null check error.

Now look at the parent template. We have `*ngFor` looping over products and stamping out an `app-product-card` for each one. And we have `[product]="p"`. Those square brackets — you recognize those from Day 16b. That's property binding. But instead of binding to a native HTML attribute like `[src]` or `[disabled]`, we're binding to a component input.

The flow is: the parent has `p` from the loop. The square brackets say "evaluate the expression `p` and push it into the `product` input of the child." The child receives it and can use it in its own template.

This is the first direction of Angular communication: parent to child, data flowing down. The syntax is property binding pointing at an `@Input()` decorated property.

Let me show you the power of this. If you have twenty product cards, you write `*ngFor` once. Angular handles stamping out twenty `ProductCardComponent` instances, each receiving its own product. The child component doesn't know or care how many siblings it has. It just displays whatever `product` it receives.

One important rule: the child should never modify its `@Input()` directly. If `ProductCardComponent` does something like `this.product.price = 0`, it's mutating data that belongs to the parent. That's a violation of the pattern. If the child needs to change data, it should emit an event — which is exactly what we do next.

---

## [14:00–22:00] @Output and EventEmitter — Child to Parent

Now the harder direction: child to parent. Something happens in the child — a button click, a form submission, a selection. The parent needs to know about it and react. How?

Slide 5. Same `ProductCardComponent`, but now we add two things. First: `@Output() addToCart = new EventEmitter<Product>()`. The `@Output` decorator marks this as an outgoing event channel. The `EventEmitter<Product>` is the actual object that can emit values. The generic type `Product` tells TypeScript what type of value will travel through this channel.

Second: in the `onAddClick` method, we call `this.addToCart.emit(this.product)`. That's the child firing an event up to whoever is listening, carrying the product as the payload.

In the child template, we have a button with `(click)="onAddClick()"`. A click triggers the method. The method emits the event. The event travels upward.

Now in the parent template: `(addToCart)="handleAdd($event)"`. Those parentheses are event binding — we saw this with native DOM events like `(click)` and `(input)`. Here we're binding to a custom event with the same syntax. The `$event` is Angular's placeholder for the emitted value — in this case, the `Product` object.

The parent's `handleAdd` method receives that product and does whatever it wants — push it to a cart, send it to a server, show a confirmation. The parent owns the reaction; the child just reports what happened.

I want to draw your attention to Slide 6 — the mental model slide. This is probably the most important conceptual diagram in today's lesson: **data flows down, events flow up**. The parent pushes data into children through `@Input`. Children push notifications up to parents through `@Output` and `EventEmitter`. 

If you're ever confused about which way something should flow, ask yourself: is this data that a child needs to display? It flows down via `@Input`. Is this a notification about something the user did in a child? It flows up via `@Output`.

React uses the exact same pattern — props down, callbacks up. Vue has a very similar system. This is a universal pattern in component-based frontend development. Once you internalize it in Angular, you'll recognize it everywhere.

Let me tell you the single most common mistake I see here: people try to make siblings communicate directly. Component A wants to talk to Component B and they're both children of the same parent. You can't do that directly. You have to go up: Component A emits an event to the parent, the parent updates its state, and the parent passes new data down to Component B. The parent acts as the message broker between siblings. In Part 2, we'll see how services simplify this.

---

## [22:00–28:30] Directives — Deep Dive

Alright, let's shift to directives. We've been using them casually since Day 16b. Now we understand them properly.

Slide 7. What is a directive? It's a class that tells Angular how to transform the DOM. Here's the interesting part: components are technically directives — they're just directives that also have a template. So every `@Component` you've written is already a directive. The distinction is that other directive types don't have their own templates. They modify elements that already exist.

We have three types. Components — directive with a template, we know these well. Structural directives — they add, remove, or reshape DOM elements. The `*` prefix is your visual cue. And attribute directives — they change the appearance or behavior of an element without adding or removing it. `[ngClass]` and `[ngStyle]` from Day 16b are attribute directives.

Slide 8 shows something important: what the asterisk actually means. When you write `*ngIf="isLoggedIn"`, Angular transforms it internally. That's called desugaring. What Angular actually works with is this: `<ng-template [ngIf]="isLoggedIn">` wrapping your element.

Why does this matter? It explains the behavior. When `isLoggedIn` is false, the element is not there at all. It's not hidden with `display: none`. It doesn't exist in the DOM. Angular never even renders it. The `<ng-template>` is a stamping machine — when the condition is true, it stamps the element into the DOM. When false, it removes it.

This is why `*ngIf` is better than CSS hiding for security-sensitive content. If you use `display: none`, the HTML is still there in the source — a user could reveal it. With `*ngIf`, the HTML is completely absent.

---

## [28:30–36:00] *ngIf, *ngFor, *ngSwitch In Depth

Slide 9 — `*ngIf` with `else`. The basic form you know: `*ngIf="condition"`. But there's an else clause, and it's elegant.

You create a template reference variable on an `<ng-template>` — that's the `#guestBlock` in the example. Then in your `*ngIf`, you write `*ngIf="isLoggedIn; else guestBlock"`. When the condition is false, Angular renders the `guestBlock` template instead.

For even more complex cases, you have the `then` / `else` pattern. You point `then` at the success template and `else` at the fallback. This is useful when you have an `*ngIf` check on a data object — like `*ngIf="user"` — and you want to show a loading spinner until the data arrives.

And let me introduce `<ng-container>`. It's an invisible grouping element that Angular removes from the rendered output. It renders no DOM node. It's useful when you need a structural directive but you don't want an extra `<div>` cluttering your layout. You'll use this frequently when you have two structural directives that need to apply to the same logical block — since you can't put two structural directives on the same element.

Slide 10 — `*ngFor` in depth. You know the basics. Let me show you the extra variables it exports. Inside a `*ngFor`, you can pull out `index`, `first`, `last`, `even`, `odd` — all boolean or number flags that tell you about the current item's position in the list. You assign them with the `as` keyword. Look at the code: `index as i`, `first as isFirst`. Then use them anywhere in that template block.

But the most important part of this slide is `trackBy`. Look at the `trackById` function in the component class. It takes the index and the item, and returns a unique identifier — the product's `id`. You pass this function to `*ngFor` with `trackBy: trackById`.

Here's why this matters enormously: without `trackBy`, when the `products` array changes — say you refresh from an API — Angular destroys every single DOM node in the list and re-creates them all from scratch. Even if 9 out of 10 items didn't change. With `trackBy`, Angular compares the old IDs to the new IDs and only re-renders the elements that actually changed. For long lists, this is the difference between a smooth user experience and a janky one.

Make this a habit: every `*ngFor` gets a `trackBy` function. No exceptions.

Slide 11 — `*ngSwitch`. Think of it as a template-level switch statement. You bind `[ngSwitch]="someValue"` on a parent element — note the square brackets, it's a regular attribute binding, not structural. Then inside, you use `*ngSwitchCase="'value'"` on each branch, and `*ngSwitchDefault` for the fallback.

The example here is an order status badge. Given an order status string, we render a different badge — pending, shipped, delivered, cancelled, or unknown. Before `*ngSwitch`, you'd write four nested `*ngIf`s. That's ugly. Use `*ngSwitch` when you have three or more branches on the same value.

Quick decision guide: one condition? Use `*ngIf`. One condition with two branches? Use `*ngIf` with `else`. Three or more branches on one value? Use `*ngSwitch`.

---

## [36:00–43:00] Custom Attribute Directives

Now let's build our own directive. Slide 12. The goal: an `[appHighlight]` directive that changes a paragraph's background color when the user hovers over it, and resets it when they leave.

First, generate it: `ng generate directive highlight`. Angular CLI creates the file and registers it in the module.

Look at the directive class. The selector is `[appHighlight]` — square brackets make it an attribute selector, meaning "apply this directive to any element that has the `appHighlight` attribute." That's CSS attribute selector syntax applied to Angular.

We have `@Input() appHighlight = 'yellow'`. Notice the input has the same name as the selector. This is a pattern that lets you pass a value directly: `<p [appHighlight]="'lightblue'">` — you bind to the directive AND pass a value in one attribute.

`ElementRef` is injected in the constructor. This gives us a reference to the host DOM element — the actual element this directive is placed on. `this.el.nativeElement` is that DOM node, and we can manipulate it directly.

`@HostListener('mouseenter')` and `@HostListener('mouseleave')` attach event listeners to the host element. When `mouseenter` fires, we set the background color. When `mouseleave` fires, we clear it.

This is the cleanest way to add interactive DOM behavior to arbitrary elements. Instead of writing event handling code in every component that needs hover highlighting, you encapsulate it once in a directive and apply it declaratively wherever you need it.

---

## [43:00–52:00] Pipes In Depth

Let's talk about pipes. Slide 13. The concept: a pipe takes a value, transforms it for display, and returns the result. It never modifies the original data. It's a pure transformation.

The syntax: `{{ value | pipeName }}`. If the pipe accepts arguments, you pass them after colons: `{{ value | pipeName : arg1 : arg2 }}`.

Let me walk through the built-in pipes table. `date` — incredibly flexible. You pass a format string and it renders a date object exactly how you want it. `currency` — formats numbers as money, respects locale by default. `number` — the format string is `'minIntegerDigits.minFractionDigits-maxFractionDigits'`, so `'1.2-2'` means at least one integer digit, exactly two decimal places. `percent` — multiply by 100 and add the percent sign. `uppercase`, `lowercase`, `titlecase` — exactly what you'd expect. `json` — incredibly useful for debugging; dumps any object as formatted JSON right in your template. `slice` — works on arrays and strings, same as JavaScript's `slice`.

And then there's `async`. I'm going to mention it briefly and we'll come back to it on Day 19b. `async` unwraps an Observable or Promise and displays the current value. It also automatically unsubscribes when the component is destroyed. It's one of Angular's most important pipes and you'll use it constantly with HTTP calls. For now, just know it exists.

Slide 14 — chaining pipes. You can pipe the output of one pipe into another: `{{ product.name | uppercase | slice:0:20 }}`. Angular executes them left to right. Uppercase runs first, then slice takes the first 20 characters.

And the date format strings table. This trips people up. `'short'` gives you a compact date plus time. `'mediumDate'` — just the date, no time, medium format — this is what I use most often. `'fullDate'` gives you the full weekday name. Or you can write a custom format string like `'MM/dd/yyyy'` using date tokens.

One warning about chaining: if your chain has more than two pipes, it's probably a sign that the logic should move into a method in the component class. Templates should be readable. Complex multi-step transformations are easier to test and understand as a named TypeScript method.

---

## [52:00–58:00] Complete Example Walkthrough

Let's bring everything together. Slides 15 and 16.

Look at Slide 15 — the complete `ProductCard` example. This one template uses every concept we covered today. `@Input() product` and `@Input() index` — two inputs from the parent. `@Output() addToCart` — event emitter to parent.

In the template: `[ngClass]="{ 'out-of-stock': !product.inStock }"` — dynamically applies the out-of-stock CSS class based on state. `{{ product.name | titlecase }}` — pipe for display. `{{ product.price | currency }}` — another pipe. `*ngSwitch` on `product.category` — four branches. `[disabled]="!product.inStock"` — native attribute binding. And the button uses a ternary expression to change its label.

Then the parent template loops with `*ngFor`, uses `index as i` and `trackBy: trackById`, passes two `@Input` properties, and listens for one `@Output` event.

This is a realistic component. Everything has a purpose. Nothing is redundant. I want you to notice that the `ProductCardComponent` class has almost no logic — it just declares its inputs and outputs. The complexity lives in the template and the parent. That's the right balance for a presentational component.

---

## [58:00–60:00] Part 1 Summary and Handoff

Slide 17 — the summary. Let's recap the four areas we covered.

Component communication: `@Input` for data in, `@Output` plus `EventEmitter` for events out. Data flows down, events flow up.

Directives: `*ngIf` with else and ng-template, `*ngFor` with index and trackBy — which you must use, `*ngSwitch` for multi-branch display. Custom attribute directives with `@Directive`, `ElementRef`, and `@HostListener`. And Angular 17+ introduces `@if`, `@for`, `@switch` as cleaner built-in alternatives.

Pipes: the full suite of built-in pipes, chaining syntax, and the critical distinction between pure and impure.

Here's what we haven't solved yet: both `ProductListComponent` and `CartComponent` still have their data living right inside them. Our navbar badge still shows zero. Sibling components still can't share state without threading it through every parent in the tree. That's the problem Part 2 solves with Angular services and dependency injection. See you in five minutes.
