// =============================================================================
// Day 20b — Part 1: Angular Signals (Angular 16+)
// =============================================================================
// Topics covered:
//   1. What are Signals? Why do they exist?
//   2. Creating and using signals (signal(), .set(), .update())
//   3. Computed signals (computed())
//   4. Effects with signals (effect())
//   5. Signals vs Observables — when to use each
// =============================================================================
// NOTE: This file is written as a standalone TypeScript reference.
// In a real Angular project, these patterns appear inside @Component classes
// or @Injectable services. Each section ends with a runnable example showing
// the Angular-component context in comments.
// =============================================================================

import {
  signal,
  computed,
  effect,
  Signal,
  WritableSignal,
} from '@angular/core';

// =============================================================================
// SECTION 1 — What Are Signals?
// =============================================================================
// A Signal is a reactive primitive — a value container that notifies Angular
// whenever the value changes. Angular 16 introduced them as a lighter,
// more predictable alternative to Zone.js-based change detection.
//
// Key facts:
// • A signal ALWAYS holds a value (never undefined unless you say so)
// • Reading a signal is just a function call: mySignal()
// • Writing a signal triggers Angular to update only the parts of the
//   template that actually depend on it (fine-grained reactivity)
// • No subscription management — no subscribe(), no unsubscribe()
// =============================================================================

// =============================================================================
// SECTION 2 — Creating and Using Signals
// =============================================================================
// signal(initialValue)  → creates a WritableSignal<T>
// .set(newValue)        → replace the value entirely
// .update(fn)           → derive the new value from the current one
// .asReadonly()         → returns a read-only view (Signal<T>, not WritableSignal)
// =============================================================================

// ── Basic writable signal ─────────────────────────────────────────────────────
const courseTitle: WritableSignal<string> = signal('Angular Signals Deep Dive');

// Read: call the signal like a function
console.log('Initial title:', courseTitle()); // "Angular Signals Deep Dive"

// .set() — completely replaces the value
courseTitle.set('Angular Signals — Updated Title');
console.log('After set():', courseTitle()); // "Angular Signals — Updated Title"

// .update() — transforms the current value using a callback
courseTitle.update((current) => current.toUpperCase());
console.log('After update():', courseTitle()); // "ANGULAR SIGNALS — UPDATED TITLE"

// ── Numeric signal with .update() ────────────────────────────────────────────
const enrollmentCount: WritableSignal<number> = signal(0);

enrollmentCount.update((n) => n + 1); // increment
enrollmentCount.update((n) => n + 1); // increment again
console.log('Enrollment count:', enrollmentCount()); // 2

// ── Object signal ─────────────────────────────────────────────────────────────
interface Course {
  id: number;
  title: string;
  rating: number;
  published: boolean;
}

const selectedCourse: WritableSignal<Course | null> = signal(null);

selectedCourse.set({
  id: 1,
  title: 'Angular Fundamentals',
  rating: 4.8,
  published: true,
});

// To mutate a property, use .update() — always return a NEW object
selectedCourse.update((course) =>
  course ? { ...course, rating: 4.9 } : null
);

console.log('Updated rating:', selectedCourse()?.rating); // 4.9

// ── Array signal ──────────────────────────────────────────────────────────────
const courseList: WritableSignal<Course[]> = signal([]);

courseList.update((list) => [
  ...list,
  { id: 1, title: 'Angular Fundamentals', rating: 4.8, published: true },
]);
courseList.update((list) => [
  ...list,
  { id: 2, title: 'Angular Signals', rating: 4.9, published: false },
]);

console.log('Course list length:', courseList().length); // 2

// ── Read-only signal (expose state without allowing mutation) ─────────────────
const _internalCount: WritableSignal<number> = signal(0);
const publicCount: Signal<number> = _internalCount.asReadonly();

// publicCount.set(5); // ← TypeScript ERROR — read-only!
console.log('Public count:', publicCount()); // 0

// ─────────────────────────────────────────────────────────────────────────────
// ANGULAR COMPONENT CONTEXT
// =============================================================================
// In a real component this looks like:
//
// @Component({
//   standalone: true,
//   template: `
//     <h1>{{ title() }}</h1>
//     <p>Enrolled: {{ count() }}</p>
//     <button (click)="enroll()">Enroll</button>
//   `,
// })
// export class CourseDetailComponent {
//   title = signal('Angular Signals');
//   count = signal(0);
//
//   enroll() {
//     this.count.update(n => n + 1);
//   }
// }
//
// Angular's template compiler sees title() and count() as signal reads.
// When their values change, ONLY the DOM nodes that read them are updated.
// No full change detection cycle is needed.
// =============================================================================

