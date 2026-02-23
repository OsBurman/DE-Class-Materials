# Week 4 - Day 17b: Angular Services & DI
## Part 2 Slide Descriptions

---

### Slide 1: Part 2 Title Slide
**Title:** Angular Services & Dependency Injection
**Subtitle:** Part 2 — Custom Pipes, Services, DI & Component Encapsulation
**Visual:** Four labeled pillars: "Custom Pipes", "Services", "Dependency Injection", "Encapsulation"
**Notes:** Opening slide for Part 2. Transition from Part 1 (component communication, directives, built-in pipes) to the "architecture" half of the day.

---

### Slide 2: Custom Pipes — Why Build Your Own?
**Title:** When Built-In Pipes Aren't Enough
**Visual:** Three real-world scenarios

**Scenario 1 — Business formatting:**
```
// You need: "5 minutes ago", "2 hours ago", "Yesterday"
// Built-in date pipe gives: "Feb 23, 2026 3:45 PM"  ← not helpful
```

**Scenario 2 — Domain-specific display:**
```
// You need: phone number "1234567890" → "(123) 456-7890"
// No built-in pipe for that
```

**Scenario 3 — List filtering:**
```
// You need: products | filter:'electronics'
// No built-in pipe for that
```

**Rule of thumb box:**
> Use a custom pipe when you need to **transform a value for display** in multiple templates and the logic is more than a one-liner.

**Notes:** Custom pipes keep transformation logic out of component classes and make templates readable. The key phrase is "for display" — pipes should never modify the original data or trigger side effects.

---

### Slide 3: Creating a Custom Pipe — The Structure
**Title:** Anatomy of a Custom Pipe
**Visual:** Annotated pipe class

```bash
ng generate pipe phone-number
# creates: phone-number.pipe.ts + phone-number.pipe.spec.ts
```

```typescript
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'phoneNumber'         // ← used in template: {{ val | phoneNumber }}
})
export class PhoneNumberPipe implements PipeTransform {

  transform(value: string, format: string = 'us'): string {
    //       ↑ input value    ↑ optional argument (after the colon)

    if (!value) return '';
    const digits = value.replace(/\D/g, '');  // strip non-digits

    if (format === 'us' && digits.length === 10) {
      return `(${digits.slice(0,3)}) ${digits.slice(3,6)}-${digits.slice(6)}`;
    }
    return value;  // fallback: return as-is
  }
}
```

**Usage:**
```html
{{ '5551234567' | phoneNumber }}          <!-- (555) 123-4567 -->
{{ '5551234567' | phoneNumber:'us' }}     <!-- (555) 123-4567 -->
```

**Notes:** The `PipeTransform` interface requires one method: `transform(value, ...args)`. The first argument is always the left side of the pipe. Additional colon-separated arguments map to additional parameters.

---

### Slide 4: Pure vs Impure Pipes
**Title:** Pure Pipes vs Impure Pipes
**Visual:** Two-column definition

**Pure Pipe (default — `pure: true`):**
```typescript
@Pipe({ name: 'filter', pure: true })  // default behavior
```
- Runs **only when the input reference changes**
- Highly performant — Angular memoizes the result
- ✅ Use for most transformations (format, convert, calculate)
- ⚠️ Will NOT re-run if you mutate an array (same reference)

**Impure Pipe (`pure: false`):**
```typescript
@Pipe({ name: 'filter', pure: false })
```
- Runs **on every change detection cycle**
- Can detect mutations inside arrays and objects
- ⚠️ Performance cost — use sparingly
- ✅ Use for: `async` pipe, filter pipes that need to react to array mutations

**Best practice box:**
> Prefer pure pipes. For filtering, consider filtering in the component class with a computed property and binding to that instead — better performance and easier to test.

**Notes:** The `async` pipe is impure by design — it needs to update whenever the Observable emits a new value. Students will encounter impure pipes when they work with RxJS in Day 19b.

---

### Slide 5: A Reusable Filter Pipe
**Title:** Building a Search Filter Pipe
**Visual:** Full implementation

```typescript
@Pipe({ name: 'filter', pure: false })
export class FilterPipe implements PipeTransform {
  transform(items: any[], searchTerm: string, field: string): any[] {
    if (!items || !searchTerm) return items;
    return items.filter(item =>
      item[field]
        ?.toString()
        .toLowerCase()
        .includes(searchTerm.toLowerCase())
    );
  }
}
```

**Usage:**
```html
<input [(ngModel)]="searchTerm" placeholder="Search products...">

<app-product-card
  *ngFor="let p of products | filter:searchTerm:'name';
          trackBy: trackById"
  [product]="p">
</app-product-card>
```

