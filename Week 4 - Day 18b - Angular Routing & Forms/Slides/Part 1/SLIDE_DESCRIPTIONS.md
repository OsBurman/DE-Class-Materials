# Day 18b — Part 1: Angular Routing & Navigation
## Slide Descriptions

---

### Slide 1: Title Slide
**"Angular Routing & Navigation"**
Subtitle: RouterModule, Route Configuration, Parameters, Nested Routes
Week 4 – Day 18b, Part 1

---

### Slide 2: Day 17b Recap + Today's Agenda

**Recap — Day 17b:**
- Services as singleton state holders (`@Injectable({ providedIn: 'root' })`)
- Dependency injection via constructor (`constructor(private myService: MyService)`)
- `@Input()` / `@Output()` for component communication
- `EventEmitter` for child → parent events

**Today — Part 1:**
- What the Angular Router does and why you need it
- Setting up the router (RouterModule / provideRouter)
- Defining routes with the Routes array
- `<router-outlet>` — where matched components render
- RouterLink, RouterLinkActive for declarative navigation
- Programmatic navigation with the Router service
- Route parameters (`:id`) with ActivatedRoute
- Query parameters — reading and writing
- Nested routes and child router-outlets

**Today — Part 2 (after break):**
- Route guards (CanActivate, CanDeactivate)
- Lazy loading for performance
- Template-driven and reactive forms
- Form validation (built-in and custom)

---

### Slide 3: The Problem — What the Router Solves

**Without a router:**
- One URL = one page
- Clicking a link = full page reload
- Browser history broken (Back button reloads the whole app)
- User can't bookmark or share a specific "view"

**With the Angular Router:**
- URL changes without a page reload (HTML5 History API)
- The right component swaps into the page based on the URL
- Browser Back/Forward works correctly
- Deep linking and bookmarking works

**Visual diagram:**
```
 ┌─────────────────────────────────────────┐
 │  App Shell (always rendered)            │
 │  ┌─────────────────────────────────┐    │
 │  │  <app-navbar>                   │    │
 │  └─────────────────────────────────┘    │
 │  ┌─────────────────────────────────┐    │
 │  │  <router-outlet>                │    │
 │  │  ↕ swaps based on URL:          │    │
 │  │  /home      → HomeComponent     │    │
 │  │  /products  → ProductsComponent │    │
 │  │  /cart      → CartComponent     │    │
 │  └─────────────────────────────────┘    │
 └─────────────────────────────────────────┘
```

**Angular Router is built-in** — no separate install needed. Part of `@angular/router`.

---

### Slide 4: Router Setup — Two Approaches

**NgModule approach (traditional — widely used in enterprise):**
```typescript
// app.module.ts
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

const routes: Routes = [ /* ... defined separately */ ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],  // forRoot = top-level config
  exports: [RouterModule]                   // export so components can use directives
})
export class AppModule {}
```

**Standalone approach (Angular 14+ — new projects):**
```typescript
// main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter }        from '@angular/router';
import { AppComponent }         from './app/app.component';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes)    // same routes array, no NgModule needed
  ]
});
```

**Key notes:**
- Both approaches use the exact same `Routes` array and the same directives (`routerLink`, `routerLinkActive`)
- `RouterModule.forRoot()` = one-time top-level setup; child modules use `RouterModule.forChild()`
- Enterprise codebases commonly use NgModule; new projects may use standalone
- The concepts (routes, outlet, link, guards) are identical regardless of setup style

---

### Slide 5: Route Configuration — The Routes Array

```typescript
// app.routes.ts (or inline in app.module.ts)
import { Routes } from '@angular/router';

const routes: Routes = [
  // Default redirect
  { path: '',              redirectTo: '/home', pathMatch: 'full' },

  // Static routes
  { path: 'home',          component: HomeComponent },
  { path: 'products',      component: ProductListComponent },
  { path: 'cart',          component: CartComponent },
  { path: 'login',         component: LoginComponent },

  // Dynamic segment — :id is a route parameter
  { path: 'products/:id',  component: ProductDetailComponent },

  // Wildcard — catch-all 404, MUST be last
  { path: '**',            component: NotFoundComponent },
];
```

**Rules:**
- Angular matches routes **top-to-bottom** — first match wins
- `path: ''` + `redirectTo` + `pathMatch: 'full'` = default redirect when URL is exactly `/`
- `pathMatch: 'full'` — do NOT use `'prefix'` here or it catches everything
- `path: '**'` — wildcard catch-all; must be the **last** entry
- No leading slash in path strings — `'products'` not `'/products'`
- Exact matching is the default for non-empty paths (unlike Angular v1)

---

