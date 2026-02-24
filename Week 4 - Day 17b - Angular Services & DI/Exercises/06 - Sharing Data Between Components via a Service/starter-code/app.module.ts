import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { NotificationSenderComponent } from './notification-sender.component';
import { NotificationBannerComponent } from './notification-banner.component';

// TODO: Add both notification components to declarations and FormsModule to imports.
@NgModule({
  declarations: [
    AppComponent,
    // TODO: NotificationSenderComponent
    // TODO: NotificationBannerComponent
  ],
  imports: [
    BrowserModule,
    // TODO: FormsModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
