// =============================================================================
// 03-component-encapsulation.ts — Component Encapsulation (ViewEncapsulation)
// =============================================================================
// By default, Angular scopes each component's styles so they don't leak into
// other components. This is called View Encapsulation.
//
// Angular has three modes:
//   ViewEncapsulation.Emulated  — default; Angular adds unique attributes to
//                                  scope styles artificially (no real Shadow DOM)
//   ViewEncapsulation.ShadowDom — uses native browser Shadow DOM for true isolation
//   ViewEncapsulation.None      — NO scoping; styles become global
//
// SECTIONS:
//  1. Emulated encapsulation (default) — how it works under the hood
//  2. ShadowDom encapsulation
//  3. None encapsulation — and why it's dangerous
//  4. :host, :host-context, ::ng-deep selectors
//  5. Practical example — component library card styling
// =============================================================================

import { Component, ViewEncapsulation } from '@angular/core';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Emulated Encapsulation (the default)
// ─────────────────────────────────────────────────────────────────────────────
// Angular adds a unique attribute like _ngcontent-abc-c1 to every element
// in this component's template. The styles are compiled to:
//   p[_ngcontent-abc-c1] { color: navy; }
// So only paragraphs INSIDE THIS component are styled navy.

@Component({
  selector: 'app-emulated-encapsulation',
  template: `
    <div class="card">
      <h3>Emulated Encapsulation (default)</h3>
      <p>This paragraph is navy — scoped to this component only.</p>
      <p>The styles won't affect <code>p</code> tags in other components.</p>
    </div>
  `,
  styles: [`
    /* These styles are SCOPED to this component — they won't leak out */
    .card {
      border: 2px solid navy;
      padding: 16px;
      border-radius: 8px;
    }
    p {
      color: navy;
      font-weight: 500;
    }
    h3 {
      margin-top: 0;
      text-transform: uppercase;
      font-size: 0.9rem;
      color: #666;
    }
  `],
  encapsulation: ViewEncapsulation.Emulated  // this is the default — same as omitting it
})
export class EmulatedEncapsulationComponent {}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — ShadowDom Encapsulation
// ─────────────────────────────────────────────────────────────────────────────
// Uses the browser's native Shadow DOM API.
// TRUE isolation — global styles from the parent page cannot penetrate.
// Inspect the element in DevTools — you'll see a #shadow-root node.
// NOTE: slightly less browser-compatible and harder to override styles from outside.

@Component({
  selector: 'app-shadow-encapsulation',
  template: `
    <div class="card">
      <h3>ShadowDom Encapsulation</h3>
      <p>This uses real browser Shadow DOM.</p>
      <p>External CSS resets and global styles cannot reach inside.</p>
      <p>Check DevTools — you'll see a #shadow-root boundary.</p>
    </div>
  `,
  styles: [`
    .card { border: 2px solid purple; padding: 16px; border-radius: 8px; }
    p     { color: purple; }
    h3    { color: #444; font-size: 0.85rem; text-transform: uppercase; }
  `],
  encapsulation: ViewEncapsulation.ShadowDom  // real Shadow DOM
})
export class ShadowDomEncapsulationComponent {}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — None Encapsulation
// ─────────────────────────────────────────────────────────────────────────────
// Styles defined here are NOT scoped — they become GLOBAL styles.
// Any .card class or p selector in the entire application will be affected.
//
// ⚠️ Use only intentionally — e.g. third-party component overrides, or
// base-level typography/reset styles that you WANT to be global.

