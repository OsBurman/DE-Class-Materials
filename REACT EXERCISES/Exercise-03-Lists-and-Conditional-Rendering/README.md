# Exercise 03 — Lists & Conditional Rendering

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Use **`Array.map()`** to render lists of components
- Understand why the **`key` prop** is required and how to choose a good key
- Use **`Array.filter()`** to derive a filtered list from state
- Use the **ternary operator** `condition ? a : b` for inline conditional rendering
- Use the **`&&` short-circuit** operator to conditionally show elements
- Handle **empty states** (showing a message when no items match)
- Update items immutably in an array

---

## 📋 What You're Building
A **Movie Watchlist App** where users can browse a movie collection, search by title, filter by genre, mark movies as watched/unwatched, and delete movies.

```
┌───────────────────────────────────────────────────────────┐
│ 🎬 My Watchlist        3 watched / 8 total                │
│                                                           │
│ Search: [Inception________]  Genre: [All ▾]               │
│                                                           │
│ ┌─────────────────────────────────────────────────────┐   │
│ │ ✅ Inception         Action  2010  ⭐ 8.8   [🗑]    │   │
│ │ Interstellar         Action  2014  ⭐ 8.6   [✓][🗑] │   │
│ │ The Dark Knight      Action  2008  ⭐ 9.0   [✓][🗑] │   │
│ └─────────────────────────────────────────────────────┘   │
│                                                           │
│  (empty state: "No movies match your search 🎭")          │
└───────────────────────────────────────────────────────────┘
```

---

## 🏗️ Project Setup
```bash
cd "Exercise-03-Lists-and-Conditional-Rendering/starter-code"
npm install
npm run dev
```

---

## 📁 File Structure
```
src/
├── main.jsx
├── index.css
├── App.jsx            ← all state + filter logic
├── App.css
└── components/
    ├── FilterBar.jsx  ← search input + genre dropdown
    ├── MovieList.jsx  ← renders MovieCard items or empty state
    └── MovieCard.jsx  ← single movie row
```

---

## ✅ TODOs

### `App.jsx`
- [ ] **TODO 1**: Import `useState` and initialize `movies` state with `INITIAL_MOVIES`
- [ ] **TODO 2**: Declare `searchTerm` state (string, default `''`)
- [ ] **TODO 3**: Declare `selectedGenre` state (string, default `'All'`)
- [ ] **TODO 4**: Derive `filteredMovies` — **no useState** — compute it:
  ```js
  const filteredMovies = movies
    .filter(m => selectedGenre === 'All' || m.genre === selectedGenre)
    .filter(m => m.title.toLowerCase().includes(searchTerm.toLowerCase()))
  ```
- [ ] **TODO 5**: Implement `toggleWatched(id)` — flip the movie's `watched` boolean (use map + spread)
- [ ] **TODO 6**: Implement `deleteMovie(id)` — filter out the movie with the matching id
- [ ] **TODO 7**: Compute `watchedCount` from movies array (not filteredMovies!)

### `components/FilterBar.jsx`
- [ ] **TODO 8**: Accept `searchTerm`, `onSearchChange`, `selectedGenre`, `onGenreChange`, `genres` as props
- [ ] **TODO 9**: Controlled `<input>` bound to `searchTerm` with `onChange`
- [ ] **TODO 10**: Controlled `<select>` bound to `selectedGenre` with `onChange`
- [ ] **TODO 11**: Render the genre options by mapping over the `genres` array

### `components/MovieList.jsx`
- [ ] **TODO 12**: Accept `movies`, `onToggle`, `onDelete` as props
- [ ] **TODO 13**: If `movies.length === 0`, render an empty state message using a ternary
- [ ] **TODO 14**: Map over `movies` and render a `<MovieCard key={movie.id} ...>` for each

### `components/MovieCard.jsx`
- [ ] **TODO 15**: Accept `movie`, `onToggle`, `onDelete` as props
- [ ] **TODO 16**: Apply CSS class `movie-card--watched` when `movie.watched` is true
- [ ] **TODO 17**: Show a ✅ icon using `&&` or ternary when `movie.watched` is true
- [ ] **TODO 18**: Wire up "Mark Watched" button: `onClick={() => onToggle(movie.id)}`
- [ ] **TODO 19**: Wire up "Delete" button: `onClick={() => onDelete(movie.id)}`
- [ ] **TODO 20**: Show different button text: "✓ Watched" vs "Mark Watched" based on `movie.watched`

---

## 💡 Key Concepts

| Pattern | Code |
|---------|------|
| Render a list | `items.map(item => <Card key={item.id} {...item} />)` |
| Filter a list | `items.filter(item => item.active)` |
| Conditional render | `{isLoggedIn && <UserPanel />}` |
| Ternary | `{count > 0 ? <List /> : <EmptyState />}` |
| Conditional class | `className={\`card \${active ? 'active' : ''}\`}` |
| Update one item | `items.map(i => i.id === id ? { ...i, done: true } : i)` |
| Remove one item | `items.filter(i => i.id !== id)` |
