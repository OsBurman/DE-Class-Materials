import { Component, computed, effect, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { interval } from 'rxjs';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface Product { id: number; name: string; price: number; emoji: string; }
export interface CartItem { product: Product; quantity: number; }

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CurrencyPipe, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  readonly products: Product[] = [
    { id: 1, name: 'Angular Sticker Pack', price: 9.99, emoji: '🎨' },
    { id: 2, name: 'TypeScript T-Shirt', price: 24.99, emoji: '👕' },
    { id: 3, name: 'RxJS Coffee Mug', price: 14.99, emoji: '☕' },
    { id: 4, name: 'Dev Mechanical Keyboard', price: 129.00, emoji: '⌨️' },
    { id: 5, name: 'Signal-Powered Hoodie', price: 49.99, emoji: '🧥' },
    { id: 6, name: 'NgRx Poster', price: 19.99, emoji: '🗓️' },
  ];

  cart = signal<CartItem[]>([]);
  discountCode = signal(localStorage.getItem('discountCode') ?? '');

  cartCount = computed(() => this.cart().reduce((sum, i) => sum + i.quantity, 0));
  subtotal = computed(() => this.cart().reduce((sum, i) => sum + i.product.price * i.quantity, 0));
  discount = computed(() => this.discountCode().toUpperCase() === 'ANGULAR10' ? 0.1 : 0);
  total = computed(() => this.subtotal() * (1 - this.discount()));

  tick = toSignal(interval(1000), { initialValue: 0 });
  elapsed = computed(() => {
    const secs = this.tick()!;
    const mm = String(Math.floor(secs / 60)).padStart(2, '0');
    const ss = String(secs % 60).padStart(2, '0');
    return `${mm}:${ss}`;
  });

  constructor() {
    effect(() => {
      localStorage.setItem('discountCode', this.discountCode());
    });
  }

  addToCart(product: Product): void {
    this.cart.update(items => {
      const existing = items.find(i => i.product.id === product.id);
      if (existing) {
        return items.map(i => i.product.id === product.id ? { ...i, quantity: i.quantity + 1 } : i);
      }
      return [...items, { product, quantity: 1 }];
    });
  }

  removeFromCart(id: number): void {
    this.cart.update(items => items.filter(i => i.product.id !== id));
  }

  setDiscountCode(value: string): void {
    this.discountCode.set(value);
  }
}
