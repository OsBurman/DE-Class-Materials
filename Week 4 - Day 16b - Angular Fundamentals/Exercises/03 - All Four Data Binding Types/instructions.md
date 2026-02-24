# Exercise 03: All Four Data Binding Types

## Objective
Implement Angular's four data binding techniques — interpolation, property binding, event binding, and two-way binding — plus template reference variables, all in a single component.

## Background
Angular data binding is the mechanism that keeps your component's TypeScript class in sync with its HTML template. The four types cover every direction and use case: displaying values (interpolation), setting element properties (property binding), responding to user events (event binding), and syncing a form field in both directions at once (two-way binding). Template reference variables give you a way to reference DOM elements or Angular directives directly in the template.

## Requirements

1. **Interpolation** — In `binding-demo.component.html`, display the `username` and `score` class properties using `{{ }}` syntax:
   - `<p>Player: {{ username }}</p>`
   - `<p>Score: {{ score }}</p>`

2. **Property binding** — Bind the `isDisabled` boolean property to a button's `disabled` attribute and the `imageUrl` string to an `<img>` element's `src`:
   - `<button [disabled]="isDisabled">Submit</button>`
   - `<img [src]="imageUrl" [alt]="imageAlt" width="80">`

3. **Event binding** — Wire up two buttons using `(click)` event binding:
   - A "Level Up" button that calls `levelUp()` — this method increments `score` by 10
   - A "Reset" button that calls `reset()` — this method resets `score` to 0 and `username` to `'Player One'`

4. **Two-way binding** — Add a text input that uses `[(ngModel)]` to bind to the `username` property bidirectionally. As the user types, the interpolation in Requirement 1 should update in real time.
   - `<input [(ngModel)]="username" placeholder="Enter username">`
   - You must import `FormsModule` in `app.module.ts` for `ngModel` to work.

5. **Template reference variable** — Add a second input for a "message" and a button:
   - `<input #messageInput placeholder="Type a message">`
   - `<button (click)="logMessage(messageInput.value)">Log</button>`
   - In `binding-demo.component.ts`, implement `logMessage(msg: string)` to set `lastMessage = msg`
   - Display `lastMessage` in the template with interpolation

## Hints
- Property binding syntax `[property]="expression"` evaluates the expression and passes the result to the DOM property — NOT the HTML attribute
- Event binding syntax `(event)="handler()"` attaches a DOM event listener; use `$event` to access the event object if needed
- `[(ngModel)]` is "banana in a box" — property binding `[]` in and event binding `()` out combined
- Template reference variables (`#varName`) capture a reference to the element; you can pass `varName.value` to a method

## Expected Output
```
Player: Player One
Score: 0

[text input pre-filled with "Player One" — editing updates the line above]
[Level Up button — clicking adds 10 to Score]
[Reset button — clicking resets Score to 0 and name to "Player One"]
[img displays the placeholder image]
[Submit button is disabled]
[message input + Log button — clicking sets Last Message below]

Last Message: (whatever was typed)
```
