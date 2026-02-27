# Exercise 05 — JPA Relationships

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Map a **One-to-Many** relationship (`@OneToMany` / `@ManyToOne`)
- Map a **Many-to-Many** relationship (`@ManyToMany` with a join table)
- Use `cascade` and `fetch` type options correctly
- Prevent **infinite JSON recursion** with `@JsonManagedReference` / `@JsonBackReference`
- Write derived and `@Query` repository methods involving joins
- Use **pagination** with `Pageable` and `Page<T>`

---

## 📋 What You're Building
A **Blog Post API** with three related entities:
- `Author` — 1 author has many `Post`s (`@OneToMany`)
- `Post` — many posts belong to 1 author (`@ManyToOne`), and many posts have many `Tag`s (`@ManyToMany`)
- `Tag` — many tags can be on many posts (`@ManyToMany`)

### Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/authors` | Get all authors |
| `POST` | `/api/authors` | Create an author |
| `GET` | `/api/authors/{id}/posts` | Get all posts by an author |
| `GET` | `/api/posts` | Get all posts (paginated: `?page=0&size=5`) |
| `GET` | `/api/posts/{id}` | Get a post with its author and tags |
| `POST` | `/api/posts` | Create a post (requires `authorId`) |
| `POST` | `/api/posts/{id}/tags` | Add a tag to a post |
| `GET` | `/api/tags` | Get all tags |
| `GET` | `/api/tags/{id}/posts` | Get all posts with a given tag |

---

## 🏗️ Project Setup
```bash
cd Exercise-05-JPA-Relationships/starter-code
./mvnw spring-boot:run
# H2 Console: http://localhost:8080/h2-console
```

---

## 📁 File Structure
```
src/main/java/com/exercise/blog/
├── BlogApplication.java
├── entity/
│   ├── Author.java      ← ⭐ @OneToMany → posts
│   ├── Post.java        ← ⭐ @ManyToOne → author, @ManyToMany ↔ tags
│   └── Tag.java         ← ⭐ @ManyToMany ↔ posts
├── repository/
│   ├── AuthorRepository.java
│   ├── PostRepository.java   ← ⭐ pagination + custom queries
│   └── TagRepository.java
├── service/
│   └── BlogService.java      ← ⭐ business logic
└── controller/
    └── BlogController.java   ← ⭐ REST endpoints
```

---

## ✅ TODOs

### `entity/Author.java`
- [ ] **TODO 1**: Add `@Entity`, `@Table(name="authors")`, `@Id`, `@GeneratedValue`
- [ ] **TODO 2**: Add `@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` on the `posts` list
- [ ] **TODO 3**: Add `@JsonManagedReference` on the `posts` list (prevents infinite recursion in JSON)

### `entity/Post.java`
- [ ] **TODO 4**: Add `@Entity`, `@Table(name="posts")`, `@Id`, `@GeneratedValue`
- [ ] **TODO 5**: Add `@ManyToOne(fetch = FetchType.LAZY)` and `@JoinColumn(name = "author_id")` on the `author` field
- [ ] **TODO 6**: Add `@JsonBackReference` on the `author` field
- [ ] **TODO 7**: Add `@ManyToMany` on the `tags` set, with `@JoinTable(name="post_tags", joinColumns=@JoinColumn(name="post_id"), inverseJoinColumns=@JoinColumn(name="tag_id"))`
- [ ] **TODO 8**: Add `@CreationTimestamp` on `createdAt`

### `entity/Tag.java`
- [ ] **TODO 9**: Add `@Entity`, `@Table(name="tags")`, `@Id`, `@GeneratedValue`
- [ ] **TODO 10**: Add `@ManyToMany(mappedBy = "tags")` on the `posts` set
- [ ] **TODO 11**: Add `@JsonIgnore` on the `posts` set (avoid recursion in JSON)

### `repository/PostRepository.java`
- [ ] **TODO 12**: Extend `JpaRepository<Post, Long>`
- [ ] **TODO 13**: Add `Page<Post> findByAuthorId(Long authorId, Pageable pageable)`
- [ ] **TODO 14**: Add `List<Post> findByTagsId(Long tagId)` (find posts that have a given tag)
- [ ] **TODO 15**: Add `@Query` to find posts containing a keyword in title OR content

### `service/BlogService.java` and `controller/BlogController.java`
- [ ] **TODO 16**: Implement all service methods
- [ ] **TODO 17**: Implement all controller endpoints

---

## 💡 Key Concepts

### One-to-Many / Many-to-One
```
Author ←────────────── Post
  id                    id
  name                  title
  posts (List)    ←──  author (FK: author_id)
```
```java
// Author.java
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
@JsonManagedReference  // serializes posts WITH author, breaks cycle
private List<Post> posts = new ArrayList<>();

// Post.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "author_id")
@JsonBackReference   // this side is NOT serialized (avoids Author→Post→Author loop)
private Author author;
```

### Many-to-Many
```
Post ─────────────── post_tags ─────────────── Tag
 id │                post_id                    id │
    │                tag_id                        │
    └──────────────────────────────────────────────┘
```
```java
// Post.java (owning side)
@ManyToMany
@JoinTable(name = "post_tags",
    joinColumns = @JoinColumn(name = "post_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id"))
private Set<Tag> tags = new HashSet<>();

// Tag.java (inverse side)
@ManyToMany(mappedBy = "tags")
@JsonIgnore   // prevent infinite loop
private Set<Post> posts = new HashSet<>();
```

### Pagination
```java
// Controller
@GetMapping
public Page<Post> getPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {
    return postRepository.findAll(PageRequest.of(page, size));
}
```
