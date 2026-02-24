import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-rating-badge',
  templateUrl: './rating-badge.component.html'
})
export class RatingBadgeComponent {
  @Input() productName!: string;  // '!' = definitely assigned via @Input
  @Input() rating!: number;
  @Output() upvoted = new EventEmitter<string>();

  onUpvoteClick(): void {
    this.upvoted.emit(this.productName);  // bubble the product name up to the parent
  }
}
