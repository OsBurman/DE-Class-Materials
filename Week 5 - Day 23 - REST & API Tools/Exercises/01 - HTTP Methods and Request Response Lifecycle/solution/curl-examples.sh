#!/usr/bin/env bash
# Exercise 01 — Solution: curl HTTP Methods
# All commands use JSONPlaceholder (https://jsonplaceholder.typicode.com)
# which is a free fake REST API for testing.

# 1. GET — retrieve a single post
# -s suppresses the progress meter; output is formatted JSON
curl -s https://jsonplaceholder.typicode.com/posts/1
# Response: {"userId":1,"id":1,"title":"sunt aut facere...","body":"..."}

echo "---"

# 2. POST — create a new resource
# -X POST    : sets the HTTP method
# -H         : adds a request header
# -d         : sets the request body (use single quotes around JSON on bash)
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"title":"foo","body":"bar","userId":1}' \
  https://jsonplaceholder.typicode.com/posts
# Response: {"title":"foo","body":"bar","userId":1,"id":101}

echo "---"

# 3. PUT — fully replace resource (all fields must be included)
curl -s -X PUT \
  -H "Content-Type: application/json" \
  -d '{"id":1,"title":"updated title","body":"updated body","userId":1}' \
  https://jsonplaceholder.typicode.com/posts/1
# Response: {"id":1,"title":"updated title","body":"updated body","userId":1}

echo "---"

# 4. PATCH — partial update (only send changed fields)
curl -s -X PATCH \
  -H "Content-Type: application/json" \
  -d '{"title":"patched title"}' \
  https://jsonplaceholder.typicode.com/posts/1
# Response: original object with title replaced by "patched title"

echo "---"

# 5. DELETE — remove a resource; server returns empty body {}
curl -s -X DELETE \
  https://jsonplaceholder.typicode.com/posts/1
# Response: {}
