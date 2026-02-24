import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

// TODO 1 – Import RouterModule and Routes from '@angular/router'

import { AppComponent } from './app.component';
import { ProductListComponent } from './product-list.component';
import { ProductDetailComponent } from './product-detail.component';

// TODO 2 – Define a Routes array called `routes`:
//   { path: '',            redirectTo: '/products', pathMatch: 'full' }
//   { path: 'products',    component: ProductListComponent }
//   { path: 'products/:id', component: ProductDetailComponent }

@NgModule({
  declarations: [AppComponent, ProductListComponent, ProductDetailComponent],
  imports: [
    BrowserModule,
    // TODO 3 – Add RouterModule.forRoot(routes)
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
