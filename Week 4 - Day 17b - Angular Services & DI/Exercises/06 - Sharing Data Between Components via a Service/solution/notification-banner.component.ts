import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationService } from './notification.service';

@Component({
  selector: 'app-notification-banner',
  templateUrl: './notification-banner.component.html'
})
export class NotificationBannerComponent implements OnInit, OnDestroy {
  currentMessage = '';
  private sub!: Subscription;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    // Subscribe to the shared observable — updates arrive whenever send() is called
    this.sub = this.notificationService.message$.subscribe(
      msg => this.currentMessage = msg
    );
  }

  dismiss(): void {
    this.notificationService.clear();  // resets BehaviorSubject → subscribers get ''
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();  // prevent memory leak when component is destroyed
  }
}
