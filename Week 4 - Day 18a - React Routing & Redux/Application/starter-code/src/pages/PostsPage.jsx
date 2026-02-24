import { useSelector, useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
// TODO Task 5: Import setCategory action creator from '../store/postsSlice'

const CATEGORIES = ['all', 'React', 'Redux', 'JavaScript', 'Other'];

export default function PostsPage() {
  const dispatch = useDispatch();

  // TODO Task 5: Get posts and selectedCategory from Redux state
  // const { posts, selectedCategory } = useSelector(state => state.posts);
  const posts = [];
  const selectedCategory = 'all';

  // TODO Task 5: Compute filteredPosts — if selectedCategory is 'all', show all posts,
  // otherwise show only posts where post.category === selectedCategory
  const filteredPosts = posts;

  return (
    <main className="page">
      <div className="page-header">
        <h1>Blog Posts</h1>
        <Link to="/new" className="btn-primary">+ New Post</Link>
      </div>

      {/* TODO Task 5: Render category filter buttons.
          On click, dispatch setCategory(category).
          Add 'active' class to the currently selected category button. */}
      <div className="category-filters">
        {CATEGORIES.map(cat => (
          <button key={cat} className="filter-btn">{cat}</button>
        ))}
      </div>

      {/* TODO Task 5: Map filteredPosts to post cards.
          Each card should have a <Link to={`/posts/${post.id}`}> around the title. */}
      <div className="posts-grid">
        {filteredPosts.length === 0 && <p className="empty">No posts in this category.</p>}
      </div>
    </main>
  );
}
