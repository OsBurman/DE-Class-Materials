# Week 4 - Day 17b: Angular Services & DI
## Part 1 Slide Descriptions

---

### Slide 1: Title Slide
**Title:** Angular Services & Dependency Injection
**Subtitle:** Day 17b — Part 1: Component Communication, Directives & Pipes
**Visual:** Angular logo centered; four labeled boxes below: "@Input / @Output", "Directives", "*ngIf / *ngFor / *ngSwitch", "Pipes"
**Notes:** Opening slide. Sets the agenda for the first hour.

---

### Slide 2: Where We Left Off — Day 16b Recap
**Title:** Day 16b Recap: What We Can Do So Far
**Visual:** Two-column checklist

| ✅ We Can Do | ❓ We Can't Do Yet |
|---|---|
| Create components with `@Component` | Pass data between components |
| Bind data with interpolation & `[]` | React to child events in parent |
| Handle events with `()` | Filter/transform displayed values |
| Two-way bind with `[(ngModel)]` | Share logic across many components |
| Use `*ngIf` / `*ngFor` basics | Apply custom conditional display logic |

**Notes:** Bridge slide. Acknowledge what was built on Day 16b — the `ProductListComponent` that returned hardcoded data. Today we wire components together and extract reusable logic.

---

### Slide 3: Component Communication — The Problem
**Title:** The Problem with Island Components
**Visual:** Diagram showing three sibling components (`HeaderComponent`, `ProductListComponent`, `CartComponent`) as isolated boxes with no arrows between them. Label: "How does the cart know a product was added?"
**Sub-visual:** Arrow pointing down: "We need a way for parents to pass data IN, and children to send events OUT."
**Notes:** Motivates `@Input` and `@Output`. The analogy: components are like people — you have to speak to pass information, you can't read each other's minds.

---

### Slide 4: @Input — Passing Data Into a Child
**Title:** `@Input()` — Parent to Child Data Flow
**Visual:** Two-panel code block

**Child component:**
```typescript
// product-card.component.ts
import { Component, Input } from '@angular/core';
import { Product } from '../models/product.model';

@Component({
  selector: 'app-product-card',
  template: `
    <div class="card">
      <h3>{{ product.name }}</h3>
      <p>{{ product.price | currency }}</p>
    </div>
  `
})
export class ProductCardComponent {
  @Input() product!: Product;  // ! = definite assignment
}
```

**Parent template:**
```html
<!-- product-list.component.html -->
<app-product-card
  *ngFor="let p of products"
  [product]="p">
</app-product-card>
```

**Arrow diagram:** Parent → `[product]="p"` → Child `@Input() product`
**Notes:** The square brackets `[product]` are the property binding syntax from Day 16b applied to a component input. The child declares what it accepts; the parent decides what it sends.

---

### Slide 5: @Output and EventEmitter — Child to Parent
**Title:** `@Output()` & `EventEmitter` — Child to Parent Events
**Visual:** Three-part code sequence

**Child — declares and emits:**
```typescript
import { Component, Input, Output, EventEmitter } from '@angular/core';

export class ProductCardComponent {
  @Input() product!: Product;
  @Output() addToCart = new EventEmitter<Product>();

  onAddClick(): void {
    this.addToCart.emit(this.product);  // sends product up
  }
}
```

**Child template:**
```html
<button (click)="onAddClick()">Add to Cart</button>
```

**Parent — listens:**
```html
<app-product-card
  [product]="p"
  (addToCart)="handleAdd($event)">  <!-- $event = emitted Product -->
</app-product-card>
```

```typescript
handleAdd(product: Product): void {
  this.cart.push(product);
}
```

**Arrow diagram:** Child `EventEmitter.emit()` → `(addToCart)` → Parent method
**Notes:** `$event` in the parent template captures exactly what the child emitted. The `EventEmitter<Product>` generic type tells TypeScript what type of value will be emitted.

---

### Slide 6: @Input + @Output Mental Model
**Title:** The Mailbox Analogy
**Visual:** House diagram
- Parent house has a **mailbox slot** labeled `[product]` — things the parent drops in
- Child house has a **doorbell** labeled `(addToCart)` — notifications the child rings
- Caption: "Data flows DOWN through inputs. Events flow UP through outputs."

**Key rules table:**

