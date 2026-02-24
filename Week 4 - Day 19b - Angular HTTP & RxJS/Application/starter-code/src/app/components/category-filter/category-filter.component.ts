import { Component, Input, Output, EventEmitter } from '@angular/core';

const CATEGORIES = ['technology', 'business', 'science', 'health', 'sports', 'entertainment'];

// TODO Task 6: Build CategoryFilterComponent
@Component({
  selector: 'app-category-filter',
  templateUrl: './category-filter.component.html',
})
export class CategoryFilterComponent {
  categories = CATEGORIES;
  // TODO Task 6: @Input() selectedCategory: string = 'technology'
  @Input() selectedCategory = 'technology';
  // TODO Task 6: @Output() categoryChange = new EventEmitter<string>()
  @Output() categoryChange = new EventEmitter<string>();

  selectCategory(category: string): void {
    // TODO Task 6: Emit the selected category
    this.categoryChange.emit(category);
  }
}
