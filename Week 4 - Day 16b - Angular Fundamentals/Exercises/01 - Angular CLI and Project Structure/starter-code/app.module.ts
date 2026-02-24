import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';

// TODO 1: Add a one-sentence comment here explaining what @NgModule does.

@NgModule({
  declarations: [
    AppComponent   // Components, directives, and pipes that belong to this module
  ],
  imports: [
    // TODO 2: Add BrowserModule here.
    // BrowserModule provides browser-specific services and directives (ngIf, ngFor).
    // Every browser app must import it exactly once — in the root AppModule.
  ],
  providers: [],   // Services registered here are available application-wide
  bootstrap: [
    // TODO 3: Add AppComponent here.
    // The bootstrap array tells Angular which component to render into index.html.
    // Almost always just AppComponent.
  ]
})
export class AppModule { }
