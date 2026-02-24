import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Signal Counter</h1>

    <!-- Read signals by calling them as functions in the template -->
    <p>Count: {{ count() }}</p>
    <p>Hello, {{ username() }}</p>

    <div>
      <button (click)="increment()">Increment</button>
      <button (click)="decrement()">Decrement</button>
      <button (click)="reset()">Reset</button>
    </div>

    <div>
      <input [(ngModel)]="inputName" placeholder="Enter name" />
      <button (click)="setUsername(inputName)">Set Name</button>
    </div>
  `
})
export class AppComponent {
  // signal<number>(0) creates a writable signal with an initial value of 0
  count = signal<number>(0);

  // String signal — TypeScript infers the type from the initial value
  username = signal<string>('Guest');

  inputName = '';

  increment() {
    // .update() receives the current value and returns the new value
    this.count.update(prev => prev + 1);
  }

  decrement() {
    // Math.max prevents the counter going below 0
    this.count.update(prev => Math.max(0, prev - 1));
  }

  reset() {
    // .set() replaces the value unconditionally
    this.count.set(0);
  }

  setUsername(name: string) {
    if (name.trim()) {
      this.username.set(name.trim());
    }
  }
}
