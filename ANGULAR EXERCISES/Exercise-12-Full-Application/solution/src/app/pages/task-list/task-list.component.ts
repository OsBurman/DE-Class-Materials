import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { NotificationService } from '../../services/notification.service';
import { SearchFilterPipe } from '../../pipes/search-filter.pipe';
import { TimeAgoPipe } from '../../pipes/time-ago.pipe';
import { Status, Task } from '../../models/task.model';

const PRIORITY_ORDER = { high: 0, medium: 1, low: 2 };

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, SearchFilterPipe, TimeAgoPipe],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css',
})
export class TaskListComponent {
  private taskService = inject(TaskService);
  private notificationService = inject(NotificationService);

  searchQuery  = signal('');
  statusFilter = signal<Status | 'all'>('all');
  sortBy       = signal<'dueDate' | 'priority' | 'title'>('dueDate');

  filteredTasks = computed(() => {
    let tasks = this.taskService.tasks();
    const sf = this.statusFilter();
    if (sf !== 'all') tasks = tasks.filter(t => t.status === sf);

    return [...tasks].sort((a, b) => {
      switch (this.sortBy()) {
        case 'priority': return PRIORITY_ORDER[a.priority] - PRIORITY_ORDER[b.priority];
        case 'title':    return a.title.localeCompare(b.title);
        default:         return (a.dueDate || 'zzz').localeCompare(b.dueDate || 'zzz');
      }
    });
  });

  setFilter(f: Status | 'all') { this.statusFilter.set(f); }

  deleteTask(task: Task) {
    this.taskService.deleteTask(task.id);
    this.notificationService.send({ type: 'warning', message: `Deleted: "${task.title}"` });
  }
}
