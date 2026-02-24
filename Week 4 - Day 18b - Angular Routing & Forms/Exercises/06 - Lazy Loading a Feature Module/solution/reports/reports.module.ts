import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportsRoutingModule } from './reports-routing.module';
import { ReportsHomeComponent } from './reports-home.component';

@NgModule({
  declarations: [ReportsHomeComponent],
  imports: [CommonModule, ReportsRoutingModule],
})
export class ReportsModule {}
