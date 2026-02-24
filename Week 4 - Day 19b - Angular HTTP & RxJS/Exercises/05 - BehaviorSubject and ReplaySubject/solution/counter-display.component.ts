import { Component, OnInit } from '@angular/core';
import { StateService } from './state.service';

@Component({
  selector: 'app-counter-display',
  template: `
    <p>Count: <strong>{{ count }}</strong></p>
    <ul>
      <li *ngFor="let m of messages">{{ m }}</li>
    </ul>
  `,
})
export class CounterDisplayComponent implements OnInit {
  count    = 0;
  messages: string[] = [];

  constructor(private stateService: StateService) {}

  ngOnInit(): void {
    this.stateService.count$.subscribe(n => (this.count = n));
    this.stateService.log$.subscribe(msg => this.messages.push(msg));
  }
}
