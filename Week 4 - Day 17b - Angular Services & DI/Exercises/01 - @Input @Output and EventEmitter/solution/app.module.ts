import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { RatingBadgeComponent } from './rating-badge.component';

@NgModule({
  declarations: [
    AppComponent,
    RatingBadgeComponent   // child component must be declared in the same module
  ],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
