import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { BookListComponent } from './book-list.component';

// TODO: Add BookListComponent to declarations and FormsModule to imports.
@NgModule({
  declarations: [
    AppComponent,
    // TODO: BookListComponent
  ],
  imports: [
    BrowserModule,
    // TODO: FormsModule  (required for [(ngModel)])
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
