[SLIDE 1 — Title Slide]
Title: "Angular HTTP & RxJS: Connecting Your App to the World"
Subtitle: HTTP requests, RxJS Observables, Operators, and Best Practices
Include a simple diagram: Component → HttpClient → API → Response → Component

SCRIPT:
"Good morning everyone. Today is one of the most practical sessions we'll have in this course, because by the end of this hour you'll be able to connect an Angular application to a real backend API, handle responses and errors properly, and do it all in a way that won't cause bugs down the road.
Everything we're covering today builds a complete picture — from making a simple HTTP request, to transforming the response with RxJS operators, to protecting your app from memory leaks. These are things you will use in literally every Angular project you ever build professionally, so pay close attention.
Let's jump in."

---
SECTION 1: HttpClient Setup (5 minutes)
---

[SLIDE 2 — What is HttpClient?]
Title: "Angular's HttpClient"
Bullet points:

Angular's built-in service for making HTTP requests
Lives in @angular/common/http
Must be provided in your application
Returns Observables — not Promises
Include a small code snippet showing provideHttpClient() in app.config.ts

SCRIPT:
"Angular ships with a powerful HTTP client built right in. It's called HttpClient, and it lives in the @angular/common/http package. You don't need to install anything extra — it's part of the framework.
Before you can use it, you need to make it available to your application. In modern Angular — version 17 and later — you do this in your app.config.ts file by calling provideHttpClient() inside the providers array. If you're in an older project using NgModules, you would import HttpClientModule into your AppModule instead.
Once it's provided, you inject it into any component or service using the inject() function or through the constructor. Most of the time in real projects, you'll be calling HttpClient from a service, not directly from a component — and we'll talk about why that's the right pattern as we go.
The most important thing to understand about HttpClient right now is this: every method it exposes — GET, POST, PUT, DELETE — returns an Observable. Not a Promise. This is where RxJS comes in, and we'll get to that very shortly."

---
SECTION 2: Making HTTP Requests — GET, POST, PUT, DELETE (12 minutes)
---

[SLIDE 3 — GET and POST]
Title: "Fetching and Creating Data: GET & POST"
Two-column layout:

Left — GET:
  Purpose: Fetch data from the server
  Code: this.http.get<Product[]>('/api/products')
  Returns: Observable that emits one value (the response body) then completes

Right — POST:
  Purpose: Send data to create a new resource
  Code: this.http.post<Product>('/api/products', newProduct)
  Note: Angular auto-serializes the body to JSON and sets Content-Type for you

SCRIPT:
"Let's look at the two HTTP methods you'll use most often.
GET is how you fetch data. You call this.http.get<T>() and pass it a URL. The generic type parameter T tells TypeScript what shape of data to expect back. So if you're fetching a list of products, you'd write this.http.get<Product[]>('/api/products'). This returns an Observable that will emit exactly one value — the response body — and then complete.
POST is how you create new data. You call this.http.post<T>() and pass it the URL plus a body object containing the data you want to send. Angular automatically serializes that object to JSON and sets the Content-Type header for you — you don't have to do that manually.
Here's something critical to understand about both of these: calling http.get() or http.post() does NOT immediately send the request. The Observable is lazy. The HTTP request only goes out when something subscribes to it. This is a really common source of confusion for beginners, so let me say it again — just creating the Observable does nothing. You have to subscribe."

[SLIDE 4 — PUT and DELETE]
Title: "Updating and Removing Data: PUT & DELETE"
Two-column layout:

Left — PUT:
  Purpose: Fully replace an existing resource
  Code: this.http.put<Product>('/api/products/1', updatedProduct)
  Note: The URL includes the resource ID. The full updated object goes in the body.

Right — DELETE:
  Purpose: Remove a resource
  Code: this.http.delete('/api/products/1')
  Note: The URL includes the resource ID. No body needed.

