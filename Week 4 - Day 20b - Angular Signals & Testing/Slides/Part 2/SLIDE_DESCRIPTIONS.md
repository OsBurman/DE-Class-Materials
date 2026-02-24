# Day 20b Part 2 — Testing Angular Applications
## Slide Descriptions

**Total slides: 16**

---

### Slide 1 — Title Slide

**Title:** Testing Angular Applications
**Subtitle:** Jasmine · Karma · TestBed · Mocking
**Day:** Week 4 — Day 20b | Part 2 of 2

**Objectives listed on slide:**
- Understand the Angular testing toolchain (Jasmine, Karma, TestBed)
- Write component unit tests with `ComponentFixture`
- Test services — with and without TestBed
- Test HTTP calls with `HttpClientTestingModule`
- Mock dependencies using `useValue`, `useClass`, and Jasmine spies
- Test signal-based components
- Run tests, generate coverage reports, and organize test files

---

### Slide 2 — The Angular Testing Toolchain

**Title:** Three Tools — One Workflow

**Comparison table:**

| Tool | Role | Analogy |
|---|---|---|
| **Jasmine** | Test framework — `describe`, `it`, `expect` syntax | JUnit 5 (Java) |
| **Karma** | Test runner — launches a browser, runs the tests | Maven's `mvn test` |
| **TestBed** | Angular's test harness — creates components with full DI | Spring Boot `@SpringBootTest` |

**How they work together (flow diagram described):**
```
You write → Jasmine spec files (.spec.ts)
           ↓
Karma      → Launches a real (or headless) browser
           ↓
Angular    → TestBed compiles components inside the browser
           ↓
Jasmine    → Runs your describe/it blocks and reports pass/fail
```

**Commands on slide:**
```bash
# Angular CLI sets everything up by default
ng test                   # run tests in watch mode (Karma opens browser)
ng test --watch=false     # run once (for CI pipelines)
ng test --code-coverage   # generate coverage/index.html report
```

---

### Slide 3 — Jasmine Fundamentals

**Title:** Jasmine — Test Syntax Basics

**Code block:**
```typescript
// describe() — groups related tests into a suite
describe('add()', () => {

  // it() — a single test case
  it('adds two positive numbers', () => {
    expect(add(2, 3)).toBe(5);             // assertion
  });

  it('returns 0 for add(0, 0)', () => {
    expect(add(0, 0)).toBe(0);
  });

  it('throws on invalid input', () => {
    expect(() => add('a', 1)).toThrow();   // assert it throws
  });
});
```

**Common matchers reference table:**

| Matcher | Checks |
|---|---|
| `.toBe(value)` | Strict equality (`===`) — primitives |
| `.toEqual(value)` | Deep equality — objects, arrays |
| `.toBeTruthy()` / `.toBeFalsy()` | Truthy / falsy |
| `.toBeNull()` / `.toBeUndefined()` | Null / undefined |
| `.toContain(item)` | Array contains item / string contains substring |
| `.toBeGreaterThan(n)` | Numeric comparison |
| `.toHaveBeenCalled()` | Spy was called at least once |
| `.toHaveBeenCalledWith(...)` | Spy was called with specific args |
| `.toHaveBeenCalledTimes(n)` | Spy was called exactly n times |
| `.toThrow()` / `.toThrowError(msg)` | Function threw an error |

---

### Slide 4 — Setup and Teardown

**Title:** `beforeEach` / `afterEach` — Test Lifecycle

**Code block:**
```typescript
describe('UserService', () => {
  let service: UserService;

  beforeAll(() => {
    // Runs ONCE before all tests in this describe block
    // Use for expensive one-time setup (database seed, etc.)
    console.log('Suite starting');
  });

  beforeEach(() => {
    // Runs before EACH test — always start fresh
    service = new UserService();   // new instance = no shared state
  });

  afterEach(() => {
    // Runs after each test — cleanup
    // e.g., httpMock.verify() — assert no outstanding HTTP requests
  });

  afterAll(() => {
    // Runs ONCE after all tests — final teardown
  });

  it('starts with empty users', () => {
    expect(service.users().length).toBe(0);
  });

  it('adds a user', () => {
    service.addUser({ id: '1', name: 'Alice' });
    expect(service.users().length).toBe(1);
  });
});
```

