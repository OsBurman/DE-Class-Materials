# Angular Services, Dependency Injection & Component Communication
### Subtitle: "Connecting the pieces of your Angular app"

---

**OPENING SCRIPT**

"Good morning everyone. Today's lesson is one of those sessions where things start to really click together. Up to this point you've been building components in relative isolation — you know how to create them, style them, display data. But real applications aren't isolated. Data needs to flow between components, logic needs to be shared, and your app needs to be organized in a way that doesn't turn into spaghetti the moment it grows. That's exactly what today covers.

By the end of this lesson you'll understand how components talk to each other using inputs and outputs, how directives let you control what's in the DOM, how pipes transform your displayed data, and — the big one — how Angular Services and Dependency Injection give you a clean, scalable way to share logic and data across your entire application. Let's get into it."

---

## PART 1: COMPONENT COMMUNICATION

---

### SLIDE 1: Section Title — Component Communication
**Subtitle:** @Input, @Output, and EventEmitter

**Visual:** A simple diagram showing:
- Parent Component → Child Component (arrow labeled @Input)
- Child Component → Parent Component (arrow labeled @Output / EventEmitter)

**Script:**
"Angular apps are built from trees of components. A parent component renders child components, and those children might render their own children. The question is: how do they talk to each other? Angular gives you two decorators for this — @Input and @Output."

---

### SLIDE 2: @Input — Passing Data Down

**Code:**
```typescript
// child.component.ts
@Component({ selector: 'app-child', template: `<p>{{ title }}</p>` })
export class ChildComponent {
  @Input() title: string = '';
}

// parent.component.html
<app-child [title]="'Hello from Parent'"></app-child>
```

**Callout:** "Property binding [ ] flows data from parent to child"

**Script:**
"@Input is how a parent passes data down to a child. Think of it like a function parameter — you're saying 'this component accepts a value from outside.' In the child component you decorate a property with @Input. In the parent template, you use square bracket property binding to pass the value in. The square brackets are important — they tell Angular this is a dynamic binding, not a plain string.

The parent owns the data. The child just receives it and displays it. The child has no ability to modify the parent's copy of that data directly — it's one-way. That's intentional. It keeps data flow predictable.

One important gotcha: don't mutate an @Input object directly inside the child. If your parent passes in an object and you mutate its properties in the child, you're changing the parent's data without the parent knowing. Either emit an event and let the parent update it, or if you must, clone the object first."

---

### SLIDE 3: @Output & EventEmitter — Sending Data Up

**Code:**
```typescript
// child.component.ts
@Output() userClicked = new EventEmitter<string>();

onButtonClick() {
  this.userClicked.emit('Button was clicked!');
}

// child.component.html
<button (click)="onButtonClick()">Click Me</button>

// parent.component.html
<app-child (userClicked)="handleClick($event)"></app-child>

// parent.component.ts
handleClick(message: string) {
  console.log(message);
}
```

**Script:**
"Now what about the other direction? What if something happens inside the child — a button click, a form submission — and the parent needs to know about it? That's @Output and EventEmitter.

In the child you create an EventEmitter and decorate it with @Output. When something happens, you call .emit() on it, optionally passing data along. In the parent template you listen to it using event binding — parentheses — just like you'd listen to a native DOM event like (click). The $event variable catches whatever the child emitted.

This pattern is the foundation of how Angular components communicate without being tightly coupled. The parent doesn't reach into the child, and the child doesn't reach into the parent. They communicate through a clearly defined interface."

---

### SLIDE 4: @Input + @Output Together — The Full Picture

**Visual / Diagram:**
```
Parent data → [@Input] → Child displays it
                              ↓
                         User interacts
                              ↓
Parent updates data ← [@Output emits] ←
```

**Callout:** "The parent owns the data. The child receives it and reports back. Clear, predictable, one direction at a time."

**Script:**
"When you combine @Input and @Output together — the parent sends data down, and the child emits updates back up — you get a clean two-way communication pattern. The parent owns the data. The child is purely a view that receives state and reports user interactions. This is how real Angular apps are structured at the component level.

