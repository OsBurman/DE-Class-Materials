// =============================================================================
// Day 19b — Angular HTTP & RxJS  |  Part 2
// File: 01-rxjs-operators.ts
//
// Topics covered:
//   1. What is an Observable? Cold vs Hot Observables
//   2. Creating Observables (of, from, interval, fromEvent)
//   3. map      — transform each value
//   4. filter   — exclude values by predicate
//   5. tap      — side effects without modification
//   6. switchMap  — flatten, cancel previous (search / navigation)
//   7. mergeMap   — flatten, keep all (parallel requests)
//   8. debounceTime — wait for quiet period (typeahead)
//   9. distinctUntilChanged — deduplicate consecutive equal values
//   10. Combining operators in a realistic search pipeline
//   11. concatMap + exhaustMap (brief overview for completeness)
// =============================================================================

import { Observable, of, from, interval, fromEvent } from 'rxjs';
import {
  map,
  filter,
  tap,
  switchMap,
  mergeMap,
  concatMap,
  exhaustMap,
  debounceTime,
  distinctUntilChanged,
  take,
  delay,
  catchError,
  finalize,
} from 'rxjs/operators';

// ---------------------------------------------------------------------------
// Section 1 — What is an Observable?
// ---------------------------------------------------------------------------
//
// An Observable is a LAZY, CANCELLABLE stream of values over time.
//
// Compare to a Promise:
//
//   Promise                     Observable
//   ─────────────────────────── ─────────────────────────────────────────
//   Eager (starts immediately)  Lazy (starts only when subscribed)
//   One value                   Zero, one, or many values
//   Not cancellable             Cancellable via unsubscribe()
//   No operators built in       Huge operator library (RxJS)
//
// Cold Observable: each subscriber gets its own independent execution.
//   e.g. http.get() — each subscriber triggers a new HTTP request.
//
// Hot Observable: all subscribers share ONE execution / source.
//   e.g. mouse clicks, a BehaviorSubject — we cover those in the next file.

// ✅ Creating Observables manually (useful for understanding internals)
const manualObservable = new Observable<string>((subscriber) => {
  subscriber.next('First value');
  subscriber.next('Second value');
  subscriber.complete(); // Signals: no more values
});

manualObservable.subscribe({
  next: (val) => console.log('Received:', val),
  error: (err) => console.error('Error:', err),
  complete: () => console.log('Stream complete'),
});
// Output:
// Received: First value
// Received: Second value
// Stream complete

// ---------------------------------------------------------------------------
// Section 2 — Creation Helper Functions
// ---------------------------------------------------------------------------

// of() — emits the values you pass, then completes
const courseIds$ = of(1, 2, 3, 4, 5);
// Emits: 1, 2, 3, 4, 5 → complete

// from() — converts an array, Promise, or iterable to an Observable
const courseTitles$ = from([
  'Angular Fundamentals',
  'RxJS Deep Dive',
  'Spring Boot Mastery',
]);
// Emits each string one at a time

// interval() — emits an incrementing number every N milliseconds
const ticker$ = interval(1000).pipe(take(5));
// Emits: 0, 1, 2, 3, 4  (one per second, then completes)

// fromEvent() — wraps a DOM event as an Observable
// const searchInput = document.getElementById('search-input') as HTMLInputElement;
// const keyup$ = fromEvent<KeyboardEvent>(searchInput, 'keyup');
// Emits every time the user types a key

// ---------------------------------------------------------------------------
// Section 3 — map (Transform Values)
// ---------------------------------------------------------------------------
//
// map() applies a function to every emitted value — like Array.map but for streams.

interface RawCourse {
  id: number;
  course_title: string;    // snake_case from API
  instructor_name: string;
  duration_hours: number;
}

interface Course {
  id: number;
  title: string;           // camelCase for our UI
  instructor: string;
  duration: number;
}

// Pretend this comes from http.get<RawCourse[]>('/api/courses')
const rawCourses$: Observable<RawCourse[]> = of([
  { id: 1, course_title: 'Angular Mastery', instructor_name: 'Jane Dev', duration_hours: 20 },
  { id: 2, course_title: 'RxJS Patterns',   instructor_name: 'Bob Rx',   duration_hours: 8  },
]);

const mappedCourses$: Observable<Course[]> = rawCourses$.pipe(
  map((rawList) =>
    rawList.map((raw): Course => ({
      id: raw.id,
      title: raw.course_title,
      instructor: raw.instructor_name,
      duration: raw.duration_hours,
    }))
  )
);

// The outer map() is the RxJS operator — it acts on each emission from the Observable.
// The inner .map() is Array.prototype.map — it transforms each item inside the array.

mappedCourses$.subscribe((courses) =>
  console.log('Normalised courses:', courses)
);

// ---------------------------------------------------------------------------
// Section 4 — filter (Exclude Values by Predicate)
// ---------------------------------------------------------------------------
//
// filter() only lets values through when the predicate returns true.

