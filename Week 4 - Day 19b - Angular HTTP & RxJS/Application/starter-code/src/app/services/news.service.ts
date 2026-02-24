import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, tap, catchError, switchMap } from 'rxjs/operators';
import { Article, ApiResponse, MOCK_ARTICLES } from '../models/article.model';

@Injectable({ providedIn: 'root' })
export class NewsService {
  // TODO Task 2: Create BehaviorSubject state
  // private articlesSubject = new BehaviorSubject<Article[]>([]);
  // articles$ = this.articlesSubject.asObservable();
  // private loadingSubject = new BehaviorSubject<boolean>(false);
  // loading$ = this.loadingSubject.asObservable();

  // For now, exposing mock data so the app renders without an API key
  articles$ = new BehaviorSubject<Article[]>(MOCK_ARTICLES).asObservable();
  loading$ = new BehaviorSubject<boolean>(false).asObservable();

  private readonly API_BASE = 'https://newsdata.io/api/1/news';

  constructor(private http: HttpClient) {}

  // TODO Task 3: Implement fetchArticles(category: string)
  // 1. Set loading to true
  // 2. Call this.http.get<ApiResponse>(url)
  // 3. Pipe: tap(loading true), map(response => response.results), catchError(err => of([]))
  // 4. Subscribe and push to articlesSubject, set loading false in finalize
  fetchArticles(category: string): void {
    // TODO: Replace mock data with real API call
    // For development without an API key, use MOCK_ARTICLES
    console.log(`Fetching articles for category: ${category}`);
  }

  // TODO Task 4: Implement searchArticles(query$: Observable<string>)
  // Use switchMap — if a new search starts, cancel the previous request
  searchArticles(query$: Observable<string>): Observable<Article[]> {
    return query$.pipe(
      switchMap(query => {
        // TODO: Make HTTP request for search query
        // For now, filter mock data
        return of(MOCK_ARTICLES.filter(a =>
          a.title.toLowerCase().includes(query.toLowerCase())
        ));
      })
    );
  }
}
