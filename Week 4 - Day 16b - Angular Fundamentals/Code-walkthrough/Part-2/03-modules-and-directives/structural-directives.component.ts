// =============================================================================
// structural-directives.component.ts — NgModule & Structural Directives
// =============================================================================
// PART A — NgModule in depth
//   @NgModule: declarations, imports, exports, providers, bootstrap
//   BrowserModule vs CommonModule
//   Feature module pattern
//
// PART B — Structural Directives
//   *ngIf   — conditional rendering
//   *ngFor  — list rendering with trackBy
//   *ngSwitch — multi-branch conditional
//   The <ng-template> desugaring explanation
// =============================================================================

import { Component } from '@angular/core';

// ── Data models ───────────────────────────────────────────────────────────────
interface Course {
  id: number;
  title: string;
  level: 'beginner' | 'intermediate' | 'advanced';
  enrolled: boolean;
  rating: number;
}

type FilterType = 'all' | 'enrolled' | 'available';
type LevelFilter = 'all' | 'beginner' | 'intermediate' | 'advanced';

@Component({
  selector: 'app-structural-directives',
  templateUrl: './structural-directives.component.html',
  styleUrls: ['./structural-directives.component.css']
})
export class StructuralDirectivesComponent {

  // ── *ngIf demo data ───────────────────────────────────────────────────────
  isLoggedIn = false;
  showDetails = false;
  loadingState: 'idle' | 'loading' | 'loaded' | 'error' = 'idle';
  currentUser = { name: 'Alice', role: 'instructor' };

  // ── *ngFor demo data ──────────────────────────────────────────────────────
  courses: Course[] = [
    { id: 101, title: 'Angular Fundamentals',   level: 'beginner',     enrolled: true,  rating: 4.8 },
    { id: 102, title: 'RxJS Deep Dive',          level: 'intermediate', enrolled: false, rating: 4.6 },
    { id: 103, title: 'NgRx State Management',   level: 'advanced',     enrolled: true,  rating: 4.9 },
    { id: 104, title: 'Angular Performance',     level: 'advanced',     enrolled: false, rating: 4.7 },
    { id: 105, title: 'TypeScript Essentials',   level: 'beginner',     enrolled: false, rating: 4.5 },
  ];

  activeFilter: FilterType = 'all';
  levelFilter: LevelFilter = 'all';

  // ── *ngSwitch demo data ───────────────────────────────────────────────────
  selectedTab: 'overview' | 'curriculum' | 'reviews' | 'instructor' = 'overview';

  // ── Methods ───────────────────────────────────────────────────────────────

  // Used by *ngIf demo
  simulateLoad(): void {
    this.loadingState = 'loading';
    setTimeout(() => { this.loadingState = 'loaded'; }, 1500);
  }

  simulateError(): void {
    this.loadingState = 'loading';
    setTimeout(() => { this.loadingState = 'error'; }, 1000);
  }

  resetLoad(): void {
    this.loadingState = 'idle';
  }

  // Computed property for filtered list (avoids calling a function in template)
  // ⚠️ Returning a new array from a template function every CD cycle is wasteful.
  // A getter or a stored property is better — here we use a getter.
  get filteredCourses(): Course[] {
    return this.courses.filter(course => {
      const matchesEnrollment =
        this.activeFilter === 'all'      ? true :
        this.activeFilter === 'enrolled' ? course.enrolled :
                                           !course.enrolled;

      const matchesLevel =
        this.levelFilter === 'all' ? true :
        course.level === this.levelFilter;

      return matchesEnrollment && matchesLevel;
    });
  }

  // trackBy function — tells Angular how to uniquely identify list items.
  // Without trackBy: Angular destroys and recreates DOM nodes on every update.
  // With trackBy:    Angular reuses existing DOM nodes, only updating changed ones.
  //
  // SIGNATURE: (index: number, item: T) => any
  // RETURN VALUE: any unique identifier for the item (usually the ID)
  trackByCourseId(index: number, course: Course): number {
    return course.id;
  }

  toggleEnrollment(course: Course): void {
    course.enrolled = !course.enrolled;
  }

  addCourse(): void {
    const newId = Math.max(...this.courses.map(c => c.id)) + 1;
    this.courses.push({
      id: newId,
      title: `New Course ${newId}`,
      level: 'beginner',
      enrolled: false,
      rating: 4.0
    });
  }

  removeCourse(id: number): void {
    this.courses = this.courses.filter(c => c.id !== id);
  }
}
