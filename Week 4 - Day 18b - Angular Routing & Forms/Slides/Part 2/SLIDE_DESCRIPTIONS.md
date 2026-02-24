# Day 18b — Part 2: Route Guards, Lazy Loading & Angular Forms
## Slide Descriptions

---

### Slide 1: Title Slide
**"Route Guards, Lazy Loading & Angular Forms"**
Subtitle: CanActivate, CanDeactivate, Lazy Loading, Template-Driven & Reactive Forms
Week 4 – Day 18b, Part 2

---

### Slide 2: Part 2 Overview + Bridge from Part 1

**Quick recap of Part 1:**
- Router setup, route config, `<router-outlet>`
- `routerLink` / `routerLinkActive` / `Router.navigate()`
- Route params via `ActivatedRoute` — snapshot vs Observable
- Query params — URL-based filter/sort state
- Nested routes + inner `<router-outlet>`

**Part 2 topics:**
1. **Route guards** — intercept navigation before the component loads
2. **Lazy loading** — download feature code on demand, not upfront
3. **Template-driven forms** — HTML-centric forms with `ngModel`
4. **Reactive forms** — TypeScript-centric forms with `FormGroup` / `FormControl`
5. **Form validation** — built-in validators + custom `ValidatorFn`

**Bridge from Day 18a:**
- In React, we built a `ProtectedRoute` component that checked auth and rendered `<Navigate>` or `<Outlet>`
- Angular handles the same pattern through **guards** — a function/class that runs **before** the component even instantiates
- Same problem, different mechanism — Angular separates the auth logic from the component

---

### Slide 3: Route Guards — All Types Overview

Angular provides several guard hooks, each covering a different navigation event:

| Guard | Runs When | Primary Use Case |
|---|---|---|
| `CanActivate` | Before entering a route | Is the user authenticated? |
| `CanActivateChild` | Before entering any **child** route | Per-section authorization |
| `CanDeactivate` | Before **leaving** a route | Unsaved changes warning |
| `CanMatch` | Before route is matched (replaces `CanLoad` in v15+) | Don't even match if not authorized |
| `Resolve` | Before route activates, after guard passes | Pre-fetch data before component renders |

**Today's focus:** `CanActivate` (most common) and `CanDeactivate` (essential UX pattern)

**How a guard fits into navigation:**
```
User clicks link / navigate() called
  ↓
Router matches route
  ↓
CanActivate guard runs
  ├── returns true  → component loads ✅
  └── returns false / UrlTree → redirect ❌  (component never loads)
```

Guards run **before** the component is created — unlike React's `ProtectedRoute`, the component is never instantiated if the guard rejects.

---

### Slide 4: CanActivate — Functional Guard (Angular 15+, Recommended)

```typescript
// src/app/core/guards/auth.guard.ts
import { inject }              from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService }           from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);  // inject() works inside functions in Angular 14+
  const router      = inject(Router);

  if (authService.isLoggedIn()) {
    return true;    // ✅ allow navigation
  }

  // ❌ redirect to login, pass the attempted URL as a query param
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};
```

**Applying the guard to a route:**
```typescript
// app.routes.ts
{
  path: 'dashboard',
  component: DashboardComponent,
  canActivate: [authGuard]       // array — multiple guards run in order
},
{
  path: 'admin',
  component: AdminComponent,
  canActivate: [authGuard, adminGuard]   // both must return true
}
```

**Why functional guards?**
- No class boilerplate — just an exported function
- `inject()` gives access to any DI service without a constructor
- Angular team's recommended approach going forward

---

### Slide 5: CanActivate — Class-Based Guard (Traditional, Still Valid)

```typescript
// auth.guard.ts — class-based (pre-Angular 15 pattern, still works)
import { Injectable }            from '@angular/core';
import { CanActivate,
         ActivatedRouteSnapshot,
         RouterStateSnapshot,
         Router, UrlTree }       from '@angular/router';
import { AuthService }           from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree {
    if (this.authService.isLoggedIn()) {
      return true;
    }
    return this.router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }
}
```

