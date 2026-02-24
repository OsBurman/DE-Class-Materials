import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-product-detail',
  template: `
    <div style="padding:1rem;">
      <h2>Product Detail</h2>
      <!-- paramMap provides the dynamic :id segment from the URL path -->
      <p><strong>Product ID:</strong> {{ productId }}</p>
      <!-- queryParamMap provides optional ?key=value pairs -->
      <p><strong>Category:</strong>  {{ category }}</p>
      <a routerLink="/products">← Back to list</a>
    </div>
  `,
})
export class ProductDetailComponent implements OnInit {
  productId: string | null = null;
  category:  string | null = null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // snapshot is fine here because the component is destroyed when navigating away;
    // use route.paramMap (Observable) if the same component instance can receive new params.
    this.productId = this.route.snapshot.paramMap.get('id');
    this.category  = this.route.snapshot.queryParamMap.get('category');
  }
}
