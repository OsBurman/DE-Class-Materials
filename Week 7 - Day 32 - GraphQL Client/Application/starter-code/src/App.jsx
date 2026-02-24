import { useState } from 'react'
import BookList from './components/BookList'
import AuthorList from './components/AuthorList'

// TODO Task 4: Connect all components

function App() {
  const [activeTab, setActiveTab] = useState('books')

  return (
    <div className="app">
      <h1>📚 Book Library</h1>

      <div className="tabs">
        <button
          className={`tab ${activeTab === 'books' ? 'active' : ''}`}
          onClick={() => setActiveTab('books')}
        >
          Books
        </button>
        <button
          className={`tab ${activeTab === 'authors' ? 'active' : ''}`}
          onClick={() => setActiveTab('authors')}
        >
          Authors
        </button>
      </div>

      {activeTab === 'books' ? <BookList /> : <AuthorList />}
    </div>
  )
}

export default App