SCRIPT:
"The other two core methods are PUT and DELETE.
PUT is used when you want to fully replace an existing resource. You pass the URL — including the resource ID — and the full updated object as the body. The server replaces the existing record entirely with what you send.
DELETE is the simplest one. You just pass the URL with the resource ID and Angular sends the DELETE request. No body required.
You'll use GET and POST the most, but PUT and DELETE show up in any app that lets users edit or remove data — which is most apps."

[SLIDE 5 — Code: A ProductService Example]
Title: "Putting It Together: A Real Service"
Show a complete, clean service class with:

getProducts(), getProductById(id), createProduct(product), updateProduct(id, product), deleteProduct(id)
Each method returning the correct Observable type
HttpClient injected via inject()

SCRIPT:
"Here's what a real service looks like when you combine all four methods. Notice a few things. The service is decorated with @Injectable({ providedIn: 'root' }) which means Angular creates one shared instance of it across your whole app. HttpClient is injected at the top. And each method is clean, focused, and returns an Observable.
This pattern — putting your HTTP calls inside a service — is important because it keeps your components clean. Your components should be focused on the UI. Data fetching is the service's responsibility.
Also notice that none of these methods have .subscribe() in them. The service hands the Observable back to whoever is calling it, and that caller decides when to subscribe. This is the correct pattern."

---
SECTION 3: HttpParams and HttpHeaders (5 minutes)
---

[SLIDE 6 — HttpParams]
Title: "Adding Query Parameters: HttpParams"
Show a code block building a query string with HttpParams:
  const params = new HttpParams()
    .set('page', '1')
    .set('limit', '20');
  this.http.get<Product[]>('/api/products', { params });

Key points:
HttpParams builds query strings: /api/products?page=1&limit=20
Immutable — .set() returns a new instance, it does NOT modify the original
Chain your calls or reassign to a variable each time

SCRIPT:
"Sometimes a plain URL isn't enough — you need to send query parameters with your request. Angular gives you the HttpParams class for this.
You create a new instance and use .set() to add each parameter. One critical thing: HttpParams is immutable. When you call .set(), it doesn't change the existing object — it returns a brand new one. So you need to either chain your calls as you see here, or reassign the variable each time. If you forget this and just call .set() without capturing the result, your parameters silently disappear.
Once your params object is built, you pass it in the options object on your HTTP call. Angular takes care of appending them to the URL correctly."

[SLIDE 7 — HttpHeaders]
Title: "Setting Request Headers: HttpHeaders"
Show a code block setting headers:
  const headers = new HttpHeaders()
    .set('Authorization', 'Bearer ' + token)
    .set('Content-Type', 'application/json');
  this.http.get<Product[]>('/api/products', { headers });

Key points:
HttpHeaders sets custom headers on a request
Also immutable — same rules as HttpParams
Common uses: Authorization tokens, custom Content-Type
In real apps, use interceptors instead of setting headers manually every time

SCRIPT:
"For request headers, you use HttpHeaders — and it works exactly the same way as HttpParams. Create an instance, call .set() to add each header, and pass it in the options object. Also immutable, same rules apply.
The most common use is setting an Authorization header with a Bearer token for authenticated APIs.
That said, in real applications you almost never set headers manually on every single request. That's repetitive and easy to forget. Instead, you use interceptors — which is exactly what we're covering next, and it's one of the most powerful features in Angular's HTTP system."

---
SECTION 4: Interceptors (8 minutes)
---

[SLIDE 8 — What Are Interceptors?]
Title: "HTTP Interceptors: Cross-Cutting Concerns"
Diagram showing: Request → [Interceptor 1] → [Interceptor 2] → Server → [Interceptor 2] → [Interceptor 1] → Response
Bullet points:

Intercept every outgoing request and incoming response
Write the logic once — it applies to every HTTP call automatically
Perfect for: auth tokens, logging, loading spinners, error handling
Implemented as functions (modern Angular) or classes (legacy)

