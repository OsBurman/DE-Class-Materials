import { Injectable } from '@angular/core';
// TODO: import BehaviorSubject, ReplaySubject from 'rxjs'
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class StateService {
  // TODO: declare private countSubject = new BehaviorSubject<number>(0)
  // TODO: expose count$ = this.countSubject.asObservable()

  // TODO: declare private logSubject = new ReplaySubject<string>(3)
  // TODO: expose log$ = this.logSubject.asObservable()

  increment(): void {
    // TODO: emit countSubject.value + 1
    // TODO: log 'Incremented to <newValue>'
  }

  decrement(): void {
    // TODO: emit countSubject.value - 1
    // TODO: log 'Decremented to <newValue>'
  }

  reset(): void {
    // TODO: emit 0
    // TODO: log 'Reset to 0'
  }
}
