import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard">
      <h1>Dashboard</h1>

      <!-- TODO: Stat cards using computed signals from TaskService -->
      <!-- Display: Total Tasks, Todo, In Progress, Done -->
      <div class="stats">
        <div class="card">
          <span class="count">{{ taskService.tasks().length }}</span>
          <span class="label">Total Tasks</span>
        </div>
        <!-- TODO: remaining stat cards -->
      </div>

      <!-- TODO: Recent Tasks list (last 5) -->
      <section>
        <h2>Recent Tasks</h2>
        <p>Display 5 most recently created tasks here.</p>
      </section>
    </div>
  `
})
export class DashboardComponent {
  taskService = inject(TaskService);
  // TODO: Add computed signal references for todoCount, inProgressCount, doneCount, recentTasks
}
