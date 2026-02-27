import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppNotification } from '../../services/notification.service';

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
    .toast-container { position: fixed; top: 4.5rem; right: 1rem; z-index: 999; display: flex; flex-direction: column; gap: .5rem; }
    .toast { padding: .65rem 1.1rem; border-radius: 6px; font-weight: 600; min-width: 240px; box-shadow: 0 4px 12px rgba(0,0,0,.15); animation: slideIn .25s ease; }
    .toast.success { background: #22c55e; color: #fff; }
    .toast.error   { background: #ef4444; color: #fff; }
    .toast.warning { background: #f59e0b; color: #fff; }
    .toast.info    { background: #3b82f6; color: #fff; }
    @keyframes slideIn { from { opacity: 0; transform: translateX(30px); } to { opacity: 1; transform: none; } }
  `]
})
export class NotificationToastComponent {
  @Input() toasts: AppNotification[] = [];
}
