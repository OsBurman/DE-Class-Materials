import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>Order Calculator</h1>

    <div>
      <label>
        Unit Price ($):
        <!-- [value] reads the signal; (input) updates it on every keystroke -->
        <input type="number" [value]="unitPrice()"
               (input)="unitPrice.set(+(($event.target as HTMLInputElement).value))"
               min="0" />
      </label>
    </div>

    <div>
      <label>
        Quantity:
        <input type="number" [value]="quantity()"
               (input)="quantity.set(+(($event.target as HTMLInputElement).value))"
               min="1" />
      </label>
    </div>

    <div>
      <label>
        Discount (%):
        <input type="number" [value]="discountPercent()"
               (input)="discountPercent.set(+(($event.target as HTMLInputElement).value))"
               min="0" max="100" />
      </label>
    </div>

    <hr />

    <!-- computed signals are also read by calling them as functions -->
    <p>Subtotal:        ${{ subtotal().toFixed(2) }}</p>
    <p>Discount Amount: ${{ discountAmount().toFixed(2) }}</p>
    <p>Total:           ${{ total().toFixed(2) }}</p>
    <p><strong>Summary:</strong> {{ summary() }}</p>
  `
})
export class AppComponent {
  // Writable signals — the source of truth
  unitPrice = signal<number>(25);
  quantity = signal<number>(1);
  discountPercent = signal<number>(0);

  // computed() tracks every signal read inside its function.
  // Angular re-evaluates only when a dependency changes (memoised).
  subtotal = computed(() => this.unitPrice() * this.quantity());

  discountAmount = computed(() => this.subtotal() * this.discountPercent() / 100);

  // total depends on two computed signals — Angular handles the chain automatically
  total = computed(() => this.subtotal() - this.discountAmount());

  summary = computed(() =>
    `Qty: ${this.quantity()} × $${this.unitPrice()} = $${this.subtotal().toFixed(2)}` +
    `  |  Discount: ${this.discountPercent()}%` +
    `  |  Total: $${this.total().toFixed(2)}`
  );
}
