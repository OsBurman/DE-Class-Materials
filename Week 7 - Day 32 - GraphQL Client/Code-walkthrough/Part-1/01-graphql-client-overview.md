# GraphQL Client Libraries — Overview

## Why Do You Need a GraphQL Client?

You *can* call a GraphQL API with plain `fetch` or `axios`:

```js
// Plain fetch — works, but you get nothing for free
const response = await fetch('http://localhost:8080/graphql', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    query: `{ books { id title author { name } } }`
  })
});
const { data, errors } = await response.json();
```

This works, but you lose:
- **Caching** — every identical query hits the network again
- **Normalized store** — no automatic deduplication of objects
- **Loading/error state** — you wire that up manually every time
- **Automatic re-fetching** — no reactivity when data changes
- **Optimistic updates** — no instant UI feedback on mutations
- **DevTools** — no visibility into the cache or in-flight requests
- **Code generation** — no auto-generated TypeScript types from the schema

A dedicated GraphQL client handles all of that for you.

---

## The Major GraphQL Client Libraries

| Library | Framework | Key Features |
|---|---|---|
| **Apollo Client** | React (primary), Vue, Vanilla JS | Normalized InMemoryCache, hooks (`useQuery`, `useMutation`), DevTools |
| **Apollo Angular** | Angular | `Apollo` service, `gql` tag, integrates with Angular DI and observables |
| **urql** | React, Vue, Svelte | Lightweight, exchanges (plugin system), good defaults |
| **Relay** | React only | Facebook's client, strict conventions, compiler-generated queries, best for large scale |
| **graphql-request** | Agnostic | Minimal, no cache — use when you just need to fire a query (server-side, CLIs) |
| **TanStack Query + graphql-request** | React, Vue, Angular | Combine TanStack's caching with graphql-request's simplicity |

### When to choose what:
- **Apollo Client** → React apps, team already knows Redux-style cache thinking, need DevTools
- **Apollo Angular** → Angular apps, want RxJS-native observables
- **urql** → React apps, want something lighter than Apollo with good SSR support
- **Relay** → Large-scale React at Facebook/Meta style, OK with strict conventions
- **graphql-request** → Node.js scripts, Next.js Server Components, quick prototypes

---

## Apollo Client for React — Architecture Overview

```
React Component
     │
     │  useQuery(GET_BOOKS)        ← hook provided by @apollo/client
     ▼
Apollo Client
     │
     ├── InMemoryCache              ← normalized object store (key: __typename + id)
     │       │
     │       └─ cache hit?  ──────► return cached data immediately
     │
     └── HTTP Link                  ← sends POST /graphql if no cache hit
             │
             ▼
         Spring for GraphQL server
```

**Key packages:**
```bash
npm install @apollo/client graphql
```

**Minimum setup:**
```tsx
import { ApolloClient, InMemoryCache, ApolloProvider } from '@apollo/client';

const client = new ApolloClient({
  uri: 'http://localhost:8080/graphql',   // your Spring GraphQL endpoint
  cache: new InMemoryCache(),              // normalized cache
});

// Wrap your app:
<ApolloProvider client={client}>
  <App />
</ApolloProvider>
```

---

## Apollo Angular — Architecture Overview

```
Angular Component
     │
     │  apollo.watchQuery(...)      ← Apollo service, injected via DI
     ▼
Apollo Angular (wraps Apollo Client core)
     │
     ├── InMemoryCache              ← same cache as React version
     │
     └── HttpLink                   ← uses Angular's HttpClient under the hood
             │
             ▼
         Spring for GraphQL server
```

**Key packages:**
```bash
npm install apollo-angular @apollo/client graphql
```

**Minimum setup (`graphql.module.ts`):**
```ts
import { ApolloModule, APOLLO_OPTIONS } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';
import { InMemoryCache } from '@apollo/client/core';

@NgModule({
  imports: [ApolloModule],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: (httpLink: HttpLink) => ({
        cache: new InMemoryCache(),
        link: httpLink.create({ uri: 'http://localhost:8080/graphql' }),
      }),
      deps: [HttpLink],
    },
  ],
})
export class GraphQLModule {}
```

---

## Comparison at a Glance

| | Apollo (React) | Apollo (Angular) |
|---|---|---|
| Query hook/method | `useQuery(QUERY)` | `apollo.watchQuery({ query })` |
| Mutation hook/method | `useMutation(MUTATION)` | `apollo.mutate({ mutation })` |
| Reactivity model | React re-render on state change | RxJS Observable |
| Cache | `InMemoryCache` (shared core) | `InMemoryCache` (shared core) |
| DevTools | Apollo Client DevTools (browser ext) | Apollo Client DevTools (browser ext) |
| Error access | `{ data, loading, error }` from hook | `.valueChanges` observable, catch errors with RxJS |

---

## The `gql` Tag

Both Apollo Client and Apollo Angular use the `gql` tagged template literal to define operations.
It parses the GraphQL string into a DocumentNode at build time — not at runtime — which means:
- Syntax errors caught early
- Enables IDE autocompletion (with GraphQL language plugins)
- Works with code generation tools

```ts
import { gql } from '@apollo/client';     // React
import { gql } from 'apollo-angular';      // Angular

const GET_BOOKS = gql`
  query GetAllBooks {
    books {
      id
      title
      author {
        name
      }
    }
  }
`;
```

> ⚠️ **Watch out:** The `gql` import path differs between React Apollo and Apollo Angular.
> React: `from '@apollo/client'`
> Angular: `from 'apollo-angular'`
> Using the wrong one compiles fine but can cause type mismatches with code-generated types.
