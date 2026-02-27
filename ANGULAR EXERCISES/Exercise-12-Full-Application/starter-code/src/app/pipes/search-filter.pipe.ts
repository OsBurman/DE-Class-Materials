import { Pipe, PipeTransform } from '@angular/core';

// TODO: Filter tasks[] by title (case-insensitive contains match on the query string)
// If query is empty/falsy, return the full array unchanged.
@Pipe({ name: 'searchFilter', standalone: true })
export class SearchFilterPipe implements PipeTransform {
  transform(items: any[], query: string): any[] {
    return items; // your code here
  }
}
