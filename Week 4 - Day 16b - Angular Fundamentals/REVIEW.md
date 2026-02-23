# Day 16b Review: Angular Fundamentals

**Week 4 — Monday (Angular Track)**
**Prerequisites:** Day 15 (TypeScript), basic HTML/CSS
**Leads into:** Day 17b (Angular Services, @Input/@Output, Pipes)

---

## Learning Objectives Checklist

By the end of Day 16b, students can:
- [ ] Set up an Angular project using the Angular CLI
- [ ] Create and structure components with proper three-file architecture
- [ ] Implement all four types of data binding in templates
- [ ] Use structural directives (`*ngIf`, `*ngFor`) for dynamic DOM rendering
- [ ] Describe the component lifecycle and use `ngOnInit` and `ngOnDestroy`

---

## Part 1: Angular Overview, CLI, TypeScript, Components & Templates

### What Is Angular?

Angular is a **full TypeScript framework** for building web applications, maintained by Google.

| Characteristic | Angular | React |
|---|---|---|
| **Type** | Full framework | UI library |
| **Language** | TypeScript (required) | JS or TypeScript (optional) |
| **Routing** | Built-in (`@angular/router`) | External (`react-router`) |
| **HTTP** | Built-in (`HttpClient`) | External (`fetch`/`axios`) |
| **Forms** | Built-in (template + reactive) | External (`react-hook-form`/etc.) |
| **DI System** | Built-in | Not included |
| **Opinionated** | High | Low |

> **AngularJS ≠ Angular**: AngularJS (2010, v1.x) is a completely different framework. Angular (2016+) is a full rewrite. `$scope`, `ng-controller`, `$http` are AngularJS. Do not confuse them.

---

### Angular's Core Building Blocks

| Block | Purpose | Covered |
|---|---|---|
| **Component** | UI unit (class + template + styles) | Day 16b ✅ |
| **Module (NgModule)** | Organizational container | Day 16b ✅ |
| **Service** | Reusable logic / data access | Day 17b |
| **Directive** | Modify DOM elements | Day 16b (basics) ✅ |
| **Pipe** | Transform displayed values | Day 17b |
| **Dependency Injection** | Provide services to classes | Day 17b |

---

### Angular CLI Commands

```bash
# Install globally
npm install -g @angular/cli
ng version

# Create a new project
ng new my-angular-app
# → Prompts: Add Angular routing? (Yes) | Stylesheet format? (CSS)

# Start development server (localhost:4200)
cd my-angular-app
ng serve

# Generate a component
ng generate component components/user-card
ng g c components/user-card    # shorthand

# Other generators
ng g s services/user           # service
ng g m features/products       # module
ng g p pipes/currency-format   # pipe

# Build / test / lint
ng build
ng test
ng lint
```

**CLI auto-registration:** `ng g c` generates four files AND adds the component to `AppModule`'s `declarations` array automatically.

---

### Project Structure

```
my-angular-app/
├── src/
│   ├── app/
│   │   ├── app.component.ts       ← Root component class
│   │   ├── app.component.html     ← Root template
│   │   ├── app.component.css      ← Root styles
│   │   ├── app.component.spec.ts  ← Root tests
│   │   └── app.module.ts          ← Root NgModule
│   ├── index.html                 ← Single HTML page (<app-root>)
│   ├── main.ts                    ← Entry point (bootstrapModule)
│   └── styles.css                 ← Global styles
├── angular.json                   ← Workspace config
└── tsconfig.json                  ← TypeScript config (strict: true)
```

**`index.html`** contains `<app-root></app-root>` — Angular mounts the entire app here.
**`main.ts`** calls `platformBrowserDynamic().bootstrapModule(AppModule)`.

---

### TypeScript in Angular

Angular **requires** TypeScript. It is not opt-in.

**Decorators from Day 15 — used directly in Angular:**
```typescript
// @Component IS a TypeScript decorator
@Component({
  selector: 'app-greeting',
  templateUrl: './greeting.component.html',
  styleUrls: ['./greeting.component.css']
})
export class GreetingComponent { }
```

**Interfaces for data shapes:**
```typescript
interface Product {
  id: number;
  name: string;
  price: number;
  inStock: boolean;
}
```

