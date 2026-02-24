# Angular Fundamentals — Overview & Architecture
## Day 16b Reference: Angular Overview, CLI & Project Structure

---

## SECTION 1: WHAT IS ANGULAR?

Angular is a **full-featured framework** (not just a library) built and maintained by Google.
Released in 2016 (Angular 2+), rewritten from AngularJS (Angular 1.x).

### Key characteristics:
- **Opinionated** — Angular tells you how to structure your app (unlike React, which is flexible)
- **TypeScript-first** — TypeScript is not optional; it's the default and only recommended language
- **Complete platform** — routing, HTTP client, forms, animations, testing all built-in
- **Component-based** — everything is a component, organized into modules

### Angular vs React:

| Feature              | Angular                          | React                         |
|----------------------|----------------------------------|-------------------------------|
| Type                 | Full framework                   | UI library                    |
| Language             | TypeScript (required)            | JavaScript / TypeScript       |
| Learning curve       | Steeper (more concepts upfront)  | Gentler (fewer concepts)      |
| Structure            | Highly opinionated               | Very flexible                 |
| Forms                | Built-in (Template & Reactive)   | External library needed       |
| HTTP                 | Built-in HttpClient              | Fetch / Axios (external)      |
| Routing              | Built-in RouterModule            | React Router (external)       |
| State management     | Services + RxJS                  | Redux / Zustand (external)    |
| Used by              | Google, Forbes, Samsung          | Meta, Airbnb, Netflix         |

---

## SECTION 2: ANGULAR ARCHITECTURE

```
Angular Application Architecture
─────────────────────────────────────────────────────────────────

  ┌─────────────────────────────────────────────────────────┐
  │                    AppModule (Root)                     │
  │                                                         │
  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
  │  │  Component  │  │  Component  │  │  Component  │    │
  │  │  (UI layer) │  │             │  │             │    │
  │  │  TS + HTML  │  │  TS + HTML  │  │  TS + HTML  │    │
  │  │  + CSS      │  │  + CSS      │  │  + CSS      │    │
  │  └──────┬──────┘  └─────────────┘  └─────────────┘    │
  │         │                                               │
  │         │ injects                                       │
  │         ▼                                               │
  │  ┌─────────────┐  ┌─────────────┐                      │
  │  │   Service   │  │   Service   │  ← Business logic    │
  │  │ (DI layer)  │  │             │    & data access      │
  │  └──────┬──────┘  └─────────────┘                      │
  │         │                                               │
  │         │                                               │
  │         ▼                                               │
  │  ┌─────────────────────────────┐                        │
  │  │     HttpClient / APIs       │  ← Data layer          │
  │  └─────────────────────────────┘                        │
  └─────────────────────────────────────────────────────────┘
```

### Core building blocks:

1. **Modules** (`@NgModule`)
   - Containers that group related components, directives, pipes, and services
   - Every app has a root `AppModule`
   - Feature modules organize large apps (e.g., `UsersModule`, `CoursesModule`)

2. **Components** (`@Component`)
   - The UI building blocks — a TypeScript class + HTML template + CSS styles
   - Each component controls a piece of the view
   - Identified by a CSS selector (e.g., `app-course-card`)

3. **Templates**
   - HTML files with Angular-specific syntax (data binding, directives)
   - Compiled by Angular at build time into efficient DOM operations

4. **Services** (`@Injectable`)
   - Reusable business logic and data access
   - Shared across components via Dependency Injection
   - Keeps components thin (UI only) and services fat (logic)

5. **Dependency Injection (DI)**
   - Angular's built-in DI framework
   - Services are provided once, injected where needed
   - No manual instantiation: `constructor(private userService: UserService)`

6. **Directives**
   - Structural: modify DOM structure (`*ngIf`, `*ngFor`)
   - Attribute: modify appearance/behavior (`[class]`, `[style]`, custom)

7. **Pipes**
   - Transform displayed values: `{{ date | date:'short' }}`, `{{ price | currency }}`

---

## SECTION 3: ANGULAR CLI & PROJECT SETUP

### Installation:
```bash
# Install Angular CLI globally (one time)
npm install -g @angular/cli

# Verify installation
ng version
```

