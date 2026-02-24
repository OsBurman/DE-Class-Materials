// =============================================================================
// template-syntax.component.ts — Angular Template Syntax Deep-Dive
// =============================================================================
// Angular templates are HTML files with Angular-specific extensions.
// This file (and its paired .html) covers every major template feature:
//
//  SECTION 1 — Interpolation and Template Expressions
//  SECTION 2 — Template Statements (event handlers)
//  SECTION 3 — Built-in Pipes
//  SECTION 4 — Safe Navigation Operator ?.
//  SECTION 5 — Non-null Assertion Operator !. in templates
//  SECTION 6 — ng-template: named template fragments
//  SECTION 7 — ng-container: invisible wrapper element
//  SECTION 8 — ng-content: content projection (slot API)
// =============================================================================

import { Component, Input } from '@angular/core';

// ── Interfaces ────────────────────────────────────────────────────────────────
export interface User {
  name: string;
  email: string;
  address?: {         // Optional nested object — perfect for safe nav demo
    city: string;
    country: string;
  };
  memberSince: Date;
  role: 'admin' | 'student' | 'instructor';
}

export interface Product {
  name: string;
  price: number;      // raw number — Pipes will format it as currency
  discount?: number;  // optional percentage
  launchDate: Date;
}

// ── Component ─────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-template-syntax',
  templateUrl: './template-syntax.component.html',
  styleUrls: ['./template-syntax.component.css']
})
export class TemplateSyntaxComponent {

  // ── SECTION 1: Interpolation ──────────────────────────────────────────────
  // {{ expression }} evaluates the expression and converts result to a string.
  // Expressions must be simple — NO: assignments, new, ++/--, | (bitwise)
  appName = 'Angular Course Platform';
  currentYear = new Date().getFullYear(); // JS expressions are fine
  pi = 3.14159;
  items = ['Angular', 'TypeScript', 'RxJS', 'NgRx'];

  // ── SECTION 2: Template Statements ───────────────────────────────────────
  // Template statements appear inside (event)="statement" bindings.
  // Unlike expressions, statements CAN have side effects (assignments, calls).
  counter = 0;
  lastAction = '';

  increment(): void {
    this.counter++;
    this.lastAction = 'incremented';
  }

  decrement(): void {
    this.counter--;
    this.lastAction = 'decremented';
  }

  reset(): void {
    this.counter = 0;
    this.lastAction = 'reset';
  }

  // ── SECTION 3: Built-in Angular Pipes ────────────────────────────────────
  // Pipes transform data in the template: {{ value | pipeName:arg1:arg2 }}
  // They do NOT mutate the original data — they return a new formatted value.
  rawPrice = 49.99;
  bigNumber = 1234567.89;
  today = new Date();
  greeting = 'hello angular world';
  percentValue = 0.85;

  product: Product = {
    name: 'Angular Masterclass',
    price: 299,
    discount: 20,
    launchDate: new Date('2024-03-15')
  };

  // ── SECTION 4: Safe Navigation Operator ?.  ───────────────────────────────
  // Prevents "Cannot read property of null/undefined" errors in templates.
  // If any step in the chain is null/undefined, the whole expression returns
  // undefined (rendered as empty string) instead of throwing.
  userWithAddress: User = {
    name: 'Alice',
    email: 'alice@example.com',
    address: { city: 'New York', country: 'USA' },
    memberSince: new Date('2022-01-10'),
    role: 'instructor'
  };

  userWithoutAddress: User = {
    name: 'Bob',
    email: 'bob@example.com',
    // address is omitted — accessing .address.city would normally throw
    memberSince: new Date('2023-06-20'),
    role: 'student'
  };

  // ── SECTION 5: Non-null Assertion Operator !. ─────────────────────────────
  // Tells the TypeScript compiler "I know this is not null — trust me."
  // Used in templates when you are CERTAIN a value exists, but TS doesn't know.
  // Overuse is a code smell — prefer safe navigation (?) when in doubt.
  confirmedUser: User | null = {
    name: 'Carol',
    email: 'carol@example.com',
    memberSince: new Date(),
    role: 'admin'
  };
}
