// =============================================================================
// Day 19b — Angular HTTP & RxJS  |  Part 1
// File: 01-http-client-crud.ts
//
// Topics covered:
//   1. HttpClient setup (HttpClientModule / provideHttpClient)
//   2. GET requests (single resource, list)
//   3. POST requests (create)
//   4. PUT requests  (full update)
//   5. DELETE requests
//   6. HttpParams  (query parameters)
//   7. HttpHeaders (auth headers, custom headers)
//   8. Error handling with catchError + throwError
//
// This file is written as an Angular service that would live at
//   src/app/services/course.service.ts
// alongside a minimal AppModule / app.config.ts to show bootstrapping.
// =============================================================================

import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

// ---------------------------------------------------------------------------
// Section 0 — Bootstrap / Setup
// ---------------------------------------------------------------------------
//
// ✅ Modern Angular 17+ (standalone): add to main.ts / app.config.ts
//
//    import { provideHttpClient } from '@angular/common/http';
//    bootstrapApplication(AppComponent, {
//      providers: [provideHttpClient()]
//    });
//
// ✅ NgModule-based Angular: add HttpClientModule to AppModule imports
//
//    import { HttpClientModule } from '@angular/common/http';
//    @NgModule({ imports: [HttpClientModule, ...] })
//    export class AppModule {}
//
// ⚠️  WATCH OUT: Forgetting to provide HttpClientModule / provideHttpClient()
//     is the #1 cause of "NullInjectorError: No provider for HttpClient" errors.
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// Section 1 — Data Models
// ---------------------------------------------------------------------------

export interface Course {
  id: number;
  title: string;
  instructor: string;
  duration: number; // hours
  level: 'beginner' | 'intermediate' | 'advanced';
  enrollmentCount?: number;
}

export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface ApiError {
  message: string;
  code: string;
  statusCode: number;
}

// ---------------------------------------------------------------------------
// Section 2 — HttpParams (Query Parameters)
// ---------------------------------------------------------------------------
//
// HttpParams builds the ?key=value query string for you.
// It is IMMUTABLE — every call returns a new instance.

function buildCourseSearchParams(
  page: number,
  pageSize: number,
  level?: string,
  search?: string
): HttpParams {
  // Start with required pagination params
  let params = new HttpParams()
    .set('page', page.toString())
    .set('pageSize', pageSize.toString());

  // Conditionally append optional params
  if (level) {
    params = params.set('level', level);
  }
  if (search) {
    params = params.set('search', search);
  }

  return params;
  // Produces: ?page=1&pageSize=10&level=beginner&search=angular
}

// ---------------------------------------------------------------------------
// Section 3 — HttpHeaders (Custom and Auth Headers)
// ---------------------------------------------------------------------------
//
// HttpHeaders is also IMMUTABLE — use .set() / .append() to build headers.

function buildAuthHeaders(token: string): HttpHeaders {
  return new HttpHeaders({
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
    'X-App-Version': '2.0',
  });
}

// ---------------------------------------------------------------------------
// Section 4 — Error Handling Helper
// ---------------------------------------------------------------------------
//
// We centralise error handling so every HTTP method can reuse it.
// catchError intercepts errors in the Observable stream and lets us
// decide what to emit downstream — usually throwError() with a clean message.

function handleHttpError(error: HttpErrorResponse): Observable<never> {
  let userMessage: string;

  if (error.status === 0) {
    // Network error or CORS issue — error.error is a ProgressEvent
    userMessage = 'Network error — check your internet connection.';
    console.error('Network / client-side error:', error.error);
  } else {
    // Server returned a non-2xx status
    userMessage =
      error.error?.message ??
      `Server error: ${error.status} ${error.statusText}`;
    console.error(`Backend returned status ${error.status}:`, error.error);
  }

  // throwError() creates an Observable that immediately errors.
  // The component's subscribe({ error: ... }) callback receives this.
  return throwError(() => new Error(userMessage));
}

// ---------------------------------------------------------------------------
// Section 5 — CourseService (HttpClient in an Injectable service)
// ---------------------------------------------------------------------------

@Injectable({ providedIn: 'root' })
export class CourseService {
  // Base URL would normally come from environment.ts
  private readonly apiUrl = 'https://api.lms-demo.io/v1/courses';

  // Angular injects HttpClient automatically because we provided it above.
  constructor(private http: HttpClient) {}

  // -------------------------------------------------------------------------
  // 5a — GET  (fetch a paginated list)
  // -------------------------------------------------------------------------
  //
  // http.get<T>(url, options) returns Observable<T>.
  // The generic type T tells TypeScript the expected response shape.

  getCourses(
    page = 1,
    pageSize = 10,
    level?: string,
    search?: string
  ): Observable<PaginatedResponse<Course>> {
    const params = buildCourseSearchParams(page, pageSize, level, search);

    return this.http
      .get<PaginatedResponse<Course>>(this.apiUrl, { params })
      .pipe(
        retry(1),            // Retry once on transient network failure
        catchError(handleHttpError)
      );
  }

  // -------------------------------------------------------------------------
  // 5b — GET  (fetch a single resource by ID)
  // -------------------------------------------------------------------------

  getCourseById(id: number): Observable<Course> {
    // Template literal builds:  https://api.lms-demo.io/v1/courses/42
    return this.http
      .get<Course>(`${this.apiUrl}/${id}`)
      .pipe(catchError(handleHttpError));
  }

