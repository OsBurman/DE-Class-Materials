import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { EmulatedCardComponent } from './emulated-card.component';
import { NoneCardComponent } from './none-card.component';
import { ShadowCardComponent } from './shadow-card.component';

@NgModule({
  declarations: [AppComponent, EmulatedCardComponent, NoneCardComponent, ShadowCardComponent],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
