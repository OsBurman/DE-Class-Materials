# Angular Fundamentals — 1-Hour Presentation Script & Slide Guide

---

## SECTION 1: Opening & Angular Overview (8 minutes)

**SLIDE 1 — Title Slide**
*Content: "Angular Fundamentals" | Subtitle: "Components, Templates, Data Binding & the CLI" | Your name/date*

---

**SLIDE 2 — What is Angular?**
*Content: Angular logo, brief definition, key traits: component-based, opinionated, TypeScript-first, full framework (not just a library)*

**Script:**
"Good morning everyone. Today we're diving deep into Angular — one of the most widely used front-end frameworks in enterprise and production applications. Before we write a single line of code, I want to make sure we all have a solid mental model of what Angular actually is, because that context is going to make everything else click.

Angular is a platform and framework for building single-page client applications using HTML and TypeScript. I want to emphasize the word *framework* here — unlike React, which is just a UI library, Angular is an opinionated, full-featured framework. It comes with decisions already made for you: how you structure your code, how you handle forms, how you communicate with servers, how you test. Some developers love this, some find it restrictive at first — but for teams and large codebases, that consistency is incredibly valuable.

Angular is developed and maintained by Google, and it's been around in its current form since 2016. It uses TypeScript as its primary language, which we'll talk about shortly, and it's built entirely around the concept of components."

---

**SLIDE 3 — Angular Architecture Overview**
*Content: Diagram showing the key building blocks: Modules, Components, Templates, Services, Directives, Pipes, with arrows showing relationships*

**Script:**
"Let's look at the big picture architecture. Angular applications are made up of several key building blocks and understanding how they relate to each other is fundamental.

At the top level you have NgModules, which are containers that group related code together. Every Angular app has at least one — the root module, which by convention is called AppModule. I want to flag here that newer versions of Angular — version 17 and beyond — have shifted toward a different pattern called *standalone components*, which don't require NgModules at all. We'll cover NgModules today as the foundation, and I'll point out where the standalone approach differs so you're not confused when you see it in the wild.

Inside modules you have Components, which are the core building blocks of the UI. Every piece of your interface — a navigation bar, a product card, a login form — is a component. Each component has three parts: a TypeScript class, an HTML template, and CSS styles.

Then you have Templates, which define the view for a component. They look like HTML but they have Angular-specific syntax that gives them superpowers.

Directives are instructions you place in templates to manipulate the DOM — we'll cover the most important ones today.

Services are where your business logic and data fetching live — we'll revisit those in a future lesson.

And Pipes are used to transform data in templates, things like formatting dates or currency — also a future topic.

Today we're focusing primarily on Components, Templates, Data Binding, Directives, and Modules. These are the foundational pieces that everything else is built on."

---

## SECTION 2: Angular CLI & Project Structure (8 minutes)

**SLIDE 4 — What is the Angular CLI?**
*Content: CLI logo, bullet points: "Command Line Interface for Angular", common commands listed: ng new, ng serve, ng generate, ng build, ng test*

---

**SLIDE 5 — Setting Up Your First Project**
*Content: Code block showing terminal commands AND a screenshot of the default Angular welcome page at localhost:4200 side by side:*
```bash
npm install -g @angular/cli
ng new my-app
cd my-app
ng serve
```
*Screenshot: the default Angular app running in the browser at localhost:4200 — the Angular logo with "my-app is running" message*

**Script:**
"The Angular CLI — Command Line Interface — is your best friend when working with Angular. It handles the heavy lifting of scaffolding, building, testing, and serving your application. Without it, setting up an Angular project manually would be extremely tedious.

Let's walk through what happens when you set up a project from scratch.

First, you install the CLI globally on your machine using npm. This gives you the `ng` command available everywhere in your terminal.

Then you run `ng new` followed by your project name. The CLI will ask you a few questions — whether you want to add routing, and which stylesheet format you prefer, CSS, SCSS, and so on. It then generates a complete project with all dependencies installed.

