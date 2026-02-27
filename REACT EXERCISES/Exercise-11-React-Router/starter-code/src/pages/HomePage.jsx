import { Link } from 'react-router-dom'

function HomePage() {
  return (
    <div className="page hero">
      <h1>Welcome to the Bookstore 📚</h1>
      <p>Browse our curated collection of developer books and level up your skills.</p>
      <Link to="/books" className="btn">Browse Books</Link>
    </div>
  )
}

export default HomePage
