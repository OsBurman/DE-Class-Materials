// =============================================================================
// lifecycle-and-refs.component.ts — Template Reference Variables & Lifecycle
// =============================================================================
// This file covers two related concepts that deal with TIMING and ACCESS:
//
//  PART A — Template Reference Variables  (#varName)
//    • Declare a reference to any DOM element or component in a template
//    • Access it from other parts of the template (no TS code needed)
//    • Access it from the class via @ViewChild()
//
//  PART B — Angular Lifecycle Hooks
//    • ngOnChanges   — @Input() value changed
//    • ngOnInit      — component initialized (safe to use @Input() values)
//    • ngDoCheck     — custom change detection
//    • ngAfterContentInit   — ng-content fully projected
//    • ngAfterContentChecked
//    • ngAfterViewInit      — component's view (and children) fully rendered
//    • ngAfterViewChecked
//    • ngOnDestroy   — component about to be removed (cleanup!)
// =============================================================================

import {
  Component,
  Input,
  OnChanges,
  OnInit,
  DoCheck,
  AfterContentInit,
  AfterContentChecked,
  AfterViewInit,
  AfterViewChecked,
  OnDestroy,
  SimpleChanges,
  ViewChild,
  ElementRef
} from '@angular/core';

// ── PART A: Template Reference Variables Demo ─────────────────────────────────
@Component({
  selector: 'app-ref-demo',
  template: `
    <!-- Template reference variable: #searchInput -->
    <!-- The # prefix declares a reference. Angular assigns it the DOM element. -->
    <input #searchInput type="text" placeholder="Type to search…">

    <!-- Access the reference variable elsewhere in the same template -->
    <!-- No component class property needed — purely template-level! -->
    <button (click)="onSearch(searchInput.value)">Search</button>
    <button (click)="searchInput.focus()">Focus Input</button>
    <button (click)="searchInput.value = ''">Clear</button>

    <p>Current value: {{ searchInput.value }}</p>

    <hr>

    <!-- Referencing a child COMPONENT (not just a DOM element) -->
    <!-- When used on a component selector, the ref gives the component INSTANCE -->
    <!-- (Covered in @ViewChild below for class-level access) -->
    <p>Search result: {{ searchResult }}</p>
  `
})
export class RefDemoComponent {
  searchResult = '';

  onSearch(value: string): void {
    this.searchResult = value ? `Searching for: "${value}"` : 'Empty search';
  }
}