`cd` into your new project folder and run `ng serve`. This compiles your application and starts a local development server. When you open your browser to localhost:4200, this is what you'll see — the default Angular welcome page. That's your app running. From here, every change you make to your code is reflected immediately without having to refresh.

That's genuinely the entire setup process. The CLI handles webpack configuration, TypeScript compilation, live reloading — all of it — completely behind the scenes."

---

**SLIDE 6 — Project Structure**
*Content: Folder tree diagram showing:*
```
my-app/
├── src/
│   ├── app/
│   │   ├── app.component.ts
│   │   ├── app.component.html
│   │   ├── app.component.css
│   │   └── app.module.ts
│   ├── main.ts
│   └── index.html
├── angular.json
├── tsconfig.json
└── package.json
```

**Script:**
"Let's orient ourselves inside the project the CLI just created.

The `src` folder is where all your application code lives. Everything else at the root level — angular.json, tsconfig.json, package.json — is configuration.

Inside `src`, the `app` folder is your main workspace. You'll see the root component already generated for you: `app.component.ts` is the TypeScript class, `app.component.html` is the template, and `app.component.css` is the styles. There's also `app.module.ts`, which is the root NgModule.

`main.ts` is the entry point of the application — it's what bootstraps, or starts up, the root module.

`index.html` is the single HTML page that gets served — it contains a single custom tag, `<app-root>`, which is where Angular injects your entire component tree.

One of the best things about this structure is that it's completely consistent across every Angular project in the world. When you join a new team using Angular, you already know exactly where to find things."

---

**SLIDE 7 — Generating with the CLI**
*Content: Code examples:*
```bash
ng generate component products
ng generate service data
ng g c user-profile   # shorthand
```
*Note: "The CLI creates the files AND updates the module automatically"*

**Script:**
"Beyond creating projects, `ng generate` is probably the command you'll use most in day-to-day development. When you generate a component, the CLI creates all four files — the TypeScript class, the HTML template, the CSS file, and the spec file for testing — and it automatically registers the component in the nearest NgModule. You never have to manually add components to your module declarations when you use the CLI. That's one of the biggest time-savers it offers."

---

## SECTION 3: TypeScript in Angular (8 minutes)

**SLIDE 8 — JavaScript vs TypeScript: What's the Difference?**
*Content: Side-by-side code comparison:*

```javascript
// JavaScript
function getTotal(price, quantity) {
  return price * quantity;
}

let product = {
  name: "Laptop",
  price: 999
};
```

```typescript
// TypeScript
function getTotal(price: number, quantity: number): number {
  return price * quantity;
}

let product: { name: string; price: number } = {
  name: "Laptop",
  price: 999
};
```
*Note: "TypeScript is a superset of JavaScript — all valid JS is valid TS"*

**Script:**
"Before we get into components, let's talk about TypeScript, because Angular is written entirely in it and you'll be writing it every day.

If you've only worked in JavaScript before, TypeScript is going to feel familiar very quickly. At its core, TypeScript is just JavaScript with one major addition: types. You can explicitly declare what type a variable, parameter, or function return value is supposed to be.

Look at the two examples side by side. The JavaScript version of `getTotal` takes two parameters, but JavaScript has no idea they're supposed to be numbers — you could accidentally pass in a string and JavaScript would happily let you. The TypeScript version says `price: number, quantity: number` — now if you try to call this function with the wrong types, TypeScript flags the error *before your code ever runs*. That's the core value proposition: catching bugs at compile time instead of at runtime.

The other thing to know is that TypeScript compiles down to regular JavaScript. The browser never sees TypeScript — Angular's build process handles the compilation step entirely. You write TypeScript, Angular compiles it, the browser runs JavaScript. You get all the safety of types without any browser compatibility concerns.

You don't have to be a TypeScript expert to write Angular, but understanding the basics will save you a lot of head-scratching."

---

**SLIDE 9 — Classes and Decorators**
*Content: Code example showing a class with a decorator:*

