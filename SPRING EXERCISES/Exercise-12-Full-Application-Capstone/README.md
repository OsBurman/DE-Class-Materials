# Exercise 12 — Full Application Capstone: Social Media API

## 🎯 Learning Objectives
This capstone exercise combines **all concepts from Exercises 01–11** into a single, production-like application. By completing it, you will demonstrate mastery of:

| Concept | From Exercise |
|---------|---------------|
| REST Controllers, CRUD | 01 |
| DTOs & Request Mapping | 02 |
| Service Layer & DI | 03 |
| Spring Data JPA | 04 |
| JPA Relationships | 05 |
| Exception Handling | 06 |
| Bean Validation | 07 |
| Spring Security | 08 |
| JWT Authentication | 09 |
| Testing | 10 |
| Spring AOP | 11 |

---

## 📋 What You're Building
A **Social Media API** — users create profiles, write posts, follow each other, and like posts.

### Entities & Relationships
```
User ──── Post (OneToMany)
User ──── User (ManyToMany, self-referential: followers/following)
Post ──── Like (OneToMany)
Post ──── Comment (OneToMany)
Post ──── Tag (ManyToMany)
```

### Endpoints
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/auth/register` | Public | Register |
| `POST` | `/api/auth/login` | Public | Login → JWT |
| `GET` | `/api/users/{id}` | JWT | Get profile |
| `PUT` | `/api/users/{id}` | JWT | Update own profile |
| `POST` | `/api/users/{id}/follow` | JWT | Follow a user |
| `DELETE` | `/api/users/{id}/follow` | JWT | Unfollow |
| `GET` | `/api/users/{id}/followers` | JWT | List followers |
| `GET` | `/api/users/{id}/following` | JWT | List following |
| `GET` | `/api/feed` | JWT | Get posts from followed users |
| `POST` | `/api/posts` | JWT | Create post |
| `GET` | `/api/posts/{id}` | JWT | Get post |
| `PUT` | `/api/posts/{id}` | JWT | Update own post |
| `DELETE` | `/api/posts/{id}` | JWT | Delete own post |
| `GET` | `/api/posts` | JWT | List all posts (paginated) |
| `POST` | `/api/posts/{id}/like` | JWT | Like a post |
| `DELETE` | `/api/posts/{id}/like` | JWT | Unlike a post |
| `POST` | `/api/posts/{id}/comments` | JWT | Comment on a post |
| `GET` | `/api/posts/{id}/comments` | JWT | List post comments |
| `GET` | `/api/admin/stats` | ADMIN | Platform statistics |

---

## 🏗️ Project Setup
```bash
cd Exercise-12-Full-Application-Capstone/starter-code
./mvnw spring-boot:run
```

---

## 📁 File Structure
```
src/main/java/com/exercise/socialmedia/
├── SocialMediaApplication.java
├── annotation/
│   └── Audited.java
├── aspect/
│   ├── LoggingAspect.java
│   └── PerformanceAspect.java
├── config/
│   ├── SecurityConfig.java             ← JWT security
│   └── JwtProperties.java
├── jwt/
│   ├── JwtUtil.java
│   └── JwtAuthenticationFilter.java
├── entity/
│   ├── User.java                       ← Self-referential ManyToMany (followers)
│   ├── Post.java                       ← ManyToOne (author), ManyToMany (tags)
│   ├── Comment.java                    ← ManyToOne (post, author)
│   ├── Like.java                       ← ManyToOne (post, user)
│   └── Tag.java                        ← ManyToMany (posts)
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── UserProfileResponse.java
│   ├── PostRequest.java
│   ├── PostResponse.java
│   └── CommentRequest.java
│   └── CommentResponse.java
├── exception/
│   ├── ErrorResponse.java
│   ├── ValidationErrorResponse.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   ├── UnauthorizedException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   ├── CommentRepository.java
│   ├── LikeRepository.java
│   └── TagRepository.java
├── service/
│   ├── UserDetailsServiceImpl.java
│   ├── AuthService.java
│   ├── UserService.java
│   ├── PostService.java
│   └── CommentService.java
└── controller/
    ├── AuthController.java
    ├── UserController.java
    ├── PostController.java
    ├── CommentController.java
    └── AdminController.java
```

---

## ✅ TODOs

This capstone has fewer hand-holding TODOs — you are expected to apply what you've learned. Key gaps left for you to fill:

### Security & JWT
- [ ] **TODO 1**: Complete `JwtUtil.java` — implement `generateToken()`, `extractUsername()`, `isTokenValid()`
- [ ] **TODO 2**: Complete `JwtAuthenticationFilter.java` — extend `OncePerRequestFilter`, implement `doFilterInternal()`
- [ ] **TODO 3**: Complete `SecurityConfig.java` — configure `SecurityFilterChain`, add JWT filter, expose `AuthenticationManager`

### Service Layer
- [ ] **TODO 4**: In `PostService.java`, implement `getFeed(String username)`:
  - Get the current user's following list
  - Return all posts from those users, ordered by creation date (newest first)
- [ ] **TODO 5**: In `UserService.java`, implement `follow(Long targetId, String currentUsername)`:
  - Add the target user to the current user's following set
  - Prevent self-following
- [ ] **TODO 6**: In `UserService.java`, implement `unfollow(Long targetId, String currentUsername)`

### AOP
- [ ] **TODO 7**: Add `@Audited(action = "CREATE_POST")` on `PostService.createPost()`
- [ ] **TODO 8**: Add `@Audited(action = "FOLLOW_USER")` on `UserService.follow()`
- [ ] **TODO 9**: Complete `LoggingAspect` with `@Before` and `@AfterThrowing` for all service methods

### Validation
- [ ] **TODO 10**: Add appropriate validation annotations to `PostRequest` and `RegisterRequest`

### Testing
- [ ] **TODO 11**: Write `PostServiceTest.java` — unit test with Mockito:
  - Test `createPost()` happy path
  - Test `getPostById()` throws when not found
- [ ] **TODO 12**: Write `PostControllerTest.java` — `@WebMvcTest` with MockMvc:
  - Test `GET /api/posts` returns 200
  - Test `POST /api/posts` with invalid body returns 400

---

## 💡 Implementation Tips

### Self-Referential ManyToMany (Followers)
```java
@ManyToMany
@JoinTable(
    name = "user_followers",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "follower_id")
)
@JsonIgnore
private Set<User> followers = new HashSet<>();

@ManyToMany(mappedBy = "followers")
@JsonIgnore
private Set<User> following = new HashSet<>();
```

### Getting Current User in Controller
```java
@PostMapping("/posts")
public ResponseEntity<PostResponse> createPost(
        @Valid @RequestBody PostRequest request,
        Principal principal) {
    return ResponseEntity.status(201)
            .body(postService.createPost(request, principal.getName()));
}
```

### Checking Ownership
```java
if (!post.getAuthor().getUsername().equals(currentUsername)) {
    throw new UnauthorizedException("You can only modify your own posts");
}
```
