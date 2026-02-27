import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CartItem } from '../app.component';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CurrencyPipe],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent {

  // TODO 13: Declare @Input() items as CartItem[] with a default empty array.

  // TODO 14: Declare @Output() cartCleared as EventEmitter<void>.

  // TODO 15: Implement clearCart() that emits the cartCleared event.
  clearCart(): void {
    // your code here
  }
}
