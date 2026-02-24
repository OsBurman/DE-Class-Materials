# Exercise 06: Testing Services and HTTP Calls

## Objective
Test an Angular HTTP service using `HttpClientTestingModule` and `HttpTestingController` to intercept, inspect, and flush HTTP requests without a real backend.

## Background
Angular provides `HttpClientTestingModule` as a drop-in replacement for `HttpClientModule` in tests. Paired with `HttpTestingController`, it lets you assert that specific HTTP requests were made (correct URL, method, body) and respond to them with controlled data — all synchronously, with no real network traffic.

## Requirements
You are given a `DataService` that fetches posts from a REST API and can also create posts. Your job is to write the tests.

### Service under test (`data.service.ts`)
The service exposes:
- `getPosts(): Observable<Post[]>` — sends `GET /api/posts`
- `getPost(id: number): Observable<Post>` — sends `GET /api/posts/:id`
- `createPost(post: Partial<Post>): Observable<Post>` — sends `POST /api/posts` with the post body

### Interface
```ts
interface Post { id: number; title: string; body: string; userId: number; }
```

### Tests to write (`data.service.spec.ts`)
Write the following test cases:

1. **`should be created`** — inject the service and assert it is truthy.
2. **`getPosts() should send GET /api/posts`** — call `getPosts()`, use `httpMock.expectOne('/api/posts')`, assert the request method is `'GET'`, flush with an array of two mock posts, and verify the Observable emitted those posts.
3. **`getPost() should send GET /api/posts/:id`** — call `getPost(1)`, expect the URL `/api/posts/1`, verify method is `'GET'`, flush a single mock post.
4. **`createPost() should send POST /api/posts with the correct body`** — call `createPost({ title: 'Test', body: 'Body', userId: 1 })`, expect `/api/posts`, verify method is `'POST'`, verify `request.body` contains `title: 'Test'`, flush a mock response.
5. **`should handle 404 error on getPost()`** — call `getPost(999)`, expect the request, flush it with a `{ status: 404, statusText: 'Not Found' }` error object, and assert the Observable errors.
6. **`verify() passes with no outstanding requests`** — after each test's expectations and flushes, call `httpMock.verify()` — place this in `afterEach`.

## Hints
- Import `HttpClientTestingModule` and `HttpTestingController` from `@angular/common/http/testing`.
- Inject the controller with `TestBed.inject(HttpTestingController)` in `beforeEach`.
- `httpMock.expectOne(url)` returns a `TestRequest` — check `.request.method` and `.request.body` on it.
- To flush an error: `req.flush('Not found', { status: 404, statusText: 'Not Found' })`.
- `httpMock.verify()` in `afterEach` ensures no unexpected HTTP calls were made.

## Expected Output
All 6 specs pass:
```
DataService
  ✓ should be created
  ✓ getPosts() should send GET /api/posts
  ✓ getPost() should send GET /api/posts/:id
  ✓ createPost() should send POST /api/posts with the correct body
  ✓ should handle 404 error on getPost()
  ✓ verify() passes with no outstanding requests

6 specs, 0 failures
```
