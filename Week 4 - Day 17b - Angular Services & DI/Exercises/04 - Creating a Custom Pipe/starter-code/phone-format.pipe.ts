import { Pipe, PipeTransform } from '@angular/core';

// TODO 3: Add the @Pipe decorator with name: 'phoneFormat'
@Pipe({ name: 'phoneFormat' })
export class PhoneFormatPipe implements PipeTransform {
  // TODO 4: Implement transform(value: string): string
  //         1. Strip all non-digit characters from value.
  //         2. If digits.length !== 10, return original value unchanged.
  //         3. Otherwise return "(XXX) XXX-XXXX" formatted string.
  transform(value: string): string {
    // TODO: implement phone formatting logic
    return value;
  }
}
