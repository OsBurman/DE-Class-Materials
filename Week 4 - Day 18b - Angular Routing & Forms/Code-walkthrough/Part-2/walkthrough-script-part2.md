# Day 18b — Part 2 Walkthrough Script
## Angular Route Guards, Lazy Loading & Forms

**Duration:** ~100 minutes  
**Files:** `01-route-guards.ts`, `02-lazy-loading.ts`, `03-template-driven-forms.ts`, `04-reactive-forms.ts`  
**Prerequisites:** Part 1 complete (RouterModule, route params, nested routes)

---

## Transition from Part 1

> "In Part 1 we covered the core routing mechanics — setting up routes, navigating, reading parameters, and building nested layouts. You now understand the skeleton of an Angular router.
>
> In Part 2 we go deeper on two fronts. First, we add **security and performance** to the router — route guards that protect pages and lazy loading that keeps your bundle lean. Then we tackle the second major topic of the day: **Angular Forms**, which have two completely different approaches worth knowing."

---

## Segment 1 — Route Guards (~30 min)

### Setup (2 min)

> "Open `01-route-guards.ts`. The goal of a route guard is simple: intercept a navigation and decide whether to allow it, block it, or redirect. Angular calls your guard function before activating any route — think of it as middleware for your router."

---

### 1.1 — CanActivate: Class-Based (Section 1, ~10 min)

> "The classic approach — a class that implements the `CanActivate` interface. Point to the `AuthGuard` class."

**Walk through:**
- `@Injectable({ providedIn: 'root' })` — the guard is a service; it lives in the DI container
- `implements CanActivate` — the interface contract requires a `canActivate()` method
- Method signature: takes `ActivatedRouteSnapshot` and `RouterStateSnapshot`
  - **Ask class:** "What useful thing is on `RouterStateSnapshot`?" → the current URL (`state.url`)
- Return type: `boolean | UrlTree | Observable<boolean | UrlTree>`
  - `true` → allow
  - `false` → block (user stays on current page)
  - `UrlTree` → block AND redirect to a different route
- Highlight `this.router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } })`
  - **Ask class:** "Why save the return URL?" → so after login we can send the user back where they tried to go

> "This is cleaner than returning `false` and calling `router.navigate()` separately — you express the redirect as a value."

**Route config:**
```typescript
{ path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] }
```

---

### 1.2 — Role Guard (Section 2, ~5 min)

> "What if some pages require a specific role — admin vs student? We use the route's `data` property to pass static configuration into the guard."

**Walk through:**
- `route.data['role']` — how the guard reads the required role from route config
- `canActivate: [AuthGuard, RoleGuard]` — multiple guards; all must pass, evaluated in order
- If AuthGuard fails first, RoleGuard is never called

> "This is the open/closed principle in action — you don't change the guard's code to add new roles, you change the route configuration."

---

### 1.3 — Functional Guards (Section 3, ~8 min)

> "Angular 15 introduced functional guards. Instead of a class, you write a plain arrow function. Let's look at `authGuard`."

**Walk through:**
- `CanActivateFn` — the type for a functional guard
- `inject()` works inside functional guards — Angular's DI context is active at the call site
- Same logic as the class guard, but no `@Injectable`, no class, no `implements`

> "This is the direction Angular is heading. Functional guards are easier to test — they're just functions. Compose them like functions. Older codebases use class guards, so learn both."

**Key difference in route config:**
- Class guard: `canActivate: [AuthGuard]` — the CLASS itself (Angular instantiates it)
- Functional guard: `canActivate: [authGuard]` — the FUNCTION reference directly

**Q&A prompt:** "If you have three functional guards on a route — auth, role, and subscription check — in what order does Angular evaluate them?" → left to right in the array; first failure stops the chain.

---

### 1.4 — CanDeactivate (Sections 4–5, ~7 min)

> "CanActivate guards the ENTRY to a route. CanDeactivate guards the EXIT — it runs when the user tries to navigate AWAY from the current component."

**Use case:** "You're on a form editing a course. You've made changes but haven't saved. The user accidentally clicks the nav bar. Without CanDeactivate, all changes are lost silently."

