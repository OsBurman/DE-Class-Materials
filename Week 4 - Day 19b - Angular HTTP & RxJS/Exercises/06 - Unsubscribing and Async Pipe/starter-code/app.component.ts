import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <h2>Subscription Management</h2>
    <h3>Manual unsubscribe (takeUntil)</h3>
    <app-manual-unsubscribe *ngIf="showManual"></app-manual-unsubscribe>
    <button (click)="showManual = !showManual">
      {{ showManual ? 'Destroy' : 'Create' }} manual component
    </button>
    <hr>
    <h3>Async pipe</h3>
    <app-async-pipe></app-async-pipe>
  `,
})
export class AppComponent {
  showManual = true;
}
