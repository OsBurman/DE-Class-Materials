import { Component, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>Theme Configurator</h1>

    <!-- TODO: Display theme() and fontSize() -->
    <p>Theme: &nbsp;&nbsp; Font Size: px</p>

    <div>
      <button (click)="toggleTheme()">Toggle Theme</button>
      <button (click)="increaseFontSize()">Increase Font</button>
      <button (click)="decreaseFontSize()">Decrease Font</button>
    </div>

    <h3>Effect Log:</h3>
    <ul>
      <!-- TODO: Render each entry in logHistory() using *ngFor or @for -->
    </ul>
  `
})
export class AppComponent {
  // TODO: Create writable signal 'theme' of type 'light' | 'dark', initialised to 'light'
  theme = signal<'light' | 'dark'>('light');

  // TODO: Create writable signal 'fontSize' (number), initialised to 16
  fontSize = signal<number>(16);

  // TODO: Create writable signal 'logHistory' as string[], initialised to []
  logHistory = signal<string[]>([]);

  constructor() {
    // TODO: Create an effect() here that:
    //  1. Reads theme() and fontSize()
    //  2. Sets document.body.setAttribute('data-theme', theme())
    //  3. Sets document.body.style.fontSize = fontSize() + 'px'
    //  4. Pushes a log entry "[timestamp] theme=X, fontSize=Y" into logHistory using .update()
  }

  // TODO: Implement toggleTheme() — use .update() to flip between 'light' and 'dark'
  toggleTheme() {

  }

  // TODO: Implement increaseFontSize() — add 2, cap at 24
  increaseFontSize() {

  }

  // TODO: Implement decreaseFontSize() — subtract 2, minimum 10
  decreaseFontSize() {

  }
}
