import { useParams, useNavigate, Link } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
// TODO Task 6: Import deletePost action creator

export default function PostDetailPage() {
  const { id } = useParams();  // TODO Task 6: use id to find the post
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // TODO Task 6: Get all posts from Redux and find the matching post
  // const posts = useSelector(state => state.posts.posts);
  // const post = posts.find(p => p.id === Number(id));

  // TODO Task 6: If post is not found, return a "Post not found" message + Link back home

  function handleDelete() {
    // TODO Task 6: Dispatch deletePost with the post's id
    // After dispatch, navigate back to '/'
  }

  return (
    <main className="page post-detail">
      <Link to="/" className="back-link">← Back to posts</Link>
      {/* TODO: Render post title, category badge, date, content */}
      {/* TODO: Add delete button that calls handleDelete() */}
      <p style={{ color: '#aaa' }}>Post content goes here</p>
    </main>
  );
}
