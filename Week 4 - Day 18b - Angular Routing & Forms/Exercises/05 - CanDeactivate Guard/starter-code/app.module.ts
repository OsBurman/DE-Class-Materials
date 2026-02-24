import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { EditProfileComponent } from './edit-profile.component';
// TODO 7 – Import unsavedChangesGuard from './unsaved-changes.guard'

const routes: Routes = [
  { path: '',             redirectTo: '/edit-profile', pathMatch: 'full' },
  // TODO 7 – Add canDeactivate: [unsavedChangesGuard] to this route
  { path: 'edit-profile', component: EditProfileComponent },
];

@NgModule({
  declarations: [AppComponent, EditProfileComponent],
  imports: [BrowserModule, RouterModule.forRoot(routes)],
  bootstrap: [AppComponent],
})
export class AppModule {}
