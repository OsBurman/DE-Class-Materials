// Day 16b Part 1 — Angular Architecture: Components, Decorators, Templates
// Run: npm install && npm start

import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

const CSS = `
  * { box-sizing: border-box; }
  :host { display: block; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; }
  .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; }
  .header { background: #dd0031; color: white; padding: 1.5rem 2rem; border-radius: 8px; margin-bottom: 2rem; }
  .header h1 { font-size: 1.6rem; margin-bottom: .3rem; }
  .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
  .card h2 { color: #dd0031; margin-bottom: 1rem; padding-bottom: .4rem; border-bottom: 2px solid #ff6d00; }
  .badge { display: inline-block; background: #dd0031; color: white; border-radius: 4px; padding: 2px 8px; font-size: .75rem; margin: 2px; }
  .code { background: #1e1e1e; color: #d4e157; padding: 1rem; border-radius: 6px; font-size: .82rem; white-space: pre; overflow: auto; margin: .6rem 0; }
  table { width: 100%; border-collapse: collapse; font-size: .9rem; }
  th { background: #dd0031; color: white; padding: .5rem; text-align: left; }
  td { padding: .45rem; border-bottom: 1px solid #f0f0f0; }
  .tree { font-family: monospace; font-size: .9rem; line-height: 2; color: #555; }
`;

// ── Child Components ──────────────────────────────────────────────────────────

@Component({
  selector: 'app-course-badge',
  standalone: true,
  template: `<span [style.background]="color" style="color:white;padding:3px 10px;border-radius:12px;font-size:.8rem;margin:3px;display:inline-block">{{label}}</span>`
})
export class CourseBadgeComponent {
  @Input() label = '';
  @Input() color = '#dd0031';
}

