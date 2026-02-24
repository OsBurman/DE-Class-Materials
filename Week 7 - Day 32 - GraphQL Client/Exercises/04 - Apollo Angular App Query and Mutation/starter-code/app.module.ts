import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ApolloModule, APOLLO_OPTIONS } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';
import { InMemoryCache } from '@apollo/client/core';
import { AppComponent } from './app.component';

// TODO 1a: In the providers array, provide APOLLO_OPTIONS using useFactory.
//          The factory function receives HttpLink and should return:
//          { cache: new InMemoryCache(), link: httpLink.create({ uri: 'http://localhost:4000/graphql' }) }
//
//          Shape:
//          {
//            provide: APOLLO_OPTIONS,
//            useFactory: (httpLink: HttpLink) => ({ ... }),
//            deps: [HttpLink],
//          }

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    FormsModule,
    // TODO 1b: Add HttpClientModule and ApolloModule to the imports array
  ],
  providers: [
    // TODO 1a: Add APOLLO_OPTIONS provider here
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
