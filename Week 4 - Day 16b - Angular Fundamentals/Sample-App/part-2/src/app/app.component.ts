// Day 16b Part 2 — Data Binding (4 types), Lifecycle Hooks, NgModule, *ngIf/*ngFor
// Uses NgModule pattern to demonstrate the traditional Angular approach
// Run: npm install && npm start

import {
  Component, OnInit, OnChanges, OnDestroy, AfterViewInit,
  Input, SimpleChanges
} from '@angular/core';

// ─────────────────────────────────────────────────────────────────────────────
// Sub-component: demonstrates @Input + lifecycle hooks
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-lifecycle-demo',
  template: `
    <div style="background:#fff3e0;padding:1rem;border-radius:6px;border-left:4px solid #ff6d00;margin-top:.5rem">
      <strong>LifecycleDemoComponent</strong> — value: <strong>{{ value }}</strong>
      <ul style="margin:.5rem 0 0 1.2rem;font-size:.85rem;color:#555">
        <li *ngFor="let event of log">{{ event }}</li>
      </ul>
    </div>`
})
export class LifecycleDemoComponent implements OnChanges, OnInit, AfterViewInit, OnDestroy {
  @Input() value = '';
  log: string[] = [];

  ngOnChanges(changes: SimpleChanges) {
    if (changes['value']) {
      this.log.push(`ngOnChanges → value: "${changes['value'].currentValue}"`);
    }
  }
  ngOnInit()        { this.log.push('ngOnInit: component ready (runs once after first ngOnChanges)'); }
  ngAfterViewInit() { this.log.push('ngAfterViewInit: view + child views initialized'); }
  ngOnDestroy()     { console.log('ngOnDestroy: cleanup here (unsubscribe, clearInterval, etc.)'); }
}

