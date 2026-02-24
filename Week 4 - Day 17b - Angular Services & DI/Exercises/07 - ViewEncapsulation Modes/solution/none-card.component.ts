import { Component, ViewEncapsulation } from '@angular/core';

// None: styles are injected into <head> with no scoping — they leak globally
@Component({
  selector: 'app-none-card',
  encapsulation: ViewEncapsulation.None,
  styles: ['.card { border: 3px solid red; }'],
  template: `
    <div class="card">
      <h3>No Encapsulation</h3>
      <p>These styles are injected globally into &lt;head&gt;.<br>
         They <strong>leak</strong> and can override styles in other components.</p>
      <small>Open DevTools → &lt;head&gt; to see the unscoped &lt;style&gt; tag.</small>
    </div>
  `
})
export class NoneCardComponent { }
