import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError, takeUntilDestroyed } from 'rxjs/operators';

export interface SearchResult {
  id: number;
  title: string;
  body: string;
}

@Component({
  selector: 'app-live-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="live-search">
      <h3>Live Search (JSONPlaceholder Posts)</h3>
      <input [formControl]="searchControl" placeholder="Type to search posts…" class="search-input" />

      @if (isLoading) {
        <p class="loading">Searching…</p>
      }

      @if (error) {
        <p class="error">{{ error }}</p>
      }

      <ul class="results">
        @for (result of results; track result.id) {
          <li>
            <strong>{{ result.title }}</strong>
            <p>{{ result.body | slice:0:80 }}…</p>
          </li>
        }
      </ul>

      @if (results.length === 0 && !isLoading && searchControl.value) {
        <p class="no-results">No results found.</p>
      }
    </div>
  `,
  styles: [`
    .live-search { padding: 1rem; }
    .search-input { width: 100%; padding: .5rem; font-size: 1rem; border: 1px solid #ccc; border-radius: 4px; }
    .results { list-style: none; padding: 0; }
    .results li { padding: .5rem 0; border-bottom: 1px solid #eee; }
    .results li strong { display: block; text-transform: capitalize; }
    .results li p { margin: .25rem 0 0; color: #555; font-size: .9rem; }
    .loading { color: #888; }
    .error { color: crimson; }
    .no-results { color: #aaa; }
  `]
})
export class LiveSearchComponent implements OnInit {
  private http = inject(HttpClient);
  private destroyRef = inject(DestroyRef);

  searchControl = new FormControl('');
  results: SearchResult[] = [];
  isLoading = false;
  error = '';

  ngOnInit() {
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (!query || query.length < 2) {
          this.results = [];
          return of([]);
        }
        this.isLoading = true;
        this.error = '';
        return this.http.get<SearchResult[]>(
          `https://jsonplaceholder.typicode.com/posts?q=${query}`
        ).pipe(
          catchError(() => {
            this.error = 'Search failed. Please try again.';
            return of([]);
          })
        );
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(results => {
      this.isLoading = false;
      this.results = results;
    });
  }
}
