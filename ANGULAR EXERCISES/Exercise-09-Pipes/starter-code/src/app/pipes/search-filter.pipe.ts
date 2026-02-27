import { Pipe, PipeTransform } from '@angular/core';

// TODO 1: Add the @Pipe decorator:
//   @Pipe({ name: 'searchFilter', standalone: true })

// TODO 2: Implement PipeTransform.
//   transform(employees: any[], query: string): any[]
//   - If query is empty/falsy, return the full employees array
//   - Otherwise filter where name, department, OR role includes the query (case-insensitive)

export class SearchFilterPipe implements PipeTransform {
  transform(employees: any[], query: string): any[] {
    // your code here
    return employees;
  }
}
