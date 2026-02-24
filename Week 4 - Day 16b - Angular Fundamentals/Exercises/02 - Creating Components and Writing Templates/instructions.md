# Exercise 02: Creating Components and Writing Templates

## Objective
Create a new Angular component by hand — with the correct decorator, class, and template — and write template expressions to display dynamic data.

## Background
In Angular, every piece of UI is a component. A component is a TypeScript class decorated with `@Component` that pairs a class (logic + data) with an HTML template (view). The Angular CLI generates components automatically with `ng generate component name`, but every developer must understand the anatomy of a component so they can read, debug, and extend any Angular codebase.

## Requirements

1. **Create a `ProductCardComponent`** in `product-card.component.ts`:
   - Decorate with `@Component`, selector `'app-product-card'`, `templateUrl` pointing to `product-card.component.html`
   - Add these typed properties to the class:
     - `name: string = 'Wireless Headphones'`
     - `price: number = 79.99`
     - `brand: string = 'SoundWave'`
     - `inStock: boolean = true`
     - `rating: number = 4`

2. **Write the template** in `product-card.component.html`:
   - Display product name in an `<h2>`
   - Display brand in a `<p>` as `"Brand: SoundWave"`
   - Display price in a `<p>` as `"Price: $79.99"` — use Angular's `{{ price }}` interpolation
   - Display stock status in a `<p>` as `"In Stock: true"` (just interpolate the boolean)
   - Display rating in a `<p>` as `"Rating: 4 / 5"`
   - Wrap everything in a `<div class="product-card">`

3. **Register and use the component** in `app.module.ts` and `app.component.html`:
   - Declare `ProductCardComponent` in the `declarations` array of `AppModule`
   - Use the `<app-product-card>` selector in `app.component.html` to render it

4. **Add a `getStockLabel()` method** to `ProductCardComponent` that returns `'Available'` if `inStock` is `true`, or `'Sold Out'` if false. Call it in the template as `{{ getStockLabel() }}` next to the stock status.

## Hints
- The `templateUrl` path is relative to the component file: `'./product-card.component.html'`
- Angular interpolation syntax is `{{ expression }}` — it converts any value to a string automatically
- You can call a class method directly in the template: `{{ methodName() }}`
- Don't forget to import `ProductCardComponent` at the top of `app.module.ts` before adding it to `declarations`

## Expected Output
```
Wireless Headphones
Brand: SoundWave
Price: $79.99
In Stock: true  Available
Rating: 4 / 5
```