### Slide 6: RouterOutlet — Where Matched Components Render

```html
<!-- app.component.html -->
<app-navbar></app-navbar>

<router-outlet></router-outlet>
<!-- ↑ Angular inserts the matched component AFTER this element in the DOM -->
<!-- It is a placeholder, not a wrapper — the component renders next to it -->

<app-footer></app-footer>
```

**What actually happens in the DOM when you navigate to `/products`:**
```html
<app-navbar>...</app-navbar>
<router-outlet></router-outlet>
<app-product-list>           ← inserted by router
  <h2>Products</h2>
  ...
</app-product-list>
<app-footer>...</app-footer>
```

**Key points:**
- `<router-outlet>` is a **directive** exported by `RouterModule`
- The matched component is a sibling, not a child, of the outlet element in the DOM
- You can have **named outlets** (advanced pattern) — `<router-outlet name="sidebar">`
- There is exactly one **primary** outlet per level of the route tree (more on this with nested routes)

---

### Slide 7: RouterLink and RouterLinkActive

**Declarative navigation — replace `<a href>` with `routerLink`:**
```html
<!-- Static path — no binding needed -->
<a routerLink="/products">All Products</a>
<a routerLink="/cart">Cart</a>

<!-- Dynamic path — use property binding with array syntax -->
<a [routerLink]="['/products', product.id]">{{ product.name }}</a>
<!-- Generates: /products/42 -->

<!-- With query params -->
<a [routerLink]="['/products']" [queryParams]="{ category: 'books' }">
  Books
</a>
```

**RouterLinkActive — adds a CSS class when the route is active:**
```html
<nav>
  <a routerLink="/home"     routerLinkActive="active-link">Home</a>
  <a routerLink="/products" routerLinkActive="active-link">Products</a>
  <a routerLink="/cart"     routerLinkActive="active-link">Cart</a>
</nav>

<!-- IMPORTANT: Prevent root path from always being active -->
<a
  routerLink="/home"
  routerLinkActive="active-link"
  [routerLinkActiveOptions]="{ exact: true }"
>
  Home
</a>
```

**Why not `<a href="/products">`?**
- `href` causes a **full page reload** — the entire Angular app restarts
- `routerLink` intercepts the click, updates the History API, and swaps the component — no reload

---

### Slide 8: Programmatic Navigation — The Router Service

```typescript
import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({ ... })
export class LoginComponent {
  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {}

  // Navigate to a static path
  goHome(): void {
    this.router.navigate(['/home']);
  }

  // Navigate with a dynamic segment
  viewProduct(id: number): void {
    this.router.navigate(['/products', id]);
    // Equivalent: this.router.navigateByUrl(`/products/${id}`);
  }

  // After login — replace history so Back doesn't return to login
  afterLogin(): void {
    this.router.navigate(['/dashboard'], { replaceUrl: true });
  }

  // After logout — clear forward/back history
  afterLogout(): void {
    this.router.navigate(['/login'], { replaceUrl: true });
  }

  // Navigate relative to current route
  goToChild(): void {
    this.router.navigate(['settings'], { relativeTo: this.route });
  }
}
```

**Two navigation methods:**
| Method | Use When |
|---|---|
| `navigate([])` | Preferred — array segments, relative navigation support |
| `navigateByUrl('')` | When you have a full URL string already built |

---

### Slide 9: Route Parameters — :id Segments

**Route definition:**
```typescript
{ path: 'products/:id', component: ProductDetailComponent }
```

**Accessing the parameter — Approach 1: snapshot**
```typescript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute }     from '@angular/router';

@Component({ ... })
export class ProductDetailComponent implements OnInit {
  product: Product | undefined;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    // snapshot: read the params at the moment the component is created
    const idStr = this.route.snapshot.paramMap.get('id');  // always a STRING
    const id    = Number(idStr);                           // convert!
    this.product = this.productService.getById(id);
  }
}
```

**⚠️ ALWAYS convert route params from string:**
```typescript
// Wrong — param is "42" (string), product.id is 42 (number)
products.find(p => p.id === this.route.snapshot.paramMap.get('id'))  // never matches!

// Correct
products.find(p => p.id === Number(this.route.snapshot.paramMap.get('id')))
```

**Multiple parameters:**
```typescript
// Route: /users/:userId/posts/:postId
const userId = Number(this.route.snapshot.paramMap.get('userId'));
const postId = Number(this.route.snapshot.paramMap.get('postId'));
```

---

### Slide 10: snapshot vs Observable — paramMap

**The limitation of snapshot:**
```typescript
// User navigates: /products/1 → /products/2
// Angular reuses the ProductDetailComponent instance (same component, new param)
// ngOnInit does NOT re-run — snapshot is STALE
```

