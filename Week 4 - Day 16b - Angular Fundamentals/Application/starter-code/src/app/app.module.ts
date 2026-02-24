import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { TeamMemberCardComponent } from './team-member-card/team-member-card.component';
import { TeamListComponent } from './team-list/team-list.component';

@NgModule({
  declarations: [
    AppComponent,
    TeamMemberCardComponent,
    TeamListComponent
  ],
  imports: [
    BrowserModule,
    FormsModule   // Required for [(ngModel)]
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
