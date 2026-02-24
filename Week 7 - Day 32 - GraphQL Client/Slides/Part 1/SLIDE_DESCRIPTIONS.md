# Day 32 Part 1 — GraphQL Client Libraries, Apollo Client (React & Angular), and Error Handling
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 32 Part 1: GraphQL Client Libraries, Apollo Client for React & Angular, and Error Handling

**Subtitle:** Consuming GraphQL APIs from the frontend

**Learning Objectives:**
- Compare available GraphQL client libraries and choose the right one for a project
- Set up Apollo Client in React and Angular applications
- Execute queries and mutations using Apollo Client hooks and services
- Handle loading, error, and data states from GraphQL responses
- Configure `errorPolicy` and `onError` link for client-side error management
- Add authentication headers via Apollo Link middleware
- Implement real-time subscriptions with `useSubscription` and WebSocket links

---

### Slide 2 — GraphQL Client Libraries Overview

**Title:** GraphQL Client Libraries — Why You Need One and What's Available

**Content:**

**Can't you just use fetch?**
```javascript
// You could — this is a valid GraphQL request:
const response = await fetch('/graphql', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    query: `query GetBooks { books { id title author { name } } }`,
    variables: {}
  })
});
const { data, errors } = await response.json();
```

But you'd have to build yourself: caching, cache invalidation, normalized state, loading states, error handling, React/Angular state integration, subscriptions over WebSocket, retry logic, dev tooling, and TypeScript types.

**The four main options:**

| Library | Weight | Caching | Best For |
|---------|--------|---------|---------|
| **Apollo Client** | Medium | Full normalization | Most production applications |
| **urql** | Light | Pluggable | Projects needing less opinionation |
| **Relay** | Heavy | Highly optimized | Large-scale React apps (Meta-style) |
| **graphql-request** | Minimal | None | Simple scripts, SSR, pair with React Query |

**Course focus: Apollo Client** — industry standard, excellent DevTools, largest ecosystem, best documentation, most employer recognition.

---

### Slide 3 — Apollo Client for React — Installation

**Title:** Apollo Client — Installation and Packages

**Content:**

```bash
npm install @apollo/client graphql
# @apollo/client   — Apollo Client core + React integration (hooks, ApolloProvider)
# graphql          — Required peer dependency — the GraphQL parsing engine
```

**What `@apollo/client` includes:**
- `ApolloClient` — the main client class
- `InMemoryCache` — normalized in-memory cache
- `HttpLink` — HTTP transport for queries and mutations
- `ApolloProvider` — React context provider
- `gql` — template literal tag for writing queries
- `useQuery`, `useMutation`, `useSubscription` — React hooks
- `ApolloError` — error wrapper class

**For subscriptions (WebSocket), two additional packages:**
```bash
npm install graphql-ws @apollo/client
# graphql-ws — WebSocket client implementing the GraphQL over WebSocket protocol
# GraphQLWsLink is exported from @apollo/client/link/subscriptions
```

**For Angular:**
```bash
npm install apollo-angular @apollo/client graphql
# apollo-angular — Angular-specific adapter; uses @apollo/client core internally
```

---

### Slide 4 — ApolloClient and ApolloProvider

**Title:** Setting Up ApolloClient in a React Application

**Content:**

```typescript
// src/main.tsx (or index.tsx)
import React from 'react';
import ReactDOM from 'react-dom/client';
import {
  ApolloClient,
  InMemoryCache,
  ApolloProvider,
  HttpLink,
} from '@apollo/client';
import App from './App';

// 1. Create an HttpLink — defines WHERE to send requests
const httpLink = new HttpLink({
  uri: 'http://localhost:8080/graphql',
});

// 2. Create an InMemoryCache — stores query results, normalizes by __typename + id
const cache = new InMemoryCache();

// 3. Create the ApolloClient — the central coordinator
const client = new ApolloClient({
  link: httpLink,   // transport layer
  cache: cache,     // storage layer
  // Shorthand: uri: 'http://localhost:8080/graphql' creates an HttpLink automatically
});

// 4. Wrap the app in ApolloProvider — makes client available everywhere via context
ReactDOM.createRoot(document.getElementById('root')!).render(
  <ApolloProvider client={client}>
    <App />
  </ApolloProvider>
);
```