**`tsconfig.json` key settings:**
```json
{
  "compilerOptions": {
    "strict": true               // All TypeScript strict checks
  },
  "angularCompilerOptions": {
    "strictTemplates": true      // Type-check HTML template files
  }
}
```

`strictTemplates` is Angular-specific — it catches type errors in `.html` files at build time.

---

### The @Component Decorator

```typescript
@Component({
  selector: 'app-user-card',      // HTML tag: <app-user-card>
  templateUrl: './user-card.component.html',
  styleUrls: ['./user-card.component.css'],
  // OR: inline template/styles:
  // template: `<h1>{{ title }}</h1>`,
  // styles: [`h1 { color: red; }`],
  // standalone: true             // Angular 14+ (no NgModule required)
})
export class UserCardComponent { }
```

**Selector types:**
| Type | Syntax | Used As | Use For |
|---|---|---|---|
| Element | `'app-my-component'` | `<app-my-component>` | Components ✅ |
| Attribute | `'[appHighlight]'` | `<div appHighlight>` | Directives |
| Class | `'.app-menu'` | `<div class="app-menu">` | Rare |

- The `app-` prefix prevents conflicts with native HTML elements
- Custom prefix can be set in `angular.json` per project

---

### Component Files (Three-File Structure)

```typescript
// user-card.component.ts — Logic
@Component({ selector: 'app-user-card', ... })
export class UserCardComponent implements OnInit {
  // TypeScript interface for data shape
  user: User = { name: 'Alice', role: 'admin' };
  isExpanded = false;

  constructor() { }         // ← DI only — no initialization here

  ngOnInit(): void {        // ← Initialization logic here
    this.loadUserData();
  }

  toggleExpand(): void {
    this.isExpanded = !this.isExpanded;
  }
}
```

```html
<!-- user-card.component.html — View -->
<div class="card">
  <h3>{{ user.name }}</h3>
  <span [class.admin-badge]="user.role === 'admin'">{{ user.role }}</span>
  <button (click)="toggleExpand()">Toggle</button>
</div>
```

```css
/* user-card.component.css — Scoped styles */
.card { border: 1px solid #ccc; padding: 16px; }
/* These styles only apply to THIS component's template */
```

**CSS Encapsulation:** Angular adds unique attributes to compiled elements so component styles don't leak. No global CSS pollution.

---

### Templates and Interpolation

Angular templates are a **superset of HTML** — all valid HTML is valid in a template.

**Interpolation `{{ }}`:**
```html
<h1>{{ title }}</h1>
<p>{{ user.name.toUpperCase() }}</p>
<p>{{ price * quantity }}</p>
<p>{{ isAdmin ? 'Admin' : 'User' }}</p>
<p>{{ user?.address?.city }}</p>   <!-- Safe navigation operator -->
<p>{{ user?.email ?? 'No email' }}  <!-- Nullish coalescing -->
```

**What IS allowed in expressions:** property access, method calls, arithmetic, string concatenation, ternary, optional chaining `?.`, nullish coalescing `??`, template literals (limited).

**What is NOT allowed:** `if`/`for`/`while` statements, assignments (`=`), `new`, references to global objects (`window`, `console`, `Math`).

> **Why restricted?** Template expressions run during change detection, which fires frequently. Side effects would cause unpredictable behavior and performance issues.

**TypeScript getters — best practice for derived values:**
```typescript
get formattedPrice(): string {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' })
    .format(this.price);
}
```
```html
<span>{{ formattedPrice }}</span>  <!-- Looks like a property, calls the getter -->
```

---

## Part 2: Data Binding, Lifecycle, NgModule & Directives

### The Four Types of Data Binding

| Type | Syntax | Direction | Use For |
|---|---|---|---|
| **Interpolation** | `{{ expression }}` | Component → Template | Displaying values as text |
| **Property Binding** | `[property]="expr"` | Component → Template | DOM properties, class, style |
| **Event Binding** | `(event)="handler()"` | Template → Component | User interactions |
| **Two-Way Binding** | `[(ngModel)]="prop"` | Bidirectional | Form inputs |

> **"Banana in a box"** mnemonic for `[( )]` — parentheses (banana 🍌) inside square brackets (box 📦) = both directions.

