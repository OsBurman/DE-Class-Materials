import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  products = [
    { name: 'React Fundamentals',   rating: 4 },
    { name: 'Spring Boot',          rating: 5 },
    { name: 'TypeScript Deep Dive', rating: 3 },
  ];

  // Called when a RatingBadgeComponent emits the (upvoted) event
  onUpvote(name: string): void {
    const product = this.products.find(p => p.name === name);
    if (product) {
      product.rating++;  // mutate in-place — Angular's change detection picks this up
    }
  }
}
