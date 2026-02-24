# Exercise 04 — Postman Requests and Collections

## Learning Objectives
By the end of this exercise you will be able to:
- Navigate the Postman interface and create HTTP requests
- Organise requests into a Collection with folders
- Use environment variables to avoid hard-coded values
- Send GET, POST, PUT, PATCH, and DELETE requests to a live API
- Inspect request and response details in Postman

## Prerequisites
- Postman desktop installed (https://www.postman.com/downloads/) **or** Postman Web (app.getpostman.com)
- Internet access to reach `https://jsonplaceholder.typicode.com`

---

## Background

Postman is the industry-standard tool for designing, testing, and documenting APIs. Rather than crafting curl commands by hand, Postman provides:
- A GUI for building requests with headers, body, and auth
- **Collections** — saved groups of requests you can share and automate
- **Environments** — sets of variables (e.g., `baseUrl`, `authToken`) that switch between dev/staging/prod without editing every request
- A **Collection Runner** for batch execution and CI integration

---

## Setup

### Step 1 — Create an Environment
1. Click the **Environments** tab (left sidebar) → **+** to add a new environment.
2. Name it `JSONPlaceholder`.
3. Add one variable:
   - **Variable:** `baseUrl`  **Initial value:** `https://jsonplaceholder.typicode.com`  **Current value:** `https://jsonplaceholder.typicode.com`
4. Click **Save**, then select this environment from the top-right environment dropdown.

### Step 2 — Import the Starter Collection
1. Click **Import** (top left).
2. Select `starter-code/postman-collection.json` from this exercise folder.
3. The **Library API Practice** collection will appear in your sidebar.

---

## Tasks

### Part 1 — Sending Requests

Work through each request stub in the imported collection. For each one:
1. Fill in the request URL using `{{baseUrl}}` (the environment variable you created).
2. Add any required headers.
3. Add the request body where indicated.
4. Click **Send** and verify the response.

**Request 1 — Get All Posts**
- Method: `GET`
- URL: `{{baseUrl}}/posts`
- Expected status: `200 OK`
- Note: How many objects are returned? What is the shape of each object?

**Request 2 — Get a Single Post**
- Method: `GET`
- URL: `{{baseUrl}}/posts/1`
- Expected status: `200 OK`
- Note: Record the `userId`, `id`, `title`, and `body` fields.

**Request 3 — Create a Post**
- Method: `POST`
- URL: `{{baseUrl}}/posts`
- Header: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  {
    "title": "My New Post",
    "body": "This is the post body.",
    "userId": 1
  }
  ```
- Expected status: `201 Created`
- Note: What `id` does the server assign?

**Request 4 — Full Update (Replace) a Post**
- Method: `PUT`
- URL: `{{baseUrl}}/posts/1`
- Header: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  {
    "id": 1,
    "title": "Replaced Title",
    "body": "Replaced body content.",
    "userId": 1
  }
  ```
- Expected status: `200 OK`

**Request 5 — Partial Update a Post**
- Method: `PATCH`
- URL: `{{baseUrl}}/posts/1`
- Header: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  {
    "title": "Only the Title Changed"
  }
  ```
- Expected status: `200 OK`
- Note: Which fields are present in the response?

**Request 6 — Delete a Post**
- Method: `DELETE`
- URL: `{{baseUrl}}/posts/1`
- Expected status: `200 OK`
- Note: JSONPlaceholder returns `{}` for DELETE — a real API may return `204 No Content`.

**Request 7 — Get Comments for a Post (nested resource)**
- Method: `GET`
- URL: `{{baseUrl}}/posts/1/comments`
- Expected status: `200 OK`

---

### Part 2 — Organising with Folders

1. Inside the **Library API Practice** collection, create two folders:
   - `Posts`
   - `Comments`
2. Drag Requests 1–6 into the **Posts** folder.
3. Drag Request 7 into the **Comments** folder.

---

### Part 3 — Reflection Questions (in the collection description or a sticky note)

Answer these questions in the collection's **Description** field (click the collection name → Edit):

1. What is the difference between a Postman **Collection** and a Postman **Environment**?
2. Why should you use `{{baseUrl}}` instead of the hard-coded URL string?
3. When would you use `PUT` vs `PATCH`?
4. How does `201 Created` differ from `200 OK` for a POST request?

---

## Deliverable
Save your completed collection and export it from Postman:
- **Collection** → `⋯` → **Export** → **Collection v2.1** → save as `my-solution.json` (for your own records).

The reference solution is provided in `solution/postman-collection.json`.
