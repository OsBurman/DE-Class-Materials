# Week 4 - Day 16b: Angular Fundamentals
## Part 2 Lecture Script — Data Binding, Lifecycle, NgModule & Directives

**Total runtime:** 60 minutes
**Delivery pace:** ~165 words/minute
**Format:** Verbatim instructor script with [MM:SS–MM:SS] timing markers

---

## [00:00–02:00] Opening

Welcome back to Part 2. In Part 1 we established the foundation: what Angular is, how the CLI works, what a component looks like, and how templates display data with interpolation. Now we go deeper.

Part 2 has four major areas. First, the four types of data binding — we saw glimpses of property binding and event binding in Part 1, and now we formalize all four. Second, template reference variables — a technique for working with DOM elements and component instances directly in the template. Third, NgModule — Angular's organizational container. And fourth, the component lifecycle and structural directives — `*ngIf`, `*ngFor`, and the hooks like `ngOnInit` and `ngOnDestroy` that let you respond to a component's moments of birth and death.

Let's start with the binding system, because everything else in Angular templates builds on it.

---

## [02:00–06:00] The Four Types of Data Binding

**[Slide 2 — The Four Types of Data Binding]**

Angular has four types of data binding, and understanding the direction of data flow in each one is critical.

**Interpolation** — `{{ expression }}` — is one-way, from the component to the template. You display a value. You saw this throughout Part 1.

**Property binding** — `[property]="expression"` — is also one-way, from the component to the template. The square brackets around `property` mean you're binding a DOM property to a component expression. The DOM property gets the value from the component.

**Event binding** — `(event)="handler()"` — is one-way in the opposite direction, from the template to the component. The parentheses around `event` mean you're listening for a DOM event and calling a component method when it fires. User interaction → component method.

**Two-way binding** — `[(ngModel)]="expression"` — is bidirectional. Changes in the component update the template. User input in the template updates the component. The `[( )]` syntax is nicknamed the "banana in a box" — a parenthesis inside square brackets. Square brackets are input (component → template), parentheses are output (template → component). Combine them and you get both.

Remember these with this mental model: square brackets are like pointing FROM the component TO the DOM. Parentheses are like catching events that come FROM the DOM TO the component. And the banana in a box — `[( )]` — does both simultaneously.

---

## [06:00–11:30] Property Binding

**[Slide 3 — Property Binding]**

Property binding binds a DOM property — not an HTML attribute — to a TypeScript expression. The distinction matters, and I want to explain it.

HTML attributes and DOM properties are not the same thing. An `<input>` element has an `value` HTML attribute and a `value` DOM property, and they behave differently. The attribute sets the initial value. The property reflects the current state. This is why Angular uses `[value]` to bind to the DOM property.

```html
<!-- Bind src property to imageUrl expression -->
<img [src]="product.imageUrl" [alt]="product.name">

<!-- Bind disabled property to the result of !product.inStock -->
<button [disabled]="!product.inStock">Add to Cart</button>

<!-- Bind the href property -->
<a [href]="profileUrl">View Profile</a>
```

The expression inside the quotes is a TypeScript expression evaluated in the context of your component class. It can be a property, a method call, a ternary, any valid expression.

Class and style binding are specialized property binding variants you'll use constantly:

```html
<!-- Add/remove a CSS class based on a boolean -->
<div [class.active]="isSelected">...</div>
<div [class.error]="hasError">...</div>
<div [class.large]="size === 'large'">...</div>

<!-- Bind inline style properties -->
<div [style.color]="textColor">...</div>
<div [style.fontSize.px]="fontSize">...</div>
<div [style.opacity]="isVisible ? 1 : 0">...</div>
```

For `[style.fontSize.px]`, the `.px` part is a unit suffix — Angular automatically appends `px` to the numeric value. You can also use `.rem`, `.em`, `.%` this way.

For attribute binding, when you need to bind to an HTML attribute that doesn't correspond to a DOM property — common for accessibility and ARIA attributes — use `[attr.name]`:

```html
<button [attr.aria-label]="buttonLabel">
<td [attr.colspan]="columnSpan">
```

---

## [11:30–17:00] Event Binding

**[Slide 4 — Event Binding]**

Event binding listens for DOM events and calls component methods in response. The parentheses wrap the event name:

```html
<button (click)="addToCart()">Add to Cart</button>
<input (input)="onSearchChange($event)">
<form (submit)="handleSubmit($event)">
<div (mouseover)="showTooltip()" (mouseout)="hideTooltip()">
```

