// =============================================================================
// Day 32 — GraphQL Client: Apollo Client for React
// Bookstore Application — Full working example
//
// Topics covered:
//   1. Apollo Client setup & ApolloProvider
//   2. Consuming GraphQL APIs from frontend
//   3. Query execution with useQuery
//   4. Response handling (data, loading, error)
//   5. Mutations with useMutation
//   6. Error handling (network errors vs GraphQL errors)
// =============================================================================

// FILE: src/apollo/client.ts
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 1: Apollo Client Setup
// ──────────────────────────────────────────────────────────────────────────────
import {
  ApolloClient,
  InMemoryCache,
  ApolloProvider,
  HttpLink,
  from,
  ApolloError,
} from '@apollo/client';
import { onError } from '@apollo/client/link/error';

// Error link — runs for EVERY request and logs both network and GraphQL errors
// This is separate from the per-query error handling in your components
const errorLink = onError(({ graphQLErrors, networkError, operation }) => {
  if (graphQLErrors) {
    graphQLErrors.forEach(({ message, locations, path }) => {
      console.error(
        `[GraphQL Error] Operation: ${operation.operationName} | ` +
        `Message: ${message} | Path: ${path}`
      );
    });
  }
  if (networkError) {
    console.error(`[Network Error] ${networkError.message}`);
  }
});

// HTTP link — sends all GraphQL operations as POST to /graphql
const httpLink = new HttpLink({
  uri: 'http://localhost:8080/graphql',
  // Add auth headers here if needed:
  // headers: { Authorization: `Bearer ${getToken()}` }
});

