import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { NotificationService } from '../../services/notification.service';
import { SearchFilterPipe } from '../../pipes/search-filter.pipe';
import { TimeAgoPipe } from '../../pipes/time-ago.pipe';
import { Status, Priority } from '../../models/task.model';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, SearchFilterPipe, TimeAgoPipe],
  template: `
    <div class="task-list">
      <div class="toolbar">
        <h1>Tasks</h1>
        <a routerLink="/tasks/new" class="btn-primary">+ New Task</a>
      </div>

      <!-- TODO: Search input bound to searchQuery signal -->

      <!-- TODO: Status filter buttons (All, Todo, In-Progress, Done) -->

      <!-- TODO: Sort dropdown -->

      <!-- TODO: Render filtered/sorted tasks using | searchFilter:searchQuery() -->
      <!-- Each row: title (link to detail), priority badge, status badge, due date, time ago, delete button -->
      <p>Tasks will appear here.</p>
    </div>
  `
})
export class TaskListComponent {
  taskService = inject(TaskService);
  notificationService = inject(NotificationService);

  searchQuery = signal('');
  statusFilter = signal<Status | 'all'>('all');
  sortBy = signal<'dueDate' | 'priority' | 'title'>('dueDate');

  // TODO: computed() that filters by statusFilter then applies sort
  filteredTasks = computed(() => this.taskService.tasks());

  deleteTask(id: number) {
    // TODO: call taskService.deleteTask(id) then notificationService.send(...)
  }
}
