# Angular Routing & Forms — Revised 1-Hour Lecture Script
**Estimated Time:** ~65–70 minutes  
**Audience:** New students with some Angular background  
**Format:** Script + Slide Descriptions

---

## OPENING (5 minutes)

### [SLIDE 1: Title Slide]
**Title:** "Angular Routing & Forms"  
**Subtitle:** "Navigation, Route Guards, Lazy Loading & Form Handling"  
Include Angular logo and today's date

**Script:**
"Good morning everyone. Today we're diving into two of the most important pillars of any real Angular application — Routing and Forms. These are the things that turn a static page into a living, breathing application. By the end of this session, you're going to understand how Angular navigates between views, how it protects those views, how it loads them efficiently, and how it handles user input through forms. Let's get into it."

---

## SECTION 1: ROUTING & NAVIGATION IN ANGULAR (8 minutes)

### [SLIDE 2: What is Angular Routing?]
**Bullet points:**
- Angular is a Single Page Application (SPA) framework
- Routing lets us swap views without a full page reload
- The Angular Router maps URLs to components
- Diagram: Browser URL bar → Router → Component rendered in `<router-outlet>`

**Script:**
"Before we write any code, let's understand what we're solving. In a traditional website, every time you click a link, the browser makes a request to a server and loads a whole new HTML page. Angular works differently. It's a Single Page Application — the browser loads one HTML file once, and Angular handles all the view-switching internally. That's what the Angular Router does. It watches the URL in the browser, matches it to a component you've defined, and renders that component — all without ever making a new page request to the server.

The key thing to visualize here is the `<router-outlet>` directive. Think of it as a placeholder in your app's template. Whatever component the router decides should be active, it gets injected right there into that outlet. So your navigation bar stays, your footer stays, and only the main content area swaps out. This is the core mental model you need before we look at any code."

---

### [SLIDE 3: How Routing Works — The Flow]
**Diagram:** User clicks link → URL changes → Router matches URL to route config → Component loads into `<router-outlet>`

**Code block — AppComponent template (the app shell):**
```html
<!-- app.component.html -->
<nav>
  <a routerLink="/home">Home</a>
  <a routerLink="/about">About</a>
  <a routerLink="/products">Products</a>
</nav>

<!-- The active route's component renders here -->
<router-outlet></router-outlet>
```

**Script:**
"Here's the flow visually. A user clicks a link or you programmatically navigate somewhere. The URL changes. The Router looks at your route configuration, finds the matching route, and loads the associated component into the outlet. It's clean and predictable.

Below the diagram you can see exactly what your root AppComponent template looks like in practice. The nav stays on screen the whole time. The `<router-outlet>` is the slot where the active component gets injected. Everything outside the outlet — your nav, your footer, whatever wrapping layout you have — stays put. Keep this in your head because everything else we cover today builds on top of it."

---

## SECTION 2: ROUTERMODULE AND ROUTE CONFIGURATION (8 minutes)

### [SLIDE 4: Setting Up RouterModule]
**Code block:**
```typescript
// app-routing.module.ts
const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'about', component: AboutComponent },
  { path: 'products', component: ProductsComponent },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
```
**Annotations:** `pathMatch: 'full'` explanation, wildcard at the bottom, note about `provideRouter(routes)` in standalone apps

**Script:**
"You configure routing by defining a `Routes` array — each object maps a URL path to a component. Then you pass that array into `RouterModule.forRoot()` inside your AppModule. The `forRoot` matters — it means this is the root-level router configuration for the whole app. You'll see `forChild` later when we get to lazy loading.

Walk through the routes: the first is a redirect — when the path is empty, meaning the root URL, redirect to /home. `pathMatch: 'full'` tells the router to only apply this when the path is exactly empty. Without it, you'd get infinite redirects because an empty string technically matches the start of every URL. Then we have straightforward mappings for home, about, and products. The wildcard at the bottom catches anything that doesn't match and sends it to a 404 page. Order matters — the router reads top to bottom and uses the first match it finds. That's why the wildcard must always be last."

