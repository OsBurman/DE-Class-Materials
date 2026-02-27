import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({ selector: 'app-home', standalone: true, imports: [RouterLink],
  template: `
    <div class="hero">
      <h1>Welcome to Angular Blog 📰</h1>
      <p>Explore tutorials, deep dives, and best practices for Angular developers.</p>
      <a routerLink="/posts" class="btn">Browse All Posts →</a>
    </div>
  `,
  styles: [`.hero { text-align: center; padding: 4rem 2rem; background: linear-gradient(135deg, #667eea, #764ba2); color: white; border-radius: 16px; }
    h1 { font-size: 2.5rem; margin-bottom: 1rem; } p { font-size: 1.1rem; opacity: 0.9; margin-bottom: 2rem; }
    .btn { background: white; color: #667eea; padding: 0.75rem 2rem; border-radius: 999px; text-decoration: none; font-weight: 700; }`]
})
export class HomeComponent {}
