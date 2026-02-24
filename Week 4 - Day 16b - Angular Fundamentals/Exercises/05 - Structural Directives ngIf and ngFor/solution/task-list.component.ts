import { Component } from '@angular/core';

interface Task {
  id: number;
  title: string;
  done: boolean;
}

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html'
})
export class TaskListComponent {
  tasks: Task[] = [
    { id: 1, title: 'Buy groceries',  done: false },
    { id: 2, title: 'Read book',      done: true  },
    { id: 3, title: 'Go for a run',   done: false },
    { id: 4, title: 'Write code',     done: true  },
  ];

  toggleTask(id: number): void {
    // find() returns the Task object (a reference), so mutating task.done mutates the array element
    const task = this.tasks.find(t => t.id === id);
    if (task) {
      task.done = !task.done;
    }
  }
}
