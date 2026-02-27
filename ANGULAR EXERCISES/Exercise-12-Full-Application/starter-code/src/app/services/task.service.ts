import { Injectable, signal, computed, effect, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Task } from '../models/task.model';

const SEED_TASKS: Task[] = [
  { id: 1,  title: 'Set up Angular project',         description: 'Initialize with Angular CLI and configure routing.',        priority: 'high',   status: 'done',        dueDate: '2025-01-10', tags: ['setup', 'angular'],  createdAt: '2025-01-01T09:00:00Z' },
  { id: 2,  title: 'Design database schema',          description: 'Create ERD and finalize table structures.',                 priority: 'high',   status: 'done',        dueDate: '2025-01-12', tags: ['database', 'design'], createdAt: '2025-01-02T10:00:00Z' },
  { id: 3,  title: 'Build authentication module',     description: 'JWT login, registration, and guards.',                      priority: 'high',   status: 'in-progress', dueDate: '2025-01-20', tags: ['auth', 'security'],   createdAt: '2025-01-05T08:00:00Z' },
  { id: 4,  title: 'Write unit tests for services',  description: 'Achieve 80% coverage on TaskService and AuthService.',      priority: 'medium', status: 'todo',        dueDate: '2025-01-25', tags: ['testing'],             createdAt: '2025-01-06T11:00:00Z' },
  { id: 5,  title: 'Create dashboard UI',             description: 'Stat cards, recent tasks panel, and chart.',                priority: 'medium', status: 'in-progress', dueDate: '2025-01-22', tags: ['ui', 'dashboard'],    createdAt: '2025-01-07T14:00:00Z' },
  { id: 6,  title: 'Implement file upload',           description: 'Allow users to attach files to tasks using S3.',            priority: 'low',    status: 'todo',        dueDate: '2025-02-01', tags: ['feature', 's3'],      createdAt: '2025-01-08T09:30:00Z' },
  { id: 7,  title: 'Performance audit',               description: 'Run Lighthouse and fix Core Web Vitals issues.',            priority: 'medium', status: 'todo',        dueDate: '2025-02-05', tags: ['performance'],         createdAt: '2025-01-09T16:00:00Z' },
  { id: 8,  title: 'Deploy to production',            description: 'CI/CD pipeline on GitHub Actions + AWS ECS.',               priority: 'high',   status: 'todo',        dueDate: '2025-02-10', tags: ['devops', 'aws'],      createdAt: '2025-01-10T12:00:00Z' },
];

const STORAGE_KEY = 'taskflow_tasks';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private http = inject(HttpClient);

  // TODO Step 2a: Initialize tasks signal from localStorage or SEED_TASKS
  // Hint: JSON.parse(localStorage.getItem(STORAGE_KEY) ?? 'null') || SEED_TASKS
  tasks = signal<Task[]>(SEED_TASKS);
  private nextId = signal(100);

  // TODO Step 2b: Add computed signals for dashboard stats
  // totalCount = computed(() => ...)
  // todoCount   = computed(() => this.tasks().filter(t => t.status === 'todo').length)
  // inProgressCount = computed(() => ...)
  // doneCount   = computed(() => ...)
  // recentTasks = computed(() => [...this.tasks()].sort(...).slice(0, 5))

  constructor() {
    // TODO Step 2c: Use effect() to persist this.tasks() to localStorage on every change
    // effect(() => { localStorage.setItem(STORAGE_KEY, JSON.stringify(this.tasks())); });

    // TODO Step 2d: Load 5 todos from JSONPlaceholder and append to tasks
    // this.http.get<any[]>('https://jsonplaceholder.typicode.com/todos?_limit=5').subscribe(...)
    // Map each todo to Task shape: { id: 200 + index, title: todo.title, status: todo.completed ? 'done' : 'todo', priority: 'low', description: '', dueDate: '', tags: [], createdAt: new Date().toISOString() }
  }

  getById(id: number): Task | undefined {
    return this.tasks().find(t => t.id === id);
  }

  // TODO Step 2e: Implement addTask(task: Omit<Task, 'id' | 'createdAt'>): void
  //   Assign a new id with this.nextId(), increment nextId, push to signal
  addTask(task: Omit<Task, 'id' | 'createdAt'>): void {
    // your code here
  }

  // TODO Step 2f: Implement updateTask(id: number, partial: Partial<Task>): void
  //   Use this.tasks.update(tasks => tasks.map(t => t.id === id ? { ...t, ...partial } : t))
  updateTask(id: number, partial: Partial<Task>): void {
    // your code here
  }

  // TODO Step 2g: Implement deleteTask(id: number): void
  //   Use this.tasks.update(tasks => tasks.filter(t => t.id !== id))
  deleteTask(id: number): void {
    // your code here
  }
}
