import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'truncate' })
export class TruncatePipe implements PipeTransform {
  transform(value: string, maxLength: number = 30): string {
    if (value.length <= maxLength) {
      return value;  // no truncation needed
    }
    return value.slice(0, maxLength) + '…';  // cut and append ellipsis
  }
}