// =============================================================================
// SECTION 3 — Computed Signals
// =============================================================================
// computed(() => expression)
//
// • Creates a DERIVED, read-only Signal whose value is automatically
//   recalculated whenever any signal it reads changes
// • Computed signals are LAZY — they only recompute when they are read
//   AND one of their dependencies has changed (memoized)
// • You cannot .set() or .update() a computed signal
// =============================================================================

const price: WritableSignal<number> = signal(199.99);
const discountPercent: WritableSignal<number> = signal(20); // 20%

// Computed from two other signals
const discountedPrice: Signal<number> = computed(
  () => price() * (1 - discountPercent() / 100)
);

console.log('Discounted price:', discountedPrice().toFixed(2)); // 159.99

price.set(249.99);
console.log('After price change:', discountedPrice().toFixed(2)); // 199.99

discountPercent.set(30);
console.log('After discount change:', discountedPrice().toFixed(2)); // 174.99

// ── Computed from an array signal ─────────────────────────────────────────────
const courses: WritableSignal<Course[]> = signal([
  { id: 1, title: 'Angular Fundamentals', rating: 4.8, published: true },
  { id: 2, title: 'Angular Signals',      rating: 4.9, published: false },
  { id: 3, title: 'RxJS Deep Dive',       rating: 4.7, published: true },
]);

// Only show published courses
const publishedCourses: Signal<Course[]> = computed(
  () => courses().filter((c) => c.published)
);

// Average rating across published courses
const averageRating: Signal<number> = computed(() => {
  const pub = publishedCourses(); // reads publishedCourses, which reads courses
  if (pub.length === 0) return 0;
  return pub.reduce((sum, c) => sum + c.rating, 0) / pub.length;
});

console.log('Published count:', publishedCourses().length);   // 2
console.log('Average rating:', averageRating().toFixed(2));    // 4.75

// Publish course #2 — cascading update
courses.update((list) =>
  list.map((c) => (c.id === 2 ? { ...c, published: true } : c))
);

console.log('Published count after update:', publishedCourses().length); // 3
console.log('Average rating after update:', averageRating().toFixed(2));  // 4.80

// ─────────────────────────────────────────────────────────────────────────────
// INSTRUCTOR NOTE: Computed dependency tracking
// Angular automatically tracks which signals were called (read) inside
// computed(). If you branch — e.g. if (flag()) { return a() } else { return b() }
// — only the signals in the taken branch are tracked.
// =============================================================================

// =============================================================================
// SECTION 4 — Effects with Signals
// =============================================================================
// effect(fn)
//
// • Runs a side-effect function whenever any signal it reads changes
// • Runs ONCE immediately to establish the initial dependency set
// • Designed for side effects: logging, localStorage, analytics, DOM
//   manipulation — things that don't return a value
// • Effects must be created in an injection context (constructor, or
//   using runInInjectionContext). In these examples we call effect()
//   standalone for clarity.
// • An effect returns an EffectRef; call .destroy() to stop it.
// =============================================================================

const searchQuery: WritableSignal<string> = signal('');
const isLoading: WritableSignal<boolean> = signal(false);

// ── Basic effect ───────────────────────────────────────────────────────────────
const loggingEffect = effect(() => {
  // This runs whenever searchQuery OR isLoading changes
  console.log(`[Effect] query="${searchQuery()}"  loading=${isLoading()}`);
});

// Changing searchQuery triggers the effect
searchQuery.set('angular');   // logs: query="angular"  loading=false
isLoading.set(true);          // logs: query="angular"  loading=true

// ── Persistence effect (sync to localStorage) ─────────────────────────────────
const theme: WritableSignal<'light' | 'dark'> = signal('light');

const themeEffect = effect(() => {
  const currentTheme = theme();
  // In a browser environment this would call localStorage.setItem(...)
  console.log(`[Theme Effect] Saving theme "${currentTheme}" to localStorage`);
  // localStorage.setItem('preferred-theme', currentTheme);
});

theme.set('dark');  // logs: Saving theme "dark" to localStorage

// ── Effect with cleanup ────────────────────────────────────────────────────────
// Some effects set up timers, subscriptions, or event listeners that need
// to be torn down before the next run. Use the onCleanup() callback.

const timerInterval: WritableSignal<number> = signal(1000);

const cleanupEffect = effect((onCleanup) => {
  const ms = timerInterval();
  console.log(`[Timer Effect] Starting interval every ${ms}ms`);

  // Simulate setting up a timer
  const id = setInterval(() => {
    console.log('[Timer] tick');
  }, ms);

  // onCleanup runs BEFORE the next effect execution AND when destroyed
  onCleanup(() => {
    console.log(`[Timer Effect] Clearing interval ${id}`);
    clearInterval(id);
  });
});

timerInterval.set(500); // triggers cleanup of old interval, starts new one

