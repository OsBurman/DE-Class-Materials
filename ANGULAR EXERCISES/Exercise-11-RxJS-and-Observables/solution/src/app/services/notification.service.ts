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
  private notifications$ = new Subject<AppNotification>();
  private nextId = 1;

  send(notification: Omit<AppNotification, 'id' | 'timestamp'>): void {
    this.notifications$.next({
      ...notification,
      id: this.nextId++,
      timestamp: new Date(),
    });
  }

  getNotifications(): Observable<AppNotification> {
    return this.notifications$.asObservable();
  }
}
