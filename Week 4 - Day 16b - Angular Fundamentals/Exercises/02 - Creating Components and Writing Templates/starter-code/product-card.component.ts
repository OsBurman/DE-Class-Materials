import { Component } from '@angular/core';

// TODO 1: Add the @Component decorator with:
//           selector: 'app-product-card'
//           templateUrl: './product-card.component.html'
//           styleUrls: ['./product-card.component.css']
export class ProductCardComponent {

  // TODO 2: Add the following typed properties:
  //   name: string = 'Wireless Headphones'
  //   price: number = 79.99
  //   brand: string = 'SoundWave'
  //   inStock: boolean = true
  //   rating: number = 4

  // TODO 3: Add a getStockLabel() method that returns 'Available' when inStock is true,
  //         or 'Sold Out' when inStock is false.
  getStockLabel(): string {
    // TODO: implement this method
    return '';
  }
}
