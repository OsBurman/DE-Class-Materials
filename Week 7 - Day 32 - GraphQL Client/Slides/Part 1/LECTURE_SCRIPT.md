# Day 32 Part 1 — GraphQL Client Libraries, Apollo Client (React & Angular), and Error Handling
## Lecture Script

---

**[00:00–03:00] Opening — From Server to Client**

Good morning and welcome to Day 32. Yesterday we built the server. We spent the entire day on the Spring Boot side — defining schemas with SDL, implementing resolvers with `@QueryMapping` and `@MutationMapping` and `@SchemaMapping`, setting up subscriptions, and designing schemas with best practices. By the end of Day 31 you had a working GraphQL API listening at `POST /graphql`.

Today we switch to the other side of the wire. The frontend. How does a React component or an Angular service actually talk to that GraphQL endpoint? How does it send a query, get the result, handle loading states, react to errors, and keep the UI in sync when data changes?

Part 1 covers the fundamentals: what GraphQL client libraries exist, how to choose between them, and then a focused deep-dive into Apollo Client — setting it up in React, writing queries with `useQuery`, mutations with `useMutation`, subscriptions with `useSubscription`, setting up the equivalent services in Angular, and the error handling model. Part 2 is where we go deeper on caching, the N+1 DataLoader problem, and developer tooling for testing and debugging.

---

**[03:00–10:00] Slide 2 — GraphQL Client Libraries**

Let's start with a question: do you even need a library? GraphQL is just HTTP. If I want to call that endpoint right now, I can write about eight lines of JavaScript. `fetch('/graphql', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ query: "...", variables: {} }) })` and then `.then(r => r.json())` and I have my `{ data, errors }` object.

That works. And for a Node.js script or a server-side rendering scenario where you just need to make one call and move on, raw fetch plus a tiny library called `graphql-request` is absolutely fine.

But in a React or Angular application, that's where things get complicated fast. You need to track loading state — is the query in flight right now? You need error state — did something go wrong, and was it a network failure or a server-side validation error? You need to integrate with React's state or Angular's change detection so the UI re-renders when data arrives. You need caching — you queried the books list, then navigated to a book detail, then came back to the list. Did you fetch it again? Or is it still in memory? And if you created a new book in the meantime, is the cached list stale? These are the problems a GraphQL client library solves.

The four main options. Apollo Client is the most widely used. It has a full-featured normalized in-memory cache, React hooks, Angular services, Vue composables, excellent developer tools, TypeScript support, and the best documentation in the ecosystem. It's the right default choice for most production applications. We're using it for this course.

urql is a lighter alternative. It uses a concept called "exchanges" — a composable middleware pipeline — which makes it very flexible. The caching layer is simpler than Apollo's by default but can be extended. If your team finds Apollo too opinionated or the bundle size too large, urql is worth evaluating.

Relay is Meta's GraphQL client, open-sourced. It's highly opinionated — it requires your schema to follow specific conventions (the Relay Connection pattern for pagination), it compiles queries at build time for performance, and it has a learning curve. It's optimized for very large React applications. If you're building something at Meta's scale, Relay is what you'd reach for. For most applications, it's more burden than benefit.

graphql-request is the minimalist. It wraps the raw fetch pattern I just described, adds TypeScript generics, and gives you one function: `request`. No caching, no React integration, no subscriptions. But it's tiny. It's commonly paired with React Query for server-state management where React Query handles the loading/caching layer and graphql-request handles the actual HTTP call. That's a valid pattern, especially for projects already using React Query for REST endpoints.

For this course, Apollo Client. It's what you'll encounter most in job listings, it has the most learning resources, and its model — normalize data in a cache, use hooks to subscribe to that cache — is a pattern worth understanding deeply.

---

**[10:00–18:00] Slides 3–5 — Apollo Client React Setup**

Let's install it. `npm install @apollo/client graphql`. Two packages. `@apollo/client` is everything — the client, the cache, the hooks, the link system. `graphql` is the parsing engine, a required peer dependency. That's it for queries and mutations.

For subscriptions over WebSocket you'll add `graphql-ws` and use `GraphQLWsLink` from `@apollo/client/link/subscriptions`. I'll show that on the subscriptions slide.

Once installed, you need three things to get Apollo running in a React application: an `ApolloClient`, an `InMemoryCache`, and an `ApolloProvider`.

