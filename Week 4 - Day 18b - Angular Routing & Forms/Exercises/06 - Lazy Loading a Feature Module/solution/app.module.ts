import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
// ReportsModule is NOT imported here — the loadChildren callback fetches it on demand.

const routes: Routes = [
  { path: '',        redirectTo: '/home', pathMatch: 'full' },
  { path: 'home',    component: AppComponent },
  // loadChildren returns a Promise that resolves to the feature NgModule class.
  // Angular's build tooling (webpack/esbuild) automatically splits this into a separate chunk.
  {
    path: 'reports',
    loadChildren: () =>
      import('./reports/reports.module').then(m => m.ReportsModule),
  },
];

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, RouterModule.forRoot(routes)],
  bootstrap: [AppComponent],
})
export class AppModule {}