SCRIPT:
"Interceptors sit in the middle of every single HTTP request and response your application makes. Think of them like middleware if you've ever worked with Express on the backend.
This is incredibly powerful because it means you can do things once, in one place, and have them apply to every HTTP call automatically. The classic use case is authentication — instead of manually adding an Authorization header to every single request, you write one interceptor that adds it automatically to all outgoing requests.
Other common uses are logging requests for debugging, showing and hiding a global loading spinner, and centralized error handling.
In modern Angular you write an interceptor as a plain function. It receives the request object and a next handler. You can modify the request by cloning it — requests are also immutable — and then pass the cloned request to next(). On the response side, you pipe the result of next() and handle the Observable stream coming back."

[SLIDE 9 — Interceptor Code Example]
Title: "Writing an Auth Interceptor"
Show a clean auth interceptor function that:

Clones the request
Sets an Authorization header with a token
Calls next(clonedRequest)
Also show how to register it in app.config.ts using provideHttpClient(withInterceptors([authInterceptor]))

SCRIPT:
"Here's a practical auth interceptor. It grabs the token from wherever you store it — localStorage, a service, wherever — clones the incoming request, sets the Authorization header on the clone, and passes that clone to next(). The original request object is never modified, which is why we clone.
To register it, you pass it inside withInterceptors() in your provideHttpClient() call. You can have multiple interceptors, and they run in the order you list them.
One thing to notice: next(clonedRequest) returns an Observable. This means the interceptor can also intercept the response — which leads us right into error handling."

---
SECTION 5: Error Handling with catchError (6 minutes)
---

[SLIDE 10 — The Error Handling Problem]
Title: "What Happens When HTTP Requests Fail?"
Diagram: HTTP Request → Observable → ⚠️ error → subscription dies → component left in broken state

Key points:
HTTP requests fail — servers go down, networks drop, users send bad data
If unhandled, the Observable errors out and the subscription dies
Angular wraps all HTTP errors in an HttpErrorResponse object, which contains:
  - status: the HTTP status code (404, 500, etc.)
  - statusText: human-readable status
  - url: the URL that was called
  - error: the error body from the server, if any

SCRIPT:
"HTTP requests fail. The server goes down, the network drops, the user sends bad data and gets a 400 back. This is not an edge case — this is normal. Your app needs to handle all of it gracefully.
If you don't handle errors, here's what happens: the Observable errors out, the subscription dies, and your component can be left in a broken state — showing a spinner forever, displaying nothing, or worse.
Angular wraps all HTTP errors in an HttpErrorResponse object. This is what you'll work with whenever something goes wrong. It gives you the HTTP status code, the status text, the URL that was called, and the error body from the server if there is one. The status code is usually the most useful thing — it tells you whether you're dealing with a 404 not found, a 401 unauthorized, a 500 server error, and so on."

[SLIDE 11 — Handling Errors with catchError]
Title: "catchError: Intercepting the Error Stream"
Show the flow: HTTP Request → Observable → error → catchError → fallback Observable OR rethrow

Key points:
catchError is an RxJS operator — used inside pipe()
Receives the error and gives you two options:
  Option 1: Return a fallback Observable (e.g. EMPTY, of([])) — the component continues safely
  Option 2: Rethrow with throwError() — the subscriber's error callback handles it
Rule of thumb: use an interceptor for global errors (401 redirect), use the service for request-specific errors

SCRIPT:
"The RxJS operator for catching errors is catchError. You use it inside a pipe() call on your Observable. When an error occurs upstream, catchError steps in and gives you two choices.
Option one: return a fallback Observable. EMPTY is an Observable that immediately completes without emitting any value — useful when you just want the component to move on safely, like showing an empty list instead of crashing. You can also return of([]) if your component expects an array.
Option two: rethrow the error using throwError(). This turns it back into an Observable error that the subscriber's error callback will catch. Use this when the component actually needs to know something went wrong — to show an error message, redirect the user, or take some other action.
A clean pattern in real apps: use an interceptor for global error concerns — like automatically redirecting to the login page on a 401 — and use catchError in the service for errors specific to that request."