---

### [SLIDE 5: RouterLink and RouterLinkActive]
**Code block:**
```html
<nav>
  <a routerLink="/home" routerLinkActive="active">Home</a>
  <a routerLink="/about" routerLinkActive="active">About</a>

  <!-- Dynamic path with array syntax -->
  <a [routerLink]="['/products', product.id]">View Product</a>
</nav>
```
```css
/* styles.css */
.active {
  font-weight: bold;
  color: #007bff;
}
```

**Script:**
"In your templates, you use `routerLink` instead of `href` to navigate. Using `href` causes a full page reload — that defeats the whole point of a SPA. `routerLink` tells the Angular Router to handle the navigation client-side.

`routerLinkActive` automatically applies a CSS class to a link when its route is currently active. This is perfect for highlighting the active item in a nav menu without writing any extra logic. When you need to navigate to a dynamic path — like a product detail page with an ID — you use the array syntax with property binding brackets, passing each URL segment as its own element in the array."

---

### [SLIDE 6: Programmatic Navigation]
**Code block:**
```typescript
import { Router } from '@angular/router';

@Component({ ... })
export class LoginComponent {
  constructor(private router: Router) {}

  onLoginSuccess() {
    // Navigate by path array
    this.router.navigate(['/dashboard']);
  }

  onGoToProduct(id: number) {
    // Navigate with dynamic segment
    this.router.navigate(['/products', id]);
  }

  onSearch(term: string) {
    // Navigate with query parameters
    this.router.navigate(['/results'], {
      queryParams: { q: term, page: 1 }
    });
  }
}
```

**Script:**
"You won't always navigate in response to a user clicking a link in a template. Most of the time in real apps, navigation happens in your component class — after a form submission, after a login, after an API call completes. That's where programmatic navigation comes in.

You inject the `Router` service from `@angular/router` and call `this.router.navigate()`. It takes an array of path segments, so dynamic IDs slot right in as additional elements. If you also need to append query parameters — like a search term or a page number — you pass them as a second argument with `queryParams`. You'll use this constantly. Every single form submission that redirects somewhere after saving uses this pattern."

---

## SECTION 3: ROUTE PARAMETERS & QUERY PARAMETERS (7 minutes)

### [SLIDE 7: Route Parameters]
**Code block:**
```typescript
// Route definition
{ path: 'products/:id', component: ProductDetailComponent }

// Component
import { ActivatedRoute } from '@angular/router';

@Component({ ... })
export class ProductDetailComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    // Snapshot — reads once on init (fine if user can't navigate between instances)
    const id = this.route.snapshot.paramMap.get('id');

    // Observable — reacts to changes (use this as your default habit)
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.loadProduct(id);
    });
  }
}
```

**Script:**
"Most routes need to be dynamic. For a product details page you don't want a separate route for every product. You define one route with a parameter placeholder using a colon — `/products/:id` — and that `:id` matches any value in that segment.

To read the parameter inside your component, inject `ActivatedRoute`. You have two ways to get the value. `snapshot.paramMap.get('id')` reads the parameter once when the component initializes — fine if the user will never navigate from one product directly to another. But if they can — say there's a 'next product' link — you need to subscribe to `paramMap` as an observable, because the component won't be destroyed and re-created, it'll just receive new parameters. Get into the habit of using the observable approach. It's more robust and it'll save you from a subtle bug that's frustrating to track down."

---

### [SLIDE 8: Query Parameters]
**Code block:**
```typescript
// In template
<a [routerLink]="['/products']" [queryParams]="{ sort: 'price', order: 'asc' }">
  Sort by Price
</a>

// Resulting URL: /products?sort=price&order=asc

// Reading in component
this.route.snapshot.queryParamMap.get('sort');  // 'price'

// Or as observable
this.route.queryParamMap.subscribe(params => {
  const sort = params.get('sort');
  const order = params.get('order');
});
```
**Annotation:** Route params = required, part of the path. Query params = optional, supplementary.

