import { Pipe, PipeTransform } from '@angular/core';

// TODO 3: Add the @Pipe decorator: @Pipe({ name: 'timeAgo', standalone: true })
// TODO 4: Implement transform(date: Date | string): string
//   - Parse the date with new Date(date)
//   - Get the difference in milliseconds: Date.now() - parsed.getTime()
//   - Convert to seconds, minutes, hours, days
//   - Return a human-readable string like "5 minutes ago"

export class TimeAgoPipe implements PipeTransform {
  transform(date: Date | string): string {
    // your code here
    return '';
  }
}
