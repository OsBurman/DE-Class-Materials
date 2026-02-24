import { Pipe, PipeTransform } from '@angular/core';

// TODO Task 2: Decorate with @Pipe({ name: 'currencyFormat' })
// Implement PipeTransform interface
// transform(value: number): string
// Should return "$X.XX" format — e.g. 79.99 → "$79.99"
// Hint: value.toFixed(2) gives you 2 decimal places

@Pipe({ name: 'currencyFormat' })
export class CurrencyFormatPipe implements PipeTransform {
  transform(value: number): string {
    // TODO: return formatted price string
    return `$${value}`;
  }
}
