import { Link } from 'react-router-dom'

function HomePage() {
  return (
    <div className="page">
      <div className="hero">
        <h1>📚 Welcome to Bookstore</h1>
        <p>Discover your next favourite read from our curated collection of books.</p>
        <Link to="/books" className="btn">Browse Books</Link>
      </div>

      <section>
        <h2>Why Shop With Us?</h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px,1fr))', gap: '1rem', marginTop: '1rem' }}>
          {[
            { icon: '🚀', title: 'Fast Delivery', desc: 'Get your books within 2–3 business days.' },
            { icon: '💰', title: 'Best Prices', desc: 'Competitive pricing on all titles.' },
            { icon: '📖', title: 'Curated Selection', desc: 'Hand-picked books across all genres.' },
          ].map(f => (
            <div key={f.title} style={{ background: '#fff', padding: '1.5rem', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>{f.icon}</div>
              <strong>{f.title}</strong>
              <p style={{ color: '#64748b', fontSize: '0.9rem', marginTop: '0.25rem' }}>{f.desc}</p>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}

export default HomePage
