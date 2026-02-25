// Day 19b Part 2 — RxJS: Observables, Operators, BehaviorSubject, async pipe
// Run: npm install && npm start

import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {
  Observable, Subject, BehaviorSubject, interval, of, from, fromEvent,
  map, filter, switchMap, mergeMap, catchError, take, takeUntil,
  debounceTime, distinctUntilChanged, scan, combineLatest, forkJoin,
} from 'rxjs';

// ─────────────────────────────────────────────────────────────────────────────
// Root Component
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, AsyncPipe],
  styles: [`
    * { box-sizing: border-box; }
    .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; font-family: -apple-system, sans-serif; }
    .header { background: #dd0031; color: white; padding: 1.5rem 2rem; border-radius: 8px; margin-bottom: 2rem; }
    .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
    h2 { color: #dd0031; margin-bottom: 1rem; padding-bottom: .4rem; border-bottom: 2px solid #ff6d00; }
    .code { background: #1e1e1e; color: #d4e157; padding: 1rem; border-radius: 6px; font-size: .82rem; white-space: pre; overflow: auto; margin: .5rem 0; }
    .btn { background: #dd0031; color: white; border: none; padding: .4rem .9rem; border-radius: 4px; cursor: pointer; margin: .2rem; }
    .badge { display: inline-block; background: #dd0031; color: white; border-radius: 4px; padding: 2px 8px; font-size: .75rem; margin: 2px; }
  `],
  template: `
<div class="page">
  <div class="header">
    <h1>🅰️ Day 19b Part 2 — RxJS &amp; async pipe</h1>
  </div>

  <!-- Observable creation -->
  <div class="card">
    <h2>1. Creating Observables</h2>
    <div class="code">of(1,2,3)              // emits given values then completes
from([10,20,30])       // from an iterable/array
interval(1000)         // emits 0,1,2,3… every second
new Observable(obs => { obs.next(value); obs.complete(); })</div>
    <div style="display:flex;gap:.5rem;flex-wrap:wrap;margin-top:.8rem">
      <button class="btn" (click)="demoOf()">of(1,2,3)</button>
      <button class="btn" (click)="demoFrom()">from(array)</button>
      <button class="btn" (click)="toggleInterval()">{{ timerRunning ? '⏹ Stop' : '▶ Start' }} interval</button>
    </div>
    <div style="margin-top:.5rem;min-height:2rem;color:#555;font-size:.9rem">{{ demoOutput }}</div>
    <div *ngIf="timerRunning" style="font-size:1.3rem;font-weight:bold;color:#dd0031">⏱ {{ timerValue }}</div>
  </div>

  <!-- Operators -->
  <div class="card">
    <h2>2. Core RxJS Operators</h2>
    <div class="code">source$.pipe(
  filter(n => n % 2 === 0),       // only even numbers
  map(n => n * 10),                // multiply by 10
  take(3),                         // take first 3
  scan((acc, val) => acc + val, 0) // running total
)</div>
    <button class="btn" (click)="runOperators()">Run Pipeline</button>
    <div style="margin-top:.8rem">
      <div *ngFor="let result of operatorResults" style="padding:.3rem .6rem;background:#f9f9f9;border-radius:4px;margin:.2rem;font-family:monospace">
        {{ result }}
      </div>
    </div>
  </div>

  <!-- switchMap -->
  <div class="card">
    <h2>3. switchMap — Inner Observables</h2>
    <div class="code">// switchMap cancels the previous inner observable when a new outer value arrives
clicks$.pipe(
  switchMap(event => this.http.get('/api/data'))
)</div>
    <p style="color:#555;font-size:.85rem;margin-bottom:.8rem">
      Click different users — <code>switchMap</code> cancels the previous HTTP request if you click again quickly.
    </p>
    <div style="display:flex;gap:.4rem;flex-wrap:wrap">
      <button *ngFor="let id of [1,2,3,4]" class="btn" (click)="loadUserWithSwitchMap(id)">User {{ id }}</button>
    </div>
    <div *ngIf="switchMapResult$ | async as user" style="margin-top:.8rem;background:#f0f4ff;padding:.8rem;border-radius:6px">
      <strong>{{ user.name }}</strong> — {{ user.email }}
    </div>
  </div>

  <!-- BehaviorSubject -->
  <div class="card">
    <h2>4. BehaviorSubject — State Management</h2>
    <div class="code">// BehaviorSubject holds the current value and emits it to new subscribers immediately
const count$ = new BehaviorSubject&lt;number&gt;(0);
count$.next(count$.value + 1);  // update value
count$.subscribe(v => console.log(v));  // always gets latest value</div>
    <div style="display:flex;align-items:center;gap:.5rem;margin-top:.8rem">
      <button class="btn" (click)="decrement()">−</button>
      <strong style="font-size:2rem;min-width:3rem;text-align:center">{{ count$ | async }}</strong>
      <button class="btn" (click)="increment()">+</button>
      <button class="btn" style="background:#888" (click)="resetCount()">Reset</button>
    </div>
    <p style="color:#555;font-size:.85rem;margin-top:.8rem">The count above uses <code>| async</code> pipe — Angular automatically subscribes and unsubscribes.</p>
  </div>

  <!-- async pipe -->
  <div class="card">
    <h2>5. async Pipe — Auto-subscribe in Templates</h2>
    <div class="code">&lt;div *ngIf="users$ | async as users"&gt;
  &lt;div *ngFor="let u of users"&gt;{{ '{{' }} u.name {{ '}}' }}&lt;/div&gt;
&lt;/div&gt;
// async pipe:  - subscribes automatically
//              - triggers change detection
//              - UNSUBSCRIBES on component destroy (no memory leaks!)</div>
    <button class="btn" (click)="loadAsyncUsers()">Load Users with async pipe</button>
    <div *ngIf="asyncUsers$ | async as users; else loadingRef" style="margin-top:.8rem">
      <div *ngFor="let u of users"
           style="padding:.4rem;background:#f9f9f9;border-radius:4px;margin:.2rem;display:flex;justify-content:space-between">
        <span>{{ u.name }}</span><small style="color:#888">{{ u.email }}</small>
      </div>
    </div>
    <ng-template #loadingRef>
      <p *ngIf="asyncUsersLoading" style="color:#888;margin-top:.5rem">⏳ Loading…</p>
    </ng-template>
  </div>
</div>
  `
})
export class AppComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private http = inject(HttpClient);

  // 1. Observable creation demo
  demoOutput = '';
  timerRunning = false;
  timerValue = 0;
  private timerSub: any;

  demoOf() {
    const results: number[] = [];
    of(1, 2, 3).subscribe(v => results.push(v));
    this.demoOutput = `of(1,2,3) emitted: [${results.join(', ')}]`;
  }

  demoFrom() {
    const results: number[] = [];
    from([10, 20, 30, 40]).subscribe(v => results.push(v));
    this.demoOutput = `from([10,20,30,40]) emitted: [${results.join(', ')}]`;
  }

  toggleInterval() {
    if (this.timerRunning) {
      this.timerSub?.unsubscribe();
      this.timerRunning = false;
    } else {
      this.timerRunning = true;
      this.timerSub = interval(1000).subscribe(v => this.timerValue = v);
    }
  }

  // 2. Operators
  operatorResults: string[] = [];

  runOperators() {
    this.operatorResults = [];
    from([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]).pipe(
      filter(n => n % 2 === 0),
      map(n => n * 10),
      take(3),
      scan((acc, val) => acc + val, 0)
    ).subscribe(v => this.operatorResults.push(`Running total: ${v}`));
  }

  // 3. switchMap
  private userClick$ = new Subject<number>();
  switchMapResult$!: Observable<any>;

  loadUserWithSwitchMap(id: number) {
    this.userClick$.next(id);
  }

  // 4. BehaviorSubject
  count$ = new BehaviorSubject<number>(0);
  increment() { this.count$.next(this.count$.value + 1); }
  decrement() { this.count$.next(this.count$.value - 1); }
  resetCount() { this.count$.next(0); }

  // 5. async pipe
  asyncUsers$: Observable<any[]> | null = null;
  asyncUsersLoading = false;

  loadAsyncUsers() {
    this.asyncUsersLoading = true;
    this.asyncUsers$ = this.http.get<any[]>('https://jsonplaceholder.typicode.com/users?_limit=4');
  }

  ngOnInit() {
    // switchMap setup
    this.switchMapResult$ = this.userClick$.pipe(
      switchMap(id => this.http.get(`https://jsonplaceholder.typicode.com/users/${id}`)),
      takeUntil(this.destroy$)
    );
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.timerSub?.unsubscribe();
    this.count$.complete();
  }
}
