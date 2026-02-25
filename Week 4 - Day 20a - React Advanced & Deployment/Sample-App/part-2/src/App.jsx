// Day 20a Part 2 — Lazy Loading, Suspense, Code Splitting & Deployment Notes
// Run: npm install && npm run dev
// Build: npm run build   (creates dist/ folder)

import React, { useState, lazy, Suspense } from 'react'

const primary = '#1e3a5f', accent = '#e06c1b'
const s = {
  page:   { maxWidth: 900, margin: '0 auto', padding: '2rem 1rem', fontFamily: 'sans-serif' },
  header: { background: primary, color: 'white', padding: '1.5rem 2rem', borderRadius: 8, marginBottom: '2rem' },
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:     { color: primary, marginBottom: '.8rem', borderBottom: `2px solid ${accent}`, paddingBottom: '.4rem' },
  btn:    (c=primary, active) => ({ background: active ? c : '#eee', color: active ? 'white' : '#444', border: 'none', padding: '.4rem .9rem', borderRadius: 4, cursor: 'pointer', margin: '.2rem', fontWeight: active ? 'bold' : 'normal' }),
  sub:    { color: '#555', fontSize: '.85rem', margin: '.4rem 0' },
}

// ─────────────────────────────────────────────────────────────────────────────
// Lazily-imported "heavy" panel components
// React.lazy(() => import('./PanelName')) — each chunk only loads on demand
// ─────────────────────────────────────────────────────────────────────────────
const AnalyticsPanel = lazy(() => import('./panels/AnalyticsPanel'))
const ReportsPanel   = lazy(() => import('./panels/ReportsPanel'))
const SettingsPanel  = lazy(() => import('./panels/SettingsPanel'))

// ─────────────────────────────────────────────────────────────────────────────
// Loading fallback shown while chunk downloads
// ─────────────────────────────────────────────────────────────────────────────
function PanelSkeleton({ label }) {
  return (
    <div style={{ padding: '3rem', textAlign: 'center', color: '#888' }}>
      <div style={{ fontSize: '2rem', marginBottom: '.5rem' }}>⏳</div>
      <p>Loading {label}…</p>
      <div style={{ width: 200, height: 6, background: '#eee', borderRadius: 3, margin: '.8rem auto', overflow: 'hidden' }}>
        <div style={{ height: '100%', background: primary, width: '40%', animation: 'slide 1s infinite linear', borderRadius: 3 }} />
      </div>
      <style>{`@keyframes slide { 0%{margin-left:-40%} 100%{margin-left:100%} }`}</style>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// Navigation
// ─────────────────────────────────────────────────────────────────────────────
const tabs = [
  { id: 'analytics', label: '📊 Analytics',  Component: AnalyticsPanel },
  { id: 'reports',   label: '📋 Reports',    Component: ReportsPanel   },
  { id: 'settings',  label: '⚙️ Settings',   Component: SettingsPanel  },
]

export default function App() {
  const [activeTab, setActiveTab] = useState(null)

  const active = tabs.find(t => t.id === activeTab)

  return (
    <div style={s.page}>
      <div style={s.header}>
        <h1>⚛️ Day 20a Part 2 — Lazy Loading & Suspense</h1>
        <p style={{ opacity: .85, marginTop: '.3rem' }}>
          Each panel is a <strong>separate bundle chunk</strong> — only loaded when you click it.
        </p>
      </div>

      {/* How it works */}
      <div style={s.card}>
        <h2 style={s.h2}>How React.lazy + Suspense Work</h2>
        <ol style={{ ...s.sub, paddingLeft: '1.2rem', lineHeight: 2 }}>
          <li><code>React.lazy(() =&gt; import('./Panel'))</code> — wraps a dynamic import</li>
          <li>When first rendered, the module is fetched over the network</li>
          <li><code>&lt;Suspense fallback=...&gt;</code> shows the fallback while loading</li>
          <li>After loading, the component renders normally — and is <strong>cached</strong> for future renders</li>
          <li>In production (<code>npm run build</code>), Vite splits each lazy import into its own JS chunk</li>
        </ol>
        <p style={s.sub}>
          Open DevTools → Network tab → click a panel for the first time to see the chunk load.
        </p>
      </div>

      {/* Tab switcher */}
      <div style={s.card}>
        <h2 style={s.h2}>Dashboard — Click a Panel to Load It</h2>
        <div style={{ marginBottom: '1rem' }}>
          {tabs.map(t => (
            <button key={t.id} style={s.btn(primary, activeTab === t.id)} onClick={() => setActiveTab(t.id)}>
              {t.label}
            </button>
          ))}
          {activeTab && (
            <button style={s.btn('#aaa')} onClick={() => setActiveTab(null)}>✕ Close</button>
          )}
        </div>

        {active ? (
          <Suspense fallback={<PanelSkeleton label={active.label} />}>
            <active.Component />
          </Suspense>
        ) : (
          <div style={{ padding: '2rem', textAlign: 'center', color: '#aaa', border: '2px dashed #ddd', borderRadius: 8 }}>
            Select a panel above to load it
          </div>
        )}
      </div>

      {/* Deployment notes */}
      <div style={s.card}>
        <h2 style={s.h2}>Production Build & Deployment</h2>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '.88rem' }}>
          <thead>
            <tr style={{ background: primary, color: 'white' }}>
              <th style={{ padding: '.5rem', textAlign: 'left' }}>Step</th>
              <th style={{ padding: '.5rem', textAlign: 'left' }}>Command</th>
              <th style={{ padding: '.5rem', textAlign: 'left' }}>Result</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['1. Build', 'npm run build', 'Creates dist/ with minified JS chunks'],
              ['2. Preview', 'npm run preview', 'Serves dist/ locally to verify build'],
              ['3. Deploy (Netlify)', 'drag dist/ to netlify.com/drop', 'Live URL in < 30 seconds'],
              ['4. Deploy (Vercel)', 'npx vercel', 'Auto-detects Vite, deploys to CDN'],
              ['5. Deploy (GitHub Pages)', 'add base to vite.config.js + npm run build', 'Push dist/ to gh-pages branch'],
            ].map(([s, c, r]) => (
              <tr key={s} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '.5rem' }}>{s}</td>
                <td style={{ padding: '.5rem' }}><code>{c}</code></td>
                <td style={{ padding: '.5rem', color: '#555' }}>{r}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
