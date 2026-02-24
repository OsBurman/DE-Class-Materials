import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { EditProfileComponent } from './edit-profile.component';
import { unsavedChangesGuard } from './unsaved-changes.guard';

const routes: Routes = [
  { path: '',             redirectTo: '/edit-profile', pathMatch: 'full' },
  {
    path: 'edit-profile',
    component: EditProfileComponent,
    // canDeactivate runs when the user tries to navigate AWAY from this route.
    // The guard receives the component instance and calls component.canDeactivate().
    canDeactivate: [unsavedChangesGuard],
  },
];

@NgModule({
  declarations: [AppComponent, EditProfileComponent],
  imports: [BrowserModule, RouterModule.forRoot(routes)],
  bootstrap: [AppComponent],
})
export class AppModule {}
