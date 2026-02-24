# Day 17b Application — Angular Services & DI: Product Catalog with Cart

## Overview

You'll build a **Product Catalog with Shopping Cart** — an Angular app that uses a shared `CartService` for state, `@Input`/`@Output` for component communication, a custom `CurrencyFormatPipe`, and structural directives throughout.

---

## Learning Goals

- Pass data with `@Input` and `@Output` / `EventEmitter`
- Use `*ngIf`, `*ngFor`, `*ngSwitch` in depth
- Create and apply a custom pipe
- Build and inject an Angular service
- Share data across components using a service

---

## Prerequisites

- `cd starter-code && npm install && npm run start`

---

## Project Structure

```
starter-code/
└── src/app/
    ├── app.module.ts
    ├── app.component.ts / .html
    ├── models/product.model.ts          ← provided
    ├── services/
    │   └── cart.service.ts              ← TODO
    ├── pipes/
    │   └── currency-format.pipe.ts      ← TODO
    └── components/
        ├── product-card/                ← TODO (@Input, @Output)
        ├── product-list/                ← TODO (*ngFor, *ngSwitch)
        └── cart-sidebar/               ← TODO (CartService injection)
```

---

## Part 1 — `CartService`

**Task 1**  
Create `CartService` with `@Injectable({ providedIn: 'root' })`.  
State: `private cartItems: CartItem[] = []`  
Methods: `addItem(product)`, `removeItem(id)`, `getItems()`, `getTotal()`, `getCount()`, `clearCart()`

---

## Part 2 — Custom Pipe

**Task 2 — `CurrencyFormatPipe`**  
Transform a `number` to `"$X.XX"` format.  
Usage in template: `{{ product.price | currencyFormat }}`

---

## Part 3 — `ProductCard` Component

**Task 3 — @Input**  
`@Input() product: Product`  
`@Input() isInCart: boolean = false`

**Task 4 — @Output**  
`@Output() addToCart = new EventEmitter<Product>()`  
`@Output() removeFromCart = new EventEmitter<number>()` (emits product id)

**Task 5 — Template**  
- Show product name, price (using `currencyFormat` pipe), category badge
- "Add to Cart" button — disable with `[disabled]="isInCart"`, emit `addToCart`
- "Remove" button — only show `*ngIf="isInCart"`, emit `removeFromCart`

---

## Part 4 — `ProductList` Component

**Task 6 — `*ngFor` with index**  
Render `<app-product-card>` for each product. Pass `isInCart` by checking CartService.

**Task 7 — `*ngSwitch` for category badge color**  
Use `*ngSwitch` on `product.category` to apply different badge classes:  
Electronics → blue, Clothing → green, Books → yellow, Other → gray

**Task 8 — Handle @Output events**  
On `addToCart` event: call `cartService.addItem()`.  
On `removeFromCart` event: call `cartService.removeItem()`.

---

## Part 5 — `CartSidebar` Component

**Task 9 — Inject CartService**  
In the constructor: `constructor(public cartService: CartService) {}`  
Display items, totals, item count, and a clear cart button.

---

## Stretch Goals

1. Add a `QuantityPipe` that formats `"1 item"` vs `"3 items"` (singular/plural).
2. Add a `filter` input to `ProductList` — bind with `ngModel` and filter products by name.
3. Add `@HostListener` to `ProductCard` to highlight on mouse enter/leave.

---

## Submission Checklist

- [ ] `@Input()` used in `ProductCard`
- [ ] `@Output()` with `EventEmitter` used in `ProductCard`
- [ ] `*ngFor` used with index
- [ ] `*ngSwitch` used for category styling
- [ ] `*ngIf` used for conditional display
- [ ] Custom pipe created and applied in template
- [ ] `CartService` decorated with `@Injectable`
- [ ] Service injected via constructor DI
- [ ] Cart total and item count update reactively
