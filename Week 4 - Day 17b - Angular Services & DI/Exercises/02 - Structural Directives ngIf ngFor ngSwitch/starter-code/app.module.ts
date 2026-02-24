import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { TaskDashboardComponent } from './task-dashboard.component';

// TODO: Add TaskDashboardComponent to the declarations array.
@NgModule({
  declarations: [
    AppComponent,
    // TODO: TaskDashboardComponent
  ],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
