import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { Task } from '../../models/task.model';
import { TimeAgoPipe } from '../../pipes/time-ago.pipe';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, TimeAgoPipe],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.css',
})
export class TaskDetailComponent implements OnInit {
  @Input() id!: string;

  private taskService = inject(TaskService);
  private router = inject(Router);

  task: Task | undefined;

  ngOnInit() {
    this.task = this.taskService.getById(+this.id);
  }

  edit() { this.router.navigate(['/tasks', this.id, 'edit']); }
  back() { this.router.navigate(['/tasks']); }
}
