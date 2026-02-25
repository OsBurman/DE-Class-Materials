// Day 20b Part 2 — Angular Testing: TestBed, Component Tests, Service Tests, Mocking
// Run: npm install && npm start  (to see the app)
// Run tests: npm test  (to run Jasmine/Karma tests)

import { Component, Injectable, Input, Output, EventEmitter, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// ─────────────────────────────────────────────────────────────────────────────
// Testable Services
// ─────────────────────────────────────────────────────────────────────────────

@Injectable({ providedIn: 'root' })
export class MathService {
  add(a: number, b: number): number        { return a + b; }
  multiply(a: number, b: number): number   { return a * b; }
  factorial(n: number): number {
    if (n < 0)  throw new Error('Negative input');
    if (n === 0) return 1;
    return n * this.factorial(n - 1);
  }
  isPrime(n: number): boolean {
    if (n < 2) return false;
    for (let i = 2; i <= Math.sqrt(n); i++) {
      if (n % i === 0) return false;
    }
    return true;
  }
}

@Injectable({ providedIn: 'root' })
export class GreetingService {
  greet(name: string): string {
    if (!name.trim()) throw new Error('Name cannot be empty');
    return `Hello, ${name}!`;
  }
  farewell(name: string): string { return `Goodbye, ${name}!`; }
}

// ─────────────────────────────────────────────────────────────────────────────
// Testable Components
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-counter',
  standalone: true,
  template: `
    <div>
      <span data-testid="count">{{ count }}</span>
      <button (click)="count = count - 1" aria-label="Decrement">−</button>
      <button (click)="count = count + 1" aria-label="Increment">+</button>
      <button (click)="count = 0"         aria-label="Reset">Reset</button>
    </div>`
})
export class CounterComponent {
  @Input() initialCount = 0;
  count = 0;
  ngOnInit() { this.count = this.initialCount; }
}

@Component({
  selector: 'app-greeting',
  standalone: true,
  template: `
    <div>
      <p data-testid="greeting">{{ greeting }}</p>
      <p *ngIf="!name" data-testid="fallback">Please provide a name</p>
    </div>`
})
export class GreetingComponent {
  @Input() name = '';
  @Input() greeting = '';
}

// ─────────────────────────────────────────────────────────────────────────────
// Root Component: shows testable components + testing reference
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, CounterComponent, GreetingComponent],
  styles: [`
    * { box-sizing: border-box; }
    .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; font-family: -apple-system, sans-serif; }
    .header { background: #dd0031; color: white; padding: 1.5rem 2rem; border-radius: 8px; margin-bottom: 2rem; }
    .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
    h2 { color: #dd0031; margin-bottom: 1rem; padding-bottom: .4rem; border-bottom: 2px solid #ff6d00; }
    .code { background: #1e1e1e; color: #d4e157; padding: 1rem; border-radius: 6px; font-size: .82rem; white-space: pre; overflow: auto; margin: .5rem 0; }
    .btn { background: #dd0031; color: white; border: none; padding: .4rem .9rem; border-radius: 4px; cursor: pointer; margin: .2rem; }
    table { width: 100%; border-collapse: collapse; font-size: .9rem; }
    th { background: #dd0031; color: white; padding: .5rem; text-align: left; }
    td { padding: .45rem; border-bottom: 1px solid #f0f0f0; }
  `],
  template: `
<div class="page">
  <div class="header">
    <h1>🅰️ Day 20b Part 2 — Angular Testing</h1>
    <p style="opacity:.85">Jasmine · Karma · TestBed · Component &amp; Service Tests</p>
  </div>

  <!-- Live components -->
  <div class="card">
    <h2>Live Components (tested via spec files)</h2>
    <p style="color:#555;font-size:.85rem;margin-bottom:1rem">Run <code>npm test</code> to execute the Jasmine/Karma test suite.</p>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem">
      <div style="background:#f9f9f9;padding:1rem;border-radius:6px">
        <strong>CounterComponent</strong>
        <app-counter [initialCount]="5"></app-counter>
      </div>
      <div style="background:#f9f9f9;padding:1rem;border-radius:6px">
        <strong>GreetingComponent</strong>
        <app-greeting name="Alice" greeting="Hello, Alice!"></app-greeting>
        <app-greeting name="" greeting=""></app-greeting>
      </div>
    </div>
  </div>

  <!-- Testing anatomy -->
  <div class="card">
    <h2>1. Service Test Anatomy (Jasmine)</h2>
    <div class="code">describe('MathService', () => {
  let service: MathService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MathService);
  });

  it('should add two numbers', () => {
    expect(service.add(2, 3)).toBe(5);         // toBe — strict equality
  });

  it('should throw for negative factorial', () => {
    expect(() => service.factorial(-1))
      .toThrowError('Negative input');
  });

  it('should identify primes', () => {
    expect(service.isPrime(7)).toBeTrue();
    expect(service.isPrime(4)).toBeFalse();
  });
});</div>
  </div>

  <!-- Component test anatomy -->
  <div class="card">
    <h2>2. Component Test Anatomy (TestBed)</h2>
    <div class="code">describe('CounterComponent', () => {
  let fixture: ComponentFixture&lt;CounterComponent&gt;;
  let component: CounterComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CounterComponent],   // standalone components go in imports
    }).compileComponents();

    fixture = TestBed.createComponent(CounterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();         // trigger initial change detection
  });

  it('should render initial count', () => {
    const el = fixture.debugElement.query(By.css('[data-testid="count"]'));
    expect(el.nativeElement.textContent).toBe('0');
  });

  it('should increment on button click', () => {
    const btn = fixture.debugElement.query(By.css('[aria-label="Increment"]'));
    btn.nativeElement.click();
    fixture.detectChanges();
    expect(component.count).toBe(1);
  });
});</div>
  </div>

  <!-- Mocking -->
  <div class="card">
    <h2>3. Mocking Services</h2>
    <div class="code">// When a component depends on a service, mock it in tests:
const mockGreetingService = {
  greet: jasmine.createSpy('greet').and.returnValue('Mocked Hello!')
};

TestBed.configureTestingModule({
  imports: [GreetingComponent],
  providers: [
    { provide: GreetingService, useValue: mockGreetingService }
  ]
});

// Then verify calls:
expect(mockGreetingService.greet).toHaveBeenCalledWith('Alice');
expect(mockGreetingService.greet).toHaveBeenCalledTimes(1);</div>
  </div>

  <!-- Jasmine matchers -->
  <div class="card">
    <h2>4. Common Jasmine Matchers</h2>
    <table>
      <tr><th>Matcher</th><th>Use case</th></tr>
      <tr *ngFor="let m of matchers">
        <td><code>{{ m.name }}</code></td>
        <td style="color:#555">{{ m.use }}</td>
      </tr>
    </table>
  </div>
</div>
  `
})
export class AppComponent {
  matchers = [
    { name: 'toBe(val)',               use: 'Strict equality (===)' },
    { name: 'toEqual(obj)',            use: 'Deep equality for objects/arrays' },
    { name: 'toBeTruthy() / toBeFalsy()', use: 'Truthy/falsy checks' },
    { name: 'toBeNull() / toBeDefined()', use: 'Null or defined checks' },
    { name: 'toContain(item)',         use: 'Array or string contains' },
    { name: 'toThrowError(msg)',       use: 'Function throws an error' },
    { name: 'toHaveBeenCalled()',      use: 'Spy was called at least once' },
    { name: 'toHaveBeenCalledWith()',  use: 'Spy was called with specific args' },
    { name: 'toBeGreaterThan(n)',      use: 'Numeric comparison' },
  ];
}