Now let's look at how you actually control what those components render in the DOM."

---

## PART 2: DIRECTIVES

---

### SLIDE 5: Section Title — Directives
**Subtitle:** Structural and Attribute Directives

**Visual:** Two columns:
- **Structural** (marked with *) — changes DOM structure
- **Attribute** — changes appearance or behavior

**Script:**
"Directives are one of Angular's most powerful features. A directive is essentially a class that adds behavior to elements in the DOM. There are three kinds — component directives, which are what you've been building all along, structural directives, and attribute directives. Today we're focusing on the latter two."

---

### SLIDE 6: Structural Directives — *ngIf

**Code:**
```html
<div *ngIf="isLoggedIn">Welcome back!</div>

<div *ngIf="isLoggedIn; else guestBlock">Welcome back!</div>
<ng-template #guestBlock><p>Please log in.</p></ng-template>
```

**Key points:**
- Adds/removes elements from the DOM entirely — not just hiding them
- The * is syntactic sugar for ng-template
- Use CSS class binding if you only want to hide something visually

**Script:**
"Structural directives change the structure of the DOM — they add, remove, or rearrange elements. You recognize them by the asterisk prefix.

*ngIf is the most common one. When the condition is true, the element exists in the DOM. When it's false, Angular removes it entirely — it's not just hidden with CSS, it's gone. This matters for performance and for lifecycle hooks. If a component is removed with *ngIf, it's destroyed, and its lifecycle hooks fire accordingly.

If you just want to hide something visually, use a CSS class binding. If you want to stop a component from existing and running altogether, use *ngIf.

The else clause is useful — you provide a reference to an ng-template and Angular renders that block when the condition is false. The ng-template itself never renders on its own — it's just a blueprint Angular can use when needed."

---

### SLIDE 7: Structural Directives — *ngFor

**Code:**
```html
<ul>
  <li *ngFor="let user of users; let i = index; trackBy: trackById">
    {{ i + 1 }}. {{ user.name }}
  </li>
</ul>

// In component:
trackById(index: number, user: User) {
  return user.id;
}
```

**Key points:**
- Exposes local variables: index, first, last, even, odd
- trackBy is critical for performance — always include it

**Script:**
"*ngFor lets you loop over a collection and stamp out DOM elements for each item. The syntax gives you a local variable — 'let user of users' — and Angular also exposes several useful local variables: index for the position, first and last as booleans, even and odd for alternating styling.

Now pay attention to trackBy because this is something a lot of beginners skip and then wonder why their app feels sluggish. Let's look at exactly why it matters on the next slide."

---

### SLIDE 8: Why trackBy Matters — Before vs. After

**Visual: Two-column comparison**

| Without trackBy | With trackBy |
|---|---|
| Array updates → Angular destroys ALL list items | Array updates → Angular identifies what changed by ID |
| Rebuilds every DOM element from scratch | Only updates the elements that actually changed |
| Heavy re-render even if only one item changed | Minimal DOM updates |

**Code callout:**
```typescript
// Give Angular a stable identity for each item
trackById(index: number, user: User) {
  return user.id;  // Angular uses this to match old vs new items
}
```

**Script:**
"Without trackBy, every time your array changes Angular throws away every single list element in the DOM and re-creates them all — even if only one item was added or changed. With trackBy, you give Angular a way to identify each item — usually by a unique ID — and Angular diffs the old and new lists. It only touches the DOM elements that actually changed.

For a list of 5 items, this won't matter. For a list of 500 items that updates every few seconds — like a live data feed or a large table — this is a significant performance difference. Make trackBy a habit now."

---

### SLIDE 9: Structural Directives — ngSwitch

**Code:**
```html
<div [ngSwitch]="currentRole">
  <p *ngSwitchCase="'admin'">Admin Panel</p>
  <p *ngSwitchCase="'editor'">Editor View</p>
  <p *ngSwitchCase="'viewer'">Read Only View</p>
  <p *ngSwitchDefault>Unknown Role</p>
</div>
```

