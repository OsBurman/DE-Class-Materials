// Exercise 03: Controlled Form with Validation — SOLUTION
const { useState } = React;

function RegistrationForm() {
  // Field values
  const [name,     setName]     = useState('');
  const [email,    setEmail]    = useState('');
  const [password, setPassword] = useState('');

  // Per-field error messages
  const [nameError,     setNameError]     = useState('');
  const [emailError,    setEmailError]    = useState('');
  const [passwordError, setPasswordError] = useState('');

  // Overall success flag
  const [success, setSuccess] = useState(false);

  function handleSubmit(e) {
    e.preventDefault();

    // Reset errors before each validation pass
    setNameError('');
    setEmailError('');
    setPasswordError('');

    let valid = true;

    if (name.trim().length < 2) {
      setNameError('Name must be at least 2 characters.');
      valid = false;
    }

    if (!email.trim().includes('@')) {
      setEmailError('Please enter a valid email address.');
      valid = false;
    }

    if (password.length < 8) {
      setPasswordError('Password must be at least 8 characters.');
      valid = false;
    }

    if (valid) {
      setSuccess(true);
    }
  }

  return (
    <div>
      <h1>Registration Form</h1>

      {success && (
        <p className="success">✅ Registration successful! Welcome, {name}!</p>
      )}

      <form onSubmit={handleSubmit}>
        <h3>Create Account</h3>

        {/* ── Name ── */}
        <label htmlFor="name">Name</label>
        <input
          id="name"
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Your name"
        />
        {nameError && <p className="error">{nameError}</p>}

        {/* ── Email ── */}
        <label htmlFor="email">Email</label>
        <input
          id="email"
          type="text"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="you@example.com"
        />
        {emailError && <p className="error">{emailError}</p>}

        {/* ── Password ── */}
        <label htmlFor="password">Password</label>
        <input
          id="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Min. 8 characters"
        />
        {passwordError && <p className="error">{passwordError}</p>}

        <button type="submit">Register</button>
      </form>
    </div>
  );
}

function App() {
  return <RegistrationForm />;
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
