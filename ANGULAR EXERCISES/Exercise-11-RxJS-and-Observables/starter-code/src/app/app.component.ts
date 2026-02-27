import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, AppNotification } from './services/notification.service';
import { StockService, Stock } from './services/stock.service';
import { LiveSearchComponent } from './components/live-search/live-search.component';
// TODO 11: import Subscription from 'rxjs'

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, LiveSearchComponent],
  template: `
    <header>
      <h1>RxJS Real-time Dashboard</h1>
    </header>

    <main>
      <!-- Notification Center -->
      <section class="panel">
        <h2>Notifications</h2>
        <div class="notification-controls">
          <button (click)="sendInfo()">Send Info</button>
          <button (click)="sendSuccess()">Send Success</button>
          <button (click)="sendWarning()">Send Warning</button>
          <button (click)="sendError()">Send Error</button>
          <button (click)="clearNotifications()">Clear</button>
        </div>
        <ul class="notifications">
          @for (n of notifications; track n.id) {
            <li [class]="n.type">{{ n.message }} <small>{{ n.timestamp | date:'HH:mm:ss' }}</small></li>
          }
        </ul>
      </section>

      <!-- Stock Ticker -->
      <section class="panel">
        <h2>Live Stocks</h2>
        <!-- TODO 12: Display stocks$ | async here as a table -->
        <!-- Columns: Symbol, Name, Price ($), Change ($) -->
      </section>

      <!-- Live Search -->
      <section class="panel">
        <app-live-search />
      </section>
    </main>
  `
})
export class AppComponent implements OnInit, OnDestroy {
  // TODO 13: inject NotificationService and StockService

  notifications: AppNotification[] = [];

  // TODO 14: Declare stocks$ as an Observable<Stock[]> — use stockService.getStocks()

  // TODO 15: Declare a Subscription property to manage cleanup

  ngOnInit() {
    // TODO 16: Subscribe to notificationService.getNotifications()
    //   On each notification: prepend to this.notifications array (show newest first)
    //   Keep only the latest 10 notifications (slice(0, 10))
    //   Store the subscription

    // TODO 17: Assign this.stocks$ = this.stockService.getStocks()
  }

  ngOnDestroy() {
    // TODO 18: Unsubscribe from the subscription to prevent memory leaks
  }

  sendInfo()    { /* TODO 19: call notificationService.send({ type: 'info',    message: 'Info: ...' }) */ }
  sendSuccess() { /* TODO 20: call notificationService.send({ type: 'success', message: 'Success!' }) */ }
  sendWarning() { /* TODO 21: call notificationService.send({ type: 'warning', message: 'Warning!' }) */ }
  sendError()   { /* TODO 22: call notificationService.send({ type: 'error',   message: 'Error!' }) */ }

  clearNotifications() {
    this.notifications = [];
  }
}
