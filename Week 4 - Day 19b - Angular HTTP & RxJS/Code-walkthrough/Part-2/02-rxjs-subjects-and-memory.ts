// =============================================================================
// Day 19b — Angular HTTP & RxJS  |  Part 2
// File: 02-rxjs-subjects-and-memory.ts
//
// Topics covered:
//   1. Subject — multicast observable / event bus
//   2. BehaviorSubject — holds current value, replays to new subscribers
//   3. ReplaySubject — replays last N values to new subscribers
//   4. Memory leak demonstration (subscription without unsubscribe)
//   5. Unsubscription pattern 1 — manual Subscription container
//   6. Unsubscription pattern 2 — takeUntilDestroyed() (Angular 16+)
//   7. Unsubscription pattern 3 — async pipe (auto-subscribe / auto-unsubscribe)
//   8. Async pipe in component templates
//   9. When to use which pattern
// =============================================================================

import {
  Subject,
  BehaviorSubject,
  ReplaySubject,
  Subscription,
  interval,
  Observable,
  of,
} from 'rxjs';
import {
  takeUntilDestroyed,
  DestroyRef,
} from '@angular/core/rxjs-interop';
import { map, filter } from 'rxjs/operators';
import {
  Component,
  OnInit,
  OnDestroy,
  inject,
  Input,
} from '@angular/core';
import { AsyncPipe, NgIf, NgFor } from '@angular/common';
import { Injectable } from '@angular/core';

// ---------------------------------------------------------------------------
// Section 1 — Subject
// ---------------------------------------------------------------------------
//
// A Subject is BOTH an Observable AND an Observer.
// - As an Observer: you can call .next(), .error(), .complete() on it
// - As an Observable: you can subscribe to it
//
// Unlike a cold Observable (where each subscriber gets its own execution),
// a Subject is HOT — all subscribers share the same stream.
//
// ✅ Use cases:
//   - Event bus between unrelated components
//   - Manually triggering reloads / refreshes
//   - Cross-component communication

const courseRefresh$ = new Subject<void>();

// Component A triggers a refresh:
// courseRefresh$.next();

// Component B listens for refreshes:
const refreshSub = courseRefresh$.subscribe(() => {
  console.log('Component B: reload courses triggered!');
});

// Trigger it:
courseRefresh$.next();   // Both A and B see this
courseRefresh$.next();   // Fires again

// ⚠️  Key limitation: a Subject does NOT replay to late subscribers.
// If you subscribe AFTER the Subject has already emitted, you miss those values.

const lateSubject$ = new Subject<string>();
lateSubject$.next('Early value — nobody listening yet');

lateSubject$.subscribe((val) => console.log('Late subscriber got:', val));
lateSubject$.next('This value arrives after subscription');
// Output: Late subscriber got: This value arrives after subscription
// The 'Early value' is lost.

// ---------------------------------------------------------------------------
// Section 2 — BehaviorSubject (most common Subject in Angular apps)
// ---------------------------------------------------------------------------
//
// BehaviorSubject:
//   - REQUIRES an initial value
//   - Holds the CURRENT value in memory
//   - When a new subscriber joins, it IMMEDIATELY receives the current value
//   - Then continues to receive future values like a normal Subject
//
// ✅ Perfect for: state management — cart contents, auth status, user profile,
//    loading state, selected filters

class CartService {
  // The BehaviorSubject holds the current cart item count.
  // Initial value: 0
  private cartCount = new BehaviorSubject<number>(0);

  // Expose as Observable so consumers can't call .next() directly
  cartCount$: Observable<number> = this.cartCount.asObservable();

  addItem(): void {
    // getValue() reads the current value synchronously
    const current = this.cartCount.getValue();
    this.cartCount.next(current + 1);
  }

  removeItem(): void {
    const current = this.cartCount.getValue();
    if (current > 0) {
      this.cartCount.next(current - 1);
    }
  }

  clearCart(): void {
    this.cartCount.next(0);
  }
}

const cartService = new CartService();

// Late subscriber joins AFTER two items have been added
cartService.addItem();
cartService.addItem();

// Subscribes now — immediately receives current value (2)
cartService.cartCount$.subscribe((count) =>
  console.log('Cart count:', count)
);
// Output: Cart count: 2

// Then add another item
cartService.addItem();
// Output: Cart count: 3

// ⚠️  WATCH OUT: never expose the raw BehaviorSubject publicly.
// Use .asObservable() to give consumers read-only access.
// If you expose the BehaviorSubject itself, anyone can call .next() and corrupt state.

// ---------------------------------------------------------------------------
// Section 3 — BehaviorSubject for Auth State (realistic example)
// ---------------------------------------------------------------------------

interface User {
  id: number;
  name: string;
  email: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
class AuthService {
  private currentUser = new BehaviorSubject<User | null>(null);

  // Public read-only Observable
  currentUser$: Observable<User | null> = this.currentUser.asObservable();

  // Convenience derived Observable
  isLoggedIn$: Observable<boolean> = this.currentUser$.pipe(
    map((user) => user !== null)
  );