**Key points:**
- Cleaner than chaining multiple *ngIf statements
- [ngSwitch] on the container is an attribute binding
- *ngSwitchCase and *ngSwitchDefault are structural directives inside it
- Only one block renders at a time

**Script:**
"*ngSwitch is great when you have multiple mutually exclusive conditions — it's much cleaner than chaining several *ngIf statements. Notice the syntax: [ngSwitch] goes on the container element as a regular attribute binding — it's evaluating the expression. Then inside, *ngSwitchCase and *ngSwitchDefault are the structural directives that stamp out the right element. *ngSwitchDefault is your fallback, just like a default case in a JavaScript switch statement."

---

### SLIDE 10: Attribute Directives — ngClass and ngStyle

**Code:**
```html
<!-- Conditionally apply CSS classes -->
<div [ngClass]="{ 'active': isActive, 'disabled': isDisabled }">...</div>

<!-- Conditionally apply inline styles -->
<div [ngStyle]="{ 'color': textColor, 'font-size': fontSize + 'px' }">...</div>
```

**Key points:**
- Attribute directives change appearance or behavior — they do NOT add or remove DOM elements
- ngClass: object keys are class names, values are boolean conditions
- ngStyle: object keys are CSS properties, values are the style values
- Prefer ngClass over ngStyle — keeps your styles in CSS where they belong

**Script:**
"Attribute directives are the other category. They change how an existing element looks or behaves — they don't touch the DOM structure at all.

The most common built-in ones are ngClass and ngStyle. ngClass lets you conditionally apply CSS classes based on component state. ngStyle lets you apply inline styles dynamically. Both accept objects where the keys are class names or style properties and the values are the conditions or values.

The practical advice: use ngClass for most styling work. Define your classes in your CSS file and just apply them conditionally. ngStyle is fine for truly dynamic values like a color picker result where you can't pre-define the class."

---

## PART 3: PIPES

---

### SLIDE 11: Section Title — Pipes
**Subtitle:** Transforming Displayed Data

**Visual:** `Data → | pipe → Transformed Display`

**Script:**
"Pipes are Angular's way of transforming data for display purposes. They sit in your template after a vertical bar character — hence the name — and they take a value in and produce a transformed value out. Critically, they don't mutate your component data. They only change how it looks in the template. Your component's actual data stays untouched."

---

### SLIDE 12: The Most Useful Built-In Pipes

**Code:**
```html
{{ today | date:'longDate' }}         <!-- February 25, 2026 -->
{{ price | currency:'USD' }}          <!-- $1,200.00 -->
{{ description | uppercase }}         <!-- HELLO WORLD -->
{{ description | lowercase }}         <!-- hello world -->
{{ bigNumber | number:'1.2-2' }}      <!-- 1,200.50 -->
```

**Callout — number pipe format string:**
`'minIntegerDigits.minFractionDigits-maxFractionDigits'`
So `'1.2-2'` means: at least 1 integer digit, exactly 2 decimal places.

**Script:**
"Angular ships with a solid set of built-in pipes. DatePipe lets you format dates with format strings. CurrencyPipe handles money formatting. UpperCasePipe and LowerCasePipe are self-explanatory. NumberPipe handles decimal places and thousands separators — the format string reads as min integer digits, then a dot, then min and max fraction digits separated by a dash.

There are additional pipes available — json, slice, async, and others — which are listed on the reference slide coming up next. For now, these five are the ones you'll use constantly."

---

### SLIDE 13: Pipe Reference — Additional Built-Ins

**Reference table:**

| Pipe | Example | Output |
|---|---|---|
| json | `{{ obj \| json }}` | Pretty-prints object (great for debugging) |
| slice | `{{ list \| slice:0:3 }}` | First 3 items of an array |
| async | `{{ data$ \| async }}` | Subscribes to an Observable (covered shortly) |
| percent | `{{ 0.75 \| percent }}` | 75% |
| titlecase | `{{ 'hello world' \| titlecase }}` | Hello World |

