import React, { useState } from "react";
import { gql, useMutation } from "@apollo/client";
import { GET_BOOKS } from "./BookList";

// Mutation definition — variables map to the addBook resolver arguments
const ADD_BOOK = gql`
  mutation AddBook($title: String!, $genre: String!, $year: Int!, $authorId: ID!) {
    addBook(title: $title, genre: $genre, year: $year, authorId: $authorId) {
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

export function AddBook() {
  const [title,    setTitle]    = useState("");
  const [genre,    setGenre]    = useState("");
  const [year,     setYear]     = useState(0);
  const [authorId, setAuthorId] = useState("");

  // useMutation returns [mutate function, result object]
  // The `update` callback runs after the mutation succeeds and receives the cache + response
  const [addBook, { error: mutationError }] = useMutation<{ addBook: Book }>(ADD_BOOK, {
    update(cache, { data }) {
      // Read the current list of books from the cache
      const existing = cache.readQuery<{ books: Book[] }>({ query: GET_BOOKS });
      if (!data?.addBook) return;
      // Write the updated list (existing books + new book) back to the cache
      // Apollo will notify all components subscribed to GET_BOOKS automatically
      cache.writeQuery({
        query: GET_BOOKS,
        data: { books: [...(existing?.books ?? []), data.addBook] },
      });
    },
  });

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    // Send the mutation to the server
    addBook({ variables: { title, genre, year, authorId } });
    // Clear the form after submission
    setTitle(""); setGenre(""); setYear(0); setAuthorId("");
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Add a Book</h2>
      {/* Show a red error message if the mutation fails */}
      {mutationError && <p style={{ color: "red" }}>Error: {mutationError.message}</p>}
      <input
        placeholder="Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        required
      />
      <input
        placeholder="Genre"
        value={genre}
        onChange={(e) => setGenre(e.target.value)}
        required
      />
      <input
        type="number"
        placeholder="Year"
        value={year || ""}
        onChange={(e) => setYear(Number(e.target.value))}
        required
      />
      <input
        placeholder="Author ID"
        value={authorId}
        onChange={(e) => setAuthorId(e.target.value)}
        required
      />
      <button type="submit">Add Book</button>
    </form>
  );
}
