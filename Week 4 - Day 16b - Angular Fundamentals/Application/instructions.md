# Day 16b Application — Angular Fundamentals: Dev Team Directory

## Overview

You'll build the same **Dev Team Directory** as the React track — but in Angular. This mirrors the React Day 16a app so you can directly compare the two frameworks. You'll create components, use data binding in all four forms, apply structural directives, and hook into the component lifecycle.

---

## Learning Goals

- Set up an Angular project and understand the folder structure
- Create components with templates and TypeScript logic
- Use all four data binding techniques
- Apply `*ngIf` and `*ngFor` structural directives
- Use template reference variables
- Understand component lifecycle with `ngOnInit`
- Organize code with `NgModule`

---

## Prerequisites

- Node.js 18+ installed
- `npm install` in the starter-code folder
- `npm run start` (runs `ng serve`)
- Open `http://localhost:4200`

---

## Project Structure

```
starter-code/
├── package.json
├── angular.json
├── tsconfig.json
└── src/
    ├── index.html
    ├── main.ts
    ├── styles.css
    └── app/
        ├── app.module.ts
        ├── app.component.ts       ← root component
        ├── app.component.html
        ├── team-data.ts           ← provided data array
        ├── team-member-card/
        │   ├── team-member-card.component.ts    ← TODO
        │   └── team-member-card.component.html  ← TODO
        └── team-list/
            ├── team-list.component.ts           ← TODO
            └── team-list.component.html         ← TODO
```

---

## Part 1 — `TeamMemberCard` Component

**Task 1 — @Input properties**  
Declare `@Input()` properties matching the `TeamMember` interface:  
`name`, `role`, `skills` (string[]), `isAvailable` (boolean), `avatarUrl` (string).

**Task 2 — Template**  
In `team-member-card.component.html`:
- Interpolation: `{{ name }}`, `{{ role }}`
- Property binding: `[src]="avatarUrl"`, `[alt]="name"`
- Use `*ngIf` for the availability badge: if `isAvailable` show green badge, else show gray
- Use `*ngFor` to render each skill tag

**Task 3 — Event binding**  
Add a "View Profile" button. On click, use event binding `(click)="onViewProfile()"` to call a method that logs `"Viewing profile for: [name]"` to the console.

**Task 4 — Two-way binding (template reference)**  
Add a template reference variable `#card` on the card's root `<div>`. In the button's click handler, also toggle a `selected` boolean property — when true, add a CSS class `selected` using property binding `[class.selected]="selected"`.

---

## Part 2 — `TeamList` Component

**Task 5 — @Input members array**  
Receive `@Input() members: TeamMember[] = []`.

**Task 6 — `*ngFor` + `trackBy`**  
Render a `<app-team-member-card>` for each member using `*ngFor`. Add a `trackBy` function: `trackByMemberId(index, member) { return member.id; }`.

**Task 7 — `*ngIf` empty state**  
Use `*ngIf` above the list: if `members.length === 0`, show a "No members found" paragraph. If not empty, show the grid (`*ngIf / *ngIf` with `else` template).

**Task 8 — Lifecycle hook**  
Implement `ngOnInit()` in `TeamList` — log `"TeamList initialized with [count] members"` to the console.

---

## Part 3 — `AppComponent`

**Task 9 — Interpolation in app template**  
Display a page title using interpolation: `{{ title }}`.

**Task 10 — Two-way binding on a filter input**  
Add an `<input>` with `[(ngModel)]="filterText"` to filter the displayed team members by name. Pass the filtered list to `<app-team-list>`.

---

## Stretch Goals

1. Create a `FilterPipe` (preview for Day 17b) that filters the members array.
2. Add a `*ngSwitch` to show a different badge color based on the member's role category.
3. Add an `@Output() EventEmitter` so `TeamMemberCard` emits a `selected` event to `TeamList`.

---

## Submission Checklist

- [ ] `@Input()` used to pass data from parent to child
- [ ] Interpolation `{{ }}` used
- [ ] Property binding `[ ]` used
- [ ] Event binding `( )` used
- [ ] Two-way binding `[( )]` used with `ngModel`
- [ ] `*ngFor` used with `trackBy`
- [ ] `*ngIf` with `else` template used
- [ ] `ngOnInit` lifecycle hook implemented
- [ ] App runs without errors on `http://localhost:4200`
