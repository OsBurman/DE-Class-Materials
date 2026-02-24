import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { ProductListComponent } from './product-list.component';
import { ProductDetailComponent } from './product-detail.component';

// '' redirects to the list; ':id' captures any dynamic segment after /products/
const routes: Routes = [
  { path: '',              redirectTo: '/products', pathMatch: 'full' },
  { path: 'products',     component: ProductListComponent },
  { path: 'products/:id', component: ProductDetailComponent },
];

@NgModule({
  declarations: [AppComponent, ProductListComponent, ProductDetailComponent],
  imports: [BrowserModule, RouterModule.forRoot(routes)],
  bootstrap: [AppComponent],
})
export class AppModule {}
