# Walkthrough Script — Part 2: Custom Pipes, Services & DI, Encapsulation
## Day 17b | Week 4 | ~90 minutes

---

## Pre-Session Setup

- [ ] Open `Part-2/` folder in VS Code
- [ ] Angular app running: `ng serve` (localhost:4200)
- [ ] Browser DevTools open — Elements and Console tabs
- [ ] Whiteboard / slide with the injector hierarchy diagram ready

---

## Recap From Part 1 (3 min)

> "Quick checkpoint. In Part 1 we covered:"

- `@Input` / `@Output` — data flows down, events flow up
- `*ngIf`, `*ngFor`, `*ngSwitch` — structural directives that control the DOM
- `ngClass`, `ngStyle` — attribute directives for styling
- Built-in pipes — uppercase, date, currency, async

> "In Part 2 we build on all of that. We'll write our own pipes, then learn the single most important Angular architecture concept: Services and Dependency Injection."

---

## Segment 1 — Custom Pipes (20 min)

**File:** `01-custom-pipes.ts`

---

### 1a. What makes a custom pipe? (3 min)

> "Building a custom pipe has two requirements: the `@Pipe` decorator and the `PipeTransform` interface. That's it."

Write on board:
```ts
@Pipe({ name: 'myPipe' })
export class MyPipe implements PipeTransform {
  transform(value: any, ...args: any[]): any {
    return transformedValue;
  }
}
```

> "The `name` in `@Pipe` is what you use in the template: `{{ value | myPipe }}`. The `transform` method receives the value on the left side of the pipe, and any arguments after the colon."

> "Generate one with the CLI: `ng generate pipe truncate` — it creates the file AND registers it in your module."

---

### 1b. TruncatePipe (5 min)

Scroll to SECTION 1 — `TruncatePipe`.

> "Our first pipe solves a real problem: long text in a card UI. We want to show the first 80 characters and add an ellipsis."

```ts
transform(value: string, maxLength: number = 80, suffix: string = '…'): string {
  if (!value) return '';
  if (value.length <= maxLength) return value;
  return value.slice(0, maxLength).trimEnd() + suffix;
}
```

> "Three parameters: the value itself, plus two optional arguments with defaults. `maxLength` defaults to 80, `suffix` defaults to the ellipsis character."

Show the template usage:
```html
{{ longDescription | truncate }}           <!-- uses defaults -->
{{ longDescription | truncate:50 }}        <!-- custom max, default suffix -->
{{ longDescription | truncate:30:'[read more]' }}  <!-- custom both -->
```

> "Arguments in the template are separated by colons. Notice you don't write the first argument — that's always the piped value. The colon arguments correspond to parameters 2, 3, 4…"

Ask: "What happens if `value` is null or undefined?" → The guard at the top returns an empty string. Always handle null in pipes.

---

### 1c. FilterByPipe (4 min)

Scroll to SECTION 2 — `FilterByPipe`.

> "This pipe filters an array of objects by a property name and search term."

```ts
transform<T>(items: T[], property: string, searchTerm: string): T[]
```

```html
<li *ngFor="let c of courses | filterBy:'title':'react'">
```

> "The first colon arg is the property name to search on, the second is the search term. This is a generic pipe — it works on any array of objects, not just courses."

Ask: "What does `pure: true` mean on the `@Pipe` decorator?" → Pure pipes only re-run when the input VALUE REFERENCE changes. If you push to an array without creating a new reference, a pure pipe won't see the change. This is actually a performance feature — Angular doesn't re-run the pipe on every change detection cycle.

---

### 1d. TimeAgoPipe (4 min)

Scroll to SECTION 3 — `TimeAgoPipe`.

> "This is a formatting pipe — it takes a raw Date and returns a human-readable relative time string. This is the kind of thing you'd see in a Twitter feed or comment section."

Walk through the logic:
```ts
const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);
if (seconds < 60) return `${seconds} seconds ago`;
const minutes = Math.floor(seconds / 60);
...
```

> "Each bracket computes the next unit of time by dividing down. Once we find the right bucket, we return a human-readable string."

Demo: look at the posts array — some have timestamps set to hours ago, days ago. Show the output.

---

### 1e. Impure Pipes (4 min)

Scroll to SECTION 4 — `SearchFilterPipe`.

> "The `searchFilter` pipe has `pure: false`. This makes it an IMPURE pipe."

```ts
@Pipe({ name: 'searchFilter', pure: false })
```

