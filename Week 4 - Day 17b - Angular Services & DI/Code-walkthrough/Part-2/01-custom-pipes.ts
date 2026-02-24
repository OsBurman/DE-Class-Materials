// =============================================================================
// 01-custom-pipes.ts — Creating Custom Pipes
// =============================================================================
// When Angular's built-in pipes don't cover your use case, you build your own.
// A custom pipe is a class decorated with @Pipe that implements PipeTransform.
//
// Custom pipes are:
//   • Pure by default — only re-run when input value REFERENCE changes
//   • Impure (pure: false) — re-run on every change detection cycle
//
// SECTIONS:
//  1. Simple value-transform pipe — TruncatePipe
//  2. Pipe with arguments — FilterByPipe
//  3. Formatting pipe — TimeAgoPipe (date → "5 minutes ago")
//  4. Impure pipe — SearchFilterPipe (filters a live array)
//  5. Using custom pipes in components
// =============================================================================

import { Pipe, PipeTransform, Component } from '@angular/core';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Simple Transform Pipe: TruncatePipe
// ─────────────────────────────────────────────────────────────────────────────
// Truncates a long string to a max length and appends an ellipsis.
//
// Usage in template:  {{ longText | truncate }}
//                     {{ longText | truncate:100 }}
//                     {{ longText | truncate:50:'…' }}

