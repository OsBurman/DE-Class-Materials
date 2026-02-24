# Exercise 05 — Postman Testing and Response Assertions

## Learning Objectives
By the end of this exercise you will be able to:
- Write `pm.test()` assertions in Postman's **Tests** tab
- Assert on status code, response body fields, data types, and response time
- Use the **Collection Runner** to execute all tests in sequence
- Interpret pass/fail results and debug failing assertions

## Prerequisites
- Exercise 04 completed (familiarity with the Postman interface)
- `JSONPlaceholder` environment active (`baseUrl = https://jsonplaceholder.typicode.com`)

---

## Background: Postman Test Scripting

Postman uses **JavaScript** in its **Tests** tab (executed after a response is received).

```javascript
// Basic structure
pm.test("Descriptive name", function () {
  pm.expect(value).to.equal(expected);
});

// Common assertions
pm.expect(pm.response.code).to.equal(200);                        // status code
pm.expect(pm.response.json()).to.have.property('id');              // body has field
pm.expect(pm.response.json().id).to.be.a('number');               // field type
pm.expect(pm.response.json().title).to.be.a('string').and.not.empty;
pm.expect(pm.response.responseTime).to.be.below(2000);            // response time (ms)
pm.expect(pm.response.headers.get('Content-Type')).to.include('application/json');
```

---

## Setup

1. Click **Import** → select `starter-code/postman-collection.json` from this exercise folder.
2. Ensure the `JSONPlaceholder` environment is active.

---

## Tasks

### Part 1 — Add Tests to Each Request

Open each request in the **Library API Tests** collection and write the test scripts described below in the **Tests** tab.

---

**Request 1 — Get All Posts**

Write tests that assert:
1. Status code is `200`
2. Response body is an array
3. Array has more than 0 items
4. The first item has an `id` property
5. Response time is under `2000 ms`

```javascript
// TODO 1: status code is 200

// TODO 2: response body is an array

// TODO 3: array has more than 0 items

// TODO 4: first item has an 'id' property

// TODO 5: response time is under 2000 ms
```

---

**Request 2 — Get Single Post**

Write tests that assert:
1. Status code is `200`
2. `id` equals `1`
3. `userId` is a number
4. `title` is a non-empty string
5. `body` property exists

```javascript
// TODO 1: status code is 200

// TODO 2: id equals 1

// TODO 3: userId is a number

// TODO 4: title is a non-empty string

// TODO 5: body property exists
```

---

**Request 3 — Create a Post**

Write tests that assert:
1. Status code is `201`
2. Response has an `id` property
3. `id` is a number
4. `title` in the response matches the title you sent

```javascript
// TODO 1: status code is 201

// TODO 2: response has an 'id' property

// TODO 3: id is a number

// TODO 4: title matches what was sent (hint: check the value you used in the body)
```

---

**Request 4 — Full Update a Post (PUT)**

Write tests that assert:
1. Status code is `200`
2. `id` equals `1`
3. `title` equals `"Replaced Title"`

```javascript
// TODO 1: status code is 200

// TODO 2: id equals 1

// TODO 3: title equals "Replaced Title"
```

---

**Request 5 — Delete a Post**

Write tests that assert:
1. Status code is `200`
2. Response body is an empty object (`{}`)

```javascript
// TODO 1: status code is 200

// TODO 2: response body is an empty object
```

---

### Part 2 — Save an Environment Variable from a Response

In **Request 3 — Create a Post**, after the `pm.test()` blocks, add code to save the returned `id` into an environment variable called `newPostId`:

```javascript
// TODO: save the id from the response into pm.environment as 'newPostId'
```

Then, update **Request 2 — Get Single Post** URL to use `{{newPostId}}` instead of the hard-coded `1`.

---

### Part 3 — Run the Collection

1. Click the **Library API Tests** collection → **Run collection**.
2. Ensure all requests run in order.
3. Review pass/fail results.
4. Fix any failing tests until all pass.

---

## Reflection Questions

Answer in the collection description or a text document:

1. Why is it better to write `pm.expect(json.id).to.be.a('number')` rather than just checking `pm.expect(json.id).to.exist`?
2. What happens when a `pm.test()` block throws an uncaught error — does it count as a failure or an error?
3. How would you test a `DELETE` endpoint that returns `204 No Content` (no body)?

---

## Deliverable
All tests should pass in the Collection Runner. The reference solution is in `solution/postman-collection.json`.
