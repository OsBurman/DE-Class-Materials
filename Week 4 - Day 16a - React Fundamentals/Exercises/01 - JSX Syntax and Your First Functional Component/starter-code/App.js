// Exercise 01: JSX Syntax and Your First Functional Component

// TODO 1: Create a functional component `Header` that returns a <div> containing:
//         - <h1>React Fundamentals</h1>
//         - <p>Building UIs with components and JSX</p>
function Header() {
  // TODO: return the JSX here
}

// TODO 2: Create a functional component `Welcome` that returns:
//         - <h2>Welcome to React!</h2>
//         - <p>React uses a Virtual DOM for efficient updates.</p>
//         Wrap both in a single root element.
function Welcome() {
  // TODO: return the JSX here
}

// TODO 3: Create a functional component `InfoBox` that returns a <div> with
//         className="info-box" containing:
//         - <strong>Key concept:</strong> JSX must have a single root element.
function InfoBox() {
  // TODO: return the JSX here
}

// TODO 4: Create an App component that renders <Header />, <Welcome />, and <InfoBox />
//         in order, wrapped in a single <div>.
//
// TODO 5: Inside App, add:
//         a) A JavaScript variable (e.g., const version = "18") and embed it in JSX with {}
//            e.g.: <p>React version: {version}</p>
//         b) At least one self-closing tag: <hr /> between sections
function App() {
  // TODO: declare a variable and embed it in JSX

  return (
    <div>
      {/* TODO: render Header, a self-closing <hr />, Welcome, another <hr />, InfoBox */}
      {/* TODO: add a <p> that uses {} to show your variable */}
    </div>
  );
}

// TODO 6: Render App into the #root div using ReactDOM.createRoot
// const root = ReactDOM.createRoot(document.getElementById("root"));
// root.render(<App />);
