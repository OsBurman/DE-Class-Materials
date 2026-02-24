// =============================================================================
// 02-directives.ts — Structural and Attribute Directives
// =============================================================================
// Directives are instructions that extend HTML. Angular has three kinds:
//   1. Components — directives with a template (we've already seen these)
//   2. Structural Directives — change the DOM structure (*ngIf, *ngFor, *ngSwitch)
//   3. Attribute Directives — change appearance/behavior of an element (ngClass, ngStyle, custom)
//
// The asterisk (*) on *ngIf/*ngFor/*ngSwitch is syntactic sugar for
// an <ng-template> wrapper. Angular desugars it during compilation.
//
// SECTIONS:
//  1. *ngIf in depth — else blocks, ng-template, as alias
//  2. *ngFor in depth — index, first/last/even/odd, trackBy
//  3. *ngSwitch in depth
//  4. Attribute directives — ngClass, ngStyle
//  5. Custom attribute directive — HighlightDirective
// =============================================================================

import {
  Component, Directive, Input, HostListener, HostBinding, ElementRef, Renderer2
} from '@angular/core';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — *ngIf in Depth
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-ngif-demo',
  template: `
    <h2>*ngIf Demo</h2>

    <!-- Basic *ngIf — adds/removes element from DOM based on condition -->
    <p *ngIf="isLoggedIn">Welcome back, {{ username }}!</p>

    <!-- *ngIf with else — reference an <ng-template> for the else branch -->
    <p *ngIf="isLoggedIn; else loginPrompt">
      You have {{ notifications }} new notifications.
    </p>
    <ng-template #loginPrompt>
      <p>Please log in to see your notifications.</p>
    </ng-template>

    <!-- *ngIf with then/else — both branches are ng-templates -->
    <div *ngIf="isLoading; then loadingBlock; else contentBlock"></div>
    <ng-template #loadingBlock>
      <p>⏳ Loading courses…</p>
    </ng-template>
    <ng-template #contentBlock>
      <p>✅ {{ courses.length }} courses loaded</p>
    </ng-template>

    <!-- *ngIf as — create a local alias for the truthy value -->
    <!-- Useful when the condition is an async pipe or expensive expression -->
    <div *ngIf="getUser() as user">
      <p>{{ user.name }} — {{ user.role }}</p>
    </div>

    <!-- ⚠️ WATCH OUT: *ngIf removes the element from the DOM entirely.
         It is NOT just hidden (display:none). The component is DESTROYED and
         re-created. Use [hidden] or CSS if you want to preserve state. -->
    <button (click)="isLoggedIn = !isLoggedIn">Toggle Login</button>
    <button (click)="isLoading = !isLoading">Toggle Loading</button>
  `
})
export class NgIfDemoComponent {
  isLoggedIn   = true;
  isLoading    = false;
  username     = 'Scott';
  notifications = 3;
  courses      = ['React', 'Angular', 'Spring Boot'];

  getUser() { return { name: 'Scott', role: 'Instructor' }; }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — *ngFor in Depth
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-ngfor-demo',
  template: `
    <h2>*ngFor Demo</h2>

    <!-- Basic *ngFor -->
    <ul>
      <li *ngFor="let course of courses">{{ course.title }}</li>
    </ul>

    <!-- *ngFor with exported variables: index, first, last, even, odd -->
    <table border="1">
      <tr
        *ngFor="let course of courses;
                index as i;
                first as isFirst;
                last as isLast;
                even as isEven;
                odd as isOdd"
        [class.first-row]="isFirst"
        [class.last-row]="isLast"
        [style.background]="isEven ? '#f5f5f5' : 'white'">

        <td>{{ i + 1 }}</td>
        <td>{{ course.title }}</td>
        <td>{{ isFirst ? '🥇 First' : isLast ? '🏁 Last' : '' }}</td>
        <td>{{ isEven ? 'Even' : 'Odd' }}</td>
      </tr>
    </table>

    <!-- trackBy — critical for performance with large lists
         Without trackBy: Angular re-renders the entire list on any change.
         With trackBy: Angular only re-renders items whose tracked value changed. -->
    <ul>
      <li *ngFor="let course of courses; trackBy: trackById">
        {{ course.title }}
      </li>
    </ul>

    <!-- *ngFor over an object's keys using keyvalue pipe -->
    <ul>
      <li *ngFor="let entry of config | keyvalue">
        {{ entry.key }}: {{ entry.value }}
      </li>
    </ul>

    <button (click)="addCourse()">Add Course</button>
    <button (click)="removeFirst()">Remove First</button>
  `
})
export class NgForDemoComponent {
  courses = [
    { id: 1, title: 'React Fundamentals' },
    { id: 2, title: 'Angular Services & DI' },
    { id: 3, title: 'Spring Boot' },
  ];

  config = { theme: 'dark', language: 'en', notifications: 'enabled' };

  // trackBy function — return a unique identifier for each item
  trackById(index: number, course: { id: number; title: string }): number {
    return course.id;
  }

  addCourse(): void {
    const nextId = this.courses.length + 1;
    this.courses = [...this.courses, { id: nextId, title: `Course ${nextId}` }];
  }

