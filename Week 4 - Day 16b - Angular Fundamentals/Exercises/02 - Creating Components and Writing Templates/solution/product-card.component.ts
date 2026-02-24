import { Component } from '@angular/core';

// @Component decorator attaches Angular metadata to this class.
// selector — the HTML tag name used in other templates: <app-product-card>
// templateUrl — points to the external HTML template for this component's view
@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {
  // TypeScript class properties — Angular's change detection watches these
  // and updates the DOM when their values change.
  name: string = 'Wireless Headphones';
  price: number = 79.99;
  brand: string = 'SoundWave';
  inStock: boolean = true;
  rating: number = 4;

  // A method called from the template returns a human-readable stock label.
  // Methods are called with {{ methodName() }} in Angular templates.
  getStockLabel(): string {
    return this.inStock ? 'Available' : 'Sold Out';
  }
}
