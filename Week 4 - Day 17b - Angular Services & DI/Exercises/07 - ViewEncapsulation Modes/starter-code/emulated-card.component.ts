import { Component, ViewEncapsulation } from '@angular/core';

// TODO 1: Add 'encapsulation: ViewEncapsulation.Emulated' to the decorator
//         (or omit it — Emulated is the default). Either is correct.
@Component({
  selector: 'app-emulated-card',
  // TODO 2: Add a 'styles' array with: '.card { border: 3px solid green; background: #e8f5e9; }'
  styles: [/* TODO */],
  template: `
    <div class="card">
      <h3>Emulated Encapsulation</h3>
      <p>Styles are scoped with generated attribute selectors.<br>
         They should <strong>not</strong> affect sibling components.</p>
      <small>Open DevTools → Elements to see the _ngcontent-* attribute.</small>
    </div>
  `
})
export class EmulatedCardComponent { }