**ApolloProvider** uses React Context. Any component in the tree can use Apollo hooks (`useQuery`, `useMutation`) and they all share the same client instance and cache.

---

### Slide 5 — The gql Template Literal Tag

**Title:** gql — Writing GraphQL Documents in JavaScript

**Content:**

```typescript
import { gql } from '@apollo/client';

// gql parses the query string into a GraphQL AST (Abstract Syntax Tree) object
// This parsing happens ONCE at module load time, not on every render
const GET_BOOKS = gql`
  query GetBooks {
    books {
      id
      title
      author {
        name
      }
    }
  }
`;

// GET_BOOKS is now an object: { kind: 'Document', definitions: [...] }
// Apollo works with the AST object, not the raw string
// Apollo DevTools, TypeScript codegen, and linting tools all work with gql documents
```

**Critical rule — define gql documents OUTSIDE your component:**
```typescript
// ✅ CORRECT — defined once at module level, parsed once
const GET_BOOKS = gql`query GetBooks { books { id title } }`;

function BookList() {
  const { data } = useQuery(GET_BOOKS);  // same object reference every render
}

// ❌ INCORRECT — new AST object created on every render, defeats Apollo's caching
function BookList() {
  const GET_BOOKS = gql`query GetBooks { books { id title } }`;  // inside component!
  const { data } = useQuery(GET_BOOKS);
}
```

**Operation types in gql:**
```typescript
const MY_QUERY        = gql`query ...`;
const MY_MUTATION     = gql`mutation ...`;
const MY_SUBSCRIPTION = gql`subscription ...`;
const MY_FRAGMENT     = gql`fragment MyFragment on Book { id title }`;
```

---

### Slide 6 — useQuery — Fetching Data

**Title:** useQuery — The Primary Hook for Reading Data

**Content:**

```typescript
import { useQuery, gql } from '@apollo/client';

const GET_BOOKS = gql`
  query GetBooks {
    books {
      id
      title
      author { name }
    }
  }
`;

function BookList() {
  // useQuery runs automatically when the component mounts
  const { loading, error, data } = useQuery(GET_BOOKS);

  // loading: true while the first fetch is in flight; false after
  if (loading) return <div>Loading books...</div>;

  // error: an ApolloError object if the request failed; undefined otherwise
  if (error) return <div>Error: {error.message}</div>;

  // data: the query result — shape exactly matches the selection set
  // data.books is an array of { id, title, author: { name } }
  return (
    <ul>
      {data.books.map(book => (
        <li key={book.id}>
          <strong>{book.title}</strong> — {book.author.name}
        </li>
      ))}
    </ul>
  );
}
```

**The three return states:**

| State | `loading` | `error` | `data` |
|-------|-----------|---------|--------|
| Initial fetch | `true` | `undefined` | `undefined` |
| Success | `false` | `undefined` | result object |
| Error | `false` | `ApolloError` | `undefined` |
| Background refetch | `false` | — | previous data (still shown) |

`useQuery` is **declarative** — it runs automatically based on component lifecycle. You don't call a function to trigger it.

---

### Slide 7 — useQuery — Variables, skip, and Options

**Title:** useQuery — Parameterized Queries and Options

**Content:**

```typescript
const GET_BOOK = gql`
  query GetBook($id: ID!) {
    book(id: $id) {
      id
      title
      pageCount
      author { name }
    }
  }
