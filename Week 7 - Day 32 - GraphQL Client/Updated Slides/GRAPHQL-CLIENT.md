GraphQL Client — 1-Hour Presentation Script & Slide Guide
EDITED VERSION

SLIDE 1: Title Slide
Content: "GraphQL Client: Consuming, Testing & Optimizing GraphQL APIs"
Subtitle: Day [X] | Your Name | Course Name
SCRIPT:
"Welcome back everyone. Over the past few days we've covered the fundamentals of GraphQL — the schema, types, resolvers. Today we're shifting to the client side. This is where GraphQL gets really exciting for frontend developers, because the way you fetch and manage data changes dramatically compared to REST. By the end of this session you'll be able to wire up a React or Angular app to a GraphQL API, write queries and mutations from the frontend, handle errors gracefully, think about caching and performance, and test your APIs with tools like Postman and GraphiQL. Let's get into it."

SLIDE 2: What is a GraphQL Client?
Content:

Any tool or library that sends GraphQL operations to a server
Handles: query construction, HTTP transport, response parsing, caching
Can be as simple as fetch() — or as powerful as Apollo Client
Why use a dedicated client? Caching, state management, DX

SCRIPT:
"Before we talk about specific libraries, let's ground ourselves. A GraphQL client is anything that can send a GraphQL operation — a query, mutation, or subscription — to a server and handle the response. At the most basic level, you could do this with a plain fetch call. GraphQL runs over HTTP, and a query is just a POST request with a JSON body containing your query string and variables. So technically, you don't need a library at all. But in real applications, you almost always want one. Why? Because dedicated clients give you automatic caching, normalized data stores, loading and error states, optimistic UI updates, and deep framework integration. They take the boilerplate off your hands. The two most important clients in the ecosystem right now are Apollo Client — which works with React, Angular, and Vue — and a React-specific library called TanStack Query combined with a GraphQL fetcher. We'll focus on Apollo today since it covers both React and Angular, which are your primary targets."

SLIDE 3: GraphQL Client Libraries Overview
Content:

Apollo Client — most widely used, works with React, Angular, Vue
urql — lightweight, extensible, React/Svelte focused
Relay — Facebook's client, powerful but highly opinionated; steep learning curve, not recommended for most projects
TanStack Query + graphql-request — minimalist combo
graphql-request — bare-bones, great for scripts/SSR
Rule of thumb: Apollo for full-featured apps; graphql-request for simplicity

SCRIPT:
"Let's do a quick tour of the landscape. Apollo Client is the dominant choice. It has the largest community, the most complete feature set, and first-class support for both React and Angular. urql is a leaner alternative — it's well-designed and easier to reason about, but has a smaller ecosystem. Relay is built by Meta and used in production at Facebook. It's worth knowing it exists — it enforces strict conventions and is extremely powerful at scale — but the learning curve is steep and it's overkill for most projects, so we won't be going deep on it today. TanStack Query paired with graphql-request is a popular minimalist combo — you use graphql-request to make the actual HTTP calls and TanStack Query to manage caching and async state. For today's class we're going with Apollo Client because it directly supports both React and Angular. The concepts we cover — caching, error handling, mutations — translate to every other client too."

SLIDE 4: Apollo Client for React — Setup
Content:
bashnpm install @apollo/client graphql
javascriptimport { ApolloClient, InMemoryCache, ApolloProvider } from '@apollo/client';

const client = new ApolloClient({
  uri: 'https://your-api.com/graphql',
  cache: new InMemoryCache(),
});

// Wrap your app:
<ApolloProvider client={client}>
  <App />
</ApolloProvider>

Tip: Install Apollo DevTools (Chrome/Firefox extension) — lets you inspect your cache and query history in real time

