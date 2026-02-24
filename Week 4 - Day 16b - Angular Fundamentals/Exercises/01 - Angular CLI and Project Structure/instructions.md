# Exercise 01: Angular CLI and Project Structure

## Objective
Understand how an Angular project is organized and what each key file does by reading, annotating, and completing a pre-scaffolded project skeleton.

## Background
When you run `ng new my-app`, the Angular CLI generates a standardized folder structure. Every Angular developer must be fluent in this layout — knowing where to find components, modules, configuration, and assets. This exercise walks you through the skeleton of a real Angular project and asks you to complete the missing pieces.

> **Running Angular exercises:** These files mirror what the Angular CLI generates. To run them for real:
> 1. Install Node.js (https://nodejs.org) and Angular CLI: `npm install -g @angular/cli`
> 2. Create a project: `ng new day16b-exercises --no-standalone`
> 3. Replace the generated files with the ones in this folder
> 4. Start the dev server: `ng serve` — open `http://localhost:4200`
>
> You can also read through the files to understand the structure without running them.

## Requirements

1. Open `app.module.ts` in `starter-code/`. This is the **root NgModule**. Complete the two TODO items:
   - Add `BrowserModule` to the `imports` array (it is already imported at the top)
   - Add `AppComponent` to the `bootstrap` array (it is already declared in `declarations`)

2. Open `app.component.ts` in `starter-code/`. Complete the two TODO items:
   - Set the `selector` to `'app-root'`
   - Set the `templateUrl` to `'./app.component.html'`

3. Open `app.component.html` in `starter-code/`. This is the root component's template. Replace the placeholder comment with:
   - An `<h1>` that says "Welcome to Angular!"
   - A `<p>` that says "This project was created with the Angular CLI."
   - A `<p>` that says "Component selector: app-root"

4. Open `app.component.ts` and add a `title` property to the `AppComponent` class with the value `'My Angular App'`.

5. Add a comment above each decorator and class in `app.module.ts` and `app.component.ts` explaining what that decorator does (one sentence each).

## Hints
- `@NgModule` is Angular's way of grouping related components, directives, pipes, and services into a cohesive block
- The `bootstrap` array in `@NgModule` tells Angular which component to render into `index.html`'s `<app-root>` tag
- `@Component` is a TypeScript decorator — it attaches metadata (selector, template, styles) to a class
- The `selector` in `@Component` matches an HTML tag name; Angular replaces `<app-root></app-root>` in `index.html` with this component's template

## Expected Output
When served with `ng serve`, the browser at `http://localhost:4200` displays:

```
Welcome to Angular!
This project was created with the Angular CLI.
Component selector: app-root
```