  // -------------------------------------------------------------------------
  // 5c — POST  (create a new resource)
  // -------------------------------------------------------------------------
  //
  // http.post<T>(url, body, options) — body is serialised to JSON automatically.
  // Angular adds Content-Type: application/json only if you provide a JS object.
  // If you need to pass a Bearer token manually (outside an interceptor),
  // pass custom headers via the options object.

  createCourse(
    courseData: Omit<Course, 'id'>,
    authToken: string
  ): Observable<Course> {
    const headers = buildAuthHeaders(authToken);

    return this.http
      .post<Course>(this.apiUrl, courseData, { headers })
      .pipe(catchError(handleHttpError));
  }

  // -------------------------------------------------------------------------
  // 5d — PUT  (full replacement update)
  // -------------------------------------------------------------------------
  //
  // PUT replaces the entire resource.  PATCH would do a partial update.
  // The body must include ALL fields of the resource.

  updateCourse(id: number, updatedCourse: Course): Observable<Course> {
    return this.http
      .put<Course>(`${this.apiUrl}/${id}`, updatedCourse)
      .pipe(catchError(handleHttpError));
  }

  // -------------------------------------------------------------------------
  // 5e — DELETE  (remove a resource)
  // -------------------------------------------------------------------------
  //
  // Many DELETE endpoints return 204 No Content (empty body).
  // Using void as the generic type is clean for that case.

  deleteCourse(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.apiUrl}/${id}`)
      .pipe(catchError(handleHttpError));
  }

  // -------------------------------------------------------------------------
  // 5f — GET with observe: 'response' (access full HttpResponse)
  // -------------------------------------------------------------------------
  //
  // By default, HttpClient returns only the parsed body.
  // Passing { observe: 'response' } gives you the full HttpResponse object
  // so you can inspect status codes, headers, etc.

  getCourseWithFullResponse(id: number) {
    return this.http
      .get<Course>(`${this.apiUrl}/${id}`, { observe: 'response' })
      .pipe(catchError(handleHttpError));
    // result.status  → 200
    // result.headers → HttpHeaders
    // result.body    → Course object
  }

  // -------------------------------------------------------------------------
  // 5g — POST with HttpParams AND HttpHeaders together
  // -------------------------------------------------------------------------
  //
  // Demonstrates combining both options in a single request.

  enrollInCourse(
    courseId: number,
    userId: number,
    authToken: string
  ): Observable<{ success: boolean; enrollmentId: string }> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${authToken}`,
      'Content-Type': 'application/json',
    });

    const params = new HttpParams()
      .set('courseId', courseId.toString())
      .set('userId', userId.toString());

    const body = { enrolledAt: new Date().toISOString() };

    return this.http
      .post<{ success: boolean; enrollmentId: string }>(
        `${this.apiUrl}/enroll`,
        body,
        { headers, params }
      )
      .pipe(catchError(handleHttpError));
  }
}

// ---------------------------------------------------------------------------
// Section 6 — Component consuming the service (usage example)
// ---------------------------------------------------------------------------
//
// This is NOT a real Angular component file — it's just a class to show
// how a component subscribes to CourseService observables.
//
// In a real app this would be in course-list.component.ts

/*
@Component({ ... })
export class CourseListComponent implements OnInit, OnDestroy {

  courses: Course[] = [];
  errorMessage = '';
  loading = false;

  private subscription = new Subscription();

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    this.loading = true;

    const sub = this.courseService
      .getCourses(1, 10, 'beginner', 'angular')
      .subscribe({
        next: (response) => {
          this.courses = response.data;
          this.loading = false;
        },
        error: (err: Error) => {
          this.errorMessage = err.message;
          this.loading = false;
        },
        complete: () => {
          console.log('Stream completed — no more values expected');
        },
      });

    this.subscription.add(sub);
  }

  ngOnDestroy(): void {
    // Always unsubscribe to prevent memory leaks!
    // (More patterns covered in Part 2 — takeUntilDestroyed, async pipe)
    this.subscription.unsubscribe();
  }

  createNewCourse(): void {
    const draft = {
      title: 'Angular Mastery',
      instructor: 'Jane Dev',
      duration: 20,
      level: 'advanced' as const,
    };

    this.courseService.createCourse(draft, 'my-jwt-token').subscribe({
      next: (created) => console.log('Created:', created),
      error: (err) => console.error('Create failed:', err.message),
    });
  }

  deleteCourse(id: number): void {
    this.courseService.deleteCourse(id).subscribe({
      next: () => {
        this.courses = this.courses.filter((c) => c.id !== id);
      },
      error: (err) => console.error('Delete failed:', err.message),
    });
  }
}
*/

// ---------------------------------------------------------------------------
// Section 7 — Error Handling Scenarios Reference
// ---------------------------------------------------------------------------
//
// Status 0   → No network / CORS / client error   (error.status === 0)
// Status 400 → Bad Request  — invalid input data
// Status 401 → Unauthorized — missing / expired token
// Status 403 → Forbidden    — valid token, insufficient permissions
// Status 404 → Not Found    — resource doesn't exist
// Status 409 → Conflict     — e.g. duplicate enrolment
// Status 422 → Unprocessable Entity — validation error from server
// Status 500 → Internal Server Error — bug on the backend
//
// ✅ Best practice: centralise error mapping in handleHttpError() (Section 4)
//    and surface user-friendly messages in the component.
//
// ✅ Best practice: use retry(1) for idempotent GET requests.
//    ⚠️  Do NOT retry POST/PUT/DELETE — duplicating writes is dangerous.
