import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
// TODO: import HttpClientModule from '@angular/common/http'

import { AppComponent } from './app.component';
import { PostListComponent } from './post-list.component';

@NgModule({
  declarations: [AppComponent, PostListComponent],
  imports: [
    BrowserModule,
    // TODO: add HttpClientModule here
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
