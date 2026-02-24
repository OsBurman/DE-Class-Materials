import { Component } from '@angular/core';
import { CartService } from '../../services/cart.service';
import { Product, PRODUCTS } from '../../models/product.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
})
export class ProductListComponent {
  products: Product[] = PRODUCTS;

  // TODO Task 6: Inject CartService in the constructor
  constructor(private cartService: CartService) {}

  // TODO Task 8: Handle the addToCart @Output event from ProductCard
  onAddToCart(product: Product): void {
    // TODO: Call cartService.addItem(product)
  }

  // TODO Task 8: Handle the removeFromCart @Output event from ProductCard
  onRemoveFromCart(productId: number): void {
    // TODO: Call cartService.removeItem(productId)
  }

  isInCart(productId: number): boolean {
    return this.cartService.isInCart(productId);
  }
}
