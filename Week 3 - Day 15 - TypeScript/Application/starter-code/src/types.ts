// src/types.ts
// TODO Task 1: Define Priority and Status enums
// export enum Priority { LOW = "LOW", MEDIUM = "MEDIUM", HIGH = "HIGH" }
// export enum Status { TODO = "TODO", IN_PROGRESS = "IN_PROGRESS", DONE = "DONE" }


// TODO Task 2: Define the Task interface
// export interface Task {
//   id: number;
//   title: string;
//   description?: string;    // optional field
//   priority: Priority;
//   status: Status;
//   tags: string[];
//   createdAt: Date;
//   dueDate: Date | null;    // union type: Date or null
// }


// TODO Task 3: Define type aliases
// export type TaskId = number;
// export type FilterCriteria = "all" | "active" | "completed";
// export type TaskSummary = Pick<Task, "id" | "title" | "status">;
// export type NewTask = Omit<Task, "id" | "createdAt">;
// export type TaskUpdate = Partial<Task>;


// TODO Task 4: Define SortOption tuple type
// export type SortOption = [keyof Task, "asc" | "desc"];