  isAdmin$: Observable<boolean> = this.currentUser$.pipe(
    map((user) => user?.roles.includes('admin') ?? false)
  );

  login(user: User): void {
    this.currentUser.next(user);
  }

  logout(): void {
    this.currentUser.next(null);
  }

  // Synchronous check — useful in route guards
  get currentUserSnapshot(): User | null {
    return this.currentUser.getValue();
  }
}

const authService = new AuthService();

// Subscribe to auth state
authService.isLoggedIn$.subscribe((loggedIn) =>
  console.log('Is logged in:', loggedIn)
);
// Immediately: Is logged in: false

authService.login({ id: 1, name: 'Jane', email: 'jane@dev.io', roles: ['admin'] });
// Is logged in: true

// ---------------------------------------------------------------------------
// Section 4 — ReplaySubject
// ---------------------------------------------------------------------------
//
// ReplaySubject(bufferSize):
//   - Stores the last `bufferSize` emissions
//   - When a new subscriber joins, it REPLAYS those buffered values immediately
//   - Then continues to receive future values
//
// ✅ Use cases:
//   - Notifications / activity feed (replay last 5 notifications to new page views)
//   - Audit log / recent actions
//   - Anything where late subscribers need historical context

const recentNotifications$ = new ReplaySubject<string>(3); // Replay last 3

recentNotifications$.next('Course "Angular 101" was updated');
recentNotifications$.next('You received a grade: 95%');
recentNotifications$.next('New course available: Spring Boot');
recentNotifications$.next('System maintenance scheduled');

// A component that navigates here now subscribes:
recentNotifications$.subscribe((notification) =>
  console.log('Notification:', notification)
);
// Output (replays last 3 — the first one was pushed out of the buffer):
// Notification: You received a grade: 95%
// Notification: New course available: Spring Boot
// Notification: System maintenance scheduled

// ---------------------------------------------------------------------------
// Section 5 — Memory Leak Demonstration
// ---------------------------------------------------------------------------
//
// When a component subscribes to an infinite Observable (like interval() or
// a BehaviorSubject) and is destroyed WITHOUT unsubscribing, the subscription
// stays alive. The callback keeps firing even though the component is gone.
//
// This is a MEMORY LEAK — the component can't be garbage collected.

// BAD EXAMPLE — DO NOT DO THIS
/*
@Component({ selector: 'app-leaky', template: '...' })
class LeakyComponent implements OnInit {
  count = 0;

  ngOnInit(): void {
    // This Observable never completes — it emits forever
    interval(1000).subscribe((n) => {
      this.count = n; // After the component is destroyed, this still runs!
      console.log('Still running after destroy:', n);
    });
  }
  // No ngOnDestroy — no cleanup — memory leak!
}
*/

// ---------------------------------------------------------------------------
// Section 6 — Pattern 1: Manual Subscription Container
// ---------------------------------------------------------------------------
//
// Add every subscription to a Subscription instance.
// Call subscription.unsubscribe() in ngOnDestroy — cancels ALL of them at once.

@Component({ selector: 'app-manual-unsub', template: '...' })
class ManualUnsubscribeComponent implements OnInit, OnDestroy {
  count = 0;
  cartCount = 0;

  // Create ONE Subscription container
  private subscriptions = new Subscription();

  ngOnInit(): void {
    // Add each subscription to the container
    const countSub = interval(1000).pipe(
      map((n) => n * 10)
    ).subscribe((n) => { this.count = n; });

    const cartSub = cartService.cartCount$.subscribe(
      (count) => { this.cartCount = count; }
    );

    // Register both subscriptions
    this.subscriptions.add(countSub);
    this.subscriptions.add(cartSub);
  }

  ngOnDestroy(): void {
    // ONE call cancels everything in the container
    this.subscriptions.unsubscribe();
    console.log('ManualUnsubscribeComponent: all subscriptions cancelled');
  }
}

// ---------------------------------------------------------------------------
// Section 7 — Pattern 2: takeUntilDestroyed() — Angular 16+ (Recommended)
// ---------------------------------------------------------------------------
//
// takeUntilDestroyed() is an RxJS operator that automatically completes an
// Observable when the current injection context (component/service) is destroyed.
//
// No need for ngOnDestroy at all! Angular handles it for you.
//
// ✅ This is the MODERN RECOMMENDED approach.

@Component({ selector: 'app-modern-unsub', template: '...' })
class ModernUnsubscribeComponent implements OnInit {
  count = 0;
  cartCount = 0;

  // Inject DestroyRef — the Angular token that signals component destruction
  private destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    interval(1000).pipe(
      map((n) => n * 10),
      takeUntilDestroyed(this.destroyRef) // ← Auto-cancels on destroy
    ).subscribe((n) => { this.count = n; });

    cartService.cartCount$.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((count) => { this.cartCount = count; });
  }

  // No ngOnDestroy needed — Angular handles cleanup via DestroyRef
}

// ---------------------------------------------------------------------------
// Section 8 — Pattern 3: async Pipe (Best for Template Bindings)
// ---------------------------------------------------------------------------
//
// The async pipe:
//   - Subscribes to an Observable when the component initialises
//   - Updates the template whenever a new value arrives
//   - AUTOMATICALLY UNSUBSCRIBES when the component is destroyed
//   - Never holds a subscription in component code at all
//
// ✅ This is the cleanest approach for Observables that are ONLY used in the template.