```typescript
// A plain TypeScript class
class ProductCardComponent {
  productName: string = 'Laptop';
  price: number = 999;

  getFormattedPrice(): string {
    return `$${this.price}`;
  }
}

// The same class — now Angular knows what it is
@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html'
})
class ProductCardComponent {
  productName: string = 'Laptop';
  price: number = 999;

  getFormattedPrice(): string {
    return `$${this.price}`;
  }
}
```
*Callout: "@Component is a decorator — it attaches metadata to the class above it"*

**Script:**
"The most important TypeScript concept in Angular is the class. Every component, service, and module in Angular is a class. If you're coming from object-oriented languages like Java or C#, this will feel natural. If you've mostly written functional JavaScript, think of a class as a blueprint that bundles related data and behavior together.

Look at the top example — a plain TypeScript class. It has typed properties and a method. That's it. But Angular doesn't know anything about it yet.

Now look at the bottom example. The only thing we added is `@Component` with a configuration object above the class. That's a *decorator*. A decorator is a special function that attaches metadata to whatever sits directly below it. When Angular sees `@Component`, it reads that metadata — the selector, the template path — and now understands this class is a component and exactly how to use it.

You'll see decorators constantly in Angular: `@Component`, `@NgModule`, `@Injectable` for services, `@Input` and `@Output` for component communication. They're all the same concept — attaching instructions to a class so Angular knows what role it plays."

---

**SLIDE 10 — Interfaces: Defining the Shape of Your Data**
*Content: Code example:*

```typescript
// Without an interface — no safety
let product: any = { name: 'Laptop', price: 999 };
product.pric = 899; // typo — no error caught!

// With an interface — TypeScript has your back
interface Product {
  id: number;
  name: string;
  price: number;
}

let product: Product = { id: 1, name: 'Laptop', price: 999 };
product.pric = 899; // TypeScript error: 'pric' does not exist on type 'Product'

// Use it to type an array
products: Product[] = [];
```

**Script:**
"The third TypeScript concept you'll use heavily in Angular is interfaces. An interface defines the shape of an object — the properties it should have and what types those properties are.

Look at the top example. If you use `any` to type your data, TypeScript basically turns off — a typo like `pric` instead of `price` slips right through.

Now look at the interface version. We define a `Product` interface with three typed properties. Now if we try to set a property that doesn't exist on `Product`, TypeScript catches it immediately. If we try to assign a string to `price`, TypeScript catches it. If we try to access a property we haven't defined, TypeScript catches it.

In Angular you'll typically define interfaces for every data model in your app — products, users, orders, whatever your app deals with. You'd put them in their own files, import them into your component, and use them to type your properties and method parameters. This might seem like extra work at first, but in a large codebase it is genuinely invaluable."

---

## SECTION 4: Components & Templates (10 minutes)

**SLIDE 11 — Anatomy of a Component**
*Content: Full code example of a simple component:*
```typescript
@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {
  productName: string = 'Laptop';
  price: number = 999;
}
```
*Annotations: arrow to selector ("custom HTML tag used in parent templates"), arrow to templateUrl ("points to this component's HTML file"), arrow to styleUrls ("scoped CSS — won't leak to other components")*

**Script:**
"Let's look at the anatomy of a component in detail.

Every component starts with the @Component decorator. Inside the decorator, you provide a configuration object with three key properties.

`selector` is the custom HTML tag you'll use to place this component in other templates. So if your selector is `app-product-card`, you put `<app-product-card></app-product-card>` in a parent template and Angular will render this component there.

`templateUrl` points to the HTML file for this component. You can also use `template` with a backtick string for inline templates, but for anything beyond a few lines, a separate file is much cleaner.

`styleUrls` is an array pointing to the CSS files for this component. These styles are scoped to this component only — Angular does this automatically using a technique called view encapsulation. CSS you write in a component's stylesheet won't leak out and affect other components. That's a huge benefit.

