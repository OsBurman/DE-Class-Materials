// =============================================================================
// Day 32 — GraphQL Client: Caching Strategies & Request Optimization
// Bookstore Application
//
// Topics covered:
//   1. Apollo InMemoryCache — how normalization works
//   2. Fetch policies (cache-first, network-only, cache-and-network, no-cache)
//   3. Manual cache updates (readQuery / writeQuery)
//   4. Cache eviction and garbage collection
//   5. Batching requests with BatchHttpLink
//   6. Request optimization: polling, subscriptions as alternative
// =============================================================================

import {
  ApolloClient,
  InMemoryCache,
  gql,
  ApolloLink,
  HttpLink,
} from '@apollo/client';
import { BatchHttpLink } from '@apollo/client/link/batch-http';
import { useQuery, useMutation } from '@apollo/client';
import React from 'react';

// =============================================================================
// SECTION 1: How InMemoryCache Normalization Works
// =============================================================================
//
// Apollo's InMemoryCache stores objects in a FLAT, NORMALIZED structure.
// Instead of:
//
//   {
//     books: [
//       { id: "1", title: "Clean Code", author: { id: "10", name: "Martin" } },
//       { id: "2", title: "Refactoring", author: { id: "10", name: "Martin" } }
//     ]
//   }
//
// Apollo stores it as:
//
//   {
//     "Book:1": { id: "1", title: "Clean Code", author: { __ref: "Author:10" } },
//     "Book:2": { id: "2", title: "Refactoring", author: { __ref: "Author:10" } },
//     "Author:10": { id: "10", name: "Martin" }
//   }
//
// Why does this matter?
//   - Author:10 exists ONCE in the cache.
//   - If a mutation updates Author:10's name, ALL queries that reference that
//     author automatically re-render — no extra refetch needed.
//   - This is "cache normalization" — same data, stored once.
//
// The cache key is: __typename + id
// If your objects don't have an `id` field, you must configure keyFields.

export const cacheWithCustomKeys = new InMemoryCache({
  typePolicies: {
    // Book uses 'id' (default) — no override needed
    Book: {
      keyFields: ['id'],
    },
    // Example: a type with a compound key
    OrderItem: {
      keyFields: ['orderId', 'bookId'],  // compound key
    },
    // Example: a singleton type (only one instance, no ID needed)
    StoreSettings: {
      keyFields: [],  // empty array = singleton — stored as "StoreSettings:{}"
    },
  },
});

// =============================================================================
// SECTION 2: Fetch Policies
// =============================================================================
//
// Fetch policy controls HOW Apollo decides between cache and network.
//
// Policy          | Read cache? | Write cache? | Network? | Use when...
// ─────────────── | ─────────── | ──────────── | ──────── | ──────────────────
// cache-first     |     YES     |     YES      | if miss  | Default — good for stable data
// cache-only      |     YES     |     NO       | NEVER    | Offline mode, read-only
// network-only    |     NO      |     YES      | ALWAYS   | Always fresh (detail pages)
// no-cache        |     NO      |     NO       | ALWAYS   | Sensitive data, never cache
// cache-and-network |   YES   |     YES      | ALWAYS   | Show cached immediately, update with fresh

const GET_BOOKS = gql`
  query GetAllBooks {
    books {
      id
      title
      author { id name }
    }
  }
`;

const GET_BOOK = gql`
  query GetBookById($id: ID!) {
    book(id: $id) {
      id title genre publishedYear
      author { id name bio }
    }
  }
`;

const ADD_BOOK = gql`
  mutation AddBook($input: BookInput!) {
    createBook(input: $input) {
      id title genre publishedYear
      author { id name }
    }
  }
`;

// Example component: Book List — uses cache-first (default)
// First load: network. Subsequent loads: instant from cache.
export const BookListCached: React.FC = () => {
  const { data, loading } = useQuery(GET_BOOKS, {
    fetchPolicy: 'cache-first',
  });
  if (loading) return <p>Loading...</p>;
  return <ul>{data?.books.map((b: any) => <li key={b.id}>{b.title}</li>)}</ul>;
};

