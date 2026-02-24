import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { RegistrationComponent } from './registration.component';

// FormsModule is required for template-driven forms: it provides NgModel, NgForm, etc.
@NgModule({
  declarations: [AppComponent, RegistrationComponent],
  imports: [BrowserModule, FormsModule],
  bootstrap: [AppComponent],
})
export class AppModule {}
