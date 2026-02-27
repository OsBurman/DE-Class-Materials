import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { Task } from '../../models/task.model';
import { TimeAgoPipe } from '../../pipes/time-ago.pipe';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, TimeAgoPipe],
  template: `
    <div class="task-detail">
      @if (task) {
        <h1>{{ task.title }}</h1>
        <!-- TODO: Display all task fields in a readable layout -->
        <!-- priority, status, dueDate, description, tags, createdAt | timeAgo -->
        <div class="actions">
          <button (click)="edit()">Edit</button>
          <button (click)="back()">← Back</button>
        </div>
      } @else {
        <p>Task not found.</p>
        <button (click)="back()">← Back</button>
      }
    </div>
  `
})
export class TaskDetailComponent implements OnInit {
  @Input() id!: string;   // populated by withComponentInputBinding()

  private taskService = inject(TaskService);
  private router = inject(Router);

  task: Task | undefined;

  ngOnInit() {
    // TODO: Look up this.task = this.taskService.getById(+this.id)
  }

  edit() { this.router.navigate(['/tasks', this.id, 'edit']); }
  back() { this.router.navigate(['/tasks']); }
}