[SLIDE 12 — catchError Code Example]
Title: "catchError in Practice"
Show a service method with pipe() and catchError
Show the HttpErrorResponse being typed and checked with instanceof
Show both: returning EMPTY as a fallback, and rethrowing with throwError

⚠️ INSTRUCTOR NOTE: After walking through this code, pause before moving on. Ask the room: "When would you use EMPTY versus throwError?" Wait for a few responses. You're looking for: EMPTY when the component should silently continue (e.g. show an empty list), throwError when the component needs to know something went wrong and react to it. Clarify before proceeding.

SCRIPT:
"Here's what this looks like in code. After your HTTP call you chain .pipe() and inside it you add catchError. The callback receives the error, you check if it's an HttpErrorResponse, inspect the status code to distinguish between a 404 and a 500, and then decide how to respond.
Before we move on — quick check-in. When would you reach for EMPTY versus throwError? Think about it from the component's perspective.
[Pause for responses. Guide toward: EMPTY when the UI can safely show nothing and move on; throwError when the component needs to know something failed and take action — show an error message, redirect, log the user out, etc.]
Get comfortable with this pattern. You'll use it constantly."

---
SECTION 6: RxJS Observables and Core Operators (10 minutes)
---

[SLIDE 13 — RxJS Observables Refresher]
Title: "RxJS Observables: The Foundation"
Diagram: Observable → emits values over time → Observer (next, error, complete)
Key points:

Lazy: nothing happens until subscribe
Can emit multiple values over time (unlike Promises)
Complete and error are terminal states
HTTP Observables emit once then complete

SCRIPT:
"Let me ground us in RxJS before we go through the operators, because the mental model matters.
An Observable is a stream. It can emit zero, one, or many values over time. It can complete normally, or it can error. An Observer listens to a stream by subscribing to it, and it provides three callbacks: next for each value, error if something goes wrong, and complete when the stream is done.
What makes Observables different from Promises is that they're lazy and they're cancellable. A Promise starts executing the moment you create it. An Observable does nothing until you subscribe. And you can unsubscribe at any time, which cancels the stream — this is hugely important for preventing memory leaks, which we'll get to.
HTTP Observables are a specific kind — they emit exactly one value (the response) and then complete. But other Observables, like those from user input or a BehaviorSubject, can emit many values over a long period of time."

[SLIDE 14 — pipe() and Operators]
Title: "Transforming Streams with pipe()"
Key points:

pipe() is a method on every Observable — it's how you chain operators together
Operators are passed as arguments and run left to right
The output of one operator feeds into the input of the next
Operators never modify the original stream — they always return a new Observable

Show a simple visual:
  observable.pipe(
    operator1(),   // ← runs first
    operator2(),   // ← receives output of operator1
    operator3()    // ← receives output of operator2
  )

SCRIPT:
"Before we look at specific operators, let's make sure everyone's clear on pipe() itself. pipe() is a method available on every Observable. It's how you chain operators together. You pass in one or more operators as arguments and they run left to right — the output of one feeds into the input of the next.
Operators are functions that sit inside pipe() and transform, filter, or react to the values flowing through the stream. They never modify the original Observable — they always return a new one.
That's the whole mental model. pipe() is the pipeline, operators are the steps. Now let's look at the three you'll use every day."

[SLIDE 15 — The map Operator]
Title: "map: Transforming Values"
Code example:
  this.http.get<User>('/api/user/1').pipe(
    map(user => user.displayName)
  );
  // Observable<User> becomes Observable<string>

Key points:
Works exactly like Array.map — transforms each value into something new
Does not change the number of emissions — one value in, one value out
Use it to: extract a field, reshape an object, convert a type

