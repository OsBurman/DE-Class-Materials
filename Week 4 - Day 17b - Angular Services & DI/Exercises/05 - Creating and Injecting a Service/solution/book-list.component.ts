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

  // Angular injects BookService automatically because of providedIn:'root'
  constructor(private bookService: BookService) {}

  ngOnInit(): void {
    // Call getBooks once on init to populate the local reference
    this.books = this.bookService.getBooks();
  }

  addBook(): void {
    if (!this.newTitle.trim() || !this.newAuthor.trim()) return;
    this.bookService.addBook(this.newTitle, this.newAuthor);
    this.newTitle  = '';  // clear inputs after adding
    this.newAuthor = '';
  }

  removeBook(id: number): void {
    this.bookService.removeBook(id);
    // Re-assign so Angular detects the array replacement
    this.books = this.bookService.getBooks();
  }
}
