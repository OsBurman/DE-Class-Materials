import { Injectable } from '@angular/core';

interface Book {
  id: number;
  title: string;
  author: string;
}

// providedIn:'root' makes this a root-level singleton — no need to list in providers[]
@Injectable({ providedIn: 'root' })
export class BookService {
  private books: Book[] = [
    { id: 1, title: 'Clean Code',                         author: 'Robert C. Martin'    },
    { id: 2, title: 'The Pragmatic Programmer',           author: 'Hunt & Thomas'       },
    { id: 3, title: 'Designing Data-Intensive Applications', author: 'Martin Kleppmann' },
  ];
  private nextId = 4;

  getBooks(): Book[] {
    return this.books;  // returns reference — Angular's CD sees mutations
  }

  addBook(title: string, author: string): void {
    this.books.push({ id: this.nextId++, title, author });
  }

  removeBook(id: number): void {
    this.books = this.books.filter(b => b.id !== id);
  }
}
