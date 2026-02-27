import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PostsService, Post } from '../../services/posts.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [RouterLink, DatePipe],
  template: `
    @if (post === undefined) {
      <p>Loading...</p>
    } @else if (post === null) {
      <div class="not-found">
        <h1>😕 Post Not Found</h1>
        <a routerLink="/posts">← Back to Posts</a>
      </div>
    } @else {
      <article>
        <a routerLink="/posts" class="back-link">← Back to Posts</a>
        <div class="tags">
          @for (tag of post.tags; track tag) {
            <span class="tag">#{{ tag }}</span>
          }
        </div>
        <h1>{{ post.title }}</h1>
        <div class="meta">
          <span>✍️ {{ post.author }}</span>
          <span>📅 {{ post.date | date:'longDate' }}</span>
        </div>
        <div class="content">
          <p>{{ post.content }}</p>
        </div>
      </article>
    }
  `,
  styles: [`
    .back-link { color: #667eea; text-decoration: none; display: inline-block; margin-bottom: 1rem; }
    .tags { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
    .tag { background: #ebf4ff; color: #3182ce; font-size: 0.75rem; padding: 0.2rem 0.6rem; border-radius: 999px; }
    h1 { font-size: 2rem; margin-bottom: 0.75rem; }
    .meta { display: flex; gap: 2rem; color: #718096; margin-bottom: 2rem; font-size: 0.9rem; }
    .content { background: white; border-radius: 12px; padding: 2rem; box-shadow: 0 2px 12px rgba(0,0,0,0.08); line-height: 1.8; font-size: 1.05rem; color: #4a5568; }
    .not-found { text-align: center; padding: 4rem; }
    .not-found h1 { margin-bottom: 1rem; }
    .not-found a { color: #667eea; }
  `]
})
export class PostDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private postsService = inject(PostsService);

  post: Post | undefined | null = undefined;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      const found = this.postsService.getPostById(+id);
      this.post = found ?? null;
    } else {
      this.post = null;
    }
  }
}
