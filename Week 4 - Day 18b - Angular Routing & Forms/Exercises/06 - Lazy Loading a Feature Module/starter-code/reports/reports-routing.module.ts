import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// TODO 2 – Import ReportsHomeComponent from './reports-home.component'

// TODO 3 – Define routes: const routes: Routes = [{ path: '', component: ReportsHomeComponent }];
const routes: Routes = [];

@NgModule({
  imports: [
    // TODO 4 – Add RouterModule.forChild(routes)
  ],
  exports: [RouterModule],
})
export class ReportsRoutingModule {}
