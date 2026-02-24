// =============================================================================
// Day 20b — Part 2 | File 1: Component Tests with Jasmine & Karma
// =============================================================================
// Topics covered:
//   1. Jasmine test structure: describe, it, expect, matchers
//   2. TestBed — Angular's test module for component testing
//   3. ComponentFixture — wrapper giving access to component + DOM
//   4. DebugElement — querying the rendered template
//   5. Change detection in tests (fixture.detectChanges())
//   6. Testing @Input and @Output bindings
//   7. Testing user interaction (click, input, form submit)
//   8. Async testing (fakeAsync / tick, waitForAsync)
//   9. Testing a component that uses Signals
// =============================================================================
// HOW TO RUN:
//   ng test                       → runs all spec files in watch mode
//   ng test --no-watch --browsers=ChromeHeadless   → single run (CI)
// =============================================================================

import { ComponentFixture, TestBed, fakeAsync, tick, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { signal, computed } from '@angular/core';
import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';

// =============================================================================
// ── COMPONENTS UNDER TEST (defined here for self-contained demo) ─────────────
// =============================================================================
// In a real project these would be in separate .ts files and imported here.
// =============================================================================

// ── Simple greeting component ─────────────────────────────────────────────────
@Component({
  standalone: true,
  selector: 'app-greeting',
  template: `
    <h1 data-testid="heading">Hello, {{ name }}!</h1>
    <p data-testid="subtitle">Welcome to {{ appTitle }}</p>
  `,
})
export class GreetingComponent {
  @Input() name: string = 'Student';
  appTitle = 'Angular Testing Demo';
}

// ── Counter component (with Output) ──────────────────────────────────────────
@Component({
  standalone: true,
  selector: 'app-counter',
  template: `
    <p data-testid="count">Count: {{ count }}</p>
    <button data-testid="increment-btn" (click)="increment()">+</button>
    <button data-testid="decrement-btn" (click)="decrement()">-</button>
    <button data-testid="reset-btn"     (click)="reset()">Reset</button>
  `,
})
export class CounterComponent {
  count = 0;
  @Output() countChanged = new EventEmitter<number>();

  increment() {
    this.count++;
    this.countChanged.emit(this.count);
  }

  decrement() {
    if (this.count > 0) this.count--;
    this.countChanged.emit(this.count);
  }

  reset() {
    this.count = 0;
    this.countChanged.emit(this.count);
  }
}

// ── Course search component (async, with form) ────────────────────────────────
@Component({
  standalone: true,
  imports: [FormsModule],
  selector: 'app-course-search',
  template: `
    <input
      data-testid="search-input"
      [(ngModel)]="query"
      placeholder="Search courses..."
    />
    <button data-testid="search-btn" (click)="search()">Search</button>
    <p data-testid="result-count">Results: {{ resultCount }}</p>
  `,
})
export class CourseSearchComponent {
  query = '';
  resultCount = 0;

  search() {
    // Simulate a delay (would normally call a service)
    setTimeout(() => {
      this.resultCount = this.query.length > 0 ? 5 : 0;
    }, 200);
  }
}

// ── Signal-based cart component ────────────────────────────────────────────────
@Component({
  standalone: true,
  selector: 'app-cart',
  template: `
    <p data-testid="item-count">Items: {{ itemCount() }}</p>
    <p data-testid="total">Total: ${{ total() }}</p>
    <button data-testid="add-btn" (click)="addItem()">Add Item</button>
    <button data-testid="clear-btn" (click)="clearCart()">Clear</button>
  `,
})
export class CartComponent {
  private items = signal<{ name: string; price: number }[]>([]);

  itemCount = computed(() => this.items().length);
  total = computed(() =>
    this.items().reduce((sum, item) => sum + item.price, 0).toFixed(2)
  );

  addItem() {
    this.items.update((list) => [...list, { name: 'Course', price: 29.99 }]);
  }

  clearCart() {
    this.items.set([]);
  }
}

// =============================================================================
// SECTION 1 — Jasmine Test Structure
// =============================================================================
// describe()  → groups related tests (a "test suite")
// it()        → defines a single test ("spec")
// expect()    → creates an assertion
// Matchers:   toBe, toEqual, toBeTruthy, toBeFalsy, toContain,
//             toBeGreaterThan, toHaveBeenCalled, toHaveBeenCalledWith, ...
// beforeEach()/ afterEach() → setup/teardown per test
// beforeAll() / afterAll()  → setup/teardown per suite
// =============================================================================

describe('Jasmine Basics — matchers reference', () => {
  it('should demonstrate common matchers', () => {
    // Strict equality (===) — use for primitives
    expect(2 + 2).toBe(4);
    expect('angular').toBe('angular');

    // Deep equality — use for objects and arrays
    expect({ id: 1, name: 'React' }).toEqual({ id: 1, name: 'React' });
    expect([1, 2, 3]).toEqual([1, 2, 3]);

    // Truthiness
    expect('hello').toBeTruthy();
    expect(0).toBeFalsy();
    expect(null).toBeFalsy();
    expect([]).toBeTruthy(); // ← watch out: empty array is truthy!

    // Defined / undefined
    expect('value').toBeDefined();
    expect(undefined).toBeUndefined();
    expect(null).toBeNull();

    // Numbers
    expect(10).toBeGreaterThan(5);
    expect(10).toBeLessThanOrEqual(10);
    expect(3.14159).toBeCloseTo(3.14, 2);

    // Strings and arrays
    expect('Angular Signals').toContain('Signals');
    expect([1, 2, 3]).toContain(2);

    // Negation — prefix any matcher with .not
    expect('Angular').not.toBe('React');
    expect([]).not.toContain(99);
  });

  it('should use beforeEach for shared setup', () => {
    // beforeEach runs before EVERY it() in this describe block
    // See real usage in component describe blocks below
    expect(true).toBe(true);
  });
});

// =============================================================================
// SECTION 2 — TestBed and ComponentFixture
// =============================================================================
// TestBed is Angular's testing module factory. It creates a mini-Angular
// environment for your test. You configure it like an NgModule.
//
// TestBed.configureTestingModule({ declarations/imports, providers })
//   → sets up the test module
//
// TestBed.createComponent(MyComponent)
//   → instantiates the component in the test environment
//   → returns a ComponentFixture<MyComponent>
//
// fixture.componentInstance  → the component class instance
// fixture.nativeElement      → the raw DOM element (<div> etc.)
// fixture.debugElement       → DebugElement wrapper (preferred — framework-aware)
// fixture.detectChanges()    → triggers change detection (MUST call after setup
//                              and after any state change you want to see)
// =============================================================================

describe('GreetingComponent', () => {
  let fixture: ComponentFixture<GreetingComponent>;
  let component: GreetingComponent;

  // ── TestBed setup ─────────────────────────────────────────────────────────
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // Standalone components go in 'imports', not 'declarations'
      imports: [GreetingComponent],
    }).compileComponents(); // compiles template and styles

    fixture = TestBed.createComponent(GreetingComponent);
    component = fixture.componentInstance;

    // IMPORTANT: detectChanges() triggers the initial change detection pass.
    // Without this, the template is NOT rendered yet.
    fixture.detectChanges();
  });

  // ── Basic creation test ────────────────────────────────────────────────────
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  // ── Testing text content in the DOM ───────────────────────────────────────
  it('should render default name in the heading', () => {
    // Query by CSS attribute selector (data-testid is recommended over class/id)
    const heading: HTMLElement = fixture.debugElement
      .query(By.css('[data-testid="heading"]'))
      .nativeElement;

    expect(heading.textContent).toContain('Hello, Student!');
  });

  // ── Testing @Input binding ────────────────────────────────────────────────
  it('should display the provided name', () => {
    // Set the Input property on the component BEFORE detectChanges
    component.name = 'Alice';
    fixture.detectChanges(); // push the change to the template

    const heading: HTMLElement = fixture.debugElement
      .query(By.css('[data-testid="heading"]'))
      .nativeElement;

    expect(heading.textContent).toContain('Hello, Alice!');
  });

  it('should display app title in subtitle', () => {
    const subtitle = fixture.debugElement
      .query(By.css('[data-testid="subtitle"]'))
      .nativeElement as HTMLElement;

    expect(subtitle.textContent).toContain('Angular Testing Demo');
  });
});

