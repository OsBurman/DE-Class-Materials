import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { Observable } from 'rxjs';
import { GithubService, GitHubUser, GitHubRepo } from '../../services/github.service';

@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [RouterLink, AsyncPipe],
  template: `
    <a routerLink="/" class="back">← Back to Search</a>

    @if (user$ | async; as user) {
      <div class="profile">
        <img [src]="user.avatar_url" [alt]="user.login" class="avatar" />
        <div>
          <h1>{{ user.name || user.login }}</h1>
          <a [href]="user.html_url" target="_blank">@{{ user.login }}</a>
          @if (user.bio) { <p class="bio">{{ user.bio }}</p> }
          <div class="stats">
            <span>📦 {{ user.public_repos }} repos</span>
            <span>👥 {{ user.followers }} followers</span>
            <span>👤 {{ user.following }} following</span>
            @if (user.location) { <span>📍 {{ user.location }}</span> }
          </div>
        </div>
      </div>

      <h2>⭐ Top Repositories</h2>
      @if (repos$ | async; as repos) {
        <div class="repo-grid">
          @for (repo of repos; track repo.id) {
            <a [href]="repo.html_url" target="_blank" class="repo-card">
              <h3>{{ repo.name }}</h3>
              <p>{{ repo.description || 'No description' }}</p>
              <div class="repo-meta">
                @if (repo.language) { <span class="lang">{{ repo.language }}</span> }
                <span>⭐ {{ repo.stargazers_count }}</span>
                <span>🍴 {{ repo.forks_count }}</span>
              </div>
            </a>
          }
        </div>
      }
    } @else {
      <p class="loading">Loading user profile...</p>
    }
  `,
  styles: [`
    .back { color: #667eea; text-decoration: none; display: inline-block; margin-bottom: 1.5rem; }
    .profile { display: flex; gap: 1.5rem; align-items: flex-start; background: white; border-radius: 12px; padding: 1.5rem; box-shadow: 0 2px 12px rgba(0,0,0,0.08); margin-bottom: 2rem; }
    .avatar { width: 100px; height: 100px; border-radius: 50%; }
    h1 { font-size: 1.5rem; margin-bottom: 0.25rem; }
    a { color: #667eea; }
    .bio { color: #718096; margin: 0.5rem 0; }
    .stats { display: flex; flex-wrap: wrap; gap: 1rem; margin-top: 0.75rem; font-size: 0.9rem; color: #4a5568; }
    h2 { margin-bottom: 1rem; }
    .repo-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 1rem; }
    .repo-card { background: white; border-radius: 10px; padding: 1rem; box-shadow: 0 2px 8px rgba(0,0,0,0.07); text-decoration: none; color: inherit; display: block; transition: transform 0.2s; }
    .repo-card:hover { transform: translateY(-3px); }
    .repo-card h3 { color: #667eea; margin-bottom: 0.4rem; font-size: 0.95rem; }
    .repo-card p { font-size: 0.82rem; color: #718096; margin-bottom: 0.75rem; }
    .repo-meta { display: flex; gap: 0.75rem; font-size: 0.8rem; color: #a0aec0; }
    .lang { background: #ebf4ff; color: #3182ce; border-radius: 999px; padding: 0.1rem 0.5rem; }
    .loading { text-align: center; padding: 3rem; color: #a0aec0; }
  `]
})
export class UserDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private githubService = inject(GithubService);

  user$!: Observable<GitHubUser>;
  repos$!: Observable<GitHubRepo[]>;

  ngOnInit(): void {
    const username = this.route.snapshot.paramMap.get('username');
    if (username) {
      this.user$ = this.githubService.getUser(username);
      this.repos$ = this.githubService.getUserRepos(username);
    }
  }
}
