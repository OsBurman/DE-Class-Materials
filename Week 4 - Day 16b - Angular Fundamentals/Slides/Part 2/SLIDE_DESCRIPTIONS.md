# Week 4 - Day 16b: Angular Fundamentals
## Part 2 Slide Descriptions — Data Binding, Lifecycle, NgModule & Directives

**Total slides:** 17
**Duration:** 60 minutes
**Part 2 Topics:** Data binding (interpolation, property, event, two-way), template reference variables, component lifecycle hooks, Modules and NgModule, directives basics (*ngIf, *ngFor)

---

### Slide 1 — Part 2 Title Slide

**Layout:** Same Angular red-on-dark styling as Part 1.
**Title:** Angular Fundamentals — Part 2
**Subtitle:** Data Binding, Lifecycle Hooks, NgModule & Directives
**Visual:** A diagram of Angular's two-way data binding flow: Component ↔ Template with arrows in both directions.
**Footer:** "Building on Part 1's component foundation — now wiring components to the DOM."

---

### Slide 2 — The Four Types of Data Binding

**Title:** Angular Data Binding — Four Directions, One System

**Angular's data binding system connects component class properties to the DOM template in four ways:**

```
COMPONENT CLASS    ────────────────────────────────   DOM / TEMPLATE
                   
     {{ value }}   ──────── Interpolation ────────→   "Hello, Alice"
                            (one-way out)

      [property]   ──────── Property Binding ──────→   DOM property = value
                            (one-way out)

      (event)      ←──────── Event Binding ──────────  user action fires
                            (one-way in)

    [(ngModel)]    ←──────── Two-Way Binding ────────→  sync both ways
                            (bidirectional)
```

**Summary table:**

| Syntax | Direction | Use Case |
|---|---|---|
| `{{ expression }}` | Class → Template | Display text content |
| `[property]="expression"` | Class → Template | Set DOM/component properties |
| `(event)="handler()"` | Template → Class | Respond to user events |
| `[(ngModel)]="property"` | Class ↔ Template | Form inputs, two-way sync |

**The "banana in a box" mnemonic:** `[(ngModel)]` — the parentheses `()` look like a banana, the brackets `[]` look like a box. Banana in a box = two-way binding. Angular developers actually use this mnemonic — you'll hear it in teams.

---

### Slide 3 — Property Binding `[property]="expression"`

**Title:** Property Binding — Dynamically Setting DOM Properties

**Property binding sets a DOM element's property to the value of a component expression:**

```typescript
// Component:
@Component({ selector: 'app-demo', templateUrl: '...' })
export class DemoComponent {
  imageUrl: string   = '/images/logo.png';
  altText: string    = 'Company logo';
  isDisabled: boolean = false;
  inputValue: string = 'initial value';
  userId: number     = 42;
}
```

```html
<!-- Template: -->

<!-- DOM element property binding -->
<img [src]="imageUrl" [alt]="altText" />
<button [disabled]="isDisabled">Submit</button>
<input [value]="inputValue" />

<!-- Custom component property binding (@Input — Day 17b) -->
<app-user-card [userId]="userId"></app-user-card>

<!-- Class binding — adds/removes CSS class based on boolean -->
<div [class.active]="isActive">...</div>
<div [class.error]="hasError">...</div>

<!-- Style binding — sets a style property dynamically -->
<p [style.color]="textColor">Styled text</p>
<p [style.fontSize.px]="fontSize">Big or small?</p>

<!-- Attribute binding — for ARIA and non-DOM attributes -->
<button [attr.aria-label]="buttonLabel">Click</button>
<td [attr.colspan]="columnCount">...</td>
```

**Property vs Attribute — why it matters:**
```html
<!-- [src] binds to the DOM property (correct) -->
<img [src]="imageUrl" />

<!-- src="{{imageUrl}}" — interpolation in attribute (works but deprecated) -->
<img src="{{ imageUrl }}" />  ← avoid for non-string values

<!-- [disabled] — boolean DOM property -->
<button [disabled]="true">Disabled</button>     ← disabled
<button [disabled]="false">Enabled</button>     ← NOT disabled
<button disabled="false">Still disabled!</button> ← HTML attribute, always disabled!
```
The distinction matters: HTML attributes are always strings; DOM properties can be any type. Property binding works with the DOM property layer, not the HTML attribute layer.

