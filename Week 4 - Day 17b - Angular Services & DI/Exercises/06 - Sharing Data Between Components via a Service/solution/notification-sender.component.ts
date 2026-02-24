import { Component } from '@angular/core';
import { NotificationService } from './notification.service';

@Component({
  selector: 'app-notification-sender',
  templateUrl: './notification-sender.component.html'
})
export class NotificationSenderComponent {
  draftMessage = '';

  constructor(private notificationService: NotificationService) {}

  send(message: string): void {
    if (!message.trim()) return;
    this.notificationService.send(message);
    // Clear the draft field after sending
    if (message === this.draftMessage) {
      this.draftMessage = '';
    }
  }
}
