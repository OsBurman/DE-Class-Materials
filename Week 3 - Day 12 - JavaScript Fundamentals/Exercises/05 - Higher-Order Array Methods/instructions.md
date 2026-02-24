# Exercise 05: Higher-Order Array Methods

## Objective
Use `map`, `filter`, `reduce`, `find`, `findIndex`, `some`, `every`, and `forEach` to process arrays without mutating the source data.

## Background
Higher-order array methods take a **callback function** as their argument and apply it to each element. They are the idiomatic JavaScript alternative to manual `for` loops for transformations, filtering, and aggregation. Because most of them return a **new array** (or a single value), the original data stays intact, making code easier to reason about and test.

## Requirements

Use this shared dataset for all parts:

```js
const products = [
  { id: 1, name: "Laptop",  price: 999,  category: "Electronics", inStock: true  },
  { id: 2, name: "T-Shirt", price: 25,   category: "Clothing",    inStock: true  },
  { id: 3, name: "Headphones", price: 149, category: "Electronics", inStock: false },
  { id: 4, name: "Jeans",   price: 60,   category: "Clothing",    inStock: true  },
  { id: 5, name: "Tablet",  price: 499,  category: "Electronics", inStock: true  },
  { id: 6, name: "Jacket",  price: 120,  category: "Clothing",    inStock: false },
];
```

1. **`forEach`** — log each product's name and price in the format `Laptop: $999`.

2. **`map`** — create a new array `productNames` containing only the product names. Log it.

3. **`map` with transformation** — create `discounted` where every price is reduced by 10%. Log each item as `{ name, price: <new_price> }` (round to 2 decimal places).

4. **`filter`** — create `inStockProducts` containing only products where `inStock === true`. Log the names.

5. **`filter` chained with `map`** — create `electronicNames`: the names of all Electronics that are in stock. Log the array.

6. **`reduce`** — calculate the total price of all products. Log: `Total value: $1852`.

7. **`reduce` to group** — use `reduce` to build an object that groups product names by category:
   ```
   { Electronics: ['Laptop', 'Headphones', 'Tablet'], Clothing: ['T-Shirt', 'Jeans', 'Jacket'] }
   ```

8. **`find`** — find the first product with a price over $400 and log its name.

9. **`findIndex`** — find the index of the product named `"Jeans"` and log it.

10. **`some`** — log whether any product is out of stock.

11. **`every`** — log whether every product costs less than $1000.

12. **Chaining** — in a single chain: filter products that are in stock AND cost more than $50, map to their names, sort alphabetically, and log the result.

## Hints
- `map` always returns an array of the **same length** as the input. Use it for transformations.
- `filter` returns an array that may be **shorter** than the input. Use it for selection.
- `reduce` takes an **accumulator** and the current element — always provide the initial value as the second argument to `reduce(callback, initialValue)`.
- `find` returns the **first matching element** (or `undefined`); `findIndex` returns its **index** (or `-1`).

## Expected Output

```
--- forEach ---
Laptop: $999
T-Shirt: $25
Headphones: $149
Jeans: $60
Tablet: $499
Jacket: $120

--- map: names ---
[ 'Laptop', 'T-Shirt', 'Headphones', 'Jeans', 'Tablet', 'Jacket' ]

--- map: 10% discount ---
{ name: 'Laptop', price: 899.1 }
{ name: 'T-Shirt', price: 22.5 }
{ name: 'Headphones', price: 134.1 }
{ name: 'Jeans', price: 54 }
{ name: 'Tablet', price: 449.1 }
{ name: 'Jacket', price: 108 }

--- filter: in stock ---
[ 'Laptop', 'T-Shirt', 'Jeans', 'Tablet' ]

--- filter+map: electronics in stock ---
[ 'Laptop', 'Tablet' ]

--- reduce: total price ---
Total value: $1852

--- reduce: group by category ---
{
  Electronics: [ 'Laptop', 'Headphones', 'Tablet' ],
  Clothing: [ 'T-Shirt', 'Jeans', 'Jacket' ]
}

--- find: first product > $400 ---
Laptop

--- findIndex: "Jeans" ---
3

--- some: any out of stock ---
true

--- every: all under $1000 ---
true

--- chain: in stock, > $50, names, sorted ---
[ 'Jeans', 'Laptop', 'Tablet' ]
```