@Component({
  selector: 'app-info-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="background:#f9f9f9;border-left:4px solid #dd0031;padding:1rem;border-radius:4px;margin:.5rem 0">
      <strong style="color:#dd0031">{{ title }}</strong>
      <p style="margin:.4rem 0 0;color:#555;font-size:.9rem">{{ description }}</p>
    </div>`
})
export class InfoCardComponent {
  @Input() title = '';
  @Input() description = '';
}

// ── Root Component ────────────────────────────────────────────────────────────

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, CourseBadgeComponent, InfoCardComponent],
  styles: [CSS],
  template: `
<div class="page">

  <div class="header">
    <h1>🅰️ Day 16b Part 1 — Angular Architecture &amp; Components</h1>
    <p style="opacity:.85">Angular 17 · Standalone Components · TypeScript</p>
  </div>

  <!-- 1. What is Angular? -->
  <div class="card">
    <h2>1. What is Angular?</h2>
    <table>
      <tr><th>Concept</th><th>Description</th></tr>
      <tr *ngFor="let row of architecture">
        <td><strong>{{ row.concept }}</strong></td>
        <td style="color:#555">{{ row.desc }}</td>
      </tr>
    </table>
  </div>

  <!-- 2. @Component Decorator -->
  <div class="card">
    <h2>2. The @Component Decorator</h2>
    <p style="color:#555;margin-bottom:.8rem">Every Angular UI element is a <strong>Component</strong> — a TypeScript class decorated with <code>@Component</code>.</p>
    <div class="code">{{ componentExample }}</div>
    <div style="margin-top:1rem">
      <strong>Key decorator properties:</strong>
      <div style="display:flex;flex-wrap:wrap;gap:.3rem;margin-top:.5rem">
        <span *ngFor="let p of decoratorProps" class="badge">{{ p }}</span>
      </div>
    </div>
  </div>

  <!-- 3. Component Tree -->
  <div class="card">
    <h2>3. Component Tree</h2>
    <p style="color:#555;margin-bottom:.8rem">Angular apps are a tree of components. <code>AppComponent</code> is the root.</p>
    <div class="tree">
      AppComponent (root — bootstrapped in main.ts)<br/>
      ├── HeaderComponent<br/>
      ├── CourseListComponent<br/>
      │&nbsp;&nbsp; ├── CourseCardComponent (×n via *ngFor)<br/>
      │&nbsp;&nbsp; └── FilterBarComponent<br/>
      └── FooterComponent
    </div>
    <p style="color:#555;font-size:.85rem;margin-top:.8rem">Child components rendered below (CourseBadgeComponent):</p>
    <div style="margin-top:.5rem">
      <app-course-badge label="Java" color="#5c6bc0"></app-course-badge>
      <app-course-badge label="React" color="#0288d1"></app-course-badge>
      <app-course-badge label="Angular" color="#dd0031"></app-course-badge>
      <app-course-badge label="Spring Boot" color="#388e3c"></app-course-badge>
    </div>
  </div>

  <!-- 4. Template Syntax Preview -->
  <div class="card">
    <h2>4. Template Syntax Preview</h2>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem">
      <div>
        <strong>Interpolation {{ '{{}}'  }}</strong>
        <div class="code">  &lt;p&gt;Hello, {{ '{{' }} name {{ '}}' }}!&lt;/p&gt;</div>
        <p style="color:#555;font-size:.85rem">Renders: Hello, {{ studentName }}!</p>
      </div>
      <div>
        <strong>[property] binding</strong>
        <div class="code">  &lt;img [src]="logoUrl"&gt;</div>
        <p style="color:#555;font-size:.85rem">Data flows: class → template</p>
      </div>
      <div>
        <strong>(event) binding</strong>
        <div class="code">  &lt;button (click)="fn()"&gt;</div>
        <p style="color:#555;font-size:.85rem">Data flows: template → class</p>
      </div>
      <div>
        <strong>[(ngModel)] two-way</strong>
        <div class="code">  &lt;input [(ngModel)]="name"&gt;</div>
        <p style="color:#555;font-size:.85rem">Both directions (Day 16b Part 2)</p>
      </div>
    </div>
  </div>

  <!-- 5. Angular CLI -->
  <div class="card">
    <h2>5. Angular CLI Essentials</h2>
    <app-info-card *ngFor="let cmd of cliCommands" [title]="cmd.cmd" [description]="cmd.desc"></app-info-card>
  </div>

</div>
  `
})
export class AppComponent implements OnInit {
  studentName = 'Alice';

  architecture = [
    { concept: 'Component',  desc: 'A TypeScript class + HTML template + CSS. Building block of every Angular UI.' },
    { concept: 'Module',     desc: 'NgModule groups related components, directives, pipes, and services (legacy). Angular 17+ uses Standalone.' },
    { concept: 'Template',   desc: 'HTML with Angular-specific syntax: interpolation, binding, directives.' },
    { concept: 'Service',    desc: 'A class with @Injectable — handles business logic, API calls, shared state.' },
    { concept: 'Directive',  desc: 'Add behavior to DOM elements. *ngFor, *ngIf are built-in structural directives.' },
    { concept: 'Pipe',       desc: 'Transform displayed values: | date | currency | uppercase | async' },
    { concept: 'Router',     desc: 'Maps URL paths to components for client-side navigation.' },
  ];

  decoratorProps = ['selector', 'standalone', 'imports', 'template / templateUrl', 'styles / styleUrls'];

  componentExample = `@Component({
  selector: 'app-student-card',
  standalone: true,
  imports: [CommonModule],
  template: \`
    <div class="card">
      <h3>{{ student.name }}</h3>
    </div>
  \`,
})
export class StudentCardComponent {
  @Input() student!: { name: string };
}`;

  cliCommands = [
    { cmd: 'ng new my-app',           desc: 'Scaffold a new Angular project' },
    { cmd: 'ng serve',                desc: 'Start dev server at http://localhost:4200' },
    { cmd: 'ng generate component x', desc: 'Create a new component (alias: ng g c x)' },
    { cmd: 'ng generate service x',   desc: 'Create a new service (alias: ng g s x)' },
    { cmd: 'ng build',                desc: 'Build for production (outputs to dist/)' },
    { cmd: 'ng test',                 desc: 'Run unit tests with Karma + Jasmine' },
  ];

  ngOnInit() {
    console.log('AppComponent initialized — ngOnInit lifecycle hook fired');
  }
}
