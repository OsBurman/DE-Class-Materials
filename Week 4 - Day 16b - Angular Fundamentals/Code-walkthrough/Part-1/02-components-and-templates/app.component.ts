// =============================================================================
// app.component.ts — Root Application Component
// =============================================================================
// The AppComponent is the ROOT of every Angular application.
// Angular bootstraps this component first, rendering it into <app-root>
// inside index.html. All other components are children of AppComponent.
//
// KEY CONCEPTS IN THIS FILE:
//  1. @Component decorator — tells Angular "this class is a component"
//  2. selector — the HTML tag name used in templates
//  3. templateUrl / styleUrls — external template and style files
//  4. Class properties — data the template can bind to
//  5. Using a child component (<app-course-card>) in a parent template
// =============================================================================

import { Component } from '@angular/core';

// ── Step 1: Import the Course interface ──────────────────────────────────────
// Interfaces live in the same project — Angular has no special interface folder.
// Re-using an interface defined alongside CourseCardComponent is fine for now.
// In a real app you'd move shared interfaces to a `models/` or `shared/` folder.
import { Course } from './course-card.component';

// ── Step 2: The @Component decorator ─────────────────────────────────────────
// This is what turns a plain TypeScript class into an Angular component.
// Angular reads these metadata values at compile time.
@Component({
  selector: 'app-root',          // <app-root> in index.html
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  // ── Step 3: Class Properties (the component's state / data) ───────────────
  // These properties are accessible inside the template via data binding.
  // Angular change detection watches these and re-renders when they change.

  title = 'Angular Course Platform';     // Simple string property
  subtitle = 'Learn Angular from the ground up';

  selectedCourseId: number | null = null; // Tracks which course was selected

  // ── Step 4: An array of Course objects ────────────────────────────────────
  // This data would normally come from a Service (HTTP call).
  // For today we hard-code it here to focus on component + template concepts.
  courses: Course[] = [
    {
      id: 1,
      title: 'Angular Fundamentals',
      instructor: 'Jane Smith',
      duration: '8 weeks',
      rating: 4.8,
      enrolled: false,
      isFeatured: true
    },
    {
      id: 2,
      title: 'TypeScript Deep Dive',
      instructor: 'John Doe',
      duration: '4 weeks',
      rating: 4.5,
      enrolled: true,
      isFeatured: false
    },
    {
      id: 3,
      title: 'RxJS & Reactive Programming',
      instructor: 'Alice Johnson',
      duration: '6 weeks',
      rating: 4.9,
      enrolled: false,
      isFeatured: true
    }
  ];

  // ── Step 5: Event Handler Methods ─────────────────────────────────────────
  // The child component (CourseCardComponent) emits events via @Output().
  // This parent method receives those events.
  //
  // SYNTAX REMINDER: In the parent template you will see:
  //   (courseSelected)="onCourseSelected($event)"
  //        ↑ event name                ↑ $event = the emitted value

  onCourseSelected(courseId: number): void {
    this.selectedCourseId = courseId;
    console.log(`Course selected: ${courseId}`);
  }

  // ── Step 6: Helper method ─────────────────────────────────────────────────
  // Used in the template to compute derived data.
  // Angular calls this on EVERY change-detection cycle — for heavy computations
  // prefer a Pipe or storing the result in a property instead.
  get enrolledCount(): number {
    return this.courses.filter(c => c.enrolled).length;
  }
}
