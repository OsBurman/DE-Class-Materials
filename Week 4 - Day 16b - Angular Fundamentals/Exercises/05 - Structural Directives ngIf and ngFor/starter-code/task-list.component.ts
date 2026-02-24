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

  // TODO 5: Implement toggleTask(id: number).
  //         Find the task with the matching id in this.tasks and flip its done property.
  //         Hint: use this.tasks.find(t => t.id === id)
  toggleTask(id: number): void {
    // TODO: find the task by id and flip task.done
  }
}
