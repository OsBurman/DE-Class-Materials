import { Injectable } from '@angular/core';
import { Product, CartItem } from '../models/product.model';

// TODO Task 1: Add @Injectable({ providedIn: 'root' })
// This makes CartService available app-wide without listing it in providers.
@Injectable({ providedIn: 'root' })
export class CartService {
  // TODO Task 1: Declare a private cartItems array: CartItem[]
  private cartItems: CartItem[] = [];

  // TODO Task 1: Implement addItem(product: Product)
  // Check if the product already exists in the cart. If yes, increment quantity.
  // If not, push { product, quantity: 1 } to cartItems.
  addItem(product: Product): void {
    // TODO
  }

  // TODO Task 1: Implement removeItem(productId: number)
  // Filter out the item where product.id === productId
  removeItem(productId: number): void {
    // TODO
  }

  // TODO Task 1: Implement getItems() — return a copy of cartItems
  getItems(): CartItem[] {
    // TODO: return [...this.cartItems]
    return [];
  }

  // TODO Task 1: Implement getTotal() — sum of (item.product.price * item.quantity)
  getTotal(): number {
    // TODO
    return 0;
  }

  // TODO Task 1: Implement getCount() — sum of all quantities
  getCount(): number {
    // TODO
    return 0;
  }

  // TODO Task 1: Implement clearCart() — reset cartItems to []
  clearCart(): void {
    // TODO
  }

  isInCart(productId: number): boolean {
    return this.cartItems.some(item => item.product.id === productId);
  }
}
