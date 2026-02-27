# Exercise 08 — HTTP Client

## 🎯 Learning Objectives
- Configure **`provideHttpClient`** in `main.ts`
- Inject and use **`HttpClient`** to make GET and POST requests
- Handle **loading states** and **errors** gracefully
- Use `HttpClient` with **Observables** and `async` pipe
- Write an **HTTP interceptor** to add a header to every request
- Understand the difference between `subscribe()` and `async` pipe

---

## 📋 What You're Building
A **GitHub Users Explorer** — a real app using the live GitHub public API:
- Search for GitHub users by username
- Display a list of matching users with avatars
- Click a user to load their detailed profile and repositories
- Show a loading spinner and error messages

**API used**: `https://api.github.com`
- `GET /search/users?q={query}` — search users
- `GET /users/{username}` — get user detail
- `GET /users/{username}/repos?sort=stars&per_page=6` — get top repos

---

## 🏗️ Project Setup
```bash
ng new exercise-08-http --standalone --routing=true --style=css
cd exercise-08-http
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## 📁 File Structure
```
src/app/
├── app.component.ts / .html
├── app.routes.ts
├── services/
│   └── github.service.ts
├── interceptors/
│   └── logging.interceptor.ts
└── pages/
    ├── search/search.component.ts
    └── user-detail/user-detail.component.ts
```

---

## ✅ TODOs

### `main.ts`
- [ ] **TODO 1**: Add `provideHttpClient()` to the providers array in `bootstrapApplication`

### `github.service.ts`
- [ ] **TODO 2**: Inject `HttpClient`
- [ ] **TODO 3**: Implement `searchUsers(query: string): Observable<GitHubSearchResult>`
  - GET `https://api.github.com/search/users?q={query}&per_page=12`
- [ ] **TODO 4**: Implement `getUser(username: string): Observable<GitHubUser>`
- [ ] **TODO 5**: Implement `getUserRepos(username: string): Observable<GitHubRepo[]>`

### `search.component.ts`
- [ ] **TODO 6**: Inject `GithubService` and `Router`
- [ ] **TODO 7**: Declare `users$: Observable`, `loading`, `error`, `query` properties
- [ ] **TODO 8**: Implement `search()` — call `githubService.searchUsers(query)`, subscribe, handle loading/error states
- [ ] **TODO 9**: Navigate to `/users/:login` when a user card is clicked

### `user-detail.component.ts`
- [ ] **TODO 10**: Read the `:username` route param
- [ ] **TODO 11**: Load `user$` and `repos$` using the service
- [ ] **TODO 12**: Use the `async` pipe in the template

### `logging.interceptor.ts`
- [ ] **TODO 13**: Implement a functional HTTP interceptor that logs every request URL to the console

---

## 💡 Key Concepts Reminder

```typescript
// main.ts
bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([loggingInterceptor]))
  ]
});

// Service
private http = inject(HttpClient);
getUsers(q: string) {
  return this.http.get<SearchResult>(`https://api.github.com/search/users?q=${q}`);
}

// Component — subscribe manually
this.githubService.searchUsers(q).subscribe({
  next: (res) => { this.users = res.items; this.loading = false; },
  error: (err) => { this.error = err.message; this.loading = false; }
});

// Functional interceptor
export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  console.log('Request:', req.url);
  return next(req);
};
```
