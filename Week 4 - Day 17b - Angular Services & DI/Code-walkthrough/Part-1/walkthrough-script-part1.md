# Walkthrough Script — Part 1: Component Communication, Directives & Pipes
## Day 17b | Week 4 | ~90 minutes

---

## Pre-Session Setup

- [ ] Open `Part-1/` folder in VS Code
- [ ] Angular app running: `ng serve` (localhost:4200)
- [ ] Split screen: code on left, browser on right (or use second monitor)
- [ ] Browser DevTools open — Console tab visible

---

## Recap From Day 16b (5 min)

> "Yesterday we covered Angular Fundamentals — components, modules, the template syntax basics, and data binding. Quick pulse check:"

Ask the class:
- "What's the difference between `[property]` and `(event)` in a template?"
  - *Square brackets = property binding (data IN). Parentheses = event binding (events OUT)*
- "What does `{{ expression }}` do?"
  - *Interpolation — evaluates the expression and renders it as a string*

> "Today we go deeper into how components talk to each other, then we cover Angular's powerful directives and pipes. These four topics are what make Angular templates dynamic and interactive."

---

## Segment 1 — @Input and @Output: Component Communication (25 min)

**File:** `01-component-communication.ts`

---

### 1a. Intro — data flow in a component tree (3 min)

Draw on the board:
```
AppComponent
  └── CourseListParentComponent
        ├── CourseCardComponent [course]="c"       ← @Input: data flows DOWN
        └── EnrollButtonComponent (enrolled)="fn"  ← @Output: events flow UP
```

> "Angular applications are trees of components. Data flows DOWN the tree via @Input — the parent passes data to the child. Events flow UP the tree via @Output — the child tells the parent something happened."

> "This is a deliberate design. It makes data flow predictable. You always know where state lives and who changes it."

---

### 1b. Open `CourseCardComponent` — @Input (6 min)

Scroll to SECTION 1.

> "Let's look at a child component that receives data from its parent. Here's `CourseCardComponent` — it's a card that displays a single course."

Point to:
```ts
@Input() course!: { title: string; instructor: string; duration: number };
@Input() featured: boolean = false;
```

> "The `@Input()` decorator declares a property that the parent can set. Notice the exclamation mark on `course!` — that's TypeScript's non-null assertion. We're telling TypeScript: 'I know this will be set before the component renders.'"

> "The `featured` input has a default value of `false`. If the parent doesn't pass it, the component uses the default."

Now scroll up to the parent:
```html
<app-course-card
  *ngFor="let c of courses"
  [course]="c"
  [featured]="c.id === featuredId">
```

> "The parent binds to the child's inputs using square bracket syntax. `[course]="c"` passes the current loop item. `[featured]="c.id === featuredId"` passes a computed boolean — `featured` is true only for the selected course."

Demo: click 'Toggle Featured' — the badge moves from one card to the other.

> "Every time `featuredId` changes, Angular re-evaluates `c.id === featuredId` and passes the new boolean to each child."

**ngOnChanges:**
```ts
ngOnChanges(changes: SimpleChanges): void {
  if (changes['course']) {
    console.log('Course input changed:', changes['course'].currentValue);
```

> "If you want to react when an input changes — for example, to fetch new data when the course changes — implement `ngOnChanges`. It fires every time an @Input value changes and gives you both the current and previous value."

---

### 1c. @Output + EventEmitter (7 min)

Scroll to SECTION 2 — `EnrollButtonComponent`.

> "Now the other direction: child to parent. Here's `EnrollButtonComponent` — it has a button, and when clicked it needs to tell the parent which course was enrolled."

```ts
@Output() enrolled   = new EventEmitter<{ id: number; name: string }>();
@Output() wishlisted = new EventEmitter<number>();
```

> "`@Output()` declares an event the parent can listen for. `EventEmitter<T>` is Angular's event emitter — the generic type `T` is the shape of data you'll emit. Here `enrolled` sends an object, `wishlisted` sends just a number."

```ts
onEnrollClick(): void {
  this.enrolled.emit({ id: this.courseId, name: this.courseName });
}
```

