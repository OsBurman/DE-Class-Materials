import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';

export interface Author { id: string; name: string; }
export interface Book   { id: string; title: string; genre: string; year: number; author: Author; }

// TODO 3a: Define GET_BOOKS using gql — query books with id, title, genre, year, author { id name }
const GET_BOOKS = gql`
  # TODO 3a: Write the GetBooks query here
`;

// TODO 3b: Define ADD_BOOK using gql — mutation with variables $title, $genre, $year, $authorId
//          Return id, title, genre, year, author { id name }
const ADD_BOOK = gql`
  # TODO 3b: Write the AddBook mutation here
`;

@Injectable({ providedIn: 'root' })
export class BookService {
  constructor(private apollo: Apollo) {}

  // TODO 2a: Implement getBooks() — call this.apollo.watchQuery<{ books: Book[] }>({ query: GET_BOOKS })
  //          and return the .valueChanges Observable
  getBooks() {
    // TODO 2a: return this.apollo.watchQuery<{ books: Book[] }>({ query: GET_BOOKS }).valueChanges;
    return null as any;
  }

  // TODO 2b: Implement addBook() — call this.apollo.mutate<{ addBook: Book }>({ mutation: ADD_BOOK, variables: {...} })
  //          and return the Observable
  addBook(title: string, genre: string, year: number, authorId: string) {
    // TODO 2b: return this.apollo.mutate<{ addBook: Book }>({ mutation: ADD_BOOK, variables: { title, genre, year, authorId } });
    return null as any;
  }
}