SCRIPT:
"Let's set up Apollo Client in a React app. You install two packages — @apollo/client which is the client itself, and graphql which is the reference implementation used under the hood for parsing queries. You then create an ApolloClient instance. The two required options are uri — the endpoint of your GraphQL server — and cache — which we'll talk about in depth shortly. InMemoryCache is Apollo's default caching mechanism. Then you wrap your entire React application in ApolloProvider, passing in your client instance. This makes the client available to every component in your tree via React context. You do this setup once in your root file — index.js or main.jsx — and then every component can use Apollo's hooks without any additional wiring. One more thing while you're setting up: go install the Apollo Client DevTools browser extension right now. It's available for Chrome and Firefox. Once you have it, you'll get a dedicated Apollo tab in your browser DevTools that shows you your normalized cache in real time, your operation history, and lets you fire queries directly from the panel. It will save you enormous amounts of debugging time and we'll reference it again when we talk about debugging later."

SLIDE 5: Executing Queries in React with useQuery
Content:
javascriptimport { useQuery, gql } from '@apollo/client';

const GET_USERS = gql`
  query GetUsers {
    users {
      id
      name
      email
    }
  }
`;

function UserList() {
  const { loading, error, data } = useQuery(GET_USERS);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error.message}</p>;

  return data.users.map(user => <div key={user.id}>{user.name}</div>);
}
SCRIPT:
"This is the core pattern you'll use constantly. The gql tag is a template literal tag that parses your query string into an AST — an Abstract Syntax Tree — that Apollo can work with. We define the query outside the component so it isn't recreated on every render. Then inside the component, we call useQuery with that query document. It returns three things that you'll use in almost every query: loading — a boolean that's true while the request is in flight, error — an Apollo error object if something went wrong, and data — the response payload once it arrives. The first render, loading is true and data is undefined. Once the request completes, loading becomes false and data is populated. You handle these three states to give your users proper feedback. One thing to note — Apollo will automatically re-use cached data for identical queries. So if this component unmounts and remounts, the second render will be instant because the data is already in the cache. We'll dig into that more in the caching section."

SLIDE 6: Field Selection and Nested Queries
Content:

Field selection = you ask only for what you need
Nested queries = follow relationships in a single request

graphqlquery GetOrderDetails {
  order(id: "42") {
    id
    status
    customer {
      name
      email
    }
    items {
      product {
        title
        price
      }
      quantity
    }
  }
}

No over-fetching. No N+1 round trips (from client side)

SCRIPT:
"This is one of GraphQL's core value propositions and it's important you internalize it as a client-side developer. When you write a query, you explicitly list every field you want. Nothing else comes back. This is called field selection. In REST, a /orders/42 endpoint returns whatever the server decided to include — maybe 30 fields when you only need 5. In GraphQL, you get exactly what you ask for. Now look at this nested query. In a single request we're fetching an order, its customer information, and the items within it — each item including its product details. In REST this might take three or four sequential API calls. Here it's one. The relationships are traversed on the server. For the client, this means less code, less loading state management, and faster UI rendering. Remember: the shape of your response mirrors the shape of your query. If you asked for customer.name, you get data.order.customer.name. This predictability is a huge productivity win."

SLIDE 7: Query Variables and Dynamic Queries
Content:
javascriptconst GET_USER = gql`
  query GetUser($id: ID!) {
    user(id: $id) {
      name
      posts { title }
    }
  }
`;

const { data } = useQuery(GET_USER, {
  variables: { id: userId },
});

Variables keep queries reusable and safe (no string interpolation)
Apollo re-executes when variables change

SCRIPT:
"Real queries are rarely static. You'll almost always need to pass in some dynamic value — a user ID, a search term, a page number. The right way to do this in GraphQL is with variables. You declare them at the top of your query operation — here $id of type ID! — and then use them in the query body. On the client, you pass the actual values in the variables option of useQuery. Never concatenate values directly into query strings. That's error-prone and can introduce security issues. Variables are the safe, reusable way. Another important behavior: if your userId state changes — say the user clicks a different profile — Apollo automatically re-executes the query with the new variable value. You don't have to wire that up manually. It reacts to your variable changes."

SLIDE 8: Mutations from Client Applications
Content:
javascriptconst CREATE_POST = gql`
  mutation CreatePost($title: String!, $body: String!) {
    createPost(title: $title, body: $body) {
      id
      title
    }
  }
`;

function NewPostForm() {
  const [createPost, { loading, error, data }] = useMutation(CREATE_POST);

  const handleSubmit = () => {
    createPost({ variables: { title, body } });
  };
}