**Use in route:**
```typescript
{ path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] }
```

**Comparison:**
| | Functional Guard | Class-Based Guard |
|---|---|---|
| Syntax | Plain exported function | Class implementing `CanActivate` |
| DI access | `inject()` | Constructor injection |
| Angular version | 14+ | All versions |
| Recommended | ✅ Yes (new code) | For existing codebases |

---

### Slide 6: CanDeactivate — Unsaved Changes Warning

```typescript
// can-deactivate.guard.ts

// Interface defines what the component must implement
export interface HasUnsavedChanges {
  hasUnsavedChanges(): boolean;
}

// Functional guard — generic over the component type
export const unsavedChangesGuard: CanDeactivateFn<HasUnsavedChanges> =
  (component) => {
    if (component.hasUnsavedChanges()) {
      return confirm('You have unsaved changes. Are you sure you want to leave?');
      // confirm() returns true (stay) or false (leave) — blocks navigation
    }
    return true;  // no unsaved changes → allow navigation
  };
```

**Component implements the interface:**
```typescript
@Component({ ... })
export class EditProfileComponent implements HasUnsavedChanges {
  isDirty = false;    // set to true whenever form value changes

  hasUnsavedChanges(): boolean {
    return this.isDirty;
  }

  onFormChange(): void {
    this.isDirty = true;
  }

  onSave(): void {
    this.profileService.save(this.formData);
    this.isDirty = false;   // reset — guard won't trigger now
  }
}
```

**Route configuration:**
```typescript
{ path: 'profile/edit', component: EditProfileComponent,
  canDeactivate: [unsavedChangesGuard] }
```

**Real-world improvement:** Replace `confirm()` with a custom dialog component that returns an `Observable<boolean>` — `CanDeactivateFn` can return `boolean | Observable<boolean> | Promise<boolean>`.

---

### Slide 7: Lazy Loading — The Problem

**Without lazy loading — single bundle:**
```
main.js ─────────────────────────────── 800KB
  ├── HomeComponent
  ├── ProductsComponent
  ├── CartComponent
  ├── DashboardComponent
  ├── AdminComponent         ← most users never visit admin
  ├── ReportsComponent       ← downloaded even if never accessed
  └── all supporting code

User experience:
  → Open app → wait for 800KB → see home page
```

**With lazy loading — split bundles:**
```
main.js ──────────────────────────────── 120KB  (downloaded on load)
  ├── HomeComponent
  ├── ProductsComponent
  └── routing shell

admin.chunk.js ──────────────────────── 200KB  (downloaded only if user visits /admin)
dashboard.chunk.js ──────────────────── 150KB  (downloaded only if user visits /dashboard)

User experience:
  → Open app → wait for 120KB → see home page  (67% faster!)
  → Navigate to /admin → download admin.chunk.js (only if needed)
```

**The tradeoff:**
- First route load: slightly slower (downloads that chunk on demand)
- Overall: massive improvement for initial load time and users who never visit certain sections

---

### Slide 8: Lazy Loading — Implementation

**NgModule-based lazy loading (traditional):**
```typescript
// app.routes.ts
const routes: Routes = [
  { path: 'home',     component: HomeComponent },      // eager — small, needed immediately
  { path: 'products', component: ProductsComponent },  // eager

  // Lazy loaded NgModule
  {
    path: 'admin',
    canActivate: [authGuard],        // guard runs BEFORE the download happens
    loadChildren: () =>
      import('./features/admin/admin.module')   // dynamic import
        .then(m => m.AdminModule)
  },
];

// admin.module.ts — has its own RouterModule.forChild()
@NgModule({
  imports: [
    RouterModule.forChild([
      { path: '',        component: AdminDashboardComponent },
      { path: 'users',   component: UserManagementComponent },
      { path: 'reports', component: ReportsComponent },
    ])
  ]
})
export class AdminModule {}
```

**Standalone component lazy loading (Angular 14+, simpler):**
```typescript
{
  path: 'dashboard',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/dashboard.component')
      .then(m => m.DashboardComponent)
},

// Lazy load a group of standalone routes
{
  path: 'admin',
  loadChildren: () =>
    import('./features/admin/admin.routes')
      .then(m => m.ADMIN_ROUTES)  // exports a Routes array
}
```

