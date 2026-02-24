import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CounterService } from './counter.service';

@Component({
  selector: 'app-counter',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <span id="count">{{ service.getCount() }}</span>
      <span id="sign">{{ service.getCount() > 0 ? 'positive' : 'negative/zero' }}</span>
      <button id="btn-inc" (click)="service.increment()">Increment</button>
      <button id="btn-dec" (click)="service.decrement()">Decrement</button>
    </div>
  `
})
export class CounterComponent {
  // Inject as public so tests can spy on it directly via component.service
  constructor(public service: CounterService) {}
}