**Script:**
"Query parameters are different from route parameters. They're the key-value pairs after the question mark in a URL — things like sort order, page number, or search filters. They're optional and don't affect which route gets matched. In your template you add them with `queryParams` on a routerLink. In your component you read them with `queryParamMap` on `ActivatedRoute` — same service, different property.

The key distinction: route parameters are part of the path and are required for the route to match. Query parameters are supplementary and completely optional. If you're building a filterable, sortable list, query params are how you make those filters shareable via the URL."

---

## SECTION 4: NESTED ROUTES (5 minutes)

### [SLIDE 9: Nested Routes — Concept]
**Visual diagram:** App outlet → ProfileComponent (with its own `<router-outlet>`) → OverviewComponent or SettingsComponent

**Bullet points:**
- Child routes render inside their parent component
- The parent component template **must** have its own `<router-outlet>`
- URL is cumulative: `/profile/settings`

**Script:**
"Nested routes let you render components inside other components. Imagine a user profile page with tabs — Overview, Posts, Settings. Each tab is its own component, but they all share the profile page's layout — the user's avatar, their name — at the top.

The parent component's template must contain its own `<router-outlet>`. This is a second outlet, distinct from the root one in AppComponent. When the user navigates to `/profile/settings`, Angular renders ProfileComponent into the root outlet and SettingsComponent into the outlet inside ProfileComponent. If you forget the outlet in the parent template, nothing will render for the child routes and you'll spend twenty minutes wondering why — I promise it happens to everyone."

---

### [SLIDE 10: Nested Routes — Code Example]
**Code block:**
```typescript
// Route config
{
  path: 'profile',
  component: ProfileComponent,
  children: [
    { path: '', redirectTo: 'overview', pathMatch: 'full' },
    { path: 'overview', component: OverviewComponent },
    { path: 'settings', component: SettingsComponent }
  ]
}
```
```html
<!-- profile.component.html -->
<div class="profile-header">
  <img [src]="user.avatar" />
  <h2>{{ user.name }}</h2>
</div>

<nav>
  <a routerLink="overview" routerLinkActive="active">Overview</a>
  <a routerLink="settings" routerLinkActive="active">Settings</a>
</nav>

<!-- Child routes render here, NOT in the root outlet -->
<router-outlet></router-outlet>
```

**Script:**
"Here's the full picture in code. The profile route has a children array with its own default redirect and two child routes. The ProfileComponent template shows the shared header — avatar, name — and a tab nav, then its own `<router-outlet>` where the child component slots in. Notice the child `routerLink` values don't start with a slash — they're relative to the parent route, which keeps them clean and portable."

---

## SECTION 5: ROUTE GUARDS (10 minutes)

### [SLIDE 11: What Are Route Guards?]
**Concept:** Guards are services that run before a route activates or deactivates  

| Guard | What it does |
|---|---|
| `CanActivate` | Controls whether a route can be entered |
| `CanDeactivate` | Controls whether a route can be exited |
| `CanLoad` | Prevents a lazy-loaded module from loading at all |
| `Resolve` | Pre-fetches data before the route activates |

**Today's focus:** CanActivate and CanDeactivate  
**Analogy:** A bouncer at a door — checks before letting you in or out

**Script:**
"Route guards are one of the most practically important things in this whole lesson. In any real app, some pages should only be accessible to authenticated users. Some forms should warn you before you navigate away and lose your work. Guards handle both.

Here's a quick summary of all four guard types so you know the landscape. `CanActivate` controls whether you can enter a route. `CanDeactivate` controls whether you can leave one. `CanLoad` is specifically for lazy-loaded modules — it prevents the module's JavaScript chunk from even being downloaded if the user shouldn't have access, which is a security win over just CanActivate alone. `Resolve` pre-fetches data before the route activates, so your component receives data immediately instead of showing a loading spinner.

