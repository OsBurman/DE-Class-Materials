// src/TaskManager.ts
import { Task, TaskId, NewTask, TaskUpdate, SortOption, Status, Priority } from './types';

/**
 * TODO Task 5: Implement the generic TaskManager class.
 *
 * The class should be generic: TaskManager<T extends Task>
 * This means it works with Task or any subtype of Task.
 *
 * Implement all methods listed in instructions.md with correct TypeScript types.
 */
export class TaskManager<T extends Task> {

  private tasks: T[] = [];
  private nextId: number = 1;

  // TODO: addTask(task: NewTask): T
  // Spread task, add id and createdAt, push to tasks, return the new task
  addTask(task: NewTask): T {
    const newTask = {
      ...task,
      id: this.nextId++,
      createdAt: new Date(),
    } as T;
    this.tasks.push(newTask);
    return newTask;
  }

  // TODO: getById(id: TaskId): T | undefined
  getById(id: TaskId): T | undefined {
    return undefined; // replace
  }

  // TODO: updateTask(id: TaskId, updates: TaskUpdate): T | null
  updateTask(id: TaskId, updates: TaskUpdate): T | null {
    return null; // replace
  }

  // TODO: deleteTask(id: TaskId): boolean
  deleteTask(id: TaskId): boolean {
    return false; // replace
  }

  // TODO: filterByStatus(status: Status): T[]
  filterByStatus(status: Status): T[] {
    return []; // replace
  }

  // TODO: filterByPriority(priority: Priority): T[]
  filterByPriority(priority: Priority): T[] {
    return []; // replace
  }

  // TODO: sortTasks(option: SortOption): T[]
  // option[0] = the key to sort by, option[1] = 'asc' or 'desc'
  sortTasks(option: SortOption): T[] {
    return []; // replace
  }

  // TODO: getStats(): { total: number; done: number; inProgress: number }
  getStats() {
    return { total: 0, done: 0, inProgress: 0 };
  }

  // TODO Task 6: Generic method — findFirst<K extends keyof T>(key: K, value: T[K]): T | undefined
  findFirst<K extends keyof T>(key: K, value: T[K]): T | undefined {
    return undefined; // replace
  }

  getAllTasks(): T[] { return [...this.tasks]; }
}
