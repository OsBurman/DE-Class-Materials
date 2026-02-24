import { CanDeactivateFn } from '@angular/router';

// The interface constrains which components can use this guard.
// Any component that uses unsavedChangesGuard must implement canDeactivate().
export interface CanComponentDeactivate {
  canDeactivate(): boolean;
}

// CanDeactivateFn<T> receives the component instance as its first argument.
// The guard simply delegates to the component's own canDeactivate() method,
// keeping the "dirty" logic inside the component where it belongs.
export const unsavedChangesGuard: CanDeactivateFn<CanComponentDeactivate> =
  (component) => component.canDeactivate();
