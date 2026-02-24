import { Component, OnInit } from '@angular/core';
import { BookService } from './book.service';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html'
})
export class BookListComponent implements OnInit {
  books: any[] = [];
  newTitle  = '';
  newAuthor = '';

  // TODO 6: Inject BookService via the constructor using TypeScript constructor injection.
  //         constructor(private bookService: BookService) {}
  constructor() {}

  ngOnInit(): void {
    // TODO 7: Initialize this.books from bookService.getBooks().
  }

  addBook(): void {
    // TODO 8: Call bookService.addBook(this.newTitle, this.newAuthor).
    //         Then clear newTitle and newAuthor back to ''.
  }

  removeBook(id: number): void {
    // TODO 9: Call bookService.removeBook(id).
  }
}
