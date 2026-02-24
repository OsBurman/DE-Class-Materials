# Exercise 02 — PUT, DELETE and Error Handling with catchError

## Learning Objectives
- Perform typed `PUT` and `DELETE` HTTP requests with `HttpClient`
- Pipe `catchError` and `throwError` to surface API errors uniformly
- Display loading and error states in a component
- Understand when to use `catchError` vs component-level error handling

## Scenario
You are extending the Post Browser from Exercise 01 with an **editor** that can update and delete a post. Every HTTP call must handle server errors gracefully.

## Instructions

### Step 1 — Scaffold the module
`app.module.ts` already imports `HttpClientModule` and declares `PostEditorComponent`. No changes needed.

### Step 2 — Implement `PostService`
Open `post.service.ts` and complete the three `TODO` items:

1. **`getPost(id)`** — `GET /posts/:id`, return `Observable<Post>`.
2. **`updatePost(id, body)`** — `PUT /posts/:id`, return `Observable<Post>`. Pipe the result through `catchError` — re-throw the error with `throwError(() => err)`.
3. **`deletePost(id)`** — `DELETE /posts/:id`, return `Observable<void>`. Apply the same `catchError` pattern.

### Step 3 — Complete `PostEditorComponent`
Open `post-editor.component.ts` and complete the `TODO` items:

1. Load post `#1` in `ngOnInit`; set `this.loading = false` when done.
2. `save()` — call `updatePost`, set `this.message = 'Saved!'` on success, `this.error = err.message` on error.
3. `remove()` — call `deletePost`, set `this.message = 'Deleted.'` on success.

### Step 4 — Error simulation
To test the error path, change the id to `9999` — the API returns `404`. Observe the error message in the template.

## Expected Behaviour
- The form pre-populates with the fetched post title.
- Save updates the title and shows **Saved!**.
- Delete shows **Deleted.**.
- A network / server error shows a red error message.

## Key Concepts
| Concept | API |
|---|---|
| Update resource | `this.http.put<Post>(url, body)` |
| Delete resource | `this.http.delete<void>(url)` |
| Error handling | `.pipe(catchError(err => throwError(() => err)))` |
| Re-throw | `throwError(() => new Error(err.message))` |
