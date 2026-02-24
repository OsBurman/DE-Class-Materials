import { Component, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>Theme Configurator</h1>

    <p>Theme: {{ theme() }} &nbsp;&nbsp; Font Size: {{ fontSize() }}px</p>

    <div>
      <button (click)="toggleTheme()">Toggle Theme</button>
      <button (click)="increaseFontSize()">Increase Font</button>
      <button (click)="decreaseFontSize()">Decrease Font</button>
    </div>

    <h3>Effect Log:</h3>
    <ul>
      <!-- logHistory() returns an array; Angular re-renders the list whenever the signal changes -->
      <li *ngFor="let entry of logHistory()">{{ entry }}</li>
    </ul>
  `
})
export class AppComponent {
  theme = signal<'light' | 'dark'>('light');
  fontSize = signal<number>(16);
  logHistory = signal<string[]>([]);

  constructor() {
    // effect() MUST be created in an injection context (here: inside the constructor).
    // Angular automatically tracks every signal read inside the function.
    effect(() => {
      const t = this.theme();
      const fs = this.fontSize();

      // Side-effect: synchronise the DOM
      document.body.setAttribute('data-theme', t);
      document.body.style.fontSize = fs + 'px';

      // Append an immutable log entry.
      // Using spread [...prev, newEntry] because signals use reference equality for arrays.
      const timestamp = new Date().toLocaleTimeString();
      this.logHistory.update(prev => [
        ...prev,
        `[${timestamp}] theme=${t}, fontSize=${fs}`
      ]);
    });
  }

  toggleTheme() {
    // .update() gives the current value so we can derive the next value
    this.theme.update(t => t === 'light' ? 'dark' : 'light');
  }

  increaseFontSize() {
    this.fontSize.update(fs => Math.min(24, fs + 2));
  }

  decreaseFontSize() {
    this.fontSize.update(fs => Math.max(10, fs - 2));
  }
}
