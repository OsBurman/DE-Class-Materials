import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// TODO: import catchError and throwError from 'rxjs/operators' / 'rxjs'

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

  /** Fetch a single post by id */
  getPost(id: number): Observable<Post> {
    // TODO: return this.http.get<Post>(`${BASE_URL}/${id}`)
    throw new Error('Not implemented');
  }

  /** Update a post — pipe catchError to re-throw */
  updatePost(id: number, body: Partial<Post>): Observable<Post> {
    // TODO: return this.http.put<Post>(...)
    //   .pipe(catchError(err => throwError(() => err)))
    throw new Error('Not implemented');
  }

  /** Delete a post — pipe catchError to re-throw */
  deletePost(id: number): Observable<void> {
    // TODO: return this.http.delete<void>(...)
    //   .pipe(catchError(err => throwError(() => err)))
    throw new Error('Not implemented');
  }
}