Below the decorator is the class itself. The properties and methods you define on this class become the data and behavior for the component. Simple as that."

---

**SLIDE 12 — Component Templates**
*Content: HTML template example showing Angular template syntax alongside regular HTML. Annotations pointing out interpolation {{ }}, property binding [ ], event binding ( )*

**Script:**
"The template is the view layer of your component. It looks almost exactly like regular HTML — and in fact it IS valid HTML with Angular-specific additions layered on top.

In a template you can reference the component's class properties and methods directly. The template and the class are tightly coupled — the template has access to everything the class exposes.

This is where Angular's template syntax comes in, and it's what makes Angular templates so powerful. We're going to spend the next section going through this in detail because data binding is one of the most important concepts in the entire framework."

---

## SECTION 5: Data Binding (12 minutes)
> ⚠️ **Pacing note:** This is the densest section. Budget extra time for questions here before moving on.

**SLIDE 13 — The Four Types of Data Binding**
*Content: Clean diagram showing the four types with their syntax:*

| Type | Syntax | Direction |
|---|---|---|
| Interpolation | `{{ expression }}` | Class → Template |
| Property Binding | `[property]="expression"` | Class → Template |
| Event Binding | `(event)="handler()"` | Template → Class |
| Two-Way Binding | `[(ngModel)]="property"` | Both |

*Arrow diagram showing direction of data flow for each*

**Script:**
"Data binding is the mechanism that connects your component's TypeScript class to its HTML template. It's how data gets into the view and how user interactions get back to the class. There are four types and each one has a distinct syntax — once you learn these, you'll be able to read Angular templates fluently.

Let me go through each one."

---

**SLIDE 14 — Interpolation**
*Content:*
```typescript
// component class
title: string = 'My Angular App';
price: number = 49.99;
```
```html
<!-- template -->
<h1>{{ title }}</h1>
<p>Price: ${{ price }}</p>
<p>{{ 2 + 2 }}</p>
<p>{{ title.toUpperCase() }}</p>
```

**Script:**
"Interpolation uses double curly braces — {{ }} — and it's the simplest form of data binding. You put an expression inside the braces and Angular evaluates it and renders the result as text in the DOM.

The expression can be a property from your class, a mathematical operation, a method call, or a ternary. The only things you can't do in interpolation are assignments, complex logic like loops, or calls to `new`. Templates are for displaying data, not for heavy computation — keep your logic in the class.

This is one-way binding from the class to the template — if `title` changes in the class, the template automatically updates. That reactivity is one of Angular's most powerful features."

---

**SLIDE 15 — Property Binding**
*Content:*
```html
<!-- component class has: imageUrl = 'logo.png'; isDisabled = true; -->

<img [src]="imageUrl">
<button [disabled]="isDisabled">Submit</button>
<input [value]="username">
```
*Note: "Square brackets bind to DOM properties, not HTML attributes"*

**Script:**
"Property binding lets you set the properties of HTML elements or child components dynamically. The syntax is square brackets around the property name, followed by an expression in quotes.

A common point of confusion here — you're binding to DOM properties, not HTML attributes. Most of the time these map directly, but they're not always identical. `src` is both an attribute and a property, so `[src]` works intuitively. But something like `[disabled]` evaluates to a boolean — if the expression is true, the button is disabled; if false, it's enabled. You're manipulating the live DOM, not the HTML markup.

Like interpolation, this is one-way — from the class to the template."

---

**SLIDE 16 — Event Binding**
*Content:*
```typescript
// component class
onClick() {
  console.log('Button clicked!');
}
onInputChange(event: Event) {
  const value = (event.target as HTMLInputElement).value;
}
```
```html
<button (click)="onClick()">Click Me</button>
<input (input)="onInputChange($event)">
```

**Script:**
"Event binding goes in the other direction — from the template to the class. The syntax is parentheses around the event name, followed by the handler in quotes.

Any browser event works here — click, input, keydown, mouseover, blur, and so on. You pass in the name of a method from your class and Angular calls it when the event fires.

