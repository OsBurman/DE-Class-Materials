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
        <!-- TODO: Bind this input so changing it calls unitPrice.set(+event.target.value) -->
        <input type="number" [value]="unitPrice()" (input)="unitPrice.set(0)" min="0" />
      </label>
    </div>

    <div>
      <label>
        Quantity:
        <!-- TODO: Bind this input so changing it calls quantity.set(+event.target.value) -->
        <input type="number" [value]="quantity()" (input)="quantity.set(0)" min="1" />
      </label>
    </div>

    <div>
      <label>
        Discount (%):
        <!-- TODO: Bind this input so changing it calls discountPercent.set(+event.target.value) -->
        <input type="number" [value]="discountPercent()" (input)="discountPercent.set(0)" min="0" max="100" />
      </label>
    </div>

    <hr />

    <!-- TODO: Display subtotal(), discountAmount(), total(), and summary() -->
    <p>Subtotal:        $</p>
    <p>Discount Amount: $</p>
    <p>Total:           $</p>
    <p><strong>Summary:</strong> </p>
  `
})
export class AppComponent {
  // TODO: Create writable signal 'unitPrice' initialised to 25
  unitPrice = signal<number>(0); // replace 0 with correct initial value

  // TODO: Create writable signal 'quantity' initialised to 1
  quantity = signal<number>(0); // replace 0 with correct initial value

  // TODO: Create writable signal 'discountPercent' initialised to 0
  discountPercent = signal<number>(0);

  // TODO: Create computed signal 'subtotal' = unitPrice * quantity
  // subtotal = computed(() => ...);

  // TODO: Create computed signal 'discountAmount' = subtotal * discountPercent / 100
  // discountAmount = computed(() => ...);

  // TODO: Create computed signal 'total' = subtotal - discountAmount
  // total = computed(() => ...);

  // TODO: Create computed signal 'summary' returning the formatted string described in instructions
  // summary = computed(() => `...`);
}