**Callout:** "Keep this as a reference. The five on the previous slide are your daily drivers."

**Script:**
"Here's a quick reference for the rest. The async pipe is special and we'll cover it properly when we get to Observables in a few slides. The others are straightforward — json is particularly handy when debugging, you can drop it onto any object in your template and see exactly what's in it."

---

### SLIDE 14: Chaining Pipes & Parameters

**Code:**
```html
<!-- Chaining: output of one becomes input of the next -->
{{ today | date:'shortDate' | uppercase }}

<!-- Parameters: passed after a colon -->
{{ price | currency:'EUR':'symbol':'1.2-2' }}
```

**Key points:**
- Pipes chain left to right
- Parameters come after a colon
- Multiple parameters use multiple colons

**Script:**
"Pipes can be chained — the output of one becomes the input of the next, reading left to right. Parameters are passed after a colon. Multiple parameters each get their own colon. This is clean, readable, and keeps your component code free of display-formatting logic."

---

### SLIDE 15: Creating a Custom Pipe

**Code:**
```typescript
// truncate.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'truncate' })
export class TruncatePipe implements PipeTransform {
  transform(value: string, limit: number = 50, trail: string = '...'): string {
    if (!value) return '';
    return value.length > limit ? value.substring(0, limit) + trail : value;
  }
}

// In template:
{{ longText | truncate:100:'… read more' }}
```

**Steps callout:**
1. Create the class
2. Decorate with `@Pipe({ name: '...' })`
3. Implement `PipeTransform`
4. Write the `transform` method
5. Declare in NgModule's `declarations` array

**Script:**
"Custom pipes follow a simple recipe. You create a class and decorate it with @Pipe, giving it a name — this is what you'll use in templates. Then you implement the PipeTransform interface, which requires one method: transform. The first parameter is always the value being piped in. Additional parameters are the ones you pass with colons in the template.

You must declare the pipe in your NgModule's declarations array, just like a component. Then it's available anywhere in that module.

This truncate pipe is a perfect real-world example — a common display need that doesn't belong in every individual component but gets reused in many places. Write it once, use it everywhere."

---

## PART 4: SERVICES & DEPENDENCY INJECTION

---

### SLIDE 16: Section Title — Services & Dependency Injection
**Subtitle:** The backbone of scalable Angular apps

**Visual:** Multiple components all pointing to a single Service box in the center.

**Script:**
"This is the heart of today's lesson. Services and Dependency Injection are what separate Angular beginners from Angular developers. Everything before this point has been about individual components doing their own thing. Services are about shared logic and shared state. Let's start with the problem they solve."

---

### SLIDE 17: The Problem — Without Services

**Visual: Two-column layout**

**Without Services:**
- Component A fetches users from the API
- Component B also fetches users from the API
- Component C also fetches users from the API
- Change the API endpoint → update 3 files
- Bug in the logic → fix it 3 times
- Each component has its own separate copy of the data

**The result:**
Duplicated logic, inconsistent state, maintenance nightmare.

**Script:**
"Imagine you have three components that all need to display a list of users. Without services, each one would have its own copy of the HTTP call, its own copy of the filtering logic, its own separate version of the data. Change the API endpoint and you're updating three files. Have a bug in the filtering logic and you're fixing it three times. And the data in each component is its own separate copy — they can drift out of sync.

A service is the solution. It's just a TypeScript class that holds logic or data that multiple components need."

---

### SLIDE 18: The Solution — What a Service Is

**Visual: Clean diagram**
```
[Component A]  [Component B]  [Component C]
      \               |               /
       \              |              /
        ↓             ↓             ↓
              [ UserService ]
              - getUsers()
              - addUser()
              - users data lives here
```

**Callout:** "Services follow the Single Responsibility Principle — one class, one job. Components become lightweight: they just coordinate between the user and the service."

**Script:**
"A service is a TypeScript class that holds logic or data that multiple components need. It could be fetching data from an API, managing a piece of application state like the current user, handling calculations, logging — anything that isn't specifically about displaying a view belongs in a service.