> "Impure pipes are re-evaluated on EVERY change detection cycle — even if nothing changed. For a live search field, that's what we want: as the user types, the filter updates immediately."

```html
<input [(ngModel)]="searchQuery" />
<li *ngFor="let c of courses | searchFilter:searchQuery">
```

> "As `searchQuery` changes, Angular runs change detection, the impure pipe re-runs, and the filtered list updates."

**Watch out:**
> "Impure pipes can be a performance problem if your transform function is expensive or your list is huge. For complex filtering, a computed property in the component class is often better — it only updates when you explicitly recalculate it."

**Registering pipes:**
> "Don't forget — every custom pipe must be declared in an NgModule's `declarations` array, just like components. If you forget, you'll get 'The pipe 'truncate' could not be found'. The CLI's `ng generate pipe` does this for you automatically."

---

## Segment 2 — Services and Dependency Injection (35 min)

**File:** `02-services-and-di.ts`

---

### 2a. What problem does a service solve? (4 min)

> "Before we look at code, let's understand the motivation."

Draw on board:
```
Without a service:
AppComponent
  ├── CourseBrowserComponent  (has courses array)
  └── EnrolledBadgeComponent  (needs enrolled list) ← how does it get the data?

The only option without services:
  • Lift state up to AppComponent
  • Pass it down through every component in between
  • That's prop drilling — it gets messy fast
```

> "Services solve this. Instead of the data living in a component, it lives in a service. Any component that needs it just injects the service."

```
With a service:
CourseService  ← ONE source of truth
  ← CourseBrowserComponent injects it
  ← EnrolledBadgeComponent injects it
  ← HeaderComponent injects it
  All get the SAME data, no prop drilling
```

---

### 2b. Creating a service — CourseService (6 min)

Scroll to SECTION 1 — `CourseService`.

```ts
@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private courses: Course[] = [...];
```

> "The `@Injectable` decorator tells Angular: 'this class can participate in Dependency Injection — it can receive injected dependencies AND can be injected into other classes.'"

> "`providedIn: 'root'` means Angular registers this service with the ROOT injector. There will be ONE instance shared across the entire application. This is the standard way to create a singleton service."

Point to the private array:
> "The data is private. Components can't reach in and mutate it. They have to call the public methods — `getAll()`, `enroll()`, `unenroll()`. This is encapsulation at the service level."

---

### 2c. Injecting the service (5 min)

Scroll to SECTION 2 — `CourseBrowserComponent`.

```ts
constructor(private courseService: CourseService) {}
```

> "Constructor injection. Angular reads the constructor parameter's TYPE — `CourseService` — and says 'I know how to provide that, I'll look it up in the injector.' The component doesn't create the service, it just declares that it needs one."

> "The `private` keyword is TypeScript shorthand that BOTH declares `this.courseService` as a property AND assigns the injected value to it in one line."

```ts
ngOnInit(): void {
  this.courses = this.courseService.getAll();
}
```

> "The service is available immediately in `ngOnInit` because the constructor already ran. Never call methods that rely on `@ViewChild` or projected content in ngOnInit though — those aren't ready yet."

Demo: add console.log in the service constructor to prove it only runs ONCE even though multiple components inject it.

---

### 2d. Sharing data between sibling components (6 min)

Scroll to SECTION 3 — `CourseSiblingListComponent` and `EnrolledCoursesComponent`.

> "Both of these components are SIBLINGS — neither is the parent of the other. Watch what happens when we inject the same service into both."

Point to both constructors:
```ts
// In CourseSiblingListComponent:
constructor(private svc: CourseService) {}

// In EnrolledCoursesComponent:
constructor(private svc: CourseService) {}
```

> "Angular provides the SAME instance to both. When `CourseSiblingListComponent` calls `svc.enroll(id)`, the service's internal array is mutated. When `EnrolledCoursesComponent` calls `svc.getEnrolled()`, it reads from the same array. They're looking at the same data."

Demo: click Enroll in the list, then click Refresh in the enrolled component.

> "You might notice the user has to click Refresh. That's a limitation of the manual approach. Let's see a better way."

---

### 2e. BehaviorSubject pattern — reactive data sharing (8 min)

Scroll to SECTION 4 — `CartService`.

> "Instead of components pulling data on demand, we push data to them. The `BehaviorSubject` is perfect for this."

```ts
private cartSubject = new BehaviorSubject<CartItem[]>([]);
cart$: Observable<CartItem[]> = this.cartSubject.asObservable();
```

