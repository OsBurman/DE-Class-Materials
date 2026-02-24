import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
// TODO Task 4: Import PostsPage, PostDetailPage, NewPostPage from their files

export default function App() {
  return (
    <>
      <Navbar />
      {/* TODO Task 4: Define your routes here:
           /           → PostsPage
           /posts/:id  → PostDetailPage
           /new        → NewPostPage
           *           → a simple "404 Not Found" component
      */}
      <Routes>
        <Route path="/" element={<div>PostsPage goes here</div>} />
      </Routes>
    </>
  );
}
