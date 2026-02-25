// Day 18a Part 1 — React Router: Navigation, Route Parameters, Nested Routes
// Run: npm install && npm run dev

import React, { useState } from 'react'
import {
  BrowserRouter, Routes, Route, Link, NavLink, Navigate,
  useParams, useNavigate, useLocation, Outlet
} from 'react-router-dom'

const primary = '#1e3a5f', accent = '#e06c1b'
const s = {
  nav:    { background: primary, padding: '.8rem 2rem', display: 'flex', gap: '1.5rem', alignItems: 'center' },
  navLink: (isActive) => ({ color: isActive ? accent : 'white', textDecoration: 'none', fontWeight: isActive ? 'bold' : 'normal', padding: '.3rem .6rem', borderRadius: 4, background: isActive ? 'rgba(255,255,255,.1)' : 'transparent' }),
  page:   { maxWidth: 960, margin: '0 auto', padding: '2rem 1rem' },
  card:   { background: 'white', borderRadius: 8, padding: '1.5rem', marginBottom: '1.5rem', boxShadow: '0 2px 8px rgba(0,0,0,.08)' },
  btn:    (c=primary) => ({ background: c, color: 'white', border: 'none', padding: '.4rem .9rem', borderRadius: 4, cursor: 'pointer', textDecoration: 'none', display: 'inline-block', margin: '.2rem' }),
}

// ── Data ─────────────────────────────────────────────────────────────────────
const courses = [
  { id: 1, name: 'Java Fundamentals', icon: '☕', weeks: 'Week 1-2', students: 42, desc: 'Core Java, OOP, Collections, Streams, Threading and design patterns.' },
  { id: 2, name: 'Frontend (HTML/CSS/JS)', icon: '🌐', weeks: 'Week 3-4', students: 40, desc: 'HTML5 semantic markup, CSS Flexbox/Grid, JavaScript ES6+, React, Angular.' },
  { id: 3, name: 'Spring Boot', icon: '🍃', weeks: 'Week 5-6', students: 35, desc: 'Spring MVC, Spring Data JPA, Spring Security, REST APIs and microservices.' },
  { id: 4, name: 'DevOps & Cloud', icon: '☁️', weeks: 'Week 7-8', students: 30, desc: 'Docker, Kubernetes, CI/CD pipelines, AWS services and deployment strategies.' },
]

// ── Pages ─────────────────────────────────────────────────────────────────────
function Home() {
  const navigate = useNavigate()
  return (
    <div style={s.page}>
      <h1 style={{ color: primary }}>🏠 Home</h1>
      <p style={{ margin: '1rem 0', color: '#555' }}>Welcome to the Academy Course Portal — built with React Router v6</p>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2,1fr)', gap: '1rem' }}>
        {courses.map(c => (
          <div key={c.id} style={s.card} onClick={() => navigate(`/courses/${c.id}`)} style={{ ...s.card, cursor: 'pointer' }}>
            <div style={{ fontSize: '1.5rem' }}>{c.icon} <strong>{c.name}</strong></div>
            <small style={{ color: '#888' }}>{c.weeks} · {c.students} students</small>
          </div>
        ))}
      </div>
    </div>
  )
}