interface Enrollment {
  userId: number;
  courseId: number;
  status: 'active' | 'completed' | 'cancelled';
  grade?: number;
}

const enrollments$: Observable<Enrollment> = from([
  { userId: 1, courseId: 10, status: 'active'    },
  { userId: 2, courseId: 10, status: 'completed', grade: 92 },
  { userId: 3, courseId: 10, status: 'cancelled' },
  { userId: 4, courseId: 10, status: 'completed', grade: 78 },
] as Enrollment[]);

// Only let completed enrollments through
const completedEnrollments$ = enrollments$.pipe(
  filter((enrollment) => enrollment.status === 'completed')
);

// Chain map after filter to extract just the grades
const grades$ = completedEnrollments$.pipe(
  filter((e): e is Enrollment & { grade: number } => e.grade !== undefined),
  map((e) => e.grade)
);

grades$.subscribe((grade) => console.log('Grade:', grade));
// Output:
// Grade: 92
// Grade: 78

// ---------------------------------------------------------------------------
// Section 5 — tap (Side Effects Without Modification)
// ---------------------------------------------------------------------------
//
// tap() lets you "peek" at stream values for debugging or side effects
// WITHOUT changing what gets passed downstream.
// Think of it as a transparent window into the stream.

const coursePipeline$ = rawCourses$.pipe(
  tap((raw) => console.log('📥 Raw data received from API:', raw)), // inspect before
  map((rawList) =>
    rawList.map((raw): Course => ({
      id: raw.id,
      title: raw.course_title,
      instructor: raw.instructor_name,
      duration: raw.duration_hours,
    }))
  ),
  tap((courses) => console.log('✅ Mapped courses ready for UI:', courses))  // inspect after
);

// tap is your best debugging tool in RxJS chains.
// Remove it before shipping to production (or use a logging service).

// ---------------------------------------------------------------------------
// Section 6 — switchMap (Cancel Previous, Switch to New)
// ---------------------------------------------------------------------------
//
// switchMap:
//   - Subscribes to a new "inner" Observable for each outer emission
//   - If a new outer value arrives BEFORE the inner Observable completes,
//     it CANCELS the previous inner subscription
//
// ✅ Perfect for: search typeahead, navigation (route changes should cancel API calls)
// ❌ Not for: parallel independent requests (use mergeMap instead)

// Simulate an HTTP search call that takes some time
function searchCourses(query: string): Observable<Course[]> {
  console.log(`🔍 Searching for: "${query}"`);
  // delay() simulates network latency
  return of([{ id: 1, title: `Results for: ${query}`, instructor: 'API', duration: 5 }]).pipe(
    delay(300)
  );
}

// Simulate a user typing quickly: each emission is a new search term
const searchTerms$: Observable<string> = from(['a', 'an', 'ang', 'angu', 'angular']);

const searchResults$ = searchTerms$.pipe(
  debounceTime(200),          // Wait for 200ms pause in typing (Section 8)
  distinctUntilChanged(),     // Don't search if the term hasn't changed (Section 9)
  switchMap((term) => searchCourses(term))
  // If the user types 'angu' before the 'ang' search completes,
  // the 'ang' request is cancelled and only 'angu' fires.
);

searchResults$.subscribe((results) => console.log('Search results:', results));

// ---------------------------------------------------------------------------
// Section 7 — mergeMap (Keep All, Run in Parallel)
// ---------------------------------------------------------------------------
//
// mergeMap:
//   - Subscribes to a new inner Observable for each outer emission
//   - Does NOT cancel previous — all run CONCURRENTLY
//
// ✅ Perfect for: loading details for multiple independent items simultaneously
// ❌ Not for: search (you'd get results from stale queries arriving late)

// Simulate fetching details for each course ID in parallel
function getCourseDetails(id: number): Observable<Course> {
  return of({ id, title: `Course ${id} Details`, instructor: 'API', duration: 10 }).pipe(
    delay(Math.random() * 200) // Different latencies to prove parallel execution
  );
}

const courseIds2$: Observable<number> = from([1, 2, 3]);

// mergeMap: all three requests fire simultaneously
const allCourseDetails$ = courseIds2$.pipe(
  mergeMap((id) => getCourseDetails(id))
  // Results arrive in completion order — NOT necessarily in input order
);

allCourseDetails$.subscribe((course) =>
  console.log('Loaded course:', course.id, course.title)
);

// ---------------------------------------------------------------------------
// Section 8 — debounceTime (Wait for a Quiet Period)
// ---------------------------------------------------------------------------
//
// debounceTime(ms) delays emissions until there has been silence for `ms`.
// It discards all values that arrive within the window and only emits the LAST one.
//
// ✅ Essential for typeahead search — don't fire a request on every single keystroke.

