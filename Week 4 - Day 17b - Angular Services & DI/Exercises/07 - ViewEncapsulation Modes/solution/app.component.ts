import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <h1 style="font-family:Arial,sans-serif; margin:20px;">ViewEncapsulation Demo</h1>
    <style>
      /* Global base — padding and layout for all .card elements */
      .card { padding: 16px; margin: 12px; border-radius: 6px; font-family: Arial, sans-serif; max-width: 420px; }
    </style>
    <app-emulated-card></app-emulated-card>
    <app-none-card></app-none-card>
    <app-shadow-card></app-shadow-card>
  `
})
export class AppComponent { }
