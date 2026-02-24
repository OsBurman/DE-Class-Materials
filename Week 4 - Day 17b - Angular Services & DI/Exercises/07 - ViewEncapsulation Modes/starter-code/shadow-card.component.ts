import { Component, ViewEncapsulation } from '@angular/core';

// TODO 5: Add 'encapsulation: ViewEncapsulation.ShadowDom' to the decorator.
@Component({
  selector: 'app-shadow-card',
  // TODO 6: Add a 'styles' array with: '.card { border: 3px solid blue; background: #e3f2fd; }'
  styles: [/* TODO */],
  template: `
    <div class="card">
      <h3>Shadow DOM Encapsulation</h3>
      <p>Styles are truly isolated via native browser Shadow DOM.<br>
         They <strong>cannot</strong> be overridden from outside.</p>
      <small>Open DevTools → Elements to see the #shadow-root node.</small>
    </div>
  `
})
export class ShadowCardComponent { }
