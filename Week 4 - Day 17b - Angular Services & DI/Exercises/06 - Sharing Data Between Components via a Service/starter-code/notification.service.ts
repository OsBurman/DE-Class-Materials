import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

// TODO 1: Add @Injectable({ providedIn: 'root' }) decorator.
@Injectable({ providedIn: 'root' })
export class NotificationService {
  // TODO 2: Declare a private BehaviorSubject<string> initialized to ''.
  //         private messageSubject = new BehaviorSubject<string>('');

  // TODO 3: Expose a public read-only observable:
  //         message$ = this.messageSubject.asObservable();

  // TODO 4: Implement send(message: string) — call messageSubject.next(message).
  send(message: string): void {
    // TODO: this.messageSubject.next(message);
  }

  // TODO 5: Implement clear() — call messageSubject.next('').
  clear(): void {
    // TODO: this.messageSubject.next('');
  }
}
