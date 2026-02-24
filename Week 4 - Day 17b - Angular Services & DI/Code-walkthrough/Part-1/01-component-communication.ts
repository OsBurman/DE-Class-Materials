// =============================================================================
// 01-component-communication.ts — @Input, @Output, EventEmitter
// =============================================================================
// Angular components form a tree. Data flows DOWN via @Input (parent → child)
// and UP via @Output + EventEmitter (child → parent).
//
// SECTIONS:
//  1. @Input — passing data from parent to child
//  2. @Output + EventEmitter — sending events from child to parent
//  3. Two-way binding with [(ngModel)] (banana-in-a-box syntax)
//  4. Input transformation and required inputs (Angular 16+)
//  5. Parent-to-child method call via @ViewChild
// =============================================================================

import {
  Component, Input, Output, EventEmitter,
  ViewChild, OnChanges, SimpleChanges, OnInit
} from '@angular/core';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — @Input: Parent passes data DOWN to child
// ─────────────────────────────────────────────────────────────────────────────

// CHILD COMPONENT — receives a course object from its parent
@Component({
  selector: 'app-course-card',
  template: `
    <div class="course-card" [class.featured]="featured">
      <h3>{{ course.title }}</h3>
      <p>Instructor: {{ course.instructor }}</p>
      <p>Duration: {{ course.duration }} weeks</p>
      <span *ngIf="featured" class="badge">⭐ Featured</span>
    </div>
  `
})
export class CourseCardComponent implements OnChanges {
  // @Input() declares a property the parent can bind to
  @Input() course!: { title: string; instructor: string; duration: number };

  // @Input with a default value — parent can override, or the default is used
  @Input() featured: boolean = false;

  // ngOnChanges fires whenever an @Input value changes from the parent
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['course']) {
      console.log('Course input changed:', changes['course'].currentValue);
      console.log('Previous value:', changes['course'].previousValue);
    }
  }
}

// PARENT COMPONENT — creates the data and passes it to the child
@Component({
  selector: 'app-course-list-parent',
  template: `
    <h2>Available Courses</h2>

    <!-- Bind the 'course' input using property binding [propertyName]="expression" -->
    <app-course-card
      *ngFor="let c of courses"
      [course]="c"
      [featured]="c.id === featuredId">
    </app-course-card>

    <button (click)="featuredId = featuredId === 1 ? 2 : 1">Toggle Featured</button>
  `
})
export class CourseListParentComponent {
  featuredId = 1;

  courses = [
    { id: 1, title: 'React Hooks', instructor: 'Scott', duration: 1 },
    { id: 2, title: 'Spring Boot', instructor: 'Alex',  duration: 2 },
    { id: 3, title: 'Angular DI',  instructor: 'Maria', duration: 1 },
  ];
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — @Output + EventEmitter: Child sends events UP to parent
// ─────────────────────────────────────────────────────────────────────────────

// CHILD COMPONENT — has a button that emits an event with data
@Component({
  selector: 'app-enroll-button',
  template: `
    <div class="enroll-panel">
      <h4>{{ courseName }}</h4>
      <!-- (click) is Angular's event binding syntax -->
      <button (click)="onEnrollClick()">Enroll Now</button>
      <button (click)="onWishlistClick()">Add to Wishlist</button>
    </div>
  `
})
export class EnrollButtonComponent {
  @Input()  courseName!: string;
  @Input()  courseId!: number;

  // @Output declares an event the parent can listen for
  // EventEmitter<T> — T is the type of data emitted
  @Output() enrolled    = new EventEmitter<{ id: number; name: string }>();
  @Output() wishlisted  = new EventEmitter<number>();  // just emits the id

  onEnrollClick(): void {
    // .emit() fires the event and sends data up to the parent
    this.enrolled.emit({ id: this.courseId, name: this.courseName });
  }

