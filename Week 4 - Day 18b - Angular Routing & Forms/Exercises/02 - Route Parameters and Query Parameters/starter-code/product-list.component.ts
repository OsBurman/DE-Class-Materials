import { Component } from '@angular/core';
// TODO 4 – Import Router from '@angular/router'

interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
}

@Component({
  selector: 'app-product-list',
  template: `
    <div style="padding:1rem;">
      <h2>Products</h2>
      <div
        *ngFor="let product of products"
        style="border:1px solid #ccc; border-radius:6px; padding:0.75rem; margin-bottom:0.75rem; cursor:pointer;"
        (click)="viewProduct(product)"
      >
        <strong>{{ product.name }}</strong>
        <span style="margin-left:1rem; color:#666;">{{ product.category }}</span>
        <span style="float:right;">\${{ product.price }}</span>
      </div>
    </div>
  `,
})
export class ProductListComponent {
  // TODO 4 – Inject Router in the constructor: private router: Router
  constructor() {}

  products: Product[] = [
    { id: 1, name: 'Wireless Headphones', category: 'electronics', price: 79 },
    { id: 2, name: 'Running Shoes',       category: 'footwear',    price: 120 },
    { id: 3, name: 'Coffee Maker',        category: 'appliances',  price: 45 },
  ];

  viewProduct(product: Product): void {
    // TODO 4 – Navigate to ['/products', product.id] with queryParams: { category: product.category }
  }
}
