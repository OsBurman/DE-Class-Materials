import { useState } from 'react'
import { useQuery, useMutation } from '@apollo/client'
import { GET_BOOK } from '../graphql/queries'
import { ADD_REVIEW, DELETE_BOOK } from '../graphql/mutations'
import LoadingSpinner from './LoadingSpinner'
import ErrorMessage from './ErrorMessage'

/**
 * BookDetail — modal overlay showing full book details + reviews.
 *
 * TODO Task 6b: Complete this component.
 */
export default function BookDetail({ bookId, onClose, onRefetch }) {
  const [showReviewForm, setShowReviewForm] = useState(false)
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '', reviewer: '' })

  // TODO Task 6b-i: useQuery GET_BOOK with variables: { id: bookId }
  const { loading, error, data, refetch } = useQuery(GET_BOOK, {
    variables: { id: bookId },
  })

  // TODO Task 6b-ii: useMutation ADD_REVIEW
  const [addReview, { loading: reviewLoading }] = useMutation(ADD_REVIEW)

  // TODO Task 6b-iii: useMutation DELETE_BOOK
  const [deleteBook] = useMutation(DELETE_BOOK)

  const handleAddReview = async (e) => {
    e.preventDefault()
    // TODO: call addReview mutation, then refetch()
    try {
      await addReview({
        variables: {
          bookId,
          input: {
            rating: parseInt(reviewForm.rating),
            comment: reviewForm.comment || null,
            reviewer: reviewForm.reviewer,
          }
        }
      })
      setShowReviewForm(false)
      setReviewForm({ rating: 5, comment: '', reviewer: '' })
      refetch()
    } catch (err) {
      alert('Error adding review: ' + err.message)
    }
  }

  const handleDelete = async () => {
    if (!confirm('Delete this book?')) return
    // TODO: call deleteBook mutation, then onClose + onRefetch
    await deleteBook({ variables: { id: bookId } })
    onClose()
    onRefetch()
  }

  if (loading) return (
    <div className="modal-overlay"><div className="modal"><LoadingSpinner /></div></div>
  )
  if (error) return (
    <div className="modal-overlay"><div className="modal"><ErrorMessage message={error.message} /><button onClick={onClose}>Close</button></div></div>
  )

  const book = data?.book
  if (!book) return null

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
          <h2>{book.title}</h2>
          <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '1.5rem', cursor: 'pointer' }}>×</button>
        </div>

        <p><strong>Author:</strong> {book.author?.name}</p>
        {book.author?.bio && <p style={{ color: '#6b7280', fontSize: '0.9rem', marginTop: '0.25rem' }}>{book.author.bio}</p>}
        <p style={{ marginTop: '0.5rem' }}><strong>Genre:</strong> {book.genre}</p>
        <p><strong>Published:</strong> {book.publishedYear}</p>
        {book.averageRating && <p><strong>Rating:</strong> ⭐ {book.averageRating.toFixed(1)}</p>}

        <div className="reviews-section">
          <h3>Reviews ({book.reviews?.length ?? 0})</h3>
          {book.reviews?.map(r => (
            <div key={r.id} className="review-item">
              <p><strong>{r.reviewer}</strong> — {'⭐'.repeat(r.rating)}</p>
              {r.comment && <p style={{ color: '#6b7280', marginTop: '0.25rem' }}>{r.comment}</p>}
            </div>
          ))}

          {!showReviewForm && (
            <button className="btn" style={{ marginTop: '0.75rem' }} onClick={() => setShowReviewForm(true)}>
              + Add Review
            </button>
          )}

          {showReviewForm && (
            <form onSubmit={handleAddReview} style={{ marginTop: '1rem' }}>
              <label>Rating (1–5)</label>
              <input type="number" min="1" max="5" value={reviewForm.rating}
                onChange={e => setReviewForm(f => ({ ...f, rating: e.target.value }))} required />
              <label>Your Name</label>
              <input type="text" value={reviewForm.reviewer}
                onChange={e => setReviewForm(f => ({ ...f, reviewer: e.target.value }))} required />
              <label>Comment (optional)</label>
              <textarea value={reviewForm.comment} rows="3"
                onChange={e => setReviewForm(f => ({ ...f, comment: e.target.value }))} />
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button className="btn" type="submit" disabled={reviewLoading}>
                  {reviewLoading ? 'Submitting…' : 'Submit'}
                </button>
                <button type="button" className="btn btn-danger" onClick={() => setShowReviewForm(false)}>
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>

        <div style={{ marginTop: '1.5rem', borderTop: '1px solid #e5e7eb', paddingTop: '1rem' }}>
          <button className="btn btn-danger" onClick={handleDelete}>Delete Book</button>
        </div>
      </div>
    </div>
  )
}
