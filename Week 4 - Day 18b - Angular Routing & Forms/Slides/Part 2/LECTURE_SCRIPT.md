# Day 18b — Part 2: Route Guards, Lazy Loading & Angular Forms
## Lecture Script

**Total time:** 60 minutes
**Delivery pace:** ~165 words/minute
**Format:** Verbatim instructor script with timing markers

---

## [00:00–02:00] Opening — Bridge from Part 1

Welcome back. In Part 1 we covered the Angular Router end to end — setting it up, defining routes, navigating with `routerLink` and `Router.navigate()`, reading parameters and query params with `ActivatedRoute`, and structuring nested routes with inner outlets. If your understanding of those fundamentals feels solid, we're in great shape.

Part 2 has three distinct sections. We start with route guards — the mechanism Angular uses to protect routes before a component even loads. Then we briefly cover lazy loading, which is how you split your application bundle and only download code when the user actually needs it. And then the rest of the session is forms. Angular has two complete, parallel form systems — template-driven and reactive — and we're going to cover both, including built-in and custom validation. A lot of ground, but it all fits together by the end. Let's go.

---

## [02:00–10:00] Route Guards — The Concept and CanActivate

Let me frame the problem. In Part 1, we built a routing setup where `/dashboard` leads to `DashboardComponent`. But right now, anyone can navigate to `/dashboard` whether they're logged in or not. You never want that on a real application. You need something that runs before the component loads, checks whether the user has permission, and either allows the navigation or redirects them somewhere else.

In React yesterday, we solved this with a `ProtectedRoute` component — a component that checked auth state and rendered either `<Outlet>` or `<Navigate>`. The guard logic was mixed into the component tree. Angular takes a different approach: it separates the guard logic entirely from the component. A guard is a standalone function or class that Angular calls during the navigation lifecycle, before the target component is ever instantiated. If the guard says no, the component never loads. That separation is cleaner — your component doesn't have to care about authorization at all, and you can apply the same guard to many routes without changing those components.

Angular has several guard types. `CanActivate` is the one you'll use most — it runs before entering a route. `CanDeactivate` runs before leaving — we'll do that right after. There's also `CanActivateChild` for guarding all children of a parent route, and `Resolve` for pre-fetching data before a component loads.

Let's build `CanActivate`. In modern Angular — version 15 and up — the recommended pattern is a functional guard. It's just an exported function with the type `CanActivateFn`. Inside the function, you use `inject()` to get any services you need. This is the same `inject()` function from Angular's core that lets you access the DI container outside of a constructor.

Here's the auth guard. You inject `AuthService` and check `authService.isLoggedIn()`. If that returns true, you return `true` and navigation proceeds. If the user is not logged in, you return `router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } })`. That's a `UrlTree` — Angular's way of saying "redirect to this URL instead of the one the user requested." Notice you're passing the attempted URL as a `returnUrl` query param. That way, after the user logs in, you can read `returnUrl` and send them right back to where they were trying to go. That's the polished user experience — no sending people to the home page after login just because they happened to be unauthenticated.

Applying the guard to a route is simple: add `canActivate: [authGuard]` to the route object. It's an array, so you can chain multiple guards — all of them have to return true for navigation to proceed. And that whole array runs before a single line of the component's constructor executes.

Now, you'll also encounter the older class-based guard pattern in existing codebases. A class that's decorated with `@Injectable({ providedIn: 'root' })` and implements the `CanActivate` interface. The `canActivate` method has the same signature, the same logic, same return type. The class gets constructor injection instead of `inject()`. Either way, the behavior is identical. Know both, prefer functional in new code.

---

## [10:00–18:00] CanDeactivate — Leaving Guards

`CanDeactivate` is less commonly used than `CanActivate`, but it solves a problem that every application with edit forms has. User opens a form, makes changes, then clicks a nav link without saving. All their work is gone. `CanDeactivate` lets you intercept that navigation and warn the user before it happens.

The pattern looks like this. You define an interface — `HasUnsavedChanges` — with a single method `hasUnsavedChanges()` that returns a boolean. Your guard is a `CanDeactivateFn` that's generic over that interface type. When the guard runs, it receives the component instance as its first argument — the actual live component that's about to be left behind. It calls `component.hasUnsavedChanges()`. If that returns true, it shows a confirmation dialog. If the user confirms, navigation proceeds. If they cancel, navigation is blocked and the user stays on the page.

