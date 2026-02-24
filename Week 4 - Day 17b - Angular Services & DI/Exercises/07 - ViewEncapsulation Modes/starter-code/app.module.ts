import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { EmulatedCardComponent } from './emulated-card.component';
import { NoneCardComponent } from './none-card.component';
import { ShadowCardComponent } from './shadow-card.component';

// TODO: Add all three card components to declarations.
@NgModule({
  declarations: [
    AppComponent,
    // TODO: EmulatedCardComponent
    // TODO: NoneCardComponent
    // TODO: ShadowCardComponent
  ],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
