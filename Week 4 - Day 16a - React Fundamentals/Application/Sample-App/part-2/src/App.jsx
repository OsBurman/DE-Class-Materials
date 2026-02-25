// Day 16a Part 2 — Props, Data Flow, Lists/Keys, Conditional Rendering, Composition
// Run: npm install && npm run dev

import React from 'react'

const styles = {
  page:   { maxWidth: 960, margin: '0 auto', padding: '2rem 1rem' },
  header: { background: '#1e3a5f', color: 'white', padding: '1.5rem 2rem', borderRadius: 8, marginBottom: '2rem' },
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem',
            boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  h2:     { color: '#1e3a5f', marginBottom: '1rem', borderBottom: '2px solid #e06c1b', paddingBottom: '.4rem' },
  badge:  (color) => ({ background: color, color: 'white', padding: '2px 10px', borderRadius: 12,
                        fontSize: '.8rem', fontWeight: 'bold', display: 'inline-block' }),
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. PROPS — passing data into components
// ─────────────────────────────────────────────────────────────────────────────
// Props make components reusable — same component, different data

function StudentCard({ name, cohort, score, avatar = '🎓', enrolled = true }) {
  // Destructure props directly in the function signature
  const grade = score >= 90 ? 'A' : score >= 80 ? 'B' : score >= 70 ? 'C' : 'F'
  const gradeColor = score >= 90 ? '#27ae60' : score >= 80 ? '#2980b9' : score >= 70 ? '#f39c12' : '#e74c3c'

  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: '1rem',
      background: '#f9f9f9', borderRadius: 8, padding: '.8rem 1rem',
      marginBottom: '.5rem', border: '1px solid #eee'
    }}>
      <span style={{ fontSize: '2rem' }}>{avatar}</span>
      <div style={{ flex: 1 }}>
        <strong>{name}</strong>
        <br />
        <small style={{ color: '#888' }}>Cohort: {cohort}</small>
      </div>
      {/* Conditional rendering — show/hide based on enrolled prop */}
      {enrolled
        ? <span style={styles.badge(gradeColor)}>Grade {grade} ({score})</span>
        : <span style={styles.badge('#999')}>Not enrolled</span>
      }
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. LISTS & KEYS
// ─────────────────────────────────────────────────────────────────────────────
const courses = [
  { id: 1, name: 'Java Fundamentals', week: 'Week 1-2', icon: '☕', enrolled: 42 },
  { id: 2, name: 'HTML & CSS',        week: 'Week 3',   icon: '🌐', enrolled: 40 },
  { id: 3, name: 'JavaScript',        week: 'Week 3',   icon: '📜', enrolled: 38 },
  { id: 4, name: 'React',             week: 'Week 4',   icon: '⚛️', enrolled: 35 },
  { id: 5, name: 'Spring Boot',       week: 'Week 5-6', icon: '🍃', enrolled: 30 },
]

function CourseRow({ course }) {
  return (
    <tr>
      <td style={{ padding: '.5rem .8rem' }}>{course.icon} {course.name}</td>
      <td style={{ padding: '.5rem .8rem', color: '#888' }}>{course.week}</td>
      <td style={{ padding: '.5rem .8rem', textAlign: 'center' }}>
        <span style={styles.badge('#1e3a5f')}>{course.enrolled}</span>
      </td>
    </tr>
  )
}

function CourseTable({ courses, filter }) {
  const displayed = filter ? courses.filter(c => c.week.includes(filter)) : courses
  return (
    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '.5rem' }}>
      <thead>
        <tr style={{ background: '#1e3a5f', color: 'white' }}>
          <th style={{ padding: '.5rem .8rem', textAlign: 'left' }}>Course</th>
          <th style={{ padding: '.5rem .8rem', textAlign: 'left' }}>Week</th>
          <th style={{ padding: '.5rem .8rem', textAlign: 'center' }}>Students</th>
        </tr>
      </thead>
      <tbody>
        {/* key prop is required — helps React identify which items changed */}
        {displayed.map(course => (
          <CourseRow key={course.id} course={course} />
        ))}
      </tbody>
    </table>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. CONDITIONAL RENDERING — 4 techniques