// ─────────────────────────────────────────────────────────────────────────────
// Root Component
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-root',
  styles: [`
    * { box-sizing: border-box; }
    .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; font-family: -apple-system, sans-serif; }
    .header { background: #dd0031; color: white; padding: 1.5rem 2rem; border-radius: 8px; margin-bottom: 2rem; }
    .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
    h2 { color: #dd0031; margin-bottom: 1rem; padding-bottom: .4rem; border-bottom: 2px solid #ff6d00; }
    .code { background: #1e1e1e; color: #d4e157; padding: 1rem; border-radius: 6px; font-size: .82rem; white-space: pre; overflow: auto; margin: .5rem 0; }
    .btn { background: #dd0031; color: white; border: none; padding: .4rem .9rem; border-radius: 4px; cursor: pointer; margin: .2rem; }
    input { padding: .35rem .6rem; border: 1px solid #ccc; border-radius: 4px; margin: .2rem; }
  `],
  template: `
<div class="page">

  <div class="header">
    <h1>🅰️ Day 16b Part 2 — Data Binding, Lifecycle &amp; Directives</h1>
    <p style="opacity:.85">NgModule pattern · FormsModule · *ngIf · *ngFor</p>
  </div>

  <!-- 1. Four types of data binding -->
  <div class="card">
    <h2>1. The Four Types of Data Binding</h2>

    <strong>① Interpolation — {{ '{{' }} expression {{ '}}' }}</strong>
    <div class="code">  &lt;p&gt;Hello, {{ '{{' }} userName {{ '}}' }}!&lt;/p&gt;</div>
    <p style="margin:.3rem 0 .8rem;color:#555">Output: Hello, <strong>{{ userName }}</strong>!</p>

    <strong>② Property Binding — [property]="expression"</strong>
    <div class="code">  &lt;button [disabled]="isLoading"&gt;Submit&lt;/button&gt;</div>
    <div style="margin:.3rem 0 .8rem">
      <button [disabled]="isLoading" class="btn">{{ isLoading ? 'Loading…' : 'Submit' }}</button>
      <button class="btn" style="background:#888" (click)="isLoading = !isLoading">Toggle [disabled]</button>
    </div>

    <strong>③ Event Binding — (event)="handler()"</strong>
    <div class="code">  &lt;button (click)="counter = counter + 1"&gt;+&lt;/button&gt;</div>
    <div style="margin:.3rem 0 .8rem">
      <button class="btn" (click)="counter = counter - 1">−</button>
      <strong style="padding:0 .8rem;font-size:1.3rem">{{ counter }}</strong>
      <button class="btn" (click)="counter = counter + 1">+</button>
      <button class="btn" style="background:#888" (click)="counter = 0">Reset</button>
    </div>

    <strong>④ Two-Way Binding — [(ngModel)]="property"  <span style="font-weight:normal;color:#888">(requires FormsModule in AppModule)</span></strong>
    <div class="code">  &lt;input [(ngModel)]="twoWayName"&gt;</div>
    <div style="margin:.3rem 0">
      <input [(ngModel)]="twoWayName" placeholder="Type your name…" />
      <span style="color:#555;margin-left:.5rem">→ Hello, <strong>{{ twoWayName || '?' }}</strong>!</span>
    </div>
  </div>

  <!-- 2. Lifecycle hooks -->
  <div class="card">
    <h2>2. Component Lifecycle Hooks</h2>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:.6rem;margin-bottom:1rem">
      <div *ngFor="let hook of lifecycleHooks" style="padding:.5rem;background:#f9f9f9;border-radius:4px;border-left:3px solid #dd0031">
        <code style="color:#dd0031">{{ hook.name }}</code>
        <p style="font-size:.82rem;color:#555;margin-top:.2rem">{{ hook.desc }}</p>
      </div>
    </div>
    <strong>Live demo — change the input to trigger ngOnChanges:</strong>
    <div style="margin:.5rem 0">
      <input [value]="lifecycleValue" (input)="lifecycleValue = $any($event.target).value" placeholder="Type to trigger ngOnChanges…" />
    </div>
    <app-lifecycle-demo [value]="lifecycleValue"></app-lifecycle-demo>
  </div>

  <!-- 3. NgModule -->
  <div class="card">
    <h2>3. NgModule Structure</h2>
    <p style="color:#555;margin-bottom:.6rem">This app uses the classic NgModule pattern. Angular 17+ defaults to standalone but NgModule is still widely used.</p>
    <div class="code">{{ ngModuleExample }}</div>
  </div>

  <!-- 4. *ngIf and *ngFor -->
  <div class="card">
    <h2>4. Structural Directives: *ngIf and *ngFor</h2>

    <strong>*ngIf</strong>
    <div class="code">  &lt;div *ngIf="isLoggedIn"&gt;Welcome!&lt;/div&gt;
  &lt;div *ngIf="!isLoggedIn"&gt;Please log in&lt;/div&gt;</div>
    <div style="margin:.4rem 0 1rem">
      <button class="btn" (click)="isLoggedIn = !isLoggedIn">{{ isLoggedIn ? 'Log Out' : 'Log In' }}</button>
      <span style="margin-left:.8rem">
        <span *ngIf="isLoggedIn" style="color:green">✅ Welcome, {{ userName }}!</span>
        <span *ngIf="!isLoggedIn" style="color:#888">🔒 Please log in</span>
      </span>
    </div>

    <strong>*ngFor</strong>
    <div class="code">  &lt;li *ngFor="let s of students; let i = index"&gt;{{ '{{' }} i+1 {{ '}}' }}. {{ '{{' }} s.name {{ '}}' }}&lt;/li&gt;</div>
    <ul style="list-style:none;padding:0;margin:.4rem 0">
      <li *ngFor="let s of students; let i = index"
          style="padding:.35rem .6rem;background:#f9f9f9;border-radius:4px;margin:.2rem;display:flex;justify-content:space-between">
        <span>{{ i + 1 }}. {{ s.name }}</span>
        <span [style.color]="s.grade >= 90 ? 'green' : s.grade >= 70 ? '#e67e22' : '#e74c3c'">{{ s.grade }}</span>
      </li>
    </ul>
    <button class="btn" (click)="addStudent()">+ Add Student</button>
    <button class="btn" style="background:#888" (click)="removeStudent()">Remove Last</button>
  </div>

</div>
  `
})
export class AppComponent implements OnInit {
  userName = 'Alice';
  isLoading = false;
  counter = 0;
  twoWayName = '';
  lifecycleValue = 'Angular';
  isLoggedIn = true;

  students = [
    { id: 1, name: 'Alice',  grade: 92 },
    { id: 2, name: 'Bob',    grade: 78 },
    { id: 3, name: 'Carol',  grade: 85 },
  ];
  private nextId = 4;

  addStudent() {
    this.students = [...this.students, { id: this.nextId, name: `Student ${this.nextId}`, grade: Math.floor(60 + Math.random() * 40) }];
    this.nextId++;
  }
  removeStudent() { this.students = this.students.slice(0, -1); }

  lifecycleHooks = [
    { name: 'ngOnChanges',     desc: 'Called when @Input values change. Receives SimpleChanges object.' },
    { name: 'ngOnInit',        desc: 'After first render. Best place for setup and API calls.' },
    { name: 'ngDoCheck',       desc: 'Every change detection cycle.' },
    { name: 'ngAfterViewInit', desc: 'After view + child views are initialized. Access @ViewChild here.' },
    { name: 'ngOnDestroy',     desc: 'Before destruction. Unsubscribe to prevent memory leaks.' },
  ];

  ngModuleExample = `@NgModule({
  declarations: [AppComponent, LifecycleDemoComponent],
  imports: [
    BrowserModule,
    FormsModule,    // ← enables [(ngModel)]
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}`;

  ngOnInit() {
    console.log('AppComponent ngOnInit — component is ready');
  }
}
