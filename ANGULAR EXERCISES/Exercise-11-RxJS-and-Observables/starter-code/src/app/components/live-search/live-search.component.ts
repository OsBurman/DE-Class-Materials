import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { Observable, of } from 'rxjs';
// TODO 7: import debounceTime, distinctUntilChanged, switchMap, catchError, takeUntilDestroyed

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
      <input [formControl]="searchControl" placeholder="Type to search posts..." class="search-input" />

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
    .search-input { width: 100%; padding: .5rem; font-size: 1rem; }
    .results { list-style: none; padding: 0; }
    .results li { padding: .5rem 0; border-bottom: 1px solid #eee; }
    .loading { color: #888; }
    .error { color: crimson; }
    .no-results { color: #aaa; }
  `]
})
export class LiveSearchComponent implements OnInit {
  searchControl = new FormControl('');
  results: SearchResult[] = [];
  isLoading = false;
  error = '';

  // TODO 8: inject DestroyRef using inject(DestroyRef) so you can use takeUntilDestroyed()

  ngOnInit() {
    // TODO 9: Build the search pipeline on this.searchControl.valueChanges:
    //   .pipe(
    //     debounceTime(300),
    //     distinctUntilChanged(),
    //     switchMap(query => {
    //       if (!query || query.length < 2) return of([]);
    //       this.isLoading = true;
    //       this.error = '';
    //       // fetch from https://jsonplaceholder.typicode.com/posts?q=<query>
    //       // return this.http.get<SearchResult[]>(`...`).pipe(catchError(() => { this.error = '...'; return of([]); }));
    //     }),
    //     takeUntilDestroyed(this.destroyRef)
    //   )
    //   .subscribe(results => { this.isLoading = false; this.results = results; });
  }

  // TODO 10: inject HttpClient so the switchMap can call the JSONPlaceholder API
}
