import { Component, ViewEncapsulation } from '@angular/core';

// ShadowDom: uses the native browser Shadow DOM — true style isolation
@Component({
  selector: 'app-shadow-card',
  encapsulation: ViewEncapsulation.ShadowDom,
  styles: ['.card { border: 3px solid blue; background: #e3f2fd; }'],
  template: `
    <div class="card">
      <h3>Shadow DOM Encapsulation</h3>
      <p>Styles are truly isolated via native browser Shadow DOM.<br>
         They <strong>cannot</strong> be overridden from outside, and don't leak out.</p>
      <small>Open DevTools → Elements to see the #shadow-root node.</small>
    </div>
  `
})
export class ShadowCardComponent { }