---

### Slide 4 — Event Binding `(event)="handler()"`

**Title:** Event Binding — Responding to User Actions

**Event binding listens for DOM events and calls a component method:**

```typescript
// Component:
export class FormDemoComponent {
  clickCount: number = 0;
  inputText: string = '';
  mouseX: number = 0;
  mouseY: number = 0;

  handleClick(): void {
    this.clickCount++;
  }

  handleInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.inputText = target.value;
  }

  handleKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.submitForm();
    }
  }

  handleMouseMove(event: MouseEvent): void {
    this.mouseX = event.clientX;
    this.mouseY = event.clientY;
  }

  submitForm(): void {
    console.log('Submitted:', this.inputText);
  }
}
```

```html
<!-- Template: -->

<!-- Click event -->
<button (click)="handleClick()">
  Clicked {{ clickCount }} times
</button>

<!-- Input event with $event -->
<input (input)="handleInput($event)" placeholder="Type here..." />
<p>You typed: {{ inputText }}</p>

<!-- Keyboard event -->
<input (keydown)="handleKeyDown($event)" placeholder="Press Enter to submit" />

<!-- Mouse event -->
<div (mousemove)="handleMouseMove($event)">
  Mouse: {{ mouseX }}, {{ mouseY }}
</div>

<!-- Inline expression — for very simple cases -->
<button (click)="clickCount = clickCount + 1">Quick increment</button>

<!-- Prevent default browser behavior -->
<a href="/old-route" (click)="navigate($event)">Navigate</a>
<!-- In component: navigate(event: Event) { event.preventDefault(); ... } -->
```

**`$event` is the DOM event object** — its type depends on the event. `click` → `MouseEvent`, `keydown` → `KeyboardEvent`, `input` → `Event`, `submit` → `SubmitEvent`. TypeScript types these correctly.

---

### Slide 5 — Two-Way Binding `[(ngModel)]`

**Title:** Two-Way Binding — The Banana in a Box

**Two-way binding synchronizes a component property with a form input element — changes in either direction update both:**

**Step 1 — Import `FormsModule` in `AppModule`:**
```typescript
// app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';  // ← Required for ngModel
import { AppComponent } from './app.component';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    FormsModule   // ← Add this
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

**Step 2 — Use `[(ngModel)]` in the template:**
```typescript
// Component:
export class LoginComponent {
  username: string = '';
  password: string = '';
  rememberMe: boolean = false;

  onSubmit(): void {
    console.log('Submitting:', this.username);
  }
}
```

```html
<!-- Template: -->
<form (submit)="onSubmit()">
  <div>
    <label for="username">Username</label>
    <input id="username" type="text" [(ngModel)]="username" name="username" />
    <p>Live preview: {{ username }}</p>
  </div>

  <div>
    <label for="password">Password</label>
    <input id="password" type="password" [(ngModel)]="password" name="password" />
  </div>

  <div>
    <input id="remember" type="checkbox" [(ngModel)]="rememberMe" name="rememberMe" />
    <label for="remember">Remember me</label>
    <p>Remember me: {{ rememberMe }}</p>
  </div>

  <button type="submit">Log In</button>
</form>
```

**How `[(ngModel)]` works internally:**
`[(ngModel)]="username"` is syntactic sugar for:
```html
<input [ngModel]="username" (ngModelChange)="username = $event" />
```
The `[ngModel]` property binding sends the value to the input; the `(ngModelChange)` event binding updates the property when the user types. Angular's `FormsModule` provides the `ngModel` directive that wires these together.

**Note:** Reactive Forms — an alternative, more powerful approach — are covered on Day 18b.

---

### Slide 6 — Template Reference Variables

**Title:** Template Reference Variables — `#ref`

**A template reference variable creates a named reference to a DOM element or component within the template:**

```html
<!-- #refName declares a template reference variable -->

<!-- Reference to a DOM element -->
<input #nameInput type="text" placeholder="Enter name" />
<button (click)="greet(nameInput.value)">Greet</button>
<!-- nameInput.value = the current value of the input -->

<!-- Reference to a component instance -->
<app-user-card #userCard></app-user-card>
<button (click)="userCard.somePublicMethod()">Call Method</button>

<!-- Multiple reference variables in a template -->
<form #loginForm="ngForm" (submit)="onSubmit(loginForm)">
  <input #emailInput type="email" name="email" [(ngModel)]="email" required />
  <p *ngIf="emailInput.value.length > 0">
    Email entered: {{ emailInput.value }}
  </p>
  <button type="submit" [disabled]="!loginForm.valid">Submit</button>
</form>
```