SCRIPT:
"map is your most-used operator. It works exactly like Array.map — it takes each value coming through the stream and transforms it into something else.
In this example, the HTTP call returns a full User object. But if the component only needs the display name, you use map to extract just that string. The Observable type changes from Observable<User> to Observable<string>.
You'll use map constantly — any time the shape of the API response doesn't match exactly what your component needs, map is how you reshape it."

[SLIDE 16 — The filter Operator]
Title: "filter: Letting Values Through Conditionally"
Code example:
  source$.pipe(
    filter(product => product.inStock === true)
  );
  // Only products where inStock is true pass through

Key points:
Works exactly like Array.filter — only values that pass the condition continue
Values that fail the condition are dropped entirely — they do not emit
Use it to: ignore irrelevant events, guard against null/undefined, conditionally process data

SCRIPT:
"filter works just like Array.filter. It only lets values through the stream if they pass a condition you define. Values that don't pass are simply dropped — they never reach the next operator or the subscriber.
In this example, only products that are in stock make it through. Out-of-stock products are silently ignored.
A very common use in HTTP work is filtering out null or undefined values before they reach your component, which prevents template errors when data hasn't loaded yet."

[SLIDE 17 — The tap Operator]
Title: "tap: Side Effects Without Changing the Stream"
Code example:
  this.http.get<Product[]>('/api/products').pipe(
    tap(products => console.log('Products loaded:', products)),
    map(products => products.filter(p => p.inStock))
  );
  // tap logs the value but passes it through unchanged

Key points:
tap performs a side effect without modifying the value
The value flows through tap completely unchanged
Use it to: log values for debugging, trigger a loading flag, track analytics
Think of it as a transparent window — you can look, but you don't touch

SCRIPT:
"tap is the odd one out — it doesn't transform anything. It lets you perform a side effect without touching the value flowing through the stream at all.
In this example, tap logs the products to the console. The value passes through completely unchanged and reaches the map operator just as it came in.
This is incredibly useful for debugging. You can drop a tap anywhere in your pipe() chain to see exactly what's coming through at that point, without breaking anything. It's also commonly used to toggle a loading spinner — set a loading flag to false when the data arrives, without touching the data itself.
Think of tap as a transparent window. You can look through it, but you don't touch what's on the other side."

[SLIDE 18 — switchMap: Cancelling Stale Requests]
Title: "switchMap: Safe HTTP from Streams"
⚠️ INSTRUCTOR NOTE: Spend extra time here. Walk through the marble diagram slowly before moving to the bullet points.

Marble diagram:
  Source:    ----a--------b--c-------->
  Inner:         ---A--A--|
                          ---B--B--|
                               ---C--C--|
  Output:    -------A--A-----B--B---C--C-->
  Label: "When b arrives, the inner Observable for a is cancelled"

Bullet points:
Takes each emitted value and maps it to a new inner Observable (e.g. an HTTP request)
When a new value arrives, it cancels the previous inner Observable
Prevents race conditions — you only ever get results from the most recent request
Perfect for: search inputs, route param changes

SCRIPT:
"Now we get to an operator that is more advanced but incredibly important — switchMap.
Sometimes you have a stream of values, and for each value you need to make a new HTTP request. A search box is the classic example — every keystroke triggers an API call. The problem is that if the user types quickly, you might have multiple requests in flight at the same time, and there's no guarantee they come back in the right order. This is called a race condition, and it leads to stale or incorrect data showing up in your UI.
switchMap solves this. Take a look at the marble diagram. The source stream emits values a, b, and c. For each one, switchMap starts a new inner Observable — a new HTTP request. But the key behavior: the moment b arrives, switchMap cancels whatever was running for a. It switches to the new one. Same when c arrives — b gets cancelled.
In the context of search: if the user types 'ang', then quickly types 'angular', you don't care about the response for 'ang' anymore. switchMap cancels that request automatically. No stale data, no race conditions.
Any time user input or a route change triggers an HTTP call, switchMap is your default choice."

