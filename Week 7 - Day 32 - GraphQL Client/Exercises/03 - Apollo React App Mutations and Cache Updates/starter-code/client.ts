import { ApolloClient, InMemoryCache } from "@apollo/client";

// Same Apollo Client setup as Exercise 02 — one instance shared across the app
export const client = new ApolloClient({
  uri: "http://localhost:4000/graphql",
  cache: new InMemoryCache(),
});