---

### Property Binding

```html
<!-- DOM Properties -->
<img [src]="product.imageUrl" [alt]="product.name">
<button [disabled]="!product.inStock">Add to Cart</button>
<input [value]="searchTerm">

<!-- Class binding (single) -->
<div [class.active]="isSelected">...</div>
<div [class.error]="hasError">...</div>

<!-- Style binding (single) -->
<div [style.color]="textColor">...</div>
<div [style.fontSize.px]="fontSize">...</div>   <!-- .px is a unit suffix -->

<!-- Attribute binding (for ARIA and non-DOM attributes) -->
<button [attr.aria-label]="buttonLabel">
<td [attr.colspan]="columnSpan">
```

**HTML attribute vs DOM property:**
- HTML attribute `disabled="false"` still disables the button (attribute is always a string)
- DOM property `[disabled]="false"` correctly enables it (property is a boolean)
- Always use `[property]` binding for dynamic values

---

### Event Binding

```html
<button (click)="addToCart()">Add to Cart</button>
<input (input)="onSearch($event)">
<input (keyup.enter)="submitSearch()">  <!-- Angular event filter -->
<form (submit)="handleSubmit($event)">
```

**`$event`** is the native DOM event object:
```typescript
onSearch(event: Event): void {
  const input = event.target as HTMLInputElement;
  this.searchTerm = input.value;
}

onKeyDown(event: KeyboardEvent): void {
  if (event.key === 'Escape') this.clearSearch();
}

handleSubmit(event: SubmitEvent): void {
  event.preventDefault();
  this.processForm();
}
```

| Event | Type | Access Pattern |
|---|---|---|
| `(click)` | `MouseEvent` | `event.clientX`, `event.button` |
| `(input)` | `Event` | `(event.target as HTMLInputElement).value` |
| `(keydown)` | `KeyboardEvent` | `event.key`, `event.code` |
| `(submit)` | `SubmitEvent` | `event.preventDefault()` |

---

### Two-Way Binding with ngModel

```typescript
// app.module.ts — REQUIRED
import { FormsModule } from '@angular/forms';
@NgModule({ imports: [BrowserModule, FormsModule] })
export class AppModule { }
```

```typescript
export class LoginComponent {
  username = '';
  password = '';
  rememberMe = false;
}
```

```html
<input [(ngModel)]="username" placeholder="Username">
<input [(ngModel)]="password" type="password">
<input [(ngModel)]="rememberMe" type="checkbox">
<p>Typing: {{ username }}</p>
```

**How `[(ngModel)]` works internally:**
```html
<!-- This shorthand: -->
[(ngModel)]="username"
<!-- Expands to: -->
[ngModel]="username" (ngModelChange)="username = $event"
```

> **Common error:** "Can't bind to 'ngModel' since it isn't a known property" → Add `FormsModule` to `AppModule.imports`.

---

### Template Reference Variables

```html
<!-- Reference a DOM element -->
<input #searchInput type="text">
<button (click)="search(searchInput.value)">Search</button>

<!-- Reference a component instance -->
<app-video-player #player></app-video-player>
<button (click)="player.play()">Play</button>
<button (click)="player.pause()">Pause</button>

<!-- Reference an NgForm directive instance -->
<form #loginForm="ngForm" (ngSubmit)="onSubmit(loginForm)">
  <input name="email" ngModel required type="email">
  <button [disabled]="loginForm.invalid">Login</button>
</form>
```

- `#ref` gives a reference to the **DOM element** by default
- `#ref="directiveName"` gives a reference to a specific **directive instance**
- Template reference variables are scoped to the template — not accessible from the TypeScript class
- To access from TypeScript: use `@ViewChild` (Day 17b)

---

### NgModule: @NgModule Anatomy

```typescript
@NgModule({
  declarations: [
    AppComponent,       // Components, directives, pipes owned by this module
    UserCardComponent,  // Each component declared in EXACTLY ONE module
    HighlightDirective,
  ],
  imports: [
    BrowserModule,      // Root module only — provides browser APIs
    FormsModule,        // Provides ngModel
    // HttpClientModule — Day 19b
    // RouterModule     — Day 18b
  ],
  exports: [
    UserCardComponent,  // Shared with other modules that import this one
  ],
  providers: [
    // Services — prefer providedIn: 'root' in the service itself (Day 17b)
  ],
  bootstrap: [AppComponent]  // Root module only — the component mounted in index.html
})
export class AppModule { }
```

