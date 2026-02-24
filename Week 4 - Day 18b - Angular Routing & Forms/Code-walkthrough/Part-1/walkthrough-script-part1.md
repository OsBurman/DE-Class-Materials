# Part 1 Walkthrough Script — Day 18b: Angular Routing & Forms
## RouterModule · Route Config · Route Parameters · Query Params · Nested Routes
### Estimated time: ~90 minutes

---

## Before You Start

**Recap from Day 17b (Angular Services & DI):**

> "Yesterday we built Angular services and used dependency injection — `@Injectable`, constructor injection, `BehaviorSubject`, `InjectionToken`. You now know how to share data between sibling components through a service.
>
> Today we're adding the second major structural pillar of Angular apps: **routing**. Until now every demo we've written was a single view. Real apps have multiple pages. Angular's router lets us map URLs to components, pass data through the URL, and protect routes with guards. That's Part 1. In Part 2 we build forms — both template-driven and reactive — because almost every routed page has some kind of form.
>
> Let's start with a question: in a traditional multi-page website, how do you get from one page to another? You click a link, the browser makes a new HTTP request, and the server sends a new HTML document. Angular is a Single Page Application — there's ONE HTML file. How does it fake navigation? That's what the Angular Router does."

---

## File: `01-routing-and-navigation.ts`

### [INTRO — 3 min]

> "Open `01-routing-and-navigation.ts`. The Angular router is part of the `@angular/router` package — it's separate from the core framework, which means it's optional (useful for embedded widgets that don't need navigation). But for full apps you always use it.
>
> Let's look at the imports at the top. I want to name each one because each plays a specific role."

Walk through the imports:

> - **`RouterModule`** — "The NgModule that registers the router service and all its directives."
> - **`Routes`** — "Just a TypeScript type alias for `Array<Route>`. You use it to type your route config array."
> - **`Router`** — "The router SERVICE — inject this when you need to navigate programmatically."
> - **`RouterLink`** — "A directive you put on anchor tags and buttons to navigate without a page reload."
> - **`RouterLinkActive`** — "A directive that adds a CSS class to a link when its route is currently active."

---

### [SECTION 2 — Routes array — 10 min]

> "Now look at the `APP_ROUTES` constant. This is the route configuration — a plain JavaScript array of objects. Each object maps a URL path to a component."

Point to the empty-string redirect:

> "The first entry has `path: ''` — the empty string matches the root URL `/`. We redirect it to `'home'`. Notice `pathMatch: 'full'`. **Watch out:** For empty paths, you MUST add `pathMatch: 'full'`. Without it, Angular matches every URL (because every URL 'starts with' an empty string) and you get an infinite redirect loop."

> "**Ask the class:** What does `pathMatch: 'full'` mean? Right — the whole URL must equal the path, not just start with it."

Point to the wildcard:

> "The last entry is `path: '**'` — the wildcard. It catches any URL that didn't match anything above it. ALWAYS put it last. If you put it first, every URL hits the NotFound page."

> "Notice: paths don't start with `/`. Angular adds the slash. If you accidentally write `'/home'` with a leading slash you'll get a runtime error."

---

### [SECTION 3 — AppRoutingModule — 5 min]

> "Look at `AppRoutingModule`. We call `RouterModule.forRoot(APP_ROUTES)`. The `forRoot` method:
> 1. Creates the singleton Router service
> 2. Registers the route configuration
> 3. Sets up the browser History API integration
>
> You call `forRoot` EXACTLY ONCE — in the root AppModule. In feature modules (lazy-loaded) you call `forChild` instead."

> "**Watch out:** Calling `forRoot` in a feature module is a common mistake. It creates a second Router instance which breaks routing entirely. Rule: `forRoot` in `AppModule`, `forChild` in feature modules."

---

### [SECTION 4 — NavComponent: RouterLink and RouterLinkActive — 12 min]

> "Scroll to `NavComponent`. This is the navigation bar. Look at the first anchor tag — it has three attributes."

Point to `[routerLink]`:

> "`[routerLink]="['/home']"` — the square brackets mean this is an Angular property binding. We pass an array. Why an array? Because it's composable — you can build paths with params: `['/courses', courseId]` → `/courses/5`. For simple paths, you can also write `routerLink="/home"` as a string — but the array form is the recommended convention."

Point to `routerLinkActive`:

> "`routerLinkActive="active"` — when the current URL matches this link's route, Angular adds the CSS class `active` to this element. No manual JavaScript needed. You define the class in your CSS."

Point to `[routerLinkActiveOptions]`:

> "`[routerLinkActiveOptions]="{ exact: true }"` — this is critical for the home link. Without `exact`, `/home` would be considered active on EVERY page because Angular checks 'does the URL start with `/home`'. Wait — that's not right. Actually the real gotcha is for `''` (root). If your NavLink is to `'/'` (or `''`), without `exact: true` it matches every route. With `exact: true` it only matches when the URL is exactly `/home`."

> "**Ask the class:** If I remove `{ exact: true }` from the Home link, what happens when I navigate to `/courses`? Right — Home appears highlighted because `/courses` starts with `/`. With exact, only an exact `/home` URL activates it."