// Example: Book Detail — uses network-only (always current)
// A detail page should always show the latest data, not a potentially stale cache.
export const BookDetailFresh: React.FC<{ bookId: string }> = ({ bookId }) => {
  const { data, loading } = useQuery(GET_BOOK, {
    variables: { id: bookId },
    fetchPolicy: 'network-only',
  });
  if (loading) return <p>Loading detail...</p>;
  return <p>{data?.book?.title}</p>;
};

// Example: cache-and-network — show stale data IMMEDIATELY, then update
// This is great for UX: the user sees something instantly, then it refreshes.
export const BookListOptimistic: React.FC = () => {
  const { data, loading, networkStatus } = useQuery(GET_BOOKS, {
    fetchPolicy: 'cache-and-network',
    notifyOnNetworkStatusChange: true,  // re-render when networkStatus changes
  });
  // networkStatus === 1 means initial loading (no cache)
  // networkStatus === 4 means refetching (cache served, network updating)
  if (loading && !data) return <p>Loading...</p>;

  return (
    <div>
      {networkStatus === 4 && <small>Refreshing...</small>}
      <ul>{data?.books.map((b: any) => <li key={b.id}>{b.title}</li>)}</ul>
    </div>
  );
};

// =============================================================================
// SECTION 3: Manual Cache Updates (readQuery / writeQuery)
// =============================================================================
//
// Instead of refetching GET_BOOKS after a mutation, we can manually write
// the new book into the cache. This saves a network round trip.
//
// Pattern:
//   1. Read the current cache state with cache.readQuery(...)
//   2. Compute the new state (add/remove/update the item)
//   3. Write the new state with cache.writeQuery(...)

interface Book {
  id: string;
  title: string;
  genre: string;
  publishedYear: number;
  author: { id: string; name: string };
  __typename: string;
}

interface BooksData {
  books: Book[];
}

export const AddBookWithCacheUpdate: React.FC = () => {
  const [addBook, { loading, error }] = useMutation(ADD_BOOK, {
    // The 'update' function receives the cache and the mutation result.
    // It runs AFTER the mutation succeeds.
    update(cache, { data: { createBook } }) {
      // Step 1: Read current cache contents
      const existing = cache.readQuery<BooksData>({ query: GET_BOOKS });

      if (existing) {
        // Step 2: Construct the new state (prepend the new book)
        const updatedBooks = [createBook, ...existing.books];

        // Step 3: Write the new state back to the cache
        cache.writeQuery<BooksData>({
          query: GET_BOOKS,
          data: { books: updatedBooks },
        });
        // Any component using GET_BOOKS will re-render immediately.
        // No extra network request needed.
      }
    },
  });

  const handleAdd = () => {
    addBook({
      variables: {
        input: {
          title: 'The Pragmatic Programmer',
          authorId: '1',
          genre: 'Technology',
          publishedYear: 2019,
        },
      },
    });
  };

  return (
    <button onClick={handleAdd} disabled={loading}>
      {loading ? 'Adding...' : 'Add Sample Book'}
    </button>
  );
};

// =============================================================================
// SECTION 4: Cache Eviction and Garbage Collection
// =============================================================================
//
// When you delete an item, you need to remove it from the cache.
// cache.evict() removes a specific object.
// cache.gc() removes any orphaned references (objects that nothing points to).

const DELETE_BOOK = gql`
  mutation DeleteBook($id: ID!) {
    deleteBook(id: $id) { id title }
  }
`;

