import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <h2>Shared Counter</h2>
    <app-counter-controls></app-counter-controls>
    <app-counter-display></app-counter-display>
  `,
})
export class AppComponent {}
