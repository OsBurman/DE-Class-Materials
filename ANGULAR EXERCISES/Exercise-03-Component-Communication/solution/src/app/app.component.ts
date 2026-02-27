import { Component } from '@angular/core';
import { ProductCardComponent } from './product-card/product-card.component';
import { CartComponent } from './cart/cart.component';

export interface Product {
  id: number;
  name: string;
  price: number;
  image: string;
  category: string;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ProductCardComponent, CartComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  products: Product[] = [
    { id: 1, name: 'Wireless Headphones', price: 79.99, image: 'https://picsum.photos/200?random=1', category: 'Electronics' },
    { id: 2, name: 'Mechanical Keyboard', price: 129.00, image: 'https://picsum.photos/200?random=2', category: 'Electronics' },
    { id: 3, name: 'Running Shoes', price: 95.50, image: 'https://picsum.photos/200?random=3', category: 'Apparel' },
    { id: 4, name: 'Coffee Maker', price: 49.99, image: 'https://picsum.photos/200?random=4', category: 'Kitchen' },
    { id: 5, name: 'Yoga Mat', price: 29.00, image: 'https://picsum.photos/200?random=5', category: 'Fitness' },
    { id: 6, name: 'Desk Lamp', price: 34.99, image: 'https://picsum.photos/200?random=6', category: 'Office' },
  ];

  cartItems: CartItem[] = [];

  onAddToCart(product: Product): void {
    const existing = this.cartItems.find(i => i.product.id === product.id);
    if (existing) {
      existing.quantity++;
    } else {
      this.cartItems.push({ product, quantity: 1 });
    }
  }

  onCartCleared(): void {
    this.cartItems = [];
  }

  get cartTotal(): number {
    return this.cartItems.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  }
}
