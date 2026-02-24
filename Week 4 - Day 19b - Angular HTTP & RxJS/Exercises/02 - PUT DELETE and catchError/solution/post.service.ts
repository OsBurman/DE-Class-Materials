import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

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

  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(`${BASE_URL}/${id}`);
  }

  updatePost(id: number, body: Partial<Post>): Observable<Post> {
    return this.http
      .put<Post>(`${BASE_URL}/${id}`, body)
      .pipe(catchError(err => throwError(() => err)));
  }

  deletePost(id: number): Observable<void> {
    return this.http
      .delete<void>(`${BASE_URL}/${id}`)
      .pipe(catchError(err => throwError(() => err)));
  }
}