useMutation returns a function + result state
Mutation is triggered manually, not automatically on render

SCRIPT:
"Queries run automatically when a component renders. Mutations are different — you trigger them in response to a user action, like submitting a form or clicking a button. The useMutation hook returns a tuple. The first element is the mutation function — here we call it createPost. The second is an object with loading, error, and data — same pattern as useQuery. When the user submits the form, you call createPost with your variables and Apollo sends the mutation to the server. The loading state goes true while it's in flight, which you can use to disable the submit button and prevent duplicate submissions. When it completes, data contains whatever fields you asked for in the mutation's selection set — here the new post's id and title. A critical concept with mutations is: after a successful mutation, your cache might be out of date. If you added a new post, the cached list of posts doesn't know about it yet. We'll cover how to handle that coming up shortly."

SLIDE 9: Apollo Client for Angular — Setup
Content:
bashng add apollo-angular
typescript// app.module.ts
import { ApolloModule } from 'apollo-angular';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  imports: [ApolloModule, HttpClientModule],
})
export class AppModule {}

// In a config file or constructor:
apollo.create({
  link: new HttpLink({ uri: '/graphql' }),
  cache: new InMemoryCache(),
});
SCRIPT:
"Apollo Angular is the officially maintained Angular integration for Apollo Client. The setup follows Angular conventions — it's module-based rather than wrapping a component tree like in React. The easiest way to set it up is ng add apollo-angular which does most of the configuration for you automatically. Under the hood, Apollo Angular uses Angular's HttpClient for transport — this is important because it means interceptors, auth headers, and other HttpClient middleware you've already set up will work with GraphQL requests too. You import ApolloModule and HttpClientModule into your app module, then configure your client with a URI and cache. The HttpLink is what connects Apollo to Angular's HTTP layer. Once configured, you inject the Apollo service anywhere you need it."

SLIDE 10: Queries and Mutations in Angular
Content:
typescriptimport { Apollo, gql } from 'apollo-angular';

const GET_USERS = gql`query { users { id name } }`;

@Component({ ... })
export class UsersComponent implements OnInit {
  users$ = this.apollo.watchQuery({ query: GET_USERS }).valueChanges;

  constructor(private apollo: Apollo) {}
}

// In template:
// *ngIf="users$ | async as result"
// {{ result.data.users | json }}
SCRIPT:
"In Angular, instead of hooks you inject the Apollo service into your component or service. For queries, you call apollo.watchQuery() which returns a QueryRef object. Calling .valueChanges on it gives you an Observable that emits every time the data changes — including cache updates. This integrates perfectly with Angular's async pipe in templates. You can also use apollo.query() which returns a single-emission Observable if you don't need live cache updates. For mutations it's apollo.mutate() which returns an Observable you subscribe to or pipe through. The pattern is slightly more verbose than React hooks but it fits Angular's reactive programming model. The underlying concepts — variables, caching, error handling — are identical to the React version."

SLIDE 11: Response Handling Deep Dive
Content:

Response structure:

json{
  "data": { "users": [...] },
  "errors": [{ "message": "...", "locations": [...], "path": [...] }]
}

data AND errors can both be present at the same time
Partial success is a real GraphQL scenario
Always check both

SCRIPT:
"This is something that trips up a lot of developers coming from REST. In REST, a 200 means success and a 4xx or 5xx means failure. GraphQL almost always returns HTTP 200 — even when there are errors. The errors are in the response body. The GraphQL spec allows for partial success — meaning data and errors can both be non-null simultaneously. Imagine a query that fetches a user and their posts. If the user resolver succeeds but the posts resolver throws, you'll get the user data and an error for the posts field. Partially populated data. Your client needs to handle this. Apollo Client actually splits these into two categories — network errors and GraphQL errors — and we'll see how to handle both."

SLIDE 12: Error Handling in GraphQL
Content:
javascriptconst { loading, error, data } = useQuery(GET_USERS);

// Apollo splits errors:
// error.networkError — HTTP/connection failures
// error.graphQLErrors — Server-side GraphQL errors

