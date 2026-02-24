import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
// TODO: import HTTP_INTERCEPTORS from '@angular/common/http'
import { AuthInterceptor } from './auth.interceptor';

import { AppComponent } from './app.component';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, HttpClientModule],
  providers: [
    // TODO: register AuthInterceptor using HTTP_INTERCEPTORS with multi: true
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
