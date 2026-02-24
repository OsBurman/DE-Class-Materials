import React from "react";
import { gql, useQuery } from "@apollo/client";

// gql parses the query string into an AST that Apollo understands.
// The nested author { id name } field demonstrates a nested GraphQL query.
const GET_BOOKS = gql`
  query GetBooks {
    books {
      id
      title
      genre
      year
      author {
        id
        name
      }
    }
  }
`;

interface Author {
  id: string;
  name: string;
}
interface Book {
  id: string;
  title: string;
  genre: string;
  year: number;
  author: Author;
}

export function BookList() {
  // useQuery returns { loading, error, data } — all three must be handled
  const { loading, error, data } = useQuery<{ books: Book[] }>(GET_BOOKS);

  // Show a loading state while the network request is in flight
  if (loading) return <p>Loading books...</p>;

  // Show a user-friendly error message if the query fails
  if (error) return <p>Error loading books: {error.message}</p>;

  // Render the list — each book's author comes from the nested resolver
  return (
    <ul>
      {data?.books.map((book) => (
        <li key={book.id}>
          {book.title} ({book.year}) — genre: {book.genre} — by {book.author.name}
        </li>
      ))}
    </ul>
  );
}
