import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { UserPanelComponent } from './user-panel.component';
import { TaskListComponent } from './task-list.component';

@NgModule({
  declarations: [AppComponent, UserPanelComponent, TaskListComponent],
  imports: [BrowserModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
