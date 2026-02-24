import { gql } from '@apollo/client'

// TODO Task 2a: Define GET_ALL_BOOKS query
// Fields: id, title, genre, publishedYear, author { name }, averageRating
export const GET_ALL_BOOKS = gql`
  # TODO
  query GetAllBooks {
    books {
      id
      title
      genre
      publishedYear
      author {
        name
      }
      averageRating
    }
  }
`

// TODO Task 2b: Define GET_BOOK query
// Include: id, title, genre, publishedYear, author { id, name, bio },
//          reviews { id, rating, comment, reviewer }, averageRating
export const GET_BOOK = gql`
  # TODO
  query GetBook($id: ID!) {
    book(id: $id) {
      id
      title
      genre
      publishedYear
      author {
        id
        name
        bio
      }
      reviews {
        id
        rating
        comment
        reviewer
      }
      averageRating
    }
  }
`

// TODO Task 2c: Define SEARCH_BOOKS query
export const SEARCH_BOOKS = gql`
  # TODO
  query SearchBooks($titleContains: String!) {
    searchBooks(titleContains: $titleContains) {
      id
      title
      genre
      publishedYear
      author {
        name
      }
      averageRating
    }
  }
`

// TODO Task 2d: Define GET_ALL_AUTHORS query
export const GET_ALL_AUTHORS = gql`
  # TODO
  query GetAllAuthors {
    authors {
      id
      name
      bio
      books {
        id
        title
      }
    }
  }
`
