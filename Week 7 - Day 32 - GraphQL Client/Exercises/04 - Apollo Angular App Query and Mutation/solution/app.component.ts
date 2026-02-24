import { Component, OnInit } from '@angular/core';
import { BookService, Book } from './book.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  books: Book[] = [];
  // Form field state bound via [(ngModel)] in the template
  newTitle    = '';
  newGenre    = '';
  newYear     = 0;
  newAuthorId = '';

  constructor(private bookService: BookService) {}

  ngOnInit(): void {
    // Subscribe to the query Observable — Angular handles the async delivery
    this.bookService.getBooks().subscribe(({ data }) => {
      this.books = data.books;
    });
  }

  addBook(): void {
    // Call the mutation and update the local books array on success
    this.bookService
      .addBook(this.newTitle, this.newGenre, this.newYear, this.newAuthorId)
      .subscribe((result) => {
        const newBook = result.data?.addBook;
        if (newBook) {
          this.books = [...this.books, newBook]; // immutable update triggers change detection
        }
        // Clear the form after successful add
        this.newTitle = ''; this.newGenre = ''; this.newYear = 0; this.newAuthorId = '';
      });
  }
}
