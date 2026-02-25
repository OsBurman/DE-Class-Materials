// Day 17b Part 1 — @Input/@Output, Structural Directives, Built-in Pipes
// Run: npm install && npm start

import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe, UpperCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

// ── Child component: demonstrates @Input and @Output ─────────────────────────
@Component({
  selector: 'app-student-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="background:#f9f9f9;border:1px solid #eee;border-radius:8px;padding:1rem;margin:.4rem 0;display:flex;justify-content:space-between;align-items:center">
      <div>
        <strong>{{ student.name }}</strong>
        <span style="margin-left:.5rem;color:#888;font-size:.85rem">{{ student.email }}</span>
      </div>
      <div>
        <span [style.background]="student.grade >= 90 ? '#27ae60' : student.grade >= 70 ? '#e67e22' : '#e74c3c'"
              style="color:white;padding:2px 10px;border-radius:12px;font-size:.85rem">
          {{ student.grade }}
        </span>
        <button (click)="select.emit(student)"
                style="background:#dd0031;color:white;border:none;padding:.25rem .6rem;border-radius:4px;cursor:pointer;margin-left:.5rem">
          Select
        </button>
      </div>
    </div>`
})
export class StudentCardComponent {
  @Input() student!: { name: string; email: string; grade: number };   // Receives data from parent
  @Output() select = new EventEmitter<typeof this.student>();           // Sends event to parent
}

// ── Root Component ────────────────────────────────────────────────────────────
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, StudentCardComponent],
  styles: [`
    * { box-sizing: border-box; }
    .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; font-family: -apple-system, sans-serif; }
    .header { background: #dd0031; color: white; padding: 1.5rem 2rem; border-radius: 8px; margin-bottom: 2rem; }
    .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
    h2 { color: #dd0031; margin-bottom: 1rem; padding-bottom: .4rem; border-bottom: 2px solid #ff6d00; }
    .code { background: #1e1e1e; color: #d4e157; padding: 1rem; border-radius: 6px; font-size: .82rem; white-space: pre; overflow: auto; margin: .5rem 0; }
    table { width: 100%; border-collapse: collapse; font-size: .9rem; }
    th { background: #dd0031; color: white; padding: .5rem; text-align: left; }
    td { padding: .45rem; border-bottom: 1px solid #f0f0f0; }
  `],
  template: `
<div class="page">
  <div class="header">
    <h1>🅰️ Day 17b Part 1 — @Input/@Output, Directives &amp; Pipes</h1>
  </div>

  <!-- @Input / @Output -->
  <div class="card">
    <h2>1. @Input and @Output</h2>
    <div class="code">// Parent passes data DOWN via @Input
&lt;app-student-card [student]="alice"&gt;&lt;/app-student-card&gt;

// Child emits events UP via @Output + EventEmitter
&lt;app-student-card (select)="onSelect($event)"&gt;&lt;/app-student-card&gt;</div>
    <div style="margin-top:1rem">
      <app-student-card
        *ngFor="let s of students"
        [student]="s"
        (select)="selectedStudent = $event">
      </app-student-card>
      <div *ngIf="selectedStudent" style="background:#e8f5e9;padding:.8rem;border-radius:6px;margin-top:.5rem">
        ✅ Selected: <strong>{{ selectedStudent.name }}</strong> ({{ selectedStudent.grade }})
      </div>
    </div>
  </div>

  <!-- Structural Directives -->
  <div class="card">
    <h2>2. Structural Directives</h2>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem">
      <div>
        <strong>*ngIf with else template</strong>
        <div class="code">&lt;div *ngIf="show; else noRef"&gt;...&lt;/div&gt;
&lt;ng-template #noRef&gt;...&lt;/ng-template&gt;</div>
        <button (click)="showDetails = !showDetails"
                style="background:#dd0031;color:white;border:none;padding:.35rem .8rem;border-radius:4px;cursor:pointer;margin-top:.4rem">
          {{ showDetails ? 'Hide' : 'Show' }} Details
        </button>
        <div *ngIf="showDetails; else hiddenRef" style="margin-top:.5rem;color:green">📋 Details are visible!</div>
        <ng-template #hiddenRef><p style="color:#aaa;margin-top:.5rem">Details hidden</p></ng-template>
      </div>
      <div>
        <strong>*ngSwitch</strong>
        <div class="code">&lt;div [ngSwitch]="status"&gt;
  &lt;p *ngSwitchCase="'active'"&gt;Active&lt;/p&gt;
  &lt;p *ngSwitchDefault&gt;Other&lt;/p&gt;
&lt;/div&gt;</div>
        <select [(ngModel)]="selectedStatus" style="margin-top:.4rem;padding:.3rem;border:1px solid #ccc;border-radius:4px">
          <option *ngFor="let s of statusOptions" [value]="s">{{ s }}</option>
        </select>
        <div [ngSwitch]="selectedStatus" style="margin-top:.4rem">
          <p *ngSwitchCase="'active'" style="color:green">✅ Active</p>
          <p *ngSwitchCase="'inactive'" style="color:#e74c3c">⛔ Inactive</p>
          <p *ngSwitchCase="'pending'" style="color:#e67e22">⏳ Pending</p>
          <p *ngSwitchDefault style="color:#888">❓ Unknown</p>
        </div>
      </div>
    </div>
  </div>

  <!-- ngClass / ngStyle -->
  <div class="card">
    <h2>3. Attribute Directives: [ngClass] and [ngStyle]</h2>
    <div class="code">&lt;div [ngClass]="{ 'active': isActive, 'highlight': score > 90 }"&gt;&lt;/div&gt;
&lt;div [ngStyle]="{ 'color': scoreColor, 'font-weight': 'bold' }"&gt;&lt;/div&gt;</div>
    <div style="margin-top:.8rem;display:flex;gap:.5rem;flex-wrap:wrap">
      <div *ngFor="let s of students"
           [ngStyle]="{ 'background': s.grade >= 90 ? '#e8f5e9' : s.grade >= 70 ? '#fff3e0' : '#fce4ec',
                        'border': '1px solid ' + (s.grade >= 90 ? '#27ae60' : s.grade >= 70 ? '#ff6d00' : '#e74c3c') }"
           style="padding:.5rem 1rem;border-radius:6px;font-size:.85rem">
        {{ s.name }}: {{ s.grade }}
      </div>
    </div>
  </div>

  <!-- Built-in Pipes -->
  <div class="card">
    <h2>4. Built-in Pipes</h2>
    <table>
      <tr><th>Pipe</th><th>Usage</th><th>Output</th></tr>
      <tr><td><code>uppercase</code></td><td><code>{{ "'angular'" }} | uppercase</code></td><td>{{ 'angular' | uppercase }}</td></tr>
      <tr><td><code>lowercase</code></td><td><code>{{ "'ANGULAR'" }} | lowercase</code></td><td>{{ 'ANGULAR' | lowercase }}</td></tr>
      <tr><td><code>date</code></td><td><code>today | date:'mediumDate'</code></td><td>{{ today | date:'mediumDate' }}</td></tr>
      <tr><td><code>date</code></td><td><code>today | date:'shortTime'</code></td><td>{{ today | date:'shortTime' }}</td></tr>
      <tr><td><code>currency</code></td><td><code>1234.5 | currency</code></td><td>{{ 1234.5 | currency }}</td></tr>
      <tr><td><code>number</code></td><td><code>3.14159 | number:'1.2-2'</code></td><td>{{ 3.14159 | number:'1.2-2' }}</td></tr>
      <tr><td><code>percent</code></td><td><code>0.856 | percent:'1.1-1'</code></td><td>{{ 0.856 | percent:'1.1-1' }}</td></tr>
      <tr><td><code>json</code></td><td><code>obj | json</code></td><td style="font-size:.75rem">{{ sampleObj | json }}</td></tr>
      <tr><td><code>slice</code></td><td><code>students | slice:0:2</code></td><td>{{ (students | slice:0:2) | json }}</td></tr>
    </table>
  </div>
</div>
  `
})
export class AppComponent implements OnInit {
  students = [
    { name: 'Alice',  email: 'alice@academy.com',  grade: 92 },
    { name: 'Bob',    email: 'bob@academy.com',    grade: 74 },
    { name: 'Carol',  email: 'carol@academy.com',  grade: 88 },
    { name: 'David',  email: 'david@academy.com',  grade: 61 },
  ];
  selectedStudent: typeof this.students[0] | null = null;
  showDetails = true;
  selectedStatus = 'active';
  statusOptions = ['active', 'inactive', 'pending', 'suspended'];
  today = new Date();
  sampleObj = { id: 1, name: 'Angular' };

  // needed for [(ngModel)] in standalone: inject FormsModule via imports in app.config.ts
  ngOnInit() {}
}
