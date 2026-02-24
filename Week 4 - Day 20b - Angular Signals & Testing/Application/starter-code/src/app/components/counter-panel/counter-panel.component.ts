import { Component } from '@angular/core';
import { CounterService } from '../../services/counter.service';

// TODO Task 2: Build CounterPanelComponent
// Inject CounterService and use signals in the template
@Component({
  selector: 'app-counter-panel',
  templateUrl: './counter-panel.component.html',
})
export class CounterPanelComponent {
  // TODO Task 2: Inject CounterService
  constructor(public counterService: CounterService) {}
}