[SLIDE 19 — mergeMap: Parallel Requests]
Title: "mergeMap: Running Requests Concurrently"
⚠️ INSTRUCTOR NOTE: Keep this slide brief. Reinforce that switchMap is what they'll reach for most — mergeMap is the contrast case.

Marble diagram:
  Source:    ----a----b----------->
  Inner:         ---A--A--|
                      ---B--B--|
  Output:    -------A--A-B--B---->
  Label: "Both inner Observables run at the same time — neither cancels the other"

Bullet points:
Lets all inner Observables run concurrently — nothing gets cancelled
Results merge in arrival order, not source order
Use when: parallel requests, fire-and-forget operations, order doesn't matter
⚠️ Do NOT use for search inputs — use switchMap instead

SCRIPT:
"mergeMap is the contrast to switchMap. Instead of cancelling the previous inner Observable, it lets all of them run at the same time and merges the results together as they arrive.
Look at the marble diagram. Both inner Observables for a and b are alive simultaneously. Their results come through as they complete.
A practical use case: you have an array of IDs and you want to fetch all of them in parallel rather than one at a time. mergeMap lets you do that.
The important distinction to keep with you: switchMap for user input triggering HTTP calls — it cancels stale requests. mergeMap for parallel work where you want everything running at once and order doesn't matter. When in doubt, switchMap is the safe default."

---
SECTION 7: RxJS Subjects (5 minutes)
---

[SLIDE 20 — BehaviorSubject: Shared State]
Title: "Sharing State with BehaviorSubject"
Show a BehaviorSubject used as a simple state store in a service
Key points:

Subjects are both Observable and Observer — they can emit AND be subscribed to
Push values in with .next(), subscribe to it like any Observable
BehaviorSubject requires an initial value and always holds the most recent value
New subscribers immediately receive the current value upon subscribing
Keep it private in the service; expose it as .asObservable()
Expose a method to update it — keeps state changes controlled and predictable

SCRIPT:
"Subjects are a special type in RxJS. Unlike a regular Observable that has one fixed producer of values, a Subject is both an Observable and an Observer — meaning you can push values into it manually with .next(), and you can subscribe to it like any Observable. This makes them perfect for sharing state between different parts of your application.
BehaviorSubject is the one you'll use constantly. It requires an initial value when you create it, and it always holds the most recent value. Any new subscriber immediately gets that current value upon subscribing. This is exactly what you want for application state — if a component subscribes to a currentUser$ BehaviorSubject, it should immediately know who's logged in, not have to wait for the next emission.
In a service, you keep the BehaviorSubject private so nothing outside the service can push values into it directly. You expose it as a plain Observable using .asObservable(), and you expose a dedicated method for updating it. This gives you controlled, predictable state management."

[SLIDE 20b — Advanced: ReplaySubject (Optional)]
Title: "ReplaySubject — For Reference Only"
⚠️ INSTRUCTOR NOTE: This slide is marked optional. Cover it only if time allows or if students ask. It is not needed for the core patterns in this course.

Key points:
ReplaySubject buffers N past values and replays them to any new subscriber
Useful when late subscribers need recent history (e.g. a log feed, a chat window)
Less common in typical application development than BehaviorSubject
You likely won't need this until you're working on more advanced reactive patterns

SCRIPT:
"ReplaySubject works similarly to BehaviorSubject, but instead of holding just the single latest value, it buffers a specified number of past values and replays all of them to any new subscriber.
This is useful in specific scenarios — like a chat feed where a late-joining component needs to catch up on recent messages. But you'll encounter this far less often than BehaviorSubject. I'm mentioning it so you know it exists, but don't feel pressure to internalize it today. BehaviorSubject covers the overwhelming majority of state-sharing use cases you'll encounter."

