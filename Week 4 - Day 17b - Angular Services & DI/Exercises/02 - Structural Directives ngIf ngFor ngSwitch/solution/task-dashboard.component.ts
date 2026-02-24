import { Component } from '@angular/core';

interface Task {
  id: number;
  title: string;
  status: 'todo' | 'in-progress' | 'done';
}

@Component({
  selector: 'app-task-dashboard',
  templateUrl: './task-dashboard.component.html'
})
export class TaskDashboardComponent {
  tasks: Task[] = [
    { id: 1, title: 'Fix login bug',      status: 'in-progress' },
    { id: 2, title: 'Build dashboard UI', status: 'todo'        },
    { id: 3, title: 'Write unit tests',   status: 'done'        },
    { id: 4, title: 'Deploy to staging',  status: 'todo'        },
  ];

  selectedTask: Task | null = null;
  showCompleted = true;

  selectTask(task: Task): void {
    this.selectedTask = task;
  }

  toggleCompleted(): void {
    this.showCompleted = !this.showCompleted;
  }
}
