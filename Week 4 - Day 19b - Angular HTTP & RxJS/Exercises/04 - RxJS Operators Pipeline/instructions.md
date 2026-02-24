# Exercise 04 — RxJS Operators Pipeline

## Learning Objectives
- Chain `map`, `filter`, `tap`, `switchMap`, and `mergeMap` on an `HttpClient` Observable
- Understand the difference between `switchMap` (cancels previous) and `mergeMap` (keeps all)
- Use `tap` for side-effects without transforming the stream
- Build a readable, composable pipeline with `pipe()`

## Scenario
You are building a **data pipeline** that fetches a list of users, filters out inactive ones, transforms their shape, and then for each user fetches their first album — all using RxJS operators.

## Instructions

### Step 1 — Implement `DataService`
Open `data.service.ts` and complete the `TODO` items in `getProcessedUsers()`:

1. `map` — transform each `RawUser` into `{ id, displayName: user.name.toUpperCase() }`.
2. `filter` — keep only users whose `id` is odd.
3. `tap` — `console.log('After filter:', users)` (side-effect, no return value change).
4. Return the piped `Observable`.

Then complete `getUserWithAlbum(userId)`:

5. Use `switchMap` to take the `userId` and fetch `/albums?userId=<id>&_limit=1` from the API.
6. Return a new object `{ userId, albumTitle: albums[0]?.title ?? 'No album' }`.

### Step 2 — Wire into `AppComponent`
Open `app.component.ts` and complete the `TODO` items:

1. In `ngOnInit`, call `this.dataService.getProcessedUsers()`, subscribe, set `this.users`.
2. After loading users, call `getUserWithAlbum(this.users[0].id)` with `switchMap` chaining (or a nested subscribe — the operator version is in the solution).

## Expected Behaviour
- Only odd-ID users (1, 3, 5, 7, 9) appear in the list.
- Their names are uppercased.
- The console shows the array after filtering.
- The first album title for user 1 is displayed below the list.

## Key Concepts
| Operator | Purpose |
|---|---|
| `map(fn)` | Transform each emitted value |
| `filter(pred)` | Only pass values matching predicate |
| `tap(fn)` | Side-effects, passes value through unchanged |
| `switchMap(fn)` | Flatten inner Observable, cancel previous |
| `mergeMap(fn)` | Flatten inner Observable, keep all concurrent |
