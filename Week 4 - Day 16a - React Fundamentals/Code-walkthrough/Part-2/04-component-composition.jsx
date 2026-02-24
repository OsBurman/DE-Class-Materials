// ============================================================
// Day 16a — React Fundamentals
// File: Component Composition
// ============================================================
// Composition = combining simple components to build complex UIs.
// React strongly favors COMPOSITION over inheritance.
// The `children` prop is the key tool for flexible composition.
// ============================================================

import React from 'react';

// ─── SECTION 1: THE `children` PROP ─────────────────────────
//
// When you write content BETWEEN opening and closing tags of a
// component, React automatically passes it as `props.children`.
//
//   <Card>
//     <h2>Title</h2>    ← these become props.children inside Card
//     <p>Body text</p>
//   </Card>
//
// This makes components act like flexible HTML containers.
// The parent decides the "shell", children decide the "content".
//

// A generic Card container — doesn't care what goes inside
function Card({ children, className = '' }) {
  return (
    <div className={`card ${className}`} style={{
      border: '1px solid #e0e0e0',
      borderRadius: '8px',
      padding: '16px',
      marginBottom: '16px',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    }}>
      {children}
    </div>
  );
}

// Using Card with completely different content each time:
function CardDemo() {
  return (
    <div>
      {/* Card used as a course preview */}
      <Card>
        <h2>React Fundamentals</h2>
        <p>Learn the core concepts of React in one day.</p>
        <button>Enroll Now</button>
      </Card>

      {/* The same Card component used as a profile snippet */}
      <Card className="profile-card">
        <img src="/avatar.png" alt="Profile" />
        <h3>Jane Smith</h3>
        <p>Full Stack Developer</p>
      </Card>

      {/* Same Card as a warning box */}
      <Card className="warning-card">
        <strong>⚠️ Warning</strong>
        <p>Your trial expires in 3 days.</p>
      </Card>
    </div>
  );
}

// ─── SECTION 2: LAYOUT COMPONENTS ───────────────────────────
//
// Layout components control HOW their children are arranged
// without caring WHAT those children are.

function TwoColumnLayout({ leftContent, rightContent }) {
  return (
    <div style={{ display: 'flex', gap: '24px' }}>
      <aside style={{ flex: '0 0 300px' }}>
        {leftContent}
      </aside>
      <main style={{ flex: 1 }}>
        {rightContent}
      </main>
    </div>
  );
}

// Using TwoColumnLayout — pass JSX as prop values:
function CoursePage() {
  return (
    <TwoColumnLayout
      leftContent={
        <div>
          <h3>Course Outline</h3>
          <ul>
            <li>Part 1: Foundations</li>
            <li>Part 2: Components</li>
            <li>Part 3: Hooks</li>
          </ul>
        </div>
      }
      rightContent={
        <div>
          <h1>React Fundamentals</h1>
          <p>A comprehensive introduction to modern React development.</p>
          <p>Duration: 6 hours | Level: Beginner | Rating: ⭐ 4.8</p>
          <button>Start Learning</button>
        </div>
      }
    />
  );
}

// ─── SECTION 3: WRAPPER / DECORATOR COMPONENTS ──────────────
//
// Wrapper components add behavior or style around any child.

