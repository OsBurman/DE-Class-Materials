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
  // TODO 1: Populate the tasks array with at least 4 Task objects.
  //         Use a mix of statuses: 'todo', 'in-progress', and 'done'.
  tasks: Task[] = [
    // { id: 1, title: '...', status: 'todo' },
  ];

  // TODO 2: Declare 'selectedTask' as type Task | null, initialized to null.
  selectedTask: Task | null = null;

  // TODO 3: Declare 'showCompleted' as a boolean, initialized to true.
  showCompleted = true;

  // TODO 4: Implement selectTask — set selectedTask to the passed task object.
  selectTask(task: Task): void {
    // TODO: this.selectedTask = task;
  }

  // TODO 5: Implement toggleCompleted — flip this.showCompleted between true/false.
  toggleCompleted(): void {
    // TODO: this.showCompleted = !this.showCompleted;
  }
}
