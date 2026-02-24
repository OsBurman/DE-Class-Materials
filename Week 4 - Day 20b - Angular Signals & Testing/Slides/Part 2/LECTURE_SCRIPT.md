# Day 20b Part 2 — Testing Angular Applications
## Lecture Script

**Total time: 60 minutes**
**Slides: 16**
**Pace: ~165 words/minute**

---

### [00:00–03:00] Slide 1 — Opening

Welcome back. Part 1 was Angular Signals — synchronous reactive state, `signal()`, `computed()`, `effect()`, and how to bridge signals with Observables. Part 2 is testing. And I want to say upfront: testing is one of those topics where a lot of developers know they should be writing tests, feel vaguely guilty when they don't, and never quite build the habit because the testing framework feels like a second language on top of the language they're already writing.

By the end of this session that should feel different. Angular's testing story is actually one of the better ones across all major frameworks. The CLI generates test files for you. The `TestBed` mirrors the `@NgModule` you already know. And because Angular is opinionated about dependency injection, mocking is clean — you just swap out a service provider and the component doesn't know the difference.

We're going to build up the full picture: Jasmine syntax, the TestBed setup, testing components, testing services, mocking HTTP, and testing signal-based components. Let's go.

---

### [03:00–11:00] Slides 2–4 — Toolchain, Jasmine, Setup

**[Slide 2]**

Three tools. Jasmine is the test framework — it provides the syntax you write: `describe`, `it`, `expect`, the matchers. Think of Jasmine as JUnit for JavaScript. It doesn't run tests itself; it just gives you the vocabulary.

Karma is the test runner. Karma launches a browser — usually Chrome or a headless Chrome — and runs your compiled tests inside that browser. The reason we run in a browser is that Angular components render DOM, and you can't test DOM manipulation in a pure Node.js process. Karma is what you start with `ng test`.

TestBed is Angular-specific. It's Angular's test harness — it sets up the Angular compiler, the dependency injection container, and creates components within that environment. If Jasmine is JUnit and Karma is Maven, TestBed is Spring Boot's `@SpringBootTest` — the thing that wires everything together for integration-level component tests.

The Angular CLI wires all three together when you run `ng new`. Your `angular.json` file points to the Karma configuration. Every file you generate with `ng generate component` comes with a `.spec.ts` file already. That's the test file.

**[Slide 3]**

Let me walk through Jasmine syntax. `describe()` groups related tests into a suite. The first argument is the name — usually the thing you're testing. `it()` is an individual test. The string is the test name — it should be human readable: "adds two positive numbers," "throws on invalid input," "displays error when login fails." The function is the test body.

Inside the test body, `expect()` takes the actual value — the thing your code produced. Then you chain a matcher: `.toBe()` for strict equality, `.toEqual()` for deep object equality. If you need to check that an object or array looks a certain way, use `toEqual`. If you're comparing primitives — numbers, strings, booleans — use `toBe`.

Look at the matchers table. `toContain()` for arrays and strings. `toBeTruthy()` and `toBeFalsy()` when you care about the truthiness but not the specific value. `toHaveBeenCalled()`, `toHaveBeenCalledWith()` — those are spy matchers we'll get to shortly. `toThrow()` when you expect a function to throw — notice that you wrap the call in another function: `expect(() => divide(10, 0)).toThrow()` — because if you just wrote `expect(divide(10, 0))`, the throw would happen before `expect` ever got called.

**[Slide 4]**

Before each test, `beforeEach()`. After each test, `afterEach()`. Once before all tests, `beforeAll()`. Once after all, `afterAll()`.

The critical one is `beforeEach`. This is where you create a fresh instance of whatever you're testing. If you shared one instance across all tests, state from test A could leak into test B and cause order-dependent failures — the most frustrating kind of test bug. Always use `beforeEach` to start fresh.

The most common pattern in `afterEach` for Angular testing is `httpMock.verify()` — we'll get to that when we test HTTP calls. It asserts that no HTTP requests were made that you didn't account for in the test.

The key principle on this slide: tests must be independent. A test that only passes when another test runs first is a hidden dependency, and it will bite you when someone reorders the test file.

---

### [11:00–23:00] Slides 5–7 — TestBed, Fixture, Full Component Test

**[Slide 5]**

TestBed. This is the heart of Angular component testing. `TestBed.configureTestingModule()` mirrors your `@NgModule`. It takes `imports`, `declarations`, and `providers`.

