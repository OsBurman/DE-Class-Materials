# Day 16b — Angular Fundamentals | Part 2 Walkthrough Script
**Duration:** ~90 minutes  
**Folder:** `Code-walkthrough/Part-2/`  
**Files covered:**
- `01-data-binding/` — data-binding.component.ts + .html
- `02-template-refs-and-lifecycle/` — lifecycle-and-refs.component.ts + lifecycle-demo.component.html
- `03-modules-and-directives/` — structural-directives.component.ts + .html

---

## Pre-Segment Recap (5 min)

> [ACTION] Write on the board:
```
Part 1 covered:
  • Angular architecture & CLI
  • @Component — selector, templateUrl, styleUrls
  • @Input() / @Output() — parent → child / child → parent
  • NgModule — declarations, imports, providers, bootstrap
  • Template syntax — {{ }}, [prop], (event), pipes, ?., ng-template/container/content

Part 2 covers:
  • All 4 binding types (with two-way binding live demo)
  • Template reference variables & @ViewChild
  • All 8 lifecycle hooks
  • Structural directives — *ngIf, *ngFor (+ trackBy), *ngSwitch
```

---

## Segment 1 — The Four Data Binding Types (30 min)

### The Big Picture

> [ACTION] Open `01-data-binding/data-binding.component.html`.

"Angular's entire template system is built on four binding patterns. Everything you do in a template is one of these four — or a combination. Let me show you all four in a single component."

> [ACTION] Draw on the board:
```
    ┌─────────────────┐              ┌────────────────┐
    │  Component      │              │     DOM        │
    │  Class          │              │   (Template)   │
    │                 │              │                │
    │  property ──────┼──{{ }} ──────► text content  │
    │                 │              │                │
    │  expression ────┼──[prop]──────► DOM property  │
    │                 │              │                │
    │  method() ◄─────┼──(event)─────│ user action   │
    │                 │              │                │
    │  property ◄────►┼──[(ngModel)]─► form input    │
    └─────────────────┘              └────────────────┘
```

### Type 1: Interpolation

"Interpolation renders text. Anything inside `{{ }}` is evaluated as a TypeScript expression and converted to a string. Critical rule: it must be an **expression that returns a value** — no assignments, no `new`, no `++`."

> [ACTION] Point to the interpolation section in the HTML, read through the examples.

> [ASK] "What's the output of `{{ 4 > 3 ? 'Yes' : 'No' }}`?"  
*Answer:* `Yes`

### Type 2: Property Binding

"Property binding sets a **DOM property** — not an HTML attribute. This distinction trips up a lot of developers."

> [ACTION] Draw on board:
```
HTML attribute  →  <input disabled>       ← sets the initial state, string-based
DOM property    →  [disabled]="isBool"    ← live, any JavaScript type
```

"The attribute exists in the HTML source. The property exists on the JavaScript DOM object. They're related but different. Angular binds to **properties**, which is why we write `[disabled]` not `[attr.disabled]` most of the time."

Walk through the template:
- `[src]` and `[alt]` on the image
- `[disabled]="isButtonDisabled"` — show it's a boolean, not a string
- `[class.highlighted]="isCardHighlighted"` — class binding
- `[style.color]="..."` — style binding
- `[attr.aria-label]` — for attributes without a DOM property equivalent

> ⚠️ **WATCH OUT:** `[class]="'active highlighted'"` replaces ALL classes. Prefer `[class.myClass]="bool"` to add/remove individual classes, or use `[ngClass]` for complex class objects.

### Type 3: Event Binding

> [ACTION] Open the browser (real Angular app), type in the keyup demo, move the mouse.

"Event binding listens to DOM events. The `$event` variable holds the native event object — `MouseEvent`, `KeyboardEvent`, `InputEvent` depending on the event."

> [ASK] "What would `(click)="counter++"` do? Is it valid?"  
*Answer:* Valid — statements in event bindings CAN have side effects. Unlike interpolation, event handlers are allowed to mutate state.

### Type 4: Two-Way Binding with ngModel

> [ACTION] Open the form section in the browser. Type in the name field and show the live preview updating.

"Watch — I type here and the preview below updates instantly. That's two-way binding. The input writes to `form.name`, and the `{{ form.name }}` reads from it."

> [ACTION] Click "Fill Sample Data" button to show programmatic update flowing INTO the form.

"Now I'll click 'Fill Sample Data' — this changes the TypeScript property programmatically. Watch the input fields update. That's the other direction — component → DOM."

> [ASK] "What module must be imported for `[(ngModel)]` to work? What happens if you forget it?"  
*Answer:* `FormsModule` in `AppModule.imports[]`. Forgetting it produces: `Can't bind to 'ngModel' since it isn't a known property of 'input'`.

"The `[()]` syntax is nicknamed **'banana in a box'** — the parentheses `()` are the banana (event binding), the brackets `[]` are the box (property binding). Together they do both simultaneously."

---

## Segment 2 — Template Reference Variables & Lifecycle Hooks (30 min)

### Template Reference Variables

> [ACTION] Open `02-template-refs-and-lifecycle/lifecycle-and-refs.component.ts`.

