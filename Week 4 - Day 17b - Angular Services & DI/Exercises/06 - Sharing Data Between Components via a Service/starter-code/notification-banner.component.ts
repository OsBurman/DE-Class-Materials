import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationService } from './notification.service';

@Component({
  selector: 'app-notification-banner',
  templateUrl: './notification-banner.component.html'
})
export class NotificationBannerComponent implements OnInit, OnDestroy {
  currentMessage = '';

  // TODO 10: Declare a private Subscription property to hold the subscription.
  //          private sub!: Subscription;

  // TODO 11: Inject NotificationService via the constructor.
  constructor() {}

  ngOnInit(): void {
    // TODO 12: Subscribe to notificationService.message$ and store the emitted
    //          value in this.currentMessage. Store the subscription in this.sub.
  }

  dismiss(): void {
    // TODO 13: Call notificationService.clear().
  }

  ngOnDestroy(): void {
    // TODO 14: Call this.sub.unsubscribe() to prevent memory leaks.
  }
}
