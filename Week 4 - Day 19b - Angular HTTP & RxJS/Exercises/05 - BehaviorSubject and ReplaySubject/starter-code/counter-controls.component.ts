import { Component } from '@angular/core';
// TODO: import StateService from './state.service'

@Component({
  selector: 'app-counter-controls',
  template: `
    <button (click)="increment()">+</button>
    <button (click)="decrement()">-</button>
    <button (click)="reset()">Reset</button>
  `,
})
export class CounterControlsComponent {
  // TODO: inject StateService

  increment(): void { /* TODO: call stateService.increment() */ }
  decrement(): void { /* TODO: call stateService.decrement() */ }
  reset(): void     { /* TODO: call stateService.reset()     */ }
}
