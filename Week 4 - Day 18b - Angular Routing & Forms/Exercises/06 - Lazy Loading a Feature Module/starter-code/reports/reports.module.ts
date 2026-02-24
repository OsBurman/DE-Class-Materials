import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportsHomeComponent } from './reports-home.component';
// TODO 5 – Import ReportsRoutingModule from './reports-routing.module'

@NgModule({
  declarations: [ReportsHomeComponent],
  imports: [
    CommonModule,
    // TODO 5 – Add ReportsRoutingModule here
  ],
})
export class ReportsModule {}