**Common use cases:**
```html
<!-- 1. Read input value without ngModel -->
<input #searchBox type="text" />
<button (click)="search(searchBox.value)">Search</button>

<!-- 2. Auto-focus an element on button click -->
<input #focusMe type="text" />
<button (click)="focusMe.focus()">Focus the input</button>

<!-- 3. Reference a component's public API -->
<app-video-player #player></app-video-player>
<button (click)="player.play()">Play</button>
<button (click)="player.pause()">Pause</button>
```

**Access in the component class with `@ViewChild` (brief mention — Day 17b covers in depth):**
```typescript
import { ViewChild, ElementRef } from '@angular/core';

export class FormComponent {
  @ViewChild('nameInput') nameInput!: ElementRef;

  focusInput(): void {
    this.nameInput.nativeElement.focus();
  }
}
```

---

### Slide 7 — NgModule: Angular's Module System

**Title:** NgModule — Organizing an Angular Application

**Angular applications are organized into modules.** A module is a container that groups related components, directives, pipes, and services.

**The `@NgModule` decorator:**
```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { UserCardComponent } from './user-card/user-card.component';
import { ProductListComponent } from './product-list/product-list.component';
import { HeaderComponent } from './header/header.component';

@NgModule({
  declarations: [
    // Components, directives, and pipes that BELONG to this module
    AppComponent,
    UserCardComponent,
    ProductListComponent,
    HeaderComponent,
  ],
  imports: [
    // Other modules whose exported things this module needs
    BrowserModule,    // provides CommonModule (*ngIf, *ngFor), BrowserAnimationsModule, etc.
    FormsModule,      // provides ngModel (two-way binding)
  ],
  exports: [
    // Things this module makes available to other modules that import it
    UserCardComponent,
    HeaderComponent,
  ],
  providers: [
    // Services registered at the module level (Day 17b)
  ],
  bootstrap: [
    // The root component to bootstrap (only in the root AppModule)
    AppComponent,
  ]
})
export class AppModule { }
```

**The four main NgModule properties explained:**

| Property | Purpose | Rule |
|---|---|---|
| `declarations` | Which components/directives/pipes this module owns | A component can only be declared in ONE module |
| `imports` | What other modules this module needs | Must import `BrowserModule` in the root module |
| `exports` | What this module makes public | Only export what other modules need |
| `providers` | Services to provide (inject) | Most services use `providedIn: 'root'` instead |

---

### Slide 8 — Feature Modules and the Module Hierarchy

**Title:** Scaling with Feature Modules

**For larger applications, Angular recommends splitting functionality into feature modules:**

```
AppModule (root)
├── imports: [BrowserModule, FormsModule, RouterModule, UserModule, ProductModule]
├── declarations: [AppComponent]
└── bootstrap: [AppComponent]

UserModule (feature module)
├── imports: [CommonModule, FormsModule]
├── declarations: [UserListComponent, UserCardComponent, UserProfileComponent]
└── exports: [UserCardComponent]   ← only share what's needed

ProductModule (feature module)
├── imports: [CommonModule]
├── declarations: [ProductGridComponent, ProductCardComponent]
└── exports: [ProductGridComponent]
```

**`BrowserModule` vs `CommonModule`:**
```typescript
// Root module (AppModule) — use BrowserModule
imports: [BrowserModule]    // includes CommonModule + browser-specific setup

// Feature modules — use CommonModule (NOT BrowserModule)
imports: [CommonModule]     // provides *ngIf, *ngFor, ngClass, ngStyle, async pipe
// BrowserModule should only be imported ONCE — in the root module
```

**Standalone Components (Angular 14+) — the modern alternative:**
```typescript
@Component({
  selector: 'app-user-card',
  standalone: true,               // No NgModule needed
  imports: [CommonModule],        // Import what this component needs directly
  templateUrl: './user-card.component.html',
  styleUrls: ['./user-card.component.css']
})
export class UserCardComponent { }
```
Angular 17+ defaults to standalone components in `ng new`. NgModules are still the primary pattern in existing large codebases — you'll work with both.

