import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Product } from '../app.component';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CurrencyPipe],
  template: `
    <div class="product-card">
      <img [src]="product.image" [alt]="product.name" />
      <div class="card-body">
        <span class="category-badge">{{ product.category }}</span>
        <h3>{{ product.name }}</h3>
        <p class="price">{{ product.price | currency }}</p>
        <button class="btn-add-cart" (click)="onAddClick()">🛒 Add to Cart</button>
      </div>
    </div>
  `,
  styles: [`
    .product-card { border-radius: 12px; overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,0.1); background: white; }
    img { width: 100%; height: 160px; object-fit: cover; }
    .card-body { padding: 1rem; }
    .category-badge { background: #ebf4ff; color: #3182ce; border-radius: 999px; padding: 0.15rem 0.6rem; font-size: 0.75rem; font-weight: 600; }
    h3 { margin: 0.5rem 0 0.25rem; font-size: 1rem; }
    .price { color: #667eea; font-weight: 700; font-size: 1.1rem; margin-bottom: 0.75rem; }
    .btn-add-cart { width: 100%; background: #667eea; color: white; border: none; border-radius: 8px; padding: 0.5rem; cursor: pointer; font-weight: 600; }
    .btn-add-cart:hover { background: #5a67d8; }
  `]
})
export class ProductCardComponent {
  @Input() product!: Product;
  @Output() addToCart = new EventEmitter<Product>();

  onAddClick(): void {
    this.addToCart.emit(this.product);
  }
}
