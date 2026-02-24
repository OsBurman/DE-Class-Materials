import { Component } from '@angular/core';

// @Component is a decorator that turns a plain TypeScript class into an Angular component.
// It attaches metadata: the CSS selector, the HTML template, and optional styles.
@Component({
  selector: 'app-root',          // Angular replaces <app-root></app-root> with this template
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  // Class properties are accessible in the template via {{ propertyName }}
  title = 'My Angular App';
}
