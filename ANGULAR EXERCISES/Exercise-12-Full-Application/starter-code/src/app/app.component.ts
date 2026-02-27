import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { NotificationService, AppNotification } from './services/notification.service';
import { NotificationToastComponent } from './components/notification-toast/notification-toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, NotificationToastComponent],
  template: `
    <nav>
      <span class="brand">TaskFlow</span>
      <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Dashboard</a>
      <a routerLink="/tasks" routerLinkActive="active">Tasks</a>
      <a routerLink="/tasks/new" class="btn-new">+ New Task</a>
    </nav>

    <router-outlet />

    <!-- TODO: Include <app-notification-toast /> here -->
  `
})
export class AppComponent implements OnInit, OnDestroy {
  // TODO: inject NotificationService, subscribe, maintain toasts list, pass to component
  private sub = new Subscription();
  ngOnInit()   { /* subscribe */ }
  ngOnDestroy(){ this.sub.unsubscribe(); }
}