Components become lightweight — they inject the service and call its methods. The logic and data live in one place. Fix a bug once. Update an endpoint once. And all three components are looking at the same data."

---

### SLIDE 19: Creating a Service

**Code:**
```typescript
// user.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private users: string[] = ['Alice', 'Bob', 'Charlie'];

  getUsers(): string[] {
    return this.users;
  }

  addUser(name: string): void {
    this.users.push(name);
  }
}
```

**Callout:** "`@Injectable` marks it as injectable. `providedIn: 'root'` makes it a singleton available app-wide."

**Script:**
"Creating a service is straightforward. You create a class and decorate it with @Injectable. The most important option is providedIn. When you set it to 'root', Angular registers this service with the root injector — meaning there is exactly one instance of it for your entire application. Every component that injects it gets the same instance. This is how services share state — they're singletons.

Use the Angular CLI to scaffold this: `ng generate service user`. It creates the file with the @Injectable decorator already in place."

---

### SLIDE 20: CLI Scaffold — What ng generate service Creates

**Visual: Two-column layout**

**Left — Run this command:**
```bash
ng generate service user
```
or the shorthand:
```bash
ng g s user
```

**Right — Files created:**
```
src/app/
├── user.service.ts        ← your service
└── user.service.spec.ts   ← its test file
```

**user.service.ts already contains:**
```typescript
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor() { }
}
```

**Script:**
"When you run ng generate service, the CLI gives you the service file pre-wired with @Injectable and providedIn: 'root' already set. It also creates a spec file for testing. You just add your methods. Always use the CLI for this — it eliminates the chance of forgetting the decorator or misspelling anything."

---

### SLIDE 21: What is Dependency Injection?

**Visual:**
```
[ComponentA]    [ComponentB]    [ComponentC]
      \               |               /
       \              |              /
        ↓             ↓             ↓
         Angular's Injector (the middleman)
                      |
                      ↓
              [ UserService instance ]
                (one shared instance)
```

**Definition callout:** "DI is a design pattern where a class receives its dependencies rather than creating them itself."

**Script:**
"Dependency Injection sounds academic but the idea is simple. Instead of your component doing `const service = new UserService()` — which would create a brand new, isolated instance that no other component shares — you ask Angular to give you an instance. Angular's injector knows how to create services and manages their lifetimes. You just declare what you need, and Angular handles the rest.

This matters for three reasons. First, sharing — all components that inject the same root-level service get the same instance, so they share the same data. Second, testability — in tests you can inject a fake version of a service instead of the real one. Third, decoupling — your component doesn't need to know how to construct its dependencies."

---

### SLIDE 22: Injecting a Service — Constructor Injection

**Code:**
```typescript
// Traditional approach — constructor injection
// Most common in existing Angular codebases
import { Component } from '@angular/core';
import { UserService } from './user.service';

@Component({ ... })
export class UserListComponent {
  users: string[];

  constructor(private userService: UserService) {
    this.users = this.userService.getUsers();
  }
}
```

**Callout:** "The `private` keyword makes `userService` a class property automatically. Angular sees the type annotation and looks it up in the injector."

**Script:**
"The traditional way to inject a service — and what you'll see in most existing Angular code — is constructor injection. You add a parameter to the constructor with a type annotation of the service class. Angular sees the type, looks up the matching service in its injector, and passes the instance in. The private keyword means it becomes a class property automatically. You can now use this.userService anywhere in the component."

---

### SLIDE 23: Injecting a Service — The Modern inject() Function

**Code:**
```typescript
// Modern approach (Angular 14+) — inject() function
import { Component, inject } from '@angular/core';
import { UserService } from './user.service';

@Component({ ... })
export class UserListComponent {
  private userService = inject(UserService);
  users = this.userService.getUsers();
}
```

**Comparison callout:**

| | Constructor Injection | inject() |
|---|---|---|
| Where you'll see it | Most existing codebases | Modern Angular projects |
| Works in | Components, Services | Components, Directives, Pipes, Services |
| Syntax | Constructor parameter | Property assignment |
| Result | Identical — same instance from the injector |