"A template reference variable gives you a handle on a DOM element or component instance — **directly inside the template**, without writing any TypeScript."

> [ACTION] Show the inline template in `RefDemoComponent`:
```html
<input #searchInput type="text">
<button (click)="searchInput.focus()">Focus</button>
<button (click)="searchInput.value = ''">Clear</button>
```

"The `#searchInput` declares the variable. Everything after the `#` is the variable name. We can call DOM methods directly — `focus()`, clear the `value` — without touching the class."

**`@ViewChild` — class-level access**

"For accessing a template element from TypeScript (not just the template), we use `@ViewChild`. Notice in the TypeScript file:"

```typescript
@ViewChild('titleInput') titleInputRef!: ElementRef<HTMLInputElement>;
```

"Angular populates this property in `ngAfterViewInit`. Never try to use a `@ViewChild` in `ngOnInit` — the view hasn't rendered yet and it will be `undefined`."

> ⚠️ **WATCH OUT:** This is one of the most common Angular bugs. `@ViewChild` is always `undefined` in `ngOnInit`. Use it in `ngAfterViewInit`.

### Lifecycle Hooks

> [ACTION] Open `lifecycle-demo.component.html` in the browser.

"Angular components have a well-defined lifecycle. Let me walk through all eight hooks — but I promise we'll focus on the three you'll use in real life: `ngOnInit`, `ngOnChanges`, and `ngOnDestroy`."

> [ACTION] Walk through the lifecycle order on the board:

```
      Component created
           │
    ┌──────▼──────────┐
    │  constructor()   │  ← DI only. @Input() NOT set yet.
    └──────┬──────────┘
           │
    ┌──────▼──────────┐
    │  ngOnChanges()   │  ← @Input() values just arrived (first time + every change)
    └──────┬──────────┘
           │
    ┌──────▼──────────┐
    │  ngOnInit()      │  ← Initialization. API calls go here. Runs ONCE.
    └──────┬──────────┘
           │
    ┌──────▼──────────┐
    │  ngDoCheck()     │  ← Every CD cycle. Use sparingly!
    └──────┬──────────┘
           │
    ┌──────▼────────────────┐
    │ ngAfterContentInit()   │  ← ng-content projected. Runs ONCE.
    └──────┬────────────────┘
           │
    ┌──────▼─────────────────────┐
    │ ngAfterContentChecked()     │  ← After every content check
    └──────┬─────────────────────┘
           │
    ┌──────▼─────────────┐
    │  ngAfterViewInit()  │  ← View rendered. @ViewChild available. Runs ONCE.
    └──────┬─────────────┘
           │
    ┌──────▼──────────────────┐
    │  ngAfterViewChecked()   │  ← After every view check
    └──────┬──────────────────┘
           │
          ...  (many CD cycles)
           │
    ┌──────▼──────────┐
    │  ngOnDestroy()   │  ← CLEANUP. Unsubscribe. Clear timers. ALWAYS implement.
    └─────────────────┘
```

**The three hooks you'll use daily:**

1. **`ngOnInit()`** — "Your component's setup method. HTTP calls, subscription setup, reading `@Input()` values. Put almost everything here instead of the constructor."

2. **`ngOnChanges()`** — "Called when a parent changes an `@Input()`. The `SimpleChanges` object tells you what changed, its `previousValue`, and its `currentValue`. Use when you need to recompute something based on new prop values."

3. **`ngOnDestroy()`** — "The cleanup hook. **Every subscription you create must be unsubscribed here.** Angular doesn't do it for you. Memory leaks in Angular are almost always a missing `ngOnDestroy`."

> [ASK] "In React, where do you put initialization logic and cleanup? How does that compare?"  
*Answer:* `useEffect(() => { ... return cleanup; }, [])` does both. Angular separates them into `ngOnInit` (init) and `ngOnDestroy` (cleanup).

**Implementing the interface**

"Notice the class `implements OnInit, OnDestroy`. This is optional but a good practice — if you implement the interface and forget to write the method, TypeScript will give you a compile error."

---

## Segment 3 — Structural Directives (20 min)

> [ACTION] Open `03-modules-and-directives/structural-directives.component.html`.

"Structural directives CHANGE THE DOM STRUCTURE — they add, remove, or repeat elements. They always start with an asterisk `*`. The `*` is syntactic sugar for `ng-template` wrapping — Angular desugars it during compilation."

### NgModule — CommonModule vs BrowserModule

"Quick NgModule note before we dive into the directives. The directives we're about to use come from `CommonModule`. In your root `AppModule`, `BrowserModule` re-exports `CommonModule` — so you get them for free. In any feature module you create, import `CommonModule` directly."

> ⚠️ **WATCH OUT:** Importing `BrowserModule` in a feature module (not the root) causes an error. Always use `CommonModule` in feature modules.

### *ngIf

> [ACTION] Click the Toggle Login button — show element appearing/disappearing.