**NgModule rules:**
- Every component is declared in **exactly one** module
- `BrowserModule` — root AppModule only; `CommonModule` — feature modules
- A module must be imported before you can use its exported components/directives/pipes
- The CLI adds generated components to `declarations` automatically

---

### Standalone Components (Angular 14+)

```typescript
@Component({
  selector: 'app-greeting',
  standalone: true,             // No NgModule required
  imports: [CommonModule, FormsModule],  // Import directly in the component
  template: `<h1>{{ message }}</h1>`
})
export class GreetingComponent {
  message = 'Hello from a standalone component!';
}
```

- Angular 17+ generates standalone components by default
- Conceptually identical to NgModule-based components — just no separate module file
- You'll encounter NgModule-based code in most existing Angular codebases

---

### Component Lifecycle: All Hooks

```
Component Created
      ↓
  ngOnChanges     ← Called when @Input properties change (also called first here)
      ↓
  ngOnInit        ← Called ONCE after first ngOnChanges; inputs are set
      ↓
  ngDoCheck       ← Called every change detection cycle (use sparingly)
      ↓
  ngAfterContentInit    ← After ng-content is initialized
      ↓
  ngAfterContentChecked ← After ng-content is checked
      ↓
  ngAfterViewInit       ← After view + child views initialized; DOM accessible
      ↓
  ngAfterViewChecked    ← After view is checked
      ↓
  [repeat ngOnChanges, ngDoCheck, ngAfterContentChecked, ngAfterViewChecked on updates]
      ↓
  ngOnDestroy     ← Just before component is destroyed
```

**The four you'll use constantly:**

| Hook | When | Use For |
|---|---|---|
| `ngOnInit` | Once, after inputs set | API calls, initialization |
| `ngOnDestroy` | Before destruction | Cleanup: unsubscribe, clearInterval |
| `ngOnChanges` | Every `@Input` change | React to parent data changes |
| `ngAfterViewInit` | After view rendered | DOM access, third-party libs |

---

### constructor vs ngOnInit

```typescript
@Component({ ... })
export class ProductComponent implements OnInit {
  products: Product[] = [];

  // ✅ Constructor: ONLY for dependency injection
  constructor(private productService: ProductService) { }

  // ✅ ngOnInit: ALL initialization logic
  ngOnInit(): void {
    this.loadProducts();   // @Input props are available here
  }
}
```

| | `constructor` | `ngOnInit` |
|---|---|---|
| **When** | Object instantiation (before Angular finishes setup) | After Angular sets up component fully |
| **@Input props** | Not yet set | Set and available |
| **Use for** | Dependency injection | HTTP calls, init logic, reading @Input |
| **DOM access** | Never | No — use ngAfterViewInit |

> **Rule:** Constructor = DI only. ngOnInit = initialization. Always.

---

### ngOnDestroy: Preventing Memory Leaks

```typescript
export class PollingComponent implements OnInit, OnDestroy {
  data: any;
  private intervalId!: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.intervalId = setInterval(() => this.fetchData(), 5000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalId);   // ← Always clean up
  }

  private fetchData(): void { /* ... */ }
}
```

**What to clean up in ngOnDestroy:**
| Resource | Cleanup Method |
|---|---|
| RxJS subscriptions | `subscription.unsubscribe()` |
| `setInterval` / `setTimeout` | `clearInterval()` / `clearTimeout()` |
| `addEventListener` (manual) | `removeEventListener()` |
| WebSocket connections | `socket.close()` |
| Third-party library instances | Library-specific cleanup |

---

### Structural Directives: *ngIf

```html
<!-- Basic -->
<div *ngIf="isLoggedIn">Welcome, {{ username }}!</div>

<!-- With else clause -->
<div *ngIf="isLoggedIn; else loginPrompt">
  <h2>Dashboard</h2>
</div>
<ng-template #loginPrompt>
  <p>Please log in to continue.</p>
</ng-template>

<!-- *ngIf as (alias the truthy value) -->
<div *ngIf="currentUser as user">
  <h2>{{ user.name }}</h2>
</div>
```

