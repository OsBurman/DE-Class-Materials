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
  template: `
    <div class="task-form">
      <h1>{{ isEditMode ? 'Edit Task' : 'New Task' }}</h1>

      <!-- TODO: Reactive form -->
      <!-- Fields: title, description, priority (low/medium/high), status (todo/in-progress/done), dueDate, tags (comma-separated) -->
      <!-- In edit mode, pre-fill with existing task values -->
      <!-- Submit: create or update task, send notification, navigate to /tasks -->
      <!-- Cancel: navigate back without saving -->

      <form [formGroup]="form" (ngSubmit)="submit()">
        <p>Build your form fields here.</p>
        <button type="submit" [disabled]="form.invalid">{{ isEditMode ? 'Update' : 'Create' }}</button>
        <button type="button" (click)="cancel()">Cancel</button>
      </form>
    </div>
  `
})
export class TaskFormComponent implements OnInit {
  @Input() id?: string;    // present in edit mode

  private fb = inject(FormBuilder);
  private taskService = inject(TaskService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  get isEditMode() { return !!this.id; }

  // TODO: Build reactive form with FormBuilder
  form = this.fb.group({
    title:       ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    priority:    ['medium' as const, Validators.required],
    status:      ['todo' as const, Validators.required],
    dueDate:     ['', Validators.required],
    tags:        [''],   // comma-separated, convert to/from array on submit/fill
  });

  ngOnInit() {
    if (this.isEditMode) {
      // TODO: Find task by +this.id!, patch form values (join tags array to string)
    }
  }

  submit() {
    if (this.form.invalid) return;
    // TODO: Convert tags string → string[] (split by comma, trim, filter empty)
    // TODO: If edit mode → taskService.updateTask(), else → taskService.addTask()
    // TODO: notificationService.send({ type: 'success', message: '...' })
    // TODO: this.router.navigate(['/tasks'])
  }

  cancel() { this.router.navigate(['/tasks']); }
}
