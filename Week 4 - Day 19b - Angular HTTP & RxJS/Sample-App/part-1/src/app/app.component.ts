// Day 19b Part 1 — HttpClient: GET/POST/PUT/DELETE, HTTP Interceptors
// Uses JSONPlaceholder API for real HTTP calls
// Run: npm install && npm start

import { Component, Injectable, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpInterceptorFn, HttpRequest, HttpHandlerFn,
         HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError, tap, finalize } from 'rxjs';

const BASE = 'https://jsonplaceholder.typicode.com';

// ─────────────────────────────────────────────────────────────────────────────
// HTTP Interceptor (functional style, Angular 15+)
// ─────────────────────────────────────────────────────────────────────────────
let interceptorLogs: string[] = [];

export const loggingInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const start = Date.now();
  // Clone and add custom header
  const modifiedReq = req.clone({
    headers: req.headers.set('X-Academy-Client', 'Angular-17')
  });
  interceptorLogs.push(`→ ${req.method} ${req.url}`);
  return next(modifiedReq).pipe(
    tap(event => {
      if (event instanceof HttpResponse) {
        interceptorLogs.push(`← ${event.status} (${Date.now() - start}ms)`);
      }
    }),
    catchError((err: HttpErrorResponse) => {
      interceptorLogs.push(`✗ Error ${err.status}: ${err.message}`);
      return throwError(() => err);
    })
  );
};