// ─────────────────────────────────────────────────────────────────────────────
function ConditionalDemo({ isLoggedIn, userRole, notifications }) {
  return (
    <div>
      {/* Technique 1: Ternary */}
      <p>Ternary: {isLoggedIn ? '✅ Welcome back!' : '🔒 Please log in'}</p>

      {/* Technique 2: && short-circuit */}
      {notifications > 0 && (
        <p>Short-circuit: 🔔 You have {notifications} notification{notifications > 1 ? 's' : ''}</p>
      )}

      {/* Technique 3: if/else in a function */}
      <p>Role badge: {renderRoleBadge(userRole)}</p>

      {/* Technique 4: Null (render nothing) */}
      {userRole === 'banned' && null /* renders nothing */}
    </div>
  )
}

function renderRoleBadge(role) {
  if (role === 'admin')   return <span style={styles.badge('#e06c1b')}>Admin</span>
  if (role === 'student') return <span style={styles.badge('#1e3a5f')}>Student</span>
  return <span style={styles.badge('#888')}>Guest</span>
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. COMPOSITION — children prop + slot pattern
// ─────────────────────────────────────────────────────────────────────────────
function Panel({ title, children, accent = '#1e3a5f' }) {
  // children is a special prop — whatever is between open and close tags
  return (
    <div style={{
      border: `2px solid ${accent}`, borderRadius: 8, overflow: 'hidden', marginBottom: '1rem'
    }}>
      <div style={{ background: accent, color: 'white', padding: '.5rem 1rem' }}>
        <strong>{title}</strong>
      </div>
      <div style={{ padding: '1rem' }}>{children}</div>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
// ROOT APP
// ─────────────────────────────────────────────────────────────────────────────
const students = [
  { id: 1, name: 'Alice',    cohort: 'Spring 2024', score: 93, avatar: '👩‍💻' },
  { id: 2, name: 'Bob',      cohort: 'Spring 2024', score: 82, avatar: '👨‍💻' },
  { id: 3, name: 'Carol',    cohort: 'Spring 2024', score: 75, avatar: '👩‍🎓' },
  { id: 4, name: 'David',    cohort: 'Spring 2024', score: 96, avatar: '👨‍🎓' },
  { id: 5, name: 'Eve',      cohort: 'Spring 2024', score: 88, avatar: '🧑‍💻', enrolled: false },
]

export default function App() {
  return (
    <div style={styles.page}>
      <div style={styles.header}>
        <h1>⚛️ Day 16a Part 2 — Props, Lists &amp; Conditional Rendering</h1>
        <p style={{ color: '#b0c8e8', marginTop: '.3rem' }}>npm install &amp;&amp; npm run dev</p>
      </div>

      {/* 1. Props */}
      <div style={styles.card}>
        <h2 style={styles.h2}>1. Props — Passing Data to Components</h2>
        <p style={{ marginBottom: '1rem', color: '#555' }}>
          Each <code>StudentCard</code> gets different data via props — same component, different output.
        </p>
        {students.map(s => (
          <StudentCard key={s.id} {...s} />  // spread operator passes all properties as props
        ))}
        <p style={{ marginTop: '.5rem', fontSize: '.85rem', color: '#888' }}>
          Props flow one-way: parent → child (unidirectional data flow)
        </p>
      </div>

      {/* 2. Lists & Keys */}
      <div style={styles.card}>
        <h2 style={styles.h2}>2. Lists &amp; Keys</h2>
        <p style={{ marginBottom: '.5rem', color: '#555' }}>
          Use <code>.map()</code> to render lists. Always provide a unique <code>key</code> prop.
        </p>
        <CourseTable courses={courses} />
        <p style={{ marginTop: '.5rem', fontSize: '.85rem', color: '#888' }}>
          Filtered (Week 3 only): <CourseTable courses={courses} filter="Week 3" />
        </p>
      </div>

      {/* 3. Conditional Rendering */}
      <div style={styles.card}>
        <h2 style={styles.h2}>3. Conditional Rendering</h2>
        <ConditionalDemo isLoggedIn={true} userRole="admin" notifications={3} />
        <hr style={{ margin: '.8rem 0', borderColor: '#eee' }} />
        <ConditionalDemo isLoggedIn={false} userRole="student" notifications={0} />
      </div>

      {/* 4. Composition */}
      <div style={styles.card}>
        <h2 style={styles.h2}>4. Component Composition — children prop</h2>
        <Panel title="📋 Student Notes" accent="#1e3a5f">
          <p>This content is passed as <code>children</code> to the Panel component.</p>
          <p>Any JSX between &lt;Panel&gt; tags becomes <code>props.children</code>.</p>
        </Panel>
        <Panel title="⚠️ Important" accent="#e06c1b">
          <strong>Keys must be unique among siblings</strong> — not globally unique.
        </Panel>
      </div>
    </div>
  )
}
