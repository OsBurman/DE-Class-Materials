// ============================================================
// Day 16a — React Fundamentals
// File: Props and Data Flow
// ============================================================
// Props (short for "properties") are how you pass data FROM a
// parent component TO a child component.
// Props make components dynamic and reusable.
// ============================================================

import React from 'react';

// ─── SECTION 1: WHAT ARE PROPS? ──────────────────────────────
//
// Without props, every CourseCard would show the same data.
// Props let you configure a component differently each time you use it.
//
// Rule: Props flow ONE WAY — parent → child (never child → parent)
// Rule: Props are READ-ONLY inside the receiving component
//       NEVER modify a prop. Treat props like function arguments.
//

// ─── SECTION 2: PASSING PROPS (from the parent) ──────────────
//
// Props are passed like HTML attributes:
//   <CourseCard title="React Fundamentals" duration="6 hours" />
//
// Any JavaScript value can be passed as a prop:
//   String:   title="React Fundamentals"
//   Number:   rating={4.8}             ← use {} for non-strings
//   Boolean:  isNew={true}             ← or just: isNew (shorthand for true)
//   Object:   instructor={{ name: "Jane", id: 1 }}
//   Array:    tags={["react", "js"]}
//   Function: onEnroll={handleEnroll}
//

// ─── SECTION 3: RECEIVING PROPS (in the child) ───────────────

// Pattern A: `props` object parameter
function CourseCardBasic(props) {
  return (
    <div className="course-card">
      <h3>{props.title}</h3>
      <p>Duration: {props.duration}</p>
      <p>Rating: ⭐ {props.rating} / 5.0</p>
    </div>
  );
}

// Pattern B: Destructured props (most common — cleaner)
function CourseCard({ title, duration, rating, isNew, tags }) {
  return (
    <div className="course-card">
      {isNew && <span className="badge">NEW</span>}
      <h3>{title}</h3>
      <p>⏱ {duration}</p>
      <p>⭐ {rating} / 5.0</p>
      <div className="tags">
        {tags.map((tag) => (
          <span key={tag} className="tag">#{tag}</span>
        ))}
      </div>
    </div>
  );
}

// Usage — passing different data to the same component:
function CourseList() {
  return (
    <div>
      <CourseCard
        title="React Fundamentals"
        duration="6 hours"
        rating={4.8}
        isNew={true}
        tags={['react', 'javascript', 'frontend']}
      />
      <CourseCard
        title="Spring Boot Mastery"
        duration="10 hours"
        rating={4.9}
        isNew={false}
        tags={['java', 'spring', 'backend']}
      />
      <CourseCard
        title="TypeScript Deep Dive"
        duration="8 hours"
        rating={4.7}
        isNew={true}
        tags={['typescript', 'javascript']}
      />
    </div>
  );
}

// ─── SECTION 4: DEFAULT PROPS ────────────────────────────────
//
// Use default parameter values to provide fallback values
// when a prop is not passed.

function Avatar({ name, imageUrl = '/default-avatar.png', size = 40 }) {
  return (
    <div className="avatar" style={{ width: size, height: size }}>
      <img
        src={imageUrl}
        alt={`${name}'s avatar`}
        style={{ width: '100%', borderRadius: '50%' }}
      />
      <span>{name}</span>
    </div>
  );
}

// Using Avatar with and without optional props:
function TeamSection() {
  return (
    <div>
      {/* Both optional props provided */}
      <Avatar name="Alice" imageUrl="/alice.png" size={60} />

      {/* size defaults to 40, imageUrl defaults to '/default-avatar.png' */}
      <Avatar name="Bob" />

      {/* Only size overridden */}
      <Avatar name="Carlos" size={80} />
    </div>
  );
}

// ─── SECTION 5: PASSING OBJECTS AS PROPS ─────────────────────
//
// When a component needs many related pieces of data,
// pass an object instead of many individual props.

function UserProfileCard({ user }) {
  return (
    <div className="profile-card">
      <Avatar name={user.name} imageUrl={user.avatarUrl} size={50} />
      <div className="profile-details">
        <h3>{user.name}</h3>
        <p>📧 {user.email}</p>
        <p>📍 {user.location}</p>
        <p>🏢 {user.company}</p>
        <p>👤 Member since {user.joinYear}</p>
      </div>
    </div>
  );
}

// Using the spread operator to pass all properties of an object as props:
function SpreadExample() {
  const user = {
    name: 'Dana Lee',
    email: 'dana@example.com',
    location: 'San Francisco',
    company: 'TechCorp',
    joinYear: 2022,
    avatarUrl: '/dana.png',
  };

  return (
    <div>
      {/* Option A: pass the object directly */}
      <UserProfileCard user={user} />

      {/* Option B: spread each property individually (useful for wrappers) */}
      {/* <SomeComponent {...user} /> */}
    </div>
  );
}

// ─── SECTION 6: PASSING FUNCTIONS AS PROPS ───────────────────
//
// This is how child components communicate back UP to parents.
// The parent defines a function and passes it as a prop.
// The child calls the function when something happens.
// This is called "lifting state up" — covered more in Day 17a.

function LikeButton({ courseTitle, onLike }) {
  return (
    <button
      className="like-btn"
      onClick={() => onLike(courseTitle)}
    >
      ❤️ Like "{courseTitle}"
    </button>
  );
}

function CourseWithLike() {
  // The parent defines WHAT happens when like is clicked
  function handleLike(title) {
    alert(`You liked: ${title}`);
    // In a real app: update state, call an API, etc.
  }

  return (
    <div>
      <h3>React Fundamentals</h3>
      {/* Pass the function as a prop — no () at the end! */}
      <LikeButton courseTitle="React Fundamentals" onLike={handleLike} />
    </div>
  );
}

// ─── SECTION 7: ONE-WAY DATA FLOW DIAGRAM ────────────────────
//
//    App (owns data)
//     │
//     ├── NavBar  (receives: user from props)
//     │
//     ├── CourseList  (receives: courses from props)
//     │    │
//     │    ├── CourseCard  (receives: title, rating from props)
//     │    ├── CourseCard  (receives: title, rating from props)
//     │    └── CourseCard  (receives: title, rating from props)
//     │
//     └── Footer  (receives: nothing — no props needed)
//
// Data always flows DOWN the tree (parent → child).
// To send data UP, pass a callback function as a prop.
// This "one-way data flow" makes bugs easier to trace —
// you always know where data came from.
//

export { CourseCard, CourseList, Avatar, UserProfileCard, LikeButton, CourseWithLike };
