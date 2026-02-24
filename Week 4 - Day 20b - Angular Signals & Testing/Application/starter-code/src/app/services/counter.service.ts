import { Injectable, signal, computed, effect } from '@angular/core';

// TODO Task 1: Build CounterService using Angular Signals
@Injectable({ providedIn: 'root' })
export class CounterService {
  // TODO Task 1: Declare signals
  // count = signal(0);
  // history = signal<number[]>([]);

  // TODO Task 1: Declare computed signals
  // doubled = computed(() => this.count() * 2);
  // isPositive = computed(() => this.count() > 0);

  // Placeholder signals (replace with real implementation)
  count = signal(0);
  history = signal<number[]>([]);
  doubled = computed(() => this.count() * 2);
  isPositive = computed(() => this.count() > 0);

  constructor() {
    // TODO Task 1: Add an effect() that:
    // 1. Logs "Counter changed to: [value]" whenever count changes
    // 2. Appends the new count value to history signal
    // effect(() => { ... });
  }

  increment(): void {
    // TODO Task 1: this.count.update(v => v + 1)
  }

  decrement(): void {
    // TODO Task 1: this.count.update(v => v - 1)
  }

  reset(): void {
    // TODO Task 1: this.count.set(0)
  }

  stepBy(n: number): void {
    // TODO Task 1: this.count.update(v => v + n)
  }
}
