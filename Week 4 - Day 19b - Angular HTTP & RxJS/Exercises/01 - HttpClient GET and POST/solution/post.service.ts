import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Post {
  id: number;
  title: string;
  body: string;
  userId: number;
}

const BASE_URL = 'https://jsonplaceholder.typicode.com/posts';

@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private http: HttpClient) {}

  /** Fetch the first 10 posts. */
  getPosts(): Observable<Post[]> {
    const params = new HttpParams().set('_limit', '10');
    return this.http.get<Post[]>(BASE_URL, { params });
  }

  /** Create a new post. */
  createPost(body: Partial<Post>): Observable<Post> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<Post>(BASE_URL, body, { headers });
  }
}
