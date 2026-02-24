import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { CounterDisplayComponent } from './counter-display.component';
import { CounterControlsComponent } from './counter-controls.component';

@NgModule({
  declarations: [AppComponent, CounterDisplayComponent, CounterControlsComponent],
  imports: [BrowserModule],
  bootstrap: [AppComponent],
})
export class AppModule {}