Notice `$event` — that's a special keyword in Angular templates that captures the native browser event object. So if you want to know what a user typed, you pass `$event` to your handler and pull the value out of `event.target`.

This is the only direction of data flow that goes FROM the template TO the class. It's what lets your UI communicate user actions back to your component's logic."

---

**SLIDE 17 — Two-Way Data Binding**
*Content:*
```typescript
// Must import FormsModule in your NgModule
username: string = '';
```
```html
<input [(ngModel)]="username">
<p>Hello, {{ username }}</p>
```
*Diagram showing bidirectional arrows between class and template*

*Note at bottom of slide: "Think of [( )] as 'banana in a box' — it's property binding and event binding combined into one"*

**Script:**
"Two-way binding combines property binding and event binding into a single, elegant syntax. It's sometimes called the 'banana in a box' syntax — parentheses inside square brackets — [()].

The most common usage is with `ngModel` on form inputs. When you bind `[(ngModel)]` to a property, two things happen simultaneously: the input's value is kept in sync with the class property, AND any time the user types in the input, the class property is immediately updated.

So if I have `username = 'Alice'` in my class and I bind it with `[(ngModel)]`, the input will show 'Alice'. If the user clears it and types 'Bob', the class property automatically becomes 'Bob'. They stay perfectly in sync in real time.

Important note: `ngModel` lives in `FormsModule`, so you must import `FormsModule` in your NgModule for it to work. I'll remind you of this when we get to modules.

*[If time allows:]* Under the hood, two-way binding is really just shorthand for writing a property binding and an event binding at the same time — `[value]="username" (input)="username = $event.target.value"`. Angular gives us the `[()]` syntax as a convenience wrapper so we don't have to write both every time."

---

## SECTION 6: Template Reference Variables (4 minutes)

**SLIDE 18 — Template Reference Variables**
*Content:*
```html
<!-- # declares a reference variable -->
<input #myInput type="text">
<button (click)="logValue(myInput.value)">Log</button>

<!-- Reference to a component -->
<app-child #childRef></app-child>
<button (click)="childRef.someMethod()">Call Child</button>
```

**Script:**
"Template reference variables are a feature that's easy to overlook but incredibly useful. By adding a hash symbol followed by a name to any element in your template, you create a local variable that references that element or component instance.

In the example here, `#myInput` creates a reference to the input element. Now anywhere else in that template, `myInput` gives you the actual DOM element, so `myInput.value` gives you what the user typed — without needing to go through an event object or two-way binding.

You can also put a reference variable on a component element, in which case the reference points to the component instance itself, giving you access to its public properties and methods directly from the template.

Template reference variables only exist within the template — they're not accessible in the TypeScript class unless you use `@ViewChild`, which we'll cover in a later lesson. But for simple template-to-template interactions, they're a very clean solution."

---

## SECTION 7: Component Lifecycle Hooks (8 minutes)

**SLIDE 19 — The Component Lifecycle**
*Content: Clean two-item focused list with a brief note:*

| Hook | When it runs | Use it for |
|---|---|---|
| `ngOnInit` ⭐ | Once, after component inputs are ready | Fetch data, set up initial state |
| `ngOnDestroy` ⭐ | Just before the component is removed | Clean up subscriptions, cancel timers |

*Note at bottom: "Angular has 8 lifecycle hooks in total — we'll introduce the rest as real use cases come up. These two cover the vast majority of what you'll need."*

---

**SLIDE 20 — Implementing Lifecycle Hooks**
*Content:*
```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({ selector: 'app-example', template: '' })
export class ExampleComponent implements OnInit, OnDestroy {

  ngOnInit(): void {
    console.log('Component initialized');
    // fetch data, set up subscriptions
  }

  ngOnDestroy(): void {
    console.log('Component destroyed');
    // clean up subscriptions, timers
  }
}
```