`;

function BookDetail({ bookId }: { bookId: string | null }) {
  const { loading, error, data, refetch, networkStatus } = useQuery(GET_BOOK, {
    // Pass variables — must match the declared types in the query document
    variables: { id: bookId },

    // skip: true prevents the query from running at all
    // Use this when required data isn't ready yet (null ID, not logged in, etc.)
    skip: !bookId,

    // fetchPolicy — how to use the cache (covered in Part 2)
    fetchPolicy: 'cache-and-network',

    // pollInterval — auto-refetch every N milliseconds
    // pollInterval: 5000,

    // onCompleted — callback when data arrives
    onCompleted: (data) => console.log('Loaded book:', data.book.title),
  });

  // refetch() — manually trigger a fresh network request
  // Useful for a "Refresh" button
  return (
    <>
      {loading && <p>Loading...</p>}
      {error && <p>Error: {error.message}</p>}
      {data && <h1>{data.book.title}</h1>}
      <button onClick={() => refetch()}>Refresh</button>
    </>
  );
}
```

**`skip` vs `variables` with null:**
```typescript
// skip: !userId — query does not run at all, no network request made
const { data } = useQuery(GET_USER, { variables: { id: userId }, skip: !userId });

// lazy alternative — useQuery with lazy loading
import { useLazyQuery } from '@apollo/client';
const [getBook, { loading, data }] = useLazyQuery(GET_BOOK);
// Call getBook({ variables: { id: "1" } }) imperatively when needed
```

---

### Slide 8 — useMutation — Writing Data

**Title:** useMutation — Executing Write Operations

**Content:**

```typescript
import { useMutation, gql } from '@apollo/client';

const CREATE_BOOK = gql`
  mutation CreateBook($input: CreateBookInput!) {
    createBook(input: $input) {
      id
      title
      author { name }
    }
  }
`;

function CreateBookForm() {
  // useMutation returns a tuple: [triggerFn, resultObject]
  const [createBook, { loading, error, data }] = useMutation(CREATE_BOOK);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = e.currentTarget;
    const title = (form.elements.namedItem('title') as HTMLInputElement).value;

    try {
      // Call the trigger function imperatively — unlike useQuery, mutations don't run automatically
      const result = await createBook({
        variables: {
          input: { title, authorId: '5' }
        }
      });
      console.log('Created book:', result.data?.createBook.id);
    } catch (err) {
      // Network errors are thrown; GraphQL errors are in the result or error object
      console.error('Mutation failed:', err);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="title" placeholder="Book title" />
      <button type="submit" disabled={loading}>
        {loading ? 'Creating...' : 'Create Book'}
      </button>
      {error && <p style={{ color: 'red' }}>Error: {error.message}</p>}
      {data && <p>Created: {data.createBook.title}</p>}
    </form>
  );
}
```

**Key contrast with useQuery:**
- `useQuery` is **declarative** — runs automatically when component mounts
- `useMutation` is **imperative** — you call the trigger function when you want it to run

---

### Slide 9 — useMutation — Options and Patterns

**Title:** useMutation — Callbacks, refetchQueries, and Optimistic UI

**Content:**

```typescript
const [createBook] = useMutation(CREATE_BOOK, {
  // Automatically re-run these queries after the mutation succeeds
  // Simple and reliable — guarantees the list is up to date
  // Downside: triggers additional network requests
  refetchQueries: [
    { query: GET_BOOKS },                                       // by query doc
    { query: GET_BOOKS, variables: { authorId: '5' } },        // with variables
    'GetFeaturedBooks',                                          // by operation name
  ],

  // Wait for refetch to complete before mutation loading → false
  awaitRefetchQueries: true,

  // Callback when mutation completes successfully
  onCompleted: (data) => {
    console.log('Created:', data.createBook.id);
    navigate('/books');
  },

  // Callback when mutation fails (network or GraphQL error)
  onError: (error) => {
    toast.error(error.message);
  },
});

// Optimistic UI — show the result BEFORE the server confirms
// Apollo automatically rolls back if the mutation fails
const [updateBook] = useMutation(UPDATE_BOOK, {
  optimisticResponse: {
    updateBook: {
      __typename: 'Book',
      id: bookId,
      title: newTitle,   // assume success — show immediately
    },
  },
});
```

