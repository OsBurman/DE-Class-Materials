import { Pipe, PipeTransform } from '@angular/core';

export interface Employee {
  id: number;
  name: string;
  department: string;
  role: string;
  salary: number;
  startDate: Date;
  bio: string;
}

@Pipe({ name: 'searchFilter', standalone: true })
export class SearchFilterPipe implements PipeTransform {
  transform(employees: Employee[], query: string): Employee[] {
    if (!query?.trim()) return employees;
    const q = query.toLowerCase();
    return employees.filter(e =>
      e.name.toLowerCase().includes(q) ||
      e.department.toLowerCase().includes(q) ||
      e.role.toLowerCase().includes(q)
    );
  }
}