Your component class implements the `HasUnsavedChanges` interface. It tracks a `isDirty` flag — set to `true` any time the user changes the form, set back to `false` after a successful save. The `hasUnsavedChanges()` method just returns `isDirty`.

A couple of important nuances. The `confirm()` call I showed in the slides returns a boolean synchronously — it works, but it's a browser-native dialog that you can't style. In production apps, you'd want a custom dialog component. The good news is that `CanDeactivateFn` can return not just `boolean`, but also `Promise<boolean>` or `Observable<boolean>`. So you can open a modal, wait for the user to click "Stay" or "Leave," and resolve the observable with the appropriate value. That's a pattern worth knowing exists even if we're not implementing it fully today.

You apply it to the route with `canDeactivate: [unsavedChangesGuard]`. Now if the user tries to navigate away from an edit form with unsaved changes, they get a chance to confirm before anything is lost.

---

## [18:00–25:00] Lazy Loading

Let's talk about lazy loading. This is a performance optimization, and it's one that pays off significantly in large applications.

By default, Angular compiles your entire application into a bundle — one main JavaScript file. When a user opens your app, their browser downloads that entire bundle before Angular can start. If your app has a full admin section, a reporting dashboard, a user management panel — all of that code gets downloaded on initial load even if this user is a regular customer who will never see the admin section. That wastes bandwidth, increases load time, and frustrates users.

Lazy loading lets you split your application into separate code chunks. The main bundle stays small — just enough to boot the app and render the first page. Each feature section becomes its own chunk file that only downloads when the user actually navigates to that route. If a user never goes to `/admin`, they never download the admin code. Period.

The implementation uses JavaScript's dynamic import syntax. Instead of `component: AdminComponent` in your route, you write `loadChildren` with a function that returns a dynamic import. The import returns a promise of a module, and you pull out the specific module class from it. When Angular sees `loadChildren`, it knows that this route's code is in a separate chunk. It will not include that code in the main bundle. At runtime, when the user first navigates to `/admin`, Angular downloads the chunk and renders the component.

In Angular 14 and newer with standalone components, there's an even simpler variant: `loadComponent` instead of `loadChildren`. You dynamically import the component file directly, pull out the component class, and that's it. No separate module file needed.

One important detail: you can still apply guards to lazy-loaded routes. The guard runs synchronously — if it returns false, Angular doesn't even initiate the download. So unauthorized users don't pay the bandwidth cost of downloading admin code they're not allowed to use.

---

## [25:00–37:00] Template-Driven Forms

Now we shift into forms, and we have a lot to cover. Angular has two complete, parallel form systems. Let's start with the older but simpler one: template-driven forms.

Template-driven forms are exactly what the name says — you define the form logic in the HTML template. The form structure, the validation rules, the binding — it's all in the template. The component class just holds the data model and the submit handler.

The first thing you need is `FormsModule` imported into your module. That import activates several directives: `ngModel` for two-way binding, `ngForm` which gets automatically attached to any `<form>` element, and `ngModelGroup` for grouping related fields.

Let's build a login form. The `<form>` element gets a template reference variable: `#loginForm="ngForm"`. That gives you a reference to the `NgForm` directive that Angular automatically attaches to the form. You can read the overall form validity from it — `loginForm.valid`, `loginForm.invalid` — and pass it to your submit handler.

Each input that you want to track needs two things: a `name` attribute and `[(ngModel)]`. The `name` attribute is mandatory — `ngForm` uses it to register the control in the form group it manages internally. Without a name, the input is invisible to the form. The `[(ngModel)]` is the two-way binding — the square brackets push the current value to the input, the parentheses listen for changes and update your component property. When the user types, `credentials.email` updates immediately.

Validators in template-driven forms are HTML attributes. `required` marks the field as mandatory. `email` triggers Angular's email format check. `minlength="8"` sets a minimum length. These attributes look like standard HTML but Angular intercepts them and adds proper validation logic.

