import { Component, signal, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { BehaviorSubject, Subscription } from 'rxjs';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, AsyncPipe],
  template: `
    <h1>Signals vs Observables</h1>

    <!-- async pipe handles subscribe/unsubscribe automatically -->
    <section>
      <h2>Observable Counter</h2>
      <p>Count: {{ count$ | async }}</p>
      <button (click)="incrementObs()">+1</button>
      <button (click)="decrementObs()">-1</button>
      <button (click)="resetObs()">Reset</button>
    </section>

    <section>
      <h2>Signal Counter</h2>
      <!-- Signals are read directly — no pipe, no subscription -->
      <p>Count: {{ sigCount() }}</p>
      <button (click)="incrementSig()">+1</button>
      <button (click)="decrementSig()">-1</button>
      <button (click)="resetSig()">Reset</button>
    </section>

    <section>
      <h2>Interop</h2>
      <!-- toSignal wraps the observable; read it like any signal -->
      <p>Observable → Signal: {{ obsAsSignal() }}</p>
      <!-- Stored in a plain property after subscribing to toObservable() -->
      <p>Signal → Observable: {{ sigAsObsValue }}</p>
    </section>
  `
})
export class AppComponent implements OnInit, OnDestroy {
  // ── Observable-based counter ──────────────────────────────────────────────
  private _countSubject = new BehaviorSubject<number>(0);
  // Expose as public observable — consumers can't accidentally call .next()
  count$ = this._countSubject.asObservable();

  incrementObs() { this._countSubject.next(this._countSubject.value + 1); }
  decrementObs() { this._countSubject.next(this._countSubject.value - 1); }
  resetObs()     { this._countSubject.next(0); }

  // ── Signal-based counter ──────────────────────────────────────────────────
  sigCount = signal<number>(0);

  incrementSig() { this.sigCount.update(n => n + 1); }
  decrementSig() { this.sigCount.update(n => n - 1); }
  resetSig()     { this.sigCount.set(0); }

  // ── Interop: Observable → Signal ──────────────────────────────────────────
  // toSignal() must be called in an injection context (field initialiser qualifies).
  // initialValue provides the value before the first emission.
  obsAsSignal = toSignal(this.count$, { initialValue: 0 });

  // ── Interop: Signal → Observable ──────────────────────────────────────────
  // toObservable() must also be in an injection context.
  private sigCount$ = toObservable(this.sigCount);

  sigAsObsValue = 0;
  private sigObsSub?: Subscription;

  ngOnInit() {
    // Subscribe manually because we want to store the value in a plain property.
    this.sigObsSub = this.sigCount$.subscribe(val => {
      this.sigAsObsValue = val;
    });
  }

  ngOnDestroy() {
    // Always unsubscribe from manual subscriptions to prevent memory leaks.
    this.sigObsSub?.unsubscribe();
  }
}
