import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// TODO: import map, filter, tap, switchMap from 'rxjs/operators'

const API = 'https://jsonplaceholder.typicode.com';

export interface RawUser  { id: number; name: string; }
export interface User     { id: number; displayName: string; }
export interface Album    { id: number; title: string; userId: number; }

@Injectable({ providedIn: 'root' })
export class DataService {
  constructor(private http: HttpClient) {}

  /** Fetch /users, transform and filter via RxJS pipeline */
  getProcessedUsers(): Observable<User[]> {
    return this.http.get<RawUser[]>(`${API}/users`).pipe(
      // TODO: map each user to { id: user.id, displayName: user.name.toUpperCase() }
      // TODO: filter to keep only odd-id users (id % 2 !== 0)
      // TODO: tap to console.log('After filter:', users)
    );
  }

  /** Given a userId, fetch their first album using switchMap */
  getUserWithAlbum(userId: number): Observable<{ userId: number; albumTitle: string }> {
    // TODO: use switchMap on an of(userId) observable, or chain from a single-value source
    // Hint: import { of } from 'rxjs' and pipe switchMap(() => this.http.get<Album[]>(...))
    throw new Error('Not implemented');
  }
}
