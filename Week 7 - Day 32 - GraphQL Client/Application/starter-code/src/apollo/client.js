import { ApolloClient, InMemoryCache, HttpLink } from '@apollo/client'

// TODO Task 1: Create and configure the Apollo Client
// The GraphQL endpoint is /graphql (proxied to http://localhost:8080/graphql via Vite)

export const client = new ApolloClient({
  link: new HttpLink({
    uri: '/graphql',
  }),
  cache: new InMemoryCache(),
})
