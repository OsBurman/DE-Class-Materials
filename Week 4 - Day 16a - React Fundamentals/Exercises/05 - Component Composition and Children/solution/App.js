// Exercise 05 Solution: Component Composition and Children

// ─── Building blocks ──────────────────────────────────────────────────────────

// Card — a reusable wrapper that renders any children inside a styled box.
// props.children is whatever JSX is placed between <Card> and </Card>.
function Card({ title, children }) {
  return (
    <div className="card">
      <h3 className="card-title">{title}</h3>
      <div className="card-body">{children}</div>
    </div>
  );
}

// Sidebar — renders a dark nav panel from a links array.
// Uses the link string itself as the key (acceptable when the list is static).
function Sidebar({ links }) {
  return (
    <nav className="sidebar">
      <ul>
        {links.map((link) => (
          <li key={link}>• {link}</li>
        ))}
      </ul>
    </nav>
  );
}

// MainContent — a simple layout wrapper; passes children through.
function MainContent({ children }) {
  return <main className="main-content">{children}</main>;
}

// PageLayout — composes sidebar + main area.
// The sidebar is passed as a JSX prop (not children) so the parent controls it.
function PageLayout({ sidebar, children }) {
  return (
    <div className="page-layout">
      {sidebar}
      <MainContent>{children}</MainContent>
    </div>
  );
}

// ─── App ───────────────────────────────────────────────────────────────────────
const navLinks = ["Dashboard", "Profile", "Settings", "Help"];

function App() {
  return (
    <div>
      <h1 style={{ maxWidth: 900, margin: "0 auto 1rem" }}>My App</h1>

      {/* PageLayout receives the Sidebar as a JSX prop and Card components as children */}
      <PageLayout sidebar={<Sidebar links={navLinks} />}>

        <Card title="Welcome">
          <p>Hello! This content is passed as <code>props.children</code> to the Card.</p>
          <p>Any JSX between the opening and closing Card tags ends up here.</p>
        </Card>

        <Card title="About React Composition">
          <p>React favours <strong>composition</strong> over inheritance.</p>
          <ul>
            <li>Build small, focused components</li>
            <li>Nest them inside one another</li>
            <li>Use <code>children</code> for flexible slots</li>
          </ul>
        </Card>

        <Card title="Stats">
          <p>Components built today: <strong>6</strong></p>
          <p>Exercises remaining: <strong>0</strong></p>
        </Card>

      </PageLayout>
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);

// ─────────────────────────────────────────────────────────────────────────────
// READING: Class Component vs Functional Component
//
// The Card component above written as a CLASS COMPONENT (pre-hooks style):
//
//   class Card extends React.Component {
//     render() {
//       const { title, children } = this.props;  // props via this.props
//       return (
//         <div className="card">
//           <h3 className="card-title">{title}</h3>
//           <div className="card-body">{children}</div>
//         </div>
//       );
//     }
//   }
//
// Key differences:
//   • Must extend React.Component
//   • Logic lives in a render() method
//   • Props are accessed via this.props (not a function argument)
//   • State is managed with this.state / this.setState()
//   • Lifecycle methods: componentDidMount, componentDidUpdate, componentWillUnmount
//
// Modern React (2019+) uses FUNCTIONAL components + Hooks for everything.
// Class components still work and you will encounter them in legacy codebases.
// ─────────────────────────────────────────────────────────────────────────────
