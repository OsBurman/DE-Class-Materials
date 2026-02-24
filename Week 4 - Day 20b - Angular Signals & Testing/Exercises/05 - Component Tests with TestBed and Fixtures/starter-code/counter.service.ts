import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CounterService {
  private count = 0;

  increment(): void { this.count++; }
  decrement(): void { this.count--; }
  getCount(): number { return this.count; }
}
