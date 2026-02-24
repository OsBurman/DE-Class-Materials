import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

// TODO 1 – Import RouterModule and Routes from '@angular/router'

import { AppComponent } from './app.component';
import { DashboardLayoutComponent } from './dashboard-layout.component';
import { OverviewComponent } from './overview.component';
import { AnalyticsComponent } from './analytics.component';
import { SettingsComponent } from './settings.component';

// TODO 2 – Define a Routes array called `routes`:
//   { path: '', redirectTo: '/dashboard', pathMatch: 'full' }
//   {
//     path: 'dashboard',
//     component: DashboardLayoutComponent,
//     children: [
//       { path: '',          component: OverviewComponent },
//       { path: 'analytics', component: AnalyticsComponent },
//       { path: 'settings',  component: SettingsComponent },
//     ]
//   }

@NgModule({
  declarations: [
    AppComponent,
    DashboardLayoutComponent,
    OverviewComponent,
    AnalyticsComponent,
    SettingsComponent,
  ],
  imports: [
    BrowserModule,
    // TODO 3 – Add RouterModule.forRoot(routes)
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
