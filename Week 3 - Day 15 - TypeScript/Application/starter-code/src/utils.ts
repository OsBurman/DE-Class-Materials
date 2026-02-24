// src/utils.ts
import { Task, Priority } from './types';

// TODO Task 7: isHighPriority — type guard
// export function isHighPriority(task: Task): task is Task & { priority: Priority.HIGH } {
//   return task.priority === Priority.HIGH;
// }

// TODO Task 7: hasDeadline — type guard
// export function hasDeadline(task: Task): task is Task & { dueDate: Date } {
//   return task.dueDate !== null;
// }

// TODO Task 8: createReadonlyTask — use Readonly<Task>
// export function createReadonlyTask(task: Task): Readonly<Task> {
//   return Object.freeze({ ...task });
// }

// TODO Task 8: getRequiredFields — use Required<Pick<Task, "title" | "priority">>
// export function getRequiredFields(task: Partial<Task>): Required<Pick<Task, "title" | "priority">> {
//   if (!task.title || !task.priority) throw new Error("title and priority are required");
//   return { title: task.title, priority: task.priority };
// }