---
SECTION 8: Unsubscribing and Memory Leaks (5 minutes)
---

[SLIDE 21 — The Memory Leak Problem]
Title: "What Is a Subscription Memory Leak?"
Diagram: Component destroyed → subscription still active → callback fires on dead component → memory leak / errors

Key points:
When you subscribe to an Observable in a component, Angular does NOT clean it up automatically
If the user navigates away, Angular destroys the component — but the subscription lives on
If the Observable emits after the component is gone: errors, unpredictable behavior, memory never freed
HTTP Observables from HttpClient are safe — they complete after one emission
The danger: long-lived Observables — Subjects, interval(), fromEvent(), WebSocket streams

SCRIPT:
"This is one of the most important things I'll tell you today from a real-world perspective. Memory leaks caused by forgotten subscriptions are a genuine problem in Angular apps, and they're easy to accidentally introduce.
Here's what happens: you subscribe to an Observable in a component. The user navigates away. Angular destroys the component. But the subscription is still alive — it's still listening. If the Observable emits a new value, the callback fires and tries to update a component that no longer exists. At best you get a console error. At worst you get unpredictable behavior and memory that never gets freed.
Now — HTTP Observables from HttpClient are actually safe by default, because they complete after emitting one value, and a completed Observable cleans itself up. The danger is with long-lived Observables: Subjects from your services, interval(), fromEvent(), WebSocket streams — anything that keeps emitting after the component is gone."

[SLIDE 22 — Solving the Leak: Three Approaches]
Title: "Preventing Memory Leaks: Your Options"
Three approaches shown side by side:

1. takeUntilDestroyed() — Modern Angular (16+), preferred
   import from @angular/core/rxjs-interop
   Add to pipe() before subscribing
   Automatically unsubscribes when the component is destroyed

2. async pipe — Cleanest for templates
   Subscribe in the template, not in TypeScript
   Angular handles subscribe and unsubscribe automatically
   Covered in depth on the next slide

3. Manual ngOnDestroy — Older code / manual control
   Store the Subscription in a variable
   Call .unsubscribe() in ngOnDestroy()
   Verbose but reliable

SCRIPT:
"There are three ways to solve this, and knowing all three matters because you'll encounter all of them in real codebases.
takeUntilDestroyed() is the modern Angular approach — available from Angular 16 onwards. You import it from @angular/core/rxjs-interop and add it to your pipe(). It automatically unsubscribes the moment the component's destroy lifecycle fires. Clean, minimal, and no boilerplate.
The async pipe is arguably even cleaner — you never subscribe in TypeScript at all. You bind directly to the Observable in the template, and Angular manages the subscription entirely. We'll cover this properly on the next slide.
Manual ngOnDestroy is the old-school approach. You store the Subscription in a class property and call .unsubscribe() yourself when the component is destroyed. It works, but it's more code and easy to forget. You'll see this in older codebases, so it's worth knowing."

---
SECTION 9: The Async Pipe (4 minutes)
---

[SLIDE 23 — The Async Pipe]
Title: "async Pipe: The Cleanest Pattern"
Show template code with async pipe on an Observable
Show the equivalent component code WITHOUT async pipe for comparison
Benefits listed:

Automatically subscribes when component initializes
Automatically unsubscribes when component is destroyed
Triggers change detection properly with OnPush
No manual subscription management needed

SCRIPT:
"The async pipe is arguably the most elegant thing in Angular's HTTP and RxJS toolkit because it removes almost all the manual work.
Here's how it works: instead of subscribing in your component TypeScript, you keep the Observable as a property and bind to it directly in your template using the async pipe. Angular subscribes to it automatically when the component renders, and unsubscribes automatically when the component is destroyed. You never touch a subscription object at all.
Compare the two approaches: without async pipe, you have a subscribe call in ngOnInit, a variable to store the data, an ngOnDestroy to clean up, a Subscription reference — it's a lot of boilerplate. With the async pipe, your component just exposes the Observable and your template handles the rest.
The async pipe also works beautifully with Angular's OnPush change detection strategy, which is worth knowing for performance optimization later in your studies. When the Observable emits a new value, the async pipe triggers change detection automatically.
Use the async pipe as your default pattern wherever possible. It's safer, cleaner, and less code."