**Key principle callout:**
> Every test should start with a clean slate. `beforeEach` creates a fresh instance. Tests must be independent — if test B only passes when test A runs first, you have hidden coupling.

---

### Slide 5 — TestBed — Angular's Testing Module

**Title:** `TestBed` — Configuring the Angular DI System for Tests

**Code block:**
```typescript
import { TestBed } from '@angular/core/testing';
import { CounterComponent } from './counter.component';

describe('CounterComponent', () => {

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      // For STANDALONE components (Angular 14+) — use imports:
      imports: [CounterComponent],

      // For MODULE-BASED components — use declarations:
      // declarations: [CounterComponent],

      // Provide mocks instead of real services
      providers: [
        { provide: CounterService, useValue: { count: signal(5) } }
      ]

    }).compileComponents(); // async — compiles templates and CSS
  });

});
```

**`TestBed` vs `@NgModule` side-by-side note:**
> `configureTestingModule` mirrors `@NgModule`. It sets up the Angular DI container for tests. Every `it()` block in the describe gets a fresh module from the `beforeEach`.

**Why `async`/`await`?**
> `compileComponents()` is asynchronous — it fetches external template and style URLs. Always `await` it.

---

### Slide 6 — ComponentFixture — The Component Wrapper

**Title:** `ComponentFixture` — Your Test Handle on the Component

**Code block:**
```typescript
describe('CounterComponent', () => {
  let component: CounterComponent;
  let fixture:   ComponentFixture<CounterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CounterComponent]
    }).compileComponents();

    fixture   = TestBed.createComponent(CounterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();  // triggers ngOnInit + initial render
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
```

**`ComponentFixture<T>` reference table:**

