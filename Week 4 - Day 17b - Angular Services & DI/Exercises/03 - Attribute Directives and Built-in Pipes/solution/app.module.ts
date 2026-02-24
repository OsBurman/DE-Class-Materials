import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { SalesReportComponent } from './sales-report.component';

@NgModule({
  declarations: [AppComponent, SalesReportComponent],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
