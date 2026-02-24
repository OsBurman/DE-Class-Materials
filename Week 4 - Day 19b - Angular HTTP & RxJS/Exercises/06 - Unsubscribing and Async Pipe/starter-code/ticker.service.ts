import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
// TODO: import interval from 'rxjs'
// TODO: import map from 'rxjs/operators'

@Injectable({ providedIn: 'root' })
export class TickerService {
  /** Emits 1, 2, 3... every second */
  getTicker(): Observable<number> {
    // TODO: return interval(1000).pipe(map(i => i + 1))
    throw new Error('Not implemented');
  }
}
