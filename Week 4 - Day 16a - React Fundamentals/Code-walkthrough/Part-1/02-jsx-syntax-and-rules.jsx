// ============================================================
// Day 16a — React Fundamentals
// File 2: JSX Syntax and Rules
// ============================================================
// JSX = JavaScript XML
// It looks like HTML inside JavaScript, but it is NOT HTML.
// Babel/Vite transpiles JSX into React.createElement() calls.
// ============================================================

import React from 'react';

// ─── SECTION 1: JSX IS SYNTACTIC SUGAR ───────────────────────
//
// This JSX:
//   const element = <h1 className="title">Hello, world!</h1>;
//
// Gets compiled to:
//   const element = React.createElement(
//     'h1',
//     { className: 'title' },
//     'Hello, world!'
//   );
//
// You never call React.createElement directly — JSX does it for you.
// But understanding this explains WHY JSX has certain rules.
//

// ─── SECTION 2: JSX BASIC SYNTAX ─────────────────────────────

// Valid JSX — looks like HTML
const greeting = <h1>Hello, React!</h1>;

// JSX can span multiple lines — wrap in parentheses to avoid
// JavaScript's automatic semicolon insertion (ASI) trapping you
const card = (
  <div className="card">
    <h2>Card Title</h2>
    <p>Card body text goes here.</p>
  </div>
);

// ─── SECTION 3: RULE 1 — ONE ROOT ELEMENT ────────────────────
//
// ❌ INVALID — two siblings at the top level:
//   const bad = (
//     <h1>Title</h1>
//     <p>Paragraph</p>
//   );
//
// ✅ OPTION A — Wrap in a real container:
const withDiv = (
  <div>
    <h1>Title</h1>
    <p>Paragraph</p>
  </div>
);

// ✅ OPTION B — Use a Fragment (renders no extra DOM node)
// Fragment long form:
const withFragment = (
  <React.Fragment>
    <h1>Title</h1>
    <p>Paragraph</p>
  </React.Fragment>
);

// Fragment shorthand (most common):
const withShortFragment = (
  <>
    <h1>Title</h1>
    <p>Paragraph</p>
  </>
);

// ─── SECTION 4: RULE 2 — className, NOT class ────────────────
//
// `class` is a reserved word in JavaScript (for ES6 classes).
// In JSX, use `className` for CSS classes.

const styledButton = (
  <button className="btn btn-primary">Click Me</button>
);

// Similarly, `for` (label attribute) becomes `htmlFor`:
const labelExample = (
  <label htmlFor="email">Email Address</label>
);
//   Note: <label for="email"> would still work in the browser but
//         React will warn you in the console. Always use htmlFor.

// ─── SECTION 5: RULE 3 — SELF-CLOSING TAGS ───────────────────
//
// In HTML, some tags don't need a closing tag: <br>, <img>, <input>
// In JSX, ALL tags MUST be closed — either with a closing tag or self-closing />.

// ❌ INVALID in JSX:
//   <br>
//   <img src="photo.jpg">
//   <input type="text">

// ✅ VALID — self-closing:
const selfClosingExamples = (
  <div>
    <br />
    <img src="photo.jpg" alt="A photo" />
    <input type="text" placeholder="Enter name" />
    <hr />
  </div>
);

// ─── SECTION 6: RULE 4 — JAVASCRIPT EXPRESSIONS IN JSX ───────
//
// Use curly braces {} to embed ANY JavaScript EXPRESSION in JSX.
// An expression is anything that produces a value.

const userName = 'Alex';
const itemCount = 5;
const price = 9.99;

const expressionExamples = (
  <div>
    {/* String variable */}
    <h2>Welcome, {userName}!</h2>

    {/* Arithmetic */}
    <p>You have {itemCount} items in your cart.</p>

    {/* Template string */}
    <p>Total: ${(itemCount * price).toFixed(2)}</p>

    {/* Ternary expression (covered more in Part 2 conditionals) */}
    <p>Status: {itemCount > 0 ? 'Has items' : 'Empty cart'}</p>

    {/* Function call */}
    <p>Today: {new Date().toLocaleDateString()}</p>
  </div>
);

