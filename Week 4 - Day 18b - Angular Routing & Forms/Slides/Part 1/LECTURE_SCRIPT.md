# Day 18b — Part 1: Angular Routing & Navigation
## Lecture Script

**Total time:** 60 minutes
**Delivery pace:** ~165 words/minute
**Format:** Verbatim instructor script with timing markers

---

## [00:00–02:00] Opening — Bridge from Day 17b

Good morning, everyone. Yesterday in Day 17b we went deep into Angular services and dependency injection — we built singleton services using `@Injectable({ providedIn: 'root' })`, we saw how Angular's DI system hands the same instance to every component that asks for it, and we worked with `@Input` and `@Output` to wire components together. That pattern — services as the single source of truth — is going to come back in a few minutes, because the Router itself is a service. `ActivatedRoute` is a service. Everything in Angular gets injected the same way.

Today we are doing two massive topics that complete the Angular picture. Part 1 is the Angular Router — how Angular handles navigation between views, how the URL stays in sync with what the user sees, and how you read parameters out of the URL. Part 2 after the break is route guards and forms. By the end of today, you will have the full Angular skill set that you need for any real project. Let's get into it.

---

## [02:00–08:00] What the Router Solves — The SPA Navigation Problem

Let's start with the problem. If you have used plain HTML and a server-rendered site before, you know how navigation works: user clicks a link, browser sends a GET request to the server, server returns a new HTML page, browser loads it fresh. Simple. But also slow — you have to re-download and re-execute everything on every click.

Angular is a Single Page Application framework. There is one HTML file. Angular loads once. And from that point on, Angular manages what you see by swapping components in and out — not by doing page reloads. But that immediately creates a problem: if the URL never changes, the user can't bookmark a specific product page. They can't share a link to the cart. The browser's Back button does nothing useful. That breaks every expectation a user has about how the web works.

The Angular Router solves this by hooking into something called the HTML5 History API. Every modern browser has this API, and it lets JavaScript update the URL in the address bar — and the browser's history stack — without actually requesting a new page from the server. The router intercepts every link click and navigation call, pushes or replaces an entry in the browser history, and then decides which Angular component to render. The result: the URL looks exactly like a normal multi-page website, the Back button works correctly, bookmarks work, sharing links works — but there's never a full page reload.

So think about the diagram on your screen. You have an App Shell — that's `AppComponent` — and inside it there's a navbar that always renders. Below the navbar is a slot called `<router-outlet>`. That slot is where the router injects the right component based on the URL. Go to `/products` — Angular renders `ProductsComponent` in that slot. Go to `/cart` — it renders `CartComponent`. The navbar stays, the shell stays, only the inner component swaps. That is the entire mental model.

---

## [08:00–16:00] Router Setup and Route Configuration

Let's look at how you actually wire this up. Angular's router is part of the framework — it's in the `@angular/router` package, nothing to install separately. You do have to configure it, though.

There are two common patterns depending on how your project is set up. The traditional approach uses NgModules. In your `AppModule`, you import `RouterModule` and call `RouterModule.forRoot()` passing in your routes array. The `forRoot` method is specifically for the top-level app configuration — you call it once, in the root module only. Child feature modules use `RouterModule.forChild()` instead, which I'll mention again when we get to lazy loading. You also need to export `RouterModule` from your `AppModule` so its directives — `routerLink`, `routerLinkActive`, `routerOutlet` — are available in your component templates.

The second, more modern approach for Angular 14 and newer is standalone components. Here there's no `AppModule` at all. In `main.ts` you call `bootstrapApplication` and pass a `providers` array, and one of those providers is `provideRouter(routes)`. Same routes array, same behavior, just no module. Most new Angular projects are heading this direction. If you see either pattern in the wild, know that the routes, the directives, the guards — they all work identically.

Now the routes array itself. This is where all your navigation logic lives. Each entry in the array is a route object with at minimum a `path` and a `component`. The `path` is the URL segment — no leading slash, just the word like `'products'` or `'cart'`. The `component` is the Angular component class that should render when that path is active.

A few special entries you'll use in every app: first, the empty path entry. `path: ''` with `redirectTo: '/home'` and `pathMatch: 'full'` — this handles the bare root URL. When someone navigates to just your domain with no path, they get redirected to `/home`. The `pathMatch: 'full'` is critical here — if you use `'prefix'` on an empty string it matches everything and your app breaks. Always use `'full'` with the empty path redirect.

Second, the wildcard catch-all: `path: '**'` with a `NotFoundComponent`. This is your 404 handler. And it must be the last entry in your array, because Angular matches routes top to bottom, and a wildcard would catch everything if it came first. Angular takes the first match it finds — there's no "best match" logic, just first match.

You can also define dynamic segments with a colon: `'products/:id'`. That colon marks `:id` as a route parameter — a placeholder that captures whatever the user put in that position in the URL. We'll read that value in a moment.

---

## [16:00–23:00] RouterOutlet, RouterLink, and RouterLinkActive

