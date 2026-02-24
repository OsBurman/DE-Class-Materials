import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <h1 style="font-family:Arial,sans-serif; margin: 20px;">Notification System</h1>
    <div style="max-width:600px; margin: 0 20px;">
      <app-notification-banner></app-notification-banner>
      <app-notification-sender></app-notification-sender>
    </div>
  `
})
export class AppComponent { }