**Annotation:** `filter:searchTerm:'name'` — three parts: pipe name, first arg (search term variable), second arg (field to search on, a string literal)

**Note box:** "This pipe is `pure: false` because the `products` array reference may not change even if `searchTerm` changes."
**Notes:** This is a concrete, practical example students will want to use immediately. Walk through the colon-separated argument passing carefully.

---

### Slide 6: Services — The "Why"
**Title:** Services: Solving Two Problems at Once
**Visual:** Problem diagram

**Problem 1 — Duplicated Logic:**
```
ProductListComponent  →  calls API, formats data, filters
CartComponent         →  calls SAME API, same formatting
OrderComponent        →  calls SAME API again...
```
Arrow: "Three components each doing the same work. Bug fix? Update three places."

**Problem 2 — Sharing State:**
```
User adds product in ProductListComponent
CartComponent needs to know — but has no connection to ProductList
```
Arrow: "No way to share data without threading it through every parent."

**Solution box:**
```
         ProductService
        /       |        \
ProductList   Cart   OrderComponent
   (reads)  (reads)    (reads)
```
> A **service** is a class that holds shared logic and/or state. Any component that needs it gets an injected copy.

**Notes:** This is the moment students realize why everything so far has felt incomplete — all the Day 16b examples had hardcoded data in the component itself. Services fix that.

---

### Slide 7: Dependency Injection — The Concept
**Title:** Dependency Injection — Don't Create, Request
**Visual:** Two side-by-side approaches

**Without DI (bad — tight coupling):**
```typescript
export class ProductListComponent {
  private service = new ProductService();  // ← YOU create it
  // Problems:
  // - Can't swap for a mock in tests
  // - Angular can't manage its lifetime
  // - Multiple components create separate instances (no shared state)
}
```

**With DI (correct — loose coupling):**
```typescript
export class ProductListComponent {
  constructor(private productService: ProductService) {}
  // ← Angular creates and injects it for you
  // Benefits:
  // ✅ Angular manages the lifetime (singleton by default)
  // ✅ Easy to swap a mock in tests
  // ✅ All components share the same instance
}
```

**Definition box:**
> **Dependency Injection** is a design pattern where a class receives its dependencies from an external source (the DI container) rather than creating them itself.

**Notes:** The DI container in Angular is called the **Injector**. When Angular sees `private productService: ProductService` in a constructor, it looks up or creates a `ProductService` instance and passes it in.

---

### Slide 8: Creating a Service — The Structure
**Title:** Anatomy of an Angular Service
**Visual:** Annotated service class

```bash
ng generate service product
# creates: product.service.ts + product.service.spec.ts
```

```typescript
import { Injectable } from '@angular/core';
import { Product } from './models/product.model';

@Injectable({
  providedIn: 'root'    // ← register with the ROOT injector (app-wide singleton)
})
export class ProductService {
  private products: Product[] = [
    { id: 1, name: 'Laptop',   price: 999,  category: 'electronics', inStock: true },
    { id: 2, name: 'T-Shirt',  price: 25,   category: 'clothing',    inStock: true },
    { id: 3, name: 'Headphones', price: 149, category: 'electronics', inStock: false },
  ];

  getProducts(): Product[] {
    return this.products;
  }

  getById(id: number): Product | undefined {
    return this.products.find(p => p.id === id);
  }
}
```

**Key annotations:**
- `@Injectable` — marks this class as available for DI
- `providedIn: 'root'` — registers it with the app-level injector; creates ONE instance for the whole app

**Notes:** `providedIn: 'root'` is the modern way (Angular 6+) to register a service. You don't need to add it to any `NgModule.providers` array. It's also tree-shakable — if nothing uses the service, it won't be bundled.

---

### Slide 9: Injecting and Using a Service
**Title:** Injecting a Service into a Component
**Visual:** Three-step pattern

**Step 1 — Constructor injection:**
```typescript
import { Component, OnInit } from '@angular/core';
import { ProductService } from '../services/product.service';
import { Product } from '../models/product.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];

  constructor(private productService: ProductService) {}
  //          ↑ Angular sees this type and injects a ProductService instance

  ngOnInit(): void {
    this.products = this.productService.getProducts();
    //              ↑ call the service in ngOnInit, not constructor
  }
}
```

**Template (same as before — nothing changes here):**
```html
<app-product-card
  *ngFor="let p of products; trackBy: trackById"
  [product]="p">
</app-product-card>
```

**The rule box:** "**Constructor** = declare dependencies. **ngOnInit** = use them. Never call APIs or heavy logic in the constructor."
**Notes:** This is the pattern students will use 100% of the time. The constructor declares what the component needs; Angular satisfies those needs before `ngOnInit` runs.

---

