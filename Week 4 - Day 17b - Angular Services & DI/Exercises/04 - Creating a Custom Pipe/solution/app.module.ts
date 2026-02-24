import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { PipeDemoComponent } from './pipe-demo.component';
import { TruncatePipe } from './truncate.pipe';
import { PhoneFormatPipe } from './phone-format.pipe';

@NgModule({
  declarations: [AppComponent, PipeDemoComponent, TruncatePipe, PhoneFormatPipe],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
