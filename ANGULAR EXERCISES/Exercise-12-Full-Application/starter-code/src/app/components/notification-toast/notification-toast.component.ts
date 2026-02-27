import { Component, Input, OnInit } from '@angular/core';
import { AppNotification } from '../../services/notification.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notification-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      @for (toast of toasts; track toast.id) {
        <div [class]="'toast ' + toast.type">{{ toast.message }}</div>
      }
    </div>
  `,
  styles: [`
    .toast-container { position: fixed; top: 1rem; right: 1rem; z-index: 999; display: flex; flex-direction: column; gap: .5rem; }
    .toast { padding: .6rem 1rem; border-radius: 6px; font-weight: 600; min-width: 220px; animation: fadeIn .2s ease; }
    .toast.success { background: #22c55e; color: #fff; }
    .toast.error   { background: #ef4444; color: #fff; }
    .toast.warning { background: #f59e0b; color: #fff; }
    .toast.info    { background: #3b82f6; color: #fff; }
    @keyframes fadeIn { from { opacity: 0; transform: translateX(20px); } to { opacity: 1; transform: none; } }
  `]
})
export class NotificationToastComponent {
  // TODO: Accept @Input() toasts: AppNotification[] = [] from parent
  toasts: AppNotification[] = [];
}
