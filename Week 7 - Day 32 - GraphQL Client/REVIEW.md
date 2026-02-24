# Day 32 Review — GraphQL Client
## Quick Reference Guide

---

## 1. GraphQL Client Library Comparison

| Library | Bundle | Caching | Best For |
|---------|--------|---------|---------|
| **Apollo Client** | Medium | Full normalization | Most production apps (React, Angular, Vue) |
| **urql** | Light | Pluggable (exchanges) | Projects needing less opinionation |
| **Relay** | Heavy | Highly optimized | Large-scale React apps (Meta-style schema conventions) |
| **graphql-request** | Minimal | None | Scripts, SSR, paired with React Query |

---

## 2. Apollo Client React — Installation

```bash
npm install @apollo/client graphql

# For subscriptions (WebSocket):
npm install graphql-ws    # graphql-ws protocol client
```

---

## 3. ApolloClient + ApolloProvider Setup

```tsx
// src/main.tsx
import { ApolloClient, InMemoryCache, ApolloProvider, HttpLink } from '@apollo/client';

const httpLink = new HttpLink({ uri: 'http://localhost:8080/graphql' });
const cache = new InMemoryCache();

const client = new ApolloClient({ link: httpLink, cache });

// Wrap the entire app — makes client available via React Context
ReactDOM.createRoot(document.getElementById('root')!).render(
  <ApolloProvider client={client}>
    <App />
  </ApolloProvider>
);
```

---

## 4. gql Template Literal Tag

```typescript
import { gql } from '@apollo/client';

// gql parses query string → GraphQL AST (at module load time)
// ALWAYS define gql documents OUTSIDE the component (module scope)
const GET_BOOKS = gql`
  query GetBooks {
    books { id title author { name } }
  }
`;
// Naming convention: SCREAMING_SNAKE_CASE for gql documents
```

---

## 5. useQuery — Basic Pattern

```typescript
const { loading, error, data } = useQuery(GET_BOOKS);

if (loading) return <p>Loading...</p>;
if (error)   return <p>Error: {error.message}</p>;

return data.books.map(book => <div key={book.id}>{book.title}</div>);
```

**Return states:**

| `loading` | `error` | `data` | State |
|-----------|---------|--------|-------|
| `true` | `undefined` | `undefined` | Initial fetch in flight |
| `false` | `undefined` | result | Success |
| `false` | `ApolloError` | `undefined` | Error (errorPolicy: 'none') |

---

## 6. useQuery — Variables and Options

```typescript
const { loading, error, data, refetch } = useQuery(GET_BOOK, {
  variables: { id: bookId },          // must match declared types in gql document
  skip: !bookId,                       // don't run if bookId is null/undefined
  fetchPolicy: 'cache-and-network',    // cache strategy
  pollInterval: 5000,                  // auto-refetch every 5 seconds
  onCompleted: (data) => { ... },      // callback on success
});

// useLazyQuery — fires imperatively
const [getBook, { loading, data }] = useLazyQuery(GET_BOOK);
getBook({ variables: { id: "1" } });  // call when needed
```

---

## 7. useMutation — Basic Pattern

```typescript
const [createBook, { loading, error, data }] = useMutation(CREATE_BOOK);

// Call the trigger function imperatively (unlike useQuery which is declarative)
const handleSubmit = async () => {
  try {
    const result = await createBook({
      variables: { input: { title, authorId } }
    });
    console.log('Created:', result.data.createBook.id);
  } catch (err) {
    console.error(err);
  }
};
```

---

## 8. useMutation — Options and Patterns

```typescript
const [createBook] = useMutation(CREATE_BOOK, {
  refetchQueries: [{ query: GET_BOOKS }],        // re-run queries after mutation
  awaitRefetchQueries: true,                      // wait for refetch before completion
  onCompleted: (data) => navigate('/books'),      // callback on success
  onError: (error) => toast.error(error.message), // callback on failure
  optimisticResponse: {                           // show result before server confirms
    createBook: { __typename: 'Book', id: 'temp', title }
  },
});
```

---

## 9. useSubscription — Real-Time Data

```typescript
const BOOK_ADDED = gql`
  subscription OnBookAdded {
    bookAdded { id title author { name } }
  }
`;

const { data, loading, error } = useSubscription(BOOK_ADDED);
// useSubscription runs automatically on mount (like useQuery)
// receives server-pushed events over WebSocket
```

