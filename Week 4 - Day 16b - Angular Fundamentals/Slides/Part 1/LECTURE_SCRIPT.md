# Week 4 - Day 16b: Angular Fundamentals
## Part 1 Lecture Script — Architecture, CLI, TypeScript, Components & Templates

**Total runtime:** 60 minutes
**Delivery pace:** ~165 words/minute
**Format:** Verbatim instructor script with [MM:SS–MM:SS] timing markers

---

## [00:00–02:30] Opening

Welcome to Week 4, Day 16b — Angular Fundamentals. If you're in this session, you're on the Angular track. The React track is running the same week in parallel — by the end of Week 4, both tracks converge on the same backend and you'll understand how both major frontend frameworks approach the same problems differently.

Let me set expectations for today. Angular has more concepts upfront than React, so there's more to absorb on day one. React starts simpler and adds complexity as you go. Angular gives you the full structure immediately, and once it clicks, it's very powerful and very organized. Both are excellent choices — the industry uses both extensively.

Today, Day 16b, is Angular Fundamentals. Part 1 covers the big picture: what Angular is and how it's structured, the Angular CLI, TypeScript's role in Angular, how to create and structure components, and template syntax. Part 2 covers the four types of data binding, template reference variables, the component lifecycle, NgModule, and the two most-used structural directives — `*ngIf` and `*ngFor`.

I'll be connecting everything to what you've already learned — particularly TypeScript from Day 15, because Angular and TypeScript are deeply intertwined. The decorators you learned in Day 15 are the foundation of Angular's entire component system.

Let's start.

---

## [02:30–07:00] What Is Angular?

**[Slide 2 — What Is Angular?]**

Angular is a comprehensive, opinionated frontend framework for building web applications. It was created and is maintained by Google. The key word in that description is "framework" — not "library."

React, which the parallel track is learning today, is a library. It handles UI rendering and that's it. You bring your own router, your own HTTP client, your own form handling. Angular is the opposite: it's a full framework that ships with a router, an HTTP client, a forms module, a dependency injection system, a test runner, a build tool, and a CLI. Everything you need to build a production application is included and designed to work together.

There's an important naming distinction I have to make right away because it trips up a lot of people searching for tutorials online. AngularJS — with the "JS" — is the original framework from 2010. It's a completely different codebase, a different API, a different philosophy. In 2016, Google did a complete rewrite from scratch and released Angular 2. That rewrite is simply called "Angular" — no version suffix in the name. If you search for Angular tutorials and find something that shows `ng-controller` or `$scope`, that's AngularJS — it's a different framework, and that knowledge doesn't transfer. Make sure you're looking at Angular 2+.

As of today, we're on Angular 19. Angular follows semantic versioning with a major release every six months. The core API is stable — the jump from Angular 14 to Angular 19 is not like jumping from AngularJS to Angular 2. It's incremental improvements, not a rewrite.

Now, some of you are going to ask: why learn both React and Angular? Because the industry uses both. If you apply to a company using React and you know Angular, you're already thinking in components and you'll learn React quickly. Same in reverse. And many larger organizations have both — a React customer-facing app and an Angular internal tool, or vice versa. Knowing both frameworks makes you significantly more employable than knowing only one.

The key philosophical difference is this: React is a library you compose into a framework. Angular is a framework that's already composed for you. Angular is more opinionated — there's an Angular way to do most things. This is both a constraint and a feature.

---

## [07:00–11:00] Angular's Architecture

**[Slide 3 — Angular Architecture]**

Let me walk you through Angular's six core building blocks, because these are the vocabulary you need to understand everything else we cover.

The first is the **Component** — this is the fundamental UI unit. Same concept as React components — a component is a piece of UI with its own template, class, and styles. Every visible thing on an Angular page is a component.

The second is the **Module**, or NgModule. This is Angular's way of organizing an application into cohesive units. A module declares which components, directives, and pipes belong to it, and which external modules it needs. The root module — AppModule — is what Angular bootstraps when your app starts.