@Pipe({
  name: 'truncate'  // this is the name used in templates: | truncate
})
export class TruncatePipe implements PipeTransform {
  // transform(value, ...args) is the required method from PipeTransform
  // The first argument is the value being piped
  // Additional arguments come from the template: | truncate:50:'…'
  transform(value: string, maxLength: number = 80, suffix: string = '…'): string {
    if (!value) return '';
    if (value.length <= maxLength) return value;
    return value.slice(0, maxLength).trimEnd() + suffix;
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Pipe With Arguments: FilterByPipe
// ─────────────────────────────────────────────────────────────────────────────
// Filters an array of objects by a specified property and search term.
//
// Usage: {{ courses | filterBy:'title':'react' }}
//        {{ users   | filterBy:'role':'admin'  }}

@Pipe({
  name: 'filterBy',
  pure: true  // default — only re-evaluates when reference to 'courses' changes
})
export class FilterByPipe implements PipeTransform {
  transform<T extends Record<string, unknown>>(
    items: T[],
    property: string,
    searchTerm: string
  ): T[] {
    if (!items || !searchTerm) return items;
    const term = searchTerm.toLowerCase();
    return items.filter(item => {
      const val = item[property];
      return typeof val === 'string' && val.toLowerCase().includes(term);
    });
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Formatting Pipe: TimeAgoPipe
// ─────────────────────────────────────────────────────────────────────────────
// Converts a Date or timestamp to a human-readable relative time string.
//
// Usage: {{ post.createdAt | timeAgo }}
// Output: "just now" / "5 minutes ago" / "3 hours ago" / "2 days ago"

@Pipe({
  name: 'timeAgo'
})
export class TimeAgoPipe implements PipeTransform {
  transform(value: Date | string | number): string {
    if (!value) return '';

    const date = value instanceof Date ? value : new Date(value);
    const now = new Date();
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    if (seconds < 5)  return 'just now';
    if (seconds < 60) return `${seconds} seconds ago`;

    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes} minute${minutes !== 1 ? 's' : ''} ago`;

    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours} hour${hours !== 1 ? 's' : ''} ago`;

    const days = Math.floor(hours / 24);
    if (days < 30) return `${days} day${days !== 1 ? 's' : ''} ago`;

    const months = Math.floor(days / 30);
    if (months < 12) return `${months} month${months !== 1 ? 's' : ''} ago`;

    const years = Math.floor(months / 12);
    return `${years} year${years !== 1 ? 's' : ''} ago`;
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Impure Pipe: SearchFilterPipe
// ─────────────────────────────────────────────────────────────────────────────
// An IMPURE pipe runs on every change detection cycle — even if inputs haven't
// changed by reference. Use for:
//   • Arrays mutated in place (push/pop)
//   • Async data that changes frequently
//   • Filters tied to a live search input
//
// ⚠️ Impure pipes can hurt performance. Use sparingly. Consider a computed
// property in the component instead for complex filtering.

@Pipe({
  name: 'searchFilter',
  pure: false  // impure — re-runs on every change detection cycle
})
export class SearchFilterPipe implements PipeTransform {
  transform(items: { title: string; level: string }[], query: string): typeof items {
    if (!query.trim()) return items;
    const q = query.toLowerCase();
    return items.filter(item =>
      item.title.toLowerCase().includes(q) ||
      item.level.toLowerCase().includes(q)
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Demo Component Using All Custom Pipes
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-custom-pipes-demo',
  template: `
    <h2>Custom Pipes Demo</h2>

    <!-- TruncatePipe -->
    <h3>truncate</h3>
    <p>{{ longDescription | truncate }}</p>
    <p>{{ longDescription | truncate:50 }}</p>
    <p>{{ longDescription | truncate:30:'[read more]' }}</p>

    <!-- FilterByPipe -->
    <h3>filterBy</h3>
    <ul>
      <li *ngFor="let c of courses | filterBy:'title':'spring'">
        {{ c.title }} — {{ c.level }}
      </li>
    </ul>

    <!-- TimeAgoPipe -->
    <h3>timeAgo</h3>
    <ul>
      <li *ngFor="let post of posts">
        "{{ post.title }}" — {{ post.createdAt | timeAgo }}
      </li>
    </ul>

    <!-- SearchFilterPipe (impure) — responds to every keystroke -->
    <h3>searchFilter (impure)</h3>
    <input [(ngModel)]="searchQuery" placeholder="Search courses…" />
    <ul>
      <li *ngFor="let c of courses | searchFilter:searchQuery">
        {{ c.title }} ({{ c.level }})
      </li>
    </ul>

    <!-- Chaining custom pipes with built-in pipes -->
    <h3>Chaining custom + built-in pipes</h3>
    <p>{{ longDescription | truncate:40 | uppercase }}</p>
  `
})
export class CustomPipesDemoComponent {
  searchQuery = '';

  longDescription = `Angular is a platform and framework for building single-page
    client applications using HTML and TypeScript. It implements core and optional
    functionality as a set of TypeScript libraries.`;

  courses = [
    { title: 'React Fundamentals',    level: 'Beginner'     },
    { title: 'Angular Services & DI', level: 'Intermediate' },
    { title: 'Spring Boot Basics',    level: 'Beginner'     },
    { title: 'Spring Security',       level: 'Advanced'     },
    { title: 'Angular Signals',       level: 'Advanced'     },
  ];

  posts = [
    { title: 'Just posted',   createdAt: new Date() },
    { title: 'Minutes ago',   createdAt: new Date(Date.now() - 5 * 60 * 1000) },
    { title: 'Hours ago',     createdAt: new Date(Date.now() - 3 * 60 * 60 * 1000) },
    { title: 'Days ago',      createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000) },
    { title: 'Last month',    createdAt: new Date(Date.now() - 45 * 24 * 60 * 60 * 1000) },
  ];
}

// ─────────────────────────────────────────────────────────────────────────────
// HOW TO REGISTER CUSTOM PIPES
// ─────────────────────────────────────────────────────────────────────────────
/*
  In your AppModule (or a shared module), add each pipe to the `declarations` array:

  @NgModule({
    declarations: [
      AppComponent,
      TruncatePipe,        // ← declare here
      FilterByPipe,        // ← declare here
      TimeAgoPipe,         // ← declare here
      SearchFilterPipe,    // ← declare here
    ],
    exports: [
      TruncatePipe,        // ← export if you want to use it in feature modules
      ...
    ]
  })
  export class AppModule {}

  Or generate with CLI: ng generate pipe truncate
  This auto-creates the file AND adds it to declarations.
*/
