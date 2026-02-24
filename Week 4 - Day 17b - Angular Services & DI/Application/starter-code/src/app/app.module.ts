// app.module.ts — Product Catalog
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { ProductCardComponent } from './components/product-card/product-card.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import { CartSidebarComponent } from './components/cart-sidebar/cart-sidebar.component';
import { CurrencyFormatPipe } from './pipes/currency-format.pipe';

@NgModule({
  declarations: [
    AppComponent,
    ProductCardComponent,
    ProductListComponent,
    CartSidebarComponent,
    CurrencyFormatPipe,
  ],
  imports: [BrowserModule, FormsModule],
  bootstrap: [AppComponent]
})
export class AppModule {}
