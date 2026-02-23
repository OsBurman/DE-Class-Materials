# Week 4 - Day 16b: Angular Fundamentals
## Part 1 Slide Descriptions — Overview, CLI, TypeScript, Components & Templates

**Total slides:** 17
**Duration:** 60 minutes
**Part 1 Topics:** Angular overview and architecture, Angular CLI and project structure, TypeScript in Angular context, components and templates, template syntax and expressions

---

### Slide 1 — Title Slide

**Layout:** Angular's official red (#DD0031) and white on a dark background (#1a1a1a). Angular's shield logo centered above the title.
**Title:** Angular Fundamentals
**Subtitle:** Week 4 - Day 16b | Part 1: Architecture, CLI, Components & Templates
**Visual:** The Angular red/white shield logo.
**Footer:** "Building directly on TypeScript (Day 15) — Angular is TypeScript-first by design."

---

### Slide 2 — What Is Angular?

**Title:** What Is Angular?

**Content:**

Angular is a **comprehensive, opinionated frontend framework** for building web applications — developed and maintained by Google.

- First released as **AngularJS** in 2010; completely rewritten as **Angular 2** in 2016
- Angular 2+ is simply called **Angular** (not AngularJS — these are different frameworks entirely)
- Current major release: **Angular 19** (2025) — uses semantic versioning, major release every 6 months
- TypeScript-first: Angular is written in TypeScript and expects your code to be TypeScript

**React vs Angular — the key philosophical difference (both are on your Week 4 track):**

| | React | Angular |
|---|---|---|
| Type | UI library | Full framework |
| Language | JavaScript (TypeScript optional) | TypeScript (required) |
| DOM manipulation | Virtual DOM | Direct DOM (Change Detection) |
| Routing | Separate package (React Router) | Built-in (@angular/router) |
| HTTP | Separate library (Axios/fetch) | Built-in (HttpClient) |
| Forms | Third-party (React Hook Form) | Built-in (template & reactive) |
| State management | Third-party (Redux) | Services + RxJS (built-in) |
| Two-way binding | Manual (props + callbacks) | Built-in `[(ngModel)]` |
| Learning curve | Gentler initially | Steeper but more structured |

**Angular is "batteries included."** You don't choose your router, your HTTP client, your form library, or your DI system. Angular chose them for you — and they all work together out of the box.

---

### Slide 3 — Angular's Architecture: The Big Picture

**Title:** Angular Architecture — The Core Building Blocks

**Visual:** An architecture diagram showing the relationships between Angular's core concepts.

```
Angular Application
├── NgModule (AppModule)           ← Application container
│   ├── Components                 ← UI building blocks
│   │   ├── Template (.html)       ← View/markup
│   │   ├── Class (.ts)            ← Logic + data
│   │   └── Styles (.css)          ← Scoped styling
│   ├── Directives                 ← DOM behavior modifiers
│   ├── Pipes                      ← Data transformation (Day 17b)
│   └── Services                   ← Shared logic + DI (Day 17b)
└── Standalone Components (Angular 14+) ← Module-free components
```

**The six core concepts:**

| Concept | What It Does |
|---|---|
| **Module (NgModule)** | Container that groups related features; declares what's available |
| **Component** | UI building block: template + class + styles |
| **Service** | Reusable logic or data access; shared via DI (Day 17b) |
| **Directive** | Instructions to modify DOM elements or component behavior |
| **Pipe** | Transforms displayed values in templates (Day 17b) |
| **Dependency Injection** | Framework for providing dependencies to components and services (Day 17b) |

**Today's focus:** Modules, Components, basic Directives. Services, Pipes, and DI in depth: Day 17b.

---

### Slide 4 — Angular CLI: Installation and Project Creation

**Title:** The Angular CLI — Your Primary Development Tool

**Installing the Angular CLI:**
```bash
npm install -g @angular/cli
ng version   # verify installation — shows Angular CLI version
```

**Creating a new Angular project:**
```bash
ng new my-angular-app
# Prompts:
# ? Would you like to add Angular routing? (y/N)  → y
# ? Which stylesheet format would you like to use? → CSS
```

