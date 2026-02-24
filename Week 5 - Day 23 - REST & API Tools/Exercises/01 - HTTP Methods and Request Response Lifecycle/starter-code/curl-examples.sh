#!/usr/bin/env bash
# Exercise 01 — curl Practice: HTTP Methods
# Run each command in your terminal and observe the response.
# Replace the TODO comments with the correct curl flags.

# TODO 1: Write a GET request to fetch post 1 from JSONPlaceholder.
#         Expected: JSON with id=1, title, body, userId fields.


# TODO 2: Write a POST request to create a new post.
#         URL: https://jsonplaceholder.typicode.com/posts
#         Body (JSON): {"title":"foo","body":"bar","userId":1}
#         Required header: Content-Type: application/json
#         Expected: the posted object echoed back with a new id (101)


# TODO 3: Write a PUT request to fully replace post 1.
#         URL: https://jsonplaceholder.typicode.com/posts/1
#         Body (JSON): {"id":1,"title":"updated title","body":"updated body","userId":1}
#         Required header: Content-Type: application/json
#         Expected: the full replacement object echoed back


# TODO 4: Write a PATCH request to partially update post 1 (title only).
#         URL: https://jsonplaceholder.typicode.com/posts/1
#         Body (JSON): {"title":"patched title"}
#         Required header: Content-Type: application/json
#         Expected: the merged object echoed back


# TODO 5: Write a DELETE request to delete post 1.
#         URL: https://jsonplaceholder.typicode.com/posts/1
#         Expected: {} (empty JSON body, status 200)
