# Exercise 01: @Input, @Output, and EventEmitter

## Objective
Practice passing data from a parent component to a child with `@Input`, and emitting events from the child back to the parent with `@Output` and `EventEmitter`.

## Background
In Angular, components communicate through a well-defined contract: parents pass data *down* via input properties, and children send events *up* via output event emitters. You are building a product review app where a parent `AppComponent` displays a list of products and a child `RatingBadgeComponent` shows each product's rating — and lets the user up-vote it.

## Requirements

1. Create a `RatingBadgeComponent` with:
   - An `@Input()` property `productName: string` — the name to display.
   - An `@Input()` property `rating: number` — the current numeric rating.
   - An `@Output()` property `upvoted` of type `EventEmitter<string>` — emits the product name when the button is clicked.
   - A template that displays `productName` and `rating`, and a **"👍 Upvote"** button that calls `upvoted.emit(productName)` when clicked.

2. In `AppComponent`:
   - Maintain an array `products` of at least 3 objects, each with `name: string` and `rating: number`.
   - Add an `onUpvote(name: string)` method that finds the matching product and increments its `rating` by 1.
   - Use `<app-rating-badge>` in the template, passing `product.name` and `product.rating` via `@Input` bindings, and listening to the `(upvoted)` output to call `onUpvote($event)`.

3. Declare both components in `AppModule`.

## Hints
- `@Input()` binds a parent *property expression* to the child: `[rating]="product.rating"`.
- `@Output()` listens with event binding syntax: `(upvoted)="onUpvote($event)"`.
- `EventEmitter` must be typed: `new EventEmitter<string>()`.
- Use `*ngFor` to loop over the `products` array in the parent template.

## Expected Output
```
Product Reviews

React Fundamentals     ⭐ Rating: 4  [👍 Upvote]
Spring Boot            ⭐ Rating: 5  [👍 Upvote]
TypeScript Deep Dive   ⭐ Rating: 3  [👍 Upvote]

(Clicking Upvote on "React Fundamentals" changes its rating to 5)
```