---

### Slide 9: Template-Driven Forms — Setup

**What template-driven forms are:**
- Form logic defined **in the HTML template** using Angular directives
- Two-way binding with `[(ngModel)]` keeps component data in sync with inputs
- Angular automatically creates a `FormGroup` under the hood for named controls
- Simpler for small/basic forms; validation defined via HTML attributes

**Setup — import FormsModule:**
```typescript
// app.module.ts
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [FormsModule]     // enables ngModel, ngForm, ngModelGroup
})
```

**Basic login form:**
```html
<!-- login.component.html -->
<form #loginForm="ngForm" (ngSubmit)="onSubmit(loginForm)">

  <label for="email">Email</label>
  <input
    id="email"
    type="email"
    name="email"                    ← REQUIRED — ngForm tracks by name
    [(ngModel)]="credentials.email" ← two-way binding
    required                        ← HTML validator (triggers Angular validation)
    email                           ← Angular email format validator
    #emailInput="ngModel"           ← template reference to this control
  />

  <button type="submit" [disabled]="loginForm.invalid">Login</button>
</form>
```

**`name` attribute is mandatory** — `ngForm` uses it to register the control in the form group.

---

### Slide 10: Template-Driven Forms — Validation and State

**Displaying validation errors:**
```html
<input
  type="email"
  name="email"
  [(ngModel)]="credentials.email"
  required
  email
  #emailInput="ngModel"
/>

<!-- Show error only after user has touched the field (not on initial load) -->
<div *ngIf="emailInput.invalid && emailInput.touched">
  <span *ngIf="emailInput.errors?.['required']">Email is required.</span>
  <span *ngIf="emailInput.errors?.['email']">Please enter a valid email.</span>
</div>
```

**Component class:**
```typescript
export class LoginComponent {
  credentials = { email: '', password: '' };

  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.authService.login(this.credentials);
    }
  }
}
```

**Form and control state properties:**
| Property | `true` when... |
|---|---|
| `valid` / `invalid` | All validators pass / any fails |
| `touched` / `untouched` | Field has been blurred / never focused |
| `dirty` / `pristine` | Value has changed / never changed |
| `errors` | Object with error keys (e.g. `{ required: true }`) |

Angular automatically adds CSS classes: `ng-valid`, `ng-invalid`, `ng-touched`, `ng-dirty` — great for styling.

---

### Slide 11: Reactive Forms — FormGroup and FormControl

**What reactive forms are:**
- Form model defined **in TypeScript** — the template just binds to it
- Explicit `FormGroup` with named `FormControl` instances
- Synchronous access to form value and validity at any time
- Easier to test (no DOM needed for logic), better for complex/dynamic forms

**Setup — import ReactiveFormsModule:**
```typescript
import { ReactiveFormsModule } from '@angular/forms';
@NgModule({ imports: [ReactiveFormsModule] })
```

**Component class — manual FormGroup:**
```typescript
import { Component, OnInit }           from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({ ... })
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;

  ngOnInit(): void {
    this.registerForm = new FormGroup({
      name:     new FormControl('',  [Validators.required, Validators.minLength(2)]),
      email:    new FormControl('',  [Validators.required, Validators.email]),
      password: new FormControl('',  [Validators.required, Validators.minLength(8)]),
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      console.log(this.registerForm.value);  // { name: '...', email: '...', password: '...' }
      this.authService.register(this.registerForm.value);
    }
  }
}
```

---

### Slide 12: Reactive Forms — FormBuilder Shorthand

**FormBuilder** is an injectable service that reduces boilerplate:

```typescript
import { Component }           from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

@Component({ ... })
export class RegisterComponent {
  // Can initialize inline — FormBuilder is available via DI
  registerForm = this.fb.group({
    name:     ['',  [Validators.required, Validators.minLength(2)]],
    email:    ['',  [Validators.required, Validators.email]],
    password: ['',  [Validators.required, Validators.minLength(8)]],
    confirm:  ['',  Validators.required],
  }, {
    validators: passwordMatchValidator   // group-level validator (cross-field)
  });

  constructor(private fb: FormBuilder) {}
}
```

