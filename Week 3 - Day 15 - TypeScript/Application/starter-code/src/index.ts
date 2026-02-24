// src/index.ts — Entry point
import { TaskManager } from './TaskManager';
import { Priority, Status, NewTask } from './types';
import { isHighPriority, hasDeadline } from './utils';

const manager = new TaskManager();

// TODO: Add 5 tasks using manager.addTask()
// Example:
// const task1 = manager.addTask({
//   title: "Set up project repo",
//   description: "Initialize Git, create branches",
//   priority: Priority.HIGH,
//   status: Status.DONE,
//   tags: ["setup", "git"],
//   dueDate: new Date("2026-03-01"),
// });

// TODO: Call every method and print results
// manager.getById(1)
// manager.updateTask(1, { status: Status.IN_PROGRESS })
// manager.filterByStatus(Status.DONE)
// manager.filterByPriority(Priority.HIGH)
// manager.sortTasks(["priority", "asc"])
// manager.getStats()
// manager.findFirst("title", "Set up project repo")

// TODO: Use type guards
// manager.getAllTasks().forEach(task => {
//   if (isHighPriority(task)) console.log("HIGH PRIORITY:", task.title);
//   if (hasDeadline(task)) console.log("Has deadline:", task.dueDate);
// });

console.log("Task Manager initialized. Complete the TODOs!");
