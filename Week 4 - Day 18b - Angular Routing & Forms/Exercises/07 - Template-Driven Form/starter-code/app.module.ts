import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
// TODO 1 – Import FormsModule from '@angular/forms'

import { AppComponent } from './app.component';
import { RegistrationComponent } from './registration.component';

@NgModule({
  declarations: [AppComponent, RegistrationComponent],
  imports: [
    BrowserModule,
    // TODO 1 – Add FormsModule here
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