Once your routes are configured, you need two things in your templates: a place to render the matched component, and links that trigger the router.

The `<router-outlet>` directive is the render target. You put it in `AppComponent`'s template, typically below your navbar. When a route matches, Angular renders the matched component right after the outlet element in the DOM. Not inside it — after it. The outlet itself just marks the location; it's a sibling, not a parent wrapper.

Now for links. The wrong thing to write is `<a href="/products">`. That works, but it causes a full page reload — the browser makes a real HTTP request, downloads the page fresh, and your entire Angular application restarts from scratch. Every piece of state is gone. That is never what you want in a SPA.

The right thing is `routerLink`. Write `<a routerLink="/products">All Products</a>`. Angular intercepts that click, updates the history API, and swaps the component — no reload, state preserved, fast. For static paths, you can just use the string attribute syntax. For dynamic paths where you're composing segments, use property binding with an array: `[routerLink]="['/products', product.id]"`. Angular joins those segments with slashes. So if `product.id` is 42, the result is `/products/42`.

The `routerLinkActive` directive adds a CSS class to the element when its link's route is currently active. You just write `routerLinkActive="active-link"` and Angular does the rest. When the URL matches that link, the class appears. When the URL changes away, the class disappears. This is how you highlight the current nav item without writing any JavaScript.

One gotcha: the root path `/` is a prefix of every URL, so without extra configuration, the home link always has the active class. Fix this with `[routerLinkActiveOptions]="{ exact: true }"` on the home link. Now it only activates when the URL is exactly `/` — no more false positives.

---

## [23:00–32:00] Programmatic Navigation — The Router Service

Sometimes you need to navigate in response to something other than a click — after a form submission succeeds, after a login call returns, after a timer fires. That's where programmatic navigation comes in.

You inject the `Router` service in your component's constructor — same dependency injection pattern you learned yesterday. `constructor(private router: Router) {}`. Then you call `this.router.navigate()`.

The `navigate` method takes an array of path segments as its first argument. For a static path: `this.router.navigate(['/home'])`. For a dynamic one: `this.router.navigate(['/products', id])` where `id` is a variable. Angular joins the segments. This array approach is the preferred way because it gives you type safety and makes it easy to compose paths.

There's also `navigateByUrl` which takes a full URL string — useful when you have a pre-built URL from something like a return URL query param. Both work.

The second argument to `navigate` is an options object. The most important option is `replaceUrl: true`. When you navigate normally, Angular pushes a new entry onto the browser history stack. The user can press Back and return to the previous page. Sometimes you explicitly don't want that — after a user logs in, you don't want the Back button to take them back to the login page. After logout, same thing. `replaceUrl: true` replaces the current history entry instead of pushing a new one. The user simply cannot navigate back to where they came from.

Another option is `relativeTo`. When you pass `relativeTo: this.route` (where `this.route` is an injected `ActivatedRoute`), the path segments are resolved relative to the current route rather than from the root. So if you're on `/dashboard` and you call `this.router.navigate(['settings'], { relativeTo: this.route })`, you end up at `/dashboard/settings`. Useful inside nested route structures.

---

## [32:00–43:00] Route Parameters — ActivatedRoute

Let's talk about reading data from the URL. The most common case is route parameters — those dynamic segments like `:id` in `/products/:id`.

You inject another service called `ActivatedRoute`. This is not the same as `Router`. `Router` is for navigating to places. `ActivatedRoute` is for reading information about the place you currently are. Constructor injection, same as always: `constructor(private route: ActivatedRoute) {}`.

The route parameters are accessible via `paramMap`, and you have two ways to read them. The first is the snapshot approach: `this.route.snapshot.paramMap.get('id')`. Snapshot gives you the state of the route at the moment your component was initialized. It's simple, it works great in most cases, and it returns a string.

That string part is important — stop me if you've heard this one before from Day 18a. Route parameters are always strings. If you have a product ID that's a number in your data, and you do `products.find(p => p.id === this.route.snapshot.paramMap.get('id'))`, you're comparing a number against a string and you will never find a match. Always convert: `const id = Number(this.route.snapshot.paramMap.get('id'))`. Every single time.

Now, the snapshot approach has one limitation. Angular tries to be efficient by reusing component instances when possible. If a user is on `/products/1` and navigates to `/products/2`, Angular might keep the same `ProductDetailComponent` alive rather than destroying and recreating it. If the component is reused, `ngOnInit` doesn't run again — which means your snapshot read doesn't update. The component still shows product 1.

The fix is the Observable approach: subscribe to `this.route.paramMap` as a live stream. Every time the parameters change, your subscription callback fires, and you can re-fetch the data. `this.route.paramMap.subscribe(params => { const id = Number(params.get('id')); this.loadProduct(id); })`. This works correctly regardless of whether Angular reuses the component or recreates it.

