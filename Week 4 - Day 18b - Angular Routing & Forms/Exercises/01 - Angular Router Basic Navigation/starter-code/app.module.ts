// app.module.ts  (starter)
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
// TODO 1: Import RouterModule and Routes from '@angular/router'.

import { AppComponent } from './app.component';
import { HomeComponent } from './home.component';
import { AboutComponent } from './about.component';
import { ContactComponent } from './contact.component';
import { NotFoundComponent } from './not-found.component';

// TODO 2: Define a `routes` array of type Routes with four entries:
//   { path: '',        component: HomeComponent }
//   { path: 'about',   component: AboutComponent }
//   { path: 'contact', component: ContactComponent }
//   { path: '**',      component: NotFoundComponent }   ← must be last
const routes = []; // replace with typed Routes array

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AboutComponent,
    ContactComponent,
    NotFoundComponent,
  ],
  imports: [
    BrowserModule,
    // TODO 3: Add RouterModule.forRoot(routes) here.
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