**Script:**
"Every Angular component goes through a lifecycle — it gets created, it renders, it updates when data changes, and eventually it gets destroyed. Angular gives you lifecycle hooks — methods you can implement on your component class to run code at specific moments in that lifecycle. There are eight hooks in total, but today we're focusing on the two you'll use in nearly every component you ever write.

The `constructor` runs first, before Angular does anything. It should only be used for dependency injection — do not try to access input properties or the DOM here, because Angular hasn't set those up yet. We'll talk more about dependency injection in the services lesson.

`ngOnInit` is the big one. It runs once, after Angular has fully initialized the component. This is where you put your initialization logic: fetching data from a server, setting up initial state, subscribing to observables. The golden rule is: anything you're tempted to put in the constructor, put in `ngOnInit` instead.

`ngOnDestroy` runs just before Angular removes the component from the DOM. This is critical for cleanup — canceling HTTP requests, unsubscribing from observables, clearing timers. Failing to clean up here is one of the most common sources of memory leaks in Angular applications.

The remaining hooks are more specialized and we'll introduce them as you actually need them. For now, `ngOnInit` and `ngOnDestroy` will handle the vast majority of what you need.

To use a lifecycle hook, you implement the corresponding interface — `implements OnInit` — and define the method. The interface isn't strictly required, but it's best practice because TypeScript will warn you if you've misspelled the method name."

---

## SECTION 8: Modules & NgModule (5 minutes)

**SLIDE 21 — NgModule**
*Content:*
```typescript
@NgModule({
  declarations: [
    AppComponent,
    ProductCardComponent,
    UserProfileComponent
  ],
  imports: [
    BrowserModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```
*Annotations — declarations: "components/directives/pipes you own"; imports: "other modules whose features you need"; providers: "services — covered next lesson"; bootstrap: "the first component Angular renders"*

---

**SLIDE 22 — NgModule: What Each Property Does**
*Content: Four-row table:*

| Property | What goes here | Example |
|---|---|---|
| `declarations` | Your components, directives, pipes | `ProductCardComponent` |
| `imports` | Modules from Angular or third parties | `FormsModule`, `BrowserModule` |
| `providers` | Services (covered next lesson) | `UserService` |
| `bootstrap` | The root component Angular starts with | `AppComponent` |

**Script:**
"NgModule is the organizational container of Angular. Every Angular application has a root module — `AppModule` — and the `@NgModule` decorator tells Angular everything it needs to know about that module.

`declarations` is where you register every component, directive, and pipe that belongs to this module. If you create a component and don't add it here, Angular won't know it exists and you'll get an error when you try to use it. This is what the CLI does automatically for you when you generate a component.

`imports` is where you bring in functionality from other modules. `BrowserModule` provides all the essential browser functionality and is required in your root module. `FormsModule` gives you `ngModel` for two-way binding — and this is what I mentioned when we covered two-way binding. If `ngModel` isn't working, check that `FormsModule` is imported here. `HttpClientModule` gives you HTTP capabilities. Angular's features are organized into modules and you opt into them explicitly.

`providers` is where services get registered — we'll cover that in the services lesson.

`bootstrap` tells Angular which component to render first when the app starts. This is always your root component, AppComponent.

As your application grows, you'll create feature modules to organize related functionality — a `ProductsModule` containing everything related to products, for example. This also enables lazy loading, which we'll cover in the routing lesson.

One more thing before we move on: Angular 14 introduced *standalone components*, and as of Angular 17 they are the default when you create a new project. Standalone components don't need to be declared in an NgModule at all — instead, they import their own dependencies directly in the `@Component` decorator, and the app is bootstrapped with `bootstrapApplication()` instead of a root module. You will see this in modern projects and tutorials. We're teaching NgModule today because it builds the right mental model first — once you understand what NgModule does, standalone components will make immediate sense because you'll know exactly what they're replacing."

---

## SECTION 9: Directives (8 minutes)

**SLIDE 23 — What are Directives?**
*Content: Three types defined:*