| Direction | Syntax | Decorator | What crosses |
|---|---|---|---|
| Parent → Child | `[propName]="value"` | `@Input()` | Data / objects |
| Child → Parent | `(eventName)="handler($event)"` | `@Output()` + `EventEmitter` | Events / values |

**Notes:** This mental model — data down, events up — is universal in component-based frameworks. React uses the same pattern (props down, callbacks up). Reinforce this pattern constantly.

---

### Slide 7: Directives — What Are They?
**Title:** Directives: Instructions for the DOM
**Visual:** Definition box:
> A **directive** is a class that tells Angular how to transform the DOM. Components are technically directives with a template.

**Three directive types table:**

| Type | What it does | Examples |
|---|---|---|
| **Component** | Creates a DOM subtree with a template | `AppComponent`, `ProductCardComponent` |
| **Structural** | Adds, removes, or reshapes DOM elements | `*ngIf`, `*ngFor`, `*ngSwitch` |
| **Attribute** | Changes appearance or behavior of an element | `[ngClass]`, `[ngStyle]`, custom `highlight` |

**Visual:** Annotated HTML:
```html
<div *ngIf="isVisible">        ← structural (removes/adds element)
  <p [ngClass]="'active'">     ← attribute (changes classes)
    Hello
  </p>
</div>
```
**Notes:** Students have already used `*ngIf` and `*ngFor` on Day 16b. Today we go deeper — the asterisk desugaring, `*ngSwitch`, and writing a custom attribute directive.

---

### Slide 8: The Asterisk (*) Desugaring
**Title:** What Does the `*` Actually Mean?
**Visual:** Side-by-side transformation

**Shorthand (what you write):**
```html
<p *ngIf="isLoggedIn">Welcome back!</p>
```

**Desugared (what Angular sees):**
```html
<ng-template [ngIf]="isLoggedIn">
  <p>Welcome back!</p>
</ng-template>
```

**Explanation boxes:**
- `*ngIf` is syntactic sugar for `<ng-template [ngIf]="...">`
- `<ng-template>` is never rendered directly — Angular decides whether to stamp it into the DOM
- The `*` prefix is a signal: "this directive will manipulate the host element structurally"

**Notes:** Students don't need to write the desugared form — but understanding it explains *why* `*ngIf` removes the element entirely (rather than hiding it with CSS). It's not there at all.

---

### Slide 9: *ngIf In Depth
**Title:** `*ngIf` with `else` and `ng-template`
**Visual:** Code block with labeled sections

```html
<!-- Basic -->
<div *ngIf="isLoggedIn">Welcome!</div>

<!-- With else -->
<div *ngIf="isLoggedIn; else guestBlock">Welcome back!</div>
<ng-template #guestBlock>
  <div>Please log in.</div>
</ng-template>

<!-- With then and else -->
<ng-container *ngIf="user; then userCard; else loading">
</ng-container>

<ng-template #userCard>
  <app-user-card [user]="user"></app-user-card>
</ng-template>
<ng-template #loading>
  <p>Loading...</p>
</ng-template>
```

**`<ng-container>` note box:** "An invisible grouping element — renders no DOM node itself. Use it when you need a structural directive but don't want an extra `<div>`."
**Notes:** The `#guestBlock` is a template reference variable applied to the `<ng-template>`. The `else` clause points to that template by name.

---

### Slide 10: *ngFor In Depth
**Title:** `*ngFor` with Index, TrackBy & Exported Variables
**Visual:** Annotated code block

```html
<ul>
  <li *ngFor="let product of products;
              index as i;
              first as isFirst;
              last as isLast;
              even as isEven;
              trackBy: trackById">

    <span [class.highlight]="isFirst">{{ i + 1 }}. {{ product.name }}</span>
    <span *ngIf="isLast"> (last item)</span>
  </li>
</ul>
```

**TrackBy function:**
```typescript
trackById(index: number, product: Product): number {
  return product.id;  // Angular reuses DOM nodes by ID — massive perf win
}
```

**Exported variables table:**

| Variable | Type | Meaning |
|---|---|---|
| `index` | `number` | 0-based position |
| `first` | `boolean` | Is this the first item? |
| `last` | `boolean` | Is this the last item? |
| `even` | `boolean` | Is the index even? |
| `odd` | `boolean` | Is the index odd? |

**Notes:** `trackBy` is the most important performance optimization in Angular lists. Without it, Angular re-renders every list item on any array change. With it, only changed items are touched.

---

