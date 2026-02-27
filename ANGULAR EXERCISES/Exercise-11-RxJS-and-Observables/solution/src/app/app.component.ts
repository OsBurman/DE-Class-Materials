import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { NotificationService, AppNotification } from './services/notification.service';
import { StockService, Stock } from './services/stock.service';
import { LiveSearchComponent } from './components/live-search/live-search.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, DecimalPipe, LiveSearchComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit, OnDestroy {
  private notificationService = inject(NotificationService);
  private stockService = inject(StockService);

  notifications: AppNotification[] = [];
  stocks: Stock[] = [];

  private sub = new Subscription();

  ngOnInit() {
    this.sub.add(
      this.notificationService.getNotifications().subscribe(n => {
        this.notifications = [n, ...this.notifications].slice(0, 10);
      })
    );

    this.sub.add(
      this.stockService.getStocks().subscribe(stocks => {
        this.stocks = stocks;
      })
    );
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  sendInfo()    { this.notificationService.send({ type: 'info',    message: '💡 System updated successfully.' }); }
  sendSuccess() { this.notificationService.send({ type: 'success', message: '✅ Operation completed!' }); }
  sendWarning() { this.notificationService.send({ type: 'warning', message: '⚠️ Disk usage above 80%.' }); }
  sendError()   { this.notificationService.send({ type: 'error',   message: '🔴 Connection lost. Retrying…' }); }

  clearNotifications() {
    this.notifications = [];
  }
}