### Slide 10: The Injector Hierarchy
**Title:** Angular's Injector Tree
**Visual:** Tree diagram

```
Root Injector (AppModule / providedIn: 'root')
│   └── Singleton: one instance for entire app
│
├── Module Injector (Feature Modules — lazy loaded)
│   └── Separate instance per loaded module
│
└── Element Injector (Component-level)
    └── New instance per component subtree
```

**`providers` placement comparison:**

| Where registered | Who gets this instance |
|---|---|
| `@Injectable({ providedIn: 'root' })` | Entire app — one shared instance |
| `NgModule.providers: [MyService]` | All components in that module |
| `@Component({ providers: [MyService] })` | That component and its children only |

**Use case example:**
```typescript
// Component-level provider — each ProductCard gets its OWN CartService
@Component({
  selector: 'app-product-card',
  providers: [CartService]  // ← fresh instance per ProductCard
})
```

**Notes:** 90% of the time, `providedIn: 'root'` is exactly what you want. Component-level providers are useful for stateful services that should be isolated per component instance (e.g., a form wizard state service).

---

### Slide 11: Sharing State Between Components via a Service
**Title:** Services as Shared State Holders
**Visual:** Full cart service example

```typescript
@Injectable({ providedIn: 'root' })
export class CartService {
  private items: Product[] = [];

  add(product: Product): void {
    this.items.push(product);
  }

  remove(productId: number): void {
    this.items = this.items.filter(p => p.id !== productId);
  }

  getItems(): Product[] {
    return [...this.items];  // return a copy — don't expose mutable state
  }

  getCount(): number {
    return this.items.length;
  }

  clear(): void {
    this.items = [];
  }
}
```

**Two components using it:**
```typescript
// ProductCardComponent
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

**Notes:** Both components get the SAME `CartService` instance because it's `providedIn: 'root'`. When `ProductCard` calls `add()`, the `NavbarComponent`'s `cartCount` getter automatically reflects the change. This is how services bridge sibling and distant components.

---

### Slide 12: Component Encapsulation
**Title:** ViewEncapsulation — CSS Scoping in Angular
**Visual:** Concept + three modes

**The problem:**
```css
/* product-card.component.css */
h3 { color: red; }
/* Without encapsulation, this would make ALL h3s red across the whole app */
```

**The three modes:**
```typescript
import { Component, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css'],
  encapsulation: ViewEncapsulation.Emulated   // ← default
})
```

| Mode | Behavior | Use When |
|---|---|---|
| `Emulated` (default) | Angular adds unique attributes to scope CSS to this component | Almost always |
| `None` | No scoping — styles leak globally | Theming, global utilities |
| `ShadowDom` | Uses browser's native Shadow DOM | Web component libraries |

**What Angular generates (Emulated mode):**
```html
<h3 _ngcontent-xyz-c23>Product Name</h3>
<!-- ↑ Angular adds this unique attribute -->
```
```css
h3[_ngcontent-xyz-c23] { color: red; }
/* ↑ CSS is scoped to just this component's elements */
```
**Notes:** Emulated encapsulation is what students get by default and should use in almost every case. The `None` mode is occasionally needed when styling third-party components whose DOM is created outside Angular's control.

---

### Slide 13: Providers Deep Dive — useClass, useValue, useFactory
**Title:** Beyond `providedIn: 'root'` — Advanced Providers
**Visual:** Three provider tokens

**`useClass` — swap one service for another (e.g., in tests):**
```typescript
// In your AppModule or test:
providers: [
  { provide: ProductService, useClass: MockProductService }
  // Angular injects MockProductService wherever ProductService is requested
]
```

**`useValue` — inject a constant:**
```typescript
export const API_URL = new InjectionToken<string>('apiUrl');

providers: [
  { provide: API_URL, useValue: 'https://api.myapp.com' }
]

// In a service:
constructor(@Inject(API_URL) private apiUrl: string) {}
```

**`useFactory` — build the service dynamically:**
```typescript
providers: [
  {
    provide: LogService,
    useFactory: (env: Environment) =>
      env.production ? new SilentLogService() : new ConsoleLogService(),
    deps: [Environment]
  }
]
```

**Notes:** Students won't use `useFactory` often, but they'll encounter `useClass` when they start writing unit tests (Day 20b / Day 28). `InjectionToken` + `useValue` is very common for app-level config like API URLs.

---

### Slide 14: Service Communication Pattern — Full Flow
**Title:** The Full Data Flow Pattern
**Visual:** Layered architecture diagram

```
┌─────────────────────────────────────┐
│         Component Layer              │
│  ProductListComponent  CartComponent │
│  NavbarComponent  OrderComponent     │
└────────────┬──────────────┬──────────┘
             │   inject     │   inject