> "`.emit()` fires the event and passes data up. Think of it as: the child raises its hand and says 'I'm enrolling in course 17 — here's the details.'"

Now the parent template:
```html
(enrolled)="onEnrolled($event)"
(wishlisted)="onWishlisted($event)"
```

> "The parent listens with parentheses — the same syntax as DOM events like `(click)`. `$event` is Angular's special variable containing whatever the child emitted. If the child called `.emit(42)`, `$event` is `42`."

Demo the component — enroll, wishlist, see the parent's state update.

**Watch out:**
> "A very common mistake: using `@Output()` but forgetting to instantiate `new EventEmitter()`. You'll get a runtime error 'enrolled.emit is not a function'. Always initialize it."

---

### 1d. Two-way binding [(ngModel)] (4 min)

Scroll to SECTION 3.

> "Two-way binding is the 'banana in a box' syntax. That's literally what the Angular team calls it — because `[()]` looks like a banana inside a box."

```html
<input [(ngModel)]="searchTerm" />
```

> "This is shorthand for two things at once: `[ngModel]='searchTerm'` binds the value from state to the input — that's data down. `(ngModelChange)='searchTerm = $event'` listens for changes and updates state — that's events up."

> "[(ngModel)] requires `FormsModule` to be imported in your `NgModule`. If you see 'Can't bind to ngModel since it isn't a known property of input' — that's your answer: import FormsModule."

---

### 1e. @ViewChild (5 min)

Scroll to SECTION 5.

> "Sometimes a parent needs to reach INTO a child and call a method directly — not through inputs or events. That's what `@ViewChild` is for."

```ts
@ViewChild(VideoPlayerComponent) player!: VideoPlayerComponent;

playLesson(title: string): void {
  this.player.play(title);  // call child method directly
}
```

> "The parent queries its own template for a `VideoPlayerComponent` instance and gets a reference to it. Then it can call methods on it directly."

**Watch out:**
> "The `@ViewChild` reference is NOT available in `ngOnInit`. The template hasn't rendered yet at that point. Use `ngAfterViewInit` to access it safely."

Ask the class:
- "When would you use @ViewChild instead of @Output?"
  - *When you need to call a specific method imperatively — like play/pause on a media player, focus on an input, or trigger an animation*

---

## Segment 2 — Directives (30 min)

**File:** `02-directives.ts`

---

### 2a. What is a directive? (2 min)

> "Directives are instructions for the DOM. Angular has three kinds: components (which have their own template), structural directives (which change what's in the DOM), and attribute directives (which change how an existing element looks or behaves)."

> "The asterisk on `*ngIf` and `*ngFor` is a visual cue that these are structural. It's actually syntactic sugar — Angular desugars it into an `<ng-template>` behind the scenes."

---

### 2b. *ngIf in depth (8 min)

Scroll to SECTION 1 — `NgIfDemoComponent`.

**Basic:**
```html
<p *ngIf="isLoggedIn">Welcome back, {{ username }}!</p>
```
> "The simplest case — the paragraph exists in the DOM only when `isLoggedIn` is true. When it's false, the element is completely removed."

**else block:**
```html
<p *ngIf="isLoggedIn; else loginPrompt">…</p>
<ng-template #loginPrompt>…</ng-template>
```
> "The `else` points to an `<ng-template>` reference. When the condition is false, Angular renders the template. The `#loginPrompt` is a template reference variable — the hashtag names it."

**then/else:**
```html
<div *ngIf="isLoading; then loadingBlock; else contentBlock"></div>
```
> "You can point both branches to templates. The div itself is the anchor — Angular renders the appropriate template in its place."

**as alias:**
```html
<div *ngIf="getUser() as user">
  <p>{{ user.name }}</p>
</div>
```
> "The `as` keyword captures the truthy value into a local variable. This is extremely useful with the `async` pipe — you call `getUser() | async as user` and only render when the observable emits, using the value inside."

**Watch out:**
> "`*ngIf` REMOVES elements from the DOM. It doesn't just hide them. The component inside is DESTROYED when the condition is false and RE-CREATED when it becomes true. If you need to preserve state inside a hidden element, use CSS `display:none` via `[hidden]` or `ngStyle` instead."

