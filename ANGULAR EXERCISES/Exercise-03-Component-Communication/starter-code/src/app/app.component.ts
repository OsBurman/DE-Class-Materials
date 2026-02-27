import { Component } from '@angular/core';
import { ProductCardComponent } from './product-card/product-card.component';
import { CartComponent } from './cart/cart.component';
import { CurrencyPipe } from '@angular/common';

// TODO 1: Define a Product interface with: id, name, price, image, category
export interface Product {
  // your fields here
}

// TODO 2: Define a CartItem interface with: product: Product, quantity: number
export interface CartItem {
  // your fields here
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ProductCardComponent, CartComponent, CurrencyPipe],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  // TODO 3: Create a `products` array with 6 sample products.
  //         Use https://picsum.photos/200 for images (each with a different ?random=N).
  products: Product[] = [
    // { id: 1, name: 'Wireless Headphones', price: 79.99, image: '...', category: 'Electronics' },
    // ...
  ];

  // TODO 4: Create an empty `cartItems` array of type CartItem[]
  cartItems: CartItem[] = [];

  // TODO 5: Implement onAddToCart(product: Product).
  //   - Check if the product is already in cartItems (by product.id)
  //   - If yes: increment its quantity
  //   - If no: push { product, quantity: 1 }
  onAddToCart(product: Product): void {
    // your code here
  }

  // TODO 6: Implement onCartCleared() — reset cartItems to an empty array.
  onCartCleared(): void {
    // your code here
  }

  // TODO 7: Create a `cartTotal` getter that returns the sum of (price × quantity)
  //         for all cart items.
  get cartTotal(): number {
    // your code here
    return 0;
  }
}