**WebSocket link setup:**
```typescript
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';
import { split } from '@apollo/client';
import { getMainDefinition } from '@apollo/client/utilities';

const wsLink = new GraphQLWsLink(createClient({ url: 'ws://localhost:8080/graphql-ws' }));

const splitLink = split(
  ({ query }) => {
    const def = getMainDefinition(query);
    return def.kind === 'OperationDefinition' && def.operation === 'subscription';
  },
  wsLink,                      // subscriptions → WebSocket
  authLink.concat(httpLink),   // everything else → HTTP
);
```

---

## 10. Apollo Angular — Setup

```typescript
// app.config.ts (standalone Angular 17+)
import { provideApollo } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';
import { InMemoryCache } from '@apollo/client/core';
import { inject } from '@angular/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),        // required — Apollo Angular uses HttpClient
    provideApollo(() => {
      const httpLink = inject(HttpLink);
      return {
        link: httpLink.create({ uri: 'http://localhost:8080/graphql' }),
        cache: new InMemoryCache(),
      };
    }),
  ],
};
```

---

## 11. Apollo Angular — Queries

```typescript
@Component({ ... })
export class BookListComponent {
  private apollo = inject(Apollo);

  // watchQuery: subscribes to cache — re-emits on cache updates
  // .valueChanges → Observable<ApolloQueryResult<{ books: Book[] }>>
  books$ = this.apollo
    .watchQuery<{ books: Book[] }>({ query: GET_BOOKS })
    .valueChanges;
}
```

```html
@if (books$ | async; as result) {
  @if (result.loading) { <p>Loading...</p> }
  @if (result.error)   { <p>Error: {{ result.error.message }}</p> }
  @for (book of result.data?.books; track book.id) {
    <div>{{ book.title }}</div>
  }
}
```

**`watchQuery` vs `query`:**
- `watchQuery` — stays alive, re-emits on cache updates
- `query` — one-shot Observable, completes after first result

---

## 12. Apollo Angular — Mutations

```typescript
onSubmit(): void {
  this.apollo.mutate<{ createBook: Book }>({
    mutation: CREATE_BOOK,
    variables: { input: { title: this.title, authorId: '5' } },
    refetchQueries: [{ query: GET_BOOKS }],
  }).subscribe({
    next: ({ data }) => console.log('Created:', data?.createBook),
    error: (err) => console.error(err.message),
  });

  // Or with async/await:
  // const result = await firstValueFrom(this.apollo.mutate({ mutation, variables }));
}
```

---

## 13. Error Handling — Two Error Types

| Type | Cause | Access |
|------|-------|--------|
| **Network error** | HTTP 4xx/5xx, CORS, timeout, unreachable | `error.networkError` |
| **GraphQL errors** | Resolver exception, validation fail, auth denied | `error.graphQLErrors` |

```typescript
const { error } = useQuery(GET_BOOKS);

if (error?.networkError)   { /* HTTP/transport failure */ }
if (error?.graphQLErrors)  {
  error.graphQLErrors.forEach(e => {
    console.error(e.message, e.extensions?.code, e.path);
  });
}
```

**`errorPolicy` options:**

| Value | Behavior |
|-------|----------|
| `'none'` (default) | Discard `data` if any GraphQL errors |
| `'all'` | Return partial `data` AND `errors` |
| `'ignore'` | Return partial `data`, suppress errors |

---

## 14. Authentication — setContext Link

```typescript
import { setContext } from '@apollo/client/link/context';

const authLink = setContext((_, { headers }) => ({
  headers: {
    ...headers,
    Authorization: localStorage.getItem('authToken')
      ? `Bearer ${localStorage.getItem('authToken')}`
      : '',
  },
}));

// Link chain: authLink → httpLink
const client = new ApolloClient({
  link: authLink.concat(httpLink),
  cache: new InMemoryCache(),
});
```

---

## 15. Global Error Handling — onError Link

```typescript
import { onError } from '@apollo/client/link/error';
import { from } from '@apollo/client';

const errorLink = onError(({ graphQLErrors, networkError }) => {
  graphQLErrors?.forEach(({ extensions }) => {
    if (extensions?.code === 'UNAUTHORIZED') {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
  });
  if (networkError) console.error('Network error:', networkError.message);
});

// Compose: errorLink runs before httpLink
const client = new ApolloClient({
  link: from([errorLink, authLink, httpLink]),
  cache: new InMemoryCache(),
});
```

---

## 16. InMemoryCache — Normalization

Apollo stores objects by `__typename + id` as the cache key:
```
"Book:1"   → { id: "1", title: "Clean Code", author: { __ref: "Author:5" } }
"Book:2"   → { id: "2", title: "Refactoring", author: { __ref: "Author:6" } }
"Author:5" → { id: "5", name: "Robert Martin", bio: "..." }
```

