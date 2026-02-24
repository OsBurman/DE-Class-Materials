import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'phoneFormat' })
export class PhoneFormatPipe implements PipeTransform {
  transform(value: string): string {
    // Strip everything that isn't a digit
    const digits = value.replace(/\D/g, '');

    // Only format if we have exactly 10 digits
    if (digits.length !== 10) {
      return value;  // return original if invalid
    }

    // Format as (XXX) XXX-XXXX
    return `(${digits.slice(0, 3)}) ${digits.slice(3, 6)}-${digits.slice(6)}`;
  }
}