**Script:**
"The newer approach using the inject() function is cleaner and increasingly preferred in modern Angular. You call inject() with the service class as the argument and assign it to a property. No constructor needed. Both approaches give you the exact same result — your component has the same shared service instance either way.

You'll need to know both: constructor injection to read existing code, inject() for new code you write."

---

### SLIDE 24: The Injector Hierarchy — Overview

**Visual: Tree diagram**
```
         Root Injector (providedIn: 'root')
         — One instance, available everywhere —
                      |
           ┌──────────┴──────────┐
      Module Injector        Module Injector
   (providers in NgModule)
           |
    Component Injector
   (providers in @Component)
```

**Key concept:** "Where you provide a service determines how many instances exist and who can access it."

**Script:**
"Angular has a hierarchy of injectors, and where you provide a service determines its scope — how many instances exist and who can see them. There are three levels. Let's look at each one separately."

---

### SLIDE 25: Injector Scope — Root (Singleton)

**Code:**
```typescript
@Injectable({
  providedIn: 'root'   // ← registered at the top of the hierarchy
})
export class AuthService { }
```

**Result:**
- ✅ One instance for the entire application
- ✅ Available to any component, directive, pipe, or service
- ✅ State is shared universally

**Best for:** Auth, user data, HTTP data services, app-wide state

**Script:**
"providedIn: 'root' should be your default choice for most services. There is one instance, and it lives for the entire lifetime of the app. Every component anywhere in the app that injects AuthService gets the exact same object. This is what makes shared state possible — they're all reading from and writing to the same instance."

---

### SLIDE 26: Injector Scope — NgModule Level

**Code:**
```typescript
// feature.module.ts
@NgModule({
  providers: [FeatureService]   // ← registered at module level
})
export class FeatureModule { }
```

**Result:**
- Components in this module share one instance of FeatureService
- A different module that also provides FeatureService gets its own separate instance
- Components outside this module cannot access it

**Best for:** Feature-specific services that shouldn't be app-wide

**Script:**
"When you add a service to the providers array of an @NgModule, every component in that module shares one instance — but it's completely separate from any other module's instance. This is useful when you have a feature that has its own data needs that shouldn't bleed into the rest of the app. You get isolation between features while still sharing within a feature."

---

### SLIDE 27: Injector Scope — Component Level

**Code:**
```typescript
@Component({
  selector: 'app-wizard',
  templateUrl: './wizard.component.html',
  providers: [WizardStateService]   // ← registered at component level
})
export class WizardComponent { }
```

**Result:**
- WizardComponent and all its children share a fresh instance
- Every time WizardComponent is created, a new instance of WizardStateService is created with it
- When WizardComponent is destroyed, the service instance is destroyed with it

**Best for:** Sandboxed state — form wizards, modal dialogs, isolated feature instances

**Script:**
"When you add a service to the providers array of a @Component, that component and all its children get their own fresh, isolated instance. Every time the component is created, a new service instance is created. When the component is destroyed, the service goes with it.

This is useful for things like a multi-step form wizard. Each wizard on the page gets its own step-tracking service, completely isolated. If you had two wizards open at the same time, they wouldn't interfere with each other."

---

### SLIDE 28: Sharing Data Between Unrelated Components Using a Service

**Visual:**
```
[ProductComponent]                [HeaderComponent]
  - injects CartService             - injects CartService
  - calls addItem()                 - calls getCount()
           \                               /
            ↓                             ↓
                   [ CartService ]
                   items: Product[]
                   (the same array, one instance)
```

**Code:**
```typescript
// cart.service.ts
@Injectable({ providedIn: 'root' })
export class CartService {
  private items: Product[] = [];

  addItem(product: Product) { this.items.push(product); }
  getItems(): Product[] { return this.items; }
  getCount(): number { return this.items.length; }
}
```

