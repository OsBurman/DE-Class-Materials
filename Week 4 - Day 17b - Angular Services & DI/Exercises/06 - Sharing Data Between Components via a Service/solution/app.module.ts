import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { NotificationSenderComponent } from './notification-sender.component';
import { NotificationBannerComponent } from './notification-banner.component';

@NgModule({
  declarations: [AppComponent, NotificationSenderComponent, NotificationBannerComponent],
  imports: [BrowserModule, FormsModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
