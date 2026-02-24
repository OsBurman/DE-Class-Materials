# Day 18b — Angular Routing & Forms: Complete Reference

**Topics:** Angular Router, RouterModule, Route Parameters, Query Parameters, Nested Routes, CanActivate, CanActivateChild, CanDeactivate, Lazy Loading, Template-Driven Forms, Reactive Forms, FormBuilder, FormArray, Form Validation

---

## Table of Contents
1. [Router Setup](#1-router-setup)
2. [Route Configuration](#2-route-configuration)
3. [RouterOutlet](#3-routeroutlet)
4. [RouterLink and RouterLinkActive](#4-routerlink-and-routerlinkactive)
5. [Programmatic Navigation](#5-programmatic-navigation)
6. [Route Parameters](#6-route-parameters)
7. [Query Parameters](#7-query-parameters)
8. [Nested Routes](#8-nested-routes)
9. [Route Guards — CanActivate](#9-route-guards--canactivate)
10. [Route Guards — CanDeactivate](#10-route-guards--candeactivate)
11. [Lazy Loading](#11-lazy-loading)
12. [Template-Driven Forms](#12-template-driven-forms)
13. [Reactive Forms — FormGroup and FormControl](#13-reactive-forms--formgroup-and-formcontrol)
14. [FormBuilder](#14-formbuilder)
15. [FormArray — Dynamic Form Controls](#15-formarray--dynamic-form-controls)
16. [Built-In Validators](#16-built-in-validators)
17. [Custom Validators](#17-custom-validators)
18. [Template-Driven vs Reactive Comparison](#18-template-driven-vs-reactive-comparison)
19. [Common Mistakes & Fixes](#19-common-mistakes--fixes)
20. [Quick Reference Syntax](#20-quick-reference-syntax)
21. [Looking Ahead — Day 19b](#21-looking-ahead--day-19b)

---

## 1. Router Setup

### Install (already included in `@angular/router` — no extra install)

### NgModule Approach (Traditional)
```typescript
// app.module.ts
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [ /* ... */ ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],  // forRoot = top-level, once only
  exports: [RouterModule]                   // export for use in component templates
})
export class AppModule {}
```

### Standalone Approach (Angular 14+)
```typescript
// main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter }        from '@angular/router';
import { AppComponent }         from './app/app.component';

bootstrapApplication(AppComponent, {
  providers: [provideRouter(routes)]
});
```

**Key facts:**
- `RouterModule.forRoot()` — top-level setup, imports only once
- `RouterModule.forChild()` — used in feature modules (lazy loading)
- `provideRouter()` — standalone equivalent, no module required
- Both approaches produce identical routing behavior

---

## 2. Route Configuration

```typescript
const routes: Routes = [
  // Default redirect (pathMatch: 'full' REQUIRED on empty path)
  { path: '',              redirectTo: '/home', pathMatch: 'full' },

  // Static routes
  { path: 'home',          component: HomeComponent },
  { path: 'products',      component: ProductListComponent },
  { path: 'cart',          component: CartComponent },
  { path: 'login',         component: LoginComponent },

  // Route parameter — :id captures a dynamic segment
  { path: 'products/:id',  component: ProductDetailComponent },

  // Protected route (guard added in Section 9)
  { path: 'dashboard',     component: DashboardComponent },

  // Wildcard 404 — MUST BE LAST
  { path: '**',            component: NotFoundComponent },
];
```

**Matching rules:**
- Top-to-bottom — first match wins
- Exact matching is the default for non-empty paths
- `pathMatch: 'full'` — only match if the ENTIRE path matches (required for `''` redirect)
- `pathMatch: 'prefix'` — matches if the path starts with the segment (default, dangerous on `''`)
- `'**'` wildcard catches everything — must be the absolute last entry

---

## 3. RouterOutlet

```html
<!-- app.component.html -->
<app-navbar></app-navbar>

<router-outlet></router-outlet>
<!-- Angular renders the matched component AFTER this element in the DOM -->
<!-- It is a placeholder, not a wrapper — matched component is a sibling -->

<app-footer></app-footer>
```

**DOM result when navigating to `/products`:**
```html
<app-navbar>...</app-navbar>
<router-outlet></router-outlet>
<app-product-list>     ← inserted by router, AFTER the outlet
  ...
</app-product-list>
<app-footer>...</app-footer>
```

---

## 4. RouterLink and RouterLinkActive

```html
<!-- Static link -->
<a routerLink="/products">Products</a>

<!-- Dynamic link (property binding with array) -->
<a [routerLink]="['/products', product.id]">{{ product.name }}</a>

<!-- Link with query params -->
<a [routerLink]="['/products']" [queryParams]="{ category: 'books' }">Books</a>

<!-- Active class — adds class when this route is active -->
<a routerLink="/products" routerLinkActive="active-link">Products</a>

<!-- Exact match — prevents '/' from always being active -->
<a
  routerLink="/home"
  routerLinkActive="active-link"
  [routerLinkActiveOptions]="{ exact: true }"
>Home</a>

<!-- Custom active styling -->
<a
  routerLink="/products"
  routerLinkActive
  #rla="routerLinkActive"
  [style.fontWeight]="rla.isActive ? 'bold' : 'normal'"
>Products</a>
```

**Rule:** Never use `<a href="/path">` for internal navigation — it causes a full page reload.

---

## 5. Programmatic Navigation

```typescript
import { Router, ActivatedRoute } from '@angular/router';

@Component({ ... })
export class MyComponent {
  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {}

  // Navigate to static path
  goHome(): void {
    this.router.navigate(['/home']);
  }

  // Navigate with dynamic segment
  viewProduct(id: number): void {
    this.router.navigate(['/products', id]);
  }

  // Replace history entry (no Back button to this page)
  afterLogin(redirectUrl: string): void {
    this.router.navigate([redirectUrl], { replaceUrl: true });
  }

  afterLogout(): void {
    this.router.navigate(['/login'], { replaceUrl: true });
  }

  // Relative navigation (relative to current route)
  goToChild(): void {
    this.router.navigate(['settings'], { relativeTo: this.route });
  }

  // Navigate with query params
  filterProducts(category: string): void {
    this.router.navigate(['/products'], {
      queryParams: { category },
      queryParamsHandling: 'merge'   // preserve existing params
    });
  }
}
```

| Option | Effect |
|---|---|
| `replaceUrl: true` | Replaces current history entry — no Back button |
| `relativeTo: this.route` | Resolves path relative to current route |
| `queryParams: {}` | Appends `?key=val` to URL |
| `queryParamsHandling: 'merge'` | Merge new params with existing instead of replacing |

---

## 6. Route Parameters

### Definition
```typescript
{ path: 'products/:id', component: ProductDetailComponent }
{ path: 'users/:userId/posts/:postId', component: PostComponent }
```

### Reading — snapshot (component recreated on each navigation)
```typescript
import { ActivatedRoute } from '@angular/router';

ngOnInit(): void {
  const id = Number(this.route.snapshot.paramMap.get('id'));  // ALWAYS convert string!
  this.loadProduct(id);
}
```

### Reading — Observable (component reused, params change in place)
```typescript
import { Subscription } from 'rxjs';

private sub!: Subscription;

ngOnInit(): void {
  this.sub = this.route.paramMap.subscribe(params => {
    const id = Number(params.get('id'));
    this.loadProduct(id);
  });
}

ngOnDestroy(): void {
  this.sub.unsubscribe();   // prevent memory leak
}
```

### When to use each

| Use `snapshot` when... | Use `paramMap` Observable when... |
|---|---|
| Navigating to a completely different component | Navigating from `/products/1` → `/products/2` (same component) |
| Component is always fresh on each visit | Component is reused, only params change |
| Simpler code, one-time read | Need to react to param changes without destroying the component |

⚠️ **Route parameters are ALWAYS strings** — always convert with `Number()` when comparing with numeric IDs.

---

## 7. Query Parameters

```typescript
// Navigate with query params
this.router.navigate(['/products'], {
  queryParams: { category: 'electronics', sort: 'price', page: 1 }
});
// URL: /products?category=electronics&sort=price&page=1

// Merge with existing params (preserve category when changing sort)
this.router.navigate(['/products'], {
  queryParams: { sort: 'name' },
  queryParamsHandling: 'merge'
});
```

```html
<!-- Declarative with query params -->
<a [routerLink]="['/products']" [queryParams]="{ category: 'books' }">Books</a>
```

```typescript
// Reading query params
ngOnInit(): void {
  this.route.queryParamMap.subscribe(params => {
    this.category = params.get('category') ?? 'all';
    this.sort      = params.get('sort')     ?? 'name';
    this.page      = Number(params.get('page') ?? '1');
    this.loadProducts();
  });

  // One-time snapshot read
  const category = this.route.snapshot.queryParamMap.get('category');
}
```

**Why query params over component state:**
- URL-based state is bookmarkable and shareable
- Browser Back button navigates through filter history
- Survives page refresh
- Deep-linkable from emails, notifications, search results

---

## 8. Nested Routes

### Route Configuration
```typescript
const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,       // always rendered for /dashboard/**
    children: [
      { path: '',         component: DashboardHomeComponent },  // /dashboard
      { path: 'profile',  component: ProfileComponent },        // /dashboard/profile
      { path: 'settings', component: SettingsComponent },       // /dashboard/settings
      { path: '**',       component: NotFoundComponent },       // /dashboard/anything-else
    ]
  },
  { path: '**', component: NotFoundComponent }   // top-level catch-all
];
```

### Parent Component Template
```html
<!-- dashboard.component.html -->
<div class="dashboard-shell">
  <aside class="sidebar">
    <!-- Relative links — no leading slash -->
    <a routerLink="profile"  routerLinkActive="active">Profile</a>
    <a routerLink="settings" routerLinkActive="active">Settings</a>
  </aside>
  <main>
    <router-outlet></router-outlet>  <!-- children render here -->
  </main>
</div>
```

### URL → Render table

| URL | Top-level outlet | Inner outlet |
|---|---|---|
| `/dashboard` | `DashboardComponent` | `DashboardHomeComponent` (index) |
| `/dashboard/profile` | `DashboardComponent` | `ProfileComponent` |
| `/dashboard/settings` | `DashboardComponent` | `SettingsComponent` |
| `/dashboard/xyz` | `DashboardComponent` | `NotFoundComponent` (nested catch-all) |
| `/home` | `HomeComponent` | — |

---

## 9. Route Guards — CanActivate

### Functional Guard (Angular 15+, Recommended)
```typescript
// src/app/core/guards/auth.guard.ts
import { inject }              from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService }           from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router      = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }  // preserve attempted URL
  });
};
```

### Applying to Routes
```typescript
{ path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] }
{ path: 'admin',     component: AdminComponent,     canActivate: [authGuard, adminGuard] }

// Protect all children at once
{
  path: 'dashboard',
  component: DashboardComponent,
  canActivate: [authGuard],
  children: [ ... ]   // guard protects every child automatically
}
```

### Class-Based Guard (Pre-Angular 15, Still Valid)
```typescript
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    if (this.authService.isLoggedIn()) return true;
    return this.router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }
}
```

### Redirect-Back After Login
```typescript
// Login component — read the returnUrl, redirect after success
@Component({ ... })
export class LoginComponent {
  constructor(private router: Router, private route: ActivatedRoute) {}

  onLoginSuccess(): void {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? '/dashboard';
    this.router.navigate([returnUrl], { replaceUrl: true });
  }
}
```

### canActivateChild — Guard Each Child Route Individually

`canActivateChild` sits on a **parent route** but fires separately before each **child** navigation. The parent component itself always renders — only the child activation is gated.

| | `canActivate` on parent | `canActivateChild` on parent |
|---|---|---|
| When it runs | Once, when entering the parent | Before every child-route navigation |
| Parent component loads | Blocked if guard returns false | Always renders |
| Re-runs on child change | No | Yes |
| Use when | Gate the whole section at entry | Per-child access or token re-validation |

```typescript
import { CanActivateChildFn, Router } from '@angular/router';
import { inject }                      from '@angular/core';
import { AuthService }                from '../services/auth.service';

export const authChildGuard: CanActivateChildFn = (childRoute, state) => {
  const authService = inject(AuthService);
  const router      = inject(Router);

  if (authService.isLoggedIn()) return true;
  return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};

// Apply on the parent route — fires before each child is activated
{
  path: 'dashboard',
  component: DashboardComponent,
  canActivateChild: [authChildGuard],
  children: [
    { path: 'profile',  component: ProfileComponent  },   // guard runs
    { path: 'settings', component: SettingsComponent },   // guard runs
    { path: 'reports',  component: ReportsComponent  },   // guard runs
  ]
}
```

**Tip:** You can combine both on the same route — `canActivate` blocks entering the parent; `canActivateChild` guards each child independently.

---

## 10. Route Guards — CanDeactivate

```typescript
// Interface for components that might have unsaved changes
export interface HasUnsavedChanges {
  hasUnsavedChanges(): boolean;
}

// Functional guard
export const unsavedChangesGuard: CanDeactivateFn<HasUnsavedChanges> =
  (component) => {
    if (component.hasUnsavedChanges()) {
      return confirm('You have unsaved changes. Leave without saving?');
    }
    return true;
  };

// Apply to route
{ path: 'profile/edit', component: EditProfileComponent, canDeactivate: [unsavedChangesGuard] }
```

```typescript
// Component implements the interface
@Component({ ... })
export class EditProfileComponent implements HasUnsavedChanges {
  isDirty = false;

  hasUnsavedChanges(): boolean { return this.isDirty; }

  onFieldChange(): void { this.isDirty = true; }
  onSave(): void { /* save */ this.isDirty = false; }
}
```

**Production pattern:** Replace `confirm()` with a custom dialog that returns `Observable<boolean>`. `CanDeactivateFn` accepts `boolean | Promise<boolean> | Observable<boolean>`.

---

## 11. Lazy Loading

### NgModule Lazy Loading
```typescript
// app.routes.ts
{
  path: 'admin',
  canActivate: [authGuard],           // guard fires BEFORE download
  loadChildren: () =>
    import('./features/admin/admin.module').then(m => m.AdminModule)
}

// admin.module.ts
@NgModule({
  imports: [RouterModule.forChild([   // forChild, not forRoot
    { path: '',       component: AdminDashboardComponent },
    { path: 'users',  component: UserManagementComponent },
  ])]
})
export class AdminModule {}
```

### Standalone Component Lazy Loading (Angular 14+)
```typescript
// Single component
{
  path: 'dashboard',
  loadComponent: () =>
    import('./features/dashboard/dashboard.component')
      .then(m => m.DashboardComponent)
}

// Group of standalone routes
{
  path: 'admin',
  loadChildren: () =>
    import('./features/admin/admin.routes')
      .then(m => m.ADMIN_ROUTES)   // ADMIN_ROUTES: Routes array
}
```

### Bundle Impact
```
Without lazy loading:  main.js = 800KB (everything)
With lazy loading:     main.js = 120KB + admin.chunk.js = 200KB (on demand)
```

---

## 12. Template-Driven Forms

### Setup
```typescript
import { FormsModule } from '@angular/forms';
@NgModule({ imports: [FormsModule] })
```

### Form Template
```html
<form #loginForm="ngForm" (ngSubmit)="onSubmit(loginForm)">

  <input
    type="email"
    name="email"                          ← REQUIRED — ngForm tracks by name
    [(ngModel)]="credentials.email"       ← two-way binding
    required                              ← HTML validator
    email                                 ← Angular email validator
    #emailInput="ngModel"                 ← template ref to this control
  />
  <div *ngIf="emailInput.invalid && emailInput.touched">
    <span *ngIf="emailInput.errors?.['required']">Email is required.</span>
    <span *ngIf="emailInput.errors?.['email']">Invalid email format.</span>
  </div>

  <input
    type="password"
    name="password"
    [(ngModel)]="credentials.password"
    required
    minlength="8"
    #passwordInput="ngModel"
  />
  <div *ngIf="passwordInput.invalid && passwordInput.touched">
    <span *ngIf="passwordInput.errors?.['required']">Password required.</span>
    <span *ngIf="passwordInput.errors?.['minlength']">
      Min 8 chars (got {{ passwordInput.errors?.['minlength'].actualLength }})
    </span>
  </div>

  <button type="submit" [disabled]="loginForm.invalid">Login</button>
</form>
```

### Component Class
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

### Form State Properties

| Property | Meaning |
|---|---|
| `valid` / `invalid` | All validators pass / any fail |
| `touched` / `untouched` | Has been focused+blurred / never |
| `dirty` / `pristine` | Value changed / never changed |
| `errors` | Error object (null when valid) |

**Show error pattern:** `field.invalid && field.touched` — avoid showing errors before the user interacts.

---

## 13. Reactive Forms — FormGroup and FormControl

### Setup
```typescript
import { ReactiveFormsModule } from '@angular/forms';
@NgModule({ imports: [ReactiveFormsModule] })
```

### Component Class (Manual)
```typescript
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({ ... })
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;

  ngOnInit(): void {
    this.registerForm = new FormGroup({
      name:     new FormControl('', [Validators.required, Validators.minLength(2)]),
      email:    new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value);
    }
  }

  // Access value/state programmatically
  getEmailError(): string {
    const ctrl = this.registerForm.get('email');
    if (ctrl?.errors?.['required']) return 'Email is required';
    if (ctrl?.errors?.['email'])    return 'Invalid format';
    return '';
  }
}
```

### Template
```html
<form [formGroup]="registerForm" (ngSubmit)="onSubmit()">

  <input type="text" formControlName="name" />
  <div *ngIf="registerForm.get('name')?.invalid && registerForm.get('name')?.touched">
    <span *ngIf="registerForm.get('name')?.errors?.['required']">Name required.</span>
    <span *ngIf="registerForm.get('name')?.errors?.['minlength']">
      Min {{ registerForm.get('name')?.errors?.['minlength'].requiredLength }} chars.
    </span>
  </div>

  <button type="submit" [disabled]="registerForm.invalid">Register</button>
</form>
```

---

## 14. FormBuilder

```typescript
import { FormBuilder, Validators } from '@angular/forms';

@Component({ ... })
export class RegisterComponent {
  registerForm = this.fb.group({
    name:     ['',  [Validators.required, Validators.minLength(2)]],
    email:    ['',  [Validators.required, Validators.email]],
    password: ['',  [Validators.required, Validators.minLength(8)]],
    confirm:  ['',  Validators.required],
  }, {
    validators: passwordMatchValidator   // group-level cross-field validator
  });

  constructor(private fb: FormBuilder) {}
}
```

### Equivalences
```typescript
new FormGroup({ name: new FormControl('', Validators.required) })
// ≡
this.fb.group({ name: ['', Validators.required] })

new FormControl('')   // ≡  this.fb.control('')
new FormArray([])     // ≡  this.fb.array([])
```

### Patching Values (Edit Forms)
```typescript
// Set specific fields (others unchanged)
this.registerForm.patchValue({ name: 'Alice', email: 'alice@example.com' });

// Set all fields (all must be provided)
this.registerForm.setValue({
  name: 'Alice', email: 'alice@example.com', password: '', confirm: ''
});

// Reset form and clear validation state
this.registerForm.reset();
```

---

## 15. FormArray — Dynamic Form Controls

`FormArray` holds an **ordered list** of `FormControl`s or `FormGroup`s. Use it when the number of inputs is dynamic — for example, "add another phone number", a list of skills, or line items in an invoice.

### Component Class

```typescript
import { FormArray, FormBuilder, Validators } from '@angular/forms';

@Component({ ... })
export class ProfileComponent {
  form = this.fb.group({
    name:   ['', Validators.required],
    phones: this.fb.array([])   // empty to start; controls added dynamically
  });

  constructor(private fb: FormBuilder) {}

  // Typed getter for clean access in template and component methods
  get phones(): FormArray {
    return this.form.get('phones') as FormArray;
  }

  addPhone(): void {
    this.phones.push(
      this.fb.control('', [Validators.required, Validators.pattern(/^\d{10}$/)])
    );
  }

  removePhone(index: number): void {
    this.phones.removeAt(index);
  }

  onSubmit(): void {
    if (this.form.valid) {
      console.log(this.form.value);
      // { name: 'Alice', phones: ['1234567890', '9876543210'] }
    }
  }
}
```

### Template

```html
<form [formGroup]="form" (ngSubmit)="onSubmit()">
  <input formControlName="name" placeholder="Name" />

  <div formArrayName="phones">
    <div *ngFor="let phone of phones.controls; let i = index">
      <input [formControlName]="i" placeholder="Phone number" />
      <button type="button" (click)="removePhone(i)">Remove</button>
    </div>
  </div>

  <button type="button" (click)="addPhone()">+ Add Phone</button>
  <button type="submit" [disabled]="form.invalid">Save</button>
</form>
```

### FormArray of FormGroups (Complex Items)

For structured items (e.g., full address entries), each element is a `FormGroup` inside the array:

```typescript
get addresses(): FormArray { return this.form.get('addresses') as FormArray; }

addAddress(): void {
  this.addresses.push(this.fb.group({
    street: ['', Validators.required],
    city:   ['', Validators.required],
    zip:    ['', Validators.required],
  }));
}
```

```html
<!-- Use [formGroupName]="i" when each item is a FormGroup -->
<div formArrayName="addresses">
  <div *ngFor="let addr of addresses.controls; let i = index" [formGroupName]="i">
    <input formControlName="street" />
    <input formControlName="city" />
    <input formControlName="zip" />
    <button type="button" (click)="removeAddress(i)">Remove</button>
  </div>
</div>
```

**Key FormArray API:**

| Method | Purpose |
|---|---|
| `.push(control)` | Add a control at the end |
| `.removeAt(i)` | Remove control at index `i` |
| `.at(i)` | Get control at index `i` |
| `.controls` | Array of all `AbstractControl`s |
| `.length` | Number of controls |
| `.clear()` | Remove all controls |
| `.setValue(array)` | Set all values (array length must match) |
| `.patchValue(array)` | Update matching values only |

---

## 16. Built-In Validators

```typescript
import { Validators } from '@angular/forms';

Validators.required              // not null/empty
Validators.requiredTrue          // must be exactly true (checkboxes)
Validators.email                 // valid email format
Validators.minLength(n)          // string length >= n
Validators.maxLength(n)          // string length <= n
Validators.min(n)                // numeric value >= n
Validators.max(n)                // numeric value <= n
Validators.pattern(regex)        // must match regex
```

### Error Keys Returned by `control.errors`

| Validator | Error key | Error value |
|---|---|---|
| `required` | `required` | `true` |
| `email` | `email` | `true` |
| `minLength(8)` | `minlength` | `{ requiredLength: 8, actualLength: n }` |
| `maxLength(20)` | `maxlength` | `{ requiredLength: 20, actualLength: n }` |
| `min(0)` | `min` | `{ min: 0, actual: n }` |
| `max(100)` | `max` | `{ max: 100, actual: n }` |
| `pattern(...)` | `pattern` | `{ requiredPattern, actualValue }` |
| Valid | — | `null` |

---

## 17. Custom Validators

### Field-Level (`ValidatorFn`)
```typescript
import { AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

export function noSpacesValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const hasSpaces = (control.value as string)?.includes(' ');
    return hasSpaces ? { noSpaces: true } : null;   // null = VALID
  };
}

// Usage
new FormControl('', [Validators.required, noSpacesValidator()])
```

### Cross-Field / Group-Level
```typescript
export function passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirm  = group.get('confirm')?.value;
  return password === confirm ? null : { passwordMismatch: true };
}

// Applied to FormGroup
this.fb.group({ ... }, { validators: passwordMatchValidator });

// In template — error lives on the FORM, not a control
<span *ngIf="form.errors?.['passwordMismatch'] && form.touched">
  Passwords do not match.
</span>
```

### Async Validator (`AsyncValidatorFn`)
```typescript
import { AsyncValidatorFn } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export function emailTakenValidator(authService: AuthService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    return authService.isEmailTaken(control.value).pipe(
      map(taken => taken ? { emailTaken: true } : null),
      catchError(() => of(null))   // on HTTP error, treat as valid
    );
  };
}

// Usage — async validators are the THIRD argument
new FormControl('', [Validators.email], [emailTakenValidator(this.authService)])
//               ↑ sync validators      ↑ async validators
```

---

## 18. Template-Driven vs Reactive Comparison

| | Template-Driven | Reactive |
|---|---|---|
| **Module** | `FormsModule` | `ReactiveFormsModule` |
| **Form model lives in** | HTML template | TypeScript class |
| **Input binding** | `[(ngModel)]` | `formControlName` |
| **Validators defined** | HTML attributes (`required`, `email`) | `Validators.*` in TypeScript |
| **Access form ref** | `#form="ngForm"` template ref | `this.form.get('field')` |
| **Dynamic fields** | Difficult | Easy with `FormArray` |
| **Unit testing** | Needs DOM / TestBed | Pure TypeScript — no DOM needed |
| **Type safety** | Limited | Full (`FormControl<string>`) |
| **Best for** | Simple 2–4 field forms, quick prototypes | Complex forms, dynamic fields, tested code |

**Rule of thumb:** Use reactive forms by default for any form in a production application. Use template-driven only for truly trivial forms where validation is minimal.

---

## 19. Common Mistakes & Fixes

### Routing

| Mistake | Fix |
|---|---|
| `<a href="/products">` for internal links | `<a routerLink="/products">` |
| `pathMatch: 'prefix'` on empty path redirect | Use `pathMatch: 'full'` |
| Wildcard `**` not last in routes array | Move `**` to the absolute end |
| `navigate(['/products', id])` where `id` is undefined | Ensure `id` is set before navigating |
| Comparing `paramMap.get('id') === product.id` (string vs number) | `Number(paramMap.get('id')) === product.id` |
| Using `snapshot` when navigating between same component | Use `paramMap` Observable instead |
| Forgetting `{ relativeTo: this.route }` for relative navigation | Add `{ relativeTo: this.route }` to `navigate()` options |
| `routerLink="/profile"` inside dashboard (absolute) | Use `routerLink="profile"` (relative, no leading slash) |

### Guards

| Mistake | Fix |
|---|---|
| Returning `false` from guard (no redirect) | Return `router.createUrlTree(['/login'])` so user knows where to go |
| Not storing `returnUrl` in guard | Pass `{ queryParams: { returnUrl: state.url } }` to `createUrlTree` |
| `canActivate: [AuthGuard]` with functional guard | Functional guards: `canActivate: [authGuard]` (no `new`, no class name) |

### Forms

| Mistake | Fix |
|---|---|
| Missing `name` attribute on `[(ngModel)]` input | Add `name="fieldName"` — `ngForm` won't track the field without it |
| Showing errors on untouched fields | `field.invalid && field.touched` — always include `touched` condition |
| `form.get('email').errors['required']` without optional chaining | `form.get('email')?.errors?.['required']` — `get` can return null |
| Mixing `[(ngModel)]` with `formControlName` in the same form | Use one approach per form — they are separate systems |
| `form.value` used before checking `form.valid` | Always check `if (form.valid)` before submitting |
| Group-level error checked on control instead of form | Check `form.errors?.['passwordMismatch']` not `form.get('password')?.errors` |

---

## 20. Quick Reference Syntax

```typescript
// --- ROUTING SETUP ---
RouterModule.forRoot(routes)       // NgModule
provideRouter(routes)              // Standalone in main.ts

// --- ROUTE OBJECT SHAPE ---
{ path: 'products',       component: ProductsComponent }
{ path: 'products/:id',   component: ProductDetailComponent }
{ path: '',               redirectTo: '/home', pathMatch: 'full' }
{ path: '**',             component: NotFoundComponent }
{ path: 'dashboard',      component: DashboardComponent, canActivate: [authGuard],
  children: [ { path: 'profile', component: ProfileComponent } ] }
{ path: 'admin',          canActivate: [authGuard],
  loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule) }

// --- NAVIGATION ---
this.router.navigate(['/products', id])
this.router.navigate(['/login'], { replaceUrl: true })
this.router.navigate(['settings'], { relativeTo: this.route })
this.router.navigate(['/products'], { queryParams: { sort: 'price' }, queryParamsHandling: 'merge' })

// --- READING ROUTE DATA ---
this.route.snapshot.paramMap.get('id')        // one-time read
this.route.paramMap.subscribe(...)            // reactive
this.route.snapshot.queryParamMap.get('cat')  // query param
this.route.queryParamMap.subscribe(...)       // reactive query param
```

```html
<!-- TEMPLATE DIRECTIVES -->
<router-outlet></router-outlet>
<a routerLink="/products">Products</a>
<a [routerLink]="['/products', id]">Detail</a>
<a [routerLink]="['/products']" [queryParams]="{ sort: 'price' }">Sorted</a>
<a routerLink="/home" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Home</a>
```

```typescript
// --- GUARDS ---
export const authGuard: CanActivateFn = (route, state) => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  return auth.isLoggedIn()
    ? true
    : router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};

// --- TEMPLATE-DRIVEN FORMS ---
// Module: FormsModule
// Template: name="x" [(ngModel)]="model.x" required #xInput="ngModel"
// Error: *ngIf="xInput.invalid && xInput.touched"
// Submit: (ngSubmit)="onSubmit(form)" where form: NgForm

// --- REACTIVE FORMS ---
// Module: ReactiveFormsModule
form = this.fb.group({
  field: ['initialValue', [Validators.required, Validators.minLength(3)]]
});
// Template: [formGroup]="form" (ngSubmit)="onSubmit()"
//           formControlName="field"
// Access:   form.get('field')?.value / .invalid / .errors

// --- VALIDATORS ---
Validators.required | Validators.email | Validators.minLength(n)
Validators.maxLength(n) | Validators.min(n) | Validators.max(n) | Validators.pattern(regex)
// Custom: (control) => ValidationErrors | null
// Group:  (group) => ValidationErrors | null  → applied as { validators: fn }
```

---

## 21. Looking Ahead — Day 19b

**Day 19b: Angular HTTP & RxJS** builds directly on today's patterns:

| Today (Day 18b) | Day 19b |
|---|---|
| `paramMap.subscribe()` — our first Observable usage | Full deep dive on RxJS: `pipe`, `map`, `filter`, `switchMap` |
| Manual `this.sub.unsubscribe()` pattern | `async` pipe for automatic unsubscription |
| `catchError(() => of(null))` in async validator | `catchError` in HTTP error handling |
| Services injected into guards | `HttpClient` injected into services for API calls |
| `queryParams` for passing data | `HttpParams` for building API query strings |
| Guards intercepting routes | HTTP Interceptors intercepting every outgoing request |
| Auth guard checks `authService.isLoggedIn()` | Interceptor automatically attaches the JWT token to every request |

**Day 19b topics preview:**
- `HttpClient`: `get<T>()`, `post<T>()`, `put<T>()`, `delete<T>()` — all return Observables
- `HttpParams` and `HttpHeaders` — type-safe request construction
- `catchError` and `throwError` — handling 401, 404, 500 errors gracefully
- HTTP Interceptors — add auth headers, log requests, handle errors globally
- RxJS operators: `map`, `filter`, `tap`, `switchMap` (cancels previous request), `mergeMap`
- `BehaviorSubject` for shared mutable state in services
- `async` pipe — subscribe in the template, unsubscribe automatically on component destroy
