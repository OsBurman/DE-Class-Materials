import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { RatingBadgeComponent } from './rating-badge.component';

// TODO: Add RatingBadgeComponent to the declarations array.
@NgModule({
  declarations: [
    AppComponent,
    // TODO: RatingBadgeComponent
  ],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
