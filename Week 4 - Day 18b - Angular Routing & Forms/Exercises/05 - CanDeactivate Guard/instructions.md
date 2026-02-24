# Exercise 05 – CanDeactivate Guard

## Learning Objectives
- Create a **CanDeactivate** functional guard using `CanDeactivateFn`
- Allow a component to signal "dirty" state to the guard via a `canDeactivate()` method
- Prompt users before discarding unsaved changes

## Background
`CanDeactivate` runs when a user tries to leave a route. Unlike `CanActivate` (which checks
whether you *can enter* a route), `CanDeactivate` asks whether it is safe to *leave*. The guard
receives a reference to the **component instance** so it can call a method on it to check for
unsaved changes.

## Exercise

You have a simple "Edit Profile" page. When the user modifies the name field the component
sets `hasUnsavedChanges = true`. The guard should show a `confirm()` dialog if the user tries
to navigate away with unsaved changes.

### Starter code TODOs

**`edit-profile.component.ts`**
- TODO 1 – Add a `hasUnsavedChanges: boolean` property (starts as `false`)
- TODO 2 – Add a `canDeactivate()` method that returns `boolean`:
  - If `hasUnsavedChanges`, call `window.confirm('You have unsaved changes. Leave anyway?')` and return its result
  - Otherwise return `true`
- TODO 3 – Set `hasUnsavedChanges = true` whenever the user changes the input

**`unsaved-changes.guard.ts`**
- TODO 4 – Import `CanDeactivateFn` from `@angular/router`
- TODO 5 – Define an interface `CanComponentDeactivate` with a `canDeactivate(): boolean` method
- TODO 6 – Export a `const unsavedChangesGuard: CanDeactivateFn<CanComponentDeactivate>` that calls `component.canDeactivate()`

**`app.module.ts`**
- TODO 7 – Add `canDeactivate: [unsavedChangesGuard]` to the edit-profile route

## Files
```
starter-code/
  app.module.ts
  app.component.ts
  edit-profile.component.ts
  unsaved-changes.guard.ts
solution/
  app.module.ts
  app.component.ts
  edit-profile.component.ts
  unsaved-changes.guard.ts
```

## Expected Behaviour
1. Navigating to `/edit-profile` shows a form with a name field.
2. Typing in the field marks the form as dirty.
3. Clicking the "Go Home" link triggers the guard → a confirm dialog appears.
4. Confirming allows navigation; cancelling keeps the user on the edit page.
5. Saving clears `hasUnsavedChanges`, so navigating away requires no confirmation.