**Script:**
"Here's one of the most important patterns you'll use. Two completely unrelated components — a ProductComponent and a HeaderComponent — need to share cart state. They have no parent-child relationship. @Input and @Output don't help here. The service is the answer.

Both components inject the same CartService singleton. When ProductComponent calls addItem(), the items array in the service updates. When HeaderComponent calls getCount(), it reads from that same updated array. They're not communicating with each other at all — they're both communicating with the shared service. This scales infinitely — add 20 more components that need cart data and they all just inject the service."

---

## PART 5: OBSERVABLES & REACTIVE SERVICES

---

### SLIDE 29: Section Title — Observables
**Subtitle:** A primer before we make services reactive

**Script:**
"Before we look at reactive services, we need to understand what an Observable is — because we're about to use one, and it'll look strange if you haven't seen the concept."

---

### SLIDE 30: What is an Observable?

**Visual: Three-panel comparison**

| | Regular value | Promise | Observable |
|---|---|---|---|
| Delivers | One value, right now | One value, in the future | Many values, over time |
| Example | `const x = 5` | Fetch API result | Mouse clicks, live data, HTTP stream |
| Cancel it? | N/A | No | Yes |

**The core idea:**
```typescript
// An Observable is like a stream.
// You subscribe to it to start receiving values.
// It can emit values now, later, or continuously.

const clicks$ = fromEvent(button, 'click');

clicks$.subscribe(event => {
  console.log('User clicked!', event);
});
```

**Callout:** "The $ suffix on a variable name is a convention meaning 'this is an Observable'."

**Script:**
"An Observable is a stream of values over time. Think of it like a water pipe — you connect to it by subscribing, and values flow through to you whenever they're emitted. This is different from a regular function call, which gives you one value right now, or a Promise, which gives you one value in the future. An Observable can emit many values — or none — and keeps emitting until it completes or you unsubscribe.

You'll use Observables constantly in Angular — HTTP calls return Observables, user input events can be Observables, and your services will expose Observables so components automatically update when data changes.

The dollar sign convention on variable names — like `items$` — just signals to other developers that this variable holds an Observable."

---

### SLIDE 31: Subscribing and the Problem it Creates

**Code:**
```typescript
// Subscribing manually in a component
export class ProductListComponent implements OnInit, OnDestroy {
  items: Product[] = [];
  private sub: Subscription;

  ngOnInit() {
    this.sub = this.productService.items$.subscribe(items => {
      this.items = items;
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();  // ← REQUIRED — or you get a memory leak
  }
}
```

**Callout:** "If you forget to unsubscribe, the subscription keeps running even after the component is destroyed. This is a memory leak."

**Script:**
"When you subscribe to an Observable manually in a component, you have to unsubscribe when the component is destroyed — otherwise the subscription keeps running in memory long after the component is gone. This is a memory leak, and in a large app it adds up.

You manage this by storing the subscription and calling unsubscribe() in ngOnDestroy. It works — but it's boilerplate you have to remember every time.

The better way is to let Angular handle it for you, which is exactly what the async pipe does."

---

### SLIDE 32: The async Pipe — Let Angular Handle Subscriptions

**Code:**
```html
<!-- Angular subscribes for you and unsubscribes when component is destroyed -->
<ul>
  <li *ngFor="let item of cartService.items$ | async">
    {{ item.name }}
  </li>
</ul>

<span>Total items: {{ (cartService.items$ | async)?.length }}</span>
```

**Comparison:**

| Manual subscribe | async pipe |
|---|---|
| You subscribe in ngOnInit | Angular subscribes for you |
| You must call unsubscribe in ngOnDestroy | Angular unsubscribes automatically |
| Boilerplate in every component | No boilerplate |

**Script:**
"The async pipe is one of Angular's most useful features. When you pipe an Observable through async in a template, Angular subscribes to it for you and automatically unsubscribes when the component is destroyed. No memory leaks, no boilerplate. The template re-renders every time the Observable emits a new value.

The ?. optional chaining is a safety measure — before the Observable emits its first value, it's null, and calling .length on null would throw an error. The question mark handles that gracefully.