@Component({
  selector: 'app-none-encapsulation',
  template: `
    <div class="global-card">
      <h3>ViewEncapsulation.None</h3>
      <p>⚠️ These styles are injected GLOBALLY.</p>
      <p>Any .global-card in the app will have this red border!</p>
    </div>
  `,
  styles: [`
    /* WARNING: these styles apply to the ENTIRE document */
    .global-card {
      border: 2px solid red;
      padding: 16px;
      border-radius: 8px;
    }
    /* If you used a generic selector like 'p' here, it would affect ALL <p> tags
       everywhere in the application */
  `],
  encapsulation: ViewEncapsulation.None  // styles become global
})
export class NoneEncapsulationComponent {}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Special Selectors: :host, :host-context, ::ng-deep
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-special-selectors',
  template: `
    <div class="inner">
      <h3>Special CSS Selectors</h3>
      <p class="content">Content text</p>
      <button class="btn">Click me</button>
    </div>
  `,
  styles: [`
    /*
     * :host — styles the HOST ELEMENT itself (the <app-special-selectors> tag)
     * Useful for display, margin, or flex properties on the component wrapper.
     */
    :host {
      display: block;           /* components are inline by default! */
      margin-bottom: 16px;
      border: 1px dashed #999;
    }

    /* :host with a condition — applies when the host element has class 'active' */
    :host(.active) {
      border-color: green;
    }

    /*
     * :host-context — applies when an ANCESTOR has the given selector.
     * Common use: dark mode theming, print styles, RTL layouts.
     */
    :host-context(.dark-theme) .inner {
      background: #333;
      color: white;
    }

    /*
     * ::ng-deep — PIERCE through child component encapsulation.
     * Use to style content inside child components you don't own.
     *
     * ⚠️ Deprecated but still widely used. Always combine with :host
     *    to limit the scope — otherwise it becomes global.
     */
    :host ::ng-deep .mat-card-title {
      font-size: 24px;   /* override Angular Material styles */
    }

    /* Without :host, ::ng-deep would affect all .mat-card-title in the app */
    /* ::ng-deep .mat-card-title { }  ← DON'T do this — global leak */

    /* Regular component-scoped styles */
    .inner  { padding: 12px; }
    .btn    { background: #007bff; color: white; border: none; padding: 8px 16px; }
  `]
})
export class SpecialSelectorsComponent {}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Practical Example: Reusable Card Component
// ─────────────────────────────────────────────────────────────────────────────
// A reusable component library card that uses :host for consistent spacing
// and protects its styles from the consuming application.

@Component({
  selector: 'app-course-info-card',
  template: `
    <div class="card" [class.card--featured]="featured">
      <div class="card__header">
        <span class="card__badge">{{ level }}</span>
        <h3 class="card__title">{{ title }}</h3>
      </div>
      <div class="card__body">
        <ng-content></ng-content>   <!-- slot for projected content -->
      </div>
      <div class="card__footer">
        <button class="btn" (click)="action.emit()">
          {{ buttonLabel }}
        </button>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      width: 100%;
    }
    .card {
      background: white;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      overflow: hidden;
      transition: box-shadow 0.2s;
    }
    .card:hover       { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
    .card--featured   { border-color: #007bff; border-width: 2px; }
    .card__header     { background: #f5f5f5; padding: 12px 16px; }
    .card__badge      { font-size: 0.75rem; background: #007bff; color: white; padding: 2px 8px; border-radius: 12px; }
    .card__title      { margin: 8px 0 0; font-size: 1.1rem; }
    .card__body       { padding: 12px 16px; color: #555; font-size: 0.9rem; }
    .card__footer     { padding: 12px 16px; border-top: 1px solid #eee; }
    .btn              { background: #007bff; color: white; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
    .btn:hover        { background: #0056b3; }
  `],
  encapsulation: ViewEncapsulation.Emulated  // explicit (same as omitting)
})
export class CourseInfoCardComponent {
  title       = 'Angular Services & DI';
  level       = 'Intermediate';
  buttonLabel = 'Enroll Now';
  featured    = false;
  // @Output not importing here for brevity — see component-communication.ts
  action      = { emit: () => console.log('Action clicked') };
}

// Parent uses the card — its own styles don't affect the card internals
@Component({
  selector: 'app-encapsulation-demo',
  template: `
    <h2>Component Encapsulation Demo</h2>

    <app-emulated-encapsulation></app-emulated-encapsulation>
    <app-shadow-encapsulation></app-shadow-encapsulation>
    <app-none-encapsulation></app-none-encapsulation>
    <app-special-selectors></app-special-selectors>

    <h3>Reusable Card (Emulated — default)</h3>
    <app-course-info-card></app-course-info-card>

    <!-- Content projection via ng-content -->
    <app-course-info-card>
      <p>Learn how Angular's DI system works.</p>
      <ul>
        <li>Services and providers</li>
        <li>Injector hierarchy</li>
        <li>BehaviorSubject pattern</li>
      </ul>
    </app-course-info-card>
  `
})
export class EncapsulationDemoComponent {}

/*
─────────────────────────────────────────────────────────────────────────────
ENCAPSULATION QUICK REFERENCE
─────────────────────────────────────────────────────────────────────────────
Mode         How it works                              Use when
─────────────────────────────────────────────────────────────────────────────
Emulated     Angular adds unique attrs to scope styles  Default — almost always
ShadowDom    Real browser Shadow DOM                    Component library / widgets
None         Styles become global                       Global resets, 3rd-party overrides
─────────────────────────────────────────────────────────────────────────────
Selector     What it targets
─────────────────────────────────────────────────────────────────────────────
:host          The component's own host element
:host(.cls)    Host when it has class .cls
:host-context  Applied when an ancestor matches
::ng-deep      Pierce child component styles (use sparingly, always scope with :host)
─────────────────────────────────────────────────────────────────────────────
*/
