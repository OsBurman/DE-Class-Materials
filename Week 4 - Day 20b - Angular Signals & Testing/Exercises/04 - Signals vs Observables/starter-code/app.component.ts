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

    <!-- Part A: Side-by-side counters -->
    <section>
      <h2>Observable Counter</h2>
      <!-- TODO: Display the observable counter using the async pipe: count$ | async -->
      <p>Count: </p>
      <button (click)="incrementObs()">+1</button>
      <button (click)="decrementObs()">-1</button>
      <button (click)="resetObs()">Reset</button>
    </section>

    <section>
      <h2>Signal Counter</h2>
      <!-- TODO: Display the signal counter by calling sigCount() -->
      <p>Count: </p>
      <button (click)="incrementSig()">+1</button>
      <button (click)="decrementSig()">-1</button>
      <button (click)="resetSig()">Reset</button>
    </section>

    <!-- Part B: Interop -->
    <section>
      <h2>Interop</h2>
      <!-- TODO: Display obsAsSignal() -->
      <p>Observable → Signal: </p>
      <!-- TODO: Display sigAsObsValue -->
      <p>Signal → Observable: </p>
    </section>
  `
})
export class AppComponent implements OnInit, OnDestroy {
  // ── Part A: Observable-based counter ──────────────────────────────────────
  // TODO: Create a BehaviorSubject<number> initialised to 0, named _countSubject
  // TODO: Expose it as count$ observable via .asObservable()

  incrementObs() {
    // TODO: Emit the next value using _countSubject.next()
  }

  decrementObs() {
    // TODO: Emit the next value
  }

  resetObs() {
    // TODO: Reset to 0
  }

  // ── Part A: Signal-based counter ──────────────────────────────────────────
  // TODO: Create a writable signal<number> named sigCount initialised to 0

  incrementSig() {
    // TODO: Use .update()
  }

  decrementSig() {
    // TODO: Use .update()
  }

  resetSig() {
    // TODO: Use .set(0)
  }

  // ── Part B: Interop ───────────────────────────────────────────────────────
  // TODO: Convert count$ to a signal using toSignal(). Name it obsAsSignal.
  //       Call toSignal(this.count$, { initialValue: 0 })

  sigAsObsValue = 0;
  private sigObsSub?: Subscription;

  ngOnInit() {
    // TODO: Convert sigCount to an Observable using toObservable(),
    //       subscribe and store each emitted value in sigAsObsValue.
    //       Save the subscription so you can unsubscribe later.
  }

  ngOnDestroy() {
    // TODO: Unsubscribe from sigObsSub to prevent memory leaks
  }
}
