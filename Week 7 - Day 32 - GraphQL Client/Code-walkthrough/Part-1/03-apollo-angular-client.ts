// =============================================================================
// Day 32 — GraphQL Client: Apollo Angular
// Bookstore Application — Full Angular example
//
// Topics covered:
//   1. Apollo Angular setup (GraphQLModule)
//   2. Consuming GraphQL APIs from Angular components
//   3. Query execution with apollo.watchQuery (Observable)
//   4. Response handling with async pipe
//   5. Mutations from Angular components
//   6. Error handling in GraphQL (network errors vs GraphQL errors)
// =============================================================================

// =============================================================================
// FILE: src/app/graphql.module.ts
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 1: Apollo Angular Setup
// ──────────────────────────────────────────────────────────────────────────────
import { NgModule } from '@angular/core';
import { APOLLO_OPTIONS, ApolloModule } from 'apollo-angular';
import { ApolloClientOptions, ApolloLink, InMemoryCache } from '@apollo/client/core';
import { HttpLink } from 'apollo-angular/http';
import { onError } from '@apollo/client/link/error';

// Global error link — intercepts all GraphQL + network errors
function createErrorLink() {
  return onError(({ graphQLErrors, networkError, operation }) => {
    if (graphQLErrors) {
      graphQLErrors.forEach(({ message, path }) =>
        console.error(`[GraphQL Error] ${operation.operationName}: ${message} @ ${path}`)
      );
    }
    if (networkError) {
      console.error(`[Network Error] ${networkError.message}`);
    }
  });
}

export function createApolloOptions(httpLink: HttpLink): ApolloClientOptions<unknown> {
  // Apollo Angular uses Angular's HttpClient for requests (supports interceptors)
  const http = httpLink.create({ uri: 'http://localhost:8080/graphql' });

  return {
    // Combine the error link and the http link
    link: ApolloLink.from([createErrorLink(), http]),
    cache: new InMemoryCache({
      typePolicies: {
        Book: { keyFields: ['id'] },
        Author: { keyFields: ['id'] },
      },
    }),
    defaultOptions: {
      watchQuery: {
        fetchPolicy: 'cache-first',
        errorPolicy: 'all', // 'all' returns partial data even when some fields error
      },
    },
  };
}

@NgModule({
  imports: [ApolloModule],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApolloOptions,
      deps: [HttpLink],    // Angular DI injects HttpLink here
    },
  ],
})
export class GraphQLModule {}

// Import GraphQLModule in AppModule:
// imports: [GraphQLModule, HttpClientModule, ...]

// =============================================================================
// FILE: src/app/graphql/operations.ts
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 2: Defining Operations with gql
// ──────────────────────────────────────────────────────────────────────────────
import { gql } from 'apollo-angular';  // Note: import from 'apollo-angular', not '@apollo/client'

export const GET_ALL_BOOKS = gql`
  query GetAllBooks {
    books {
      id
      title
      genre
      publishedYear
      author {
        id
        name
      }
    }
  }
`;

export const GET_BOOK_BY_ID = gql`
  query GetBookById($id: ID!) {
    book(id: $id) {
      id
      title
      genre
      publishedYear
      author {
        id
        name
        bio
      }
    }
  }
`;

export const ADD_BOOK = gql`
  mutation AddBook($input: BookInput!) {
    createBook(input: $input) {
      id
      title
      genre
      publishedYear
      author {
        id
        name
      }
    }
  }
`;

export const DELETE_BOOK = gql`
  mutation DeleteBook($id: ID!) {
    deleteBook(id: $id) {
      id
      title
    }
  }
`;

// =============================================================================
// FILE: src/app/services/book.service.ts
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 3: Apollo Angular Service Pattern
// ──────────────────────────────────────────────────────────────────────────────
// Best practice: put GraphQL logic in a service, not directly in components.
// This keeps components clean and makes the GraphQL operations testable.
import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { Observable, map, catchError, throwError } from 'rxjs';
import { GET_ALL_BOOKS, GET_BOOK_BY_ID, ADD_BOOK, DELETE_BOOK } from './operations';

