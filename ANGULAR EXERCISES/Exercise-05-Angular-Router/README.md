# Exercise 05 — Angular Router

## 🎯 Learning Objectives
- Configure the Angular Router with `provideRouter`
- Use `RouterLink` and `RouterLinkActive` for navigation
- Use `RouterOutlet` to render routed components
- Read **route parameters** with `ActivatedRoute` / `inject`
- Read **query parameters** from the URL
- Create a **route guard** to protect pages

---

## 📋 What You're Building
A **Mini Blog App** with four pages:
- `/` — Home page with a welcome hero
- `/posts` — List of all blog posts (searchable via query param `?search=`)
- `/posts/:id` — Single post detail page
- `/about` — About page
- `**` (wildcard) — 404 Not Found page

---

## 🏗️ Project Setup
```bash
ng new exercise-05-router --standalone --routing=true --style=css
cd exercise-05-router
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## 📁 File Structure
```
src/app/
├── app.component.ts / .html / .css
├── app.routes.ts             ← Route configuration
├── pages/
│   ├── home/home.component.ts
│   ├── post-list/post-list.component.ts
│   ├── post-detail/post-detail.component.ts
│   ├── about/about.component.ts
│   └── not-found/not-found.component.ts
└── services/
    └── posts.service.ts
```

---

## ✅ TODOs

### `app.routes.ts`
- [ ] **TODO 1**: Define a `Routes` array with these routes:
  - `''` → `HomeComponent`
  - `'posts'` → `PostListComponent`
  - `'posts/:id'` → `PostDetailComponent`
  - `'about'` → `AboutComponent`
  - `'**'` → `NotFoundComponent`

### `app.component.html`
- [ ] **TODO 2**: Add a `<nav>` with `[routerLink]` links to Home, Posts, and About
- [ ] **TODO 3**: Use `routerLinkActive="active"` on each link
- [ ] **TODO 4**: Add `<router-outlet>` where pages render

### `posts.service.ts`
- [ ] **TODO 5**: Define a `Post` interface: `id`, `title`, `excerpt`, `content`, `author`, `date`, `tags: string[]`
- [ ] **TODO 6**: Add a `getPosts()` method returning an array of 6 sample posts
- [ ] **TODO 7**: Add a `getPostById(id: number)` method

### `post-list.component.ts`
- [ ] **TODO 8**: Inject `PostsService` and `Router`
- [ ] **TODO 9**: Inject `ActivatedRoute` and read the `search` query param on init
- [ ] **TODO 10**: Create a `searchQuery` string and a `filteredPosts` getter
- [ ] **TODO 11**: Implement `onSearch()` — use `router.navigate` with `queryParams: { search }`

### `post-detail.component.ts`
- [ ] **TODO 12**: Inject `ActivatedRoute` and `PostsService`
- [ ] **TODO 13**: Read the `:id` route parameter and load the matching post
- [ ] **TODO 14**: Handle the case where the post is not found

---

## 💡 Key Concepts Reminder

```typescript
// Defining routes (app.routes.ts)
export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'posts/:id', component: PostDetailComponent },
  { path: '**', component: NotFoundComponent },
];

// main.ts — provide the router
bootstrapApplication(AppComponent, {
  providers: [provideRouter(routes)]
});

// Reading a route param in a component
const route = inject(ActivatedRoute);
const id = route.snapshot.paramMap.get('id');

// Navigating programmatically
const router = inject(Router);
router.navigate(['/posts', post.id]);

// RouterLink in template
<a [routerLink]="['/posts', post.id]">Read more</a>
<a routerLink="/about" routerLinkActive="active">About</a>
```
