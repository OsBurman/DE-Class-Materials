# Exercise 03 — Component Communication

## 🎯 Learning Objectives
- Pass data **down** the component tree with **`@Input()`**
- Send data **up** with **`@Output()` and `EventEmitter`**
- Use **`@ViewChild`** to call methods on a child component from the parent
- Understand Angular's **unidirectional data flow**

---

## 📋 What You're Building
A **Product Catalog & Shopping Cart** — a two-panel layout where:
- The left panel shows a product grid (`ProductCardComponent` per product)
- The right panel shows the cart (`CartComponent`)
- Each product card emits an "Add to Cart" event to the parent
- The parent passes the cart items to the `CartComponent`
- The cart component can emit a "clear cart" event back up

```
AppComponent (parent)
├── ProductCardComponent × N   (@Input: product) (@Output: addToCart)
└── CartComponent               (@Input: cartItems) (@Output: cartCleared)
```

---

## 🏗️ Project Setup
```bash
ng new exercise-03-component-communication --standalone --routing=false --style=css
cd exercise-03-component-communication
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## 📁 File Structure
```
src/app/
├── app.component.ts / .html / .css
├── product-card/
│   ├── product-card.component.ts
│   └── product-card.component.html
└── cart/
    ├── cart.component.ts
    └── cart.component.html
```

---

## ✅ TODOs

### `app.component.ts`
- [ ] **TODO 1**: Define `Product` interface: `id`, `name`, `price`, `image`, `category`
- [ ] **TODO 2**: Define `CartItem` interface: `product: Product`, `quantity: number`
- [ ] **TODO 3**: Create a `products` array with 6 sample products
- [ ] **TODO 4**: Create a `cartItems: CartItem[]` array (starts empty)
- [ ] **TODO 5**: Implement `onAddToCart(product: Product)` — if product is already in cart, increment quantity; otherwise push new CartItem
- [ ] **TODO 6**: Implement `onCartCleared()` — resets `cartItems` to `[]`
- [ ] **TODO 7**: Create a `get cartTotal()` getter that sums `price × quantity`

### `product-card.component.ts`
- [ ] **TODO 8**: Declare `@Input() product!: Product`
- [ ] **TODO 9**: Declare `@Output() addToCart = new EventEmitter<Product>()`
- [ ] **TODO 10**: Implement `onAddClick()` that emits `this.product`

### `product-card.component.html`
- [ ] **TODO 11**: Display product image, name, category, and formatted price
- [ ] **TODO 12**: Bind `(click)` on the "Add to Cart" button to `onAddClick()`

### `cart.component.ts`
- [ ] **TODO 13**: Declare `@Input() items: CartItem[] = []`
- [ ] **TODO 14**: Declare `@Output() cartCleared = new EventEmitter<void>()`
- [ ] **TODO 15**: Implement `clearCart()` that emits the `cartCleared` event

### `cart.component.html`
- [ ] **TODO 16**: Show "Cart is empty" when `items.length === 0`
- [ ] **TODO 17**: Loop through items and display product name, quantity, subtotal
- [ ] **TODO 18**: Bind `(click)` on the "Clear Cart" button to `clearCart()`

### `app.component.html`
- [ ] **TODO 19**: Pass each `product` to `<app-product-card>` and listen for `(addToCart)`
- [ ] **TODO 20**: Pass `cartItems` to `<app-cart>` and listen for `(cartCleared)`

---

## 💡 Key Concepts Reminder

```typescript
// Child receives data from parent
@Input() product!: Product;

// Child sends data to parent
@Output() addToCart = new EventEmitter<Product>();
this.addToCart.emit(this.product);

// Parent template
<app-product-card
  [product]="p"
  (addToCart)="onAddToCart($event)">
</app-product-card>
```
