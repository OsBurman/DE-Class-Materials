import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, Link } from 'react-router-dom';
// TODO Task 7: Import addPost action creator

const CATEGORIES = ['React', 'Redux', 'JavaScript', 'Other'];

export default function NewPostPage() {
  // TODO Task 7: useState for title, content, category (default 'React')
  const dispatch = useDispatch();
  const navigate = useNavigate();

  function handleSubmit(e) {
    e.preventDefault();
    // TODO Task 7: Validate title and content are not empty.
    // Dispatch addPost({ id: Date.now(), title, content, category, date: new Date().toISOString().slice(0, 10) })
    // After dispatch, navigate to '/'
  }

  return (
    <main className="page">
      <h1>New Post</h1>
      {/* TODO Task 7: Build the form with controlled inputs for title, content, category */}
      <form onSubmit={handleSubmit} className="post-form">
        <input type="text" placeholder="Post title" className="form-input" />
        <textarea placeholder="Write your post..." className="form-textarea" rows={6} />
        <select className="form-select">
          {CATEGORIES.map(c => <option key={c}>{c}</option>)}
        </select>
        <div className="form-actions">
          <Link to="/" className="btn-secondary">Cancel</Link>
          <button type="submit" className="btn-primary">Publish Post</button>
        </div>
      </form>
    </main>
  );
}
