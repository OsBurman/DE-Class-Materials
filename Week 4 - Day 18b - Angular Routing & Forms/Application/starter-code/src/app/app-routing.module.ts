import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// TODO Task 1: Import all page components and AuthGuard

// TODO Task 1: Define routes array
// const routes: Routes = [
//   { path: '',                component: HomeComponent },
//   { path: 'login',           component: LoginComponent },
//   { path: 'register',        component: RegisterComponent },
//   { path: 'profile/:username', component: ProfileComponent, canActivate: [AuthGuard] },
//   { path: '**', redirectTo: '' },
// ];

const routes: Routes = [
  // TODO: add routes here
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
