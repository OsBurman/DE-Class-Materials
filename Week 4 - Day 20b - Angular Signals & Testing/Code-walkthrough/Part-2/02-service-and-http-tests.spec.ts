// =============================================================================
// Day 20b — Part 2 | File 2: Service Tests, HTTP Tests & Mocking
// =============================================================================
// Topics covered:
//   1. Testing plain services (no HTTP) with Jasmine spies
//   2. Mocking dependencies with jasmine.createSpyObj
//   3. TestBed with providers — injecting real and mock dependencies
//   4. Testing services that use HttpClient
//   5. HttpClientTestingModule and HttpTestingController
//   6. Testing observable-returning methods
//   7. Testing error handling in HTTP calls
//   8. Mocking a service in a component test
// =============================================================================

import {
  TestBed,
  ComponentFixture,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, Component, OnInit } from '@angular/core';
import { Observable, throwError, of } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { By } from '@angular/platform-browser';

// =============================================================================
// ── SERVICES UNDER TEST (defined here for self-contained demo) ───────────────
// =============================================================================

interface Course {
  id: number;
  title: string;
  instructor: string;
  rating: number;
}

// ── Plain service (no HTTP) ───────────────────────────────────────────────────
@Injectable({ providedIn: 'root' })
export class CartService {
  private items: Course[] = [];

  addCourse(course: Course): void {
    if (!this.items.find((c) => c.id === course.id)) {
      this.items.push(course);
    }
  }

  removeCourse(id: number): void {
    this.items = this.items.filter((c) => c.id !== id);
  }

  getItems(): Course[] {
    return [...this.items]; // return copy to prevent direct mutation
  }

  getTotal(): number {
    return this.items.length;
  }

  clear(): void {
    this.items = [];
  }
}

// ── Logger service (will be mocked in component tests) ───────────────────────
@Injectable({ providedIn: 'root' })
export class LoggerService {
  log(message: string): void {
    console.log(`[LOG] ${message}`);
  }

  error(message: string): void {
    console.error(`[ERROR] ${message}`);
  }
}

// ── HTTP service ──────────────────────────────────────────────────────────────
@Injectable({ providedIn: 'root' })
export class CourseService {
  private apiUrl = 'https://api.courseplatform.com/courses';

  constructor(private http: HttpClient) {}

  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  createCourse(course: Omit<Course, 'id'>): Observable<Course> {
    return this.http.post<Course>(this.apiUrl, course).pipe(
      catchError(this.handleError)
    );
  }

