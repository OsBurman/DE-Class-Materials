# HTTP Status Code Reference — Day 23: REST & API Tools

## Your Task
Fill in the **Status Code** column for each scenario. Some rows have hints.

---

## Part 1 — Fill in the Status Codes

| # | Scenario | Status Code | Category |
|---|----------|-------------|----------|
| 1 | GET /users/42 — user exists, data returned | | |
| 2 | POST /users — user created successfully | | |
| 3 | DELETE /users/10 — deleted, no body returned | | |
| 4 | GET /users/999 — user does not exist | | |
| 5 | POST /users — request body missing required `email` field | | |
| 6 | GET /admin/dashboard — user is not logged in | | |
| 7 | DELETE /users/5 — user is logged in but not an admin | | |
| 8 | GET /legacy-api/v1/users — endpoint moved to /api/v2/users permanently | | |
| 9 | Server throws a NullPointerException | | |
| 10 | PUT /users/7 — all fields valid, record updated | | |
| 11 | POST /users — email already registered (conflict) | | |
| 12 | GET /api/search — too many requests (rate limited) | | |
| 13 | POST /users — JSON body malformed (invalid syntax) | | |
| 14 | Service is down for maintenance | | |
| 15 | GET /products?page=-1 — validation passes but no results exist | | |
| 16 | OPTIONS /api/posts — preflight CORS check | | |
| 17 | PATCH /users/3 — partial update accepted | | |
| 18 | POST /auth/login — invalid credentials | | |
| 19 | GET /users — returns empty array (no users exist) | | |
| 20 | PUT /users/4 — request body is semantically wrong (fails business rule) | | |

---

## Part 2 — Status Code Groups

Fill in the description for each group and give 2 examples from above:

| Range | Meaning | Examples from table |
|-------|---------|---------------------|
| 1xx | | |
| 2xx | | |
| 3xx | | |
| 4xx | | |
| 5xx | | |

---

## Part 3 — Quick Reference (Complete for Study)

| Code | Name | When to Use |
|------|------|-------------|
| 200 | OK | Standard success for GET, PUT, PATCH |
| 201 | Created | |
| 204 | No Content | |
| 301 | | |
| 302 | | |
| 400 | Bad Request | |
| 401 | | |
| 403 | | |
| 404 | Not Found | |
| 405 | | |
| 409 | | |
| 422 | | |
| 429 | | |
| 500 | | |
| 502 | | |
| 503 | | |

---

## Part 4 — Design Decisions

Answer these questions in 1–2 sentences each:

**Q1:** Should a failed login return 401 or 403? Why?
> TODO: Your answer here

**Q2:** Should a search that returns zero results return 200 or 404? Why?
> TODO: Your answer here

**Q3:** When should you use 422 Unprocessable Entity instead of 400 Bad Request?
> TODO: Your answer here

**Q4:** What is the difference between 401 and 403?
> TODO: Your answer here
