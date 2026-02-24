import { Component } from '@angular/core';
import { NotificationService } from './notification.service';

@Component({
  selector: 'app-notification-sender',
  templateUrl: './notification-sender.component.html'
})
export class NotificationSenderComponent {
  draftMessage = '';

  // TODO 6: Inject NotificationService via the constructor.
  constructor() {}

  send(message: string): void {
    // TODO 7: Call notificationService.send(message).
    //         If sending draftMessage, also clear this.draftMessage = '' afterward.
  }
}
