import { useLocalStorage } from '../hooks/useLocalStorage'

// This widget saves notes to localStorage so they survive a page reload.
// TODO: Replace the useState below with useLocalStorage('dev-notes', '') so the notes persist.

function NotesWidget() {
  // TODO: use useLocalStorage instead of a plain import
  const [notes, setNotes] = useLocalStorage('dev-notes', '')

  return (
    <div className="widget">
      <h2>📝 Notes Widget</h2>
      <span className="hook-name">useLocalStorage</span>
      <p style={{ fontSize: '0.85rem', color: '#64748b', marginBottom: '0.75rem' }}>
        Your notes are saved to localStorage. Refresh the page — they'll still be here!
      </p>
      {/* TODO: Bind value={notes} and onChange={e => setNotes(e.target.value)} */}
      <textarea rows={5} placeholder="Type your notes here…" value={notes} onChange={e => setNotes(e.target.value)} />
      <button className="btn-primary" style={{ marginTop: '0.5rem' }} onClick={() => setNotes('')}>
        Clear
      </button>
    </div>
  )
}

export default NotesWidget
