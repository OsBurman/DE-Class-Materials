import { Pipe, PipeTransform } from '@angular/core';
import { Task } from '../models/task.model';

@Pipe({ name: 'searchFilter', standalone: true })
export class SearchFilterPipe implements PipeTransform {
  transform(items: Task[], query: string): Task[] {
    if (!query || !query.trim()) return items;
    const q = query.toLowerCase();
    return items.filter(t =>
      t.title.toLowerCase().includes(q) ||
      t.tags.some(tag => tag.toLowerCase().includes(q))
    );
  }
}