---

### Slide 9 — Component Lifecycle: The Full Picture

**Title:** Component Lifecycle Hooks — Responding to Angular's Component Events

**Angular manages the lifecycle of every component and directive. Lifecycle hooks are methods you implement to respond to key moments in that lifecycle:**

```
Component created
       ↓
ngOnChanges()         ← Called when @Input properties change (before ngOnInit on first change)
       ↓
ngOnInit()            ← Called ONCE after first ngOnChanges; component fully initialized
       ↓
ngDoCheck()           ← Called on every change detection cycle (use sparingly)
       ↓
ngAfterContentInit()  ← Called after Angular projects content into the component (ng-content)
       ↓
ngAfterContentChecked() ← After every check of projected content
       ↓
ngAfterViewInit()     ← Called after the component's view and child views are initialized
       ↓
ngAfterViewChecked()  ← After every check of the component's view
       ↓
[Repeat ngDoCheck, ngAfterContentChecked, ngAfterViewChecked on each change detection cycle]
       ↓
ngOnChanges()         ← Called again when @Input properties change
       ↓
ngOnDestroy()         ← Called just before Angular destroys the component — cleanup here
       ↓
Component destroyed
```

**The hooks you'll use 90% of the time:**
- **`ngOnInit`** — initialization logic (most common)
- **`ngOnDestroy`** — cleanup (subscriptions, timers, event listeners)
- **`ngOnChanges`** — respond to input property changes
- **`ngAfterViewInit`** — access child components / DOM after view renders

**TypeScript interface enforcement:**
```typescript
// Angular provides interfaces for each hook — implement them for type safety
export class MyComponent implements OnInit, OnDestroy, AfterViewInit {
  ngOnInit(): void { }
  ngOnDestroy(): void { }
  ngAfterViewInit(): void { }
}
```

---

### Slide 10 — ngOnInit: The Most Used Lifecycle Hook

**Title:** `ngOnInit` — Initialization Logic

**`ngOnInit()` is called once, after Angular has initialized all data-bound properties of a directive.** It is the standard place for initialization logic.

```typescript
import { Component, OnInit } from '@angular/core';

interface Product {
  id: number;
  name: string;
  price: number;
}

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
})
export class ProductListComponent implements OnInit {

  products: Product[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  // ✅ ngOnInit — initialization goes here
  ngOnInit(): void {
    this.loadProducts();
    document.title = 'Product List';  // Example of side effect in init
  }

  loadProducts(): void {
    // Simulating a data load (in real apps, this calls a service — Day 17b)
    this.products = [
      { id: 1, name: 'Laptop',   price: 999 },
      { id: 2, name: 'Mouse',    price: 29  },
      { id: 3, name: 'Keyboard', price: 79  },
    ];
    this.isLoading = false;
  }
}
```

**`constructor` vs `ngOnInit` — a critical distinction:**

| | `constructor` | `ngOnInit` |
|---|---|---|
| **Purpose** | Dependency injection, field initialization | Initialization logic |
| **Timing** | Before Angular processes bindings | After Angular processes bindings |
| **@Input available?** | ❌ No — inputs not yet bound | ✅ Yes — inputs are bound |
| **Services available?** | ✅ Yes — DI runs in constructor | ✅ Yes |
| **Use for** | `private service = inject(...)` | `this.loadData()`, `this.setupForm()` |

**Rule:** Constructors should only contain dependency injection. All initialization that requires data or inputs belongs in `ngOnInit`.

---

### Slide 11 — ngOnDestroy: Cleanup

**Title:** `ngOnDestroy` — Preventing Memory Leaks

**`ngOnDestroy()` is called just before Angular destroys the component.** This is where you clean up to prevent memory leaks.

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription, interval } from 'rxjs';

@Component({
  selector: 'app-timer',
  template: `<p>Elapsed: {{ elapsed }}s</p>`
})
export class TimerComponent implements OnInit, OnDestroy {
  elapsed: number = 0;
  private timerSubscription!: Subscription;

  ngOnInit(): void {
    // Start a timer that ticks every second
    this.timerSubscription = interval(1000).subscribe(() => {
      this.elapsed++;
    });
  }

