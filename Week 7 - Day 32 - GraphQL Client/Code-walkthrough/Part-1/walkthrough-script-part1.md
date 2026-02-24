# Day 32 — GraphQL Client: Part 1
## Instructor Walkthrough Script

**Duration:** ~90 minutes  
**Format:** Live code walkthrough + slides  
**Files referenced:**
- `01-graphql-client-overview.md`
- `02-apollo-react-client.tsx`
- `03-apollo-angular-client.ts`

---

## SECTION 1 — Opening & Context Setting (5 min)

**Say:**
> "Good morning everyone. Yesterday we built a full GraphQL server with Spring for GraphQL — schema, resolvers, mutations, subscriptions. Today we flip to the other side of the wire: the client."

> "By the end of today you'll be able to wire up a React app or an Angular app to consume a GraphQL API, run queries, execute mutations, and handle errors properly. We'll also cover how to test GraphQL APIs with Postman and how to think about performance."

> "One quick question before we start — how would you call a GraphQL API with just `fetch`, with no library at all? …Right, you'd POST to `/graphql` with a JSON body containing a `query` string. That works. So why do we need a client library at all?"

**[Pause for responses, then continue:]**

> "Exactly — plain fetch works, but you lose caching, loading states, error parsing, and developer tooling. A proper GraphQL client gives you all of that for free. Let's look at what's available."

---

## SECTION 2 — GraphQL Client Libraries Overview (12 min)

**Open `01-graphql-client-overview.md`**

**Say:**
> "This file is your reference — the landscape of GraphQL client libraries. I want to walk through this table so you know which tool to reach for in different situations."

**Point to the comparison table:**
> "There are five main players. **Apollo Client** is the industry standard for React. Large ecosystem, excellent DevTools, mature caching. **Apollo Angular** is the same core engine adapted for Angular — it uses Angular's `HttpClient` and returns RxJS Observables instead of Promises."

> "Then there's **urql** — lighter weight than Apollo, uses a plugin architecture called 'exchanges' — think middleware for GraphQL. Good choice when Apollo feels like overkill."

> "**Relay** is Facebook's own client. It's extremely powerful but has strict conventions — you write queries in a specific way, run a compiler step, and Relay generates optimized code. Great for massive apps, but steep learning curve."

> "Finally, **graphql-request** — minimal, no cache, just fires a query and returns data. Use this in Node.js scripts, server-side Next.js components, or quick one-off calls. Not for UI components."

**Ask the class:**
> "If you were starting a React project that needs to show real-time data with proper caching — which would you reach for? …Apollo Client. What if you just needed to call the API from a cron job script? …graphql-request."

**Point to the `gql` tag section:**
> "Before we dive into code, one important piece of syntax: the `gql` tagged template literal. Both Apollo flavors use it. It parses your GraphQL string into a structured DocumentNode at module load time — not every render. This means syntax errors surface at startup, not at runtime, and it enables IDE autocompletion with the right plugins."

> "⚠️ Watch out: the import path is different for React vs Angular. React uses `from '@apollo/client'`. Angular uses `from 'apollo-angular'`. This compiles fine either way, but code generation tools will produce wrong types if you use the wrong import. Memorize this distinction."

---

## SECTION 3 — Apollo Client Setup for React (15 min)

**Open `02-apollo-react-client.tsx` — scroll to Section 1 (client.ts)**

**Say:**
> "Alright, let's build the React side. The first thing you always do is create the Apollo Client instance. Think of this as your database connection object — you create it once and share it everywhere."

**Point to the `errorLink`:**
> "We're composing two links here — a link in Apollo is middleware for your GraphQL requests. The `errorLink` intercepts every request and logs errors globally. This is your catch-all. Even if individual components handle their own errors, this gives you centralized logging."

> "Notice it distinguishes between `graphQLErrors` and `networkError`. That's a critical distinction we'll come back to. Network errors mean the request never reached the server. GraphQL errors mean the server responded but reported problems."

**Point to `HttpLink`:**
> "The `httpLink` is the actual transport — it takes the GraphQL operation and POSTs it to your server endpoint. You set headers here if you need auth."

