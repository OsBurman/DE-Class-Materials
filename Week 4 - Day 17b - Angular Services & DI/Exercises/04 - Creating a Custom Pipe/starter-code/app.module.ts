import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { PipeDemoComponent } from './pipe-demo.component';
import { TruncatePipe } from './truncate.pipe';
import { PhoneFormatPipe } from './phone-format.pipe';

// TODO: Add PipeDemoComponent, TruncatePipe, and PhoneFormatPipe to declarations.
@NgModule({
  declarations: [
    AppComponent,
    // TODO: PipeDemoComponent
    // TODO: TruncatePipe
    // TODO: PhoneFormatPipe
  ],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