**refetchQueries vs manual cache update:**
- `refetchQueries` — easy, always fresh, costs a network request
- `update` function — manual, instant, no extra network cost (covered in Part 2)

---

### Slide 10 — Apollo Angular — Setup

**Title:** Apollo Angular — Installation and Project Setup

**Content:**

```bash
npm install apollo-angular @apollo/client graphql
```

**Standalone setup — `app.config.ts` (Angular 17+):**
```typescript
import { ApplicationConfig, inject } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideApollo } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';
import { InMemoryCache } from '@apollo/client/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),                   // required — Apollo Angular uses HttpClient

    provideApollo(() => {
      const httpLink = inject(HttpLink);   // inject inside the factory function
      return {
        link: httpLink.create({ uri: 'http://localhost:8080/graphql' }),
        cache: new InMemoryCache(),
        // defaultOptions can be set here:
        // defaultOptions: { watchQuery: { fetchPolicy: 'cache-and-network' } }
      };
    }),
  ],
};
```

**NgModule setup (older projects):**
```typescript
// app.module.ts
import { ApolloModule } from 'apollo-angular';
import { HttpLinkModule } from 'apollo-angular/http';

@NgModule({
  imports: [ApolloModule, HttpLinkModule, HttpClientModule],
  // ... then provide ApolloClient via forRoot() or factory
})
```

Modern Angular projects use standalone components and `app.config.ts`. Both approaches work — use `provideApollo` for new projects.

---

### Slide 11 — Apollo Angular — Queries

**Title:** Apollo Angular — Querying Data with watchQuery and valueChanges

**Content:**

```typescript
import { Component, inject } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { AsyncPipe } from '@angular/common';

interface Book { id: string; title: string; author: { name: string }; }

const GET_BOOKS = gql`
  query GetBooks {
    books { id title author { name } }
  }
`;

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [AsyncPipe],
  template: `
    @if (books$ | async; as result) {
      @if (result.loading) {
        <p>Loading books...</p>
      } @else if (result.error) {
        <p>Error: {{ result.error.message }}</p>
      } @else {
        @for (book of result.data?.books; track book.id) {
          <div>{{ book.title }} — {{ book.author.name }}</div>
        }
      }
    }
  `
})
export class BookListComponent {
  private apollo = inject(Apollo);

  // watchQuery returns a QueryRef
  // .valueChanges is an Observable<ApolloQueryResult<T>>
  // ApolloQueryResult<T> = { data: T, loading: boolean, error: ApolloError }
  books$ = this.apollo
    .watchQuery<{ books: Book[] }>({ query: GET_BOOKS })
    .valueChanges;
}
```

**`watchQuery` vs `query`:**
- `watchQuery` — subscribes to cache updates; re-emits when cache changes
- `query` — one-shot Observable; completes after first result; no cache subscription

Use `watchQuery` for components that should react to cache updates (e.g., after a mutation updates the same data). Use `query` for one-time reads.

---

### Slide 12 — Apollo Angular — Mutations

**Title:** Apollo Angular — Executing Mutations

**Content:**

```typescript
import { Component, inject } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';

const CREATE_BOOK = gql`
  mutation CreateBook($input: CreateBookInput!) {
    createBook(input: $input) {
      id
      title
    }
  }
