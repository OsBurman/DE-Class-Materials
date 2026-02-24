# Day 27 Application — Spring Data JPA: Blog Post API

## Overview

Build a **Blog Platform API** using Spring Data JPA with full entity relationships, custom queries, pagination, and transactions.

---

## Learning Goals

- Define JPA entities with `@Entity`, `@Table`, `@Column`
- Model all relationship types: `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- Use `FetchType.LAZY` vs `FetchType.EAGER`
- Extend `JpaRepository` for CRUD
- Write derived query methods
- Use `@Query` with JPQL and native SQL
- Add pagination with `Pageable`
- Use `@Transactional`

---

## Prerequisites

- Java 17+, Maven
- H2 in-memory database (auto-configured)
- `mvn spring-boot:run` → `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console`

---

## Entity Relationships

```
Author  ──< Post  ──< Comment
Post    >──< Tag
```

---

## Part 1 — Entities

**Task 1 — `Author.java`**  
`@Entity`. Fields: `id`, `name`, `email` (unique), `bio`.  
`@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)` for posts.

**Task 2 — `Tag.java`**  
`@Entity`. Fields: `id`, `name` (unique).  
`@ManyToMany(mappedBy = "tags")` back-reference to posts.

**Task 3 — `Post.java`**  
`@Entity`. Fields: `id`, `title`, `content`, `publishedAt`, `status` (enum: DRAFT, PUBLISHED).  
`@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "author_id")` for author.  
`@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)` for comments.  
`@ManyToMany @JoinTable(name = "post_tags", ...)` for tags.

**Task 4 — `Comment.java`**  
`@Entity`. Fields: `id`, `content`, `commenterName`, `createdAt`.  
`@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id")`.

---

## Part 2 — Repositories

**Task 5 — `PostRepository`**  
Extend `JpaRepository<Post, Long>`. Add:
- `List<Post> findByStatus(Post.Status status)`
- `List<Post> findByAuthorId(Long authorId)`
- `Page<Post> findByStatus(Post.Status status, Pageable pageable)`
- `@Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%") List<Post> search(@Param("keyword") String keyword)`
- `@Query(value = "SELECT * FROM post ORDER BY published_at DESC LIMIT :n", nativeQuery = true) List<Post> findTopNRecent(@Param("n") int n)`

**Task 6 — `AuthorRepository`**  
`Optional<Author> findByEmail(String email)`.  
`@Query("SELECT a, COUNT(p) FROM Author a LEFT JOIN a.posts p GROUP BY a") List<Object[]> findAuthorsWithPostCount()`.

---

## Part 3 — Service & Controller

**Task 7 — `PostService`**  
`@Transactional` on write operations.  
Implement: `createPost(PostDto, authorId)`, `getPublishedPosts(int page, int size)`, `searchPosts(keyword)`, `addTagToPost(postId, tagId)`.

**Task 8 — Pagination endpoint**  
`GET /api/posts?page=0&size=5&status=PUBLISHED` — returns `Page<PostResponseDto>` with pagination metadata.

---

## Part 4 — Advanced JPA

**Task 9 — N+1 query problem**  
Write a query that causes the N+1 problem (loading posts then accessing author for each).  
Fix it using `JOIN FETCH` in a `@Query`. Add comments explaining the difference.

**Task 10 — `data.sql`**  
Create `src/main/resources/data.sql` with INSERT statements to seed 3 authors, 8 posts, 5 tags.

---

## Submission Checklist

- [ ] All 4 entities with correct JPA annotations
- [ ] Relationship annotations with correct `fetch` types
- [ ] Custom query methods using both JPQL and native SQL
- [ ] Pagination endpoint returns page metadata
- [ ] `@Transactional` on write operations
- [ ] N+1 problem demonstrated and fixed with JOIN FETCH
- [ ] H2 console shows correct schema