To display error messages, you need two things: a template reference to the control itself (`#emailInput="ngModel"`) and a conditional block that checks the control's state. The pattern you'll use constantly is: show the error only when `emailInput.invalid && emailInput.touched`. Why the `touched` condition? Because when the form first loads, every field is invalid — the required email field is empty. But you don't want to show an error message before the user has even had a chance to type anything. `touched` becomes true only after the user has focused and blurred that field. So the combination "invalid and touched" means "the user tried and failed" — which is exactly when you want to show the error.

Inside the error block, you check `emailInput.errors?.['required']` and `emailInput.errors?.['email']` for specific messages. The `errors` property is an object where each key is the name of a failing validator. When the field passes all validators, `errors` is `null`.

The form state tracks a lot for you: `valid`/`invalid`, `touched`/`untouched` (blurred or not), `dirty`/`pristine` (changed or not). Angular also adds CSS classes — `ng-valid`, `ng-invalid`, `ng-touched`, `ng-dirty` — directly to the DOM elements. You can use those classes in your stylesheets to add red borders to invalid touched fields without any JavaScript.

The component class is refreshingly simple for template-driven forms. You have the data model — `credentials = { email: '', password: '' }` — and a submit handler that receives the `NgForm` reference and checks `form.valid` before doing anything.

---

## [37:00–50:00] Reactive Forms

Now let's talk about reactive forms. Reactive forms flip the template-driven approach: the form structure lives in TypeScript, and the template just binds to it. Same end result — a working, validated form — but a fundamentally different architecture.

To use reactive forms, you import `ReactiveFormsModule` instead of `FormsModule`.

The core building blocks are `FormControl`, `FormGroup`, and `FormArray`. A `FormControl` represents one input field — it holds the current value, the validation rules, and the current state. A `FormGroup` is a named collection of `FormControl` instances — it represents one form, or a section of a form. `FormArray` is a dynamic list of controls — useful for things like adding or removing items from a list, but we'll keep that as something to know exists.

Let me show you a registration form using raw `FormGroup` and `FormControl`. In `ngOnInit`, you construct the form group: `new FormGroup({ ... })`. Each key in the object is a field name, and the value is a `new FormControl`. `FormControl` takes two arguments: the initial value (an empty string here) and an array of validators.

Validators are imported from `@angular/forms` as the `Validators` object. `Validators.required`, `Validators.email`, `Validators.minLength(8)` — these are functions that return validator functions, and you put them in the array. When a control fails a validator, the corresponding key appears in `control.errors`.

Now, this is already cleaner than template-driven for validation, because all your validation logic is in one place in TypeScript. But the syntax is still verbose — `new FormControl` and `new FormGroup` everywhere. Angular provides `FormBuilder` to address that.

`FormBuilder` is an injectable service. You inject it via the constructor: `constructor(private fb: FormBuilder) {}`. Then instead of `new FormGroup({ name: new FormControl('', validators) })`, you write `this.fb.group({ name: ['', validators] })`. The array shorthand — initial value, then validators — is much cleaner. Same result, much less ceremony. Most real projects use `FormBuilder` rather than constructing `FormGroup` and `FormControl` directly.

Now the template. This is where reactive and template-driven differ most visibly. In the form element, you write `[formGroup]="registerForm"` — property binding that attaches your TypeScript `FormGroup` to this `<form>` element. On each input, you write `formControlName="name"` — a string attribute that tells Angular which `FormControl` from the group this input should bind to. No `[(ngModel)]` in reactive forms. The `FormGroup` drives the state. Angular connects the input's value to the `FormControl` and keeps them in sync.

Error display in reactive forms is similar but you access controls via `registerForm.get('name')`. That returns the `FormControl` for that field, and then you check `.invalid`, `.touched`, and `.errors` the same as in template-driven forms. The `?.` optional chaining is there because `get` can return null if the field name is wrong — it's defensive coding.

One important reactive feature is the ability to read or set values programmatically at any time. `registerForm.get('email')?.value` gives you the current email. `registerForm.patchValue({ name: 'Alice' })` sets specific fields without touching others. `registerForm.setValue({ name: 'Alice', email: 'a@a.com', ... })` sets all fields at once — but requires every field to be included. These methods are particularly useful when you load existing data into an edit form.

