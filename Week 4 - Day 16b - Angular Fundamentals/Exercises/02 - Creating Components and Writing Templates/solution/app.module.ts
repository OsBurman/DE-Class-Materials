import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { ProductCardComponent } from './product-card.component';

// Every component used in a template must be declared in exactly one NgModule.
// Angular won't recognise <app-product-card> unless ProductCardComponent is declared here.
@NgModule({
  declarations: [
    AppComponent,
    ProductCardComponent
  ],
  imports: [BrowserModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
