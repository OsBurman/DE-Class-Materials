import { Pipe, PipeTransform } from '@angular/core';

// TODO 5: Add @Pipe decorator: @Pipe({ name: 'truncate', standalone: true })
// TODO 6: implement transform(value: string, limit = 100, ellipsis = '...'): string
//   - If value.length <= limit, return value as-is
//   - Otherwise return value.substring(0, limit) + ellipsis

export class TruncatePipe implements PipeTransform {
  transform(value: string, limit = 100, ellipsis = '...'): string {
    // your code here
    return value;
  }
}
