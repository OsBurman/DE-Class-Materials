import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export type NotificationType = 'info' | 'success' | 'warning' | 'error';

export interface AppNotification {
  id: number;
  type: NotificationType;
  message: string;
  timestamp: Date;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {

  // TODO 1: Create a private Subject<AppNotification>
  // private notifications$ = new Subject<AppNotification>();

  // TODO 2: Implement send(notification: Omit<AppNotification, 'id' | 'timestamp'>)
  //   - Create a full notification with id: Date.now() and timestamp: new Date()
  //   - Call this.notifications$.next(fullNotification)
  send(notification: Omit<AppNotification, 'id' | 'timestamp'>): void {
    // your code here
  }

  // TODO 3: Implement getNotifications(): returns notifications$.asObservable()
  getNotifications(): Observable<AppNotification> {
    // your code here
    return new Subject<AppNotification>().asObservable();
  }
}