### Slide 11: *ngSwitch In Depth
**Title:** `*ngSwitch` — Multi-Branch Conditional Rendering
**Visual:** Code block with real-world use case

```html
<!-- Status badge component template -->
<div [ngSwitch]="order.status">
  <span *ngSwitchCase="'pending'"   class="badge yellow">⏳ Pending</span>
  <span *ngSwitchCase="'shipped'"   class="badge blue">🚚 Shipped</span>
  <span *ngSwitchCase="'delivered'" class="badge green">✅ Delivered</span>
  <span *ngSwitchCase="'cancelled'" class="badge red">❌ Cancelled</span>
  <span *ngSwitchDefault            class="badge gray">Unknown</span>
</div>
```

**Comparison box:**

| Use When | Directive |
|---|---|
| Simple true/false | `*ngIf` |
| Two conditions | `*ngIf` + `else` |
| 3+ branches on one value | `*ngSwitch` |

**Notes:** `[ngSwitch]` is an attribute binding (square brackets), not a structural directive — it sets up the switch context. The `*ngSwitchCase` children are structural. Point out the difference in syntax.

---

### Slide 12: Custom Attribute Directives
**Title:** Building a Custom Attribute Directive
**Visual:** Goal box: "Create a `[appHighlight]` directive that changes a paragraph's background when hovered."

**Generate:**
```bash
ng generate directive highlight
# or: ng g d highlight
```

**Directive class:**
```typescript
import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({ selector: '[appHighlight]' })
export class HighlightDirective {
  @Input() appHighlight = 'yellow';  // color input (same name as selector!)

  constructor(private el: ElementRef) {}

  @HostListener('mouseenter') onMouseEnter() {
    this.el.nativeElement.style.backgroundColor = this.appHighlight;
  }

  @HostListener('mouseleave') onMouseLeave() {
    this.el.nativeElement.style.backgroundColor = '';
  }
}
```

**Usage:**
```html
<p appHighlight>Default yellow highlight</p>
<p [appHighlight]="'lightblue'">Blue highlight</p>
```

**Notes:** `ElementRef` gives direct access to the DOM element. `@HostListener` attaches event listeners to the host element. Note the selector `[appHighlight]` — square brackets mean "attribute selector" in CSS (and Angular).

---

### Slide 13: Built-in Pipes
**Title:** Pipes — Transforming Values in Templates
**Visual:** Concept box: "A **pipe** takes a value, transforms it, and returns the result. It never modifies the original data."

```
{{ value | pipeName : argument1 : argument2 }}
```

**Built-in pipes table:**

| Pipe | Example | Output |
|---|---|---|
| `date` | `{{ today \| date:'mediumDate' }}` | Feb 23, 2026 |
| `currency` | `{{ 9.99 \| currency:'USD' }}` | $9.99 |
| `number` | `{{ 3.14159 \| number:'1.2-2' }}` | 3.14 |
| `percent` | `{{ 0.85 \| percent }}` | 85% |
| `uppercase` | `{{ 'hello' \| uppercase }}` | HELLO |
| `lowercase` | `{{ 'HELLO' \| lowercase }}` | hello |
| `titlecase` | `{{ 'hello world' \| titlecase }}` | Hello World |
| `json` | `{{ obj \| json }}` | `{ "a": 1 }` |
| `slice` | `{{ items \| slice:0:3 }}` | First 3 items |
| `async` | `{{ obs$ \| async }}` | Unwrapped value |

**Notes:** `async` pipe is especially powerful — it subscribes to an Observable and auto-unsubscribes when the component destroys. We'll use it heavily in Day 19b. Today: introduce it, don't deep-dive.

---

### Slide 14: Chaining Pipes and Date Formats
**Title:** Chaining Pipes & Date Format Strings
**Visual:** Chaining example

```html
<!-- Chain pipes left to right -->
{{ product.name | uppercase | slice:0:20 }}
<!-- → 'WIRELESS HEADPHONES PRE' (uppercase first, then slice) -->

{{ product.description | titlecase | slice:0:50 }}
```

**Date format reference table:**

| Format Token | Meaning | Example Output |
|---|---|---|
| `'short'` | Short date+time | 2/23/26, 3:45 PM |
| `'mediumDate'` | Medium date only | Feb 23, 2026 |
| `'longDate'` | Long date | February 23, 2026 |
| `'fullDate'` | Full with weekday | Monday, February 23, 2026 |
| `'HH:mm:ss'` | Custom 24h time | 15:45:00 |
| `'MM/dd/yyyy'` | Custom date | 02/23/2026 |

