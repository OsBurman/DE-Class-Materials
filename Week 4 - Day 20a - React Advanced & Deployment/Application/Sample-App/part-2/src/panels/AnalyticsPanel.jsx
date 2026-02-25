// AnalyticsPanel.jsx — lazy-loaded chunk #1
import React, { useState, useEffect } from 'react'

const primary = '#1e3a5f', accent = '#e06c1b'

function Bar({ label, value, max }) {
  return (
    <div style={{ marginBottom: '.6rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '.85rem', marginBottom: '.2rem' }}>
        <span>{label}</span><span style={{ fontWeight: 'bold' }}>{value}%</span>
      </div>
      <div style={{ background: '#eee', borderRadius: 4, height: 12 }}>
        <div style={{ width: `${(value / max) * 100}%`, background: accent, borderRadius: 4, height: '100%',
                      transition: 'width .6s ease' }} />
      </div>
    </div>
  )
}

export default function AnalyticsPanel() {
  const [loaded, setLoaded] = useState(false)
  useEffect(() => { const t = setTimeout(() => setLoaded(true), 100); return () => clearTimeout(t) }, [])

  const data = [
    { label: 'Java', value: 88 }, { label: 'React', value: 75 },
    { label: 'Spring Boot', value: 62 }, { label: 'Docker', value: 48 },
  ]

  return (
    <div>
      <h3 style={{ color: primary, marginBottom: '1rem' }}>📊 Student Progress Analytics</h3>
      <p style={{ color: '#555', fontSize: '.85rem', marginBottom: '1rem' }}>
        This component was lazy-loaded — it only downloaded when you clicked "Analytics".
      </p>
      {loaded && data.map(d => <Bar key={d.label} label={d.label} value={d.value} max={100} />)}
    </div>
  )
}
