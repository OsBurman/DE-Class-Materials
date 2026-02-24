# Day 17b — Angular Services & DI: Complete Reference

**Topics:** Component Communication, Directives, Pipes, Services, Dependency Injection, Encapsulation

---

## Table of Contents
1. [Component Communication — @Input & @Output](#1-component-communication)
2. [Structural Directives In Depth](#2-structural-directives)
3. [Attribute Directives](#3-attribute-directives)
4. [Built-in Pipes](#4-built-in-pipes)
5. [Custom Pipes](#5-custom-pipes)
6. [Services & Dependency Injection](#6-services--dependency-injection)
7. [Injector Hierarchy & Providers](#7-injector-hierarchy--providers)
8. [Sharing State Across Components](#8-sharing-state-across-components)
9. [Component Encapsulation](#9-component-encapsulation)
10. [Smart vs Presentational Components](#10-smart-vs-presentational-components)
11. [Complete Working Example](#11-complete-working-example)
12. [Common Mistakes & Fixes](#12-common-mistakes--fixes)
13. [Quick Reference Syntax](#13-quick-reference-syntax)
14. [Looking Ahead — Day 18b](#14-looking-ahead--day-18b)

---

## 1. Component Communication

### The Rule
> **Data flows DOWN (via `@Input`). Events flow UP (via `@Output` + `EventEmitter`).**

### @Input — Parent to Child

```typescript
// child: product-card.component.ts
import { Component, Input } from '@angular/core';

@Component({ selector: 'app-product-card', templateUrl: '...' })
export class ProductCardComponent {
  @Input() product!: Product;    // ! = will be provided by parent (definite assignment)
  @Input() index!: number;
}
```

```html
<!-- parent template -->
<app-product-card
  [product]="selectedProduct"    <!-- property binding into @Input -->
  [index]="0">
</app-product-card>
```

### @Output + EventEmitter — Child to Parent

```typescript
// child
import { Component, Input, Output, EventEmitter } from '@angular/core';

export class ProductCardComponent {
  @Input() product!: Product;
  @Output() addToCart = new EventEmitter<Product>();   // type = what you emit

  onAdd(): void {
    this.addToCart.emit(this.product);   // fires the event with payload
  }
}
```

```html
<!-- child template -->
<button (click)="onAdd()">Add to Cart</button>

<!-- parent template -->
<app-product-card
  [product]="p"
  (addToCart)="handleAdd($event)">    <!-- $event = emitted value (Product) -->
</app-product-card>
```

```typescript
// parent class
handleAdd(product: Product): void {
  this.cart.push(product);
}
```

### Key Rules

| Rule | Why |
|---|---|
| Never modify `@Input()` directly in the child | Parent owns the data; emit an event to request a change |
| `EventEmitter<T>` generic type | Tells TypeScript what type the `$event` will be |
| Siblings can't communicate directly | Route through parent: A emits → parent reacts → parent passes to B |

---

## 2. Structural Directives

### *ngIf — Conditional Rendering

```html
<!-- Basic -->
<div *ngIf="isLoggedIn">Welcome!</div>

<!-- With else -->
<div *ngIf="isLoggedIn; else guestBlock">Welcome back!</div>
<ng-template #guestBlock>
  <p>Please log in.</p>
</ng-template>

<!-- With then and else -->
<ng-container *ngIf="user; then userCard; else loadingSpinner"></ng-container>
<ng-template #userCard><app-user-card [user]="user"></app-user-card></ng-template>
<ng-template #loadingSpinner><p>Loading...</p></ng-template>
```

**Key fact:** `*ngIf="false"` removes the element from the DOM entirely — it is not in the HTML source. This is different from `display: none` (still in DOM, just hidden).

**`<ng-container>`:** Invisible grouping element. Renders no DOM node. Use when you need a structural directive host but don't want an extra `<div>`.

### *ngFor — List Rendering

```html
<ul>
  <li *ngFor="let product of products;
              index as i;
              first as isFirst;
              last as isLast;
              even as isEven;
              trackBy: trackById">
    <span [class.bold]="isFirst">{{ i + 1 }}. {{ product.name }}</span>
  </li>
</ul>
```

```typescript
// Always provide trackBy for performance
trackById(index: number, product: Product): number {
  return product.id;
}
```

**Exported variables:**

| Variable | Type | Description |
|---|---|---|
| `index` | `number` | 0-based position |
| `first` | `boolean` | Is this the first item? |
| `last` | `boolean` | Is this the last item? |
| `even` | `boolean` | Is index even? |
| `odd` | `boolean` | Is index odd? |

**`trackBy` rule:** Always use it. Without `trackBy`, Angular destroys and re-creates every DOM node on any array change. With it, only changed items are re-rendered.

### *ngSwitch — Multi-Branch Rendering

```html
<div [ngSwitch]="order.status">          <!-- [ngSwitch] is attribute binding, NOT structural -->
  <span *ngSwitchCase="'pending'">⏳ Pending</span>
  <span *ngSwitchCase="'shipped'">🚚 Shipped</span>
  <span *ngSwitchCase="'delivered'">✅ Delivered</span>
  <span *ngSwitchDefault">❓ Unknown</span>
</div>
```

**Decision guide:**

| Situation | Use |
|---|---|
| Simple true/false | `*ngIf` |
| Two branches | `*ngIf` with `else` |
| 3+ branches on one value | `*ngSwitch` |

### Angular 17+ Built-in Control Flow

```html
<!-- @if replaces *ngIf -->
@if (isLoggedIn) {
  <p>Welcome back!</p>
} @else {
  <p>Please log in.</p>
}

<!-- @for replaces *ngFor — track is required (not optional!) -->
@for (product of products; track product.id) {
  <app-product-card [product]="product" />
} @empty {
  <p>No products found.</p>
}

<!-- @switch replaces *ngSwitch -->
@switch (status) {
  @case ('active') { <span class="green">Active</span> }
  @case ('inactive') { <span class="gray">Inactive</span> }
  @default { <span>Unknown</span> }
}
```

**Note:** Angular 17+ built-in control flow doesn't require `CommonModule` imports and has better TypeScript type narrowing.

---

## 3. Attribute Directives

### Built-in Attribute Directives

```html
<!-- [ngClass] — dynamic CSS classes -->
<div [ngClass]="{ 'active': isActive, 'disabled': !isEnabled }">...</div>
<div [ngClass]="['card', 'highlight']">...</div>   <!-- array of class names -->

<!-- [ngStyle] — dynamic inline styles -->
<div [ngStyle]="{ 'color': textColor, 'font-size.px': fontSize }">...</div>
```

### Custom Attribute Directive

```typescript
// ng generate directive highlight
import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({ selector: '[appHighlight]' })
export class HighlightDirective {
  @Input() appHighlight = 'yellow';   // input name = selector name

  constructor(private el: ElementRef) {}

  @HostListener('mouseenter') onEnter() {
    this.el.nativeElement.style.backgroundColor = this.appHighlight;
  }

  @HostListener('mouseleave') onLeave() {
    this.el.nativeElement.style.backgroundColor = '';
  }
}
```

```html
<p appHighlight>Yellow on hover</p>
<p [appHighlight]="'lightblue'">Blue on hover</p>
```

**Key components:**

| Part | Purpose |
|---|---|
| `@Directive({ selector: '[appX]' })` | Attribute selector (square brackets) |
| `ElementRef` | Reference to the host DOM element |
| `@HostListener('event')` | Attaches listener to the host element |
| `@Input()` with same name as selector | Lets you pass a value with the directive |

---

## 4. Built-in Pipes

### Pipe Syntax

```
{{ value | pipeName }}
{{ value | pipeName : arg1 : arg2 }}
{{ value | pipe1 | pipe2 }}     ← chaining (left to right)
```

### Reference Table

| Pipe | Example | Output |
|---|---|---|
| `date` | `{{ today \| date:'mediumDate' }}` | Feb 23, 2026 |
| `date` (custom) | `{{ today \| date:'MM/dd/yyyy' }}` | 02/23/2026 |
| `currency` | `{{ 9.99 \| currency:'USD' }}` | $9.99 |
| `number` | `{{ 3.14159 \| number:'1.2-2' }}` | 3.14 |
| `percent` | `{{ 0.85 \| percent }}` | 85% |
| `uppercase` | `{{ 'hello' \| uppercase }}` | HELLO |
| `lowercase` | `{{ 'HELLO' \| lowercase }}` | hello |
| `titlecase` | `{{ 'hello world' \| titlecase }}` | Hello World |
| `json` | `{{ obj \| json }}` | `{ "key": "val" }` |
| `slice` | `{{ items \| slice:0:3 }}` | First 3 items |
| `async` | `{{ obs$ \| async }}` | Unwrapped Observable value |

### Date Format Tokens

| Token | Output |
|---|---|
| `'short'` | 2/23/26, 3:45 PM |
| `'mediumDate'` | Feb 23, 2026 |
| `'longDate'` | February 23, 2026 |
| `'fullDate'` | Monday, February 23, 2026 |
| `'MM/dd/yyyy'` | 02/23/2026 |
| `'HH:mm:ss'` | 15:45:00 |

---

## 5. Custom Pipes

### Anatomy

```typescript
// ng generate pipe phone-number
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'phoneNumber' })        // name used in template
export class PhoneNumberPipe implements PipeTransform {

  transform(value: string, format: string = 'us'): string {
    //       ↑ first arg = value  ↑ additional args = colon-separated in template
    if (!value) return '';
    const digits = value.replace(/\D/g, '');
    if (format === 'us' && digits.length === 10) {
      return `(${digits.slice(0,3)}) ${digits.slice(3,6)}-${digits.slice(6)}`;
    }
    return value;
  }
}
```

**Register in NgModule:**
```typescript
@NgModule({
  declarations: [PhoneNumberPipe, ...]   // ← must declare custom pipes here
})
```

### Pure vs Impure

| | Pure (default) | Impure (`pure: false`) |
|---|---|---|
| When it runs | Only when input reference changes | Every change detection cycle |
| Performance | ✅ Fast — Angular memoizes | ⚠️ Slow — runs constantly |
| Detects array mutations | ❌ No | ✅ Yes |
| Use case | Formatting, calculation | Filter pipes, `async` pipe |

```typescript
@Pipe({ name: 'filter', pure: false })   // ← impure
export class FilterPipe implements PipeTransform {
  transform(items: any[], searchTerm: string, field: string): any[] {
    if (!items || !searchTerm) return items;
    return items.filter(item =>
      item[field]?.toString().toLowerCase().includes(searchTerm.toLowerCase())
    );
  }
}
```

```html
<app-product-card
  *ngFor="let p of products | filter:searchTerm:'name'; trackBy: trackById"
  [product]="p">
</app-product-card>
```

---

## 6. Services & Dependency Injection

### Creating a Service

```typescript
// ng generate service product
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'    // ← app-wide singleton, tree-shakable
})
export class ProductService {
  private products: Product[] = [
    { id: 1, name: 'Laptop', price: 999, category: 'electronics', inStock: true },
    { id: 2, name: 'T-Shirt', price: 25, category: 'clothing', inStock: true },
  ];

  getProducts(): Product[] { return this.products; }
  getById(id: number): Product | undefined {
    return this.products.find(p => p.id === id);
  }
}
```

### Injecting a Service

```typescript
import { Component, OnInit } from '@angular/core';
import { ProductService } from '../services/product.service';

@Component({ selector: 'app-product-list', templateUrl: '...' })
export class ProductListComponent implements OnInit {
  products: Product[] = [];

  constructor(private productService: ProductService) {}
  //          ↑ declare dependency — Angular resolves and injects it

  ngOnInit(): void {
    this.products = this.productService.getProducts();
    //              ↑ use dependency — in ngOnInit, never in constructor
  }
}
```

### The Golden Rule

```typescript
// ❌ WRONG — creates a new separate instance, can't be mocked, Angular can't manage it
private service = new ProductService();

// ✅ CORRECT — Angular injects the shared singleton
constructor(private service: ProductService) {}
```

### Services Injecting Services

```typescript
@Injectable({ providedIn: 'root' })
export class OrderService {
  constructor(
    private cart: CartService,
    private productService: ProductService
  ) {}

  placeOrder(): void {
    const items = this.cart.getItems();
    // process order...
    this.cart.clear();
  }
}
```

---

## 7. Injector Hierarchy & Providers

### The Hierarchy

```
Root Injector  (providedIn: 'root')
  └── One instance for entire application

Module Injector  (NgModule.providers)
  └── One instance per module (useful for lazy-loaded feature modules)

Element Injector  (@Component.providers)
  └── One instance per component subtree
```

### Provider Configurations

```typescript
// 1. Root (most common) — app-wide singleton, tree-shakable
@Injectable({ providedIn: 'root' })

// 2. NgModule — all components in the module share this instance
@NgModule({
  providers: [ProductService]
})

// 3. Component — fresh instance per component (isolates state)
@Component({
  selector: 'app-wizard',
  providers: [WizardStateService]   // each wizard gets its own state
})

// 4. useClass — swap implementations (great for testing)
providers: [{ provide: ProductService, useClass: MockProductService }]

// 5. useValue — inject constants
export const API_URL = new InjectionToken<string>('apiUrl');
providers: [{ provide: API_URL, useValue: 'https://api.myapp.com' }]
// Inject: constructor(@Inject(API_URL) private apiUrl: string) {}

// 6. useFactory — build service dynamically
providers: [{
  provide: LogService,
  useFactory: (env: Environment) => env.production ? new SilentLog() : new ConsoleLog(),
  deps: [Environment]
}]
```

---

## 8. Sharing State Across Components

### Cart Service — Shared State Example

```typescript
@Injectable({ providedIn: 'root' })
export class CartService {
  private items: Product[] = [];

  add(product: Product): void { this.items.push(product); }

  remove(id: number): void {
    this.items = this.items.filter(p => p.id !== id);
  }

  getItems(): Product[] { return [...this.items]; }   // return a copy!
  getCount(): number { return this.items.length; }
  clear(): void { this.items = []; }
}
```

```typescript
// ProductCardComponent (in one part of the app)
constructor(private cart: CartService) {}
onAdd(): void { this.cart.add(this.product); }

// NavbarComponent (completely separate component tree)
constructor(private cart: CartService) {}
get cartCount(): number { return this.cart.getCount(); }
```

```html
<!-- navbar.component.html -->
<span class="badge">{{ cartCount }}</span>
```

**Why this works:** Both components inject the same `CartService` singleton. `ProductCard.add()` mutates the service state. `NavbarComponent.cartCount` reads from that same state.

**⚠️ Limitation:** This works for synchronous reads. For real-time reactive updates (e.g., badge updating instantly without a page action), use a `BehaviorSubject` — covered in Day 19b (Angular HTTP & RxJS).

---

## 9. Component Encapsulation

### The Three Modes

```typescript
import { Component, ViewEncapsulation } from '@angular/core';

@Component({
  encapsulation: ViewEncapsulation.Emulated   // ← default, almost always correct
})
```

| Mode | How it works | Use when |
|---|---|---|
| `Emulated` (default) | Angular adds unique attribute selectors to scope CSS | Always, unless you have a specific reason |
| `None` | No scoping — CSS is global | Intentional global styles / theming |
| `ShadowDom` | Browser native Shadow DOM | Web component libraries |

### What Emulated Mode Generates

```html
<!-- Your code -->
<h3>Product Name</h3>

<!-- What Angular renders -->
<h3 _ngcontent-abc-c12>Product Name</h3>
```

```css
/* Your CSS */
h3 { color: red; }

/* What Angular applies */
h3[_ngcontent-abc-c12] { color: red; }
```

The `h3` in other components doesn't have `_ngcontent-abc-c12`, so it's unaffected.

---

## 10. Smart vs Presentational Components

### Pattern Summary

| | Presentational ("Dumb") | Smart ("Container") |
|---|---|---|
| Purpose | Display only | Orchestrate data + logic |
| Services | None | Injects services |
| Inputs | `@Input()` for all data | Minimal — gets data from services |
| Outputs | `@Output()` for all user actions | Handles outputs from children |
| Reusability | High — works in any parent | Lower — tied to specific services |
| Testing | Easy — just pass inputs, check outputs | Requires service mocks |

```typescript
// ✅ Presentational — no service injection
export class ProductCardComponent {
  @Input() product!: Product;
  @Output() addToCart = new EventEmitter<Product>();
  onAdd(): void { this.addToCart.emit(this.product); }
}

// ✅ Smart — injects services, passes data to children
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  constructor(
    private productService: ProductService,
    private cartService: CartService
  ) {}
  ngOnInit(): void { this.products = this.productService.getProducts(); }
  handleAddToCart(p: Product): void { this.cartService.add(p); }
}
```

---

## 11. Complete Working Example

Full product management mini-app combining all Day 17b concepts.

### Models

```typescript
// models/product.model.ts
export interface Product {
  id: number;
  name: string;
  price: number;
  category: 'electronics' | 'clothing' | 'other';
  inStock: boolean;
}
```

### Custom Pipe

```typescript
// pipes/category-label.pipe.ts
@Pipe({ name: 'categoryLabel' })
export class CategoryLabelPipe implements PipeTransform {
  transform(value: string): string {
    const labels: Record<string, string> = {
      electronics: '🔌 Electronics',
      clothing: '👕 Clothing',
      other: '📦 Other'
    };
    return labels[value] ?? value;
  }
}
```

### Services

```typescript
// services/product.service.ts
@Injectable({ providedIn: 'root' })
export class ProductService {
  private products: Product[] = [
    { id: 1, name: 'Laptop',     price: 999, category: 'electronics', inStock: true },
    { id: 2, name: 'T-Shirt',    price: 25,  category: 'clothing',    inStock: true },
    { id: 3, name: 'Headphones', price: 149, category: 'electronics', inStock: false },
  ];
  getAll(): Product[] { return [...this.products]; }
}

// services/cart.service.ts
@Injectable({ providedIn: 'root' })
export class CartService {
  private items: Product[] = [];
  add(p: Product): void { if (!this.items.find(i => i.id === p.id)) this.items.push(p); }
  getItems(): Product[] { return [...this.items]; }
  getCount(): number { return this.items.length; }
  remove(id: number): void { this.items = this.items.filter(p => p.id !== id); }
}
```

### Presentational Component — ProductCard

```typescript
// product-card.component.ts
@Component({
  selector: 'app-product-card',
  template: `
    <div class="card" [ngClass]="{ 'out-of-stock': !product.inStock }">
      <span class="index">#{{ index + 1 }}</span>
      <h3>{{ product.name | titlecase }}</h3>
      <p class="price">{{ product.price | currency }}</p>
      <p class="category">{{ product.category | categoryLabel }}</p>
      <button (click)="onAdd()" [disabled]="!product.inStock">
        {{ product.inStock ? 'Add to Cart' : 'Out of Stock' }}
      </button>
    </div>
  `
})
export class ProductCardComponent {
  @Input() product!: Product;
  @Input() index = 0;
  @Output() addToCart = new EventEmitter<Product>();
  onAdd(): void { this.addToCart.emit(this.product); }
}
```

### Smart Component — ProductList

```typescript
// product-list.component.ts
@Component({
  selector: 'app-product-list',
  template: `
    <div class="controls">
      <input [(ngModel)]="searchTerm" placeholder="Search...">
      <select [(ngModel)]="selectedCategory">
        <option value="">All</option>
        <option value="electronics">Electronics</option>
        <option value="clothing">Clothing</option>
      </select>
    </div>

    <app-product-card
      *ngFor="let p of filteredProducts; index as i; trackBy: trackById"
      [product]="p"
      [index]="i"
      (addToCart)="handleAdd($event)">
    </app-product-card>

    <p *ngIf="filteredProducts.length === 0">No products match your search.</p>
  `
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  searchTerm = '';
  selectedCategory = '';

  constructor(
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.products = this.productService.getAll();
  }

  get filteredProducts(): Product[] {
    return this.products.filter(p => {
      const matchesSearch = p.name.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesCategory = !this.selectedCategory || p.category === this.selectedCategory;
      return matchesSearch && matchesCategory;
    });
  }

  handleAdd(product: Product): void {
    this.cartService.add(product);
    console.log(`Cart now has ${this.cartService.getCount()} items`);
  }

  trackById(_: number, p: Product): number { return p.id; }
}
```

### Navbar Component (Sibling — no parent-child to ProductList)

```typescript
// navbar.component.ts
@Component({
  selector: 'app-navbar',
  template: `
    <nav>
      <span class="brand">My Shop</span>
      <span class="cart-badge">🛒 {{ cartCount }}</span>
    </nav>
  `
})
export class NavbarComponent {
  constructor(private cartService: CartService) {}
  get cartCount(): number { return this.cartService.getCount(); }
}
```

---

## 12. Common Mistakes & Fixes

| Mistake | Fix |
|---|---|
| `new MyService()` in component | Inject via constructor: `constructor(private svc: MyService) {}` |
| Calling service methods in constructor | Move to `ngOnInit` |
| Mutating `@Input()` in the child | Emit an `@Output()` event; let parent update the data |
| Forgetting `trackBy` on `*ngFor` | Always add: `trackBy: trackById` |
| Forgetting to declare custom pipe in NgModule | Add to `declarations` array in your module |
| Using impure pipe on a large list | Filter in the component class with a getter; bind to that |
| Expecting real-time badge updates from service mutation | Use `BehaviorSubject` (Day 19b) for reactive push-based updates |
| Two structural directives on same element (`*ngIf` + `*ngFor`) | Wrap inner element in `<ng-container>` and put one directive on it |
| Direct DOM access via `document.getElementById` | Use `ElementRef` in a directive or `@ViewChild` in the component |

---

## 13. Quick Reference Syntax

```html
<!-- @Input binding -->
<child-comp [propertyName]="parentExpression"></child-comp>

<!-- @Output binding -->
<child-comp (eventName)="parentMethod($event)"></child-comp>

<!-- Both -->
<child-comp [data]="items" (selected)="onSelect($event)"></child-comp>

<!-- *ngIf -->
<div *ngIf="condition; else elseTmpl">...</div>
<ng-template #elseTmpl>...</ng-template>

<!-- *ngFor with all options -->
<li *ngFor="let item of items; index as i; trackBy: trackFn">...</li>

<!-- *ngSwitch -->
<div [ngSwitch]="value">
  <span *ngSwitchCase="'a'">A</span>
  <span *ngSwitchDefault>Other</span>
</div>

<!-- Pipes -->
{{ value | date:'mediumDate' }}
{{ price | currency:'USD' }}
{{ name | uppercase | slice:0:10 }}

<!-- Custom directive -->
<p [appHighlight]="'yellow'">Hover me</p>

<!-- [ngClass] -->
<div [ngClass]="{ 'active': isActive, 'error': hasError }">...</div>
```

```typescript
// Service
@Injectable({ providedIn: 'root' })
export class MyService { }

// Inject
constructor(private myService: MyService) {}
ngOnInit(): void { this.data = this.myService.getData(); }

// Custom pipe
@Pipe({ name: 'myPipe' })
export class MyPipe implements PipeTransform {
  transform(value: T, arg?: string): string { return ...; }
}

// Custom directive
@Directive({ selector: '[appMyDir]' })
export class MyDirective {
  constructor(private el: ElementRef) {}
  @HostListener('click') onClick() { ... }
}
```

---

## 14. Looking Ahead — Day 18b

**Day 18b: Angular Routing & Forms** builds directly on today's patterns:

| Today (Day 17b) | Day 18b |
|---|---|
| Services with DI | Services injected into **route guards** (`CanActivate`) |
| `@Injectable` + constructor injection | `Router` and `ActivatedRoute` injected into components |
| Component communication | Route parameters passed as observable streams |
| `ngOnInit` for data loading | `ngOnInit` subscribes to route params to load correct data |
| Smart + Presentational pattern | Routed components act as smart containers |

**Day 18b topics preview:**
- `RouterModule` configuration — `Routes` array with `path`, `component`
- `routerLink` directive — declarative navigation in templates
- `Router.navigate()` — programmatic navigation in services/components
- Route parameters — `:id` in URL, `ActivatedRoute.params`
- Nested routes and `<router-outlet>`
- Route guards (`CanActivate`) — injecting your services to protect routes
- Lazy loading feature modules — separate bundles per route
- Template-driven forms with `NgForm` and `ngModel`
- Reactive forms with `FormBuilder`, `FormGroup`, `FormControl`
- Built-in and custom form validators
