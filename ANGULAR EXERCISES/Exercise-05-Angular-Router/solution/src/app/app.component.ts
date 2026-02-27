import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <nav class="navbar">
      <a class="brand" routerLink="/">📰 Angular Blog</a>
      <div class="nav-links">
        <a routerLink="/" routerLinkActive="active-link" [routerLinkActiveOptions]="{exact: true}">Home</a>
        <a routerLink="/posts" routerLinkActive="active-link">Posts</a>
        <a routerLink="/about" routerLinkActive="active-link">About</a>
      </div>
    </nav>
    <main class="content">
      <router-outlet />
    </main>
  `,
  styles: [`
    * { box-sizing: border-box; margin: 0; padding: 0; }
    :host { font-family: 'Segoe UI', sans-serif; }
    .navbar { display: flex; align-items: center; justify-content: space-between; padding: 1rem 2rem; background: #1a202c; color: white; }
    .brand { color: white; text-decoration: none; font-size: 1.2rem; font-weight: 700; }
    .nav-links { display: flex; gap: 1.5rem; }
    .nav-links a { color: #a0aec0; text-decoration: none; font-weight: 500; transition: color 0.2s; }
    .nav-links a:hover { color: white; }
    .nav-links a.active-link { color: #667eea; }
    .content { max-width: 900px; margin: 2rem auto; padding: 0 1rem; }
  `]
})
export class AppComponent {}
