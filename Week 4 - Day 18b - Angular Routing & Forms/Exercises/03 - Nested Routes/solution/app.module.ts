import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { DashboardLayoutComponent } from './dashboard-layout.component';
import { OverviewComponent } from './overview.component';
import { AnalyticsComponent } from './analytics.component';
import { SettingsComponent } from './settings.component';

// The parent route supplies a component (DashboardLayoutComponent) that acts as a layout
// shell. Its `children` array defines what renders inside the parent's <router-outlet>.
const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    component: DashboardLayoutComponent,
    children: [
      { path: '',          component: OverviewComponent   }, // default child
      { path: 'analytics', component: AnalyticsComponent  },
      { path: 'settings',  component: SettingsComponent   },
    ],
  },
];

@NgModule({
  declarations: [
    AppComponent,
    DashboardLayoutComponent,
    OverviewComponent,
    AnalyticsComponent,
    SettingsComponent,
  ],
  imports: [BrowserModule, RouterModule.forRoot(routes)],
  bootstrap: [AppComponent],
})
export class AppModule {}
