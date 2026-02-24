# Day 16b — Angular Fundamentals | Part 1 Walkthrough Script
**Duration:** ~90 minutes  
**Folder:** `Code-walkthrough/Part-1/`  
**Files covered:**
- `01-angular-overview-and-architecture.md`
- `02-components-and-templates/` (5 files)
- `03-template-syntax-and-expressions/` (2 files)

---

## Pre-Class Setup Checklist
- [ ] Node.js 18+ and npm installed (`node -v`, `npm -v`)
- [ ] Angular CLI installed (`npm install -g @angular/cli`, verify with `ng version`)
- [ ] Terminal open in a working directory (Desktop or Documents)
- [ ] Browser open at `http://localhost:4200` (after `ng serve`)
- [ ] VS Code with Angular Language Service extension installed

---

## Segment 1 — What Is Angular & Why It Exists (15 min)

### Opening Hook

> [ACTION] Open `01-angular-overview-and-architecture.md` in the preview pane.

"Before we write a single line of Angular, I want to answer the question every developer asks: *why does Angular exist when we already have React and Vue?*"

> [ASK] "How many of you have heard Angular called 'too complex' or 'too opinionated'? Put a thumbs up in chat."

"The complexity is real — but it's **intentional**. React hands you a UI library and says 'go figure out the rest.' Angular hands you a **complete framework** — routing, HTTP, forms, testing, state management patterns — all with opinions baked in. For enterprise teams, that's actually *less* complexity because everyone follows the same patterns."

### Draw the Architecture Diagram on the Board

```
        ┌─────────────────────────────────────────┐
        │             Angular Application          │
        │                                          │
        │   ┌──────────────┐                       │
        │   │  AppModule   │  ← Root NgModule      │
        │   │  @NgModule   │    (the manifest)      │
        │   └──────┬───────┘                       │
        │          │ bootstraps                     │
        │   ┌──────▼───────────────────────────┐   │
        │   │         AppComponent             │   │
        │   │         (root component)          │   │
        │   │  ┌─────────┐  ┌──────────────┐   │   │
        │   │  │ChildComp│  │ AnotherComp  │   │   │
        │   │  └─────────┘  └──────────────┘   │   │
        │   └──────────────────────────────────┘   │
        │                                          │
        │   Services    Directives    Pipes        │
        │   (business   (DOM          (data         │
        │    logic)      behavior)    transform)    │
        └─────────────────────────────────────────┘
```

> [ACTION] Draw this on the whiteboard while narrating.

"Angular apps are a tree of components, all declared inside modules, with services and directives available throughout. Everything is TypeScript — no JSX, no special file format, just `.ts`, `.html`, and `.css`."

### Live Demo: Scaffold a New App

> [ACTION] In terminal:
```bash
ng new course-platform --routing=false --style=css
cd course-platform
ng serve
```
> [ACTION] Open `http://localhost:4200` in browser — show the default Angular welcome screen.

"While that compiles, let's tour the project structure."

> [ACTION] Open the project in VS Code. Walk through:
- `src/app/app.component.ts` — the root component
- `src/app/app.component.html` — its template
- `src/app/app.module.ts` — the root module
- `src/index.html` — note `<app-root>` tag
- `angular.json` — build configuration
- `tsconfig.json` — TypeScript strict mode settings

"Notice: Angular CLI generates this entire structure for you. In a real project you'd never touch `angular.json` manually unless you're customizing the build."

> [ASK] "What's the difference between `app.component.html` and `index.html`? Why do we need both?"

*Answer:* `index.html` is the shell — loaded once by the browser. `app.component.html` is Angular's template — rendered dynamically by the framework. Angular replaces `<app-root>` with the compiled component output.

---

## Segment 2 — Components & Templates (35 min)

### The Component Triplet

> [ACTION] Open `02-components-and-templates/course-card.component.ts`.

"Every Angular component is (typically) three files working together."

> [ACTION] Draw on the board:
```
course-card.component.ts     ← class + metadata (@Component)
course-card.component.html   ← what it renders
course-card.component.css    ← scoped styles
```

"Let's read the TypeScript file top to bottom and understand each piece."

Walk through the `.ts` file:

1. **The `@Component` decorator** — "This is Angular metadata. The compiler reads this and wires up the template, styles, and selector. Without this decorator, it's just a plain TypeScript class."

