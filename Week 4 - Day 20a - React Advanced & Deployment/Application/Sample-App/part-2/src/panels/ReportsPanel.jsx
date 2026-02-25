// ReportsPanel.jsx — lazy-loaded chunk #2
import React from 'react'

const primary = '#1e3a5f'

const reports = [
  { id: 'RPT-001', title: 'Week 1 Java Fundamentals',  date: '2024-01-07', status: 'Complete', grade: 'A' },
  { id: 'RPT-002', title: 'Week 2 OOP & Collections',  date: '2024-01-14', status: 'Complete', grade: 'B+' },
  { id: 'RPT-003', title: 'Week 3 Frontend Basics',    date: '2024-01-21', status: 'Complete', grade: 'A-' },
  { id: 'RPT-004', title: 'Week 4 React & Angular',    date: '2024-01-28', status: 'In Progress', grade: '—' },
  { id: 'RPT-005', title: 'Week 5 Spring Boot',        date: '2024-02-04', status: 'Upcoming', grade: '—' },
]

const statusColor = { Complete: '#27ae60', 'In Progress': '#e67e22', Upcoming: '#888' }

export default function ReportsPanel() {
  return (
    <div>
      <h3 style={{ color: primary, marginBottom: '1rem' }}>📋 Course Reports</h3>
      <p style={{ color: '#555', fontSize: '.85rem', marginBottom: '1rem' }}>
        This chunk was only fetched when you navigated here — reducing the initial bundle size.
      </p>
      <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '.88rem' }}>
        <thead>
          <tr style={{ background: primary, color: 'white' }}>
            {['ID', 'Report', 'Date', 'Status', 'Grade'].map(h => (
              <th key={h} style={{ padding: '.5rem', textAlign: 'left' }}>{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {reports.map(r => (
            <tr key={r.id} style={{ borderBottom: '1px solid #eee' }}>
              <td style={{ padding: '.45rem', color: '#888' }}>{r.id}</td>
              <td style={{ padding: '.45rem' }}>{r.title}</td>
              <td style={{ padding: '.45rem', color: '#888' }}>{r.date}</td>
              <td style={{ padding: '.45rem' }}>
                <span style={{ color: statusColor[r.status], fontWeight: 'bold' }}>{r.status}</span>
              </td>
              <td style={{ padding: '.45rem', fontWeight: 'bold' }}>{r.grade}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