**Equivalence:**
```typescript
// These are identical results:
new FormGroup({ name: new FormControl('', Validators.required) })
this.fb.group({ name: ['', Validators.required] })

// FormBuilder also provides:
this.fb.control('')           // single FormControl
this.fb.array([])             // FormArray (for dynamic lists of controls)
```

**FormArray example (dynamic form fields):**
```typescript
this.fb.group({
  skills: this.fb.array([
    this.fb.control('JavaScript'),
    this.fb.control('TypeScript'),
  ])
})
// Access: form.get('skills') as FormArray
// Add:    (form.get('skills') as FormArray).push(this.fb.control(''))
```

---

### Slide 13: Reactive Forms — Template Binding

```html
<!-- register.component.html -->
<form [formGroup]="registerForm" (ngSubmit)="onSubmit()">

  <!-- name field -->
  <label>Name</label>
  <input type="text" formControlName="name" />
  <div *ngIf="registerForm.get('name')?.invalid && registerForm.get('name')?.touched">
    <span *ngIf="registerForm.get('name')?.errors?.['required']">
      Name is required.
    </span>
    <span *ngIf="registerForm.get('name')?.errors?.['minlength']">
      Minimum {{ registerForm.get('name')?.errors?.['minlength'].requiredLength }} characters.
    </span>
  </div>

  <!-- email field -->
  <label>Email</label>
  <input type="email" formControlName="email" />
  <div *ngIf="registerForm.get('email')?.invalid && registerForm.get('email')?.touched">
    <span *ngIf="registerForm.get('email')?.errors?.['required']">Email required.</span>
    <span *ngIf="registerForm.get('email')?.errors?.['email']">Invalid email format.</span>
  </div>

  <!-- group-level error (cross-field) -->
  <div *ngIf="registerForm.errors?.['passwordMismatch'] && registerForm.touched">
    Passwords do not match.
  </div>

  <button type="submit" [disabled]="registerForm.invalid">Register</button>
</form>
```

**Key directives:**
- `[formGroup]="registerForm"` — binds `<form>` to the TypeScript `FormGroup`
- `formControlName="name"` — binds an input to a named `FormControl` in the group
- **No `[(ngModel)]`** in reactive forms — the `FormGroup` drives the state

---

### Slide 14: Built-In Validators

```typescript
import { Validators } from '@angular/forms';

// Commonly used validators:
Validators.required              // not null, not empty string
Validators.requiredTrue          // must be exactly true (checkboxes)
Validators.email                 // valid email format
Validators.minLength(n)          // string length >= n
Validators.maxLength(n)          // string length <= n
Validators.min(n)                // numeric value >= n
Validators.max(n)                // numeric value <= n
Validators.pattern(regex)        // must match the regex

// Usage:
new FormControl('', [
  Validators.required,
  Validators.minLength(8),
  Validators.pattern(/^(?=.*[A-Z])(?=.*\d).+$/)  // password complexity
])
```

**Error object structure (what `control.errors` returns):**
```typescript
// After failed validation:
control.errors?.['required']        // → true
control.errors?.['email']           // → true
control.errors?.['minlength']       // → { requiredLength: 8, actualLength: 5 }
control.errors?.['maxlength']       // → { requiredLength: 20, actualLength: 25 }
control.errors?.['min']             // → { min: 0, actual: -5 }
control.errors?.['pattern']         // → { requiredPattern: '...', actualValue: '...' }

// When control is valid:
control.errors   // → null
```

---

### Slide 15: Custom Validators

**Field-level custom validator (`ValidatorFn`):**
```typescript
import { AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

// Factory function — returns a ValidatorFn
export function noSpacesValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const hasSpaces = (control.value as string)?.includes(' ');
    return hasSpaces
      ? { noSpaces: { value: control.value } }  // return error object
      : null;                                    // null = VALID
  };
}

// Usage
new FormControl('', [Validators.required, noSpacesValidator()])
```