**Walk through:**
- `CanComponentDeactivate` interface — defines the contract the component must implement
- `UnsavedChangesGuard.canDeactivate(component)` — calls `component.canDeactivate()` and returns the result
- Component's `canDeactivate()` method — `confirm('Leave anyway?')` returns boolean
- Route config: `canDeactivate: [UnsavedChangesGuard]`

> "The guard delegates the decision TO the component — the component knows best whether it has unsaved state."

**Functional version:** Point out `unsavedChangesGuard` — same pattern, no class.

**Watch-out:** `confirm()` is a synchronous browser dialog — it blocks the UI thread. In production, use a modal dialog service that returns an Observable.

---

### 1.5 — Async Auth Guard (Section 6, ~3 min)

> "Real apps often get auth state from an Observable — Firebase, NgRx, etc. Angular handles this natively. The guard returns `Observable<boolean | UrlTree>` and Angular subscribes automatically."

**Key point:**
- You NEVER call `.subscribe()` yourself on guard return values
- Angular's router subscribes for you, takes the first emitted value, and decides the route outcome

---

### Segment 1 Summary (2 min)

> "Guards give you three superpowers on the router: protect pages with CanActivate, require roles by reading route data, and prevent data loss with CanDeactivate. Modern Angular favors functional guards — clean, composable, testable."

**Board diagram:**
```
User clicks /admin
     ↓
CanActivate: [authGuard, roleGuard]
     ↓ (both pass)
canActivateChild fires for any children
     ↓
Component activates
     ↓
User navigates away
     ↓
CanDeactivate runs → confirm or allow
```

---

## Segment 2 — Lazy Loading (~20 min)

### Setup (2 min)

> "Open `02-lazy-loading.ts`. This is a performance topic. We're going to shrink the initial JavaScript bundle your users download."

> "In Section 1 there's a comment explaining the core problem. Who can read it out loud?" ← cold call

---

### 2.1 — The Problem and Solution (Sections 1–2, ~5 min)

> "By default, Angular packages everything into `main.js`. A large app can have a 2MB+ bundle — users on mobile or slow connections stare at a loading spinner."

**Eager vs Lazy side-by-side (Section 2):**
- Eager: imports the component → goes into `main.js`
- Lazy: `loadChildren: () => import('./courses/courses.module').then(m => m.CoursesModule)`

Break down the lazy loading syntax:
- `loadChildren` — a function, NOT a string (old Angular used strings)
- `() => import(...)` — standard JS dynamic import, returns a Promise
- `.then(m => m.CoursesModule)` — from the module object, grab the NgModule class
- No `component` property — the entire feature module is loaded
- The path in `import()` is relative to the APP routing module

---

### 2.2 — Feature Module Structure (Sections 3–4, ~7 min)

> "A lazy-loaded route points to a feature module. That module must define its OWN routes with `RouterModule.forChild()`."

**Walk through `coursesRoutes`:**
- Paths are RELATIVE to the lazy-loaded parent (`courses`)
- `''` → matches `/courses` exactly
- `:id` → matches `/courses/123`

> "The golden rule: `forRoot()` in AppRoutingModule, ONCE. `forChild()` in every feature module. If you call `forRoot()` inside a feature module, you'll get duplicate router instances and mysterious bugs."

**Modern standalone approach (Section 4):**
- `loadComponent` for a single standalone component
- `loadChildren` returning `ADMIN_ROUTES` (an array, no NgModule needed)

> "If you're starting a new Angular 17+ project, you'll likely use standalone. Legacy codebases use NgModule. Both are common in the wild."

**Full app routes (Section 5):** Walk through which routes are eager (home, login) vs lazy (courses, admin, profile). Point out `canMatch` comment.

---

### 2.3 — Preloading Strategies (Sections 6–8, ~6 min)

> "Lazy loading solves the initial bundle size problem but creates a tiny delay on first navigation. Preloading is the compromise: small initial bundle, lazy modules load silently in the background."

**Show the table from Section 6:**
- `NoPreloading` (default) — pure lazy, delay on first nav
- `PreloadAllModules` — background-preloads all lazy modules after init
- Custom strategy — selective control

**Custom `SelectivePreloadingStrategy` (Section 7):**
- Implements `PreloadingStrategy` — one method: `preload(route, load)`
- Checks `route.data['preload']` — your flag
- Calls `load()` to trigger the module fetch, or `of(null)` to skip

> "This is the goldilocks approach — preload only the routes users are most likely to visit next."

