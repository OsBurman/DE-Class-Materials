# Day 20b — Part 2 Walkthrough Script
# Angular Testing with Jasmine & Karma — Afternoon Session

**Files referenced (in order):**
1. `01-component-tests.spec.ts`
2. `02-service-and-http-tests.spec.ts`

**Total time:** ~90 minutes  
**Format:** Instructor-led code walkthrough — files open in editor, `ng test` running in a terminal

---

## Segment 1 — Afternoon Intro & Why Testing Matters (5 min)

> "Welcome back. This afternoon is about testing — specifically Angular testing with Jasmine and Karma."

> "I want to start with a question. Show of hands: has anyone worked on a codebase where you were afraid to change something because you didn't know what would break?"

*(Pause for hands)*

> "That's exactly the pain that tests solve. Tests are a safety net. They let you refactor, add features, and update dependencies with confidence — because if you break something, a test tells you immediately."

> "In Angular, the testing stack is Jasmine for the test syntax and assertions, and Karma as the test runner that runs your specs in a browser. Angular CLI sets all of this up for you automatically — every `ng generate component` creates a `.spec.ts` file alongside it."

> "Let's open `01-component-tests.spec.ts` and start from the top."

---

## Segment 2 — Jasmine Structure: describe, it, expect (8 min)

*Navigate to Section 1 — "Jasmine Basics — matchers reference"*

> "Before we look at Angular-specific code, let's make sure everyone is comfortable with Jasmine's syntax. It's very readable — it's designed to read almost like English."

> "Three building blocks. `describe()` groups related tests — think of it as a chapter title. `it()` is a single test — the scenario you're testing. And `expect()` is your assertion — the claim you're making about the code."

> "Look at the test: `it('should demonstrate common matchers', () => { ... })`. The string you pass to `it()` should complete the sentence 'it should…'. This makes test output self-documenting."

*Walk through the matchers list*

> "`toBe()` is strict equality — triple equals. Use it for primitives. `toEqual()` does deep comparison — use it for objects and arrays. These are the two you'll use most."

> "**Watch out:** empty arrays — `[]` — are truthy in JavaScript. This surprises people. `expect([]).toBeTruthy()` passes. If you want to assert an array is empty, use `expect(array.length).toBe(0)` or `expect(array).toEqual([])`."

> "Every matcher can be negated with `.not`: `expect('Angular').not.toBe('React')`. Clean and readable."

---

## Segment 3 — TestBed and ComponentFixture (15 min)

*Navigate to Section 2 — GreetingComponent tests*

> "Now the Angular-specific part. When you test a component, you can't just call `new GreetingComponent()` — Angular components depend on a template, change detection, dependency injection, and a bunch of other framework machinery."

> "TestBed is Angular's test module factory. It creates a miniature Angular environment — just enough framework to run your component. Think of it as a lightweight NgModule just for your test."

> "`TestBed.configureTestingModule({})` sets it up. Look at our configuration — `imports: [GreetingComponent]`. Because `GreetingComponent` is standalone, it goes in `imports`. If it were an older NgModule-style component, it would go in `declarations`. Standalone is the modern approach and what you'll use in new Angular 14+ projects."

> "`compileComponents()` is async — it processes the template and styles. Always `await` it."

> "Then `TestBed.createComponent(GreetingComponent)` instantiates the component and returns a `ComponentFixture`. Think of the fixture as a wrapper that gives you three windows into your component:"

> "One: `fixture.componentInstance` — the TypeScript class. You can set properties and call methods directly on it."
> "Two: `fixture.nativeElement` — the raw DOM element."
> "Three: `fixture.debugElement` — Angular's wrapper around the DOM, which understands the framework. This is usually what you want."

*Point to `fixture.detectChanges()`*

> "This is the most important method you'll use. Angular doesn't automatically update the template in tests — you have to tell it to. `fixture.detectChanges()` triggers a change detection cycle, which processes all your bindings and renders the template."

> "**Watch out:** If you forget `detectChanges()` in `beforeEach`, your tests will query an empty DOM. Nothing will be in the template yet. Always call `detectChanges()` in `beforeEach` after creating the fixture, and again after any state change you want to see in the DOM."

