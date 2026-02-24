import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { ManualUnsubscribeComponent } from './manual-unsubscribe.component';
import { AsyncPipeComponent } from './async-pipe.component';

@NgModule({
  declarations: [AppComponent, ManualUnsubscribeComponent, AsyncPipeComponent],
  imports: [BrowserModule],
  bootstrap: [AppComponent],
})
export class AppModule {}