Today we're going deep on CanActivate and CanDeactivate since those are the two you'll use constantly. CanLoad and Resolve follow the same patterns — once you understand these two, those will click quickly."

---

### [SLIDE 12: CanActivate Guard — Full Code]
**Code block:**
```typescript
// auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  constructor(private auth: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree {
    if (this.auth.isLoggedIn()) {
      return true;  // Let the navigation proceed
    }
    // Redirect to login, preserving where they were trying to go
    return this.router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }
}
```
```typescript
// Route config — applying the guard
{
  path: 'dashboard',
  component: DashboardComponent,
  canActivate: [AuthGuard]
}
```

**Script:**
"Here's a complete CanActivate guard. You create a service, implement the `CanActivate` interface, and write your `canActivate` method. Inside, you check whether the user is logged in. If yes, return `true` and the router proceeds. If not, return a `UrlTree` created with `router.createUrlTree`.

Returning a UrlTree is the modern preferred approach over calling `router.navigate` directly — it's more declarative and works correctly alongside resolvers and other guards. Notice we're also passing the original URL as a `returnUrl` query parameter, so after the user logs in you can redirect them back to where they were trying to go.

In your route config, you add `canActivate: [AuthGuard]` to any route you want to protect. One guard class, reusable across as many routes as you want."

---

### [SLIDE 13: CanDeactivate Guard]
**Code block:**
```typescript
// can-deactivate.guard.ts
export interface ComponentCanDeactivate {
  canDeactivate: () => boolean;
}

@Injectable({ providedIn: 'root' })
export class UnsavedChangesGuard implements CanDeactivate<ComponentCanDeactivate> {
  canDeactivate(component: ComponentCanDeactivate): boolean {
    if (!component.canDeactivate()) {
      return confirm('You have unsaved changes. Leave anyway?');
    }
    return true;
  }
}
```
```typescript
// edit-profile.component.ts
export class EditProfileComponent implements ComponentCanDeactivate {
  constructor(private fb: FormBuilder) {}

  form = this.fb.group({ name: [''] });

  canDeactivate(): boolean {
    return !this.form.dirty;  // true = safe to leave, false = has unsaved changes
  }
}
```
```typescript
// Route config
{ path: 'edit-profile', component: EditProfileComponent, canDeactivate: [UnsavedChangesGuard] }
```

**Script:**
"CanDeactivate works in the opposite direction — it asks 'are you sure you want to leave?' It's generic, meaning it wraps a specific component type.

The typical pattern is: define an interface called `ComponentCanDeactivate` with a `canDeactivate()` method. Your component implements that interface and returns `true` if it's safe to leave — meaning the form isn't dirty — or `false` if there are unsaved changes. The guard calls that method on the component, and if it returns false, shows a confirmation dialog. The user gets a chance to cancel and save their work. This is a significant UX improvement and takes very little code to implement."

---

## SECTION 6: LAZY LOADING (5 minutes)

### [SLIDE 14: Why Lazy Loading?]
**Diagram:** Eager loading (one large bundle sent on first load) vs. Lazy loading (small initial bundle + separate chunks downloaded on demand)

**Bullet points:**
- Loading everything upfront slows initial page load
- Lazy loading splits the app into separate JS chunks
- Chunks are downloaded only when the user navigates to that feature
- Angular CLI handles the code splitting automatically

**Script:**
"As your app grows, so does the JavaScript bundle that gets sent to the browser. If you eagerly load every module upfront, users on slow connections are waiting to download code for pages they might never visit. Lazy loading solves this by splitting your app into separate chunks. The initial bundle stays small. Feature modules are only downloaded the first time a user navigates to that feature. This can dramatically improve your app's initial load time, and it requires very little extra work to set up."