export interface Author {
  id: string;
  name: string;
  bio?: string;
}

export interface Book {
  id: string;
  title: string;
  genre: string;
  publishedYear: number;
  author: Author;
}

export interface BookInput {
  title: string;
  authorId: string;
  genre: string;
  publishedYear: number;
}

@Injectable({ providedIn: 'root' })
export class BookService {
  constructor(private apollo: Apollo) {}

  // ── SECTION 3a: Executing Queries ──────────────────────────────────────────
  // apollo.watchQuery() returns a QueryRef — a live Observable that emits
  // whenever the cache updates (not just once like a plain Observable).
  // Use this for data displayed in the UI.
  getAllBooks(): Observable<Book[]> {
    return this.apollo
      .watchQuery<{ books: Book[] }>({
        query: GET_ALL_BOOKS,
        fetchPolicy: 'cache-first',
      })
      .valueChanges  // valueChanges is the Observable<ApolloQueryResult<T>>
      .pipe(
        map(result => {
          // result.data contains the actual GraphQL data
          // result.loading tells you if a network request is in flight
          // result.errors contains any partial GraphQL errors
          if (result.errors) {
            result.errors.forEach(err => console.error('GraphQL error:', err.message));
          }
          return result.data.books;
        }),
        catchError(err => {
          // This catches network errors thrown by the Apollo link chain
          console.error('Query error:', err);
          return throwError(() => err);
        })
      );
  }

  // For one-shot queries (not live), use apollo.query() instead of watchQuery()
  getBookById(id: string): Observable<Book> {
    return this.apollo
      .query<{ book: Book }>({
        query: GET_BOOK_BY_ID,
        variables: { id },
        // 'network-only' bypasses cache — good for detail views where
        // you always want the most current data
        fetchPolicy: 'network-only',
      })
      .pipe(
        map(result => result.data.book),
        catchError(err => throwError(() => err))
      );
  }

  // ── SECTION 3b: Executing Mutations ───────────────────────────────────────
  // apollo.mutate() returns a one-shot Observable — subscribe once, done.
  addBook(input: BookInput): Observable<Book> {
    return this.apollo
      .mutate<{ createBook: Book }>({
        mutation: ADD_BOOK,
        variables: { input },
        // refetchQueries tells Apollo to re-run GET_ALL_BOOKS after success
        refetchQueries: [{ query: GET_ALL_BOOKS }],
        // awaitRefetchQueries: true means the mutation Observable won't complete
        // until the refetch also completes — useful for synchronizing UI updates
        awaitRefetchQueries: true,
      })
      .pipe(
        map(result => result.data!.createBook),
        catchError(err => throwError(() => err))
      );
  }

  deleteBook(id: string): Observable<{ id: string; title: string }> {
    return this.apollo
      .mutate<{ deleteBook: { id: string; title: string } }>({
        mutation: DELETE_BOOK,
        variables: { id },
        update(cache, { data }) {
          // Manually remove the deleted book from the InMemoryCache
          // so the list updates without a full refetch
          const deleted = data?.deleteBook;
          if (deleted) {
            cache.evict({ id: cache.identify({ __typename: 'Book', id: deleted.id }) });
            cache.gc();
          }
        },
      })
      .pipe(
        map(result => result.data!.deleteBook),
        catchError(err => throwError(() => err))
      );
  }
}

// =============================================================================
// FILE: src/app/components/book-list/book-list.component.ts
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 4: Query Response Handling in an Angular Component
// ──────────────────────────────────────────────────────────────────────────────
import { Component, OnInit } from '@angular/core';

// Using the async pipe in the template is the Angular way:
//   *ngIf="books$ | async as books"
// It subscribes + unsubscribes automatically — no memory leaks.