  updateCourse(id: number, updates: Partial<Course>): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}`, updates).pipe(
      catchError(this.handleError)
    );
  }

  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let message = 'An unexpected error occurred';
    if (error.status === 404) {
      message = 'Course not found';
    } else if (error.status === 500) {
      message = 'Server error — please try again later';
    }
    return throwError(() => new Error(message));
  }
}

// ── Component that uses CourseService (for mocking demo) ─────────────────────
@Component({
  standalone: true,
  selector: 'app-course-list',
  template: `
    <p data-testid="status">{{ status }}</p>
    <ul>
      <li *ngFor="let course of courses" data-testid="course-item">
        {{ course.title }}
      </li>
    </ul>
    <p data-testid="error-msg">{{ errorMessage }}</p>
  `,
})
export class CourseListComponent implements OnInit {
  courses: Course[] = [];
  status = 'Loading…';
  errorMessage = '';

  constructor(
    private courseService: CourseService,
    private logger: LoggerService
  ) {}

  ngOnInit(): void {
    this.courseService.getCourses().subscribe({
      next: (data) => {
        this.courses = data;
        this.status = `Loaded ${data.length} courses`;
        this.logger.log('Courses loaded successfully');
      },
      error: (err: Error) => {
        this.status = 'Failed';
        this.errorMessage = err.message;
        this.logger.error(err.message);
      },
    });
  }
}

// =============================================================================
// SECTION 1 — Testing a Plain Service (no HTTP)
// =============================================================================
// For services with no dependencies, you can instantiate them directly:
//   const service = new CartService();
// Or use TestBed injection for consistency:
//   service = TestBed.inject(CartService);
// =============================================================================

describe('CartService — unit tests', () => {
  let service: CartService;

  const mockCourse1: Course = { id: 1, title: 'Angular', instructor: 'Alice', rating: 4.8 };
  const mockCourse2: Course = { id: 2, title: 'React',   instructor: 'Bob',   rating: 4.7 };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with an empty cart', () => {
    expect(service.getTotal()).toBe(0);
    expect(service.getItems()).toEqual([]);
  });

  it('should add a course to the cart', () => {
    service.addCourse(mockCourse1);
    expect(service.getTotal()).toBe(1);
    expect(service.getItems()).toContain(mockCourse1);
  });

  it('should not add the same course twice', () => {
    service.addCourse(mockCourse1);
    service.addCourse(mockCourse1); // duplicate
    expect(service.getTotal()).toBe(1);
  });

  it('should remove a course by id', () => {
    service.addCourse(mockCourse1);
    service.addCourse(mockCourse2);

    service.removeCourse(1);

    expect(service.getTotal()).toBe(1);
    expect(service.getItems()).not.toContain(mockCourse1);
    expect(service.getItems()).toContain(mockCourse2);
  });

  it('should clear all items', () => {
    service.addCourse(mockCourse1);
    service.addCourse(mockCourse2);
    service.clear();

    expect(service.getTotal()).toBe(0);
    expect(service.getItems()).toEqual([]);
  });

  it('should return a COPY of items, not the internal array', () => {
    service.addCourse(mockCourse1);

    const items = service.getItems();
    items.push(mockCourse2); // try to mutate the returned array

    // The internal array should not be affected
    expect(service.getTotal()).toBe(1);
  });
});

// =============================================================================
// SECTION 2 — Mocking Dependencies with Jasmine Spies
// =============================================================================
// spyOn(object, 'methodName')
//   → wraps an existing method with a spy
//   → tracks calls, arguments, return values
//
// jasmine.createSpyObj('name', ['method1', 'method2', ...])
//   → creates a mock object with all specified methods as spies
//   → use when you want a complete fake (no real object needed)
//
// spy.and.returnValue(value)     → stub the return value
// spy.and.callFake(fn)           → stub with a custom function
// spy.and.throwError('message')  → stub to throw
// expect(spy).toHaveBeenCalled()
// expect(spy).toHaveBeenCalledWith(arg1, arg2)
// expect(spy).toHaveBeenCalledTimes(n)
// =============================================================================

describe('Jasmine Spies — standalone demo', () => {
  it('should spy on an existing method with spyOn()', () => {
    const logger = new LoggerService();

    // Wrap .log() with a spy — tracks calls without running the real code
    const spy = spyOn(logger, 'log');

    logger.log('Hello tests');

    expect(spy).toHaveBeenCalled();
    expect(spy).toHaveBeenCalledWith('Hello tests');
    expect(spy).toHaveBeenCalledTimes(1);
  });

  it('should stub a return value with .and.returnValue()', () => {
    const service = new CartService();
    spyOn(service, 'getTotal').and.returnValue(99); // lie about the total

    // Even though cart is empty, getTotal() now returns 99
    expect(service.getTotal()).toBe(99);
  });

  it('should create a full mock object with jasmine.createSpyObj()', () => {
    // Creates a fake CourseService — no real HTTP calls, no real logic
    const mockCourseService = jasmine.createSpyObj<CourseService>(
      'CourseService',
      ['getCourses', 'getCourseById', 'createCourse', 'deleteCourse']
    );

    // Configure what the spy returns
    const fakeCourses: Course[] = [
      { id: 1, title: 'Mock Course', instructor: 'Spy', rating: 5.0 },
    ];
    mockCourseService.getCourses.and.returnValue(of(fakeCourses));

    // Use it like a real service
    let result: Course[] = [];
    mockCourseService.getCourses().subscribe((courses) => (result = courses));

    expect(result).toEqual(fakeCourses);
    expect(mockCourseService.getCourses).toHaveBeenCalledTimes(1);
  });
});

// =============================================================================
// SECTION 3 — Testing HTTP Services with HttpClientTestingModule
// =============================================================================
// HttpClientTestingModule replaces the real HttpClient with a test double.
// HttpTestingController lets you:
//   • expectOne(url)    → assert that exactly one request was made to url
//   • expectNone(url)   → assert no request was made
//   • match(url)        → get all requests matching url
//   • req.flush(data)   → provide the mock response body
//   • req.flush(data, { status: 404, statusText: 'Not Found' }) → error resp
//   • req.error(...)    → simulate a network error
//   • controller.verify() → assert no unexpected requests remain
// =============================================================================

describe('CourseService — HTTP tests', () => {
  let service: CourseService;
  let httpController: HttpTestingController;

  const mockCourses: Course[] = [
    { id: 1, title: 'Angular',  instructor: 'Alice', rating: 4.8 },
    { id: 2, title: 'React',    instructor: 'Bob',   rating: 4.7 },
    { id: 3, title: 'Node.js',  instructor: 'Carol', rating: 4.6 },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      // HttpClientTestingModule intercepts ALL HttpClient calls in tests
      imports: [HttpClientTestingModule],
      providers: [CourseService],
    });

    service = TestBed.inject(CourseService);
    httpController = TestBed.inject(HttpTestingController);
  });

  // IMPORTANT: After each test, verify no unexpected requests were made
  afterEach(() => {
    httpController.verify();
  });

  // ── GET all courses ────────────────────────────────────────────────────────
  it('should GET all courses', () => {
    let receivedCourses: Course[] = [];

    // 1. Call the service method — this queues a pending HTTP request
    service.getCourses().subscribe((courses) => {
      receivedCourses = courses;
    });

    // 2. Assert that exactly one GET request was made to the expected URL
    const req = httpController.expectOne('https://api.courseplatform.com/courses');
    expect(req.request.method).toBe('GET');

    // 3. Flush (resolve) the request with mock data
    req.flush(mockCourses);

    // 4. Now the observable has completed — check the result
    expect(receivedCourses.length).toBe(3);
    expect(receivedCourses[0].title).toBe('Angular');
  });

  // ── GET by ID ──────────────────────────────────────────────────────────────
  it('should GET a course by id', () => {
    let receivedCourse: Course | undefined;

    service.getCourseById(1).subscribe((course) => {
      receivedCourse = course;
    });

    const req = httpController.expectOne('https://api.courseplatform.com/courses/1');
    expect(req.request.method).toBe('GET');

    req.flush(mockCourses[0]); // respond with the first mock course

    expect(receivedCourse?.title).toBe('Angular');
  });

  // ── POST (create) ──────────────────────────────────────────────────────────
  it('should POST to create a new course', () => {
    const newCourse = { title: 'Vue.js', instructor: 'Dave', rating: 4.5 };
    const createdCourse: Course = { id: 4, ...newCourse };

    let result: Course | undefined;

    service.createCourse(newCourse).subscribe((c) => (result = c));

    const req = httpController.expectOne('https://api.courseplatform.com/courses');
    expect(req.request.method).toBe('POST');

    // Verify the request body contains the right data
    expect(req.request.body).toEqual(newCourse);

    req.flush(createdCourse); // server returns the created object with id

    expect(result?.id).toBe(4);
    expect(result?.title).toBe('Vue.js');
  });

  // ── PUT (update) ───────────────────────────────────────────────────────────
  it('should PUT to update a course', () => {
    const updates = { rating: 5.0 };
    const updatedCourse: Course = { ...mockCourses[0], rating: 5.0 };

    let result: Course | undefined;

    service.updateCourse(1, updates).subscribe((c) => (result = c));

    const req = httpController.expectOne('https://api.courseplatform.com/courses/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updates);

    req.flush(updatedCourse);

    expect(result?.rating).toBe(5.0);
  });

  // ── DELETE ─────────────────────────────────────────────────────────────────
  it('should DELETE a course by id', () => {
    let completed = false;

    service.deleteCourse(1).subscribe({ complete: () => (completed = true) });

    const req = httpController.expectOne('https://api.courseplatform.com/courses/1');
    expect(req.request.method).toBe('DELETE');

    req.flush(null); // DELETE typically returns no body

    expect(completed).toBeTrue();
  });

  // ── Error handling — 404 ───────────────────────────────────────────────────
  it('should handle 404 error with a friendly message', () => {
    let errorMessage = '';

    service.getCourseById(999).subscribe({
      next: () => fail('Should have thrown an error'),
      error: (err: Error) => (errorMessage = err.message),
    });

    const req = httpController.expectOne('https://api.courseplatform.com/courses/999');

    // Flush with an error status
    req.flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(errorMessage).toBe('Course not found');
  });

  // ── Error handling — 500 ───────────────────────────────────────────────────
  it('should handle 500 server error', () => {
    let errorMessage = '';

    service.getCourses().subscribe({
      next: () => fail('Should have thrown an error'),
      error: (err: Error) => (errorMessage = err.message),
    });

    // The service retries once — handle the retry request too
    const reqs = httpController.match('https://api.courseplatform.com/courses');
    expect(reqs.length).toBe(2); // original + 1 retry (retry(1))

    reqs.forEach((req) =>
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' })
    );

    expect(errorMessage).toBe('Server error — please try again later');
  });

  // ── Network error ──────────────────────────────────────────────────────────
  it('should handle network errors', () => {
    let errorThrown = false;

    service.getCourses().subscribe({
      next: () => fail('Should have failed'),
      error: () => (errorThrown = true),
    });

    // Handle both original + retry attempts
    const reqs = httpController.match('https://api.courseplatform.com/courses');
    reqs.forEach((req) =>
      req.error(new ProgressEvent('network error'))
    );

    expect(errorThrown).toBeTrue();
  });
});

// =============================================================================
// SECTION 4 — Mocking a Service in a Component Test
// =============================================================================
// When testing a component that depends on a service, you should provide a
// MOCK of the service instead of the real one. This:
//   1. Prevents real HTTP calls in unit tests
//   2. Lets you control the service's responses precisely
//   3. Keeps tests fast and deterministic
//
// Three patterns:
//   a) jasmine.createSpyObj — clean, type-safe mock
//   b) { provide: ServiceClass, useValue: fakeObject } — manual mock
//   c) { provide: ServiceClass, useClass: MockServiceClass } — mock class
// =============================================================================

describe('CourseListComponent — with mocked CourseService', () => {
  let fixture: ComponentFixture<CourseListComponent>;
  let component: CourseListComponent;

  // ── Pattern a: jasmine.createSpyObj ───────────────────────────────────────
  let mockCourseService: jasmine.SpyObj<CourseService>;
  let mockLoggerService: jasmine.SpyObj<LoggerService>;

  const fakeCourses: Course[] = [
    { id: 1, title: 'Angular Signals', instructor: 'Alice', rating: 4.9 },
    { id: 2, title: 'Angular Testing', instructor: 'Alice', rating: 4.8 },
  ];

  beforeEach(async () => {
    // Create spy objects BEFORE configureTestingModule so we can provide them
    mockCourseService = jasmine.createSpyObj<CourseService>('CourseService', [
      'getCourses',
    ]);
    mockLoggerService = jasmine.createSpyObj<LoggerService>('LoggerService', [
      'log',
      'error',
    ]);

    // Default happy-path: getCourses returns two fake courses
    mockCourseService.getCourses.and.returnValue(of(fakeCourses));

    await TestBed.configureTestingModule({
      imports: [CourseListComponent],
      providers: [
        // ← Replace the real services with our spies
        { provide: CourseService, useValue: mockCourseService },
        { provide: LoggerService, useValue: mockLoggerService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit → calls mockCourseService.getCourses()
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call getCourses() on init', () => {
    // Verify the service was called exactly once
    expect(mockCourseService.getCourses).toHaveBeenCalledTimes(1);
  });

  it('should display loaded courses in the template', () => {
    const items = fixture.debugElement.queryAll(By.css('[data-testid="course-item"]'));
    expect(items.length).toBe(2);
    expect(items[0].nativeElement.textContent).toContain('Angular Signals');
  });

  it('should update status text after loading', () => {
    const status = fixture.debugElement
      .query(By.css('[data-testid="status"]'))
      .nativeElement as HTMLElement;

    expect(status.textContent).toContain('Loaded 2 courses');
  });

  it('should log success message via LoggerService', () => {
    expect(mockLoggerService.log).toHaveBeenCalledWith('Courses loaded successfully');
  });

  // ── Testing error scenarios ────────────────────────────────────────────────
  it('should display error message when getCourses() fails', () => {
    // Override the spy to return an error Observable for THIS test
    mockCourseService.getCourses.and.returnValue(
      throwError(() => new Error('Course not found'))
    );

    // Re-create the component to trigger ngOnInit with the new spy behavior
    fixture = TestBed.createComponent(CourseListComponent);
    fixture.detectChanges();

    const errorMsg = fixture.debugElement
      .query(By.css('[data-testid="error-msg"]'))
      .nativeElement as HTMLElement;

    expect(errorMsg.textContent).toContain('Course not found');
    expect(mockLoggerService.error).toHaveBeenCalledWith('Course not found');
  });

  it('should show Failed status on error', () => {
    mockCourseService.getCourses.and.returnValue(
      throwError(() => new Error('Server error'))
    );

    fixture = TestBed.createComponent(CourseListComponent);
    fixture.detectChanges();

    const status = fixture.debugElement
      .query(By.css('[data-testid="status"]'))
      .nativeElement as HTMLElement;

    expect(status.textContent).toContain('Failed');
  });
});

// =============================================================================
// SECTION 5 — Pattern b: useValue with a manually constructed mock object
// =============================================================================
// Sometimes you want a plain object instead of a spy, especially for
// services that only return static data.
// =============================================================================

describe('CourseListComponent — with useValue mock', () => {
  beforeEach(async () => {
    // Inline mock object — no jasmine spy tracking, just a stub
    const fakeCourseService = {
      getCourses: () =>
        of([{ id: 99, title: 'Stubbed Course', instructor: 'Stub', rating: 3.0 }]),
    };

    await TestBed.configureTestingModule({
      imports: [CourseListComponent],
      providers: [
        { provide: CourseService, useValue: fakeCourseService },
        {
          provide: LoggerService,
          useValue: { log: () => {}, error: () => {} }, // no-op stubs
        },
      ],
    }).compileComponents();
  });

  it('should render data from the stub service', () => {
    const fixture = TestBed.createComponent(CourseListComponent);
    fixture.detectChanges();

    const status = fixture.debugElement
      .query(By.css('[data-testid="status"]'))
      .nativeElement as HTMLElement;

    expect(status.textContent).toContain('Loaded 1 courses');
  });
});