if (error?.networkError) {
  console.log('Network issue:', error.networkError);
}
if (error?.graphQLErrors) {
  error.graphQLErrors.forEach(e => console.log(e.message, e.path));
}

// Global error handling with Apollo Link:
import { onError } from "@apollo/client/link/error";

const errorLink = onError(({ graphQLErrors, networkError }) => {
  if (networkError?.statusCode === 401) redirectToLogin();
});
SCRIPT:
"Apollo Client gives you two distinct error buckets. networkError is for transport-level failures — the server is down, the user is offline, you got a 401 or 500 HTTP response. graphQLErrors is an array of errors from the GraphQL execution itself — a resolver threw, a field wasn't found, a permission check failed. In your components you can handle these inline as we just saw. But for application-wide concerns — like redirecting to login on a 401, or showing a global error toast — you should use Apollo's error link. onError from the error link package intercepts every operation and gives you a place to react globally before the error reaches your component. You compose this link with your HTTP link when creating the Apollo client. This pattern keeps your cross-cutting error concerns in one place and your component error handling focused on UX-specific cases."

SLIDE 13: How Apollo's Normalized Cache Works
Content:

Apollo's InMemoryCache is a normalized cache
On response, Apollo breaks data into individual objects
Each object is stored by __typename + id (e.g., User:42)
Same object referenced in multiple queries = stored once
Update it in one place → every query referencing it updates automatically

Example:

Query A fetches User:42 in a user list
Query B fetches User:42 in a profile view
Apollo stores one User:42 entry — both views stay in sync automatically

SCRIPT:
"The cache is Apollo's secret weapon and understanding it will save you hours of debugging. Apollo's InMemoryCache is a normalized cache. That means when you get back a response, Apollo doesn't store it as a blob keyed to the query. Instead it breaks the response down into individual objects, identifies each one by its __typename and id, and stores them flat. So if you have a User:42 that appears in five different queries, it's stored once. Update it in one place and every query that references it is automatically updated. This is incredibly powerful — it means mutations that touch existing records often update your UI for free, without any extra work on your part."

SLIDE 14: Cache Fetch Policies
Content:
PolicyBehaviorUse when...cache-first (default)Use cache; skip network if hitData doesn't change oftennetwork-onlyAlways go to serverData freshness is criticalcache-and-networkShow cache immediately, then update from networkFast first render + freshnessno-cacheDon't cache at allSensitive/one-time datacache-onlyNever go to networkFully offline scenarios
javascriptconst { data } = useQuery(GET_USERS, {
  fetchPolicy: 'cache-and-network',
});
SCRIPT:
"Now that you understand how the cache stores data, you need to control when Apollo hits the network versus the cache. That's what fetch policies do. cache-first is the default — if the data is in the cache, use it and skip the network entirely. This is great for performance but means you might show stale data. network-only skips the cache completely — you always go to the server. Use this for data where freshness is critical, like account balances or live scores. cache-and-network is a nice middle ground — it returns whatever stale cache data it has immediately for a fast first render, then fires a network request in the background and updates the UI when fresh data arrives. This is often the best default for most app screens. Choose your policy deliberately based on how stale your data can tolerate being — don't default to network-only everywhere just to feel safe, because you're throwing away Apollo's biggest advantage."

SLIDE 15: Updating the Cache After Mutations — refetchQueries
Content:

After a mutation, list queries in the cache may be stale
Simplest fix: refetchQueries — tells Apollo which queries to re-run after the mutation completes

javascriptconst [createPost] = useMutation(CREATE_POST, {
  refetchQueries: [{ query: GET_POSTS }],
});

✅ Simple to write and easy to reason about
⚠️ Costs one extra network request per listed query
Good default choice for most CRUD operations

SCRIPT:
"When you run a mutation that creates or deletes something, your cache has a problem. If you created a new post, the cached list of posts doesn't know about it yet. The simplest way to fix this is refetchQueries. You pass it an array of queries you want Apollo to automatically re-run after the mutation completes. Here we're telling Apollo: after createPost finishes, go re-fetch GET_POSTS. Your list will update automatically. This is the pattern you'll reach for most often — it's easy to understand, it works reliably, and the tradeoff is just one extra network request. Get comfortable with this first before moving on to the more advanced cache update approach on the next slide."

