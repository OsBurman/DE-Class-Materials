# Exercise 03: Attribute Directives and Built-in Pipes

## Objective
Practice applying `[ngClass]` and `[ngStyle]` attribute directives to conditionally style elements, and apply Angular's built-in pipes (`date`, `currency`, `uppercase`, `number`) to format displayed values.

## Background
Attribute directives change the appearance or behavior of an existing element without altering the DOM structure. Pipes transform values in templates before display. You are building a sales report table that applies conditional row highlighting and formats dates, prices, and text using Angular's built-in tools.

## Requirements

1. Create a `SalesReportComponent` with a `sales` array of at least 4 objects:
   ```
   { product: string, amount: number, date: Date, featured: boolean }
   ```
   Initialize at least one item with `featured: true` and at least one with `amount > 1000`.

2. Render each sale as a table row using `*ngFor`.

3. Apply **`[ngClass]`** to each row:
   - Add class `'featured'` when `sale.featured` is `true`.
   - Add class `'high-value'` when `sale.amount > 1000`.

4. Apply **`[ngStyle]`** to the product name cell to set `font-weight: 'bold'` when the sale is featured, and `'normal'` otherwise.

5. Use the following built-in **pipes** in the template:
   - `uppercase` on the product name.
   - `currency` on the amount (default USD).
   - `date:'mediumDate'` on the sale date.
   - `number:'1.0-0'` (no decimals) on the amount in a separate column labeled "Units rounded".

6. Add CSS styles for `.featured { background-color: #fffbcc; }` and `.high-value { border-left: 4px solid crimson; }` (inline `<style>` in the component or `styles` array).

7. Declare the component in `AppModule`.

## Hints
- `[ngClass]` accepts an object: `[ngClass]="{ 'featured': sale.featured, 'high-value': sale.amount > 1000 }"`.
- `[ngStyle]` accepts an object: `[ngStyle]="{ 'font-weight': sale.featured ? 'bold' : 'normal' }"`.
- Pipes chain with `|`: `{{ sale.amount | currency | uppercase }}` would apply both.
- The `date` pipe accepts a format string: `{{ sale.date | date:'mediumDate' }}`.

## Expected Output
```
Sales Report

PRODUCT NAME         | Amount       | Date          | Units rounded
---------------------------------------------------------------------------
REACT WORKSHOP       | $1,200.00    | Feb 10, 2026  | 1,200   ← featured+high-value row (yellow bg, red left border)
ANGULAR BASICS       | $450.00      | Feb 12, 2026  | 450
SPRING BOOT BOOTCAMP | $980.00      | Feb 14, 2026  | 980     ← featured row (yellow bg, bold name)
JAVA FUNDAMENTALS    | $2,100.00    | Feb 16, 2026  | 2,100   ← high-value (red left border)
```
