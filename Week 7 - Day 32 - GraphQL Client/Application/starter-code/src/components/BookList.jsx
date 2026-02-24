import { useState } from 'react'
import { useQuery } from '@apollo/client'
import { GET_ALL_BOOKS, SEARCH_BOOKS } from '../graphql/queries'
import BookCard from './BookCard'
import BookDetail from './BookDetail'
import AddBookForm from './AddBookForm'
import SearchBar from './SearchBar'
import LoadingSpinner from './LoadingSpinner'
import ErrorMessage from './ErrorMessage'

/**
 * BookList component — displays all books with search, detail view, and add form.
 *
 * TODO Task 5: Complete this component.
 */
export default function BookList() {
  const [selectedBookId, setSelectedBookId] = useState(null)
  const [showAddForm, setShowAddForm] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [isSearching, setIsSearching] = useState(false)

  // TODO Task 5a: Use useQuery with GET_ALL_BOOKS
  const { loading, error, data, refetch } = useQuery(GET_ALL_BOOKS)

  // TODO Task 5b: Use useLazyQuery with SEARCH_BOOKS for search functionality
  // const [searchBooks, { loading: searchLoading, data: searchData }] = useLazyQuery(SEARCH_BOOKS)

  const books = isSearching
    ? [] // TODO: replace with searchData?.searchBooks
    : data?.books ?? []

  const handleSearch = (term) => {
    setSearchTerm(term)
    if (term.trim()) {
      setIsSearching(true)
      // TODO: call searchBooks({ variables: { titleContains: term } })
    } else {
      setIsSearching(false)
    }
  }

  if (loading) return <LoadingSpinner />
  if (error) return <ErrorMessage message={error.message} />

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h2>Books ({books.length})</h2>
        <button className="btn" onClick={() => setShowAddForm(true)}>+ Add Book</button>
      </div>

      <SearchBar onSearch={handleSearch} />

      <div className="book-grid">
        {books.map(book => (
          <BookCard
            key={book.id}
            book={book}
            onClick={() => setSelectedBookId(book.id)}
          />
        ))}
      </div>

      {selectedBookId && (
        <BookDetail
          bookId={selectedBookId}
          onClose={() => setSelectedBookId(null)}
          onRefetch={refetch}
        />
      )}

      {showAddForm && (
        <AddBookForm
          onClose={() => setShowAddForm(false)}
          onSuccess={() => { setShowAddForm(false); refetch(); }}
        />
      )}
    </div>
  )
}