The `$event` variable is special. Angular automatically provides it and it's the native DOM event object. For a click event it's a `MouseEvent`. For an input event it's an `Event` with a target that has a `value` property. For a keyboard event it's a `KeyboardEvent`.

In your component:

```typescript
onSearchChange(event: Event): void {
  const input = event.target as HTMLInputElement;
  this.searchTerm = input.value;
  this.filterProducts();
}

handleKeyDown(event: KeyboardEvent): void {
  if (event.key === 'Enter') {
    this.submitSearch();
  }
  if (event.key === 'Escape') {
    this.clearSearch();
  }
}

handleSubmit(event: SubmitEvent): void {
  event.preventDefault();
  this.processForm();
}
```

Notice the TypeScript types on the event parameter. `event: Event`, `event: KeyboardEvent`, `event: SubmitEvent` — these give you autocomplete and type safety when accessing properties on the event object. `event.target as HTMLInputElement` is a type assertion — TypeScript knows event.target is an EventTarget, but we know in this context it's an input element, so we assert the specific type.

For simple cases, you can do everything inline:

```html
<button (click)="count = count + 1">Increment</button>
<input (keyup.enter)="submitForm()">
```

`keyup.enter` is Angular's event filter syntax — it only fires when the Enter key is released. You can also use `keyup.space`, `keydown.escape`, etc. These are Angular-specific conveniences on top of standard DOM events.

---

## [17:00–22:00] Two-Way Binding with ngModel

**[Slide 5 — Two-Way Binding]**

Two-way binding with `[(ngModel)]` is the simplest way to keep a form input in sync with a component property. The template and the class update each other simultaneously.

```typescript
export class LoginComponent {
  username: string = '';
  password: string = '';
  rememberMe: boolean = false;
}
```

```html
<input [(ngModel)]="username" placeholder="Username">
<input [(ngModel)]="password" type="password" placeholder="Password">
<input [(ngModel)]="rememberMe" type="checkbox">
<p>Welcome, {{ username }}</p>
```

As the user types in the input, `username` in the component class updates in real time. If you change `username` in the class from code, the input immediately reflects the new value. The `<p>{{ username }}</p>` updates as you type — live feedback, no manual event handling.

**Critical requirement**: `[(ngModel)]` requires `FormsModule` to be imported in your `AppModule`. Without it, you'll get the error: "Can't bind to 'ngModel' since it isn't a known property of 'input'."

```typescript
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [BrowserModule, FormsModule],
})
export class AppModule { }
```

Add `FormsModule` to `imports` and the error disappears.

How does `[(ngModel)]` work internally? It's actually shorthand for property binding plus event binding combined:

```html
<!-- This: -->
[(ngModel)]="username"

<!-- Is equivalent to: -->
[ngModel]="username" (ngModelChange)="username = $event"
```

`[ngModel]` binds the current value into the input. `(ngModelChange)` listens for changes and updates the component property. The `[( )]` syntax is a convenience that combines both. Angular built-in forms with full validation — template-driven and reactive forms — are covered in depth on Day 18b.

---

## [22:00–27:00] Template Reference Variables

**[Slide 6 — Template Reference Variables]**

A template reference variable creates a named reference to a DOM element or component instance, and it's accessible anywhere in the same template.

The syntax is a hash sign followed by a name:

```html
<input #searchInput type="text" placeholder="Search...">
<button (click)="search(searchInput.value)">Search</button>
```

In this example, `#searchInput` creates a reference to the input element. Elsewhere in the template, `searchInput` refers to that element, and `.value` accesses its current value — no two-way binding required. This is useful when you just need to read a value once on button click rather than tracking every keystroke.

You can reference components the same way:

```html
<app-video-player #player></app-video-player>
<button (click)="player.play()">Play</button>
<button (click)="player.pause()">Pause</button>
```

If `app-video-player` is an Angular component, `#player` gives you a reference to the component instance, and you can call its public methods and access its public properties directly from the template. This is template-to-template interaction without going through the parent component class at all.

For forms, the `ngForm` directive adds helpful capabilities:

```html
<form #loginForm="ngForm" (ngSubmit)="onSubmit(loginForm)">
  <input name="username" ngModel required>
  <button type="submit" [disabled]="loginForm.invalid">Login</button>
</form>
```

`#loginForm="ngForm"` captures the `NgForm` directive instance — not just the DOM element. `loginForm.invalid` is a boolean from the form validation state. The button disables itself when the form is invalid. No TypeScript needed for this behavior — it's all in the template.

