import { useState } from 'react'
import { useMutation, useQuery } from '@apollo/client'
import { ADD_BOOK } from '../graphql/mutations'
import { GET_ALL_AUTHORS } from '../graphql/queries'

/**
 * AddBookForm — modal form for creating a new book.
 *
 * TODO Task 6c: Complete this component.
 * Use the ADD_BOOK mutation and populate the author dropdown with GET_ALL_AUTHORS.
 */
export default function AddBookForm({ onClose, onSuccess }) {
  const [form, setForm] = useState({
    title: '',
    genre: '',
    publishedYear: new Date().getFullYear(),
    authorId: '',
  })

  // TODO Task 6c-i: Use useQuery to fetch authors for the dropdown
  const { data: authorsData } = useQuery(GET_ALL_AUTHORS)

  // TODO Task 6c-ii: Use useMutation ADD_BOOK
  const [addBook, { loading, error }] = useMutation(ADD_BOOK)

  const handleSubmit = async (e) => {
    e.preventDefault()
    // TODO: call addBook mutation with form values, then call onSuccess()
    try {
      await addBook({
        variables: {
          input: {
            ...form,
            publishedYear: parseInt(form.publishedYear),
          }
        }
      })
      onSuccess()
    } catch (err) {
      console.error(err)
    }
  }

  const set = (field) => (e) => setForm(f => ({ ...f, [field]: e.target.value }))

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
          <h2>Add New Book</h2>
          <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '1.5rem', cursor: 'pointer' }}>×</button>
        </div>

        <form onSubmit={handleSubmit}>
          <label>Title</label>
          <input type="text" value={form.title} onChange={set('title')} required />

          <label>Genre</label>
          <input type="text" value={form.genre} onChange={set('genre')} required />

          <label>Published Year</label>
          <input type="number" value={form.publishedYear} onChange={set('publishedYear')} min="1000" max="2099" required />

          <label>Author</label>
          <select value={form.authorId} onChange={set('authorId')} required>
            <option value="">— select author —</option>
            {authorsData?.authors?.map(a => (
              <option key={a.id} value={a.id}>{a.name}</option>
            ))}
          </select>

          {error && <p className="error">{error.message}</p>}

          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button className="btn" type="submit" disabled={loading}>
              {loading ? 'Adding…' : 'Add Book'}
            </button>
            <button type="button" className="btn btn-danger" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  )
}
