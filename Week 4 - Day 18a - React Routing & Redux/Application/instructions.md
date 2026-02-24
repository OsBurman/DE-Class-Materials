# Day 18a Application — React Routing & Redux: Mini Blog

## Overview

You'll build a **Mini Blog** — a multi-page React app with client-side routing and global state management using Redux Toolkit. Users can browse posts, read individual posts, write new ones, and filter by category.

---

## Learning Goals

- Implement client-side routing with React Router v6
- Use route parameters and navigate programmatically
- Manage global state with Redux Toolkit (slices, actions, reducers)
- Connect components with `useSelector` and `useDispatch`
- Debug state with Redux DevTools

---

## Prerequisites

- `cd starter-code && npm install && npm run dev`

---

## Project Structure

```
starter-code/
├── package.json
├── vite.config.js
├── index.html
└── src/
    ├── main.jsx             ← Router setup
    ├── App.jsx              ← Route definitions
    ├── App.css
    ├── store/
    │   ├── store.js         ← TODO: configure Redux store
    │   └── postsSlice.js    ← TODO: create posts slice
    └── pages/
        ├── PostsPage.jsx    ← TODO: list all posts
        ├── PostDetailPage.jsx ← TODO: single post view
        └── NewPostPage.jsx  ← TODO: create post form
    components/
        └── Navbar.jsx       ← provided
```

---

## Part 1 — Redux Store

**Task 1 — `postsSlice.js`**  
Use `createSlice` from Redux Toolkit:
- State: `{ posts: [...initialPosts], status: 'idle', selectedCategory: 'all' }`
- Actions: `addPost(post)`, `deletePost(id)`, `setCategory(category)`
- Export action creators and reducer

**Task 2 — `store.js`**  
Configure with `configureStore({ reducer: { posts: postsReducer } })`. Export store.

**Task 3 — Provide store**  
In `main.jsx`, wrap `<App />` in `<Provider store={store}>` and `<BrowserRouter>`.

---

## Part 2 — Routing in `App.jsx`

**Task 4 — Define routes**  
```jsx
<Routes>
  <Route path="/" element={<PostsPage />} />
  <Route path="/posts/:id" element={<PostDetailPage />} />
  <Route path="/new" element={<NewPostPage />} />
  <Route path="*" element={<NotFound />} />
</Routes>
```

---

## Part 3 — Pages

**Task 5 — `PostsPage`**  
`useSelector` to get posts and selectedCategory. `useDispatch` to dispatch `setCategory`. Render filtered post cards. Each card links to `/posts/:id` using `<Link>`.

**Task 6 — `PostDetailPage`**  
Use `useParams()` to get `id`. `useSelector` to find the post by id.  
If not found, show "Post not found" and a `<Link>` back home.  
Add a delete button that dispatches `deletePost(id)` then navigates to `/` with `useNavigate()`.

**Task 7 — `NewPostPage`**  
Controlled form: title, content, category (select). On submit: dispatch `addPost({...})` with a generated id, then navigate to `/`. Add a Cancel button that navigates back.

---

## Stretch Goals

1. Add a `<NavLink>` to Navbar that shows an active class on the current route.
2. Create a nested route `/posts/:id/edit` for editing a post.
3. Add `localStorage` middleware to Redux to persist posts.

---

## Submission Checklist

- [ ] `createSlice` used with 3 action creators
- [ ] `configureStore` used
- [ ] `<Provider>` wraps the app
- [ ] `<BrowserRouter>` and `<Routes>` configured
- [ ] `useParams()` used in PostDetailPage
- [ ] `useNavigate()` used after delete and after create
- [ ] `useSelector` used in at least 2 components
- [ ] `useDispatch` used to dispatch actions
- [ ] 404 catch-all route added