| Type | What it does | Example |
|---|---|---|
| **Structural** | Adds or removes DOM elements | `*ngIf`, `*ngFor` |
| **Attribute** | Changes appearance or behavior of an element | `[ngClass]`, `[ngStyle]` |
| **Component** | A directive with its own template | Every component you write |

*Focus note: "Today: Structural Directives — the ones you'll use every single day"*

---

**SLIDE 24 — *ngIf**
*Content:*
```typescript
// component class
isLoggedIn: boolean = true;
user = { name: 'Alice', role: 'admin' };
```
```html
<div *ngIf="isLoggedIn">Welcome back!</div>

<div *ngIf="user.role === 'admin'; else regularUser">
  Admin Panel
</div>
<ng-template #regularUser>
  <p>Regular Dashboard</p>
</ng-template>
```

**Script:**
"Structural directives change the structure of the DOM by adding or removing elements. The asterisk prefix — `*ngIf`, `*ngFor` — is what tells you you're looking at a structural directive.

`*ngIf` conditionally renders an element based on a truthy or falsy expression. If the expression is true, the element is added to the DOM. If it's false, the element is completely removed — not just hidden with CSS, actually removed from the DOM entirely. This is important to understand because when you remove a component with `*ngIf`, its lifecycle hooks fire, including `ngOnDestroy`.

You can also use the `else` clause by creating an `<ng-template>` block with a template reference variable. When the condition is false, Angular renders that template block instead. This keeps both branches of your logic together in one place."

---

**SLIDE 25 — *ngFor**
*Content:*
```typescript
// component class
products = [
  { id: 1, name: 'Laptop', price: 999 },
  { id: 2, name: 'Mouse', price: 29 },
  { id: 3, name: 'Keyboard', price: 79 }
];

trackByProductId(index: number, product: any): number {
  return product.id;
}
```
```html
<ul>
  <li *ngFor="let product of products; trackBy: trackByProductId">
    {{ product.name }} - ${{ product.price }}
  </li>
</ul>

<!-- with index -->
<div *ngFor="let item of products; let i = index">
  {{ i + 1 }}. {{ item.name }}
</div>
```
*Note: "trackBy tells Angular how to identify each item — only changed items re-render instead of the whole list"*

**Script:**
"`*ngFor` iterates over a collection and stamps out a template for each item. If you know JavaScript's `for...of` loop, the syntax is very intentionally similar — `let product of products`.

For each item in the products array, Angular creates a new `<li>` element with that item's data. Add an item to the array in your class and the DOM updates automatically. Remove an item and Angular removes the corresponding element.

You can access useful local variables inside `*ngFor`. `index` gives you the current iteration number starting at zero. There's also `first`, `last`, `even`, and `odd` — booleans useful for things like alternating row colors in a table.

I've also included `trackBy` in this example, and I want to take a minute on it because it's worth building the habit now. `trackBy` tells Angular how to uniquely identify each item in the list. Without it, when the list changes, Angular throws away every DOM element and re-renders the entire list from scratch. With `trackBy`, Angular checks each item's identity, finds which ones actually changed, and only updates those. You provide a function that returns a unique identifier — typically the item's `id`. For short static lists it doesn't matter much, but as soon as your lists are dynamic or large, `trackBy` is something you'll want to have."

---

## SECTION 10: Putting It All Together (5 minutes)

**SLIDE 26 — Full Component Example**
*Content: Complete, annotated component using every concept from today:*

```typescript
// product-list.component.ts
import { Component, OnInit } from '@angular/core';

interface Product {
  id: number;
  name: string;
  price: number;
}

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];          // ← typed with interface
  cart: Product[] = [];
  searchTerm: string = '';

  ngOnInit(): void {                 // ← lifecycle hook
    this.products = [                // ← initialization logic here, not constructor
      { id: 1, name: 'Laptop',   price: 999 },
      { id: 2, name: 'Mouse',    price: 29  },
      { id: 3, name: 'Keyboard', price: 79  }
    ];
  }

  addToCart(product: Product): void {
    this.cart.push(product);
  }

  trackById(index: number, product: Product): number {
    return product.id;
  }
}
```