export const DeleteWithCacheEviction: React.FC<{ bookId: string }> = ({ bookId }) => {
  const [deleteBook] = useMutation(DELETE_BOOK, {
    update(cache, { data: { deleteBook } }) {
      // Identify the cache key for this object: "Book:<id>"
      const cacheId = cache.identify({
        __typename: 'Book',
        id: deleteBook.id,
      });

      // Evict (remove) the object from the cache
      cache.evict({ id: cacheId });

      // Run garbage collection to clean up references to the evicted object
      // (e.g., if GET_BOOKS had a reference to this Book, that reference is cleared)
      cache.gc();

      // After eviction, any query that included this book will automatically
      // re-render with the book removed — no refetch needed.
    },
  });

  return (
    <button onClick={() => deleteBook({ variables: { id: bookId } })}>
      Delete
    </button>
  );
};

// =============================================================================
// SECTION 5: Batching Requests with BatchHttpLink
// =============================================================================
//
// Problem: When multiple components mount at the same time, each with useQuery,
// they each fire a separate HTTP request. 3 components = 3 POST /graphql calls.
//
// Solution: BatchHttpLink collects operations fired within a short window
// (default 10ms) and sends them as a single HTTP request with an array body.
// The server receives an array and returns an array of results.
//
// NOTE: Your Spring for GraphQL server must support batching to use this.
//       Add spring.graphql.websocket.path and batch endpoint config.

export const batchingClient = new ApolloClient({
  // Replace HttpLink with BatchHttpLink for automatic request batching
  link: new BatchHttpLink({
    uri: 'http://localhost:8080/graphql',
    batchMax: 5,            // maximum operations per batch
    batchInterval: 20,      // wait up to 20ms for operations to accumulate
  }),
  cache: new InMemoryCache(),
});

// With BatchHttpLink, if BookList and BookDetail both mount simultaneously,
// Apollo collects both queries and sends ONE request:
//
// POST /graphql
// Body: [
//   { "query": "query GetAllBooks { books { id title } }" },
//   { "query": "query GetBookById($id: ID!) { book(id: $id) { title } }", "variables": { "id": "1" } }
// ]
//
// Server responds: [ { "data": { "books": [...] } }, { "data": { "book": {...} } } ]

// =============================================================================
// SECTION 6: Polling — Automatic Periodic Refetching
// =============================================================================
//
// For data that changes frequently but doesn't need WebSocket overhead,
// use polling: Apollo re-runs the query on an interval.

export const PollingBookList: React.FC = () => {
  const { data, startPolling, stopPolling } = useQuery(GET_BOOKS, {
    pollInterval: 5000,  // refetch every 5 seconds automatically
  });

  return (
    <div>
      <button onClick={() => stopPolling()}>Pause Auto-Refresh</button>
      <button onClick={() => startPolling(5000)}>Resume Auto-Refresh</button>
      <ul>
        {data?.books.map((b: any) => <li key={b.id}>{b.title}</li>)}
      </ul>
    </div>
  );
};

// =============================================================================
// SECTION 7: Pagination with fetchMore
// =============================================================================
//
// For large datasets, load additional pages on demand using fetchMore.
// This appends results to the existing cache instead of replacing them.

const GET_BOOKS_PAGINATED = gql`
  query GetBooksPaginated($offset: Int!, $limit: Int!) {
    books(offset: $offset, limit: $limit) {
      id
      title
      author { id name }
    }
  }
`;

export const PaginatedBookList: React.FC = () => {
  const PAGE_SIZE = 10;
  const { data, loading, fetchMore } = useQuery(GET_BOOKS_PAGINATED, {
    variables: { offset: 0, limit: PAGE_SIZE },
  });

  const loadMore = () => {
    fetchMore({
      variables: {
        offset: data?.books.length ?? 0,
        limit: PAGE_SIZE,
      },
      // updateQuery merges the new page results with the existing cache data
      updateQuery(previousResult, { fetchMoreResult }) {
        if (!fetchMoreResult) return previousResult;
        return {
          books: [...previousResult.books, ...fetchMoreResult.books],
        };
      },
    });
  };

  if (loading && !data) return <p>Loading...</p>;

  return (
    <div>
      <ul>
        {data?.books.map((b: any) => <li key={b.id}>{b.title}</li>)}
      </ul>
      <button onClick={loadMore}>Load More</button>
    </div>
  );
};