The tradeoff: subscriptions need to be cleaned up to prevent memory leaks. You implement `OnDestroy`, store the subscription in a property, and call `.unsubscribe()` in `ngOnDestroy`. Angular 16 introduced `takeUntilDestroyed` as a cleaner alternative that handles this automatically — but for now, the manual approach is what you'll encounter in most codebases.

Decision guide: use snapshot when you're confident navigation to a new set of params will always create a fresh component instance — like navigating from `/products` to `/products/1`, where the component changes entirely. Use the Observable approach when you might navigate from `/products/1` to `/products/2` and expect the same component to update in place.

Multiple params work the same way: `this.route.snapshot.paramMap.get('userId')` and `this.route.snapshot.paramMap.get('postId')` for a route like `/users/:userId/posts/:postId`.

---

## [43:00–51:00] Query Parameters

Query parameters are the key-value pairs after the question mark in a URL: `/products?category=electronics&sort=price&page=2`. They're different from route params in that they're optional, they don't affect which route is matched, and they're perfect for storing UI state that should be shareable and bookmarkable — filters, sort order, search terms, page numbers.

To navigate with query params programmatically, pass a `queryParams` object as part of the navigate options:
```
this.router.navigate(['/products'], {
  queryParams: { category: 'electronics', sort: 'price', page: 1 }
})
```

In a template, use the `queryParams` binding alongside `routerLink`:
```
[routerLink]="['/products']" [queryParams]="{ category: 'books' }"
```

Reading them back in the component works just like route params: `this.route.queryParamMap.subscribe(params => { this.category = params.get('category') ?? 'all'; })`. The `?? 'all'` is a nullish coalescing default — if the param is absent, fall back to `'all'`.

There's also a useful option when you want to change one query param without wiping out the rest. Pass `queryParamsHandling: 'merge'` and Angular will merge the new params with the existing ones rather than replacing them entirely.

Why does this matter? Think about a product listing page. The user selects category "Electronics." That's stored in the URL. Then they sort by price — the category should still be there. With `queryParamsHandling: 'merge'`, updating sort preserves category. Without it, every navigation call would reset all params.

The big benefit of query params over component state is persistence. If a user bookmarks `/products?category=electronics&sort=price` and comes back tomorrow, they land right where they left off. If they share that link with a colleague, the colleague sees the same filtered view. You get all that for free just by keeping state in the URL instead of in a component variable.

---

## [51:00–58:00] Nested Routes

The last major concept for Part 1 is nested routes. This is the pattern where a parent component stays on screen and only the inner content swaps based on a sub-path.

Classic example: a dashboard. When the user is in the dashboard section — whether they're on `/dashboard`, `/dashboard/profile`, or `/dashboard/settings` — the dashboard sidebar and header should always be visible. Only the main content area should change. That's a nested route.

Here's how you set it up in the routes array. Your dashboard route gets a `component` — the `DashboardComponent`, which acts as the layout shell — and a `children` array. Inside `children`, you have the same kind of route objects: empty path for the default view, `'profile'` for the profile page, `'settings'` for settings.

The parent `DashboardComponent` must have its own `<router-outlet>` in its template. When Angular resolves `/dashboard/profile`, it uses the top-level outlet to render `DashboardComponent`, and then it uses `DashboardComponent`'s inner outlet to render `ProfileComponent`. Two outlets, two levels.

Inside the parent template, your nav links are relative — `routerLink="profile"` with no leading slash. That resolves relative to the current route's context, so it becomes `/dashboard/profile`. If you wrote `routerLink="/profile"` with a leading slash, that would go to root `/profile` — probably not what you want.

An empty path child route is the equivalent of an index route from React Router — it defines what renders when the user is at exactly the parent path with nothing after it. Without an empty path child, navigating to `/dashboard` would render the `DashboardComponent` shell with an empty outlet — nothing in the main content area.

The wildcard `**` inside children catches invalid sub-paths. So `/dashboard/doesntexist` hits the nested wildcard rather than the top-level one. You can provide a different NotFound experience for the dashboard section if you want, or reuse the same component.

---

## [58:00–60:00] Part 1 Summary + Bridge to Part 2

Let's land the plane on Part 1. You have everything you need to build a fully navigable Angular application. You know how to set up the router, define your routes, use `<router-outlet>` as the render target, navigate declaratively with `routerLink` and programmatically with `Router.navigate()`. You know how to read route parameters with `ActivatedRoute` — convert those strings! — and query parameters for shareable UI state. And you know how nested routes give you persistent layout shells around swappable inner content.

In Part 2, we're going to tackle two more big areas. First, route guards — what do you do when certain routes should only be accessible to logged-in users? You need something that runs before the component loads and redirects unauthorized visitors. That's `CanActivate`. And we'll look at `CanDeactivate` too, which fires before a user leaves a page — perfect for "you have unsaved changes" warnings.

Then we're going to move into forms. Angular has two complete form systems — template-driven and reactive — and you need to know both because you'll encounter both in real projects. We'll build real forms with real validation by the end of the session. Take a quick break, and we'll pick back up in a few minutes.