---

**SLIDE 27 — Full Template Example**
*Content: The matching template with annotations:*

```html
<!-- product-list.component.html -->

<h2>Products ({{ products.length }})</h2>   <!-- interpolation -->

<input [(ngModel)]="searchTerm"             <!-- two-way binding -->
       placeholder="Search products...">

<ul>
  <li *ngFor="let product of products; trackBy: trackById">  <!-- *ngFor -->
    <span [class.highlight]="product.price > 500">           <!-- property binding -->
      {{ product.name }} — ${{ product.price }}              <!-- interpolation -->
    </span>
    <button (click)="addToCart(product)">Add to Cart</button> <!-- event binding -->
  </li>
</ul>

<p *ngIf="cart.length === 0">Your cart is empty.</p>  <!-- *ngIf -->
<p *ngIf="cart.length > 0">                           <!-- *ngIf -->
  {{ cart.length }} item(s) in cart
</p>
```

**Script:**
"Let's bring everything together. Here is a complete, working component — the kind you'll be writing by the end of this week.

In the TypeScript file: we define a `Product` interface so our data is typed. We initialize our `products` array in `ngOnInit` — not the constructor. We have an `addToCart` method the template will call. We have a `trackById` function for `*ngFor` performance.

In the template: `{{ products.length }}` is interpolation. `[(ngModel)]` on the search input is two-way binding — that `searchTerm` property updates in real time as the user types. `*ngFor` renders one `<li>` per product, using `trackBy` for performance. `[class.highlight]` is property binding — it conditionally adds a CSS class when the price is over 500. The `(click)` on the button is event binding — it calls `addToCart` and passes the product. The two `*ngIf` blocks at the bottom show the right message depending on whether the cart is empty.

Every piece of syntax we covered today is in this one component. Notice the pattern: square brackets for data going IN, parentheses for events coming OUT, double curly braces for text content, asterisk for structural directives. Once this pattern clicks, Angular templates are very readable."

---

## SECTION 11: Wrap-Up (2 minutes)

**SLIDE 28 — Key Takeaways**
*Content:*
- Angular is a full framework built around components
- The CLI handles scaffolding, serving, building, and generating
- TypeScript adds types, interfaces, classes, and decorators — Angular is built entirely on these
- Components = TypeScript class + HTML template + CSS
- Four data binding types: `{{ }}`, `[ ]`, `( )`, `[( )]`
- Lifecycle hooks: start with `ngOnInit` (init logic) and `ngOnDestroy` (cleanup)
- NgModule organizes your app; standalone components are the modern default in Angular 17+
- `*ngIf` and `*ngFor` control DOM structure dynamically; use `trackBy` with `*ngFor`

---

**SLIDE 29 — What's Coming Next**
*Content: Preview of upcoming lessons:*
- `@Input` / `@Output` — component communication
- Services & Dependency Injection
- Routing & Navigation
- Reactive & Template-Driven Forms
- HTTP & the `HttpClient`
- Standalone Components in depth

**Script:**
"Let's recap what we covered today. You now know how Angular is structured at a high level. You can create a project with the CLI and understand what every file does. You understand TypeScript classes, decorators, and interfaces — the building blocks Angular is made of. You can build components and connect them to templates using all four types of data binding. You can tap into the component lifecycle with `ngOnInit` and `ngOnDestroy`. You understand NgModule and that modern Angular is moving toward standalone components. And you can use `*ngIf` and `*ngFor` to build dynamic, data-driven templates.

For practice before the next class: build the product list component from the last two slides yourself, from scratch, without looking at the slides. Then extend it — add a button to remove items from the list, and add a `*ngIf` that shows a 'no products' message when the list is empty. That single exercise will cement every concept we covered today.

Any questions before we wrap up?"