import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReportsHomeComponent } from './reports-home.component';

// forChild() registers routes scoped to this feature module.
// forRoot() must only be called once in AppModule.
const routes: Routes = [
  { path: '', component: ReportsHomeComponent }, // matches '/reports' exactly
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReportsRoutingModule {}