**Point to `ApolloClient`:**
> "The client combines both links using `from([errorLink, httpLink])` — links run in order, left to right. Then we give it a `new InMemoryCache()` — that's the normalized object store. The `typePolicies` tell it how to identify objects uniquely in the cache using `__typename + id`."

**Point to the commented ApolloProvider section:**
> "You wrap your entire React app in `ApolloProvider` — just like React Router's `BrowserRouter` or Redux's `Provider`. This makes the Apollo client available to every component via context. Do this once at the root, never repeat it."

---

## SECTION 4 — Consuming GraphQL APIs: useQuery (15 min)

**Scroll to `BookList.tsx` (Section 3)**

**Say:**
> "Now the fun part — consuming the API. This is the `BookList` component. It displays all books."

**Point to the `useQuery` call:**
> "The `useQuery` hook is the heart of Apollo React. You pass it the `GET_ALL_BOOKS` document node we defined earlier. It immediately starts the query — either from cache or from the network. It returns three things you always need: `data`, `loading`, and `error`."

**Ask:**
> "Before I explain the render logic — what do you think `data` equals on the very first render, before the request completes? …Right, it's `undefined`. This is the most common bug with Apollo beginners: accessing `data.books` before checking `loading`."

**Point to the loading check:**
> "Always handle `loading` first. `data` is undefined here — if you try to access `data.books` before this guard, you'll get 'Cannot read property books of undefined'."

**Point to the error check:**
> "Then handle `error`. Notice we check `error.networkError` first — if the server is down, there are no GraphQL errors to report. Then we map `error.graphQLErrors` — it's an array, because a single response can contain multiple field-level errors."

**Point to the success render:**
> "Finally, the happy path. Notice we use optional chaining: `data?.books ?? []`. Never assume `data` is populated — use defensive access."

**Point to `refetch`:**
> "`useQuery` also returns a `refetch` function. Calling it re-runs the query against the network, bypassing the cache. We wire that to the Refresh button."

**Scroll to `BookDetail.tsx` (Section 4)**

> "One more pattern: `useLazyQuery`. The regular `useQuery` runs immediately when the component mounts. `useLazyQuery` gives you a function you call manually — use this when the query depends on user input, like a search field. You call the function, pass variables, and it fires."

---

## SECTION 5 — Mutations with useMutation (15 min)

**Scroll to `AddBookForm.tsx` (Section 5)**

**Say:**
> "Mutations work differently from queries. Queries run automatically. Mutations wait for you to pull the trigger."

**Point to `useMutation`:**
> "`useMutation` returns a tuple: the mutation function and the result object. The result object has the same `data`, `loading`, `error` pattern as queries."

**Point to `refetchQueries`:**
> "After we add a book, we need the `BookList` to update. The simplest way: `refetchQueries: [{ query: GET_ALL_BOOKS }]`. This tells Apollo: 'after this mutation succeeds, re-run this query.' It's a round trip, but it's guaranteed fresh data."

> "We'll see a more efficient approach in Part 2 — manually updating the cache. But for understanding, `refetchQueries` is the right place to start."

**Point to `onCompleted` and `onError`:**
> "`onCompleted` fires after a successful mutation — perfect for success toasts, form resets, navigation. `onError` fires on failure — use it for error state management."

**Point to `handleSubmit` and the try/catch:**
> "The mutation function also returns a Promise, so you can await it and wrap it in try/catch. Both `onError` and catch will fire for errors. In practice, pick one approach — usually `onError` for UI state, catch as a safety net."

**Scroll to `DeleteBookButton.tsx` (Section 6)**

> "The last pattern is optimistic updates. Instead of waiting for the server to confirm deletion, we tell Apollo what the server's response will look like — the `optimisticResponse`. Apollo immediately applies it to the cache, the UI updates instantly, and if the server says something different, Apollo rolls it back. This makes deletes feel instant even on slow connections."

