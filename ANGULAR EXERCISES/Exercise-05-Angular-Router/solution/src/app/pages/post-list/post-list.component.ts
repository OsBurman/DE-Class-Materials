import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PostsService, Post } from '../../services/posts.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [RouterLink, FormsModule, DatePipe],
  template: `
    <div>
      <h1>📚 All Posts</h1>
      <div class="search-row">
        <input [(ngModel)]="searchQuery" placeholder="Search posts..." (keyup.enter)="onSearch()" />
        <button (click)="onSearch()">Search</button>
      </div>
      @if (filteredPosts.length === 0) {
        <p class="empty">No posts found for "{{ searchQuery }}"</p>
      }
      <div class="post-grid">
        @for (post of filteredPosts; track post.id) {
          <div class="post-card">
            <div class="tags">
              @for (tag of post.tags; track tag) {
                <span class="tag">#{{ tag }}</span>
              }
            </div>
            <h2>{{ post.title }}</h2>
            <p>{{ post.excerpt }}</p>
            <div class="meta">
              <span>{{ post.author }}</span>
              <span>{{ post.date | date:'mediumDate' }}</span>
            </div>
            <a [routerLink]="['/posts', post.id]" class="read-more">Read More →</a>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    h1 { font-size: 2rem; margin-bottom: 1.5rem; }
    .search-row { display: flex; gap: 0.5rem; margin-bottom: 2rem; }
    .search-row input { flex: 1; padding: 0.6rem 1rem; border: 1px solid #cbd5e0; border-radius: 8px; font-size: 1rem; }
    .search-row button { background: #667eea; color: white; border: none; border-radius: 8px; padding: 0.6rem 1.2rem; cursor: pointer; }
    .empty { color: #a0aec0; text-align: center; padding: 3rem; }
    .post-grid { display: grid; gap: 1.5rem; }
    .post-card { background: white; border-radius: 12px; padding: 1.5rem; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
    .tags { display: flex; gap: 0.5rem; margin-bottom: 0.75rem; }
    .tag { background: #ebf4ff; color: #3182ce; font-size: 0.75rem; padding: 0.2rem 0.6rem; border-radius: 999px; }
    h2 { font-size: 1.3rem; margin-bottom: 0.5rem; }
    p { color: #718096; line-height: 1.6; margin-bottom: 1rem; }
    .meta { display: flex; justify-content: space-between; color: #a0aec0; font-size: 0.85rem; margin-bottom: 1rem; }
    .read-more { color: #667eea; text-decoration: none; font-weight: 600; }
    .read-more:hover { text-decoration: underline; }
  `]
})
export class PostListComponent implements OnInit {
  private postsService = inject(PostsService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  searchQuery = '';

  get filteredPosts(): Post[] {
    if (!this.searchQuery.trim()) return this.postsService.getPosts();
    return this.postsService.getPosts().filter(p =>
      p.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      p.tags.some(t => t.includes(this.searchQuery.toLowerCase()))
    );
  }

  ngOnInit(): void {
    const search = this.route.snapshot.queryParamMap.get('search');
    if (search) this.searchQuery = search;
  }

  onSearch(): void {
    this.router.navigate(['/posts'], {
      queryParams: this.searchQuery ? { search: this.searchQuery } : {}
    });
  }
}
