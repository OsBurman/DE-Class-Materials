import { Routes } from '@angular/router';
import { DashboardComponent }  from './pages/dashboard/dashboard.component';
import { TaskListComponent }   from './pages/task-list/task-list.component';
import { TaskDetailComponent } from './pages/task-detail/task-detail.component';
import { TaskFormComponent }   from './pages/task-form/task-form.component';
import { NotFoundComponent }   from './pages/not-found/not-found.component';

export const routes: Routes = [
  { path: '',            component: DashboardComponent },
  { path: 'tasks',       component: TaskListComponent },
  { path: 'tasks/new',   component: TaskFormComponent },
  { path: 'tasks/:id/edit', component: TaskFormComponent },
  { path: 'tasks/:id',   component: TaskDetailComponent },
  { path: '**',          component: NotFoundComponent },
];