// =============================================================================
// SECTION 3 — Testing User Interaction and @Output
// =============================================================================
// Clicking DOM elements:
//   debugElement.query(By.css('button')).triggerEventHandler('click', null)
//   — OR —
//   nativeElement.click()
//
// Testing @Output:
//   Spy on the EventEmitter or subscribe to it
// =============================================================================

describe('CounterComponent', () => {
  let fixture: ComponentFixture<CounterComponent>;
  let component: CounterComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CounterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CounterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should start at count 0', () => {
    const countEl = fixture.debugElement
      .query(By.css('[data-testid="count"]'))
      .nativeElement as HTMLElement;

    expect(countEl.textContent).toContain('Count: 0');
  });

  it('should increment count when + button is clicked', () => {
    // Find the button and click it
    const btn = fixture.debugElement.query(By.css('[data-testid="increment-btn"]'));
    btn.triggerEventHandler('click', null); // fire the click event

    // After a DOM change you MUST call detectChanges to sync template
    fixture.detectChanges();

    expect(component.count).toBe(1);

    const countEl = fixture.debugElement
      .query(By.css('[data-testid="count"]'))
      .nativeElement as HTMLElement;
    expect(countEl.textContent).toContain('Count: 1');
  });

  it('should not go below 0 when decrement is clicked at zero', () => {
    const btn = fixture.debugElement.query(By.css('[data-testid="decrement-btn"]'));
    btn.triggerEventHandler('click', null);
    fixture.detectChanges();

    expect(component.count).toBe(0); // stays at 0
  });

  it('should reset to 0 when reset button is clicked', () => {
    component.count = 5;
    fixture.detectChanges();

    const resetBtn = fixture.debugElement.query(By.css('[data-testid="reset-btn"]'));
    resetBtn.triggerEventHandler('click', null);
    fixture.detectChanges();

    expect(component.count).toBe(0);
  });

  // ── Testing @Output ────────────────────────────────────────────────────────
  it('should emit the new count when increment is clicked', () => {
    // Spy on the EventEmitter — jasmine.createSpy creates a tracking function
    const emitSpy = spyOn(component.countChanged, 'emit');

    const btn = fixture.debugElement.query(By.css('[data-testid="increment-btn"]'));
    btn.triggerEventHandler('click', null);

    // Verify emit was called with the correct value
    expect(emitSpy).toHaveBeenCalledWith(1);
  });

  it('should emit 0 when reset is clicked', () => {
    component.count = 5;
    const emitSpy = spyOn(component.countChanged, 'emit');

    fixture.debugElement
      .query(By.css('[data-testid="reset-btn"]'))
      .triggerEventHandler('click', null);

    expect(emitSpy).toHaveBeenCalledWith(0);
  });
});