**`*ngIf` vs `[hidden]` / `display: none`:**
| | `*ngIf="false"` | `[hidden]="true"` |
|---|---|---|
| **DOM** | Removed | Still present |
| **Component** | Destroyed (ngOnDestroy fires) | Still alive |
| **Memory** | Freed | In use |
| **Re-render cost** | On every show | None |
| **Use when** | Rarely shown | Frequently toggled |

---

### Structural Directives: *ngFor

```html
<!-- Basic iteration -->
<li *ngFor="let product of products">{{ product.name }}</li>

<!-- With local variables -->
<tr *ngFor="let user of users;
            let i = index;
            let isFirst = first;
            let isLast = last;
            let isEven = even;
            let isOdd = odd">
  <td>{{ i + 1 }}</td>
  <td>{{ user.name }}</td>
  <td [class.even-row]="isEven">{{ user.email }}</td>
</tr>

<!-- With trackBy for performance -->
<li *ngFor="let product of products; trackBy: trackByProductId">
  {{ product.name }}
</li>
```

**`trackBy` function:**
```typescript
trackByProductId(index: number, product: Product): number {
  return product.id;
}
```

Without `trackBy`: list refresh destroys/recreates all DOM elements.
With `trackBy`: only changed items are updated in the DOM.

> **Angular 17+ control flow (alternative syntax):**
> ```html
> @for (product of products; track product.id) { <li>{{ product.name }}</li> }
> @if (isLoggedIn) { <div>Welcome!</div> }
> ```
> Track is **required** in the new syntax. Both syntaxes are valid in modern Angular.

---

### Attribute Directives: ngClass and ngStyle

```html
<!-- ngClass — multiple classes from an object -->
<div [ngClass]="{
  'active':    isSelected,
  'disabled':  !isEnabled,
  'primary':   variant === 'primary',
  'large':     size === 'large'
}">

<!-- ngClass — from a method -->
<div [ngClass]="getStatusClasses()">

<!-- ngStyle — multiple inline styles -->
<div [ngStyle]="{
  'color':       textColor,
  'font-size':   fontSize + 'px',
  'font-weight': isBold ? 'bold' : 'normal'
}">
```

**When to use which:**
| Use Case | Recommended Syntax |
|---|---|
| Toggle one CSS class | `[class.active]="bool"` |
| Toggle multiple classes | `[ngClass]="{ ... }"` |
| Set one style property | `[style.color]="value"` |
| Set multiple style properties | `[ngStyle]="{ ... }"` |

---

## Complete Day 16b Example

Putting it all together — a functional component using all of Part 1 and Part 2:

```typescript
// product-list.component.ts
interface Product {
  id: number;
  name: string;
  price: number;
  category: string;
  inStock: boolean;
}

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit, OnDestroy {
  title = 'Product Catalog';
  products: Product[] = [];
  isLoading = true;
  searchTerm = '';
  selectedCategory = 'All';
  private refreshInterval!: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.loadProducts();
    this.refreshInterval = setInterval(() => this.loadProducts(), 30000);
  }

  ngOnDestroy(): void {
    clearInterval(this.refreshInterval);
  }

  loadProducts(): void {
    this.products = [
      { id: 1, name: 'Laptop Pro',   price: 1299, category: 'Electronics', inStock: true  },
      { id: 2, name: 'USB-C Hub',    price: 49,   category: 'Electronics', inStock: true  },
      { id: 3, name: 'Desk Lamp',    price: 35,   category: 'Office',      inStock: false },
      { id: 4, name: 'Ergonomic Chair', price: 399, category: 'Office',    inStock: true  },
    ];
    this.isLoading = false;
  }

  get filteredProducts(): Product[] {
    return this.products
      .filter(p => this.selectedCategory === 'All' || p.category === this.selectedCategory)
      .filter(p => p.name.toLowerCase().includes(this.searchTerm.toLowerCase()));
  }

  trackByProductId(index: number, product: Product): number {
    return product.id;
  }

  addToCart(product: Product): void {
    alert(`Added ${product.name} to cart!`);
  }
}
```