**DevTools demo script (Section 9):**
> "In your app, open DevTools → Network → filter JS. Navigate to a lazy route and watch a new chunk file appear — that's the lazy module being loaded on demand. With PreloadAllModules, those chunks appear right after the initial page load, without any user action."

**Q&A prompt:** "You have an admin module and a reporting module. Admin is visited by 0.1% of users. Reporting is visited by 90%. Which would you preload?" → reporting

---

### Segment 2 Summary (2 min)

> "Lazy loading: use `loadChildren` with dynamic `import()`. Feature modules use `forChild()`. Preloading erases the first-navigation delay without sacrificing initial bundle size. In production Angular apps, almost everything except the home page and login should be lazy-loaded."

---

## Segment 3 — Template-Driven Forms (~20 min)

### Setup (2 min)

> "Open `03-template-driven-forms.ts`. Angular has two form systems. This first one — template-driven — keeps most logic in the HTML. It's great for simple forms and very approachable."

> "Requires `FormsModule` imported in your module. That import activates the `ngModel`, `ngForm`, and validator directives."

---

### 3.1 — ngModel and Two-Way Binding (Section 1, ~4 min)

> "The star of template-driven forms: `[(ngModel)]`. Let's decode the banana-in-a-box syntax."

Write on board:
```
[(ngModel)]  =  [ngModel] + (ngModelChange)
                 set value    update on change
```

> "It's syntactic sugar. Angular updates the input when your property changes, and updates your property when the input changes. Classic two-way binding."

- `name="name"` — **required** on every ngModel input. Angular uses this to register the control in the form. Without it, the control won't appear in `f.value`.

---

### 3.2 — ngForm and Template Variables (Section 2, ~6 min)

> "Look at the full form template. Two template reference variables at work."

**First: `#f="ngForm"` on the form element**
- `ngForm` is a directive Angular attaches automatically to every `<form>`
- `#f` captures the `NgForm` instance
- `f.valid`, `f.value`, `f.dirty`, `f.touched` — form-level state
- `[disabled]="f.invalid"` on the submit button — a common pattern

**Second: `#nameField="ngModel"` on the input**
- `ngModel` is the directive on the individual input
- `#nameField` captures the per-field `NgModel` instance
- `nameField.invalid && nameField.touched` — field-level state

**Walk through the state table (Section 3):**
> "Every control tracks these. And Angular adds CSS classes to match — `.ng-valid`, `.ng-invalid`, `.ng-touched`, `.ng-dirty`. You can style these directly in your CSS."

---

### 3.3 — Validators and Error Display (Sections 4 + template, ~5 min)

> "Built-in HTML5 attributes become Angular validators when FormsModule is active."

Walk through the error display pattern:
```html
<div *ngIf="nameField.invalid && nameField.touched">
  <span *ngIf="nameField.errors?.['required']">Name is required.</span>
  <span *ngIf="nameField.errors?.['minlength']">
    Min {{ nameField.errors?.['minlength'].requiredLength }} chars.
    You entered {{ nameField.errors?.['minlength'].actualLength }}.
  </span>
</div>
```

> "Why `touched` and not just `invalid`? If we showed errors immediately on page load, every empty field would be red before the user has touched anything. We wait until they've interacted with the field."

**`email` directive:** `<input type="email" email ngModel />` — the `email` attribute activates Angular's email format validator.

---

### 3.4 — Submission and Reset (Sections 5 + 7, ~3 min)

> "The submit handler pattern:"

```typescript
onSubmit(form: NgForm): void {
  if (form.invalid) {
    form.control.markAllAsTouched();  // show all errors at once
    return;
  }
  // use form.value
  form.reset();
}
```

> "`markAllAsTouched()` is the 'submit was clicked, show me everything wrong' pattern. `reset()` clears values AND resets all the state flags — pristine, untouched."

---

### Segment 3 Summary (2 min)

> "Template-driven: all logic in the HTML. `FormsModule`, `[(ngModel)]`, `#f="ngForm"`, `#field="ngModel"`. Error display uses field-level state. Great for login forms, contact forms, any 3–5 field form. For anything more complex — reactive forms."

---

## Segment 4 — Reactive Forms (~28 min)

### Setup (2 min)