- Objects are stored **once** and **referenced** — not duplicated
- Updating `Author:5` updates it everywhere it's referenced
- Apollo automatically adds `__typename` to every query

**Custom key fields:**
```typescript
const cache = new InMemoryCache({
  typePolicies: {
    AuthorBio: { keyFields: ['authorId', 'version'] }, // composite key
    PageInfo:  { keyFields: false },                   // no cache identity (embedded)
  },
});
```

---

## 17. Fetch Policies

| Policy | Behavior | Use Case |
|--------|----------|---------|
| `cache-first` | Cache → network if missing | Static data (default) |
| `cache-and-network` | Return cache + refetch in background | Lists, feeds |
| `network-only` | Always network, update cache | Critical fresh data |
| `no-cache` | Always network, never cache | Sensitive data |
| `cache-only` | Cache only, error if missing | Offline mode |

```typescript
useQuery(GET_BOOKS, {
  fetchPolicy: 'cache-and-network',   // per-query
  nextFetchPolicy: 'cache-first',     // after first load
});

// Or set client-wide default:
new ApolloClient({
  defaultOptions: { watchQuery: { fetchPolicy: 'cache-and-network' } }
});
```

---

## 18. Updating Cache After Mutations — refetchQueries

```typescript
// Simple: re-run queries, always fresh, costs a network request
const [createBook] = useMutation(CREATE_BOOK, {
  refetchQueries: [
    { query: GET_BOOKS },
    'GetFeaturedBooks',                // by operation name
  ],
  awaitRefetchQueries: true,
});
```

---

## 19. Updating Cache After Mutations — update Function

```typescript
// Manual: no extra network request, more code
const [createBook] = useMutation(CREATE_BOOK, {
  update(cache, { data }) {
    const newBook = data?.createBook;
    if (!newBook) return;
    const existing = cache.readQuery<{ books: Book[] }>({ query: GET_BOOKS });
    cache.writeQuery({
      query: GET_BOOKS,
      data: { books: [...(existing?.books ?? []), newBook] },
    });
  },
});

// For delete — evict the specific entry:
const [deleteBook] = useMutation(DELETE_BOOK, {
  update(cache, { data }) {
    cache.evict({ id: cache.identify({ __typename: 'Book', id: data.deleteBook.id }) });
    cache.gc();  // remove dangling references
  },
});
```

---

## 20. Optimistic UI

```typescript
const [updateBook] = useMutation(UPDATE_BOOK, {
  optimisticResponse: {
    updateBook: {
      __typename: 'Book',   // REQUIRED — cache key
      id: bookId,           // REQUIRED — cache key
      title: newTitle,      // assumed success
    },
  },
  // Apollo rolls back automatically if mutation fails
});
```

**Timing:** write to cache immediately → UI updates → server responds → reconcile. If failure, rollback.

---

## 21. N+1 Problem

```
Query: { books { title author { name } } }

Without DataLoader:
  1 query for books (returns N books)
  N queries for authors (one per book)
  Total: N+1 queries

Example: 100 books → 101 SQL queries per GraphQL request
```

**Root cause:** `@SchemaMapping` is called independently per parent object — it cannot see the other waiting parents.

---

## 22. DataLoader — How It Works

DataLoader batches all `load(id)` calls made within one event loop tick into a single batch function call.

```
Collecting phase (same tick):
  Book 1: load(authorId=10) → queued
  Book 2: load(authorId=11) → queued
  Book 3: load(authorId=10) → deduplicated
  ...

Dispatch phase (end of tick):
  Batch fn called with: [10, 11, 12, ...]  (unique IDs only)
  → 1 SQL: SELECT * FROM authors WHERE id IN (10, 11, 12, ...)
  → Map distributed back to each waiting resolver
```

**Result:** 2 queries regardless of N (books + authors batch).
**Per-request cache:** same ID loaded twice in one request → second call returns cached future.

---

## 23. @BatchMapping — Spring for GraphQL Implementation

```java
// One-to-one: Book → Author
@BatchMapping(typeName = "Book", field = "author")
public Map<Book, Author> author(List<Book> books) {
    Set<Long> authorIds = books.stream()
        .map(Book::getAuthorId).collect(Collectors.toSet());

    Map<Long, Author> authorsById = authorService.findAllByIds(authorIds).stream()
        .collect(Collectors.toMap(Author::getId, a -> a));

    return books.stream()
        .collect(Collectors.toMap(book -> book, book -> authorsById.get(book.getAuthorId())));
}

// One-to-many: Book → List<Review>
@BatchMapping(typeName = "Book", field = "reviews")
public Map<Book, List<Review>> reviews(List<Book> books) {
    List<Long> bookIds = books.stream().map(Book::getId).collect(Collectors.toList());
    Map<Long, List<Review>> reviewsByBookId = reviewService.findAllByBookIds(bookIds)
        .stream().collect(Collectors.groupingBy(Review::getBookId));

    return books.stream().collect(Collectors.toMap(
        book -> book,
        book -> reviewsByBookId.getOrDefault(book.getId(), Collections.emptyList())
    ));
}
```