`;

@Component({
  selector: 'app-book-form',
  standalone: true,
  template: `
    <form (ngSubmit)="onSubmit()">
      <input [(ngModel)]="title" name="title" placeholder="Book title" />
      <button type="submit">Create Book</button>
    </form>
  `
})
export class BookFormComponent {
  title = '';
  private apollo = inject(Apollo);

  onSubmit(): void {
    this.apollo.mutate<{ createBook: { id: string; title: string } }>({
      mutation: CREATE_BOOK,
      variables: { input: { title: this.title, authorId: '5' } },
      refetchQueries: [{ query: GET_BOOKS }],   // refresh book list
    }).subscribe({
      next: ({ data }) => {
        console.log('Created:', data?.createBook.title);
      },
      error: (err) => {
        console.error('Mutation failed:', err.message);
      }
    });
  }
}
```

**Observable vs Promise:**
`apollo.mutate` returns an `Observable`. To convert to a Promise (for async/await):
```typescript
const result = await this.apollo.mutate({ mutation: CREATE_BOOK, variables }).toPromise();
// Or with firstValueFrom from rxjs:
import { firstValueFrom } from 'rxjs';
const result = await firstValueFrom(this.apollo.mutate({ mutation: CREATE_BOOK, variables }));
```

---

### Slide 13 — Error Handling — Two Types of GraphQL Errors

**Title:** Error Handling — NetworkError vs GraphQLErrors

**Content:**

Apollo Client distinguishes two fundamentally different error categories. Understanding this is essential for correct error handling.

**Type 1 — Network Errors (HTTP/transport-level):**
```typescript
// Occurs when:
// - Server is unreachable (ECONNREFUSED, timeout)
// - CORS headers are missing
// - Server returned HTTP 4xx/5xx (except 200)
// - Request body parsing failed on the server

const { error } = useQuery(GET_BOOKS);
if (error?.networkError) {
  console.error('Network error:', error.networkError.message);
  // error.networkError is a standard JavaScript Error or ServerError
}
```

**Type 2 — GraphQL Errors (returned in response body `errors` array):**
```typescript
// Occurs when:
// - Resolver threw an exception (BookNotFoundException, etc.)
// - Schema validation failed (wrong argument type)
// - Business logic error (@PreAuthorize failed, not found, etc.)
// - Partial failure (some fields resolved, some didn't)

if (error?.graphQLErrors) {
  error.graphQLErrors.forEach(gqlError => {
    console.error('GraphQL error message:', gqlError.message);
    console.error('Error code:', gqlError.extensions?.code);  // "NOT_FOUND", "FORBIDDEN", etc.
    console.error('Location:', gqlError.locations);           // line/column in the query
    console.error('Path:', gqlError.path);                    // which field errored
  });
}
```

**`ApolloError` combines both:**
```typescript
// error from useQuery/useMutation is an ApolloError
// error.message    — combined message string
// error.networkError   — NetworkError | null
// error.graphQLErrors  — ReadonlyArray<GraphQLError>
```

**Remember from Day 31:** GraphQL responses always return HTTP 200. A `networkError` with status code will only appear for non-200 responses (e.g., a 500 from Spring Boot before the GraphQL layer processes the request).

---

### Slide 14 — Error Handling — errorPolicy and the onError Link

**Title:** errorPolicy Options and Global Error Handling

**Content:**

**`errorPolicy` — controls behavior when GraphQL errors arrive alongside partial data:**

```typescript
// 'none' (default) — discard data entirely if any GraphQL errors
// → { data: undefined, error: ApolloError }
const { data, error } = useQuery(GET_BOOKS);

// 'all' — return BOTH data (partial) AND errors
// → { data: { books: [...], errorField: null }, error: ApolloError }
const { data, error } = useQuery(GET_BOOKS, { errorPolicy: 'all' });

// 'ignore' — return data, suppress errors entirely
// → { data: { books: [...] }, error: undefined }
const { data, error } = useQuery(GET_BOOKS, { errorPolicy: 'ignore' });
```

