import { createSlice } from '@reduxjs/toolkit';

// Seed data — provided
const initialPosts = [
  { id: 1, title: 'Getting Started with React', content: 'React is a JavaScript library for building user interfaces...', category: 'React', date: '2024-01-15' },
  { id: 2, title: 'Understanding Redux', content: 'Redux is a predictable state container for JavaScript apps...', category: 'Redux', date: '2024-01-18' },
  { id: 3, title: 'React Router Basics', content: 'React Router enables client-side routing in React apps...', category: 'React', date: '2024-01-20' },
];

// TODO Task 1: Create the posts slice using createSlice
// Name: 'posts'
// Initial state: { posts: initialPosts, selectedCategory: 'all' }
// Reducers:
//   addPost(state, action)      — push action.payload to state.posts
//   deletePost(state, action)   — filter out post with id === action.payload
//   setCategory(state, action)  — set state.selectedCategory = action.payload
const postsSlice = createSlice({
  name: 'posts',
  initialState: {
    posts: initialPosts,
    selectedCategory: 'all',
  },
  reducers: {
    // TODO: addPost
    // TODO: deletePost
    // TODO: setCategory
  },
});

// TODO: Export action creators and reducer
// export const { addPost, deletePost, setCategory } = postsSlice.actions;
export default postsSlice.reducer;
