import { Component } from '@angular/core';
import { StateService } from './state.service';

@Component({
  selector: 'app-counter-controls',
  template: `
    <button (click)="increment()">+</button>
    <button (click)="decrement()">-</button>
    <button (click)="reset()">Reset</button>
  `,
})
export class CounterControlsComponent {
  constructor(private stateService: StateService) {}

  increment(): void { this.stateService.increment(); }
  decrement(): void { this.stateService.decrement(); }
  reset(): void     { this.stateService.reset();     }
}
