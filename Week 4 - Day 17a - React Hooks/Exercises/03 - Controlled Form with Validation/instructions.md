# Exercise 03: Controlled Form with Validation

## Learning Objectives
- Bind form inputs to state with controlled components (`value` + `onChange`)
- Maintain per-field error state alongside field value state
- Validate inputs on submit and display inline error messages
- Clear errors and show a success message when all fields are valid

---

## Background

A **controlled component** is a form element whose value is entirely driven by React state.
Every keystroke calls `onChange`, which updates state, which re-renders the input — React is the "single source of truth."

```jsx
const [email, setEmail] = useState('');
<input value={email} onChange={e => setEmail(e.target.value)} />
```

Validation is run in the `handleSubmit` function **before** you would normally send data to a server.

---

## Requirements

### Component: `RegistrationForm`

Build a registration form with three controlled fields and client-side validation.

#### Fields

| Field | Validation Rule |
|-------|----------------|
| Name | Required; must be at least 2 characters |
| Email | Required; must contain `@` |
| Password | Required; must be at least 8 characters |

#### Behavior

1. **Controlled inputs** — each field uses its own `useState` pair: `(value, setValue)`.
2. **Error state** — maintain a separate error string for each field, e.g. `const [nameError, setNameError] = useState('')`.
3. **Submit handler** — `handleSubmit(e)` should:
   - Call `e.preventDefault()` to stop the page from reloading.
   - Validate all fields.
   - If any validation fails, set the corresponding error state and return early.
   - If all fields are valid, clear all errors and set a `success` boolean state to `true`.
4. **Inline errors** — render each error below its input only when the error string is non-empty.
5. **Success message** — when `success` is `true`, display a "Registration successful!" message instead of (or alongside) the form.

#### Example outline

```jsx
function RegistrationForm() {
  const [name,          setName]          = useState('');
  const [email,         setEmail]         = useState('');
  const [password,      setPassword]      = useState('');
  const [nameError,     setNameError]     = useState('');
  const [emailError,    setEmailError]    = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [success,       setSuccess]       = useState(false);

  function handleSubmit(e) {
    e.preventDefault();
    // TODO: validate each field and set errors; if all valid set success = true
  }

  return (
    <form onSubmit={handleSubmit}>
      {/* controlled inputs + inline errors + submit button + success message */}
    </form>
  );
}
```

---

## Tips

- Use `value={name}` and `onChange={e => setName(e.target.value)}` on every input.
- Show an error paragraph conditionally: `{nameError && <p className="error">{nameError}</p>}`.
- Clear all error states before re-validating on each submit attempt.
- `input[type="password"]` hides characters automatically — no extra logic needed.