**Global error handling with the `onError` link:**
```typescript
import { onError } from '@apollo/client/link/error';
import { from } from '@apollo/client';

// onError link runs for every operation — centralized handler
const errorLink = onError(({ graphQLErrors, networkError, operation }) => {
  if (graphQLErrors) {
    graphQLErrors.forEach(({ message, extensions }) => {
      // Redirect to login if any operation returns UNAUTHORIZED
      if (extensions?.code === 'UNAUTHORIZED') {
        localStorage.removeItem('authToken');
        window.location.href = '/login';
      }
      console.error(`[GraphQL error] ${operation.operationName}: ${message}`);
    });
  }
  if (networkError) {
    console.error(`[Network error]: ${networkError.message}`);
  }
});

const client = new ApolloClient({
  link: from([errorLink, httpLink]),   // errorLink runs BEFORE httpLink
  cache: new InMemoryCache(),
});
```

---

### Slide 15 — Authentication and Subscriptions

**Title:** Authentication Headers (setContext) and useSubscription

**Content:**

**Adding authentication with the `setContext` link:**
```typescript
import { setContext } from '@apollo/client/link/context';

const authLink = setContext((operation, { headers }) => {
  const token = localStorage.getItem('authToken');
  return {
    headers: {
      ...headers,
      Authorization: token ? `Bearer ${token}` : '',
    },
  };
});

// Chain: authLink → httpLink
const client = new ApolloClient({
  link: authLink.concat(httpLink),
  cache: new InMemoryCache(),
});
```

**`useSubscription` — real-time data:**
```typescript
const BOOK_ADDED = gql`
  subscription OnBookAdded {
    bookAdded { id title author { name } }
  }
`;

function LiveBookFeed() {
  const { data, loading, error } = useSubscription(BOOK_ADDED);
  if (loading) return null;
  if (error) return <p>Subscription error</p>;
  return <div>New book: {data.bookAdded.title}</div>;
}
```

**WebSocket link setup for subscriptions:**
```typescript
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';
import { split } from '@apollo/client';
import { getMainDefinition } from '@apollo/client/utilities';

const wsLink = new GraphQLWsLink(
  createClient({ url: 'ws://localhost:8080/graphql-ws' })
);

// Route: subscriptions → wsLink, everything else → authLink + httpLink
const splitLink = split(
  ({ query }) => {
    const def = getMainDefinition(query);
    return def.kind === 'OperationDefinition' && def.operation === 'subscription';
  },
  wsLink,
  authLink.concat(httpLink),
);

const client = new ApolloClient({ link: splitLink, cache: new InMemoryCache() });
```

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Summary — GraphQL Clients, React & Angular, Error Handling

**Content:**

**Client library comparison:**

| Library | Complexity | Caching | Use When |
|---------|-----------|---------|---------|
| Apollo Client | Medium | Full normalization | Most production apps |
| urql | Low-Medium | Pluggable | Simpler projects |
| Relay | High | Optimized | Large-scale React |
| graphql-request | Minimal | None | Scripts, SSR |

**Apollo Client React hooks:**

| Hook | Purpose | Trigger |
|------|---------|---------|
| `useQuery(QUERY, options)` | Fetch data | Automatic (on mount) |
| `useLazyQuery(QUERY)` | Fetch data | Manual (call trigger fn) |
| `useMutation(MUTATION)` | Write data | Manual (call trigger fn) |
| `useSubscription(SUB)` | Real-time data | Automatic (on mount) |

**Apollo Angular equivalents:**

| Angular | React Equivalent |
|---------|-----------------|
| `apollo.watchQuery(...).valueChanges` | `useQuery(...)` |
| `apollo.query(...)` | `useLazyQuery(...)` |
| `apollo.mutate(...)` | `useMutation(...)` |

**Error types:**
- `networkError` — HTTP/transport-level failure
- `graphQLErrors` — business logic errors from the `errors` array
- Always check **both** — they can occur independently or together

**Part 2 Preview:** Apollo InMemoryCache normalization, fetch policies, updating cache after mutations, optimistic UI, query batching, the N+1 problem and DataLoader, testing with Postman, and GraphiQL/Apollo Sandbox tooling.