  onWishlistClick(): void {
    this.wishlisted.emit(this.courseId);
  }
}

// PARENT COMPONENT — listens for events from child using (eventName)="handler($event)"
@Component({
  selector: 'app-course-detail-parent',
  template: `
    <h2>Course Enrollment Demo</h2>

    <app-enroll-button
      [courseName]="'Angular Services & DI'"
      [courseId]="17"
      (enrolled)="onEnrolled($event)"
      (wishlisted)="onWishlisted($event)">
    </app-enroll-button>

    <p *ngIf="enrollmentMessage">{{ enrollmentMessage }}</p>
    <ul>
      <li *ngFor="let id of wishlist">Course #{{ id }} in wishlist</li>
    </ul>
  `
})
export class CourseDetailParentComponent {
  enrollmentMessage = '';
  wishlist: number[] = [];

  // $event contains whatever the child passed to .emit()
  onEnrolled(event: { id: number; name: string }): void {
    this.enrollmentMessage = `✅ Enrolled in "${event.name}" (ID: ${event.id})`;
  }

  onWishlisted(courseId: number): void {
    if (!this.wishlist.includes(courseId)) {
      this.wishlist.push(courseId);
    }
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Two-Way Binding [(ngModel)] — the "banana in a box"
// ─────────────────────────────────────────────────────────────────────────────
// [(ngModel)] is shorthand for [ngModel]="value" (ngModelChange)="value=$event"
// Requires FormsModule imported in your NgModule.

@Component({
  selector: 'app-search-bar',
  template: `
    <div>
      <label>Search Courses:</label>

      <!-- Two-way binding — reads AND writes the searchTerm property -->
      <input [(ngModel)]="searchTerm" placeholder="Type to search…" />

      <!-- Equivalent long form — same behavior: -->
      <!-- <input [ngModel]="searchTerm" (ngModelChange)="searchTerm = $event" /> -->

      <p>You searched for: "{{ searchTerm }}"</p>
      <button (click)="searchTerm = ''">Clear</button>
    </div>
  `
})
export class SearchBarComponent {
  searchTerm = '';
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Input with alias and required (Angular 16+)
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-rating-badge',
  template: `
    <span class="badge badge-{{ colorClass }}">
      {{ label }}: {{ rating | number:'1.1-1' }}
    </span>
  `
})
export class RatingBadgeComponent {
  // 'alias' lets the parent use a different attribute name than the property name
  // @Input('courseRating') internal property name is 'rating'
  @Input('courseRating') rating: number = 0;

  // Computed getter — derived from the input
  get label(): string { return this.rating >= 4 ? 'Top Rated' : 'Rating'; }
  get colorClass(): string { return this.rating >= 4 ? 'success' : 'secondary'; }
}

// PARENT uses the alias name 'courseRating', not 'rating'
// <app-rating-badge [courseRating]="4.8"></app-rating-badge>

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — @ViewChild: Parent calls child methods directly
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-video-player',
  template: `
    <div class="player">
      <p>{{ isPlaying ? '▶ Playing' : '⏸ Paused' }}: {{ currentVideo }}</p>
    </div>
  `
})
export class VideoPlayerComponent {
  isPlaying = false;
  currentVideo = '';

  play(videoTitle: string): void {
    this.currentVideo = videoTitle;
    this.isPlaying = true;
    console.log(`Playing: ${videoTitle}`);
  }

  pause(): void { this.isPlaying = false; }
}

@Component({
  selector: 'app-course-viewer',
  template: `
    <h2>Course Viewer (ViewChild Demo)</h2>

    <!-- The child component instance -->
    <app-video-player></app-video-player>

    <!-- Parent controls the child imperatively -->
    <button (click)="playLesson('Intro to DI')">▶ Play Lesson 1</button>
    <button (click)="playLesson('Services Deep Dive')">▶ Play Lesson 2</button>
    <button (click)="pausePlayer()">⏸ Pause</button>
  `
})
export class CourseViewerComponent implements OnInit {
  // @ViewChild queries the template for a child component/directive/element
  // { static: false } means it's available after ngAfterViewInit (after render)
  @ViewChild(VideoPlayerComponent) player!: VideoPlayerComponent;

  ngOnInit(): void {
    // ⚠️ player is NOT available in ngOnInit — template hasn't rendered yet
    // Use ngAfterViewInit() instead for @ViewChild access
  }

  playLesson(title: string): void {
    this.player.play(title);  // call child method directly
  }

  pausePlayer(): void {
    this.player.pause();
  }
}