SLIDE 16: Updating the Cache After Mutations — Direct Cache Update
Content:

For zero extra network requests: update the cache directly

javascriptuseMutation(CREATE_POST, {
  update(cache, { data: { createPost } }) {
    cache.modify({
      fields: {
        posts(existing = []) {
          const newRef = cache.writeFragment({
            data: createPost,
            fragment: gql`fragment NewPost on Post { id title }`
          });
          return [...existing, newRef];
        }
      }
    });
  }
});

✅ Instant UI update, no extra network request
⚠️ More code, more complexity
Use when performance matters or UX needs to feel instant

SCRIPT:
"The more optimized approach is to directly update the cache yourself using the update callback. Instead of refetching the list from the server, you write the new item directly into the relevant cache fields. You use cache.modify to target specific fields, and cache.writeFragment to create a reference to the new object. Apollo then re-renders any component that was reading those cache fields. This is more code, but it results in an instant UI update with zero additional network calls. The tradeoff is complexity — this code is harder to read and debug. My recommendation: start with refetchQueries. Once your app is working and you want to optimize specific high-traffic interactions, reach for direct cache updates. Don't start with the harder approach."

SLIDE 17: Batching and Request Optimization
Content:

Multiple components querying simultaneously = multiple HTTP requests
BatchHttpLink collects operations within a time window and sends them as one request

javascriptimport { BatchHttpLink } from "@apollo/client/link/batch-http";

const link = new BatchHttpLink({
  uri: '/graphql',
  batchMax: 5,       // max operations per batch
  batchInterval: 20, // ms window to collect operations
});

✅ Fewer HTTP requests on initial page load
⚠️ Adds a small delay (the batch window)
Server must support batched requests (Apollo Server does by default)

SCRIPT:
"When your page first loads, you might have five different components that each fire their own query simultaneously. That's five separate HTTP requests. Request batching solves this by collecting all operations that fire within a short time window — say 20 milliseconds — and sending them as a single HTTP request containing an array of operations. The server processes them and returns an array of results. Apollo splits those results back out and delivers them to the right components. You get this by swapping HttpLink for BatchHttpLink. The tradeoffs: batching adds a small delay equal to your batch interval because Apollo has to wait to see if more operations arrive before sending. Also, your server needs to be able to handle batched requests — most Apollo Server setups do this out of the box. Batching is a great quick win for pages with many independent data requirements."

SLIDE 18: The N+1 Problem
Content:

Classic backend performance problem — important to understand as a frontend dev
Query posts → for each post, query its author separately = N+1 DB calls
You can accidentally cause the client-side equivalent:

javascript// ❌ Don't do this — N+1 on the client
posts.forEach(post => {
  useQuery(GET_AUTHOR, { variables: { id: post.authorId } });
});

// ✅ Do this — fetch everything in one query
query GetPostsWithAuthors {
  posts {
    id
    title
    author { name }
  }
}

DataLoader solves N+1 on the server — batches and deduplicates DB calls

SCRIPT:
"The N+1 problem is primarily a backend concern but you need to understand it because you can accidentally create the client-side equivalent. The classic N+1: you query for 10 posts, then for each post you make a separate query to get the author. That's 1 query for the list plus 10 queries for authors — 11 total, hence N+1. On the client, the equivalent mistake is calling useQuery in a loop — don't do that. Instead, design your GraphQL queries to fetch related data in one shot using nested selection, as we saw earlier. Now, DataLoader is a server-side utility created by Facebook. It solves N+1 in resolvers by batching all individual database lookups that happen within the same event loop tick into a single batched query. So even if 10 different post resolvers each try to look up an author, DataLoader collects those 10 IDs and fires one SELECT * FROM users WHERE id IN (...) query. As a frontend developer you benefit from DataLoader transparently — your nested queries are fast because the server is smart about it. But knowing this exists helps you understand why some queries perform well and helps you have informed conversations with your backend team."

SLIDE 19: Testing GraphQL APIs in Postman
Content:

Set method to POST, URL to your GraphQL endpoint
Body tab → select GraphQL mode (gives schema introspection + autocomplete)
Or use raw JSON:

