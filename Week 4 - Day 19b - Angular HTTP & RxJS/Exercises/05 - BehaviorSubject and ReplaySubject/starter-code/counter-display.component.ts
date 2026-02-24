import { Component, OnInit } from '@angular/core';
// TODO: import StateService from './state.service'

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

  // TODO: inject StateService

  ngOnInit(): void {
    // TODO: subscribe to stateService.count$, set this.count
    // TODO: subscribe to stateService.log$, push each message to this.messages
  }
}