2. **`selector: 'app-course-card'`** — "This is the custom HTML element tag. When Angular sees `<app-course-card>` anywhere in a template, it renders this component. By convention Angular CLI prefixes selectors with `app-`."

3. **`templateUrl` vs `template`** — "You can write the HTML inline with `template: \`...\`` (backtick string) or point to an external file. External file is better for anything more than 3 lines."

4. **`@Input() course!: Course`** — "The `!` is the non-null assertion — TypeScript's way of saying 'I promise this will be set before it's used.' The `@Input()` decorator marks this property as something the parent component can set."

5. **The `Course` interface** — "Angular components are just TypeScript classes. We can use interfaces, generics, enums — all standard TypeScript."

> [ASK] "In React, we pass data to child components via props. What's the Angular equivalent?"

*Answer:* `@Input()` decorated properties.

### Walk Through the Template

> [ACTION] Open `02-components-and-templates/course-card.component.html`.

"Now let's look at what this component renders. Angular templates are HTML-first — you extend HTML with Angular syntax rather than writing JS that returns HTML (like JSX)."

Point out each binding:

| Template Syntax | Meaning |
|---|---|
| `{{ course.title }}` | Interpolation — renders a string |
| `[class.featured]="course.isFeatured"` | Property binding — sets CSS class conditionally |
| `(click)="toggleEnrollment()"` | Event binding — calls method on click |

"Notice these are all the same concept: **moving data between the component class and the DOM**. The direction of the brackets tells you the direction of data flow."

> [ACTION] Draw on the board:
```
Component Class  →  Template    :  {{ }}  and  [property]="expr"
Template (DOM)   →  Component   :  (event)="handler()"
Both directions  ←→             :  [(ngModel)]="prop"   ← Part 2
```

### Walk Through the Parent Component

> [ACTION] Open `02-components-and-templates/app.component.ts`.

"This is the parent. It owns the data — an array of `Course` objects. It passes individual courses DOWN to `CourseCardComponent`."

> [ACTION] Open `02-components-and-templates/app.component.html`.

"Here's where the magic connects. See `*ngFor="let course of courses"`? That's a structural directive that repeats `<app-course-card>` once per course. We'll cover it fully in Part 2 — for now just notice how the `[course]` binding passes data to the child."

Point to the `(courseSelected)="onCourseSelected($event)"` line:

"The child emits events upward with `@Output()` — the parent listens here. `$event` is whatever value the child emitted. One-way data flow with explicit event emission — same mental model as React, different syntax."

### The NgModule — App Module

> [ACTION] Open `02-components-and-templates/app.module.ts`.

"Before Angular can use our `CourseCardComponent`, it must know about it. That's `declarations`. Every component you create must be declared in exactly one module."

Walk through each section:
- **`declarations`** — components, directives, pipes you OWN
- **`imports`** — modules whose exported pieces you WANT to use
- **`providers`** — services (mostly handled by `providedIn: 'root'` now)
- **`bootstrap`** — the first component to render

> ⚠️ **WATCH OUT:** A common early mistake is forgetting to add a new component to `declarations`. The error message is: `'app-whatever' is not a known element`. The fix: add it to the right module's `declarations` array.

> [ACTION] Live: In your real Angular app, generate and use a component:
```bash
ng generate component course-card
```
"See how the CLI automatically adds it to `AppModule.declarations`? That's one of the productivity benefits of the Angular CLI."

---

## Segment 3 — Template Syntax Deep-Dive (30 min)

> [ACTION] Open `03-template-syntax-and-expressions/template-syntax.component.html`.

"Let's go through each template feature. I'll navigate the HTML and you follow along in the code."

### Interpolation vs Template Statements

"Open the Interpolation section. `{{ }}` is for **expressions** — they return a value and must have no side effects. (click) handlers are **statements** — they can call functions and assign variables."

> [ASK] "Why does Angular enforce that restriction? What would go wrong if `{{ counter++ }}` worked?"

*Answer:* Angular runs change detection repeatedly. A side-effecting expression would mutate state during rendering, causing an infinite loop.

### Pipes Live Demo

> [ACTION] In your real Angular app, type the pipe examples in real time:

```html
<!-- In app.component.html -->
<p>{{ 49.99 | currency:'USD' }}</p>
<p>{{ today | date:'longDate' }}</p>
<p>{{ 'hello world' | uppercase }}</p>
```

"Pipes are pure functions that transform display values. They NEVER mutate the original data."

Run through the built-in pipes from the file:
| Pipe | Example output |
|---|---|
| `uppercase` | `HELLO ANGULAR WORLD` |
| `titlecase` | `Hello Angular World` |
| `currency:'USD'` | `$49.99` |
| `date:'shortDate'` | `3/15/24` |
| `number:'1.2-2'` | `1,234,567.89` |
| `percent:'1.0-0'` | `85%` |
| `json` | Raw JSON — great for debugging |

> [ASK] "Can you chain pipes? What would `{{ name | uppercase | slice:0:5 }}` do?"

*Answer:* Yes — pipes are applied left to right. The result of the first is passed to the second.

### Safe Navigation Operator

"This one saves you from runtime errors. Who has seen `TypeError: Cannot read properties of undefined` before?"

> [ACTION] Show the section in the HTML:
```html
{{ userWithoutAddress.address?.city }}
```

"Without the `?.`, if `address` is undefined, Angular throws at runtime. With `?.`, it short-circuits and returns `undefined`, which renders as empty string. It's the same as optional chaining in modern JavaScript."

### ng-template, ng-container, ng-content

"These three are where Angular's template system gets powerful — and where most beginners get confused."

> [ACTION] Walk through Section 6, 7, 8 in the HTML file, narrating each one:

- **`ng-template`**: "A fragment that doesn't render until something asks for it. Used as the `else` branch in `*ngIf`."
- **`ng-container`**: "An invisible DOM wrapper. Angular removes it — only its children remain. Critical for combining two structural directives."
- **`ng-content`**: "Angular's slot API. The child component decides where projected content appears."

> [ACTION] Draw the content projection diagram:
```
Parent template:              Child template:
<app-card>               →    <div class="card">
  <h3>My Title</h3>             <ng-content></ng-content>
  <p>My Body</p>                  ↑ h3 and p appear here
</app-card>               →    </div>
```

---

## Recap & Q&A (10 min)

### Key Takeaways — Write on Board

1. Angular is a **complete framework** — router, HTTP, forms, DI included
2. Every component = `.ts` + `.html` + `.css`
3. `@Component` decorator connects the three files
4. `@Input()` = props; `@Output()` = callbacks/events
5. Every component must be **declared in one module**
6. Template syntax: `{{ }}` expressions, `[prop]` binding, `(event)` handler
7. **Safe nav `?.`** prevents null errors in templates
8. **Pipes** transform display values — they never mutate
9. `ng-template`: fragments; `ng-container`: invisible wrapper; `ng-content`: slots

### Q&A Questions

1. "What's the difference between `templateUrl` and `template` in `@Component`? When would you use each?"
2. "If I create a new component with `ng generate component`, what files are created and what is automatically updated?"
3. "What does the `selector` field in `@Component` determine? Can I use it as an attribute instead of an element?"
4. "What's the Angular equivalent of React's `children` prop? How does it work differently?"
5. "We used `?.` (safe navigation). When would you use `!.` (non-null assertion) instead? What are the risks?"

---

## Take-Home Exercises

1. **Component creation**: Create a `user-profile` component with `@Input()` for a `User` object (name, email, role). Display all three fields using interpolation and pipes (`titlecase`, `date`).

2. **Parent → child data flow**: Create a `user-list` parent component with an array of 4 users. Use `*ngFor` to render `<app-user-profile>` for each one. *(Structural directives covered in Part 2 — try it now as a preview)*

3. **Pipe challenge**: Display a price of `1234.5` as `$1,234.50 USD` using the `currency` pipe with custom formatting. Then display it as euros.

4. **Safe navigation**: Create a component with a `currentUser: User | null` property. Display a greeting that reads `"Welcome, [name]!"` using safe navigation so it doesn't throw when `currentUser` is null. Bonus: add a fallback message when null.

---

→ **TRANSITION to Part 2:** "Now that we understand how components are structured and how templates work, Part 2 goes deeper: all four binding types with two-way binding, template reference variables, lifecycle hooks, NgModule patterns, and structural directives in detail."