| Property / Method | What it gives you |
|---|---|
| `.componentInstance` | The TypeScript class instance — access properties and methods |
| `.nativeElement` | The root DOM `HTMLElement` — standard DOM queries |
| `.debugElement` | Angular's wrapper — query by directive, CSS, component type |
| `.detectChanges()` | Manually trigger change detection (you're in control in tests) |
| `.whenStable()` | Promise that resolves when async operations complete |
| `.destroy()` | Trigger `ngOnDestroy` — test cleanup lifecycle |

**Key point:**
> In tests, Angular does **not** automatically run change detection. After any state change, call `fixture.detectChanges()` to sync the view before making DOM assertions.

---

### Slide 7 — Writing a Component Test — Full Example

**Title:** Testing a Component End to End

**The component being tested:**
```typescript
@Component({
  selector: 'app-counter',
  standalone: true,
  template: `
    <p data-testid="count">{{ count() }}</p>
    <button data-testid="increment" (click)="increment()">+</button>
    <button data-testid="reset"     (click)="reset()">Reset</button>
  `
})
export class CounterComponent {
  count = signal(0);
  increment() { this.count.update(c => c + 1); }
  reset()     { this.count.set(0); }
}
```

**The test suite:**
```typescript
it('displays initial count of 0', () => {
  const p = fixture.nativeElement.querySelector('[data-testid="count"]');
  expect(p.textContent.trim()).toBe('0');
});

it('increments count on button click', () => {
  const btn = fixture.nativeElement.querySelector('[data-testid="increment"]');
  btn.click();
  fixture.detectChanges();  // sync view after state change

  expect(component.count()).toBe(1);  // test state
  const p = fixture.nativeElement.querySelector('[data-testid="count"]');
  expect(p.textContent.trim()).toBe('1');  // test view
});

it('resets count to 0', () => {
  component.count.set(5);
  fixture.detectChanges();
  fixture.nativeElement.querySelector('[data-testid="reset"]').click();
  fixture.detectChanges();
  expect(component.count()).toBe(0);
});
```

---

### Slide 8 — Querying the DOM in Tests

**Title:** DOM Queries — nativeElement vs debugElement

**nativeElement — standard DOM API:**
```typescript
// querySelector — single match (first found)
const title    = fixture.nativeElement.querySelector('h1');
const submitBtn = fixture.nativeElement.querySelector('[data-testid="submit"]');

// querySelectorAll — all matches (returns NodeList)
const items    = fixture.nativeElement.querySelectorAll('.list-item');

// Reading values
expect(title.textContent).toContain('Dashboard');
expect(submitBtn.disabled).toBeFalse();
expect(items.length).toBe(3);
```

**debugElement — Angular-aware queries:**
```typescript
import { By } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';

// By.css — same as querySelector but returns DebugElement
const card = fixture.debugElement.query(By.css('.card'));

// By.directive — find elements by Angular directive
const links = fixture.debugElement.queryAll(By.directive(RouterLink));
```

**Triggering events manually:**
```typescript
// Simulating text input
const input = fixture.nativeElement.querySelector('input');
input.value = 'Angular Signals';
input.dispatchEvent(new Event('input'));  // triggers (input) binding
fixture.detectChanges();

// Or: triggerEventHandler on debugElement
const inputDE = fixture.debugElement.query(By.css('input'));
inputDE.triggerEventHandler('input', { target: { value: 'Angular Signals' } });
fixture.detectChanges();
```

**Best practice callout:**
> Prefer `data-testid` attributes over CSS class selectors. Classes change for styling reasons — test IDs signal your intent to other developers: "this is queried by tests."

---

### Slide 9 — Testing Services

**Title:** Testing Services — Direct and via TestBed

**Option A: Direct instantiation — no TestBed (fastest, purest unit test):**
```typescript
describe('CalculatorService (direct)', () => {
  let service: CalculatorService;

  beforeEach(() => {
    service = new CalculatorService();  // direct — no DI needed
  });

  it('adds two numbers', () => {
    expect(service.add(2, 3)).toBe(5);
  });

  it('throws on division by zero', () => {
    expect(() => service.divide(10, 0)).toThrowError('Division by zero');
  });
});
```

**Option B: TestBed injection — when service has dependencies:**
```typescript
describe('CartService (TestBed)', () => {
  let service: CartService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CartService]
      // If CartService depends on ProductService, mock it here:
      // { provide: ProductService, useValue: mockProductService }
    });

    // Get the DI-resolved instance — same instance injected everywhere
    service = TestBed.inject(CartService);
  });

  it('starts with an empty cart', () => {
    expect(service.count()).toBe(0);
    expect(service.isEmpty()).toBeTrue();
  });

  it('adds an item', () => {
    service.addItem({ id: '1', name: 'Book', price: 20, qty: 1 });
    expect(service.count()).toBe(1);
    expect(service.total()).toBe(20);
  });
});
```

---

### Slide 10 — Testing HTTP Calls

**Title:** `HttpClientTestingModule` and `HttpTestingController`

**Full example — testing a service that fetches data:**
```typescript
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

describe('ProductService', () => {
  let service:  ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:   [HttpClientTestingModule],
      providers: [ProductService]
    });
    service  = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();  // asserts no unexpected HTTP requests were made
  });

  it('fetches the product list', () => {
    const mockData = [{ id: 1, name: 'Laptop', price: 999 }];

    service.getProducts().subscribe(products => {
      expect(products).toEqual(mockData);      // assert response
      expect(products.length).toBe(1);
    });

    // Assert exactly one request was made
    const req = httpMock.expectOne('/api/products');
    expect(req.request.method).toBe('GET');

    req.flush(mockData);  // deliver the mock response → triggers subscribe callback
  });

  it('handles a 500 error', () => {
    service.getProducts().subscribe({
      next:  ()  => fail('should have errored'),
      error: err => expect(err.status).toBe(500)
    });
    httpMock.expectOne('/api/products').flush('Server error', {
      status: 500, statusText: 'Internal Server Error'
    });
  });
});
```

---

### Slide 11 — Mocking Dependencies — Three Approaches

**Title:** Providing Mocks in TestBed

**Approach 1 — `useValue`: inline mock object (simplest)**
```typescript
providers: [
  {
    provide: AuthService,
    useValue: {
      isLoggedIn: () => true,
      currentUser: signal({ id: '1', name: 'Alice' })
    }
  }
]
// Best when: you only need a few methods and don't need to assert calls
```

**Approach 2 — `jasmine.createSpyObj`: full spy object (most common)**
```typescript
const mockAuthService = jasmine.createSpyObj<AuthService>(
  'AuthService',
  ['isLoggedIn', 'login', 'logout']  // methods to spy on
);
mockAuthService.isLoggedIn.and.returnValue(true);
mockAuthService.login.and.returnValue(of({ token: 'abc' }));

providers: [
  { provide: AuthService, useValue: mockAuthService }
]
// Best when: you need to assert the component called the service correctly
```

**Approach 3 — `useClass`: alternative implementation**
```typescript
class MockAuthService {
  isLoggedIn() { return true; }
  login()      { return of({ token: 'test-token' }); }
  logout()     {}
}

providers: [
  { provide: AuthService, useClass: MockAuthService }
]
// Best when: mock has complex logic or you share it across many test files
```

---

### Slide 12 — Spies in Depth

**Title:** Jasmine Spies — Intercept and Assert

**`spyOn` — wrap an existing object method:**
```typescript
describe('ProductsComponent', () => {
  let productService: ProductService;

  beforeEach(() => {
    // ... TestBed setup ...
    productService = TestBed.inject(ProductService);
  });

  it('loads products on init', () => {
    const spy = spyOn(productService, 'getProducts').and.returnValue(
      of([{ id: 1, name: 'Widget' }])
    );
    fixture.detectChanges();  // triggers ngOnInit

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.products()).toHaveSize(1);
  });
});
```

**Spy return behaviors:**

```typescript
spy.and.returnValue(value)          // always return this value
spy.and.returnValues(a, b, c)       // return a, then b, then c on successive calls
spy.and.callThrough()               // execute the real implementation
spy.and.callFake((arg) => ...)      // custom function replaces the real one
spy.and.throwError('message')       // throw instead of returning
spy.and.rejectWith(new Error())     // reject a Promise
```

**Assertions on spies:**
```typescript
expect(spy).toHaveBeenCalled();
expect(spy).toHaveBeenCalledTimes(2);
expect(spy).toHaveBeenCalledWith('/api/products', { page: 1 });
expect(spy).not.toHaveBeenCalled();  // useful for guard tests
```

---

### Slide 13 — Testing Signal-Based Components

**Title:** Signals in Tests — Natural and Direct

**Signal components test exactly like any other component:**
```typescript
@Component({
  standalone: true,
  template: `
    <p data-testid="count">{{ count() }}</p>
    <button (click)="increment()">+</button>
  `
})
export class CounterComponent {
  count = signal(0);
  increment() { this.count.update(c => c + 1); }
}
```
```typescript
it('increments the signal state', () => {
  // Can set signal directly from the test
  component.count.set(5);
  fixture.detectChanges();

  const p = fixture.nativeElement.querySelector('[data-testid="count"]');
  expect(p.textContent.trim()).toBe('5');

  // Or trigger via the UI
  fixture.nativeElement.querySelector('button').click();
  fixture.detectChanges();
  expect(component.count()).toBe(6);
});
```

**Testing computed signals:**
```typescript
it('computed values update with the signal', () => {
  const doubled = computed(() => component.count() * 2);
  component.count.set(7);
  expect(doubled()).toBe(14);   // computed reads signal synchronously
});
```

**Testing a service with signals:**
```typescript
it('cart total updates when item is added', () => {
  const service = TestBed.inject(CartService);
  expect(service.total()).toBe(0);
  service.addItem({ id: '1', name: 'Book', price: 25, qty: 2 });
  expect(service.total()).toBe(50);  // signal updated synchronously
});
```

---

### Slide 14 — Running Tests and Code Coverage

**Title:** `ng test` — Running, Watching, Reporting

**Commands:**
```bash
# Run in watch mode — Karma opens a browser, re-runs on file save
ng test

# Run once — for CI/CD pipelines
ng test --watch=false

# Run once with headless Chrome — no visible browser (CI-friendly)
ng test --watch=false --browsers=ChromeHeadless

# Generate HTML coverage report in coverage/
ng test --code-coverage

# Run tests matching a file pattern
ng test --include="src/app/features/auth/**/*.spec.ts"
```

**Coverage report — `coverage/index.html`:**

| Column | What it measures |
|---|---|
| **Statements** | Percentage of statements executed |
| **Branches** | Percentage of if/else paths taken |
| **Functions** | Percentage of functions called |
| **Lines** | Percentage of code lines executed |

Color coding: 🔴 Red = 0% | 🟡 Yellow = below threshold | 🟢 Green = at or above threshold

**Setting coverage thresholds — `karma.conf.js`:**
```javascript
coverageReporter: {
  thresholds: {
    global: { statements: 80, branches: 70, functions: 80, lines: 80 }
  }
}
// Tests fail in CI if coverage drops below these thresholds
```

---

### Slide 15 — Test Organization and Best Practices

**Title:** Organizing and Writing Tests Well

**File structure — co-locate tests with source:**
```
src/
└── app/
    ├── features/
    │   ├── auth/
    │   │   ├── auth.service.ts
    │   │   ├── auth.service.spec.ts       ← test next to source
    │   │   ├── login.component.ts
    │   │   └── login.component.spec.ts
    │   └── products/
    │       ├── product.service.ts
    │       ├── product.service.spec.ts
    │       ├── product-list.component.ts
    │       └── product-list.component.spec.ts
    └── shared/
        ├── button.component.ts
        └── button.component.spec.ts
```

**Best practices checklist:**

| Practice | Why |
|---|---|
| One `.spec.ts` per `.ts` file — co-located | Easy to find; same change set |
| `beforeEach` for fresh instance | Tests are independent — no order dependency |
| `afterEach(() => httpMock.verify())` | Catches unexpected HTTP calls |
| `data-testid` attributes for DOM queries | Stable across style changes |
| Test behavior, not implementation | Don't test private methods or internals |
| `fixture.detectChanges()` after every state change | Angular doesn't auto-detect in tests |
| Use `jasmine.createSpyObj` over manual mocks | Less code, cleaner assertions |
| Keep test descriptions human-readable | `it('displays an error when login fails')` not `it('test1')` |

---

### Slide 16 — Part 2 Summary

**Title:** Day 20b Complete — Signals + Testing

**Testing tool reference:**

| Tool / API | Import From | Purpose |
|---|---|---|
| `TestBed` | `@angular/core/testing` | Configure Angular's DI for tests |
| `ComponentFixture<T>` | `@angular/core/testing` | Create, control, and query a component |
| `HttpClientTestingModule` | `@angular/common/http/testing` | Replace real HTTP with mock layer |
| `HttpTestingController` | `@angular/common/http/testing` | Assert and respond to HTTP requests |
| `jasmine.createSpyObj` | Jasmine (global) | Create mock objects with spy methods |
| `spyOn(obj, 'method')` | Jasmine (global) | Wrap existing method with a spy |
| `By.css` / `By.directive` | `@angular/platform-browser` | Query DOM via Angular DebugElement |

**Week 4 Recap — What We've Covered:**
- Day 16b: Angular fundamentals — components, modules, directives
- Day 17b: Services and Dependency Injection
- Day 18b: Routing and Reactive Forms
- Day 19b: HTTP, RxJS — HttpClient, Observables, pipes
- **Day 20b: Signals (reactive state) and Testing**

**Up next — Week 5:** SQL fundamentals, REST API design, Maven/Gradle, and Spring Boot.
