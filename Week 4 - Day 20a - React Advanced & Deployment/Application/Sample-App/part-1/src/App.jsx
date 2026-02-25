// Day 20a Part 1 — React Performance: memo, useMemo, useCallback, render tracking
// Run: npm install && npm run dev

import React, { useState, useMemo, useCallback, memo, useRef, useEffect } from 'react'

const primary = '#1e3a5f', accent = '#e06c1b', success = '#27ae60', warn = '#e67e22'
const s = {
  page:   { maxWidth: 960, margin: '0 auto', padding: '2rem 1rem', fontFamily: 'sans-serif' },
  header: { background: primary, color: 'white', padding: '1.5rem 2rem', borderRadius: 8, marginBottom: '2rem' },
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:     { color: primary, marginBottom: '.8rem', borderBottom: `2px solid ${accent}`, paddingBottom: '.4rem' },
  btn:    (c=primary) => ({ background: c, color: 'white', border: 'none', padding: '.4rem .9rem', borderRadius: 4, cursor: 'pointer', margin: '.2rem' }),
  badge:  (c='#888') => ({ background: c, color: 'white', borderRadius: 10, padding: '1px 8px', fontSize: '.75rem', marginLeft: '.4rem' }),
  sub:    { color: '#555', fontSize: '.85rem', margin: '.4rem 0' },
}

