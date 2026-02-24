import React from "react";
import { gql, useQuery } from "@apollo/client";

// TODO 3: Define the GET_BOOKS query using the gql template literal tag.
//         It should query the `books` field and select:
//           id, title, genre, year
//           author { id name }
//
// Example shape:
//   const GET_BOOKS = gql`
//     query {
//       books {
//         id
//         title
//         ...
//       }
//     }
//   `;

const GET_BOOKS = gql`
  # TODO 3: Write your query here — select id, title, genre, year, and nested author { id name }
`;

// Type describing one book returned by the server
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
  // TODO 4a: Call useQuery(GET_BOOKS) and destructure { loading, error, data }
  const { loading, error, data } = useQuery<{ books: Book[] }>(GET_BOOKS);

  // TODO 4b: If loading is true, return <p>Loading books...</p>
  if (loading) return null; // replace null with the correct JSX

  // TODO 4c: If error is defined, return <p>Error loading books: {error.message}</p>
  if (error) return null; // replace null with the correct JSX

  // TODO 4d: Return a <ul> where each book is an <li> showing:
  //          "[title] ([year]) — genre: [genre] — by [author.name]"
  return (
    <ul>
      {/* TODO 4d: Map over data?.books and render one <li> per book */}
    </ul>
  );
}
