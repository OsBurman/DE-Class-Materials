// SettingsPanel.jsx — lazy-loaded chunk #3
import React, { useState } from 'react'

const primary = '#1e3a5f', accent = '#e06c1b'

function Toggle({ label, checked, onChange }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '.6rem 0', borderBottom: '1px solid #f0f0f0' }}>
      <span style={{ color: '#333' }}>{label}</span>
      <div onClick={onChange} style={{ width: 40, height: 22, background: checked ? accent : '#ccc', borderRadius: 11, cursor: 'pointer', position: 'relative', transition: 'background .2s' }}>
        <div style={{ position: 'absolute', top: 3, left: checked ? 21 : 3, width: 16, height: 16, background: 'white', borderRadius: '50%', transition: 'left .2s', boxShadow: '0 1px 3px rgba(0,0,0,.3)' }} />
      </div>
    </div>
  )
}

export default function SettingsPanel() {
  const [settings, setSettings] = useState({
    notifications: true, darkMode: false, autoSave: true, analytics: false
  })

  const toggle = key => setSettings(s => ({ ...s, [key]: !s[key] }))

  return (
    <div>
      <h3 style={{ color: primary, marginBottom: '1rem' }}>⚙️ Settings</h3>
      <p style={{ color: '#555', fontSize: '.85rem', marginBottom: '1rem' }}>
        Loaded on demand — this entire settings UI wasn't in the initial bundle.
      </p>
      {Object.entries(settings).map(([k, v]) => (
        <Toggle key={k} label={k.replace(/([A-Z])/g, ' $1').replace(/^./, s => s.toUpperCase())}
                checked={v} onChange={() => toggle(k)} />
      ))}
    </div>
  )
}