---

### [SLIDE 15: Implementing Lazy Loading]
**Code block:**
```typescript
// app-routing.module.ts — lazy load the admin feature
{
  path: 'admin',
  loadChildren: () =>
    import('./admin/admin.module').then(m => m.AdminModule)
}
```
```typescript
// admin-routing.module.ts — use forChild, NOT forRoot
const adminRoutes: Routes = [
  { path: '', component: AdminDashboardComponent },
  { path: 'users', component: AdminUsersComponent }
];

@NgModule({
  imports: [RouterModule.forChild(adminRoutes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {}
```
**Annotations:** `loadChildren` with dynamic import; `forChild` not `forRoot` in the feature module

**Script:**
"The syntax uses `loadChildren` with a dynamic import — standard JavaScript module syntax. The router won't touch that module until someone navigates to `/admin`. Inside your feature module you use `RouterModule.forChild()` instead of `forRoot()` — `forChild` registers routes without re-initializing the router singleton that `forRoot` created. If you accidentally use `forRoot` in a feature module, you'll get bugs that are confusing to diagnose.

The Angular build tools see that dynamic import and automatically split it into a separate JavaScript chunk. It looks complex but it's one of those features that's straightforward once you see the pattern."

---

## SECTION 7: TEMPLATE-DRIVEN FORMS (6 minutes)

### [SLIDE 16: Two Approaches to Forms in Angular]
**Comparison table:**

| | Template-Driven | Reactive |
|---|---|---|
| Logic lives in | Template (HTML) | Component class (TypeScript) |
| Module needed | `FormsModule` | `ReactiveFormsModule` |
| Form model | Implicit (Angular creates it) | Explicit (you create it) |
| Testing | Harder — requires DOM | Easier — pure TypeScript |
| Dynamic forms | Difficult | Natural |
| Best for | Simple, quick forms | Complex, scalable forms |

**Script:**
"Angular gives you two ways to build forms and they have genuinely different philosophies — let's walk through this table row by row.

Logic location: template-driven puts everything in HTML with directives. Reactive puts the structure in your TypeScript class. Module: you import `FormsModule` for template-driven, `ReactiveFormsModule` for reactive. Form model: in template-driven, Angular implicitly creates the form model for you behind the scenes. In reactive, you explicitly define it — you're in full control. Testing: reactive forms are much easier to test because the form model is a plain TypeScript object you can poke at without spinning up the DOM. Dynamic forms: if you need to add or remove controls at runtime, template-driven becomes painful fast — reactive handles it naturally with `FormArray`. Best for: template-driven is great for simple, quick forms like a login page. Reactive scales better as forms get more complex.

You need to know both — you'll encounter both in real projects."

---

### [SLIDE 17: Template-Driven Forms — Key Concepts]
**Bullet points:**
- Import `FormsModule` in your module
- `ngModel` tracks value and validation state
- `[(ngModel)]` = two-way binding (banana-in-a-box)
- `name` attribute is required on every form control
- `#myForm="ngForm"` gives you access to the overall form state

**Code block:**
```html
<form #myForm="ngForm" (ngSubmit)="onSubmit(myForm)">
  <input
    name="email"
    [(ngModel)]="user.email"
    required
    email
    #emailField="ngModel">

  <div *ngIf="emailField.invalid && emailField.touched">
    Please enter a valid email
  </div>

  <button type="submit" [disabled]="myForm.invalid">Submit</button>
</form>
```

**Script:**
"For template-driven forms, you import `FormsModule` in your module. The key directive is `ngModel`. When you put it on an input, Angular automatically tracks its value and validation state. The banana-in-a-box syntax `[(ngModel)]` gives you two-way binding — the input reflects a component property and updates it as the user types.

Every form control needs a `name` attribute because Angular uses it to register the control with the parent form group. You access the form state through the template reference `#myForm="ngForm"`.

