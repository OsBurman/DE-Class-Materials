import React from "react";
import { gql, useQuery } from "@apollo/client";

// Exported so AddBook can reference the same query in its cache update
export const GET_BOOKS = gql`
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

interface Author { id: string; name: string; }
interface Book   { id: string; title: string; genre: string; year: number; author: Author; }

export function BookList() {
  const { loading, error, data } = useQuery<{ books: Book[] }>(GET_BOOKS);

  if (loading) return <p>Loading books...</p>;
  if (error)   return <p>Error loading books: {error.message}</p>;

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
