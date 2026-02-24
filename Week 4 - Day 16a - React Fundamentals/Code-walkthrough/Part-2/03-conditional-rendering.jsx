// ============================================================
// Day 16a — React Fundamentals
// File: Rendering & Conditional Rendering
// ============================================================
// Conditional rendering = showing or hiding parts of the UI
// based on conditions (state, props, or any JS expression).
// React has several patterns for this — each has its place.
// ============================================================

import React from 'react';

// ─── SECTION 1: REACT'S RENDERING RULES ─────────────────────
//
// React renders (displays) these things:
//   ✅ JSX elements:    <div>, <MyComponent />
//   ✅ Strings:         {'Hello'}
//   ✅ Numbers:         {42}
//   ✅ Arrays:          {[<li/>, <li/>]}
//   ✅ null / undefined → renders NOTHING (useful for hiding)
//   ✅ false, true      → renders NOTHING (but ⚠️ 0 does render!)
//
//   ❌ Objects:         {myObject} → runtime error
//   ❌ Functions:       {myFunc}   → runtime error
//

// ─── SECTION 2: PATTERN 1 — IF / EARLY RETURN ───────────────
//
// Best for: entirely different UI paths, large blocks to show/hide

function UserDashboard({ user }) {
  // Early return: if no user, show nothing (or a fallback)
  if (!user) {
    return <p>Please log in to view your dashboard.</p>;
  }

  // If we get here, user is guaranteed to exist
  return (
    <div className="dashboard">
      <h2>Welcome back, {user.name}!</h2>
      <p>Email: {user.email}</p>
      <p>Last login: {user.lastLogin}</p>
    </div>
  );
}

// Early return for loading states — very common pattern
function ProfilePage({ isLoading, user }) {
  if (isLoading) {
    return (
      <div className="loading-spinner">
        <p>⏳ Loading profile...</p>
      </div>
    );
  }

  if (!user) {
    return <p>User not found.</p>;
  }

  return (
    <div className="profile">
      <h1>{user.name}</h1>
      <p>{user.bio}</p>
    </div>
  );
}

// ─── SECTION 3: PATTERN 2 — TERNARY OPERATOR ────────────────
//
// Best for: two-choice conditions inline in JSX
// Syntax: condition ? valueIfTrue : valueIfFalse

function ToggleButton({ isOn, onToggle }) {
  return (
    <button
      className={isOn ? 'btn-on' : 'btn-off'}
      onClick={onToggle}
    >
      {isOn ? '🟢 ON' : '🔴 OFF'}
    </button>
  );
}

function PricingTag({ price, isSale, originalPrice }) {
  return (
    <div className="pricing">
      {isSale ? (
        <span>
          <span className="original-price" style={{ textDecoration: 'line-through' }}>
            ${originalPrice}
          </span>
          <span className="sale-price" style={{ color: 'red', marginLeft: '8px' }}>
            ${price} SALE!
          </span>
        </span>
      ) : (
        <span className="regular-price">${price}</span>
      )}
    </div>
  );
}

// ─── SECTION 4: PATTERN 3 — LOGICAL AND (&&) ────────────────
//
// Best for: show something OR show nothing (no else branch)
// Syntax: condition && <JSX to show if condition is truthy>
//
// ⚠️ WARNING: If condition is 0 (number zero), React renders "0"!
//   items.length && <List />  ← if items.length is 0, renders "0" on screen!
//   Fix: convert to boolean: items.length > 0 && <List />

function NotificationBadge({ count }) {
  return (
    <div className="nav-icon">
      🔔
      {/* Only render the badge if count > 0 */}
      {count > 0 && (
        <span className="badge">{count > 99 ? '99+' : count}</span>
      )}
    </div>
  );
}