// =============================================================================
// SECTION 4 — Async Testing: fakeAsync / tick and waitForAsync
// =============================================================================
// Angular often involves async operations. Two strategies:
//
// fakeAsync(fn) + tick(ms):
//   • Wraps the test in a synthetic time zone
//   • tick(ms) fast-forwards virtual time by ms milliseconds
//   • Best for: setTimeout, setInterval, Promises that resolve synchronously
//
// waitForAsync(fn):
//   • Returns a real async test — runs until the Zone is stable
//   • Best for: real Promises, HttpClient, observable streams
// =============================================================================

describe('CourseSearchComponent — async tests', () => {
  let fixture: ComponentFixture<CourseSearchComponent>;
  let component: CourseSearchComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseSearchComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── fakeAsync + tick ───────────────────────────────────────────────────────
  it('should show results after search (fakeAsync)', fakeAsync(() => {
    // Set the input value
    component.query = 'angular';
    fixture.detectChanges();

    // Click search button
    fixture.debugElement
      .query(By.css('[data-testid="search-btn"]'))
      .triggerEventHandler('click', null);

    // The search() method uses setTimeout(200ms). Advance virtual time.
    tick(200);

    // Now the callback has run — sync the template
    fixture.detectChanges();

    const resultEl = fixture.debugElement
      .query(By.css('[data-testid="result-count"]'))
      .nativeElement as HTMLElement;

    expect(resultEl.textContent).toContain('Results: 5');
  }));

  it('should show 0 results when query is empty (fakeAsync)', fakeAsync(() => {
    component.query = '';
    fixture.detectChanges();

    fixture.debugElement
      .query(By.css('[data-testid="search-btn"]'))
      .triggerEventHandler('click', null);

    tick(200);
    fixture.detectChanges();

    const resultEl = fixture.debugElement
      .query(By.css('[data-testid="result-count"]'))
      .nativeElement as HTMLElement;

    expect(resultEl.textContent).toContain('Results: 0');
  }));
});

