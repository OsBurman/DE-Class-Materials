import { Pipe, PipeTransform } from '@angular/core';

// TODO: Implement the transform(date: string | Date): string method
// Return a human-readable "time ago" string: "just now", "5 minutes ago", "2 hours ago", "3 days ago", etc.
@Pipe({ name: 'timeAgo', standalone: true })
export class TimeAgoPipe implements PipeTransform {
  transform(date: string | Date): string {
    return ''; // your code here
  }
}
