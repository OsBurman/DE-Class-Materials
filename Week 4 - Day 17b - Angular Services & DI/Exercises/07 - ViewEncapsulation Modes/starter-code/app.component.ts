import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <h1>ViewEncapsulation Demo</h1>
    <style>
      /* Global base styles for all .card elements */
      .card { padding: 16px; margin: 12px; border-radius: 6px; font-family: Arial, sans-serif; }
    </style>
    <!-- TODO: Render app-emulated-card, app-none-card, app-shadow-card -->
  `
})
export class AppComponent { }