**Requirements:**
- Parent type (`Book`) must correctly implement `equals()` and `hashCode()`
- Use Java records or `@EqualsAndHashCode` (Lombok) on entity classes

---

## 24. Testing in Postman

```
Setup:
1. POST http://localhost:8080/graphql
2. Body → GraphQL tab
3. Write operation in query field
4. Write variables JSON in variables panel
5. Authorization tab → Bearer Token for auth

Import schema via introspection:
  Collections → New → GraphQL → enter URL
  → Postman generates collection from schema

Save as Collection → share with team or export to git
```

---

## 25. GraphiQL — Reference

Access: `http://localhost:8080/graphiql` (requires `spring.graphql.graphiql.enabled=true`)

| Feature | How to Use |
|---------|-----------|
| Schema Explorer | Click 📚 icon — browse all types, fields, arguments |
| Autocomplete | `Ctrl+Space` / `Cmd+Space` |
| Run query | `Ctrl+Enter` / `Cmd+Enter` |
| Prettify | `Shift+Ctrl+P` |
| Variables | Bottom-left panel |
| Headers | Bottom tab (for Authorization) |
| History | Clock icon — previous queries |
| Multiple ops | Declare named operations; use ▶ dropdown |

---

## 26. Apollo Sandbox and Altair

| Tool | Access | Best For |
|------|--------|---------|
| **GraphiQL** | `localhost:8080/graphiql` | Development iteration |
| **Postman** | App | Testing collections, CI/CD, team sharing |
| **Apollo Sandbox** | `sandbox.apollo.dev` | Apollo Studio, field analytics |
| **Altair** | App/extension | Subscriptions, pre-request scripts, environments |

---

## 27. Writing Effective GraphQL Queries — Reference

```graphql
# ✅ Always use named operations
query GetBookList { books { id title author { name } } }

# ✅ Request only fields you render
query GetBookList { books { id title author { name } } }   # not isbn, bio, etc.

# ✅ Use __typename for debugging
query Debug { book(id: "1") { __typename id } }

# ✅ Use fragments for DRY queries
fragment BookItem on Book { id title author { name } }
query GetBooks { books { ...BookItem } }
query SearchBooks { searchBooks(query: $q) { ...BookItem } }
```

---

## 28. Debugging Strategies

1. **Start minimal:** `{ book(id: "1") { id } }` → add fields one at a time
2. **Check `errors` array** in every response — partial success is real
3. **Use `__typename`** to verify type resolution
4. **Use named operations** — visible in server logs and APM
5. **Apollo DevTools** browser extension — inspect cache, replay queries
6. **Test in GraphiQL first** — validate server works before debugging client code

---

## 29. Nested Queries and Pagination

```graphql
# Nested — one request, multiple levels
query GetBook($id: ID!) {
  book(id: $id) {
    title
    author { name books { title } }
    reviews(minRating: 4) { rating reviewer { name } }
  }
}
```

**Cursor-based pagination with fetchMore:**
```typescript
const { data, fetchMore } = useQuery(GET_BOOKS_PAGE);

const loadMore = () => {
  fetchMore({
    variables: { cursor: data?.books.pageInfo.endCursor },
    updateQuery: (prev, { fetchMoreResult }) => ({
      books: {
        ...fetchMoreResult.books,
        edges: [...prev.books.edges, ...fetchMoreResult.books.edges],
      },
    }),
  });
};
```

---

## 30. Quick Comparison: React Hooks vs Angular Services

| React | Angular | Purpose |
|-------|---------|---------|
| `useQuery(QUERY)` | `apollo.watchQuery({ query }).valueChanges` | Fetch data (reactive) |
| `useLazyQuery(QUERY)` | `apollo.query({ query })` | Fetch data (imperative) |
| `useMutation(MUTATION)` | `apollo.mutate({ mutation })` | Write data |
| `useSubscription(SUB)` | Apollo Angular WebSocket link | Real-time data |
| `ApolloProvider` | `provideApollo()` in app.config.ts | Client setup |
