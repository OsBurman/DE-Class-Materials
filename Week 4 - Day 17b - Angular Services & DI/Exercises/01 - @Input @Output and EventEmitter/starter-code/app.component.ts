import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  // TODO 1: Declare a 'products' array with at least 3 objects, each having
  //         name: string and rating: number.
  products = [
    // { name: '...', rating: N },
  ];

  // TODO 2: Implement onUpvote(name: string) — find the product in the array
  //         whose name matches and increment its rating by 1.
  onUpvote(name: string): void {
    // TODO: find product by name and rating++
  }
}
