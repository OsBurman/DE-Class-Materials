import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';  // Required for [(ngModel)]
import { AppComponent } from './app.component';
import { BindingDemoComponent } from './binding-demo.component';

@NgModule({
  declarations: [
    AppComponent,
    BindingDemoComponent
  ],
  imports: [
    BrowserModule,
    FormsModule    // FormsModule provides the NgModel directive used by [(ngModel)]
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
