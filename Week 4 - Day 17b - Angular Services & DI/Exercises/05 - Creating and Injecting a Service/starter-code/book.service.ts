import { Injectable } from '@angular/core';

interface Book {
  id: number;
  title: string;
  author: string;
}

// TODO 1: Add the @Injectable decorator with { providedIn: 'root' }
//         so Angular registers this service in the root injector automatically.
@Injectable({ providedIn: 'root' })
export class BookService {
  // TODO 2: Declare a private 'books' array of Book objects.
  //         Pre-populate with at least 3 books.
  private books: Book[] = [
    // { id: 1, title: '...', author: '...' },
  ];
  private nextId = 4; // starts after the pre-populated items

  // TODO 3: Implement getBooks() — return the full books array.
  getBooks(): Book[] {
    // TODO: return this.books;
    return [];
  }

  // TODO 4: Implement addBook(title, author) — push a new Book onto this.books.
  //         Use this.nextId++ for the id.
  addBook(title: string, author: string): void {
    // TODO: create a book object and push it; increment nextId
  }

  // TODO 5: Implement removeBook(id) — filter out the book with the matching id.
  removeBook(id: number): void {
    // TODO: this.books = this.books.filter(b => b.id !== id);
  }
}
