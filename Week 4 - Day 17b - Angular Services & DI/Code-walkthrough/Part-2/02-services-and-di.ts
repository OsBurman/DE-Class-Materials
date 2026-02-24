// =============================================================================
// 02-services-and-di.ts — Services, Dependency Injection & Sharing Data
// =============================================================================
// A SERVICE is a class that holds logic and data NOT tied to any single
// component. Services are perfect for:
//   • Sharing data between components that are far apart in the tree
//   • HTTP calls, business logic, caching
//   • Cross-cutting concerns (logging, auth state, notifications)
//
// DEPENDENCY INJECTION (DI) is Angular's built-in mechanism for providing
// service instances to the classes that need them.
//
// SECTIONS:
//  1. Creating a basic service — CourseService
//  2. Injecting a service into a component
//  3. Sharing data between sibling components via a service
//  4. Providers and injector hierarchy
//  5. providedIn: 'root' vs module-level providers
//  6. Service with an Observable (BehaviorSubject pattern)
//  7. Logger service — cross-cutting concern example
// =============================================================================

import {
  Injectable, Component, OnInit, OnDestroy, NgModule,
  InjectionToken, Inject
} from '@angular/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Creating a Basic Service
// ─────────────────────────────────────────────────────────────────────────────
// @Injectable({ providedIn: 'root' }) registers this service with the ROOT
// injector — one shared singleton instance across the entire application.

@Injectable({
  providedIn: 'root'  // ← "tree-shakeable" provider at the root level
})
export class CourseService {
  // Private data — only this service can mutate it directly
  private courses: Course[] = [
    { id: 1, title: 'React Hooks',        level: 'Intermediate', enrolled: false },
    { id: 2, title: 'Angular Services',   level: 'Intermediate', enrolled: false },
    { id: 3, title: 'Spring Boot',        level: 'Beginner',     enrolled: true  },
    { id: 4, title: 'Spring Security',    level: 'Advanced',     enrolled: false },
    { id: 5, title: 'Docker & Kubernetes',level: 'Advanced',     enrolled: false },
  ];

  // Public read-only methods — components call these instead of touching the array
  getAll(): Course[] {
    return this.courses;
  }

  getById(id: number): Course | undefined {
    return this.courses.find(c => c.id === id);
  }

  enroll(id: number): void {
    const course = this.getById(id);
    if (course) {
      course.enrolled = true;
      console.log(`[CourseService] Enrolled in: ${course.title}`);
    }
  }

  unenroll(id: number): void {
    const course = this.getById(id);
    if (course) course.enrolled = false;
  }

