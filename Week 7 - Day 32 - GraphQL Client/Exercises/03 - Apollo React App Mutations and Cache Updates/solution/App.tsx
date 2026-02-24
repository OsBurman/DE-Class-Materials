import React from "react";
import { BookList } from "./BookList";
import { AddBook } from "./AddBook";

// Renders the book list above the add-book form
export function App() {
  return (
    <div>
      <h1>Bookstore</h1>
      <BookList />
      <AddBook />
    </div>
  );
}
