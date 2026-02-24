import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
// TODO: import TickerService from './ticker.service'

@Component({
  selector: 'app-async-pipe',
  // TODO: update template to use {{ tick$ | async }}
  template: `<p>Async tick: {{ tick$ | async }}</p>`,
})
export class AsyncPipeComponent implements OnInit {
  // TODO: declare tick$: Observable<number>
  tick$!: Observable<number>;

  // TODO: inject TickerService

  ngOnInit(): void {
    // TODO: assign this.tick$ = this.tickerService.getTicker()
  }
}