The third is **Services**. Services are reusable pieces of logic or data access that can be shared across components. The canonical example: you have a user service that fetches user data from an API. Instead of putting that HTTP call in three different components, you put it in one service that all three can inject and use. Services are covered in depth tomorrow — Day 17b.

The fourth is **Directives**. Directives are instructions to modify DOM elements or component behavior. Angular has two types: structural directives that change the DOM layout — `*ngIf` and `*ngFor` — and attribute directives that change the appearance or behavior of existing elements, like `ngClass` and `ngStyle`. We cover these in Part 2 today.

The fifth is **Pipes**. Pipes transform displayed values in templates. Date formatting, currency formatting, uppercase, JSON display — these are all built-in pipes. You can create custom pipes. Deep dive on pipes is tomorrow — Day 17b.

The sixth is **Dependency Injection** — Angular's system for providing services to components without components having to create those services themselves. It's a powerful pattern, and it's how Angular's entire services system works. Covered tomorrow.

Today's focus is modules, components, and basic directives. Write that down — the rest follows.

---

## [11:00–15:30] The Angular CLI

**[Slide 4 — Angular CLI]**

The Angular CLI — Command Line Interface — is not optional. You will use it for everything. Let me walk you through the essential commands.

First, install the CLI globally:

```bash
npm install -g @angular/cli
ng version   # verify
```

Then create a project:

```bash
ng new my-angular-app
```

The CLI will ask you two questions. First: do you want to add Angular routing? Say yes for any real project. Second: which stylesheet format? Pick CSS for now. Sass is popular but CSS keeps things simple today.

Then:

```bash
cd my-angular-app
ng serve
```

The development server starts at `localhost:4200` with live reload — changes in your code reflect in the browser without a manual refresh.

The command you'll use most frequently, aside from `ng serve`, is `ng generate component`. When you want to create a new component:

```bash
ng generate component components/user-card
# or shorthand:
ng g c components/user-card
```

This creates four files: the TypeScript class, the HTML template, the CSS file, and a spec file for tests. It also automatically adds the component to `AppModule`'s declarations array. You don't manually edit the module file — the CLI does it.

Why does the CLI matter this much in Angular, when in React you just create a file? Because Angular has very specific conventions about how files are structured, named, and registered. The CLI enforces those conventions consistently. In a team of ten developers, everyone's components look the same, are in the right place, and are registered correctly. The larger the team, the more this matters.

Other useful commands: `ng build` compiles your application for production. `ng test` runs unit tests. `ng lint` runs ESLint. You'll use all of these in a real project workflow.

---

## [15:30–21:00] Project Structure

**[Slide 5 — Project Structure]**

Let me walk through the project structure that `ng new` generates.

The `src/` directory is where all your application code lives.

Inside `src/app/` you'll find your application's modules and components. By default you get the root AppComponent — four files: `app.component.ts` (the class), `app.component.html` (the template), `app.component.css` (the styles), and `app.component.spec.ts` (the tests). You'll also find `app.module.ts` — the root NgModule.

`src/index.html` is your single HTML page. Open it and you'll see it's nearly empty — there's a `<app-root></app-root>` tag in the body. That's the selector for AppComponent, and Angular mounts your entire application into that element.

`src/main.ts` is the entry point — it calls `platformBrowserDynamic().bootstrapModule(AppModule)`, which tells Angular: start the application with AppModule as the root.

`src/styles.css` is for global styles — fonts, CSS resets, utility classes that should apply across the entire application. Individual component styles go in the component's `.css` file.

At the root you have `angular.json` — this is the workspace configuration file. It defines build targets, test configuration, asset paths, global style imports. You'll rarely edit it directly, but it's what the CLI reads to understand how to build your project.

`tsconfig.json` is the TypeScript configuration. Angular sets `strict: true` by default, which means all TypeScript strict checks are enabled. You'll also notice `strictTemplates: true` — this is Angular-specific and means your HTML templates are type-checked. If you pass a number to a component that expects a string, TypeScript catches it in the HTML file. This is one of Angular's big DX advantages.

---

## [21:00–26:00] TypeScript in Angular

**[Slide 6 — TypeScript in Angular]**

This is where your TypeScript knowledge from Day 15 pays off immediately.