*Navigate to the `should render default name` test*

> "Now we query the DOM. `fixture.debugElement.query(By.css('[data-testid=\"heading\"]'))`. The `By.css()` utility accepts any CSS selector. I'm using `data-testid` attributes — this is the recommended approach."

> "Why `data-testid` instead of class names? Class names are for styling. If a designer renames a class, your test breaks even though the component works fine. `data-testid` is your test's contract with the template — independent of styling."

> "`.nativeElement` gives us the actual DOM element, so we can use `.textContent` to check what's displayed."

---

## Segment 4 — Testing @Input and @Output (10 min)

*Navigate to `should display the provided name` test*

> "Testing @Input is straightforward: set the property on `componentInstance`, then call `detectChanges()` to push the change to the template. That's the full pattern."

> "Notice the order: set property, then detectChanges. Not the other way around. Think of detectChanges as 'commit the pending state to the DOM'."

*Navigate to Section 3 — CounterComponent tests*

> "CounterComponent has both @Input simulation (setting count directly) and @Output testing. Let's focus on interaction first."

*Navigate to the increment test*

> "To click a button, find it with `debugElement.query(By.css(...))` and call `.triggerEventHandler('click', null)`. The second argument is the event object — we pass `null` because this handler doesn't use the event data."

> "After the click, call `detectChanges()` again — because the click changed `component.count`, and we need Angular to sync that to the template."

> "Two assertions: we check the component property directly (`component.count`), and we check the DOM text. Both are valid strategies. Checking the property is faster; checking the DOM is more confidence-inspiring because it verifies the template binding."

*Navigate to the @Output tests*

> "`spyOn(component.countChanged, 'emit')` wraps the EventEmitter's `emit` method with a spy. After clicking the button, `toHaveBeenCalledWith(1)` verifies the component emitted the right value."

> "This is the pattern for all @Output testing: spy on `.emit`, trigger the action, assert emit was called with the right argument."

---

## Segment 5 — Async Testing with fakeAsync (10 min)

*Navigate to Section 4 — CourseSearchComponent async tests*

> "Real components often do things asynchronously — setTimeout, HTTP calls, Promises. Angular's `fakeAsync` utility lets you test these synchronously by controlling time."

> "Look at the `search()` method in `CourseSearchComponent`. It uses `setTimeout(200)` — wait 200ms then update the result count. In a real test, you'd have to wait 200ms. In a `fakeAsync` test, you fast-forward time with `tick(200)` — it's instant."

> "The wrapper: `it('...', fakeAsync(() => { ... }))`. Inside, after triggering the async operation, call `tick(200)` to advance the clock. Then `detectChanges()` to sync the template."

> "**Watch out:** If you use `fakeAsync` but don't call `tick()` to drain all pending timers, Jasmine will throw: 'X timer(s) still in the queue.' You must tick enough to clear all pending async work."

> "For real Promises and HTTP — which we'll see shortly — `waitForAsync` is often better. But for `setTimeout` and `setInterval`, `fakeAsync` + `tick` is the standard pattern."

---

## Segment 6 — Testing Signal Components (5 min)

*Navigate to Section 5 — CartComponent Signal tests*

> "Signal-based components are tested identically to traditional components. The test structure is exactly the same: TestBed, fixture, detectChanges, query DOM."

> "The one thing to understand: signals update synchronously, but the template still needs `detectChanges()` to pick up the new values. So you always call detectChanges after any action that would change a signal."

> "Notice the last test — `should be able to read signal values directly on the component`. We can call `component.itemCount()` directly in our assertion — without going through the DOM at all. This is a nice advantage of signals: they're just functions you can call in tests."

> "Now let's open `02-service-and-http-tests.spec.ts`."

---

## Segment 7 — Testing Plain Services (8 min)

*Navigate to Section 1 — CartService tests*

> "Services without dependencies are the simplest thing to test. You don't even need TestBed — you could just `new CartService()`. But using `TestBed.inject(CartService)` is the conventional approach because it works with Angular's DI system."

