import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { ContactFormComponent } from './contact-form.component';

// ReactiveFormsModule provides FormGroup, FormControl, FormBuilder, and the
// [formGroup] / formControlName directives used in reactive form templates.
@NgModule({
  declarations: [AppComponent, ContactFormComponent],
  imports: [BrowserModule, ReactiveFormsModule],
  bootstrap: [AppComponent],
})
export class AppModule {}