Look at the error message div — it only shows when the field is both invalid AND touched, meaning the user has already interacted with it. Showing errors before the user has even tried to fill out the form is a common mistake. This pattern prevents it."

---

## SECTION 8: REACTIVE FORMS (8 minutes)

### [SLIDE 18: Reactive Forms — Core Concepts]
**Three building blocks:**

| Class | Represents |
|---|---|
| `FormControl` | A single input field |
| `FormGroup` | A collection of controls (a form or form section) |
| `FormArray` | A dynamic list of controls |

**Bullet points:**
- Import `ReactiveFormsModule`
- Form model defined in TypeScript, not in the template
- Observable streams: `valueChanges`, `statusChanges`

**Script:**
"Reactive forms flip the model. Instead of defining your form structure in HTML, you define it explicitly in your component class using three building blocks.

`FormControl` represents a single input. `FormGroup` represents a collection of controls — your whole form or a section of it. `FormArray` represents a dynamic list of controls — perfect for things like adding multiple phone numbers or multiple addresses, where the user can click 'add another' and a new row appears. We'll look at a `FormArray` example shortly.

One of the most powerful things about reactive forms is that everything is observable. The `valueChanges` property on any control or group is an observable that emits every time the value changes. This means you can use RxJS with your forms — debounce input, make API calls on change, combine form values with other streams. This is why reactive forms scale so much better for complex applications."

---

### [SLIDE 19: FormBuilder and Reactive Form Example]
**Code block:**
```typescript
// Component
@Component({ ... })
export class LoginComponent implements OnInit {
  form: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  // Getter — cleaner template access than form.get('email') everywhere
  get email() { return this.form.get('email'); }
  get password() { return this.form.get('password'); }

  onSubmit() {
    if (this.form.valid) {
      console.log(this.form.value);
      this.router.navigate(['/dashboard']);
    }
  }
}
```
```html
<!-- Template -->
<form [formGroup]="form" (ngSubmit)="onSubmit()">
  <input formControlName="email">
  <div *ngIf="email.invalid && email.touched">Invalid email</div>

  <input type="password" formControlName="password">
  <div *ngIf="password.invalid && password.touched">
    Password must be at least 8 characters
  </div>

  <button [disabled]="form.invalid">Submit</button>
</form>
```

**Script:**
"FormBuilder is a service that provides shorthand for creating form models. Instead of writing `new FormControl('')` and `new FormGroup({})` everywhere, you inject `FormBuilder` and use `fb.group()`. The result is identical — it's purely syntactic convenience, but it makes larger forms much cleaner.

Each control is defined as an array — first element is the initial value, second is a validator or array of validators. We define getters for each control so the template can reference `email` directly instead of `form.get('email')` everywhere. In the template, you bind the form element with `[formGroup]` and each input gets `formControlName` matching the key in the group. The template is lean — almost no logic in the HTML. Structure, initial values, and validation are all in TypeScript where they're testable."

---

### [SLIDE 20: FormArray — Dynamic Form Lists]
**Code block:**
```typescript
@Component({ ... })
export class ContactFormComponent implements OnInit {
  form: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.form = this.fb.group({
      name: ['', Validators.required],
      phones: this.fb.array([
        this.fb.control('', Validators.required)  // start with one
      ])
    });
  }

  // Getter for the FormArray
  get phones() {
    return this.form.get('phones') as FormArray;
  }

  addPhone() {
    this.phones.push(this.fb.control('', Validators.required));
  }

  removePhone(index: number) {
    this.phones.removeAt(index);
  }
}
```
```html
<form [formGroup]="form">
  <input formControlName="name" placeholder="Name">

  <div formArrayName="phones">
    <div *ngFor="let phone of phones.controls; let i = index">
      <input [formControlName]="i" placeholder="Phone number">
      <button type="button" (click)="removePhone(i)">Remove</button>
    </div>
  </div>

  <button type="button" (click)="addPhone()">+ Add Phone</button>
</form>
```