> "Open `04-reactive-forms.ts`. In reactive forms, the form model lives in the COMPONENT CLASS. The template is just a view that binds to that model. This inverts the template-driven approach."

> "Requires `ReactiveFormsModule`. The key classes: `FormGroup`, `FormControl`, `FormArray`, `FormBuilder`, `Validators`."

---

### 4.1 — FormGroup and FormControl (Sections 1–2, ~7 min)

> "The comment at the top shows the manual setup. Then we look at `FormBuilder` which reduces boilerplate."

**Manual setup:**
```typescript
registrationForm = new FormGroup({
  name: new FormControl('', [Validators.required, Validators.minLength(3)]),
  email: new FormControl('', [Validators.required, Validators.email]),
});
```

- `FormControl(initialValue, syncValidators)` — second arg is an array of validators
- `FormGroup({})` — a named collection of controls

**Template binding:**
- `[formGroup]="registrationForm"` — binds the FormGroup to the `<form>` element
- `formControlName="name"` — binds a specific control by its string key (no brackets!)

> "Notice: no `#f="ngForm"`. No `[(ngModel)]`. The template and component communicate through the shared FormGroup object."

**FormBuilder shorthand:**
```typescript
fb.group({ name: ['', [Validators.required]] })
// is exactly the same as:
new FormGroup({ name: new FormControl('', [Validators.required]) })
```

---

### 4.2 — Reading Values and Errors (Section 4, ~4 min)

> "Accessing a control and its errors:"

```typescript
this.form.get('email')?.value
this.form.get('email')?.errors?.['required']
```

> "The `?.` is important — `get()` returns `AbstractControl | null`. Always use optional chaining."

**Getter shortcut pattern:**
```typescript
get email() { return this.registrationForm.get('email')!; }
```
> "Define a getter in the class, use `email.errors?.['required']` in the template — much cleaner than `.get('email')?.errors?.['required']` everywhere."

**Error display:**
```html
<div *ngIf="registrationForm.get('email')?.invalid &&
            registrationForm.get('email')?.touched">
```

---

### 4.3 — Custom Validators (Sections 5–6, ~7 min)

> "Walk through `noWhitespaceValidator`. A validator is just a function."

```typescript
export function noWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
  const hasWhitespace = (control.value ?? '').includes(' ');
  return hasWhitespace ? { noWhitespace: { actual: control.value } } : null;
}
```

- Returns `null` → valid ✅
- Returns an object → invalid ❌ — the object IS the errors value

**Anatomy of the error object:**
```typescript
{ noWhitespace: { actual: 'bad value' } }
// errors['noWhitespace'] → { actual: 'bad value' }
// you can include extra context in the payload
```

**Cross-field validator — `passwordMatchValidator` (Section 6):**
- Applied to the GROUP, not a field
- `group.get('password')` — access sibling fields through the group
- Usage: `fb.group({ ... }, { validators: passwordMatchValidator })`
- Error is on the FORM object: `registrationForm.errors?.['passwordMismatch']`

> "This is a common gotcha: where is the error? Field-level validators → errors on the control. Group-level validators → errors on the FormGroup."

**Q&A prompt:** "If you wanted a validator that checks email format AND ensures the domain is `.edu`, would you use a field validator or group validator?" → field validator — it only needs the one control's value

---

### 4.4 — Async Validators (Section 7, ~3 min)

> "Quick look at async validators — the pattern for checking server-side uniqueness."

```typescript
return timer(400).pipe(
  switchMap(() => {
    const isTaken = takenEmails.includes(control.value);
    return of(isTaken ? { emailTaken: true } : null);
  })
);
```

- `timer(400)` — debounce: wait 400ms after last keystroke before firing
- Returns `Observable<ValidationErrors | null>` — Angular subscribes, takes the first value
- While pending: `control.status === 'PENDING'`
- In template: `<span *ngIf="emailControl.pending">Checking...</span>`

---

### 4.5 — FormArray: Dynamic Fields (Sections 8–9, ~6 min)

> "FormArray is one of the biggest advantages of reactive forms over template-driven. It handles dynamic lists of fields."

**Walk through the skills example:**
- `fb.array([fb.control('', Validators.required)])` — starts with one field
- `get skills()` getter — typed as `FormArray` for IDE help
- `skills.push(fb.control(''))` — `addSkill()`
- `skills.removeAt(index)` — `removeSkill(i)`

