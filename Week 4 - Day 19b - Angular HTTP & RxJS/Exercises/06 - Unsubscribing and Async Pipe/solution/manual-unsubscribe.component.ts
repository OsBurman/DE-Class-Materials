import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TickerService } from './ticker.service';

@Component({
  selector: 'app-manual-unsubscribe',
  template: `<p>Manual tick: {{ tick }}</p>`,
})
export class ManualUnsubscribeComponent implements OnInit, OnDestroy {
  tick = 0;

  /** Signals teardown to all takeUntil operators. */
  private destroy$ = new Subject<void>();

  constructor(private tickerService: TickerService) {}

  ngOnInit(): void {
    this.tickerService
      .getTicker()
      .pipe(takeUntil(this.destroy$))
      .subscribe(n => (this.tick = n));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