For standalone components — which is the modern Angular pattern we've been using all week — you use `imports` and put the component class in there. For module-based components, you use `declarations`. You'll see both in existing codebases.

The `providers` array is where the magic of testing comes in. Instead of providing the real `AuthService` or `ProductService`, you provide a mock. The component doesn't know the difference — it just injects whatever is provided for that token. We'll get deep into mocking on slides 11 and 12.

`compileComponents()` is asynchronous because in development, components can have external template URLs and style URLs that need to be fetched. Even for inline templates, you still call it for consistency. That's why the `beforeEach` takes an `async` function and you `await` it.

**[Slide 6]**

`TestBed.createComponent()` creates an instance of your component and returns a `ComponentFixture`. The fixture is your test handle. Think of it as a wrapper around both the component instance and its DOM.

The four things you'll use constantly:

`.componentInstance` — the TypeScript class object. You can read and write properties directly. `component.count.set(5)` — you can manipulate signal state from the test.

`.nativeElement` — the root DOM HTMLElement. From here you use `querySelector`, `querySelectorAll`, read `textContent`, check `disabled`, all the standard DOM APIs.

`.debugElement` — Angular's wrapper, which lets you query by directive type, not just CSS.

`.detectChanges()` — this is the one people forget at first. In tests, Angular does not automatically run change detection. You're in control. After any state change — you click a button, you set a signal, you get an HTTP response — you must call `fixture.detectChanges()` before making DOM assertions. Otherwise you're reading stale DOM.

The initial `fixture.detectChanges()` in `beforeEach` triggers the first render and runs `ngOnInit`.

**[Slide 7]**

Let's put it all together with a full component test. The component is a counter. It has a signal, an increment method, and a reset method. The template uses `data-testid` attributes — that's my strong recommendation for test selectors. CSS classes and element selectors change when you refactor or redesign. `data-testid` attributes are there specifically for tests, and by convention you don't remove them without touching the test file too.

Three tests. The first checks the initial display. We query for the element with `data-testid="count"`, read its text content, expect it to be "0". No user interaction, no state changes needed.

The second test verifies increment. We get the increment button, call `.click()`, then `fixture.detectChanges()` to re-render. Now we make two assertions: one on the signal state — `component.count()` should be 1 — and one on the DOM — the paragraph text should now contain "1". Both matter. The signal assertion verifies internal state. The DOM assertion verifies the template updated correctly.

The third test verifies reset. We use `component.count.set(5)` to put the component in a known state — we don't want to click increment five times, that would make the test slow and fragile. We detect changes to render 5, click reset, detect changes again, and assert the signal is back to 0.

Notice that Arrange-Act-Assert pattern: set up state, perform an action, assert the result.

---

### [23:00–33:00] Slides 8–9 — DOM Queries and Service Testing

**[Slide 8]**

Querying the DOM. The `nativeElement` approach uses standard browser DOM APIs. `querySelector` for one element, `querySelectorAll` for all of them. This is what most of your tests will use because it's the same API you use everywhere else in web development.

The `debugElement` approach is for cases where CSS selectors aren't enough. `By.directive(RouterLink)` finds all elements that have the `RouterLink` directive applied — there's no CSS selector equivalent of that.

Triggering events. Clicking buttons is straightforward — `.click()` is a real browser API. Typing in input fields is slightly more work. You set `input.value` directly, then dispatch an `'input'` event, then detect changes. The `dispatchEvent` call is what triggers Angular's `(input)` binding. Without it, Angular doesn't know the value changed.

The `triggerEventHandler` on `debugElement` is an alternative — it calls the Angular event handler directly without going through the browser event system. Slightly more controlled, slightly more Angular-specific.

The `data-testid` best practice note: I recommend adding these to components you're going to test. It signals to your teammates — literally, signals with a sign — that this element is tested and shouldn't be changed without updating the test.

**[Slide 9]**

Testing services. There are two approaches depending on what the service needs.

Option A: direct instantiation. If your service doesn't depend on anything — no HTTP client, no other services, no Angular-specific APIs — you can just `new` it up directly. No TestBed needed. This is the fastest possible unit test. Create the service, call its methods, assert the results. If you're testing a `CalculatorService` with pure math methods, there's no reason to involve TestBed at all.

