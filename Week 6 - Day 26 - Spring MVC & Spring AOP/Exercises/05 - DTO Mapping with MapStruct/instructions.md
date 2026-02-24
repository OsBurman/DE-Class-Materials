# Exercise 05 — DTO Mapping with MapStruct

## Learning Objectives
- Understand why a dedicated mapping library reduces boilerplate
- Configure MapStruct in `pom.xml` using `annotationProcessorPaths`
- Define a `@Mapper` interface and use it as a Spring bean
- Map between an entity (`Book`) and a DTO (`BookDto`) automatically

---

## Background

In Exercise 02, you wrote a `toDto()` method by hand. As your models grow, manual mapping becomes error-prone and tedious. **MapStruct** is an annotation processor that generates the mapping code at compile time — no runtime reflection.

### How MapStruct works

1. Define a mapper interface annotated with `@Mapper`
2. Declare method signatures (MapStruct figures out the implementation)
3. MapStruct generates a concrete implementation class at compile time
4. With `componentModel = "spring"`, the generated class is a Spring bean you can `@Autowired`

### MapStruct vs ModelMapper

| | MapStruct | ModelMapper |
|---|---|---|
| Timing | Compile-time code generation | Runtime reflection |
| Performance | Faster (no reflection at runtime) | Slower |
| Safety | Compile-time errors for unmapped fields | Silent failures at runtime |
| Debugging | Read the generated source file | Harder to trace |

---

## Starter Code

| File | Status |
|---|---|
| `pom.xml` | Add MapStruct dependency + annotation processor |
| `LibraryApplication.java` | Complete `@SpringBootApplication` setup |
| `Book.java` | Entity — provided as-is |
| `BookDto.java` | DTO — provided as-is |
| `BookMapper.java` | Define the `@Mapper` interface |
| `BookController.java` | Inject `BookMapper` and use it |

---

## Tasks

### 1. Finish `pom.xml`

Add the MapStruct dependency in `<dependencies>`:
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
```

Add the annotation processor in the `maven-compiler-plugin` configuration:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.5.5.Final</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### 2. Finish `LibraryApplication.java`
Add `@SpringBootApplication` and `SpringApplication.run(...)`.

### 3. Implement `BookMapper.java`
- Annotate the interface with `@Mapper(componentModel = "spring")`
- Declare two methods:
  - `BookDto toDto(Book book)` — maps `Book` → `BookDto`
  - `Book toEntity(BookDto dto)` — maps `BookDto` → `Book`
- **Note:** Since `BookDto` has no `id`, MapStruct will leave the `id` field as `0` when mapping `BookDto → Book`. For a real app you would use `@Mapping(target = "id", ignore = true)`.

### 4. Finish `BookController.java`
- Inject `BookMapper` via constructor
- `GET /api/books` — map all books to `BookDto` using `bookMapper.toDto(book)`
- `POST /api/books` — accept `BookDto`, map to `Book` using `bookMapper.toEntity(dto)`, save, return the DTO

---

## Expected Behaviour

```bash
# GET all books (returns DTOs — no id field exposed)
curl http://localhost:8080/api/books
# → [{"title":"Clean Code","genre":"Programming"}, ...]

# POST — client sends DTO without id
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Refactoring","genre":"Programming"}'
# → 201 {"title":"Refactoring","genre":"Programming"}
```

---

## Reflection Questions

1. Where does the generated mapper implementation live? (Hint: look in `target/generated-sources/`)
2. Why is `componentModel = "spring"` important?
3. What happens if `Book` has a field that `BookDto` does not? How would you tell MapStruct to ignore it?
4. What are the trade-offs of using MapStruct vs writing manual mapping code?
