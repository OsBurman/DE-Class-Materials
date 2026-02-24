// =============================================================================
// 03-pipes.ts — Built-in Pipes (intro to custom pipes in Part 2)
// =============================================================================
// A pipe transforms a value for display in the template.
// Syntax: {{ value | pipeName:arg1:arg2 }}
// Pipes can be chained: {{ value | pipe1 | pipe2 }}
//
// Angular ships with pipes for: strings, numbers, dates, currency,
// JSON serialization, arrays, percentages, and async values.
//
// SECTIONS:
//  1. String pipes — uppercase, lowercase, titlecase, slice
//  2. Number and currency pipes
//  3. Date pipe
//  4. Percent pipe
//  5. JSON and KeyValue pipes
//  6. Slice pipe on arrays
//  7. Async pipe — consuming Observables and Promises in templates
//  8. Chaining pipes and pipe precedence
// =============================================================================

import { Component } from '@angular/core';
import { Observable, interval } from 'rxjs';
import { map, take } from 'rxjs/operators';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1–8 — All built-in pipes in one component for easy comparison
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-pipes-demo',
  template: `
    <h2>Built-In Pipes Demo</h2>

    <!-- ── SECTION 1: String Pipes ────────────────────────────────────────── -->
    <h3>String Pipes</h3>
    <p>Original:   {{ courseName }}</p>
    <p>uppercase:  {{ courseName | uppercase }}</p>
    <p>lowercase:  {{ courseName | lowercase }}</p>
    <p>titlecase:  {{ 'angular services and dependency injection' | titlecase }}</p>

    <!-- slice:start:end — same semantics as JS Array.slice / String.slice -->
    <p>slice 0..7:  {{ courseName | slice:0:7 }}</p>
    <p>slice -2:    {{ courseName | slice:-2 }}</p>    <!-- last 2 chars -->

    <!-- ── SECTION 2: Number and Currency Pipes ───────────────────────────── -->
    <h3>Number & Currency Pipes</h3>
    <!-- number:'minIntegerDigits.minFractionDigits-maxFractionDigits' -->
    <p>Raw number:          {{ price }}</p>
    <p>number '1.2-2':      {{ price | number:'1.2-2' }}</p>
    <p>number '4.0-0':      {{ price | number:'4.0-0' }}</p>  <!-- zero-padded -->

    <!-- currency:code:display:digitsInfo -->
    <p>currency USD:        {{ price | currency }}</p>
    <p>currency USD symbol: {{ price | currency:'USD':'symbol':'1.2-2' }}</p>
    <p>currency GBP:        {{ price | currency:'GBP':'symbol-narrow' }}</p>
    <p>currency EUR narrow: {{ price | currency:'EUR':'symbol' }}</p>

    <!-- ── SECTION 3: Date Pipe ───────────────────────────────────────────── -->
    <h3>Date Pipe</h3>
    <!-- date:'format':'timezone':'locale' -->
    <p>Raw date:       {{ courseDate }}</p>
    <p>default date:   {{ courseDate | date }}</p>
    <p>shortDate:      {{ courseDate | date:'shortDate' }}</p>
    <p>longDate:       {{ courseDate | date:'longDate' }}</p>
    <p>mediumDate:     {{ courseDate | date:'mediumDate' }}</p>
    <p>fullDate:       {{ courseDate | date:'fullDate' }}</p>
    <p>custom format:  {{ courseDate | date:"EEEE, MMMM d 'at' h:mm a" }}</p>
    <p>time only:      {{ courseDate | date:'h:mm a' }}</p>
    <p>ISO 8601:       {{ courseDate | date:'yyyy-MM-dd' }}</p>

    <!-- ── SECTION 4: Percent Pipe ────────────────────────────────────────── -->
    <h3>Percent Pipe</h3>
    <p>Completion (raw):  {{ completionRate }}</p>
    <p>percent default:   {{ completionRate | percent }}</p>         <!-- 0.76 → 76% -->
    <p>percent 1.1-2:     {{ completionRate | percent:'1.1-2' }}</p>  <!-- 76.0% -->

    <!-- ── SECTION 5: JSON and KeyValue Pipes ─────────────────────────────── -->
    <h3>JSON & KeyValue Pipes</h3>
    <!-- json — serializes any object for debugging in the template -->
    <pre>{{ courseConfig | json }}</pre>

    <!-- keyvalue — iterate over an object's entries as { key, value } pairs -->
    <ul>
      <li *ngFor="let entry of courseConfig | keyvalue">
        <strong>{{ entry.key }}</strong>: {{ entry.value }}
      </li>
    </ul>

    <!-- ── SECTION 6: Slice Pipe on Arrays ────────────────────────────────── -->
    <h3>Slice Pipe on Arrays</h3>
    <p>All courses: {{ allCourses.join(', ') }}</p>
    <!-- slice applied to an array — useful for "show N items" UI -->
    <p>First 3: {{ allCourses | slice:0:3 | json }}</p>
    <p>Last 2:  {{ allCourses | slice:-2 | json }}</p>

    <ul>
      <!-- Combine with *ngFor — show only the first 3 -->
      <li *ngFor="let c of allCourses | slice:0:3">{{ c }}</li>
    </ul>

    <!-- ── SECTION 7: Async Pipe ──────────────────────────────────────────── -->
    <h3>Async Pipe</h3>
    <!--
      The async pipe:
        • Subscribes to an Observable or Promise automatically
        • Displays the emitted value
        • UNSUBSCRIBES on component destroy (prevents memory leaks!)
        • Triggers change detection on each emission
    -->
    <p>Counter (Observable): {{ counter$ | async }}</p>
    <p>Greeting (Promise):   {{ greeting$ | async }}</p>

    <!-- async with *ngIf — safe to use when value might be null initially -->
    <div *ngIf="userData$ | async as user">
      <p>Logged in as: {{ user.name }} ({{ user.role }})</p>
    </div>

    <!-- ── SECTION 8: Chaining Pipes ──────────────────────────────────────── -->
    <h3>Chaining Pipes</h3>
    <!-- Pipes are applied left to right -->
    <p>{{ courseName | uppercase | slice:0:7 }}</p>
    <!-- 1. uppercase: "ANGULAR SERVICES & DI"  2. slice:0:7: "ANGULAR" -->

    <p>{{ courseDate | date:'mediumDate' | uppercase }}</p>
    <!-- 1. date → "Feb 24, 2026"  2. uppercase → "FEB 24, 2026" -->

    <!-- ⚠️ WATCH OUT: pipe has LOWER precedence than the ternary operator -->
    <!-- {{ condition ? 'Yes' : 'No' | uppercase }}  ← only 'No' gets uppercased! -->
    <!-- Use parentheses: {{ (condition ? 'Yes' : 'No') | uppercase }} -->
    <p>{{ (isActive ? 'active' : 'inactive') | uppercase }}</p>
  `
})
export class PipesDemoComponent {
  courseName    = 'Angular Services & DI';
  price         = 1299.5;
  courseDate    = new Date('2026-02-24T09:00:00');
  completionRate = 0.762;
  isActive      = true;

