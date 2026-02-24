import { Component } from '@angular/core';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart-sidebar',
  templateUrl: './cart-sidebar.component.html',
})
export class CartSidebarComponent {
  // TODO Task 9: Inject CartService using public modifier so template can access it
  // constructor(public cartService: CartService) {}
  constructor(public cartService: CartService) {}
}