---
CLOSING: Putting It All Together (5 minutes)
---

[SLIDE 24 — The Complete Pattern]
Title: "Putting It All Together"
Show a complete diagram connecting all the pieces:
App Config (provideHttpClient + interceptors) → Service (HttpClient, operators, error handling) → Component (async pipe, takeUntilDestroyed) → Template (async pipe)

A short checklist style recap:

✅ HttpClient provided and injected into a service
✅ HTTP methods return typed Observables
✅ pipe() with operators to transform data
✅ catchError to handle failures
✅ Interceptors for auth and global concerns
✅ async pipe or takeUntilDestroyed to prevent memory leaks

SCRIPT:
"Let's zoom out and connect all the dots.
You set up HttpClient once at the application level. You register your interceptors there too — they run globally without you having to think about them on each request. Your services contain all the HTTP logic. They inject HttpClient, call the right methods, pipe through operators to shape the data, and use catchError to handle failures gracefully. They hand Observables back to components without subscribing themselves.
Your components are thin. They call service methods, get Observables back, and expose those Observables to the template. They use the async pipe to subscribe in the template automatically, or they use takeUntilDestroyed() if they need to subscribe in TypeScript.
Subjects, particularly BehaviorSubject, live in services and give you a clean way to share state reactively across your application.
This architecture — services doing the work, components staying thin, templates using async pipe — is the Angular way. It scales well, it's testable, and it prevents an entire class of bugs.
Everything we covered today is production-level knowledge. This isn't just academic — this is how real Angular applications at real companies are built. Practice building a small feature that uses a GET and POST with error handling and an interceptor, and the whole picture will click."

[SLIDE 25 — Key Takeaways]
Title: "What You Should Now Know"
Clean summary list:

How to set up and use HttpClient for all four HTTP operations
How to customize requests with HttpParams and HttpHeaders
How to write interceptors for auth tokens and global concerns
How to handle errors gracefully with catchError and HttpErrorResponse
How pipe() works and how to chain operators
Core RxJS operators: map, filter, tap, switchMap, mergeMap
BehaviorSubject for shared state (ReplaySubject covered as optional)
How to prevent memory leaks with async pipe and takeUntilDestroyed

SCRIPT:
"Before I open it up for questions — here's everything you should feel confident about from today. If any of these items feel fuzzy, that's your study target for tonight.
Take a look at these. If you feel good about all of them, you're in great shape. If something isn't clicking, please raise your hand now or come find me after class.
Any questions?"

[SLIDE 26 — Q&A + Practice Suggestions]
Title: "Practice: Build It Yourself"
Suggested mini-projects:

Build a service that fetches from a public API (JSONPlaceholder — jsonplaceholder.typicode.com — is free and perfect for this)
Add an interceptor that logs every request URL to the console
Add catchError to return an empty array on failure
Build a search input that uses switchMap to call an API on each keystroke
Display results using async pipe in the template
Resources listed on slide for students to reference later:
  - Angular Docs: angular.dev
  - RxJS Docs: rxjs.dev
  - RxMarbles (interactive operator diagrams): rxmarbles.com

SCRIPT:
"Here are some practice suggestions. JSONPlaceholder is a free fake REST API — perfect for practicing all of this without needing a backend. Build a simple app that hits it, and work through each of the concepts we covered today.
For visualizing how RxJS operators work — especially switchMap and mergeMap — the marble diagrams we walked through in class are your reference. The rxmarbles.com site has interactive versions of these if you want to experiment on your own time.
Official docs are at angular.dev and rxjs.dev. Both are excellent.
See you next class."