import { Component, ViewEncapsulation } from '@angular/core';

// Emulated is the default — Angular adds _ngcontent-* attribute selectors to scope styles
@Component({
  selector: 'app-emulated-card',
  encapsulation: ViewEncapsulation.Emulated,  // could be omitted — this is the default
  styles: ['.card { border: 3px solid green; background: #e8f5e9; }'],
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