One important scope note: template reference variables are scoped to the template. They cannot be accessed directly from the component class. If you need to access a DOM element or child component from the TypeScript class, you use `@ViewChild` — a topic coming in Day 17b.

---

## [27:00–33:00] NgModule

**[Slide 7 — NgModule and AppModule]**

NgModule is Angular's mechanism for organizing an application into cohesive, reusable blocks. Let's look at the full `AppModule` and understand every part.

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { UserCardComponent } from './components/user-card/user-card.component';

@NgModule({
  declarations: [
    AppComponent,
    UserCardComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
  ],
  exports: [],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

**`declarations`** — the components, directives, and pipes that belong to this module. Every component you create must be declared in exactly one module. If a component isn't declared, Angular doesn't know it exists and it won't work. The CLI adds components here automatically on generation.

**`imports`** — the other modules this module needs. `BrowserModule` provides browser-specific functionality that every Angular web app needs — it should appear only in the root AppModule, not in feature modules. `FormsModule` provides `ngModel`. `HttpClientModule` provides `HttpClient` (Day 19b). Every module you want to use in this module's components goes in `imports`.

**`exports`** — what this module makes available to other modules that import it. For AppModule, this is typically empty. Feature modules export the components they want to share.

**`providers`** — services this module provides via dependency injection. For most services, you'll use `providedIn: 'root'` in the service itself (Day 17b covers this). Putting services in `providers` is an older pattern.

**`bootstrap`** — the root component that Angular should instantiate and insert into `index.html`. Only the root AppModule has a `bootstrap` array.

The rule: every component, directive, and pipe is declared in exactly one NgModule. Import a module to use the things it exports. Export things from a module to share them with other modules.

---

## [33:00–37:00] Standalone Components and Module Hierarchy

**[Slide 8 — Feature Modules and Standalone]**

As your Angular application grows, you organize it into feature modules. A feature module groups related components, directives, and pipes together.

```typescript
@NgModule({
  declarations: [UserListComponent, UserDetailComponent, UserCardComponent],
  imports: [CommonModule, RouterModule],
  exports: [UserCardComponent]
})
export class UserModule { }
```

Notice `CommonModule` instead of `BrowserModule`. This is a common mistake: `BrowserModule` should only be imported once, in the root `AppModule`. Feature modules should import `CommonModule`, which provides `*ngIf`, `*ngFor`, and the `async` pipe. `BrowserModule` re-exports `CommonModule`, which is why they work in `AppModule`.

Starting in Angular 14, Angular introduced **standalone components**. A standalone component doesn't need an NgModule — it manages its own imports directly.

```typescript
@Component({
  selector: 'app-greeting',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `<h1>{{ message }}</h1>`
})
export class GreetingComponent {
  message = 'Hello!';
}
```

Angular 17+ creates standalone components by default when you use `ng generate component`. The `ng new` workspace also defaults to a standalone bootstrap. You'll see standalone components in modern Angular codebases. The NgModule approach is still common in large existing codebases — you need to understand both. Conceptually they're the same component system; standalone just removes the requirement for a separate module file.

---

## [37:00–44:00] Component Lifecycle

**[Slides 9, 10 — Lifecycle Overview and ngOnInit]**

Every Angular component goes through a lifecycle: it's created, it renders, it updates as data changes, and eventually it's destroyed. Angular gives you hooks — methods you can implement — to respond at specific moments in that lifecycle.

The complete sequence is:
1. `ngOnChanges` — called when any input property changes
2. `ngOnInit` — called once after the first ngOnChanges, after inputs are set
3. `ngDoCheck` — called on every change detection cycle
4. `ngAfterContentInit` — called after projected content (ng-content) is initialized
5. `ngAfterContentChecked` — called after projected content is checked
6. `ngAfterViewInit` — called after the component's view and child views are initialized
7. `ngAfterViewChecked` — called after the view is checked
8. `ngOnDestroy` — called just before the component is destroyed

In practice, you'll use four hooks in ninety percent of your code: `ngOnInit`, `ngOnDestroy`, `ngOnChanges`, and `ngAfterViewInit`. Learn these four deeply.

**`ngOnInit`** is where you put initialization logic — things that should happen once when the component is ready.

```typescript
@Component({ selector: 'app-product-list', ... })
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  isLoading = true;
  errorMessage = '';

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    // Tomorrow: this becomes an HTTP call via a service
    // Today: hardcoded data
    this.products = [
      { id: 1, name: 'Laptop',   price: 999 },
      { id: 2, name: 'Monitor',  price: 299 },
    ];
    this.isLoading = false;
  }
}
```

**Constructor vs ngOnInit** — this distinction matters enormously. The constructor runs when JavaScript instantiates the object. At that point, Angular hasn't finished setting up the component yet — `@Input` properties from the parent aren't available, the view isn't rendered. The constructor is only for dependency injection: accepting services as parameters.

`ngOnInit` runs after Angular has finished the initial setup — all `@Input` properties are set, the component is fully configured. All initialization logic goes here.

If you call an API in the constructor and that API depends on an `@Input` property, it won't work — the input isn't set yet. If you call it in `ngOnInit`, it works. This is the practical reason the distinction matters.

---

## [44:00–49:00] ngOnDestroy and ngOnChanges

**[Slides 11, 12 — ngOnDestroy and ngOnChanges]**

**`ngOnDestroy`** runs just before Angular destroys the component and removes it from the DOM. Its primary purpose is cleanup — preventing memory leaks.

```typescript
export class TimerComponent implements OnInit, OnDestroy {
  count = 0;
  private intervalId!: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.intervalId = setInterval(() => {
      this.count++;
    }, 1000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalId);
  }
}
```

Without `ngOnDestroy`, if this component is removed from the DOM — but the interval is still running — that interval keeps executing forever, incrementing a property on a component that doesn't exist. That's a memory leak. `ngOnDestroy` stops the timer.

The same pattern applies to RxJS subscriptions (Day 19b), DOM event listeners added with `addEventListener`, and WebSocket connections. Angular 16+ introduced `takeUntilDestroyed`, a modern RxJS operator that automatically handles subscription cleanup. We'll cover that in Day 19b when we do RxJS properly.

**`ngOnChanges`** fires every time a parent component changes an `@Input` property of this component. It receives a `SimpleChanges` object:

```typescript
ngOnChanges(changes: SimpleChanges): void {
  if (changes['productId']) {
    const change = changes['productId'];
    console.log('Previous:', change.previousValue);
    console.log('Current:', change.currentValue);
    console.log('First change?', change.firstChange);
    this.loadProductDetails(change.currentValue);
  }
}
```

`@Input` is fully covered tomorrow — Day 17b — but I want you to know that `ngOnChanges` is the hook for responding to those changes.

**`ngAfterViewInit`** fires after Angular has finished rendering the component's view and all its child component views. This is the earliest point at which you can safely access DOM elements from the TypeScript class. If you need to interact with the DOM directly — measuring an element's dimensions, initializing a third-party JavaScript library, drawing on a canvas — `ngAfterViewInit` is the correct hook.

---

## [49:00–53:30] Structural Directives: *ngIf

**[Slide 13 — *ngIf]**

Structural directives change the DOM structure — they add, remove, or rearrange elements. The asterisk prefix `*` is Angular's shorthand for working with `ng-template`.

`*ngIf` conditionally includes or removes an element from the DOM:

```html
<div *ngIf="isLoggedIn">Welcome back, {{ username }}!</div>
<div *ngIf="!isLoggedIn">Please log in.</div>

<div *ngIf="isLoading">Loading...</div>
<div *ngIf="errorMessage">Error: {{ errorMessage }}</div>
```

This is fundamentally different from just hiding with CSS `display: none`. With `*ngIf="false"`, the element is **removed from the DOM entirely**. Any component inside that element is **destroyed** — its `ngOnDestroy` fires, subscriptions are cleaned up, memory is freed. With `display: none`, the element is still there, the component is still alive, subscriptions are still running.

For a loading spinner or an error message that appears occasionally, `*ngIf` is appropriate — no point keeping that in the DOM when it's not needed. For a tab panel where you want fast switching and the content is expensive to re-initialize, `display: none` or Angular's `[hidden]` binding may be preferable.

The `else` clause handles the fallback case cleanly:

```html
<div *ngIf="products.length > 0; else noProducts">
  <p>We found {{ products.length }} products.</p>
</div>
<ng-template #noProducts>
  <p>No products found. Try a different search.</p>
</ng-template>
```

`ng-template` is a blueprint — Angular doesn't render it until something explicitly uses it. The `#noProducts` is a template reference variable pointing to that blueprint. When the `*ngIf` condition is false, Angular instantiates the `#noProducts` template.

The `*ngIf as` pattern is useful for avoiding repeated null checks and handling `async` results:

```html
<div *ngIf="currentUser as user">
  <h2>{{ user.name }}</h2>
  <p>{{ user.email }}</p>
</div>
```

---

## [53:30–57:30] Structural Directives: *ngFor and Attribute Directives

**[Slides 14, 15, 16 — *ngFor and Attribute Directives]**

`*ngFor` iterates over a collection and renders the template once for each item:

```html
<ul>
  <li *ngFor="let product of products">
    {{ product.name }} — ${{ product.price }}
  </li>
</ul>
```

`*ngFor` provides local variables you can access in the template:

```html
<tr *ngFor="let user of users; let i = index; let isLast = last; let isEven = even">
  <td>{{ i + 1 }}</td>
  <td>{{ user.name }}</td>
  <td>{{ user.email }}</td>
  <td *ngIf="isLast">← Last row</td>
  <td [class.highlight]="isEven">{{ isEven ? 'Even row' : '' }}</td>
</tr>
```

`index`, `first`, `last`, `even`, `odd`, and `count` are all available.

**`trackBy`** is critical for performance in lists that update. Without it, when the data array changes — say new data comes from an API — Angular destroys and recreates all the DOM elements. With `trackBy`, Angular reuses DOM elements that correspond to unchanged items:

```typescript
trackByUserId(index: number, user: User): number {
  return user.id;
}
```

```html
<li *ngFor="let user of users; trackBy: trackByUserId">
  {{ user.name }}
</li>
```

With `trackBy`, Angular can identify which items are new, which changed, and which were removed — and it only updates the DOM for what actually changed. Always add `trackBy` when iterating over data that comes from an API.

Angular 17+ introduced a new built-in control flow syntax that replaces `*ngFor` and `*ngIf`:

```html
@for (user of users; track user.id) {
  <li>{{ user.name }}</li>
}
@if (isLoggedIn) {
  <div>Welcome back!</div>
}
```

The new syntax doesn't need `CommonModule`. Track is mandatory, not optional. The performance characteristics are similar. Both syntaxes are valid in modern Angular — the new syntax will eventually become the standard, but you'll encounter `*ngFor` and `*ngIf` in every Angular codebase for years.

**Attribute directives** modify the appearance or behavior of elements. `ngClass` and `ngStyle` are the most common:

```html
<!-- ngClass: add/remove multiple classes -->
<div [ngClass]="{ 'active': isSelected, 'error': hasError, 'large': size === 'large' }">

<!-- ngStyle: apply multiple styles -->
<div [ngStyle]="{ 'color': textColor, 'font-size': fontSize + 'px' }">
```

For single class or style, prefer the shorthand forms: `[class.active]="isSelected"` and `[style.color]="textColor"`. They're more readable and performant. Use `ngClass` and `ngStyle` when you're applying multiple classes or styles from a single expression.

---

## [57:30–60:00] Day 16b Summary

**[Slide 17 — Day 16b Summary]**

Let's close with our five learning objectives for today and check them off.

First: **Set up Angular projects using CLI** — `ng new`, `ng serve`, `ng generate component`. You know the commands, you know what they create, you know why the CLI matters.

Second: **Create components with proper structure** — three-file components, `@Component` decorator with `selector`/`templateUrl`/`styleUrls`, CSS encapsulation, class anatomy with properties and methods, `implements OnInit`.

Third: **Implement data binding techniques** — all four types. `{{ interpolation }}` for displaying data. `[property]="expr"` for one-way DOM binding including class and style variants. `(event)="handler()"` for user interaction. `[(ngModel)]="prop"` for two-way form binding. Template reference variables with `#ref`.

Fourth: **Use structural directives for dynamic templates** — `*ngIf` with else clauses and `ng-template`, `*ngFor` with local variables and `trackBy`, `ngClass` and `ngStyle` for dynamic styling.

Fifth: **Understand component lifecycle** — the full hook sequence, `ngOnInit` for initialization (constructor for DI only), `ngOnDestroy` for cleanup, `ngOnChanges` for responding to input changes, `ngAfterViewInit` for DOM access.

Looking ahead in the Angular track: tomorrow — Day 17b — we wire components together with `@Input` and `@Output`, we inject services into components, and we cover pipes. `loadProducts()` today returned hardcoded data — tomorrow it calls a service. Day 18b is Angular Routing and Forms. Day 19b is HttpClient and RxJS, where that service actually makes HTTP calls. Day 20b is Signals and testing — the modern Angular approach to reactive state.

Today was the foundation. Every Angular concept you'll learn over the next four days builds directly on what you did today. Great work.

---

*[END OF PART 2 SCRIPT]*
