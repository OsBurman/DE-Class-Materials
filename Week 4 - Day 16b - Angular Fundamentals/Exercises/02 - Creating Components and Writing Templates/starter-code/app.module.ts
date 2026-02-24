import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
// TODO 10: Import ProductCardComponent from './product-card.component'

@NgModule({
  declarations: [
    AppComponent,
    // TODO 11: Add ProductCardComponent to the declarations array
  ],
  imports: [BrowserModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
