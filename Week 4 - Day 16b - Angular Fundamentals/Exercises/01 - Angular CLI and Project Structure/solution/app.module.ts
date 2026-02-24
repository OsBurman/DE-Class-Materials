import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';

// @NgModule is a decorator that marks a class as an Angular module and supplies
// configuration metadata: what components belong here, what to import, what to export,
// and which component bootstraps the application.
@NgModule({
  declarations: [
    AppComponent   // Components/directives/pipes that are part of this module
  ],
  imports: [
    BrowserModule  // Provides ngIf, ngFor, AsyncPipe, and browser-platform services
  ],
  providers: [],
  bootstrap: [
    AppComponent   // Angular renders this component into index.html's <app-root> tag
  ]
})
export class AppModule { }
