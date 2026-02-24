import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
// TODO 1 – Import ReactiveFormsModule from '@angular/forms'

import { AppComponent } from './app.component';
import { ContactFormComponent } from './contact-form.component';

@NgModule({
  declarations: [AppComponent, ContactFormComponent],
  imports: [
    BrowserModule,
    // TODO 1 – Add ReactiveFormsModule here
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