  ngOnDestroy(): void {
    // ✅ Unsubscribe to prevent the interval running after component is gone
    this.timerSubscription.unsubscribe();
  }
}
```

**What to clean up in `ngOnDestroy`:**

| What | Why |
|---|---|
| RxJS subscriptions (Day 19b) | Subscriptions keep component in memory even after destruction |
| `setInterval` / `setTimeout` | Timers continue running after component is gone |
| DOM event listeners added manually | `document.addEventListener` leaks if not removed |
| WebSocket connections | Connections should close when component is gone |

**The takeUntilDestroyed operator (Angular 16+):**
```typescript
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

export class MyComponent {
  private destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    interval(1000)
      .pipe(takeUntilDestroyed(this.destroyRef))  // auto-unsubscribes on destroy
      .subscribe(n => console.log(n));
  }
  // No ngOnDestroy needed — takeUntilDestroyed handles it
}
```

---

### Slide 12 — ngOnChanges and ngAfterViewInit

**Title:** `ngOnChanges` and `ngAfterViewInit`

**`ngOnChanges` — responding to @Input property changes:**
```typescript
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-score-display',
  template: `
    <div>
      <p>Current score: {{ score }}</p>
      <p *ngIf="changeMessage">{{ changeMessage }}</p>
    </div>
  `
})
export class ScoreDisplayComponent implements OnChanges {
  @Input() score: number = 0;   // @Input covered in depth on Day 17b
  changeMessage: string = '';

  ngOnChanges(changes: SimpleChanges): void {
    // 'changes' is an object keyed by input property name
    if (changes['score']) {
      const { previousValue, currentValue, firstChange } = changes['score'];
      if (firstChange) {
        this.changeMessage = `Initial score: ${currentValue}`;
      } else {
        const delta = currentValue - previousValue;
        this.changeMessage = delta > 0
          ? `Score up by ${delta}!`
          : `Score down by ${Math.abs(delta)}`;
      }
    }
  }
}
```

**`ngAfterViewInit` — access to child components and DOM:**
```typescript
import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-canvas-demo',
  template: `<canvas #myCanvas width="400" height="300"></canvas>`
})
export class CanvasDemoComponent implements AfterViewInit {
  @ViewChild('myCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  ngAfterViewInit(): void {
    // The canvas element is now in the DOM and accessible
    const ctx = this.canvasRef.nativeElement.getContext('2d');
    if (ctx) {
      ctx.fillStyle = '#e74c3c';
      ctx.fillRect(50, 50, 100, 100);
    }
  }
}
```

**Why `ngAfterViewInit` for DOM access?** During `ngOnInit`, the component's view hasn't rendered yet — `@ViewChild` references are `undefined`. `ngAfterViewInit` fires after the view is fully rendered, making DOM access safe.

---

### Slide 13 — Structural Directives: `*ngIf`

**Title:** `*ngIf` — Conditional Rendering

**`*ngIf` conditionally adds or removes an element from the DOM based on a truthy/falsy expression:**

```typescript
// Component:
export class DashboardComponent {
  isLoggedIn: boolean = true;
  userRole: string = 'admin';
  isLoading: boolean = false;
  error: string | null = null;
  items: string[] = [];
}
```

```html
<!-- Basic *ngIf -->
<div *ngIf="isLoggedIn">
  <h2>Welcome back!</h2>
</div>

<!-- *ngIf with else clause -->
<div *ngIf="isLoggedIn; else notLoggedIn">
  <h2>Dashboard</h2>
</div>
<ng-template #notLoggedIn>
  <p>Please log in to continue.</p>
</ng-template>

<!-- *ngIf with then and else -->
<div *ngIf="isLoading; then loadingTpl; else contentTpl"></div>
<ng-template #loadingTpl>
  <app-spinner></app-spinner>
</ng-template>
<ng-template #contentTpl>
  <app-dashboard-content></app-dashboard-content>
</ng-template>

<!-- *ngIf with ngIf-as (expose the value) -->
<div *ngIf="currentUser$ | async as user">
  <h2>Hello, {{ user.name }}</h2>  ← 'user' is the resolved value
