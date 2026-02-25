import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import {
  HomeComponent, CourseListComponent, CourseDetailComponent,
  SyllabusComponent, StudentsTabComponent, NotFoundComponent
} from './app.component';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter([
      { path: '',          component: HomeComponent },
      { path: 'courses',   component: CourseListComponent },
      { path: 'courses/:id', component: CourseDetailComponent, children: [
          { path: 'syllabus', component: SyllabusComponent },
          { path: 'students', component: StudentsTabComponent },
      ]},
      { path: 'about', component: HomeComponent },
      { path: '**',        component: NotFoundComponent },
    ]),
    provideHttpClient(),
  ]
};
