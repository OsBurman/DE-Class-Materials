import { Component } from '@angular/core';

@Component({
  selector: 'app-edit-profile',
  template: `
    <div style="padding:1.5rem;">
      <h2>Edit Profile</h2>

      <label>
        Name:
        <input
          type="text"
          [value]="name"
          (input)="onNameChange($event)"
          style="margin-left:0.5rem;"
        />
      </label>

      <div style="margin-top:1rem;">
        <button (click)="save()">Save</button>
        <span *ngIf="hasUnsavedChanges" style="margin-left:1rem; color:orange;">
          ⚠ Unsaved changes
        </span>
        <span *ngIf="saved" style="margin-left:1rem; color:green;">
          ✔ Saved!
        </span>
      </div>

      <p style="margin-top:1rem;">
        <a routerLink="/">← Go Home</a>
        (try clicking while there are unsaved changes)
      </p>
    </div>
  `,
})
export class EditProfileComponent {
  name = 'Jane Doe';
  hasUnsavedChanges = false;
  saved = false;

  onNameChange(event: Event): void {
    this.name = (event.target as HTMLInputElement).value;
    this.hasUnsavedChanges = true;
    this.saved = false;
  }

  save(): void {
    // In a real app, call an API here.
    this.hasUnsavedChanges = false;
    this.saved = true;
  }

  // Called by the CanDeactivate guard before the user leaves the route.
  canDeactivate(): boolean {
    if (this.hasUnsavedChanges) {
      return window.confirm('You have unsaved changes. Leave anyway?');
    }
    return true;
  }
}
