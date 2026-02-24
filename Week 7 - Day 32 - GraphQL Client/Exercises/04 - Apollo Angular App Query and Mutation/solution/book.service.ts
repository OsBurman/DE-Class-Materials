import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';

export interface Author { id: string; name: string; }
export interface Book   { id: string; title: string; genre: string; year: number; author: Author; }

// Query definition — selects books with nested author
const GET_BOOKS = gql`
  query GetBooks {
    books {
      id
      title
      genre
      year
      author {
        id
        name
      }
    }
  }
`;

// Mutation definition — accepts four variables and returns the new book
const ADD_BOOK = gql`
  mutation AddBook($title: String!, $genre: String!, $year: Int!, $authorId: ID!) {
    addBook(title: $title, genre: $genre, year: $year, authorId: $authorId) {
      id
      title
      genre
      year
      author {
        id
        name
      }
    }
  }
`;

@Injectable({ providedIn: 'root' })
export class BookService {
  constructor(private apollo: Apollo) {}

  // watchQuery returns a QueryRef; valueChanges is an Observable that emits on each update
  getBooks() {
    return this.apollo.watchQuery<{ books: Book[] }>({ query: GET_BOOKS }).valueChanges;
  }

  // mutate returns a cold Observable — subscribe to trigger the request
  addBook(title: string, genre: string, year: number, authorId: string) {
    return this.apollo.mutate<{ addBook: Book }>({
      mutation: ADD_BOOK,
      variables: { title, genre, year, authorId },
    });
  }
}