// Combine links: error handling first, then the actual HTTP request
export const apolloClient = new ApolloClient({
  link: from([errorLink, httpLink]),
  cache: new InMemoryCache({
    // Tell Apollo how to identify Book and Author objects in the cache.
    // By default it uses __typename + id. Customize if your server
    // uses a different identifier field name.
    typePolicies: {
      Book: {
        keyFields: ['id'],
      },
      Author: {
        keyFields: ['id'],
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      // 'cache-first'  → check cache first, hit network only on miss (default)
      // 'network-only' → always go to network, still write to cache
      // 'no-cache'     → always go to network, skip cache entirely
      fetchPolicy: 'cache-first',
    },
  },
});

// =============================================================================
// FILE: src/index.tsx  (abbreviated)
// ──────────────────────────────────────────────────────────────────────────────
// ApolloProvider makes the client available to ALL child components via context.
// It must wrap the component tree — typically at the app root.
// ──────────────────────────────────────────────────────────────────────────────
/*
import React from 'react';
import ReactDOM from 'react-dom/client';
import { ApolloProvider } from '@apollo/client';
import { apolloClient } from './apollo/client';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <ApolloProvider client={apolloClient}>
    <App />
  </ApolloProvider>
);
*/

// =============================================================================
// FILE: src/graphql/queries.ts
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 2: Defining GraphQL Operations with gql
// ──────────────────────────────────────────────────────────────────────────────
import { gql } from '@apollo/client';

// Query: Fetch all books with their authors
// Note: we only request the fields we actually display — no over-fetching
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

// Query: Fetch a single book by ID (uses a variable)
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

// Mutation: Add a new book
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

// Mutation: Delete a book by ID
export const DELETE_BOOK = gql`
  mutation DeleteBook($id: ID!) {
    deleteBook(id: $id) {
      id
      title
    }
  }
`;

// =============================================================================
// FILE: src/components/BookList.tsx
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 3: Query Execution and Response Handling with useQuery
// ──────────────────────────────────────────────────────────────────────────────
import React from 'react';
import { useQuery } from '@apollo/client';
import { GET_ALL_BOOKS } from '../graphql/queries';

// TypeScript types matching the GraphQL schema
interface Author {
  id: string;
  name: string;
}

interface Book {
  id: string;
  title: string;
  genre: string;
  publishedYear: number;
  author: Author;
}

interface GetAllBooksData {
  books: Book[];
}

export const BookList: React.FC = () => {
  // ── useQuery returns three key values: ──────────────────────────────────────
  //   data    → the resolved GraphQL response (undefined until loaded)
  //   loading → true while the request is in-flight
  //   error   → ApolloError if anything went wrong (network OR GraphQL error)
  // ──────────────────────────────────────────────────────────────────────────
  const { data, loading, error, refetch } = useQuery<GetAllBooksData>(GET_ALL_BOOKS, {
    // fetchPolicy overrides the client default for this specific query
    fetchPolicy: 'cache-first',
    // notifyOnNetworkStatusChange: true would expose a `networkStatus` value
    // for more granular loading states (initial load vs refetch vs poll)
    notifyOnNetworkStatusChange: true,
  });

  // ── Loading state ───────────────────────────────────────────────────────────
  // Always handle loading FIRST — data is undefined here
  if (loading) return <p>Loading books...</p>;

  // ── Error state ─────────────────────────────────────────────────────────────
  // ApolloError has two categories:
  //   error.networkError  → couldn't reach the server (CORS, server down, timeout)
  //   error.graphQLErrors → server responded but reported one or more field errors
  if (error) {
    if (error.networkError) {
      return <p>⚠️ Network error: {error.networkError.message}. Is the server running?</p>;
    }
    // graphQLErrors is an array — multiple errors can occur in one response
    return (
      <div>
        <p>GraphQL errors:</p>
        <ul>
          {error.graphQLErrors.map((gqlError, i) => (
            <li key={i}>{gqlError.message}</li>
          ))}
        </ul>
      </div>
    );
  }

  // ── Success state ───────────────────────────────────────────────────────────
  const books = data?.books ?? [];

  return (
    <div>
      <h2>Bookstore Catalog</h2>
      <button onClick={() => refetch()}>Refresh</button>
      {books.length === 0 ? (
        <p>No books found.</p>
      ) : (
        <ul>
          {books.map((book) => (
            <li key={book.id}>
              <strong>{book.title}</strong> by {book.author.name} ({book.publishedYear})
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

// =============================================================================
// FILE: src/components/BookDetail.tsx
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 4: Query with Variables (lazy loading on demand)
// ──────────────────────────────────────────────────────────────────────────────
import { useLazyQuery } from '@apollo/client';
import { GET_BOOK_BY_ID } from '../graphql/queries';

export const BookDetail: React.FC = () => {
  const [bookId, setBookId] = React.useState('');

  // useLazyQuery does NOT run immediately — you call the returned function manually
  // Use this when the query depends on user input (search, click, etc.)
  const [getBook, { data, loading, error }] = useLazyQuery(GET_BOOK_BY_ID);

  const handleSearch = () => {
    if (bookId.trim()) {
      // Variables are passed here, matching the $id: ID! in the query definition
      getBook({ variables: { id: bookId } });
    }
  };

  return (
    <div>
      <h2>Look Up a Book</h2>
      <input
        value={bookId}
        onChange={(e) => setBookId(e.target.value)}
        placeholder="Enter book ID"
      />
      <button onClick={handleSearch}>Search</button>

      {loading && <p>Searching...</p>}
      {error && <p>Error: {error.message}</p>}
      {data?.book && (
        <div>
          <h3>{data.book.title}</h3>
          <p>Author: {data.book.author.name}</p>
          <p>Genre: {data.book.genre}</p>
          <p>Published: {data.book.publishedYear}</p>
          <p>Bio: {data.book.author.bio}</p>
        </div>
      )}
    </div>
  );
};

// =============================================================================
// FILE: src/components/AddBookForm.tsx
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 5: Mutations with useMutation
// ──────────────────────────────────────────────────────────────────────────────
import { useMutation } from '@apollo/client';
import { ADD_BOOK, GET_ALL_BOOKS } from '../graphql/queries';

interface BookInput {
  title: string;
  authorId: string;
  genre: string;
  publishedYear: number;
}

export const AddBookForm: React.FC = () => {
  const [formData, setFormData] = React.useState<BookInput>({
    title: '',
    authorId: '',
    genre: '',
    publishedYear: new Date().getFullYear(),
  });

  const [successMessage, setSuccessMessage] = React.useState('');

  // useMutation returns:
  //   [mutateFn, { data, loading, error }]
  //
  // The mutation does NOT run until you call mutateFn().
  // 'refetchQueries' tells Apollo to re-run GET_ALL_BOOKS after the mutation
  // succeeds, ensuring the BookList component shows the new book immediately.
  const [addBook, { loading: adding, error: addError }] = useMutation(ADD_BOOK, {
    // OPTION 1: Refetch — re-runs the query, guaranteed fresh data
    refetchQueries: [{ query: GET_ALL_BOOKS }],

    // OPTION 2: update function — manually update the cache without a refetch
    // (more efficient, shown in Part 2 under caching strategies)
    //
    // update(cache, { data: { createBook } }) {
    //   const existing = cache.readQuery<GetAllBooksData>({ query: GET_ALL_BOOKS });
    //   if (existing) {
    //     cache.writeQuery({
    //       query: GET_ALL_BOOKS,
    //       data: { books: [...existing.books, createBook] },
    //     });
    //   }
    // },

    onCompleted(data) {
      // onCompleted fires after a successful mutation — use for side effects
      setSuccessMessage(`"${data.createBook.title}" added successfully!`);
      setFormData({ title: '', authorId: '', genre: '', publishedYear: new Date().getFullYear() });
    },
    onError(error) {
      // onError fires on any mutation error — network or GraphQL
      console.error('Mutation failed:', error.message);
    },
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSuccessMessage('');
    try {
      await addBook({
        variables: {
          input: formData,
        },
      });
    } catch (err) {
      // Errors are also thrown from the awaited call — both onError and catch fire.
      // In practice, handle in onError and keep the try/catch as a safety net.
      console.error('Caught error:', err);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Add a Book</h2>
      <input
        value={formData.title}
        onChange={(e) => setFormData({ ...formData, title: e.target.value })}
        placeholder="Title"
        required
      />
      <input
        value={formData.authorId}
        onChange={(e) => setFormData({ ...formData, authorId: e.target.value })}
        placeholder="Author ID"
        required
      />
      <input
        value={formData.genre}
        onChange={(e) => setFormData({ ...formData, genre: e.target.value })}
        placeholder="Genre"
      />
      <input
        type="number"
        value={formData.publishedYear}
        onChange={(e) => setFormData({ ...formData, publishedYear: parseInt(e.target.value) })}
      />
      <button type="submit" disabled={adding}>
        {adding ? 'Adding...' : 'Add Book'}
      </button>

      {/* ── Error handling display ────────────────────────────────────────── */}
      {addError && (
        <div style={{ color: 'red' }}>
          {addError.networkError && (
            <p>Network error: {addError.networkError.message}</p>
          )}
          {addError.graphQLErrors.map((err, i) => (
            // GraphQL errors often include an 'extensions' object with a code
            // e.g. err.extensions?.code === 'VALIDATION_ERROR'
            <p key={i}>Error: {err.message} (code: {err.extensions?.code as string})</p>
          ))}
        </div>
      )}

      {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}
    </form>
  );
};

// =============================================================================
// FILE: src/components/DeleteBookButton.tsx
// ──────────────────────────────────────────────────────────────────────────────
// SECTION 6: Optimistic Updates — instant UI feedback before server confirms
// ──────────────────────────────────────────────────────────────────────────────
import { DELETE_BOOK } from '../graphql/queries';

interface DeleteButtonProps {
  bookId: string;
  bookTitle: string;
}

export const DeleteBookButton: React.FC<DeleteButtonProps> = ({ bookId, bookTitle }) => {
  const [deleteBook, { loading }] = useMutation(DELETE_BOOK, {
    // optimisticResponse: simulate the server's response immediately
    // The cache is updated instantly, and if the server disagrees, it rolls back.
    optimisticResponse: {
      deleteBook: {
        __typename: 'Book',
        id: bookId,
        title: bookTitle,
      },
    },
    // update: manually evict the deleted book from the cache
    update(cache, { data: { deleteBook } }) {
      cache.evict({ id: cache.identify(deleteBook) });
      cache.gc(); // garbage collect orphaned references
    },
  });

  return (
    <button
      onClick={() => deleteBook({ variables: { id: bookId } })}
      disabled={loading}
    >
      {loading ? 'Deleting...' : 'Delete'}
    </button>
  );
};
