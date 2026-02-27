import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CartItem } from '../app.component';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CurrencyPipe],
  template: `
    <div class="cart">
      <h2>🛒 Cart ({{ items.length }} items)</h2>
      @if (items.length === 0) {
        <p class="empty">Your cart is empty.</p>
      } @else {
        <ul>
          @for (item of items; track item.product.id) {
            <li>
              <span class="item-name">{{ item.product.name }}</span>
              <span class="item-qty">×{{ item.quantity }}</span>
              <span class="item-subtotal">{{ item.product.price * item.quantity | currency }}</span>
            </li>
          }
        </ul>
        <div class="cart-footer">
          <strong>Total: {{ total | currency }}</strong>
          <button (click)="clearCart()">🗑 Clear Cart</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .cart { background: white; border-radius: 12px; padding: 1.25rem; box-shadow: 0 2px 12px rgba(0,0,0,0.1); }
    h2 { font-size: 1.1rem; margin-bottom: 1rem; }
    .empty { color: #a0aec0; text-align: center; padding: 1rem; }
    ul { list-style: none; display: flex; flex-direction: column; gap: 0.5rem; margin-bottom: 1rem; }
    li { display: flex; justify-content: space-between; align-items: center; font-size: 0.9rem; }
    .item-name { flex: 1; }
    .item-qty { color: #718096; margin: 0 0.5rem; }
    .item-subtotal { font-weight: 600; color: #2d3748; }
    .cart-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #e2e8f0; padding-top: 0.75rem; }
    button { background: #e53e3e; color: white; border: none; border-radius: 6px; padding: 0.4rem 0.8rem; cursor: pointer; font-size: 0.85rem; }
  `]
})
export class CartComponent {
  @Input() items: CartItem[] = [];
  @Output() cartCleared = new EventEmitter<void>();

  get total(): number {
    return this.items.reduce((sum, i) => sum + i.product.price * i.quantity, 0);
  }

  clearCart(): void {
    this.cartCleared.emit();
  }
}