### Creating a new project:
```bash
ng new my-angular-app
# Prompts:
#   Would you like to add Angular routing? → Yes
#   Which stylesheet format? → CSS (or SCSS)

cd my-angular-app
ng serve             # start dev server at http://localhost:4200
ng serve --open      # start AND open browser automatically
```

### Key CLI commands:
```bash
# Generate a component
ng generate component course-card
# shorthand:
ng g c course-card

# Generate a service
ng generate service courses
ng g s courses

# Generate a module
ng generate module courses
ng g m courses

# Generate a pipe
ng generate pipe truncate
ng g p truncate

# Build for production
ng build --configuration production

# Run tests
ng test
ng e2e
```

---

## SECTION 4: PROJECT STRUCTURE

```
my-angular-app/
├── src/
│   ├── app/
│   │   ├── app.component.ts       ← Root component (TypeScript class)
│   │   ├── app.component.html     ← Root component template
│   │   ├── app.component.css      ← Root component styles
│   │   ├── app.component.spec.ts  ← Root component unit tests
│   │   └── app.module.ts          ← Root NgModule
│   │
│   ├── assets/                    ← Static files (images, fonts)
│   ├── environments/
│   │   ├── environment.ts         ← Dev environment variables
│   │   └── environment.prod.ts    ← Prod environment variables
│   │
│   ├── index.html                 ← Single HTML page (SPA shell)
│   ├── main.ts                    ← App entry point — bootstraps AppModule
│   └── styles.css                 ← Global styles
│
├── angular.json                   ← Angular CLI workspace config
├── package.json                   ← npm dependencies
├── tsconfig.json                  ← TypeScript compiler config
└── tsconfig.app.json              ← TypeScript config for the app
```

### How Angular boots:
```
1. Browser loads index.html
2. index.html loads main.ts (compiled bundle)
3. main.ts calls: platformBrowserDynamic().bootstrapModule(AppModule)
4. AppModule declares AppComponent as the bootstrap component
5. Angular finds <app-root> in index.html and renders AppComponent there
6. AppComponent template renders — app is alive!
```

---

## SECTION 5: ANGULAR COMPONENT ANATOMY (PREVIEW)

Every component consists of exactly 4 files generated by the CLI:

```
course-card/
├── course-card.component.ts       ← The class (logic)
├── course-card.component.html     ← The template (view)
├── course-card.component.css      ← The styles (scoped)
└── course-card.component.spec.ts  ← The tests
```

### The @Component decorator — all key options:
```typescript
@Component({
  selector: 'app-course-card',    // CSS selector: used as <app-course-card>
  templateUrl: './course-card.component.html',  // external template file
  styleUrls: ['./course-card.component.css'],   // external style files

  // Alternative: inline template and styles (small components)
  // template: '<div>{{ title }}</div>',
  // styles: ['div { color: red; }'],

  // Change detection strategy (advanced — covered later):
  // changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CourseCardComponent {
  // Component class — properties and methods
  title = 'React Fundamentals';
  rating = 4.8;
}
```

---

## SECTION 6: TYPESCRIPT IN ANGULAR CONTEXT

Angular relies on TypeScript features that plain JavaScript cannot do:

```typescript
// 1. Decorators — @Component, @NgModule, @Injectable, @Input, @Output
@Component({ selector: 'app-root' })
export class AppComponent { }

// 2. Interfaces — strict contracts for data shapes
interface Course {
  id: number;
  title: string;
  instructor: string;
  price: number;
  isAvailable: boolean;
}

// 3. Access modifiers in constructors — DI shorthand
@Injectable({ providedIn: 'root' })
export class CourseService {
  // Angular's DI + TypeScript shorthand: declares AND injects HttpClient
  constructor(private http: HttpClient) { }
}

// 4. Generics — typed HTTP responses
// http.get<Course[]>('/api/courses') → TypeScript knows the return type

// 5. Enums — for status values, roles, etc.
enum CourseLevel {
  Beginner = 'BEGINNER',
  Intermediate = 'INTERMEDIATE',
  Advanced = 'ADVANCED'
}

// 6. Type assertions & null safety
const title = document.querySelector('h1') as HTMLHeadingElement;
```

Key TypeScript settings Angular uses:
- `strict: true` in tsconfig (catches bugs at compile time)
- `experimentalDecorators: true` (needed for @Component, @NgModule etc.)
- `emitDecoratorMetadata: true` (needed for DI to know parameter types)
