import { Component } from '@angular/core';
import { NgClass, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';

// TODO 1: Define a Task interface with:
//   id: number
//   title: string
//   completed: boolean
//   priority: 'low' | 'medium' | 'high'

// export interface Task { ... }

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgClass, NgStyle, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  // TODO 2: Create a `tasks` array with at least 5 sample tasks.
  //         Include a mix of completed/incomplete and different priorities.
  tasks: any[] = [
    // { id: 1, title: 'Buy groceries', completed: false, priority: 'low' },
    // ...
  ];

  // TODO 3: Declare a `filter` property with type 'all' | 'active' | 'completed'.
  //         Default to 'all'.
  filter: 'all' | 'active' | 'completed' = 'all';

  // TODO 4: Declare a `newTaskTitle` string property (starts as empty string).
  newTaskTitle = '';

  // TODO 5: Create a `filteredTasks` getter that returns:
  //   - all tasks if filter === 'all'
  //   - incomplete tasks if filter === 'active'
  //   - completed tasks if filter === 'completed'
  get filteredTasks() {
    // your code here
    return this.tasks;
  }

  // TODO 6: Implement addTask().
  //   - Only add if newTaskTitle is not empty
  //   - Create a new Task with a unique id, the title, completed: false, priority: 'medium'
  //   - Reset newTaskTitle to ''
  addTask(): void {
    // your code here
  }

  // TODO 7: Implement toggleTask(id: number).
  //   - Find the task by id and flip its `completed` boolean.
  toggleTask(id: number): void {
    // your code here
  }

  // TODO 8: Implement deleteTask(id: number).
  //   - Remove the task with the matching id from `tasks`.
  deleteTask(id: number): void {
    // your code here
  }

  // TODO 9: Create two getters:
  //   - `completedCount` returns how many tasks are completed
  //   - `totalCount` returns total number of tasks
  get completedCount(): number {
    // your code here
    return 0;
  }

  get totalCount(): number {
    return this.tasks.length;
  }
}