Angular is not just TypeScript-compatible — it is written in TypeScript. The Angular framework source code is TypeScript. Angular's entire design assumes you're writing TypeScript. This is fundamentally different from React, where TypeScript is opt-in.

The most direct connection from Day 15: decorators. Day 15 taught you what a TypeScript decorator is — a function that takes a class and adds metadata or behavior to it. In Angular, `@Component`, `@NgModule`, `@Injectable`, `@Input`, `@Output` are all TypeScript decorators. Not Angular magic — TypeScript decorators applied to classes.

```typescript
@Component({
  selector: 'app-greeting',
  templateUrl: './greeting.component.html',
})
export class GreetingComponent {
  message: string = 'Hello, Angular!';
}
```

That `@Component(...)` is a decorator. It's the exact pattern from Day 15's decorator section — a function call with a configuration object that runs against the class.

Interfaces from Day 15 are used everywhere in Angular. When you define the shape of your component's data — a User object, a Product object — you use a TypeScript interface:

```typescript
interface User {
  id: number;
  name: string;
  email: string;
  role: 'admin' | 'user';
}
```

Union types, generics, optional properties, the `implements` keyword — all the TypeScript features from Day 15 are in regular use in Angular code.

The `strictTemplates` option in `tsconfig.json` means Angular runs TypeScript's type checker on your HTML templates. This catches a class of bugs that React doesn't — passing the wrong type to a component property, or accessing a property that doesn't exist on a type, is caught at build time in the HTML file itself. Once you've worked with this, going back to untyped templates feels uncomfortable.

---

## [26:00–32:00] The @Component Decorator

**[Slide 7 — The @Component Decorator]**

Let's look at the @Component decorator — the heart of every Angular component.

```typescript
@Component({
  selector: 'app-greeting',
  templateUrl: './greeting.component.html',
  styleUrls: ['./greeting.component.css']
})
export class GreetingComponent {
  message: string = 'Hello, Angular!';
}
```

Three properties I want to walk through.

**`selector`** — this defines the HTML tag that represents this component. I've got `'app-greeting'`, which means I can use this component in any template with `<app-greeting></app-greeting>`. That tag gets replaced with the component's rendered output. The `app-` prefix is the default project prefix. It prevents name collisions with native HTML elements — there's no native `<app-greeting>` element. You can change the prefix in `angular.json` to match your company or project name.

Selectors can be element selectors — `'app-component-name'` — which is what you use for components. They can also be attribute selectors — `'[appHighlight]'` — which you'll see for directives. For components, always use an element selector with the `app-` prefix and kebab-case.

**`templateUrl`** — the path to the HTML template file. This is the relative path from the component's TypeScript file to its HTML file. Alternatively, you can use `template` for inline HTML: `template: '<h1>Hello</h1>'`. For anything more than a line or two, use `templateUrl` and a separate file — your editor will provide HTML syntax highlighting and linting.

**`styleUrls`** — an array of paths to CSS files. It's an array because you can have multiple stylesheets. Use `styles` for inline CSS. The critical behavior here is encapsulation: styles in a component's CSS file apply ONLY to that component's template. Angular does this by adding generated attribute selectors to your compiled output. This is called ViewEncapsulation — the default mode is Emulated, which means no CSS from `user-card.component.css` leaks into `product-card.component.css`.

---

## [32:00–38:00] Component Files and Class Anatomy

**[Slides 8 & 9 — Component Files and Class Anatomy]**

A typical Angular component consists of three files. Let me walk through each.

The TypeScript file contains the class. This is where all logic, data, and methods live. The HTML file contains the template — the view. The CSS file contains scoped styles for this component only.

Let's look at a realistic component class:

```typescript
@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  title: string = 'Our Products';
  products: Product[] = [];
  isLoading: boolean = true;

  constructor() { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.products = [
      { id: 1, name: 'Laptop',   price: 999, inStock: true  },
      { id: 2, name: 'Mouse',    price: 29,  inStock: true  },
      { id: 3, name: 'Keyboard', price: 79,  inStock: false },
    ];
    this.isLoading = false;
  }
}
```