**Starting the development server:**
```bash
cd my-angular-app
ng serve
# → Application bundle generation complete.
# → Local: http://localhost:4200/
```

**The most useful CLI commands:**

| Command | Shorthand | What It Does |
|---|---|---|
| `ng new project-name` | — | Create a new Angular project |
| `ng serve` | `ng s` | Start dev server with live reload |
| `ng generate component name` | `ng g c name` | Generate a component (4 files) |
| `ng generate service name` | `ng g s name` | Generate a service |
| `ng generate module name` | `ng g m name` | Generate a module |
| `ng build` | — | Build for production |
| `ng test` | — | Run unit tests (Karma + Jasmine) |
| `ng lint` | — | Run ESLint on the project |

**Why the CLI matters:** Angular projects have a specific structure, build pipeline (esbuild/Webpack), and code generation patterns. The CLI enforces consistency and generates correctly structured boilerplate. In a team environment, "ng generate" keeps everyone's code consistent.

---

### Slide 5 — Project Structure: What `ng new` Generates

**Title:** Angular Project Structure

```
my-angular-app/
├── src/
│   ├── app/
│   │   ├── app.component.ts        ← Root component class
│   │   ├── app.component.html      ← Root component template
│   │   ├── app.component.css       ← Root component styles
│   │   ├── app.component.spec.ts   ← Unit tests (Day 20b)
│   │   └── app.module.ts           ← Root NgModule
│   ├── assets/                     ← Static files (images, fonts)
│   ├── index.html                  ← Single HTML page (like React's)
│   ├── main.ts                     ← Entry point — bootstraps the app
│   └── styles.css                  ← Global styles
├── angular.json                    ← Angular workspace configuration
├── tsconfig.json                   ← TypeScript configuration
├── tsconfig.app.json               ← App-specific TypeScript config
└── package.json
```

**Key files explained:**

**`src/index.html`** — The single HTML page. Contains `<app-root></app-root>` — the selector for the root component:
```html
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>MyAngularApp</title>
  <base href="/">
</head>
<body>
  <app-root></app-root>   ← Your entire application mounts here
</body>
</html>
```

**`src/main.ts`** — Entry point:
```typescript
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
```

**`angular.json`** — Configures build targets, assets, styles, scripts, and test configuration. The CLI reads this to know how to build your project.

---

### Slide 6 — TypeScript in Angular: A First-Class Citizen

**Title:** TypeScript in Angular — Not Optional, Not Bolted On

**Angular is written in TypeScript and requires TypeScript.** Unlike React, where TypeScript is optional, Angular projects are TypeScript projects. Every `.ts` file is TypeScript.

**How your Day 15 TypeScript knowledge applies directly in Angular:**

**Decorators (from Day 15):**
```typescript
// @Component IS a TypeScript decorator — exactly what Day 15 covered
@Component({
  selector: 'app-user-card',
  templateUrl: './user-card.component.html',
  styleUrls: ['./user-card.component.css']
})
export class UserCardComponent implements OnInit {
  // class body
}
```

**Interfaces for type safety:**
```typescript
// Define data shapes — identical to Day 15 usage
interface User {
  id: number;
  name: string;
  email: string;
  role: 'admin' | 'user' | 'viewer';
}

@Component({ /* ... */ })
export class UserListComponent {
  users: User[] = [];          // typed array
  selectedUser: User | null = null;   // union type
}
```

**TypeScript strict mode in Angular:**

`tsconfig.json` defaults:
```json
{
  "compilerOptions": {
    "strict": true,              // All strict checks enabled
    "noImplicitOverride": true,  // Must use override keyword
    "strictTemplates": true      // Type-check templates (powerful!)
  }
}
```

`strictTemplates: true` means Angular type-checks your HTML templates — if you pass the wrong type to a component prop, TypeScript catches it **in the HTML file**, not just in TypeScript files. This is a significant DX advantage.

---

### Slide 7 — The @Component Decorator

**Title:** The @Component Decorator — Angular's Component Definition

**Every Angular component is a TypeScript class decorated with @Component:**

```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-greeting',           // HTML tag name: <app-greeting>
  templateUrl: './greeting.component.html',  // external HTML template
  styleUrls: ['./greeting.component.css']    // external CSS (array)
})
export class GreetingComponent {
  // Component class — properties and methods
  message: string = 'Hello, Angular!';
}
```