---

### [SECTION 5 — Programmatic navigation with Router service — 8 min]

> "Look at the `goToLogin()` method in `NavComponent`. We inject `private router: Router` in the constructor — this is the Angular Router service."

> "`router.navigate(['/login'])` is the code equivalent of clicking a `[routerLink]`. You'd use this when navigation happens as a RESULT of an action — after a form submits successfully, after an API call completes, after a user confirms a dialog."

Point to the `queryParams` option:

> "We're also adding `queryParams: { returnUrl: '/dashboard' }`. This appends `?returnUrl=%2Fdashboard` to the URL. Why? So after the user logs in, we can read that param and redirect them back to where they were. This is a very common real-world pattern."

> "Other navigate options:
> - `replaceUrl: true` — replaces the current entry in browser history instead of pushing a new one. Use this for login redirects so the user can't hit Back and land on the login page again.
> - `relativeTo: this.route` — navigate relative to the current route rather than from the root."

---

### [SECTION 6 — router-outlet — 5 min]

> "Scroll to `AppComponent`. The entire template is just: NavBar + `<router-outlet>` + footer. The `<router-outlet>` is the slot where the matched component renders."

> "**Watch out:** If you forget `<router-outlet>`, the route URL changes in the browser bar but NOTHING renders on screen. This is one of the most confusing issues for Angular beginners. Always check: does my parent component have `<router-outlet>`?"

> "The NavBar is OUTSIDE `<router-outlet>`. That means it renders on every page — which is what we want for persistent navigation."

> "**Transition:** Great — we can navigate between pages. But what about passing data through the URL? Like `/courses/5` or `/courses?category=frontend`. That's what File 2 covers."

---

---

## File: `02-route-parameters-and-query-params.ts`

### [INTRO — 3 min]

> "Open `02-route-parameters-and-query-params.ts`. There are two types of URL-based data in Angular routing:
>
> 1. **Route parameters** — segments IN the path: `/courses/5` (the `5` is a param)
> 2. **Query parameters** — after the `?`: `/courses?category=frontend&page=2`
>
> Each is accessed via the `ActivatedRoute` service — inject it into the component that needs to read the URL."

---

### [SECTION 1 — Route param declaration — 3 min]

> "Look at Section 1 — the comment block showing route config. You declare a route param with a colon prefix: `{ path: 'courses/:courseId', component: CourseDetailComponent }`. The `:courseId` part is the slot — any value in that URL position gets captured under the key `courseId`."

> "**Ask the class:** What value does `courseId` have when the URL is `/courses/react`? Right — the string `'react'`. It doesn't have to be a number."

---

### [SECTION 2 — snapshot vs observable — 12 min]

> "Scroll to `CourseDetailComponent`. In `ngOnInit`, look at the two options — A and B."

> "**Option A — snapshot:** `this.route.snapshot.paramMap.get('courseId')`. Quick and simple — reads the param value ONCE when the component initializes. This is fine when navigating TO this component always creates a NEW component instance — which is the default."

> "**Option B — observable:** `this.route.paramMap.subscribe(params => {...})`. This subscribes to an Observable that emits a new value every time the param changes WITHOUT destroying and recreating the component."

> "**Ask the class:** When would the same component instance be reused with a different param? Right — imagine a Next Course button on the detail page. Angular is smart about performance — if you navigate from `/courses/1` to `/courses/2` and the same component is mapped to both routes, Angular might reuse the existing instance. If you used snapshot, `ngOnInit` would NOT re-run, and you'd be stuck showing Course 1 data with Course 2's URL."

> "**Best practice:** Use the observable when there are any navigation links that stay on the same component type. Use snapshot when the component is always freshly created."

> "**Watch out:** Always unsubscribe in `ngOnDestroy` — or use the `async` pipe / `takeUntilDestroyed()` operator. Our code stores the sub in `this.paramSub` and calls `unsubscribe()` in `ngOnDestroy`."

Point to the `Number(id)` conversion:

> "Route params are ALWAYS strings. `'5' !== 5` in strict equality. If you look up a course by id and forget to convert, `COURSES.find(c => c.id === '5')` returns undefined every time. Always convert: `Number(id)` or `parseInt(id, 10)`."

---

### [SECTION 3–4 — Query params — 12 min]

> "Now look at `CourseListComponent`. This reads query parameters — the `?` part of the URL."

> "`this.route.queryParamMap.subscribe(params => {...})` — same observable pattern as paramMap, just for query params. We read `category`, `level`, and `page` and use them to filter our course list."

> "Why use query params for filters instead of component state?  Three reasons:"
> 1. "Shareable — I can copy `/courses?category=frontend&level=beginner` and send it to a colleague."
> 2. "Bookmarkable — the user can save the filtered view."
> 3. "Back button works — pressing Back restores the previous filter state."

Point to `applyFilter`:

> "In `applyFilter`, we call `router.navigate([], { relativeTo: this.route, queryParams: {...}, queryParamsHandling: 'merge' })`. Two things to note:"