// Adds a loading overlay when `isLoading` is true
function LoadingWrapper({ isLoading, children }) {
  if (isLoading) {
    return (
      <div style={{ position: 'relative', minHeight: '100px' }}>
        <div style={{
          position: 'absolute', inset: 0,
          background: 'rgba(255,255,255,0.8)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          ⏳ Loading...
        </div>
        {children}
      </div>
    );
  }
  return children;
}

// Adds a required login gate
function ProtectedSection({ isLoggedIn, children }) {
  if (!isLoggedIn) {
    return (
      <div className="login-prompt">
        <p>🔒 Please log in to view this content.</p>
        <button>Sign In</button>
      </div>
    );
  }
  return children;
}

// ─── SECTION 4: COMPOSING MULTIPLE COMPONENTS ────────────────
//
// Real apps are built by nesting components inside components.
// Each component has ONE responsibility.

// Atomic components (no children dependency):
function Badge({ text, color = '#0066cc' }) {
  return (
    <span style={{
      background: color, color: 'white', padding: '2px 8px',
      borderRadius: '12px', fontSize: '12px', fontWeight: 'bold',
    }}>
      {text}
    </span>
  );
}

function StarRating({ rating, maxStars = 5 }) {
  return (
    <span aria-label={`${rating} out of ${maxStars} stars`}>
      {Array.from({ length: maxStars }, (_, i) => (
        <span key={i} style={{ color: i < Math.round(rating) ? '#f5a623' : '#ccc' }}>
          ★
        </span>
      ))}
      <span style={{ marginLeft: '4px', fontSize: '14px' }}>({rating})</span>
    </span>
  );
}

function InstructorAvatar({ name, avatarUrl }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
      <img
        src={avatarUrl || '/default-avatar.png'}
        alt={name}
        style={{ width: 32, height: 32, borderRadius: '50%' }}
      />
      <span style={{ fontSize: '14px', color: '#555' }}>{name}</span>
    </div>
  );
}

// Molecule component: composed from atomic components
function CourseCard({ course }) {
  return (
    <Card>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <h3 style={{ margin: 0 }}>{course.title}</h3>
        {course.isNew && <Badge text="NEW" color="#22c55e" />}
        {course.isBestseller && <Badge text="BESTSELLER" color="#f59e0b" />}
      </div>

      <InstructorAvatar name={course.instructorName} avatarUrl={course.instructorAvatar} />
      <StarRating rating={course.rating} />

      <div style={{ display: 'flex', gap: '8px', marginTop: '8px', flexWrap: 'wrap' }}>
        {course.tags.map((tag) => (
          <Badge key={tag} text={`#${tag}`} color="#6366f1" />
        ))}
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '12px' }}>
        <strong>${course.price.toFixed(2)}</strong>
        <button>Enroll</button>
      </div>
    </Card>
  );
}

// Organism: composed from molecules and atoms
function CourseCatalog({ courses, isLoading, isLoggedIn }) {
  return (
    <ProtectedSection isLoggedIn={isLoggedIn}>
      <LoadingWrapper isLoading={isLoading}>
        <section className="catalog">
          <h2>Featured Courses</h2>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '16px' }}>
            {courses.map((course) => (
              <CourseCard key={course.id} course={course} />
            ))}
          </div>
        </section>
      </LoadingWrapper>
    </ProtectedSection>
  );
}

// Demo data to show how CourseCatalog renders
const sampleCourses = [
  {
    id: 'c1', title: 'React Fundamentals', rating: 4.8, price: 49.99,
    isNew: true, isBestseller: false,
    instructorName: 'Jane Smith', instructorAvatar: '/jane.png',
    tags: ['react', 'javascript'],
  },
  {
    id: 'c2', title: 'Spring Boot Mastery', rating: 4.9, price: 59.99,
    isNew: false, isBestseller: true,
    instructorName: 'Bob Lee', instructorAvatar: '/bob.png',
    tags: ['java', 'spring', 'backend'],
  },
];

function AppDemo() {
  return (
    <CourseCatalog
      courses={sampleCourses}
      isLoading={false}
      isLoggedIn={true}
    />
  );
}

// ─── SECTION 5: COMPOSITION vs INHERITANCE ──────────────────
//
// Object-oriented languages use class INHERITANCE to reuse code:
//   class SpecialButton extends Button { ... }
//
// React recommends COMPOSITION instead:
//   - Don't subclass components
//   - Instead, accept children or specific props
//   - Build complex UIs by combining simple ones
//
// Why? Inheritance creates tight coupling. If Button changes,
// SpecialButton breaks. Composition stays flexible — swap out
// any piece without affecting others.
//
// The React team statement:
//   "We haven't found any use cases where we would recommend
//    creating component inheritance hierarchies."
//

export { Card, TwoColumnLayout, LoadingWrapper, ProtectedSection, CourseCard, CourseCatalog, AppDemo };
