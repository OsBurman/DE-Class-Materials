// Exercise 05: Component Composition and Children

// TODO 1: Create a Card component that accepts title and children.
//         Render:
//           <div className="card">
//             <h3 className="card-title">{title}</h3>
//             <div className="card-body">{children}</div>
//           </div>
//         Any JSX placed between <Card> and </Card> will appear in card-body.
function Card({ title, children }) {
  return (
    <div className="card">
      <h3 className="card-title">{title}</h3>
      {/* TODO: render children inside card-body */}
      <div className="card-body"></div>
    </div>
  );
}

// TODO 2: Create a Sidebar component that accepts a links array of strings.
//         Render a <nav className="sidebar"> with a <ul> and one <li> per link.
//         Remember: each <li> needs a key prop!
function Sidebar({ links }) {
  return (
    <nav className="sidebar">
      <ul>
        {/* TODO: map over links to render <li> elements */}
      </ul>
    </nav>
  );
}

// TODO 3: Create a MainContent component that accepts children.
//         Render <main className="main-content">{children}</main>
function MainContent({ children }) {
  return (
    <main className="main-content">
      {/* TODO: render children */}
    </main>
  );
}

// TODO 4: Create a PageLayout component that accepts sidebar and children.
//         Render:
//           <div className="page-layout">
//             {sidebar}
//             <MainContent>{children}</MainContent>
//           </div>
function PageLayout({ sidebar, children }) {
  return (
    <div className="page-layout">
      {/* TODO: render sidebar prop and wrap children in MainContent */}
    </div>
  );
}

// TODO 5: Complete App to compose the full page:
//         - Define navLinks array with at least 3 strings
//         - Render <PageLayout sidebar={<Sidebar links={navLinks} />}>
//             with two or more <Card> components inside, each with a title and
//             some content between the tags (this becomes props.children)
//         </PageLayout>
const navLinks = ["Dashboard", "Profile", "Settings"];

function App() {
  return (
    <div>
      <h1>My App</h1>
      {/* TODO: Compose PageLayout, Sidebar, and Card components here */}
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);

// ─────────────────────────────────────────────────────────────────────────────
// TODO 6 (Reading Exercise): At the very bottom of this file, add a comment
// block showing what the Card component above would look like as a class
// component. Compare the two styles.
// ─────────────────────────────────────────────────────────────────────────────
