import { Component, OnInit, OnDestroy } from '@angular/core';
// TODO: import Subject from 'rxjs'
// TODO: import takeUntil from 'rxjs/operators'
// TODO: import TickerService from './ticker.service'

@Component({
  selector: 'app-manual-unsubscribe',
  template: `<p>Manual tick: {{ tick }}</p>`,
})
export class ManualUnsubscribeComponent implements OnInit, OnDestroy {
  tick = 0;

  // TODO: declare private destroy$ = new Subject<void>()
  // TODO: inject TickerService

  ngOnInit(): void {
    // TODO: subscribe to tickerService.getTicker().pipe(takeUntil(this.destroy$))
    //   set this.tick on each emission
  }

  ngOnDestroy(): void {
    // TODO: this.destroy$.next()
    // TODO: this.destroy$.complete()
  }
}
