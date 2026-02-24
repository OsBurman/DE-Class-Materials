# Day 32 Application — GraphQL Client: React Book Browser

## Overview

Build a **React front-end** that consumes the Day 31 Book Library GraphQL API using **Apollo Client**. Practice writing queries, mutations, and managing cache.

---

## Learning Goals

- Set up Apollo Client with `ApolloProvider`
- Write GraphQL queries with `useQuery`
- Write mutations with `useMutation`
- Use query variables
- Handle loading and error states
- Use Apollo cache and `refetchQueries`
- Write fragments for reusable query parts

---

## Prerequisites

- Node.js 18+, `npm install` → `npm run dev`
- Day 31 GraphQL API running at `http://localhost:8080/graphql`
- App: `http://localhost:5173`

---

## Part 1 — Apollo Client Setup

**Task 1 — `src/apollo/client.js`**  
```js
// TODO: Create ApolloClient with:
// - uri pointing to localhost:8080/graphql
// - InMemoryCache
export const client = ...
```

**Task 2 — `src/main.jsx`**  
```jsx
// TODO: Wrap <App /> in <ApolloProvider client={client}>
```

---

## Part 2 — Queries

**Task 3 — `src/graphql/queries.js`**  
```js
import { gql } from '@apollo/client';

export const GET_BOOKS = gql`
  # TODO: query for all books (id, title, genre, author name, averageRating)
`;

export const GET_BOOK = gql`
  # TODO: query for a single book by id — include reviews
`;

export const SEARCH_BOOKS = gql`
  # TODO: query with $query variable
`;

export const BOOK_FIELDS = gql`
  fragment BookFields on Book {
    # TODO: define a reusable fragment
  }
`;
```

---

## Part 3 — Components

**Task 4 — `src/components/BookList.jsx`**  
```jsx
const BookList = () => {
  const { loading, error, data } = useQuery(GET_BOOKS);
  // TODO: show loading spinner, error message, or grid of BookCard components
};
```

**Task 5 — `src/components/BookCard.jsx`**  
```jsx
// TODO: Show book cover placeholder, title, author, genre badge, star rating
// onClick → navigate to /books/:id
```

**Task 6 — `src/pages/BookDetail.jsx`**  
```jsx
// TODO: useParams for id
// TODO: useQuery(GET_BOOK, { variables: { id } })
// TODO: show full book details + review list + AddReviewForm
```

**Task 7 — `src/components/SearchBar.jsx`**  
```jsx
// TODO: useLazyQuery(SEARCH_BOOKS) — fire query on form submit, not mount
// TODO: show results below search bar
```

---

## Part 4 — Mutations

**Task 8 — `src/graphql/mutations.js`**  
```js
export const ADD_REVIEW = gql`
  mutation AddReview($bookId: ID!, $input: ReviewInput!) {
    # TODO
  }
`;

export const ADD_BOOK = gql`
  mutation AddBook($input: BookInput!) {
    # TODO
  }
`;
```

**Task 9 — `src/components/AddReviewForm.jsx`**  
```jsx
const [addReview, { loading }] = useMutation(ADD_REVIEW, {
  // TODO: refetchQueries: [{ query: GET_BOOK, variables: { id: bookId } }]
});
// TODO: controlled form — rating (1-5), comment, reviewerName
// TODO: call addReview() on submit
```

---

## Part 5 — Error Handling

**Task 10**  
Create `src/components/ErrorMessage.jsx` and `src/components/LoadingSpinner.jsx`. Use them consistently across all components that use `useQuery` or `useMutation`.

---

## Submission Checklist

- [ ] Apollo Client configured and `ApolloProvider` wraps App
- [ ] `GET_BOOKS` query populates BookList
- [ ] `GET_BOOK` query populates BookDetail
- [ ] `SEARCH_BOOKS` uses `useLazyQuery`
- [ ] `ADD_REVIEW` mutation with cache refetch
- [ ] Fragment `BookFields` used in at least 2 queries
- [ ] All loading/error states handled
- [ ] Navigating to `/books/:id` shows book detail