**Approach 2: paramMap Observable (handles reuse)**
```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute }               from '@angular/router';
import { Subscription }                 from 'rxjs';

@Component({ ... })
export class ProductDetailComponent implements OnInit, OnDestroy {
  private sub!: Subscription;

  ngOnInit(): void {
    // Subscribe to live param changes — runs every time :id changes
    this.sub = this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      this.loadProduct(id);    // re-fetches when param changes
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();    // prevent memory leak
  }
}
```

**Decision guide:**

| Use `snapshot` when... | Use `paramMap` Observable when... |
|---|---|
| Navigate **away** to a different component | Stay on the **same component** with different params |
| Component is always fresh on entry | Navigating from `/products/1` to `/products/2` in-place |
| Simpler code — one-time read | Need to react to param changes without component teardown |

---

### Slide 11: Query Parameters

**Navigating with query params (programmatically):**
```typescript
// Produces URL: /products?category=electronics&sort=price&page=1
this.router.navigate(['/products'], {
  queryParams: { category: 'electronics', sort: 'price', page: 1 }
});

// Preserve existing query params while adding/changing one
this.router.navigate(['/products'], {
  queryParams: { sort: 'name' },
  queryParamsHandling: 'merge'   // keeps category=electronics, changes sort
});
```

**Navigating with query params (declaratively):**
```html
<a [routerLink]="['/products']" [queryParams]="{ category: 'books', sort: 'price' }">
  Books by Price
</a>
```

**Reading query params in the component:**
```typescript
ngOnInit(): void {
  // Observable — recommended for reactive filtering
  this.route.queryParamMap.subscribe(params => {
    this.category = params.get('category') ?? 'all';
    this.sort     = params.get('sort')     ?? 'name';
    this.page     = Number(params.get('page') ?? '1');
    this.loadProducts();
  });

  // Snapshot — for one-time read on init
  const category = this.route.snapshot.queryParamMap.get('category');
}
```

**Why query params over component state?**
- Filter/sort/page state lives in the URL
- Bookmarkable, shareable, survives page refresh
- Browser Back button undoes filter changes

---

### Slide 12: Nested Routes — Configuration

**When to use nested routes:**
- A parent "shell" component should always be visible (dashboard with sidebar)
- A section of the app has its own sub-navigation
- Shared layout (header, breadcrumb) for a group of related pages

**Nested route definition:**
```typescript
const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,       // ← always rendered for /dashboard/**
    children: [
      { path: '',         component: DashboardHomeComponent },   // /dashboard
      { path: 'profile',  component: ProfileComponent },         // /dashboard/profile
      { path: 'settings', component: SettingsComponent },        // /dashboard/settings
      { path: 'orders',   component: OrdersComponent },          // /dashboard/orders
      { path: '**',       component: NotFoundComponent }         // /dashboard/anything-else
    ]
  },

  // Top-level routes continue here
  { path: 'login',  component: LoginComponent },
  { path: '**',     component: NotFoundComponent }
];
```

**How it resolves:**
- `/dashboard` → renders `DashboardComponent`, inserts `DashboardHomeComponent` into its outlet
- `/dashboard/profile` → renders `DashboardComponent`, inserts `ProfileComponent` into its outlet
- `/login` → renders `LoginComponent` directly in the top-level outlet

---

### Slide 13: Nested Routes — Parent Component with Inner Outlet

```html
<!-- dashboard.component.html — the layout/shell component -->
<div class="dashboard-shell">
  <aside class="sidebar">
    <nav>
      <!-- Relative links — no leading slash -->
      <a routerLink="profile"  routerLinkActive="active">👤 Profile</a>
      <a routerLink="settings" routerLinkActive="active">⚙️ Settings</a>
      <a routerLink="orders"   routerLinkActive="active">📦 Orders</a>
    </nav>
  </aside>

  <main class="content">
    <router-outlet></router-outlet>  <!-- Children render here -->
  </main>
</div>
```

**Two router-outlets — how they relate:**
```
Top-level <router-outlet> (in app.component.html)
  → Renders DashboardComponent when URL is /dashboard/**

Inner <router-outlet> (in dashboard.component.html)
  → Renders the matched child (ProfileComponent, SettingsComponent, etc.)
```

**Relative vs absolute `routerLink` in nested context:**
- `routerLink="profile"` (no `/`) → relative to current route: `/dashboard/profile` ✅
- `routerLink="/profile"` (with `/`) → absolute root: `/profile` ❌ (wrong!)
- `routerLink="/dashboard/profile"` → absolute and explicit ✅

---

