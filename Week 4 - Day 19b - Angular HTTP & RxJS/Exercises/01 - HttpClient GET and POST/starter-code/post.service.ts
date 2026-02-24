import { Injectable } from '@angular/core';
// TODO: import HttpClient, HttpParams, HttpHeaders from '@angular/common/http'
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
  // TODO: inject HttpClient via constructor

  /** Fetch the first 10 posts using HttpParams */
  getPosts(): Observable<Post[]> {
    // TODO: create HttpParams with _limit=10
    // TODO: return this.http.get<Post[]>(BASE_URL, { params })
    throw new Error('Not implemented');
  }

  /** Create a new post with Content-Type header */
  createPost(body: Partial<Post>): Observable<Post> {
    // TODO: create HttpHeaders with 'Content-Type': 'application/json'
    // TODO: return this.http.post<Post>(BASE_URL, body, { headers })
    throw new Error('Not implemented');
  }
}