┌────────────▼──────────────▼──────────┐
│          Service Layer                │
│   ProductService    CartService       │
│   (business logic + shared state)     │
└──────────────────────────────────────┘
```

**Real coding pattern:**
```typescript
// Services call other services too:
@Injectable({ providedIn: 'root' })
export class OrderService {
  constructor(
    private cart: CartService,       // ← inject another service
    private productService: ProductService
  ) {}

  placeOrder(): Order {
    const items = this.cart.getItems();
    // ... create order from cart items
    this.cart.clear();
    return order;
  }
}
```

**Notes:** Services can inject other services — Angular handles the dependency resolution chain automatically. The service layer is where all real business logic should live, keeping components thin.

---

### Slide 15: Smart vs Presentational Components
**Title:** A Pattern That Uses Everything We Learned
**Visual:** Two component types

**Presentational ("Dumb") Component:**
```typescript
// product-card.component.ts — only @Input/@Output, no services
@Component({ selector: 'app-product-card', ... })
export class ProductCardComponent {
  @Input() product!: Product;
  @Output() addToCart = new EventEmitter<Product>();
  // No constructor injection — pure display
}
```

**Smart ("Container") Component:**
```typescript
// product-list.component.ts — injects services, passes data down
@Component({ selector: 'app-product-list', ... })
export class ProductListComponent implements OnInit {
  products: Product[] = [];

  constructor(
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.products = this.productService.getProducts();
  }

  handleAddToCart(product: Product): void {
    this.cartService.add(product);
  }
}
```

**Template:**
```html
<!-- product-list.component.html — passes data down, listens for events up -->
<app-product-card
  *ngFor="let p of products; trackBy: trackById"
  [product]="p"
  (addToCart)="handleAddToCart($event)">
</app-product-card>
```

**Notes:** This pattern — smart containers orchestrate services and pass data to dumb presentational components via `@Input` — is the dominant pattern in professional Angular apps. React has the same pattern (container vs presentational components).

---

### Slide 16: Common Patterns & Pitfalls
**Title:** Patterns & Pitfalls to Know
**Visual:** Two-column table

**✅ Best Practices:**

| Pattern | Why |
|---|---|
| `providedIn: 'root'` for most services | Tree-shakable, app-wide singleton |
| Keep components thin — logic in services | Easier to test, reuse |
| `ngOnInit` for initialization, constructor for DI | Constructors should be fast |
| `trackBy` in every `*ngFor` | Prevents unnecessary DOM re-renders |
| Return copies from services (`[...arr]`) | Prevents external mutation of service state |
| Custom pipe for reusable display transforms | DRY templates |

**❌ Common Mistakes:**

| Mistake | Fix |
|---|---|
| `new MyService()` in a component | Inject via constructor instead |
| Heavy logic in constructors | Move to `ngOnInit` |
| Mutating `@Input()` directly | Emit output event, let parent update |
| Impure filter pipes on large lists | Filter in the component class |
| Forgetting `trackBy` on dynamic lists | Always add `trackBy` |
| Not registering pipe in module | Add pipe to `NgModule.declarations` |

**Notes:** The mistake "mutating @Input() directly" deserves special attention. `@Input()` properties are owned by the parent. The child should never modify them — it should emit an event asking the parent to make the change.

---

### Slide 17: Day 17b Summary
**Title:** Day 17b Complete — What You Can Now Build
**Visual:** Five-section summary

**📡 Component Communication**
- `@Input()` → data in from parent
- `@Output()` + `EventEmitter` → events out to parent
- Data flows **down**, events flow **up**

**🏗️ Directives**
- Structural: `*ngIf` (with else), `*ngFor` (trackBy, index), `*ngSwitch`
- Attribute: `[ngClass]`, `[ngStyle]`, custom with `@Directive` + `@HostListener`
- Angular 17+: `@if`, `@for`, `@switch`

**🔧 Pipes**
- Built-in: `date`, `currency`, `number`, `uppercase`, `titlecase`, etc.
- Custom pure pipes: `implements PipeTransform`, `transform()` method
- Impure pipes: `pure: false` — use sparingly

**⚙️ Services & DI**
- `@Injectable({ providedIn: 'root' })` → app-wide singleton
- Constructor injection: `constructor(private svc: MyService) {}`
- Services share logic AND state across components
- Injector hierarchy: root → module → component

**🏛️ Architecture**
- Component encapsulation: `ViewEncapsulation.Emulated` (default, scoped CSS)
- Smart containers + presentational components = clean separation
- Service layer holds all business logic; components stay thin

**🔭 Coming up — Day 18b:** Angular Routing, Guards, Lazy Loading & Forms
