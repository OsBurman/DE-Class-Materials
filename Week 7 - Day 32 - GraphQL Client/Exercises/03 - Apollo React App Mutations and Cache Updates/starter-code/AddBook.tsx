import React, { useState } from "react";
import { gql, useMutation } from "@apollo/client";
import { GET_BOOKS } from "./BookList";

// TODO 2: Define the ADD_BOOK mutation using gql.
//         It should accept variables: $title: String!, $genre: String!, $year: Int!, $authorId: ID!
//         and return the new book's id, title, genre, year, and author { id name }.
//
// const ADD_BOOK = gql`
//   mutation AddBook($title: String!, ...) {
//     addBook(title: $title, ...) {
//       id title genre year author { id name }
//     }
//   }
// `;
const ADD_BOOK = gql`
  # TODO 2: Write the ADD_BOOK mutation here
`;

interface Author { id: string; name: string; }
interface Book   { id: string; title: string; genre: string; year: number; author: Author; }

export function AddBook() {
  const [title,    setTitle]    = useState("");
  const [genre,    setGenre]    = useState("");
  const [year,     setYear]     = useState(0);
  const [authorId, setAuthorId] = useState("");

  // TODO 3: Call useMutation(ADD_BOOK) with an `update` callback that:
  //         1. Reads the existing books array from cache with cache.readQuery({ query: GET_BOOKS })
  //         2. Appends the new book (data?.addBook) to the array
  //         3. Writes the updated array back with cache.writeQuery(...)
  //
  // useMutation returns [addBook, { error }] — destructure the array.
  const [addBook, { error: mutationError }] = useMutation(ADD_BOOK, {
    update(cache, { data }) {
      // TODO 3: implement the cache update here
    },
  });

  // TODO 4: Build a controlled form with four inputs: Title (text), Genre (text),
  //         Year (number), Author ID (text). Bind each to its state variable.

  // TODO 5: On form submit, call addBook with the four variables, then reset all fields.
  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    // TODO 5: call addBook({ variables: { title, genre, year, authorId } })
    //         then reset all state fields to empty / 0
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Add a Book</h2>
      {/* TODO 6: Render mutationError message if mutationError is defined */}
      {/* TODO 4: Render Title input */}
      {/* TODO 4: Render Genre input */}
      {/* TODO 4: Render Year input (type="number") */}
      {/* TODO 4: Render Author ID input */}
      <button type="submit">Add Book</button>
    </form>
  );
}