Option B: TestBed injection. If the service has dependencies — it injects `HttpClient`, or it injects other services — then you need TestBed to set up the dependency injection container. `TestBed.configureTestingModule` with `providers: [CartService]`. Then `TestBed.inject(CartService)` to get the instance. This is the same DI system as in production, just in test mode.

The critical difference from `createComponent`: you use `TestBed.inject()` to get services, not `TestBed.createComponent()`. The `inject()` method resolves a service from the DI container.

Notice the signal-based service tests at the bottom. `service.total()` returns 0 initially. After `service.addItem()`, `service.total()` returns 50. Synchronous, readable, no async, no subscribe. This is one of the nice benefits of signals for testing — the state is always directly readable.

---

### [33:00–43:00] Slides 10–11 — HTTP Testing and Mocking

**[Slide 10]**

Testing HTTP calls. This is where a lot of developers feel uncertain, but once you see the pattern it's actually one of the cleanest things about Angular testing.

The `HttpClientTestingModule` replaces the real HTTP layer with a controlled fake. When your service calls `this.http.get('/api/products')`, it doesn't actually make a network request. It's intercepted. You control exactly what comes back.

Setup: import `HttpClientTestingModule` and inject both the `ProductService` and the `HttpTestingController`. The controller is your handle on the fake HTTP layer.

`afterEach(() => httpMock.verify())` — always include this. It asserts that every HTTP request made during the test was expected and responded to. If your component fired off a request you forgot to account for in the test, `verify()` will fail the test and tell you about it. This prevents ghost requests from silently passing.

Now the test. You call `service.getProducts().subscribe(...)`. This fires the Observable but doesn't send a real request. `httpMock.expectOne('/api/products')` returns a `TestRequest` object and asserts exactly one request was made to that URL. You can inspect `req.request.method` to assert it was a GET. Then `req.flush(mockData)` — flush delivers the response. This triggers the subscribe callback. Your assertions inside subscribe run.

The error test works the same way but you call `flush` with an error status object. You verify the component handled the 500 correctly.

The pattern is: call the service method, intercept the request, assert the request was formed correctly, flush a response, assert the response was handled correctly. Four steps every time.

**[Slide 11]**

Mocking dependencies. Three approaches, each for a different situation.

Approach one: `useValue` with an inline object. This is the simplest mock. You just write `{ provide: AuthService, useValue: { isLoggedIn: () => true } }`. Angular injects that plain object wherever `AuthService` is requested. The component's code calls `this.authService.isLoggedIn()` and gets `true`. This approach is fine when you only need to control return values and don't need to assert that methods were called.

Approach two: `jasmine.createSpyObj`. This is the one I use most. You call `jasmine.createSpyObj<AuthService>('AuthService', ['isLoggedIn', 'login', 'logout'])` and get back an object where every listed method is a spy. You then configure each spy's behavior: `.and.returnValue(true)`, `.and.returnValue(of({ token: 'abc' }))`. The huge advantage: you can later assert `expect(mockAuthService.login).toHaveBeenCalledWith(credentials)`. If your component called the wrong method, or called it with the wrong args, or forgot to call it entirely, the test catches it.

Approach three: `useClass` with a fake implementation class. Best when the mock is complex enough to deserve its own class, or when you share the same mock across many test files. Write a `MockAuthService` class that implements the `AuthService` interface. Import it once, use it everywhere.

---

### [43:00–52:00] Slides 12–13 — Spies and Signals in Tests

**[Slide 12]**

Spies in depth. We've already seen `jasmine.createSpyObj` for creating spy objects from scratch. `spyOn` is for wrapping an existing method on a real object.

The scenario: your component injects `ProductService` via TestBed. `TestBed.inject(ProductService)` gives you the actual instance from DI. You can then spy on its methods with `spyOn(productService, 'getProducts')`. This keeps the real service object but replaces the specific method with a spy that you control.

`.and.returnValue(of([...]))` — the `of()` creates an Observable that emits immediately. When the component calls `productService.getProducts()`, it gets back the Observable you provided.

The test flow: set up the spy, call `fixture.detectChanges()` which triggers `ngOnInit`, your `ngOnInit` calls `this.productService.getProducts()`, the spy intercepts and returns your mock Observable, the component processes it, detect changes again if needed, then assert.

Look at the return behavior options: `returnValue` for always returning the same thing. `returnValues` for successive calls — useful when you test "first load then reload." `callThrough` for when you want the real implementation to run but also track calls. `callFake` for custom logic. `throwError` for testing error handling. `rejectWith` for rejected Promises.

