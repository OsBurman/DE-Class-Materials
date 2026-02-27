import { Component, computed, effect, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { interval } from 'rxjs';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface Product {
  id: number;
  name: string;
  price: number;
  emoji: string;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CurrencyPipe, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  readonly products: Product[] = [
    { id: 1, name: 'Angular Sticker Pack',   price: 9.99,  emoji: '🎨' },
    { id: 2, name: 'TypeScript T-Shirt',     price: 24.99, emoji: '👕' },
    { id: 3, name: 'RxJS Coffee Mug',        price: 14.99, emoji: '☕' },
    { id: 4, name: 'Dev Mechanical Keyboard', price: 129.00, emoji: '⌨️' },
    { id: 5, name: 'Signal-Powered Hoodie',  price: 49.99, emoji: '🧥' },
    { id: 6, name: 'NgRx Poster',            price: 19.99, emoji: '🗓️' },
  ];

  // TODO 1: Create a cart signal: cart = signal<CartItem[]>([])
  // TODO 2: Create a discountCode signal: discountCode = signal('')

  // TODO 3: Computed — cartCount (sum of all quantities)
  // cartCount = computed(() => ...);

  // TODO 4: Computed — subtotal (sum of price * quantity)
  // subtotal = computed(() => ...);

  // TODO 5: Computed — discount rate (0.1 if code === 'ANGULAR10', else 0)
  // discount = computed(() => ...);

  // TODO 6: Computed — total = subtotal * (1 - discount)
  // total = computed(() => ...);

  // TODO 7: Effect — save discountCode to localStorage whenever it changes.
  // In the constructor, write:
  //   effect(() => { localStorage.setItem('discountCode', this.discountCode()); });

  constructor() {
    // TODO 7: effect goes here
  }

  // TODO 8: addToCart(product: Product)
  //   Use this.cart.update(items => {
  //     const existing = items.find(i => i.product.id === product.id);
  //     if (existing) return items.map(i => i.product.id === product.id ? { ...i, quantity: i.quantity + 1 } : i);
  //     return [...items, { product, quantity: 1 }];
  //   });
  addToCart(product: Product): void {
    // your code here
  }

  // TODO 9: removeFromCart(id: number)
  //   Use this.cart.update(items => items.filter(i => i.product.id !== id));
  removeFromCart(id: number): void {
    // your code here
  }

  // TODO 10: Use toSignal(interval(1000), { initialValue: 0 }) to create a tick signal
  // tick = toSignal(interval(1000), { initialValue: 0 });

  // TODO 11: computed elapsed — converts tick count to "MM:SS" format
  // elapsed = computed(() => {
  //   const secs = this.tick()!;
  //   const mm = String(Math.floor(secs / 60)).padStart(2, '0');
  //   const ss = String(secs % 60).padStart(2, '0');
  //   return `${mm}:${ss}`;
  // });
}