Demo: toggle the button, open DevTools Elements — show the element appearing and disappearing from the DOM.

---

### 2c. *ngFor in depth (8 min)

Scroll to SECTION 2 — `NgForDemoComponent`.

**Exported variables:**
```html
*ngFor="let course of courses;
        index as i;
        first as isFirst;
        last as isLast;
        even as isEven;
        odd as isOdd"
```
> "ngFor exports several local variables you can capture into local aliases. `index` is the iteration index (0-based). `first` and `last` are booleans — useful for styling the first/last row differently. `even` and `odd` help with zebra-stripe tables."

Demo the table — point out how even rows have a different background.

**trackBy — critical for performance:**
```ts
trackById(index: number, course: { id: number }): number {
  return course.id;
}
```
```html
<li *ngFor="let course of courses; trackBy: trackById">
```

> "This is the most important optimization for lists. Without `trackBy`, every time the array reference changes — even if you just added one item — Angular tears down and re-creates EVERY element in the list."

> "With `trackBy`, Angular uses your function to identify each item. If an item's identity hasn't changed, Angular reuses the existing DOM element. For a list of 1000 rows this is a massive performance win."

Ask: "What should `trackBy` return?" → A unique identifier for the item — typically the `id` field.

---

### 2d. *ngSwitch (5 min)

Scroll to SECTION 3 — `NgSwitchDemoComponent`.

```html
<div [ngSwitch]="userRole">
  <div *ngSwitchCase="'admin'">…</div>
  <div *ngSwitchCase="'instructor'">…</div>
  <div *ngSwitchDefault>…</div>
</div>
```

> "Notice that `[ngSwitch]` is on the CONTAINER with square brackets — it's an attribute directive, not a structural one. The structural directives are the `*ngSwitchCase` and `*ngSwitchDefault` on the children."

> "Use ngSwitch when you have 3 or more mutually exclusive cases. For 2 cases, *ngIf with else is fine. For 3+ cases, ngSwitch reads more clearly than chained *ngIf."

Demo: change the select dropdown — the content area swaps.

---

### 2e. Attribute directives — ngClass and ngStyle (4 min)

Scroll to SECTION 4.

```html
[ngClass]="{ 'card-featured': course.isFeatured, 'card-expired': course.isExpired }"
```
> "ngClass applies CSS classes conditionally using an object map. The key is the class name, the value is the condition. Any truthy value adds the class."

```html
[ngStyle]="{ 'background-color': isFeatured ? '#fff3cd' : 'white' }"
```
> "ngStyle applies inline CSS styles from an object. Note the camelCase or quoted kebab-case for property names."

> "Quick tip: for toggling a SINGLE class, the shorthand `[class.featured]='condition'` is cleaner than ngClass. For a single inline style, `[style.color]='expression'` is cleaner than ngStyle."

---

### 2f. Custom attribute directive — HighlightDirective (3 min)

Scroll to SECTION 5.

```ts
@Directive({ selector: '[appHighlight]' })
export class HighlightDirective {
  @Input('appHighlight') highlightColor: string = 'lightyellow';

  @HostListener('mouseenter') onMouseEnter() { … }
  @HostListener('mouseleave') onMouseLeave() { … }
}
```

> "To create a custom directive, use `@Directive` with a selector in square brackets. That's what makes it an attribute selector — the element must have the `appHighlight` attribute."

> "`@HostListener` listens to DOM events on the host element — the element the directive is applied to. `@HostBinding` binds a property on the host element."

> "Usage: `<p appHighlight>Hover me</p>` or `<p appHighlight='lightblue'>` to pass a color."

Demo the highlight directive in the browser.

---

## Segment 3 — Pipes (20 min)

**File:** `03-pipes.ts`

---

### 3a. What is a pipe? (2 min)

> "A pipe takes a value, transforms it, and returns a new value for display. The original data is never mutated — pipes are pure by default. They only exist in the template — there's no need to import them into your TypeScript code."

> "Syntax: `{{ value | pipeName:argument1:argument2 }}`"

---

### 3b. String pipes (3 min)

```html
{{ courseName | uppercase }}
{{ courseName | lowercase }}
{{ 'angular services and dependency injection' | titlecase }}
{{ courseName | slice:0:7 }}
```