// ─────────────────────────────────────────────────────────────────────────────
// Service: wraps HttpClient
// ─────────────────────────────────────────────────────────────────────────────
interface Post  { id?: number; userId: number; title: string; body: string; }
interface User  { id: number; name: string; email: string; company: { name: string }; }

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  getPosts(limit = 5): Observable<Post[]> {
    return this.http.get<Post[]>(`${BASE}/posts?_limit=${limit}`);
  }

  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(`${BASE}/posts/${id}`);
  }

  createPost(post: Omit<Post, 'id'>): Observable<Post> {
    return this.http.post<Post>(`${BASE}/posts`, post);
  }

  updatePost(id: number, changes: Partial<Post>): Observable<Post> {
    return this.http.put<Post>(`${BASE}/posts/${id}`, changes);
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE}/posts/${id}`);
  }

  getUsers(limit = 4): Observable<User[]> {
    return this.http.get<User[]>(`${BASE}/users?_limit=${limit}`);
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Root Component
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  styles: [`
    * { box-sizing: border-box; }
    .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; font-family: -apple-system, sans-serif; }
    .header { background: #dd0031; color: white; padding: 1.5rem 2rem; border-radius: 8px; margin-bottom: 2rem; }
    .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
    h2 { color: #dd0031; margin-bottom: 1rem; padding-bottom: .4rem; border-bottom: 2px solid #ff6d00; }
    .code { background: #1e1e1e; color: #d4e157; padding: 1rem; border-radius: 6px; font-size: .82rem; white-space: pre; overflow: auto; margin: .5rem 0; }
    .btn { background: #dd0031; color: white; border: none; padding: .4rem .9rem; border-radius: 4px; cursor: pointer; margin: .2rem; }
    input, textarea { padding: .35rem .6rem; border: 1px solid #ccc; border-radius: 4px; width: 100%; margin-bottom: .4rem; }
  `],
  template: `
<div class="page">
  <div class="header">
    <h1>🅰️ Day 19b Part 1 — HttpClient &amp; Interceptors</h1>
    <p style="opacity:.85">JSONPlaceholder API · GET · POST · PUT · DELETE</p>
  </div>

  <!-- GET -->
  <div class="card">
    <h2>1. GET — Fetch Data</h2>
    <div class="code">this.http.get&lt;Post[]&gt;('{{ BASE }}/posts?_limit=5')
  .subscribe(posts => this.posts = posts);</div>
    <button class="btn" (click)="loadPosts()" [disabled]="loadingPosts">
      {{ loadingPosts ? '⏳ Loading…' : '📥 GET /posts' }}
    </button>
    <button class="btn" (click)="loadUsers()" [disabled]="loadingUsers" style="background:#5c6bc0">
      {{ loadingUsers ? '⏳ Loading…' : '📥 GET /users' }}
    </button>
    <div *ngIf="posts.length" style="margin-top:.8rem">
      <div *ngFor="let p of posts" style="padding:.4rem;background:#f9f9f9;border-radius:4px;margin:.2rem">
        <strong>#{{ p.id }}</strong> {{ p.title | slice:0:60 }}…
      </div>
    </div>
    <div *ngIf="users.length" style="margin-top:.8rem;display:grid;grid-template-columns:1fr 1fr;gap:.5rem">
      <div *ngFor="let u of users" style="padding:.5rem;background:#f0f4ff;border-radius:4px">
        <strong>{{ u.name }}</strong><br/>
        <small style="color:#888">{{ u.email }}</small>
      </div>
    </div>
    <div *ngIf="error" style="margin-top:.5rem;color:#e74c3c;font-size:.85rem">❌ {{ error }}</div>
  </div>

  <!-- POST/PUT/DELETE -->
  <div class="card">
    <h2>2. POST / PUT / DELETE</h2>
    <div style="display:flex;gap:1rem;flex-wrap:wrap">
      <div style="flex:1;min-width:200px">
        <strong>POST — Create</strong>
        <input #postTitle placeholder="Post title…" style="margin-top:.4rem" />
        <button class="btn" (click)="createPost(postTitle.value)">POST</button>
        <div *ngIf="createResult" style="background:#e8f5e9;padding:.5rem;border-radius:4px;font-size:.82rem;margin-top:.4rem">
          ✅ Created id={{ createResult.id }}
        </div>
      </div>
      <div style="flex:1;min-width:200px">
        <strong>PUT — Update post #1</strong>
        <input #putTitle placeholder="New title…" style="margin-top:.4rem" />
        <button class="btn" style="background:#388e3c" (click)="updatePost(putTitle.value)">PUT</button>
        <div *ngIf="updateResult" style="background:#e8f5e9;padding:.5rem;border-radius:4px;font-size:.82rem;margin-top:.4rem">
          ✅ Updated: "{{ updateResult.title | slice:0:30 }}"
        </div>
      </div>
      <div style="flex:1;min-width:200px">
        <strong>DELETE — Remove post</strong>
        <div style="margin-top:.4rem">
          <button class="btn" style="background:#e74c3c" (click)="deletePost()">DELETE post #1</button>
        </div>
        <div *ngIf="deleteResult" style="background:#e8f5e9;padding:.5rem;border-radius:4px;font-size:.82rem;margin-top:.4rem">
          ✅ Deleted (server simulated)
        </div>
      </div>
    </div>
  </div>

  <!-- Interceptor -->
  <div class="card">
    <h2>3. HTTP Interceptor Log</h2>
    <p style="color:#555;font-size:.85rem;margin-bottom:.8rem">
      The <code>loggingInterceptor</code> intercepts every request, adds a header, and logs timing.
    </p>
    <div style="background:#1e1e1e;color:#98c379;padding:.8rem;border-radius:6px;font-family:monospace;font-size:.82rem;max-height:180px;overflow-y:auto">
      <div *ngFor="let log of interceptorLogs">{{ log }}</div>
      <div *ngIf="!interceptorLogs.length" style="color:#888">Make a request above to see interceptor logs</div>
    </div>
    <button class="btn" style="background:#888;margin-top:.5rem" (click)="clearLogs()">Clear</button>
  </div>
</div>
  `
})
export class AppComponent implements OnInit {
  posts: any[]  = [];
  users: any[]  = [];
  loadingPosts  = false;
  loadingUsers  = false;
  error: string | null = null;
  createResult: any = null;
  updateResult: any = null;
  deleteResult  = false;
  interceptorLogs = interceptorLogs;
  private api = inject(ApiService);
  readonly BASE = BASE;

  ngOnInit() { this.loadPosts(); }

  loadPosts() {
    this.loadingPosts = true;
    this.error = null;
    this.api.getPosts(5).pipe(finalize(() => this.loadingPosts = false)).subscribe({
      next: posts => this.posts = posts,
      error: err  => this.error = err.message,
    });
  }

  loadUsers() {
    this.loadingUsers = true;
    this.api.getUsers(4).pipe(finalize(() => this.loadingUsers = false)).subscribe({
      next: users => this.users = users,
    });
  }

  createPost(title: string) {
    if (!title) return;
    this.api.createPost({ userId: 1, title, body: 'Created from Angular app' }).subscribe({
      next: post => this.createResult = post,
    });
  }

  updatePost(title: string) {
    if (!title) return;
    this.api.updatePost(1, { title }).subscribe({
      next: post => this.updateResult = post,
    });
  }

  deletePost() {
    this.api.deletePost(1).subscribe({ next: () => this.deleteResult = true });
  }

  clearLogs() { interceptorLogs.length = 0; }
}