json{
  "query": "query { users { id name email } }",
  "variables": {}
}

Add auth token in Headers tab if needed
Save queries to Collections for team sharing and regression testing
Same approach for mutations — just write a mutation string in the query field

SCRIPT:
"Postman has had native GraphQL support for a while now and it's one of the best tools for quickly testing and debugging your API before wiring it to your frontend. Create a new request, set it to POST, and point it at your GraphQL endpoint. In the Body tab you'll see a GraphQL option — select that. Postman will attempt to introspect your schema and give you autocomplete for your query fields. This is extremely useful for exploration. You can also just use raw JSON mode and write the request body manually as a JSON object with a query string and a variables object. If your API requires authentication, add your Authorization header in the Headers tab. Postman lets you save queries into collections, so you can build up a collection of all your important operations — great for sharing with your team or for regression testing. For mutations, the same approach applies — just write a mutation string in the query field. Note: the screenshots on this slide show the key Postman panels — you don't need to follow along in Postman right now, just get familiar with the layout so you can try it yourself after class."

SLIDE 20: GraphQL Playground and GraphiQL
Content:

GraphiQL — in-browser GraphQL IDE, usually at /graphql on dev servers
Apollo Server ships with the Sandbox explorer in dev mode
Split-pane interface: write queries left, see results right
Key panels to know:

Docs — auto-generated schema browser (every type, field, argument)
Explorer — click fields to build queries visually, no typing required
Variables — pass variables as JSON
Headers — add auth tokens



SCRIPT:
"GraphiQL is an in-browser IDE that most GraphQL servers expose, especially in development. If your server is running locally, try navigating to /graphql or /graphiql in your browser after class. You'll see a split-pane interface: write queries on the left, see results on the right. The Docs panel shows your entire schema — every query, mutation, type, and field — automatically generated. This is your best friend when you're learning a new GraphQL API. Click through the types, see what fields are available, what arguments they accept, what types they return. The Explorer pane in modern versions lets you click checkboxes to build queries visually without typing. Use the Variables panel at the bottom to pass JSON variables instead of hardcoding them in the query. If the API requires authentication, add your token in the Headers panel. Every GraphQL developer lives in these tools. I'd strongly recommend spending 15–20 minutes after class exploring your server's schema here before writing any client code — it'll make the whole process much smoother."

SLIDE 21: Debugging GraphQL — What to Check
Content:
Common mistakes:

Querying fields that don't exist → check schema in Docs panel
Wrong variable types (Int vs ID)
Missing required arguments
Forgetting to include id in mutation responses (breaks cache normalization)

Debug checklist:

Check network tab — is the request being sent? Is the payload correct?
Check the raw response — is there an errors array?
Open Apollo DevTools — what's in the cache? What operations fired?
Log errors properly:

javascriptconsole.log(JSON.stringify(error, null, 2))
// NOT just console.log(error) — nested structure won't display correctly
SCRIPT:
"When a query isn't working, follow this checklist. First, open your browser's network tab and confirm the request is actually being sent. Check the payload — is the query correct? Is it going to the right URL? Next look at the raw response — remember, GraphQL returns 200 even on errors. Expand the response body and look for an errors array. Read those error messages — they're usually descriptive. Apollo Server will tell you things like 'field X does not exist on type Y' or 'argument required'. Third, open the Apollo DevTools tab — this is that extension we set up during the Apollo setup section. You can see exactly what data Apollo has stored, fire queries directly from the devtools, and inspect your full operation history. This tool will save you a huge amount of time. Finally, always log your error objects with console.log(JSON.stringify(error, null, 2)) rather than just console.log(error) — Apollo error objects have nested structure that doesn't display well in the console by default."

SLIDE 22: Performance Optimization — Summary
Content:

Use field selection — never ask for more than you need
Use fragments to reuse field selections across queries
Choose the right cache policy for each query
Use pagination (fetchMore) instead of loading everything at once
Use BatchHttpLink when many components query on mount
Use useLazyQuery for user-triggered fetches (e.g., search fields)
Design queries to avoid N+1 — fetch related data in one nested query
Monitor with Apollo Studio or Apollo DevTools

