# Exercise 10 — Angular Signals

## 🎯 Learning Objectives
- Create reactive state with **`signal()`**
- Derive values with **`computed()`**
- Run side effects with **`effect()`**
- Convert Observables to signals with **`toSignal()`**
- Convert signals to Observables with **`toObservable()`**
- Understand the difference between signals and `BehaviorSubject`
- Use **`input()` signal** (Angular 17.1+) for component inputs

---

## 📋 What You're Building
A **Shopping Cart with Signals** — a real-time cart app where:
- Product inventory uses `signal()` to track quantities
- Cart totals use `computed()` to auto-derive from cart items
- A discount code field uses `effect()` to persist to localStorage
- A countdown timer uses `toSignal()` to convert an interval Observable

---

## 🏗️ Project Setup
```bash
ng new exercise-10-signals --standalone --routing=false --style=css
cd exercise-10-signals
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## ✅ TODOs

### `app.component.ts`
- [ ] **TODO 1**: Create a `cart = signal<CartItem[]>([])` signal
- [ ] **TODO 2**: Create `discountCode = signal('')`
- [ ] **TODO 3**: Create a `cartCount = computed(...)` that sums all item quantities
- [ ] **TODO 4**: Create a `subtotal = computed(...)` that sums all (price × quantity)
- [ ] **TODO 5**: Create a `discount = computed(...)` that returns 0.1 if code is 'ANGULAR10', else 0
- [ ] **TODO 6**: Create a `total = computed(...)` that applies the discount to subtotal
- [ ] **TODO 7**: Create an `effect(...)` that saves `discountCode()` to `localStorage.setItem('discountCode', ...)`
- [ ] **TODO 8**: Implement `addToCart(product)` — use `cart.update(...)` to add or increment
- [ ] **TODO 9**: Implement `removeFromCart(id)` — use `cart.update(...)` to filter out
- [ ] **TODO 10**: Use `toSignal(interval(1000))` to create a `tick` signal for a live counter
- [ ] **TODO 11**: Create an `elapsed = computed(...)` that converts tick count to MM:SS format

### `app.component.html`
- [ ] **TODO 12**: Use `cart()` (called as a function) in the template to display cart items
- [ ] **TODO 13**: Use `computed` values (`cartCount()`, `subtotal()`, `total()`) in the template
- [ ] **TODO 14**: Two-way bind `discountCode` using the signal setter pattern

---

## 💡 Key Concepts Reminder

```typescript
import { signal, computed, effect, toSignal } from '@angular/core';
import { interval } from 'rxjs';

// signal — writable reactive state
count = signal(0);
count.set(5);              // replace value
count.update(n => n + 1);  // update based on previous

// computed — derived, read-only
double = computed(() => this.count() * 2);

// effect — side effect that runs when signals change
constructor() {
  effect(() => {
    console.log('count changed to', this.count());
  });
}

// toSignal — convert Observable to Signal
tick = toSignal(interval(1000), { initialValue: 0 });

// In templates — call signals like functions
{{ count() }}  ← note the ()!
```
