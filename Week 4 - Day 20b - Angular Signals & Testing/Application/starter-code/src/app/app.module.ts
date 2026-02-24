import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { CounterPanelComponent } from './components/counter-panel/counter-panel.component';
import { StatsPanelComponent } from './components/stats-panel/stats-panel.component';

@NgModule({
  declarations: [AppComponent, CounterPanelComponent, StatsPanelComponent],
  imports: [BrowserModule],
  bootstrap: [AppComponent]
})
export class AppModule {}