@Component({
  selector: 'app-book-list',
  template: `
    <h2>Bookstore Catalog</h2>
    <button (click)="refresh()">Refresh</button>

    <div *ngIf="loading">Loading books...</div>

    <!-- Error handling: network vs GraphQL errors ─────────────────────────── -->
    <div *ngIf="errorMessage" class="error">
      {{ errorMessage }}
    </div>

    <!-- Success state ─────────────────────────────────────────────────────── -->
    <ul *ngIf="!loading && !errorMessage">
      <li *ngFor="let book of books">
        <strong>{{ book.title }}</strong>
        by {{ book.author.name }} ({{ book.publishedYear }})
      </li>
    </ul>
  `,
})
export class BookListComponent implements OnInit {
  books: Book[] = [];
  loading = true;
  errorMessage = '';

  constructor(private bookService: BookService) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.loading = true;
    this.errorMessage = '';

    this.bookService.getAllBooks().subscribe({
      next: (books) => {
        this.books = books;
        this.loading = false;
      },
      error: (err) => {
        // ApolloError wraps both network and GraphQL errors
        if (err.networkError) {
          this.errorMessage = `Network error: ${err.networkError.message}`;
        } else if (err.graphQLErrors?.length) {
          this.errorMessage = err.graphQLErrors.map((e: any) => e.message).join(', ');
        } else {
          this.errorMessage = 'An unexpected error occurred.';
        }
        this.loading = false;
      },
    });
  }

  refresh(): void {
    this.loadBooks();
  }
}

// =============================================================================
// FILE: src/app/components/add-book/add-book.component.ts
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 5: Mutations from Angular Component
// ──────────────────────────────────────────────────────────────────────────────
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-book',
  template: `
    <h2>Add a Book</h2>
    <form [formGroup]="bookForm" (ngSubmit)="onSubmit()">
      <input formControlName="title" placeholder="Title" />
      <input formControlName="authorId" placeholder="Author ID" />
      <input formControlName="genre" placeholder="Genre" />
      <input formControlName="publishedYear" type="number" placeholder="Year" />
      <button type="submit" [disabled]="bookForm.invalid || submitting">
        {{ submitting ? 'Adding...' : 'Add Book' }}
      </button>
    </form>

    <p *ngIf="successMessage" class="success">{{ successMessage }}</p>
    <p *ngIf="errorMessage" class="error">{{ errorMessage }}</p>
  `,
})
export class AddBookComponent {
  bookForm: FormGroup;
  submitting = false;
  successMessage = '';
  errorMessage = '';

  constructor(private fb: FormBuilder, private bookService: BookService) {
    this.bookForm = this.fb.group({
      title: ['', Validators.required],
      authorId: ['', Validators.required],
      genre: [''],
      publishedYear: [new Date().getFullYear(), [Validators.required, Validators.min(1000)]],
    });
  }

  onSubmit(): void {
    if (this.bookForm.invalid) return;

    this.submitting = true;
    this.successMessage = '';
    this.errorMessage = '';

    const input: BookInput = {
      title: this.bookForm.value.title,
      authorId: this.bookForm.value.authorId,
      genre: this.bookForm.value.genre,
      publishedYear: Number(this.bookForm.value.publishedYear),
    };

    this.bookService.addBook(input).subscribe({
      next: (newBook) => {
        this.successMessage = `"${newBook.title}" added successfully!`;
        this.bookForm.reset({ publishedYear: new Date().getFullYear() });
        this.submitting = false;
      },
      error: (err) => {
        // Parse Apollo errors for display
        if (err.graphQLErrors?.length) {
          this.errorMessage = err.graphQLErrors[0].message;
        } else if (err.networkError) {
          this.errorMessage = `Server unreachable: ${err.networkError.message}`;
        } else {
          this.errorMessage = 'Mutation failed. Please try again.';
        }
        this.submitting = false;
      },
    });
  }
}