> "`BehaviorSubject` is an Observable that always remembers its current value and immediately emits it to new subscribers. So if a component subscribes AFTER the cart already has items, it gets the current cart immediately."

> "We expose it as an Observable using `.asObservable()`. This prevents components from calling `.next()` directly — they have to go through the service's public methods. The service is the only thing that can change the data."

Show `CartBadgeComponent`:
```ts
ngOnInit(): void {
  this.sub = this.cart.cart$.subscribe(items => {
    this.cartItems = items;
  });
}

ngOnDestroy(): void {
  this.sub.unsubscribe();
}
```

> "The component subscribes in ngOnInit and unsubscribes in ngOnDestroy. Whenever the cart changes, Angular pushes the new value to the component and triggers change detection automatically — no manual refresh button needed."

**Watch out:**
> "Always unsubscribe in ngOnDestroy! If you don't, the subscription lives on after the component is destroyed. This is one of the most common Angular memory leaks."

Demo: add items from `AddToCartComponent` — the badge updates instantly.

---

### 2f. Providers and injector hierarchy (6 min)

> "Time for a more conceptual section — understanding WHERE providers live and what that means for instance sharing."

Draw the injector hierarchy on the board:
```
ROOT INJECTOR (AppModule, providedIn:'root')
    └── MODULE INJECTOR (FeatureModule, providers:[...])
          └── COMPONENT INJECTOR (providers:[...] in @Component)
                └── CHILD COMPONENT INJECTOR
```

> "When a component asks for a service, Angular walks UP the injector tree until it finds a provider. The first match wins."

**Root — singleton:**
```ts
@Injectable({ providedIn: 'root' })
export class GlobalNotificationService {}
```
> "One instance shared everywhere. This is what you want for services like authentication, notifications, or cart."

**Component-level provider:**
```ts
@Component({
  selector: 'app-counter',
  providers: [SomeService]  // new instance per component instance
})
```
> "Each INSTANCE of this component gets its own INSTANCE of the service. Useful when you have a list of items and each needs independent state — like a separate form state per item in a list."

Ask: "If I inject `CourseService` into a child component but also list it in the parent's `providers`, what happens?"
→ The child gets the PARENT's instance, not the root instance. The parent-level provider shadows the root.

---

### 2g. InjectionToken (3 min)

Scroll to SECTION 6 — `API_BASE_URL`.

> "What if you need to inject a string — like an API base URL — rather than a class? You can't use a class as the token because it's not a class."

```ts
export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL');
```
```ts
// In AppModule:
{ provide: API_BASE_URL, useValue: 'https://api.example.com' }

// In service constructor:
constructor(@Inject(API_BASE_URL) private baseUrl: string) {}
```

> "InjectionToken creates a typed token you can use for any value — strings, objects, booleans, factory functions. The string `'API_BASE_URL'` is just a human-readable label for error messages."

---

## Segment 3 — Component Encapsulation (15 min)

**File:** `03-component-encapsulation.ts`

---

### 3a. The problem encapsulation solves (2 min)

> "When you write CSS in a component's `styles` array, you expect it to only affect that component. But in plain HTML, CSS is global. How does Angular prevent styles from leaking?"

---

### 3b. Emulated (default) (4 min)

Scroll to SECTION 1 — `EmulatedEncapsulationComponent`.

> "With `Emulated` — the default — Angular adds a unique attribute like `_ngcontent-abc-c1` to every element this component creates. It then rewrites your CSS to include that attribute."

Inspect in DevTools — show the generated attribute on the DOM elements.

> "Your `.card { border: 2px solid navy }` becomes `.card[_ngcontent-abc-c1] { border: 2px solid navy }`. No other element in the app has that attribute, so the style only applies here. No actual Shadow DOM is used — Angular fakes it with attributes."

---

### 3c. ShadowDom (2 min)

Scroll to SECTION 2.

> "ShadowDom mode uses the browser's native Shadow DOM API. Open DevTools — you'll see a `#shadow-root` boundary under the element. CSS from the outside can't penetrate a Shadow DOM boundary."

> "Use this for component libraries where you need ironclad style isolation. The trade-off: global styles from the app won't apply either, and it's slightly harder to override styles from outside."

---

### 3d. None (2 min)

Scroll to SECTION 3.

> "ViewEncapsulation.None removes all scoping. Your styles are injected into the document head as global styles."

**Watch out:**
> "This is dangerous if you use generic selectors. If you write `p { color: red }` in a None component, EVERY paragraph in your application turns red. Always use very specific selectors if you need None — or limit yourself to classes like `.global-card`."