Properties declared in the class — `title`, `products`, `isLoading` — are bindable in the template. You can display them with interpolation, bind them to DOM properties, or change them in event handlers.

`implements OnInit` tells TypeScript that this class will have an `ngOnInit` method, and TypeScript will enforce that the method signature is correct. You don't have to use `implements` for lifecycle hooks — Angular calls them by name — but it's best practice because it catches spelling mistakes.

The `constructor` is for dependency injection. When you add services to your component — which we do on Day 17b — they go in the constructor parameter list. Rule: keep the constructor lightweight. No HTTP calls, no DOM access, no complex initialization in the constructor. That goes in `ngOnInit`.

Methods like `loadProducts` are callable from templates via event binding. Any `public` method on the class is accessible in the template.

---

## [38:00–44:00] Templates and Interpolation

**[Slides 10, 11 — Templates and Interpolation]**

The template is an HTML file with Angular-specific syntax added on top. Everything in a standard HTML file is valid in an Angular template. Angular adds a layer of bindings, directives, and expressions on top.

The simplest and most common Angular template feature is interpolation — double curly braces:

```html
<h1>{{ title }}</h1>
<p>{{ user.name }}</p>
<p>{{ firstName.toUpperCase() }}</p>
<p>{{ age >= 18 ? 'Adult' : 'Minor' }}</p>
```

Interpolation evaluates a JavaScript expression in the context of the component class, converts the result to a string, and inserts it into the HTML. `this` is implicit — you write `user.name`, not `this.user.name`.

The safe navigation operator is essential and you'll use it constantly:
```html
<p>{{ user?.address?.city }}</p>
```

Without the `?.`, if `user` is null or `address` is undefined, Angular throws a runtime error. With `?.`, it evaluates to `undefined`, Angular converts it to an empty string, and nothing renders. Safer, cleaner.

Now, what can go in the curly braces? Expressions — things that produce a value. Variable references, method calls, arithmetic, ternary operators, string concatenation, optional chaining, the nullish coalescing operator `??`. All of these work.

What cannot go in curly braces? Statements. `if` blocks, `for` loops, `while` loops, assignments, `new` expressions, references to `window` or `console` — none of these work. Angular template expressions are intentionally restricted because they run during change detection, which happens frequently. Side effects in templates would create unpredictable behavior.

The guideline is: compute in the component class, display in the template. If you need complex logic — calculating a formatted date, deriving a sorted list, computing a status message — do it in a method or TypeScript getter, then bind to the result in the template.

TypeScript getters are particularly elegant for this:
```typescript
get formattedPrice(): string {
  return '$' + this.price.toFixed(2);
}
```

In the template, `{{ formattedPrice }}` looks like a property access but calls the getter. Clean, readable, type-safe.

---

## [44:00–49:00] Generating Components with the CLI

**[Slide 13 — Generating Components]**

Let me show you how to generate components with the CLI, because the specific syntax matters.

```bash
ng generate component components/user-card
```

This creates the component inside a `components/user-card/` directory. The four files generated are: `user-card.component.ts`, `user-card.component.html`, `user-card.component.css`, and `user-card.component.spec.ts`.

What does the generated TypeScript file look like?

```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-user-card',
  templateUrl: './user-card.component.html',
  styleUrls: ['./user-card.component.css']
})
export class UserCardComponent {

}
```

Minimal boilerplate. Correctly named selector. Correctly configured paths. It's ready to use.

And here's the key behavior: the CLI automatically adds `UserCardComponent` to the `declarations` array of `AppModule`. Open `app.module.ts` after running `ng generate` and you'll see it's already there. You didn't have to manually register it. Forget to add a component to `declarations` and it won't work — the CLI prevents this class of error entirely.

A few flags worth knowing:
- `--skip-tests` skips the `.spec.ts` file if you're not writing tests right now
- `--standalone` generates a standalone component that doesn't need an NgModule
- `--inline-template` puts the HTML in the TypeScript file instead of a separate file

For team consistency, always use `ng generate` rather than creating files manually. The naming conventions, file structure, and module registration all get handled automatically.

---

