// =============================================================================
// data-binding.component.ts — All Four Angular Binding Types
// =============================================================================
// Angular's template binding system has FOUR distinct patterns.
// Understanding which one to use, and when, is fundamental to Angular.
//
//  TYPE 1 — Interpolation           {{ expression }}
//            Direction: Component → DOM (one-way, to text content)
//
//  TYPE 2 — Property Binding        [target]="expression"
//            Direction: Component → DOM (one-way, to DOM properties/attributes)
//
//  TYPE 3 — Event Binding           (event)="statement"
//            Direction: DOM → Component (one-way, from DOM events)
//
//  TYPE 4 — Two-Way Binding         [(ngModel)]="property"
//            Direction: Component ↔ DOM (both ways simultaneously)
//            REQUIRES: FormsModule imported in AppModule
// =============================================================================

import { Component } from '@angular/core';

// ── Data model ────────────────────────────────────────────────────────────────
interface RegistrationForm {
  name: string;
  email: string;
  course: string;
  agreed: boolean;
}

@Component({
  selector: 'app-data-binding',
  templateUrl: './data-binding.component.html',
  styleUrls: ['./data-binding.component.css']
})
export class DataBindingComponent {

  // ════════════════════════════════════════════════════════════════════════════
  // TYPE 1 — Interpolation Data
  // ════════════════════════════════════════════════════════════════════════════
  // These properties are READ by the template via {{ }}.
  // Changing them causes Angular to update the displayed text automatically.

  studentName = 'Alice Johnson';
  courseTitle = 'Angular Fundamentals';
  rating = 4.8;
  enrollmentDate = new Date('2024-01-15');
  tags = ['Angular', 'TypeScript', 'RxJS'];

  // ════════════════════════════════════════════════════════════════════════════
  // TYPE 2 — Property Binding Data
  // ════════════════════════════════════════════════════════════════════════════
  // Property binding sets DOM properties (NOT HTML attributes — an important
  // distinction we'll discuss in the walkthrough).

  imageUrl = 'https://angular.io/assets/images/logos/angular/angular.svg';
  imageAlt = 'Angular logo';
  isButtonDisabled = false;
  isCardHighlighted = true;
  buttonLabel = 'Enroll Now';
  progressValue = 65;       // <progress [value]="progressValue" [max]="100">
  linkTarget = '_blank';

  // ════════════════════════════════════════════════════════════════════════════
  // TYPE 3 — Event Binding Handlers
  // ════════════════════════════════════════════════════════════════════════════
  // These methods are CALLED BY the template when DOM events fire.

  clickCount = 0;
  lastKey = '';
  mousePosition = { x: 0, y: 0 };
  hovered = false;

  onButtonClick(): void {
    this.clickCount++;
    console.log(`Button clicked ${this.clickCount} time(s)`);
  }

  onKeyPress(event: KeyboardEvent): void {
    this.lastKey = event.key;
  }

  onMouseMove(event: MouseEvent): void {
    this.mousePosition = { x: event.clientX, y: event.clientY };
  }

  onMouseEnter(): void { this.hovered = true; }
  onMouseLeave(): void { this.hovered = false; }

  toggleDisabled(): void {
    this.isButtonDisabled = !this.isButtonDisabled;
  }

  // ════════════════════════════════════════════════════════════════════════════
  // TYPE 4 — Two-Way Binding via ngModel
  // ════════════════════════════════════════════════════════════════════════════
  // ngModel keeps a form field and a component property in sync.
  // Typing in the input IMMEDIATELY updates the property.
  // Changing the property programmatically IMMEDIATELY updates the input.
  //
  // PREREQUISITE: FormsModule must be imported in AppModule.
  //   import { FormsModule } from '@angular/forms';
  //   Add to the imports[] array of @NgModule.
  //
  // The [()] syntax is nicknamed "banana in a box" — the banana () is event
  // binding; the box [] is property binding. Together they do both at once.

  // Live form model — updated by ngModel in real time
  form: RegistrationForm = {
    name: '',
    email: '',
    course: 'angular-fundamentals',
    agreed: false
  };

  formSubmitted = false;
  availableCourses = [
    { value: 'angular-fundamentals', label: 'Angular Fundamentals' },
    { value: 'rxjs-deep-dive',       label: 'RxJS Deep Dive' },
    { value: 'ngrx-state-mgmt',      label: 'NgRx State Management' },
  ];

  onSubmit(): void {
    this.formSubmitted = true;
    console.log('Form submitted:', this.form);
  }

  resetForm(): void {
    this.form = { name: '', email: '', course: 'angular-fundamentals', agreed: false };
    this.formSubmitted = false;
  }

  // Demonstrate programmatic update of two-way bound property
  fillSampleData(): void {
    this.form.name = 'Bob Sample';
    this.form.email = 'bob@example.com';
    this.form.agreed = true;
    // Notice: the input fields in the browser update IMMEDIATELY
  }
}
