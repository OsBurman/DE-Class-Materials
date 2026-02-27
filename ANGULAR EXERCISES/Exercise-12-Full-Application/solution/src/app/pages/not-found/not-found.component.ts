import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div style="text-align:center; padding: 5rem 2rem;">
      <h1 style="font-size:3rem">404</h1>
      <p style="color:#64748b; margin:.75rem 0 1.5rem">Page not found.</p>
      <a routerLink="/" style="color:#3b82f6; font-weight:600">← Go to Dashboard</a>
    </div>
  `
})
export class NotFoundComponent {}