// ── Destroying an effect ───────────────────────────────────────────────────────
// loggingEffect.destroy(); // stops the effect from ever running again

// ─────────────────────────────────────────────────────────────────────────────
// ANGULAR COMPONENT CONTEXT
// =============================================================================
// Effects are typically created in the constructor:
//
// @Component({ ... })
// export class SearchComponent {
//   query = signal('');
//
//   constructor() {
//     effect(() => {
//       console.log('Query changed:', this.query());
//       // safe — effect is automatically destroyed with the component
//     });
//   }
// }
//
// Angular automatically destroys component effects when the component is
// destroyed. You don't need to manage cleanup manually in most cases.
// =============================================================================

// =============================================================================
// SECTION 5 — Signals vs Observables: Comparison
// =============================================================================
// Both are reactive tools in Angular. Knowing when to use each is key.
// =============================================================================

// ── SIGNALS ─────────────────────────────────────────────────────────────────
//
// ✅ Best for: synchronous, UI-bound state that lives in a component or service
// ✅ Simple API: signal(), computed(), effect() — no operators, no imports
// ✅ No subscription management — no memory leaks from forgotten unsubscribe()
// ✅ Fine-grained change detection — only the exact DOM that depends on the
//    signal is updated, not the entire component tree
// ✅ Value is always available: mySignal() never returns undefined unless typed
// ✅ Works perfectly in Angular templates without async pipe
//
// ❌ Not designed for: streams of multiple values over time (HTTP responses,
//    WebSocket messages, event streams)
// ❌ Limited transformation toolkit (no map/filter/debounce out of the box)

// ── OBSERVABLES ─────────────────────────────────────────────────────────────
//
// ✅ Best for: async operations — HTTP, WebSockets, user events, timers
// ✅ Rich operator library via RxJS (map, filter, switchMap, debounceTime, etc.)
// ✅ Handles streams of 0–∞ values over time
// ✅ Built into Angular's HttpClient, Router, Forms (valueChanges)
// ✅ Lazy — doesn't start until subscribed
//
// ❌ Higher learning curve (operators, marble diagrams, cold vs hot)
// ❌ Subscription management required (takeUntilDestroyed, async pipe, etc.)
// ❌ Observable does NOT hold a current value (you need BehaviorSubject for that)

// ── Side-by-side example ─────────────────────────────────────────────────────
//
// OBSERVABLE APPROACH (traditional):
// ─────────────────────────────────
// @Component({ template: `{{ title$ | async }}` })
// export class TitleComponent implements OnDestroy {
//   title$ = new BehaviorSubject('Angular');
//   private destroy$ = new Subject<void>();
//
//   updateTitle(t: string) { this.title$.next(t); }
//   ngOnDestroy() { this.destroy$.next(); this.destroy$.complete(); }
// }
//
// SIGNAL APPROACH (Angular 16+):
// ─────────────────────────────
// @Component({ template: `{{ title() }}` })
// export class TitleComponent {
//   title = signal('Angular');
//
//   updateTitle(t: string) { this.title.set(t); }
//   // No cleanup needed — signal has no subscription
// }

// ── Bridging: toSignal() and toObservable() ───────────────────────────────────
// Angular provides utilities to convert between the two worlds:
//
// import { toSignal, toObservable } from '@angular/core/rxjs-interop';
//
// // Convert an Observable → Signal (requires injection context)
// const routeId = toSignal(this.route.params.pipe(map(p => p['id'])));
// // Now use routeId() in template — no async pipe needed!
//
// // Convert a Signal → Observable (useful for RxJS pipelines)
// const count$ = toObservable(this.count);
// count$.pipe(debounceTime(300)).subscribe(...)

// ── Decision tree ─────────────────────────────────────────────────────────────
//
// "Is the value synchronous UI state (selection, toggle, count, form value)?"
//   → SIGNAL
//
// "Is the value coming from an async source (HTTP, WebSocket, router, timer)?"
//   → OBSERVABLE (convert to Signal at the edge with toSignal() if needed)
//
// "Do I need RxJS operators like debounceTime, switchMap, combineLatest?"
//   → OBSERVABLE
//
// "Is this a simple derived value (fullName from firstName + lastName)?"
//   → COMPUTED SIGNAL

// =============================================================================
// SUMMARY — Quick Reference
// =============================================================================
// signal(val)         Create a writable reactive value
// mySignal()          Read the value (also registers as dependency)
// mySignal.set(v)     Replace value
// mySignal.update(fn) Transform value (fn receives current, returns new)
// mySignal.asReadonly() Expose as read-only Signal<T>
// computed(() => ...) Derive a read-only value from other signals
// effect(() => ...)   Run a side effect when dependencies change
// toSignal(obs$)      Bridge: Observable → Signal
// toObservable(sig)   Bridge: Signal → Observable
// =============================================================================
