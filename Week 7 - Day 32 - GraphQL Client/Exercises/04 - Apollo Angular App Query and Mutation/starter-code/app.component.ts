import { Component, OnInit } from '@angular/core';
import { BookService, Book } from './book.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  books: Book[] = [];
  // Form fields bound via [(ngModel)]
  newTitle    = '';
  newGenre    = '';
  newYear     = 0;
  newAuthorId = '';

  constructor(private bookService: BookService) {}

  ngOnInit(): void {
    // TODO 4a: Subscribe to bookService.getBooks() and assign the result's data.books to this.books
    //
    // this.bookService.getBooks().subscribe(({ data }) => {
    //   this.books = data.books;
    // });
  }

  addBook(): void {
    // TODO 4b: Call bookService.addBook(newTitle, newGenre, newYear, newAuthorId).subscribe(...)
    //          Inside the subscription, push result.data?.addBook onto this.books, then reset form fields
  }
}