**@Component metadata properties:**

| Property | Description | Example |
|---|---|---|
| `selector` | CSS selector; how you use this component in templates | `'app-greeting'` |
| `templateUrl` | Path to external HTML template file | `'./greeting.component.html'` |
| `template` | Inline HTML template (alternative to templateUrl) | `` `<h1>{{title}}</h1>` `` |
| `styleUrls` | Array of paths to external CSS files | `['./greeting.component.css']` |
| `styles` | Array of inline CSS strings (alternative) | `['h1 { color: red; }']` |
| `standalone` | Angular 14+: component without NgModule | `true` |

**Selector types:**
```typescript
selector: 'app-greeting'        // Element selector: <app-greeting>
selector: '[appHighlight]'      // Attribute selector: <div appHighlight>
selector: '.app-greeting'       // CSS class selector: <div class="app-greeting">
```
Element selectors (`app-xxx`) are the standard for components. Attribute selectors are used for directives (Part 2).

**Naming convention:** The `app-` prefix is the default project prefix set in `angular.json`. It avoids name collisions with native HTML elements and third-party components. You can change the prefix for your project (e.g., your company's abbreviation).

---

### Slide 8 — Component Files: The Three-File Structure

**Title:** Component Files — TypeScript, HTML, and CSS Together

**A typical Angular component consists of three files:**

**1. `user-card.component.ts` — The class (logic + data):**
```typescript
import { Component, OnInit } from '@angular/core';

interface User {
  id: number;
  name: string;
  email: string;
  isOnline: boolean;
}

@Component({
  selector: 'app-user-card',
  templateUrl: './user-card.component.html',
  styleUrls: ['./user-card.component.css']
})
export class UserCardComponent implements OnInit {
  user: User = {
    id: 1,
    name: 'Alice Chen',
    email: 'alice@example.com',
    isOnline: true
  };

  ngOnInit(): void {
    // Runs when component is initialized
    console.log('UserCardComponent initialized');
  }
}
```

**2. `user-card.component.html` — The template (view):**
```html
<div class="user-card">
  <h3>{{ user.name }}</h3>
  <p>{{ user.email }}</p>
  <span class="status">{{ user.isOnline ? 'Online' : 'Offline' }}</span>
</div>
```

**3. `user-card.component.css` — The styles (scoped):**
```css
.user-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
}
.status {
  font-weight: bold;
  color: green;
}
```

**Style encapsulation:** Angular styles are **scoped by default** — CSS in `user-card.component.css` applies ONLY to `UserCardComponent`'s template. Angular adds unique attribute selectors to the compiled output (e.g., `[_ngcontent-xyz-c123]`) to achieve this. No CSS leakage between components.

---

### Slide 9 — Component Class Anatomy

**Title:** Inside the Component Class

**A complete component class demonstrating the key elements:**

```typescript
import { Component, OnInit } from '@angular/core';

// Interface (TypeScript — from Day 15)
interface Product {
  id: number;
  name: string;
  price: number;
  inStock: boolean;
}

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {

  // ── Properties (these become bindable in the template) ──────────
  title: string = 'Our Products';
  products: Product[] = [];
  selectedProduct: Product | null = null;
  isLoading: boolean = true;

  // ── Constructor: for dependency injection (Day 17b) ─────────────
  constructor() {
    // Keep constructor lightweight — use ngOnInit for initialization
  }

  // ── Lifecycle hook ───────────────────────────────────────────────
  ngOnInit(): void {
    // Runs after Angular initializes all data-bound properties
    this.loadProducts();
  }

  // ── Methods (called from template event bindings) ────────────────
  loadProducts(): void {
    this.products = [
      { id: 1, name: 'Laptop',   price: 999,  inStock: true  },
      { id: 2, name: 'Mouse',    price: 29,   inStock: true  },
      { id: 3, name: 'Keyboard', price: 79,   inStock: false },
    ];
    this.isLoading = false;
  }

  selectProduct(product: Product): void {
    this.selectedProduct = product;
  }

  getFormattedPrice(price: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  }
}
```

**Key principle:** The class is the **controller** — it holds data and business logic. The template is the **view** — it displays data and captures user events. They're connected through Angular's data binding system (Part 2).

---

### Slide 10 — Templates: Angular's HTML Superset

**Title:** Angular Templates — HTML + Angular Syntax

**An Angular template is an HTML file augmented with Angular-specific syntax:**

```
Standard HTML:          Angular additions:
─────────────           ──────────────────
<div>                   {{ expressions }}      ← Interpolation
<p>                     [property]="expr"      ← Property binding
<h1>                    (event)="handler()"    ← Event binding
<input>                 [(ngModel)]="prop"     ← Two-way binding
<button>                *ngIf="condition"      ← Structural directive
<table>                 *ngFor="let x of xs"   ← Structural directive
                        #templateRef           ← Template reference variable
                        | pipe                 ← Pipe (Day 17b)
```

**The template has access to everything in the component class:**
- Properties: `user`, `title`, `isLoading`, `products`
- Methods: `handleClick()`, `getFormattedPrice(price)`
- Angular built-in directives: `*ngIf`, `*ngFor`, `ngClass`, `ngStyle`

**Templates are NOT JavaScript — they're a restricted subset of expressions:**

```html
<!-- ✅ These work in Angular templates: -->
{{ user.name }}
{{ user.name.toUpperCase() }}
{{ isAdmin ? 'Admin' : 'User' }}
{{ items.length }}
{{ getDisplayName(user) }}

<!-- ❌ These do NOT work (assignment, new, global objects): -->
{{ user.name = 'Alice' }}        ← assignment (not allowed)
{{ new Date() }}                 ← new operator (not allowed)
{{ console.log('debug') }}       ← global objects (not allowed)
{{ window.location.href }}       ← window (not allowed)
```

Angular templates are intentionally limited — they're for displaying data, not executing business logic. Logic belongs in the component class.

---

### Slide 11 — Interpolation: `{{ }}`

**Title:** Interpolation — Displaying Component Data in Templates

**Interpolation uses double curly braces to embed expressions from the component class into the template:**

```typescript
// Component class:
@Component({ selector: 'app-profile', templateUrl: '...' })
export class ProfileComponent {
  firstName: string = 'Alice';
  lastName: string  = 'Chen';
  age: number       = 30;
  joinDate: Date    = new Date('2022-03-15');
  scores: number[]  = [95, 87, 91, 88];
}
```

```html
<!-- Template: -->
<div class="profile">
  <!-- String properties -->
  <h1>{{ firstName }} {{ lastName }}</h1>

  <!-- Arithmetic expressions -->
  <p>Age: {{ age }}</p>
  <p>Birth year (approx): {{ 2024 - age }}</p>

  <!-- Method calls on values -->
  <p>Name uppercase: {{ firstName.toUpperCase() }}</p>
  <p>Full name: {{ (firstName + ' ' + lastName).trim() }}</p>

  <!-- Array operations -->
  <p>Number of scores: {{ scores.length }}</p>
  <p>Average score: {{ (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(1) }}</p>

  <!-- Ternary expression -->
  <p>Status: {{ age >= 18 ? 'Adult' : 'Minor' }}</p>
</div>
```

**The safe navigation operator `?.`:**
Prevents errors when a value might be `null` or `undefined`:
```html
<!-- Without safe navigation — throws if user is null -->
<p>{{ user.name }}</p>

<!-- With safe navigation — renders nothing if user is null -->
<p>{{ user?.name }}</p>
<p>{{ user?.address?.city }}</p>
```

**Interpolation converts everything to a string.** Whatever the expression evaluates to, Angular calls `.toString()` on it before inserting it into the DOM.

---

### Slide 12 — Template Expressions: Rules and Capabilities

**Title:** Template Expressions — What's Allowed

**Angular evaluates template expressions in the context of the component instance.** `this` is implied — you write `user.name`, not `this.user.name`.

**Expressions that work:**
```html
{{ title }}                    ← Component property
{{ getDisplayName() }}         ← Component method call
{{ items[0] }}                 ← Array access
{{ user?.email ?? 'No email' }}← Optional chaining + nullish coalescing
{{ count * 2 }}                ← Arithmetic
{{ 'Hello, ' + name + '!' }}  ← String concatenation
{{ isActive ? 'Active' : 'Inactive' }}  ← Ternary
```

**Template expression restrictions — intentional design:**
```html
{{ x = 5 }}            ← ❌ Assignment not allowed (use two-way binding)
{{ x++ }}              ← ❌ Increment/decrement not allowed
{{ new Date() }}       ← ❌ new keyword not allowed
{{ console.log() }}    ← ❌ Global objects not accessible
{{ window.location }}  ← ❌ window not accessible
{{ for (let i...) }}   ← ❌ Statements not allowed, only expressions
```

**Why these restrictions?** Templates are evaluated frequently during change detection. Side effects in templates would make change detection unpredictable and applications hard to debug. If you need complex logic, put it in the component class and expose the result as a property.

**Best practice — compute in the class, display in the template:**
```typescript
// ✅ Compute in the class:
export class StatsComponent {
  scores: number[] = [95, 87, 91];
  
  get average(): number {
    return this.scores.reduce((a, b) => a + b, 0) / this.scores.length;
  }
  
  get topScore(): number {
    return Math.max(...this.scores);
  }
}
```
```html
<!-- ✅ Display the result: -->
<p>Average: {{ average | number:'1.1-1' }}</p>
<p>Top score: {{ topScore }}</p>
```
TypeScript **getters** are perfect for derived values — they appear as properties in the template but compute their value dynamically.

---

### Slide 13 — Generating Components with the CLI

**Title:** Generating Components with `ng generate`

**The Angular CLI generates components with all boilerplate in the correct structure:**

```bash
# Generate a component:
ng generate component components/user-card
# or shorthand:
ng g c components/user-card

# Output:
# CREATE src/app/components/user-card/user-card.component.ts
# CREATE src/app/components/user-card/user-card.component.html
# CREATE src/app/components/user-card/user-card.component.css
# CREATE src/app/components/user-card/user-card.component.spec.ts
# UPDATE src/app/app.module.ts   ← automatically adds to declarations
```

**What gets generated — `user-card.component.ts`:**
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

**Useful generation flags:**
```bash
# Inline template (no separate .html file)
ng g c my-component --inline-template

# Inline styles (no separate .css file)
ng g c my-component --inline-style

# Skip test file generation
ng g c my-component --skip-tests

# Standalone component (Angular 14+)
ng g c my-component --standalone

# Specify path explicitly:
ng g c pages/dashboard
ng g c shared/components/button
```

**CLI auto-registers:** The CLI automatically adds the new component to the `declarations` array in `AppModule` (or the nearest module). You don't need to manually edit the module — one of the quality-of-life benefits of using the CLI consistently.

---

### Slide 14 — A Complete Component Example

**Title:** A Complete, Working Angular Component

**A product card component — realistic and complete:**

**`product-card.component.ts`:**
```typescript
import { Component } from '@angular/core';

interface Product {
  name: string;
  price: number;
  category: string;
  inStock: boolean;
  imageUrl?: string;
}

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
    inStock: true,
    imageUrl: '/images/headphones.jpg'
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

**`product-card.component.html`:**
```html
<div class="product-card">
  <div class="product-image">
    <img [src]="product.imageUrl || '/images/placeholder.jpg'"
         [alt]="product.name" />
  </div>
  <div class="product-info">
    <span class="category">{{ product.category }}</span>
    <h3 class="product-name">{{ product.name }}</h3>
    <p class="product-price">{{ formattedPrice }}</p>
    <p class="stock-status">
      {{ product.inStock ? 'In Stock' : 'Out of Stock' }}
    </p>
  </div>
  <button class="btn-add-to-cart"
          [disabled]="!product.inStock"
          (click)="addToCart()">
    Add to Cart
  </button>
</div>
```

---

### Slide 15 — How Angular Bootstraps

**Title:** Angular's Bootstrap Process

**How a request to `localhost:4200` becomes a running Angular application:**

```
1. Browser requests http://localhost:4200/
   ↓
2. Dev server returns src/index.html
   (contains <app-root></app-root> and Angular bundle script tags)
   ↓
3. Browser loads and executes main.ts
   platformBrowserDynamic().bootstrapModule(AppModule)
   ↓
4. AppModule is loaded — Angular reads @NgModule metadata
   declarations: [AppComponent, UserCardComponent, ...]
   imports: [BrowserModule, FormsModule, ...]
   bootstrap: [AppComponent]   ← root component
   ↓
5. Angular finds AppComponent's selector: 'app-root'
   Locates <app-root> in index.html
   Instantiates AppComponent and renders its template there
   ↓
6. AppComponent's template contains other components (selectors)
   Angular instantiates each child component recursively
   ↓
7. Application is rendered — change detection starts
   Angular watches for data changes and updates the DOM
```

**Angular 17+ standalone bootstrap (modern alternative):**
```typescript
// main.ts — standalone bootstrap (no AppModule needed)
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';

bootstrapApplication(AppComponent, {
  providers: [/* app-level providers */]
}).catch(err => console.error(err));
```

**Change Detection:** After bootstrap, Angular continuously runs change detection — monitoring component properties for changes and updating the DOM to reflect them. The default strategy runs on every browser event. `ChangeDetectionStrategy.OnPush` (advanced — Day 20b parallel) only runs when inputs change.

---

### Slide 16 — Angular DevTools

**Title:** Angular DevTools — Your Development Companion

**Install:** Chrome/Edge extension — "Angular DevTools" (published by Google/Angular team)

**What Angular DevTools shows:**

```
Components tab:
  ▾ AppComponent
    ▾ ProductListComponent
        ProductCardComponent
          Properties:
            product: {name: "Headphones", price: 79.99, ...}
            formattedPrice: "$79.99"
        ProductCardComponent
        ProductCardComponent
    ▾ HeaderComponent
        title: "My Store"

Profiler tab:
  Records a change detection cycle — shows which components were
  checked, how many times, and how long each check took.
  Essential for performance optimization.
```

**Also useful for Angular development:**
- **Browser console:** Angular prints helpful warnings — missing imports, template binding errors, deprecation notices
- **ng serve error overlay:** TypeScript and template errors shown in the terminal and browser
- **Source maps:** Enabled by default in development — debugger breakpoints map to your TypeScript source, not compiled JavaScript
- **`angular.json` → `sourceMap: true`** in the build options (default in development)

---

### Slide 17 — Part 1 Summary + Part 2 Preview

**Title:** Part 1 Complete

**Summary checklist:**

| Topic | Key Takeaway |
|---|---|
| What is Angular | Full framework (router, HTTP, forms, DI all built-in); TypeScript required |
| Angular vs React | Framework vs library; both component-based; different philosophy |
| Core building blocks | Components, Modules, Services, Directives, Pipes, DI |
| Angular CLI | `ng new` to create; `ng generate component` to scaffold; `ng serve` to run |
| Project structure | `src/app/` contains components; `app.module.ts` is root module; `main.ts` bootstraps |
| TypeScript in Angular | Decorators, interfaces, strict mode, `strictTemplates` — Day 15 knowledge applies directly |
| @Component decorator | `selector`, `templateUrl`, `styleUrls` — metadata that configures the component |
| Three-file component | `.ts` (class/logic), `.html` (template/view), `.css` (scoped styles) |
| Template expressions | Interpolation `{{ }}`; limited JS expressions; no assignments/statements/globals |
| Safe navigation | `user?.name` — prevents null reference errors |
| CSS encapsulation | Angular scopes component styles by default — no CSS leakage |
| Bootstrap process | `main.ts` → `AppModule` → `AppComponent` → component tree |
| Angular DevTools | Browser extension for inspecting component tree and profiling change detection |

**Coming up in Part 2:**
- **Data binding** — the four binding types: interpolation, property, event, two-way
- **Template reference variables** — `#ref` for direct template element access
- **Component lifecycle hooks** — `ngOnInit`, `ngOnDestroy`, `ngOnChanges`, `ngAfterViewInit`
- **NgModule in depth** — declarations, imports, exports, providers, bootstrap
- **Structural directives** — `*ngIf` for conditional rendering, `*ngFor` for list rendering
