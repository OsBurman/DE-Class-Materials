import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'timeAgo', standalone: true })
export class TimeAgoPipe implements PipeTransform {
  transform(date: string | Date): string {
    const seconds = Math.floor((Date.now() - new Date(date).getTime()) / 1000);
    if (seconds < 60)      return 'just now';
    if (seconds < 3600)    return `${Math.floor(seconds / 60)} minutes ago`;
    if (seconds < 86400)   return `${Math.floor(seconds / 3600)} hours ago`;
    if (seconds < 2592000) return `${Math.floor(seconds / 86400)} days ago`;
    return new Date(date).toLocaleDateString();
  }
}
