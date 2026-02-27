import { Injectable, signal, computed, effect, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Task } from '../models/task.model';

const SEED_TASKS: Task[] = [
  { id: 1,  title: 'Set up Angular project',        description: 'Initialize with Angular CLI and configure routing.',       priority: 'high',   status: 'done',        dueDate: '2025-01-10', tags: ['setup', 'angular'],  createdAt: '2025-01-01T09:00:00Z' },
  { id: 2,  title: 'Design database schema',         description: 'Create ERD and finalize table structures.',                priority: 'high',   status: 'done',        dueDate: '2025-01-12', tags: ['database', 'design'], createdAt: '2025-01-02T10:00:00Z' },
  { id: 3,  title: 'Build authentication module',    description: 'JWT login, registration, and guards.',                     priority: 'high',   status: 'in-progress', dueDate: '2025-01-20', tags: ['auth', 'security'],   createdAt: '2025-01-05T08:00:00Z' },
  { id: 4,  title: 'Write unit tests for services', description: 'Achieve 80% coverage on TaskService and AuthService.',     priority: 'medium', status: 'todo',        dueDate: '2025-01-25', tags: ['testing'],             createdAt: '2025-01-06T11:00:00Z' },
  { id: 5,  title: 'Create dashboard UI',            description: 'Stat cards, recent tasks panel, and chart.',               priority: 'medium', status: 'in-progress', dueDate: '2025-01-22', tags: ['ui', 'dashboard'],    createdAt: '2025-01-07T14:00:00Z' },
  { id: 6,  title: 'Implement file upload',          description: 'Allow users to attach files to tasks using S3.',           priority: 'low',    status: 'todo',        dueDate: '2025-02-01', tags: ['feature', 's3'],      createdAt: '2025-01-08T09:30:00Z' },
  { id: 7,  title: 'Performance audit',              description: 'Run Lighthouse and fix Core Web Vitals issues.',           priority: 'medium', status: 'todo',        dueDate: '2025-02-05', tags: ['performance'],         createdAt: '2025-01-09T16:00:00Z' },
  { id: 8,  title: 'Deploy to production',           description: 'CI/CD pipeline on GitHub Actions + AWS ECS.',              priority: 'high',   status: 'todo',        dueDate: '2025-02-10', tags: ['devops', 'aws'],      createdAt: '2025-01-10T12:00:00Z' },
];

const STORAGE_KEY = 'taskflow_tasks';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private http = inject(HttpClient);

  tasks = signal<Task[]>(
    JSON.parse(localStorage.getItem(STORAGE_KEY) ?? 'null') ?? SEED_TASKS
  );

  private nextId = signal(200);

  totalCount      = computed(() => this.tasks().length);
  todoCount       = computed(() => this.tasks().filter(t => t.status === 'todo').length);
  inProgressCount = computed(() => this.tasks().filter(t => t.status === 'in-progress').length);
  doneCount       = computed(() => this.tasks().filter(t => t.status === 'done').length);
  recentTasks     = computed(() =>
    [...this.tasks()].sort((a, b) => b.createdAt.localeCompare(a.createdAt)).slice(0, 5)
  );

  constructor() {
    effect(() => {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.tasks()));
    });

    this.http.get<any[]>('https://jsonplaceholder.typicode.com/todos?_limit=5').subscribe({
      next: todos => {
        const mapped: Task[] = todos.map((t, i) => ({
          id: 200 + i,
          title: t.title,
          description: 'Imported from JSONPlaceholder',
          priority: 'low',
          status: t.completed ? 'done' : 'todo',
          dueDate: '',
          tags: ['imported'],
          createdAt: new Date().toISOString(),
        }));
        this.tasks.update(existing => [...existing, ...mapped]);
      }
    });
  }

  getById(id: number): Task | undefined {
    return this.tasks().find(t => t.id === id);
  }

  addTask(task: Omit<Task, 'id' | 'createdAt'>): void {
    const newTask: Task = { ...task, id: this.nextId(), createdAt: new Date().toISOString() };
    this.nextId.update(n => n + 1);
    this.tasks.update(tasks => [...tasks, newTask]);
  }

  updateTask(id: number, partial: Partial<Task>): void {
    this.tasks.update(tasks => tasks.map(t => t.id === id ? { ...t, ...partial } : t));
  }

  deleteTask(id: number): void {
    this.tasks.update(tasks => tasks.filter(t => t.id !== id));
  }
}