### Slide 14: Router Events — Navigation Lifecycle

```typescript
import { Router, NavigationStart, NavigationEnd,
         NavigationCancel, NavigationError } from '@angular/router';

@Component({ ... })
export class AppComponent {
  isLoading = false;

  constructor(private router: Router) {
    // router.events is an Observable stream of all navigation events
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.isLoading = true;   // show spinner
      }
      if (event instanceof NavigationEnd ||
          event instanceof NavigationCancel ||
          event instanceof NavigationError) {
        this.isLoading = false;  // hide spinner
      }
    });
  }
}
```

**Navigation event sequence (happy path):**
```
NavigationStart
  → RoutesRecognized
  → GuardsCheckStart / GuardsCheckEnd
  → ResolveStart / ResolveEnd
  → NavigationEnd
```

**Guard or error path:**
```
NavigationStart → GuardsCheckStart → NavigationCancel  (guard returned false)
NavigationStart → NavigationError                       (unexpected error)
```

**Common use cases:**
- Global loading spinner tied to NavigationStart/End
- Analytics: track page views on NavigationEnd
- Scroll-to-top on NavigationEnd

---

### Slide 15: Relative Navigation

```typescript
// Navigating relative to current route (inject ActivatedRoute too)
this.router.navigate(['../sibling'],  { relativeTo: this.route }); // up one, then sibling
this.router.navigate(['child'],       { relativeTo: this.route }); // one level down
this.router.navigate(['../../other'], { relativeTo: this.route }); // up two, then other
```

```html
<!-- In template — relative routerLink (no leading slash) -->
<a routerLink="profile">  → /dashboard/profile  (if on /dashboard)</a>
<a routerLink="../home">  → /home               (up one from /dashboard)</a>
<a routerLink="/cart">    → /cart               (absolute — always root)</a>
```

**Rule of thumb:**
- Use **relative** links inside a feature section (dashboard sub-pages linking to each other)
- Use **absolute** links when crossing between major sections of the app
- Always prefer `[routerLink]` array syntax for dynamic segments over string concatenation

---

### Slide 16: Complete Routing — File Structure

```
src/
  app/
    app.routes.ts               ← centralized routes definition
    app.component.ts/html       ← root component with top-level <router-outlet>
    core/
      guards/
        auth.guard.ts           ← (Part 2)
      services/
        auth.service.ts
    features/
      home/
        home.component.ts
      products/
        products.component.ts
        product-detail.component.ts
      cart/
        cart.component.ts
      dashboard/
        dashboard.component.ts  ← shell: sidebar + inner <router-outlet>
        dashboard.routes.ts     ← optional: child routes in own file
        profile/
          profile.component.ts
        settings/
          settings.component.ts
      auth/
        login.component.ts
    shared/
      navbar/
        navbar.component.ts
      not-found/
        not-found.component.ts
```

**Sample `app.routes.ts`:**
```typescript
export const routes: Routes = [
  { path: '',           redirectTo: '/home', pathMatch: 'full' },
  { path: 'home',       component: HomeComponent },
  { path: 'products',   component: ProductsComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'cart',       component: CartComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    children: [
      { path: '',        component: DashboardHomeComponent },
      { path: 'profile', component: ProfileComponent },
      { path: 'settings', component: SettingsComponent },
    ]
  },
  { path: 'login',      component: LoginComponent },
  { path: '**',         component: NotFoundComponent },
];
```

---

### Slide 17: Part 1 Summary

**Angular Router — the essentials:**

| Concept | API |
|---|---|
| Setup (NgModule) | `RouterModule.forRoot(routes)` |
| Setup (Standalone) | `provideRouter(routes)` in `main.ts` |
| Outlet placeholder | `<router-outlet>` in template |
| Declarative link | `routerLink="/path"` or `[routerLink]="['/path', id]"` |
| Active class | `routerLinkActive="class-name"` |
| Programmatic nav | `this.router.navigate(['/path', id])` |
| Route params | `this.route.snapshot.paramMap.get('id')` → convert with `Number()` |
| Live param changes | `this.route.paramMap.subscribe(params => ...)` |
| Query params | `this.router.navigate(['/path'], { queryParams: { key: val } })` |
| Read query params | `this.route.queryParamMap.subscribe(...)` |
| Nested routes | `children: [...]` in route config + inner `<router-outlet>` |
| Navigate back | `this.router.navigate(['../'], { relativeTo: this.route })` |
| Replace history | `{ replaceUrl: true }` option |

**Coming up in Part 2:**
- Route guards — protect routes before the component even loads
- Lazy loading — split your app into code chunks that download on demand
- Template-driven and reactive forms — collect and validate user input