I want to be clear about the comparison so you can make good decisions in practice. Template-driven is faster to write for simple forms. Two to four fields, basic required and email validation — template-driven is totally appropriate. But reactive forms scale much better. Dynamic forms where fields are added and removed at runtime — use `FormArray` in reactive. Complex cross-field validation — reactive. Unit testing form logic without a browser — reactive, because your `FormGroup` is just a TypeScript object. Most professional Angular projects use reactive forms by default and only reach for template-driven for extremely simple cases.

---

## [50:00–57:00] Custom Validators

Built-in validators cover most cases, but sometimes your app has domain-specific rules. Maybe usernames can't contain spaces. Maybe two fields have to match. Custom validators handle this.

A `ValidatorFn` is a function that takes an `AbstractControl` — that's the base type for both `FormControl` and `FormGroup` — and returns either `null` if the control is valid, or an error object if it's not.

The pattern is a factory function that returns the validator. You write a function that takes any configuration parameters and returns another function — the actual validator. The inner function receives the control, checks the value, and returns `null` or an error object.

For a "no spaces" validator: the factory has no parameters, the inner function checks if `control.value` contains a space, and returns `{ noSpaces: { value: control.value } }` if it does or `null` if not. The error key — `noSpaces` in this case — is how the template identifies this specific failure: `control.errors?.['noSpaces']`.

Cross-field validators are applied at the `FormGroup` level, not the individual `FormControl` level. The classic example is password confirmation — both password and confirm fields need to have the same value, but neither field individually knows about the other. So you write a validator that receives the entire group, reads both values, and returns an error on the group if they don't match. The error appears on `form.errors` rather than `form.get('password')?.errors`.

In your `FormBuilder.group()` call, the second argument is an options object. Pass your group-level validator there: `{ validators: passwordMatchValidator }`. In the template, check `form.errors?.['passwordMismatch']` to display the cross-field error message.

There are also async validators — the third argument to `FormControl`. These return an Observable or Promise rather than a direct value. The use case is checking uniqueness against a server — "is this email already registered?" You can't do that synchronously. The async validator calls your service, gets a response, and maps the result to either `null` or an error object. Angular shows a `pending` state on the control while the async validator is running, which you can use to display a loading indicator. We won't implement one today, but know this capability exists for when you need it.

---

## [57:00–60:00] Day 18b Summary + Bridge to Day 19b

Let's wrap up. Today was two parts and a lot of material, so let me give you the summary in one pass.

Routing: you set up the Angular Router with a `Routes` array, you use `<router-outlet>` as the render target, you navigate declaratively with `routerLink` and programmatically with `Router.navigate()`. You read URL data with `ActivatedRoute` — route params are always strings, always convert them. Query params go in the URL for shareable state. Nested routes use `children` and an inner outlet in the parent component.

Guards: `CanActivate` intercepts navigation before the component loads and either allows it or redirects. Functional guards with `inject()` are the modern pattern. `CanDeactivate` catches navigation away from a page — implement the interface in your component, return `isDirty` state so the guard can warn the user. Lazy loading uses `loadChildren` with a dynamic import to split your bundle and download code on demand.

Forms: template-driven uses `FormsModule`, `[(ngModel)]`, and HTML attributes for validation — the form model lives in the template. Reactive uses `ReactiveFormsModule`, `FormGroup`, `FormControl`, and `FormBuilder` — the form model lives in TypeScript. Both approaches track validity, touched, dirty, and errors. Built-in validators cover required, email, min/max length. Custom validators are plain functions that return null or an error object.

On Thursday in Day 19b, you're getting Angular HTTP and RxJS. Everything that called `.subscribe()` today — `paramMap.subscribe()`, `queryParamMap.subscribe()` — that was RxJS. We just used it without fully explaining it. Day 19b is when we go deep on Observables, the pipe method, operators like `map`, `filter`, `switchMap`, and `catchError`. And you'll see how `HttpClient` builds on all of that — every HTTP call returns an Observable that you can transform, combine, and handle errors on. Plus interceptors, which let you inject an authorization header into every single outgoing request without touching each individual service call. You're going to see how all of this connects. Great work today — see you Thursday.
