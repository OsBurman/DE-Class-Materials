// ============================================================
// Day 16b — Angular Fundamentals
// File: Components & Templates — TypeScript Class
// ============================================================
// This is a self-contained component demonstration.
// In a real Angular project this would live in:
//   src/app/course-card/course-card.component.ts
// ============================================================
import { Component, OnInit } from '@angular/core';

// ─── SECTION 1: THE @Component DECORATOR ─────────────────────
//
// @Component is what turns a plain TypeScript class into an
// Angular component. It's a decorator — a TypeScript feature
// that adds metadata to the class below it.
//
// The metadata tells Angular:
//  - What HTML tag to use for this component (selector)
//  - Where to find the template (templateUrl)
//  - Where to find the styles (styleUrls)

@Component({
  selector: 'app-course-card',           // Used as: <app-course-card>
  templateUrl: './course-card.component.html',
  styleUrls: ['./course-card.component.css'],
})
export class CourseCardComponent implements OnInit {
  // ─── SECTION 2: COMPONENT PROPERTIES ───────────────────────
  //
  // Properties declared here are available in the template.
  // TypeScript types are inferred or explicit.

  // Primitive properties
  title: string = 'React Fundamentals';
  instructorName: string = 'Jane Smith';
  durationHours: number = 6;
  rating: number = 4.8;
  reviewCount: number = 1247;
  price: number = 49.99;
  isAvailable: boolean = true;
  isNew: boolean = true;

  // Object property
  instructor = {
    name: 'Jane Smith',
    title: 'Senior Frontend Engineer',
    avatarUrl: '/assets/instructors/jane.png',
    bio: '10+ years building React applications at scale.',
  };

  // Array property
  tags: string[] = ['react', 'javascript', 'frontend', 'hooks'];

  // Date property
  publishedDate: Date = new Date('2024-01-15');

  // Computed property (getter)
  get discountedPrice(): number {
    return this.price * 0.8; // 20% off
  }

  get ratingStars(): string {
    return '★'.repeat(Math.round(this.rating)) + '☆'.repeat(5 - Math.round(this.rating));
  }

  // ─── SECTION 3: COMPONENT METHODS ──────────────────────────
  //
  // Methods declared here can be called from the template
  // using event binding: (click)="enroll()"

  enroll(): void {
    alert(`Enrolling in: ${this.title}`);
  }

  addToWishlist(): void {
    console.log(`Added "${this.title}" to wishlist`);
  }

  formatPrice(price: number): string {
    return `$${price.toFixed(2)}`;
  }

  // ─── SECTION 4: ngOnInit ────────────────────────────────────
  //
  // ngOnInit is a lifecycle hook that runs AFTER the component
  // is created and its inputs are bound. This is where you
  // typically fetch data or initialize things.
  // (Full lifecycle hooks are covered in Part 2)

  ngOnInit(): void {
    console.log(`CourseCardComponent initialized for: ${this.title}`);
    // In a real app: this.courseService.getCourse(this.courseId).subscribe(...)
  }
}