</div>
```

**`*ngIf` vs CSS `display: none` — an important distinction:**

| `*ngIf="false"` | `[style.display]="'none'"` / `hidden` |
|---|---|
| Element **removed from DOM** | Element **stays in DOM** (hidden) |
| Component **destroyed** (ngOnDestroy runs) | Component **stays alive** |
| No memory or performance overhead | Component still active, still subscribed |
| Use when: element is conditionally needed | Use when: quick toggle, keeping state |

**The `*` prefix:** The asterisk in `*ngIf` is shorthand for `ng-template`. Angular expands `*ngIf="condition"` to:
```html
<ng-template [ngIf]="condition">
  <div>...</div>
</ng-template>
```

---

### Slide 14 — Structural Directives: `*ngFor`

**Title:** `*ngFor` — Rendering Lists

**`*ngFor` repeats a template for each item in a collection:**

```typescript
// Component:
interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

export class UserListComponent {
  users: User[] = [
    { id: 1, name: 'Alice Chen',  email: 'alice@example.com', role: 'admin' },
    { id: 2, name: 'Bob Martin',  email: 'bob@example.com',   role: 'user'  },
    { id: 3, name: 'Carol Davis', email: 'carol@example.com', role: 'user'  },
  ];
}
```

```html
<!-- Basic *ngFor -->
<ul>
  <li *ngFor="let user of users">
    {{ user.name }} — {{ user.email }}
  </li>
</ul>

<!-- *ngFor with local variables -->
<div *ngFor="let user of users; let i = index; let isFirst = first; let isLast = last; let isEven = even">
  <span>{{ i + 1 }}.</span>           <!-- 1-based index -->
  <span>{{ user.name }}</span>
  <span *ngIf="isFirst"> 🏆 First</span>
  <span *ngIf="isLast"> 🏁 Last</span>
  <div [class.striped]="isEven">...</div>
</div>
```

**`*ngFor` local variables:**

| Variable | Type | Description |
|---|---|---|
| `index` | `number` | Zero-based index of current item |
| `first` | `boolean` | True for the first item |
| `last` | `boolean` | True for the last item |
| `even` | `boolean` | True for even-indexed items |
| `odd` | `boolean` | True for odd-indexed items |
| `count` | `number` | Total number of items in the collection |

---

### Slide 15 — `*ngFor` with `trackBy` for Performance

**Title:** `*ngFor` Performance — `trackBy`

**The `trackBy` function tells Angular how to identify each item in the list, so it can re-use DOM nodes instead of destroying and recreating them:**

```typescript
// Component:
export class UserListComponent {
  users: User[] = [/* ... */];

  // trackBy function: returns a unique identifier per item
  trackByUserId(index: number, user: User): number {
    return user.id;
  }
}
```

```html
<!-- Without trackBy: Angular recreates ALL elements when the array reference changes -->
<div *ngFor="let user of users">{{ user.name }}</div>

<!-- With trackBy: Angular only updates the changed items -->
<div *ngFor="let user of users; trackBy: trackByUserId">
  {{ user.name }}
</div>
```

**Why trackBy matters:**
```
Without trackBy — array updated from API:
  Old: [Alice, Bob, Carol]
  New: [Alice, Bob, Carol, Dana]
  → Angular destroys and rebuilds ALL four DOM elements
  → Any open dropdowns/state inside those elements is lost

With trackBy — same update:
  Angular compares by user.id
  → Alice (id:1): same → reuse DOM node
  → Bob   (id:2): same → reuse DOM node
  → Carol (id:3): same → reuse DOM node
  → Dana  (id:4): new → create ONE new DOM node
```

**New Angular 17+ control flow syntax (alternative to `*ngFor`):**
```html
<!-- Angular 17+ @for with track (required, more explicit than trackBy) -->
@for (user of users; track user.id) {
  <div>{{ user.name }}</div>
} @empty {
  <p>No users found.</p>
}
```
The `@for` block with `track` is the modern syntax (Angular 17+) and is required to specify tracking. `*ngFor` remains supported. The syllabus uses `*ngFor` — learn both, as you'll see both in the industry.

---

### Slide 16 — Attribute Directives: `ngClass` and `ngStyle`

**Title:** Attribute Directives — `ngClass` and `ngStyle`

**Attribute directives change the appearance or behavior of existing DOM elements. They don't add or remove elements — they modify them.**

**`ngClass` — conditionally apply CSS classes:**
```typescript
// Component:
export class StatusComponent {
  status: string = 'active';
  isHighlighted: boolean = true;
  errorCount: number = 3;
  user = { role: 'admin', isOnline: true };
}
```

```html
<!-- Object syntax: { 'class-name': condition } -->
<div [ngClass]="{ 'active': status === 'active', 'inactive': status !== 'active' }">
  Status