**Ask:**
> "When would you NOT want an optimistic update? …When the result is unpredictable — like generating a server-side ID, or when the mutation could fail in ways the client can't anticipate. Don't optimistically update financial transactions."

---

## SECTION 6 — Apollo Angular (15 min)

**Open `03-apollo-angular-client.ts` — Section 1 (GraphQLModule)**

**Say:**
> "Now the Angular side. The concepts are identical, but the implementation is Angular-flavored — Dependency Injection, Observables, modules."

**Point to `createApolloOptions`:**
> "The setup function creates the same error link + HTTP link combination, and the same `InMemoryCache`. The difference: `HttpLink` from Apollo Angular uses Angular's `HttpClient` under the hood, which means Angular's HTTP interceptors work on your GraphQL requests — great for adding auth tokens."

**Point to the `@NgModule` providers:**
> "The `APOLLO_OPTIONS` injection token is how Apollo Angular integrates with Angular's DI system. The factory function receives `HttpLink` as a dependency. No `new HttpLink()` — Angular's DI handles instantiation."

> "Import `GraphQLModule` in your `AppModule`. It makes the `Apollo` service available everywhere in the app."

**Scroll to Section 3 (BookService)**

> "The Angular best practice is to put GraphQL logic in a service, not directly in components. This separates concerns and makes testing easier."

**Point to `apollo.watchQuery()`:**
> "`watchQuery` is the Angular equivalent of `useQuery` — it returns a live Observable that emits whenever the cache updates. You subscribe to `.valueChanges` to get the actual data stream."

> "The `map` operator extracts `result.data.books`. The `catchError` operator handles network-level errors."

**Point to `apollo.query()`:**
> "For one-shot queries where you don't need live updates — like a detail page — use `apollo.query()` instead of `watchQuery()`. It returns a single-emit Observable."

**Scroll to `BookListComponent` (Section 4)**

> "In the component, we inject `BookService` and subscribe in `ngOnInit`. The error handler checks `err.networkError` vs `err.graphQLErrors` — same pattern as React, just different Angular plumbing."

**Point to the async pipe comment:**
> "A cleaner Angular pattern is to expose an Observable property and use the `async` pipe in the template: `*ngIf='books$ | async as books'`. The async pipe subscribes and unsubscribes automatically — no memory leaks from forgotten `unsubscribe` calls."

**Scroll to `AddBookComponent` (Section 5)**

> "Mutations in Angular: `apollo.mutate()` returns a one-shot Observable. Subscribe once, and you get either a next with the data or an error. Notice `awaitRefetchQueries: true` in the service — the Observable won't complete until the refetch also finishes, keeping the UI in sync."

---

## SECTION 7 — Wrap-Up Part 1 (5 min)

**Say:**
> "Let's summarize Part 1:"
> - "GraphQL clients give you caching, loading states, error handling, and DevTools that plain fetch doesn't"
> - "Apollo Client for React: `ApolloProvider` at root, `useQuery` for data, `useLazyQuery` for on-demand, `useMutation` for writes"
> - "Apollo Angular: `GraphQLModule` for setup, `Apollo` service injected via DI, `watchQuery` returns Observables"
> - "Both: `gql` tagged template literals, `InMemoryCache`, `refetchQueries` for post-mutation refresh, error objects with `networkError` and `graphQLErrors`"

---

### 🎯 Quick-Check Questions

1. **"What are the three values returned by `useQuery`?"**
   > *data, loading, error*

2. **"What's the difference between `useQuery` and `useLazyQuery`?"**
   > *`useQuery` runs on mount. `useLazyQuery` runs when you call the returned function.*

3. **"What does `refetchQueries` do after a mutation?"**
   > *Re-runs the listed queries against the network to refresh the cache.*

4. **"What's the Angular equivalent of Apollo's `useQuery` hook?"**
   > *`apollo.watchQuery()` + `.valueChanges` Observable subscription.*

5. **"Name two categories of errors in an `ApolloError`."**
   > *`networkError` (server unreachable) and `graphQLErrors` (server responded with errors).*

---

*End of Day 32 — Part 1 Script. Break before Part 2.*
