# Day 19b Application — Angular HTTP & RxJS: News Feed App

## Overview

You'll build a **News Feed App** — an Angular app that fetches articles from a public API using `HttpClient`, applies RxJS operators, manages state with `BehaviorSubject`, and uses an HTTP interceptor to add headers to every request.

---

## Learning Goals

- Make HTTP requests with `HttpClient`
- Use RxJS operators: `map`, `filter`, `tap`, `catchError`, `switchMap`
- Use `BehaviorSubject` to manage and share reactive state
- Implement an HTTP interceptor
- Use the `async` pipe to subscribe in templates
- Prevent memory leaks by unsubscribing

---

## Prerequisites

- `cd starter-code && npm install && npm run start`
- Uses the free **NewsData.io** API — register for a free key at `https://newsdata.io` (or use the provided mock data mode)

---

## Project Structure

```
starter-code/
└── src/app/
    ├── app.module.ts
    ├── models/article.model.ts       ← provided
    ├── interceptors/
    │   └── api-key.interceptor.ts    ← TODO
    ├── services/
    │   └── news.service.ts           ← TODO
    └── components/
        ├── news-feed/                ← TODO
        ├── article-card/             ← TODO
        └── category-filter/         ← TODO
```

---

## Part 1 — HTTP Interceptor

**Task 1 — `ApiKeyInterceptor`**  
Implement `HttpInterceptor`. In `intercept()`, clone the request and add an `x-app-name: angular-news-app` header. Pass to `next.handle(clonedReq)`.  
Register it in `AppModule` providers.

---

## Part 2 — `NewsService`

**Task 2 — BehaviorSubject state**  
```ts
private articlesSubject = new BehaviorSubject<Article[]>([]);
articles$ = this.articlesSubject.asObservable();
private loadingSubject = new BehaviorSubject<boolean>(false);
loading$ = this.loadingSubject.asObservable();
```

**Task 3 — `fetchArticles(category: string)`**  
Use `this.http.get<ApiResponse>(url)`.  
Chain: `.pipe(tap(() => this.loadingSubject.next(true)), map(response => response.results), catchError(err => { ... return of([]); }))`.  
Subscribe and push results to `articlesSubject`.

**Task 4 — `searchArticles(query: string)`**  
Use `switchMap` — if a new search starts before the old one finishes, cancel the previous request.

---

## Part 3 — Components

**Task 5 — `NewsFeed`**  
Inject `NewsService`. Use `async` pipe in template:
```html
<app-article-card *ngFor="let article of newsService.articles$ | async" [article]="article">
```
Show loading spinner while `loading$ | async` is true.  
`ngOnDestroy`: no manual subscription needed (async pipe handles it). Add a comment explaining why.

**Task 6 — `CategoryFilter`**  
List of category buttons. On click, emit `@Output() categoryChange` with the selected category. Highlight the active category.

**Task 7 — `ArticleCard`**  
`@Input() article: Article`. Display: title, description, source, publishedAt (use `DatePipe`), image, link.

---

## Stretch Goals

1. Add `debounceTime(300)` and `distinctUntilChanged()` to a search input stream.
2. Use `combineLatest` to combine the articles stream with a filter stream.
3. Add a `ReplaySubject(5)` to keep the last 5 search results in a history panel.

---

## Submission Checklist

- [ ] `HttpClient` used for GET request
- [ ] HTTP interceptor adds custom header to all requests
- [ ] `BehaviorSubject` used for articles and loading state
- [ ] `map`, `tap`, `catchError` used in pipe
- [ ] `switchMap` used for search
- [ ] `async` pipe used in template (no manual subscribe)
- [ ] `DatePipe` used for article date
- [ ] Loading and error states handled in UI
