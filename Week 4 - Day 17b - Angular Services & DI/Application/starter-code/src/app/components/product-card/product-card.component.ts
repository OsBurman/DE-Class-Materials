import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
})
export class ProductCardComponent {
  // TODO Task 3: Declare @Input() product: Product
  // TODO Task 3: Declare @Input() isInCart: boolean = false

  // TODO Task 4: Declare @Output() addToCart = new EventEmitter<Product>()
  // TODO Task 4: Declare @Output() removeFromCart = new EventEmitter<number>()

  onAdd(): void {
    // TODO Task 4: Emit addToCart with this.product
  }

  onRemove(): void {
    // TODO Task 4: Emit removeFromCart with this.product.id
  }
}
