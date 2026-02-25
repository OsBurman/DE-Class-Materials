import React from 'react'
import ReactDOM from 'react-dom/client'
import { ApolloClient, InMemoryCache, ApolloProvider, gql, useQuery, useMutation } from '@apollo/client'
import App from './App'

/**
 * Day 32 — Part 1: GraphQL Client with React + Apollo Client
 * ===========================================================
 * Run: npm install && npm start
 * Visit: http://localhost:3000
 *
 * NOTE: This app connects to the GraphQL server from Day 31 Part 1.
 * Start the Day 31 Part 1 server first: cd ../../../"Week 7 - Day 31 - GraphQL/Application/Sample-App/part-1" && mvn spring-boot:run
 * Then start this React app: npm install && npm start
 *
 * If the Day 31 server is not running, mock data will be shown instead.
 *
 * Topics: ApolloClient, ApolloProvider, useQuery, useMutation,
 *         gql template literal, InMemoryCache, loading/error states
 */

// ── Apollo Client Setup ──────────────────────────────────────────────────
const client = new ApolloClient({
  uri: 'http://localhost:8080/graphql',  // Day 31 Spring Boot GraphQL server
  cache: new InMemoryCache(),
})

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ApolloProvider client={client}>
      <App />
    </ApolloProvider>
  </React.StrictMode>
)