@Component({
  selector: 'app-async-pipe-demo',
  standalone: true,
  imports: [AsyncPipe, NgIf, NgFor],
  template: `
    <!-- async pipe subscribes and auto-unsubscribes -->
    <p>Cart items: {{ cartService.cartCount$ | async }}</p>

    <!-- ngIf with async pipe — shows content only when logged in -->
    <ng-container *ngIf="authService.currentUser$ | async as user">
      <p>Welcome, {{ user.name }}!</p>
      <p *ngIf="authService.isAdmin$ | async">You have admin access.</p>
    </ng-container>

    <!-- Loading pattern with async pipe -->
    <ng-container *ngIf="courses$ | async as courses; else loading">
      <div *ngFor="let course of courses">{{ course.title }}</div>
    </ng-container>
    <ng-template #loading>
      <p>Loading courses...</p>
    </ng-template>
  `,
})
class AsyncPipeDemoComponent {
  // Inject services (in a real app these would be proper Angular services)
  cartService = cartService;
  authService = authService;

  // Observable that will eventually hold course data
  // In a real app: courses$ = this.courseService.getCourses();
  courses$ = of([
    { id: 1, title: 'Angular Mastery' },
    { id: 2, title: 'RxJS in Practice' },
  ]);

  // Notice: NO subscribe() call in the component class.
  // NO ngOnDestroy.
  // The async pipe handles everything.
}

// ---------------------------------------------------------------------------
// Section 9 — When to Use Which Pattern
// ---------------------------------------------------------------------------
//
//  Pattern                  | When to use
//  ─────────────────────────┼───────────────────────────────────────────────────
//  async pipe               | Observable only used in the template (best default)
//  takeUntilDestroyed()     | Observable result is needed in component logic
//                           | (updating other variables, triggering side effects)
//  manual Subscription      | Legacy code / Angular < 16 / third-party libraries
//                           | that don't support the newer approaches
//
// ⚠️  NEVER do these:
//   - Subscribe inside subscribe  (nested subscribes — "callback hell for streams")
//   - Subscribe without any cleanup to an infinite observable
//   - Re-subscribe on every change detection cycle

// ---------------------------------------------------------------------------
// Section 10 — Subject vs BehaviorSubject vs ReplaySubject (Summary Table)
// ---------------------------------------------------------------------------
//
//  Type                | Initial value | Replays to new subscriber | Best for
//  ────────────────────┼───────────────┼───────────────────────────┼───────────────────────────────
//  Subject             | None          | No                        | Events, triggers, one-way
//                      |               |                           | notifications
//  BehaviorSubject<T>  | Required      | Latest 1 value            | Current state (cart, auth,
//                      |               |                           | selected item, loading flag)
//  ReplaySubject<T>(n) | None          | Latest N values           | Recent history, notifications,
//                      |               |                           | log replays
//
// ✅ Quick rule: if you need to share "what the current state IS", use BehaviorSubject.
//               If you need to broadcast "something happened", use Subject.
//               If you need "what recently happened", use ReplaySubject.

// ---------------------------------------------------------------------------
// Section 11 — Full Component Example (everything together)
// ---------------------------------------------------------------------------
//
// A realistic component that:
//   - Loads courses on init via HttpClient (simulated)
//   - Handles loading / error states
//   - Uses BehaviorSubject for loading state (shared to template via async pipe)
//   - Uses takeUntilDestroyed() for auto-cleanup

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [AsyncPipe, NgIf, NgFor],
  template: `
    <div *ngIf="isLoading$ | async" class="spinner">Loading...</div>
    <div *ngIf="error" class="error-message">{{ error }}</div>

    <div *ngIf="!(isLoading$ | async) && !error">
      <div *ngFor="let course of courses">
        <h3>{{ course.title }}</h3>
        <p>{{ course.instructor }} — {{ course.duration }}h</p>
      </div>
    </div>
  `,
})
class CourseListComponent implements OnInit {
  courses: Array<{ id: number; title: string; instructor: string; duration: number }> = [];
  error = '';

  // BehaviorSubject for loading state — start as false
  private _isLoading = new BehaviorSubject<boolean>(false);
  isLoading$ = this._isLoading.asObservable(); // Template uses async pipe on this

  private destroyRef = inject(DestroyRef);

  // Simulated course service (in real app: inject CourseService)
  private fakeCourses$ = of([
    { id: 1, title: 'Angular Mastery', instructor: 'Jane Dev', duration: 20 },
    { id: 2, title: 'RxJS in Practice', instructor: 'Bob Rx', duration: 8 },
  ]);

  ngOnInit(): void {
    this._isLoading.next(true);
    this.error = '';

    this.fakeCourses$.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (courses) => {
        this.courses = courses;
        this._isLoading.next(false);
      },
      error: (err: Error) => {
        this.error = err.message;
        this._isLoading.next(false);
      },
    });
  }
}
