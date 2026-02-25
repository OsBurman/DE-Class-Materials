import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent, TemplateFormComponent, ReactiveFormComponent,
         LoginPageComponent, ProtectedPageComponent, authGuard } from './app.component';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter([
      { path: '',          component: TemplateFormComponent },
      { path: 'reactive',  component: ReactiveFormComponent },
      { path: 'protected', component: ProtectedPageComponent, canActivate: [authGuard] },
      { path: 'login',     component: LoginPageComponent },
      { path: '**',        redirectTo: '' },
    ]),
    provideHttpClient(),
  ]
};
