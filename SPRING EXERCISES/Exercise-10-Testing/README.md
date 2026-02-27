# Exercise 10 — Testing Spring Boot Applications

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Write unit tests for service layers using **Mockito** (`@Mock`, `@InjectMocks`, `when()`, `verify()`)
- Write integration tests for controllers using **MockMvc** and `@WebMvcTest`
- Write repository tests using `@DataJpaTest`
- Write full application tests using `@SpringBootTest`
- Understand the difference between unit tests, slice tests, and integration tests
- Use `@MockBean` to replace real beans with mocks in Spring context tests

---

## 📋 What You're Building
A **Product API** — the application is simple so you can focus entirely on writing tests.

### Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/products` | List all products |
| `POST` | `/api/products` | Create a product |
| `GET` | `/api/products/{id}` | Get product by ID |
| `PUT` | `/api/products/{id}` | Update product |
| `DELETE` | `/api/products/{id}` | Delete product |
| `GET` | `/api/products/search?name=` | Search by name |

---

## 🏗️ Project Setup
```bash
cd Exercise-10-Testing/starter-code
./mvnw test
```

---

## 📁 File Structure
```
src/
├── main/java/com/exercise/productapi/
│   ├── ProductApiApplication.java
│   ├── entity/Product.java
│   ├── dto/
│   │   ├── ProductRequest.java
│   │   └── ProductResponse.java
│   ├── exception/
│   │   ├── ErrorResponse.java
│   │   ├── ResourceNotFoundException.java
│   │   └── GlobalExceptionHandler.java
│   ├── repository/ProductRepository.java
│   ├── service/ProductService.java
│   └── controller/ProductController.java
└── test/java/com/exercise/productapi/
    ├── service/
    │   └── ProductServiceTest.java         ← ⭐ Unit tests (Mockito)
    ├── controller/
    │   └── ProductControllerTest.java      ← ⭐ Slice tests (@WebMvcTest)
    ├── repository/
    │   └── ProductRepositoryTest.java      ← ⭐ Slice tests (@DataJpaTest)
    └── integration/
        └── ProductIntegrationTest.java     ← ⭐ Full integration (@SpringBootTest)
```

---

## ✅ TODOs

### `service/ProductServiceTest.java` (Unit Tests)
- [ ] **TODO 1**: Annotate the class with `@ExtendWith(MockitoExtension.class)`
- [ ] **TODO 2**: Add `@Mock private ProductRepository productRepository`
- [ ] **TODO 3**: Add `@InjectMocks private ProductService productService`
- [ ] **TODO 4**: Write `getAllProducts_returnsAllProducts()`:
  - `when(productRepository.findAll()).thenReturn(List.of(testProduct))`
  - Call service, assert size == 1
- [ ] **TODO 5**: Write `getProductById_whenExists_returnsProduct()`:
  - `when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct))`
  - Assert returned product name equals expected
- [ ] **TODO 6**: Write `getProductById_whenNotFound_throwsException()`:
  - `when(productRepository.findById(99L)).thenReturn(Optional.empty())`
  - `assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L))`
- [ ] **TODO 7**: Write `createProduct_savesAndReturnsProduct()`:
  - `when(productRepository.save(any())).thenReturn(testProduct)`
  - Verify `productRepository.save()` was called once

### `controller/ProductControllerTest.java` (Slice Tests)
- [ ] **TODO 8**: Annotate class with `@WebMvcTest(ProductController.class)`
- [ ] **TODO 9**: Inject `@Autowired MockMvc mockMvc` and `@MockBean ProductService productService`
- [ ] **TODO 10**: Write `getAllProducts_returns200()`:
  - `when(productService.getAllProducts()).thenReturn(List.of(sampleResponse))`
  - `mockMvc.perform(get("/api/products")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))`
- [ ] **TODO 11**: Write `getProductById_whenExists_returns200()`:
  - Mock service, perform GET, expect status 200 and correct JSON fields
- [ ] **TODO 12**: Write `getProductById_whenNotFound_returns404()`:
  - `when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L))`
  - Expect status 404

### `repository/ProductRepositoryTest.java` (Slice Tests)
- [ ] **TODO 13**: Annotate class with `@DataJpaTest` (loads only JPA layer with H2)
- [ ] **TODO 14**: Write `findByNameContainingIgnoreCase_returnsMatchingProducts()`
- [ ] **TODO 15**: Write `findByCategory_returnsCorrectProducts()`

### `integration/ProductIntegrationTest.java` (Integration Tests)
- [ ] **TODO 16**: Annotate class with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `@AutoConfigureMockMvc`
- [ ] **TODO 17**: Write a full create-then-retrieve test:
  - POST to create a product
  - GET to verify it exists in the response

---

## 💡 Key Concepts

### Test Types Comparison
| Type | Annotation | Spring Context | Database | Speed |
|------|-----------|----------------|----------|-------|
| Unit | `@ExtendWith(MockitoExtension.class)` | None | None | ⚡ Fast |
| Controller slice | `@WebMvcTest` | Web layer only | Mocked | 🏃 Medium |
| Repository slice | `@DataJpaTest` | JPA only | H2 (real) | 🏃 Medium |
| Integration | `@SpringBootTest` | Full | H2 (real) | 🐢 Slow |

### Mockito Quick Reference
```java
// Stubbing — what to return
when(mock.method(arg)).thenReturn(value);
when(mock.method(any())).thenThrow(new RuntimeException());

// Verification — was it called?
verify(mock).method(arg);
verify(mock, times(2)).method(any());
verify(mock, never()).method(any());
```

### MockMvc Quick Reference
```java
mockMvc.perform(get("/api/products")
        .contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$[0].name").value("Laptop"))
    .andExpect(jsonPath("$.length()").value(3));

// POST with body:
mockMvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
    .andExpect(status().isCreated());
```
