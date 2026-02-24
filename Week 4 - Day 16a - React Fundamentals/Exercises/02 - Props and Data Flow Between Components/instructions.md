# Exercise 02: Props and Data Flow Between Components

## Objective
Pass data from a parent component to child components using **props**, and understand React's one-way data flow.

## Background
In React, data flows **down** the component tree — from parent to child — via **props** (short for properties). Props are read-only: a child cannot modify the props it receives. This makes data flow predictable and easy to trace. Props can be any JavaScript value: strings, numbers, booleans, arrays, objects, or functions.

## Requirements

1. Create a `UserCard` component that accepts these props:
   - `name` (string)
   - `role` (string)
   - `isActive` (boolean)
   
   It should render a `<div className="user-card">` containing:
   - `<h3>` with the user's `name`
   - `<p>` with `"Role: "` + `role`
   - `<p>` with `"Status: Active"` if `isActive` is `true`, or `"Status: Inactive"` if `false`

2. Create a `Badge` component that accepts a `label` prop (string) and renders a `<span className="badge">` with that label.

3. Create a `ProductCard` component that accepts:
   - `title` (string)
   - `price` (number)
   - `category` (string)
   - `inStock` (boolean, **default value: `true`**)
   
   Render:
   - `<h3>` with `title`
   - `<p>` with `"$" + price.toFixed(2)`
   - A `<Badge label={category} />`
   - `<p>` with `"In Stock"` or `"Out of Stock"` based on `inStock`

4. Create an `App` component that renders:
   - Three `<UserCard />` instances with different prop values (mix active/inactive)
   - Two `<ProductCard />` instances: one with `inStock={false}`, one using the default

5. Demonstrate **destructured props** in at least one component (use `function Card({ name, role })` syntax rather than `function Card(props)`).

## Hints
- Props are passed like HTML attributes: `<UserCard name="Alice" role="Admin" isActive={true} />`
- Use `{true}` / `{false}` for boolean props, not the string `"true"`
- Default prop values can be set with default parameter syntax: `function Card({ inStock = true })`
- To pass a number, wrap it in curly braces: `price={29.99}` not `price="29.99"`

## Expected Output
The browser renders three user cards and two product cards, e.g.:

```
Alice                   Bob                     Carol
Role: Admin             Role: Developer         Role: Designer
Status: Active          Status: Inactive        Status: Active

Widget Pro              Basic Kit
$29.99                  $9.99
[Electronics]           [Tools]
In Stock                Out of Stock
```
