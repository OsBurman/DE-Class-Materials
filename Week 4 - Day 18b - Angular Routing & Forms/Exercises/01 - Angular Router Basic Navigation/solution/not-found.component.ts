import { Component } from '@angular/core';

@Component({
  selector: 'app-not-found',
  template: `
    <h1>404 – Page Not Found</h1>
    <p>The page you are looking for does not exist.</p>
    <!-- routerLink navigates back to the home route without a page reload -->
    <a routerLink="/">← Go Home</a>
  `,
})
export class NotFoundComponent {}
