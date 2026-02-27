import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({ selector: 'app-not-found', standalone: true, imports: [RouterLink],
  template: `<div style="text-align:center; padding: 4rem"><h1>404 — Page Not Found</h1><p>The page you're looking for doesn't exist.</p><a routerLink="/">Go Home</a></div>`
})
export class NotFoundComponent {}
