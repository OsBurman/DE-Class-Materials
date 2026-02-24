# Exercise 02: Route Parameters and Query Strings

## Objective
Use `useParams` to read dynamic URL segments and `useSearchParams` to read and update query string parameters.

## Background
Many real-world routes carry data in the URL — a product ID like `/products/42`, or filter options like `/products?category=books&sort=price`. React Router v6 exposes `useParams` for path parameters and `useSearchParams` for the query string. You will build a simple product catalog that demonstrates both.

## Requirements
1. Define a route `/products` that renders a `<ProductListPage>` showing a list of clickable product names.
2. Define a dynamic route `/products/:id` that renders a `<ProductDetailPage>`.
3. In `<ProductDetailPage>`, use `useParams` to read the `:id` parameter and display the matching product's name and description. If the ID doesn't match any product, display "Product not found."
4. In `<ProductListPage>`, add a `<select>` dropdown for a `category` filter (`all`, `electronics`, `books`, `clothing`). When the user changes the selection, update the URL query string so it reads e.g. `?category=electronics` — without navigating away from the page.
5. Use `useSearchParams` to read the current `category` value and filter the displayed product list accordingly. When category is `all` (or no query param is present), show all products.
6. Each product name in the list must be a `<Link>` that navigates to `/products/:id`.

## Hints
- `useParams()` returns an object — destructure the key that matches the parameter name in your route, e.g. `const { id } = useParams()`.
- `useSearchParams()` returns `[searchParams, setSearchParams]` — similar to `useState`. Call `setSearchParams({ category: value })` to update the query string.
- `searchParams.get('category')` returns the value as a string, or `null` if the key is absent.
- Use `parseInt(id, 10)` to convert the string `:id` to a number for array lookup.

## Expected Output

At `/products` (all categories):
```
Filter: [all ▾]

Electronics
Books
Clothing

- Laptop → (link to /products/1)
- React Handbook → (link to /products/2)
- T-Shirt → (link to /products/3)
```

At `/products?category=electronics`:
```
Filter: [electronics ▾]

- Laptop → (link to /products/1)
```

At `/products/2`:
```
← Back to Products

React Handbook
Category: books
A comprehensive guide to React development.
```