> "Look at the `beforeEach`. `TestBed.configureTestingModule({})` with empty config, then `TestBed.inject(CartService)`. That's it."

> "The tests themselves read like specifications: 'should start with empty cart', 'should add a course', 'should not add the same course twice'. Each test sets up state, calls methods, and asserts the result."

*Navigate to the `return a COPY of items` test*

> "This test is subtle and important. It verifies that `getItems()` returns a copy, not the internal array. If someone gets the array and pushes to it, the service's state shouldn't change. We call `getItems()`, push to the returned array, then verify the service still reports 1 item."

> "Testing boundary conditions and defensive code like this is what separates thorough test suites from shallow ones."

---

## Segment 8 — Jasmine Spies Deep Dive (10 min)

*Navigate to Section 2 — Spy demos*

> "Spies are the core of mocking in Jasmine. Two tools: `spyOn()` and `jasmine.createSpyObj()`."

> "`spyOn(object, 'methodName')` wraps a specific method on an existing object. The real code doesn't run — the spy intercepts the call and records it. You can then assert `toHaveBeenCalled()`, `toHaveBeenCalledWith(...)`, `toHaveBeenCalledTimes(n)`."

*Navigate to the `.and.returnValue()` example*

> "`.and.returnValue(99)` makes the spy lie about its return value. `service.getTotal()` would normally return 0 (cart is empty), but the spy makes it return 99. This is how you set up specific conditions for a test without needing real data."

*Navigate to `jasmine.createSpyObj`*

> "This is what you'll use most for service mocking. Pass a name string and an array of method names. You get back an object where every method is a spy. No real logic, no real HTTP, no real database — just tracking functions you can configure."

> "Look at `.and.returnValue(of(fakeCourses))`. `of()` from RxJS wraps a value in an Observable that completes immediately. This is the standard way to stub Observable-returning methods in tests."

> "**Watch out:** `of()` is synchronous. The subscription callback runs immediately when `subscribe()` is called. There's no asynchrony to worry about. This makes testing Observable service methods very straightforward."

---

## Segment 9 — HttpClientTestingModule (12 min)

*Navigate to Section 3 — CourseService HTTP tests*

> "Now the most important testing topic for backend-connected Angular apps: testing services that use HttpClient."

> "The setup: `imports: [HttpClientTestingModule]`. This replaces the real HttpClient with a test double. No real network calls are made. You control what the 'server' returns."

> "You also inject `HttpTestingController`. This is your API for the fake HTTP backend."

*Navigate to the GET all courses test*

> "The pattern has four steps. Step one: call the service method — this queues a pending HTTP request internally."

> "Step two: `httpController.expectOne(url)` — assert that exactly one request was made to that URL and get a reference to it. If zero requests or two requests were made, the test fails here. This is how you verify your service actually called the right endpoint."

> "Step three: `req.flush(data)` — provide the mock response. This is like the server responding with data. The Observable in the service resolves, and the subscription callback runs."

> "Step four: assert on the received data."

> "This is the complete cycle. The `afterEach` with `httpController.verify()` is critical — it asserts that no HTTP requests were made that weren't expected. Without it, you might have extra requests in your code and not know it."

*Navigate to the POST test*

> "Look at `expect(req.request.body).toEqual(newCourse)`. We're verifying the REQUEST body — what your service is sending to the server. This is important: you want to know not just that an HTTP call was made, but that the right data was sent."

*Navigate to the 404 error test*

> "Error handling tests are often the most valuable. `req.flush('Not Found', { status: 404, statusText: 'Not Found' })` — the second argument to flush lets you set the HTTP status code."

> "The service maps 404 to 'Course not found'. The test verifies that mapping. Without this test, a future developer might change the error handling and break the user experience without knowing it."

*Navigate to the retry test*

> "Look at the 500 test — it uses `httpController.match()` instead of `expectOne()`. Because our service calls `retry(1)`, it makes two HTTP requests when the first one fails. `match()` returns ALL matching requests. We then flush both with errors."

> "This is testing that your retry logic actually works. This is the kind of test that catches bugs in production HTTP handling."

---

## Segment 10 — Mocking Services in Component Tests (8 min)