```html
<!-- product-list.component.html -->
<div class="product-list">
  <h1>{{ title }}</h1>

  <!-- Two-way binding (FormsModule required) -->
  <input [(ngModel)]="searchTerm" placeholder="Search products...">

  <!-- Template reference variable -->
  <select #categorySelect (change)="selectedCategory = categorySelect.value">
    <option>All</option>
    <option>Electronics</option>
    <option>Office</option>
  </select>

  <!-- *ngIf for loading state -->
  <div *ngIf="isLoading; else productGrid">
    <p>Loading products...</p>
  </div>

  <!-- ng-template referenced by *ngIf else -->
  <ng-template #productGrid>
    <!-- *ngFor with trackBy -->
    <div *ngFor="let product of filteredProducts; trackBy: trackByProductId"
         [ngClass]="{ 'out-of-stock': !product.inStock, 'in-stock': product.inStock }">

      <!-- Interpolation -->
      <h3>{{ product.name }}</h3>
      <p>{{ product.category }}</p>
      <p>{{ product.price | currency }}</p>

      <!-- Property binding + Event binding -->
      <button [disabled]="!product.inStock" (click)="addToCart(product)">
        {{ product.inStock ? 'Add to Cart' : 'Out of Stock' }}
      </button>
    </div>

    <!-- *ngIf for empty state -->
    <p *ngIf="filteredProducts.length === 0">
      No products match your search.
    </p>
  </ng-template>
</div>
```

---

## Common Mistakes and Fixes

| Mistake | Error | Fix |
|---|---|---|
| Using `[(ngModel)]` without `FormsModule` | "Can't bind to 'ngModel'..." | Add `FormsModule` to `AppModule.imports` |
| Forgetting `declarations` for a component | "Component is not a known element" | Run `ng g c` (CLI adds it) or add manually to `declarations` |
| Importing `BrowserModule` in a feature module | Runtime warning | Use `CommonModule` in feature modules only |
| Logic in the constructor | `@Input` values undefined | Move initialization to `ngOnInit` |
| Forgetting `ngOnDestroy` cleanup | Memory leaks | Implement `OnDestroy`, clean up subscriptions/timers |
| `*ngFor` without `trackBy` on large lists | Poor performance on data updates | Add `trackBy` function returning a unique ID |
| Using global objects in templates | Runtime error | Call from a component method instead |
| Declaring a component in two modules | Compilation error | Each component belongs to exactly one module |

---

## Quick Reference: All Binding Syntax

```html
<!-- Interpolation (one-way, component → template) -->
{{ expression }}
{{ user?.name }}
{{ price | currency }}

<!-- Property binding (one-way, component → DOM) -->
[property]="expression"
[class.active]="isActive"
[style.color]="color"
[style.fontSize.px]="size"
[attr.aria-label]="label"

<!-- Event binding (one-way, DOM → component) -->
(click)="method()"
(input)="method($event)"
(keyup.enter)="method()"
(submit)="method($event)"

<!-- Two-way binding (bidirectional) -->
[(ngModel)]="property"   <!-- Requires FormsModule -->

<!-- Template reference variable -->
#variableName
#formName="ngForm"

<!-- Structural directives -->
*ngIf="condition"
*ngIf="condition; else templateRef"
*ngFor="let item of items"
*ngFor="let item of items; let i = index; trackBy: trackFn"

<!-- Attribute directives -->
[ngClass]="{ 'class': condition }"
[ngStyle]="{ 'property': value }"
```

---

## Looking Ahead: Day 17b (Tomorrow)

| Today Built | Tomorrow Adds |
|---|---|
| `loadProducts()` → hardcoded array | `ProductService.getProducts()` → API call |
| Single `ProductListComponent` | `@Input product` on `ProductCardComponent` |
| `console.log('Added to cart')` | `CartService.addItem(product)` |
| Manual DOM interaction | `@ViewChild` for template access |
| `{{ price }}` raw number | `{{ price \| currency:'USD' }}` built-in pipe |

The component structure you built today is exactly where Day 17b picks up. Services, dependency injection, `@Input`/`@Output`, and pipes all build on the component foundation from today.

---

*Day 16b Complete — Angular Fundamentals*
