import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export type NotificationType = 'info' | 'success' | 'warning' | 'error';
export interface AppNotification { id: number; type: NotificationType; message: string; }

@Injectable({ providedIn: 'root' })
export class NotificationService {
  // TODO: Implement using Subject<AppNotification>
  // Methods: send(n), getNotifications(): Observable
  send(_: Omit<AppNotification, 'id'>): void {}
  getNotifications(): Observable<AppNotification> { return new Subject<AppNotification>().asObservable(); }
}