Get in the habit of using the async pipe. It makes your component code much cleaner."

---

### SLIDE 33: Making Services Reactive with BehaviorSubject

**Code:**
```typescript
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CartService {
  // Internal — only the service can write to this
  private itemsSubject = new BehaviorSubject<Product[]>([]);

  // External — components can subscribe to this
  items$ = this.itemsSubject.asObservable();

  addItem(product: Product) {
    const current = this.itemsSubject.getValue();
    this.itemsSubject.next([...current, product]);  // emit new state
  }
}

// In component template — auto-updates when items$ emits:
<span>{{ cartService.items$ | async | json }}</span>
```

**Script:**
"Now let's bring this all together into a reactive service.

The limitation of the basic CartService we built earlier is that components only get the data once when they initialize — they don't automatically know when it changes. BehaviorSubject from RxJS solves this.

A BehaviorSubject holds a value and lets any subscriber know immediately when that value changes. You keep it private inside the service — it's the write side. You expose it as a plain Observable using asObservable() — that's the read side. External code can listen for changes but can't emit to it directly.

When you add an item, you call .next() with the new array — note the spread operator to create a new array rather than mutating. Any component using the async pipe in its template will automatically re-render with the new data. No manual subscription management, no component-to-component calls. This is the reactive pattern you'll see in professional Angular codebases."

---

## CLOSING

---

### SLIDE 34: Recap — What We Covered

**@Input / @Output / EventEmitter**
Parent-child communication — data flows down, events bubble up

**Structural Directives — *ngIf, *ngFor, ngSwitch**
Control what exists in the DOM; use trackBy with *ngFor

**Attribute Directives — ngClass, ngStyle**
Control appearance and behavior of existing elements

**Pipes**
Transform data for display without mutating the source; build custom pipes for reusable formatting

**Services & Dependency Injection**
Shared logic and state; singleton by default; injector hierarchy controls scope

**Observables & Reactive Services**
Streams of values over time; async pipe handles subscriptions automatically; BehaviorSubject powers reactive state

**Script:**
"Let's bring this all together. Today you learned how components communicate — @Input flows data down, @Output and EventEmitter bubble events up. You learned how structural directives control what exists in the DOM and how to use *ngFor efficiently with trackBy. You saw how pipes keep your templates clean by handling display transformations, and how to build your own. We covered what an Observable is and why the async pipe is your best tool for working with them. Then we went deep on Services — how to create them, how Angular's DI system provides them, how the injector hierarchy controls scope, and how a BehaviorSubject turns a service into a reactive data source."

---

### SLIDE 35: Practical Exercise — Mini Shopping List App

**Build this before next class:**

1. `AppComponent` — root, holds overall layout
2. `ItemListComponent` — uses `*ngFor` to display items (with `trackBy`)
3. `ItemComponent` — receives each item via `@Input`, emits a "remove" event via `@Output`
4. A custom `TruncatePipe` — truncates item names longer than 20 characters
5. An `ItemService` with `providedIn: 'root'` — holds the items array as a BehaviorSubject, exposes `items$`
6. A `CounterComponent` — completely separate from ItemListComponent, injects ItemService, displays the live count using the async pipe

**The goal:** CounterComponent and ItemListComponent should both update reactively when items are added or removed — without talking to each other at all.

**Script:**
"Before next class, build this mini shopping list. It touches everything from today — directives, pipes, @Input/@Output, a shared service, and reactive state via BehaviorSubject and async. If you can build this from scratch, today's material has landed. The key challenge is the last point: CounterComponent and ItemListComponent are completely unrelated. No @Input or @Output between them. The service is the only communication channel. Any questions before we close?"

---

### SLIDE 36: What's Coming Next

**Next Lesson: HTTP Client, Reactive Forms, and RxJS Observables**

**Teaser:** "You've seen BehaviorSubject and the async pipe today — next we go deep on the full reactive paradigm: operators, pipelines, and wiring up real HTTP calls."

---

*End of presentation script.*