  courseConfig = {
    language: 'TypeScript',
    framework: 'Angular',
    version: 17,
    hasLabs: true
  };

  allCourses = [
    'React Fundamentals', 'Angular DI', 'Spring Boot',
    'SQL', 'Docker', 'AWS'
  ];

  // Observable — emits 0,1,2,3,4 one per second then completes
  counter$: Observable<number> = interval(1000).pipe(take(5));

  // Promise — resolves after 1 second
  greeting$: Promise<string> = new Promise(resolve =>
    setTimeout(() => resolve('Hello from a Promise!'), 1000)
  );

  // Observable that emits an object (simulating a logged-in user)
  userData$: Observable<{ name: string; role: string }> = interval(2000).pipe(
    take(1),
    map(() => ({ name: 'Scott', role: 'Instructor' }))
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick reference card as a comment
// ─────────────────────────────────────────────────────────────────────────────
/*
PIPE QUICK REFERENCE
─────────────────────────────────────────────────────────────────
Pipe          Usage                               Example output
─────────────────────────────────────────────────────────────────
uppercase     {{ str | uppercase }}               HELLO
lowercase     {{ str | lowercase }}               hello
titlecase     {{ str | titlecase }}               Hello World
slice         {{ str | slice:0:5 }}               Hello
number        {{ n | number:'1.2-2' }}            1,299.50
currency      {{ n | currency:'USD' }}            $1,299.50
percent       {{ 0.76 | percent }}                76%
date          {{ d | date:'shortDate' }}          2/24/26
json          {{ obj | json }}                    {"key":"val"}
keyvalue      *ngFor + keyvalue                   key, value pairs
slice (arr)   {{ arr | slice:0:3 }}               first 3 elements
async         {{ obs$ | async }}                  unwrapped value
─────────────────────────────────────────────────────────────────
*/
