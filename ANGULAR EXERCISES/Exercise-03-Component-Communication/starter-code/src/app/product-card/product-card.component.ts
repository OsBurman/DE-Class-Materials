import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Product } from '../app.component';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CurrencyPipe],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css'],
})
export class ProductCardComponent {

  // TODO 8: Declare @Input() product using the Product interface.
  //         Use the definite assignment assertion (!) since it will always be provided.

  // TODO 9: Declare @Output() addToCart as an EventEmitter that emits a Product.

  // TODO 10: Implement onAddClick() that emits this.product via the addToCart EventEmitter.
  onAddClick(): void {
    // your code here
  }
}
