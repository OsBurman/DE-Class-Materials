# Exercise 02 – Route Parameters and Query Parameters

## Learning Objectives
- Read dynamic **route parameters** (`:id`) via `ActivatedRoute.snapshot.paramMap`
- Read optional **query parameters** (`?category=`) via `queryParamMap`
- Navigate programmatically with `Router.navigate()` including `queryParams`

## Background
Real applications almost always need to pass data through the URL — a product id, a user
slug, a page number, etc. Angular's `ActivatedRoute` service lets you read both **path
parameters** (`:id`) and **query parameters** (`?key=value`) from the active URL.

## Exercise

You have a small product catalogue app with two routes:

| Route | Component | Notes |
|---|---|---|
| `/products` | `ProductListComponent` | Lists products; clicking a product navigates to its detail with a query param |
| `/products/:id` | `ProductDetailComponent` | Reads `:id` from path and `category` from query string |

### Starter code TODOs

**`app.module.ts`**
- TODO 1 – Import `RouterModule` and `Routes` from `@angular/router`
- TODO 2 – Define a `routes` array: `''` → redirect to `'/products'`; `'products'` → `ProductListComponent`; `'products/:id'` → `ProductDetailComponent`
- TODO 3 – Add `RouterModule.forRoot(routes)` to `imports`

**`product-list.component.ts`**
- TODO 4 – Inject `Router` and call `this.router.navigate(['/products', product.id], { queryParams: { category: product.category } })` when a product card is clicked

**`product-detail.component.ts`**
- TODO 5 – Inject `ActivatedRoute`
- TODO 6 – In `ngOnInit`, read `this.route.snapshot.paramMap.get('id')` and store in `this.productId`
- TODO 7 – In `ngOnInit`, read `this.route.snapshot.queryParamMap.get('category')` and store in `this.category`

## Files
```
starter-code/
  app.module.ts
  app.component.ts
  product-list.component.ts
  product-detail.component.ts
solution/
  app.module.ts
  app.component.ts
  product-list.component.ts
  product-detail.component.ts
```

## Expected Behaviour
1. App opens on `/products` showing a list of three sample products.
2. Clicking a product navigates to `/products/2?category=electronics` (for example).
3. The detail page displays the id and category read from the URL.
4. A "Back" link returns to `/products`.
