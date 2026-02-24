import { gql } from '@apollo/client'

// TODO Task 3a: Define ADD_BOOK mutation
// Variables: $input: AddBookInput!
// Return: id, title, genre, publishedYear, author { name }
export const ADD_BOOK = gql`
  # TODO
  mutation AddBook($input: AddBookInput!) {
    addBook(input: $input) {
      id
      title
      genre
      publishedYear
      author {
        name
      }
    }
  }
`

// TODO Task 3b: Define ADD_REVIEW mutation
// Variables: $bookId: ID!, $input: AddReviewInput!
export const ADD_REVIEW = gql`
  # TODO
  mutation AddReview($bookId: ID!, $input: AddReviewInput!) {
    addReview(bookId: $bookId, input: $input) {
      id
      rating
      comment
      reviewer
    }
  }
`

// TODO Task 3c: Define DELETE_BOOK mutation
export const DELETE_BOOK = gql`
  # TODO
  mutation DeleteBook($id: ID!) {
    deleteBook(id: $id)
  }
`
