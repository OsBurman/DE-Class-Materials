import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { SalesReportComponent } from './sales-report.component';

// TODO: Add SalesReportComponent to the declarations array.
@NgModule({
  declarations: [
    AppComponent,
    // TODO: SalesReportComponent
  ],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
