# Exercise 01 — REST Controllers

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Create a `@RestController` and map it to a URL path
- Use `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- Extract data from a URL with `@PathVariable`
- Extract query parameters with `@RequestParam`
- Accept a JSON request body with `@RequestBody`
- Return proper HTTP status codes using `ResponseEntity`
- Understand Spring Boot's auto-configuration and `@SpringBootApplication`

---

## 📋 What You're Building
A **Product Catalog REST API** — a simple in-memory API (no database yet) that manages a list of products.

### Endpoints to Implement
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/products` | Get all products (optional `?category=` filter) |
| `GET` | `/api/products/{id}` | Get a single product by ID |
| `POST` | `/api/products` | Create a new product |
| `PUT` | `/api/products/{id}` | Update an existing product |
| `DELETE` | `/api/products/{id}` | Delete a product |
| `GET` | `/api/products/search` | Search products by name (`?name=keyword`) |

---

## 🏗️ Project Setup
```bash
cd Exercise-01-REST-Controllers/starter-code
./mvnw spring-boot:run
# API available at http://localhost:8080
```

---

## 📁 File Structure
```
src/main/java/com/exercise/productcatalog/
├── ProductCatalogApplication.java   ← Main class (already done)
├── model/
│   └── Product.java                 ← Data model (already done)
└── controller/
    └── ProductController.java       ← ⭐ YOUR MAIN TASK
```

---

## ✅ TODOs

### `ProductController.java`
- [ ] **TODO 1**: Add `@RestController` and `@RequestMapping("/api/products")` on the class
- [ ] **TODO 2**: Inject (autowire) the `ProductRepository` using constructor injection
- [ ] **TODO 3**: Implement `getAllProducts(@RequestParam Optional<String> category)` — returns all products or filters by category; return `200 OK`
- [ ] **TODO 4**: Implement `getProductById(@PathVariable Long id)` — returns `200 OK` with the product, or `404 Not Found` if missing
- [ ] **TODO 5**: Implement `createProduct(@RequestBody Product product)` — adds the product to the store; return `201 Created` with the saved product
- [ ] **TODO 6**: Implement `updateProduct(@PathVariable Long id, @RequestBody Product updated)` — updates and returns `200 OK`, or `404` if missing
- [ ] **TODO 7**: Implement `deleteProduct(@PathVariable Long id)` — removes and returns `204 No Content`, or `404` if missing
- [ ] **TODO 8**: Implement `searchByName(@RequestParam String name)` — returns all products whose name contains the keyword (case-insensitive)

### `ProductRepository.java`
- [ ] **TODO 9**: Implement the `findByCategory(String category)` method
- [ ] **TODO 10**: Implement the `findByNameContainingIgnoreCase(String name)` method

---

## 💡 Key Concepts Reminder

| Annotation | Purpose | Example |
|-----------|---------|---------|
| `@RestController` | Marks class as REST controller (combines `@Controller` + `@ResponseBody`) | `@RestController` |
| `@RequestMapping` | Base URL for all methods in the class | `@RequestMapping("/api/products")` |
| `@GetMapping` | Handles HTTP GET | `@GetMapping("/{id}")` |
| `@PostMapping` | Handles HTTP POST | `@PostMapping` |
| `@PutMapping` | Handles HTTP PUT | `@PutMapping("/{id}")` |
| `@DeleteMapping` | Handles HTTP DELETE | `@DeleteMapping("/{id}")` |
| `@PathVariable` | Extracts `{id}` from URL | `@PathVariable Long id` |
| `@RequestParam` | Extracts `?key=value` from URL | `@RequestParam String name` |
| `@RequestBody` | Deserializes JSON body into object | `@RequestBody Product p` |
| `ResponseEntity<T>` | Full control over HTTP response | `ResponseEntity.ok(product)` |

### ResponseEntity Quick Reference
```java
ResponseEntity.ok(body)              // 200 OK
ResponseEntity.created(uri).body(x)  // 201 Created
ResponseEntity.noContent().build()   // 204 No Content
ResponseEntity.notFound().build()    // 404 Not Found
ResponseEntity.badRequest().build()  // 400 Bad Request
```

---

## 🧪 Test Your Work
Use the provided `requests.http` file or Postman:

```http
### Get all products
GET http://localhost:8080/api/products

### Get products by category
GET http://localhost:8080/api/products?category=Electronics

### Get one product
GET http://localhost:8080/api/products/1

### Create a product
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Laptop Pro",
  "description": "High-performance laptop",
  "price": 1299.99,
  "category": "Electronics",
  "stock": 50
}

### Update a product
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
  "name": "Laptop Pro X",
  "description": "Updated description",
  "price": 1499.99,
  "category": "Electronics",
  "stock": 30
}

### Delete a product
DELETE http://localhost:8080/api/products/1

### Search by name
GET http://localhost:8080/api/products/search?name=laptop
```