> "These are the most straightforward. `uppercase`, `lowercase`, `titlecase` are self-explanatory. `slice` works like JavaScript's `String.slice()` — `slice:0:7` takes characters 0 through 6."

---

### 3c. Number and currency pipes (4 min)

```html
{{ price | number:'1.2-2' }}
{{ price | currency:'USD':'symbol':'1.2-2' }}
```

> "The `number` pipe's format string is `minIntegerDigits.minFractionDigits-maxFractionDigits`. So `1.2-2` means at least 1 digit before the decimal, exactly 2 digits after."

> "`currency` takes the currency code, then the display format (symbol or code), then the same digits format. It automatically uses locale-aware formatting — commas as thousand separators, correct decimal notation."

Ask: "What would `price | currency:'GBP':'symbol-narrow'` display for 1299.5?" → `£1,299.50`

---

### 3d. Date pipe (4 min)

```html
{{ courseDate | date:'longDate' }}
{{ courseDate | date:"EEEE, MMMM d 'at' h:mm a" }}
```

> "The date pipe accepts named formats like `shortDate`, `mediumDate`, `longDate`, `fullDate` — or a custom format string using Unicode date symbols. `EEEE` = full weekday name, `MMMM` = full month name, `d` = day of month, `h:mm a` = 12-hour time with AM/PM."

> "The date pipe uses the Angular locale. To change the locale, provide `LOCALE_ID` in your AppModule."

---

### 3e. Async pipe (4 min)

Scroll to SECTION 7.

```html
<p>Counter: {{ counter$ | async }}</p>
<div *ngIf="userData$ | async as user">…</div>
```

> "This is one of the most important Angular pipes. The async pipe subscribes to an Observable or Promise, unwraps the emitted value, and renders it. And critically — it UNSUBSCRIBES when the component is destroyed."

> "Without the async pipe, you'd need to manually subscribe, store the value, and unsubscribe in ngOnDestroy. The async pipe handles all of that for you."

Demo: watch the counter increment in the browser.

**Watch out:**
> "A common gotcha with pipe operator precedence. If you write `{{ condition ? 'yes' : 'no' | uppercase }}`, only 'no' gets uppercased because `|` binds tighter than `?:`. Always wrap the ternary in parentheses: `{{ (condition ? 'yes' : 'no') | uppercase }}`."

---

### 3f. Chaining pipes (2 min)

```html
{{ courseName | uppercase | slice:0:7 }}
{{ courseDate | date:'mediumDate' | uppercase }}
```

> "Pipes chain left to right. The output of the first pipe becomes the input to the second. Here we format the date then uppercase it — great for headings that need a formatted date in all caps."

---

## Recap (5 min)

### Key Takeaways

1. `@Input` = data flows DOWN (parent → child). `@Output + EventEmitter` = events flow UP (child → parent).
2. `ngOnChanges` fires when an @Input value changes — gives you previous and current values.
3. `[(ngModel)]` = two-way binding — shorthand for `[ngModel]` + `(ngModelChange)`.
4. `@ViewChild` = parent gets a reference to a child component and can call methods on it directly.
5. `*ngIf` removes the element from the DOM (destroying the component). Use `[hidden]` to preserve state.
6. `*ngFor` with `trackBy` is essential for performance with large or dynamic lists.
7. `*ngSwitch` is cleaner than chained `*ngIf` for 3+ mutually exclusive cases.
8. `ngClass` and `ngStyle` are attribute directives for conditional styling.
9. Pipes transform values in templates without mutating data. Async pipe handles subscriptions automatically.

---

### Q&A Questions

1. "What's the difference between `[hidden]="condition"` and `*ngIf="condition"`?"
2. "If a child component has `@Output() enrolled = new EventEmitter<string>()`, what does the parent template look like to handle it?"
3. "Why is `trackBy` important in *ngFor?"

---

## Transition to Part 2

> "We introduced pipes — but we only used Angular's built-in ones. In Part 2 we'll build our own custom pipes. Then we'll cover the most important concept in Angular architecture: Services and Dependency Injection."

---

*End of Part 1 Script*