function AdminPanel({ user }) {
  return (
    <nav>
      <a href="/">Home</a>
      <a href="/profile">Profile</a>

      {/* Only show admin link if user has admin role */}
      {user.role === 'admin' && (
        <a href="/admin" className="admin-link">⚙️ Admin</a>
      )}

      {/* Only show "New" badge on recently joined accounts */}
      {user.daysOnPlatform < 7 && (
        <span className="new-user-badge">New Member 🎉</span>
      )}
    </nav>
  );
}

// ─── SECTION 5: PATTERN 4 — VARIABLE ASSIGNMENT ─────────────
//
// Best for: complex conditions, many branches, long JSX blocks
// Keeps the return statement clean.

function OrderStatusCard({ status, orderId }) {
  let statusContent;

  switch (status) {
    case 'pending':
      statusContent = (
        <div className="status pending">
          <span>🕐 Order Pending</span>
          <p>Your order #{orderId} is being processed.</p>
        </div>
      );
      break;
    case 'shipped':
      statusContent = (
        <div className="status shipped">
          <span>📦 Shipped</span>
          <p>Your order #{orderId} is on its way!</p>
        </div>
      );
      break;
    case 'delivered':
      statusContent = (
        <div className="status delivered">
          <span>✅ Delivered</span>
          <p>Your order #{orderId} has been delivered.</p>
        </div>
      );
      break;
    case 'cancelled':
      statusContent = (
        <div className="status cancelled">
          <span>❌ Cancelled</span>
          <p>Order #{orderId} was cancelled.</p>
        </div>
      );
      break;
    default:
      statusContent = <div className="status unknown">Unknown status</div>;
  }

  return (
    <div className="order-card">
      <h3>Order #{orderId}</h3>
      {statusContent}
    </div>
  );
}

// ─── SECTION 6: RETURNING NULL TO HIDE A COMPONENT ──────────
//
// A component can return null to render nothing.
// The component still mounts and can still have state/effects.
// Use this when the component itself should decide if it appears.

function ErrorBanner({ error }) {
  if (!error) return null; // Renders nothing — component is "invisible"

  return (
    <div className="error-banner" role="alert">
      <strong>⚠️ Error:</strong> {error.message}
      <button onClick={() => window.location.reload()}>Retry</button>
    </div>
  );
}

// ─── SECTION 7: COMBINING PATTERNS ───────────────────────────
//
// Real components often combine multiple patterns.
// Here's a realistic product page section:

function CourseEnrollmentSection({ course, currentUser }) {
  // Pattern: early return for missing data
  if (!course) return null;

  const isEnrolled = currentUser?.enrolledCourseIds?.includes(course.id);
  const isInstructor = currentUser?.id === course.instructorId;
  const isFree = course.price === 0;

  return (
    <aside className="enrollment-panel">
      <h2>{course.title}</h2>

      {/* Ternary: show price or FREE badge */}
      <div className="price">
        {isFree ? (
          <span className="free-badge">FREE</span>
        ) : (
          <span className="price-value">${course.price.toFixed(2)}</span>
        )}
      </div>

      {/* && : show rating only if it exists */}
      {course.rating && (
        <p>⭐ {course.rating} ({course.reviewCount} reviews)</p>
      )}

      {/* Variable: different button states */}
      {(() => {
        if (isInstructor) {
          return <button className="btn-edit">✏️ Edit Course</button>;
        }
        if (isEnrolled) {
          return <button className="btn-continue">▶️ Continue Learning</button>;
        }
        return <button className="btn-enroll">🎓 Enroll Now</button>;
      })()}

      {/* && : show discount notice if applicable */}
      {course.discountPercent > 0 && (
        <p className="discount-notice">
          🔥 {course.discountPercent}% off — offer ends soon!
        </p>
      )}
    </aside>
  );
}

export {
  UserDashboard,
  ProfilePage,
  ToggleButton,
  PricingTag,
  NotificationBadge,
  AdminPanel,
  OrderStatusCard,
  ErrorBanner,
  CourseEnrollmentSection,
};