</div>

<!-- Array syntax: apply multiple classes -->
<div [ngClass]="['base-class', isHighlighted ? 'highlighted' : '', 'another-class']">
  Element
</div>

<!-- String syntax (just a class name) -->
<div [ngClass]="status">Dynamic class from string</div>

<!-- Combining static and dynamic classes -->
<div class="card" [ngClass]="{ 'card--error': errorCount > 0, 'card--success': errorCount === 0 }">
  Card content
</div>
```

**`ngStyle` — conditionally apply inline styles:**
```html
<!-- Object syntax: { 'style-property': value } -->
<div [ngStyle]="{
  'color': user.role === 'admin' ? '#e74c3c' : '#333',
  'font-weight': user.isOnline ? 'bold' : 'normal',
  'opacity': user.isOnline ? '1' : '0.6'
}">
  {{ user.role }}
</div>

<!-- Prefer [class.x] and [style.x] for single conditions: -->
<div [class.highlighted]="isHighlighted">Better for single class</div>
<p [style.color]="textColor">Better for single style</p>

<!-- ngStyle is better when you need multiple dynamic styles at once -->
```

**When to use which:**

| Approach | Best For |
|---|---|
| `[class.name]="condition"` | Single CSS class toggle |
| `[ngClass]="{ ... }"` | Multiple CSS class toggles |
| `[style.property]="value"` | Single style property |
| `[ngStyle]="{ ... }"` | Multiple style properties |

---

### Slide 17 — Day 16b Summary

**Title:** Day 16b Complete — Angular Fundamentals

**Learning Objectives — Status:**

| LO | Topic | Covered |
|---|---|---|
| LO1 | Set up Angular projects using CLI | ✅ Part 1 — Slide 4 |
| LO2 | Create components with proper structure | ✅ Part 1 — Slides 7–9, 13 |
| LO3 | Implement data binding techniques | ✅ Part 2 — Slides 2–5 |
| LO4 | Use structural directives for dynamic templates | ✅ Part 2 — Slides 13–15 |
| LO5 | Understand component lifecycle | ✅ Part 2 — Slides 9–12 |

**Core concepts mastered today:**

| Concept | Key Rule |
|---|---|
| Angular | Full TypeScript framework; batteries included |
| CLI | `ng new` → project; `ng g c` → component; `ng serve` → dev server |
| @Component | decorator with `selector`, `templateUrl`, `styleUrls`; PascalCase class, kebab-case selector |
| Template expressions | `{{ }}` for display; limited JS (no assignments, no statements, no globals) |
| Property binding | `[property]="expression"` — class → DOM (one-way out) |
| Event binding | `(event)="handler($event)"` — DOM → class (one-way in) |
| Two-way binding | `[(ngModel)]` — requires `FormsModule`; the "banana in a box" |
| Template ref vars | `#refName` — reference DOM elements or components in the template |
| NgModule | `declarations` (owns), `imports` (needs), `exports` (shares), `bootstrap` (root only) |
| Lifecycle hooks | `ngOnInit` (init logic), `ngOnDestroy` (cleanup), `ngOnChanges` (input changes), `ngAfterViewInit` (DOM ready) |
| `*ngIf` | Adds/removes from DOM; component destroyed; `else` clause with `ng-template` |
| `*ngFor` | Repeats template per item; `trackBy` for performance; local vars `index`, `first`, `last` |
| `ngClass` / `ngStyle` | Conditional CSS classes and styles |

**Week 4 Angular track ahead (Day 17b tomorrow):**

| Day | Topics |
|---|---|
| **Day 17b (tomorrow)** | `@Input`/`@Output` for component communication; directives in depth; pipes (built-in + custom); services and Dependency Injection |
| **Day 18b** | Angular Router and navigation; route guards; lazy loading; template-driven and reactive forms |
| **Day 19b** | HttpClient for API calls; RxJS Observables and operators; error handling |
| **Day 20b** | Angular Signals; testing with Jasmine and Karma |
