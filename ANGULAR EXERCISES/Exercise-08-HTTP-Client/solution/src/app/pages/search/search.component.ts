import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { GithubService, GitHubUser } from '../../services/github.service';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div>
      <h1>🔍 GitHub User Search</h1>
      <div class="search-row">
        <input [(ngModel)]="query" placeholder="Search GitHub username..." (keyup.enter)="search()" />
        <button (click)="search()" [disabled]="loading">{{ loading ? 'Searching...' : 'Search' }}</button>
      </div>

      @if (error) {
        <div class="error-banner">⚠️ {{ error }}</div>
      }

      @if (loading) {
        <div class="spinner">Loading...</div>
      }

      @if (users.length > 0) {
        <p class="result-count">Found {{ totalCount }} users — showing {{ users.length }}</p>
        <div class="user-grid">
          @for (user of users; track user.id) {
            <div class="user-card" (click)="goToUser(user.login)">
              <img [src]="user.avatar_url" [alt]="user.login" />
              <span>{{ user.login }}</span>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    h1 { font-size: 2rem; margin-bottom: 1.5rem; }
    .search-row { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
    .search-row input { flex: 1; padding: 0.65rem 1rem; border: 1px solid #cbd5e0; border-radius: 8px; font-size: 1rem; }
    .search-row button { background: #667eea; color: white; border: none; border-radius: 8px; padding: 0.65rem 1.5rem; cursor: pointer; font-weight: 600; }
    .error-banner { background: #fff5f5; border: 1px solid #fc8181; color: #c53030; border-radius: 8px; padding: 0.75rem 1rem; margin-bottom: 1rem; }
    .spinner { text-align: center; padding: 2rem; color: #667eea; font-weight: 600; }
    .result-count { color: #718096; margin-bottom: 1rem; font-size: 0.9rem; }
    .user-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(130px, 1fr)); gap: 1rem; }
    .user-card { background: white; border-radius: 12px; padding: 1rem; text-align: center; cursor: pointer; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: transform 0.2s, box-shadow 0.2s; }
    .user-card:hover { transform: translateY(-4px); box-shadow: 0 6px 20px rgba(0,0,0,0.15); }
    .user-card img { width: 72px; height: 72px; border-radius: 50%; margin-bottom: 0.5rem; }
    .user-card span { display: block; font-size: 0.85rem; font-weight: 600; color: #2d3748; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  `]
})
export class SearchComponent {
  private githubService = inject(GithubService);
  private router = inject(Router);

  query = '';
  users: GitHubUser[] = [];
  loading = false;
  error = '';
  totalCount = 0;

  search(): void {
    if (!this.query.trim()) return;
    this.loading = true;
    this.error = '';
    this.users = [];
    this.githubService.searchUsers(this.query).subscribe({
      next: (result) => {
        this.users = result.items;
        this.totalCount = result.total_count;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to fetch users. Check your internet connection or try again.';
        this.loading = false;
      }
    });
  }

  goToUser(login: string): void {
    this.router.navigate(['/users', login]);
  }
}