**Group-level cross-field validator:**
```typescript
// Validate two fields against each other
export function passwordMatchValidator(
  group: AbstractControl
): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirm  = group.get('confirm')?.value;

  return password === confirm
    ? null
    : { passwordMismatch: true };
}

// Applied at FormGroup level:
this.fb.group(
  { password: ['', Validators.required], confirm: ['', Validators.required] },
  { validators: passwordMatchValidator }   // ← group-level
);

// Accessing in template:
<span *ngIf="form.errors?.['passwordMismatch']">Passwords do not match.</span>
```

**Custom async validator (for uniqueness checks):**
```typescript
// AsyncValidatorFn — returns Observable<ValidationErrors | null>
export function emailTakenValidator(authService: AuthService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    return authService.isEmailTaken(control.value).pipe(
      map(taken => taken ? { emailTaken: true } : null),
      catchError(() => of(null))
    );
  };
}
// Usage: new FormControl('', [Validators.email], [emailTakenValidator(this.authService)])
//                              ↑ sync             ↑ async (third argument)
```

---

### Slide 16: Template-Driven vs Reactive — Comparison

| | Template-Driven | Reactive |
|---|---|---|
| **Module** | `FormsModule` | `ReactiveFormsModule` |
| **Form model lives in** | HTML template | TypeScript class |
| **Binding** | `[(ngModel)]` two-way | `formControlName` one-way |
| **Validators** | HTML attributes (`required`, `email`) | `Validators.*` in TypeScript |
| **Access form** | `#loginForm="ngForm"` template ref | `this.myForm.get('field')` |
| **Dynamic fields** | Difficult | Easy with `FormArray` |
| **Unit testing** | Requires DOM / TestBed | Pure TypeScript — no DOM |
| **Type safety** | Limited | Full (with `FormControl<string>`) |
| **Best for** | Simple contact/login forms | Complex forms, dynamic lists, multi-step |

**When to use which:**
- **Template-driven** → Quick prototypes, simple 2–4 field forms, login/contact
- **Reactive** → Registration forms, multi-step wizards, dynamic field lists, any form you test

**Both are valid Angular** — many real projects use template-driven for simple forms and reactive for complex ones. The Angular team does not deprecate either approach.

---

### Slide 17: Day 18b Summary + Looking Ahead

**Part 1 Recap:**
- `RouterModule.forRoot()` / `provideRouter()` — set up the router
- Routes array: `{ path, component, children, redirectTo }` — wildcard `**` goes last
- `<router-outlet>` — component placeholder; inner outlets for nested routes
- `routerLink` + `routerLinkActive` — no page reloads
- `Router.navigate([])` — programmatic navigation; `replaceUrl: true` for auth flows
- `ActivatedRoute` — read params (`snapshot` vs Observable) and query params
- Nested routes — `children` array + child component's own `<router-outlet>`

**Part 2 Recap:**
- `CanActivate` — blocks navigation before component loads; return `true` or `UrlTree`
- Functional guards with `inject()` — recommended modern pattern
- `CanDeactivate` — warns before leaving; component implements `hasUnsavedChanges()`
- Lazy loading — `loadChildren` / `loadComponent` splits bundles; guard runs before download
- Template-driven forms — `FormsModule`, `[(ngModel)]`, HTML validators, `ngForm` state
- Reactive forms — `FormGroup`, `FormControl`, `FormBuilder`, `formControlName`
- Built-in validators — `Validators.required`, `.email`, `.minLength(n)`, etc.
- Custom validators — `ValidatorFn` for field-level; group-level for cross-field rules

**Coming up — Day 19b: Angular HTTP & RxJS:**
- `HttpClient` for GET, POST, PUT, DELETE requests
- Error handling with `catchError` and `throwError`
- Today's `paramMap.subscribe()` is RxJS — Day 19b goes deep on operators
- Interceptors — inject auth headers into every outgoing request automatically
- `BehaviorSubject` for shared state in services
- `async` pipe — subscribe in the template, auto-unsubscribe on destroy
