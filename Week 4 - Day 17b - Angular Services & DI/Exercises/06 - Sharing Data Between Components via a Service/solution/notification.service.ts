import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  // BehaviorSubject always holds the "current" value and replays it to new subscribers
  private messageSubject = new BehaviorSubject<string>('');

  // Expose as read-only observable — external code cannot call .next() directly
  message$ = this.messageSubject.asObservable();

  send(message: string): void {
    this.messageSubject.next(message);
  }

  clear(): void {
    this.messageSubject.next('');
  }
}
