import { Component, OnInit } from '@angular/core';
// TODO 5 – Import ActivatedRoute from '@angular/router'

@Component({
  selector: 'app-product-detail',
  template: `
    <div style="padding:1rem;">
      <h2>Product Detail</h2>
      <p><strong>Product ID:</strong> {{ productId }}</p>
      <p><strong>Category:</strong>  {{ category }}</p>
      <a routerLink="/products">← Back to list</a>
    </div>
  `,
})
export class ProductDetailComponent implements OnInit {
  productId: string | null = null;
  category:  string | null = null;

  // TODO 5 – Inject ActivatedRoute in the constructor: private route: ActivatedRoute
  constructor() {}

  ngOnInit(): void {
    // TODO 6 – Read the 'id' route param:
    //   this.productId = this.route.snapshot.paramMap.get('id');

    // TODO 7 – Read the 'category' query param:
    //   this.category = this.route.snapshot.queryParamMap.get('category');
  }
}
