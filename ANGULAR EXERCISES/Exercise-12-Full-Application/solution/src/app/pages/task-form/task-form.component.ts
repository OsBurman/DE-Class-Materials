import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.css',
})
export class TaskFormComponent implements OnInit {
  @Input() id?: string;

  private fb = inject(FormBuilder);
  private taskService = inject(TaskService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  get isEditMode() { return !!this.id; }

  form = this.fb.group({
    title:       ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    priority:    ['medium' as const, Validators.required],
    status:      ['todo' as const, Validators.required],
    dueDate:     ['', Validators.required],
    tags:        [''],
  });

  ngOnInit() {
    if (this.isEditMode) {
      const task = this.taskService.getById(+this.id!);
      if (task) {
        this.form.patchValue({
          title:       task.title,
          description: task.description,
          priority:    task.priority,
          status:      task.status,
          dueDate:     task.dueDate,
          tags:        task.tags.join(', '),
        });
      }
    }
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const raw = this.form.getRawValue();
    const tags = (raw.tags ?? '').split(',').map(t => t.trim()).filter(Boolean);

    const taskData = {
      title:       raw.title!,
      description: raw.description ?? '',
      priority:    raw.priority as any,
      status:      raw.status as any,
      dueDate:     raw.dueDate!,
      tags,
    };

    if (this.isEditMode) {
      this.taskService.updateTask(+this.id!, taskData);
      this.notificationService.send({ type: 'success', message: `✅ "${taskData.title}" updated!` });
    } else {
      this.taskService.addTask(taskData);
      this.notificationService.send({ type: 'success', message: `✅ "${taskData.title}" created!` });
    }

    this.router.navigate(['/tasks']);
  }

  cancel() { this.router.navigate(['/tasks']); }

  fieldError(name: string, error: string) {
    const ctrl = this.form.get(name);
    return ctrl?.touched && ctrl.hasError(error);
  }
}