**Notes:** Chaining works left-to-right. The output of each pipe becomes the input of the next. Useful but avoid over-chaining — if logic is complex, move it to a method in the component class.

---

### Slide 15: Complete Part 1 Example — ProductCard
**Title:** Putting It All Together: ProductCard Component
**Visual:** Full working example across three panels

**Product model:**
```typescript
export interface Product {
  id: number;
  name: string;
  price: number;
  category: string;
  inStock: boolean;
}
```

**ProductCardComponent:**
```typescript
@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html'
})
export class ProductCardComponent {
  @Input() product!: Product;
  @Input() index!: number;
  @Output() addToCart = new EventEmitter<Product>();

  onAdd(): void { this.addToCart.emit(this.product); }
}
```

**Template:**
```html
<div class="card" [ngClass]="{ 'out-of-stock': !product.inStock }">
  <span class="badge">{{ index + 1 }}</span>
  <h3>{{ product.name | titlecase }}</h3>
  <p>{{ product.price | currency }}</p>

  <div [ngSwitch]="product.category">
    <span *ngSwitchCase="'electronics'">🔌 Electronics</span>
    <span *ngSwitchCase="'clothing'">👕 Clothing</span>
    <span *ngSwitchDefault>📦 Other</span>
  </div>

  <button (click)="onAdd()" [disabled]="!product.inStock">
    {{ product.inStock ? 'Add to Cart' : 'Out of Stock' }}
  </button>
</div>
```

**Parent template (excerpt):**
```html
<app-product-card
  *ngFor="let p of products; index as i; trackBy: trackById"
  [product]="p"
  [index]="i"
  (addToCart)="handleAddToCart($event)">
</app-product-card>
```
**Notes:** This example uses every concept from Part 1: `@Input`, `@Output`, `EventEmitter`, `*ngFor` with `index` and `trackBy`, `*ngSwitch`, `[ngClass]`, `currency` pipe, `titlecase` pipe, and property binding on `[disabled]`.

---

### Slide 16: Angular 17+ Control Flow Syntax
**Title:** Modern Angular: `@if`, `@for`, `@switch` (Angular 17+)
**Visual:** Side-by-side comparison

| Old Syntax | New Syntax (Angular 17+) |
|---|---|
| `*ngIf="x; else tmpl"` | `@if (x) { ... } @else { ... }` |
| `*ngFor="let i of items; trackBy: fn"` | `@for (i of items; track i.id) { ... }` |
| `*ngSwitch` + `*ngSwitchCase` | `@switch (x) { @case ('a') { ... } }` |

**New syntax example:**
```html
@for (product of products; track product.id) {
  <app-product-card [product]="product" />
} @empty {
  <p>No products found.</p>
}

@if (isLoggedIn) {
  <app-dashboard />
} @else {
  <app-login />
}
```

**Note box:** "The new built-in control flow is faster (no need to import CommonModule), has better type narrowing, and `@for` requires `track` — enforcing the `trackBy` best practice we just covered."
**Notes:** This is Angular 17+. If students work in Angular 14–16 codebases they'll see the `*ng` prefix syntax. Show both so they're comfortable in any codebase.

---

### Slide 17: Part 1 Summary
**Title:** Part 1 Summary — What We Covered
**Visual:** Four-section summary card

**📡 Component Communication**
- `@Input()` — parent passes data into child via `[propName]="value"`
- `@Output()` + `EventEmitter` — child emits events to parent via `(eventName)="handler($event)"`
- Data flows **down**, events flow **up**

**🏗️ Structural Directives**
- `*ngIf` with `else` and `ng-template`
- `*ngFor` with `index`, `trackBy`, exported variables
- `*ngSwitch` for multi-branch rendering
- Angular 17+ `@if`, `@for`, `@switch` built-in syntax

**🎨 Attribute Directives**
- `[ngClass]`, `[ngStyle]` for dynamic styling
- Custom directives with `@Directive`, `ElementRef`, `@HostListener`

**🔧 Pipes**
- Built-in: `date`, `currency`, `number`, `uppercase`, `titlecase`, `slice`, `async`
- Pipe syntax: `{{ value | pipe : arg }}`
- Chaining pipes left-to-right

**Coming up in Part 2:** Custom pipes, Services, Dependency Injection, and sharing data across components.
