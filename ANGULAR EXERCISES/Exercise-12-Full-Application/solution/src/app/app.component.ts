import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService, AppNotification } from './services/notification.service';
import { NotificationToastComponent } from './components/notification-toast/notification-toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet, NotificationToastComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit, OnDestroy {
  private notificationService = inject(NotificationService);
  private sub = new Subscription();

  toasts = signal<AppNotification[]>([]);

  ngOnInit() {
    this.sub.add(
      this.notificationService.getNotifications().subscribe(n => {
        this.toasts.update(list => [...list, n]);
        setTimeout(() => {
          this.toasts.update(list => list.filter(t => t.id !== n.id));
        }, 4000);
      })
    );
  }

  ngOnDestroy() { this.sub.unsubscribe(); }
}
