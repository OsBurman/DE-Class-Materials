import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ApolloModule, APOLLO_OPTIONS } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';
import { InMemoryCache } from '@apollo/client/core';
import { AppComponent } from './app.component';

// APOLLO_OPTIONS factory — called once at bootstrap to configure the Apollo Client instance.
// HttpLink serializes GraphQL operations as HTTP POST requests.
// InMemoryCache normalizes and caches query results.
function apolloOptionsFactory(httpLink: HttpLink) {
  return {
    cache: new InMemoryCache(),
    link: httpLink.create({ uri: 'http://localhost:4000/graphql' }),
  };
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    FormsModule,       // required for [(ngModel)]
    HttpClientModule,  // required for HttpLink to send HTTP requests
    ApolloModule,      // registers Apollo service in the DI container
  ],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: apolloOptionsFactory,
      deps: [HttpLink],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