## [49:00–54:00] A Complete Component Example

**[Slide 14 — Complete Component]**

Let me show you a complete, realistic component — the kind you'd actually write in a project.

```typescript
// product-card.component.ts
@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {
  product: Product = {
    name: 'Wireless Headphones',
    price: 79.99,
    category: 'Electronics',
    inStock: true
  };

  get formattedPrice(): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(this.product.price);
  }

  addToCart(): void {
    console.log(`Added ${this.product.name} to cart`);
  }
}
```

And the template:

```html
<div class="product-card">
  <span class="category">{{ product.category }}</span>
  <h3>{{ product.name }}</h3>
  <p>{{ formattedPrice }}</p>
  <p>{{ product.inStock ? 'In Stock' : 'Out of Stock' }}</p>
  <button [disabled]="!product.inStock" (click)="addToCart()">
    Add to Cart
  </button>
</div>
```

Let me call out the two binding types in this template that we haven't formally covered yet. `[disabled]="!product.inStock"` — that's property binding. The square brackets bind the `disabled` DOM property to the result of `!product.inStock`. When `inStock` is false, `!inStock` is true, and the button becomes disabled. `(click)="addToCart()"` — that's event binding. Parentheses bind to a DOM event and call the component method. We cover both in depth in Part 2.

Notice the template is clean — no complex logic, no string manipulation inside curly braces. The `formattedPrice` getter does the formatting; the template just displays it. This is the Angular way.

---

## [54:00–58:00] How Angular Bootstraps

**[Slide 15 — Bootstrap Process]**

Let me briefly walk through what happens when someone opens your Angular application in a browser.

The browser requests `index.html`. The server sends back that near-empty HTML page containing `<app-root></app-root>` and script tags for the Angular bundles. The browser loads and executes `main.ts`, which calls `platformBrowserDynamic().bootstrapModule(AppModule)`.

Angular reads the `@NgModule` metadata on `AppModule`. It sees `declarations: [AppComponent, ...]`, `imports: [BrowserModule, ...]`, and `bootstrap: [AppComponent]`. It creates the component tree starting from `AppComponent`.

Angular finds `AppComponent`'s selector — `'app-root'` — in `index.html`. It replaces that element with `AppComponent`'s rendered template. That template contains other component selectors, which Angular processes recursively. Within milliseconds, the full component tree is rendered.

After bootstrap, Angular starts change detection. The change detection system monitors your component properties for changes and updates the DOM to match. Every time you click a button, every time a timer fires, Angular checks if any component properties changed and re-renders the affected parts of the DOM.

This is different from React's Virtual DOM approach. React diffs two JavaScript trees to find the minimum DOM updates. Angular's default change detection walks the component tree from the root and checks every component. For large applications, `ChangeDetectionStrategy.OnPush` tells Angular to only check a component when its inputs change — a significant performance optimization. That's covered in the Angular Signals day (Day 20b) when you'll also learn about Signals, which make change detection more granular.

---

## [58:00–60:00] Part 1 Summary

**[Slide 17 — Part 1 Summary]**

Let me close Part 1 with a quick summary.

Angular is a full TypeScript framework. It includes routing, HTTP, forms, and DI — you don't compose these from separate libraries, they're part of Angular. The tradeoff: more to learn upfront, but everything is designed to work together.

The Angular CLI is your development workflow — `ng new` to create, `ng generate component` to scaffold, `ng serve` to run. Use the CLI consistently and it handles module registration for you.

Every Angular component has three files: TypeScript class, HTML template, CSS styles. The `@Component` decorator with `selector`, `templateUrl`, and `styleUrls` wires them together. The `selector` is the HTML tag you use in other templates. CSS is scoped by default.

Template expressions use `{{ }}` for interpolation. They're limited to expressions — no statements, no assignments, no globals. Compute in the class, display in the template. Getters are excellent for derived values. The safe navigation operator `?.` prevents null reference errors.

In Part 2, we make the data binding system explicit — covering all four binding types formally — and we add lifecycle hooks, NgModule, and structural directives. Short break, and we'll pick back up.

---

*[END OF PART 1 SCRIPT]*
