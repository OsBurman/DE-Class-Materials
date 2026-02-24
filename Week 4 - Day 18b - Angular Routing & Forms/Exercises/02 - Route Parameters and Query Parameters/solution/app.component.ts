import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <h2 style="padding:0.5rem 0.75rem; background:#333; color:#fff; margin:0;">
      Product Catalogue
    </h2>
    <router-outlet></router-outlet>
  `,
})
export class AppComponent {}