// ── PART B: Full Lifecycle Demo Component ─────────────────────────────────────
@Component({
  selector: 'app-lifecycle-demo',
  templateUrl: './lifecycle-demo.component.html',
  styleUrls: ['./lifecycle-and-refs.component.css']
})
export class LifecycleDemoComponent
  implements
    OnChanges,
    OnInit,
    DoCheck,
    AfterContentInit,
    AfterContentChecked,
    AfterViewInit,
    AfterViewChecked,
    OnDestroy {

  // ── @Input() — changes trigger ngOnChanges ──────────────────────────────
  @Input() courseId: number = 0;
  @Input() courseName: string = '';

  // ── @ViewChild — gives class-level access to a template reference ────────
  // Angular sets this property AFTER the view is initialized (ngAfterViewInit).
  // Attempting to use it in ngOnInit WILL return undefined.
  @ViewChild('titleInput') titleInputRef!: ElementRef<HTMLInputElement>;

  // ── Component state ─────────────────────────────────────────────────────
  lifecycleLog: string[] = [];
  checkCount = 0;
  viewTitle = '';

  // ── LIFECYCLE: ngOnChanges ───────────────────────────────────────────────
  // Called BEFORE ngOnInit and whenever any @Input() property changes.
  // Receives a SimpleChanges object describing what changed.
  //
  // WHEN TO USE: React to specific @Input() value changes and update
  //              derived data (e.g., recalculate based on new prop values).
  ngOnChanges(changes: SimpleChanges): void {
    const log: string[] = [];

    if (changes['courseId']) {
      const { previousValue, currentValue, firstChange } = changes['courseId'];
      log.push(
        `ngOnChanges — courseId: ${previousValue} → ${currentValue}` +
        (firstChange ? ' (first change)' : '')
      );
    }

    if (changes['courseName']) {
      log.push(`ngOnChanges — courseName changed to: "${changes['courseName'].currentValue}"`);
    }

    this.lifecycleLog.unshift(...log);
  }

  // ── LIFECYCLE: ngOnInit ──────────────────────────────────────────────────
  // Called ONCE, after the first ngOnChanges (if any @Input()s exist).
  // @Input() properties ARE available here (unlike the constructor).
  //
  // WHEN TO USE: Initialization logic that needs @Input() values,
  //              HTTP calls to load data, setting up subscriptions.
  //
  // WHY NOT THE CONSTRUCTOR? Constructors should be lightweight — only DI.
  // Angular hasn't set @Input() values yet when the constructor runs.
  ngOnInit(): void {
    this.lifecycleLog.unshift(`ngOnInit — courseId=${this.courseId}, name="${this.courseName}"`);
    // ← This is where you would call a service: this.courseService.load(this.courseId)
  }

  // ── LIFECYCLE: ngDoCheck ─────────────────────────────────────────────────
  // Called on EVERY change-detection cycle (very frequently!).
  // Use sparingly — only for detecting changes Angular cannot detect itself
  // (e.g., changes inside a mutable object reference).
  //
  // ⚠️ PERFORMANCE WARNING: Heavy logic here degrades performance significantly.
  ngDoCheck(): void {
    this.checkCount++;
    // Log only first 5 times to avoid flooding the list
    if (this.checkCount <= 5) {
      this.lifecycleLog.unshift(`ngDoCheck — cycle #${this.checkCount}`);
    }
  }

  // ── LIFECYCLE: ngAfterContentInit ────────────────────────────────────────
  // Called ONCE after Angular projects external content into the component
  // (i.e., content inside <ng-content>).
  //
  // WHEN TO USE: Read @ContentChild / @ContentChildren values.
  ngAfterContentInit(): void {
    this.lifecycleLog.unshift('ngAfterContentInit — projected content is ready');
  }

  // ── LIFECYCLE: ngAfterContentChecked ────────────────────────────────────
  // Called after every check of projected content.
  // Called very frequently — keep it lightweight.
  ngAfterContentChecked(): void {
    // Intentionally not logging — fires too frequently for demo clarity
  }

  // ── LIFECYCLE: ngAfterViewInit ───────────────────────────────────────────
  // Called ONCE after the component's view (and all child views) are rendered.
  // @ViewChild references ARE available here.
  //
  // ⚠️ Do NOT modify component properties here if you have OnPush change
  //    detection — use setTimeout(() => { ... }) to avoid ExpressionChangedError.
  ngAfterViewInit(): void {
    this.lifecycleLog.unshift('ngAfterViewInit — view is fully rendered');

    // Safe to use @ViewChild here:
    if (this.titleInputRef) {
      this.titleInputRef.nativeElement.focus(); // Auto-focus the input
      this.lifecycleLog.unshift('ngAfterViewInit — auto-focused title input via @ViewChild');
    }
  }

  // ── LIFECYCLE: ngAfterViewChecked ────────────────────────────────────────
  // Called after every check of the component's view and child views.
  // Like ngAfterContentChecked — fires very frequently.
  ngAfterViewChecked(): void {
    // Intentionally not logging — fires too frequently for demo clarity
  }

  // ── LIFECYCLE: ngOnDestroy ───────────────────────────────────────────────
  // Called ONCE just before Angular destroys the component.
  // THIS IS YOUR CLEANUP HOOK — always unsubscribe, clear timers, close
  // connections here to prevent memory leaks.
  //
  // COMMON CLEANUP TASKS:
  //   • Unsubscribe from Observables / RxJS subscriptions
  //   • Clear setInterval / setTimeout
  //   • Remove event listeners added with addEventListener
  //   • Close WebSocket connections
  ngOnDestroy(): void {
    this.lifecycleLog.unshift('ngOnDestroy — component is being destroyed');
    console.log('LifecycleDemoComponent destroyed — releasing resources');
    // Example cleanup:
    // this.courseSubscription.unsubscribe();
    // clearInterval(this.pollingTimer);
  }

  // ── Class method used in template ────────────────────────────────────────
  focusTitleInput(): void {
    this.titleInputRef?.nativeElement.focus();
  }

  clearLog(): void {
    this.lifecycleLog = [];
    this.checkCount = 0;
  }
}
