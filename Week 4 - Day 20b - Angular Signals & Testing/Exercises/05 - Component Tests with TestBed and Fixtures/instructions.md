# Exercise 05: Component Tests with TestBed and Fixtures

## Objective
Write Jasmine unit tests for an Angular component using `TestBed`, component fixtures, and mocked service dependencies.

## Background
Angular's `TestBed` is the primary tool for creating an in-memory Angular environment in tests. It lets you configure a module (with components, providers, and imports), compile the component, and then interact with its DOM and class properties. A `ComponentFixture` wraps the component instance and its host element, giving you access to `fixture.componentInstance`, `fixture.nativeElement`, and `fixture.detectChanges()`.

## Requirements
You are given a `CounterComponent` and a `CounterService`. Your job is to write the tests.

### Component under test (`counter.component.ts`)
The component:
- Displays the current count in a `<span id="count">`.
- Displays a "positive" or "negative/zero" label in a `<span id="sign">`.
- Has an **Increment** button and a **Decrement** button that call `service.increment()` / `service.decrement()`.
- Shows the current count by calling `service.getCount()` in the template.

### Service (`counter.service.ts`)
The service holds a private `count` and exposes `increment()`, `decrement()`, and `getCount(): number`.

### Tests to write (`counter.component.spec.ts`)
Write the following test cases:

1. **`should create the component`** — verify the component instance is truthy.
2. **`should display the initial count as 0`** — call `fixture.detectChanges()` and assert `<span id="count">` text content is `'0'`.
3. **`should call service.increment() when Increment button is clicked`** — spy on `service.increment`, click the button, and verify the spy was called once.
4. **`should call service.decrement() when Decrement button is clicked`** — same pattern for decrement.
5. **`should display "positive" when count > 0`** — use the mock service to return `1` from `getCount()`, call `detectChanges()`, and assert the sign label is `'positive'`.
6. **`should display "negative/zero" when count <= 0`** — mock `getCount()` to return `0`, assert sign label is `'negative/zero'`.

Use a **mock service** (`{ provide: CounterService, useValue: mockService }`) instead of the real service.

## Hints
- In `TestBed.configureTestingModule`, add the component to `declarations` (or `imports` if standalone) and provide the mock via the `providers` array.
- Create the mock with `jasmine.createSpyObj('CounterService', ['increment', 'decrement', 'getCount'])`.
- Set the return value of `getCount` with `mockService.getCount.and.returnValue(1)`.
- Always call `fixture.detectChanges()` after changing mock return values before asserting DOM content.
- Get a DOM element with `fixture.nativeElement.querySelector('#count')`.

## Expected Output
All 6 specs pass (green):
```
Component: CounterComponent
  ✓ should create the component
  ✓ should display the initial count as 0
  ✓ should call service.increment() when Increment button is clicked
  ✓ should call service.decrement() when Decrement button is clicked
  ✓ should display "positive" when count > 0
  ✓ should display "negative/zero" when count <= 0

6 specs, 0 failures
```
