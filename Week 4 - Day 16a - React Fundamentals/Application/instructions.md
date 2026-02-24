# Day 16a Application — React Fundamentals: Dev Team Directory

## Overview

You're going to build a **Dev Team Directory** — a React single-page application that displays a roster of developers with their roles, skills, and availability status.

This project touches every concept from today's lesson:
- Creating and composing **functional components**
- Writing **JSX**
- Passing data with **props**
- Rendering **lists with keys**
- **Conditional rendering** based on prop values
- **Component composition** — assembling small components into a full UI

When finished, your app will look like a professional team page with cards for each developer, color-coded availability badges, and a skill tag list.

---

## Learning Goals

By completing this exercise you will demonstrate that you can:

- Scaffold a React app with Vite and understand the project structure
- Write functional components using JSX syntax
- Pass props from a parent component to a child component
- Render an array of data as a list of components using `.map()` and unique `key` props
- Use conditional rendering (`&&`, ternary `? :`) to show/hide UI elements
- Compose multiple small components into a complete page

---

## Prerequisites

- Node.js (v18+) installed — check with `node -v`
- npm installed — check with `npm -v`
- Basic HTML & CSS knowledge (Week 3)
- A code editor (VS Code recommended)

---

## Part 1 — Project Setup

### Step 1: Open the starter-code folder in your terminal

The project is already configured for you. Navigate into the `starter-code` folder:

```bash
cd starter-code
```

### Step 2: Install dependencies

```bash
npm install
```

This installs React, ReactDOM, and Vite from the pre-configured `package.json`.

### Step 3: Start the dev server

```bash
npm run dev
```

Open [http://localhost:5173](http://localhost:5173) in your browser. You should see a placeholder page — your job is to fill it in.

Your project structure looks like this:
```
starter-code/
├── index.html
├── package.json
├── vite.config.js
└── src/
    ├── main.jsx              ← entry point (do not edit)
    ├── App.jsx               ← TODO: wire up components here
    ├── App.css               ← styles (already complete)
    ├── components/
    │   ├── Header.jsx        ← already complete — use as a reference
    │   ├── TeamMemberCard.jsx  ← TODO: build this out
    │   └── TeamList.jsx      ← TODO: build this out
    └── data/
        └── teamMembers.js    ← data array (already complete)
```

---

## Part 2 — Build the `TeamMemberCard` Component

Open `src/components/TeamMemberCard.jsx`. This component is responsible for rendering a single developer's card.

### Your tasks:

**Task 1 — Destructure props**

The component receives these props:
| Prop | Type | Description |
|------|------|-------------|
| `name` | string | Developer's full name |
| `role` | string | Job title (e.g., "Frontend Developer") |
| `skills` | array of strings | List of technologies they know |
| `isAvailable` | boolean | Whether they're available for a project |
| `avatarUrl` | string | URL for their profile photo |

Destructure all five props from the function parameter.

**Task 2 — Render the card structure**

Build out the JSX to display:
- The developer's avatar image (use the `avatarUrl` prop for `src`, and `name` for `alt`)
- Their name in a heading
- Their role in a paragraph

**Task 3 — Conditional rendering for availability badge**

Below the role, render a badge showing availability:
- If `isAvailable` is `true` → show a green badge: `✅ Available`
- If `isAvailable` is `false` → show a gray badge: `🔒 On Project`

Use a **ternary operator** for this.

**Task 4 — Render the skills list**

Map over the `skills` array and render each skill as a `<span>` tag. Remember:
- Each `<span>` needs a unique `key` prop
- Use the skill string itself as the key (skills are unique per developer)

---

## Part 3 — Build the `TeamList` Component

Open `src/components/TeamList.jsx`. This component receives the full array of team members and renders a `TeamMemberCard` for each one.

### Your tasks:

**Task 5 — Receive and map over the `members` prop**

The component receives a single prop: `members` (an array of developer objects).

Use `.map()` to render a `<TeamMemberCard />` for each member. Pass all required props through. Use the developer's `id` field as the `key` prop.

**Task 6 — Conditional rendering for an empty state**

Before the list, add a check: if `members.length === 0`, render a message instead:
```
<p>No team members found.</p>
```

Use the `&&` short-circuit operator for this.

---

## Part 4 — Wire Up `App.jsx`

Open `src/App.jsx`. This is the root component — it imports data and composes all other components together.

### Your tasks:

**Task 7 — Import and use the data**

Import the `teamMembers` array from `./data/teamMembers`.

**Task 8 — Compose the components**

Render the following inside your `App` component's return:
1. The `<Header />` component (already built — just import and use it)
2. The `<TeamList />` component, passing `teamMembers` as the `members` prop

**Task 9 — Filter for available only (stretch)**

Add a button to the page that, when clicked, toggles between showing **all** developers and showing **only available** ones. You'll need `useState` for this — look ahead in the docs or ask your instructor!

---

## Part 5 — Style It (Optional but Recommended)

The `starter-code/App.css` file has a full set of styles already written — just make sure your JSX uses the correct class names listed below.

| Element | className |
|---------|-----------|
| App wrapper div | `app` |
| Cards grid | `team-grid` |
| Individual card | `card` |
| Avatar image | `card-avatar` |
| Availability badge (available) | `badge badge--available` |
| Availability badge (unavailable) | `badge badge--unavailable` |
| Skill tag span | `skill-tag` |

---

## Stretch Goals

1. **Add a search bar** — render an `<input>` that filters the displayed cards by name in real time.
2. **Sort by role** — add a `<select>` dropdown that lets users sort cards alphabetically by role.
3. **Add a "class component" version** — rewrite `TeamMemberCard` as a class component and compare the two approaches side-by-side.
4. **Create a `SkillBadge` component** — extract each skill `<span>` into its own component that accepts a `skill` prop.

---

## Submission Checklist

- [ ] App runs without errors in the browser
- [ ] At least 4 developer cards are rendered from the data array
- [ ] Each card displays: avatar, name, role, availability badge, and skills
- [ ] Availability badge uses conditional rendering (ternary)
- [ ] Skills are rendered with `.map()` and each has a unique `key` prop
- [ ] `TeamList` shows an empty-state message when the array is empty
- [ ] Components are in separate files and properly imported/exported
- [ ] No prop-drilling warnings in the console

---

## Key Concepts Reference

| Concept | Example |
|---------|---------|
| Functional component | `function MyComp() { return <div /> }` |
| Default export | `export default MyComp` |
| Named export | `export { MyComp }` |
| JSX expression | `<h1>{name}</h1>` |
| Passing a prop | `<Card name="Alice" />` |
| Receiving props | `function Card({ name }) { ... }` |
| Rendering a list | `items.map(i => <Li key={i.id} .../>)` |
| Ternary render | `{isOn ? <A /> : <B />}` |
| Short-circuit render | `{show && <Banner />}` |