// Simulate keystroke timestamps at 0ms, 100ms, 200ms, 400ms (pause), 600ms
const rapidKeystrokes$ = of('r', 'rx', 'rxj', 'rxjs').pipe(
  // Normally these come from fromEvent(inputEl, 'keyup') mapped to el.value
  debounceTime(300)
  // Only 'rxjs' would be emitted — the others arrive within the 300ms window
);

rapidKeystrokes$.subscribe((val) => console.log('Debounced search term:', val));
// Output: Debounced search term: rxjs

// ---------------------------------------------------------------------------
// Section 9 — distinctUntilChanged (Deduplicate Consecutive Emissions)
// ---------------------------------------------------------------------------
//
// distinctUntilChanged() only emits if the current value is different from
// the previous one. Prevents firing duplicate requests.

const repeatedSearchTerms$ = of('angular', 'angular', 'react', 'angular', 'angular');

const deduplicatedTerms$ = repeatedSearchTerms$.pipe(
  distinctUntilChanged()
  // Emits: 'angular', 'react', 'angular'
  // (Consecutive 'angular' pairs are filtered — the second group still emits because react interrupted)
);

deduplicatedTerms$.subscribe((term) => console.log('Distinct term:', term));
// Output:
// Distinct term: angular
// Distinct term: react
// Distinct term: angular

// ---------------------------------------------------------------------------
// Section 10 — Real-World Search Pipeline (Combining All Operators)
// ---------------------------------------------------------------------------
//
// This is the complete typeahead search pattern you'll use in every Angular app.
// fromEvent → debounceTime → distinctUntilChanged → switchMap → catchError

// In a real Angular component this would look like:

/*
@Component({
  template: `
    <input #searchInput placeholder="Search courses..." />
    <div *ngFor="let course of searchResults">{{ course.title }}</div>
    <div *ngIf="searching">Searching...</div>
    <div *ngIf="searchError" class="error">{{ searchError }}</div>
  `
})
export class CourseSearchComponent implements AfterViewInit {
  @ViewChild('searchInput') searchInputEl!: ElementRef<HTMLInputElement>;

  searchResults: Course[] = [];
  searching = false;
  searchError = '';

  constructor(private courseService: CourseService) {}

  ngAfterViewInit(): void {
    fromEvent<Event>(this.searchInputEl.nativeElement, 'input').pipe(
      // Extract the string value from the event
      map((event) => (event.target as HTMLInputElement).value),

      // Don't fire for very short terms
      filter((term) => term.length >= 2 || term.length === 0),

      // Wait 400ms after the user stops typing
      debounceTime(400),

      // Don't refetch if the term hasn't changed
      distinctUntilChanged(),

      // Log to console for debugging
      tap((term) => console.log('Fetching results for:', term)),

      // Show spinner
      tap(() => { this.searching = true; this.searchError = ''; }),

      // Cancel the previous request if a new one comes in
      switchMap((term) =>
        this.courseService.searchCourses(term).pipe(
          // Handle errors per-search without killing the outer stream
          catchError((err) => {
            this.searchError = err.message;
            return of([]);
          })
        )
      ),

      // Hide spinner (always runs: success or error)
      finalize(() => { this.searching = false; })
    ).subscribe((results) => {
      this.searchResults = results;
      this.searching = false;
    });
  }
}
*/

// ---------------------------------------------------------------------------
// Section 11 — concatMap and exhaustMap (Overview)
// ---------------------------------------------------------------------------
//
// The four flattening operators — summary table:
//
//  Operator      | Cancels previous? | Queues? | Best for
//  ──────────────┼───────────────────┼─────────┼──────────────────────────
//  switchMap     | YES               | No      | Search, navigation
//  mergeMap      | No                | No      | Parallel independent calls
//  concatMap     | No                | YES     | Sequential ordered calls
//  exhaustMap    | Ignores new       | No      | Prevent button double-submit

// concatMap — processes one at a time, QUEUES the rest in order
const orderedRequests$ = courseIds2$.pipe(
  concatMap((id) => getCourseDetails(id))
  // Results arrive in ORDER: 1, then 2, then 3
  // Even if 3 completes before 2, it waits for 2
);
// ✅ Use when order matters (e.g. sequential data processing steps)

// exhaustMap — if an inner Observable is in flight, IGNORE new outer values
const saveButton$: Observable<void> = of(undefined, undefined, undefined); // triple-click
const saveResult$ = saveButton$.pipe(
  exhaustMap(() =>
    // Simulates saving — 500ms operation
    of({ success: true }).pipe(delay(500))
  )
  // Only the first click fires — the second and third are ignored while the first is in flight
);
// ✅ Use when you want to prevent double-submit (save, login button)

orderedRequests$.subscribe(
  (course) => console.log('concatMap result:', course.id)
);
saveResult$.subscribe(
  (result) => console.log('exhaustMap save result:', result)
);
