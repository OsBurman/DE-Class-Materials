// Exercise 03: Controlled Form with Validation
const { useState } = React;

function RegistrationForm() {
  // TODO 1: Declare state for the three field VALUES: name, email, password (all '').

  // TODO 2: Declare state for the three field ERRORS: nameError, emailError, passwordError (all '').

  // TODO 3: Declare a 'success' boolean state, initially false.

  function handleSubmit(e) {
    e.preventDefault();

    // TODO 4: Reset all error states to '' before re-validating.

    let valid = true;

    // TODO 5: Validate 'name' — required and at least 2 characters.
    //         If invalid: setNameError('Name must be at least 2 characters.') and set valid = false.

    // TODO 6: Validate 'email' — required and must contain '@'.
    //         If invalid: setEmailError('Please enter a valid email address.') and set valid = false.

    // TODO 7: Validate 'password' — required and at least 8 characters.
    //         If invalid: setPasswordError('Password must be at least 8 characters.') and set valid = false.

    // TODO 8: If valid is still true, call setSuccess(true).
  }

  return (
    <div>
      <h1>Registration Form</h1>
      {/* TODO 9: When success is true, display a success message */}
      <form onSubmit={handleSubmit}>
        <h3>Create Account</h3>

        {/* ── Name ── */}
        <label htmlFor="name">Name</label>
        {/* TODO 10: Add a controlled text input bound to 'name' state */}
        <input id="name" type="text" placeholder="Your name" />
        {/* TODO 11: Conditionally render nameError below the input */}

        {/* ── Email ── */}
        <label htmlFor="email">Email</label>
        {/* TODO 12: Add a controlled email input bound to 'email' state */}
        <input id="email" type="text" placeholder="you@example.com" />
        {/* TODO 13: Conditionally render emailError below the input */}

        {/* ── Password ── */}
        <label htmlFor="password">Password</label>
        {/* TODO 14: Add a controlled password input bound to 'password' state */}
        <input id="password" type="password" placeholder="Min. 8 characters" />
        {/* TODO 15: Conditionally render passwordError below the input */}

        <button type="submit">Register</button>
      </form>
    </div>
  );
}

function App() {
  return <RegistrationForm />;
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
