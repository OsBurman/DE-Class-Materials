import { useQuery } from '@apollo/client'
import { GET_ALL_AUTHORS } from '../graphql/queries'
import LoadingSpinner from './LoadingSpinner'
import ErrorMessage from './ErrorMessage'

/**
 * AuthorList — displays all authors and their books.
 *
 * TODO Task 6e: Complete this component using useQuery.
 */
export default function AuthorList() {
  // TODO: use useQuery with GET_ALL_AUTHORS
  const { loading, error, data } = useQuery(GET_ALL_AUTHORS)

  if (loading) return <LoadingSpinner />
  if (error) return <ErrorMessage message={error.message} />

  return (
    <div>
      <h2>Authors ({data?.authors?.length ?? 0})</h2>
      {data?.authors?.map(author => (
        <div key={author.id} style={{ background: '#fff', borderRadius: 8, padding: '1.25rem', marginBottom: '1rem', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' }}>
          <h3 style={{ marginBottom: '0.25rem' }}>{author.name}</h3>
          {author.bio && <p style={{ color: '#6b7280', fontSize: '0.9rem', marginBottom: '0.75rem' }}>{author.bio}</p>}
          <p style={{ fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.4rem' }}>
            Books ({author.books?.length ?? 0}):
          </p>
          <ul style={{ paddingLeft: '1.25rem' }}>
            {author.books?.map(b => (
              <li key={b.id} style={{ fontSize: '0.9rem', color: '#374151' }}>{b.title} ({b.publishedYear})</li>
            ))}
          </ul>
        </div>
      ))}
    </div>
  )
}