// =============================================================================
// SECTION 5 — Testing Signal-Based Components
// =============================================================================
// Signal components are tested identically to class-field components.
// detectChanges() still works. Computed and effect re-computation is
// synchronous so no special async handling is needed.
// =============================================================================

describe('CartComponent — Signal-based', () => {
  let fixture: ComponentFixture<CartComponent>;
  let component: CartComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CartComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should start with 0 items and $0.00 total', () => {
    const itemCount = fixture.debugElement
      .query(By.css('[data-testid="item-count"]'))
      .nativeElement as HTMLElement;
    const total = fixture.debugElement
      .query(By.css('[data-testid="total"]'))
      .nativeElement as HTMLElement;

    expect(itemCount.textContent).toContain('Items: 0');
    expect(total.textContent).toContain('Total: $0.00');
  });

  it('should add an item when Add Item is clicked', () => {
    fixture.debugElement
      .query(By.css('[data-testid="add-btn"]'))
      .triggerEventHandler('click', null);
    fixture.detectChanges(); // signals update synchronously, but template needs detectChanges

    const itemCount = fixture.debugElement
      .query(By.css('[data-testid="item-count"]'))
      .nativeElement as HTMLElement;

    expect(itemCount.textContent).toContain('Items: 1');
  });

  it('should compute total correctly after multiple adds', () => {
    const addBtn = fixture.debugElement.query(By.css('[data-testid="add-btn"]'));

    addBtn.triggerEventHandler('click', null); // adds 29.99
    addBtn.triggerEventHandler('click', null); // adds 29.99
    addBtn.triggerEventHandler('click', null); // adds 29.99
    fixture.detectChanges();

    const total = fixture.debugElement
      .query(By.css('[data-testid="total"]'))
      .nativeElement as HTMLElement;

    expect(total.textContent).toContain('Total: $89.97');
    expect(component.itemCount()).toBe(3);
  });

  it('should clear the cart when Clear is clicked', () => {
    // Add items first
    fixture.debugElement
      .query(By.css('[data-testid="add-btn"]'))
      .triggerEventHandler('click', null);
    fixture.debugElement
      .query(By.css('[data-testid="add-btn"]'))
      .triggerEventHandler('click', null);
    fixture.detectChanges();

    // Now clear
    fixture.debugElement
      .query(By.css('[data-testid="clear-btn"]'))
      .triggerEventHandler('click', null);
    fixture.detectChanges();

    expect(component.itemCount()).toBe(0);
    expect(component.total()).toBe('0.00');
  });

  // ── Directly reading signal values in tests ────────────────────────────────
  it('should be able to read signal values directly on the component', () => {
    // You can call signals directly in test assertions — no DOM needed
    expect(component.itemCount()).toBe(0);
    expect(component.total()).toBe('0.00');

    component.addItem();

    expect(component.itemCount()).toBe(1);
    expect(component.total()).toBe('29.99');
  });
});
