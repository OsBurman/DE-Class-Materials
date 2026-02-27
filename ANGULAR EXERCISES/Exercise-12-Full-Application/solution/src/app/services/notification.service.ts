import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export type NotificationType = 'info' | 'success' | 'warning' | 'error';

export interface AppNotification {
  id: number;
  type: NotificationType;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private stream$ = new Subject<AppNotification>();
  private nextId = 1;

  send(n: Omit<AppNotification, 'id'>): void {
    this.stream$.next({ ...n, id: this.nextId++ });
  }

  getNotifications(): Observable<AppNotification> {
    return this.stream$.asObservable();
  }
}
