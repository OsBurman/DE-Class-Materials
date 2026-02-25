// Day 20b Part 1 — Angular Signals: signal(), computed(), effect()
// Requires Angular 16+ (this project uses Angular 17)
// Run: npm install && npm start

import { Component, signal, computed, effect, OnInit, Signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  styles: [`
    * { box-sizing: border-box; }
    .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; font-family: -apple-system, sans-serif; }
    .header { background: #dd0031; color: white; padding: 1.5rem 2rem; border-radius: 8px; margin-bottom: 2rem; }
    .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
    h2 { color: #dd0031; margin-bottom: 1rem; padding-bottom: .4rem; border-bottom: 2px solid #ff6d00; }
    .code { background: #1e1e1e; color: #d4e157; padding: 1rem; border-radius: 6px; font-size: .82rem; white-space: pre; overflow: auto; margin: .5rem 0; }
    .btn { background: #dd0031; color: white; border: none; padding: .4rem .9rem; border-radius: 4px; cursor: pointer; margin: .2rem; }
    input { padding: .35rem .6rem; border: 1px solid #ccc; border-radius: 4px; }
    table { width: 100%; border-collapse: collapse; font-size: .9rem; }
    th { background: #dd0031; color: white; padding: .5rem; text-align: left; }
    td { padding: .45rem; border-bottom: 1px solid #f0f0f0; }
  `],
  template: `
<div class="page">
  <div class="header">
    <h1>🅰️ Day 20b Part 1 — Angular Signals</h1>
    <p style="opacity:.85">signal() · computed() · effect() — Angular 17</p>
  </div>

  <!-- 1. Basic signal() -->
  <div class="card">
    <h2>1. signal() — Reactive State</h2>
    <div class="code">// Create a signal with an initial value
count = signal(0);

// Read: call it like a function
console.log(this.count());       // 0

// Write:
this.count.set(5);               // set to 5
this.count.update(v => v + 1);   // update based on current value
this.count.mutate(v => v++);     // mutate in place (objects/arrays)</div>

    <div style="display:flex;align-items:center;gap:1rem;margin-top:.8rem">
      <button class="btn" (click)="count.update(v => v - step())">−{{ step() }}</button>
      <span style="font-size:2.5rem;font-weight:bold;color:#dd0031;min-width:3rem;text-align:center">{{ count() }}</span>
      <button class="btn" (click)="count.update(v => v + step())">+{{ step() }}</button>
      <button class="btn" style="background:#888" (click)="count.set(0)">Reset</button>
    </div>
    <div style="margin-top:.8rem">
      Step: {{ [1,5,10].join(' / ') }} &nbsp;
      <button *ngFor="let s of [1,5,10]" class="btn" [style.background]="step()===s ? '#ff6d00' : '#888'"
              (click)="step.set(s)">{{ s }}</button>
    </div>
  </div>

  <!-- 2. computed() -->
  <div class="card">
    <h2>2. computed() — Derived State</h2>
    <div class="code">// computed() automatically re-evaluates when its signal dependencies change
doubled   = computed(() => this.count() * 2);
isEven    = computed(() => this.count() % 2 === 0);
gradeLabel = computed(() => {
  const g = this.grade();
  return g >= 90 ? 'A' : g >= 80 ? 'B' : g >= 70 ? 'C' : 'D';
});</div>

    <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:.8rem;margin-top:.8rem">
      <div style="background:#f0f4ff;padding:.8rem;border-radius:6px;text-align:center">
        <div style="color:#888;font-size:.8rem">count()</div>
        <div style="font-size:1.8rem;font-weight:bold">{{ count() }}</div>
      </div>
      <div style="background:#f0f4ff;padding:.8rem;border-radius:6px;text-align:center">
        <div style="color:#888;font-size:.8rem">doubled()</div>
        <div style="font-size:1.8rem;font-weight:bold;color:#dd0031">{{ doubled() }}</div>
      </div>
      <div [style.background]="isEven() ? '#e8f5e9' : '#fce4ec'" style="padding:.8rem;border-radius:6px;text-align:center">
        <div style="color:#888;font-size:.8rem">isEven()</div>
        <div style="font-size:1.8rem;font-weight:bold">{{ isEven() ? '✅' : '⛔' }}</div>
      </div>
    </div>

    <div style="margin-top:1rem">
      <strong>Grade signal + computed label:</strong>
      <div style="display:flex;align-items:center;gap:.8rem;margin-top:.4rem">
        <input type="range" min="0" max="100" [value]="grade()" (input)="grade.set(+$any($event.target).value)" style="flex:1" />
        <span style="min-width:3ch;font-weight:bold">{{ grade() }}</span>
        <span style="background:#dd0031;color:white;padding:2px 10px;border-radius:12px;font-weight:bold">{{ gradeLabel() }}</span>
      </div>
    </div>
  </div>

  <!-- 3. effect() -->
  <div class="card">
    <h2>3. effect() — Side Effects</h2>
    <div class="code">// effect() runs a side effect whenever its signal dependencies change
constructor() {
  effect(() => {
    // Re-runs whenever count() or theme() changes
    document.title = \`Count: \${this.count()} — \${this.theme()}\`;
    console.log('Effect ran:', this.count());
  });
}</div>

    <p style="color:#555;font-size:.85rem;margin:.4rem 0 .8rem">
      Change the count or theme below — the effect will update the page title and log to console.
    </p>
    <div style="display:flex;gap:.5rem;align-items:center">
      <strong>Theme:</strong>
      <button *ngFor="let t of ['light','dark','angular']" class="btn"
              [style.background]="theme()===t ? '#ff6d00' : '#888'"
              (click)="theme.set(t)">{{ t }}</button>
    </div>
    <div style="margin-top:.8rem;padding:.8rem;border-radius:6px"
         [style.background]="theme() === 'dark' ? '#1a1a2e' : theme() === 'angular' ? '#fce4ec' : '#f9f9f9'"
         [style.color]="theme() === 'dark' ? 'white' : '#333'">
      Theme: <strong>{{ theme() }}</strong> · Count: <strong>{{ count() }}</strong>
    </div>
    <div style="background:#1e1e1e;color:#98c379;padding:.8rem;border-radius:6px;font-family:monospace;font-size:.82rem;max-height:130px;overflow-y:auto;margin-top:.8rem">
      <div *ngFor="let log of effectLog">{{ log }}</div>
      <div *ngIf="!effectLog.length" style="color:#888">Change count or theme to see effect logs…</div>
    </div>
  </div>

  <!-- 4. Signals vs Observables -->
  <div class="card">
    <h2>4. Signals vs Observables</h2>
    <table>
      <tr><th>Feature</th><th>Signals</th><th>RxJS Observables</th></tr>
      <tr><td>Creation</td><td><code>signal(value)</code></td><td><code>new Observable(...)</code></td></tr>
      <tr><td>Reading</td><td><code>count()</code> — call as function</td><td><code>obs$.subscribe(...)</code></td></tr>
      <tr><td>Writing</td><td><code>count.set(v)</code> or <code>.update(fn)</code></td><td><code>subject.next(v)</code></td></tr>
      <tr><td>Derived</td><td><code>computed(() => count() * 2)</code></td><td><code>obs$.pipe(map(v => v*2))</code></td></tr>
      <tr><td>Side effects</td><td><code>effect(() => ...)</code></td><td><code>obs$.subscribe(fn)</code></td></tr>
      <tr><td>Template</td><td><code>{{ '{{' }} count() {{ '}}' }}</code></td><td><code>{{ '{{' }} obs$ | async {{ '}}' }}</code></td></tr>
      <tr><td>Best for</td><td>Component state, derived UI values</td><td>Async streams, HTTP, events</td></tr>
    </table>
  </div>
</div>
  `
})
export class AppComponent {
  // Signals
  count = signal(0);
  step  = signal(1);
  grade = signal(75);
  theme = signal('light');

  // Computed signals (auto-update when dependencies change)
  doubled    = computed(() => this.count() * 2);
  isEven     = computed(() => this.count() % 2 === 0);
  gradeLabel = computed(() => {
    const g = this.grade();
    return g >= 90 ? 'A' : g >= 80 ? 'B' : g >= 70 ? 'C' : 'D';
  });

  effectLog: string[] = [];

  constructor() {
    // effect() — runs when count() or theme() changes
    effect(() => {
      const msg = `[effect] count=${this.count()}, theme="${this.theme()}"`;
      this.effectLog.unshift(msg);
      if (this.effectLog.length > 10) this.effectLog.pop();
      document.title = `Count: ${this.count()} | Angular Signals`;
    });
  }
}
