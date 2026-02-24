import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, filter, tap, switchMap } from 'rxjs/operators';

const API = 'https://jsonplaceholder.typicode.com';

export interface RawUser  { id: number; name: string; }
export interface User     { id: number; displayName: string; }
export interface Album    { id: number; title: string; userId: number; }

@Injectable({ providedIn: 'root' })
export class DataService {
  constructor(private http: HttpClient) {}

  getProcessedUsers(): Observable<User[]> {
    return this.http.get<RawUser[]>(`${API}/users`).pipe(
      // 1. Transform shape — uppercase display name
      map(users => users.map(u => ({ id: u.id, displayName: u.name.toUpperCase() }))),
      // 2. Filter — keep only odd-id users
      map(users => users.filter(u => u.id % 2 !== 0)),
      // 3. Side-effect — log without changing the value
      tap(users => console.log('After filter:', users)),
    );
  }

  getUserWithAlbum(userId: number): Observable<{ userId: number; albumTitle: string }> {
    return of(userId).pipe(
      switchMap(id =>
        this.http
          .get<Album[]>(`${API}/albums?userId=${id}&_limit=1`)
          .pipe(map(albums => ({ userId: id, albumTitle: albums[0]?.title ?? 'No album' }))),
      ),
    );
  }
}