And the assertion syntax: `toHaveBeenCalled()`, `toHaveBeenCalledTimes(2)`, `toHaveBeenCalledWith('/api/products', { page: 1 })`. These work on both `jasmine.createSpyObj` spies and `spyOn` spies.

**[Slide 13]**

Testing signal-based components — and here's the thing — they test exactly the same way as any other component. There's no special signal testing API. Signals are synchronous, and tests are synchronous, so they naturally fit together.

You can read a signal state directly: `component.count()`. You can write to it directly from the test: `component.count.set(5)`. That's actually cleaner than older Angular testing where you had to work through setters or `markForCheck`. With signals, the test reaches in, sets the value, calls `detectChanges`, and asserts.

The computed signal test at the bottom of the slide: you create a `computed` inline in the test itself. `const doubled = computed(() => component.count() * 2)`. Set the signal, read the computed. No async. No subscribe. It just works.

Service signal tests are similarly clean. Inject the service, call a method, read the signal state. The assertions read like natural language: "expect service.total() to be 50." That's the kind of test that's self-documenting.

---

### [52:00–60:00] Slides 14–16 — Coverage, Best Practices, Summary

**[Slide 14]**

Running tests. `ng test` — this opens Karma in a browser window and runs in watch mode. Every time you save a file, tests rerun. The browser window shows you pass/fail in a nice UI.

`ng test --watch=false` — for CI pipelines. Runs once, exits with a status code that the CI system reads: 0 for all passing, non-zero for failures.

`ng test --code-coverage` — generates a `coverage/` directory with an HTML report. Open `coverage/index.html` in a browser and you see a file tree with percentages.

The four coverage columns: Statements, Branches, Functions, Lines. Branches is the important one — it tracks whether you tested both sides of every if/else. A component that has `if (isLoggedIn) { ... } else { ... }` needs tests for both the logged-in and logged-out states, or your branch coverage shows a gap.

Setting thresholds in `karma.conf.js` means tests fail in CI if coverage drops below your targets. Typical targets are 80% statements, 70% branches. You want the CI pipeline to prevent coverage from quietly degrading over time.

**[Slide 15]**

A few best practices to internalize.

File structure: co-locate tests with source. `counter.component.spec.ts` lives right next to `counter.component.ts`. When you open the component to make a change, the test file is right there as a reminder. Don't put tests in a separate `tests/` folder — that creates friction.

`beforeEach` for fresh instances. We've said this, but it bears repeating because it's the number one source of intermittent test failures when people are learning.

`afterEach(() => httpMock.verify())` whenever you're testing HTTP. Unchecked requests that were never flushed will quietly pass unless you verify.

Test behavior, not implementation. The test should care that "when I click increment, the count increases by one." It should not care which internal signal the component uses, what the method is called internally, or whether there's a `private` field or not. If you test internals, every refactor breaks tests even when behavior is unchanged.

`fixture.detectChanges()` after every state change. If you ever have a test that's setting state correctly but your DOM assertions are failing, the missing `detectChanges()` is almost certainly the cause.

Keep test descriptions readable: `it('displays an error message when login fails')` not `it('should work')`. Readable test names become the documentation for your component's expected behavior.

**[Slide 16]**

The tool reference table — keep this. `TestBed` from `@angular/core/testing`. `ComponentFixture<T>` from `@angular/core/testing`. `HttpClientTestingModule` and `HttpTestingController` from `@angular/common/http/testing`. Jasmine globals — `jasmine.createSpyObj`, `spyOn` — don't need an import. `By.css` and `By.directive` from `@angular/platform-browser`.

Let me put Week 4 in perspective. Day 16b you met Angular — components, modules, directives, the basic structure. Day 17b — services and dependency injection, the engine that powers the whole framework. Day 18b — routing and reactive forms. Day 19b — HttpClient and RxJS, getting data from an API. Today, Day 20b — signals for reactive state, testing for confidence.

That's the full Angular track. Starting Monday we're in Week 5: SQL fundamentals, REST API tooling, Maven and Gradle, and Spring Boot. We're moving to the backend. All the JavaScript and TypeScript you've learned stays relevant — you'll be building the APIs that your React and Angular apps talk to.

Nice work today. Take questions now or ask me at break.