**Template:**
```html
<div formArrayName="skills">
  <div *ngFor="let skill of skills.controls; let i = index">
    <input [formControlName]="i" />   <!-- note: [formControlName] with brackets here -->
    <button (click)="removeSkill(i)">Remove</button>
  </div>
</div>
```

> "Unlike `formControlName='name'` (string literal), we use `[formControlName]='i'` with brackets to bind the numeric index dynamically."

**valueChanges (Section 9):**
> "Every FormGroup and FormControl is also an Observable. `valueChanges` emits every time the user types. Use it for autosave, dependent field updates, real-time search — but always unsubscribe."

---

### 4.6 — setValue vs patchValue (Section 10, ~2 min)

> "When loading data from an API to pre-fill a form:"

- `setValue` — you must provide ALL fields or it throws
- `patchValue` — partial update, only the fields you pass

> "Use `patchValue` when loading from an API — you often don't want to clobber every field."

---

### Segment 4 Summary (3 min)

> "Reactive forms: model-first, class-first. FormGroup + FormControl + FormBuilder. Custom validators are plain functions. Cross-field validators go on the group. FormArray powers dynamic field lists. valueChanges makes the form reactive to user input as an Observable stream."

**Quick comparison:**
| | Template-Driven | Reactive |
|---|---|---|
| Model lives in | HTML template | Component class |
| `FormsModule` | ✅ | ❌ |
| `ReactiveFormsModule` | ❌ | ✅ |
| Dynamic fields | Hard | Easy (FormArray) |
| Unit testing | Hard | Easy |
| Custom validators | Directive | Function |

---

## Day Wrap-Up (5 min)

> "Today you built out the professional-grade Angular skillset — secured routes with guards, optimized loading with lazy modules, and built both styles of forms."

**What they can now do:**
- Lock down any route with `CanActivate` — class or functional
- Warn users before they lose unsaved work with `CanDeactivate`
- Lazy-load feature modules and understand preloading
- Build simple forms with template-driven (`ngModel`, `ngForm`)
- Build complex forms with reactive (`FormGroup`, `FormBuilder`, `FormArray`)
- Write custom and cross-field validators

---

## Q&A Prompts

1. "What's the difference between `canActivate` and `canActivateChild`?"
   - `canActivate` protects the route itself; `canActivateChild` protects ALL child routes of that parent

2. "When would you NOT want to use `PreloadAllModules`?"
   - When your app has many large modules and most users only visit a few pages; selective preloading is better

3. "Can you use `ngModel` in a reactive form?"
   - No — don't mix `FormsModule` and `ReactiveFormsModule` on the same form. Angular will warn you.

4. "What happens if a required field is disabled in a reactive form and you call `.value`?"
   - Disabled controls are excluded from `.value`. Use `.getRawValue()` to include them.

5. "Why does the cross-field validator need to be applied to the FormGroup and not to the confirmPassword control?"
   - The `confirmPassword` control alone doesn't have access to the `password` value. Only a group-level validator receives the parent group and can access both.

---

## Take-Home Exercises

1. **Guard chain:** Create an `AuthGuard` + `SubscriptionGuard` for a `/premium` route. The subscription guard should check `route.data['plan']` and redirect to `/upgrade` if the user's plan doesn't match.

2. **Lazy loading audit:** Look at an Angular app with 5+ features. Make a plan: which features should be lazy, which should be eager, and which should be preloaded using a selective strategy.

3. **Template-driven contact form:** Build a contact form (name, email, subject, message, urgency dropdown) with template-driven forms. Show inline errors only after touch. Prevent submit until valid.

4. **Reactive registration wizard:** Build a two-step form (personal info → account info) using reactive forms. Use FormBuilder for each step. Implement a password strength custom validator and a password-match cross-field validator. Store step values in a shared object and combine on final submit.

---

## Transition to Day 19b

> "Tomorrow in Day 19b we tackle Angular's HTTP client — making real API calls, handling responses with RxJS operators, and building a service layer. Everything you built today in forms feeds directly into that: form data gets submitted via `HttpClient`, errors come back from the API and map to your form's error state. See you then."

---

*Estimated total time: ~100 minutes (30 guards + 20 lazy loading + 20 template-driven + 28 reactive + 5 wrap-up)*