> "First: `[]` as the path means 'stay on the current route' — no path change, just update the query string."

> "Second: `queryParamsHandling: 'merge'`. **Watch out:** Without `merge`, calling `navigate` with just `{ category: 'frontend' }` would REPLACE all query params, wiping out the current `level` and `page` values. With `merge`, only the specified param updates — the others are preserved. This is the correct pattern for filter UIs."

---

### [SECTION 5 — Fragment navigation — 5 min]

> "Look at the `CourseDetailComponent` template — there are anchor tags using `[fragment]="'overview'"`. Fragments are the `#section` part of the URL — they scroll the page to an element with that `id` attribute."

> "In your component you can read the fragment with `this.route.snapshot.fragment`. It's rarely needed — mainly for 'jump to section' links within a long page."

> "**Transition:** We can navigate to pages and pass data through the URL. But what about complex page structures — a dashboard with a sidebar that stays visible? That's nested routes."

---

---

## File: `03-nested-routes.ts`

### [INTRO — 3 min]

> "Open `03-nested-routes.ts`. Nested routes let you build layouts where part of the page is FIXED (like a sidebar) and part changes based on the URL. The router renders child components INSIDE a parent component's own `<router-outlet>`."

---

### [SECTION 3 — DashboardComponent with inner outlet — 10 min]

> "Look at `DashboardComponent`. The template has a persistent sidebar on the left and a `<router-outlet>` on the right. That outlet is the SECOND level of outlet in our app:
>
> AppComponent's `<router-outlet>` → renders DashboardComponent
> DashboardComponent's `<router-outlet>` → renders the child page (Overview, Courses, Settings)
>
> Two levels of outlets, two levels of nesting. The sidebar stays visible no matter which child is active."

Draw on board:

```
AppComponent
  <router-outlet>  ← renders DashboardComponent

    DashboardComponent
      <aside> ← always visible (sidebar)
      <router-outlet> ← renders Overview / Courses / Settings
```

---

### [SECTION 4 — Children route config — 10 min]

> "Scroll to `DASHBOARD_ROUTES`. Look at the `children` array inside the `dashboard` route. The child paths are RELATIVE — they get appended to the parent path."

> "The first child has `path: '', redirectTo: 'overview', pathMatch: 'full'`. This is the default child — visiting `/dashboard` alone redirects you to `/dashboard/overview`. Same `pathMatch: 'full'` rule applies for empty child paths as for root routes."

> "**Watch out:** Child paths must NOT start with `/`. `path: '/overview'` would navigate to the root `/overview`, not `/dashboard/overview`. No leading slash in nested routes."

Point to `courses/:id`:

> "The deeply nested route `courses/:id` maps to `EnrolledCourseDetailComponent`. Even though it's nested three levels deep in the config, it still renders in DashboardComponent's outlet. The URL becomes `/dashboard/courses/2`."

---

### [SECTION 2 — Accessing parent params from a child — 5 min]

> "Look at `EnrolledCourseDetailComponent`. It reads the `:id` param from its OWN paramMap. The comment shows how to access a PARENT route's params: `this.route.parent?.snapshot.paramMap.get('userId')`. You rarely need this, but it's useful in deeply nested routes where the parent carries meaningful context."

---

### [SECTION 5 — Named outlets — 3 min]

> "Scroll to Section 5 — the comment block about named outlets. I want you to know this exists — named outlets let you have TWO router-outlets on the same page, each rendering a different component at the same time. The classic use case is a side panel that slides in alongside the main content."

> "We won't code this up today — it's an advanced feature. The core nested route pattern you just learned handles 95% of real-world needs."

---

### [PART 1 WRAP-UP — 3 min]

> "Let's recap Part 1:
>
> - `RouterModule.forRoot(routes)` registers routing in the root module. Child modules use `forChild`.
> - The Routes array maps paths to components. Empty path needs `pathMatch: 'full'`. Wildcard `**` goes last.
> - `RouterLink` navigates without reload. `RouterLinkActive` adds active CSS class. `{ exact: true }` for root links.
> - `Router.navigate()` for programmatic navigation. `queryParams` and `replaceUrl` options.
> - `ActivatedRoute` provides `paramMap` and `queryParamMap` — use snapshot for one-time reads, observable for reuse.
> - Route params and query params are always STRINGS — convert to number when needed.
> - Nested routes use `children`. Parent components MUST have their own `<router-outlet>`. Child paths have no leading slash.
>
> Take 10 minutes. Part 2 is route guards, lazy loading, and forms."

---

## Q&A Prompts for Part 1

1. "What's the difference between `RouterLink` and `Router.navigate()`? When would you use each?"
2. "Why does an empty-path redirect need `pathMatch: 'full'`?"
3. "If I navigate from `/courses/1` to `/courses/2` using the observable paramMap, what happens? What if I used snapshot?"
4. "What does `queryParamsHandling: 'merge'` do and why is it important for a filter UI?"
5. "If I have nested routes and a child never appears on screen, what's the most likely cause?"