*Navigate to Section 4 — CourseListComponent with mocked CourseService*

> "This is where everything comes together. A component that depends on a service. How do we test the component without using the real service?"

> "We create spy objects for both `CourseService` and `LoggerService`, configure their return values, and then tell TestBed to use our fakes instead of the real ones with `providers: [{ provide: CourseService, useValue: mockCourseService }]`."

> "Angular's DI system sees 'I need a CourseService' and finds our spy object. The component gets the spy, calls `getCourses()`, and gets the `of(fakeCourses)` Observable back. No network, no backend, deterministic data."

*Navigate to the error scenario test*

> "This test demonstrates the power of spy objects: you can change behavior per test. `mockCourseService.getCourses.and.returnValue(throwError(...))` makes the spy throw an error Observable just for this test."

> "We recreate the component to trigger `ngOnInit` fresh. Then we verify the error message appears and that `loggerService.error` was called."

> "Notice `fail('Should have thrown an error')` in the HTTP tests earlier. Using `fail()` in the `next` callback means: if we somehow get a success response, fail the test. This is a defensive pattern that catches incorrect test setup."

*Navigate to Section 5 — useValue inline object*

> "The simplest mock pattern: an inline object with only the methods you need. No tracking, no spy matchers — just stub behavior. Use this when you don't need to verify the service was called, just that the component handles the data correctly."

---

## Segment 11 — Running Tests & The Test Suite (5 min)

> "Let me demo `ng test` quickly."

*(Run `ng test` in the terminal if time permits)*

> "Karma opens a browser. All spec files run. Green dots = passing. Red F = failing."

> "For CI environments: `ng test --no-watch --browsers=ChromeHeadless`. Headless Chrome runs without a UI — perfect for pipelines."

> "Some quick organisation advice: keep your `.spec.ts` file next to its source file. Angular CLI does this by default. Don't put all tests in a separate folder — it makes it harder to keep tests in sync with implementation."

> "Aim for testing behavior, not implementation. Don't test that `this.count++` was called — test that the displayed count is 1. Tests that check behavior survive refactoring; tests that check implementation don't."

---

## Segment 12 — Week 4 & Angular Module Wrap-Up (5 min)

> "That's the end of Week 4 and the Angular module. Let's take a moment to look at how far you've come."

> "Day 16b: Angular fundamentals — components, templates, directives, pipes. Day 17b: Services and dependency injection. Day 18b: Routing and forms. Day 19b: HTTP and RxJS. Today: Signals and testing."

> "You can now build a complete Angular application. You can manage state with Signals, fetch data with HttpClient, navigate between pages with the Router, and write tests that give you confidence your app works."

> "Next week we move into databases and backend. Monday we start SQL — and eventually you'll be building Spring Boot APIs that your Angular frontends talk to. The full-stack picture is coming together."

> "Have a great weekend. See you Monday."

---

## Instructor Q&A Prompts

1. **"What's the difference between `toBe()` and `toEqual()`? When would `toBe()` fail but `toEqual()` pass?"**  
   *(Expected: `toBe()` is `===` — two different objects with the same content are not `===`. `toEqual()` does deep comparison — it checks structure, not reference)*

2. **"Why do we call `fixture.detectChanges()` after setting a property in a test? Doesn't Angular update automatically?"**  
   *(Expected: Angular only runs change detection automatically in the real browser Zone.js environment. In tests, you control it manually so tests are deterministic)*

3. **"What happens if you forget `httpController.verify()` in `afterEach`?"**  
   *(Expected: unexpected HTTP requests — like a retry or an extra call — won't cause test failures. You'd have a false positive — tests pass but your service has a bug)*

4. **"We used `of(fakeCourses)` to stub an Observable. When does the subscription callback run — synchronously or asynchronously?"**  
   *(Expected: synchronously. `of()` emits and completes immediately on subscribe. This is why we don't need `fakeAsync` for basic Observable stubbing)*

5. **"If a component makes an HTTP call in `ngOnInit`, when exactly does that call happen in the test?"**  
   *(Expected: when `fixture.detectChanges()` is called — that triggers `ngOnInit`. Before that, no lifecycle hooks have run)*