SCRIPT:
"Let's pull together the performance picture. Field selection is your first defense — only request fields you actually render. Fragments are a related technique — they let you define a reusable set of fields once and include it in multiple queries, which is great for consistency and maintenance. Pick your cache policy deliberately — don't default to network-only everywhere just to be safe, because you're throwing away Apollo's biggest advantage. Use fetchMore for paginated lists rather than loading thousands of records at once. useLazyQuery is a variant of useQuery that doesn't execute on render — it gives you a function you call manually, which is perfect for search fields or anything that should wait for user input. And remember that all the performance you gain on the client can be wiped out by a poorly written resolver on the backend — so if something feels slow, check whether the server is doing N+1 lookups and advocate for DataLoader usage."

SLIDE 23: Putting It All Together — Integration Checklist
Content:
React:

 Install @apollo/client + graphql
 Create ApolloClient with URI + InMemoryCache
 Wrap app in <ApolloProvider>
 Use useQuery for reads, useMutation for writes
 Handle loading, error, data states

Angular:

 ng add apollo-angular
 Import ApolloModule + HttpClientModule
 Inject Apollo service, use watchQuery / mutate
 Use async pipe with Observables

Both:

 Use variables, not string interpolation
 Handle both networkError and graphQLErrors
 Choose cache policy consciously
 Use refetchQueries (or direct update) after mutations
 Test in GraphiQL before writing client code

SCRIPT:
"Here's your integration checklist — this is a reference you can come back to every time you start a new feature. Whether you're in React or Angular, the workflow is the same: set up your client once, define your operations with gql, execute them with the appropriate hook or service method, and handle your three states — loading, error, data. Always test your queries in GraphiQL first so you know they work before adding the complexity of your component. Use variables. Handle both error types. Be intentional about caching. And don't forget refetchQueries when a mutation needs to update a list — it's the first tool you should reach for. These aren't just best practices — they're the habits that separate developers who fight with their data layer from developers who ship confidently."

SLIDE 24: Recap
Content:

GraphQL clients abstract HTTP, caching, and state management
Apollo Client works in both React (hooks) and Angular (services/Observables)
Queries run on render; mutations run on demand
Response can have partial data AND errors simultaneously
Apollo's normalized cache is powerful — understand it, use it
refetchQueries is your first tool after mutations; direct cache updates for optimization
Test with GraphiQL/Postman first, then wire to frontend
N+1 is a server problem solved by DataLoader; avoid client-side equivalents
Batching + lazy queries + field selection = fast apps

SCRIPT:
"Let's do a quick lap around what we covered. You now know what GraphQL clients are and why you'd choose one over raw fetch. You can set up Apollo Client in both React and Angular. You can write queries with field selection and nested relationships that eliminate multiple round trips. You can execute mutations and handle the cache update challenge that comes with them — starting with refetchQueries as your go-to. You understand that GraphQL errors live in the response body and that Apollo splits them into network and GraphQL errors. You know how the normalized cache works and how to choose the right fetch policy. You've got tools — GraphiQL, Postman, Apollo DevTools — to test and debug. And you understand the N+1 problem well enough to avoid causing it on the client and to recognize it on the server. Next time we'll go deeper into subscriptions and real-time data. For now — any questions?"

SLIDE 25: Q&A / Resources
Content:

Apollo Client Docs: https://www.apollographql.com/docs/react/
Apollo Angular: https://apollo-angular.com
GraphiQL: https://github.com/graphql/graphiql
Apollo DevTools: Chrome/Firefox extension
Recommended practice: Build a small React app with a public GraphQL API (e.g., SpaceX API at https://spacex-production.up.railway.app/)

SCRIPT:
"Here are your resources. The Apollo docs are genuinely excellent — read the 'Caching' and 'Error Handling' sections in particular. For practice, I strongly recommend finding a public GraphQL API and building something small with it. The SpaceX GraphQL API is a community favorite — it has launches, rockets, crew members — lots of interesting data and real-world query complexity. Open it in GraphiQL, explore the schema, build some queries, then wire them into a React component. That hands-on loop is the fastest way to solidify everything we covered today. Alright, we'll open it up for questions now."