> "Legitimate uses: global reset styles, overriding third-party CSS you don't control."

---

### 3e. :host, :host-context, ::ng-deep (5 min)

Scroll to SECTION 4 — `SpecialSelectorsComponent`.

**:host:**
```css
:host { display: block; margin-bottom: 16px; }
```
> "Angular components are inline-level elements by default. If you want a component to behave like a block element — filling its container — you need `:host { display: block }`. This is one of the most common gotchas when building reusable components."

**:host-context:**
```css
:host-context(.dark-theme) .inner { background: #333; color: white; }
```
> "`:host-context` applies styles when an ANCESTOR has the matching selector. This enables theming: add `.dark-theme` to the body or a container, and all components using `:host-context(.dark-theme)` switch to dark mode automatically."

**::ng-deep:**
```css
:host ::ng-deep .mat-card-title { font-size: 24px; }
```
> "::ng-deep pierces through child component encapsulation. Used to override styles inside components you don't own — like Angular Material. Always combine it with `:host` so it's scoped to YOUR component, not the entire app."

**Watch out:**
> "::ng-deep is technically deprecated, but it's still the only practical way to override third-party component styles in many cases. Just always prefix it with `:host`."

---

## Recap & Q&A (5 min)

### Key Takeaways

1. **Custom pipes** implement `PipeTransform` and are declared in NgModule `declarations`.
2. **Pure pipes** (default) — only re-run when the input reference changes. Fast.
3. **Impure pipes** (`pure: false`) — re-run on every change detection cycle. For live filtering.
4. **Services** hold shared logic and data. They're injected, not instantiated by components.
5. **`providedIn: 'root'`** → one singleton instance app-wide.
6. **Component-level providers** → new instance per component instance (useful for isolated state).
7. **BehaviorSubject** pattern: service exposes `Observable`, components subscribe — automatic reactive updates.
8. Always **unsubscribe** in `ngOnDestroy` to prevent memory leaks.
9. **Emulated** encapsulation (default) — Angular adds unique attributes to scope styles. Safe for almost all use cases.
10. **`:host`** styles the component element itself. **`::ng-deep`** pierces into child components (scope it with `:host`).

---

### Q&A Questions

1. "What's the difference between `providedIn: 'root'` and adding a service to a module's `providers` array?"
   - *Both register the service, but `providedIn: 'root'` is tree-shakeable — if nothing uses it, it won't be bundled. Module-level providers always bundle.*

2. "If two components inject `CourseService`, do they each get their own instance or share one?"
   - *They share one — because `providedIn: 'root'` creates a singleton. Unless one of them has `providers: [CourseService]` in its `@Component`, which would create a new scope.*

3. "Why do we use `asObservable()` when exposing a `BehaviorSubject`?"
   - *It prevents consumers from calling `.next()` directly and mutating data outside the service. Enforces the service as the single source of truth.*

4. "What is `ng-content` in the card component for?"
   - *Content projection — it's a slot where the parent can inject arbitrary template content into the child. The `<app-course-info-card>` tag can have child HTML that gets rendered inside the card's `ng-content` placeholder.*

5. "When would you use `ViewEncapsulation.None`?"
   - *For overriding third-party styles, or for truly global CSS that you intentionally want to affect the entire document. Always use specific class names, never generic selectors like `p` or `h3`.*

---

### Take-Home Exercises

1. **Build a `currencyFormat` pipe** that takes a number and a currency code, and formats it using Angular's `CurrencyPipe` internally. Hint: you can inject `CurrencyPipe` into your custom pipe.

2. **Build a `NotificationService`** with a `BehaviorSubject<Notification[]>` and two methods: `addNotification(msg)` and `clearAll()`. Create a `NotificationBellComponent` that subscribes and shows the count.

3. **Scoped service experiment** — create two `CounterComponent` instances on the same page. One uses `providedIn: 'root'` and one uses a component-level provider. Add a `count` to the service and verify that the root-provided one is shared between both, while the component-provided one is independent.

4. **Theming with :host-context** — add a `dark-theme` class toggle to `AppComponent`. Use `:host-context(.dark-theme)` in a child component to switch its colors. Toggle the class from a button and verify both components switch.

---

## Transition to Day 18b

> "We've now covered Angular's core building blocks: components, templates, directives, pipes, services, and DI. Tomorrow on the Angular track we go into Routing and Forms — how Angular navigates between views and how it handles both template-driven and reactive form patterns."

---

*End of Day 17b — Angular Services & DI*
