import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { NewsFeedComponent } from './components/news-feed/news-feed.component';
import { ArticleCardComponent } from './components/article-card/article-card.component';
import { CategoryFilterComponent } from './components/category-filter/category-filter.component';
// TODO Task 1: Import ApiKeyInterceptor
// import { ApiKeyInterceptor } from './interceptors/api-key.interceptor';

@NgModule({
  declarations: [AppComponent, NewsFeedComponent, ArticleCardComponent, CategoryFilterComponent],
  imports: [BrowserModule, HttpClientModule, FormsModule],
  providers: [
    // TODO Task 1: Register the interceptor:
    // { provide: HTTP_INTERCEPTORS, useClass: ApiKeyInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
