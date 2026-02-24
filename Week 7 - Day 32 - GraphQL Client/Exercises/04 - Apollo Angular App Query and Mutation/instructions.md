# Exercise 04: Apollo Angular App — Query and Mutation

## Objective
Set up Apollo Angular in an Angular application, inject the `Apollo` service into a component, and execute both a GraphQL query and a mutation from Angular TypeScript code.

## Background
Angular uses a module-based dependency injection system. To consume GraphQL in Angular you install `apollo-angular` and configure `ApolloModule` in `AppModule`, providing the `HttpLink` so Apollo can send HTTP POST requests to your GraphQL server. Data fetching is done by injecting the `Apollo` service and calling `apollo.watchQuery(...)` which returns an Observable — fitting naturally into Angular's RxJS-based data flow.

## Requirements

1. In `app.module.ts`, configure `ApolloModule` by providing an `Apollo` setup using `apollo.create(...)` inside the constructor. Set `uri` to `http://localhost:4000/graphql` and `cache` to `new InMemoryCache()`. Import `HttpClientModule` so `HttpLink` can make network requests.

2. In `book.service.ts`, create a `BookService` with two methods:
   - `getBooks()` — uses `this.apollo.watchQuery<{ books: Book[] }>({ query: GET_BOOKS })` and returns the `valueChanges` Observable
   - `addBook(title, genre, year, authorId)` — uses `this.apollo.mutate<{ addBook: Book }>({ mutation: ADD_BOOK, variables: {...} })` and returns the Observable

3. Define `GET_BOOKS` and `ADD_BOOK` using `gql` in `book.service.ts` with the same fields as Exercises 02/03.

4. In `app.component.ts`:
   - Inject `BookService` in the constructor
   - On `ngOnInit`, subscribe to `bookService.getBooks()` and assign `data.books` to a `books: Book[]` component property
   - Implement `addBook()` method that calls `bookService.addBook(...)` with the component's form field values, then clears the fields

5. In `app.component.html`, build a template that:
   - Renders an `<ul>` with `*ngFor` over `books`, each `<li>` showing: `[title] ([year]) — [genre] — by [author.name]`
   - Renders a form with four inputs (title, genre, year, authorId) bound with `[(ngModel)]`
   - Has a submit button that calls `addBook()`

6. The `package.json` must include: `@apollo/client`, `apollo-angular`, `graphql`, `@angular/core`, `@angular/common`, `@angular/forms`, `@angular/platform-browser`, `rxjs`.

## Hints
- `apollo.watchQuery` returns an `ApolloQueryResult` object; chain `.valueChanges` to get an Observable.
- Use the `async` pipe in the template OR subscribe manually in the component — for this exercise, subscribe in `ngOnInit` and assign to a component property.
- `[(ngModel)]` requires `FormsModule` to be imported in `AppModule`.
- Angular's `HttpLink` is automatically used when you import `HttpClientModule` and configure Apollo with `HttpLink` from `apollo-angular/http`.

## Expected Output

When the server is running and returns books:
```
Bookstore

• Clean Code (2008) — Programming — by Robert C. Martin
• Effective Java (2018) — Programming — by Joshua Bloch

Title: [__________]  Genre: [__________]  Year: [____]  Author ID: [____]  [Add Book]
```

After clicking Add Book with valid data:
```
• Clean Code (2008) — Programming — by Robert C. Martin
• Effective Java (2018) — Programming — by Joshua Bloch
• Refactoring (2018) — Programming — by Robert C. Martin
```
