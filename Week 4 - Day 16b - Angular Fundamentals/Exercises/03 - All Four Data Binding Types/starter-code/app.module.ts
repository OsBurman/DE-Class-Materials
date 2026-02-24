import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';  // Required for [(ngModel)]
import { AppComponent } from './app.component';
import { BindingDemoComponent } from './binding-demo.component';

// TODO 11: Add FormsModule to the imports array — ngModel won't work without it.
// TODO 12: Add BindingDemoComponent to the declarations array.
@NgModule({
  declarations: [
    AppComponent,
    // TODO: BindingDemoComponent
  ],
  imports: [
    BrowserModule,
    // TODO: FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
