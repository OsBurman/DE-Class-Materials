// =============================================================================
// app.module.ts — The Root NgModule
// =============================================================================
// Every Angular application has at least one NgModule — the root module.
// It is the entry point that Angular's bootstrapper reads to know:
//   • Which components/directives/pipes exist (declarations)
//   • Which other modules to import (imports)
//   • Which services to register at root level (providers)
//   • Which component to bootstrap first (bootstrap)
//
// Think of @NgModule as the "manifest" of a feature area of your app.
// =============================================================================

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';  // Required for any browser app
import { FormsModule } from '@angular/forms';               // Enables [(ngModel)] two-way binding

import { AppComponent }       from './app.component';
import { CourseCardComponent } from './course-card.component';

@NgModule({
  // ── declarations ──────────────────────────────────────────────────────────
  // Every component, directive, and pipe you CREATE must be declared here
  // (or in a feature module that is imported here).
  // Rule: a class can only be declared in ONE module.
  declarations: [
    AppComponent,         // Root component — always declared here
    CourseCardComponent,  // Our custom component — declared so Angular knows about it
  ],

  // ── imports ───────────────────────────────────────────────────────────────
  // Other modules whose exported declarations you want to use in THIS module.
  // BrowserModule: provides *ngFor, *ngIf, AsyncPipe, and browser-specific APIs.
  //   → Use BrowserModule ONLY in the root module.
  //   → Feature modules should import CommonModule instead.
  // FormsModule: enables [(ngModel)] two-way binding on <input>, <select>, etc.
  imports: [
    BrowserModule,
    FormsModule,
  ],

  // ── providers ─────────────────────────────────────────────────────────────
  // Services registered here are available application-wide (singleton).
  // Modern Angular uses providedIn: 'root' inside @Injectable() instead,
  // so you rarely add services here directly anymore.
  providers: [],

  // ── bootstrap ─────────────────────────────────────────────────────────────
  // The component Angular should render first into index.html's <app-root>.
  // There is almost always exactly ONE component listed here.
  bootstrap: [AppComponent],
})
export class AppModule {}
