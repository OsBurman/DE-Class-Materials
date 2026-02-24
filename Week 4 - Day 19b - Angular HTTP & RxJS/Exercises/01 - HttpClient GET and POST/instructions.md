# Exercise 01 — HttpClient GET and POST Requests

## Learning Objectives
- Import and configure `HttpClientModule` in an Angular module
- Inject `HttpClient` into a service
- Perform typed `GET` and `POST` requests using `Observable`
- Use `HttpParams` to attach query parameters
- Use `HttpHeaders` to set custom request headers
- Subscribe in a component and display results

## Scenario
You are building a simple **Post Browser** that fetches a list of posts from a REST API and can create new posts.

## Instructions

### Step 1 — Wire up `HttpClientModule`
Open `app.module.ts`. Import `HttpClientModule` from `@angular/common/http` and add it to the `imports` array so every component in the module can inject `HttpClient`.

### Step 2 — Build `PostService`
Open `post.service.ts` and complete the three `TODO` items:
1. Inject `HttpClient` via the constructor.
2. Implement `getPosts()` — issue a `GET` to `https://jsonplaceholder.typicode.com/posts`, attach a query-param `_limit=10` using `HttpParams`, and return the typed `Observable<Post[]>`.
3. Implement `createPost(body)` — issue a `POST` to the same base URL with `Content-Type: application/json` set via `HttpHeaders`, and return `Observable<Post>`.

### Step 3 — Build `PostListComponent`
Open `post-list.component.ts` and complete the `TODO` items:
1. Inject `PostService`.
2. In `ngOnInit`, call `this.postService.getPosts()` and subscribe; on success set `this.posts`; on error set `this.error`.
3. Implement `addPost()` — call `createPost`, push the response into `this.posts`.

### Step 4 — Wire in `AppComponent`
Open `app.component.ts` and add `<app-post-list></app-post-list>` to the inline template.

## Expected Behaviour
- On load the component shows a list of 10 post titles.
- Clicking **Add Post** appends a new placeholder post at the top of the list.
- Network errors are displayed in a red error paragraph.

## Key Concepts
| Concept | API |
|---|---|
| Bootstrap HTTP | `HttpClientModule` in `imports` |
| HTTP service | `HttpClient` in constructor |
| Query params | `new HttpParams().set('_limit', '10')` |
| Custom headers | `new HttpHeaders({ 'Content-Type': 'application/json' })` |
| Typed GET | `this.http.get<Post[]>(url, { params })` |
| Typed POST | `this.http.post<Post>(url, body, { headers })` |