"Unlike CSS `display: none`, `*ngIf` REMOVES the element from the DOM entirely. The component is destroyed and re-created. This is important for lifecycle hooks — `ngOnInit` and `ngOnDestroy` fire when the element is shown/hidden via `*ngIf`."

Walk through:
- Basic `*ngIf="condition"`
- `*ngIf="condition; else templateRef"` — show the `#loggedOutBlock` usage
- `*ngIf="condition; then thenBlock; else elseBlock"` — both branches

> [ASK] "When would you prefer CSS `display: none` over `*ngIf`? Give an example."  
*Answer:* When the component is expensive to re-initialize (e.g., a video player, a map). Or when you need to preserve form state. `*ngIf` destroys state; hidden preserves it.

### *ngFor

> [ACTION] Interact with the filter dropdowns — show the filtered list updating live.

"*ngFor repeats a template for each item in an iterable. The full syntax gives you `index`, `first`, `last`, `even`, `odd` — all useful for styling."

**trackBy — the performance key**

> [ACTION] Highlight the `trackBy: trackByCourseId` attribute, then show the TypeScript method.

"Without `trackBy`, when the `courses` array changes even slightly, Angular destroys every list item DOM node and recreates them all. With `trackBy`, Angular diffs by ID and only updates what changed."

```
Without trackBy:   5 courses → 6 courses → destroy all 5, create 6 (wasteful)
With trackBy:      5 courses → 6 courses → keep 5 existing nodes, create 1 new (efficient)
```

> [ASK] "What should the `trackBy` function return? What makes a good tracking value?"  
*Answer:* A unique, stable identifier — database ID is ideal. Avoid using index (it defeats the purpose — if you reorder the list, the indices change and Angular still recreates everything).

### *ngSwitch

"When you have more than 2 branches checking the same value, `*ngSwitch` is cleaner than multiple `*ngIf`s."

> [ACTION] Click through the tabs to show `*ngSwitch` in action.

"Notice the syntax:  
- `[ngSwitch]="selectedTab"` on the container — square brackets, not asterisk  
- `*ngSwitchCase="'overview'"` on each panel — asterisk, and the value is a **string literal**  
- `*ngSwitchDefault` for the fallback"

### ng-container — Combining Directives

> [ACTION] Scroll to the bottom section.

"You CANNOT put two structural directives on the same element. Angular will throw a compile error. The solution is `ng-container` — it holds one directive while the element holds the other. `ng-container` renders no DOM element at all."

---

## Recap & Q&A (5 min)

### Key Takeaways — Write on Board

1. **4 binding types**: `{{ }}` interpolation, `[prop]` property, `(event)` event, `[(ngModel)]` two-way
2. **`[(ngModel)]`** requires `FormsModule` in AppModule
3. **Template refs `#varName`** — DOM/component access in template; `@ViewChild` for class access
4. **`@ViewChild` available in `ngAfterViewInit`** — never in `ngOnInit`
5. **The 3 daily lifecycle hooks**: `ngOnInit` (init), `ngOnChanges` (@Input changes), `ngOnDestroy` (cleanup)
6. **`*ngIf` removes from DOM** — not just hidden; component is destroyed and recreated
7. **`trackBy` in `*ngFor`** — always use it for performance with dynamic lists
8. **`*ngSwitch`** — cleaner than multiple `*ngIf` for the same variable
9. **`ng-container`** — invisible wrapper to combine directives

### Q&A Questions

1. "What is the banana-in-a-box pattern? Write the long-form equivalent of `[(ngModel)]='name'`."
2. "I create a new component and add a `@ViewChild` reference. In `ngOnInit` it's `undefined`. What's wrong and how do I fix it?"
3. "My *ngFor list has 1,000 items. Every time I add one item, the screen flickers. What's the likely fix?"
4. "I have a component that subscribes to an Observable in ngOnInit. What will happen if I forget to unsubscribe in ngOnDestroy?"
5. "I need to show different UI for logged-out / loading / loaded / error states. Which directive is most appropriate — *ngIf or *ngSwitch?"

---

## Take-Home Exercises

1. **Two-way binding form**: Build a `profile-editor` component with fields for name, bio, and skill level (beginner/intermediate/advanced via `<select>`). Show a live preview card below the form that updates as you type. No submit button needed — just real-time binding.

2. **Lifecycle explorer**: Create a `timer` component that starts a `setInterval` in `ngOnInit` (increment a counter every second) and **clears** it in `ngOnDestroy`. Toggle the component in/out with `*ngIf` in a parent to confirm the timer stops when destroyed.

3. **trackBy challenge**: Build a list of 10 items with an "Add item" and "Shuffle" button. Implement `trackBy` using item IDs. Open DevTools → Elements panel and confirm DOM nodes are reused (not recreated) when you add or shuffle.

4. **ngSwitch dashboard**: Create a `dashboard` component with 4 tabs: Profile, Settings, Notifications, Help. Use `*ngSwitch` to render a different `<div>` for each tab. Add a badge counter to the Notifications tab using property binding on a class.

---

→ **Day 16b COMPLETE.** Next: **Day 17a — React Hooks** or **Day 17b — Angular Services & Dependency Injection**, depending on which track your students are on.
