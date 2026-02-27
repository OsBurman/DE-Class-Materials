import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

// TODO 1: Add `name` state (string, '').
// TODO 2: Call useNavigate() to get the navigate function.
// TODO 3: On form submit:
//   - Prevent default.
//   - If name.trim() is empty, return early.
//   - Call onLogin(name.trim()).
//   - Call navigate('/profile').

function LoginPage({ onLogin }) {
  // TODO: implement
  return (
    <div className="page">
      <div className="login-form">
        <h1>Login</h1>
        <form onSubmit={e => e.preventDefault()}>
          <div className="form-group">
            <label htmlFor="name">Your Name</label>
            {/* TODO: bind value and onChange */}
            <input type="text" id="name" placeholder="Enter your name" />
          </div>
          <button type="submit" className="btn" style={{ width: '100%' }}>Log In</button>
        </form>
      </div>
    </div>
  )
}

export default LoginPage
