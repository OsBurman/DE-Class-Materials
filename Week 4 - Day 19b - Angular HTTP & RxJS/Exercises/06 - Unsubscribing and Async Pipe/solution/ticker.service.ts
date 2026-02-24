import { Injectable } from '@angular/core';
import { Observable, interval } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class TickerService {
  /** Emits 1, 2, 3... every second. */
  getTicker(): Observable<number> {
    return interval(1000).pipe(map(i => i + 1));
  }
}
