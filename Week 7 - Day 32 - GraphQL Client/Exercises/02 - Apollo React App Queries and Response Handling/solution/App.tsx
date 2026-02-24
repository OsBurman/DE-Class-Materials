import React from "react";
import { BookList } from "./BookList";

// App is the root component. BookList handles its own data fetching via Apollo.
export function App() {
  return (
    <div>
      <h1>Bookstore</h1>
      <BookList />
    </div>
  );
}
