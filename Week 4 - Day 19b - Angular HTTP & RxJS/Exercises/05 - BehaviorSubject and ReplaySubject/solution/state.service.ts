import { Injectable } from '@angular/core';
import { BehaviorSubject, ReplaySubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class StateService {
  private countSubject = new BehaviorSubject<number>(0);
  readonly count$ = this.countSubject.asObservable();

  /** Buffer the last 3 action messages for late subscribers. */
  private logSubject = new ReplaySubject<string>(3);
  readonly log$ = this.logSubject.asObservable();

  increment(): void {
    const next = this.countSubject.value + 1;
    this.countSubject.next(next);
    this.logSubject.next(`Incremented to ${next}`);
  }

  decrement(): void {
    const next = this.countSubject.value - 1;
    this.countSubject.next(next);
    this.logSubject.next(`Decremented to ${next}`);
  }

  reset(): void {
    this.countSubject.next(0);
    this.logSubject.next('Reset to 0');
  }
}