// Render counter badge — turns orange on re-render, then fades back
function RenderBadge({ label }) {
  const count = useRef(0)
  const [flash, setFlash] = useState(false)
  count.current += 1
  useEffect(() => {
    setFlash(true)
    const t = setTimeout(() => setFlash(false), 400)
    return () => clearTimeout(t)
  })
  return <span style={s.badge(flash ? warn : success)}>renders: {count.current}</span>
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. React.memo — prevent re-render when props didn't change
// ─────────────────────────────────────────────────────────────────────────────
const ItemCard = memo(function ItemCard({ name, score }) {
  return (
    <div style={{ padding: '.4rem .8rem', background: '#f0f4ff', borderRadius: 4, marginBottom: '.3rem', display: 'flex', justifyContent: 'space-between' }}>
      <span>{name}</span>
      <span><strong>{score}</strong> <RenderBadge label={name} /></span>
    </div>
  )
})
// Without memo:  parent re-render → ALL ItemCards re-render
// With    memo:  only re-render when name or score changes

// ─────────────────────────────────────────────────────────────────────────────
// 2. useMemo — cache expensive computation
// ─────────────────────────────────────────────────────────────────────────────
function expensiveFilter(items, query) {
  // Simulate work (real apps: sorting large arrays, complex derived data)
  const start = performance.now()
  while (performance.now() - start < 5) {} // 5ms artificial delay
  return items.filter(i => i.name.toLowerCase().includes(query.toLowerCase()))
}

// ─────────────────────────────────────────────────────────────────────────────
// Demo: memo + useMemo + useCallback together
// ─────────────────────────────────────────────────────────────────────────────
const students = Array.from({ length: 20 }, (_, i) => ({
  id: i + 1,
  name: ['Alice','Bob','Carol','David','Eve','Frank','Grace','Hank','Ivy','Jack'][i % 10] + ` ${i + 1}`,
  score: Math.floor(60 + Math.random() * 40),
}))

export default function App() {
  const [theme,  setTheme]  = useState('light')
  const [query,  setQuery]  = useState('')
  const [sortBy, setSortBy] = useState('name')
  const [bonus,  setBonus]  = useState(0)

  // ── useMemo: only recomputes when query or sortBy changes ─────────────────
  const filtered = useMemo(() => {
    const f = expensiveFilter(students, query)
    return [...f].sort((a, b) => sortBy === 'name'
      ? a.name.localeCompare(b.name)
      : b.score - a.score
    )
  }, [query, sortBy])

  // ── useMemo: derived summary stats ────────────────────────────────────────
  const stats = useMemo(() => ({
    avg: filtered.length ? Math.round(filtered.reduce((s, i) => s + i.score, 0) / filtered.length) : 0,
    top: filtered.reduce((best, i) => i.score > (best?.score ?? 0) ? i : best, null),
  }), [filtered])

  // ── useCallback: stable reference — ItemCard with memo won't see new fn ref
  const handleHighlight = useCallback((id) => {
    console.log('Highlight student:', id)
  }, [])  // empty deps → same function reference across renders

  return (
    <div style={{ ...s.page, background: theme === 'dark' ? '#1a1a2e' : '#f5f7fa', minHeight: '100vh' }}>
      <div style={s.header}>
        <h1>⚛️ Day 20a Part 1 — Performance Optimizations</h1>
      </div>

      {/* Theme toggle — causes App re-render but memo'd items won't */}
      <div style={s.card}>
        <h2 style={s.h2}>Render Control Panel</h2>
        <p style={s.sub}>The <strong>theme</strong> toggle re-renders App — watch which badges flash orange below.</p>
        <button style={s.btn()} onClick={() => setTheme(t => t === 'light' ? 'dark' : 'light')}>
          Toggle Theme (forces App re-render)
        </button>
        <button style={s.btn(accent)} onClick={() => setBonus(b => b + 5)}>
          +5 Bonus (changes nothing in list)
        </button>
        <span style={{ ...s.sub, display:'inline', marginLeft:'1rem' }}>bonus: {bonus} (unused by memo'd items)</span>
      </div>

      {/* useMemo demo */}
      <div style={s.card}>
        <h2 style={s.h2}>useMemo — Filtered + Sorted Student List</h2>
        <p style={s.sub}>The filter runs a 5ms expensive computation. <code>useMemo</code> caches the result until <em>query</em> or <em>sortBy</em> changes.</p>
        <div style={{ display:'flex', gap:'.5rem', marginBottom:'.8rem', flexWrap:'wrap' }}>
          <input placeholder="Filter by name…" value={query} onChange={e => setQuery(e.target.value)}
                 style={{ padding:'.35rem .6rem', border:'1px solid #ccc', borderRadius:4, width:180 }} />
          {['name','score'].map(k => (
            <button key={k} style={s.btn(sortBy === k ? accent : '#888')} onClick={() => setSortBy(k)}>
              Sort by {k}
            </button>
          ))}
        </div>
        {stats.top && (
          <p style={s.sub}>📊 {filtered.length} results · avg score: <strong>{stats.avg}</strong> · top: <strong>{stats.top.name}</strong> ({stats.top.score})</p>
        )}
        <div>
          {filtered.slice(0, 8).map(s => <ItemCard key={s.id} name={s.name} score={s.score} />)}
          {filtered.length > 8 && <p style={{ color:'#888', fontSize:'.85rem' }}>… and {filtered.length - 8} more</p>}
        </div>
      </div>

      {/* memo + useCallback explanation */}
      <div style={{ ...s.card, background: '#f9f9f9' }}>
        <h2 style={s.h2}>When to Use Each Tool</h2>
        <table style={{ width:'100%', borderCollapse:'collapse', fontSize:'.9rem' }}>
          <thead><tr style={{ background: primary, color:'white' }}>
            <th style={{ padding:'.5rem' }}>Hook</th>
            <th style={{ padding:'.5rem' }}>What it does</th>
            <th style={{ padding:'.5rem' }}>Use when</th>
          </tr></thead>
          <tbody>
            {[
              ['React.memo', 'Skips re-render if props unchanged', 'Child gets same props from parent re-render'],
              ['useMemo', 'Caches computed value', 'Expensive derivation from props/state'],
              ['useCallback','Stable function reference', 'Passing callbacks to memo\'d children'],
            ].map(([h, w, u]) => (
              <tr key={h} style={{ borderBottom:'1px solid #eee' }}>
                <td style={{ padding:'.5rem' }}><code>{h}</code></td>
                <td style={{ padding:'.5rem', color:'#555' }}>{w}</td>
                <td style={{ padding:'.5rem', color:'#555' }}>{u}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <p style={{ ...s.sub, marginTop:'.8rem' }}>⚠️ Don't over-optimize! Always measure first with React DevTools Profiler.</p>
      </div>
    </div>
  )
}