The `ApolloClient` is the central coordinator. It needs to know two things: where to send requests, and how to store results. You configure the `link` option — the transport layer — and the `cache` option — the storage layer.

For the link, the simplest option is just providing `uri: 'http://localhost:8080/graphql'` and Apollo creates an `HttpLink` automatically. Or you can be explicit and create an `HttpLink` yourself with `new HttpLink({ uri: '...' })`. The explicit approach is necessary later when you need to chain multiple links together — for authentication headers, error handling, batching, subscriptions. We'll add links throughout today.

The `InMemoryCache` is Apollo's normalized in-memory data store. We'll go deep on how it works in Part 2. For now: it caches query results and Apollo reads from it before making network requests when possible.

Once you have a client, you wrap your React application in `ApolloProvider`. This uses React Context to make the client available to every component in the tree. You put it in `main.tsx` or `index.tsx` — whatever your root render file is. The entire app, including `App`, sits inside `<ApolloProvider client={client}>`. From that point on, any component anywhere can call `useQuery` or `useMutation` and they all share the same client and the same cache.

Now let's talk about `gql`. This is a template literal tag function exported from `@apollo/client`. When you write `gql` with a backtick string, it parses your GraphQL query string into an AST — an Abstract Syntax Tree — at module load time. The result is a plain JavaScript object with a `kind: 'Document'` shape that Apollo knows how to work with internally.

The critical rule: define your `gql` documents outside your component, at module scope. If you define them inside the component function body, a new AST object is created on every render. Apollo uses object reference equality as part of its query deduplication and caching logic. New object reference every render means Apollo can't reuse previous results correctly. Define them outside, once, as module-level constants.

Convention is to name them in SCREAMING_SNAKE_CASE: `GET_BOOKS`, `GET_BOOK`, `CREATE_BOOK`, `UPDATE_BOOK`, `DELETE_BOOK`. This makes it obvious what they are when you scan the file.

---

**[18:00–28:00] Slides 6–7 — useQuery**

`useQuery` is the hook you'll use more than anything else in a React GraphQL application. You pass it a `gql` document — a query — and it returns an object with three essential fields: `loading`, `error`, and `data`.

`loading` is `true` while the first network request is in flight. Once data arrives or an error occurs, it flips to `false`. Note "first network request" — on subsequent refetches, `loading` stays `false` and Apollo uses a separate `networkStatus` field for more granular state.

`error` is either an `ApolloError` object or `undefined`. If it's defined, something went wrong. We'll discuss what can go wrong in detail when we get to error handling.

`data` is the query result. Its shape exactly mirrors your selection set. If you asked for `books { id title author { name } }`, then `data.books` is an array of objects with `id`, `title`, and `author.name`. TypeScript types for this can be generated from the schema — tools like `graphql-codegen` read your schema and generate TypeScript interfaces automatically. That's a production workflow worth knowing about even if we won't set it up today.

The three states you'll always handle: loading spinner while `loading` is true, error message while `error` is defined, and the actual content when `data` is available. This pattern is essentially the same across every `useQuery` call in your application.

One important behavior: `useQuery` runs automatically when the component mounts. It's declarative. You don't call a function — you just declare "this component needs this data" and Apollo handles fetching, caching, and providing it.

Now variables. Most queries in real applications are parameterized — fetch a specific book by ID, search with a query string, paginate. Variables are passed as the `variables` property in the options object: `useQuery(GET_BOOK, { variables: { id: bookId } })`. The variable names and types must match what you declared in the `gql` document. If your document says `query GetBook($id: ID!)`, your variables object must have `{ id: "..." }`.

The `skip` option is important. Sometimes the variable isn't ready yet — say `bookId` is passed as a prop and it's `null` before the parent loads. If you call `useQuery(GET_BOOK, { variables: { id: null } })` without skip, Apollo will send the query with a null ID and you'll get a validation error. `skip: !bookId` prevents the query from running at all when `bookId` is falsy. No network request, no loading state — the query is dormant until the condition becomes true.

There's also `refetch`, which the hook returns alongside `loading`, `error`, and `data`. Calling `refetch()` triggers a fresh network request immediately. Useful for a "Refresh" button or when you know the data has changed server-side and want to sync.

And `pollInterval` — if you set `pollInterval: 5000`, Apollo automatically refetches the query every five seconds. Useful for dashboards or lists that should stay roughly up to date without full subscriptions.

