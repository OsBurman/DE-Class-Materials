import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-rating-badge',
  templateUrl: './rating-badge.component.html'
})
export class RatingBadgeComponent {
  // TODO 4: Declare an @Input() property 'productName' of type string.

  // TODO 5: Declare an @Input() property 'rating' of type number.

  // TODO 6: Declare an @Output() property 'upvoted' as a new EventEmitter<string>().

  onUpvoteClick(): void {
    // TODO 7: Emit the productName value via the 'upvoted' EventEmitter.
  }
}
