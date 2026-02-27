// Receives quote (object or null), isLoading (boolean), onRefresh (function)
export default function QuoteWidget({ quote, isLoading, onRefresh }) {
  return (
    <div className="widget">
      <h2>💬 Random Quote</h2>
      {isLoading && <p className="loading">Loading quote...</p>}
      {quote && !isLoading && (
        <>
          <p className="quote-text">"{quote.quote}"</p>
          <p className="quote-author">— {quote.author}</p>
        </>
      )}
      <button className="btn-refresh" onClick={onRefresh} disabled={isLoading}>
        {isLoading ? 'Loading...' : '🔄 New Quote'}
      </button>
    </div>
  )
}