function CourseList() {
  return (
    <div style={s.page}>
      <h1 style={{ color: primary }}>📚 Courses</h1>
      <ul style={{ listStyle: 'none', padding: 0, marginTop: '1rem' }}>
        {courses.map(c => (
          <li key={c.id} style={{ ...s.card, display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <span style={{ fontSize: '2rem' }}>{c.icon}</span>
            <div style={{ flex: 1 }}>
              <strong>{c.name}</strong> <small style={{ color: '#888' }}>({c.weeks})</small>
              <br /><small style={{ color: '#555' }}>{c.desc.slice(0, 60)}…</small>
            </div>
            <Link to={`/courses/${c.id}`} style={s.btn()}>View →</Link>
          </li>
        ))}
      </ul>
    </div>
  )
}

function CourseDetail() {
  const { id } = useParams()                    // ← useParams reads :id from URL
  const navigate = useNavigate()
  const course = courses.find(c => c.id === Number(id))

  if (!course) return <Navigate to="/not-found" replace />   // redirect if not found

  return (
    <div style={s.page}>
      <button onClick={() => navigate(-1)} style={s.btn()}>← Back</button>
      <div style={{ ...s.card, marginTop: '1rem' }}>
        <h2 style={{ color: primary }}>{course.icon} {course.name}</h2>
        <p style={{ color: '#888', margin: '.5rem 0' }}>{course.weeks} · {course.students} students enrolled</p>
        <p>{course.desc}</p>
        <div style={{ marginTop: '1rem' }}>
          <Link to={`/courses/${course.id}/syllabus`} style={s.btn()}>📋 Syllabus</Link>
          <Link to={`/courses/${course.id}/students`} style={s.btn(accent)}>👥 Students</Link>
        </div>
        <Outlet />  {/* ← nested routes render here */}
      </div>
    </div>
  )
}

// Nested routes
function Syllabus() {
  const { id } = useParams()
  return <div style={{ marginTop: '1rem', background: '#f0f4ff', padding: '1rem', borderRadius: 6 }}>
    <strong>📋 Syllabus for course {id}</strong>
    <p style={{ marginTop: '.5rem', color: '#555' }}>Topics: Fundamentals → Advanced → Projects → Assessment</p>
  </div>
}

function Students() {
  const { id } = useParams()
  const names = ['Alice', 'Bob', 'Carol', 'David', 'Eve']
  return <div style={{ marginTop: '1rem', background: '#e8f5e9', padding: '1rem', borderRadius: 6 }}>
    <strong>👥 Students in course {id}</strong>
    <div style={{ marginTop: '.5rem', display: 'flex', gap: '.5rem', flexWrap: 'wrap' }}>
      {names.map(n => <span key={n} style={{ background: '#1e3a5f', color: 'white', padding: '2px 10px', borderRadius: 12 }}>{n}</span>)}
    </div>
  </div>
}

function About() {
  const location = useLocation()         // ← useLocation gives current URL info
  return (
    <div style={s.page}>
      <h1 style={{ color: primary }}>ℹ️ About</h1>
      <div style={s.card}>
        <strong>Current location.pathname:</strong> <code>{location.pathname}</code>
        <p style={{ marginTop: '.8rem' }}>React Router provides client-side navigation without page reloads.</p>
        <ul style={{ marginTop: '.5rem', color: '#555', lineHeight: 2 }}>
          <li><code>BrowserRouter</code> — wraps the app, provides routing context</li>
          <li><code>Routes / Route</code> — defines path-to-component mapping</li>
          <li><code>Link / NavLink</code> — client-side navigation (no reload)</li>
          <li><code>useParams()</code> — reads URL parameters (:id)</li>
          <li><code>useNavigate()</code> — programmatic navigation</li>
          <li><code>useLocation()</code> — current URL info</li>
          <li><code>Outlet</code> — renders nested child routes</li>
        </ul>
      </div>
    </div>
  )
}

function NotFound() {
  return <div style={{ ...s.page, textAlign: 'center', padding: '4rem' }}>
    <div style={{ fontSize: '4rem' }}>🔍</div>
    <h2 style={{ color: primary }}>404 — Page Not Found</h2>
    <Link to="/" style={s.btn()}>Go Home</Link>
  </div>
}

// ── Navigation ─────────────────────────────────────────────────────────────────
function Nav() {
  return (
    <nav style={s.nav}>
      <span style={{ color: 'white', fontWeight: 'bold', marginRight: '1rem' }}>⚛️ Academy</span>
      {[['/', 'Home'], ['/courses', 'Courses'], ['/about', 'About']].map(([path, label]) => (
        <NavLink key={path} to={path} end style={({ isActive }) => s.navLink(isActive)}>{label}</NavLink>
      ))}
    </nav>
  )
}

// ── Root App ───────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <BrowserRouter>
      <Nav />
      <Routes>
        <Route path="/"             element={<Home />} />
        <Route path="/courses"      element={<CourseList />} />
        <Route path="/courses/:id"  element={<CourseDetail />}>
          {/* Nested routes */}
          <Route path="syllabus" element={<Syllabus />} />
          <Route path="students" element={<Students />} />
        </Route>
        <Route path="/about"        element={<About />} />
        <Route path="*"             element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  )
}
