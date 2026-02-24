import { Component, ViewEncapsulation } from '@angular/core';

// TODO 3: Add 'encapsulation: ViewEncapsulation.None' to the decorator.
@Component({
  selector: 'app-none-card',
  // TODO 4: Add a 'styles' array with: '.card { border: 3px solid red; }'
  //         Watch how this leaks and overrides the green border on the Emulated card!
  styles: [/* TODO */],
  template: `
    <div class="card">
      <h3>No Encapsulation</h3>
      <p>These styles are injected globally into &lt;head&gt;.<br>
         They <strong>leak</strong> and override styles in other components.</p>
      <small>Open DevTools → &lt;head&gt; to see the unscoped &lt;style&gt; tag.</small>
    </div>
  `
})
export class NoneCardComponent { }