// ─── SECTION 7: JSX COMMENTS ─────────────────────────────────
//
// In JSX, comments use JavaScript block syntax inside curly braces:
//   {/* This is a JSX comment */}
//
// Regular HTML comments <!-- --> do NOT work in JSX.
// Regular JS comments // only work OUTSIDE JSX tags.

const commentExample = (
  <div>
    {/* This comment is invisible in the rendered output */}
    <p>Visible paragraph</p>
    {/* Another comment */}
  </div>
);

// ─── SECTION 8: JSX ATTRIBUTES ───────────────────────────────
//
// String attributes: use quotes (just like HTML)
//   <img src="photo.jpg" alt="Profile" />
//
// Dynamic attributes: use curly braces (NOT quotes + curly braces)

const avatarUrl = 'https://example.com/avatar.png';
const altText = 'User avatar';

// ❌ WRONG — mixing quotes and curly braces:
//   <img src="{avatarUrl}" />   ← renders the literal string "{avatarUrl}"

// ✅ CORRECT — curly braces only for dynamic values:
const dynamicAttr = <img src={avatarUrl} alt={altText} />;

// Style attribute: takes a JavaScript OBJECT (not a CSS string)
// Property names are camelCase in JSX style objects

// ❌ WRONG:
//   <div style="background-color: blue; font-size: 16px;">

// ✅ CORRECT:
const styledDiv = (
  <div style={{ backgroundColor: 'blue', fontSize: '16px', padding: '20px' }}>
    Styled with inline JSX styles
  </div>
);
// Note: double curly braces {{}} — outer {} = JSX expression,
//                                  inner {} = JavaScript object literal

// ─── SECTION 9: JSX IS AN EXPRESSION ─────────────────────────
//
// JSX expressions can be stored in variables, returned from functions,
// passed as arguments, and used inside other JSX.

function getGreeting(isLoggedIn) {
  // JSX in an if/else — assigned to a variable
  let message;
  if (isLoggedIn) {
    message = <span>Welcome back!</span>;
  } else {
    message = <span>Please sign in.</span>;
  }
  return <div className="greeting">{message}</div>;
}

// ─── SECTION 10: WHAT JSX CANNOT DO ─────────────────────────
//
// JSX curly braces accept EXPRESSIONS, not STATEMENTS.
//
// ❌ CANNOT use `if` directly in JSX:
//   <div>{ if (x > 0) { 'positive' } }</div>   ← syntax error
//
// ❌ CANNOT use `for` loops directly in JSX:
//   <div>{ for (let i=0; i<3; i++) { <p>{i}</p> } }</div>   ← invalid
//
// ✅ Solutions:
//   - Use ternary or && for conditionals (Part 2)
//   - Use .map() for loops (Part 2)
//   - Pre-compute in a variable before returning JSX

// ─── PUTTING IT ALL TOGETHER: A JSX Showcase Component ───────

function JsxShowcase() {
  const productName = 'TypeScript Fundamentals';
  const price = 49.99;
  const isAvailable = true;
  const tags = ['programming', 'web dev', 'beginner'];

  return (
    <article className="product-card" style={{ border: '1px solid #ccc', padding: '16px' }}>
      {/* Header */}
      <h2>{productName}</h2>

      {/* Dynamic attribute */}
      <img
        src={`https://example.com/covers/${productName.toLowerCase().replace(/ /g, '-')}.jpg`}
        alt={`Cover of ${productName}`}
      />

      {/* Arithmetic in JSX */}
      <p className="price">Price: ${price.toFixed(2)}</p>

      {/* Ternary conditional */}
      <p className={isAvailable ? 'in-stock' : 'out-of-stock'}>
        {isAvailable ? '✅ In Stock' : '❌ Out of Stock'}
      </p>

      {/* Self-closing input */}
      <input type="number" min="1" max="10" defaultValue={1} />

      {/* Label with htmlFor */}
      <label htmlFor="qty">Quantity:</label>
      <input id="qty" type="number" />

      {/* Fragment to group two siblings without extra div */}
      <>
        <hr />
        <small>Tags: {tags.join(', ')}</small>
      </>
    </article>
  );
}

export default JsxShowcase;