**Script:**
"`FormArray` is what you reach for any time the user needs to add or remove items from a list dynamically. Here we have a contact form where a user can add multiple phone numbers.

In the component, `phones` is a `FormArray` inside the `FormGroup`. We expose it via a getter cast to `FormArray` so TypeScript knows it has array methods. `addPhone()` pushes a new `FormControl` onto the array. `removeAt(index)` removes one.

In the template, you use `formArrayName` to bind to the array, then `*ngFor` to loop over `phones.controls`. Each input gets `[formControlName]="i"` — the index — as its binding. Notice the square brackets there: the index is a variable, not a string literal, so we need property binding.

This pattern handles any dynamic list: addresses, line items, skills — anything where the count isn't fixed at design time."

---

## SECTION 9: FORM VALIDATION (8 minutes)

### [SLIDE 21: Built-in Validators]
**Code block:**
```typescript
// Reactive forms — pass as second arg to FormControl
this.fb.group({
  username:  ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
  age:       ['', [Validators.required, Validators.min(18), Validators.max(99)]],
  email:     ['', [Validators.required, Validators.email]],
  website:   ['', Validators.pattern('https?://.+')]
});
```
```html
<!-- Template-driven equivalents — HTML attributes -->
<input name="username" ngModel required minlength="3" maxlength="20">
<input name="age" ngModel required type="number" min="18" max="99">
<input name="email" ngModel required email>
```

**Script:**
"Angular ships with a solid set of built-in validators. `required`, `email` format, `minLength` and `maxLength` for strings, `min` and `max` for numeric values, and `pattern` for regex. For reactive forms, pass them as the second element in your control definition — either a single validator or an array. For template-driven forms, most of these map directly to HTML attributes and Angular picks them up automatically through `ngModel`. One naming quirk: the TypeScript class uses camelCase `Validators.minLength` but the HTML attribute is lowercase `minlength` — that trips people up occasionally."

---

### [SLIDE 22: Custom Validators — Sync]
**Code block:**
```typescript
// A custom validator is a function: AbstractControl → ValidationErrors | null
// Return null if valid. Return an object if invalid.

function noSpaces(control: AbstractControl): ValidationErrors | null {
  const hasSpaces = control.value?.includes(' ');
  return hasSpaces ? { noSpaces: true } : null;
}

function strongPassword(control: AbstractControl): ValidationErrors | null {
  const value: string = control.value || '';
  const hasUpper = /[A-Z]/.test(value);
  const hasNumber = /[0-9]/.test(value);
  return hasUpper && hasNumber ? null : { strongPassword: true };
}

// Using custom validators in FormBuilder
this.form = this.fb.group({
  username: ['', [Validators.required, noSpaces]],
  password: ['', [Validators.required, strongPassword]]
});
```
```html
<!-- Displaying custom errors in the template -->
<input formControlName="username">
<div *ngIf="form.get('username').errors?.noSpaces && form.get('username').touched">
  Username cannot contain spaces
</div>
<div *ngIf="form.get('password').errors?.strongPassword && form.get('password').touched">
  Password must contain an uppercase letter and a number
</div>
```

**Script:**
"Built-in validators won't cover every case. Custom validators are just functions — they take an `AbstractControl` and return either `null` if valid, or an object describing the error if invalid. That error object key is yours to define, and you check for that exact key in your template to show the right message.

Here we have two examples: `noSpaces` checks for spaces in a username, and `strongPassword` checks for an uppercase letter and a number. You pass them into `fb.group` just like any built-in validator. In the template, you access the errors object with optional chaining — `errors?.noSpaces` — because if the control is valid the errors object is null and you'd get a runtime error without that question mark.

Custom validators are just functions. You can put them in a shared file and reuse them across your entire application."

---