  removeFirst(): void {
    this.courses = this.courses.slice(1);
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — *ngSwitch in Depth
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-ngswitch-demo',
  template: `
    <h2>*ngSwitch Demo</h2>

    <!-- ngSwitch is on the container element (NOT prefixed with *) -->
    <!-- *ngSwitchCase and *ngSwitchDefault are on the child elements -->
    <div [ngSwitch]="userRole">
      <div *ngSwitchCase="'admin'">
        <p>👑 Admin Panel — full access</p>
        <button>Manage Users</button>
      </div>
      <div *ngSwitchCase="'instructor'">
        <p>📚 Instructor Dashboard</p>
        <button>Create Course</button>
      </div>
      <div *ngSwitchCase="'student'">
        <p>🎓 Student Portal</p>
        <button>View Courses</button>
      </div>
      <!-- Default shown when no case matches -->
      <div *ngSwitchDefault>
        <p>🔒 Guest — please log in</p>
      </div>
    </div>

    <!-- Multiple cases for same template — each *ngSwitchCase is separate -->
    <div [ngSwitch]="submissionStatus">
      <p *ngSwitchCase="'pending'">⏳ Awaiting review</p>
      <p *ngSwitchCase="'approved'">✅ Approved</p>
      <p *ngSwitchCase="'rejected'">❌ Rejected — please resubmit</p>
      <p *ngSwitchDefault>—</p>
    </div>

    <select [(ngModel)]="userRole">
      <option value="admin">Admin</option>
      <option value="instructor">Instructor</option>
      <option value="student">Student</option>
      <option value="guest">Guest</option>
    </select>
  `
})
export class NgSwitchDemoComponent {
  userRole: string = 'student';
  submissionStatus: string = 'pending';
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Attribute Directives: ngClass and ngStyle
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-attribute-directives-demo',
  template: `
    <h2>Attribute Directives — ngClass and ngStyle</h2>

    <!-- ngClass — toggle one class -->
    <p [class.active]="isActive">Simple class toggle</p>

    <!-- ngClass — object syntax: key is class name, value is condition -->
    <div [ngClass]="{
      'card': true,
      'card-featured': course.isFeatured,
      'card-expired':  course.isExpired,
      'card-new':      course.daysOld < 7
    }">
      {{ course.title }}
    </div>

    <!-- ngClass — array syntax: multiple classes applied unconditionally -->
    <div [ngClass]="['rounded', 'shadow', themeClass]">Themed Card</div>

    <!-- ngClass — string syntax: space-separated class names -->
    <div [ngClass]="'btn btn-primary'">Button</div>

    <!-- ngStyle — object syntax: key is CSS property (camelCase), value is expression -->
    <div [ngStyle]="{
      'background-color': course.isFeatured ? '#fff3cd' : 'white',
      'border-left':      '4px solid ' + statusColor,
      'font-size.px':     14
    }">
      Styled with ngStyle
    </div>

    <!-- ngStyle — individual property with unit shorthand -->
    <p [style.font-size.em]="1.2">1.2em text</p>
    <p [style.color]="course.isFeatured ? 'gold' : 'inherit'">Conditional color</p>

    <button (click)="isActive = !isActive">Toggle Active</button>
    <button (click)="course.isFeatured = !course.isFeatured">Toggle Featured</button>
  `
})
export class AttributeDirectivesDemoComponent {
  isActive    = false;
  themeClass  = 'theme-dark';
  statusColor = '#28a745';

  course = { title: 'Angular DI', isFeatured: true, isExpired: false, daysOld: 3 };
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Custom Attribute Directive
// ─────────────────────────────────────────────────────────────────────────────
// A custom directive lets you encapsulate DOM manipulation logic and apply it
// to any element with a simple attribute selector.

@Directive({
  selector: '[appHighlight]'  // applies to any element with attribute appHighlight
})
export class HighlightDirective {
  // @Input with the same name as the selector lets you pass a value:
  // <p appHighlight="yellow">Text</p>
  @Input('appHighlight') highlightColor: string = 'lightyellow';

  // @HostBinding binds a property on the host element
  @HostBinding('style.transition') transition = 'background-color 0.3s';

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  // @HostListener listens to events on the host element
  @HostListener('mouseenter')
  onMouseEnter(): void {
    this.renderer.setStyle(this.el.nativeElement, 'background-color', this.highlightColor);
    this.renderer.setStyle(this.el.nativeElement, 'cursor', 'pointer');
  }

  @HostListener('mouseleave')
  onMouseLeave(): void {
    this.renderer.removeStyle(this.el.nativeElement, 'background-color');
  }

  @HostListener('click')
  onClick(): void {
    this.renderer.setStyle(this.el.nativeElement, 'outline', '2px solid #333');
    setTimeout(() => this.renderer.removeStyle(this.el.nativeElement, 'outline'), 500);
  }
}

// Usage in a template (would be in another component):
// <p appHighlight>Hover me — default yellow highlight</p>
// <p appHighlight="lightblue">Hover me — blue highlight</p>
// <td appHighlight="lightsalmon" *ngFor="let c of courses">{{ c.title }}</td>

// ─────────────────────────────────────────────────────────────────────────────
// Demo component showing the custom directive in action
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-directive-usage-demo',
  template: `
    <h2>Custom Directive — appHighlight</h2>
    <p appHighlight>Default yellow highlight on hover</p>
    <p appHighlight="lightblue">Blue highlight on hover</p>
    <p appHighlight="lightcoral">Coral highlight on hover</p>

    <table border="1">
      <tr *ngFor="let course of courses">
        <td appHighlight="lightyellow">{{ course.title }}</td>
        <td appHighlight="lightgreen">{{ course.level }}</td>
      </tr>
    </table>
  `
})
export class DirectiveUsageDemoComponent {
  courses = [
    { title: 'Angular Basics', level: 'Beginner' },
    { title: 'Angular DI',     level: 'Intermediate' },
    { title: 'Angular Signals', level: 'Advanced' },
  ];
}
