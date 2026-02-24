import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TickerService } from './ticker.service';

@Component({
  selector: 'app-async-pipe',
  // Angular's async pipe subscribes and unsubscribes automatically.
  template: `<p>Async tick: {{ tick$ | async }}</p>`,
})
export class AsyncPipeComponent implements OnInit {
  tick$!: Observable<number>;

  constructor(private tickerService: TickerService) {}

  ngOnInit(): void {
    this.tick$ = this.tickerService.getTicker();
  }
}
