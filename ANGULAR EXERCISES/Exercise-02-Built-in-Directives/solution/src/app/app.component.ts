import { Component } from '@angular/core';
import { NgClass, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface Task {
  id: number;
  title: string;
  completed: boolean;
  priority: 'low' | 'medium' | 'high';
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgClass, NgStyle, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  tasks: Task[] = [
    { id: 1, title: 'Set up Angular project', completed: true, priority: 'high' },
    { id: 2, title: 'Build profile card component', completed: true, priority: 'medium' },
    { id: 3, title: 'Learn NgClass directive', completed: false, priority: 'medium' },
    { id: 4, title: 'Practice @for loops', completed: false, priority: 'low' },
    { id: 5, title: 'Write unit tests', completed: false, priority: 'high' },
    { id: 6, title: 'Deploy to production', completed: false, priority: 'low' },
  ];

  filter: 'all' | 'active' | 'completed' = 'all';
  newTaskTitle = '';

  readonly filters: Array<'all' | 'active' | 'completed'> = ['all', 'active', 'completed'];

  get filteredTasks(): Task[] {
    if (this.filter === 'active') return this.tasks.filter(t => !t.completed);
    if (this.filter === 'completed') return this.tasks.filter(t => t.completed);
    return this.tasks;
  }

  addTask(): void {
    const title = this.newTaskTitle.trim();
    if (!title) return;
    const newTask: Task = {
      id: Date.now(),
      title,
      completed: false,
      priority: 'medium',
    };
    this.tasks.push(newTask);
    this.newTaskTitle = '';
  }

  toggleTask(id: number): void {
    const task = this.tasks.find(t => t.id === id);
    if (task) task.completed = !task.completed;
  }

  deleteTask(id: number): void {
    this.tasks = this.tasks.filter(t => t.id !== id);
  }

  get completedCount(): number {
    return this.tasks.filter(t => t.completed).length;
  }

  get totalCount(): number {
    return this.tasks.length;
  }
}