---

**[28:00–38:00] Slides 8–9 — useMutation**

`useMutation` is the hook for write operations. Its API is different from `useQuery` in an important way: mutations are imperative. You get back a trigger function, and you call it when you want the mutation to execute — typically in response to a user action like form submission.

The hook returns a tuple: `[mutationFn, resultObject]`. The mutation function is the trigger. `resultObject` has the same `loading`, `error`, `data` fields as `useQuery`, but they reflect the state of the mutation — `loading` is true while the mutation is in flight, `data` is the mutation's return value, `error` is any failure.

When you call `mutationFn`, you pass the variables: `createBook({ variables: { input: { title, authorId } } })`. The mutation function returns a Promise that resolves with the result object. So you can `await` it and use the result synchronously in the handler.

Using async/await: wrap the call in `try/catch`. Network errors are thrown and land in `catch`. GraphQL errors may land in `catch` or in the `error` field depending on `errorPolicy` — by default, GraphQL errors are also thrown.

`onCompleted` and `onError` are callback options you can pass at the hook level. `onCompleted` fires when the mutation succeeds — useful for navigation: create a book, then navigate to the book detail page. `onError` fires on failure — useful for showing a toast notification. These callbacks run alongside the Promise resolution, not instead of it.

`refetchQueries` is a mutation option that re-runs specified queries after the mutation completes. The most common use case: you create a book, and you want the books list to refresh. Pass `refetchQueries: [{ query: GET_BOOKS }]` and Apollo automatically re-runs that query. It's straightforward and reliable. The downside is an extra network round-trip. In Part 2, we'll see how to update the cache manually to avoid that.

Optimistic UI: `optimisticResponse` lets you tell Apollo what the mutation result will likely be before the server responds. Apollo immediately writes that to the cache, updates the UI, and then reconciles with the real server response when it arrives. If the mutation fails, Apollo automatically rolls back the optimistic update. The user sees instant feedback instead of a loading state. The catch: you need to know what `__typename`, `id`, and other cache key fields to provide so Apollo can correctly key the optimistic entry in the cache.

---

**[38:00–50:00] Slides 10–12 — Apollo Angular**

Let's look at the Angular side. Apollo Angular is a separate package from `@apollo/client` — it's an Angular-specific adapter that wraps the Apollo Client core with Angular idioms: `inject()`, `HttpClient` for transport, Observables for results. The underlying cache, normalization, and link system are identical — the `@apollo/client` package is a dependency of `apollo-angular`.

Installation: `npm install apollo-angular @apollo/client graphql`. Three packages.

Setup in a modern standalone Angular application uses `provideApollo` in `app.config.ts`. This is the equivalent of wrapping with `ApolloProvider` in React. Inside the factory function, you inject `HttpLink` from `apollo-angular/http` and call `httpLink.create({ uri: '...' })` to create the transport. Apollo Angular uses Angular's `HttpClient` under the hood — which means it participates in `HttpClientModule` interceptors, testing utilities, and Angular's dependency injection. That's a nice advantage over Apollo Client's native `HttpLink`, which uses raw `fetch`.

`provideHttpClient()` must be in the providers array — this registers Angular's `HttpClient`.

Once set up, you use Apollo via `inject(Apollo)` in your components and services. The `Apollo` service is injectable anywhere in the component tree.

For queries, `this.apollo.watchQuery({ query: GET_BOOKS }).valueChanges` returns an `Observable<ApolloQueryResult<T>>`. The generic type parameter `T` is the shape of your response data — typically an interface like `{ books: Book[] }`. You declare this at the watchQuery call site.

`ApolloQueryResult<T>` has `data`, `loading`, and `error` — the same three fields as in React. In the Angular template, you use the `async` pipe to subscribe to the Observable and handle the latest emission. The modern Angular control flow syntax (`@if`, `@for`) works cleanly with the async pipe pattern.

`watchQuery` keeps the Observable alive and re-emits whenever the Apollo cache updates the relevant data. If a mutation elsewhere in the app writes to the same normalized cache entries, `watchQuery` subscribers will receive the updated data automatically. This is the Apollo cache update model — normalized data, single source of truth.

`apollo.query` is the alternative — it's a one-shot Observable that completes after the first result. Use it for imperative data fetching where you don't need the component to react to future cache updates.

