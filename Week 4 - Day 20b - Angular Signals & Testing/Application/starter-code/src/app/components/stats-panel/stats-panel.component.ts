import { Component } from '@angular/core';
import { CounterService } from '../../services/counter.service';

// TODO Task 4: Build StatsPanelComponent
@Component({
  selector: 'app-stats-panel',
  templateUrl: './stats-panel.component.html',
})
export class StatsPanelComponent {
  constructor(public counterService: CounterService) {}
}