  getEnrolled(): Course[] {
    return this.courses.filter(c => c.enrolled);
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Injecting a Service into a Component
// ─────────────────────────────────────────────────────────────────────────────
// Angular reads the constructor parameter types and injects the right service.
// Three ways to inject — constructor injection is the standard.

@Component({
  selector: 'app-course-browser',
  template: `
    <h3>All Courses ({{ courses.length }} total)</h3>
    <ul>
      <li *ngFor="let course of courses">
        {{ course.title }}
        <button (click)="enroll(course.id)" [disabled]="course.enrolled">
          {{ course.enrolled ? '✅ Enrolled' : 'Enroll' }}
        </button>
      </li>
    </ul>
  `
})
export class CourseBrowserComponent implements OnInit {
  courses: Course[] = [];

  // Constructor injection — Angular sees CourseService in the constructor,
  // looks it up in the injector, and passes the instance automatically.
  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    // The service is available here because the constructor ran first
    this.courses = this.courseService.getAll();
  }

  enroll(id: number): void {
    this.courseService.enroll(id);
    // Refresh local reference (in real apps, use an Observable instead)
    this.courses = [...this.courseService.getAll()];
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Sharing Data Between Sibling Components via a Service
// ─────────────────────────────────────────────────────────────────────────────
// Problem: CourseListComponent and EnrolledCoursesComponent are SIBLINGS —
// neither is the parent of the other. How do they share the enrolled list?
// Solution: both inject the SAME service instance. One writes, the other reads.

@Component({
  selector: 'app-course-list-sibling',
  template: `
    <h3>Course Browser (Sibling A)</h3>
    <ul>
      <li *ngFor="let c of courses">
        {{ c.title }}
        <button (click)="enroll(c.id)" [disabled]="c.enrolled">Enroll</button>
      </li>
    </ul>
  `
})
export class CourseSiblingListComponent implements OnInit {
  courses: Course[] = [];
  constructor(private svc: CourseService) {}
  ngOnInit(): void { this.courses = this.svc.getAll(); }
  enroll(id: number): void {
    this.svc.enroll(id);
    this.courses = [...this.svc.getAll()];  // trigger change detection
  }
}

@Component({
  selector: 'app-enrolled-courses',
  template: `
    <h3>My Enrolled Courses (Sibling B)</h3>
    <p *ngIf="enrolled.length === 0">No courses enrolled yet.</p>
    <ul>
      <li *ngFor="let c of enrolled">✅ {{ c.title }}</li>
    </ul>
    <button (click)="refresh()">🔄 Refresh</button>
  `
})
export class EnrolledCoursesComponent implements OnInit {
  enrolled: Course[] = [];
  constructor(private svc: CourseService) {}  // same CourseService instance!
  ngOnInit(): void { this.refresh(); }
  refresh(): void { this.enrolled = this.svc.getEnrolled(); }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Service With BehaviorSubject: Reactive Data Sharing
// ─────────────────────────────────────────────────────────────────────────────
// The manual "refresh" button in Sibling B is clunky. A better pattern:
// the service exposes an Observable. Components subscribe and auto-update.

@Injectable({ providedIn: 'root' })
export class CartService {
  // BehaviorSubject: remembers the current value and emits it to new subscribers
  private cartSubject = new BehaviorSubject<CartItem[]>([]);

  // Public read-only Observable — components subscribe to this
  // Using asObservable() prevents components from calling .next() directly
  cart$: Observable<CartItem[]> = this.cartSubject.asObservable();

  addItem(course: Course): void {
    const current = this.cartSubject.getValue();
    if (!current.find(i => i.courseId === course.id)) {
      this.cartSubject.next([...current, { courseId: course.id, title: course.title }]);
    }
  }

  removeItem(courseId: number): void {
    const updated = this.cartSubject.getValue().filter(i => i.courseId !== courseId);
    this.cartSubject.next(updated);
  }

  getCount(): Observable<number> {
    return new Observable(observer => {
      this.cart$.subscribe(items => observer.next(items.length));
    });
  }
}

// Component A — adds items to the cart
@Component({
  selector: 'app-add-to-cart',
  template: `
    <h3>Add to Cart</h3>
    <ul>
      <li *ngFor="let c of courses">
        {{ c.title }}
        <button (click)="addToCart(c)">🛒 Add</button>
      </li>
    </ul>
  `
})
export class AddToCartComponent {
  courses = [
    { id: 1, title: 'React Hooks',    level: 'Intermediate', enrolled: false },
    { id: 2, title: 'Spring Boot',    level: 'Beginner',     enrolled: false },
  ];
  constructor(private cart: CartService) {}
  addToCart(course: Course): void { this.cart.addItem(course); }
}

// Component B — displays cart count in a header (subscribes to the same service)
@Component({
  selector: 'app-cart-badge',
  template: `
    <div class="cart-badge">
      🛒 Cart ({{ cartItems.length }})
      <ul>
        <li *ngFor="let item of cartItems">
          {{ item.title }}
          <button (click)="remove(item.courseId)">✕</button>
        </li>
      </ul>
    </div>
  `
})
export class CartBadgeComponent implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  private sub!: Subscription;

  constructor(private cart: CartService) {}

  ngOnInit(): void {
    // Subscribe to the Observable — auto-updates whenever cart changes
    this.sub = this.cart.cart$.subscribe(items => {
      this.cartItems = items;
    });
  }

  ngOnDestroy(): void {
    // Always unsubscribe in ngOnDestroy to prevent memory leaks
    this.sub.unsubscribe();
  }

  remove(id: number): void { this.cart.removeItem(id); }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Providers and Injector Hierarchy
// ─────────────────────────────────────────────────────────────────────────────
//
// Angular has a tree of injectors mirroring the component tree:
//
//   Root Injector (AppModule)
//     └── Module Injector (FeatureModule)
//           └── Component Injector (ParentComponent)
//                 └── Component Injector (ChildComponent)
//
// When a component asks for a service, Angular walks UP the injector tree
// until it finds a provider. The FIRST match wins.
//
// providedIn: 'root'  → Root injector → ONE instance shared app-wide (singleton)
// providers: [Svc]    → Component injector → NEW instance per component instance

// Singleton service (providedIn: 'root') — shared across everything
@Injectable({ providedIn: 'root' })
export class GlobalNotificationService {
  private messages: string[] = [];

  add(msg: string): void { this.messages.push(msg); }
  getAll(): string[] { return [...this.messages]; }
}

// Component-scoped service — each instance of this component gets its OWN service
@Component({
  selector: 'app-counter-with-own-service',
  template: `<p>Count: {{ count }}</p><button (click)="increment()">+</button>`,
  // Providing in the component creates a new instance for EACH component instance
  providers: [/* SomeComponentScopedService */]
})
export class CounterWithOwnServiceComponent {
  count = 0;
  increment(): void { this.count++; }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6 — InjectionToken — injecting non-class values
// ─────────────────────────────────────────────────────────────────────────────
// You can't use class types for primitive values (strings, configs).
// InjectionToken creates a typed DI token for any value.

export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL');

// In AppModule providers:
// { provide: API_BASE_URL, useValue: 'https://api.example.com' }

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(@Inject(API_BASE_URL) private baseUrl: string) {
    console.log('[ApiService] Base URL:', this.baseUrl);
  }

  buildUrl(path: string): string {
    return `${this.baseUrl}/${path}`;
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 7 — Logger Service — cross-cutting concern
// ─────────────────────────────────────────────────────────────────────────────

@Injectable({ providedIn: 'root' })
export class LoggerService {
  private logs: LogEntry[] = [];

  log(message: string, level: 'info' | 'warn' | 'error' = 'info'): void {
    const entry: LogEntry = { message, level, timestamp: new Date() };
    this.logs.push(entry);
    const icon = level === 'error' ? '❌' : level === 'warn' ? '⚠️' : 'ℹ️';
    console[level](`${icon} [${entry.timestamp.toISOString()}] ${message}`);
  }

  getLogs(): LogEntry[] { return [...this.logs]; }
  clearLogs(): void { this.logs = []; }
}

// A component that uses both CourseService and LoggerService
@Component({
  selector: 'app-course-manager',
  template: `
    <h3>Course Manager</h3>
    <button (click)="loadCourses()">Load Courses</button>
    <button (click)="logger.clearLogs()">Clear Logs</button>
    <ul><li *ngFor="let c of courses">{{ c.title }}</li></ul>
    <h4>Log ({{ logger.getLogs().length }} entries)</h4>
    <pre *ngFor="let log of logger.getLogs()">
[{{ log.level | uppercase }}] {{ log.message }}
    </pre>
  `
})
export class CourseManagerComponent {
  courses: Course[] = [];

  // Multiple services injected via constructor — Angular resolves both
  constructor(
    private courseService: CourseService,
    public logger: LoggerService
  ) {}

  loadCourses(): void {
    this.logger.log('Fetching courses…');
    this.courses = this.courseService.getAll();
    this.logger.log(`Loaded ${this.courses.length} courses`);
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Interfaces / types used above
// ─────────────────────────────────────────────────────────────────────────────

interface Course {
  id: number;
  title: string;
  level: string;
  enrolled: boolean;
}

interface CartItem {
  courseId: number;
  title: string;
}

interface LogEntry {
  message: string;
  level: 'info' | 'warn' | 'error';
  timestamp: Date;
}