For mutations, `this.apollo.mutate({ mutation: CREATE_BOOK, variables: {...} })` returns an `Observable<MutationResult<T>>`. You subscribe to it with `.subscribe({ next, error })`, or convert to a Promise with `firstValueFrom` from RxJS if you prefer async/await in event handlers. Both patterns are valid — use whichever fits your team's convention.

Same options as React: `refetchQueries`, `update` cache function, `optimisticResponse`. The API surface is nearly identical because the core library is the same. The difference is just the delivery mechanism — Observables instead of hooks.

---

**[50:00–58:00] Slides 13–15 — Error Handling, Authentication, Subscriptions**

Error handling in GraphQL clients requires a mental model shift from REST. In REST, you check the HTTP status code. 200 means success, 404 means not found, 500 means server error. In GraphQL, the HTTP status is almost always 200 — including when things go wrong. The errors are in the response body. So Apollo Client distinguishes two error categories.

Network errors are HTTP-level failures — the server returned a non-200 status, the request timed out, CORS blocked the request, the server was unreachable. These are cases where the GraphQL layer never ran at all. You access them via `error.networkError`.

GraphQL errors are returned in the response body's `errors` array. They're business-logic errors: a resolver threw an exception, a required argument was missing, authorization failed, a resource wasn't found. The server processed the request and intentionally returned an error. You access them via `error.graphQLErrors` — an array because multiple fields can fail in a single response.

`ApolloError` wraps both. When you get `error` back from `useQuery` or `useMutation`, it's an `ApolloError` object with both `networkError` and `graphQLErrors` properties. Check both.

`errorPolicy` is the per-operation setting that controls what happens when GraphQL errors arrive alongside partial data. The default `'none'` discards `data` entirely if any errors are present — you get `error` but no `data`. `'all'` returns both — partial data AND the errors array. `'ignore'` returns partial data and suppresses the error entirely. For most cases, `'none'` is correct. For dashboards or read-heavy UIs where showing partial results is better than showing nothing, `'all'` is the right choice.

The `onError` link is for global, application-wide error handling. It's a piece of Apollo Link middleware that intercepts every response before it reaches your component hooks. The most common use case: if any operation returns `UNAUTHORIZED` in `extensions.code`, clear the token and redirect to the login page. You wire it in: `from([errorLink, httpLink])` — the `from` function composes links in order. The `errorLink` runs first, inspects the response, and then passes control to `httpLink`.

Authentication: the `setContext` link adds headers to every outgoing request. `setContext` takes a callback that receives the current request context and returns a modified context — typically with an `Authorization` header added. `setContext` returns a new Link, which you chain before the `httpLink` with `.concat()`.

For subscriptions, you need a WebSocket link alongside your HTTP link. `GraphQLWsLink` from `@apollo/client/link/subscriptions` wraps a `graphql-ws` client instance pointed at your server's WebSocket path — `ws://localhost:8080/graphql-ws`, which is the `spring.graphql.websocket.path` you configured on the server.

The `split` function routes operations: subscriptions go to the WebSocket link, everything else goes to the HTTP link. `getMainDefinition` from `@apollo/client/utilities` inspects the operation definition and returns `'subscription'` for subscription operations. The `split` acts as a router — `wsLink` for subscriptions, `authLink.concat(httpLink)` for everything else.

`useSubscription` in React works like `useQuery` — declarative, runs on mount, returns `loading`, `error`, `data`. The data arrives pushed from the server over WebSocket each time the server emits an event on the subscription's Flux. No polling, no client-initiated refetch — pure server push.

---

**[58:00–60:00] Slide 16 — Summary**

That's Part 1. You can now set up Apollo Client in both React and Angular, write queries and mutations, handle loading and error states, configure authentication headers, and wire up real-time subscriptions.

The core mental model: Apollo is a cache-first client. It stores query results in a normalized `InMemoryCache`. Hooks subscribe to that cache and re-render when relevant data changes. Mutations can invalidate or update cache entries, triggering automatic re-renders in all components showing that data.

Part 2: we go inside the cache — how normalization works, fetch policies, how to update the cache after mutations without extra network trips, optimistic UI, query batching, the N+1 problem and DataLoader in full depth, and the developer tooling that makes debugging GraphQL APIs fast — GraphiQL, Apollo Sandbox, Postman, and the Apollo DevTools browser extension.
