import { ApolloClient, InMemoryCache } from "@apollo/client";

// Create a single Apollo Client instance for the entire app.
// InMemoryCache normalizes and caches query results automatically.
export const client = new ApolloClient({
  uri: "http://localhost:4000/graphql",
  cache: new InMemoryCache(),
});