### [SLIDE 23: Cross-Field Validators — Group-Level Validation]
**Code block:**
```typescript
// A group-level validator checks multiple controls at once
function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirm  = group.get('confirmPassword')?.value;
  return password === confirm ? null : { passwordsMismatch: true };
}

// Apply the validator to the GROUP, not a single control
this.form = this.fb.group({
  password:        ['', [Validators.required, Validators.minLength(8)]],
  confirmPassword: ['', Validators.required]
}, { validators: passwordsMatch });  // <-- second arg to fb.group
```
```html
<!-- Error lives on the group, not on a specific control -->
<div *ngIf="form.errors?.passwordsMismatch && form.get('confirmPassword').touched">
  Passwords do not match
</div>
```

**Script:**
"Sometimes validation depends on more than one field — the classic example being password confirmation. This is where group-level validators come in.

The function looks the same as a control-level validator, but instead of receiving a single `FormControl`, it receives the entire `FormGroup` as an `AbstractControl`. You call `group.get('password')` and `group.get('confirmPassword')` to read both values, compare them, and return null or an error.

You attach it to the group by passing a second options argument to `fb.group` with a `validators` key. The error this produces lives on the *form group* itself, not on any individual control — so in the template you check `form.errors?.passwordsMismatch`, not a field's errors. That's the key difference from control-level validation."

---

### [SLIDE 24: Async Validators — Concept]
**Bullet points:**
- Used when validation requires an API call (e.g., checking username availability)
- Return an `Observable<ValidationErrors | null>` instead of the value directly
- Passed as the **third** argument to a `FormControl`, separate from sync validators
- The control enters a `pending` state while the async validator runs
- **Covered in depth in the Advanced Forms session**

**Script:**
"One more type of validator worth knowing about conceptually: async validators. These are for cases where you need to hit an API to validate — like checking if a username is already taken. Instead of returning an error object directly, you return an Observable that eventually emits either null or an error object.

You pass them as the third argument to a form control, separate from sync validators, and Angular handles them after all sync validators pass. While they're running, the control's `pending` property is true, so you can show a 'checking...' indicator in the UI.

We're not going deep on the implementation today — async validators work best as a standalone topic once you're comfortable with RxJS observables, so we'll come back to them in the Advanced Forms session. For now, just know they exist and what problem they solve."

---

## CLOSING & RECAP (2 minutes)

### [SLIDE 25: What We Covered Today]
**Two columns:**

**Routing**
- RouterModule, RouterLink, programmatic navigation
- Route params & query params
- Nested routes
- CanActivate, CanDeactivate (+ CanLoad/Resolve overview)
- Lazy loading

**Forms**
- Template-driven vs Reactive comparison
- FormBuilder, FormGroup, FormControl
- FormArray for dynamic lists
- Built-in validators
- Custom sync validators
- Cross-field group validators

---

### [SLIDE 26: Key Takeaways]
1. Routes map URLs to components — `<router-outlet>` is where they render
2. Use `this.router.navigate()` to navigate programmatically — you'll do this after almost every form submit
3. Guards protect routes — `CanActivate` for auth, `CanDeactivate` for unsaved data
4. Lazy loading keeps your initial bundle small — use `loadChildren` with dynamic imports
5. Template-driven forms are great for simplicity — reactive forms scale better
6. Always validate on both the client and server side

**Script:**
"Let's land the plane. Today we covered the full routing picture — basic configuration, route parameters, nested routes, programmatic navigation, guards, and lazy loading. Then we covered both form approaches — template-driven and reactive — FormBuilder, FormArray for dynamic lists, built-in validators, custom validators, and cross-field group validators.

These topics connect directly: you use route guards with your auth service, you navigate programmatically after a successful form submission, you use query params to persist filter state. They're not separate concepts — they're one toolkit. In the next session we'll go deeper on RxJS integration with reactive forms and tackle async validators properly. Good work today."
